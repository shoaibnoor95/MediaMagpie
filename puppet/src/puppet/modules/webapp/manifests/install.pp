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

  # TODO rwe: exclude the puppet-node.tar.gz from /opt/mediamagpie
  file { 'application files':
    path    => '/opt/mediamapgie',
    source  => '/tmp/mm-dist',
    owner   => 'mediamagpie',
    recurse => true,
    require => User['mediamagpie']
  }

  package { 'openjdk-7-jre': ensure => 'purged', }

  package { 'openjdk-7-jdk':
    ensure  => 'latest',
    require => Package['openjdk-7-jre']
  }

  package { "imagemagick": ensure => 'latest' }

  # package { "ffmpeg": ensure => 'latest' }
  # requires before: sudo puppet module install puppetlabs-apt
  class { 'apt':
    always_apt_update => true,
  }

  apt::ppa { 'ppa:jon-severinsson/ffmpeg': }

  #  package { [others]: }

  package { 'ffmpeg':
    ensure  => 'installed',
    require => Apt::Ppa['ppa:jon-severinsson/ffmpeg']
  }

}
