#!/usr/bin/env bash
#
# copy this file locally (or use /tmp/mm-dist/...) and execute as sudo 
# 
# Installs puppet version 3.4.3 on a target machine like ec2 AMI or vagrant machine

set -e

if [ "$EUID" -ne "0" ] ; then
        echo "Script must be run as root." >&2
        exit 1
fi

if which puppet > /dev/null ; then
        echo "Puppet is already installed"
        exit 0
fi

if ! yum repolist | grep -q puppetlabs;
then
    echo "..enabling puppet repository.."
    # https://tickets.puppetlabs.com/browse/PUP-2132
    echo -e "[main]\nenabled = 0" > /etc/yum/pluginconf.d/priorities.conf
    #rpm --quiet -ivh http://yum.puppetlabs.com/el/6/products/x86_64/puppetlabs-release-6-10.noarch.rpm
    rpm --quiet -ivh http://yum.puppetlabs.com/el/7/products/x86_64/puppetlabs-release-7-11.noarch.rpm
fi
echo "..updating.."
yum -y --quiet clean all
yum -y --quiet update

# Install puppet now
yum install -y puppet-3.4.3-1.el6

#if readlink /etc/alternatives/ruby | grep -q "ruby2\.0"
#then
#    echo "..making ruby1.8 default.."
#    ln -sf /usr/bin/ruby1.8 /etc/alternatives/ruby
#fi

#if readlink /etc/alternatives/gem | grep -q "gem2\.0"
#then
#    echo "..making gem1.8 default.."
#    ln -sf /usr/bin/gem1.8 /etc/alternatives/gem
#fi

puppet --version

# install puppetlabs-apt
puppet module install puppetlabs-apt