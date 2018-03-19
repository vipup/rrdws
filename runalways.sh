#!/bin/bash
for (( ; ; ))
do
	echo "infinite loops [ hit CTRL+C to stop]"
	export MAVEN_OPTS="-Xmx1531m -server -XX:+ExitOnOutOfMemoryError "
#	export JAVA_HOME=/usr/lib/jvm/java-8-oracle/
	git pull
#	export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64/
	mv target/catalina.base/11/logs/rrdwsout.log   /var/log/rrd/$(date +%Y%m%d-%H%M%S)rrdwsout.log
	mvn    clean  package -Dmaven.test.skip=true
	mvn    exec:java -Dexec.mainClass=cc.co.llabor.websocket.WS2RRDPump  -Dcatalina.base=target/catalina.base/11 -Dnet.sf.jsr107cache.CacheFactory=cc.co.llabor.cache.BasicCacheFactory   -Dexec.classpathScope=test   -Dmaven.test.skip=true

done

