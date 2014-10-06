# -*- mode: ruby -*-
# vi: set ft=ruby :

class abs::config {
	file { "/etc/httpd/conf.d/welcome.conf":
		ensure => absent,
		require => Class["abs::install"]
	}


    file { "/etc/pki/tls/certs/ca.crt":
    	ensure => present,
    	source => "puppet:///modules/abs/ca.crt",
    	notify => Service["httpd"],
		require => Class["abs::install"]
    }

    file { "/etc/pki/tls/private/ca.key":
    	ensure => present,
    	source => "puppet:///modules/abs/ca.key",
    	notify => Service["httpd"],
		require => Class["abs::install"]
    }

    file { "/etc/httpd/conf.d/abs.conf":
    	ensure => present,
    	source => "puppet:///modules/abs/abs.conf",
    	notify => Service["httpd"],
		require => Class["abs::install"]
    }

    file { "/etc/httpd/conf.d/ssl.conf":
        ensure => absent,
        notify => Service["httpd"],
        require => Class["abs::install"]        
    }
}
