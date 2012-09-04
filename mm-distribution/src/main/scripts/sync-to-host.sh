#!/bin/bash
# 
# Das Skript sychrnonisiert die lokale Entwicklungsverzeichnisse gegen ein 
# bestimmtes Enwickler-Template.
#
# Aufruf sync-to-host.sh <public ec2 instance name> [full]
#
#############################################################################


if [ -z "$1" ]; then
	echo "Please specify the public EC2 instance name (eg: ec2-54-247-158-88.eu-west-1.compute.amazonaws.com)"
	exit 1
fi

#
## setup variables
#
PRIVATE_KEY=~/projects/wehner/aws_rwe1.pem
VERSION="0.1"

DIR_SCRIPTS=$(cd `dirname $0` && pwd)
DIR_HOME=$(cd $DIR_SCRIPTS/../../../target/mm-distribution-$VERSION-SNAPSHOT-distribution/mm-distribution-$VERSION-SNAPSHOT && pwd)
RSYC_OPT=(-mvrcC --delete -e "ssh -l ec2-user -i $PRIVATE_KEY")
#RSYC_OPT="-mvrcC --delete -e \"ssh -l root\""
#RSYC_OPT="-mvrcC --delete"

# switch on debug-output to stdout
#set -x
# turn off debug output
#set -x

#
## --> inc/application
#
echo "** sync mediamagpie distribution..."
rsync "${RSYC_OPT[@]}" $DIR_HOME/ ec2-user@$1:mediamagpie
echo ""

## quit if not 'full' is set
if [ -z "$2" ] || ([ "$2" != "full" ])
    then
    echo "finished fast synchronisation."
    exit 0
fi


