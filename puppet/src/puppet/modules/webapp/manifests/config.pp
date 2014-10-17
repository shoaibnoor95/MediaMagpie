# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::config () {
  file { "${$base_app_dir}/conf/node.properties":
    content => template('webapp/node.properties.erb'),
    ensure  => file,
    notify  => Service["mediamagpie"],
    require => Class["webapp::install"]
  }

  # configure authbind for port 80 and 443
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

  # adding default folders
  file { [
    "/data",
    "/data/mediamagpie",
    "/data/mediamagpie/temp",
    "/data/mediamagpie/temp/thumbs",
    "/data/mediamagpie/temp/videos",
    "/data/mediamagpie/useruploads"]:
    ensure  => "directory",
    owner   => "mediamagpie",
    group   => "mediamagpie",
    mode    => 755,
    require => User['mediamagpie'],
  }

  # TODO rwe: checkout if we should use the mysql module
  # (http://puppetlabs.com/blog/manage-your-mysql-deployment-puppet-enterprise-supported-module-puppetlabs-mysql)
}
