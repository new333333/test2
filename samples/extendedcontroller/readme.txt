This sample project demonstrates how to extend Teaming's presentation tier
functionality by extending a controller class. This project includes the
following:
 
1. org.kablink.teaming.samples.extendedcontroller.ExtendedListFolderController
class extending Teaming's factory-shipped ListFolderController class to
add some custom behavior in the presentation tier. 

2. ssf-ext.xml configuration file that tells Teaming runtime to use this
extended class instead of the factory-shipped one. 

3. Ant build file (build.xml) that aids you with the build and the deploy
of a jar file containing the custom class as well as the accompanying 
configuration file. To build and deploy the artifacts, execute "deploy"
Ant target. Make sure to restart Teaming to bring it to effect.  

