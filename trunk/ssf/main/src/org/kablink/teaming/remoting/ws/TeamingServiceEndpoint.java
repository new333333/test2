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

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import org.kablink.teaming.remoting.ws.model.Binder;
import org.kablink.teaming.remoting.ws.model.DefinitionBrief;
import org.kablink.teaming.remoting.ws.model.DefinitionCollection;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.FolderCollection;
import org.kablink.teaming.remoting.ws.model.FolderEntry;
import org.kablink.teaming.remoting.ws.model.FolderEntryCollection;
import org.kablink.teaming.remoting.ws.model.FunctionMembership;
import org.kablink.teaming.remoting.ws.model.Group;
import org.kablink.teaming.remoting.ws.model.PrincipalCollection;
import org.kablink.teaming.remoting.ws.model.Subscription;
import org.kablink.teaming.remoting.ws.model.Tag;
import org.kablink.teaming.remoting.ws.model.TeamCollection;
import org.kablink.teaming.remoting.ws.model.TeamMemberCollection;
import org.kablink.teaming.remoting.ws.model.TemplateCollection;
import org.kablink.teaming.remoting.ws.model.User;
import org.kablink.teaming.remoting.ws.model.UserCollection;
import org.kablink.teaming.remoting.ws.service.admin.AdminService;
import org.kablink.teaming.remoting.ws.service.binder.BinderService;
import org.kablink.teaming.remoting.ws.service.definition.DefinitionService;
import org.kablink.teaming.remoting.ws.service.folder.FolderService;
import org.kablink.teaming.remoting.ws.service.folder.MigrationService;
import org.kablink.teaming.remoting.ws.service.ical.IcalService;
import org.kablink.teaming.remoting.ws.service.ldap.LdapService;
import org.kablink.teaming.remoting.ws.service.license.LicenseService;
import org.kablink.teaming.remoting.ws.service.profile.ProfileService;
import org.kablink.teaming.remoting.ws.service.search.SearchService;
import org.kablink.teaming.remoting.ws.service.template.TemplateService;
import org.kablink.teaming.remoting.ws.service.zone.ZoneService;
import org.kablink.teaming.util.SpringContextUtil;


public class TeamingServiceEndpoint implements ServiceLifecycle, 
		BinderService,
		DefinitionService, 
		FolderService, 
		MigrationService,
		IcalService, 
		LdapService,
		LicenseService, 
		ProfileService, 
		SearchService, 
		TemplateService,
		ZoneService,
		AdminService
		{
		
	public void init(Object context) throws ServiceException {
	}

	public void destroy() {
	}

	protected BinderService getBinderService() {
		return (BinderService) SpringContextUtil.getBean("binderService");
	}

	protected DefinitionService getDefinitionService() {
		return (DefinitionService) SpringContextUtil.getBean("definitionService");
	}

	protected FolderService getFolderService() {
		return (FolderService) SpringContextUtil.getBean("folderService");
	}

	protected IcalService getIcalService() {
		return (IcalService) SpringContextUtil.getBean("icalService");
	}

	protected LdapService getLdapService() {
		return (LdapService) SpringContextUtil.getBean("ldapService");
	}

	protected LicenseService getLicenseService() {
		return (LicenseService) SpringContextUtil.getBean("licenseService");
	}

	protected MigrationService getMigrationService() {
		return (MigrationService) SpringContextUtil.getBean("migrationService");
	}

	protected ProfileService getProfileService() {
		return (ProfileService) SpringContextUtil.getBean("profileService");
	}

	protected SearchService getSearchService() {
		return (SearchService) SpringContextUtil.getBean("searchService");
	}

	protected TemplateService getTemplateService() {
		return (TemplateService) SpringContextUtil.getBean("templateService");
	}

	protected ZoneService getZoneService() {
		return (ZoneService) SpringContextUtil.getBean("zoneService");
	}
	
	protected AdminService getAdminService() {
		return (AdminService) SpringContextUtil.getBean("adminService");
	}
	
	/// Binder Service
	
	public long binder_addBinder(String accessToken, Binder binder) {
		return getBinderService().binder_addBinder(accessToken, binder);
	}
	public long binder_copyBinder(String accessToken, long sourceId, long destinationId, boolean cascade) {
		return getBinderService().binder_copyBinder(accessToken, sourceId, destinationId, cascade);
	}
	public void binder_deleteBinder(String accessToken, long binderId, boolean deleteMirroredSource) {
		getBinderService().binder_deleteBinder(accessToken, binderId, deleteMirroredSource);
	}
	public Binder binder_getBinder(String accessToken, long binderId, boolean includeAttachments) {
		return getBinderService().binder_getBinder(accessToken, binderId, includeAttachments);
	}
	public Binder binder_getBinderByPathName(String accessToken, String pathName, boolean includeAttachments) {
		return getBinderService().binder_getBinderByPathName(accessToken, pathName, includeAttachments);
	}
	public void binder_moveBinder(String accessToken, long binderId, long destinationId) {
		getBinderService().binder_moveBinder(accessToken, binderId, destinationId);
	}
	public void binder_modifyBinder(String accessToken, Binder binder) {
		getBinderService().binder_modifyBinder(accessToken, binder);
	}
	public void binder_removeFile(String accessToken, long binderId, String fileName) {
		getBinderService().binder_removeFile(accessToken, binderId, fileName);
	}
	public FileVersions binder_getFileVersions(String accessToken, long binderId, String fileName) {
		return getBinderService().binder_getFileVersions(accessToken, binderId, fileName);
	}
	
	public void binder_uploadFile(String accessToken, long binderId, String fileUploadDataItemName, String fileName) {
		getBinderService().binder_uploadFile(accessToken, binderId, fileUploadDataItemName, fileName);
	}
	

	/*
	public long binder_addBinderWithXML(String accessToken, long parentId, String definitionId, String inputDataAsXML) {
		return getBinderService().binder_addBinderWithXML(accessToken, parentId, definitionId, inputDataAsXML);
	}
	public String binder_getTeamMembersAsXML(String accessToken, long binderId) {
		return getBinderService().binder_getTeamMembersAsXML(accessToken, binderId);
	}*/
	public void binder_indexBinder(String accessToken, long binderId) {
		getBinderService().binder_indexBinder(accessToken, binderId);
	}
    public Long[] binder_indexTree(String accessToken, long binderId) {
    	return getBinderService().binder_indexTree(accessToken, binderId);
    }
	public void binder_setDefinitions(String accessToken, long binderId, String[] definitionIds, String[] workflowAssociations) {
		getBinderService().binder_setDefinitions(accessToken, binderId, definitionIds, workflowAssociations);
	}
	public void binder_setFunctionMembership(String accessToken, long binderId, FunctionMembership[] functionMemberships) {
		getBinderService().binder_setFunctionMembership(accessToken, binderId, functionMemberships);
	}
	/*
	public void binder_setFunctionMembershipWithXML(String accessToken, long binderId, String inputDataAsXml) {
		getBinderService().binder_setFunctionMembershipWithXML(accessToken, binderId, inputDataAsXml);
	}*/
	public void binder_setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit) {
		getBinderService().binder_setFunctionMembershipInherited(accessToken, binderId, inherit);
	}
	public void binder_setOwner(String accessToken, long binderId, long userId) {
		getBinderService().binder_setOwner(accessToken, binderId, userId);
	}
	public TeamMemberCollection binder_getTeamMembers(String accessToken, long binderId) {
		return getBinderService().binder_getTeamMembers(accessToken, binderId);
	}
	public void binder_setTeamMembers(String accessToken, long binderId, String[] memberNames) {
		getBinderService().binder_setTeamMembers(accessToken, binderId, memberNames);
	}
	public Subscription binder_getSubscription(String accessToken, long binderId) {
		return getBinderService().binder_getSubscription(accessToken, binderId);
		
	}
	public void binder_setSubscription(String accessToken, long binderId, Subscription subscription) {
		getBinderService().binder_setSubscription(accessToken, binderId, subscription);
	}

	public FolderCollection binder_getFolders(String accessToken, long binderId) {
		return getBinderService().binder_getFolders(accessToken, binderId);
	}
	public void binder_deleteTag(String accessToken, long binderId, String tagId) {
		getBinderService().binder_deleteTag(accessToken, binderId, tagId);
	}
	public Tag[] binder_getTags(String accessToken, long binderId) {
		return getBinderService().binder_getTags(accessToken, binderId);
	}

	public void binder_setTag(String accessToken, Tag tag) {
		getBinderService().binder_setTag(accessToken, tag);
	}
		
	/// Folder Service
	
	public FolderEntry folder_getEntry(String accessToken, long entryId, boolean includeAttachments) {
		return getFolderService().folder_getEntry(accessToken, entryId, includeAttachments);
	}

	public FolderEntryCollection folder_getEntries(String accessToken, long binderId) {
		return getFolderService().folder_getEntries(accessToken, binderId);
	}

	public FolderEntry folder_getEntryByFileName(String accessToken, long binderId, String fileName, boolean includeAttachments) {
		return getFolderService().folder_getEntryByFileName(accessToken, binderId, fileName, includeAttachments);
	}
	public void folder_addEntryWorkflow(String accessToken, long entryId, String definitionId) {
		getFolderService().folder_addEntryWorkflow(accessToken, entryId, definitionId);
	}
    public void folder_deleteEntryWorkflow(String accessToken, long entryId, String definitionId) {
    	getFolderService().folder_deleteEntryWorkflow(accessToken, entryId, definitionId);
    }
	/*
	public long folder_addEntryWithXML(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return getFolderService().folder_addEntryWithXML(accessToken, binderId, definitionId, inputDataAsXML, attachedFileName);
	}
	public long folder_addReplyWithXML(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return getFolderService().folder_addReplyWithXML(accessToken, binderId, parentId, definitionId, inputDataAsXML, attachedFileName);
	}*/
	public void folder_modifyWorkflowState(String accessToken, long entryId, long stateId, String toState) {
		getFolderService().folder_modifyWorkflowState(accessToken, entryId, stateId, toState);
	}
	public void folder_setWorkflowResponse(String accessToken, long entryId, long stateId, String question, String response) {
		getFolderService().folder_setWorkflowResponse(accessToken,  entryId, stateId, question, response);
	}
	/*
	public String folder_getEntriesAsXML(String accessToken, long binderId) {
		return getFolderService().folder_getEntriesAsXML(accessToken, binderId);
	}
	public String folder_getEntryAsXML(String accessToken, long binderId, long entryId, boolean includeAttachments) {
		return getFolderService().folder_getEntryAsXML(accessToken, binderId, entryId, includeAttachments);
	}
	public void folder_modifyEntryWithXML(String accessToken, long binderId, long entryId, String inputDataAsXML) {
		getFolderService().folder_modifyEntryWithXML(accessToken, binderId, entryId, inputDataAsXML);
	}*/
	public void folder_synchronizeMirroredFolder(String accessToken, long binderId) {
		getFolderService().folder_synchronizeMirroredFolder(accessToken, binderId);
	}
	public void folder_removeFile(String accessToken, long entryId, String fileName) {
		getFolderService().folder_removeFile(accessToken, entryId, fileName);
	}
	public FileVersions folder_getFileVersions(String accessToken, long entryId, String fileName) {
		return getFolderService().folder_getFileVersions(accessToken, entryId, fileName);
	}
	public void folder_uploadFile(String accessToken,long entryId, String fileUploadDataItemName, String fileName) {
		getFolderService().folder_uploadFile(accessToken, entryId, fileUploadDataItemName, fileName);
	}
	public void folder_uploadFileStaged(String accessToken, long entryId, String fileUploadDataItemName, String fileName, String stagedFileRelativePath) {
		getFolderService().folder_uploadFileStaged(accessToken, entryId, fileUploadDataItemName, fileName, stagedFileRelativePath);
	}
	public void folder_deleteEntry(String accessToken, long entryId) {
		getFolderService().folder_deleteEntry(accessToken, entryId);
	}
    public long folder_copyEntry(String accessToken, long entryId, long destinationId) {
    	return getFolderService().folder_copyEntry(accessToken, entryId, destinationId);
    }
    public void folder_moveEntry(String accessToken, long entryId, long destinationId) {
       	getFolderService().folder_moveEntry(accessToken, entryId, destinationId);
    }
    public void folder_reserveEntry(String accessToken, long entryId) {
       	getFolderService().folder_reserveEntry(accessToken, entryId);
    }
    public void folder_unreserveEntry(String accessToken, long entryId) {
       	getFolderService().folder_unreserveEntry(accessToken, entryId);
    }
	public Subscription folder_getSubscription(String accessToken, long entryId) {
		return getFolderService().folder_getSubscription(accessToken, entryId);
	}
	public void folder_setSubscription(String accessToken, long entryId, Subscription subscription) {
		getFolderService().folder_setSubscription(accessToken, entryId, subscription);
	}
	public void folder_deleteEntryTag(String accessToken, long entryId, String tagId) {
		getFolderService().folder_deleteEntryTag(accessToken, entryId, tagId);
	}
	public Tag[] folder_getEntryTags(String accessToken, long entryId) {
		return getFolderService().folder_getEntryTags(accessToken, entryId);
	}
	public void folder_setEntryTag(String accessToken, Tag tag) {
		getFolderService().folder_setEntryTag(accessToken, tag);
	}
	public void folder_setRating(String accessToken, long entryId, long value) {
		getFolderService().folder_setRating(accessToken, entryId, value);
	}
	public long folder_addEntry(String accessToken, FolderEntry entry, String attachedFileName) {
		return getFolderService().folder_addEntry(accessToken, entry, attachedFileName);
	}
	
	public long folder_addReply(String accessToken, long parentEntryId, FolderEntry reply, String attachedFileName) {
		return getFolderService().folder_addReply(accessToken, parentEntryId, reply, attachedFileName);
	}

	public void folder_modifyEntry(String accessToken, FolderEntry entry) {
		getFolderService().folder_modifyEntry(accessToken, entry);
	}

	/// Ical Service
	
	public void ical_uploadCalendarEntriesWithXML(String accessToken, long folderId, String iCalDataAsXML) {
		getIcalService().ical_uploadCalendarEntriesWithXML(accessToken, folderId, iCalDataAsXML);
	}
	
	/// LDAP Service
	
	public void ldap_syncAll(String accessToken) {
		getLdapService().ldap_syncAll(accessToken);
	}
	public void ldap_syncUser(String accessToken, Long userId) {
		getLdapService().ldap_syncUser(accessToken, userId);
	}
	
	/// License Service
	
	public long license_getExternalUsers(String accessToken) {
		return getLicenseService().license_getExternalUsers(accessToken);
	}
	public long license_getRegisteredUsers(String accessToken) {
		return getLicenseService().license_getRegisteredUsers(accessToken);
	}
	public void license_updateLicense(String accessToken) {
		getLicenseService().license_updateLicense(accessToken);
	}
	
	/// Migration Service
	
	public long migration_addBinderWithXML(String accessToken, long parentId, String definitionId, String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return getMigrationService().migration_addBinderWithXML(accessToken, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	public void migration_addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate) {
		getMigrationService().migration_addEntryWorkflow(accessToken, binderId, entryId, definitionId, startState, modifier, modificationDate);
	}
	public long migration_addFolderEntryWithXML(String accessToken, long binderId, String definitionId, String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate, boolean subscribe) {
		return getMigrationService().migration_addFolderEntryWithXML(accessToken, binderId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate, subscribe);
	}
	public long migration_addReplyWithXML(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return getMigrationService().migration_addReplyWithXML(accessToken, binderId, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	public void migration_uploadFolderFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName, String modifier, Calendar modificationDate) {
		getMigrationService().migration_uploadFolderFile(accessToken, binderId, entryId, fileUploadDataItemName, fileName, modifier, modificationDate);
	}
	public void migration_uploadFolderFileStaged(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate) {
		getMigrationService().migration_uploadFolderFileStaged(accessToken, binderId, entryId, fileUploadDataItemName, fileName, stagedFileRelativePath, modifier, modificationDate);
	}
	public long migration_addBinder(String accessToken, Binder binder) {
		return getMigrationService().migration_addBinder(accessToken, binder);
	}
	
	public long migration_addFolderEntry(String accessToken, FolderEntry entry, boolean subscribe) {
		return getMigrationService().migration_addFolderEntry(accessToken, entry, subscribe);
	}
	
	public long migration_addReply(String accessToken, long parentEntryId, FolderEntry reply) {
		return getMigrationService().migration_addReply(accessToken, parentEntryId, reply);
	}
	
	/// Profile Service
	
	public void profile_addGroupMember(String accessToken, String groupName, String userName) {
		getProfileService().profile_addGroupMember(accessToken, groupName, userName);
	}
	public void profile_removeGroupMember(String accessToken, String groupName, String userName) {
		getProfileService().profile_removeGroupMember(accessToken, groupName, userName);
	}
	public PrincipalCollection profile_getGroupMembers(String accessToken, String groupName) {
		return getProfileService().profile_getGroupMembers(accessToken, groupName);
	}

	public long profile_addUserWorkspace(String accessToken, long userId) {
		return getProfileService().profile_addUserWorkspace(accessToken, userId);
	}
	public void profile_deletePrincipal(String accessToken, long principalId, boolean deleteWorkspace) {
		getProfileService().profile_deletePrincipal(accessToken, principalId, deleteWorkspace);
	}
	public void profile_removeFile(String accessToken, long principalId, String fileName) {
		getProfileService().profile_removeFile(accessToken, principalId, fileName);
	}
	public FileVersions profile_getFileVersions(String accessToken, long principalId, String fileName) {
		return getProfileService().profile_getFileVersions(accessToken, principalId, fileName);
	}
	public void profile_uploadFile(String accessToken, long principalId, String fileUploadDataItemName, String fileName) {
		getProfileService().profile_uploadFile(accessToken, principalId, fileUploadDataItemName, fileName);
	}

	/*
	public String profile_getPrincipalsAsXML(String accessToken, int firstRecord, int maxRecords) {
		return getProfileService().profile_getPrincipalsAsXML(accessToken, firstRecord, maxRecords);
	}
	public String profile_getPrincipalAsXML(String accessToken, long binderId, long principalId) {
		return getProfileService().profile_getPrincipalAsXML(accessToken, binderId, principalId);
	}
	*/
	public PrincipalCollection profile_getPrincipals(String accessToken, int firstRecord, int maxRecords) {
		return getProfileService().profile_getPrincipals(accessToken, firstRecord, maxRecords);
	}

	public User profile_getUser(String accessToken, long userId, boolean includeAttachments) {
		return getProfileService().profile_getUser(accessToken, userId, includeAttachments);
	}
	public User profile_getUserByName(String accessToken, String userName, boolean includeAttachments) {
		return getProfileService().profile_getUserByName(accessToken, userName, includeAttachments);
	}
	public UserCollection profile_getUsers(String accessToken, Boolean captive, int firstRecord, int maxRecords) {
		return getProfileService().profile_getUsers(accessToken, captive, firstRecord, maxRecords);
	}

	public Group profile_getGroup(String accessToken, long groupId, boolean includeAttachments) {
		return getProfileService().profile_getGroup(accessToken, groupId, includeAttachments);
	}
	public Group profile_getGroupByName(String accessToken, String groupName, boolean includeAttachments) {
		return getProfileService().profile_getGroupByName(accessToken, groupName, includeAttachments);
	}
	public long profile_addUser(String accessToken, User user) {
		return getProfileService().profile_addUser(accessToken, user);
	}
	
	public long profile_addGroup(String accessToken, Group group) {
		return getProfileService().profile_addGroup(accessToken, group);
	}
	
	public void profile_modifyUser(String accessToken, User user) {
		getProfileService().profile_modifyUser(accessToken, user);
	}

	public void profile_modifyGroup(String accessToken, Group group) {
		getProfileService().profile_modifyGroup(accessToken, group);
	}

	// Search Service
	
	public String search_getHotContent(String accessToken, String limitType, Long binderId) {
		return getSearchService().search_getHotContent(accessToken, limitType, binderId);
	}
	/*
	public String search_getTeamsAsXML(String accessToken) {
		return getSearchService().search_getTeamsAsXML(accessToken);
	}*/
	public String search_getWorkspaceTreeAsXML(String accessToken, long binderId, int levels, String page) {
		return getSearchService().search_getWorkspaceTreeAsXML(accessToken, binderId, levels, page);
	}
	public String search_search(String accessToken, String query, int offset, int maxResults) {
		return getSearchService().search_search(accessToken, query, offset, maxResults);
	}
	public TeamCollection search_getTeams(String accessToken) {
		return getSearchService().search_getTeams(accessToken);
	}
	public TeamCollection search_getUserTeams(String accessToken, long userId) {
		return getSearchService().search_getUserTeams(accessToken, userId);
	}
	public FolderEntryCollection search_getFolderEntries(String accessToken, String query, int offset, int maxResults) {
		return getSearchService().search_getFolderEntries(accessToken, query, offset, maxResults);
	}
	
	/// Template Service
	
	public long template_addBinder(String accessToken, long parentBinderId, long binderConfigId, String title) {
		return getTemplateService().template_addBinder(accessToken, parentBinderId, binderConfigId, title);
	}
	/*
	public String template_getTemplatesAsXML(String accessToken) {
		return getTemplateService().template_getTemplatesAsXML(accessToken);
	}*/
	public TemplateCollection template_getTemplates(String accessToken) {
		return getTemplateService().template_getTemplates(accessToken);
	}
		
	/// Zone Service
	
	public Long zone_addZone(String accessToken, String zoneName, String virtualHost, String mailDomain) {
		return getZoneService().zone_addZone(accessToken, zoneName, virtualHost, mailDomain);
	}
	public void zone_deleteZone(String accessToken, String zoneName) {
		getZoneService().zone_deleteZone(accessToken, zoneName);
	}
	public void zone_modifyZone(String accessToken, String zoneName, String virtualHost, String mailDomain) {
		getZoneService().zone_modifyZone(accessToken, zoneName, virtualHost, mailDomain);
	}

	/// Definition Service
	
	public String definition_getDefinitionAsXML(String accessToken, String definitionId) {
		return getDefinitionService().definition_getDefinitionAsXML(accessToken, definitionId);
	}
	/*
	public String definition_getDefinitionsAsXML(String accessToken) {
		return getDefinitionService().definition_getDefinitionsAsXML(accessToken);
	}*/
	public DefinitionCollection definition_getDefinitions(String accessToken) {
		return getDefinitionService().definition_getDefinitions(accessToken);
	}
	public DefinitionCollection definition_getLocalDefinitions(String accessToken, long binderId, boolean includeAncestors) {
		return getDefinitionService().definition_getLocalDefinitions(accessToken, binderId, includeAncestors);
	}
	public DefinitionBrief definition_getDefinitionByName(String accessToken, String name) {
		return getDefinitionService().definition_getDefinitionByName(accessToken, name);
	}
	public DefinitionBrief definition_getLocalDefinitionByName(String accessToken, long binderId, String name, boolean includeAncestors) {
		return getDefinitionService().definition_getLocalDefinitionByName(accessToken, binderId, name, includeAncestors);
	}

	public void admin_destroyApplicationScopedToken(String accessToken, String token) {
		getAdminService().admin_destroyApplicationScopedToken(accessToken, token);
	}
	public String admin_getApplicationScopedToken(String accessToken, long applicationId, long userId) {
		return getAdminService().admin_getApplicationScopedToken(accessToken, applicationId, userId);
	}	

}
