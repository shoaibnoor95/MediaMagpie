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
