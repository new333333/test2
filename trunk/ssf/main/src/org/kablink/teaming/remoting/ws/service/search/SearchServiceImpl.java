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
package org.kablink.teaming.remoting.ws.service.search;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.AuditTrail.AuditType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.report.ReportModule.ActivityInfo;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.remoting.ws.model.TeamBrief;
import org.kablink.teaming.remoting.ws.model.TeamCollection;
import org.kablink.teaming.remoting.ws.model.Timestamp;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.tree.WebSvcTreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.search.Constants;


public class SearchServiceImpl extends BaseService implements SearchService, SearchServiceInternal {

	protected final Log logger = LogFactory.getLog(getClass());

	public String search_search(String accessToken, String query, int offset, int maxResults)
	{
		query = StringCheckUtil.check(query);
		
		Document queryDoc = getDocument(query);
		
		Document doc = DocumentHelper.createDocument();
		Element folderElement = doc.addElement("searchResults");

		Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, offset, maxResults);
		List entrylist = (List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		Iterator entryIterator = entrylist.listIterator();
		while (entryIterator.hasNext()) {
			Map result = (Map) entryIterator.next();
			String docType = (String) result.get(Constants.DOC_TYPE_FIELD);
			Element resultElem = null;
			if(Constants.DOC_TYPE_ATTACHMENT.equals(docType)) {
				String attachmentType = "file";
				resultElem = folderElement.addElement(attachmentType);
			} else if(Constants.DOC_TYPE_BINDER.equals(docType)) {
				String binderType = (String) result.get(Constants.ENTITY_FIELD);
				resultElem = folderElement.addElement(docType);
				resultElem.addAttribute("type", binderType);
				resultElem.addAttribute("id", (String) result.get(Constants.DOCID_FIELD));
				resultElem.addAttribute("title", (String) result.get(Constants.TITLE_FIELD));
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
				}
				resultElem = folderElement.addElement(elementName);
				addEntryAttributes(resultElem, result, isPrincipal);
			}
		}
		
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
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId());
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
	
	public String search_getHotContent(String accessToken, String limitType, Long binderId)
	{
		Document doc = DocumentHelper.createDocument();
		Element entries = doc.addElement("entries");

		AuditType type = null;
		if(limitType != null && !limitType.equals("activity")) {
			type = AuditType.valueOf(limitType);
			if(!(type.equals(AuditType.view) || type.equals(AuditType.modify) || type.equals(AuditType.download))) {
				type = null;
			}
		}
		if(type == null) {
			limitType = "activity";
		}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		GregorianCalendar start = new GregorianCalendar();
		//get users over last 2 weeks
		start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
		Binder binder = null;
		if(binderId != null) {
			binder = getBinderModule().getBinder(new Long(binderId));
		}
		Collection<ActivityInfo> results = getReportModule().getActivity(type, start.getTime(), new java.util.Date(), binder);
		for(ActivityInfo info : results) {
			Element resultElem = null;
			if(info.getWhoOrWhat().getEntityType().equals(EntityType.folderEntry)) {
				resultElem = entries.addElement("entry");
				FolderEntry entry = (FolderEntry) info.getWhoOrWhat();
				addEntryAttributes(resultElem, entry);
				Element child = resultElem.addElement("creation");
				addPrincipalToDocument(child, entry.getCreation().getPrincipal());
				child.addElement("date").addText(sdf.format(entry.getCreation().getDate()));
				child = resultElem.addElement("modification");
				addPrincipalToDocument(child, entry.getModification().getPrincipal());
				child.addElement("date").addText(sdf.format(entry.getModification().getDate()));
			} else if(info.getWhoOrWhat().getEntityType().equals(EntityType.folder)) {
				resultElem = entries.addElement("folder");
				resultElem.addAttribute("id", info.getWhoOrWhat().getId().toString());
				resultElem.addAttribute("title", info.getWhoOrWhat().getTitle());
				addRating(resultElem, info.getWhoOrWhat());
			} else if(info.getWhoOrWhat().getEntityType().equals(EntityType.workspace)) {
				resultElem = entries.addElement("workspace");
				resultElem.addAttribute("id", info.getWhoOrWhat().getId().toString());
				resultElem.addAttribute("title", info.getWhoOrWhat().getTitle());
				addRating(resultElem, info.getWhoOrWhat());
			}
			resultElem.addAttribute(limitType + "Count", "" + info.getCount());
			resultElem.addAttribute("last" + limitType.substring(0, 1).toUpperCase() + limitType.substring(1), sdf.format(info.getLast()));
		}

		return doc.getRootElement().asXML();
	}

	public TeamCollection search_getTeams(String accessToken) {
		User user = RequestContextHolder.getRequestContext().getUser();
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId());
		
		List<TeamBrief> teamList = new ArrayList<TeamBrief>();
		for(Map binder : myTeams) {
			String binderIdStr = (String) binder.get(Constants.DOCID_FIELD);
			Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;
			teamList.add(new TeamBrief(binderId, (String) binder.get(Constants.TITLE_FIELD),
					new Timestamp((String) binder.get(Constants.CREATOR_NAME_FIELD), (Date) binder.get(Constants.CREATION_DATE_FIELD)),
					new Timestamp((String) binder.get(Constants.MODIFICATION_NAME_FIELD), (Date) binder.get(Constants.MODIFICATION_DATE_FIELD)),
					PermaLinkUtil.getPermalink(binder)));
		}
	
		TeamBrief[] array = new TeamBrief[teamList.size()];
		return new TeamCollection(user.getId(), user.getName(), teamList.toArray(array));
	}
}
