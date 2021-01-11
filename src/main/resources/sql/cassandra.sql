CREATE KEYSPACE IF NOT EXISTS ams 
WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 2 };

use ams;
 
DROP TABLE IF EXISTS eid_articles; 
DROP TABLE IF EXISTS pii_eid_mapping;
DROP TABLE IF EXISTS doi_eid_mapping;
DROP TABLE IF EXISTS pmid_eid_mapping;
DROP TABLE IF EXISTS article_benchmarks;

CREATE TABLE IF NOT EXISTS  eid_articles (
    scopus_eid                                  text,
    scopus_pii                                  text,   
    scopus_doi                                  text,
    scopus_pmid                                 text,
    scopus_doc_type                             text,
    scopus_source_type                          text,
    scopus_source_id                            bigint,
    scopus_ait_sort_date                        text,
    scopus_title                                text,
    scopus_volume                               text,
    scopus_issue                                text,
    scopus_issn                                 text,
    scopus_subj_codes                           set<int>,
    scopus_authors                              text,
    scopus_citations_count                      bigint,  
    scopus_fwci                                 float,
    altmetric_citation_id                       bigint,
    altmetric_users_count                       map<text,int>, 
    altmetric_sources_count                     map<text,int>, 
    altmetric_sources_by_country                text,
    mendeley_id                                 text,
    mendeley_url                                text,
    mendeley_readers_by_country                 map<text,int>,    
    mendeley_readers_by_discipline              map<text,int>, 
    mendeley_readers_by_status                  map<text,int>,  
    mendeley_groups_count                       bigint,       
    mendeley_readers_count                      bigint, 
--  TO BE REMOVED    buckets_count                               map<text,int>, 
    buckets                                     text, 
    percentiles                                 text,
    percentiles_last_update                     timestamp,
    fwci_last_update                            timestamp,
    citations_last_update                       timestamp,
    buckets_last_update                         timestamp,
    scopus_last_update                          timestamp,
    altmetric_last_update                       timestamp,
    mendeley_last_update                        timestamp,
    code_version                                text,
 PRIMARY KEY (scopus_eid))
WITH comment='scopus articles only';

-- TO DO : Remove old column names 
ALTER TABLE eid_articles DROP buckets_count;
-- ALTER TABLE eid_articles ADD buckets text;
-- ALTER TABLE eid_articles ADD scopus_fwci float;
-- ALTER TABLE eid_articles ADD scopus_title text;
-- ALTER TABLE eid_articles ADD scopus_issn text;
-- ALTER TABLE eid_articles ADD scopus_source_id bigint;
-- ALTER TABLE eid_articles ADD fwci_last_update timestamp;
-- ALTER TABLE eid_articles ADD citations_last_update timestamp;
-- ALTER TABLE eid_articles ADD altmetric_users_count  map<text,int>;
-- 

ALTER KEYSPACE system_auth WITH REPLICATION =
  { 'class' : 'SimpleStrategy', 'replication_factor' : 2 };

CREATE TABLE IF NOT EXISTS  pii_eid_mapping (
    pii         text,   
    eid         text,
    last_update timestamp,
 PRIMARY KEY (pii))
WITH comment='science direct pii to scopus eid mapping table';

CREATE TABLE IF NOT EXISTS  doi_eid_mapping (
    doi         text, 
    eid         text,  
    last_update timestamp,
 PRIMARY KEY (doi))
WITH comment='doi to scopus eid mapping table';

CREATE TABLE IF NOT EXISTS  pmid_eid_mapping (
    pmid        text,   
    eid         text,
    last_update timestamp,
 PRIMARY KEY (pmid))
WITH comment='pmid to scopus eid mapping table';

-- CREATE TABLE IF NOT EXISTS  issn_eid_mapping (
--     issn        text,   
--     eid         text,
--     last_update timestamp,
--  PRIMARY KEY (pmid))
-- WITH comment='issn to scopus eid mapping table';

CREATE TABLE IF NOT EXISTS  mendeley_eid_mapping (
    mendeley_id text,   
    eid         text,
    last_update timestamp,
 PRIMARY KEY (pmid))
WITH comment='mendeley to scopus eid mapping table';


CREATE TABLE IF NOT EXISTS  article_benchmarks (
    id              text,  -- "3206:le:2005:7:540"
    benchmark_type  text,
    description     text,
    count           int,
---confidence      text,
    subj_code       int,
    doc_type        text,
    pub_year        int,
    pub_month       int,
    period          int,
    scores          map<text,text>,
    percentiles     map<text,text>,
    last_update     timestamp,
 PRIMARY KEY (id))
WITH comment='Article Benchmarks by doctype or subject look up table';


COPY subject_codes(code,subject,abbrev,detail) from './subject.txt' WITH DELIMITER = '|' AND QUOTE = '''' AND ESCAPE = '''' AND NULL = '<null>';





