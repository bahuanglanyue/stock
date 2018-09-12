#!/bin/sh
pid=`ps -ef | grep -i GatewayStart | grep -v grep | awk '{print $2}'| xargs`

if [ "$pid" != ""  ]; then
        echo "have pid, will restart"
        kill -9 $pid
fi

cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR/conf
LIB_DIR=$DEPLOY_DIR/lib
LIB_JARS=`ls $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`

java -server -Xms256m -Xmx512m -classpath $CONF_DIR:$LIB_JARS  com.yingli.main.GatewayStart