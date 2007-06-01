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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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
			} else if(args[0].equals("printFolderEntries")) {
				fetchAndPrintXML("getFolderEntriesAsXML", new Object[] {Long.parseLong(args[1])});			
			} else if(args[0].equals("printFolderEntry")) {
				fetchAndPrintXML("getFolderEntryAsXML", new Object[] {Long.parseLong(args[1]),Long.parseLong(args[2])});			
			} else if(args[0].equals("printDefinition")) {
				fetchAndPrintXML("getDefinitionAsXML", new Object[] {args[1]});			
			} else if(args[0].equals("printDefinitionConfig")) {
				fetchAndPrintXML("getDefinitionConfigAsXML", new Object[0]);			
			} else if(args[0].equals("addEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				fetchAndPrintIdentifier("addFolderEntry", new Object[] {Long.parseLong(args[1]), args[2], s});			
			} else if(args[0].equals("modifyEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				justDoIt("modifyFolderEntry", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), s});			
			} else if(args[0].equals("uploadFile")) {
				justDoIt("uploadFolderFile", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]}, args[4]);			
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
	
	static String readText(String filename)
	{
		StringBuffer buf = new StringBuffer();
		try {
			FileReader f = new FileReader(filename);
			char in[] = new char[32768];
			int len;
			while((len = f.read(in, 0, 32767)) > 0) {
				buf.append(in, 0, len);
			}
		} catch(IOException e) {
			System.err.println("Error reading file " + filename + ": " + e);
		}
		return buf.toString();
	}

	static Object fetch(String operation, Object[] args) throws Exception {
		return fetch(operation, args, null);
	}
	
	static Object fetch(String operation, Object[] args, String filename) throws Exception {
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
		
		if(filename != null) {
			DataHandler dhSource = new DataHandler(new FileDataSource(new File(filename)));
		
			call.addAttachmentPart(dhSource); //Add the file.
        
			call.setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
		}
		
		return call.invoke(args);
	}

	static void fetchAndPrintXML(String operation, Object[] args) throws Exception {
		String wsTreeAsXML = (String) fetch(operation, args);

		FacadeClientHelper.printXML(wsTreeAsXML);
	}

	static void justDoIt(String operation, Object[] args) throws Exception {
		justDoIt(operation, args, null);
	}
	
	static void justDoIt(String operation, Object[] args, String filename) throws Exception {
		fetch(operation, args, filename);
	}


	static void fetchAndPrintIdentifier(String operation, Object[] args) throws Exception {
		Long ident = (Long) fetch(operation, args);

		System.out.println(ident);
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("printWorkspaceTree <workspace id> <depth>");
		System.out.println("printPrincipal <binder id> <principal id>");
		System.out.println("printFolderEntries <folder id>");
		System.out.println("printFolderEntry <folder id> <entry id>");
		System.out.println("printDefinition <definition id>");
		System.out.println("printDefinitionConfig");
		System.out.println("addEntry <folder id> <definition id> <entryDataXMLString>");
		System.out.println("modifyEntry <folder id> <entry id> <entryDataXMLString>");
		System.out.println("uploadFile <folder id> <entry id> <fileDataFieldName> <filename>");
	}
}