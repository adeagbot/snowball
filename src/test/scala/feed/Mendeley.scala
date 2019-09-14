package test.scala.feed
import java.net.URLEncoder
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import com.datastax.spark.connector._
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpPost, HttpGet}
import org.apache.http.message.BasicNameValuePair
import scala.util.parsing.json._


object Mendeley {
   var token=""
  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {
    println(getToken);
  }
def getContent(eid:String,doi:String,pmid:String):String = {

    var content=""
    
    
    if(!eid.isEmpty){
      content = getRestContent("https://api.mendeley.com/catalog?view=stats&scopus="+ URLEncoder.encode(eid, "UTF-8")+"&access_token="+getToken)    
    }else if(!doi.isEmpty){
       content = getRestContent("https://api.mendeley.com/catalog?view=stats&scopus="+ URLEncoder.encode(eid, "UTF-8")+"&access_token="+getToken)    
    }else if(!pmid.isEmpty){
      content = getRestContent("https://api.mendeley.com/catalog?view=stats&doi="+URLEncoder.encode(pmid, "UTF-8") +"&access_token="+getToken)
    }
//    //eid
//    
//    println(" GETTING FROM EID............");
//    //scopus_doi
//    if(content.length < 10 && !doi.isEmpty ){
//      println(" GETTING FROM DOI............");
//      
//    }else if (content.length < 10   &&  !pmid.isEmpty  ){
//      println(" GETTING FROM  PMID ............");
//      content = getRestContent("https://api.mendeley.com/catalog?view=stats&pmid="+ URLEncoder.encode(data.getString(2).mkString, "UTF-8")+"&access_token="+getToken)
//    }
//    println(" CONTENT LENGHT : ............:"+content.length);
    return content.slice(1, content.length- 1)

  }

  def getRestContent(url:String): String = {
    val httpClient = new DefaultHttpClient()
    val httpResponse = httpClient.execute(new HttpGet(url))
    val entity = httpResponse.getEntity()
    var content = ""
    if (entity != null) {
      val inputStream = entity.getContent()
      content = scala.io.Source.fromInputStream(inputStream).getLines.mkString
      inputStream.close
    }
    httpClient.getConnectionManager().shutdown()

    return   content
  }

  def getToken():String ={

    val accessURI: String = "https://api-oauth2.mendeley.com/oauth/token"
    val clientID: String = "****"
    val clientSecret: String = "?????????"
    val httpClient = new DefaultHttpClient()
    val httpPost = new HttpPost(accessURI)
    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
    val nameValuePairs = new java.util.ArrayList[NameValuePair](1)
    nameValuePairs.add(new BasicNameValuePair("client_id", clientID));
    nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));
    nameValuePairs.add(new BasicNameValuePair("grant_type", "client_credentials"));
    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    var content = ""
     try {
       val httpResponse = httpClient.execute(httpPost)
       val entity = httpResponse.getEntity()
       if (entity != null) {
         val inputStream = entity.getContent()
         content = scala.io.Source.fromInputStream(inputStream).getLines.mkString
         inputStream.close
       }
       JSON.parseFull(content).get;
//       val json: JsValue = content.parseJson
//       val obj = json.asJsObject
//       content =  obj.getFields("access_token").mkString.replace("\"", "")
     }catch {
       case ex: Exception => println("Exception  while getting Token:"+content)

     } finally {
       httpClient.getConnectionManager().shutdown()
     }
     return content
  }
  

}
