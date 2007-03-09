This zip file contains a sample program that demonstrates the use Web Services
to retrieve a workspace tree as XML from the server. The client program is
written using Apache Axis for SOAP and Apache WSS4j for WS-Security support. 

1. Replace localhost with appropriate host name in the endpoint string in 
com.sitescape.team.samples.remoting.client.ws.axis.PrintWorkspaceTree.java
as shown below:

	String endpoint = "http://localhost:8080/ssf/ws/Facade";

2. The program uses liferay.com.1/test as login username/password. 
To specify different username/password, modify "user" parameter value
in client_deploy.wsdd as shown below:

	<parameter name="user" value="liferay.com.1"/>

Also modify the following section of code in 
com.sitescape.team.samples.remoting.client.ws.security.PWCallback.java
to change the username and/or password:
	
	if ("liferay.com.1".equals(id)) {
		String encryptedPassword = PasswordEncryptor.encrypt("test");
		pc.setPassword(encryptedPassword);
	}

3. Recompile the program by executing build.bat. Unix users will have to
convert the batch file.

4. Make sure that the server is running, and execute print_workspace_tree.bat.

5. The XML representation of the workspace tree returned from the WS call
contains the following attributes:

image="/icons/<gif file name>"

The image file can be retrieved directly by prefixing it with 
http://<hostname>:8080/ssf/images. For example, 
http://<hostname>:8080/ssf/images/icons/workspace.gif

6. You can retrieve WSDL from http://<hostname>:8080/ssf/ws/Facade?wsdl
Please note that not all operations described in the WSDL are currently 
operable though (work in progress).
