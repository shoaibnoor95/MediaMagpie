# -*- mode: ruby -*-
# vi: set ft=ruby :

class abs::service {
  
	service { "apache2":
		enable => true,
		ensure => running,
		restart => true,
		require => [Exec["webapp::proxy_http"]]
	}
}