This sample shows how to write a simple portlet based on Springframework's 
portlet framework and deploy it as part of Teaming portlet application.
Deploying a portlet as part of Teaming has unique advantage over packaging
it as a separate and independent portlet application that happens to be hosted
by the same portal in that the portlet would have full access to the internals 
of Teaming software. Consequently, it is important to understand this as one 
of the mechanisms for extending Teaming's out-of-the-box functionality.
If your goal is to write a standalone portlet that has nothing to do with
Teaming, then it is strongly advised to use the standard extension/deployment 
mechanism that the portal product provides instead.

IF YOU HAVE ACCESS TO FULL SOURCE TREE (preferred mode of development)
======================================================================
When developed in this mode, the modifications are made to the source tree and
the required artifacts are deployed onto the binary installation. This mode
allows safe and repeated development/deployment cycles, and is a preferred
mode of development. 

Important: Your binary installation and the source tree must match in terms
of product edition in order for the techniques described in this section to
work properly. For example, if your binary installation contains Enterprise
edition of the product, you must NOT use this technique unless you also have 
access to the Enterprise edition of the source tree.

1. Stop Kablink server.

2. Build and deploy the sample by running "ant deploy" from the command line.
This builds teaming-sample-portlet.jar and copies it into 
<kablink installation>/webapps/ssf/WEB-INF/lib directory. It also copies
a jsp file and some required configuration files into respective sub-directories
under <kablink installation>/webapps/ssf/WEB-INF.

3. Edit <kablink source>/web/docroot/WEB-INF/portlet.xml and add the following:

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

4. Edit <kablink source>/web/docroot/WEB-INF/liferay-portlet.xml and add the following:

	<portlet>
		<portlet-name>ss_sample</portlet-name>
		<restore-current-view>false</restore-current-view>
		<maximize-edit>true</maximize-edit>
		<instanceable>true</instanceable>
	</portlet>

5. Edit <kablink source>/web/docroot/WEB-INF/liferay-display.xml and add the following:

	<portlet id="ss_sample" />

6. (Optional) Edit <kablink source>/web/docroot/WEB-INF/context/adapter-servlet.xml
and add the following line to the <list> element of the "adaptedPortlets" bean
definition. This step is necessary ONLY IF the new portlet is to plug into the
Kablink's proprietary adapter environment. In other words, any Kablink portlet 
designed to serve URLs of format http://<host>:<port>/ssf/a belongs to this category.

	<value>ss_sample</value>

7. Execute clean-build-and-full-deploy Ant target in <kablink source>/build.xml
appropriate for the edition of the product you're working with.

8. Start Kablink server.

9. Log into Kablink using browser, select Add Content, add Sample Portlet from
under Kablink category.

IF YOU ONLY HAVE BINARY INSTALLATION
====================================

1. Stop Kablink server.

2. Build and deploy the sample by running "ant deploy" from the command line.
This builds teaming-sample-portlet.jar and copies it into 
<kablink installation>/webapps/ssf/WEB-INF/lib directory. It also copies
a jsp file and some required configuration files into respective sub-directories
under <kablink installation>/webapps/ssf/WEB-INF.

3. Edit <kablink installation>/webapps/ssf/WEB-INF/portlet.xml and add the 
following. If you prefer making this changes in the source tree and deploy
the changes from there

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


4. Edit <kablink installation>/webapps/ssf/WEB-INF/liferay-portlet.xml and 
add the following:

	<portlet>
		<portlet-name>ss_sample</portlet-name>
		<restore-current-view>false</restore-current-view>
		<maximize-edit>true</maximize-edit>
		<instanceable>true</instanceable>
	</portlet>

5. Edit <kablink installation>/webapps/ssf/WEB-INF/liferay-display.xml and
add the following:

	<portlet id="ss_sample" />

6. (Optional) Edit <kablink installation>/webapps/ssf/WEB-INF/context/adapter-servlet.xml
and add the following line to the <list> element of the "adaptedPortlets" bean
definition. This step is necessary ONLY IF the new portlet is to plug into the
Kablink's proprietary adapter environment.

	<value>ss_sample</value>

7. Edit <kablink installation>/webapps/ssf/WEB-INF/web.xml and add the 
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

8. Start Kablink server.

9. Log into Kablink using browser, select Add Content, add Sample Portlet from
under Kablink category.
