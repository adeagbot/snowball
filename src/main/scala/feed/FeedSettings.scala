package main.scala.feed;

/**
 * @author terry
 */
import com.typesafe.config.{Config, ConfigFactory}

/* Initializes Altmetric, Cafe and Mendeley settings. */
final class FeedSettings(rootConfig: Config) {
//  config.checkValid(ConfigFactory.defaultReference(), "feed-app")
  
  def this() = this(ConfigFactory.load)

  protected val config = rootConfig.getConfig("app.feed")
  
  val ApiKey=config.getString("altmetric.apikey")
  
  val CitationUrl=config.getString("altmetric.citation.url")
  val FetchUrl=config.getString("altmetric.fetch.url")
  
  val QueueName=config.getString("cafe.sqs.queue.name")
  val AccessKeyId=config.getString("cafe.s3.key")
  val SecretAccessKey=config.getString("cafe.s3.secret")

  val ClientId= config.getString("mendeley.app.id")
  val SecretId = config.getString("mendeley.app.secret")
  val OauthUrl=config.getString("mendeley.oauth.url")
  val CatalogUrl=config.getString("mendeley.catalog.url")
   
  val MaxResults: Int = config.getInt("max.results") 
  
  val SdBucket=config.getString("cafe.sd.bucket.name")
  val AniBucket=config.getString("cafe.sc.ani.bucket.name")
  val AprBucket=config.getString("cafe.sc.apr.bucket.name")
  val IprBucket=config.getString("cafe.sc.ipr.bucket.name") 
  
  def printSetting(path: String) {
      println("The setting '" + path + "' is: " + config.getString(path))
  }  
  
}
