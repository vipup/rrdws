#!/bin/bash
	export MAVEN_OPTS="-Xmx1531m -server -XX:+ExitOnOutOfMemoryError "
	mv target/catalina.base/11/logs/rrdwsout.log   /var/log/rrd/$(date +%Y%m%d-%H%M%S)rrdwsout.log
	mvn    exec:java -Dexec.mainClass=cc.co.llabor.websocket.WS2RRDPump  -Dcatalina.base=target/catalina.base/11 -Dnet.sf.jsr107cache.CacheFactory=cc.co.llabor.cache.BasicCacheFactory   -Dexec.classpathScope=test   -Dmaven.test.skip=true

