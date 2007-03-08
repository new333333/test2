package com.sitescape.team.samples.remoting.client.ws.axis;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.FileProvider;

import com.sitescape.team.samples.remoting.client.util.FacadeClientHelper;

/**
 * This sample program fetches workspace tree as XML string from the server 
 * through a SOAP-based Web Services interface and print them to the console.
 *
 */
public class PrintWorkspaceTree
{
	public static void main(String[] args) {
		try {
			printWorkspaceTree();
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	private static void printWorkspaceTree() throws Exception {
		// Replace the hostname in the endpoint appropriately.
		String endpoint = "http://peace:8080/ssf/ws/Facade";
		
		// Make sure that the client_deploy.wsdd file is accessible to the program.
		EngineConfiguration config = new FileProvider("client_deploy.wsdd");
		
		Service service = new Service(config);
		
		Call call = (Call) service.createCall();
		
		call.setTargetEndpointAddress(new URL(endpoint));
		
		// We are going to invoke the remote operation to fetch from the top
		// workspace all the way down to top-level folders. 
		call.setOperationName(new QName("getWorkspaceTreeAsXML"));
		
		String wsTreeAsXML = (String) call.invoke(new Object[] {new Long(1), new Integer(-1)});
		
		FacadeClientHelper.printXML(wsTreeAsXML);
		
		System.out.println("***************************************************************");
		
		// This time, we are going to fetch only one level deep from the top
		// workspace. You can fetch the tree starting from different workspace 
		// or folder by specifying its id as the first argument. 
		
		wsTreeAsXML = (String) call.invoke(new Object[] {new Long(1), new Integer(1)});
		
		FacadeClientHelper.printXML(wsTreeAsXML);
	}
}