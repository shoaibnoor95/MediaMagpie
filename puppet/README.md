Running puppet in vagrant
=========================

# puppet apply
$ sudo puppet apply -v /vagrant/puppet/manifests/base.pp 

# noop
$ sudo puppet apply -v /vagrant/puppet/manifests/base.pp --noop

# get graph
open the '.dot' files in OmniGraffle or GraphViz

$ sudo puppet apply --graph --noop /vagrant/puppet/manifests/base.pp
$ cat /var/lib/puppet/state/graphs/relationships.dot
$ cp  /var/lib/puppet/state/graphs/relationships.dot /vagrant

