# -*- mode: ruby -*-
# vi: set ft=ruby :

class mysql {
  # setup variables
  $root_pw = hiera('db.root.pw')
  $schema_name = hiera('db.schema.name')

  # run classes
  include "mysql::install"
  include "mysql::config"
  include "mysql::service"

  Class["mysql::install"] ->
  Class["mysql::config"] ->
  Class["mysql::service"]
}

define mysqldb ($user, $password) {
  # create the database / schema
  exec { "create-${mysql::schema_name}-db":
    unless  => "/usr/bin/mysql -uroot -p${mysql::root_pw} ${mysql::schema_name}",
    command => "/usr/bin/mysql -uroot -p${mysql::root_pw} -e \"create database ${mysql::schema_name} DEFAULT CHARACTER SET utf8;\"",
    require => Service["mysql"],
  }

  # create user and grant rights
  exec { "grant-${mysql::schema_name}-db":
    unless  => "/usr/bin/mysql -u${user} -p${password} ${mysql::schema_name}",
    command => "/usr/bin/mysql -uroot -p${mysql::root_pw} -e \"grant all on ${mysql::schema_name}.* to '${user}'@'%' identified by '$password';\"",
    require => [Service["mysql"], Exec["create-${mysql::schema_name}-db"]],
  }

  # delete user and database after setup
  # eg: DROP DATABASE mediamagpie; DROP USER 'mmagpie'@'localhost'; FLUSH PRIVILEGES;
#  exec { "delete-${mysql::schema_name}-db":
#    onlyif  => "/usr/bin/mysql -u${user} -p${password} ${mysql::schema_name}",
#    command => "/usr/bin/mysql -uroot -p${mysql::root_pw} -e \"DROP DATABASE ${mysql::schema_name}; DROP USER '${user}'@'localhost'; FLUSH PRIVILEGES;\"",
#    require => Exec["grant-${mysql::schema_name}-db"],
#  }
}
