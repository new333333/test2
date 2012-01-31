#! /bin/sh

# The following controls what get's displayed as this shell script
# runs.
WSCLIENT_SHOWOPTIONS="false"
 WSCLIENT_JVMVERBOSE="false"


# Displays the options we'll use to run the test(s).
function showOptions() {
	if [ "$WSCLIENT_SHOWOPTIONS" == "true" ] || [ "$WSCLIENT_SHOWOPTIONS" == "TRUE" ] ; then
		clear
		echo WSCLIENT_CLASSPATH
		echo ------------------
		echo $WSCLIENT_CLASSPATH
		echo
		echo WSCLIENT_OPTS
		echo -------------
		echo $WSCLIENT_OPTS
		echo
		echo WSCLIENT_MAIN
		echo -------------
		echo $WSCLIENT_MAIN
		echo
		echo
		echo
	fi
}


# Defines the options that we need to run the test(s).
function setOptions() {
	# Define the classpath...
	WSCLIENT_CLASSPATH="classes"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/activation.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/addressing.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/axis.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/bcprov-jdk15.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/commons-codec.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/commons-digester.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/commons-discovery.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/commons-httpclient.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/commons-logging.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/dom4j.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/jaxen.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/jaxrpc.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/kablink-teaming-util-search.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/kablink-teaming-wsclient.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/mail.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/opensaml.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/saaj.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/wsdl4j.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/wss4j.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/xalan.jar"
	WSCLIENT_CLASSPATH=$WSCLIENT_CLASSPATH";lib/xmlsec.jar"

	# ...define the JVM options...
	WSCLIENT_OPTS="-Dhost=localhost"
	WSCLIENT_OPTS=$WSCLIENT_OPTS" -Dport=8080"
	WSCLIENT_OPTS=$WSCLIENT_OPTS" -Dusername=admin"
	WSCLIENT_OPTS=$WSCLIENT_OPTS" -Dpassword=admin"
	WSCLIENT_OPTS=$WSCLIENT_OPTS" -Dauthmethod=basic"
	WSCLIENT_OPTS=$WSCLIENT_OPTS" -classpath "\"$WSCLIENT_CLASSPATH\"
	if [ "$WSCLIENT_JVMVERBOSE" == "true" ] || [ "$WSCLIENT_JVMVERBOSE" == "TRUE" ] ; then
		WSCLIENT_OPTS=$WSCLIENT_OPTS" -verbose"
	fi

	# ...define the class we start with...
	WSCLIENT_MAIN="org.kablink.teaming.samples.wsclient.TeamingServiceClientWithCall"

	# ...and if requested to do so, show the options.
	showOptions
}

setOptions
java $WSCLIENT_OPTS $WSCLIENT_MAIN %1 %2 %3 %4 %5 %6 %7 %8 %9

