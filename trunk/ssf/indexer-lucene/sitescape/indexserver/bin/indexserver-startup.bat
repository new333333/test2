@echo off


cd \ss\v8\sportal\indexer-lucene
cd
echo on
echo %CLASSPATH%
set CLASSPATH=.\luceneserver.jar;.\luceneapi.jar;..\lib\lucene.jar;..\lib\commons-logging.jar
javaw -Xmx512m -Djava.security.policy=policy.all com.sitescape.ef.lucene.server.SsfIndexServer