# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

    config.vm.provider :virtualbox do |v|
      v.gui = false
      v.customize ["modifyvm", :id, "--memory", 615]
    end

    # based on initial box: $vagrant box add chef/ubuntu-14.04
    # an exported box (vagrant package)
    #config.vm.box = "chef/ubuntu-14.04"
    #config.vm.box_url = "file://package.box"
    # rwe: ubuntu 14.04, see: http://www.vagrantbox.es/
    config.vm.box = "Official Ubuntu 14.04 daily Cloud Image amd64"
    config.vm.box_url = "https://cloud-images.ubuntu.com/vagrant/trusty/current/trusty-server-cloudimg-amd64-vagrant-disk1.box"
  
    config.vm.synced_folder "./puppet/src/puppet", "/tmp/mm-puppet/etc/puppet"
    config.vm.synced_folder "./mm-distribution/target/mm-dist", "/tmp/mm-dist"
    
    config.vm.define :mm do |mm|
        mm.vm.hostname = 'mediamagpie-01.local.localdomain'
        
        mm.vm.network :private_network, ip: "192.168.254.102"
        # http
        mm.vm.network :forwarded_port, guest: 80, host: 8081, auto_correct: true
        # https
        mm.vm.network :forwarded_port, guest: 443, host: 8082, auto_correct: true
        # http jetty
        mm.vm.network :forwarded_port, guest: 8080, host: 8090, auto_correct: true
        # https jetty
        mm.vm.network :forwarded_port, guest: 8443, host: 8091, auto_correct: true
        # remote debugging
        mm.vm.network :forwarded_port, guest: 5000, host: 8000, auto_correct: true
        # mysql db
        mm.vm.network :forwarded_port, guest: 3306, host: 13306, auto_correct: true

        # provisioning
        mm.vm.provision "shell", inline: "puppet module install puppetlabs-apt"

        mm.vm.provision :puppet do |puppet|
            puppet.manifests_path = "puppet/src/puppet/manifests"
            puppet.manifest_file = "site.pp"
            puppet.module_path = "puppet/src/puppet/modules"
            puppet.options = "--verbose --debug --hiera_config=/tmp/mm-puppet/etc/puppet/hiera.yaml"
        end
    end
end

