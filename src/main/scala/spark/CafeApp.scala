package main.scala.spark;

/**
 * @author terry
 */

import main.scala.feed.Cafe
import main.scala.feed.Mendeley

import main.scala.model.{ScienceDirect,Scopus}
 
import com.datastax.spark.connector.cql.CassandraConnector

object CafeApp extends MainApp {
    val mendeley=Mendeley.getInstance
    
   val rdd=sc.parallelize(Cafe.pages)
              .flatMap {Cafe.scopusMessages}
              //.map{Cafe.getScopusObject}
            //  .map(scopus=>(scopus,scopus.getMendeleyObject(Mendeley.getInstance)))
                  .collect()
                  .foreach(println)                  
  
 //rdd.saveAsTextFile("hdfs://localhost:9000/user/ams/cafe");                   
//    
    Cafe.scienceDirectMessages
    .map{Cafe.getScienceDirectObject}
    .map(sd=>(sd,sd.getMendeleyObject(Mendeley.getInstance)))
    .foreach { println }
    
    mendeley.close
    

//val lines = sc.cassandraTable[(String, String, String, String)](CASSANDRA_SCHEMA, table).
//  select("a","b","c","d").
//  where("d=?", d).cache()
// val connector = CassandraConnector(defaultConf)
//CassandraConnector(conf).withSessionDo { session =>
//  session.execute("CREATE KEYSPACE test2 WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 }")
//  session.execute("CREATE TABLE test2.words (word text PRIMARY KEY, count int)")
//}  
//   
//lines.foreachPartition(partition => {
//    val session: Session = connector.openSession //once per partition
//    partition.foreach{elem => 
//        val delete = s"DELETE FROM "+CASSANDRA_SCHEMA+"."+table+" where     channel='"+elem._1 +"' and ctid='"+elem._2+"'and cvid='"+elem._3+"';"
//        session.execute(delete)
//    }
//    session.close()
//}) 
   
// private def verifyKeyValueTable(tableName: String) {
//    conn.withSessionDo { session =>
//      val result = session.execute(s"""SELECT * FROM "$ks".""" + tableName).all()
//      result should have size 3
//      for (row <- result) {
//        Some(row.getInt(0)) should contain oneOf(1, 2, 3)
//        Some(row.getLong(1)) should contain oneOf(1, 2, 3)
//        Some(row.getString(2)) should contain oneOf("value1", "value2", "value3")
//      }
//    }
//  }   

//  def makeBatchBuilder(session: Session): (BoundStatement => Any, BatchSize, Int, Iterator[(Int, String)]) => GroupingBatchBuilder[(Int, String)] = {
//    val stmt = session.prepare(s"""INSERT INTO "$ks".tab (id, value) VALUES (:id, :value)""")
//    val boundStmtBuilder = new BoundStatementBuilder(rowWriter, stmt)
//    val batchStmtBuilder = new BatchStatementBuilder(Type.UNLOGGED, rkg, ConsistencyLevel.LOCAL_ONE)
//    new GroupingBatchBuilder[(Int, String)](boundStmtBuilder, batchStmtBuilder, _: BoundStatement => Any, _: BatchSize, _: Int, _: Iterator[(Int, String)])
//  }   

}