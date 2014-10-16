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
  exec { "create-${mysql::schema_name}-db":
    unless  => "/usr/bin/mysql -u${user} -p${password} ${mysql::schema_name}",
    command => "/usr/bin/mysql -uroot -p${mysql::root_pw} -e \"create database ${mysql::schema_name}; grant all on ${mysql::schema_name}.* to ${user}@localhost identified by '$password';\"",
    require => Service["mysql"],
  }
}
