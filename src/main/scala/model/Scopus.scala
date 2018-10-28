package main.scala.model;

/**
 * @author terry
 */

import scala.io.Source
import scala.xml._
import org.joda.time.DateTime

import main.scala.feed.{Mendeley=>Api}    
@SerialVersionUID(15L)
class Scopus extends Serializable {
   var  scopusEid,scopusPii,scopusPmid,scopusDoi=""
   var  scopusAction=""
   var  scopusTitle,scopusVolume=""
   var  scopusDocType,scopusSourceType=""
   var  scopusSourceTitle=""
   var  scopusAitSortDate=""
   var  scopusSourceId=0
   var  scopusSubjCodes=None: Option[Set[Int]]
   var  scopusCitationsCount=0
   var  scopusFwci=0.0f
   var  scopusIssn="" 
   
   
   override def toString=s"""$scopusEid,$scopusPii,$scopusDoi,$scopusAction,
     #$scopusPmid,$scopusVolume,$scopusDocType,$scopusSourceType,
     #$scopusAitSortDate,
     #$scopusTitle\n""".stripMargin('#')
   
   def readFile:String={
    val filename = "70249115530.xml"
    Source.fromFile(filename).getLines.mkString
  }
   
   def getMendeleyObject(api:Api)={
     val mObj=api.getDocument(title=this.scopusTitle,
                                 scopus=this.scopusEid,
                                 doi=this.scopusDoi,
                                 pmid=this.scopusPmid,
                                 issn=this.scopusIssn)
     mObj match{
       case m:Mendeley=>Some(m)
       case _=>None
     }
   }       
   
   def parseInt(str:String):Int=try{
     str.toInt
   }catch{
     case e:NumberFormatException=>0
   }
   
   def getDate(day:String,month:String,year:String)={
        var m=parseInt(month);
        var y=parseInt(year);
        if(m<1 || m>12)m=1;//invalid month set to 1 
        if(y>0)y+"-"+m+"-"+day else ""
   }

  def parseXML(content:String):this.type=try{
    if (content.isEmpty()) return this;
    
    val element=XML.loadString(content)
    
    val day= (element  \\"date-sort" \\"@day").text
    val month= (element \\"date-sort" \\"@month").text 
    val year= (element  \\"date-sort" \\"@year").text
    
    this.scopusAitSortDate=getDate(day,month,year)
    
    this.scopusSourceType=(element \\"source" \\"@type").text
    this.scopusSourceId=parseInt((element  \\"source" \\"@srcid").text)
    this.scopusSourceTitle=(element  \\"source" \\"sourcetitle").text
    
    val classifications=(element  \\"classifications").filter(n=>{
      (n\\"classifications"\\"@type").text=="ASJC"
    }).flatMap { x => x\\"classification" }.map { x => parseInt(x.text) }
    
    this.scopusSubjCodes=Some(classifications.toSet); 
    
    this.scopusVolume=(element\\"volume").text
//        this.scopusDoi=(element\\"doi").find(n=>n.toString.contains("xocs:doi"))
//                              .map { x =>x.text }.getOrElse("")
                          
    val issue=(element\\"issue").find(n=>n.toString.contains("xocs:issue"))
                                .map { x =>x.text }.getOrElse("")
    
    this.scopusDocType=(element\\"doctype").find(n=>n.toString.contains("cto:doctype"))
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
    val nodeText=(element\\"titletext")
    this.scopusTitle= if(nodeText.isEmpty)"" else nodeText(0).text; 
      this;
    }catch {
      case e:org.xml.sax.SAXParseException=>{
        println(e)
        this
      }
    }  
}