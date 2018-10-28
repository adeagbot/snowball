package main.scala.feed;

/**
 * @author terry
 */

import scala.util.matching.Regex
import scalaj.http._
import spray.json._
import spray.json.DefaultJsonProtocol._
import java.net.ConnectException
import java.io.StringReader
import javax.json._
import javax.json.stream.JsonParsingException
import scala.collection.JavaConversions._
import main.scala.utils.{CountryNames,Curator}
import java.net.ConnectException
import javax.json.stream.JsonParsingException

import main.scala.model.{Mendeley => MendeleyObject}

//using singleton design pattern to return a single API instance 
object Mendeley {
  val mendeley = new Mendeley
  mendeley.connect
  def getInstance = mendeley
} 

class Mendeley private extends Feed { 
  
  val settings = new FeedSettings
  
  import settings._ 
  
  def connect=Curator.connect
  def close=Curator.close
  
  def getDocumentType(typeId:String,docId:String,title:String)={
    if(!docId.isEmpty) {
      typeId match{ 
        case "pmid"|"doi"|"scopus"|"ssrn"|"arxiv"|"isbn"|"issn" => {
         val mObj= getCatalog(typeId,docId,Curator.getData)
             
         mObj.getOrElse("") match{
             case m:MendeleyObject=>{
                 //println(typeId +": "+docId)
                 //println(mObj)
                 mObj.get.isTitleMatch(title) match {
                   case true => mObj.get
                   case false => None
                 }                      
             }
             case _=>None
         }

        }
        case default => None
      }      
    }else  None
 }
  // Using partial functions to get document
   val byEid=getDocumentType("scopus",_: String,_: String)  
   val byDoi=getDocumentType("doi",_: String,_: String) 
   val byPmid=getDocumentType("pmid",_: String,_: String)
   val byIssn=getDocumentType("issn",_: String,_: String)
   val byIsbn=getDocumentType("isbn",_: String,_: String)
   val byArxiv=getDocumentType("arxiv",_: String,_: String)
   val bySsrn=getDocumentType("ssrn",_: String,_: String) 
 
 def getDocument(scopus:String="",doi:String="",pmid:String="",
                         issn:String="",isbn:String="",arxiv:String="",
                         ssrn:String="",title:String="")={
     val mObj=byEid(scopus,title)
     mObj  match{
       case m:MendeleyObject=>mObj
       case _=>{val mObj=byDoi(doi,title)
               mObj match{
       case m:MendeleyObject=>mObj
       case _=>{val mObj=byPmid(pmid,title)
               mObj match{
       case m:MendeleyObject=>mObj
       case _=>{val mObj=byIssn(issn,title)
                mObj match{
       case m:MendeleyObject=>mObj
       case _=>{val mObj=byIsbn(isbn,title)
                mObj match{
       case m:MendeleyObject=>mObj
       case _=>{val mObj=byArxiv(arxiv,title)
                 mObj match{
       case m:MendeleyObject=>mObj
       case _=>{ bySsrn(ssrn,title)}
       }}} }}}}}}}
     }
   }
   
   def convertToMap(jvalue: JsonValue)={
       val reader = Json.createReader(new StringReader(jvalue.toString));
       reader.readObject();  
   }
 
  def getCountryCode(json:JsonObject):Option[Map[String,Int]]={
     json match {
       case j:JsonObject=>{
          val a=j.map {
             case (country, count) => {
               try{
                 (CountryNames.getCode(country),Integer.parseInt(count.toString))
               }catch{
                 case e:NoSuchElementException=>{
                   (country,Integer.parseInt(count.toString))
                 }
               }
             }    
         }
          Some(a.toMap)
       }
       case null => None  
     }
   }
  
   def getDiscipline(json:JsonObject):Option[Map[String,Int]]={
     json match {
       case j:JsonObject=>{
         val a=j.map {
             case (discipline, value) => {
                val count=convertToMap(value)
                      .map(i=>i._2.toString())
                      .map{Integer.parseInt}
                      .reduceLeft(_ + _)
               (discipline,count)
             }
         }
         Some(a.toMap)
       }
       case null=>None 
     }
   }
 
  def getStatus(json:JsonObject):Option[Map[String,Int]]={
    json match{
      case j:JsonObject =>{
        val a=j.map {
             case (status, count) => {
               (status,Integer.parseInt(count.toString))
             }
         }
        Some(a.toMap)
      }
      case null=>None 
    }

 }
  
  def getIndentifiers(json:JsonObject):Option[Map[String,String]]={
    json match {
      case j:JsonObject=>{
        val a=j.map {
             case (key, value) => {
               (key,value.toString)
             }
         }
        Some(a.toMap)
      }
      case null=>None
    }
 } 
  
  
  @throws(classOf[JsonParsingException])
  private def parseJson (content: String)={

        var reader = Json.createReader(new StringReader(content));                            
        var strArray = reader.readArray()
        
        if(strArray.length>0){
          reader= Json.createReader(new StringReader(strArray.get(0).toString));
          var  strObject = reader.readObject();
          
          val identifiers=getIndentifiers(strObject.getJsonObject("identifiers"));
          val academic_status=getStatus(
              strObject.getJsonObject("reader_count_by_academic_status"));
          val discipline=getDiscipline(strObject.getJsonObject("reader_count_by_subdiscipline"));
          val country=getCountryCode(strObject.getJsonObject("reader_count_by_country"));         
            
          reader.close
  
          val mObj=new MendeleyObject(strObject.getString("title"),identifiers.get)
          mObj.mendeleyId=strObject.getString("id")
          mObj.mendeleyUrl=strObject.getString("link")
          mObj.mendeleyReadersCount=strObject.getInt("reader_count")
          mObj.mendeleyGroupsCount=strObject.getInt("group_count") 
 
          mObj.mendeleyReadersByStatus=academic_status.getOrElse(null)
          mObj.mendeleyReadersByDiscipline=discipline.getOrElse(null)
          mObj.mendeleyReadersByCountry=country.getOrElse(null)
          Some(mObj)
        } else {
          None;
        }
  }

  private def getCatalog(typeId:String,id:String,token:String):Option[MendeleyObject]=try{
    println(s"$id : $token")
    val response: HttpResponse[String] =Http(CatalogUrl)
                            .timeout(connTimeoutMs = 1000, readTimeoutMs = 5000)
                            .param("view","stats")
                            .param("access_token",token)
                            .param(typeId,id)
                            .asString  
    response.code match {
      case 200 => {
       //log.info(""+response.body.parseJson.prettyPrint)
        val mObj=parseJson(response.body)
        mObj match {
          case Some(e)=>Option(e)
          case None =>{
            log.error(s"$typeId : $id :Not Found")
            None
          }
        }
      }
      case 401 => 
        {
          println(response.body)
          println(response.statusLine)
          getCatalog(typeId,id,getAccessToken)
        }
      case _   => {
        log.error(response.statusLine)
        None
      }
    }
  }catch {
    case e: ConnectException => {
       log.error(e.toString);
       None
    } 
  }
  
  @throws(classOf[ConnectException])
  private def getAccessToken:String={
    val form=Seq(
          "client_id" ->ClientId,
          "client_secret"->SecretId,
          "grant_type" -> "client_credentials",
          "scope" -> "all"
        );
    val response: HttpResponse[String]=Http(OauthUrl)
                                .timeout(connTimeoutMs = 1000, readTimeoutMs = 5000)
                                .postForm(form).asString 
    response.code match {
      case 200 => {
        val token=response.body.parseJson
          .asJsObject("Invalid Json")
          .getFields("access_token")(0).toString.replaceAll("\"", ""); 
        Curator.setData(token)
        token
      }
      case _   => {
        println(response.statusLine)
        ""
      }
    }       
  }  
}