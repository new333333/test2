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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TeamingServiceClientWithCall extends WSClientBase
{
	public static void main(String[] args) {
		if(args.length == 0) {
			printUsage();
			return;
		}

		TeamingServiceClientWithCall wsClient = new TeamingServiceClientWithCall();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		try {
			if(args[0].equals("getWorkspaceTree")) {
				wsClient.fetchAndPrintXML("TeamingService", "search_getWorkspaceTreeAsXML", new Object[] {null, Long.parseLong(args[1]), Integer.parseInt(args[2]), (args.length > 3)?args[3]:""});
			} else if(args[0].equals("getUser")) {
				wsClient.fetchAndPrintDE("TeamingService", "profile_getUser", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("getGroup")) {
				wsClient.fetchAndPrintDE("TeamingService", "profile_getGroup", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("getPrincipals")) {
				wsClient.fetch("TeamingService", "profile_getPrincipals", new Object[] {null, Integer.parseInt(args[1]), Integer.parseInt(args[2])});
			} else if(args[0].equals("getFolderEntries")) {
				wsClient.fetchAndPrintACK("TeamingService", "folder_getEntries", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("getTeamMembers")) {
				wsClient.fetchAndPrintACK("TeamingService", "binder_getTeamMembers", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("getTeams")) {
				wsClient.fetchAndPrintACK("TeamingService", "search_getTeams", new Object[] {null});
			} else if(args[0].equals("getFolderEntry")) {
				wsClient.fetchAndPrintDE("TeamingService", "folder_getEntry", new Object[] {null, Long.parseLong(args[1]),Long.parseLong(args[2]), Boolean.parseBoolean(args[3])});			
			} else if(args[0].equals("getDefinition")) {
				wsClient.fetchAndPrintXML("TeamingService", "definition_getDefinitionAsXML", new Object[] {null, args[1]});
			} else if(args[0].equals("addWorkflow")) {
				wsClient.fetchAndPrintACK("TeamingService", "folder_addEntryWorkflow", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3]});
			} else if(args[0].equals("modifyWorkflow")) {
				wsClient.fetchAndPrintACK("TeamingService", "folder_modifyWorkflowState", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), Long.parseLong(args[3]), args[4]});
			} else if(args[0].equals("uploadFile")) {
				wsClient.fetchAndPrintACK("TeamingService", "folder_uploadFile", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]}, args[4]);
			} else if(args[0].equals("uploadFileStaged")) {
				wsClient.fetchAndPrintACK("TeamingService", "folder_uploadFileStaged", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5]});
			} else if(args[0].equals("uploadCalendar")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				String attachFile = null;
				if(args.length > 3) {
					attachFile = args[3];
				}
				wsClient.fetchAndPrintACK("TeamingService", "ical_uploadCalendarEntriesWithXML", new Object[] {null, Long.parseLong(args[1]), s}, attachFile);
			} else if(args[0].equals("search")) {
				String s = readText(args[1]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintXML("TeamingService", "search_search", new Object[] {null, s, Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("addUserToGroup")) {
				wsClient.fetchAndPrintACK("TeamingService", "profile_addUserToGroup", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("getFolderTitle")) {
				wsClient.fetchAndPrintString("TeamingService", "folder_getFolderTitle", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("addZone")) {
				String mailDomain = null;
				if(args.length > 3)
					mailDomain = args[3];
				wsClient.fetchAndPrintIdentifier("TeamingService", "zone_addZone", new Object[] {null, args[1], args[2], mailDomain});
			} else if(args[0].equals("modifyZone")) {
				String mailDomain = null;
				if(args.length > 3)
					mailDomain = args[3];
				wsClient.fetchAndPrintACK("TeamingService", "zone_modifyZone", new Object[] {null, args[1], args[2], mailDomain});
			} else if(args[0].equals("deleteZone")) {
				wsClient.fetchAndPrintACK("TeamingService", "zone_deleteZone", new Object[] {null, args[1]});
			} else if(args[0].equals("getHotContent")) {
				Long binderId = null;
				if(args.length > 2) {
					binderId = Long.valueOf(args[2]); 
				}
				wsClient.fetchAndPrintXML("TeamingService", "search_getHotContent", new Object[] {null, args[1], binderId});
			} else if(args[0].equals("getDefinitions")) {
				wsClient.fetchAndPrintACK("TeamingService", "definition_getDefinitions", new Object[] {null});
			} else if(args[0].equals("getTemplates")) {
				wsClient.fetchAndPrintACK("TeamingService", "template_getTemplates", new Object[] {null});
			} else if(args[0].equals("setTeamMembers")) {
				String names[] = args[2].split(",");
				wsClient.fetchAndPrintACK("TeamingService", "binder_setTeamMembers", new Object[] {null, Long.parseLong(args[1]), names});
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
					wsClient.fetchAndPrintACK("TeamingService", "binder_setDefinitions", new Object[] {null, Long.parseLong(args[1]), idsList, wfs});
			} else if(args[0].equals("setFunctionMembership")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintACK("TeamingService", "binder_setFunctionMembership", new Object[] {null, Long.parseLong(args[1]), s});
			} else if(args[0].equals("setFunctionMembershipInherited")) {
				wsClient.fetchAndPrintACK("TeamingService", "binder_setFunctionMembershipInherited", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("setOwner")) {
				wsClient.fetchAndPrintACK("TeamingService", "binder_setOwner", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("indexBinder")) {
				wsClient.fetchAndPrintACK("TeamingService", "binder_indexBinder", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("synchronize")) {
				wsClient.fetchAndPrintACK("TeamingService", "folder_synchronizeMirroredFolder", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("migrateEntryWithXML")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[5]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[7]));
				
				wsClient.fetchAndPrintIdentifier("TeamingService", "migration_addFolderEntryWithXML", new Object[] {null, Long.parseLong(args[1]), args[2], s, args[4], c1,
						args[6], c2});
			} else if(args[0].equals("migrateReplyWithXML")) {
				String s = readText(args[4]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[8]));
				
				wsClient.fetchAndPrintIdentifier("TeamingService", "migration_addReplyWithXML", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], s, args[5], c1,
						args[7], c2});
			} else if(args[0].equals("migrateWorkflow")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				wsClient.fetchAndPrintACK("TeamingService", "migration_addEntryWorkflow", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], c1});
			} else if(args[0].equals("migrateBinderWithXML")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[5]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[7]));
				wsClient.fetchAndPrintIdentifier("TeamingService", "migration_addBinderWithXML", new Object[] {null, Long.parseLong(args[1]), args[2], s, args[4], c1, args[6], c2});
			} else if(args[0].equals("migrateFile")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				wsClient.fetchAndPrintACK("TeamingService", "migration_uploadFolderFile", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], c1}, args[4]);
			} else if(args[0].equals("migrateFileStaged")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[7]));
				wsClient.fetchAndPrintACK("TeamingService", "migration_uploadFolderFileStaged", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], args[6], c1}, null);
			} else if(args[0].equals("getFolders")) {
				wsClient.fetchAndPrintACK("TeamingService", "binder_getFolders", new Object[] {null, Long.parseLong(args[1])});
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
	
	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("getWorkspaceTree <workspace id> <depth> [<page>]");
		System.out.println("getUser <binder id> <user id>");
		System.out.println("getGroup <binder id> <group id>");
		System.out.println("getPrincipals <first> <max>");
		System.out.println("getFolderEntries <folder id>");
		System.out.println("getTeamMembers <binder id>");
		System.out.println("getTeams");
		System.out.println("getFolderEntry <folder id> <entry id> <includeAttachments>");
		System.out.println("getDefinition <definition id>");
		System.out.println("getDefinitions");
		System.out.println("getTemplates");
		System.out.println("setDefinitions <binder id> <comma separated definitionIds> <comma separated definitionId,workflowId>");
		System.out.println("setTeamMembers <binder id> <comma separated names>");
		System.out.println("addWorkflow <folder id> <entry id> <definition id>");
		System.out.println("modifyWorkflow <folder id> <entry id> <state id> <toState");
		System.out.println("uploadFile <folder id> <entry id> <fileDataFieldName> <filename>");
		System.out.println("uploadFileStaged <folder id> <entry id> <fileDataFieldName> <fileName> <stagedFileRelativePath>");
		System.out.println("uploadCalendar <folder id> <xmlFilename> [<iCalFilename>]");
		System.out.println("search <xmlFilename> <offset> <maxResults>");
		System.out.println("addUserToGroup <user id> <group id>");
		System.out.println("-- BEGIN: The following is to be used only in conjunction with extendedws sample --");
		System.out.println("getFolderTitle <folder id>");
		System.out.println("-- END:");
		System.out.println("-- BEGIN: The following is to be used only with ICEcore Enterprise server with appropriate license --");
		System.out.println("addZone <zone name> <virtual host> [<mail domain>]");
		System.out.println("modifyZone <zone name> <virtual host> [<mail domain>]");
		System.out.println("deleteZone <zone name>");
		System.out.println("-- END:");
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
		System.out.println("getFolders <binder id>");
		
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
