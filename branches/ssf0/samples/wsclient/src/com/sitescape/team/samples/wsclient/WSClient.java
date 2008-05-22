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
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.DateFormat;

/**
 * This WS client program uses Apache Axis to invoke the Teaming web services. 
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
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		try {
			if(args[0].equals("printWorkspaceTree")) {
				wsClient.fetchAndPrintXML("TeamingService", "search_getWorkspaceTreeAsXML", new Object[] {null, Long.parseLong(args[1]), Integer.parseInt(args[2]), (args.length > 3)?args[3]:""});
			} else if(args[0].equals("printPrincipal")) {
				wsClient.fetchAndPrintXML("TeamingService", "profile_getPrincipalAsXML", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("printPrincipals")) {
				wsClient.fetchAndPrintXML("TeamingService", "profile_getPrincipalsAsXML", new Object[] {null, Integer.parseInt(args[1]), Integer.parseInt(args[2])});
			} else if(args[0].equals("printFolderEntries")) {
				wsClient.fetchAndPrintXML("TeamingService", "folder_getFolderEntriesAsXML", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("addFolder")) {
				wsClient.fetchAndPrintIdentifier("TeamingService", "template_addBinder", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3]});
			} else if(args[0].equals("printTeamMembers")) {
				wsClient.fetchAndPrintXML("TeamingService", "binder_getTeamMembersAsXML", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("printTeams")) {
				wsClient.fetchAndPrintXML("TeamingService", "search_getTeamsAsXML", new Object[] {null});
			} else if(args[0].equals("printFolderEntry")) {
				wsClient.fetchAndPrintXML("TeamingService", "folder_getFolderEntryAsXML", new Object[] {null, Long.parseLong(args[1]),Long.parseLong(args[2]), Boolean.parseBoolean(args[3])});			
			} else if(args[0].equals("printDefinition")) {
				wsClient.fetchAndPrintXML("TeamingService", "definition_getDefinitionAsXML", new Object[] {null, args[1]});
			} else if(args[0].equals("printDefinitionConfig")) {
				wsClient.fetchAndPrintXML("TeamingService", "definition_getDefinitionConfigAsXML", new Object[] {null});
			} else if(args[0].equals("addEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				String filename = null;
				if(args.length > 4) {
					filename = args[4];
				}
				wsClient.fetchAndPrintIdentifier("TeamingService", "folder_addFolderEntry", new Object[] {null, Long.parseLong(args[1]), args[2], s, filename}, filename);
			} else if(args[0].equals("addWorkflow")) {
				wsClient.justDoIt("TeamingService", "folder_addEntryWorkflow", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3]});
			} else if(args[0].equals("modifyWorkflow")) {
				wsClient.justDoIt("TeamingService", "folder_modifyWorkflowState", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), Long.parseLong(args[3]), args[4]});
			} else if(args[0].equals("addReply")) {
				String s = readText(args[4]);
				System.out.println("XML: " + s);
				
				wsClient.fetchAndPrintIdentifier("TeamingService", "folder_addReply", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], s});
			} else if(args[0].equals("modifyEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				wsClient.justDoIt("TeamingService", "folder_modifyFolderEntry", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), s});
			} else if(args[0].equals("uploadFile")) {
				wsClient.justDoIt("TeamingService", "folder_uploadFolderFile", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]}, args[4]);
			} else if(args[0].equals("uploadFileStaged")) {
				wsClient.justDoIt("TeamingService", "folder_uploadFolderFileStaged", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5]});
			} else if(args[0].equals("uploadCalendar")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				String attachFile = null;
				if(args.length > 3) {
					attachFile = args[3];
				}
				wsClient.justDoIt("TeamingService", "ical_uploadCalendarEntries", new Object[] {null, Long.parseLong(args[1]), s}, attachFile);
			} else if(args[0].equals("search")) {
				String s = readText(args[1]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintXML("TeamingService", "search_search", new Object[] {null, s, Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("addUserToGroup")) {
				wsClient.justDoIt("TeamingService", "profile_addUserToGroup", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("getFolderTitle")) {
				wsClient.fetchAndPrintString("TeamingService", "folder_getFolderTitle", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("addZoneUnderPortal")) {
				String mailDomain = null;
				if(args.length > 3)
					mailDomain = args[3];
				wsClient.justDoIt("TeamingService", "zone_addZoneUnderPortal", new Object[] {null, args[1], args[2], mailDomain});
			} else if(args[0].equals("modifyZoneUnderPortal")) {
				String mailDomain = null;
				if(args.length > 3)
					mailDomain = args[3];
				wsClient.justDoIt("TeamingService", "zone_modifyZoneUnderPortal", new Object[] {null, args[1], args[2], mailDomain});
			} else if(args[0].equals("deleteZoneUnderPortal")) {
				wsClient.justDoIt("TeamingService", "zone_deleteZoneUnderPortal", new Object[] {null, args[1]});
			} else if(args[0].equals("getHotContent")) {
				Long binderId = null;
				if(args.length > 2) {
					binderId = Long.valueOf(args[2]); 
				}
				wsClient.fetchAndPrintXML("TeamingService", "search_getHotContent", new Object[] {null, args[1], binderId});
			} else if(args[0].equals("listDefinitions")) {
				wsClient.fetchAndPrintXML("TeamingService", "definition_getDefinitionsAsXML", new Object[] {null});
			} else if(args[0].equals("listTemplates")) {
				wsClient.fetchAndPrintXML("TeamingService", "template_getTemplatesAsXML", new Object[] {null});
			} else if(args[0].equals("setTeamMembers")) {
				String names[] = args[2].split(",");
				wsClient.justDoIt("TeamingService", "binder_setTeamMembers", new Object[] {null, Long.parseLong(args[1]), names});
			} else if(args[0].equals("setDefinitions")) {
				String ids[] = args[2].split(",");
				List idsList = new ArrayList();
				for (int i=0; i<ids.length; ++i) {
					idsList.add(ids[i]);
				}
				List wfs = new ArrayList();
				if (args.length > 3) {
					ids = args[3].split(","); //entryDef,workflowDef
					for (int i=0; i+1<ids.length; i+=2) {
						wfs.add(ids[i] + "," + ids[i+1]);
					}
				}
					wsClient.justDoIt("TeamingService", "binder_setDefinitions", new Object[] {null, Long.parseLong(args[1]), idsList, wfs});
			} else if(args[0].equals("setFunctionMembership")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				wsClient.justDoIt("TeamingService", "binder_setFunctionMembership", new Object[] {null, Long.parseLong(args[1]), s});
			} else if(args[0].equals("setFunctionMembershipInherited")) {
				wsClient.justDoIt("TeamingService", "binder_setFunctionMembershipInherited", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("setOwner")) {
				wsClient.justDoIt("TeamingService", "binder_setOwner", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("indexBinder")) {
				wsClient.justDoIt("TeamingService", "binder_indexBinder", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("synchronize")) {
				wsClient.justDoIt("TeamingService", "folder_synchronizeMirroredFolder", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("migrateEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[5]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[7]));
				
				wsClient.fetchAndPrintIdentifier("TeamingService", "migration_addFolderEntry", new Object[] {null, Long.parseLong(args[1]), args[2], s, args[4], c1,
						args[6], c2});
			} else if(args[0].equals("migrateReply")) {
				String s = readText(args[4]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[8]));
				
				wsClient.fetchAndPrintIdentifier("TeamingService", "migration_addReply", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], s, args[5], c1,
						args[7], c2});
			} else if(args[0].equals("migrateWorkflow")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				wsClient.justDoIt("TeamingService", "migration_addEntryWorkflow", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], c1});
			} else if(args[0].equals("migrateBinder")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[5]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[7]));
				wsClient.fetchAndPrintIdentifier("TeamingService", "migration_addBinder", new Object[] {null, Long.parseLong(args[1]), args[2], s, args[4], c1, args[6], c2});
			} else if(args[0].equals("migrateFile")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				wsClient.justDoIt("TeamingService", "migration_uploadFolderFile", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], c1}, args[4]);
			} else if(args[0].equals("migrateFileStaged")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[7]));
				wsClient.justDoIt("TeamingService", "migration_uploadFolderFileStaged", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], args[6], c1}, null);
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
		System.out.println("printPrincipals <first> <max>");
		System.out.println("printFolderEntries <folder id>");
		System.out.println("addFolder <parent binder id> <binder config id> <title>");
		System.out.println("printTeamMembers <binder id>");
		System.out.println("printTeams");
		System.out.println("printFolderEntry <folder id> <entry id> <includeAttachments>");
		System.out.println("printDefinition <definition id>");
		System.out.println("printDefinitionConfig");
		System.out.println("listDefinitions");
		System.out.println("listTemplates");
		System.out.println("setDefinitions <binder id> <comma separated definitionIds> <comma separated definitionId,workflowId>");
		System.out.println("setTeamMembers <binder id> <comma separated names>");
		System.out.println("addEntry <folder id> <definition id> <entryDataXMLString> [<attachmentFileName>]");
		System.out.println("addReply <folder id> <entry id> <definition id> <entryDataXMLString>");
		System.out.println("addWorkflow <folder id> <entry id> <definition id>");
		System.out.println("modifyWorkflow <folder id> <entry id> <state id> <toState");
		System.out.println("modifyEntry <folder id> <entry id> <entryDataXMLString>");
		System.out.println("uploadFile <folder id> <entry id> <fileDataFieldName> <filename>");
		System.out.println("uploadFileStaged <folder id> <entry id> <fileDataFieldName> <fileName> <stagedFileRelativePath>");
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
		System.out.println("setFunctionMembership <binderId> <functionDataXml>");
		System.out.println("setFunctionMembershipInherited <binderId> <boolean>");
		System.out.println("setOwner <binderId> <userId>");
		System.out.println("indexBinder <folder id>");
		System.out.println("migrateBinder <parentBinder id> <definition id> <entryDataXMLString> <creator> <createDate> <modifier> <modDate> ");
		System.out.println("migrateEntry <folder id> <definition id> <entryDataXMLString> <creator> <createDate> <modifier> <modDate> ");
		System.out.println("migrateReply <folder id> <entry id> <definition id> <entryDataXMLString> <creator> <createDate> <modifier> <modDate> ");
		System.out.println("migrateWorkflow <folder id> <entry id> <definition id> <startState> <modifier> <modDate>");
		System.out.println("migrateFile <folder id> <entry id> <fileDataFieldName> <filename> <modifier> <modDate>");
		System.out.println("migrateFileStaged <folder id> <entry id> <fileDataFieldName> <fileName> <stagedFileRelativePath> <modifier> <modDate>");
		System.out.println("synchronize <mirrored folder id>");
		
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