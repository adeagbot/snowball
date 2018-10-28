package main.scala.model;

@SerialVersionUID(16L)
class Benchmark extends Serializable {
  var scopusEid: String = ""
  var scopusDocType:String=""
  var scopusAitSortDate:String=""
  var scopusSubjCodes:Set[Int]=null
  var scopusCitationsCount: Int = 0  
  var mendeleyReadersCount: Int = 0
  var altmetricSourcesCount:Map[String,String]=null  
}    


