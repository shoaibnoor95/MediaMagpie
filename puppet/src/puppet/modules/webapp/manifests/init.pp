# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp {
  include "webapp::install"
  include "webapp::config"
  include "webapp::service"

  Class["webapp::install"] ->
  Class["webapp::config"] ->
  Class["webapp::service"]
}