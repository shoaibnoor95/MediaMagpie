-- usage, eg: /usr/local/mysql/bin/mysql -u root < modules/mm-conductor/src/main/script/mysql-init.sql
-- DROP DATABASE mediamagpie;
CREATE DATABASE IF NOT EXISTS mediamagpie DEFAULT CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON mediamagpie.* TO 'mmagpie'@'127.0.0.1' IDENTIFIED BY 'mmagpie' WITH GRANT OPTION;
