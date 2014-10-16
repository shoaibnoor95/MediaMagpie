# -*- mode: ruby -*-
# vi: set ft=ruby :

class mysql::service {
  # setup some variables
  $app_user = hiera('db.app.user')
  $app_pw = hiera('db.app.pw')

  service { "mysql":
    enable  => true,
    ensure  => running,
    require => Package['mysql-server'],
  }

  # setup root's password if not done before
  exec { "set-mysql-password":
    unless  => "mysqladmin -uroot -p\"$mysql::root_pw\" status",
    path    => ["/bin", "/usr/bin"],
    command => "mysqladmin -uroot password \"$mysql::root_pw\"",
    require => Service["mysql"],
  }

  # create schema and user if not done before
  mysqldb { "myapp":
    user     => $mysql::service::app_user,
    password => $mysql::service::app_pw,
  }
}
