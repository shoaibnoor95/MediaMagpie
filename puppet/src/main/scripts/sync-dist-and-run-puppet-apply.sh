#!/bin/bash
# 
# This script syncs the mediamagpie distribution to the ec2-instance
# result is:
#            /tmp/mm-dist  <-- contains the distribution part of the application
#            /etc/puppet/modules    <-- contains the puppet part
#                       /manifests
#
# Call: sync-dist-and-puppet-2node.sh hostname
#
#############################################################################

#
## setup variables
#
PRIVATE_KEY=~/projects/wehner/aws_rwe1.pem
VERSION="0.3"

DIR_SCRIPTS=$(cd `dirname $0` && pwd)
DIR_BASE_PROJECT=$(cd $DIR_SCRIPTS/../../../.. && pwd)
DIST_ZIP="$DIR_BASE_PROJECT/mm-distribution/target/mm-distribution-$VERSION-SNAPSHOT-distribution.zip"
DIR_LOCAL_DIST="$DIR_BASE_PROJECT/mm-distribution/target/mm-dist"
PUPPET_ZIP="$DIR_BASE_PROJECT/puppet/target/mm-puppet-$VERSION-SNAPSHOT-puppet.zip"
DIR_LOCAL_PUPPET="$DIR_BASE_PROJECT/puppet/target/puppet-dist"
RSYC_OPT=(-mvrcC --delete -e "ssh -l root -i $PRIVATE_KEY")
CMD_SCP="scp -i $PRIVATE_KEY"
CMD_SSH="ssh -i $PRIVATE_KEY"

#
## check necessary parameters
#
showHelp () 
{
    echo "use: $0 <ec2-instance> [full | with-config]"
}

if [ -z "$1" ]; then
    showHelp
    echo "Please specify the public EC2 instance name (eg: ec2-54-247-158-88.eu-west-1.compute.amazonaws.com)"
    exit 1
fi
if [ "${1}" == "-h" -o "${1}" == "--help" ]; then
    showHelp
    exit 0
fi

#
## Extract the local distribution zip file into temporary folder
#
#mkdir $DIR_LOCAL_DIST
#rm -rf $DIR_LOCAL_DIST/*
#unzip $DIST_ZIP -d $DIR_LOCAL_DIST

#
## Extract the local puppet zip file into temporary folder
#
#mkdir $DIR_LOCAL_PUPPET
#rm -rf $DIR_LOCAL_PUPPET/*
#unzip $PUPPET_ZIP -d $DIR_LOCAL_PUPPET


#
## sync distribution --> node
#
echo "** sync mediamagpie distribution..."
rsync "${RSYC_OPT[@]}" $DIR_LOCAL_DIST/ ec2-user@$1:/tmp/mm-dist
echo ""

#
## copy puppet --> node
#
echo "** copy puppet distribution..."
$CMD_SCP $PUPPET_ZIP ec2-user@$1:/tmp/mm-dist/puppet-node.zip
echo ""

set -x

$CMD_SSH -l ec2-user -t $1 "sudo unzip /tmp/mm-dist/puppet-node.zip -d /"

