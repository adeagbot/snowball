package main.scala.dao;

/**
 * @author terry
 */


object Scopus{
    
   import main.scala.model.{Scopus=>ScopusObject}
    
    def create(p:ScopusObject){
  //            query='INSERT INTO '+cf+' (scopus_eid,scopus_pii,scopus_ait_sort_date,'+
  //                  'scopus_doi,scopus_pmid,scopus_title,scopus_volume,scopus_issue,'+
  //                  'scopus_issn,scopus_subj_codes,scopus_authors,scopus_source_type,'+
  //                  'scopus_source_id,scopus_doc_type,scopus_last_update,code_version) '+
  //                  'VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)';     
    }
    def find(id:Int){
      
    }
    def update(p:ScopusObject){
  //query='UPDATE '+cf+' SET code_version=?,scopus_last_update=?';    
    }
    def delete(id:Int)  {
  //   query='DELETE FROM '+cf_eid+' WHERE scopus_eid=?'; 
    }
  }