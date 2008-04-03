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
import com.sitescape.team.remoting.ws.service.binder.BinderService;
import com.sitescape.team.remoting.ws.service.definition.DefinitionService;
import com.sitescape.team.remoting.ws.service.folder.FolderService;
import com.sitescape.team.remoting.ws.service.ical.IcalService;
import com.sitescape.team.remoting.ws.service.profile.ProfileService;
import com.sitescape.team.remoting.ws.service.search.SearchService;
import com.sitescape.team.remoting.ws.service.template.TemplateService;
import com.sitescape.team.remoting.ws.service.zone.ZoneService;

/**
 * JAX-RPC compliant implementation that simply delegates to the Facade 
 * implementation class that is loaded by Spring controlled application
 * context. 
 * 
 * @author jong
 * @deprecated As of ICEcore version 1.1,
 * replaced by individual module service classes
 *
 */
public class JaxRpcFacade extends ServletEndpointSupport implements Facade {

	private TemplateService templateService;
	private DefinitionService definitionService;
	private FolderService folderService;
	private SearchService searchService;
	private IcalService icalService;
	private ProfileService profileService;
	private ZoneService zoneService;
	private BinderService binderService;
	
	protected void onInit() {
		this.templateService = (TemplateService) getWebApplicationContext().getBean("templateService");
		this.definitionService = (DefinitionService) getWebApplicationContext().getBean("definitionService");
		this.folderService = (FolderService) getWebApplicationContext().getBean("folderService");
		this.searchService = (SearchService) getWebApplicationContext().getBean("searchService");
		this.icalService = (IcalService) getWebApplicationContext().getBean("icalService");
		this.profileService = (ProfileService) getWebApplicationContext().getBean("profileService");
		this.zoneService = (ZoneService) getWebApplicationContext().getBean("zoneService");
		this.binderService = (BinderService) getWebApplicationContext().getBean("binderService");
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.definition#getDefinitionAsXML}.
	 */
	public String getDefinitionAsXML(String definitionId) {
		return this.definitionService.getDefinitionAsXML(null, definitionId);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.definition#getDefinitionConfigAsXML}.
	 */
	public String getDefinitionConfigAsXML() {
		return this.definitionService.getDefinitionConfigAsXML(null);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.definition#getDefinitionListAsXML}.
	 */
	public String getDefinitionListAsXML() {
		return this.definitionService.getDefinitionListAsXML(null);

	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.admin.remoting.ws#addBinder}.
	 */
	public long addFolder(long parentBinderId, long binderConfigId, String title) {
		return this.templateService.addBinder(null, parentBinderId, binderConfigId, title);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.folder#getFolderEntriesAsXML}.
	 */
	public String getFolderEntriesAsXML(long binderId) {
		return this.folderService.getFolderEntriesAsXML(null, binderId);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.folder#getFolderEntriesAsXML}.
	 */
	public String getFolderEntryAsXML(long binderId, long entryId, boolean includeAttachments) {
		return this.folderService.getFolderEntryAsXML(null, binderId, entryId, includeAttachments);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.folder#addFolderEntry}.
	 */
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return this.folderService.addFolderEntry(null, binderId, definitionId, inputDataAsXML, attachedFileName);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.folder#modifyFolderEntry}.
	 */
	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		this.folderService.modifyFolderEntry(null, binderId, entryId, inputDataAsXML);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.folder#uploadFolderFile}.
	 */
	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		this.folderService.uploadFolderFile(null, binderId, entryId, fileUploadDataItemName, fileName);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.folder#addEntryWorkflow}.
	 */
	public void addEntryWorkflow(long binderId, long entryId, String definitionId, String startState) {
		this.folderService.addEntryWorkflow(null, binderId, entryId, definitionId, startState);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.ical#uploadCalendarEntries}.
	 */
	public void uploadCalendarEntries(long folderId, String iCalDataAsXML)
	{
		this.icalService.uploadCalendarEntries(null, folderId, iCalDataAsXML);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.search#search}.
	 */
	public String search(String query, int offset, int maxResults)
	{
		return this.searchService.search(null, query, offset, maxResults);
	}

	/*
	public void deleteFolderEntry(long binderId, long entryId) {
		this.facade.deleteFolderEntry(binderId, entryId);
	}

	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML) {
		return this.facade.addReply(binderId, parentId, definitionId, inputDataAsXML);
	}
	*/
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.profile#getAllPrincipalsAsXML}.
	 */
	public String getAllPrincipalsAsXML(int firstRecord, int maxRecords) {
		return this.profileService.getAllPrincipalsAsXML(null, firstRecord, maxRecords);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.profile#getPrincipalAsXML}.
	 */
	public String getPrincipalAsXML(long binderId, long principalId) {
		return this.profileService.getPrincipalAsXML(null, binderId, principalId);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.profile#addUserToGroup}.
	 */
	public void addUserToGroup(long userId, String username, long groupId) {
		this.profileService.addUserToGroup(null, userId, username, groupId);
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
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.search#getWorkspaceTreeAsXML}.
	 */
	public String getWorkspaceTreeAsXML(long binderId, int levels, String page) {
		return this.searchService.getWorkspaceTreeAsXML(null, binderId, levels, page);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.binder#getTeamMembersAsXML}.
	 */
	public String getTeamMembersAsXML(long binderId) {
		return this.binderService.getTeamMembersAsXML(null, binderId);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.search#getTeamsAsXML}.
	 */
	public String getTeamsAsXML() {
		return this.searchService.getTeamsAsXML(null);
	}
	
	public void setTeamMembers(long binderId, List<Long> memberIds) {
		this.binderService.setTeamMembers(null, binderId, memberIds);
	}	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.zone#addZoneUnderPortal}.
	 */
	public void addZoneUnderPortal(String zoneName, String virtualHost, String mailDomain) {
		this.zoneService.addZoneUnderPortal(null, zoneName, virtualHost, mailDomain);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.zone#modifyZoneUnderPortal}.
	 */
	public void modifyZoneUnderPortal(String zoneName, String virtualHost, String mailDomain) {
		this.zoneService.modifyZoneUnderPortal(null, zoneName, virtualHost, mailDomain);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.zone#deleteZoneUnderPortal}.
	 */
	public void deleteZoneUnderPortal(String zoneName) {
		this.zoneService.deleteZoneUnderPortal(null, zoneName);
	}


}
