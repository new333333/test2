1. Build the sample and deploy the jar file containing the additional web
services operation into the Kablink installation by running "ant deploy" 
from the command line.

2. Edit <kablink installation>/webapps/ssf/WEB-INF/server-config.wsdd 
(for WS-Security authentication) and <kablink installation>/webapps/ssr/WEB-INF/server-config.wsdd 
(for Basic Authentication) and replace 
org.kablink.teaming.remoting.ws.TeamingServiceEndpoint with 
org.kablink.teaming.samples.extendedws.server.ExtendedTeamingServiceEndpoint.

3. Edit <kablink installation>/webapps/ssf/WEB-INF/context/applicationContext-ext.xml
and add the following bean definition. This bean definition overrides the factory-shipped
default definition of the bean with the same name in applicationContext.xml.

	<bean id="folderService" parent="wsProxyTemplate">
		<property name="target">
			<bean class="org.kablink.teaming.samples.extendedws.server.ExtendedFolderServiceImpl" parent="dependOnAllModulesTemplate">
			</bean>
		</property>
		<property name="proxyInterfaces">
			<value>org.kablink.teaming.samples.extendedws.server.ExtendedFolderService</value>
		</property>
	</bean>

4. Restart Kablink server.

5. To test the newly added WS operation, go to samples/wsclient, execute
"ant", and run "teamingservice-client-with-call.bat getFolderTitle 1" 
(on Windows). 
It runs the sample client program that invokes the folder_getFolderTitle 
WS operation that was built and deployed in the step 1 above.

You can also point your browser to http://<kablink host>:<port>/ssf/ws/TeamingServiceV1?wsdl
or http://<kablink host>:<port>/ssr/ws/TeamingServiceV1?wsdl to verify that 
the newly added WS operation is properly exported through the Teaming WSDL.
