MediaMagpie
===========
Media Magpie is a JAVA web application based on the spring framework that offers functionality to store and publish photos and videos to other users.

The application can be started as a standalone application on each linux machine and can scan specified paths for photos and videos. It is also possible to run this application on a server machine like an AWS EC2 one and to upload medias (photos/videos) simple by drag&drop your files to the upload page. The installation to an EC2 instance is very easy because there is a puppet-configuration that will do the complete installation process for you.

MediaMapgpie uses a relational database (currently mysql) to store all information around your medias like name, description, tags, EXIF-data etc. and user and jobs.


### Features

Here is a list of some features:
- Supports different file formats like png, jpeg, gif, mpg etc.
- MediaMagpie can crawl 0..n local directories to register medias
- Media files can be uploaded via web frontend (drag & drop)
- You can set meta information to medias like 'name' or 'tags'
- You can arrange medias within albums
- "View Modes" can be set to Albums used to hide or publish an album to other users or visitors
- Supports drag & drop of media files
- Thumbs of images and videos will be rendered when needed
- The user can specify the size of preview/ thumb image
- The application supports SSL to protect your credentials and medias
- Optionally, you can store your medias on an amazon S3 system
- The provisioning and installation of an EC2 Instance will be automatically solved with a puppet configuration
- A vagrant solution a also available 

### Prerequisites

#### JAVA
MediaMagpie runs on each unix-/Mac platforms which have JAVA 1.7 JRE installed. The project contains a shell start script that starts the integrated jetty9  server and deploys the applications. The installation process is very simple.
#### Databases
All RDBMS can be used which are supported by Hibernate. Currently we use MySql and Hsql


### Installation

a) MediaMagpie is a project based on maven build system. To create the distribution simple call
```java
mvn clean package -Prelease
```
 within the base directory.
 
b) To install the project with puppet on an ubuntu server, just call: 
```bash
bash puppet/src/main/scripts/sync-dist-and-run-puppet-apply.sh <Name or IP of ubuntu machine>
```
If you install mediamagpie using puppet, puppet will install the application as an service which always run when the server starts. See /etc/init.d/mediamagpie.sh

### Links

* Bug Tracking System: https://github.com/rwe17/MediaMagpie/issues?state=open (former: [JIRA] http://ralfwehner.dyndns.org:8082/secure/Dashboard.jspa).

### Licenses from 3rd Party libs

See mm-distribution/license.html

### Start the application

### Starting the application with a shell script
In module mm-distribution there is a shell script 'mediamagpie.sh' which starts the application using the internal jetty server.
See modules/mm-distritubion/README.md for more information.
 
### Start from eclipse ide
Create a 'Java Application' Run Target with properties:
 - Project     : mm-conductor
 - Main Class  : de.wehner.mediamagpie.conductor.StartJetty9
 - VM Arguments: -Xmx350m -Ddeploy.mode=local 
Run or Debug your configuration and open you browser with url: http://localhost:8088 

