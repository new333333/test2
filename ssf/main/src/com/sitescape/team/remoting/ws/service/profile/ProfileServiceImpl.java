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
package com.sitescape.team.remoting.ws.service.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.BaseService;
import com.sitescape.team.remoting.ws.util.DomInputData;
import com.sitescape.team.util.stringcheck.StringCheckUtil;

public class ProfileServiceImpl extends BaseService implements ProfileService {

	public String profile_getAllPrincipalsAsXML(String accessToken, int firstRecord, int maxRecords) {
		Document doc = DocumentHelper.createDocument();
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_OFFSET, new Integer(firstRecord));
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxRecords));
		Map results = getProfileModule().getPrincipals(getProfileModule().getProfileBinder().getId(), options);
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
		Long bId = new Long(binderId);
		Long pId = new Long(principalId);
		
		// Retrieve the raw entry.
		Principal entry = 
			getProfileModule().getEntry(bId, pId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = addPrincipalToDocument(doc, entry);
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		return xml;
	}
	
	public long profile_addUser(String accessToken, long binderId, String definitionId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getProfileModule().addUser(new Long(binderId), definitionId, new DomInputData(doc, getIcalModule()), null, null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public long profile_addGroup(String accessToken, long binderId, String definitionId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getProfileModule().addGroup(new Long(binderId), definitionId, new DomInputData(doc, getIcalModule()), null, null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void profile_addUserToGroup(String accessToken, long userId, String username, long groupId) {
		getProfileModule().addUserToGroup(Long.valueOf(userId), username, Long.valueOf(groupId));
	}
	
	public void profile_modifyPrincipal(String accessToken, long binderId, long principalId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			getProfileModule().modifyEntry(new Long(binderId), new Long(principalId), new DomInputData(doc, getIcalModule()));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void profile_deletePrincipal(String accessToken, long binderId, long principalId) {
		try {
			getProfileModule().deleteEntry(new Long(binderId), new Long(principalId), null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	public long profile_addUserWorkspace(String accessToken, long userId) {
		User user = (User)getProfileModule().getEntry(getProfileModule().getProfileBinder().getId(), userId);
		return getProfileModule().addUserWorkspace(user, null).getId();
	}

}
