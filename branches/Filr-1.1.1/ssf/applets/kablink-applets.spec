#
# spec file for package kablink-applets 
#
# Copyright (c) 2008 SUSE LINUX Products GmbH, Nuernberg, Germany.
# This file and all modifications and additions to the pristine
# package are under the same license as the package itself.
#
# Please submit bugfixes or comments via http://bugs.opensuse.org/
#

# norootforbuild


Name:           kablink-applets
License:        GPL v2 or later
Version:        2.0.0
Release:        1
Summary:        Kablink applets
Group:          Productivity/Networking/Novell
BuildRoot:      %{_tmppath}/%{name}-%{version}-build
Source:         kablink-applets-%{version}.tar.bz2
Buildrequires:  ant-contrib ant-antlr axis java-1_6_0-openjdk-demo tomcat6
BuildArch:      noarch
Requires:       tomcat6

%description
This package has the applets for the kablink teaming open source product


Authors:
--------



%prep
%setup -q

%build
env >mikenv.txt
cd applets
ant deploy

%install
install -d -m 755 $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf
install -d -m 755 $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets
install -d -m 755 $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets/workflow-viewer 
install -d -m 755 $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets/fileedit
install -d -m 755 $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets/fileopen
install -d -m 755 $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets/droptarget 

install $RPM_BUILD_DIR/kablink-applets-%{version}/applets/workflow-viewer/kablink-teaming-workflowviewer-applet.jar $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets/workflow-viewer/kablink-teaming-workflowviewer-applet.jar
install $RPM_BUILD_DIR/kablink-applets-%{version}/applets/fileedit/kablink-teaming-fileedit-applet.jar $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets/fileedit/kablink-teaming-fileedit-applet.jar
install $RPM_BUILD_DIR/kablink-applets-%{version}/applets/fileopen/kablink-teaming-fileopen-applet.jar  $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets/fileopen/kablink-teaming-fileopen-applet.jar 
install $RPM_BUILD_DIR/kablink-applets-%{version}/applets/droptarget/kablink-teaming-droptarget-applet.jar $RPM_BUILD_ROOT/usr/share/tomcat6/webapps/ssf/applets/droptarget/kablink-teaming-droptarget-applet.jar 

%files
%defattr(-,root,root)
%dir /usr/share/tomcat6/webapps/ssf
%dir /usr/share/tomcat6/webapps/ssf/applets
%dir /usr/share/tomcat6/webapps/ssf/applets/workflow-viewer
%dir /usr/share/tomcat6/webapps/ssf/applets/fileedit
%dir /usr/share/tomcat6/webapps/ssf/applets/fileopen
%dir /usr/share/tomcat6/webapps/ssf/applets/droptarget
/usr/share/tomcat6/webapps/ssf/applets/workflow-viewer/kablink-teaming-workflowviewer-applet.jar
/usr/share/tomcat6/webapps/ssf/applets/fileedit/kablink-teaming-fileedit-applet.jar
/usr/share/tomcat6/webapps/ssf/applets/fileopen/kablink-teaming-fileopen-applet.jar
/usr/share/tomcat6/webapps/ssf/applets/droptarget/kablink-teaming-droptarget-applet.jar
