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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
				wsClient.fetchAndPrintXML("TeamingServiceV1", "search_getWorkspaceTreeAsXML", new Object[] {null, Long.parseLong(args[1]), Integer.parseInt(args[2]), (args.length > 3)?args[3]:""});
			} else if(args[0].equals("getTopWorkspaceId")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_getTopWorkspaceId", new Object[] {null});
			} else if(args[0].equals("getUser")) {
				wsClient.fetchAndPrintUser("TeamingServiceV1", "profile_getUser", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("getUserByName")) {
				wsClient.fetchAndPrintUser("TeamingServiceV1", "profile_getUserByName", new Object[] {null, args[1], Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("getUsersByEmail")) {
				String emailType = null;
				if(args.length > 2)
					emailType = args[2];
				wsClient.fetchAndPrintDEArray("TeamingServiceV1", "profile_getUsersByEmail", new Object[] {null, args[1], emailType});
			} else if(args[0].equals("getGroup")) {
				wsClient.fetchAndPrintDE("TeamingServiceV1", "profile_getGroup", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("getGroupMembers")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "profile_getGroupMembers", new Object[] {null, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("getPrincipals")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "profile_getPrincipals", new Object[] {null, Integer.parseInt(args[1]), Integer.parseInt(args[2])});
			} else if(args[0].equals("getUsers")) {
				Boolean captive = null;
				if(args.length > 3) {
					captive = Boolean.valueOf(args[3]);
				}
				wsClient.fetchAndPrintACK("TeamingServiceV1", "profile_getUsers", new Object[] {null, captive, Integer.parseInt(args[1]), Integer.parseInt(args[2])});
			} else if(args[0].equals("getFolderEntries")) {
				wsClient.fetchAndPrintFEC("TeamingServiceV1", "folder_getEntries", new Object[] {null, Long.parseLong(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("getTrashEntries")) {
				wsClient.fetchAndPrintTRC("TeamingServiceV1", "binder_getTrashEntries", new Object[] {null, Long.parseLong(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("getTeamMembers")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_getTeamMembers", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4])});
			} else if(args[0].equals("getTeams")) {
				wsClient.fetchAndPrintTeamC("TeamingServiceV1", "search_getTeams", new Object[] {null});
			} else if(args[0].equals("getUserTeams")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "search_getUserTeams", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("getMaxUserQuota")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "profile_getMaxUserQuota", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("getUserGroups")) {
				wsClient.fetchAndPrintGC("TeamingServiceV1", "profile_getUserGroups", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("getBinder")) {
				wsClient.fetchAndPrintBinder("TeamingServiceV1", "binder_getBinder", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("getBinderTags")) {
				wsClient.fetchAndPrintTags("TeamingServiceV1", "binder_getTags", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("setBinderDefinitionsInherited")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_setDefinitionsInherited", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("getFolderEntry")) {
				wsClient.fetchAndPrintFolderEntry("TeamingServiceV1", "folder_getEntry", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3])});			
			} else if(args[0].equals("getFolderEntryTags")) {
				wsClient.fetchAndPrintTags("TeamingServiceV1", "folder_getEntryTags", new Object[] {null, Long.parseLong(args[1])});			
			} else if(args[0].equals("getToken")) {
				wsClient.fetchAndPrintString("TeamingServiceV1", "admin_getApplicationScopedToken", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});			
			} else if(args[0].equals("destroyToken")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "admin_destroyApplicationScopedToken", new Object[] {null, args[1]});			
			} else if(args[0].equals("getDefinition")) {
				wsClient.fetchAndPrintXML("TeamingServiceV1", "definition_getDefinitionAsXML", new Object[] {null, args[1]});
			} else if(args[0].equals("addWorkflow")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_addEntryWorkflow", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("modifyWorkflow")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_modifyWorkflowState", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3]});
			} else if(args[0].equals("uploadFile")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_uploadFile", new Object[] {null, Long.parseLong(args[1]), args[2], args[3]}, args[3]);
			} else if(args[0].equals("uploadFileStaged")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_uploadFileStaged", new Object[] {null, Long.parseLong(args[1]), args[2], args[3], args[4]});
			} else if(args[0].equals("uploadCalendar")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				String attachFile = null;
				if(args.length > 3) {
					attachFile = args[3];
				}
				wsClient.fetchAndPrintACK("TeamingServiceV1", "ical_uploadCalendarEntriesWithXML", new Object[] {null, Long.parseLong(args[1]), s}, attachFile);
			} else if(args[0].equals("getAttachmentAsByteArray")) {
				wsClient.fetchAndPrintByteArray("TeamingServiceV1", "folder_getAttachmentAsByteArray", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("incrementFileMajorVersion")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_incrementFileMajorVersion", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("getFileVersionAsByteArray")) {
				wsClient.fetchAndPrintByteArray("TeamingServiceV1", "folder_getFileVersionAsByteArray", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("setFileVersionNote")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_setFileVersionNote", new Object[] {null, Long.parseLong(args[1]), args[2], args[3]});
			} else if(args[0].equals("promoteFileVersionCurrent")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_promoteFileVersionCurrent", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("deleteFileVersion")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_deleteFileVersion", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("setFileVersionStatus")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_setFileVersionStatus", new Object[] {null, Long.parseLong(args[1]), args[2], Integer.parseInt(args[3])});
			} else if(args[0].equals("getFileVersions")) {
				wsClient.fetchAndPrintFileVersions("TeamingServiceV1", "folder_getFileVersions", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("getFileVersionsFromAttachment")) {
				wsClient.fetchAndPrintFileVersions("TeamingServiceV1", "folder_getFileVersionsFromAttachment", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("removeFile")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_removeFile", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("removeAttachment")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_removeAttachment", new Object[] {null, Long.parseLong(args[1]), args[2]});
			} else if(args[0].equals("validateUploadFile")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_validateUploadFile", new Object[] {null, Long.parseLong(args[1]), args[2],  Long.parseLong(args[3])});
			} else if(args[0].equals("validateUploadAttachment")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_validateUploadAttachment", new Object[] {null, Long.parseLong(args[1]), args[2],  Long.parseLong(args[3])});
			} else if(args[0].equals("search")) {
				String s = readText(args[1]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintXML("TeamingServiceV1", "search_search", new Object[] {null, s, Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("searchFolderEntries")) {
				String s = readText(args[1]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintFEC("TeamingServiceV1", "search_getFolderEntries", new Object[] {null, s, Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("addUserToGroup")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "profile_addUserToGroup", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("getFavorites")) {
				wsClient.fetchAndPrintBBArray("TeamingServiceV1", "profile_getFavorites", new Object[] {null});
			} else if(args[0].equals("getFollowedPlaces")) {
				wsClient.fetchAndPrintBBArray("TeamingServiceV1", "profile_getFollowedPlaces", new Object[] {null, stringToLongOrNull(args[1]), split(args[2]), stringToBooleanOrNull(args[3])});
			} else if(args[0].equals("getFolderTitle")) {
				wsClient.fetchAndPrintString("TeamingServiceV1", "folder_getFolderTitle", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("addZone")) {
				String mailDomain = null;
				if(args.length > 3)
					mailDomain = args[3];
				wsClient.fetchAndPrintIdentifier("TeamingServiceV1", "zone_addZone", new Object[] {null, args[1], args[2], mailDomain});
			} else if(args[0].equals("modifyZone")) {
				String mailDomain = null;
				if(args.length > 3)
					mailDomain = args[3];
				wsClient.fetchAndPrintACK("TeamingServiceV1", "zone_modifyZone", new Object[] {null, args[1], args[2], mailDomain});
			} else if(args[0].equals("deleteZone")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "zone_deleteZone", new Object[] {null, args[1]});
			} else if(args[0].equals("getHotContent")) {
				Long binderId = null;
				if(args.length > 2) {
					binderId = Long.valueOf(args[2]); 
				}
				wsClient.fetchAndPrintXML("TeamingServiceV1", "search_getHotContent", new Object[] {null, args[1], binderId});
			} else if(args[0].equals("getDefinitions")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "definition_getDefinitions", new Object[] {null});
			} else if(args[0].equals("getTemplates")) {
				wsClient.fetchAndPrintTemplateC("TeamingServiceV1", "template_getTemplates", new Object[] {null});
			} else if(args[0].equals("setTeamMembers")) {
				String names[] = split(args[2]);
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_setTeamMembers", new Object[] {null, Long.parseLong(args[1]), names});
			} else if(args[0].equals("setDefinitions")) {
				String ids[] = split(args[2]);
				List idsList = new ArrayList();
				for (int i=0; i<ids.length; ++i) {
					idsList.add(ids[i]);
				}
				List wfs = new ArrayList();
				if (args.length > 3) {
					ids = split(args[3]); //entryDef,workflowDef
					for (int i=0; i+1<ids.length; i+=2) {
						wfs.add(ids[i] + "," + ids[i+1]);
					}
				}
					wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_setDefinitions", new Object[] {null, Long.parseLong(args[1]), idsList, wfs});
			} else if(args[0].equals("setFunctionMembership")) {
				String s = readText(args[2]);
				System.out.println("XML: " + s);
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_setFunctionMembership", new Object[] {null, Long.parseLong(args[1]), s});
			} else if(args[0].equals("setFunctionMembershipInherited")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_setFunctionMembershipInherited", new Object[] {null, Long.parseLong(args[1]), Boolean.parseBoolean(args[2])});
			} else if(args[0].equals("setOwner")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_setOwner", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2])});
			} else if(args[0].equals("indexBinder")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_indexBinder", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("synchronize")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_synchronizeMirroredFolder", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("migrateEntryWithXML")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[5]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[7]));
				
				wsClient.fetchAndPrintIdentifier("TeamingServiceV1", "migration_addFolderEntryWithXML", new Object[] {null, Long.parseLong(args[1]), args[2], s, args[4], c1,
						args[6], c2});
			} else if(args[0].equals("migrateReplyWithXML")) {
				String s = readText(args[4]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[8]));
				
				wsClient.fetchAndPrintIdentifier("TeamingServiceV1", "migration_addReplyWithXML", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], s, args[5], c1,
						args[7], c2});
			} else if(args[0].equals("migrateWorkflow")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				wsClient.fetchAndPrintACK("TeamingServiceV1", "migration_addEntryWorkflow", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], c1});
			} else if(args[0].equals("migrateBinderWithXML")) {
				String s = readText(args[3]);
				System.out.println("XML: " + s);
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[5]));
				Calendar c2 = Calendar.getInstance();
				c2.setTime(df.parse((String)args[7]));
				wsClient.fetchAndPrintIdentifier("TeamingServiceV1", "migration_addBinderWithXML", new Object[] {null, Long.parseLong(args[1]), args[2], s, args[4], c1, args[6], c2});
			} else if(args[0].equals("migrateFile")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[6]));
				wsClient.fetchAndPrintACK("TeamingServiceV1", "migration_uploadFolderFile", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], c1}, args[4]);
			} else if(args[0].equals("migrateFileStaged")) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(df.parse((String)args[7]));
				wsClient.fetchAndPrintACK("TeamingServiceV1", "migration_uploadFolderFileStaged", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), args[3], args[4], args[5], args[6], c1}, null);
			} else if(args[0].equals("getFolders")) {
				wsClient.fetchAndPrintFC("TeamingServiceV1", "binder_getFolders", new Object[] {null, Long.parseLong(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3])});
			} else if(args[0].equals("getAllFoldersOfMatchingFamily")) {
				wsClient.fetchAndPrintFC("TeamingServiceV1", "binder_getAllFoldersOfMatchingFamily", new Object[] {null, splitLong(args[1]), split(args[2]), Boolean.parseBoolean(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5])});
			} else if(args[0].equals("preDeleteBinder")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_preDeleteBinder", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("restoreBinder")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_restoreBinder", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("preDeleteEntry")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_preDeleteEntry", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("restoreEntry")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "folder_restoreEntry", new Object[] {null, Long.parseLong(args[1])});
			} else if(args[0].equals("checkQuotaAndFileSizeLimit")) {
				wsClient.fetchAndPrintACK("TeamingServiceV1", "binder_checkQuotaAndFileSizeLimit", new Object[] {null, Long.parseLong(args[1]), Long.parseLong(args[2]), Long.parseLong(args[3]), args[4]});
			} else if(args[0].equals("getCreatedOrUpdatedEntries")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				wsClient.fetchAndPrintPrimitiveArray("TeamingServiceV1", "folder_getCreatedOrUpdatedEntries", 
						new Object[] {null, args[1], sdf.parse(args[2]), sdf.parse(args[3])});
			} else if(args[0].equals("testBinderAccess")) {
				String[] sIds = split(args[2]);
				long[] ids = new long[sIds.length];
				for(int i = 0; i < sIds.length; i++)
					ids[i] = Long.parseLong(sIds[i]);
				wsClient.fetchAndPrintPrimitiveArray("TeamingServiceV1", "binder_testAccess", new Object[] {null, args[1], ids});
			} else if(args[0].equals("testBinderOperation")) {
				String[] sIds = split(args[2]);
				long[] ids = new long[sIds.length];
				for(int i = 0; i < sIds.length; i++)
					ids[i] = Long.parseLong(sIds[i]);
				wsClient.fetchAndPrintPrimitiveArray("TeamingServiceV1", "binder_testOperation", new Object[] {null, args[1], ids});
			} else if(args[0].equals("getDeletedEntries")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				wsClient.fetchAndPrintPrimitiveArray("TeamingServiceV1", "folder_getDeletedEntries", 
						new Object[] {null, args[1], sdf.parse(args[2]), sdf.parse(args[3])});
			} else if(args[0].equals("getDeletedEntriesInFolders")) {
				String[] sIds = split(args[1]);
				long[] ids = new long[sIds.length];
				for(int i = 0; i < sIds.length; i++)
					ids[i] = Long.parseLong(sIds[i]);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				wsClient.fetchAndPrintPrimitiveArray("TeamingServiceV1", "folder_getDeletedEntriesInFolders", 
						new Object[] {null, ids, args[2], sdf.parse(args[3]), sdf.parse(args[4])});
			} else if(args[0].equals("getCurrentServerTime")) {
				wsClient.fetchAndPrintCalendar("TeamingServiceV1", "admin_getCurrentServerTime", new Object[] {null});
			} else if(args[0].equals("testFolderOperation")) {
				String[] sIds = split(args[2]);
				long[] ids = new long[sIds.length];
				for(int i = 0; i < sIds.length; i++)
					ids[i] = Long.parseLong(sIds[i]);
				wsClient.fetchAndPrintPrimitiveArray("TeamingServiceV1", "folder_testFolderOperation", new Object[] {null, args[1], ids});
			} else if(args[0].equals("testFolderEntryOperation")) {
				String[] sIds = split(args[2]);
				long[] ids = new long[sIds.length];
				for(int i = 0; i < sIds.length; i++)
					ids[i] = Long.parseLong(sIds[i]);
				wsClient.fetchAndPrintPrimitiveArray("TeamingServiceV1", "folder_testEntryOperation", new Object[] {null, args[1], ids});
			} else if(args[0].equals("testFolderEntryOperations")) {
				String[] ops = split(args[1]);
				wsClient.fetchAndPrintPrimitiveArray("TeamingServiceV1", "folder_testEntryOperations", new Object[] {null, ops, Long.parseLong(args[2])});
			} else if(args[0].equals("getReleaseInfo")) {
				wsClient.fetchAndPrintReleaseInfo("TeamingServiceV1", "admin_getReleaseInfo", new Object[] {null});
			} else if(args[0].equals("getZoneConfig")) {
				wsClient.fetchAndPrintZoneConfig("TeamingServiceV1", "admin_getZoneConfig", new Object[] {null});
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
		System.out.println("getTopWorkspaceId");
		System.out.println("getUser  <user id>  <includeAttachments>");
		System.out.println("getUserByName  <user name>  <includeAttachments>");
		System.out.println("getUsersByEmail  <email address>  [<email type>]");
		System.out.println("getGroup  <group id>  <includeAttachments>");
		System.out.println("getGroupMembers  <groupName> <first> <max>");
		System.out.println("getUserGroups <user id>");
		System.out.println("getPrincipals <first> <max>");
		System.out.println("getUsers <first> <max> [<captive>]");
		System.out.println("getFolderEntries <folder id> <first> <max>"); 
		System.out.println("getTrashEntries <folder id> <first> <max>"); 
		System.out.println("getCreatedOrUpdatedEntries <family> <startDateTime - yyyyMMddHHmm> <endDateTime - yyyyMMddHHmm>"); 
		System.out.println("getDeletedEntries <family> <startDateTime (yyyyMMddHHmm)> <endDateTime (yyyyMMddHHmm)>"); 
		System.out.println("getDeletedEntriesInFolders <comma separated folder ids> <family> <startDateTime (yyyyMMddHHmm)> <endDateTime (yyyyMMddHHmm)>"); 
		System.out.println("getTeamMembers <binder id> <explodeGroups> <first> <max>");
		System.out.println("getTeams");
		System.out.println("getUserTeams <user id>");
		System.out.println("getMaxUserQuota <user id>");
		System.out.println("getBinder <binder id> <includeAttachments>");
		System.out.println("getBinderTags <binder id>");
		System.out.println("setBinderDefinitionsInherited <binder id> <setBinderDefinitionsInherited>");
		System.out.println("getFolderEntry <entry id> <includeAttachments> <eventAsIcalString>");
		System.out.println("getFolderEntryTags <entry id>");
		System.out.println("getDefinition <definition id>");
		System.out.println("getDefinitions");
		System.out.println("getTemplates");
		System.out.println("getToken <application id> <user id>");
		System.out.println("destroyToken <token>");
		System.out.println("setDefinitions <binder id> <comma separated definitionIds> <comma separated definitionId,workflowId>");
		System.out.println("setTeamMembers <binder id> <comma separated names>");
		System.out.println("addWorkflow <entry id> <definition id>");
		System.out.println("modifyWorkflow <entry id> <state id> <toState");
		System.out.println("uploadFile <entry id> <fileDataFieldName> <filename>");
		System.out.println("uploadFileStaged  <entry id> <fileDataFieldName> <fileName> <stagedFileRelativePath>");
		System.out.println("uploadCalendar <folder id> <xmlFilename> [<iCalFilename>]");
		System.out.println("search <xmlFilename> <offset> <maxResults>");
		System.out.println("searchFolderEntries <xmlFilename> <offset> <maxResults>");
		System.out.println("addUserToGroup <user id> <group id>");
		System.out.println("getFavorites");
		System.out.println("getFollowedPlaces <user id> \"family1, family2,...\" <is library>");
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
		System.out.println("getFolders <binder id> <first> <max>");
		System.out.println("getAllFoldersOfMatchingFamily \"starting-binder-id1, starting-binder-id2,...\" \"family1, family2,...\" <restrictByTeamMembership> <first> <max>");
		System.out.println("preDeleteBinder <binder id>");
		System.out.println("restoreBinder <binder id>");
		System.out.println("preDeleteEntry <entry id>");
		System.out.println("restoreEntry <entry id>");
		System.out.println("checkQuotaAndFileSizeLimit <user id> <binder id> <file size in bytes> <file name>");
		System.out.println("testBinderAccess <work area operation name> \"binder id1, binder id2,....\"");
		System.out.println("testBinderOperation <binder operation name> \"binder id1, binder id2,....\"");
		System.out.println("getAttachmentAsByteArray <entry id> <attachment id>");
		System.out.println("incrementFileMajorVersion <entry id> <attachment id>");
		System.out.println("getFileVersionAsByteArray <entry id> <file version id>");
		System.out.println("setFileVersionNote <entry id> <file version id> <note>");
		System.out.println("promoteFileVersionCurrent <entry id> <file version id>");
		System.out.println("deleteFileVersion <entry id> <file version id>");
		System.out.println("setFileVersionStatus <entry id> <file version id> <status>");
		System.out.println("getFileVersions <entry id> <file name>");
		System.out.println("getFileVersionsFromAttachment <entry id> <attachment id>");
		System.out.println("removeFile <entry id> <file name>");
		System.out.println("removeAttachment <entry id> <attachment id>");
		System.out.println("getCurrentServerTime");
		System.out.println("testFolderOperation <folder operation name> \"folder id1, folder id2,....\"");
		System.out.println("testFolderEntryOperation <folder entry operation name> \"entry id1, entry id2,....\"");
		System.out.println("testFolderEntryOperations \"operation name1, operation name2,....\" <folder entry id>");
		System.out.println("getReleaseInfo");
		System.out.println("getZoneConfig");
		System.out.println("validateUploadFile <entry id> <filename> <file size>");
		System.out.println("validateUploadAttachment <entry id> <attachment id> <file size>");
		
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
