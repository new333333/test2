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
package com.sitescape.team.samples.remoting.client.ws.axis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.attachments.AttachmentPart;
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
				fetchAndPrintXML("getWorkspaceTreeAsXML", new Object[] {Long.parseLong(args[1]), Integer.parseInt(args[2]), (args.length > 3)?args[3]:""});
			} else if(args[0].equals("printPrincipal")) {
				fetchAndPrintXML("getPrincipalAsXML", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("printAllPrincipals")) {
				fetchAndPrintXML("getAllPrincipalsAsXML", new Object[] {Integer.parseInt(args[1]), Integer.parseInt(args[2])});
			} else if(args[0].equals("printFolderEntries")) {
				fetchAndPrintXML("getFolderEntriesAsXML", new Object[] {Long.parseLong(args[1])});
			} else if(args[0].equals("addFolder")) {
				fetchAndPrintIdentifier("addFolder", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3]});
			} else if(args[0].equals("printTeamMembers")) {
				fetchAndPrintXML("getTeamMembersAsXML", new Object[] {Long.parseLong(args[1])});
			} else if(args[0].equals("printTeams")) {
				fetchAndPrintXML("getTeamsAsXML", new Object[] {});
			} else if(args[0].equals("printFolderEntry")) {
				fetchAndPrintXML("getFolderEntryAsXML", new Object[] {Long.parseLong(args[1]),Long.parseLong(args[2]), Boolean.parseBoolean(args[3])});			
			} else if(args[0].equals("printDefinition")) {
				fetchAndPrintXML("getDefinitionAsXML", new Object[] {args[1]});
			} else if(args[0].equals("printDefinitionConfig")) {
				fetchAndPrintXML("getDefinitionConfigAsXML", new Object[0]);
			} else if(args[0].equals("addEntry")) {
				String s = FacadeClientHelper.readText(args[3]);
				System.out.println("XML: " + s);
				String filename = null;
				if(args.length > 3) {
					filename = args[4];
				}
				fetchAndPrintIdentifier("addFolderEntry", new Object[] {Long.parseLong(args[1]), args[2], s, filename}, filename);
			} else if(args[0].equals("modifyEntry")) {
				String s = FacadeClientHelper.readText(args[3]);
				System.out.println("XML: " + s);
				justDoIt("modifyFolderEntry", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), s});
			} else if(args[0].equals("uploadFile")) {
				justDoIt("uploadFolderFile", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]}, args[4]);
			} else if(args[0].equals("uploadFileStaged")) {
				justDoIt("uploadFolderFileStaged", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]});
			} else if(args[0].equals("uploadCalendar")) {
				String s = FacadeClientHelper.readText(args[2]);
				System.out.println("XML: " + s);
				String attachFile = null;
				if(args.length > 3) {
					attachFile = args[3];
				}
				justDoIt("uploadCalendarEntries", new Object[] {Long.parseLong(args[1]), s}, attachFile);
			} else if(args[0].equals("addWorkflow")) {
				justDoIt("addEntryWorkflow", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]});
			} else if(args[0].equals("search")) {
				String s = FacadeClientHelper.readText(args[1]);
				System.out.println("XML: " + s);
				fetchAndPrintXML("search", new Object[] {s, Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("getBinderTitle")) {
				fetchAndPrintString("getBinderTitle", new Object[] {Long.parseLong(args[1])});
			} else if(args[0].equals("listDefinitions")) {
				fetchAndPrintXML("getDefinitionListAsXML", new Object[] {});
			} else if(args[0].equals("setTeamMembers")) {
				String ids[] = args[2].split(",");
				List idsList = new ArrayList();
				for (int i=0; i<ids.length; ++i) {
					idsList.add(Long.parseLong((String)ids[i]));
				}
				justDoIt("setTeamMembers", new Object[] {Long.parseLong(args[1]), idsList});
			} else if(args[0].equals("setDefinitions")) {
				String ids[] = args[2].split(",");
				List idsList = new ArrayList();
				for (int i=0; i<ids.length; ++i) {
					idsList.add(ids[i]);
				}
				List wfs = new ArrayList();
				if (args.length > 3) {
					ids = args[3].split(","); //entryDef,workflowDef
					for (int i=0; i<ids.length; ++i) {
						wfs.add(ids[i]);
					}
				}
				justDoIt("setDefinitions", new Object[] {Long.parseLong(args[1]), idsList, wfs});
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
		call.setProperty(WSHandlerConstants.USER, "admin");
		
		if(filename != null) {
			DataHandler dhSource = new DataHandler(new FileDataSource(new File(filename)));
		
			call.addAttachmentPart(dhSource); //Add the file.
        
			call.setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
		}
		
		Object result = call.invoke(args);
		
		org.apache.axis.MessageContext messageContext = call.getMessageContext();
		org.apache.axis.Message returnedMessage = messageContext.getResponseMessage();
		System.out.println("Number of attachments is -> " +
			returnedMessage.countAttachments());
		Iterator iteAtta = returnedMessage.getAttachments();
		DataHandler[] dhTab = new DataHandler[returnedMessage.countAttachments()];
		for (int i=0;iteAtta.hasNext();i++) {
			AttachmentPart ap = (AttachmentPart) iteAtta.next();
			dhTab[i] = ap.getDataHandler();
			System.out.println("Filename=" + dhTab[i].getName());
		}
		return result;
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
		fetchAndPrintIdentifier(operation, args, null);
	}
	
	static void fetchAndPrintIdentifier(String operation, Object[] args, String filename) throws Exception {
		Long ident = (Long) fetch(operation, args, filename);

		System.out.println(ident);
	}

	static void fetchAndPrintString(String operation, Object[] args) throws Exception {
		fetchAndPrintString(operation, args, null);
	}
	
	static void fetchAndPrintString(String operation, Object[] args, String filename) throws Exception {
		String str = (String) fetch(operation, args, filename);

		System.out.println(str);
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("addFolder <parent binder id> <binder config id> <title>");
		System.out.println("printWorkspaceTree <workspace id> <depth> [<page>]");
		System.out.println("printPrincipal <binder id> <principal id>");
		System.out.println("printAllPrincipals <first> <max>");
		System.out.println("printFolderEntries <folder id>");
		System.out.println("search <xmlFilename> <offset> <maxResults>");
		System.out.println("printTeamMembers <binder id>");
		System.out.println("printTeams");
		System.out.println("setTeamMembers <team id> <command separated list of memberId>");
		System.out.println("printFolderEntry <folder id> <entry id> <includeAttachments>");
		System.out.println("printDefinition <definition id>");
		System.out.println("printDefinitionConfig");
		System.out.println("listDefinitions");
		System.out.println("addEntry <folder id> <definition id> <entryDataXMLString> [<attachmentFileName>]");
		System.out.println("modifyEntry <folder id> <entry id> <entryDataXMLString>");
		System.out.println("addWorkflow <folder id> <entry id> <definition id> <startState>");
		System.out.println("uploadFile <folder id> <entry id> <fileDataFieldName> <filename>");
		System.out.println("uploadFileStaged <folder id> <entry id> <fileDataFieldName> <stagedFileRelativePath>");
		System.out.println("uploadCalendarEntries <folder id> <xmlFilename> [<iCalFilename>]");
		System.out.println("-- The following is to be used only in conjunction with extendedws sample --");
		System.out.println("getBinderTitle <binder id>");
		System.out.println("setDefinitions <folder id> <comma separated definitionIds> comma separated definitionId,workflowId>");
	}
}