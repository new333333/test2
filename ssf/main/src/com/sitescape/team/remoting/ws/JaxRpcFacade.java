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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.remoting.Facade;
import com.sitescape.team.remoting.ws.service.binder.BinderService;
import com.sitescape.team.remoting.ws.service.definition.DefinitionService;
import com.sitescape.team.remoting.ws.service.definition.DefinitionServiceImpl;
import com.sitescape.team.remoting.ws.service.folder.FolderService;
import com.sitescape.team.remoting.ws.service.folder.MigrationService;
import com.sitescape.team.remoting.ws.service.ical.IcalService;
import com.sitescape.team.remoting.ws.service.profile.ProfileService;
import com.sitescape.team.remoting.ws.service.search.SearchService;
import com.sitescape.team.remoting.ws.service.template.TemplateService;
import com.sitescape.team.remoting.ws.service.zone.ZoneService;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.util.LongIdUtil;

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
	private MigrationService migrationService;
	
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
		return ((DefinitionServiceImpl) this.definitionService).getDefinitionConfigAsXML(null);
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
	 * replaced by {@link com.sitescape.team.remoting.ws.service.binder#getDefinitions}.
	 */
	public void setDefinitions(long binderId, String[] definitionIds, String[] workflowAssociations) {
		this.binderService.setDefinitions(null, binderId, definitionIds, workflowAssociations);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.binder#setFunctionMembership}.
	 */
	public void setFunctionMembership(long binderId, String inputDataAsXml) {
		this.binderService.setFunctionMembership(null, binderId, inputDataAsXml);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.binder#setFunctionMembershipInherited}.
	 */
	public void setFunctionMembershipInherited(long binderId, boolean inherit) {
		this.binderService.setFunctionMembershipInherited(null, binderId, inherit);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.binder#setOwner}.
	 */
	public void setOwner(long binderId, long userId) {
		this.binderService.setOwner(null, binderId, userId);
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
	 * replaced by {@link com.sitescape.team.remoting.ws.service.folder#addReply}.
	 */
	public long addReply(long binderId, long parentEntryId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return this.folderService.addReply(null, binderId, parentEntryId, definitionId, inputDataAsXML, attachedFileName);
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
	public void synchronizeMirroredFolder(long binderId) {
		this.folderService.synchronizeMirroredFolder(null, binderId);
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
	 * replaced by {@link com.sitescape.team.remoting.ws.service.profile#addUserWorkspace}.
	 */
	public long addUserWorkspace(long userId) {
		return this.profileService.addUserWorkspace(null, userId);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.profile#addUserToGroup}.
	 */
	public void addUserToGroup(long userId, String username, long groupId) {
		this.profileService.addUserToGroup(null, userId, username, groupId);
	}
	
	
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
	
	public void setTeamMembers(long binderId, String[] memberNames) {
		this.binderService.setTeamMembers(null, binderId, memberNames);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.zone#deleteZoneUnderPortal}.
	 */
	public void indexFolder(long folderId) {
		this.binderService.indexBinder(null, folderId);
	}
	//Migration services from sitescape forum
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.folder.migration#addBinder}.
	 */
	public long migrateBinder(long parentId, String definitionId, String inputDataAsXML,
			String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return this.migrationService.addBinder(null, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.migration#addFolderEntry}.
	 */
	public long migrateFolderEntry(long binderId, String definitionId, String inputDataAsXML, 
							   String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return this.migrationService.addFolderEntry(null, binderId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
		
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.migration#addReply}.
	 */
	public long migrateReply(long binderId, long parentId, String definitionId,
					     String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return this.migrationService.addReply(null, binderId, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.migration#uploadFolderFile}.
	 */
	public void migrateFolderFile(long binderId, long entryId, String fileUploadDataItemName,
								 String fileName, String modifier, Calendar modificationDate) {
		this.migrationService.uploadFolderFile(null, binderId, entryId, fileUploadDataItemName, fileName, modifier,  modificationDate);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.migration#uploadFolderFileStaged}.
	 */
	public void migrateFolderFileStaged(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate) {
		this.migrationService.uploadFolderFileStaged(null, binderId, entryId, fileUploadDataItemName, fileName, stagedFileRelativePath, modifier,  modificationDate);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link com.sitescape.team.remoting.ws.service.migration#addEntryWorkflow}.
	 */
	public void migrateEntryWorkflow(long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate) {
		this.migrationService.addEntryWorkflow(null, binderId, entryId, definitionId, startState, modifier, modificationDate);
	}

}
