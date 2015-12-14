This sample project demonstrates how to extend Teaming's presentation tier
functionality by adding a new custom controller class. This project includes
the following:
 
1. org.kablink.teaming.samples.customcontroller.CustomController class that
processes all requests coming in with URLs where the value of "action" parameter 
is "custom_action". It also acts as a mini custom dispatcher based on the value
of the "operation" parameter.

2. ssf-ext.xml configuration file that instructs Teaming runtime to associate
the action parameter value of "custom_action" with the custom controller defined
above. Essentially, this configuration file specifies the necessary mapping from
the custom URLs to the responsible controller so that the control is delegated
to the custom controller when encountering such URLs.

3. Ant build file (build.xml) that aids you with the build and the deploy process.
It builds a jar file containing the custom class. It can also deploy the artifacts
(specifically, jar, jsp, and configuration files) into the right places under the
target Tomcat. To build and deploy the artifacts, execute "deploy" Ant target. 

To see how this controller works, build and deploy it as explained above. Start
Teaming server, log in as usual, and then manually enter the following URL in the browser:

http://localhost:8080/ssf/a/do?p_name=ss_forum&p_action=0&action=custom_action&operation=add_log_message

