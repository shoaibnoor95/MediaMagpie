# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|

    config.vm.provider :virtualbox do |v|
      v.gui = false
    end

    #config.vm.box = "precise64"
    #config.vm.box_url = "http://files.vagrantup.com/precise64.box"
    # see: http://www.vagrantbox.es/
    config.vm.box = "CentOS 6.3 x86_64 minimal"
    config.vm.box_url = "https://dl.dropbox.com/u/7225008/Vagrant/CentOS-6.3-x86_64-minimal.box"
    #config.vm.box = "CentOS 6.4 x86_64 Minimal (VirtualBox Guest Additions 4.2.12, Chef 11.4.4, Puppet 3.1.1)"
    #config.vm.box_url = "http://developer.nrel.gov/downloads/vagrant-boxes/CentOS-6.4-x86_64-v20130427.box"
    
    config.vm.define :mm do |mm|
        mm.vm.hostname = 'mediamagpie-01.local.localdomain'
        
        #mm.vm.network :hostonly, "192.168.100.10"
        #mm.vm.network :forwarded_port, guest: 80, host: 8080, auto_correct: true
        #mm.vm.network :forwarded_port, guest: 5000, host: 8000, auto_correct: true
        mm.vm.network :private_network, ip: "192.168.254.201"

        mm.vm.provision :shell, :inline => "echo Hello, World"
        #mm.vm.provision :shell, :path => 'billing-deploy/vagrant/update_puppet3.sh'
        #mm.vm.provision :shell do |shell|
         #   shell.path = 'billing-deploy/vagrant/puppet_apply.sh'
          #  shell.args = 'local'
        #end
        mm.vm.provision :puppet do |puppet|
            puppet.manifests_path = "puppet/manifests"
            puppet.manifest_file = "base.pp"
        end
    end
end

