#!/bin/bash
#
# See also: http://www.somacon.com/p572.php
#
#############################################################################

set -x
#
## setup variables
#
DIR_SCRIPTS=$(cd `dirname $0` && pwd)
DIR_SQL_SCRIPTS=$(cd ${DIR_SCRIPTS}/../sql && pwd)
# user, used from application
APP_DB_NAME=mediamagpie
APP_DB_USER=mmagpie
# db's root user credentials, used to access the db from outside EC2
ROOT_DB_USER_PW=MediaMagpie

## install mysql if required  
sudo yum install mysql
sudo yum install mysql-server
#sudo yum install mysql-client

# Start mysql server
sudo /etc/init.d/mysqld start
sudo /etc/init.d/mysql start

# Configure mysql to run on startup
sudo chkconfig mysqld on

## setup the application database schema and user
mysql -u root < ${DIR_SQL_SCRIPTS}/mysql-init.sql

#### create the database
# Set root password for MySQL (Choose a strong one if the MySQL TCP port is going to be left open.)
#mysqladmin -u root password '${ROOT_DB_USER_PW}'

