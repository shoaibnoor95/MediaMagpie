MediaMagpie
===========
Media Magpie is a JAVA web application that offers functionality to store and publish photos to other users or visitors within a web application. It uses a relational database to store the location of media files and meta-information like the photo format, name, description, tags etc.
In future versions, it is planned to support media files like videos too, so that is the reason when speaking of 'medias'.


Features
--------
Here is a list of some features:
- Supports different file formats like png, jpeg, gif etc.
- MediaMagpie can crawl 0..n local directories to register medias
- Media files can be uploaded via web frontend
- You can set meta information to medias like 'name' or 'tags'
- You can arrange medias within albums
- "View Modes" can be set to Albums used to hide or publish an album to other users or visitors
- Supports drag & drop of media files
- Preview images files will be resized automatically only when needed
- The user can specify preview/ thumb image sizes

Prerequisites
-------------
### JAVA
MediaMagpie runs on each unix-/Mac platforms which have JAVA 1.6 JRE installed. The project contains a shell start script that starts the integrated jetty server and deploys the applications.
### Databases
All RDBMS can be used which are supported by Hibernate. For a first test, a very simple solution is the internal HSQL database, that will be delivered within the MediaMagpie distribution.


Installation
------------
MediaMagpie is a project based on maven build system. To create the dsitrition simple call 'mvn package' within the base directory.

Links
-----
* Bug Tracking System: Go to [JIRA](http://ralfwehner.dyndns.org:8082/secure/Dashboard.jspa).

Licenses from 3rd Party libs
----------------------------
See mm-distribution/license.html
