package main.scala.model;
  
/**
 * @author terry
 */

import main.scala.utils.Levenshtein
@SerialVersionUID(10L)
class Mendeley(private val mendeleyTitle:String,
  private val identifiers:Map[String,String]) extends Serializable {
//        println(identifiers)
  var scopusEid: String = identifiers.getOrElse("scopus", "")
  var mendeleyId=""
  var mendeleyUrl=""
  var mendeleyReadersCount=0
  var mendeleyGroupsCount=0  
  var mendeleyReadersByCountry=Map.empty[String,Int] 
  var mendeleyReadersByDiscipline=Map.empty[String,Int]  
  var mendeleyReadersByStatus=Map.empty[String,Int] 
  
   def isTitleMatch(source:String):Boolean={
      if (!source.isEmpty() && !this.mendeleyTitle.isEmpty()){
        val pattern = "\\W".r
        val s1= pattern replaceAllIn(source, "")toLowerCase()
        val s2= pattern replaceAllIn(this.mendeleyTitle, "")toLowerCase()
        val d=Levenshtein.distance(s1,s2)
        val l=if(s1.length>=s2.length)s1.length else s2.length
        val p=((l-d).toFloat/l)*100;
        
//            println("%s \n %s \n %d \n %d \n %f".format(s1, s2, d,l, p))
        if (p>=90) true else false
      }else {
        false
      }
    }    
    override def toString=s"""$mendeleyId,$scopusEid
         #$mendeleyTitle,
         #$mendeleyUrl,
         #$mendeleyReadersCount,$mendeleyGroupsCount\n""".stripMargin('#')    
} 
