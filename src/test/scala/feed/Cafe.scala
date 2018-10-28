package test.feed

/**
 * @author terry
 */

import main.scala.feed.Cafe
import main.scala.feed.Mendeley

import main.scala.model.{ScienceDirect,Scopus}

object CafeTest {
   //Cafe.scienceDirectMessages.foreach { println }
//    println(Cafe.fetched("sc-ani-xml-prod", "84918592010"))
//    println(Cafe.fetched("sc-apr-xml-prod", "7006261695"))
//    println(Cafe.fetched("smcsdxcrxml-prod", "S0749379714002724"))  
  
    val mendeley=Mendeley.getInstance
  
    Cafe.scopusMessages
      .map{Cafe.getScopusObject}
      .map(scopus=>(scopus,scopus.getMendeleyObject(Mendeley.getInstance)))
    .foreach { println }
//    
    Cafe.scienceDirectMessages
    .map{Cafe.getScienceDirectObject}
    .map(sd=>(sd,sd.getMendeleyObject(Mendeley.getInstance)))
    .foreach { println }
    
    mendeley.close  
}