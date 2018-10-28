package main.scala.feed;

/**
 * @author terry
 */
import main.scala.model.{Altmetric=>AltmetricModel}
import scala.math._
import scala.collection.mutable.HashMap
import scala.collection.JavaConversions._

import scalaj.http._
import spray.json._
import DefaultJsonProtocol._ // if you don't supply your own Protocol (see below)

import java.io.StringReader;
import javax.json._
import javax.json.stream.JsonParsingException;



object Altmetric extends Feed{
  
  val settings = new FeedSettings
  import settings._ 
  
  def getCitationObject(id:String)={
    val source: PartialFunction[(String,Int), (String,Int)] = {
      case (k,v) if k != "connotea" && k!="mendeley" && v!=0=> (k,v)
    }     
    
    val json=fetch(id)
    val c=parseCitationObject(json);
    val p=parsePostsObject(json)
    c match {
      case Some(c)=>{
        val cObj=new AltmetricModel(
                          doi=c.getOrElse("doi", "").replace("\"",""),
                          pmid=c.getOrElse("pmid", "").replace("\"",""),
                          title=c.getOrElse("title", ""),
                          posts=p.get)
        val i=parseSourcesCount(json).get.toMap
        cObj.altmetricCitationId=c.getOrElse("altmetric_id", "")
        cObj.altmetricSourcesCount=i.collect(source)
        Some(cObj);
      }
      case _=>None
    }
  }  
  
  def cited(page:Int):String={
      val response: HttpResponse[String] =Http(CitationUrl)
                              .timeout(connTimeoutMs = 1000, readTimeoutMs = 5000)
                              .param("key",ApiKey)
                              .param("num_results",MaxResults.toString)
                              .param("page",page.toString)
                              .asString  
     response.code match {
      case 200 => {
        ids(response.body).toSet.mkString(";");
      }
      case _   => {
        log.error(response.statusLine)
        ""
      }
    }       
  }
  
 def pages={
    val response: HttpResponse[String] =Http(CitationUrl)
                                .timeout(connTimeoutMs = 1000, readTimeoutMs = 5000)
                                .param("key",ApiKey)
                                .asString  
    response.code match {
      case 200 =>1 to 2
      //case 200 =>1 to lastPage(response.body).getOrElse(1)
      case _   => {
        log.error(response.statusLine)
        0 to 0
      }
    }                                 
  } 
  
  private def lastPage(content: String):Option[Int] =try {
    val query=content.parseJson
        .asJsObject("Invalid Json")
        .getFields("query")(0);
    val total=Integer.parseInt(query.asJsObject("Invalid Json")
                   .getFields("total")(0).toString)
    Some(ceil(total/MaxResults).toInt)
  } catch {
    case e: NumberFormatException => None
  }

  @throws(classOf[JsonParsingException])
  private def ids (content: String):Seq[Int]={
        var reader = Json.createReader(new StringReader(content));                            
        var strObject = reader.readObject(); 
        reader.close;
        val resultObject = strObject.getJsonArray("results");
      
        for (i<-0 until resultObject.size()) yield{
           reader= Json.createReader(new StringReader(resultObject.get(i).toString));
           strObject = reader.readObject();
           reader.close;
           strObject.getInt("altmetric_id");
        }
  }
 
  private def parseCitationObject(content:String)=try{
        var reader = Json.createReader(new StringReader(content));                            
        var strObject = reader.readObject(); 
        reader.close;
        val obj=strObject.getJsonObject("citation")
        val entry = HashMap.empty[String,String]
        entry+=("altmetric_id" ->strObject.getInt("altmetric_id").toString);
        val result=for ((k,v) <- obj)yield entry+=(k-> v.toString)
        Some(result.toVector(0))
  }catch {
    case e: Throwable => {
       log.error(e.toString);
       None
    }    
  }
  
  private def parsePostsObject(content:String)=try{
        var reader = Json.createReader(new StringReader(content));                            
        var strObject = reader.readObject(); 
        reader.close;
        val obj=strObject.getJsonObject("posts")
        val entry = HashMap.empty[String,String]
        val result=for ((k,v) <- obj)yield entry+=(k-> v.toString)
        Some(result.toVector(0))   
  }catch {
    case e: Throwable => {
       log.error(e.toString);
       None
    }    
  }

  
  private def parseSourcesCount(content:String)=try{
        var reader = Json.createReader(new StringReader(content));                            
        var strObject = reader.readObject(); 
        reader.close;
        val post=strObject.getJsonObject("posts")
        val readers=strObject.getJsonObject("counts").getJsonObject("readers")
        val entry = HashMap.empty[String,String]
        val p=for ((k,v) <- post)yield entry+=(k-> v.toString) 
        val count = getPostsCount(p.toVector(0).toMap)
        val result=for ((k,v) <- readers)yield count.get+=(k-> v.toString.toInt)
        Some(result.toVector(0))
        
  }catch {
    case e: Throwable => {
       log.error(e.toString);
       None
    }    
  }
  
  private def getPostsCount(posts:Map[String,String])={
       val entry = HashMap.empty[String,Int]
       val result=for ((k,v) <- posts)yield {
          val reader = Json.createReader(new StringReader(v.toString));
          val r=reader.readArray()
          reader.close
          entry+=(k-> r.size())
       }
       Some(result.toVector(0))     
  }
  
  
  
  private def fetch(id:String)={
//     println(FetchUrl+id)
//     println(s"$ApiKey")
     val response: HttpResponse[String] =Http(FetchUrl+id)
                              .timeout(connTimeoutMs = 1000, readTimeoutMs = 5000)
                              .param("key",ApiKey)
                              .asString  
     response.code match {
      case 200 => {
        response.body
      }
      case _   => {
        log.error(response.statusLine)
        ""
      }
    }      
  }
}


