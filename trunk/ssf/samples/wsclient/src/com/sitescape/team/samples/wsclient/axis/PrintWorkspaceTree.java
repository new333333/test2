/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.samples.wsclient.axis;

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

import com.sitescape.team.samples.wsclient.util.FacadeClientHelper;
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
		// the username in the WS deployment descriptor client_deploy.wsdd.
		call.setProperty(WSHandlerConstants.USER, "admin");
		
		// Programmatically set the password type and callback class. The type
		// should be either PW_TEXT or PW_DIGEST. Alternatively the same can
		// be specified in the WS deployment descriptor client_deploy.wsdd.
		//call.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
		//call.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, 
		//		"com.sitescape.team.samples.remoting.client.ws.security.PWCallbackText");
		//call.setProperty(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
		//call.setProperty(WSHandlerConstants.PW_CALLBACK_CLASS, 
		//		"com.sitescape.team.samples.remoting.client.ws.security.PWCallbackDigest");

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