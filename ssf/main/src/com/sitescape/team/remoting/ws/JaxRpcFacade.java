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

import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.sitescape.team.module.binder.remoting.ws.BinderService;
import com.sitescape.team.module.definition.remoting.ws.DefinitionService;
import com.sitescape.team.module.folder.remoting.ws.FolderService;
import com.sitescape.team.module.ical.remoting.ws.IcalService;
import com.sitescape.team.module.profile.remoting.ws.ProfileService;
import com.sitescape.team.module.template.remoting.ws.TemplateService;
import com.sitescape.team.module.zone.remoting.ws.ZoneService;
import com.sitescape.team.remoting.Facade;

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
	private BinderService binderService;
	private IcalService icalService;
	private ProfileService profileService;
	private ZoneService zoneService;
	
	protected void onInit() {
		this.templateService = (TemplateService) getWebApplicationContext().getBean("templateService");
		this.definitionService = (DefinitionService) getWebApplicationContext().getBean("definitionService");
		this.folderService = (FolderService) getWebApplicationContext().getBean("folderService");
		this.binderService = (BinderService) getWebApplicationContext().getBean("binderService");
		this.icalService = (IcalService) getWebApplicationContext().getBean("icalService");
		this.profileService = (ProfileService) getWebApplicationContext().getBean("profileService");
		this.zoneService = (ZoneService) getWebApplicationContext().getBean("zoneService");
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.definition.remoting.ws#getDefinitionAsXML}.
	 */
	public String getDefinitionAsXML(String definitionId) {
		return this.definitionService.getDefinitionAsXML(definitionId);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.definition.remoting.ws#getDefinitionConfigAsXML}.
	 */
	public String getDefinitionConfigAsXML() {
		return this.definitionService.getDefinitionConfigAsXML();
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.admin.remoting.ws#addBinder}.
	 */
	public long addFolder(long parentBinderId, long binderConfigId, String title) {
		return this.templateService.addBinder(parentBinderId, binderConfigId, title);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.folder.remoting.ws#getFolderEntriesAsXML}.
	 */
	public String getFolderEntriesAsXML(long binderId) {
		return this.folderService.getFolderEntriesAsXML(binderId);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.folder.remoting.ws#getFolderEntriesAsXML}.
	 */
	public String getFolderEntryAsXML(long binderId, long entryId, boolean includeAttachments) {
		return this.folderService.getFolderEntryAsXML(binderId, entryId, includeAttachments);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.folder.remoting.ws#addFolderEntry}.
	 */
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return this.folderService.addFolderEntry(binderId, definitionId, inputDataAsXML, attachedFileName);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.folder.remoting.ws#modifyFolderEntry}.
	 */
	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		this.folderService.modifyFolderEntry(binderId, entryId, inputDataAsXML);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.folder.remoting.ws#uploadFolderFile}.
	 */
	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		this.folderService.uploadFolderFile(binderId, entryId, fileUploadDataItemName, fileName);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.ical.remoting.ws#uploadCalendarEntries}.
	 */
	public void uploadCalendarEntries(long folderId, String iCalDataAsXML)
	{
		this.icalService.uploadCalendarEntries(folderId, iCalDataAsXML);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.binder.remoting.ws#search}.
	 */
	public String search(String query, int offset, int maxResults)
	{
		return this.binderService.search(query, offset, maxResults);
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
	 * replaced by {@link com.sitescape.team.module.profile.remoting.ws#getAllPrincipalsAsXML}.
	 */
	public String getAllPrincipalsAsXML(int firstRecord, int maxRecords) {
		return this.profileService.getAllPrincipalsAsXML(firstRecord, maxRecords);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.profile.remoting.ws#getPrincipalAsXML}.
	 */
	public String getPrincipalAsXML(long binderId, long principalId) {
		return this.profileService.getPrincipalAsXML(binderId, principalId);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.profile.remoting.ws#addUserToGroup}.
	 */
	public void addUserToGroup(long userId, String username, long groupId) {
		this.profileService.addUserToGroup(userId, username, groupId);
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
	 * replaced by {@link com.sitescape.team.module.binder.remoting.ws#getWorkspaceTreeAsXML}.
	 */
	public String getWorkspaceTreeAsXML(long binderId, int levels, String page) {
		return this.binderService.getWorkspaceTreeAsXML(binderId, levels, page);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.binder.remoting.ws#getTeamMembersAsXML}.
	 */
	public String getTeamMembersAsXML(long binderId) {
		return this.binderService.getTeamMembersAsXML(binderId);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.binder.remoting.ws#getTeamsAsXML}.
	 */
	public String getTeamsAsXML() {
		return this.binderService.getTeamsAsXML();
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.zone.remoting.ws#addZoneUnderPortal}.
	 */
	public void addZoneUnderPortal(String zoneName, String virtualHost, String mailDomain) {
		this.zoneService.addZoneUnderPortal(zoneName, virtualHost, mailDomain);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.zone.remoting.ws#modifyZoneUnderPortal}.
	 */
	public void modifyZoneUnderPortal(String zoneName, String virtualHost, String mailDomain) {
		this.zoneService.modifyZoneUnderPortal(zoneName, virtualHost, mailDomain);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.module.zone.remoting.ws#deleteZoneUnderPortal}.
	 */
	public void deleteZoneUnderPortal(String zoneName) {
		this.zoneService.deleteZoneUnderPortal(zoneName);
	}

}
