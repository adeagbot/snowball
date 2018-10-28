package main.scala.utils;

/**
 * @author terry
 */
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import scala.io.Source

object CountryNames {
  private val codes=readFile;
  
  @throws(classOf[FileNotFoundException])
  @throws(classOf[IOException])
  private def readFile:Option[Map[String,String]]={
      try{
        val filename = System.getProperty("user.dir")+
                      File.separator+"src"+
                      File.separator+"main"+
                      File.separator+"resources"+
                      File.separator+"country_codes.txt";
        
        val m=Source.fromFile(filename).getLines().drop(1).map(i=>{
          val a=i.split(';');
          a(0).trim->a(1).trim
        }).toMap
        Some(m)
      }catch{
        case e: FileNotFoundException => {
          println("Couldn't find that file.")
          throw e
        }
        case e: IOException => {
          println("Had an IOException trying to read that file")
          throw e
        }
      }
  }
  
  @throws(classOf[NoSuchElementException])
  def getCode(country:String):String=codes.get(country.toUpperCase)
  
  
  def getAllCodes =codes.get.values.toArray
  
  def getNames(code:String):Array[String]={
    codes.get.filter(i=>i._2==code.toUpperCase).map(i=>i._1).toArray
  }
  
  def getAllNames=codes.get.keys.toArray    
}


