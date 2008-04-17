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
package com.sitescape.team.module.workspace.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.InternalException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.BinderComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoWorkspaceByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.lucene.Hits;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.SearchUtils;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.util.Validator;

/**
 * @author Jong Kim
 *
 */
public class WorkspaceModuleImpl extends CommonDependencyInjection implements WorkspaceModule {

	protected Log logger = LogFactory.getLog(getClass());
    protected DefinitionModule definitionModule;

	public boolean testAccess(Workspace workspace, WorkspaceOperation operation) {
		try {
			checkAccess(workspace, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	public void checkAccess(Workspace workspace, WorkspaceOperation operation) throws AccessControlException {
		switch (operation) {
		case addFolder:
	    	getAccessControlManager().checkOperation(workspace, WorkAreaOperation.CREATE_FOLDERS);
	    	break;
		case addWorkspace:
	    	getAccessControlManager().checkOperation(workspace, WorkAreaOperation.CREATE_WORKSPACES);
	    	break;
		case changeEntryTimestamps:
			getAccessControlManager().checkOperation(workspace, WorkAreaOperation.SITE_ADMINISTRATION);
			break;
		default:
	    	throw new NotSupportedException(operation.toString(), "checkAccess");
		}
	}

    protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	private Workspace loadWorkspace(Long workspaceId)  {
        Workspace workspace = (Workspace)getCoreDao().loadBinder(workspaceId, RequestContextHolder.getRequestContext().getZoneId());
        if (workspace.isDeleted()) throw new NoBinderByTheIdException(workspace.getId());
        return workspace;
		
	}
	private BinderProcessor loadProcessor(Workspace workspace) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.team.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
		return (BinderProcessor)getProcessorManager().getProcessor(workspace, BinderProcessor.PROCESSOR_KEY);
	}

	public Workspace getWorkspace() 
   		throws NoWorkspaceByTheIdException, AccessControlException {
    	return getWorkspace(null);
    }
    public Workspace getWorkspace(Long workspaceId) 
    	throws NoWorkspaceByTheIdException, AccessControlException {
        Workspace workspace=null;        
         
        if (workspaceId == null) {
        	workspace = getCoreDao().findTopWorkspace(RequestContextHolder.getRequestContext().getZoneName());
        } else {
        	workspace = (Workspace)getCoreDao().loadBinder(workspaceId, RequestContextHolder.getRequestContext().getZoneId());  
        }
        if (workspace.isDeleted()) throw new NoBinderByTheIdException(workspace.getId());
		// Check if the user has "read" access to the workspace.
		getAccessControlManager().checkOperation(workspace, WorkAreaOperation.READ_ENTRIES);
 
       return workspace;
    }
    public Workspace getTopWorkspace() {
		Workspace top = getCoreDao().findTopWorkspace(RequestContextHolder.getRequestContext().getZoneName());
		return top;
    }
   	public Collection getWorkspaceTree(Long id) throws AccessControlException {
    	Workspace top = getWorkspace(id);
        return getWorkspaceTree(top);
    }
   	
   	public Collection getWorkspaceTree(Workspace top) {
        User user = RequestContextHolder.getRequestContext().getUser();
      	//order result
        Comparator c = new BinderComparator(user.getLocale(), BinderComparator.SortByField.title);
       	TreeSet<Binder> tree = new TreeSet<Binder>(c);
     	for (Iterator iter=top.getBinders().iterator(); iter.hasNext();) {
    		Binder b = (Binder)iter.next();
    		if (b.isDeleted()) continue;
    		// To make this method consistent with the Dom construction counterpart
    		// (ie, getDomWorkspaceTree), the following additional check is necessary 
    		// before testing its access control.
    		if ((b instanceof Folder) || (b instanceof Workspace)) {
    			// Check if the user has "read" access to the binder.
    			if (getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES))
    				tree.add(b);
    		}
        }
     	return tree;
   	}
    	 
   	public Set<String> getChildrenTitles(Workspace top) {
       	TreeSet<String> titles = new TreeSet<String>();
     	for (Iterator iter=top.getBinders().iterator(); iter.hasNext();) {
    		Binder b = (Binder)iter.next();
       		if (b.isDeleted()) continue;
       	 	// To make this method consistent with the Dom construction counterpart
    		// (ie, getDomWorkspaceTree), the following additional check is necessary 
    		// before testing its access control.
       		if ((b instanceof Folder) || (b instanceof Workspace)) {
       		     	// Check if the user has "read" access to the binder.
       			if(getAccessControlManager().testOperation(b, WorkAreaOperation.READ_ENTRIES))
       				titles.add(b.getTitle());
       		}
        }
     	return titles;
   		
   	}
   	
    public org.dom4j.Document getDomWorkspaceTree(DomTreeBuilder domTreeHelper) throws AccessControlException {
       	return getDomWorkspaceTree(null, domTreeHelper, -1);
    }
    public org.dom4j.Document getDomWorkspaceTree(Long id, DomTreeBuilder domTreeHelper, int levels) 
    		throws AccessControlException {
    	//getWorkspace does access check
    	Workspace top = getWorkspace(id);
 
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new BinderComparator(user.getLocale(),BinderComparator.SortByField.searchTitle);
    	Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	buildWorkspaceDomTree(rootElement, top, c, domTreeHelper, levels);
    	return wsTree;
    }
    
    public org.dom4j.Document getDomWorkspaceTree(Long topId, Long bottomId, DomTreeBuilder domTreeHelper) 
    		throws AccessControlException {
        User user = RequestContextHolder.getRequestContext().getUser();
       	//getWorkspace does access check
    	Workspace top = getWorkspace(topId);
 		Workspace bottom = (Workspace)getCoreDao().loadBinder(bottomId, user.getZoneId());
        
        List<Workspace> ancestors = new ArrayList<Workspace>();
        Workspace parent = bottom;
        //build inverted list of parents
        while ((parent != null) && !parent.equals(top)) {
        	ancestors.add(parent);
        	parent = (Workspace)parent.getParentBinder();
        }
        if (parent == null) throw new InternalException("Top is not a parent"); 
        ancestors.add(parent);
        Comparator c = new BinderComparator(user.getLocale(),BinderComparator.SortByField.searchTitle);
    	Document wsTree = DocumentHelper.createDocument();
    	Element rootElement = wsTree.addElement(DomTreeBuilder.NODE_ROOT);
    	for (int i=ancestors.size()-1; i>=0; --i) {
    		buildWorkspaceDomTree(rootElement, (Workspace)ancestors.get(i), c, domTreeHelper, 1);
    		if (i != 0) {
    			parent = ancestors.get(i-1);
    			String parentId = parent.getId().toString();
    			Iterator itRootElements = rootElement.selectNodes("./" + DomTreeBuilder.NODE_CHILD).iterator();
    			rootElement = null;
    			while (itRootElements.hasNext()) {
    				Element childNode = (Element)itRootElements.next();
    				String id = childNode.attributeValue("id");
    				int n = id.indexOf(".");
    				if (n >= 0) id = id.substring(0, n);
    				if (id.equals(parentId)) {
    					rootElement = childNode;
    					break;
    				}
    			}
    			if (rootElement == null) break;
    		}
    	}
    	return wsTree;
    }
 
    protected void buildWorkspaceDomTree(Element current, Workspace top, Comparator c, 
    		DomTreeBuilder domTreeHelper, int levels) {
    	Element next; 
    	
 		//callback to setup tree
    	domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_WORKSPACE, top, current);
 		if (levels == 0) return;
    	--levels;
		TreeSet ws = new TreeSet(c);
		List searchBinders = null;
		if (levels >= 0 && (!domTreeHelper.getPage().equals("") || top.getBinderCount() > 10)) {  //what is the best number to avoid search??
			//do search
			BinderProcessor processor = loadProcessor(top);
			if (domTreeHelper.getPage().equals("")) {
				Map options = new HashMap();
				options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(SPropsUtil.getInt("wsTree.maxBucketSize")));
				Map searchResults = processor.getBinders(top, options);
				searchBinders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);
				int results = (Integer)searchResults.get(ObjectKeys.TOTAL_SEARCH_COUNT);
				if (results > SPropsUtil.getInt("wsTree.maxBucketSize")) { //just to get started
					searchResults = buildVirtualTree(current, top, domTreeHelper, results);
					//If no results are returned, the work was completed in buildVirtualTree and we can exit now
					if (searchResults == null) return;
					searchBinders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);
				}
			} else {
				//We are looking for a virtual page
				Map searchResults = buildVirtualTree(current, top, domTreeHelper, 0);
				//If no results are returned, the work was completed in buildVirtualTree and we can exit now
				if (searchResults == null) return;
				searchBinders = (List)searchResults.get(ObjectKeys.SEARCH_ENTRIES);
			}
			if (domTreeHelper.supportsType(DomTreeBuilder.TYPE_FOLDER, null)) {
				//get folders
				for (int i=0; i<searchBinders.size(); ++i) {
					Map search = (Map)searchBinders.get(i);
					String entityType = (String)search.get(EntityIndexUtils.ENTITY_FIELD);
					if (EntityType.folder.name().equals(entityType)) {
						String sId = (String)search.get(EntityIndexUtils.DOCID_FIELD);
						try {
							Long id = Long.valueOf(sId);
							Object obj = getCoreDao().load(Folder.class, id);
							if (obj != null) ws.add(obj);
						} catch (Exception ex) {continue;}					
					}				
				}
				for (Iterator iter=ws.iterator(); iter.hasNext();) {
	 				Folder f = (Folder)iter.next();
	 	      		if (f.isDeleted()) continue;
	 				// 	Check if the user has "read" access to the folder.
	 				next = current.addElement(DomTreeBuilder.NODE_CHILD);
	 				if (domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_FOLDER, f, next) == null) 
	 					current.remove(next);
	 			}
	        }
	    	ws.clear();
			//get workspaces
			for (int i=0; i<searchBinders.size(); ++i) {
				Map search = (Map)searchBinders.get(i);
				String entityType = (String)search.get(EntityIndexUtils.ENTITY_FIELD);
				if (!EntityType.folder.name().equals(entityType)) {
					String sId = (String)search.get(EntityIndexUtils.DOCID_FIELD);
					try {
						Long id = Long.valueOf(sId);
						Object obj = getCoreDao().load(Workspace.class, id);
						if (obj != null) ws.add(obj);
					} catch (Exception ex) {continue;}					
				}
			}
	    	
	      	for (Iterator iter=ws.iterator(); iter.hasNext();) {
	     		Workspace w = (Workspace)iter.next();
	      		if (w.isDeleted()) continue;
	      		next = current.addElement(DomTreeBuilder.NODE_CHILD);
	   			buildWorkspaceDomTree(next, w, c, domTreeHelper, levels);
	       	}    
      	} else {
			if (domTreeHelper.supportsType(DomTreeBuilder.TYPE_FOLDER, null)) {
				//get folders sorted
				ws.addAll(top.getFolders());
				for (Iterator iter=ws.iterator(); iter.hasNext();) {
	 				Folder f = (Folder)iter.next();
	 	      		if (f.isDeleted()) continue;
	 				// 	Check if the user has "read" access to the folder.
					if(!getAccessControlManager().testOperation(f, WorkAreaOperation.READ_ENTRIES))
		 				continue;
	 				next = current.addElement(DomTreeBuilder.NODE_CHILD);
	 				if (domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_FOLDER, f, next) == null) 
	 					current.remove(next);
				}
			} 
			ws.clear();
			//handle sorted workspaces
			ws.addAll(top.getWorkspaces());
	      	for (Iterator iter=ws.iterator(); iter.hasNext();) {
	     		Workspace w = (Workspace)iter.next();
	      		if (w.isDeleted()) continue;
	     		// Check if the user has "read" access to the workspace.
				if(!getAccessControlManager().testOperation(w, WorkAreaOperation.READ_ENTRIES))
	 				continue;
	      		next = current.addElement(DomTreeBuilder.NODE_CHILD);
	   			buildWorkspaceDomTree(next, w, c, domTreeHelper, levels);
			}
				
		}
	
    }
    //Build a list of buckets (or get the final page)
    protected Map buildVirtualTree(Element current, Workspace top, DomTreeBuilder domTreeHelper, int totalHits) {
    	Element next;
    	int maxBucketSize = SPropsUtil.getInt("wsTree.maxBucketSize");
    	int skipLength = maxBucketSize;
    	if (totalHits > maxBucketSize) {
    		skipLength = totalHits / maxBucketSize;
    		if (skipLength < maxBucketSize) skipLength = maxBucketSize;
    	}

    	//See if this has a page already set
    	List tuple = domTreeHelper.getTuple();
    	String tuple1 = "";
    	String tuple2 = "";
    	if (tuple != null && tuple.size() >= 2) {
    		tuple1 = (String) tuple.get(0);
    		tuple2 = (String) tuple.get(1);
    	}
    	
    	Document queryTree = DocumentHelper.createDocument();
		Element qTreeRootElement = queryTree.addElement(QueryBuilder.QUERY_ELEMENT);
		Element qTreeAndElement = qTreeRootElement.addElement(QueryBuilder.AND_ELEMENT);
		
		Element field = qTreeAndElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDERS_PARENT_ID_FIELD);
		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(top.getId().toString());
   	
		field = qTreeAndElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
		child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(BasicIndexUtils.DOC_TYPE_BINDER);
      	//Create the Lucene query
    	Set pids = getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser());
    	QueryBuilder qb = new QueryBuilder(pids);
    	SearchObject so = qb.buildQuery(queryTree);
    	if(logger.isDebugEnabled()) {
    		logger.debug("Query is: " + queryTree.asXML());
    	}
    	
    	//Set the sort order
   		SortField[] fields = new SortField[1];
   		String sortBy = EntityIndexUtils.NORM_TITLE;   		
    	
    	fields[0] = new SortField(sortBy,  SortField.AUTO, true);
    	so.setSortBy(fields);
    	Query soQuery = so.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice   	
    	if(logger.isDebugEnabled()) {
    		logger.debug("Query is: " + soQuery.toString());
    	}
 
    	//Before doing the search, create another query in case the buckets are exhausted
    	Document queryTreeFinal = DocumentHelper.createDocument();
		qTreeRootElement = queryTreeFinal.addElement(QueryBuilder.QUERY_ELEMENT);
		qTreeAndElement = qTreeRootElement.addElement(QueryBuilder.AND_ELEMENT);
		
		field = qTreeAndElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDERS_PARENT_ID_FIELD);
		child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(top.getId().toString());
   	
		field = qTreeAndElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
		child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(BasicIndexUtils.DOC_TYPE_BINDER);

		QueryBuilder qbFinal = new QueryBuilder(pids);
    	SearchObject singleBucketSO = qbFinal.buildQuery(queryTreeFinal);
		
   		Element range = qTreeAndElement.addElement(QueryBuilder.RANGE_ELEMENT);
   		range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.NORM_TITLE);
   		range.addAttribute(QueryBuilder.INCLUSIVE_ATTRIBUTE, QueryBuilder.INCLUSIVE_TRUE);
		Element start = range.addElement(QueryBuilder.RANGE_START);
		start.setText(tuple1);
		Element end = range.addElement(QueryBuilder.RANGE_FINISH);
		end.setText(tuple2);

		//Create the Lucene query
		SearchObject soFinal = qbFinal.buildQuery(queryTreeFinal);
    	if(logger.isDebugEnabled()) {
    		logger.debug("Final query is: " + queryTreeFinal.asXML());
    	}
    	
    	//Set the sort order
   		SortField[] fieldsFinal = new SortField[1];
   		String sortByFinal = EntityIndexUtils.NORM_TITLE;   		
    	
    	fieldsFinal[0] = new SortField(sortByFinal,  SortField.AUTO, true);
    	soFinal.setSortBy(fieldsFinal);
    	Query soQueryFinal = soFinal.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice   	
    	if(logger.isDebugEnabled()) {
    		logger.debug("Query is: " + soQueryFinal.toString());
    	}
    	LuceneSession luceneSession = getLuceneSessionFactory().openSession();
        
    	List results = new ArrayList();
    	Hits hits = null;
    	try {
    		if (totalHits == 0) {
    			//We have to figure out the size of the pool before building the buckets
    			Hits testHits = luceneSession.search(soQueryFinal, soFinal.getSortBy(), 0, maxBucketSize);
    			totalHits = testHits.getTotalHits();
    			if (totalHits > maxBucketSize) {
    				skipLength = testHits.getTotalHits() / maxBucketSize;
    				if (skipLength < maxBucketSize) skipLength = maxBucketSize;
    			}
    		}
	        if (totalHits > skipLength) results = luceneSession.getNormTitles(soQuery, tuple1, tuple2, skipLength);
	        if (results == null || results.size() <= 1) {
	        	//We must be at the end of the buckets; now get the real entries
	        	if ("".equals(tuple1) && "".equals(tuple2)) {
	            	singleBucketSO.setSortBy(fieldsFinal);
	            	soQueryFinal = singleBucketSO.getQuery();    //Get the query into a variable to avoid doing this very slow operation twice
	        	}
	        	hits = luceneSession.search(soQueryFinal, soFinal.getSortBy(), 0, -1);
	        }
        }
        finally {
            luceneSession.close();
        }
        //See if we are at the end of the bucket search
        if (hits != null) {
    	    List entries = SearchUtils.getSearchEntries(hits);
    	    //SearchUtils.extendPrincipalsInfo(entries, getProfileDao());
                   
            Map retMap = new HashMap();
            retMap.put(ObjectKeys.SEARCH_ENTRIES,entries);
            retMap.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(hits.getTotalHits()));
            retMap.put(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, new Integer(hits.length()));
     
            domTreeHelper.setPage("");
        	return retMap; 
        }
        //Build the virtual tree
        String page = domTreeHelper.getPage();
        if (!page.equals("")) page += ".";
        for (int i = 0; i < results.size(); i++) {
        	List result = (List) results.get(i);
        	Map skipMap = new HashMap();
        	skipMap.put(DomTreeBuilder.SKIP_TUPLE, result);
        	skipMap.put(DomTreeBuilder.SKIP_PAGE, page + String.valueOf(i));
        	skipMap.put(DomTreeBuilder.SKIP_BINDER_ID, top.getId().toString());
			next = current.addElement(DomTreeBuilder.NODE_CHILD);
			if (domTreeHelper.setupDomElement(DomTreeBuilder.TYPE_SKIPLIST, skipMap, next) == null) current.remove(next);
        }
        return null;
    }
    //no transaction by default     
    public Long addFolder(Long parentWorkspaceId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
    	return addFolder(parentWorkspaceId, definitionId, inputData, fileItems, null);
    }
    //no transaction by default     
    public Long addFolder(Long parentWorkspaceId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options) throws AccessControlException, WriteFilesException {

    	Workspace parentWorkspace = loadWorkspace(parentWorkspaceId);
    	checkAccess(parentWorkspace, WorkspaceOperation.addFolder);
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(parentWorkspace, WorkspaceOperation.changeEntryTimestamps);
    	Definition def = null;
    	if (Validator.isNotNull(definitionId)) { 
    		def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
    	}
    	        
    	Binder binder = loadProcessor(parentWorkspace).addBinder(parentWorkspace, def, Folder.class, inputData, fileItems, options);
    	return binder.getId();
   }
 
    public Long addWorkspace(Long parentWorkspaceId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException {
    	return addWorkspace(parentWorkspaceId, definitionId, inputData, fileItems, null);
    }
    public Long addWorkspace(Long parentWorkspaceId, String definitionId, InputDataAccessor inputData,
       		Map fileItems, Map options) throws AccessControlException, WriteFilesException {
    	Workspace parentWorkspace = loadWorkspace(parentWorkspaceId);
 		    
    	Definition def = null;
    	if (Validator.isNotNull(definitionId)) { 
    		def = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
    	}
    	//allow users workspaces to be created for all users
    	if (parentWorkspace.isReserved() && ObjectKeys.PROFILE_ROOT_INTERNALID.equals(parentWorkspace.getInternalId())) { 
    		if ((def == null) || (def.getType() != Definition.USER_WORKSPACE_VIEW)) {
    			checkAccess(parentWorkspace, WorkspaceOperation.addWorkspace);
    		}
    	} else {
    		checkAccess(parentWorkspace, WorkspaceOperation.addWorkspace);
    	}
		if (options != null && (options.containsKey(ObjectKeys.INPUT_OPTION_CREATION_DATE) || 
				options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)))
			checkAccess(parentWorkspace, WorkspaceOperation.changeEntryTimestamps);
      	
    	return loadProcessor(parentWorkspace).addBinder(parentWorkspace, def, Workspace.class, inputData, fileItems, options).getId();
    }
 
}