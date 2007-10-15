Building and Deploying ICEcore Within Development Environment
=============================================================

1. INSTALLING ICEcore BINARY AND SOURCE DISTRIBUTIONS

(0.1) Download and install a Java development kit

* If the JDK is already installed, skip to (0.2).

* Download a Java Development Kit (JDK) release (version 1.5.x or later) from:

    http://java.sun.com/javase/

* Install the JDK according to the instructions included with the release.

* Set an environment variable JAVA_HOME to the pathname of the directory into
  which you installed the JDK release.

(0.2) Download and install Apache Ant 1.6.x on your computer

* If Apache Ant 1.6.x is already installed on your computer, skip to (1).

* Download a binary distribution of Ant 1.6.x from:

    http://ant.apache.org/bindownload.cgi

* Unpack the binary distribution into a convenient location so that the
  Ant release resides in its own directory (conventionally named
  "apache-ant-[version]").  For the purposes of the remainder of this 
  document, the symbolic name "${ant.home}" is used to refer to the full
  pathname of the release directory.

* Create an ANT_HOME environment variable to point the directory ${ant.home}.

* Modify the PATH environment variable to include the directory
  ${ant.home}/bin in its list.  This makes the "ant" command line script
  available, which will be used to actually perform the build.

(1) Download and install ICEcore Liferay/Tomcat bundle kit.

* Refer to the ICEcore Installation and Configuration Guide for detailed
  instructions. For the purpose of the remainder of this document, it is
  assumed that the bundled binary distribution is installed into C:/icecore
  directory.
  
(2) Download and install ICEcore source distribution.

* For the purpose of the remainder of this document, it is assumed that 
  the source package is unpacked into C:/icecore-1.0 directory.
  
2. BUILDING ICEcore SOURCE AND RE-DEPLOYING IT INTO BINARY INSTALLATION USING ANT

  Note: You can not use the source distribution alone to build or re-package a
        full bundled binary distribution from scratch. You must first install 
        a bundle kit before (re)deploying the changes you make to the source 
        tree to the binary installation.

  Note: If you installed the bundle kit in a directory different from
        C:/icecore, modify the value of the app.server.liferay.tomcat.dir
        property in C:/icecore-1.0/app.server.properties file to reflect the
        new location.
        
(1) Building and deploying ICEcore

* For clean rebuild of ICEcore and deployment into the binary installation:
  
  cd C:/icecore-1.0
  ant clean-build-and-full-deploy
  
* For a quicker rebuild of only modified code and redeployment you can use:

  cd C:/icecore-1.0
  ant build-and-full-deploy

