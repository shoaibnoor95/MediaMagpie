# -*- mode: ruby -*-
# vi: set ft=ruby

class repository {
  # We need cURL installed to import the key
  package { 'curl': ensure => installed }

  # Install the GPG key
  exec { 'import-key':
    path    => '/bin:/usr/bin',
    command => 'curl http://repos.servergrove.com/servergrove-rhel-6/RPM-GPG-KEY-servergrove-rhel-6 -o /etc/pki/rpm-gpg/RPM-GPG-KEY-servergrove-rhel-6',
    require => Package['curl'],
  }
 
  # Enable the repository
  yumrepo { 'servergrove.repo':
    baseurl  => 'http://repos.servergrove.com/servergrove-rhel-6/$basearch',
    enabled  => 1,
    gpgcheck => 1,
    gpgkey   => 'file:///etc/pki/rpm-gpg/RPM-GPG-KEY-servergrove-rhel-6',
    require  => Exec['import-key']
  }

}
 
class mysql {
#  # Installs the MySQL server and MySQL client
#  package { ['mysql-server', 'mysql-client']: ensure => installed, }
# 
#  # Ensures the Apache service is running
#  service { 'mysql':
#    ensure  => running,
#    require => Package['mysql-server'],
#  }
}

class installJdk {

  # download oracle's jdk-7 rpm
  exec { "download-java-rpm":
    path    => '/bin:/usr/bin',
    command => "curl https://s3-eu-west-1.amazonaws.com/yum-repos/jdk-7u25-linux-x64.rpm -o /tmp/jdk-7.rpm",
    creates => "/tmp/jdk-7.rpm"
  }
  exec { "install-jdk":
    path    => '/bin:/usr/bin',
    command => "rpm -Uvh /tmp/jdk-7.rpm"
  }
 
}

stage { pre: before => Stage[main] }
 
include "mysql"
include "installJdk"

# Forces the repository to be configured before any other task
class { 'repository': stage => pre }

# vim:et:sts=4:sw=4:ts=4:ai

