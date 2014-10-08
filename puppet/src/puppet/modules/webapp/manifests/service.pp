# -*- mode: ruby -*-
# vi: set ft=ruby :

class abs::service {
	service { "httpd":
		enable => true,
		ensure => running,
		require => [Class["abs::config"], Class["php-dev::config"]]
	}
}