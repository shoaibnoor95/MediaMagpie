# -*- mode: ruby -*-
# vi: set ft=ruby :

class sayHello {
  exec { 'blah':
    path    => '/bin:/usr/bin',
    command => 'echo starting puppet setup of vagrant $::fqdn box...'
  }
}

node default {
  
  # do some variable definitions
  $public_ip = $::ec2_public_ipv4
  
  
  notify { "Running on machine: $::fqdn and osfamily: $::osfamily and public-ip: $public_ip": }

  require sayHello

  include webapp
}

# vim:et:sts=4:sw=4:ts=4:ai
