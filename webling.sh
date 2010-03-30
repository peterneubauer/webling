#!/bin/bash

CWD=`dirname $0`
WORKING_D=`(cd $CWD && pwd )`
LOGFILE="$WORKING_D/log/webling-main.log"
PIDFILE="$WORKING_D/tmp/pids/webling.pid"
WORKERPIDS="$WORKING_D/tmp/pids/worker.pids"

# Path to jar
JAR="$WORKING_D/target/webling-*-standalone.jar"

# Find Java
if [ "$JAVA_HOME" = "" ] ; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

# Set Java options
if [ "$JAVA_OPTIONS" = "" ] ; then
  JAVA_OPTIONS="-Xms64M -Xmx256M"
fi

WORKER_JAVA_OPTIONS="-Xms32M -Xmx64M"

start_worker ()
{
  $JAVA $WORKER_JAVA_OPTIONS -cp $JAR com.tinkerpop.webling.GremlinWorker $1 > /dev/null & echo $! >> $WORKERPIDS
}

build_webling ()
{
  mvn install -q
}

kill_workers ()
{
  x=0
  
  if [ -f $WORKERPIDS ] ; then
    while [ $x -lt $(wc -l <$WORKERPIDS) ]
    do
      let x=x+1
      kill -9 `head -n $x $WORKERPIDS | tail -n 1`
    done
    
    rm -f $WORKERPIDS
  fi

  echo "$x gremlin workers killed."
}

webling_start ()
{
    PORT=$1

    if [ "$PORT" = "" ] ; then
        PORT=8080
    fi

    echo "Starting Webling on $PORT port..."

    # Launch the application
    $JAVA $JAVA_OPTIONS -cp $JAR com.tinkerpop.webling.WeblingLauncher $PORT > $LOGFILE & 
    PID=$!
    echo -n "PWD: "
    pwd
    echo "Pid is ${PID} -> ${PIDFILE}"
    ps ax | grep "${PID}"
    echo $PID > $PIDFILE
    echo "-> SUCCESS"
}

webling_stop ()
{
    echo "Stopping Webling..."

    if [ -f $PIDFILE ] ; then
        PID=`cat $PIDFILE`
        kill -3 $PID

        if kill -9 $PID ; then
            echo "-> SUCCESS"
        else
            echo "-> FAILED"
        fi

        rm -f $PIDFILE
    fi

    kill_workers
}

usage()
{
    echo ""
    echo "Usage: $0 <command>"
    echo ""
    echo "where <command> is one of the following:"
    echo "    start <port> - start Webling if it is not running"
    echo "    stop - stop Webling if it is running"
    echo "    status - report whether Webling is running"
    echo "    restart <port> - stop and restart Webling"
    echo "    usage, help - print this message"
}

case "$1" in
    usage|help)
        usage
        ;;
    start)
        webling_start $2
        ;;
    stop)
        webling_stop
        ;;
    restart)
        webling_stop
        webling_start $2
        ;;
    status)
        if [ -f $PIDFILE ] ; then
            echo "Webling is running on pid `cat $PIDFILE`."
        else
            echo "Webling is not running yet. Use '$0 start' to get it running."
        fi
        ;;
    start_worker)
        start_worker $2 
        ;;

    build)
        build_webling
        ;;
    build_and_go)
        build_webling
        webling_start $2
        ;;
    *)
        usage
esac
