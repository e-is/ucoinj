ucoinj
======

uCoin Java Client API.



## Architecture

uCoinj has four main components :

 - shared: common classes
 
 - core-client: a Client API to access to a uCoin network.
   
 - elasticsearch: a tools to index all blockchain and more.
    
 - web: a web client (HTML + JS + REST service), elasticsearch for data navigation, payement and more !


## Test it

The elasticsearch component is ready to use !

 - Install Java JRE 7 or more.
 
    - Windows: see [Oracle web site](http://oracle.com/java/index.html)
    
    - Linux (Ubuntu):
 
```bash
sudo apt-get install openjdk-8-jre 
```

 - Install [libsodium](http://doc.libsodium.org/installation/index.html) (Linux only)
  
     - Linux: See [installation](http://doc.libsodium.org/installation/index.html). After installation, make sure the file 'libsodium.so' 
       exists on: /usr/local/lib or /opt/local/lib. If not, create a symbolic link.
       
     - Windows: no installation need (include in binaries) 
  
 - Download lastest release of file ucoinj-elasticsearch-X.Y-standalone.zip
 
 - Unzip, then start a elasticsearch node, just do :
 
```bash
unzip ucoinj-elasticsearch-X.Y-standalone.zip
cd ucoinj-elasticsearch-X.Y
./ucoinj-elasticsearch.sh start index -h <node_host> -p <node_port>
```

Example on meta_brouzouf test currency :

```bash
$ ./ucoinj-elasticsearch.sh start index -h  metab.ucoin.io -p 9201
2016-01-07 23:34:34,771  INFO Starting uCoinj :: ElasticSearch Indexer with arguments [start, index, -h, metab.ucoin.io, -p, 9201]
2016-01-07 23:34:34,856  INFO Application basedir: /home/user/.ucoinj-elasticsearch
2016-01-07 23:34:34,861  INFO Starts i18n with locale [fr] at [/home/user/.ucoinj-elasticsearch/data/i18n]
2016-01-07 23:34:35,683  INFO Starts ElasticSearch node with cluster name [ucoinj-elasticsearch] at [/home/user/.ucoinj-elasticsearch/data].
*** uCoinj :: Elasticsearch successfully started *** >> To quit, press [Q] or enter
2016-01-07 23:34:45,015  INFO Indexing last blocks of [meta_brouzouf] from peer [metab.ucoin.io:9201]
2016-01-07 23:35:01,597  INFO Indexing block #999 / 47144 (2%)...
2016-01-07 23:35:15,554  INFO Indexing block #1998 / 47144 (4%)...
2016-01-07 23:35:30,713  INFO Indexing block #2997 / 47144 (6%)...
2016-01-07 23:35:45,747  INFO Indexing block #3996 / 47144 (8%)...
...
2016-01-07 23:45:00,175  INFO All blocks indexed 
```

Show help :

```bash
$ ./ucoinj-elasticsearch.sh --help

Usage: ucoinj-elaticsearch.<sh|bat> <commands> [options]

Commands:

 start                            Start elastic search node
 index                            Index blocks from BMA Node
 reset-data                       Reset indexed data for the uCoin node's currency


Options:

 --help                           Output usage information
 -h --host <user>                          uCoin node host (with Basic Merkled API)
 -p --port <pwd>                           uCoin node port (with Basic Merkled API)

 -esh  --es-host <user>           ElasticSearch node host
 -esp  --es-port <pwd>            ElasticSearch node port

```

## Use it

When a blockchain currency has been indexed, you can test some fun queries :

 - get a block by number (e.g the block #0):
    
    http://localhost:9200/meta_brouzouf/block/0 -> with some additional metadata given by ES
    
    http://localhost:9200/meta_brouzouf/block/0/_source -> the original JSON block
        
 - Block #125 with only hash, dividend and memberCount:
 
    http://localhost:9200/meta_brouzouf/block/125/_source?_source=number,hash,dividend,membersCount
      
 - All blocks using a pubkey (or whatever):
 
    http://localhost:9200/meta_brouzouf/block/_search?q=9sbUKBMvJVxtEVhC4N9zV1GFTdaempezehAmtwA8zjKQ1
       
 - All blocks with a dividend, with only some selected fields (like dividend, number, hahs).
   Note : Query executed in command line, using CURL:

```bash
curl -XGET 'http://localhost:9200/meta_brouzouf/block/_search' -d '{
"query": {
        "filtered" : {
            "filter": {
                "exists" : { "field" : "dividend" }
            }
        }
    },
    "_source": ["number", "dividend", "hash", "membersCount"]
 }'
```
        
 - Get blocks from 0 to 100 

```bash
curl -XGET 'http://localhost:9200/meta_brouzouf/block/_search' -d '{
    "query": {
        "filtered" : {
            "filter": {
                "exists" : { "field" : "dividend" }
            }
        }
    }
}'
```


More documentation here :

  - ElasticSearch [official web site](http://www.elastic.co/guide/en/elasticsearch/reference/1.3/docs-get.html#get-source-filtering)
  
  - a good [tutorial](http://okfnlabs.org/blog/2013/07/01/elasticsearch-query-tutorial.html) 


## Compile
 
 Install required dependencies:
 
  - Install Java JDK (8 or more) 
  
  - Install [libsodium](http://doc.libsodium.org/installation/index.html)
 
    - Linux: after [installation](http://doc.libsodium.org/installation/index.html), make sure the file 'libsodium.so' exists on: /usr/local/lib or /opt/local/lib.
      If not, create a symbolic link.
 
    - Windows: copy the file 'sodium.dll' into directory 'ucoinj-core/lib/'
 
  - Install [Maven 3](http://maven.apache.org/).
 
 Compile:
  
```
	git clone https://github.com/ucoin-io/ucoinj.git
	cd ucoinj
	git submodule init
	git submodule sync
	git submodule update
	
    mvn install
```
 
 To package binaries :

```bash
$ mvn install -DperformRelase
```

## Roadmap

 - Detect blockchain rollback
 
 - Allow to store data in embedded database (SQLLite or HsqlDB) 
 
 - Refactor to only use HTML + JS in UI (remove wicket dependencies) 
