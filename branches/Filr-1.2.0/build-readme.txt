Building and Deploying Kablink Within Development Environment 
=============================================================

1. INSTALLING Kablink BINARY AND SOURCE DISTRIBUTIONS

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

(1) Download and install Kablink Liferay/Tomcat bundle kit.

* Refer to the Kablink Installation and Configuration Guide for detailed
  instructions. For the purpose of the remainder of this document, it is
  assumed that the bundled binary distribution is installed into C:/kablink
  directory.
  
(2) Download and install Kablink source distribution.

* For the purpose of the remainder of this document, it is assumed that 
  the source package is unpacked into C:/kablink-2.0-src directory.
  
2. BUILDING Kablink SOURCE AND/OR RE-DEPLOYING IT INTO BINARY INSTALLATION USING ANT

  Note: You can not use the source distribution alone to build or re-package a
        full bundled binary distribution from scratch. You must first install 
        a bundle kit before (re)deploying the changes you make to the source 
        tree to the binary installation.

  Note: If you installed the bundle kit in a directory different from
        C:/kablink, modify the value of the app.server.liferay.tomcat.dir
        property in C:/kablink-2.0-src/app.server.properties file to reflect the
        new location.
        
(1) Building Kablink artifacts

* For clean rebuild of Kablink:

  cd C:/kablink-2.0-src
  ant clean-build
  
  Note: The above command does not deploy the updated files. You can locate
  the updated files (eg. .jar file) from individual sub-project directories.

(2) Building and deploying Kablink

* For clean rebuild of Kablink and deployment into the binary installation:

  Note: Shutdown Kablink server before executing the following command.
  
  cd C:/kablink-2.0-src
  ant clean-build-and-full-deploy

  Note: You can use build-and-full-deploy instead of clean-build-and-full-deploy
  in the above command if you want a slightly faster rebuild that only recompiles
  modified code. The time difference is not significant though.
  
(3) Building and deploying at sub-projects level

* Most of the subprojects have a deploy Ant target that lets you deploy updated
  artifacts at smaller granularity. Depending on the type and scope of the
  changes being made, this may provide a faster way to deploy and test your
  changes. When this smaller control is not effective, you must use the full
  build command described above. Re-deploying modified jar files or configuration
  files almost always requires a restart of the Kablink server.
  
* The web subproject contains an additional Ant target called deploy-light 
  which could be handy when all you need to re-deploy to the installation area
  are the common web-tier artifacts such as .jsp, .html, .js, .css, and image
  files, etc. The main difference is that you can deploy these artifacts
  using deploy-light without having to stop and restart the Kablink server.
  
