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
    mvn package


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
+ run: mvn tomcat:deploy -DskipTests=true

Hint for md-formatting: See https://github.com/SpringSource/cloudfoundry-samples/blob/master/stocks/README.md 