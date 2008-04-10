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

	public void setDefinitions(long binderId, List<String>definitionIds, List<String>workflowAssociations) {
		this.facade.setDefinitions(binderId, definitionIds, workflowAssociations);
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
	public void addEntryWorkflow(long binderId, long entryId, String definitionId, String startState) {
		this.facade.addEntryWorkflow(binderId, entryId, definitionId, startState);
	}

	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		this.facade.uploadFolderFile(binderId, entryId, fileUploadDataItemName, fileName);
	}
	
	public void uploadFolderFileStaged(long binderId, long entryId, 
			String fileUploadDataItemName, String stagedFileRelativePath) {
		this.facade.uploadFolderFileStaged(binderId, entryId, fileUploadDataItemName, stagedFileRelativePath);
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
	/*
	public long addUser(long binderId, String definitionId, String inputDataAsXML) {
		return this.facade.addUser(binderId, definitionId, inputDataAsXML);
	}

	public long addGroup(long binderId, String definitionId, String inputDataAsXML) {
		return this.facade.addGroup(binderId, definitionId, inputDataAsXML);
	}

	public void modifyPrincipal(long binderId, long principalId, String inputDataAsXML) {
		this.facade.modifyPrincipal(binderId, principalId, inputDataAsXML);
	}

	public void deletePrincipal(long binderId, long principalId) {
		this.facade.deletePrincipal(binderId, principalId);
	}
	*/
	public String getWorkspaceTreeAsXML(long binderId, int levels, String page) {
		return this.facade.getWorkspaceTreeAsXML(binderId, levels, page);
	}
	
	public String getTeamMembersAsXML(long binderId) {
		return this.facade.getTeamMembersAsXML(binderId);
	}

	public String getTeamsAsXML() {
		return this.facade.getTeamsAsXML();
	}
	public void setTeamMembers(long binderId, List<Long> memberIds) {
		this.facade.setTeamMembers(binderId, memberIds);
	}

	public void synchronizeMirroredFolder(long binderId) {
		this.facade.synchronizeMirroredFolder(binderId);
	}
}
