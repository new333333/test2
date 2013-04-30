/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.samples.wsclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.DateFormat;

import org.apache.axis.AxisFault;

/**
 * This WS client program uses Apache Axis to invoke the Teaming web services. 
 * 
 * @author jong
 *
 */
public class FacadeClient extends WSClientBase
{
	public static void main(String[] args) {
		if(args.length == 0) {
			printUsage();
			return;
		}

		FacadeClient wsClient = new FacadeClient();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

		try {
			if(args[0].equals("printWorkspaceTree")) {
				wsClient.fetchAndPrintXML("Facade", "getWorkspaceTreeAsXML", new Object[] {Long.parseLong(args[1]), Integer.parseInt(args[2]), (args.length > 3)?args[3]:""});
			} else if(args[0].equals("printPrincipal")) {
				wsClient.fetchAndPrintXML("Facade", "getPrincipalAsXML", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("printPrincipals")) {
				wsClient.fetchAndPrintXML("Facade", "getAllPrincipalsAsXML", new Object[] {Integer.parseInt(args[1]), Integer.parseInt(args[2])});
			} else if(args[0].equals("printFolderEntries")) {
				wsClient.fetchAndPrintXML("Facade", "getFolderEntriesAsXML", new Object[] {Long.parseLong(args[1])});
			} else if(args[0].equals("addFolder")) {
				wsClient.fetchAndPrintIdentifier("Facade", "addFolder", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3]});
			} else if(args[0].equals("printTeamMembers")) {
				wsClient.fetchAndPrintXML("Facade", "getTeamMembersAsXML", new Object[] {Long.parseLong(args[1])});
			} else if(args[0].equals("printTeams")) {
				wsClient.fetchAndPrintXML("Facade", "getTeamsAsXML", new Object[] {});
			} else if(args[0].equals("printFolderEntry")) {
				wsClient.fetchAndPrintXML("Facade", "getFolderEntryAsXML", new Object[] {Long.parseLong(args[1]),Long.parseLong(args[2]), Boolean.parseBoolean(args[3])});			
			} else if(args[0].equals("printDefinition")) {
				wsClient.fetchAndPrintXML("Facade", "getDefinitionAsXML", new Object[] {args[1]});
			} else if(args[0].equals("printDefinitionConfig")) {
				wsClient.fetchAndPrintXML("Facade", "getDefinitionConfigAsXML", new Object[] {});
			} else if(args[0].equals("listDefinitions")) {
				wsClient.fetchAndPrintXML("Facade", "getDefinitionListAsXML", new Object[] {});
			} else if(args[0].equals("addEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				String filename = null;
				if(args.length > 4) {
					filename = args[4];
				}
				wsClient.fetchAndPrintIdentifier("Facade", "addFolderEntry", new Object[] {Long.parseLong(args[1]), args[2], s, filename}, filename);
			} else if(args[0].equals("addReply")) {
				String s = readText(args[4]);
				System.out.println("XML: " + s);
				
				wsClient.fetchAndPrintIdentifier("Facade", "addReply", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], s});
			} else if(args[0].equals("modifyEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintACK("Facade", "modifyFolderEntry", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), s});
			} else if(args[0].equals("uploadFile")) {
				wsClient.fetchAndPrintACK("Facade", "uploadFolderFile", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4]}, args[4]);
			} else if(args[0].equals("uploadCalendar")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				String attachFile = null;
				if(args.length > 3) {
					attachFile = args[3];
				}
				wsClient.fetchAndPrintACK("Facade", "uploadCalendarEntries", new Object[] {Long.parseLong(args[1]), s}, attachFile);
			} else if(args[0].equals("search")) {
				String s = readText(args[1]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintXML("Facade", "search", new Object[] {s, Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("getFolderTitle")) {
				wsClient.fetchAndPrintString("Facade", "getFolderTitle", new Object[] {Long.parseLong(args[1])});
			} else if(args[0].equals("setTeamMembers")) {
				String names[] = args[2].split(",");
				wsClient.fetchAndPrintACK("Facade", "setTeamMembers", new Object[] {Long.parseLong(args[1]), names});
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
					wsClient.fetchAndPrintACK("Facade", "setDefinitions", new Object[] {Long.parseLong(args[1]), idsList, wfs});
			} else if(args[0].equals("setFunctionMembership")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintACK("Facade", "setFunctionMembership", new Object[] {Long.parseLong(args[1]), s});
			} else if(args[0].equals("setFunctionMembershipInherited")) {
				wsClient.fetchAndPrintACK("Facade", "setFunctionMembershipInherited", new Object[] {Long.parseLong(args[1]), Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("setOwner")) {
				wsClient.fetchAndPrintACK("Facade", "setOwner", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("indexBinder")) {
				wsClient.fetchAndPrintACK("Facade", "indexFolder", new Object[] {Long.parseLong(args[1])});
			} else if(args[0].equals("synchronize")) {
				wsClient.fetchAndPrintACK("Facade", "synchronizeMirroredFolder", new Object[] {Long.parseLong(args[1])});
			} else if(args[0].equals("migrateEntry")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[5]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[7]));
				
				wsClient.fetchAndPrintIdentifier("Facade", "migrateFolderEntry", new Object[] {Long.parseLong(args[1]), args[2], s, args[4], c1,
						args[6], c2});
			} else if(args[0].equals("migrateReply")) {
				String s = readText(args[4]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[8]));
				
				wsClient.fetchAndPrintIdentifier("Facade", "migrateReply", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], s, args[5], c1,
						args[7], c2});
			} else if(args[0].equals("migrateWorkflow")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				wsClient.fetchAndPrintACK("Facade", "migrateEntryWorkflow", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], c1});
			} else if(args[0].equals("migrateBinder")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[5]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[7]));
				wsClient.fetchAndPrintIdentifier("Facade", "migrateBinder", new Object[] {Long.parseLong(args[1]), args[2], s, args[4], c1, args[6], c2});
			} else if(args[0].equals("migrateFile")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				wsClient.fetchAndPrintACK("Facade", "migrateFolderFile", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], c1}, args[4]);
			} else if(args[0].equals("migrateFileStaged")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[7]));
				wsClient.fetchAndPrintACK("Facade", "migrateFolderFileStaged", new Object[] {Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], args[6], c1}, null);
			} else {
				System.out.println("Invalid arguments");
				printUsage();
				return;
			}
		}
		catch(AxisFault e) {
			System.out.println("FaultActor: " + e.getFaultActor());
			System.out.println("FaultNode: " + e.getFaultNode());
			System.out.println("FaultReason: " + e.getFaultReason());
			System.out.println("FaultRole: " + e.getFaultRole());
			System.out.println("FaultString: " + e.getFaultString());
			e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();			
		}
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
		System.out.println("setDefinitions <binder id> <comma separated definitionIds> <comma separated definitionId,workflowId>");
		System.out.println("setTeamMembers <binder id> <comma separated names>");
		System.out.println("addEntry <folder id> <definition id> <fileNameContainingEntryDataXMLString> [<attachmentFileName>]");
		System.out.println("addReply <folder id> <entry id> <definition id> <fileNameContainingEntryDataXMLString>");
		System.out.println("modifyEntry <folder id> <entry id> <entryDataXMLString>");
		System.out.println("uploadFile <folder id> <entry id> <fileDataFieldName> <filename>");
		System.out.println("uploadCalendar <folder id> <xmlFilename> [<iCalFilename>]");
		System.out.println("search <xmlFilename> <offset> <maxResults>");
		System.out.println("-- BEGIN: The following is to be used only in conjunction with extendedws sample --");
		System.out.println("getFolderTitle <folder id>");
		System.out.println("-- END: --");
		System.out.println("setFunctionMembership <binderId> <functionDataXml>");
		System.out.println("setFunctionMembershipInherited <binderId> <boolean>");
		System.out.println("setOwner <binderId> <userId>");
		System.out.println("indexFolder <folder id>");
		System.out.println("migrateBinder <parentBinder id> <definition id> <entryDataXMLString> <creator> <createDate> <modifier> <modDate> ");
		System.out.println("migrateEntry <folder id> <definition id> <entryDataXMLString> <creator> <createDate> <modifier> <modDate> ");
		System.out.println("migrateReply <folder id> <entry id> <definition id> <entryDataXMLString> <creator> <createDate> <modifier> <modDate> ");
		System.out.println("migrateWorkflow <folder id> <entry id> <definition id> <startState> <modifier> <modDate>");
		System.out.println("migrateFile <folder id> <entry id> <fileDataFieldName> <filename> <modifier> <modDate>");
		System.out.println("migrateFileStaged <folder id> <entry id> <fileDataFieldName> <fileName> <stagedFileRelativePath> <modifier> <modDate>");
		System.out.println("synchronize <mirrored folder id>");
	}

}