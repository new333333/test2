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
package com.sitescape.team.remoting.ws;


import java.util.Calendar;
import java.util.List;

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.sitescape.team.remoting.Facade;

/**
 * JAX-RPC compliant implementation that simply delegates to the Facade 
 * implementation class that is loaded by Spring controlled application
 * context. 
 * 
 * @author jong
 *
 */
public class JaxRpcFacade extends ServletEndpointSupport implements Facade {

	private Facade facade;
	
	protected void onInit() {
		this.facade = (Facade) getWebApplicationContext().getBean("wsFacade");
	}

	public String getDefinitionAsXML(String definitionId) {
		return this.facade.getDefinitionAsXML(definitionId);
	}
	
	public String getDefinitionConfigAsXML() {
		return this.facade.getDefinitionConfigAsXML();
	}
	public String getDefinitionListAsXML() {
		return this.facade.getDefinitionListAsXML();
	}

	public void setDefinitions(long binderId, String[] definitionIds, String[] workflowAssociations) {
		this.facade.setDefinitions(binderId, definitionIds, workflowAssociations);
	}
	public void setFunctionMembership(long binderId, String inputDataAsXml) {
		this.facade.setFunctionMembership(binderId, inputDataAsXml);
	}
	public void setFunctionMembershipInherited(long binderId, boolean inherit) {
		this.facade.setFunctionMembershipInherited(binderId, inherit);
	}
	public void setOwner(long binderId, long userId) {
		this.facade.setOwner(binderId, userId);
	}
	public long addFolder(long parentBinderId, long binderConfigId, String title) {
		return this.facade.addFolder(parentBinderId, binderConfigId, title);
	}

	public String getFolderEntriesAsXML(long binderId) {
		return this.facade.getFolderEntriesAsXML(binderId);
	}

	public String getFolderEntryAsXML(long binderId, long entryId, boolean includeAttachments) {
		return this.facade.getFolderEntryAsXML(binderId, entryId, includeAttachments);
	}
	

	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return this.facade.addFolderEntry(binderId, definitionId, inputDataAsXML, attachedFileName);
	}

	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		this.facade.modifyFolderEntry(binderId, entryId, inputDataAsXML);
	}
	public void migrateEntryWorkflow(long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate) {
		this.facade.migrateEntryWorkflow(binderId, entryId, definitionId, startState, modifier, modificationDate);
	}

	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		this.facade.uploadFolderFile(binderId, entryId, fileUploadDataItemName, fileName);
	}
	
	
	public void uploadCalendarEntries(long folderId, String iCalDataAsXML)
	{
		this.facade.uploadCalendarEntries(folderId, iCalDataAsXML);
	}

	public String search(String query, int offset, int maxResults)
	{
		return this.facade.search(query, offset, maxResults);
	}

	/*
	public void deleteFolderEntry(long binderId, long entryId) {
		this.facade.deleteFolderEntry(binderId, entryId);
	}
	*/
	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return this.facade.addReply(binderId, parentId, definitionId, inputDataAsXML, attachedFileName);
	}

	
	public String getAllPrincipalsAsXML(int firstRecord, int maxRecords) {
		return this.facade.getAllPrincipalsAsXML(firstRecord, maxRecords);
	}
	public String getPrincipalAsXML(long binderId, long principalId) {
		return this.facade.getPrincipalAsXML(binderId, principalId);
	}
	public long addUserWorkspace(long userId) {
		return this.facade.addUserWorkspace(userId);
	}

	public String getWorkspaceTreeAsXML(long binderId, int levels, String page) {
		return this.facade.getWorkspaceTreeAsXML(binderId, levels, page);
	}
	
	public String getTeamMembersAsXML(long binderId) {
		return this.facade.getTeamMembersAsXML(binderId);
	}

	public String getTeamsAsXML() {
		return this.facade.getTeamsAsXML();
	}
	public void setTeamMembers(long binderId, Long[] memberIds) {
		this.facade.setTeamMembers(binderId, memberIds);
	}

	public void synchronizeMirroredFolder(long binderId) {
		this.facade.synchronizeMirroredFolder(binderId);
	}
	public void indexFolder(long folderId) {
		this.facade.indexFolder(folderId);
	}
	public long migrateBinder(long parentId, String definitionId, String inputDataAsXML,
			String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return this.facade.migrateBinder(parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	
	public long migrateFolderEntry(long binderId, String definitionId,  String inputDataAsXML,  
							   String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return this.facade.migrateFolderEntry(binderId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
		
	public long migrateReply(long binderId, long parentId, String definitionId,
					     String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return this.facade.migrateReply(binderId, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}

	public void migrateFolderFile(long binderId, long entryId, String fileUploadDataItemName,
								 String fileName, String modifier, Calendar modificationDate) {
		this.facade.migrateFolderFile(binderId, entryId, fileUploadDataItemName, fileName, modifier, modificationDate);
	}
	public void migrateFolderFileStaged(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate) {
		this.facade.migrateFolderFileStaged(binderId, entryId, fileUploadDataItemName, fileName, stagedFileRelativePath, modifier, modificationDate);
	}

}
