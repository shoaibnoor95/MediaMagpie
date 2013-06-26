# -*- mode: ruby -*-
# vi: set ft=ruby
class sayHello {
    exec { 'blah':
    path    => '/bin:/usr/bin',
    command => 'echo starting puppet...'
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
}
 
class installJdk {

  # download oracle's jdk-7 rpm
  exec { 'download-java-rpm':
    path    => '/bin:/usr/bin',
    command => "curl -s  -o /tmp/jdk-7.rpm https://s3-eu-west-1.amazonaws.com/yum-repos/jdk-7u25-linux-x64.rpm",
    creates => "/tmp/jdk-7.rpm"
  }
  package { "jdk":
    ensure => "absent"
  }
#  exec { "remove-old-jdk":
#    path    => '/bin:/usr/bin',
#    command => "rpm -ev jdk",
#    require  => Exec['download-java-rpm']
#  }
  exec { "install-jdk":
    path    => '/bin:/usr/bin',
    command => "rpm -Uvh /tmp/jdk-7.rpm",
#    require  => [Exec['download-java-rpm'],Exec['remove-old-jdk']]
    require  => [Exec['download-java-rpm'],Package['jdk']]
  }
 
}

# Definition of stages which will be executed in this order: 
#   first -> main -> last
stage { 'first': before => Stage['main'] }
stage { 'last': require => Stage['main'] }
    
# Forces the repository to be configured before any other task
class {
    'repository': stage => first;
    'installJdk': stage => main;
}
#Class {
#    'repository': require =>  Class['sayHello'];
# }

# vim:et:sts=4:sw=4:ts=4:ai

