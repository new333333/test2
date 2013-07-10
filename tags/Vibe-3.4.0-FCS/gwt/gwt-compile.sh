#!/bin/sh


# Hack to recognize the Mac vs. Linux.
GWT_TOOLS="../tools/gwt"
if [ -z "$Apple_PubSub_Socket_Render" ] ; then
	GWT_DEVJAR="gwt-dev.jar"
else
	GWT_DEVJAR="gwt-dev.jar"
fi


APPDIR=`dirname $0`;
$JAVA_HOME/bin/java  -Xmx256M -cp "$APPDIR/src:$APPDIR/bin:$GWT_TOOLS/gwt-user.jar:$GWT_TOOLS/$GWT_DEVJAR:$GWT_TOOLS/gwt-fx.jar" com.google.gwt.dev.Compiler -war "$APPDIR/www" "$@" org.kablink.teaming.gwt.GwtTeaming

