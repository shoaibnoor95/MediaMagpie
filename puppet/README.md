Running puppet in vagrant
=========================

# Test vagrant provisioning whith vagrant

## apply puppet
$ sudo puppet apply -v /vagrant/puppet/manifests/base.pp 

## only test puppet
$ sudo puppet apply -v /vagrant/puppet/manifests/base.pp --noop

## get graph of dependencies
open the '.dot' files in OmniGraffle or GraphViz

$ sudo puppet apply --graph --noop /vagrant/puppet/manifests/base.pp
$ cat /var/lib/puppet/state/graphs/relationships.dot
$ cp  /var/lib/puppet/state/graphs/relationships.dot /vagrant

