# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::config {
  
  $webapp_conf_dir = "/opt/mediamapgie/conf"

  file { "${webapp_conf_dir}/node.properties":
    content => template('webapp/node.properties.erb'),
    # notify => Service["mediamagpie"],
    ensure  => file,
    require => Class["webapp::install"]
  }

}
