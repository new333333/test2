java ^
-Dhost=localhost ^
-Dport=8080 ^
-Dusername=admin ^
-Dpassword=test ^
-Dauthmethod=wss ^
-cp classes;lib/wsdl4j.jar;lib/activation.jar;lib/mail.jar;lib/commons-httpclient.jar;lib/dom4j.jar;lib/axis.jar;lib/axis-ant.jar;lib/jaxrpc.jar;lib/commons-logging.jar;lib/commons-discovery.jar;lib/saaj.jar;lib/wss4j.jar;lib/addressing.jar;lib/bcprov-jdk15.jar.jar;lib/commons-codec.jar;lib/opensaml.jar;lib/xalan.jar;lib/xmlsec.jar;lib/ssf-wsclient.jar ^
com.sitescape.team.samples.wsclient.TeamingServiceClientWithCall %1 %2 %3 %4 %5 %6 %7 %8 %9
