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
package com.sitescape.team.remoting.ws.service.binder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.BinderComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.rss.util.UrlUtil;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.BaseService;
import com.sitescape.team.remoting.ws.model.FolderBrief;
import com.sitescape.team.remoting.ws.model.FolderCollection;
import com.sitescape.team.remoting.ws.model.FunctionMembership;
import com.sitescape.team.remoting.ws.model.PrincipalBrief;
import com.sitescape.team.remoting.ws.model.TeamMemberCollection;
import com.sitescape.team.remoting.ws.model.Timestamp;
import com.sitescape.team.remoting.ws.util.DomInputData;
import com.sitescape.team.remoting.ws.util.ModelInputData;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.stringcheck.StringCheckUtil;
import com.sitescape.team.web.util.PermaLinkUtil;
import com.sitescape.util.search.Constants;

public class BinderServiceImpl extends BaseService implements BinderService, BinderServiceInternal {
	public long binder_addBinderWithXML(String accessToken, long parentId, String definitionId, String inputDataAsXML)
	{
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		try {
			Document doc = getDocument(inputDataAsXML);
			return getBinderModule().addBinder(new Long(parentId), definitionId, 
					new DomInputData(doc, getIcalModule()), new HashMap(), null).longValue();
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	public com.sitescape.team.remoting.ws.model.Subscription binder_getSubscription(String accessToken, long binderId) {
		Binder binder = getBinderModule().getBinder(binderId);
		Subscription sub = getBinderModule().getSubscription(binder);
		if (sub == null) return null;
		return toSubscriptionModel(sub);
		
	}
	public void binder_setSubscription(String accessToken, long binderId, com.sitescape.team.remoting.ws.model.Subscription subscription) {
		if (subscription == null || subscription.getStyles().length == 0) {
			getBinderModule().setSubscription(binderId, null);
			return;
		}
		Map subMap = new HashMap();
		com.sitescape.team.remoting.ws.model.SubscriptionStyle[] styles = subscription.getStyles();
		for (int i=0; i<styles.length; ++i) {
			subMap.put(styles[i].getStyle(), styles[i].getEmailTypes());
		}
		getBinderModule().setSubscription(binderId, subMap);
	}
	
	public void binder_setDefinitions(String accessToken, long binderId, String[] definitionIds, String[] workflowAssociations) {
		HashMap wfs = new HashMap();
		for (int i=0; i<workflowAssociations.length; i++) {
			String [] vals = workflowAssociations[i].split(",");
			wfs.put(vals[0], vals[1]);
		}
		getBinderModule().setDefinitions(binderId, Arrays.asList(definitionIds), wfs);
	}
	public String binder_getTeamMembersAsXML(String accessToken, long binderId)
	{
		Binder binder = getBinderModule().getBinder(new Long(binderId));
		SortedSet<Principal> principals = getBinderModule().getTeamMembers(binder, true);
		Document doc = DocumentHelper.createDocument();
		Element team = doc.addElement("team");
		team.addAttribute("inherited", binder.isTeamMembershipInherited()?"true":"false");
		for(Principal p : principals) {
			addPrincipalToDocument(team, p);
		}
		
		return doc.getRootElement().asXML();
	}
	public void binder_setTeamMembers(String accessToken, long binderId, String []memberNames) {
		Collection<Principal> principals = getProfileModule().getPrincipalsByName(Arrays.asList(memberNames));
		Set<Long>ids = new HashSet();
		for (Principal p:principals) {
			ids.add(p.getId());
		}
		
		getBinderModule().setTeamMembers(binderId, ids);
	}
	public void binder_setFunctionMembershipWithXML(String accessToken, long binderId, String inputDataAsXml) {
		Binder binder = getBinderModule().getBinder(binderId);
		List<Function> functions = getAdminModule().getFunctions();
		Document doc = getDocument(inputDataAsXml);
		Map wfms = new HashMap();
		List<Element> wfmElements = doc.getRootElement().selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_FUNCTION_MEMBERSHIP);
		for (Element wfmElement:wfmElements) {
			 String functionName = XmlUtils.getProperty(wfmElement, ObjectKeys.XTAG_WA_FUNCTION_NAME);
			 Function func = null;
			 for (Function f:functions) {
				 if (f.getName().equals(functionName)) {
					 func = f;
					 break;
				 }
			 }
			 if (func == null) continue;
			 List<Element> nameElements = wfmElement.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_PROPERTY + "[@name='" + ObjectKeys.XTAG_WA_MEMBER_NAME + "']");
			 Set<String> names = new HashSet();
			 for (Element e:nameElements) {
				 names.add(e.getTextTrim());				 
			 }
			 Collection<Principal> principals = getProfileModule().getPrincipalsByName(names);
			 Set<Long>ids = new HashSet();
			 for (Principal p:principals) {
				 ids.add(p.getId());
			 }
			 ids.addAll(LongIdUtil.getIdsAsLongSet(XmlUtils.getProperty(wfmElement, ObjectKeys.XTAG_WA_MEMBERS), ","));

			 if (ids.isEmpty()) continue;
			 wfms.put(func.getId(), ids);
		}
		getAdminModule().setWorkAreaFunctionMemberships(binder, wfms);
	}
	public void binder_setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit) {
		Binder binder = getBinderModule().getBinder(binderId);
		getAdminModule().setWorkAreaFunctionMembershipInherited(binder, inherit); 		
	}
	public void binder_setOwner(String accessToken, long binderId, long userId) {
		Binder binder = getBinderModule().getBinder(binderId);
		getAdminModule().setWorkAreaOwner(binder, userId, false); 		
	}
	public void binder_indexBinder(String accessToken, long binderId) {
		getBinderModule().indexBinder(binderId, true);
	}
	
	public Long[] binder_indexTree(String accessToken, long binderId) {
		Set<Long> binderIds = getBinderModule().indexTree(binderId);
		Long[] array = new Long[binderIds.size()];
		return binderIds.toArray(array);
	}
	public TeamMemberCollection binder_getTeamMembers(String accessToken, long binderId) {
		Binder binder = getBinderModule().getBinder(new Long(binderId));
		SortedSet<Principal> principals = getBinderModule().getTeamMembers(binder, true);
		
		List<PrincipalBrief> principalList = new ArrayList<PrincipalBrief>();
		for(Principal p : principals) {
			principalList.add(toPrincipalBrief(p));
		}
		
		PrincipalBrief[] array = new PrincipalBrief[principalList.size()];
		return new TeamMemberCollection(binder.isTeamMembershipInherited(),
				principalList.toArray(array));
	}
	
	public long binder_addBinder(String accessToken, com.sitescape.team.remoting.ws.model.Binder binder) {
		try {
			return getBinderModule().addBinder(binder.getParentBinderId(), binder.getDefinitionId(), 
					new ModelInputData(binder), new HashMap(), null).longValue();
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	public long binder_copyBinder(String accessToken, long sourceId, long destinationId, boolean cascade) {
		return getBinderModule().copyBinder(sourceId, destinationId, cascade, null);
	}
	public String[] binder_deleteBinder(String accessToken, long binderId, boolean deleteMirroredSource) {
		Set<Exception>errors = getBinderModule().deleteBinder(binderId, deleteMirroredSource, null);
		String[] strErrors = new String[errors.size()];
		int i=0;
		for (Exception ex:errors) {
			strErrors[i++] = ex.getLocalizedMessage();
		}
		return strErrors;
	}
	public void binder_moveBinder(String accessToken, long binderId, long destinationId) {
		getBinderModule().moveBinder(binderId, destinationId, null);
	}
	public com.sitescape.team.remoting.ws.model.Binder binder_getBinder(String accessToken, long binderId, boolean includeAttachments) {

		// Retrieve the raw binder.
		Binder binder =  getBinderModule().getBinder(binderId);

		com.sitescape.team.remoting.ws.model.Binder binderModel = 
			new com.sitescape.team.remoting.ws.model.Binder(); 

		fillBinderModel(binderModel, binder);
		
		return binderModel;
	}
	public void binder_modifyBinder(String accessToken, com.sitescape.team.remoting.ws.model.Binder binder) {
		try {
			getBinderModule().modifyBinder(binder.getId(), 
				new ModelInputData(binder), null, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
	}
	public void binder_uploadFile(String accessToken, long binderId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}

	public void binder_setFunctionMembership(String accessToken, long binderId, FunctionMembership[] functionMemberships) {
		if(functionMemberships == null) return;
		Binder binder = getBinderModule().getBinder(binderId);
		List<Function> functions = getAdminModule().getFunctions();
		Map wfms = new HashMap();
		FunctionMembership functionMembership;
		for(int i = 0; i < functionMemberships.length; i++) {
			functionMembership = functionMemberships[i];
			 Function func = null;
			 for (Function f:functions) {
				 if (f.getName().equals(functionMembership.getFunctionName())) {
					 func = f;
					 break;
				 }
			 }
			 if (func == null) continue;
			 Set<Long>ids = new HashSet();
			 String[] memberNames = functionMembership.getMemberNames();
			 if(memberNames != null) {
				 Collection<Principal> principals = getProfileModule().getPrincipalsByName(Arrays.asList(memberNames));
				 for (Principal p:principals) {
					 ids.add(p.getId());
				 }
			 }
			 Long[] memberIds = functionMembership.getMemberIds();
			 if(memberIds != null) {
				 ids.addAll(Arrays.asList(memberIds));
			 }
			 if (ids.isEmpty()) continue;
			 wfms.put(func.getId(), ids);
		}
		getAdminModule().setWorkAreaFunctionMemberships(binder, wfms);
	}
	
	public FolderCollection binder_getFolders(String accessToken, long binderId) {
        User user = RequestContextHolder.getRequestContext().getUser();
        // Probably sorting by title isn't as important in web services as in browser UI, 
        // but it wouldn't hurt either.
        Comparator c = new BinderComparator(user.getLocale(),BinderComparator.SortByField.title);
		Binder binder = getBinderModule().getBinder(binderId);		
		Map searchResults = getBinderModule().getBinders(binder, new HashMap());
		List searchBinders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);
	
		List<FolderBrief> folderList = new ArrayList<FolderBrief>();
		//get folders
		Long id;
		String title;
		Timestamp creation;
		Timestamp modification;
		String permaLink;
		String rssUrl;
		String icalUrl;
		for (int i=0; i<searchBinders.size(); ++i) {
			Map search = (Map)searchBinders.get(i);
			String entityType = (String)search.get(Constants.ENTITY_FIELD);
			if (!EntityType.folder.name().equals(entityType))
				continue;
			id = Long.valueOf((String)search.get(Constants.DOCID_FIELD));
			title = (String) search.get(Constants.TITLE_FIELD);
			creation = new Timestamp((String) search.get(Constants.MODIFICATION_NAME_FIELD), (Date) search.get(Constants.MODIFICATION_DATE_FIELD));
			modification = new Timestamp((String) search.get(Constants.CREATOR_NAME_FIELD), (Date) search.get(Constants.CREATION_DATE_FIELD));
			permaLink = PermaLinkUtil.getURL(id, (String) search.get(Constants.ENTITY_FIELD));
			rssUrl = UrlUtil.getFeedURL(null, id.toString()); // folder only
			icalUrl = com.sitescape.team.ical.util.UrlUtil.getICalURL(null, id.toString(), null); // folder only
			folderList.add(new FolderBrief(id, 
					title,
					creation,
					modification,
					permaLink,
					rssUrl,
					icalUrl));								
		}
		FolderBrief[] array = new FolderBrief[folderList.size()];
		return new FolderCollection(binderId, folderList.toArray(array));
	}
	public void binder_deleteTag(String accessToken, long binderId, String tagId) {
		getBinderModule().deleteTag(binderId, tagId);
	}
	public void binder_setTag(String accessToken, com.sitescape.team.remoting.ws.model.Tag tag) {
		getBinderModule().setTag(tag.getEntityId(), tag.getName(), tag.isPublic());
	}
	public com.sitescape.team.remoting.ws.model.Tag[] binder_getTags(String accessToken, long binderId) {
		Collection<Tag>tags = getBinderModule().getTags(getBinderModule().getBinder(binderId));
		com.sitescape.team.remoting.ws.model.Tag[] results = new com.sitescape.team.remoting.ws.model.Tag[tags.size()];
		int i=0;
		for (Tag tag:tags) {
			results[i++] = toTagModel(tag);
		}
		return results;
	}

}
