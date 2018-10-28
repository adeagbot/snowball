package main.scala.spark;

/**
 * @author terry
 */
//import org.apache.spark.{Logging, SparkContext, SparkConf}  
import org.apache.spark.rdd._
import org.apache.spark.SparkContext._
import com.datastax.spark.connector._

import main.scala.model.Benchmark

object BenchmarkApp  extends MainApp {
  log.info(s"Benchmark Work started.")

 /**
   * compute percentile from an unsorted Spark RDD
   * @param data: input data set of Long integers
   * @param tile: percentile to compute (eg. 85 percentile)
   * @return value of input data at the specified percentile
   */
  def computePercentile(data: RDD[Long], tile: Double): Double = {
    // NIST method; data to be sorted in ascending order
    val r = data.sortBy(x => x)
    val c = r.count()
    if (c == 1) r.first()
    else {
      val n = (tile / 100d) * (c + 1d)
      val k = math.floor(n).toLong
      val d = n - k
      if (k <= 0) r.first()
      else {
        val index = r.zipWithIndex().map(_.swap)
        val last = c
        if (k >= c) {
          index.lookup(last - 1).head
        } else {
          index.lookup(k - 1).head + d * (index.lookup(k).head - index.lookup(k - 1).head)
        }
      }
    }
  }   
  
  /* Cassandra and Spark settings. */
//  val settings = new SparkCassandraSettings()
//  import settings._
//   /** Configures Spark. */
//  val conf = new SparkConf(true)
//    .set("spark.cassandra.connection.host", CassandraSeed)
//    .set("spark.cleaner.ttl", SparkCleanerTtl.toString)
//    .setMaster(SparkMaster)  

    val rowRdd= sc.cassandraTable[Benchmark]("ams", "eid_articles")
    
    // class inside object
//println(new OuterObject.InnerClass().x)
    
// object inside class
//println(new OuterClass().InnerObject.y)    
    
     rowRdd.saveAsTextFile("hdfs://localhost:9000/user/benchmark");  
}

// def getMetrics(data:ScalaGettableData):String = {      
//     var content="{" 
//     content+= "\"eid\":\""+data.getStringOption(0).mkString+"\","
//     content+= "\"pii\":\""+data.getStringOption(1).mkString
//                                          .replace("\"","")
//                                          .replace("'","")
//                                          .replace("\\","")
//                                          .replace(" ","")+"\"," 
//     content+="\"doc_type\":\""+  data.getStringOption(5).mkString+"\","
//     content+="\"pub_date\":\""+  data.getStringOption(6).mkString+"\","
//     val subj_codes=data.getSet[Int](7)
//     if(subj_codes.nonEmpty){
//       content+="\"subj_codes\":"+  data.getSet[Int](7).toString
//                                                      .replace("Set(","[")
//                                                      .replace(")","]")+","
//     }else content+="\"subj_codes\":[],"
//
//     val citations=data.getLongOption(8)
//     if(citations.nonEmpty)content+="\"citations\":"+ citations.mkString+","
//     else content+="\"citations\":0,"
//     
//     val fwci=data.getFloatOption(9)
//     if(fwci.nonEmpty)content+="\"fwci\":"+ fwci.mkString+","
//     else content+="\"fwci\":0.0,"   
//     
//    val groups=data.getLongOption(13)
//    if(groups.nonEmpty)content+="\"groups\":"+ groups.mkString+","
//    else content+="\"groups\":0,"    
//     
//    var map="\"sources\":{"       
//    val sources=data.getMap[String,Int](15);    
//    map=map +sources.view.map{ 
//      case (key: String, value: Int) =>  "\""+key+"\":"+value+","  
//    }.mkString
//        
//    val mendeley= data.getLongOption(12)
//    if(mendeley.nonEmpty){
//       map+= "\"mendeley\":"+mendeley.mkString  
//    } else map+= "\"mendeley\":0"   
//    map+="}"
//    
//     return content+map+"}";
//   }
//   
//   def getDocumentIds(data:ScalaGettableData):String = {      
//     var content="{" 
//     val pii=data.getStringOption(1);
//     if(pii.nonEmpty){
//       content+= "\"pii\":\""+pii.mkString.replace("\"","")
//                                          .replace("'","")
//                                          .replace("\\","")
//                                          .replace(" ","")+"\","  
//     }
//            
//     
//     val pmid=data.getStringOption(2);
//     if(pmid.nonEmpty){
//       content+= "\"pmid\":\""+pmid.mkString.replace("\"","")
//                                            .replace("'","")+"\","
//     }
//     
//     val doi=data.getStringOption(3);
//     if(doi.nonEmpty){
//       content+="\"doi\":\""+ doi.mkString.replace("\"","")
//                                          .replace("'","")
//                                          .replace("\\","")
//                                          .replace(" ","")+"\","  
//     }
//     
//     val title=data.getStringOption(4);
//     if(title.nonEmpty)content+= "\"title\":\""+title.mkString+"\","
//     
//     val issn=data.getStringOption(10);
//     if(issn.nonEmpty)content+="\"issn\":\""+ issn.mkString+"\"," 
//     
//     val mendeley_id=data.getStringOption(11);
//     if(mendeley_id.nonEmpty)content+="\"mendeley_id\":\""+ mendeley_id.mkString+"\","
//     
//     val altmetric_id=data.getLongOption(14)
//     if(altmetric_id.nonEmpty)content+="\"altmetric_id\":\""+ altmetric_id.mkString+"\","
//    
//     content+= "\"eid\":\""+data.getStringOption(0).mkString+"\""
//     return content+"}";
//   }   
//}
