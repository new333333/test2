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
package com.sitescape.team.remoting;

import java.util.Calendar;
/**
 * WS facade for business tier.
 * 
 * @author jong
 *
 */
public interface Facade {
	//
	// Definition operations
	// 
	public String getDefinitionAsXML(String definitionId);
	
	public String getDefinitionConfigAsXML();
	/**
	 * Return a list of definitions with their titles,id and type
	 * @return
	 */
	public String getDefinitionListAsXML();
	/**
	 * 
	 * @param accessToken
	 * @param binderId
	 * @param definitionIds
	 * @param workflowAssociations <Pairs of entryDefinitionId,workflowDefinitionId
	 */
	public void setDefinitions(long binderId, String[] definitionIds, String[]workflowAssociations);
	/**
	 * Set function membership for a binder.  Can mix and match memberName and/or members.
	 * <workAreaFunctionMemberships>  
	 * <workAreaFunctionMembership>
	 * <property name="functionName">__role.visitor</property>
	 * <property name="memberName">kelly</property>
	 * <property name="memberName">jenny</property>
	 * <property name="members">1,2,3</property>
	 * </workAreaFunctionMembership>
	 * </workAreaFunctionMemberships>  
	 * @param accessToken
	 * @param binderId
	 * @param inputDataAsXml
	 */
	public void setFunctionMembership(long binderId, String inputDataAsXml);
	/**
	 * Set function inheritance.  Calling <code>setFunctionMembership<code> will automatically set this to false.
	 * @param binderId
	 * @param inherit
	 */
	public void setFunctionMembershipInherited(long binderId, boolean inherit);
	/**
	 * Set the binder owner
	 * @param binderId
	 * @param userId
	 */
	public void setOwner(long binderId, long userId);
	// 
	// Folder operations
	// 	
	public long addFolder(long parentBinderId, long binderConfigId, String title);
	
	public String getFolderEntriesAsXML(long folderId);

	public String getFolderEntryAsXML(long folderId, long entryId, boolean includeAttachments);

	public long addFolderEntry(long folderId, String definitionId, String inputDataAsXML, String attachedFileName);
	
	public void modifyFolderEntry(long folderId, long entryId, String inputDataAsXML);
	
	public void addEntryWorkflow(long binderId, long entryId, String definitionId, String startState);

	public void uploadFolderFile(long folderId, long entryId, 
			String fileUploadDataItemName, String fileName);


	public void uploadCalendarEntries(long folderId, String iCalDataAsXML);

	/*
	 * Set maxResults = -1 for "all results"
	 */
	public String search(String query, int offset, int maxResults);

	/*
    public void deleteFolderEntry(long folderId, long entryId);

	*/
	
	public long addReply(long folderId, long parentEntryId, String definitionId, String inputDataAsXML, String attachedFileName);


	//
	// Profile operations
	//
	public String getAllPrincipalsAsXML(int firstRecord, int maxRecords);
	public String getPrincipalAsXML(long binderId, long principalId);


	//
	// Workspace operations
	//
	
	/**
	 * Returns workspace tree represented in XML.
	 * 
	 * @param binderId starting binder id
	 * @param levels depth to return. -1 means all
	 *  
	 * @return XML representation of the workspace tree
	 */
	public String getWorkspaceTreeAsXML(long binderId, int levels, String page);
	
	/**
	 * Returns team members for the given binder as Principal entries in XML
	 * 
	 * @param binderId id of binder
	 * 
	 * @return XML representation of team membership
	 */
	public String getTeamMembersAsXML(long binderId);
	
	/**
	 * Returns the teams that the caller is on
	 * 
	 * @return XML representation of teams
	 */
	public String getTeamsAsXML();
	/**
	 * Set team members for the binder
	 * @param binderId
	 * @param memberIds
	 */
	public void setTeamMembers(long binderId, Long[] memberIds);

	public void synchronizeMirroredFolder(long binderId);
	public void indexFolder(long folderId);
	//Migration services from sitescape forum
	public long migrateBinder(long parentId, String definitionId, String inputDataAsXML,
			String creator, Calendar creationDate, String modifier, Calendar modificationDate);
	
	public long migrateFolderEntry(long binderId, String definitionId, String inputDataAsXML, 
							   String creator, Calendar creationDate, String modifier, Calendar modificationDate);
		
	public long migrateReply(long binderId, long parentId, String definitionId,
					     String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate);

	public void migrateFolderFile(long binderId, long entryId, String fileUploadDataItemName,
								 String fileName, String modifier, Calendar modificationDate);
	public void migrateFolderFileStaged(long binderId, long entryId, 
			String fileUploadDataItemName, String stagedFileRelativePath, String modifier, Calendar modificationDate);


}

