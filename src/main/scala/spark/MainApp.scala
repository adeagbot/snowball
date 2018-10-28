package main.scala.spark;

/**
 * @author terry
 */
import org.apache.spark.{Logging, SparkContext, SparkConf}  
import org.apache.spark.SparkContext._

trait MainApp extends App with Logging{
  
    /* Initialize Cassandra and Spark settings. */
  val settings = new SparkCassandraSettings()
  import settings._
  
    val appName=this.getClass.getName
    log.info(s"$appName started.") 
  
   val conf = new SparkConf().setAppName(this.getClass.getName)
//                              .set("spark.cassandra.connection.host", args(0))
//                              .set("spark.cassandra.auth.username", args(1))            
//                              .set("spark.cassandra.auth.password", args(2));
    
//    val file=args(3);
// 
    protected lazy val sc = new SparkContext(conf)

}


