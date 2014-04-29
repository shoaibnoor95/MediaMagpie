# -*- mode: ruby -*-
# vi: set ft=ruby

#############################################################################
#
# Puppet provisioning for a vagrant box that is similar to an ec2 instance
#
#############################################################################

class sayHello {
    exec { 'blah':
    path    => '/bin:/usr/bin',
    command => 'echo starting puppet setup of vagrant box...'
  }
}

class repository {
  require sayHello

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

  # add two repositories to add ffmpeg
  yumrepo { 'dag.repo':
    baseurl  => 'http://apt.sw.be/redhat/el5/en/x86_64/dag/',
    enabled  => 0,
    gpgcheck => 1,
    require  => Exec['import-key']
  }
  yumrepo { 'centos5.5.repo':
    baseurl  => 'http://mirror.centos.org/centos/5/os/x86_64/',
    enabled  => 1,
    gpgcheck => 1,
    gpgkey   => 'http://mirror.centos.org/centos/RPM-GPG-KEY-CentOS-5',
    require  => Exec['import-key']
  }
}
 
# Define stages which will be executed in order: 
#   first -> main -> last
stage { 'first': before => Stage['main'] }
stage { 'last': require => Stage['main'] }
    
# Forces the repository to be configured before any other task
class {
    'repository': stage => first;
    'portal::install': stage => main;
}

import 'install.pp'

# vim:et:sts=4:sw=4:ts=4:ai

