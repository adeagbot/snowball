
package main.scala.metric

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext    
import org.apache.spark.SparkContext._
import org.elasticsearch.spark._ 


object Indexer {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {
    if(args.length!=3){
      println("Usage: ScalaIndexer <jobname> <path> <localhost:9200>");
      System.exit(1);
    }
    
    val appName=args(0);
    val file=args(1);
    val conf = new SparkConf().setAppName(appName);
    conf.set("spark.speculation","false");
    conf.set("es.nodes", args(2)); 
// conf.set("es.index.auto.create", "true");
    conf.set("es.write.operation","upsert");
    conf.set("es.mapping.id","id");    

    val sc = new SparkContext(conf)      
    val distFile = sc.textFile(file);
//    try{
      distFile.saveJsonToEs("{source}/{type}")
//    }catch{
//        case ex: Exception =>{
//            System.err.println("SparkIndexer Exception message : "+ex.getMessage)
//        }   
//        System.exit(1);
//    }
  }

}
