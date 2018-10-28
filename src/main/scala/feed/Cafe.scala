package main.scala.feed;

/**
 * @author terry
 */

import scala.math._
import scala.io.Source

import awscala._,s3._,sqs._

import com.amazonaws.services.{ s3 => aws }

import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap

import spray.json._
import DefaultJsonProtocol._

import java.io.StringReader;
import javax.json._
import javax.json.stream.JsonParsingException;

import main.scala.model.Scopus
import main.scala.model.ScienceDirect

object Cafe extends Feed{
  
  val settings = new FeedSettings
  import settings._  
  
  implicit val sqs = SQS(AccessKeyId,SecretAccessKey)(Region.EU_WEST_1)
  implicit val s3 = S3(AccessKeyId,SecretAccessKey)((Region.US_EAST_1))
  val queue = sqs.queue(QueueName); 

  def lastPage:Option[Int]={
    queue match {
      case Some(q) => {
    // get queue attributes
        val attribute = sqs.queueAttributes(queue.get, "ApproximateNumberOfMessages")
        val x=attribute.getOrElse("ApproximateNumberOfMessages", "0");
        val total =Integer.parseInt(x)
        Some(ceil(total/MaxResults).toInt)
      }
      case _ =>{
       log.error("No queue found");
        None ;
      }
    }
  }
    
  def pages= 1 to lastPage.getOrElse(1) 
  
  def parseJson(content:String)=try{
        var reader = Json.createReader(new StringReader(content));   
        var strObject = reader.readObject(); 
        reader.close;
        val bucket=strObject.getString("bucket").replace("\"","")
        val resultObject = strObject.getJsonArray("entries");
        val result=for (i<-0 until resultObject.size()) yield{
           val entry = HashMap.empty[String,String]
           entry+=("bucket" ->bucket);
           reader= Json.createReader(new StringReader(resultObject.get(i).toString));
           strObject = reader.readObject();
           reader.close;
           for ((k,v) <- strObject)entry+=(k-> v.toString);
           val key=entry.getOrElse("key", "").replace("\"","");
           entry+=("key"-> key);
           entry
        }
        Some(result.get(0))
  }catch{
    case e: Throwable => {
       log.error(e.toString);
       None
    }
  }  
  
  def parseMessage(message:String)={
         var json=  message.parseJson
                 .asJsObject("Invalid Json")
                 .getFields("Message")(0)
                 .toString
                 .drop(1)
                 .dropRight(1)
                 .replace("\\","");
                 //.replaceAll("\\\\", "");
        
        parseJson(json)
  }
    
  
  def messages= queue match {// receive messages
      case Some(q) => {
        // get queue messages
         val receivedMessages: Seq[Message] = sqs.receiveMessage(queue.get,10,3)
         // sqs.deleteMessages(receivedMessages);
         Some(receivedMessages);
      }
      case _ =>{
        log.error("No queue found");
        None ;
      }
  }
  
  def scopusMessages: Array[HashMap[String,String]] ={
    messages.get.map(i=>{
        val contentMap=parseMessage(i.body).get
        if(contentMap.getOrElse("bucket", "")==AniBucket)sqs.delete(i)
        contentMap
    }).filter{i=>i.getOrElse("bucket", "")==AniBucket}.toArray
  }
  
 def scienceDirectMessages: Array[HashMap[String,String]] ={
    messages.get.map(i=>{
        val contentMap=parseMessage(i.body).get
        if(contentMap.getOrElse("bucket", "")==SdBucket)sqs.delete(i)
        contentMap
    }).filter{i=>i.getOrElse("bucket", "")==SdBucket}.toArray
  }  

  def fetch(bucket:String,key:String)=  try {
      val obj=s3.getObject(new aws.model.GetObjectRequest(bucket, key))
      Source.fromInputStream(obj.getObjectContent).mkString 
  } catch {
    case e: aws.model.AmazonS3Exception => {
      log.error(e.toString);
      ""
    }
  } 
  
   def getScopusObject(map:HashMap[String,String]):Scopus={
     //println(map)
      val bucket=map.getOrElse("bucket", "");
      val key=map.getOrElse("key","");
      val scopus=new Scopus
      scopus.scopusAction=map.getOrElse("action", "").replace("\"","")
      scopus.scopusEid=map.getOrElse("eid", "").replace("\"","")
      scopus.scopusPmid=map.getOrElse("pmid", "").replace("\"","")
      scopus.scopusPii=map.getOrElse("pii", "").replace("\"","")
      scopus.scopusDoi=map.getOrElse("doi", "").replace("\"","")
      scopus.scopusIssn=map.getOrElse("issn", "").replace("\"","")
      scopus.parseXML(fetch(bucket, key));      
   }
   
   def getScienceDirectObject(map:HashMap[String,String]):ScienceDirect={
     //println(map)
      val bucket=map.getOrElse("bucket", "");
      val key=map.getOrElse("key","");
      val scienceDirect=new ScienceDirect
      scienceDirect.scienceDirectAction=map.getOrElse("action", "").replace("\"","")
      scienceDirect.scienceDirectEid=map.getOrElse("eid", "").replace("\"","")
      scienceDirect.scienceDirectPmid=map.getOrElse("pmid", "").replace("\"","")
      scienceDirect.scienceDirectPii=map.getOrElse("pii", "").replace("\"","")
      scienceDirect.scienceDirectDoi=map.getOrElse("doi", "").replace("\"","")
      scienceDirect.scienceDirectIssn=map.getOrElse("issn", "").replace("\"","")
      scienceDirect.parseXML(fetch(bucket, key));      
   }   
}