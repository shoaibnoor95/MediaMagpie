# -*- mode: ruby -*-
# vi: set ft=ruby :

class webapp::service {
  
	service { "apache2":
		enable => true,
		ensure => running,
		restart => true,
#		require => [Exec["webapp::proxy_http"]]
	}
}