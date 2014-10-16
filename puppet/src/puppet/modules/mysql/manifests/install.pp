# -*- mode: ruby -*-
# vi: set ft=ruby :

class mysql::install {
  # install mysql-server package
  package { 'mysql-server': #    require => Exec['apt-update'], # require 'apt-update' before installing
    ensure => installed, }

  package { 'mysql-client': ensure => present }
}
