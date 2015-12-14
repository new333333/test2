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
package org.kablink.teaming.remoting.ws.service.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.TeamInfo;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.remoting.ws.model.FolderEntryBrief;
import org.kablink.teaming.remoting.ws.model.FolderEntryCollection;
import org.kablink.teaming.remoting.ws.model.TeamBrief;
import org.kablink.teaming.remoting.ws.model.TeamCollection;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.tree.WebSvcTreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Restrictions;


public class SearchServiceImpl extends BaseService implements SearchService, SearchServiceInternal {

	protected final Log logger = LogFactory.getLog(getClass());

	public String search_search(String accessToken, String query, int offset, int maxResults)
	{
		query = StringCheckUtil.check(query);
		
		Document queryDoc = getDocument(query);
		
		Document doc = DocumentHelper.createDocument();
		Element folderElement = doc.addElement("searchResults");

		Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);
		List entrylist = (List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		Iterator entryIterator = entrylist.listIterator();
		while (entryIterator.hasNext()) {
			Map result = (Map) entryIterator.next();
			String docType = (String) result.get(Constants.DOC_TYPE_FIELD);
			Element resultElem = null;
			if(Constants.DOC_TYPE_ATTACHMENT.equals(docType)) {
				String attachmentType = "file";
				resultElem = folderElement.addElement(attachmentType);
				addAttachmentAttributes(resultElem, result);
			} else if(Constants.DOC_TYPE_BINDER.equals(docType)) {
				resultElem = folderElement.addElement(docType);
				addBinderAttributes(resultElem, result);
			} else if(Constants.DOC_TYPE_ENTRY.equals(docType)) {
				String entryType = (String) result.get(Constants.ENTRY_TYPE_FIELD);
				String elementName = null;
				boolean isPrincipal = true;
				if(Constants.ENTRY_TYPE_ENTRY.equalsIgnoreCase(entryType)) {
					elementName="entry";
					isPrincipal = false;
				} else if(Constants.ENTRY_TYPE_REPLY.equalsIgnoreCase(entryType)) {
					elementName="entry";
					isPrincipal = false;					
				} else if(Constants.ENTRY_TYPE_USER.equalsIgnoreCase(entryType)) {
					elementName="principal";
				} else if(Constants.ENTRY_TYPE_GROUP.equalsIgnoreCase(entryType)) {
					elementName="principal";
				} else if(Constants.ENTRY_TYPE_APPLICATION.equalsIgnoreCase(entryType)) {
					elementName="principal";
				} else if(Constants.ENTRY_TYPE_APPLICATION_GROUP.equalsIgnoreCase(entryType)) {
					elementName="principal";
				}
				resultElem = folderElement.addElement(elementName);
				addEntryAttributes(resultElem, result, isPrincipal);
			}
		}
		
		folderElement.addAttribute("first", Integer.toString(offset));
		folderElement.addAttribute("count", Integer.toString(entrylist.size()));
		folderElement.addAttribute("total", ((Integer)folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).toString());
		
		return doc.getRootElement().asXML();
	}

	public String search_getWorkspaceTreeAsXML(String accessToken, long binderId, int levels, String page) {
		org.kablink.teaming.domain.Binder binder = null;
		
		if(binderId == -1) {
			binder = getWorkspaceModule().getTopWorkspace();
		} else {
			binder = getBinderModule().getBinder(new Long(binderId));
		}

		Document tree;
		if (binder instanceof Workspace) {
			tree = getBinderModule().getDomBinderTree(binder.getId(), 
					new WsDomTreeBuilder(binder, true, this, new WebSvcTreeHelper(), page), levels);
		} 
		else {
			//org.kablink.teaming.domain.Folder topFolder = ((org.kablink.teaming.domain.Folder)binder).getTopFolder();
			tree = getBinderModule().getDomBinderTree(binder.getId(), 
					new WsDomTreeBuilder(binder, false, this, new WebSvcTreeHelper(), page), levels);
			
			//if (topFolder == null) topFolder = (org.kablink.teaming.domain.Folder)binder;
			//tree = getFolderModule().getDomFolderTree(topFolder.getId(), new WsDomTreeBuilder(topFolder, false, this, treeKey));
		}

		String xml = tree.getRootElement().asXML();
		//System.out.println(xml);

		return xml;
	}
	
	
	public String search_getTeamsAsXML(String accessToken)
	{
		User user = RequestContextHolder.getRequestContext().getUser();
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId(), 
				SearchUtils.fieldNamesList(Constants.TITLE_FIELD,Constants.DOCID_FIELD));
		Document doc = DocumentHelper.createDocument();
		Element teams = doc.addElement("teams");
		teams.addAttribute("principalId", user.getId().toString());
		for(Map binder : myTeams) {
			Element team = teams.addElement("team");
			team.addAttribute("title", (String) binder.get(Constants.TITLE_FIELD));
			team.addAttribute("binderId", (String) binder.get(Constants.DOCID_FIELD));
		}
		return doc.getRootElement().asXML();
	}
	
	public TeamCollection search_getUserTeams(String accessToken, long userId) {
		Principal entry = 
			getProfileModule().getEntry(Long.valueOf(userId));
		if(!(entry instanceof User))
			throw new IllegalArgumentException(userId + " does not represent an user. It is " + entry.getClass().getSimpleName());
		return getTeams((User) entry);
	}
	
	public TeamCollection search_getTeams(String accessToken) {
		User user = RequestContextHolder.getRequestContext().getUser();
		return getTeams(user);
	}
	
	protected TeamCollection getTeams(User user) {
        List<TeamInfo> myTeams = getProfileModule().getUserTeams(user.getId());
        TeamBrief[] teamBriefs = new TeamBrief[myTeams.size()];
        int index = 0;
        for (TeamInfo info : myTeams) {
            teamBriefs[index++] = new TeamBrief(info);
		}

		return new TeamCollection(user.getId(), user.getName(), teamBriefs);
	}

	public FolderEntryCollection search_getFolderEntries(String accessToken,
			String query, int offset, int maxResults) {
		List<FolderEntryBrief> entries = new ArrayList<FolderEntryBrief>();

		query = StringCheckUtil.check(query);
		
		Document queryDoc = getDocument(query);
		Element queryRoot = queryDoc.getRootElement();
		
		// Find the root junction element
		Iterator it = queryRoot.selectNodes("AND|OR|NOT").iterator();
		
		// Build an implicit query to filter out everything except for folder entries
		Criterion crit = Restrictions.conjunction()
			.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
			.add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}));
		Element implicit = crit.toQuery(queryRoot);
		
		// Add the user supplied query criteria into our implicit AND 
		while(it.hasNext()) {
			implicit.add(((Element)it.next()).detach());
		}
		
		// Add the SORTBY back to the end of the document
		it = queryRoot.selectNodes("SORTBY").iterator();
		while(it.hasNext()) {
			queryRoot.add(((Element)it.next()).detach());
		}
		
		Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);
		List entrylist = (List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		Iterator entryIterator = entrylist.listIterator();
		while (entryIterator.hasNext()) {
			entries.add(toFolderEntryBrief((Map) entryIterator.next()));
		}
		
		FolderEntryBrief[] array = new FolderEntryBrief[entries.size()];
		return new FolderEntryCollection(offset, 
				((Integer)folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue(), 
				entries.toArray(array));
	}
}
