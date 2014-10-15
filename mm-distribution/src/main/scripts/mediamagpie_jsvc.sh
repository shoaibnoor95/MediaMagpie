#!/bin/bash
#
# Start-/ Stop-Script for MediaMagpie using the jsvc utility
#
# 15.10.2014: Ralf Wehner
###################################################################################

## set distribution specific settings
DESC="MediaMagpie service"
MGR_USER=mediamagpie
MGR_DEPLOY_MODE=node
JAVA_HOME="/usr/lib/jvm/java-7-openjdk-amd64"
#ADDITIONAL_PARAMETERS="-Dpersistent.unit=mysql"
#ADDITIONAL_PARAMETERS="-Dwebapp.port.http=8080 -Dwebapp.port.https=9191 -Dwebapp.context.path=/mediamagpie"
VM_ARGS="-Xmx200m -XX:MaxPermSize=120m -XX:+CMSClassUnloadingEnabled $ADDITIONAL_PARAMETERS"
#########################

MGR_BIN=$(cd `dirname $0` && pwd)
MGR_HOME=`dirname $MGR_BIN`
cd $MGR_HOME

# add libs to CLASSPATH
for jar in $MGR_HOME/lib/*.jar; do
  CLASSPATH=$CLASSPATH:$jar
done

JAVA=`which java`
if [[ $JAVA_HOME != "" ]]; then
    JAVA=$JAVA_HOME/bin/java
fi
if test -z "$JAVA"; then
    echo "No java found in the PATH. Please set JAVA_HOME."
    exit 1
fi

usage()
{
    echo "Usage: $0 {start|stop|restart}"
    exit 1
}

[ $# -gt 0 ] || usage


##################################################
# Some utility functions
##################################################
findDirectory()
{
    OP=$1
    shift
    for L in $* ; do
        [ $OP $L ] || continue 
        echo $L
        break
    done 
}

# Returns 1 if process doesn't run anymore, 0 if process is still running
# (You can test the && and || operators with 'ps -p $PID && (echo success) || (echo failure)')
running()
{
    [ -f $1 ] || return 1
    PID=$(cat $1)
    ps -p $PID >/dev/null 2>/dev/null || return 1
    return 0
}

##################################################
# Get the action & configs
##################################################

ACTION=$1
shift
if [[ $# > 0 ]]; then
   MGR_DEPLOY_MODE=$1
fi
echo "Mode: ${MGR_DEPLOY_MODE}"

##################################################
# Set tmp if not already set.
##################################################

if [ -z "$TMP" ] 
then
  TMP=/tmp
fi

#####################################################
# Find a location for the pid file
#####################################################
if [  -z "$MGR_RUN" ] 
then
  MGR_RUN=`findDirectory -w /var/run /usr/var/run /tmp`
fi

#####################################################
# Find a PID for the pid file
#####################################################
if [  -z "$MGR_PID" ] 
then
  MGR_PID="$MGR_RUN/mm-conductor.pid"
fi


#####################################################
# This is how the server will be started/stopped
#####################################################
JSVC_EXEC=/usr/bin/jsvc
RUN_ARGS="$VM_ARGS -Ddeploy.mode=$MGR_DEPLOY_MODE -Dlogback.configurationFile=conf/logback.xml"
LOG_OUT=/var/log/mm-stdout.log
LOG_ERR=/var/log/mm-stderr.log
jsvc_exec()
{
    $JSVC_EXEC -home "$JAVA_HOME" -classpath :/usr/share/java/commons-daemon.jar$CLASSPATH -user $MGR_USER -outfile $LOG_OUT -errfile $LOG_ERR -pidfile $MGR_PID -cwd $MGR_HOME -debug $RUN_ARGS $1 de.wehner.mediamagpie.conductor.JettyDeamon
}


##################################################
# Do the action
##################################################
case "$ACTION" in
  start)
    echo "Starting the $DESC..."       
   
    # Start the service
    jsvc_exec
   
    echo "The $DESC has started."
  ;;

  stop)
    echo "Stopping the $DESC..."
   
    # Stop the service
    jsvc_exec "-stop"      
   
    echo "The $DESC has stopped."
  ;;

  restart)
    if [ -f "$MGR_PID" ]; then
       
        echo "Restarting the $DESC..."
       
        # Stop the service
        jsvc_exec "-stop"
       
        # Start the service
        jsvc_exec
       
        echo "The $DESC has restarted."
    else
        echo "Daemon not running, no action taken"
        exit 1
    fi
  ;;

  *)
    usage
    exit 3
  ;;
esac

