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

## Install ffmpeg in EC2 instance:
See: http://www.technowise.in/2012/07/installing-ffmpeg-on-amazon-ami.html

The packages ffmpeg and ffmpeg-devel are not available in the amazon repository to install.
So, trying to do 'yum install ffmpeg ffmpeg-devel' will give you.

No package ffmpeg available

To solve this, we can add to the rpm repo by following below instructions.

Go to etc/yum.repos.d

Create a file named dag.repo with the following contents.

[dag]
name=Dag RPM Repository for Red Hat Enterprise Linux
baseurl=http://apt.sw.be/redhat/el5/en/x86_64/dag/
gpgcheck=0
enabled=1



Similarly, create centos.repo with the following contents.

 

[centos]
name=CentOS-5.5 – Base
baseurl=http://mirror.centos.org/centos/5/os/x86_64/
gpgcheck=1
gpgkey=http://mirror.centos.org/centos/RPM-GPG-KEY-CentOS-5
enabled=1
priority=1
protect=1


After creating these files, you are ready to install the ffmpeg with the following command.

yum install ffmpeg ffmpeg-devel


That is it, you are done!


