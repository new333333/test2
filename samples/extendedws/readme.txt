1. Build the sample and deploy the jar file containing the additional web
services operation into the ICEcore installation by running "ant deploy" 
from the command line.

2. Edit <icecore installation>/webapps/ssf/WEB-INF/server-config.wsdd 
(for WS-Security authentication) and <icecore installation>/webapps/ssr/WEB-INF/server-config.wsdd 
(for Basic Authentication) and replace 
com.sitescape.team.remoting.ws.service.folder.FolderServiceEndpoint with 
com.sitescape.team.samples.extendedws.server.ExtendedFolderServiceEndpoint.

3. Edit <icecore installation>/webapps/ssf/WEB-INF/context/applicationContext-ext.xml
and add the following bean definition. This bean definition overrides the
factory-shipped default defined in applicationContext.xml.

	<bean id="folderService" parent="wsProxyTemplate">
		<property name="target">
			<bean class="com.sitescape.team.samples.extendedws.server.ExtendedFolderServiceImpl" parent="dependOnAllModulesTemplate">
			</bean>
		</property>
		<property name="proxyInterfaces">
			<value>com.sitescape.team.samples.extendedws.server.ExtendedFolderService</value>
		</property>
	</bean>

4. Restart ICEcore server.

5. To test the newly added WS operation, go to samples/wsclient, execute
"ant build", and run "wsclient.bat getFolderTitle 1" (on Windows). 
It runs the sample client program that invokes the getFolderTitle WS operation
that was built and deployed in the step 1 above.

You can also point your browser to http://<icecore host>:<port>/ssf/ws/FolderService?wsdl
or http://<icecore host>:<port>/ssr/ws/FolderService?wsdl to make sure that 
the newly added WS operation is properly exported through the WSDL.
