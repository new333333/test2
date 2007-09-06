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

/**
 * This sample program fetches workspace tree as XML string from the server
 * through a SOAP-based Web Services interface and print them to the console.
 *
 */
public class PrintWorkspaceTree
{
	public static void main(String[] args) {
		try {
			printWorkspaceTree(args[0], new Long(1), new Integer(-1));
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}
	}
	
	static void printWorkspaceTree(String endpoint, Long id, Integer depth) throws Exception {
		System.out.println("Endpoint = " + endpoint);
		// Make sure that the client_deploy.wsdd file is accessible to the program.
		EngineConfiguration config = new FileProvider("client_deploy.wsdd");

		Service service = new Service(config);

		Call call = (Call) service.createCall();

		call.setTargetEndpointAddress(new URL(endpoint));

		// We are going to invoke the remote operation to fetch from the top
		// workspace all the way down to top-level folders.
		call.setOperationName(new QName("getWorkspaceTreeAsXML"));

		// Programmatically set the username. Alternatively you can specify
		// the username in the WS deployment descriptor client_deploy.wsdd
		// if the username is known at deployment time and does not change
		// between calls, which is rarely the case in Aspen.
		call.setProperty(WSHandlerConstants.USER, "liferay.com.1");

		String wsTreeAsXML = (String) call.invoke(new Object[] {id, depth});

		FacadeClientHelper.printXML(wsTreeAsXML);

		System.out.println("***************************************************************");

		// This time, we are going to fetch only one level deep from the top
		// workspace. You can fetch the tree starting from different workspace
		// or folder by specifying its id as the first argument.

		wsTreeAsXML = (String) call.invoke(new Object[] {new Long(1), new Integer(1)});

		FacadeClientHelper.printXML(wsTreeAsXML);
	}
}