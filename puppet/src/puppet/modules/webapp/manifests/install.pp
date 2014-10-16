# -*- mode: ruby -*-
# vi: set ft=ruby :


# some hints:
# list installed packages: $ dpkg --get-selections | grep -v deinstall
#

class webapp::install {
  # execute 'apt-get update'
  #  exec { 'apt-update': # exec resource named 'apt-update'
  #    command => '/usr/bin/apt-get update' # command this resource will run
  #   }

  user { 'mediamagpie':
    name       => 'mediamagpie',
    ensure     => 'present',
    shell      => '/bin/bash',
    managehome => true
  }

  file { 'application files':
    path    => "$base_app_dir",
    source  => '/tmp/mm-dist',
    owner   => 'mediamagpie',
    recurse => true,
    notify  => Service["mediamagpie"],
    require => [User['mediamagpie'], Package['openjdk-7-jdk']]
  }

  file { "/etc/init.d/mediamagpie":
    content => template('webapp/mediamagpie_jsvc.sh.erb'),
    ensure  => file,
    owner   => 'root',
    group   => 'root',
    mode    => '0755',
  #    require => Exec['install-mediamagpie-service'];
  }

  #  package { 'openjdk-7-jre-headless': ensure => "purged", }

  package { 'openjdk-7-jdk':
    ensure => 'installed',
  #    require => Package['openjdk-7-jre-headless']
  }

  package { "imagemagick": ensure => 'latest' }

  # package { "ffmpeg": ensure => 'latest' }
  # requires before: $ sudo puppet module install puppetlabs-apt
  class { 'apt':
    always_apt_update => true,
  }

  apt::ppa { 'ppa:jon-severinsson/ffmpeg': }

  package { 'ffmpeg':
    ensure  => 'installed',
    require => Apt::Ppa['ppa:jon-severinsson/ffmpeg']
  }

  package { 'authbind': ensure => 'installed', }

  package { 'jsvc': ensure => 'latest', }

}
