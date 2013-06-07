HOWTO
=====

What for software you need?
---------------------------

+ git
+ apache ant 1.7.1
+ sun java 6

How to build?
-------------
 
```bash
    mvn clean
    mvn compile
    mvn test
    mvn package [-P warFile]
```


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

# Maven
Some useful command lines:
   $ mvn versions:display-dependency-updates
   $ mvn build-helper:remove-project-artifact -> see http://mojo.codehaus.org/build-helper-maven-plugin/remove-project-artifact-mojo.html


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


# EC2 Instances
---------------

## Update the standard open-jdk version with oracles java 1.6:
The open-jdk installation on the ec2 instances seems to have many issues, so i've got the effect that mediamagpie crashes after a while (e.g. less than one minute of usage). By this experience i decided to install the oracle java implementation.
I've found a very good installation guide here: http://livingtao.blogspot.de/2012/01/few-easy-steps-to-install-sunoracle-jdk.html

  $ wget --no-cookies --header "Cookie: gpw_e24=xxx;" http://download.oracle.com/otn-pub/java/jdk/6u34-b04/jdk-6u34-linux-i586-rpm.bin
  $ sudo bash
  $ chmod +x jdk-6u34-linux-i586-rpm.bin
  $ ./jdk-6u34-linux-i586-rpm.bin
  $ alternatives --install /usr/bin/java java /usr/java/default/bin/java 20000
  $ update-alternatives --config java
  $ ln -s /usr/java/default/jre /usr/lib/jvm/jre
  $ ln -s /usr/share/java /usr/lib/jvm-exports/jre
  

# Generating self-signed Certificate and keystore
-------------------------------------------------

  $ #openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout jetty.key -out jetty.crt
  $ openssl req \
    -x509 -nodes -days 3650 \
    -subj '/C=DE/ST=NRW/L=Bonn/CN=www.mediamagpie.org/O=Ralf Wehner/emailAddress=ralf.fred@gmail.com' \
    -newkey rsa:1024 -keyout jetty.pem -out jetty.crt
  $ openssl x509 -text -in jetty.crt
  
  $ keytool -keystore keystore -import -alias jetty -file jetty.crt -trustcacerts 
  
  
  
  
