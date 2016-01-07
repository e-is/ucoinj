ucoinj
======

uCoin Java API.


## Compile

Install required dependencies:

 - Install [libsodium](http://doc.libsodium.org/installation/index.html)

   - Linux: after [installation](http://doc.libsodium.org/installation/index.html), make sure the file 'libsodium.so' exists on: /usr/local/lib or /opt/local/lib.
     If not, create a symbolic link.

   - Windows: copy the file 'sodium.dll' into directory 'ucoinj-core/lib/'

 - Install [Maven 3](http://maven.apache.org/).

Get the source code, then compile using Maven:
 
```bash
$ mvn install
```
 
## Roadmap

Done : 
 - GET request :
   - /wot/*

In progress : 
 - POST request:
   - /wot/add
 
What TODO Next ?
  - /blockchain
  - /network


