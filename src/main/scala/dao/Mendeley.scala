package main.scala.dao;
  
/**
 * @author terry
 */


//      }
//    case class MendeleyObject(id:String,
//                              title:String,
//                              link:String,
//                              reader_count:Int,
//                              group_count:Int){
//      var identifiers=None:Option[Map[String,String]]
//      var reader_count_by_academic_status=None:Option[Map[String,Int]]
//      var reader_count_by_discipline=None:Option[Map[String,Int]]
//      var reader_count_by_country=None:Option[Map[String,Int]]  
//    } 
//}

 object Mendeley{    
   import main.scala.model.{Mendeley=>MendeleyObject}
    def create(p:MendeleyObject){
  //            query='INSERT INTO '+cf+' (scopus_eid,scopus_pii,scopus_ait_sort_date,'+
  //                  'scopus_doi,scopus_pmid,scopus_title,scopus_volume,scopus_issue,'+
  //                  'scopus_issn,scopus_subj_codes,scopus_authors,scopus_source_type,'+
  //                  'scopus_source_id,scopus_doc_type,scopus_last_update,code_version) '+
  //                  'VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)';     
    }
    def find(id:Int){
      
    }
    def update(p:MendeleyObject){
  //        var query='UPDATE '+cf_eid+' SET code_version=?,mendeley_last_update=?,'+
  //                'mendeley_id=?,mendeley_url=?,mendeley_groups_count=?,mendeley_readers_count=?';      
    }
    def delete(id:Int)  {
  //   query='DELETE FROM '+cf_eid+' WHERE scopus_eid=?'; 
    }
  } 
