This project contains a sample listener class called FolderModuleListener which
demonstrates how to define interceptors for addEntry() method. This project 
also comes with an Ant build file that can aid you with the build and the 
deployment of a jar file containing the listener class. 

In order for Teaming server to recognize the listener class, it will have to be
registered in the ssf-ext.properties file by adding a property as shown below:

module.event.listeners.org.kablink.teaming.module.folder.FolderModule=org.kablink.teaming.samples.moduleeventlisteners.FolderModuleListener
