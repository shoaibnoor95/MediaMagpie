# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::service {
  service { 'mediamagpie':
    ensure     => "running",
    enable     => true,
    hasrestart => true,
    hasstatus  => false,
    require    => [File['/etc/init.d/mediamagpie']],
  }

}