1) If you don't already have Liferay Portal installed, go to Liferay web site 
(http://www.liferay.com) and download, install and set up Liferay Portal
(refer to Liferay documentation for details).

2) Make sure that you have Ant installed. 

3) Download Kablink source from the trunk (https://forgesvn1.novell.com/svn/kablink/trunk/ssf)

4) First, build Kablink Teaming software by executing Ant script. This assumes
that you've downloaded Kablink source into /ssf.   

cd /ssf
ant build

5) Go to /ssf/samples/portal/jsr168/webroot/WEB-INF/classes/config directory, 
and edit ssf.properties file to change the value of teaming.url property to 
point to your standalone Teaming server that you want to integrate into the
Liferay portal. Alternatively, you can create ssf-ext.properties file in the
same directory and put that property and the new value in that file (instead 
of modifying ssf.properties file). This allows your custom values to survive
future source tree update.

6) Build sample Teaming portlet application WAR file as follows.  

cd /ssf/samples/portal/jsr168
ant build

7) Create a text file with the name Language-ext.properties and add the following
line in the file. 

category.teaming=Teaming

Save or copy this file into a place under Liferay where the portal can properly
locate this at runtime via classpath. For Liferay bundled with Tomcat container,
this location would be <tomcat>/webapps/ROOT/WEB-INF/classes/content. If the
"content" directory does not exist, create it. 

Note: This example assumes that the system language is English.

8) (Re)start Liferay portal, log into Liferay as admin, go to Plugins Installation
page, and install Teaming portlet plugin by uploading Teaming portlet WAR file.
For example, if running Liferay 5.2.*, click Upload File button, click Browse...
button, and navigate to the directory and select the Teaming portlet WAR file
built from the step 6 above (/ssf/samples/portal/jsr168/teaming-portlet.war), and
then click Install button. Wait for about a minute for Liferay to finish 
installing the new plugin.

9) Go to Add Application, expand Teaming category, and click Add button to add
Teaming application to the page. Now, you should see an iFrame window showing
Teaming's page (most likely a login screen). 

IMPORTANT NOTE: This sample does NOT demonstrate how users can setup and achieve
SSO between Liferay and Teaming.