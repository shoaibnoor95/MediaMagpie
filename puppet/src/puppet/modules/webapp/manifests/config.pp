# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::config ( 
  
  $webapp_conf_dir = "/opt/mediamapgie/conf",
  $webapp_name = 'mediamagpie',
  $public_ip = $::public_ip,
) {
  file { "${::webapp_conf_dir}/node.properties":
    content => template('webapp/node.properties.erb'),
    # notify => Service["mediamagpie"],
    ensure  => file,
    require => Class["webapp::install"]
  }

# TODO rwe: checkout, does we need a2enmod ssl too?
  exec { 'proxy_http':
    path    => '/sbin:/bin:/usr/sbin:/usr/bin',
    command => 'a2enmod proxy_http',
    require => Package['apache2'],
  }

  file { "/etc/apache2/sites-available/$webapp_name.conf":
    content => template('webapp/webapp.conf.erb'),
    # notify => Service["mediamagpie"],
    ensure  => file,
    require => Exec['proxy_http']
  }

#  notify { "a2ensite $webapp_name": }
  exec { "a2ensite $webapp_name":
    path    => '/sbin:/bin:/usr/sbin:/usr/bin',
    command => 'a2ensite $webapp_name',
    require => File["/etc/apache2/sites-available/$webapp_name.conf"],
  }
}
