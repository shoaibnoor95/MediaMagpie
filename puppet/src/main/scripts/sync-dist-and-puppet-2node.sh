#!/bin/bash
# 
# This script syncs the mediamagpie folder against a ec2 instance via rsync and ssh.
#
# Call: sync-dist-and-puppet-2node.sh hostname
#
#############################################################################

#
## setup variables
#
PRIVATE_KEY=~/projects/wehner/aws_rwe1.pem
VERSION="0.3"

set -x

DIR_SCRIPTS=$(cd `dirname $0` && pwd)
DIR_BASE_PROJECT=$(cd $DIR_SCRIPTS/../../../.. && pwd)
DIST_ZIP="$DIR_BASE_PROJECT/mm-distribution/target/mm-distribution-$VERSION-SNAPSHOT-distribution.zip"
DIR_LOCAL_DIST="$DIR_BASE_PROJECT/mm-distribution/target/mm-dist"
RSYC_OPT=(-mvrcC --delete -e "ssh -l ec2-user -i $PRIVATE_KEY")

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
## Extract the local zip file into temporary folder
#
mkdir $DIR_LOCAL_DIST
rm -rf $DIR_LOCAL_DIST/*
unzip $DIST_ZIP -d $DIR_LOCAL_DIST

#
## sync distribution --> node
#
echo "** sync mediamagpie distribution..."
rsync "${RSYC_OPT[@]}" $DIR_LOCAL_DIST/ ec2-user@$1:dist
echo ""



