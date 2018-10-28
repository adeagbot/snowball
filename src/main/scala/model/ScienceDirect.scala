package main.scala.model;

/**
 * @author terry
 */

import org.apache.spark.SparkContext    
import com.datastax.spark.connector._

import scala.io.Source
import scala.xml._
import org.joda.time.DateTime    
import main.scala.feed.{Mendeley=>Api}    
@SerialVersionUID(15L)
class ScienceDirect extends Serializable {
   var  scienceDirectEid,scienceDirectPii,scienceDirectPmid,scienceDirectDoi=""
   var  scienceDirectAction=""
   var  scienceDirectTitle,scienceDirectVolume=""
   var  scienceDirectDocType,scienceDirectSourceType=""
   var  scienceDirectSourceTitle=""
   var  scienceDirectAitSortDate=""
   var  scienceDirectSourceId=0
   var  scienceDirectSubjCodes=None: Option[Set[Int]]
   var  scienceDirectCitationsCount=0
   var  scienceDirectFwci=0.0f
   var  scienceDirectIssn="" 
   
   
   override def toString=s"""$scienceDirectEid,$scienceDirectPii,$scienceDirectDoi,$scienceDirectAction,
     #$scienceDirectPmid,$scienceDirectVolume,$scienceDirectDocType,$scienceDirectSourceType,
     #$scienceDirectAitSortDate,
     #$scienceDirectTitle\n""".stripMargin('#')
   
   def readFile:String={
    val filename = "70249115530.xml"
    Source.fromFile(filename).getLines.mkString
  }
   def parseInt(str:String):Int=try{
     str.toInt
   }catch{
     case e:NumberFormatException=>0
   }
   def getMendeleyObject(api:Api)={
     val mObj=api.getDocument(doi=this.scienceDirectDoi,
                                 pmid=this.scienceDirectPmid,
                                 issn=this.scienceDirectIssn,
                                 title=this.scienceDirectTitle)
                             
     mObj match{
       case m:Mendeley=>Some(m)
       case _=>None
     }
   }           

  def parseXML(content:String):this.type=try{
    if (content.isEmpty()) return this;
    
    val element=XML.loadString(content)
    
    this.scienceDirectAitSortDate=(element\\"cover-date-start")
                          .find(n=>n.toString.contains("xocs:cover-date-start"))
                          .map { x =>x.text }.getOrElse("")
    
    this.scienceDirectSourceType=(element \\"source" \\"@type").text
    this.scienceDirectSourceId=parseInt((element  \\"source" \\"@srcid").text)
    this.scienceDirectSourceTitle=(element  \\"source" \\"sourcetitle").text
    
    val classifications=(element  \\"classifications").filter(n=>{
      (n\\"classifications"\\"@type").text=="ASJC"
    }).flatMap { x => x\\"classification" }.map { x => parseInt(x.text) }
    
    this.scienceDirectSubjCodes=Some(classifications.toSet); 
    
    this.scienceDirectVolume=(element\\"volume").text
//        this.scienceDirectDoi=(element\\"doi").find(n=>n.toString.contains("xocs:doi"))
//                              .map { x =>x.text }.getOrElse("")
                          
    val issue=(element\\"issue").find(n=>n.toString.contains("xocs:issue"))
                                .map { x =>x.text }.getOrElse("")
    
    this.scienceDirectDocType=(element\\"doctype").find(n=>n.toString.contains("cto:doctype"))
                                  .map { x =>x.text }.getOrElse("")
        
    val authors=(element\\"unique-author")
        .filter (n=>n.toString.contains("cto:unique-author"))
        .map(x=>{
          val initials=(x\\"auth-initials").text
          val surname=(x\\"auth-surname").text
          val id=(x\\"auth-id").text
          Map("initials"->initials,"surname"->surname,"id"->id)
        })
//    s.authors=Some(authors.toArray);    
//        authors.foreach(println);    
//        println
    val nodeText=(element\\"title")
    this.scienceDirectTitle= if(nodeText.isEmpty)"" else nodeText(0).text; 
    this;
  }catch {
    case e:org.xml.sax.SAXParseException=>{
      println(e)
      this
    }
  }
 }    