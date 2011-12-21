-- usage, eg: mysql -u root < src/script/mysql-init-it.sql
DROP DATABASE IF EXISTS mediabutler_it; 
CREATE DATABASE mediabutler_it DEFAULT CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON mediabutler_it.* TO 'rwe'@'localhost' IDENTIFIED BY 'rwe' WITH GRANT OPTION;
