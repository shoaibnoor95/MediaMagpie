# -*- mode: ruby -*-
# vi: set ft=ruby :

class sayHello {
  exec { 'hello':
    path    => '/bin:/usr/bin',
    command => 'echo starting puppet setup of vagrant $::fqdn box...'
  }
}

node default {
  # do some variable definitions
  $webapp_name = 'mediamagpie'
  $public_ip = $::ec2_public_ipv4
  $base_app_dir = "/opt/mediamagpie"

  notify { "Running on machine: $::fqdn and osfamily: $::osfamily and public-ip: $public_ip": }

  require sayHello

  # deactivate mysql module, if you do not neet mysql database
  include mysql

  # install web application
  include webapp
}

# vim:et:sts=4:sw=4:ts=4:ai
