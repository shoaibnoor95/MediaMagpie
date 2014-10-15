# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::service {
  service { 'mediamagpie':
    ensure  => running,
    enable  => true,
    require => [File['/etc/init.d/mediamagpie']],
  }

  # ensure mysql service is running
  service { 'mysql':
    ensure  => running,
    require => Package['mysql-server']
  }

}