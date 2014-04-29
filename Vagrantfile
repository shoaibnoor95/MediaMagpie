# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

    config.vm.provider :virtualbox do |v|
      v.gui = false
      v.customize ["modifyvm", :id, "--memory", 615]
    end

    #config.vm.box = "precise64"
    #config.vm.box_url = "http://files.vagrantup.com/precise64.box"

    # pure box which is similar to an EC2 instance
    # see: http://www.vagrantbox.es/
    #config.vm.box = "CentOS 6.3 x86_64 minimal"
    #config.vm.box_url = "https://dl.dropbox.com/u/7225008/Vagrant/CentOS-6.3-x86_64-minimal.box"

    # an exported box (vagrant package)
    config.vm.box = "CentOS 6.3 x86_64 mediamagpie"
    config.vm.box_url = "file://package.box"
    
    config.vm.define :mm do |mm|
        mm.vm.hostname = 'mediamagpie-01.local.localdomain'
        
        #mm.vm.network :hostonly, "192.168.2.102"
        #mm.vm.network :forwarded_port, guest: 80, host: 8080, auto_correct: true
        #mm.vm.network :forwarded_port, guest: 443, host: 8443, auto_correct: true
        #mm.vm.network :forwarded_port, guest: 5000, host: 8000, auto_correct: true
        #mm.vm.network :private_network, ip: "192.168.254.102"

        mm.vm.provision :puppet do |puppet|
            puppet.manifests_path = "mm-distribution/src/main/puppet/manifests"
            puppet.manifest_file = "base.pp"
        end
    end
end

