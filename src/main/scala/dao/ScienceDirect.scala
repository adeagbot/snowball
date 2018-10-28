package main.scala.dao;


/**
 * @author terry
 */

import org.apache.spark.SparkContext    
import com.datastax.spark.connector._
import main.scala.model.{ScienceDirect=>ScienceDirectObject}
object ScienceDirect {
  
    
    def create(p:ScienceDirectObject){
  //            query='INSERT INTO '+cf+' (scienceDirect_eid,scienceDirect_pii,scienceDirect_ait_sort_date,'+
  //                  'scienceDirect_doi,scienceDirect_pmid,scienceDirect_title,scienceDirect_volume,scienceDirect_issue,'+
  //                  'scienceDirect_issn,scienceDirect_subj_codes,scienceDirect_authors,scienceDirect_source_type,'+
  //                  'scienceDirect_source_id,scienceDirect_doc_type,scienceDirect_last_update,code_version) '+
  //                  'VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)';     
    }
    def find(id:Int){
      
    }
    def update(p:ScienceDirectObject){
  //query='UPDATE '+cf+' SET code_version=?,scienceDirect_last_update=?';    
    }
    def delete(id:Int)  {
  //   query='DELETE FROM '+cf_eid+' WHERE scienceDirect_eid=?'; 
    }
  }