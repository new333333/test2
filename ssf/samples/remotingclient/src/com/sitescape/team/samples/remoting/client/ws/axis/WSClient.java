/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.samples.remoting.client.ws.axis;

import java.io.IOException;
import java.net.URL;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;

import com.sitescape.team.samples.remoting.client.util.FacadeClientHelper;
import com.sitescape.util.PasswordEncryptor;

public class WSClient
{
	public static void main(String[] args) {
		if(args.length == 0) {
			printUsage();
			return;
		}

		try {
			if(args[0].equals("printWorkspaceTree")) {
				fetchAndPrintXML("getWorkspaceTreeAsXML", new Object[] {Long.parseLong(args[1]), Integer.parseInt(args[2])});
			} else if(args[0].equals("printPrincipal")) {
				fetchAndPrintXML("getPrincipalAsXML", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2])});			
			} else {
				System.out.println("Invalid arguments");
				printUsage();
				return;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static void fetchAndPrintXML(String operation, Object[] args) throws Exception {
		// Replace the hostname in the endpoint appropriately.
		String endpoint = "http://localhost:8080/ssf/ws/Facade";

		// Make sure that the client_deploy.wsdd file is accessible to the program.
		EngineConfiguration config = new FileProvider("client_deploy.wsdd");

		Service service = new Service(config);

		Call call = (Call) service.createCall();

		call.setTargetEndpointAddress(new URL(endpoint));

		// We are going to invoke the remote operation to fetch the workspace
		//  or folder to print.
		call.setOperationName(new QName(operation));

		// Programmatically set the username. Alternatively you can specify
		// the username in the WS deployment descriptor client_deploy.wsdd
		// if the username is known at deployment time and does not change
		// between calls, which is rarely the case in Aspen.
		call.setProperty(WSHandlerConstants.USER, "liferay.com.1");

		String wsTreeAsXML = (String) call.invoke(args);

		FacadeClientHelper.printXML(wsTreeAsXML);
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("printWorkspaceTree <workspace id> <depth>");
		System.out.println("printPrincipalTree <workspace id> <depth>");
	}
}