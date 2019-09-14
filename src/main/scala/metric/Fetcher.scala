
package main.scala.metric

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext    
import org.apache.spark.SparkContext._

import com.datastax.spark.connector._

object Fetcher {
  
  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {
    
    if(args.length!=6){
      System.err.println("Usage: ScalaIndexer <jobname>  <localhost> <user> <password> <path> <id|metric>");
      System.exit(1);
    }    
    
    val appName=args(0);
    
    val conf = new SparkConf().setAppName(appName)
                              .set("spark.cassandra.connection.host", args(1))
                              .set("spark.cassandra.auth.username", args(2))            
                              .set("spark.cassandra.auth.password", args(3));
    val file=args(4);
 
    val sc = new SparkContext(conf)
    
    val rowRdd= sc.cassandraTable("ams", "eid_articles").
            select("scopus_eid","scopus_pii","scopus_pmid","scopus_doi",
            "scopus_title","scopus_doc_type","scopus_ait_sort_date","scopus_subj_codes",
            "scopus_citations_count","scopus_fwci","scopus_issn",
            "mendeley_id","mendeley_readers_count","mendeley_groups_count",
            "altmetric_citation_id","altmetric_sources_count")
    
   if("id".equalsIgnoreCase(args(5))){
     val dataRdd = rowRdd.map(row =>getDocumentIds(row))
     dataRdd.saveAsTextFile(file);
   } else if ("metric".equalsIgnoreCase(args(5))){
     val dataRdd = rowRdd.map(row =>getMetrics(row))
     dataRdd.saveAsTextFile(file);
   }else{
      System.err.println("Valid Arguments:<id|metric>");
      System.exit(1);     
   }    
 }

 def getMetrics(data:ScalaGettableData):String = {      
     var content="{" 
     content+= "\"eid\":\""+data.getStringOption(0).mkString+"\","
     content+= "\"pii\":\""+data.getStringOption(1).mkString
                                          .replace("\"","")
                                          .replace("'","")
                                          .replace("\\","")
                                          .replace(" ","")+"\"," 
     content+="\"doc_type\":\""+  data.getStringOption(5).mkString+"\","
     content+="\"pub_date\":\""+  data.getStringOption(6).mkString+"\","
     val subj_codes=data.getSet[Int](7)
     if(subj_codes.nonEmpty){
       content+="\"subj_codes\":"+  data.getSet[Int](7).toString
                                                      .replace("Set(","[")
                                                      .replace(")","]")+","
     }else content+="\"subj_codes\":[],"

     val citations=data.getLongOption(8)
     if(citations.nonEmpty)content+="\"citations\":"+ citations.mkString+","
     else content+="\"citations\":0,"
     
     val fwci=data.getFloatOption(9)
     if(fwci.nonEmpty)content+="\"fwci\":"+ fwci.mkString+","
     else content+="\"fwci\":0.0,"   
     
    val groups=data.getLongOption(13)
    if(groups.nonEmpty)content+="\"groups\":"+ groups.mkString+","
    else content+="\"groups\":0,"    
     
    var map="\"sources\":{"       
    val sources=data.getMap[String,Int](15);    
    map=map +sources.view.map{ 
      case (key: String, value: Int) =>  "\""+key+"\":"+value+","  
    }.mkString
        
    val mendeley= data.getLongOption(12)
    if(mendeley.nonEmpty){
       map+= "\"mendeley\":"+mendeley.mkString  
    } else map+= "\"mendeley\":0"   
    map+="}"
    
     return content+map+"}";
   }
   
   def getDocumentIds(data:ScalaGettableData):String = {      
     var content="{" 
     val pii=data.getStringOption(1);
     if(pii.nonEmpty){
       content+= "\"pii\":\""+pii.mkString.replace("\"","")
                                          .replace("'","")
                                          .replace("\\","")
                                          .replace(" ","")+"\","  
     }
            
     
     val pmid=data.getStringOption(2);
     if(pmid.nonEmpty){
       content+= "\"pmid\":\""+pmid.mkString.replace("\"","")
                                            .replace("'","")+"\","
     }
     
     val doi=data.getStringOption(3);
     if(doi.nonEmpty){
       content+="\"doi\":\""+ doi.mkString.replace("\"","")
                                          .replace("'","")
                                          .replace("\\","")
                                          .replace(" ","")+"\","  
     }
     
     val title=data.getStringOption(4);
     if(title.nonEmpty)content+= "\"title\":\""+title.mkString+"\","
     
     val issn=data.getStringOption(10);
     if(issn.nonEmpty)content+="\"issn\":\""+ issn.mkString+"\"," 
     
     val mendeley_id=data.getStringOption(11);
     if(mendeley_id.nonEmpty)content+="\"mendeley_id\":\""+ mendeley_id.mkString+"\","
     
     val altmetric_id=data.getLongOption(14)
     if(altmetric_id.nonEmpty)content+="\"altmetric_id\":\""+ altmetric_id.mkString+"\","
    
     content+= "\"eid\":\""+data.getStringOption(0).mkString+"\""
     return content+"}";
   } 
 
}
