#!/bin/sh
echo  mvn -o -e help:active-profiles tomcat:redeploy -Dcccache -Dmaven.test.skip=true -DRRD8080
env 
mvn -o -e help:active-profiles tomcat:redeploy -Dcccache -Dmaven.test.skip=true -DRRD8080
