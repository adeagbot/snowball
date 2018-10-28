package main.scala.spark;

/**
 * @author terry
 */
//import org.apache.spark.{Logging, SparkContext, SparkConf}  
import org.apache.spark.SparkContext._

import main.scala.feed.Altmetric
import main.scala.feed.{Mendeley=>MendeleyApi}

import main.scala.model.{Altmetric=>AltmetricModel}

object AltmetricApp  extends MainApp {
  
   val rdd=sc.parallelize(Altmetric.pages)
                  .flatMap {i=>Altmetric.cited(i).split(";")}
//                  .map{Altmetric.getCitationObject}
//                  .map{citation=>(citation,citation.get.getMendeleyObject(MendeleyApi.getInstance))}
                  .collect()
                  .foreach(println)
  
  MendeleyApi.getInstance.close               
  // rdd.saveAsTextFile("hdfs://localhost:9000/user/ams/altmetric");  
  
  //case class WordCount(word: String, count: Long)
//val collection = sc.parallelize(Seq(WordCount("dog", 50), WordCount("cow", 60)))
//collection.saveToCassandra("test", "words", SomeColumns("word", "count")) 
   
}
