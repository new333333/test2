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
package com.sitescape.team.remoting.ws;

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

	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML) {
		return this.facade.addReply(binderId, parentId, definitionId, inputDataAsXML);
	}
	*/
	
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
}
