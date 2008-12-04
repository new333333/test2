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
package org.kablink.teaming.remoting.ws.service.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NoFileByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.RemotingException;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.PrincipalBrief;
import org.kablink.teaming.remoting.ws.model.PrincipalCollection;
import org.kablink.teaming.remoting.ws.util.ModelInputData;


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
		}
		
	}
	public PrincipalCollection profile_getGroupMembers(String accessToken, String groupName) {
		Group group = getProfileModule().getGroup(groupName);
		List members = group.getMembers();
		PrincipalBrief[] principals = new PrincipalBrief[members.size()];
		for(int i=0; i<members.size(); ++i) {
			principals[i] = toPrincipalBrief((Principal)members.get(i));
		}
		PrincipalCollection result = new PrincipalCollection();
		result.setEntries(principals);
		result.setFirst(0);
		result.setTotal(members.size());
		return result;
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
		User user = getProfileModule().getUser(userName);
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
	
	public long profile_addGroup(String accessToken, org.kablink.teaming.remoting.ws.model.Group group) {
		try {
			return getProfileModule().addGroup(group.getDefinitionId(), new ModelInputData(group), null, null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	public long profile_addUser(String accessToken, org.kablink.teaming.remoting.ws.model.User user) {
		try {
			return getProfileModule().addUser(user.getDefinitionId(), new ModelInputData(user), null, null).longValue();
		}
		catch(WriteFilesException e) {
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
	}
	
	public void profile_modifyUser(String accessToken, org.kablink.teaming.remoting.ws.model.User user) {
		try {
			getProfileModule().modifyEntry(user.getId(), new ModelInputData(user));
		}
		catch(WriteFilesException e) {
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

	}
	
	public FileVersions profile_getFileVersions(String accessToken, long principalId, String fileName) {
		Principal entry = getProfileModule().getEntry(principalId);
		FileAttachment att = entry.getFileAttachment(fileName);
		if(att != null)
			return toFileVersions(att);
		else
			throw new NoFileByTheNameException(fileName);
	}

}
