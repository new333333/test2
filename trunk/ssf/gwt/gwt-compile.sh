#!/bin/sh
APPDIR=`dirname $0`;
java  -Xmx256M -cp "$APPDIR/src:$APPDIR/bin:/usr/local/gwt-linux-1.5.3/gwt-user.jar:/usr/local/gwt-linux-1.5.3/gwt-dev-linux.jar" com.google.gwt.dev.GWTCompiler -out "$APPDIR/www" "$@" org.kablink.teaming.gwt.GwtTeaming
