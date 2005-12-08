@echo off

cd \ss\v8\sportal\indexer-lucene
set CLASSPATH=.\luceneapi.jar;.\luceneserver.jar;..\lib\lucene.jar
start /min rmiregistry -J-Dsun.rmi.loader.logLevel=VERBOSE