#!/bin/bash
#
# Start-/ Stop-Script for MediaMagpie
#
# By setting the System.property 'jetty.socketConnector.port' to another port number, the
# web-server can run on a different port that the default 8088
#
# 12.08.2011: Ralf Wehner
###################################################################################

## set distribution specific settings
#MGR_USER=magpie
MGR_DEPLOY_MODE=live
#MGR_DEPLOY_MODE=local
#JAVA_HOME=
#ADDITIONAL_PARAMETERS="-Dpersistent.unit=mysql"
#ADDITIONAL_PARAMETERS="-Dwebapp.port.http=8080 -Dwebapp.port.https=9191 -Dwebapp.context.path=/mediamagpie"
#-Xms256m -Xmx1024m -XX:MaxPermSize=256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp
VM_ARGS="-Xmx250m -XX:MaxPermSize=120m -XX:+CMSClassUnloadingEnabled $ADDITIONAL_PARAMETERS"
#########################

CLASS=de.wehner.mediamagpie.conductor.StartJetty9
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
    echo "Usage: $0 {start|stop|run|restart|check|supervise} [jetty] [deployMode] [ CONFIGS ... ] "
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
# This is how the server will be started
#####################################################

RUN_ARGS="$VM_ARGS -Ddeploy.mode=$MGR_DEPLOY_MODE -Dlogback.configurationFile=conf/logback.xml -classpath $CLASSPATH $CLASS"
RUN_CMD="$JAVA $RUN_ARGS"
#debug
RUN_CMD="$JAVA -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5000 $RUN_ARGS"

##################################################
# Do the action
##################################################
case "$ACTION" in
  start)
        echo -n "Starting MediaMagpie Web-Application: "

          if [ -f $MGR_PID ]
          then            
            if running $MGR_PID
            then
              echo "Already Running!!"
              exit 1
            else
              # dead pid file - remove
              rm -f $MGR_PID
            fi
          fi

          if [ x$MGR_USER != x ] 
          then
              touch $MGR_PID
              chown $MGR_USER $MGR_PID
              su - $MGR_USER -c "
                $RUN_CMD 1> stdout.log 2> stderr.log &
                PID=\$!
                disown \$PID
                echo \$PID > $MGR_PID"
          else
              $RUN_CMD 1> stdout.log 2> stderr.log &
              PID=$!
              echo PID: $PID
              disown $PID
              echo $PID > $MGR_PID
          fi

          echo "STARTED Status-Manager demon `date`" 
        ;;

  stop)
        echo -n "Stopping MediaMagpie Web-Application: "
      PID=`cat $MGR_PID 2>/dev/null`
          TIMEOUT=10
          while running $MGR_PID && [ $TIMEOUT -gt 0 ]
          do
            kill $PID 2>/dev/null
            sleep 1
            let TIMEOUT=$TIMEOUT-1
          done
          
          if [ $TIMEOUT -eq 0 ]; then
            echo "Kill $PID with -9 Param!"
            kill -9 $PID 2>/dev/null
          fi
	      
      rm -f $MGR_PID
          echo OK
        ;;

  restart)
        MGR_SH=$0
        if [ ! -f $MGR_SH ]; then
          if [ ! -f $MGR_HOME/bin/mediamagpie.sh ]; then
            echo "$MGR_HOME/bin/mediamagpie.sh does not exist."
            exit 1
          fi
          MGR_SH=$MGR_HOME/bin/mediamagpie.sh
        fi
        $MGR_SH stop $MGR_DEPLOY_MODE
        sleep 5
        $MGR_SH start $MGR_DEPLOY_MODE
        ;;

  supervise)
       #
       # Under control of daemontools supervise monitor which
       # handles restarts and shutdowns via the svc program.
       #
         exec $RUN_CMD
         ;;

  run|demo)
        echo "Running MediaMagpie Web-Application: "

        if [ -f $MGR_PID ]
        then
            if running $MGR_PID
            then
              echo "Already Running!!"
              exit 1
            else
              # dead pid file - remove
              rm -f $MGR_PID
            fi
        fi

        exec $RUN_CMD
        ;;

  check)
        echo "Checking arguments to MediaMagpie Web-Application: "
        echo "MGR_HOME     =$MGR_HOME"
        echo "MGR_PID      =$MGR_PID"
        echo "JAVA         =$JAVA"
        echo "CLASSPATH    =$CLASSPATH"
        echo "RUN_CMD      =$RUN_CMD"
        echo "CLASS_COMMAND=$CLASS_COMMAND"
        echo "MGR_USER     =$MGR_USER"
        echo
        
        if [ -f $MGR_PID ]
        then
            echo "MediaMagpie running pid="`cat $MGR_PID`
            exit 0
        fi
        exit 1
        ;;

*)
        usage
    ;;
esac

exit 0
