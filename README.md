MediaMagpie
===========
Media Magpie is a JAVA web application that offers functionality to store and publish photos to other users or visitors within a web application. It uses a relational database to store the location of media files and meta-information like the photo format, name, description, tags etc.
In future versions, it is planned to support media files like videos too, so that is the reason when speaking of 'medias'.


Features
--------
Here is a list of some features:
- Supports different file formats like png, jpeg, gif etc.
- MediaMagpie can crawl 0..n local directories to register medias
- Media files can be uploaded via web frontend (drag & drop)
- You can set meta information to medias like 'name' or 'tags'
- You can arrange medias within albums
- "View Modes" can be set to Albums used to hide or publish an album to other users or visitors
- Supports drag & drop of media files
- Preview images files will be resized automatically only when needed
- The user can specify the size of preview/ thumb image
- The application supports SSL to protect your credentials and medias
- Optionally, you can store your medias on an amazon S3 system

Prerequisites
-------------
### JAVA
MediaMagpie runs on each unix-/Mac platforms which have JAVA 1.7 JRE installed. The project contains a shell start script that starts the integrated jetty server and deploys the applications. The installation process is very simple.
### Databases
All RDBMS can be used which are supported by Hibernate. For a first test, a very simple solution is the internal HSQL database, that will be delivered within the MediaMagpie distribution.


Installation
------------
MediaMagpie is a project based on maven build system. To create the dsitrition simple call 'mvn package' within the base directory.

Links
-----
* Bug Tracking System: https://github.com/rwe17/MediaMagpie/issues?state=open (former: [JIRA] http://ralfwehner.dyndns.org:8082/secure/Dashboard.jspa).

Licenses from 3rd Party libs
----------------------------
See mm-distribution/license.html

Start the application
---------------------
### Starting the application with a shell script
In module mm-distribution there is a shell script 'mediamagpie.sh' which starts the applicaiton using the internal jetty server.
See modules/mm-distritubion/README.md for more information.
 
### You can start the application from command line using the internal jetty browser
  $ cd modules/mm-conductor/
  $ mvn -DskipTests=true jetty:run

### Start from eclipse ide
Create a 'Java Application' Run Target with properties:
 - Project     : mm-conductor
 - Main Class  : de.wehner.mediamagpie.conductor.StartJetty
 - VM Arguments: -Xmx350m -Ddeploy.mode=local 
Run or Debug your configuration and open you browser with url: http://localhost:8088 


