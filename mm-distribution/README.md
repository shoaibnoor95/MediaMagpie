HOWTO
=====

Which software do you need?
---------------------------
+ java 7
+ apache maven 3
+ optional a database like mysql

How to build?
-------------
```bash
export MAVEN_OPTS="-Xmx200m -XX:MaxPermSize=120m"
mvn clean
mvn compile
mvn test
mvn package -DskipTests [-P warFile]
```


How to start jetty?
-------------------
Extract the distribution file mm-distribution-<Version>.tar into an arbitrary folder.
Run the mediamagpie.sh script to start/stop the webapplication with embedded jetty9 server.
 
```bash
bin/mediamagpie.sh start
# Change the deploy mode to use other ports/db settings
vi bin/mediamagpie.sh 
```

open your browser [localhost port 8088](http://127.0.0.1:8088/)

Using different server context path / port
------------------------------------------
When using jetty change some system properties as set in start script mediamagpie.sh .
+ -Dwebapp.port=<port> , eg: -Dwebapp.port=8087  
+ -Dwebapp.context.path=<context path> , eg: -Dwebapp.context.path=/rwe
 
When using a different context path copy the file domain.xml into your ROOT webapp directory of tomcat.


Run application in tomcat
-------------------------
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


## Maven
Some useful command lines:
   $ mvn versions:display-dependency-updates
   $ mvn versions:set -DgenerateBackupPoms=false -DnewVersion=0.2-SNAPSHOT -Pall
   $ mvn build-helper:remove-project-artifact -> see http://mojo.codehaus.org/build-helper-maven-plugin/remove-project-artifact-mojo.html


## MongoDB (currently no longer used)
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


## EC2 Instances
### Update the standard open-jdk version with oracles java 1.6: (TODO rwe: obsolete)
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

### Install openjdk 7:
```bash
  $ sudo yum update
  $ yum --enablerepo="*" list available | grep openjdk
  $ sudo yum install java-1.7.0-openjdk-devel
  $ sudo alternatives --config java
  $ sudo yum clean all
´´´

### Create swap partition to avoid 'java invoked oom-killer':
See also article http://serverfault.com/questions/268288/most-long-running-commands-instantly-killed-on-amazon-ec2-ubuntu-10-04.
```bash
  $ sudo dd if=/dev/zero of=/swap bs=1M count=500
  $ sudo mkswap /swap
  $ sudo swapon /swap
´´´


### Reduce memory usage of mysql DB
Add some settings in /etc/my.cnf
```bash
  ...
  max_connections=10
  key_buffer_size=32M
  query_cache_size=32M
  ...
´´´

## Generating self-signed Certificate and keystore (TODO rwe: obsolete?)

  $ #openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout jetty.key -out jetty.crt
  $ openssl req \
    -x509 -nodes -days 3650 \
    -subj '/C=DE/ST=NRW/L=Bonn/CN=www.mediamagpie.org/O=Ralf Wehner/emailAddress=ralf.fred@gmail.com' \
    -newkey rsa:1024 -keyout jetty.pem -out jetty.crt
  $ openssl x509 -text -in jetty.crt
  
  $ keytool -keystore keystore -import -alias jetty -file jetty.crt -trustcacerts 
  
  selfsigned crt and keystore
  $ keytool -genkey -alias MyPlugins -keyalg RSA -keystore keystore.ks  -validity 10000
  $ keytool -list -keystore keystore.ks
  $ mv keystore.ks modules/mm-conductor/src/main/resources/ssl/ 
  
  
## keytool
  1. java keystore you will first create the .jks file that will initially only contain the private key
  2. then generate a CSR and have a certificate generated from it
  3. Then you will import the certificate to the keystore including any root certificates - See more at: http://www.lmhproductions.com/37/common-java-keytool-commands/#sthash.cQFR6sFv.dpuf
  
  Lösung: (keystore pw: 'hAmster123')
  $ keytool -keystore keystore.jks -genkeypair -alias mediamagpie -keyalg RSA -keysize 2048 -dname 'C=DE,ST=NRW,L=Bonn,CN=www.mediamagpie.org,O=Ralf Wehner,emailAddress=ralf.fred@gmail.com' -validity 3650
  $ #keytool -list -keystore keystore.jks -v -storepass "hAmster123"
  $ #keytool -keystore keystore.jks -exportcert -alias mediamagpie -storepass "hAmster123" | openssl x509 -inform der -text
  $ mv keystore.jks modules/mm-conductor/src/main/resources/ssl/keystore.jks
  
  --> Create a shell script ?
  
## Vagrant
### startup vagrant box
  $ cd mediamapgie
  $ vagrant up
  
### Run puppet inside vagrant box:
  $ vagrant ssh
  $ # old: puppet apply /vagrant/mm-distribution/src/main/puppet/manifests/base.pp
  $ # sudo puppet apply --debug --modulepath=/tmp/mm-puppet/etc/puppet/modules/:/etc/puppet/modules --hiera_config=/tmp/mm-puppet/etc/puppet/hiera.yaml /tmp/mm-puppet/etc/puppet/manifests/site.pp
  
### New: update vagrant to new puppet3 (do only once when starting vagrant box the first time!)
  $ vagrand ssh
  $ # centos os: sudo /bin/bash /vagrant/mm-distribution/src/main/scripts/update_to_puppet3_yum_installer.sh
  $ # ubuntu: sudo apt-get install puppet 
  
### update ubuntu's packages
  $ sudo apt-get update && sudo apt-get upgrade
  
### run your application
  $ sudo /vagrant/mm-distribution/target/mm-distribution-0.1-SNAPSHOT-distribution/mm-distribution-0.1-SNAPSHOT/bin/mediamagpie.sh start  
  
### export your provisioned box
Before we create the new image from current box, we have to delte /etc/udev/rules.d/70-persistent-net.rules. (see: https://github.com/mitchellh/vagrant/issues/997)
  $ vagrant ssh
  $ [sudo] rm -f /etc/udev/rules.d/70-persistent-net.rules

This will create a new package.box file containing the actual state of your box.
  $ vagrant package

### If /vagrant folder is empty within vagrant
  $ [sudo] rm -f /etc/udev/rules.d/70-persistent-net.rules
  $ exit
  $ vagrant reload
  $ vagrant ssh
  