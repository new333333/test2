@echo off

setlocal
	set GWT_TOOLS="../tools/gwt"
	%JAVA_HOME%/bin/java -Xmx256M -cp "%~dp0\src;%~dp0\bin;%GWT_TOOLS%/gwt-user.jar;%GWT_TOOLS%/gwt-dev.jar" com.google.gwt.dev.Compiler -war "%~dp0\www" %* org.kablink.teaming.gwt.GwtTeaming
endlocal

