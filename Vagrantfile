# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

    config.vm.provider :virtualbox do |v|
      v.gui = false
      v.customize ["modifyvm", :id, "--memory", 615]
    end

    # based on initial box: $vagrant box add chef/ubuntu-14.04
    # an exported box (vagrant package)
    config.vm.box = "chef/ubuntu-14.04"
    config.vm.box_url = "file://package.box"
    
    config.vm.synced_folder "./puppet/src/main/puppet", "/tmp/mm-puppet/etc/puppet"
    config.vm.synced_folder "./mm-distribution/target/mm-dist", "/tmp/mm-dist"
    
    config.vm.define :mm do |mm|
        mm.vm.hostname = 'mediamagpie-01.local.localdomain'
        
        #mm.vm.network :hostonly, "192.168.2.102"
        mm.vm.network :forwarded_port, guest: 80, host: 8081, auto_correct: true
        #mm.vm.network :forwarded_port, guest: 443, host: 8443, auto_correct: true
        #mm.vm.network :forwarded_port, guest: 5000, host: 8000, auto_correct: true
        #mm.vm.network :private_network, ip: "192.168.254.102"

        mm.vm.provision :puppet do |puppet|
            puppet.manifests_path = "mm-distribution/src/main/puppet/manifests"
            puppet.manifest_file = "base.pp"
        end
    end
end

