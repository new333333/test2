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

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import com.sitescape.team.remoting.ws.model.FolderEntry;
import com.sitescape.team.remoting.ws.service.binder.BinderService;
import com.sitescape.team.remoting.ws.service.definition.DefinitionService;
import com.sitescape.team.remoting.ws.service.folder.FolderService;
import com.sitescape.team.remoting.ws.service.folder.MigrationService;
import com.sitescape.team.remoting.ws.service.ical.IcalService;
import com.sitescape.team.remoting.ws.service.ldap.LdapService;
import com.sitescape.team.remoting.ws.service.license.LicenseService;
import com.sitescape.team.remoting.ws.service.profile.ProfileService;
import com.sitescape.team.remoting.ws.service.search.SearchService;
import com.sitescape.team.remoting.ws.service.template.TemplateService;
import com.sitescape.team.remoting.ws.service.zone.ZoneService;
import com.sitescape.team.util.SpringContextUtil;

public class TeamingServiceEndpoint implements ServiceLifecycle, 
		BinderService,
		DefinitionService, 
		FolderService, 
		IcalService, 
		LdapService,
		LicenseService, 
		ProfileService, 
		SearchService, 
		TemplateService,
		ZoneService {
		
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
	
	public long binder_addBinder(String accessToken, long parentId, String definitionId, String inputDataAsXML) {
		return getBinderService().binder_addBinder(accessToken, parentId, definitionId, inputDataAsXML);
	}
	public String binder_getTeamMembersAsXML(String accessToken, long binderId) {
		return getBinderService().binder_getTeamMembersAsXML(accessToken, binderId);
	}
	public void binder_indexBinder(String accessToken, long binderId) {
		getBinderService().binder_indexBinder(accessToken, binderId);
	}
	public void binder_setDefinitions(String accessToken, long binderId, String[] definitionIds, String[] workflowAssociations) {
		getBinderService().binder_setDefinitions(accessToken, binderId, definitionIds, workflowAssociations);
	}
	public void binder_setFunctionMembership(String accessToken, long binderId, String inputDataAsXml) {
		getBinderService().binder_setFunctionMembership(accessToken, binderId, inputDataAsXml);
	}
	public void binder_setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit) {
		getBinderService().binder_setFunctionMembershipInherited(accessToken, binderId, inherit);
	}
	public void binder_setOwner(String accessToken, long binderId, long userId) {
		getBinderService().binder_setOwner(accessToken, binderId, userId);
	}
	public void binder_setTeamMembers(String accessToken, long binderId, String[] memberNames) {
		getBinderService().binder_setTeamMembers(accessToken, binderId, memberNames);
	}
	public String definition_getDefinitionAsXML(String accessToken, String definitionId) {
		return getDefinitionService().definition_getDefinitionAsXML(accessToken, definitionId);
	}
	public String definition_getDefinitionConfigAsXML(String accessToken) {
		return getDefinitionService().definition_getDefinitionConfigAsXML(accessToken);
	}
	public String definition_getDefinitionListAsXML(String accessToken) {
		return getDefinitionService().definition_getDefinitionListAsXML(accessToken);
	}
	public void folder_addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId) {
		getFolderService().folder_addEntryWorkflow(accessToken, binderId, entryId, definitionId);
	}
	public long folder_addFolderEntry(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return getFolderService().folder_addFolderEntry(accessToken, binderId, definitionId, inputDataAsXML, attachedFileName);
	}
	public long folder_addReply(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return getFolderService().folder_addReply(accessToken, binderId, parentId, definitionId, inputDataAsXML, attachedFileName);
	}
	public String folder_getFolderEntriesAsXML(String accessToken, long binderId) {
		return getFolderService().folder_getFolderEntriesAsXML(accessToken, binderId);
	}
	public String folder_getFolderEntryAsXML(String accessToken, long binderId, long entryId, boolean includeAttachments) {
		return getFolderService().folder_getFolderEntryAsXML(accessToken, binderId, entryId, includeAttachments);
	}
	public void folder_modifyFolderEntry(String accessToken, long binderId, long entryId, String inputDataAsXML) {
		getFolderService().folder_modifyFolderEntry(accessToken, binderId, entryId, inputDataAsXML);
	}
	public void folder_synchronizeMirroredFolder(String accessToken, long binderId) {
		getFolderService().folder_synchronizeMirroredFolder(accessToken, binderId);
	}
	public void folder_uploadFolderFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName) {
		getFolderService().folder_uploadFolderFile(accessToken, binderId, entryId, fileUploadDataItemName, fileName);
	}
	public void folder_uploadFolderFileStaged(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName, String stagedFileRelativePath) {
		getFolderService().folder_uploadFolderFileStaged(accessToken, binderId, entryId, fileUploadDataItemName, fileName, stagedFileRelativePath);
	}
	public void ical_uploadCalendarEntries(String accessToken, long folderId, String iCalDataAsXML) {
		getIcalService().ical_uploadCalendarEntries(accessToken, folderId, iCalDataAsXML);
	}
	public void ldap_syncAll(String accessToken) {
		getLdapService().ldap_syncAll(accessToken);
	}
	public void ldap_syncUser(String accessToken, Long userId) {
		getLdapService().ldap_syncUser(accessToken, userId);
	}
	public long license_getExternalUsers(String accessToken) {
		return getLicenseService().license_getExternalUsers(accessToken);
	}
	public long license_getRegisteredUsers(String accessToken) {
		return getLicenseService().license_getRegisteredUsers(accessToken);
	}
	public void license_updateLicense(String accessToken) {
		getLicenseService().license_updateLicense(accessToken);
	}
	public long migration_addBinder(String accessToken, long parentId, String definitionId, String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return getMigrationService().migration_addBinder(accessToken, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	public void migration_addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId, String startState, String modifier, Calendar modificationDate) {
		getMigrationService().migration_addEntryWorkflow(accessToken, binderId, entryId, definitionId, startState, modifier, modificationDate);
	}
	public long migration_addFolderEntry(String accessToken, long binderId, String definitionId, String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return getMigrationService().migration_addFolderEntry(accessToken, binderId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	public long migration_addReply(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML, String creator, Calendar creationDate, String modifier, Calendar modificationDate) {
		return getMigrationService().migration_addReply(accessToken, binderId, parentId, definitionId, inputDataAsXML, creator, creationDate, modifier, modificationDate);
	}
	public void migration_uploadFolderFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName, String modifier, Calendar modificationDate) {
		getMigrationService().migration_uploadFolderFile(accessToken, binderId, entryId, fileUploadDataItemName, fileName, modifier, modificationDate);
	}
	public void migration_uploadFolderFileStaged(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName, String stagedFileRelativePath, String modifier, Calendar modificationDate) {
		getMigrationService().migration_uploadFolderFileStaged(accessToken, binderId, entryId, fileUploadDataItemName, fileName, stagedFileRelativePath, modifier, modificationDate);
	}
	public long profile_addGroup(String accessToken, long binderId, String definitionId, String inputDataAsXML) {
		return getProfileService().profile_addGroup(accessToken, binderId, definitionId, inputDataAsXML);
	}
	public long profile_addUser(String accessToken, long binderId, String definitionId, String inputDataAsXML) {
		return getProfileService().profile_addUser(accessToken, binderId, definitionId, inputDataAsXML);
	}
	public void profile_addUserToGroup(String accessToken, long userId, String username, long groupId) {
		getProfileService().profile_addUserToGroup(accessToken, userId, username, groupId);
	}
	public long profile_addUserWorkspace(String accessToken, long userId) {
		return getProfileService().profile_addUserWorkspace(accessToken, userId);
	}
	public void profile_deletePrincipal(String accessToken, long binderId, long principalId) {
		getProfileService().profile_deletePrincipal(accessToken, binderId, principalId);
	}
	public String profile_getAllPrincipalsAsXML(String accessToken, int firstRecord, int maxRecords) {
		return getProfileService().profile_getAllPrincipalsAsXML(accessToken, firstRecord, maxRecords);
	}
	public String profile_getPrincipalAsXML(String accessToken, long binderId, long principalId) {
		return getProfileService().profile_getPrincipalAsXML(accessToken, binderId, principalId);
	}
	public void profile_modifyPrincipal(String accessToken, long binderId, long principalId, String inputDataAsXML) {
		getProfileService().profile_modifyPrincipal(accessToken, binderId, principalId, inputDataAsXML);
	}
	public String search_getHotContent(String accessToken, String limitType, Long binderId) {
		return getSearchService().search_getHotContent(accessToken, limitType, binderId);
	}
	public String search_getTeamsAsXML(String accessToken) {
		return getSearchService().search_getTeamsAsXML(accessToken);
	}
	public String search_getWorkspaceTreeAsXML(String accessToken, long binderId, int levels, String page) {
		return getSearchService().search_getWorkspaceTreeAsXML(accessToken, binderId, levels, page);
	}
	public String search_search(String accessToken, String query, int offset, int maxResults) {
		return getSearchService().search_search(accessToken, query, offset, maxResults);
	}
	public long template_addBinder(String accessToken, long parentBinderId, long binderConfigId, String title) {
		return getTemplateService().template_addBinder(accessToken, parentBinderId, binderConfigId, title);
	}
	public String template_getTemplateListAsXML(String accessToken) {
		return getTemplateService().template_getTemplateListAsXML(accessToken);
	}
	public void zone_addZoneUnderPortal(String accessToken, String zoneName, String virtualHost, String mailDomain) {
		getZoneService().zone_addZoneUnderPortal(accessToken, zoneName, virtualHost, mailDomain);
	}
	public void zone_deleteZoneUnderPortal(String accessToken, String zoneName) {
		getZoneService().zone_deleteZoneUnderPortal(accessToken, zoneName);
	}
	public void zone_modifyZoneUnderPortal(String accessToken, String zoneName, String virtualHost, String mailDomain) {
		getZoneService().zone_modifyZoneUnderPortal(accessToken, zoneName, virtualHost, mailDomain);
	}


	public FolderEntry folder_getFolderEntry(String accessToken, long binderId, long entryId, boolean includeAttachments) {
		return getFolderService().folder_getFolderEntry(accessToken, binderId, entryId, includeAttachments);
	}

}
