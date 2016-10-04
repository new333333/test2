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
package org.kablink.teaming.remoting.ws.service.binder;

import static org.kablink.util.search.Restrictions.eq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoBinderByTheNameException;
import org.kablink.teaming.domain.NoFileByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.rss.util.UrlUtil;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.remoting.ws.RemotingException;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.FolderBrief;
import org.kablink.teaming.remoting.ws.model.FolderCollection;
import org.kablink.teaming.remoting.ws.model.FunctionMembership;
import org.kablink.teaming.remoting.ws.model.PrincipalBrief;
import org.kablink.teaming.remoting.ws.model.TeamMemberCollection;
import org.kablink.teaming.remoting.ws.model.Timestamp;
import org.kablink.teaming.remoting.ws.model.TrashBrief;
import org.kablink.teaming.remoting.ws.model.TrashCollection;
import org.kablink.teaming.remoting.ws.util.DomInputData;
import org.kablink.teaming.remoting.ws.util.ModelInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.TrashHelper.TrashEntity;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction.Disjunction;

import static org.kablink.util.search.Restrictions.disjunction;

public class BinderServiceImpl extends BaseService implements BinderService, BinderServiceInternal {
	private CoreDao coreDao;
	private ProfileDao profileDao;
	private AccessControlManager accessControlManager;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	
	public ProfileDao getProfileDao() {
		return profileDao;
	}
	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected AccessControlManager getAccessControlManager() {
		return accessControlManager;
	}
	public void setAccessControlManager(AccessControlManager accessControlManager) {
		this.accessControlManager = accessControlManager;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public long binder_addBinderWithXML(String accessToken, long parentId, String definitionId, String inputDataAsXML)
	{
		
		try {
			Document doc = getDocument(inputDataAsXML);
			return getBinderModule().addBinder(new Long(parentId), definitionId, 
					new DomInputData(doc, getIcalModule()), new HashMap(), null).getId().longValue();
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		} catch (AccessControlException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}
	@Override
	public org.kablink.teaming.remoting.ws.model.Subscription binder_getSubscription(String accessToken, long binderId) {
		Binder binder = getBinderModule().getBinder(binderId);
		Subscription sub = getBinderModule().getSubscription(binder);
		if (sub == null) return null;
		return toSubscriptionModel(sub);
		
	}
	@Override
	@SuppressWarnings("unchecked")
	public void binder_setSubscription(String accessToken, long binderId, org.kablink.teaming.remoting.ws.model.Subscription subscription) {
		if (subscription == null || subscription.getStyles().length == 0) {
			getBinderModule().setSubscription(binderId, null);
			return;
		}
		Map subMap = new HashMap();
		org.kablink.teaming.remoting.ws.model.SubscriptionStyle[] styles = subscription.getStyles();
		for (int i=0; i<styles.length; ++i) {
			subMap.put(Integer.valueOf(styles[i].getStyle()), styles[i].getEmailTypes());
		}
		getBinderModule().setSubscription(binderId, subMap);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void binder_setDefinitions(String accessToken, long binderId, String[] definitionIds, String[] workflowAssociations) {
		HashMap wfs = new HashMap();
		for (int i=0; i<workflowAssociations.length; i++) {
			String [] vals = workflowAssociations[i].split(",");
			wfs.put(vals[0], vals[1]);
		}
		getBinderModule().setDefinitions(binderId, Arrays.asList(definitionIds), wfs);
	}
	@Override
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
	@Override
	@SuppressWarnings("unchecked")
	public void binder_setTeamMembers(String accessToken, long binderId, String []memberNames) {
		Collection<Principal> principals = getProfileModule().getPrincipalsByName(Arrays.asList(memberNames));
		Set<Long>ids = new HashSet();
		for (Principal p:principals) {
			ids.add(p.getId());
		}
		
		getBinderModule().setTeamMembers(binderId, ids);
	}
	@Override
	@SuppressWarnings("unchecked")
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
				 if (f.isZoneWide()) continue;
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
			 Set<Long>ids = new HashSet();
			 if(!names.isEmpty()) {
				 Collection<Principal> principals = getProfileModule().getPrincipalsByName(names);
				 for (Principal p:principals) {
					 ids.add(p.getId());
				 }
			 }
			 ids.addAll(LongIdUtil.getIdsAsLongSet(XmlUtils.getProperty(wfmElement, ObjectKeys.XTAG_WA_MEMBERS), ","));

			 if (ids.isEmpty()) continue;
			 wfms.put(func.getId(), ids);
		}
		getAdminModule().setWorkAreaFunctionMemberships(binder, wfms);
	}
	@Override
	public void binder_setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit) {
		Binder binder = getBinderModule().getBinder(binderId);
		getAdminModule().setWorkAreaFunctionMembershipInherited(binder, inherit); 		
	}
	@Override
	public void binder_setOwner(String accessToken, long binderId, long userId) {
		Binder binder = getBinderModule().getBinder(binderId);
		getAdminModule().setWorkAreaOwner(binder, userId, false); 		
	}
	@Override
	public void binder_indexBinder(String accessToken, long binderId) {
		getBinderModule().indexBinder(binderId, true);
	}
	
	@Override
	public Long[] binder_indexTree(String accessToken, long binderId) {
		Set<Long> binderIds = getBinderModule().indexTree(binderId);
		Long[] array = new Long[binderIds.size()];
		return binderIds.toArray(array);
	}
	@Override
	@SuppressWarnings("unchecked")
	public TeamMemberCollection binder_getTeamMembers(String accessToken, long binderId, boolean explodeGroups, int firstRecord, int maxRecords) {
		Binder binder = getBinderModule().getBinder(new Long(binderId));
		SortedSet<Principal> principals = getBinderModule().getTeamMembers(binder, explodeGroups);
		
		int length = principals.size();
		if(maxRecords > 0)
			length = Math.min(length - firstRecord, maxRecords);
		if(length < 0)
			length = 0;

		List<PrincipalBrief> principalList = new ArrayList<PrincipalBrief>();
		int index = 0;
		for(Iterator it = principals.iterator(); it.hasNext(); index++) {
			if(index < firstRecord)
				continue; // Skip over the first firstRecord records
			principalList.add(toPrincipalBrief((Principal)it.next()));
		}
		
		PrincipalBrief[] array = new PrincipalBrief[principalList.size()];
		return new TeamMemberCollection(firstRecord, 
				principals.size(), 
				binder.isTeamMembershipInherited(),
				principalList.toArray(array));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public long binder_addBinder(String accessToken, org.kablink.teaming.remoting.ws.model.Binder binder) {
		SimpleProfiler.start("binderService_addBinder");
		try {
			Map options = new HashMap();
			getTimestamps(options, binder);
			long binderId = getBinderModule().addBinder(binder.getParentBinderId(), binder.getDefinitionId(), 
					new ModelInputData(binder), new HashMap(), options).getId().longValue();
			SimpleProfiler.stop("binderService_addBinder");			
			return binderId;
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		} catch (AccessControlException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}
	
	@Override
	public long binder_copyBinder(String accessToken, long sourceId, long destinationId, boolean cascade) {
		return getBinderModule().copyBinder(sourceId, destinationId, cascade, null).getId().longValue();
	}
	@Override
	public void binder_deleteBinder(String accessToken, long binderId, boolean deleteMirroredSource) {
		getBinderModule().deleteBinder(binderId, deleteMirroredSource, null);
	}
	@Override
	public void binder_preDeleteBinder(String accessToken, long binderId) {
		try {
			TrashHelper.preDeleteBinder(this, binderId);
		}
		catch (Exception e) {
			throw new RemotingException(e);
		}
	}
	@Override
	@SuppressWarnings("unchecked")
	public void binder_restoreBinder(String accessToken, long binderId) {
		Binder binder = getBinderModule().getBinder(binderId);
		Long binderParentId = binder.getParentBinder().getId();
		HashMap	hm = new HashMap();
		hm.put("_docId", String.valueOf(binderId));
		hm.put("_docType", "binder");
		hm.put("_binderParentId", String.valueOf(binderParentId));
		TrashHelper.restoreEntities(
			this,
			new TrashHelper.TrashEntity(hm));
	}
	@Override
	public void binder_moveBinder(String accessToken, long binderId, long destinationId) {
		getBinderModule().moveBinder(binderId, destinationId, null);
	}
	@Override
	public org.kablink.teaming.remoting.ws.model.Binder binder_getBinder(String accessToken, long binderId, boolean includeAttachments) {

		// Retrieve the raw binder.
		Binder binder =  getBinderModule().getBinder(binderId);

		org.kablink.teaming.remoting.ws.model.Binder binderModel = 
			new org.kablink.teaming.remoting.ws.model.Binder(); 

		fillBinderModel(binderModel, binder);
		
		return binderModel;
	}
	@Override
	public org.kablink.teaming.remoting.ws.model.Binder binder_getBinderByPathName(String accessToken, String pathName, boolean includeAttachments) {
		Binder binder = getBinderModule().getBinderByPathName(pathName);
		if (binder == null) throw new NoBinderByTheNameException(pathName);
		org.kablink.teaming.remoting.ws.model.Binder binderModel = 
			new org.kablink.teaming.remoting.ws.model.Binder(); 

		fillBinderModel(binderModel, binder);
		
		return binderModel;
	}
	@Override
	public void binder_modifyBinder(String accessToken, org.kablink.teaming.remoting.ws.model.Binder binder) {
		try {
			getBinderModule().modifyBinder(binder.getId(), 
				new ModelInputData(binder), null, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		} catch (AccessControlException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}			
	}
	@Override
	public void binder_uploadFile(String accessToken, long binderId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}
	@Override
	@SuppressWarnings("unchecked")
	public void binder_removeFile(String accessToken, long binderId, String fileName) {
		try {
			Binder binder = getBinderModule().getBinder(binderId);
			FileAttachment att = binder.getFileAttachment(fileName);
			if (att == null) return;
			List deletes = new ArrayList();
			deletes.add(att.getId());
			getBinderModule().modifyBinder(binderId, new EmptyInputData(), null, deletes, null);
			
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		} catch (AccessControlException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}			

	}

	@Override
	public FunctionMembership[] binder_getFunctionMembership(String accessToken, long binderId) {
		Binder binder = getBinderModule().getBinder(binderId);
		List<WorkAreaFunctionMembership> wafml = getAdminModule().getWorkAreaFunctionMemberships(binder);
		List<Function> functions = getAdminModule().getFunctions();
		List<FunctionMembership> fml = new ArrayList<FunctionMembership>();
		for(WorkAreaFunctionMembership wafm:wafml) {
			Function func = null;
			for(Function f:functions) {
				if(f.isZoneWide()) continue;
				if(f.getId().equals(wafm.getFunctionId())) {
					func = f;
					break;
				}
			}
			if(func == null) continue;
			FunctionMembership fm = new FunctionMembership();
			fm.setFunctionName(func.getName());
			fm.setMemberIds(wafm.getMemberIds().toArray(new Long[wafm.getMemberIds().size()]));
			fml.add(fm);
		}
		return fml.toArray(new FunctionMembership[fml.size()]);
	}
	
	@Override
	@SuppressWarnings("unchecked")
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
				 if (f.isZoneWide()) continue;
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
	
	@Override
	@SuppressWarnings("unchecked")
	public FolderCollection binder_getFolders(String accessToken, final long binderId, final int firstRecord, final int maxRecords) {
        // Probably sorting by title isn't as important in web services as in browser UI, 
        // but it wouldn't hurt either.

		FolderSearcher folderSearcher = new FolderSearcher() {
			@Override
			public Map searchFolders() {
				Binder binder = getBinderModule().getBinder(binderId);		
		    	Map options = new HashMap();
		    	options.put(ObjectKeys.SEARCH_OFFSET, new Integer(firstRecord));
		    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxRecords));
				return getBinderModule().getBinders(binder, options);
			}
		};

		return getFolders(folderSearcher, firstRecord);
	}
	
	private Set<Long> getUserEffectiveIds() {
		User user = RequestContextHolder.getRequestContext().getUser();
		Set<Long> principalIds = getProfileDao().getApplicationLevelPrincipalIds(user);

		Long allUsersGroupId = Utils.getAllUsersGroupId();
		Long allExtUsersGroupId = Utils.getAllExtUsersGroupId();
      	Set<Long> principalIds2 = new HashSet<Long>(principalIds);
      	
		//check user can see all users
      	boolean canOnlySeeCommonGroupMembers = Utils.canUserOnlySeeCommonGroupMembers(user);
		if (canOnlySeeCommonGroupMembers) {
			if ((allUsersGroupId != null && principalIds2.contains(allUsersGroupId)) ||
					(allExtUsersGroupId != null && principalIds2.contains(allExtUsersGroupId))) {
				//This user is not allowed to see all users, so remove the AllUsers group ids
				principalIds2.remove(allUsersGroupId);
				principalIds2.remove(allExtUsersGroupId);
			}
		}
		return principalIds2;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public FolderCollection binder_getAllFoldersOfMatchingFamily(String accessToken, final long[] startingBinderIds, final String[] families, final boolean restrictByTeamMembership, final int firstRecord, final int maxRecords) {
		FolderSearcher folderSearcher = new FolderSearcher() {
			@Override
			public Map searchFolders() {	
		    	Criteria crit = new Criteria()
				.add(eq(Constants.ENTITY_FIELD, "folder"));
		    	
		    	if(startingBinderIds != null && startingBinderIds.length > 0) {
		    		Disjunction disj = disjunction();
		    		for(long startingBinderId:startingBinderIds) 
		    			disj.add(eq(Constants.ENTRY_ANCESTRY, String.valueOf(startingBinderId)));
		    		crit.add(disj);
		    	}

		    	if(families != null && families.length > 0) {
		    		Disjunction disj = disjunction();
		    		for(String family:families)
		    			disj.add(eq(Constants.FAMILY_FIELD, family));
		    		crit.add(disj);
		    	}
		    	
		    	if(restrictByTeamMembership) {			
		    		Set<Long> ids = getUserEffectiveIds();
		    		if(ids.size() > 0) {
		    			Disjunction disj = disjunction();
		    			for(Long id:ids) {
		    				disj.add(eq(Constants.TEAM_ACL_FIELD, id.toString()));
		    			}
		    			crit.add(disj);
		    		}
		    	}
				return getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, firstRecord, maxRecords, null);
			}
		};

		return getFolders(folderSearcher, firstRecord);
	}
	
	@SuppressWarnings("unchecked")
	private FolderCollection getFolders(FolderSearcher folderSearcher, int firstRecord) {
		Map searchResults = folderSearcher.searchFolders();
		
		List searchBinders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);

		List<FolderBrief> folderList = new ArrayList<FolderBrief>();
		//get folders
		Long id;
		String title;
		Timestamp creation;
		Timestamp modification;
		String createdWithDefinitionId;
		String defaultViewDefinitionId;
		String permaLink;
		String webdavUrl;
		String rssUrl;
		String atomUrl;
		String icalUrl;
		String family;
		Integer definitionType;
		String path;
		Long parentBinderId = null;
		for (int i=0; i<searchBinders.size(); ++i) {
			Map search = (Map)searchBinders.get(i);
			String entityType = (String)search.get(Constants.ENTITY_FIELD);
			if (!EntityType.folder.name().equals(entityType))
				continue;
			id = Long.valueOf((String)search.get(Constants.DOCID_FIELD));
			title = (String) search.get(Constants.TITLE_FIELD);
			
			UserPrincipal creator = Utils.redactUserPrincipalIfNecessary(Long.valueOf((String) search.get(Constants.CREATORID_FIELD)));
			UserPrincipal modifier = Utils.redactUserPrincipalIfNecessary(Long.valueOf((String) search.get(Constants.MODIFICATIONID_FIELD)));
			
			creation = new Timestamp(((creator != null)? creator.getName() : (String) search.get(Constants.CREATOR_NAME_FIELD)),
					Long.valueOf((String)search.get(Constants.CREATORID_FIELD)),
					(Date) search.get(Constants.CREATION_DATE_FIELD));
			modification = new Timestamp(((modifier != null)? modifier.getName() : (String) search.get(Constants.MODIFICATION_NAME_FIELD)),
					Long.valueOf((String)search.get(Constants.MODIFICATIONID_FIELD)),
					(Date) search.get(Constants.MODIFICATION_DATE_FIELD));
			createdWithDefinitionId = (String) search.get(Constants.CREATED_WITH_DEFINITION_FIELD);
			defaultViewDefinitionId = (String) search.get(Constants.COMMAND_DEFINITION_FIELD);
			family = (String) search.get(Constants.FAMILY_FIELD);
			definitionType = Integer.valueOf((String)search.get(Constants.DEFINITION_TYPE_FIELD));
			path = (String) search.get(Constants.ENTITY_PATH);
			permaLink = PermaLinkUtil.getPermalink(search);
			// Construct webdav url only for library folder
			Boolean library = null;
			String libraryStr = (String)search.get(Constants.IS_LIBRARY_FIELD);
			if(Constants.TRUE.equals(libraryStr))
				library = Boolean.TRUE;
			else if(Constants.FALSE.equals(libraryStr))
				library = Boolean.FALSE;
			if(Boolean.TRUE.equals(library) && path != null)
				webdavUrl = SsfsUtil.getLibraryBinderUrl(path);
			else
				webdavUrl = null;
			Boolean mirrored = null;
			String mirroredStr = (String)search.get(Constants.IS_MIRRORED_FIELD);
			if(Constants.TRUE.equals(mirroredStr))
				mirrored = Boolean.TRUE;
			else if(Constants.FALSE.equals(mirroredStr))
				mirrored = Boolean.FALSE;
			rssUrl = UrlUtil.getFeedURL(null, id.toString()); // folder only
			icalUrl = org.kablink.teaming.ical.util.UrlUtil.getICalURL(null, id.toString(), null); // folder only
			atomUrl = UrlUtil.getAtomURL(null, id.toString()); // folder only
			String parentBinderIdStr = (String) search.get(Constants.BINDERS_PARENT_ID_FIELD);
			if(Validator.isNotNull(parentBinderIdStr))
				parentBinderId = Long.valueOf(parentBinderIdStr);
			folderList.add(new FolderBrief(id,
					title,
					entityType,
					family,
					library,
					definitionType,
					path,
					creation,
					modification,
					permaLink,
					mirrored,
					createdWithDefinitionId,
					defaultViewDefinitionId,
					webdavUrl,
					rssUrl,
					icalUrl,
					atomUrl,
					parentBinderId));
		}
		FolderBrief[] array = new FolderBrief[folderList.size()];
		return new FolderCollection(firstRecord, 
				((Integer)searchResults.get(ObjectKeys.TOTAL_SEARCH_COUNT)).intValue(),
				folderList.toArray(array));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public TrashCollection binder_getTrashEntries(String accessToken, long binderId, int firstRecord, int maxRecords) {
		// Read the requested trash entries...
		Binder binder = getBinderModule().getBinder(binderId);
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_OFFSET,    Integer.valueOf(firstRecord));
		options.put(ObjectKeys.SEARCH_MAX_HITS,  Integer.valueOf(maxRecords));
		options.put(ObjectKeys.SEARCH_SORT_NONE, Boolean.TRUE);
		Map trashSearchMap = TrashHelper.getTrashEntities(this, binder, options);
		ArrayList trashSearchAL = ((ArrayList) trashSearchMap.get(ObjectKeys.SEARCH_ENTRIES));
		ArrayList<TrashEntity> trashEntriesAL = new ArrayList<TrashEntity>();
        for (Iterator trashEntriesIT=trashSearchAL.iterator(); trashEntriesIT.hasNext();) {
			trashEntriesAL.add(new TrashEntity((Map) trashEntriesIT.next()));
		}

        // ...convert them to TrashBrief's...
        int count = trashEntriesAL.size();
        TrashBrief[] trashBriefs = new TrashBrief[count];
        for (int i = 0; i < count; i += 1) {
        	TrashEntity te = ((TrashEntity) trashEntriesAL.get(i));
        	trashBriefs[i] = new TrashBrief(this, getCoreDao(), te);
        }
        
        // ...and return them as a TrashCollection.
        return new TrashCollection(firstRecord, count, trashBriefs);
	}

	@Override
	public void binder_deleteTag(String accessToken, long binderId, String tagId) {
		getBinderModule().deleteTag(binderId, tagId);
	}
	@Override
	public void binder_setTag(String accessToken, org.kablink.teaming.remoting.ws.model.Tag tag) {
		getBinderModule().setTag(tag.getEntityId(), tag.getName(), tag.isPublic());
	}
	@Override
	public org.kablink.teaming.remoting.ws.model.Tag[] binder_getTags(String accessToken, long binderId) {
		Collection<Tag>tags = getBinderModule().getTags(getBinderModule().getBinder(binderId));
		org.kablink.teaming.remoting.ws.model.Tag[] results = new org.kablink.teaming.remoting.ws.model.Tag[tags.size()];
		int i=0;
		for (Tag tag:tags) {
			results[i++] = toTagModel(tag);
		}
		return results;
	}
	
	@Override
	public FileVersions binder_getFileVersions(String accessToken, long binderId, String fileName) {
		Binder binder = getBinderModule().getBinder(binderId);
		FileAttachment att = binder.getFileAttachment(fileName);
		if(att != null)
			return toFileVersions(att);
		else
			throw new NoFileByTheNameException(fileName);
	}
	
	@Override
	public byte[] binder_getAttachmentAsByteArray(String accessToken, long binderId, String attachmentId) {
		Binder binder = getBinderModule().getBinder(binderId);
		return getFileAttachmentAsByteArray(binder, binder, attachmentId);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void binder_uploadFileAsByteArray(String accessToken, long binderId,
			String fileUploadDataItemName, String fileName, byte[] fileContent) {
		if (Validator.isNull(fileUploadDataItemName) || "ss_attachFile".equals(fileUploadDataItemName))
			fileUploadDataItemName="ss_attachFile1";
		File originalFile = new File(fileName);
		fileName = originalFile.getName();

		// Wrap it up in a datastructure expected by our app.
		Map fileItems = wrapFileItemInMap(fileUploadDataItemName, fileName, new ByteArrayInputStream(fileContent));
		
		try {
			// Finally invoke the business method. 
			getBinderModule().modifyBinder(new Long(binderId),  
				new EmptyInputData(), fileItems, null, null);
		} catch(WriteFilesException e) {
			throw new RemotingException(e);
		} catch (AccessControlException e) {
			throw new RemotingException(e);
		} catch (WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	@Override
	public boolean[] binder_testAccess(String accessToken, String workAreaOperationName, long[] binderIds) {
		WorkAreaOperation workAreaOperation = WorkAreaOperation.getInstance(workAreaOperationName);
		boolean[] result = new boolean[binderIds.length];
		for(int i = 0; i < binderIds.length; i++) {
			try {
				// Do not use BinderModule.getBinder() method to load the binder, since it will 
				// fail if the caller doesn't already have the appropriate right to load it. 
				Binder binder = getBinderModule().getBinderWithoutAccessCheck(binderIds[i]);
				result[i] = getAccessControlManager().testOperation(binder, workAreaOperation);
			}
			catch(NoBinderByTheIdException e) {
				// The specified binder does not exist. Instead of throwing an exception (and
				// aborting this operation all together), simply set the result to false for
				// this binder, and move on to the next binder.
				result[i] = false;
				continue;
			}
		}
		return result;
	}
	
	@Override
	public boolean[] binder_testOperation(String accessToken,
			String binderOperationName, long[] binderIds) {
		boolean[] result = new boolean[binderIds.length];
		BinderOperation binderOperation = null;
		try {
			binderOperation = BinderOperation.valueOf(binderOperationName);
		}
		catch(IllegalArgumentException e) {
			for(int i = 0; i < binderIds.length; i++)
				result[i] = false;
			return result;
		}
		for(int i = 0; i < binderIds.length; i++) {
			try {
				// Do not use BinderModule.getBinder() method to load the binder, since it will 
				// fail if the caller doesn't already have the appropriate right to load it. 
				Binder binder = getBinderModule().getBinderWithoutAccessCheck(binderIds[i]);
				result[i] = getBinderModule().testAccess(binder, binderOperation);
			}
			catch(NoBinderByTheIdException e) {
				// The specified binder does not exist. Instead of throwing an exception (and
				// aborting this operation all together), simply set the result to false for
				// this binder, and move on to the next binder.
				result[i] = false;
				continue;
			}
		}
		return result;
	}
	
	@Override
	public boolean[] binder_testOperations(String accessToken, String[] binderOperationNames, long binderId) {
		boolean[] result = new boolean[binderOperationNames.length];
		for(int i = 0; i < binderOperationNames.length; i++)
			result[i] = false;
		Binder binder;
		try {
			binder = getBinderModule().getBinderWithoutAccessCheck(binderId);
		}
		catch(NoBinderByTheIdException e) {
			return result;
		}
		BinderOperation binderOperation;
		for(int i = 0; i < binderOperationNames.length; i++) {
			try {
				binderOperation = BinderOperation.valueOf(binderOperationNames[i]);
				result[i] = getBinderModule().testAccess(binder, binderOperation);
			}
			catch(IllegalArgumentException e) {
				continue;
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	interface FolderSearcher {
		Map searchFolders();
	}
	
	@Override
	public long binder_getTopWorkspaceId(String accessToken) {
		return getWorkspaceModule().getTopWorkspaceId();
	}
	
    @Override
	public void binder_setDefinitionsInherited(String accessToken, long binderId, boolean inheritFromParent) {
    	getBinderModule().setDefinitionsInherited(binderId, inheritFromParent);
    }
    
    @Override
    public int binder_checkQuotaAndFileSizeLimit(String accessToken, Long userId, long binderId, long fileSize, String fileName) {
		Binder binder = getBinderModule().getBinder(binderId);
    	return getFileModule().checkQuotaAndFileSizeLimit(userId, binder, fileSize, fileName);
    }
}
