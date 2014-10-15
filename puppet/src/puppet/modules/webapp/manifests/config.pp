# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::config () {
  file { "${webapp_conf_dir}/node.properties":
    content => template('webapp/node.properties.erb'),
    # notify => Service["mediamagpie"],
    ensure  => file,
    require => Class["webapp::install"]
  }

  # TODO rwe: checkout, does we need a2enmod ssl too?
#  exec { 'proxy_http':
#    path    => '/sbin:/bin:/usr/sbin:/usr/bin',
#    command => 'a2enmod proxy_http',
#    require => Package['apache2'],
#  }

  ## TODO rwe: remove apache2
  #notify { "updating conf $webapp_name with public-ip: $public_ip": }

#  file { "/etc/apache2/sites-available/$webapp_name.conf":
#    content => template('webapp/webapp.conf.erb'),
#    # notify => Service["mediamagpie"],
#    ensure  => file,
#    require => Exec['proxy_http']
#  }
#
#  exec { "a2ensite $webapp_name":
#    path    => '/sbin:/bin:/usr/sbin:/usr/bin',
#    command => "a2ensite $webapp_name",
#    require => File["/etc/apache2/sites-available/$webapp_name.conf"],
#  }

  # configure authbind
  file { "/etc/authbind/byport/80":
    content => template('webapp/empty.erb'),
    ensure  => file,
    owner   => 'mediamagpie',
    group   => 'mediamagpie',
    mode    => '0750',
    require => Package['authbind']
  }
  file { "/etc/authbind/byport/443":
    content => template('webapp/empty.erb'),
    ensure  => file,
    owner   => 'mediamagpie',
    group   => 'mediamagpie',
    mode    => '0750',
    require => Package['authbind']
  }

  file { ["/data", "/data/mediamagpie", "/data/mediamagpie/temp", "/data/mediamagpie/temp/thumbs", "/data/mediamagpie/temp/videos", "/data/mediamagpie/useruploads"]:
    ensure => "directory",
    owner  => "mediamagpie",
    #    group  => "wheel",
    mode   => 755,
    require => User['mediamagpie'],
  }

}
