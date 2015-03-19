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
package org.kablink.teaming.remoting.ws.service.profile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFileByTheNameException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.ChainedInputData;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.remoting.ws.RemotingException;
import org.kablink.teaming.remoting.ws.model.BinderBrief;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.GroupBrief;
import org.kablink.teaming.remoting.ws.model.GroupCollection;
import org.kablink.teaming.remoting.ws.model.PrincipalBrief;
import org.kablink.teaming.remoting.ws.model.PrincipalCollection;
import org.kablink.teaming.remoting.ws.model.Timestamp;
import org.kablink.teaming.remoting.ws.model.UserBrief;
import org.kablink.teaming.remoting.ws.model.UserCollection;
import org.kablink.teaming.remoting.ws.util.ModelInputData;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.Validator;

public class ProfileServiceImpl extends BaseService implements ProfileService, ProfileServiceInternal {

	public String profile_getPrincipalsAsXML(String accessToken, int firstRecord, int maxRecords) {
		Document doc = DocumentHelper.createDocument();
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_OFFSET, new Integer(firstRecord));
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxRecords));
		Map results = getProfileModule().getPrincipals(options);
		List users = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
		Element rootElement = doc.addElement("principals");
		rootElement.addAttribute("first", ""+firstRecord);
		rootElement.addAttribute("count", ((Integer)results.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED)).toString());
		rootElement.addAttribute("total", ((Integer)results.get(ObjectKeys.SEARCH_COUNT_TOTAL)).toString());
		for(Object searchEntry : users) {
			Map user = (Map) searchEntry;
			addPrincipalToDocument(rootElement, user);
		}
		String xml = rootElement.asXML();
		
		return xml;
	}
	public String profile_getPrincipalAsXML(String accessToken, long binderId, long principalId) {
		Long pId = new Long(principalId);
		
		// Retrieve the raw entry.
		Principal entry = 
			getProfileModule().getEntry(pId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = addPrincipalToDocument(doc, entry);
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		return xml;
	}
	public String profile_getGroupMembersAsXML(String accessToken, String groupName) {
		Group group = getProfileModule().getGroup(groupName);
		Document doc = DocumentHelper.createDocument();
		Element team = doc.addElement("group");
		List members = group.getMembers();
		for (int i=0; i<members.size(); ++i) {
			addPrincipalToDocument(team, (Principal)members.get(i));
		}
			
		return doc.getRootElement().asXML();

	}
	
	public void profile_addGroupMember(String accessToken, String groupName, String username) {
		Group group = getProfileModule().getGroup(groupName);
		UserPrincipal member = (UserPrincipal)getProfileModule().getEntry(username);
		Map updates = new HashMap();
		List members = new ArrayList(group.getMembers());
		members.add(member);
		updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
		try {
			getProfileModule().modifyEntry(group.getId(), new MapInputData(updates));	
		}	catch(WriteFilesException e) {
			throw new RemotingException(e);
		}	catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}

	}
	public void profile_removeGroupMember(String accessToken, String groupName, String username) {
		Group group = getProfileModule().getGroup(groupName);
		UserPrincipal member = (UserPrincipal)getProfileModule().getEntry(username);
		Map updates = new HashMap();
		List members = new ArrayList(group.getMembers());
		members.remove(member);
		updates.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
		try {
			getProfileModule().modifyEntry(group.getId(), new MapInputData(updates));	
		}	catch(WriteFilesException e) {
			throw new RemotingException(e);
		}	catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
		
	}
	public PrincipalCollection profile_getGroupMembers(String accessToken, String groupName, int firstRecord, int maxRecords) {
		Group group = getProfileModule().getGroup(groupName);
		List members = group.getMembers();
		int length = members.size();
		if(maxRecords > 0)
			length = Math.min(length - firstRecord, maxRecords);
		if(length < 0)
			length = 0;
		PrincipalBrief[] principals = new PrincipalBrief[length];
		for(int i=0; i<length; ++i) {
			principals[i] = toPrincipalBrief((Principal)members.get(firstRecord + i));
		}		
		return new PrincipalCollection(firstRecord, members.size(), principals); 
	}
		
	public void profile_deletePrincipal(String accessToken, long principalId, boolean deleteWorkspace) {
		try {
			Map options = new HashMap();
			options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, Boolean.valueOf(deleteWorkspace));
			getProfileModule().deleteEntry(new Long(principalId), options);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}

	}
	public long profile_addUserWorkspace(String accessToken, long userId) {
		User user = (User)getProfileModule().getEntry(userId);
		return getProfileModule().addUserWorkspace(user, null).getId();
	}
	
	public PrincipalCollection profile_getPrincipals(String accessToken, int firstRecord, int maxRecords) {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_OFFSET, new Integer(firstRecord));
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxRecords));
		Map results = getProfileModule().getPrincipals(options);
		List users = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
		
		List<PrincipalBrief> principals = new ArrayList<PrincipalBrief>();
		for(Object searchEntry : users) {
			Map user = (Map) searchEntry;
			principals.add(toPrincipalBrief(user));
		}
		
		PrincipalBrief[] array = new PrincipalBrief[principals.size()];
		return new PrincipalCollection(firstRecord, 
				((Integer)results.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue(), 
				principals.toArray(array));
	}
		
	public org.kablink.teaming.remoting.ws.model.Group profile_getGroupByName(String accessToken, String groupName, boolean includeAttachments) {
		Group group = getProfileModule().getGroup(groupName);
		return profile_getGroup(accessToken, group.getId(), includeAttachments);
	}
	public org.kablink.teaming.remoting.ws.model.Group profile_getGroup(String accessToken, long groupId, boolean includeAttachments) {
		Long gId = Long.valueOf(groupId);
		
		// Retrieve the raw entry.
		Principal entry = 
			getProfileModule().getEntry(gId);

		if(!(entry instanceof Group))
			throw new IllegalArgumentException(gId + " does not represent a group. It is " + entry.getClass().getSimpleName());
		
		org.kablink.teaming.remoting.ws.model.Group groupModel = 
			new org.kablink.teaming.remoting.ws.model.Group();
		
		fillGroupModel(groupModel, (Group) entry);
		
		return groupModel;
	}
	public org.kablink.teaming.remoting.ws.model.User profile_getUserByName(String accessToken, String userName, boolean includeAttachments) {
		User user = null;
		if(SPropsUtil.getBoolean("profile.service.getuserbyname.legacy", false)) {
			user = getProfileModule().getUserDeadOrAlive(userName);
		}
		else {
			Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			// Read this user's ldap guid from the ldap directory.
			String ldapGuid = getLdapModule().readLdapGuidFromDirectory(userName,  zoneId);
			// Try to find the user in Teaming by their ldap guid.
			if(Validator.isNotNull(ldapGuid)) {
				try {
					// Try to find the user in Teaming by their ldap guid.
					user =  getProfileDao().findUserByLdapGuid(ldapGuid, zoneId);
				}
				catch(NoUserByTheNameException e) {
					// Nothing to do
				}
			}
			// Did we find the user by their ldap guid?
			if (user == null)
			{
				// No, try to find the user by their name.
				user = getProfileModule().getUserDeadOrAlive(userName);
			}
		}
		return profile_getUser(accessToken, user.getId(), includeAttachments);
	}
	public org.kablink.teaming.remoting.ws.model.User profile_getUser(String accessToken, long userId, boolean includeAttachments) {
		Long uId = Long.valueOf(userId);
		
		// Retrieve the raw entry.
		Principal entry = 
			getProfileModule().getEntry(uId);

		if(!(entry instanceof User))
			throw new IllegalArgumentException(uId + " does not represent an user. It is " + entry.getClass().getSimpleName());
		
		org.kablink.teaming.remoting.ws.model.User userModel = 
			new org.kablink.teaming.remoting.ws.model.User();
		
		fillUserModel(userModel, (User) entry);
		
		return userModel;
	}
	
	public org.kablink.teaming.remoting.ws.model.User[] profile_getUsersByEmail(String accessToken, String emailAddress, String emailType) {
		SortedSet<User> users = getProfileModule().getUsersByEmail(emailAddress, emailType);
		List<org.kablink.teaming.remoting.ws.model.User> list = new ArrayList();
		org.kablink.teaming.remoting.ws.model.User userModel;
		for(User user:users) {
			userModel = new org.kablink.teaming.remoting.ws.model.User();
			fillUserModel(userModel, user);
			list.add(userModel);
		}
		return list.toArray(new org.kablink.teaming.remoting.ws.model.User[0]);
	}
	
	public long profile_addGroup(String accessToken, org.kablink.teaming.remoting.ws.model.Group group) {
		try {
			return getProfileModule().addGroup(group.getDefinitionId(), new ModelInputData(group), null, null).getId().longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}	
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}
	
	public long profile_addUser(String accessToken, org.kablink.teaming.remoting.ws.model.User user, String password) {
		ChainedInputData inputData = new ChainedInputData();
		inputData.addAccessor(new ModelInputData(user));
		if(password != null) {
			Map passwordMap = new HashMap();
			passwordMap.put("password", password);
			inputData.addAccessor(new MapInputData(passwordMap));
		}
		try {
			return getProfileModule().addUser(user.getDefinitionId(), inputData, null, null).getId().longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}
	
	public void profile_modifyGroup(String accessToken, org.kablink.teaming.remoting.ws.model.Group group) {
		try {
			getProfileModule().modifyEntry(group.getId(), new ModelInputData(group));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}
	
	public void profile_modifyUser(String accessToken, org.kablink.teaming.remoting.ws.model.User user) {
		try {
			getProfileModule().modifyEntry(user.getId(), new ModelInputData(user));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}
	public void profile_uploadFile(String accessToken, long principalId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}
	public void profile_removeFile(String accessToken, long principalId, String fileName) {
		try {
			Principal entry = getProfileModule().getEntry(principalId);
			FileAttachment att = entry.getFileAttachment(fileName);
			if (att == null) return;
			List deletes = new ArrayList();
			deletes.add(att.getId());
			getProfileModule().modifyEntry(principalId, new EmptyInputData(), null, deletes, null, null);			
		}	catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}

	}
	
	public FileVersions profile_getFileVersions(String accessToken, long principalId, String fileName) {
		Principal entry = getProfileModule().getEntry(principalId);
		FileAttachment att = entry.getFileAttachment(fileName);
		if(att != null)
			return toFileVersions(att);
		else
			throw new NoFileByTheNameException(fileName);
	}
	
	public UserCollection profile_getUsers(String accessToken, Boolean captive, int firstRecord,
			int maxRecords) {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_OFFSET, new Integer(firstRecord));
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxRecords));
		Map results = getProfileModule().getUsers(options);
		List searchEntries = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
		
		List<UserBrief> users = new ArrayList<UserBrief>();
		for(Object searchEntry : searchEntries) {
			Map user = (Map) searchEntry;
			users.add(toUserBrief(user, captive));
		}
		
		UserBrief[] array = new UserBrief[users.size()];
		return new UserCollection(firstRecord, 
				((Integer)results.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue(), 
				users.toArray(array));
	}

	protected UserBrief toUserBrief(Map user, Boolean captive) {
		UserBrief userBrief = new UserBrief();
		setPrincipalBrief(user, userBrief);

		String permaLink = PermaLinkUtil.getPermalink(userBrief.getId(), EntityIdentifier.EntityType.user, captive);
		
		userBrief.setPermaLink(permaLink);
		
		return userBrief;
	}
	
	public void profile_changePassword(String accessToken, Long userId,
			String oldPassword, String newPassword) {
		getProfileModule().changePassword(userId, oldPassword, newPassword);
	}
	
	public byte[] profile_getAttachmentAsByteArray(String accessToken,
			long userId, String attachmentId) {
		Long uId = Long.valueOf(userId);
		
		Principal entry = getProfileModule().getEntry(uId);

		if(!(entry instanceof User))
			throw new IllegalArgumentException(uId + " does not represent an user. It is " + entry.getClass().getSimpleName());

		return getFileAttachmentAsByteArray(((User) entry).getParentBinder(), entry, attachmentId);
	}
	
	public void profile_uploadFileAsByteArray(String accessToken,
			long principalId, String fileUploadDataItemName, String fileName,
			byte[] fileContent) {
		if (Validator.isNull(fileUploadDataItemName) || "ss_attachFile".equals(fileUploadDataItemName))
			fileUploadDataItemName="ss_attachFile1";
		File originalFile = new File(fileName);
		fileName = originalFile.getName();

		Map fileItems = wrapFileItemInMap(fileUploadDataItemName, fileName, new ByteArrayInputStream(fileContent));
		
		try {
			getProfileModule().modifyEntry(principalId, new EmptyInputData(), fileItems, null, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	public BinderBrief[] profile_getFavorites(String accessToken) {
        List<Binder> binders = getProfileModule().getUserFavorites(null);
		List<BinderBrief> briefs = new ArrayList<BinderBrief>();
        for (Binder binder : binders) {
            BinderBrief brief = new BinderBrief();
            fillBinderBriefModel(brief, binder);
            briefs.add(brief);
        }

		BinderBrief[] ret = new BinderBrief[briefs.size()];
        briefs.toArray(ret);
		return ret;
	}

	private org.kablink.teaming.domain.Binder getBinderIfAccessible(Long binderId) {
		try {
			return getBinderModule().getBinder(binderId);
		}
		catch(NoBinderByTheIdException e) {
			return null;
		}
		catch(AccessControlException e) {
			return null;
		}
	}
	
	public GroupCollection profile_getUserGroups(String accessToken, long userId) {
		List<org.kablink.teaming.domain.Group> groups = getProfileModule().getUserGroups(userId);
		GroupBrief[] gbs = new GroupBrief[groups.size()];
		for(int i=0; i < gbs.length; ++i) {
			gbs[i] = toGroupBrief(groups.get(i));
		}		
		return new GroupCollection(gbs); 
	}
	
	protected GroupBrief toGroupBrief(Group group) {
		GroupBrief groupBrief = new GroupBrief(
				group.getId(),
				group.getParentBinder().getId(),
				group.getEntryDefId(),
				group.getTitle(),
				group.getEmailAddress(),
				group.getEntityType().toString(),
				Boolean.valueOf(group.isDisabled()),
				group.isReserved(),
				group.getName()
				);
		return groupBrief;
	}

	public BinderBrief[] profile_getFollowedPlaces(String accessToken, Long userId, String[] families, Boolean library) {
		if(userId == null)
			userId = RequestContextHolder.getRequestContext().getUserId();
		List<String> trackedPlacesIds = SearchUtils.getTrackedPlacesIds(this, userId);
		List<BinderBrief> binders = new ArrayList<BinderBrief>();
		if(trackedPlacesIds != null) {
			for(String id:trackedPlacesIds) {
				org.kablink.teaming.domain.Binder binder = getBinderIfAccessible(Long.valueOf(id));
				binder = filterBinder(binder, families, library);
				if(binder != null){
					BinderBrief brief = new BinderBrief();
					fillBinderBriefModel(brief, binder);
					binders.add(brief);
				}
			}
		}
		BinderBrief[] ret = new BinderBrief[binders.size()];
		binders.toArray(ret);
		return ret;
	}

	private org.kablink.teaming.domain.Binder filterBinder(org.kablink.teaming.domain.Binder binder, String[] families, Boolean library) {
		if(binder == null) return null;
		if(library != null) {
			if(library.booleanValue() != binder.isLibrary()) return null;
		}
		if(families != null && families.length > 0) {
			String f = null;
	    	org.dom4j.Document def = binder.getEntryDefDoc();
	    	if(def != null)
		    	f = DefinitionUtils.getFamily(def);
	    	if(f == null) return null;
	    	for(String family:families) {
	    		if(f.equals(family))
	    			return binder;
	    	}
	    	return null;
		}
		else {
			return binder;
		}
	}
	
	public long profile_getMaxUserQuota(String accessToken, Long userId) {
		if(userId != null)			
			return getProfileModule().getMaxUserQuota(userId);
		else
			return getProfileModule().getMaxUserQuota();
	}
	protected ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}
}
