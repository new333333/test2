1. Build and deploy the sample by running "ant deploy" from the command line.
This includes a jar file, jsp file and some other required configuration files.

2. Edit <icecore installation>/webapps/ssf/WEB-INF/portlet.xml and add the 
following:

    <portlet>
        <description xml:lang="en">Sample Portlet</description>
        <portlet-name>ss_sample</portlet-name>
        <display-name xml:lang="en">Sample Portlet</display-name>
        <portlet-class>org.springframework.web.portlet.DispatcherPortlet</portlet-class>
		<init-param>
			<name>contextConfigLocation</name>
			<value>/WEB-INF/context/portlet/sample.xml</value>
		</init-param>
        <expiration-cache>300</expiration-cache>
        <supports>
            <mime-type>text/html</mime-type>
            <portlet-mode>view</portlet-mode>
        </supports>
        <supported-locale>en</supported-locale>
        <resource-bundle>content.sample</resource-bundle>
    </portlet>


3. Edit <icecore installation>/webapps/ssf/WEB-INF/liferay-portlet.xml and 
add the following:

	<portlet>
		<portlet-name>ss_sample</portlet-name>
		<restore-current-view>false</restore-current-view>
		<maximize-edit>true</maximize-edit>
		<instanceable>true</instanceable>
	</portlet>

4. Edit <icecore installation>/webapps/ssf/WEB-INF/liferay-display.xml and
add the following:

		<portlet id="ss_sample" />

5. Edit <icecore installation>/webapps/ssf/WEB-INF/web.xml and add the 
following two sections in appropriate places in the file:

	<servlet>
		<servlet-name>ss_sample</servlet-name>
		<servlet-class>com.liferay.portal.kernel.servlet.PortletServlet</servlet-class>
		<init-param>
			<param-name>portlet-class</param-name>
			<param-value>org.springframework.web.portlet.DispatcherPortlet</param-value>
		</init-param>
		<load-on-startup>0</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>ss_sample</servlet-name>
		<url-pattern>/ss_sample/*</url-pattern>
	</servlet-mapping>


6. (Optional) Edit <icecore installation>/webapps/ssf/WEB-INF/context/adapter-servlet.xml
and add the following line to the <list> element of the "adaptedPortlets" bean
definition. This step is necessary ONLY IF the new portlet is to plug into the
ICEcore's proprietary adapter environment.

<value>ss_sample</value>

7. Restart ICEcore server.

8. Log into ICEcore using browser, select Add Content, add Sample Portlet from
under ICEcore category.
