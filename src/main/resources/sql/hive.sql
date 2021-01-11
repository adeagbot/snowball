-- DROP DATABASE IF EXISTS ams CASCADE;
CREATE DATABASE IF NOT EXISTS ams;

USE ams;

DROP TABLE IF EXISTS scopus_fwci;
DROP TABLE IF EXISTS scopus_articles;
DROP TABLE IF EXISTS scopus_eids;
DROP TABLE IF EXISTS scopus_meta;
DROP TABLE IF EXISTS scopus_citations;
DROP TABLE IF EXISTS scopus_mendeley;
DROP TABLE IF EXISTS scopus_classifications;
DROP TABLE IF EXISTS scopus_mendeley;
DROP TABLE IF EXISTS scopus_metrics;

DROP TABLE IF EXISTS altmetric_citations;
DROP TABLE IF EXISTS altmetric_id;
DROP TABLE IF EXISTS altmetric_users;
DROP TABLE IF EXISTS altmetric_posts;
DROP TABLE IF EXISTS altmetric_links;
DROP TABLE IF EXISTS altmetric_twitter;
DROP TABLE IF EXISTS altmetric_videos;
DROP TABLE IF EXISTS altmetric_pubpeer;
DROP TABLE IF EXISTS altmetric_publons;

DROP TABLE IF EXISTS mendeley;

DROP TABLE IF EXISTS benchmark_subject;
DROP TABLE IF EXISTS benchmark_doctype;

DROP TABLE IF EXISTS wiki_citations;
DROP TABLE IF EXISTS wiki_pmid_pmc_doi;

DROP TABLE IF EXISTS datasift_twitter;

DROP TABLE IF EXISTS twitter_histogram;
DROP TABLE IF EXISTS twitter;

CREATE EXTERNAL TABLE IF NOT EXISTS scopus_fwci(
    id   BIGINT,
    fwci FLOAT
) ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   COLLECTION ITEMS TERMINATED BY ';'
   NULL DEFINED AS ''  ;

CREATE EXTERNAL TABLE IF NOT EXISTS scopus_classifications (
    classification  STRING,
    code            INT,
    detail          STRING,
    abbreviation     STRING
) ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   COLLECTION ITEMS TERMINATED BY ';'
   NULL DEFINED AS ''  ;

CREATE EXTERNAL TABLE IF NOT EXISTS scopus_articles (
    eid             STRING,
    doc_pmid        BIGINT,
    doc_pii         STRING,
    ait_sort_date   STRING,
    source_type     STRING,
    doc_type        STRING,
    subj_codes      Array<BIGINT>,
    citing_count    BIGINT,
    cited_count     BIGINT,
    doc_doi         STRING 
) ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   COLLECTION ITEMS TERMINATED BY ';'
   NULL DEFINED AS ''  ;

LOAD DATA INPATH '/user/admin/scopus' OVERWRITE INTO TABLE scopus_articles;


CREATE EXTERNAL TABLE IF NOT EXISTS scopus_metrics (
    eid                 STRING,
    pii                 STRING,
    pub_date            STRING,
    doc_type            STRING,
    subj_code           BIGINT,
    subj_description    STRING,
    citations           BIGINT,
    fwci                FLOAT,
    groups              BIGINT,

    blogs               BIGINT,
    f1000               BIGINT,
    pubpeer             BIGINT, 
    publons             BIGINT, 
    policy              BIGINT,
    qa                  BIGINT, 
    wikipedia           BIGINT,
    twitter             BIGINT,
    facebook            BIGINT,
    googleplus          BIGINT,
    linkedin            BIGINT,   
    pinterest           BIGINT,
    weibo               BIGINT,
    reddit              BIGINT,
    video               BIGINT,
    news                BIGINT,
    citeulike           BIGINT,
    connotea            BIGINT,
    mendeley            BIGINT,

    mass_media          BIGINT,
    scholar_commentary  BIGINT,
    social_activity     BIGINT,
    scholar_activity    BIGINT,
    
    doctype_percentiles MAP<STRING,BIGINT>,
    subject_percentiles MAP<STRING,BIGINT>,

    last_update         STRING
) ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   COLLECTION ITEMS TERMINATED BY ';'
   MAP KEYS TERMINATED BY ':'
   NULL DEFINED AS '' 
STORED AS TEXTFILE;

CREATE EXTERNAL TABLE IF NOT EXISTS scopus_meta (
    eid            STRING,
    doi            STRING,
    pmid           STRING,
    pii            STRING,
    pui            STRING,
    title          STRING, 
    issue          STRING,
    issn           STRING,
    volume         STRING,
    doc_type       STRING,
    source_type    STRING,
    ait_sort_date  TIMESTAMP,
    authors        ARRAY<STRUCT<id:STRING, initials:STRING,surname:STRING>>,    
    sort_year      STRING,
    document_type  STRING,
    subj_codes     Array<BIGINT>,
    action         STRING,
    timestamp      TIMESTAMP)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'sc-ani-xml-prod/2-s2.0'  ,
              'es.mapping.names' = 'sort_year:sort-year,document_type:document-type',  
              'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30','es.query' = '?q=*:*' );

CREATE EXTERNAL TABLE IF NOT EXISTS scopus_eids  (
    eid STRING
)ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   NULL DEFINED AS ''  ; 

CREATE EXTERNAL TABLE IF NOT EXISTS scopus_citations  (
    eid STRING,
    cited_count  BIGINT) 
ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   COLLECTION ITEMS TERMINATED BY ';'
   NULL DEFINED AS ''  ; 

CREATE EXTERNAL TABLE IF NOT EXISTS mendeley (
    id            STRING,
    scopus        STRING,
    title         STRING,
    doi           STRING,
    isbn          STRING,
    issn          STRING,
    pmid          STRING,
    arxiv         STRING,
    link          STRING,
    reader_count  BIGINT,
    group_count   BIGINT,
    by_country    MAP<STRING,BIGINT>,
    by_discipline MAP<STRING,BIGINT>,
    by_status     MAP<STRING,BIGINT>)
STORED BY         'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'mendeley/count',
    'es.mapping.names' = 'by_country:reader_count_by_country,by_discipline:reader_count_by_discipline,by_status:reader_count_by_academic_status',
    'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30',
    'es.query' = '?q=*:*' );

-- CREATE EXTERNAL TABLE elastic_scopus (
--     eid STRING,
--     doc_doi STRING,
--     doc_pmid BIGINT,
--     doc_pii STRING,
--     doc_mendeley_id STRING,
--     ait_sort_date STRING,
--     source_type STRING,
--     doc_type STRING,
--     subj_code Array<BIGINT>,
--     citing_count BIGINT,
--     cited_count  BIGINT) 
-- STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
-- TBLPROPERTIES('es.resource' = 'scopus/article'  ,
--               'es.mapping.id'='eid',
--               'es.mapping.names' ='doc_doi:doi',
--               'es.write.operation'='upsert',
--               'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30');
-- insert data to Elasticsearch FROM another TABLE called 'source'
-- INSERT OVERWRITE TABLE elastic_scopus  SELECT s.*  FROM scopus_articles s;


CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_citations (
    id                           BIGINT,
    pubdate                      TIMESTAMP,
    first_seen_on                TIMESTAMP,
    doi                          STRING,
    pmid                         STRING,
    arxiv_id                     STRING,
    scopus                       STRING,
    volume                       STRING,
    issue                        STRING,
    handle                       STRING,
    journal                      STRING,
    sources_count                MAP<STRING,BIGINT>,
    users_count                  MAP<STRING,BIGINT>,
    geo_twitter                  MAP<STRING,BIGINT>,
    geo_weibo                    MAP<STRING,BIGINT>,
    links                        ARRAY<STRING>,   
    links_count                  BIGINT,
    funders_count                BIGINT)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'altmetric/citation'  ,
              'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30',
              'es.mapping.names' = 'geo_twitter:geo.twitter,geo_weibo:geo.weibo',
              'es.query' = '?q=*:*' );

CREATE TABLE IF NOT EXISTS altmetric_id 
        ROW FORMAT DELIMITED
           FIELDS TERMINATED BY ','
           NULL DEFINED AS '' 
        STORED AS TEXTFILE  AS SELECT id FROM altmetric_citations; 

-- Summary of articles over time  
CREATE TABLE IF NOT EXISTS altmetric_monthly (
   source           STRING,
   bucket           ARRAY<STRING>, 
   citation_id      BIGINT,
   year             BIGINT,
   month            BIGINT,
   count            BIGINT, -- Count of ALL usage
   timestamp        TIMESTAMP )
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = '*/monthly'  ,'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30','es.query' = '?q=*:*');

-- Summary of articles over time  
CREATE TABLE IF NOT EXISTS altmetric_weekly (
   source           STRING,
   bucket           ARRAY<STRING>, 
   citation_id      BIGINT,
   year             BIGINT,
   week             BIGINT,
   count            BIGINT, -- Count of ALL usage
   timestamp        TIMESTAMP )
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = '*/weekly'  ,'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30','es.query' = '?q=*:*');


CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_users (
   id               STRING,
   source           STRING,
   id_on_source     STRING,
   image	    STRING,
   name             STRING,
   url              STRING, 
   geo              STRUCT<country:STRING,ln:STRING,lt:STRING>,
   description      STRING,
   timestamp        TIMESTAMP)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = '*/user'  ,'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30','es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_posts (
    id            STRING,
    timestamp     TIMESTAMP,
    bucket        ARRAY<STRING>,
    citation_ids  ARRAY<BIGINT>,
    posted_on     TIMESTAMP,
    source        STRING,
    author        STRUCT<id_on_source:STRING, image:STRING,name:STRING, url:STRING,description:STRING>,
    summary       STRING,
    title         STRING,
    tweet_id      STRING,
    rt            ARRAY<STRING>,
    license       STRING,
    url           STRING)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = '*/post'  ,'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_twitter (
    id            STRING,
    timestamp     TIMESTAMP,
    bucket        ARRAY<STRING>,
    citation_ids  ARRAY<BIGINT>,
    posted_on     TIMESTAMP,
    summary       STRING,
    author        STRUCT<id_on_source:STRING, image:STRING,name:STRING, url:STRING,description:STRING>,
    rt            ARRAY<STRING>,
    license       STRING,
    url           STRING)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'twitter/post'  ,
'es.mapping.names' = 'id:tweet_id',
'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,
'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_qa (
    id            STRING,
    timestamp     TIMESTAMP,
    bucket        ARRAY<STRING>,
    citation_ids  ARRAY<BIGINT>,
    posted_on     TIMESTAMP,
    source        STRING,
    author        STRUCT<id_on_source:STRING>,
    summary       STRING,
    title         STRING,
    url           STRING)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'qa/post'  ,
'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,
'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_policy (
    id            STRING,
    timestamp     TIMESTAMP,
    bucket        ARRAY<STRING>,
    citation_ids  ARRAY<BIGINT>,
    posted_on     TIMESTAMP,
    source        STRING,
    author        STRUCT<image:STRING>,
    summary       STRING,
    title         STRING,
    license       STRING,
    url           STRING)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'policy/post'  ,
'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,
'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_videos (
    id            STRING,
    timestamp     TIMESTAMP,
    bucket        ARRAY<STRING>,
    citation_ids  ARRAY<BIGINT>,
    posted_on     TIMESTAMP,
    source        STRING,
    author        STRUCT<id_on_source:STRING, name:STRING>,
    summary       STRING,
    title         STRING,
    license       STRING,
    url           STRING)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'video/post'  ,
'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_f1000 (
    id            STRING,
    timestamp     TIMESTAMP,
    bucket        ARRAY<STRING>,
    citation_ids  ARRAY<BIGINT>,
    posted_on     TIMESTAMP,
    source        STRING,
    author        STRUCT<id_on_source:STRING>,
    f1000_classes ARRAY<STRING>,
    f1000_score   STRING,
    summary       STRING,
    title         STRING,
    license       STRING,
    url           STRING)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'f1000/post'  ,
'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_pubpeer (
    id                          STRING,
    timestamp                   TIMESTAMP,
    bucket                      ARRAY<STRING>,
    citation_ids                ARRAY<BIGINT>,
    posted_on                   TIMESTAMP,
    source                      STRING,
    author                      STRUCT<id_on_source:BIGINT, name:STRING,url:STRING>,    
    pubpeer_publication_url     STRING,
    summary                     STRING,
    title                       STRING,
    license                     STRING,
    url                         STRING)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'pubpeer/post'  ,
'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,
'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_publons (
    id                          STRING,
    timestamp                   TIMESTAMP,
    bucket                      ARRAY<STRING>,
    citation_ids                ARRAY<BIGINT>,
    posted_on                   TIMESTAMP,
    source                      STRING,
    author                      STRUCT<id_on_source:BIGINT, name:STRING,url:STRING>,    
    publons_article_url         STRING,
    publons_quality_score       BIGINT,
    publons_review_type         STRING,
    publons_significance_score  BIGINT,
    publons_weighted_average    DOUBLE,
    summary                     STRING,
    title                       STRING,
    license                     STRING,
    url                         STRING)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'publons/post'  ,
'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,
'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_wikipedia (
    posted_on     TIMESTAMP,
    source        STRING,
    author        STRUCT<name:STRING, url:STRING>,
    summary       STRING,
    title         STRING,
    wiki_lang     STRING,
    page_url      STRING,
    license       STRING,
    url           STRING,
    timestamp     TIMESTAMP,
    bucket        ARRAY<STRING>,
    citation_ids  ARRAY<BIGINT>)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'wikipedia/post'  ,'es.nodes'= 
'10.182.2.77,10.182.2.31,10.182.2.30' ,'es.query' = '?q=*:*');


CREATE EXTERNAL TABLE IF NOT EXISTS altmetric_links (
    id            BIGINT,
    doi           STRING,
    pmid          STRING,
    arxiv_id      STRING,
    scopus        STRING,
    pdf_url       STRING,
    links         ARRAY<STRING>)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'altmetric/citation'  ,
              'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30',
              'es.query' = '?q=*:*' );

CREATE EXTERNAL TABLE IF NOT EXISTS benchmark_subject(
    id                  STRING,
    code                BIGINT,
    description         STRING,
    period              BIGINT,   
    year                BIGINT,
    month               BIGINT,
    count               BIGINT,
    timestamp           TIMESTAMP) 
STORED BY         'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'benchmark/subject',
    'es.mapping.names' = 'code:subj_code,year:pub_year,month:pub_month',
    'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30',
    'es.query' = '?q=*:*' );

CREATE EXTERNAL TABLE IF NOT EXISTS benchmark_doctype(
    id                  STRING,
    code                BIGINT,
    description         STRING, 
    period              BIGINT,  
    year                BIGINT,
    month               BIGINT,
    count               BIGINT,
    timestamp           TIMESTAMP) 
STORED BY         'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'benchmark/doctype',
    'es.mapping.names' = 'code:subj_code,type:doc_type,year:pub_year,month:pub_month',
    'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30',
    'es.query' = '?q=*:*' );


CREATE EXTERNAL TABLE IF NOT EXISTS twitter (
     id            STRING,
     posted_on     TIMESTAMP,
     id_on_source  STRING)
    ROW FORMAT DELIMITED
        FIELDS TERMINATED BY ','
        NULL DEFINED AS '' 
        STORED AS TEXTFILE  ; 

CREATE EXTERNAL TABLE IF NOT EXISTS twitter_histogram (
     id            STRING,
     start_date    STRING,
     end_date      STRING,
     start_time    BIGINT, 
     count         BIGINT,
     timestamp     STRING)
    ROW FORMAT DELIMITED
        FIELDS TERMINATED BY ','
        NULL DEFINED AS '' 
        STORED AS TEXTFILE  ;

CREATE EXTERNAL TABLE IF NOT EXISTS datasift_twitter (
    id            STRING,
    author        STRING,
    summary       STRING,
    links         ARRAY<STRING>)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource' = 'datasift/twitter'  ,
'es.mapping.names' = 'id:interaction.twitter.id,
 author:interaction.interaction.author.username,
 summary:interaction.twitter.text,
 links:interaction.links.url',
'es.nodes'= '10.182.2.77,10.182.2.31,10.182.2.30' ,
'es.query' = '?q=*:*');

CREATE EXTERNAL TABLE IF NOT EXISTS wiki_citations(   
    page_id     BIGINT,
    page_title  STRING,
    rev_id      BIGINT,
    timestamp   STRING,
    type        STRING,
    id          STRING
)ROW FORMAT DELIMITED
   FIELDS TERMINATED BY '\t'

-- wget http://downloads.figshare.com/article/public/1299540

CREATE EXTERNAL TABLE IF NOT EXISTS wiki_pmid_pmc_doi(   
    pmid        BIGINT,
    pmcid       STRING,
    doi         STRING
)ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','

-- wget ftp://ftp.ebi.ac.uk/pub/databases/pmc/DOI/PMID_PMCID_DOI.csv.gz
-- gunzip PMID_PMCID_DOI.csv.gz

CREATE TABLE scopus_subj_codes AS SELECT Transform (eid,subj_code) 
USING  'node /home/ubuntu/imetric/cafe/splitter.js' AS (eid,subj_code) 
FROM scopus_articles;

CREATE TABLE IF NOT EXISTS scopus_subj AS SELECT  scopus_subj_codes.*,subj_classifications.subject 
FROM scopus_subj_codes LEFT JOIN  subj_classifications 
on (scopus_subj_codes.subj_code=subj_classifications.code)

CREATE TABLE IF NOT EXISTS scopus_articles_subj AS SELECT scopus_subj.*,
    scopus_articles.doc_pii,
    scopus_articles.ait_sort_date,
    scopus_articles.source_type,
    scopus_articles.doc_type
     FROM scopus_subj LEFT JOIN scopus_articles  ON (scopus_subj.eid=scopus_articles.eid)

CREATE TABLE IF NOT EXISTS scopus_mendeley 
ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   COLLECTION ITEMS TERMINATED BY ';'
   MAP KEYS TERMINATED BY ':'
   NULL DEFINED AS ''  
 STORED AS TEXTFILE  AS SELECT * FROM mendeley;

CREATE TABLE scopus_mendeley_citations 
ROW FORMAT DELIMITED
   FIELDS TERMINATED BY ','
   COLLECTION ITEMS TERMINATED BY ';'
   MAP KEYS TERMINATED BY ':'
   NULL DEFINED AS ''  
 STORED AS TEXTFILE  AS 
SELECT y.*,citations.id,citations.sources_count FROM 
(
  SELECT x.*,scopus_mendeley.reader_count FROM  (SELECT scopus_subj.*,
    scopus_articles.ait_sort_date,
    scopus_articles.source_type,
    scopus_articles.doc_type,
    scopus_articles.citing_count,
    scopus_articles.cited_count
     FROM scopus_subj LEFT JOIN   scopus_articles  ON (scopus_subj.eid=scopus_articles.eid)
 ) x  LEFT JOIN  scopus_mendeley ON (x.eid=scopus_mendeley.eid)
 )y LEFT JOIN  citations ON (y.eid=citations.scopus) 

INSERT OVERWRITE TABLE twitter select id,posted_on,author.id_on_source from altmetric_twitter where summary is null;
INSERT OVERWRITE TABLE scopus_eids  SELECT eid FROM scopus_meta WHERE action!="d" ;
INSERT OVERWRITE TABLE scopus_mendeley  SELECT * FROM  mendeley; 
INSERT OVERWRITE TABLE altmetric_id SELECT id FROM altmetric_citations;