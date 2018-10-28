package main.scala.utils;

/**
 * @author terry
 */


import scala.collection.immutable.StringOps
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

import org.apache.curator.framework.recipes.cache.NodeCache
import org.apache.zookeeper.KeeperException

import org.apache.curator.utils.CloseableUtils

object Curator {
  
import com.typesafe.config.{Config, ConfigFactory}

  final class ConfigSettings(rootConfig: Config) {
  //  config.checkValid(ConfigFactory.defaultReference(), "feed-app")
    
    def this() = this(ConfigFactory.load)
  
    protected val config = rootConfig.getConfig("app.curator")
    
    val connection=config.getString("zookeeper.host")
    val path=config.getString("zookeeper.znode.path")

    val token=config.getString("zookeeper.znode.data")
    def printSetting(path: String) {
        println("The setting '" + path + "' is: " + config.getString(path))
    }  
  }
  
  val settings = new ConfigSettings
  import settings._ 
  var client:CuratorFramework=null
  
  def connect =try{
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    client=
      CuratorFrameworkFactory.newClient(connection,retryPolicy)
    client.start
    client.getZookeeperClient.blockUntilConnectedOrTimedOut 
    if(client.checkExists().forPath(path)==null)
    setData(token)
  }catch{
    case e: Throwable =>{
      println(e);
      exit(1)
    }
  }
 
  def close=if (client!=null)CloseableUtils.closeQuietly(client)
  
  def getData=new String(client.getData.forPath(path))
  
  def setData(token:String)= try{
        client.setData().forPath(path, new StringOps(token).getBytes);
    } catch {
      case e:KeeperException.NoNodeException=>
        client.create().forPath(path, new StringOps(token).getBytes);
    }
}