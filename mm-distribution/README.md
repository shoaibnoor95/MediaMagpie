HOWTO
=====

What for software you need?
---------------------------

+ git
+ apache ant 1.7.1
+ sun java 6


How to build?
-------------
 
    mvn clean
    mvn compile
    mvn test
    mvn package [-P warFile]


How to import project files into Eclipse?
-----------------------------------------

    mv eclipse:eclipse

Import all project below MediaMagpie/ folder into eclipse


How to start jetty?
-------------------

Extract the distribution file mm-distribution-<Version>.jar into an arbitrary folder.
Go into this folder's /bin subdirectory and run
 
    mediamagpie.sh start

open your browser [localhost port 8088](http://127.0.0.1:8088/)

Using different server context path / port
------------------------------------------
When using jetty try to set the system properties
+ -Dwebapp.port=<port> , eg: -Dwebapp.port=8087  
+ -Dwebapp.context.path=<context path> , eg: -Dwebapp.context.path=/rwe
 
When using a different context path copy the file domain.xml into your ROOT webapp directory of tomcat.

Deploy to tomcat
----------------
+ Setup tomcat/conf/tomcat-users.xml:
  <tomcat-users>
    <role rolename="manager"/>
    <role rolename="admin"/>
    <user password="admin" roles="admin,manager" username="admin"/> 
  </tomcat-users>
+ Setup .m2/settings.xml:
  <servers>
    <server>
        <id>myserver</id>
        <username>admin</username>
        <password>admin</password>
    </server>
  </servers>
+ run: 
    $ cd module/mm-conductor
    $ mvn tomcat:deploy -DskipTests=true -P warFile


# MongoDB
Download MongoDB to your computer

    $ $ curl http://downloads.mongodb.org/osx/mongodb-osx-x86_64-2.0.2 > ~/programs/mongo.tgz
    $ tar xzf ~/programs/mongo.tgz

Run MongoDB

    $ sudo mkdir -p /data/db
	$ sudo chown `id -u` /data/db
    $ ~/programs/mongodb/bin/mongod
    
or with non-default data directory:

    $ mkdir -p ~/programs/data/mongodb/
	$ chown `id -u` ~/programs/data/mongodb/
    $ ~/programs/mongodb/bin/mongod --dbpath ~/programs/data/mongodb/ &
    


Hint for md-formatting: See https://github.com/SpringSource/cloudfoundry-samples/blob/master/stocks/README.md

# Maven
Some useful command lines:
   $ mvn versions:display-dependency-updates
 
