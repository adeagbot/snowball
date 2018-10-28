package test.feed

/**
 * @author terry
 */

import main.scala.feed.Altmetric
import main.scala.feed.Mendeley

import main.scala.model.Altmetric

object AltmetricTest {

    val mendeley=Mendeley.getInstance
     println(Altmetric.getCitationObject("2382758").get.getMendeleyObject(Mendeley.getInstance))
    
    mendeley.close  
}