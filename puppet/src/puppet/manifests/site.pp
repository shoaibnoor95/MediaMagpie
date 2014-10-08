# -*- mode: ruby -*-
# vi: set ft=ruby :

class sayHello {
  exec { 'blah':
    path    => '/bin:/usr/bin',
    command => 'echo starting puppet setup of vagrant $::fqdn box...'
  }
}

node default {

  notify { "Running on machine: $::fqdn and osfamily: $::osfamily": }

  require sayHello

  include webapp
}

# vim:et:sts=4:sw=4:ts=4:ai
