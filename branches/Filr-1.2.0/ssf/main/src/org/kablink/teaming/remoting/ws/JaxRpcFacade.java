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
package org.kablink.teaming.remoting.ws;

import java.util.Calendar;

import org.kablink.teaming.remoting.ws.service.binder.BinderService;
import org.kablink.teaming.remoting.ws.service.binder.BinderServiceInternal;
import org.kablink.teaming.remoting.ws.service.definition.DefinitionService;
import org.kablink.teaming.remoting.ws.service.definition.DefinitionServiceInternal;
import org.kablink.teaming.remoting.ws.service.folder.FolderService;
import org.kablink.teaming.remoting.ws.service.folder.FolderServiceInternal;
import org.kablink.teaming.remoting.ws.service.folder.MigrationService;
import org.kablink.teaming.remoting.ws.service.ical.IcalService;
import org.kablink.teaming.remoting.ws.service.profile.ProfileService;
import org.kablink.teaming.remoting.ws.service.profile.ProfileServiceInternal;
import org.kablink.teaming.remoting.ws.service.search.SearchService;
import org.kablink.teaming.remoting.ws.service.search.SearchServiceInternal;
import org.kablink.teaming.remoting.ws.service.template.TemplateService;
import org.springframework.remoting.jaxrpc.ServletEndpointSupport;


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
	private BinderService binderService;
	private MigrationService migrationService;
	
	protected void onInit() {
		this.templateService = (TemplateService) getWebApplicationContext().getBean("templateService");
		this.definitionService = (DefinitionService) getWebApplicationContext().getBean("definitionService");
		this.folderService = (FolderService) getWebApplicationContext().getBean("folderService");
		this.searchService = (SearchService) getWebApplicationContext().getBean("searchService");
		this.icalService = (IcalService) getWebApplicationContext().getBean("icalService");
		this.profileService = (ProfileService) getWebApplicationContext().getBean("profileService");
		this.binderService = (BinderService) getWebApplicationContext().getBean("binderService");
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.definition#getDefinitionAsXML}.
	 */
	public String getDefinitionAsXML(String definitionId) {
		return this.definitionService.definition_getDefinitionAsXML(null, definitionId);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.definition#getDefinitionConfigAsXML}.
	 */
	public String getDefinitionConfigAsXML() {
		return ((DefinitionServiceInternal) this.definitionService).definition_getDefinitionConfigAsXML(null);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.definition#getDefinitionListAsXML}.
	 */
	public String getDefinitionListAsXML() {
		return ((DefinitionServiceInternal)this.definitionService).definition_getDefinitionsAsXML(null);

	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.binder#getDefinitions}.
	 */
	public void setDefinitions(long binderId, String[] definitionIds, String[] workflowAssociations) {
		this.binderService.binder_setDefinitions(null, binderId, definitionIds, workflowAssociations);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.binder#setFunctionMembership}.
	 */
	public void setFunctionMembership(long binderId, String inputDataAsXml) {
		((BinderServiceInternal) this.binderService).binder_setFunctionMembershipWithXML(null, binderId, inputDataAsXml);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.binder#setFunctionMembershipInherited}.
	 */
	public void setFunctionMembershipInherited(long binderId, boolean inherit) {
		this.binderService.binder_setFunctionMembershipInherited(null, binderId, inherit);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.binder#setOwner}.
	 */
	public void setOwner(long binderId, long userId) {
		this.binderService.binder_setOwner(null, binderId, userId);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.module.admin.remoting.ws#addBinder}.
	 */
	public long addFolder(long parentBinderId, long binderConfigId, String title) {
		return this.templateService.template_addBinder(null, parentBinderId, binderConfigId, title);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.folder#getFolderEntriesAsXML}.
	 */
	public String getFolderEntriesAsXML(long binderId) {
		return ((FolderServiceInternal) this.folderService).folder_getEntriesAsXML(null, binderId);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.folder#getFolderEntriesAsXML}.
	 */
	public String getFolderEntryAsXML(long binderId, long entryId, boolean includeAttachments) {
		return ((FolderServiceInternal) this.folderService).folder_getEntryAsXML(null, binderId, entryId, includeAttachments);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.folder#addFolderEntry}.
	 */
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return ((FolderServiceInternal) this.folderService).folder_addEntryWithXML(null, binderId, definitionId, inputDataAsXML, attachedFileName);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.folder#addReply}.
	 */
	public long addReply(long binderId, long parentEntryId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return ((FolderServiceInternal) this.folderService).folder_addReplyWithXML(null, binderId, parentEntryId, definitionId, inputDataAsXML, attachedFileName);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.folder#modifyFolderEntry}.
	 */
	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		((FolderServiceInternal) this.folderService).folder_modifyEntryWithXML(null, binderId, entryId, inputDataAsXML);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.folder#uploadFolderFile}.
	 */
	public void synchronizeMirroredFolder(long binderId) {
		this.folderService.folder_synchronizeMirroredFolder(null, binderId);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.folder#uploadFolderFile}.
	 */
	public void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName) {
		((FolderServiceInternal)this.folderService).folder_uploadFile(null, binderId, entryId, fileUploadDataItemName, fileName);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.ical#uploadCalendarEntries}.
	 */
	public void uploadCalendarEntries(long folderId, String iCalDataAsXML)
	{
		this.icalService.ical_uploadCalendarEntriesWithXML(null, folderId, iCalDataAsXML);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.search#search}.
	 */
	public String search(String query, int offset, int maxResults)
	{
		return this.searchService.search_search(null, query, offset, maxResults);
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
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.profile#getAllPrincipalsAsXML}.
	 */
	public String getAllPrincipalsAsXML(int firstRecord, int maxRecords) {
		return ((ProfileServiceInternal)this.profileService).profile_getPrincipalsAsXML(null, firstRecord, maxRecords);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.profile#getPrincipalAsXML}.
	 */
	public String getPrincipalAsXML(long binderId, long principalId) {
		return ((ProfileServiceInternal)this.profileService).profile_getPrincipalAsXML(null, binderId, principalId);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.profile#addUserWorkspace}.
	 */
	public long addUserWorkspace(long userId) {
		return this.profileService.profile_addUserWorkspace(null, userId);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.profile#addGroupMember}.
	 */
	public void addGroupMember(String groupName, String memberName) {
		this.profileService.profile_addGroupMember(null, groupName, memberName);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.profile#removeGroupMember}.
	 */
	public void removeGroupMember(String groupName, String memberName) {
		this.profileService.profile_removeGroupMember(null, groupName, memberName);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.profile#getGroupMembersAsXML}.
	 */
	public String getGroupMembersAsXML(String groupName) {
		return ((ProfileServiceInternal)this.profileService).profile_getGroupMembersAsXML(null, groupName);
	}
	
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.search#getWorkspaceTreeAsXML}.
	 */
	public String getWorkspaceTreeAsXML(long binderId, int levels, String page) {
		return this.searchService.search_getWorkspaceTreeAsXML(null, binderId, levels, page);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.binder#getTeamMembersAsXML}.
	 */
	public String getTeamMembersAsXML(long binderId) {
		return ((BinderServiceInternal) this.binderService).binder_getTeamMembersAsXML(null, binderId);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.search#getTeamsAsXML}.
	 */
	public String getTeamsAsXML() {
		return ((SearchServiceInternal) this.searchService).search_getTeamsAsXML(null);
	}
	
	public void setTeamMembers(long binderId, String[] memberNames) {
		this.binderService.binder_setTeamMembers(null, binderId, memberNames);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.zone#deleteZoneUnderPortal}.
	 */
	public void indexFolder(long folderId) {
		this.binderService.binder_indexBinder(null, folderId);
	}
	//Migration services from sitescape forum
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.folder.migration#addBinder}.
	 */
	public long migrateBinder(long parentId, String definitionId, String inputDataAsXML,
			String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return this.migrationService.migration_addBinderWithXML(null, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.migration#addFolderEntry}.
	 */
	public long migrateFolderEntry(long binderId, String definitionId, String inputDataAsXML, 
							   String creator, Calendar creationDate, String modifier, Calendar modificationDate,
							   boolean subscribe) {
		return this.migrationService.migration_addFolderEntryWithXML(null, binderId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate,
				subscribe);
	}
		
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.migration#addReply}.
	 */
	public long migrateReply(long binderId, long parentId, String definitionId,
					     String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return this.migrationService.migration_addReplyWithXML(null, binderId, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.migration#uploadFolderFile}.
	 */
	public void migrateFolderFile(long binderId, long entryId, String fileUploadDataItemName,
								 String fileName, String modifier, Calendar modificationDate) {
		this.migrationService.migration_uploadFolderFile(null, binderId, entryId, fileUploadDataItemName, fileName, modifier,  modificationDate);
	}
	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.migration#uploadFolderFileStaged}.
	 */
	public void migrateFolderFileStaged(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate) {
		this.migrationService.migration_uploadFolderFileStaged(null, binderId, entryId, fileUploadDataItemName, fileName, stagedFileRelativePath, modifier,  modificationDate);
	}

	/**
	 * @deprecated As of ICEcore version 1.1,
	 * replaced by {@link org.kablink.teaming.remoting.ws.service.migration#addEntryWorkflow}.
	 */
	public void migrateEntryWorkflow(long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate) {
		this.migrationService.migration_addEntryWorkflow(null, binderId, entryId, definitionId, startState, modifier, modificationDate);
	}

}
