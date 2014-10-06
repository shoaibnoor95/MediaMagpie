# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::install {
  #    package { ["httpd", "mod_ssl", "openssl"]:
  #    	ensure => present,
  #    	require => Class["php-dev::install"]
  #    }
  user { 'mediamagpie':
    name   => 'mediamagpie',
    groups => 'root'
  }

  file { 'application files':
    path    => '/opt/mediamapgie',
    source  => '/tmp/mm-dist',
    owner   => 'mediamagpie',
    recurse => true,
    require => User['mediamagpie']
  }

  package { 'openjdk-7-jre': ensure => 'latest', }

}
