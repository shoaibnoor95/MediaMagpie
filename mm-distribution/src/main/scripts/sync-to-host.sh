#!/bin/bash
# 
# Das Skript sychrnonisiert die lokale Entwicklungsverzeichnisse gegen ein 
# bestimmtes Enwickler-Template.
#
# Aufruf sync-to-host.sh <public ec2 instance name> [full]
#
#############################################################################

#
## setup variables
#
PRIVATE_KEY=~/projects/wehner/aws_rwe1.pem
VERSION="0.1"

DIR_SCRIPTS=$(cd `dirname $0` && pwd)
DIR_HOME=$(cd $DIR_SCRIPTS/../../../target/mm-distribution-$VERSION-SNAPSHOT-distribution/mm-distribution-$VERSION-SNAPSHOT && pwd)
RSYC_OPT=(-mvrcC --delete -e "ssh -l ec2-user -i $PRIVATE_KEY")

# switch on debug-output to stdout
set -x
# turn off debug output
#set -x

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
## --> mediamagpie
#
echo "** sync mediamagpie distribution..."
if [ "$2" != "with-config" ]
    then
	RSYC_OPT=("${RSYC_OPT[@]}" --exclude "mediamagpie.sh" --exclude "*.properties" --exclude "target*")
fi
rsync "${RSYC_OPT[@]}" $DIR_HOME/ ec2-user@$1:mediamagpie
echo ""

## quit if not 'full' is set
if [ -z "$2" ] || ([ "$2" != "full" ])
    then
    echo "finished fast synchronisation."
    exit 0
fi


