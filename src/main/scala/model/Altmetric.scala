package main.scala.model;

  
/**
 * @author terry
 */

 import scala.collection.mutable.HashMap       
 import main.scala.feed.{Mendeley=>Api}
 import main.scala.model.{Mendeley=>MendeleyObject}
 
  @SerialVersionUID(15L)
  class Altmetric(private val title:String,
                          private val doi:String,
                          private val pmid:String,
                          private val posts:HashMap[String,String]) extends Serializable {
    var scopusEid: String = ""
    var altmetricCitationId:String=""
    var altmetricSourcesCount=Map.empty[String,Int]
    var altmetricUsersCount:Map[String,Int]=Map.empty[String,Int]
    var altmetricSourcesByCountry:String="" 
    
 override def toString=s"""$altmetricCitationId,
   #$altmetricSourcesCount
   #$pmid,
   #$doi,
   #$title\n""".stripMargin('#')          
 def getMendeleyObject(api:Api)={
       val mObj=api.getDocument(title=this.title,
                                   doi=this.doi,
                                   pmid=this.pmid)
       mObj match{
         case m:MendeleyObject=>{
           this.scopusEid=m.scopusEid
           Some(m)
         }
         case _=>None
       }
     }           
 }       

