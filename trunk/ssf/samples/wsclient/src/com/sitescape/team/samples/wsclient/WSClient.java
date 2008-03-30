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
package com.sitescape.team.samples.wsclient;

import java.io.File;

/**
 * This WS client program uses JAX-RPC compliant client binding classes 
 * generated by Axis' WSDL2Java tool.
 * <p>
 * This program is written in most part to the standard JAX-RPC interface,
 * and in fact, all of the client binding classes generated by Axis and used
 * by this program adhere to JAX-RPC specification (that is, the classes are
 * generated in accordance with JAX-RPC specification). The only Axis-specific
 * parts of this program is 1) the use of org.apache.axis.client.Call class 
 * in setting a tool specific property on the stub object, and 2) the use of
 * addAttachment method on the stub which provides Axis specific way of
 * adding an attachment. This is due to lack of standard support for handling
 * attachments in the current version of JAX-RPC (1.1) that we're using. 
 * It should be fairly easy to rewrite this program to use another JAX-RPC 
 * compliant WS tool.
 * Note: It appears that we should be able to use the standard SAAJ to
 * deal with the file upload scenario. We haven't tried it yet.
 * Note: We placed this class in .ws.jaxrpc package rather than .ws.axis
 * since it is in most part tool neutral except where explained above. 
 * 
 * @author jong
 *
 */
public class WSClient extends WSClientBase
{
	public static void main(String[] args) {
		if(args.length == 0) {
			printUsage();
			return;
		}

		WSClient wsClient = new WSClient();
		
		try {
			if(args[0].equals("printWorkspaceTree")) {
				wsClient.fetchAndPrintXML("SearchService", "getWorkspaceTreeAsXML", new Object[] {null, Long.parseLong(args[1]), Integer.parseInt(args[2]), (args.length > 3)?args[3]:""});
			} else if(args[0].equals("printPrincipal")) {
				wsClient.fetchAndPrintXML("ProfileService", "getPrincipalAsXML", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("printAllPrincipals")) {
				wsClient.fetchAndPrintXML("ProfileService", "getAllPrincipalsAsXML", new Object[] {null, Integer.parseInt(args[1]), Integer.parseInt(args[2])});
			} else if(args[0].equals("printFolderEntries")) {
				wsClient.fetchAndPrintXML("FolderService", "getFolderEntriesAsXML", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("addFolder")) {
				wsClient.fetchAndPrintIdentifier("TemplateService", "addBinder", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3]});
			} else if(args[0].equals("printTeamMembers")) {
				wsClient.fetchAndPrintXML("SearchService", "getTeamMembersAsXML", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("printTeams")) {
				wsClient.fetchAndPrintXML("SearchService", "getTeamsAsXML", new Object[] {null});
			} else if(args[0].equals("printFolderEntry")) {
				wsClient.fetchAndPrintXML("FolderService", "getFolderEntryAsXML", new Object[] {null, Long.parseLong(args[1]),Long.parseLong(args[2]), Boolean.parseBoolean(args[3])});			
			} else if(args[0].equals("printDefinition")) {
				wsClient.fetchAndPrintXML("DefinitionService", "getDefinitionAsXML", new Object[] {null, args[1]});
			} else if(args[0].equals("printDefinitionConfig")) {
				wsClient.fetchAndPrintXML("DefinitionService", "getDefinitionConfigAsXML", new Object[] {null});
			} else if(args[0].equals("addEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				String filename = null;
				if(args.length > 4) {
					filename = args[4];
				}
				wsClient.fetchAndPrintIdentifier("FolderService", "addFolderEntry", new Object[] {null, Long.parseLong(args[1]), args[2], s, filename}, filename);
			} else if(args[0].equals("modifyEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				wsClient.justDoIt("FolderService", "modifyFolderEntry", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), s});
			} else if(args[0].equals("uploadFile")) {
				wsClient.justDoIt("FolderService", "uploadFolderFile", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]}, args[4]);
			} else if(args[0].equals("uploadCalendar")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				String attachFile = null;
				if(args.length > 3) {
					attachFile = args[3];
				}
				wsClient.justDoIt("IcalService", "uploadCalendarEntries", new Object[] {null, Long.parseLong(args[1]), s}, attachFile);
			} else if(args[0].equals("search")) {
				String s = readText(args[1]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintXML("SearchService", "search", new Object[] {null, s, Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("addUserToGroup")) {
				wsClient.justDoIt("ProfileService", "addUserToGroup", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("getFolderTitle")) {
				wsClient.fetchAndPrintString("FolderService", "getFolderTitle", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("addZoneUnderPortal")) {
				String mailDomain = null;
				if(args.length > 3)
					mailDomain = args[3];
				wsClient.justDoIt("ZoneService", "addZoneUnderPortal", new Object[] {null, args[1], args[2], mailDomain});
			} else if(args[0].equals("modifyZoneUnderPortal")) {
				String mailDomain = null;
				if(args.length > 3)
					mailDomain = args[3];
				wsClient.justDoIt("ZoneService", "modifyZoneUnderPortal", new Object[] {null, args[1], args[2], mailDomain});
			} else if(args[0].equals("deleteZoneUnderPortal")) {
				wsClient.justDoIt("ZoneService", "deleteZoneUnderPortal", new Object[] {null, args[1]});
			} else if(args[0].equals("getHotContent")) {
				Long binderId = null;
				if(args.length > 2) {
					binderId = Long.valueOf(args[2]); 
				}
				wsClient.fetchAndPrintXML("SearchService", "getHotContent", new Object[] {null, args[1], binderId});
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
	
	Object fetch(String serviceName, String operation, Object[] args) throws Exception {
		return fetch(serviceName, operation, args, null);
	}

	void fetchAndPrintXML(String serviceName, String operation, Object[] args) throws Exception {
		String wsTreeAsXML = (String) fetch(serviceName, operation, args);

		printXML(wsTreeAsXML);
	}

	void justDoIt(String serviceName, String operation, Object[] args) throws Exception {
		justDoIt(serviceName, operation, args, null);
	}
	
	void justDoIt(String serviceName, String operation, Object[] args, String filename) throws Exception {
		fetch(serviceName, operation, args, filename);
	}

	void fetchAndPrintIdentifier(String serviceName, String operation, Object[] args) throws Exception {
		fetchAndPrintIdentifier(serviceName, operation, args, null);
	}
	
	void fetchAndPrintIdentifier(String serviceName, String operation, Object[] args, String filename) throws Exception {
		Long ident = (Long) fetch(serviceName, operation, args, filename);

		System.out.println(ident);
	}

	void fetchAndPrintString(String serviceName, String operation, Object[] args) throws Exception {
		fetchAndPrintString(serviceName, operation, args, null);
	}
	
	void fetchAndPrintString(String serviceName, String operation, Object[] args, String filename) throws Exception {
		String str = (String) fetch(serviceName, operation, args, filename);

		System.out.println(str);
	}

	Object fetch(String serviceName, String operation, Object[] args, String filename) throws Exception {
		return invokeWithCall(serviceName, operation, args, ((filename != null)? new File(filename) : null), null);
	}
	
	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("printWorkspaceTree <workspace id> <depth> [<page>]");
		System.out.println("printPrincipal <binder id> <principal id>");
		System.out.println("printAllPrincipals <first> <max>");
		System.out.println("printFolderEntries <folder id>");
		System.out.println("addFolder <parent binder id> <binder config id> <title>");
		System.out.println("printTeamMembers <binder id>");
		System.out.println("printTeams");
		System.out.println("printFolderEntry <folder id> <entry id> <includeAttachments>");
		System.out.println("printDefinition <definition id>");
		System.out.println("printDefinitionConfig");
		System.out.println("addEntry <folder id> <definition id> <entryDataXMLString> [<attachmentFileName>]");
		System.out.println("modifyEntry <folder id> <entry id> <entryDataXMLString>");
		System.out.println("uploadFile <folder id> <entry id> <fileDataFieldName> <filename>");
		System.out.println("uploadCalendar <folder id> <xmlFilename> [<iCalFilename>]");
		System.out.println("search <xmlFilename> <offset> <maxResults>");
		System.out.println("addUserToGroup <user id> <group id>");
		System.out.println("-- The following is to be used only in conjunction with extendedws sample --");
		System.out.println("getFolderTitle <folder id>");
		System.out.println("-- The following is to be used only with ICEcore Enterprise server with appropriate license --");
		System.out.println("addZoneUnderPortal <zone name> <virtual host> [<mail domain>]");
		System.out.println("modifyZoneUnderPortal <zone name> <virtual host> [<mail domain>]");
		System.out.println("deleteZoneUnderPortal <zone name>");
		System.out.println("getHotContent <limitType> <binder id>");
		
		// an example of addZoneUnderPortal invocation - 
		// addZoneUnderPortal fake-bestbuy www.fake-bestbuy.com mail.fake-bestbuy.com
		// addZoneUnderPortal fake-bestbuy www.fake-bestbuy.com
		
		// an example of modifyZoneUnderPortal invocation - 
		// modifyZoneUnderPortal fake-bestbuy www.fake-bestbuy.com mail.fake-bestbuy.com
		// modifyZoneUnderPortal fake-bestbuy www.fake-bestbuy.com
		
		// an example of deleteZoneUnderPortal invocation - 
		// deleteZoneUnderPortal fake-bestbuy
	}

}