package main.scala.spark;

/**
 * @author terry
 */
import com.typesafe.config.{Config, ConfigFactory}

/* Initializes  Cassandra and Spark settings. */
final class SparkCassandraSettings(rootConfig: Config) {
  
  def this() = this(ConfigFactory.load)
 
//val myConfigFile = new File("path/to/myconfig.conf")
//val fileConfig = ConfigFactory.parseFile(myConfigFile).getConfig("myconfig")
//val config = ConfigFactory.load(fileConfig)  
  
  
  
  protected val config = rootConfig.getConfig("app.spark")

  val SparkMaster: String = config.getString("master")

  val SparkCleanerTtl: Int = config.getInt("cleaner.ttl")

  val SparkStreamingBatchDuration: Long = config.getLong("streaming.batch.duration")

//  val Data = akka.japi.Util.immutableSeq(config.getStringList("data")).toSet

  val CassandraSeed: String = config.getString("spark.cassandra.connection.host")

  val CassandraKeyspace = config.getString("spark.cassandra.keyspace")

  val CassandraTable = config.getString("spark.cassandra.table")
}
