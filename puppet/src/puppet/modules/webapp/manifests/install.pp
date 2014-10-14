# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::install {
  #    package { ["httpd", "mod_ssl", "openssl"]:
  #    	ensure => present,
  #    	require => Class["php-dev::install"]
  #    }
  user { 'mediamagpie':
    name       => 'mediamagpie',
    ensure     => 'present',
    shell      => '/bin/bash',
    managehome => true
  }

  file { 'application files':
    path    => '/opt/mediamapgie',
    source  => '/tmp/mm-dist',
    owner   => 'mediamagpie',
    recurse => true,
    require => [User['mediamagpie'], Package['openjdk-7-jdk']]
  }

#  package { 'openjdk-7-jre-headless': ensure => "purged", }

  package { 'openjdk-7-jdk':
    ensure  => 'installed',
#    require => Package['openjdk-7-jre-headless']
  }

  package { "imagemagick": ensure => 'latest' }

  # package { "ffmpeg": ensure => 'latest' }
  # requires before: sudo puppet module install puppetlabs-apt
  class { 'apt':
    always_apt_update => true,
  }

  apt::ppa { 'ppa:jon-severinsson/ffmpeg': }

  package { 'ffmpeg':
    ensure  => 'installed',
    require => Apt::Ppa['ppa:jon-severinsson/ffmpeg']
  }

  package { 'apache2': ensure => 'latest', }

  # TODO rwe:
  # - install apache https://gist.github.com/jsuwo/9038610
  # - activate port forwarding
  # - install mysql
  # - prepare fresh mysql db
}
