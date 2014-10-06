# -*- mode: ruby -*-
# vi: set ft=ruby :

class sayHello {
  exec { 'blah':
    path    => '/bin:/usr/bin',
    command => 'echo starting puppet setup of vagrant box...'
  }
}

node default {
  
  require sayHello
  
  
  include webapp
}

# vim:et:sts=4:sw=4:ts=4:ai
