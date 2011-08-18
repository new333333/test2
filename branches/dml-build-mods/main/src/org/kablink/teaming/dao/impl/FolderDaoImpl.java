/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.dao.KablinkDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.AnyOwner;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.util.Constants;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * @author Jong Kim
 *
 */
public class FolderDaoImpl extends KablinkDao implements FolderDao {
	private CoreDao coreDao;

	public void setCoreDao(CoreDao coreDao) {
	   this.coreDao = coreDao;
	}
	protected CoreDao getCoreDao() {
	    return coreDao;
	}
	 	
	/**
    * Load 1 FolderEntry 
     * @param parentFolderId 
     * @param entryId
     * @param zoneId
     * @return
     * @throws DataAccessException
	 */
	public FolderEntry loadFolderEntry(Long parentFolderId, Long entryId, Long zoneId) throws DataAccessException,NoFolderEntryByTheIdException {
		long begin = System.nanoTime();
		try {
	       return loadEntry(parentFolderId, entryId, zoneId);
    	}
    	finally {
    		end(begin, "loadFolderEntry(Long,Long,Long)");
    	}	        
    }
      
	public FolderEntry loadFolderEntry(final String sortKey, final Long zoneId) throws DataAccessException,NoFolderEntryByTheIdException {
		long begin = System.nanoTime();
		try {
	        FolderEntry entry = (FolderEntry)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                         Criteria crit = session.createCriteria(FolderEntry.class)
	                         	.add(Expression.eq("HKey.sortKey", sortKey))  
	                         	//.setFetchMode("entryDef", FetchMode.SELECT)	
	                         	.setFetchMode(ObjectKeys.FIELD_ENTITY_PARENTBINDER, FetchMode.SELECT)	
	                         	.setFetchMode("topEntry", FetchMode.SELECT);	
	                         List objs = crit.list();
	                         if (objs.isEmpty()) throw new NoFolderEntryByTheIdException(sortKey);
	                         return (FolderEntry)objs.get(0);
	                     }
	               }
	               
	    		);
	        //check zone  just in case
	        if (!zoneId.equals(entry.getZoneId())) throw new NoFolderEntryByTheIdException(sortKey);
	        return entry;
    	}
    	finally {
    		end(begin, "loadFolderEntry(String,Long)");
    	}	        

	}
	public FolderEntry loadFolderEntry(Long entryId, Long zoneId) throws DataAccessException,NoFolderEntryByTheIdException {
		long begin = System.nanoTime();
		try {
			return loadEntry(null, entryId, zoneId);
    	}
    	finally {
    		end(begin, "loadFolderEntry(Long,Long)");
    	}	        
    }
	protected FolderEntry loadEntry(Long parentFolderId, Long entryId, Long zoneId) throws DataAccessException {
		FolderEntry entry = (FolderEntry)getCoreDao().load(FolderEntry.class, entryId);
		if (entry == null) throw new NoFolderEntryByTheIdException(entryId);
        if (!entry.getZoneId().equals(zoneId)) {
        	throw new NoFolderEntryByTheIdException(entryId);
        }
        if (parentFolderId != null && !parentFolderId.equals(entry.getParentFolder().getId())) {
        	throw new NoFolderEntryByTheIdException(entryId);        	
        }
        return entry;
     }
     /**
     * Query for a collection of FolderEntries.  An iterator is returned.  The entries are 
     * not pre-loaded.
     */
    public SFQuery queryEntries(final Folder folder, FilterControls filter) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
	    	final FilterControls myFilter = filter==null?new FilterControls():filter;
	    	myFilter.add(ObjectKeys.FIELD_ENTITY_PARENTBINDER, folder);
	    	Query query = (Query)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                        //sqlqueries, filters and criteria don't help with frontbase problem
	                        Query query = session.createQuery("from org.kablink.teaming.domain.FolderEntry d " + myFilter.getFilterString("d"));
	                        	
	                		List filterValues = myFilter.getFilterValues();
	               			for (int i=0; i<filterValues.size(); ++i) {
	               				query.setParameter(i, filterValues.get(i));
	                		}
	                        return query;
	                    }
	                }
	            );  
	       return new SFQuery(query);
    	}
    	finally {
    		end(begin, "queryEntries(Folder,FilterControls)");
    	}	        
    }
    /**
     * Load child entries of a folder 
     */
    public List<FolderEntry> loadEntries(final Folder folder, FilterControls filter) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
	    	final FilterControls myFilter = filter==null?new FilterControls():filter;
	    	myFilter.add(ObjectKeys.FIELD_ENTITY_PARENTBINDER, folder);
	    	List result = (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                        //sqlqueries, filters and criteria don't help with frontbase problem
	                        Query query = session.createQuery("from org.kablink.teaming.domain.FolderEntry d " + myFilter.getFilterString("d"));
	                        	
	                		List filterValues = myFilter.getFilterValues();
	               			for (int i=0; i<filterValues.size(); ++i) {
	               				query.setParameter(i, filterValues.get(i));
	                		}
	                        return query.list();
	                    }
	                }
	            );  
	       return result;
    	}
    	finally {
    		end(begin, "loadEntries(Folder,FilterControls)");
    	}	        
    }

 	/*
     * Load the ancestors and descendants of an entry.  Entry will be included in List
     */
    public List loadEntryTree(final FolderEntry entry) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
	        List result;
	        final String[] keys = entry.getHKey().getAncestorKeys();  
	       
	       if (keys == null) {
	           result = loadEntryDescendants(entry);
	           result.add(0, entry);
	       } else {
	    	   //load ancestors and descendants
	           result = (List)getHibernateTemplate().execute(
	                   new HibernateCallback() {
	                       public Object doInHibernate(Session session) throws HibernateException {
	                   		int nextPos = entry.getHKey().getLastNumber() + 1;
	               		 	HKey next = new HKey(entry.getParentEntry().getHKey(), nextPos);    
	                            return session.createCriteria(FolderEntry.class)
	                            	.add(Expression.disjunction()
	                                	.add(Expression.in("HKey.sortKey", keys))  // ancestors
	                                	.add(Expression.conjunction() //descendants
	                                			.add(Expression.ge("HKey.sortKey", entry.getHKey().getSortKey()))
	                                			.add(Expression.lt("HKey.sortKey", next.getSortKey()))
	                                	)
	                            	 )
	                            	//.setFetchMode("entryDef", FetchMode.SELECT)	
	                            	.setFetchMode(ObjectKeys.FIELD_ENTITY_PARENTBINDER, FetchMode.SELECT)	
	                            	.setFetchMode("topEntry", FetchMode.SELECT)	
	                            	.addOrder(Order.asc("HKey.sortKey"))
	                            	.list();
	                        }
	                  }
	                  
	       		);
	       	}
	        return result;
    	}
    	finally {
    		end(begin, "loadEntryTree(FolderEntry)");
    	}	        
     }     
    /**
     * Given a folder entry, pre-load its chain of FolderEntry ancestors 
     */
    public List loadEntryAncestors(final FolderEntry entry) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
	        List result = (List)getHibernateTemplate().execute(
	             new HibernateCallback() {
	                 public Object doInHibernate(Session session) throws HibernateException {
	                     String[] keys = entry.getHKey().getAncestorKeys();  
	                     return  session.createCriteria(entry.getClass())
	                     	.add(Expression.in("HKey.sortKey", keys))
	                       	//.setFetchMode("entryDef", FetchMode.SELECT)	
	                       	.setFetchMode(ObjectKeys.FIELD_ENTITY_PARENTBINDER, FetchMode.SELECT)	
	                       	.setFetchMode("topEntry", FetchMode.SELECT)	
	                     	.addOrder(Order.asc("HKey.sortKey"))
	                     	.list();
	                 }
	             }
	         );  
	         return result;
    	}
    	finally {
    		end(begin, "loadEntryAncestors(FolderEntry)");
    	}	        
     }  
    /**
     * Given a FolderEntry, pre-load all descendents of the entry.
     */
    public List loadEntryDescendants(final FolderEntry entry) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
	        List result = (List)getHibernateTemplate().execute(
	             new HibernateCallback() {
	                 public Object doInHibernate(Session session) throws HibernateException {
	                	 Criteria crit;
	                	 if (entry.getDocLevel() > 1) {
	                		int nextPos = entry.getHKey().getLastNumber() + 1;
	               		 	HKey next = new HKey(entry.getParentEntry().getHKey(), nextPos);    
	        				crit = session.createCriteria(FolderEntry.class)
	        					.add(Expression.conjunction()
	                    			.add(Expression.gt("HKey.sortKey", entry.getHKey().getSortKey()))
	                    			.add(Expression.lt("HKey.sortKey", next.getSortKey()))
								);
	                	 } else {
	                		 //this works better as an index with the DBs
	                		 crit = session.createCriteria(FolderEntry.class)
	                		 	.add(Expression.eq("topEntry", entry));                 		 
	                	 };
	                     //crit.setFetchMode("entryDef", FetchMode.SELECT);	
	                     crit.setFetchMode(ObjectKeys.FIELD_ENTITY_PARENTBINDER, FetchMode.SELECT);	
	                     crit.setFetchMode("topEntry", FetchMode.SELECT);	
	                     crit.addOrder(Order.asc("HKey.sortKey"));
	                     return crit.list();
	                 }
	             }
	         );  
	         return result;
    	}
    	finally {
    		end(begin, "loadEntryDescendants(FolderEntry)");
    	}	        
     } 

    /**
     * Get all of the ids for entries of a specified definitiontype.
     */
	public List<Long> getFolderEntriesByType(final Long zoneId, final Folder folder, final String defId) {
		long begin = System.nanoTime();
		try {
			List<Long> result = new ArrayList<Long>();
			result = (List) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					ProjectionList proj = Projections.projectionList()
						.add(Projections.groupProperty("id"));
					Criteria crit = session.createCriteria(FolderEntry.class)
						.setProjection(proj)
							.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
							.add(Restrictions.eq(ObjectKeys.FIELD_ENTITY_PARENTBINDER, folder))
							.add(Restrictions.eq(ObjectKeys.FIELD_ENTITY_DEFID, defId))
							.setCacheable(false);
					return crit.list();
				}});
			return result;
		}
		finally {
			end(begin, "getFolderEntriesByType()");
		}
	}
	
    //Change a set of entries to have a new definition id
    public void setFolderEntryType(final Folder folder, final List<Long> entryIds, final String newDefId) {
		long begin = System.nanoTime();
		try {
		   if (entryIds.isEmpty()) return;
		   getHibernateTemplate().execute(
	        	   	new HibernateCallback() {
	        	   		public Object doInHibernate(Session session) throws HibernateException {
	       		   			session.createQuery("Update org.kablink.teaming.domain.FolderEntry set entryDefId=:entryDefId where id in (:pList)")
	       		   			.setString("entryDefId", newDefId)
	    	   				.setParameterList("pList", entryIds)
	    	   				.executeUpdate();
	               	   		return null;
	        	   		}
	        	   	}
	        	 );    	
    	}
    	finally {
    		end(begin, "setFolderEntryType");
    	}	        
             		 
    }


 
    /**
     * Load all entries of a folder and it sub-folders that have been updated with a specified range.
     */
	public List loadFolderTreeUpdates(Folder folder, Date since, Date before) {
		long begin = System.nanoTime();
		try {
			return loadFolderTreeUpdates(folder, since, before, new OrderBy(Constants.ID), -1);
    	}
    	finally {
    		end(begin, "loadFolderTreeUpdates(Folder,Date,Date)");
    	}	        
	}

	/**
	 * See <code>loadFolderTreeUpdates</code>. Order results as specified.
	 */
	public List loadFolderTreeUpdates(final Folder folder, final Date since, final Date before, final OrderBy order, final int maxResults) {
		long begin = System.nanoTime();
		try {
	        List entries = (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                    	//only need to check for modification date which is the latest
	                    	Query q  = session.createQuery("from org.kablink.teaming.domain.FolderEntry x where owningBinderKey like '" + 
	                    			folder.getBinderKey().getSortKey() + "%' and (x.modification.date > ? and x.modification.date <= ?) order by " + order.getOrderByClause("x"));
							
	                		int i=0;
							q.setTimestamp(i++, since);
							q.setTimestamp(i++, before);
							if (maxResults > 0) {
								q.setMaxResults(maxResults);
							}
							return q.list();
	                    }
	                }
	            );
			return entries;
    	}
    	finally {
    		end(begin, "loadFolderTreeUpdates(Folder,Date,Date,OrderBy,int)");
    	}	        
    }	

	/**
	 * Load 1 folder
	 */
    public Folder loadFolder(Long folderId, Long zoneId) throws DataAccessException {
		long begin = System.nanoTime();
		try {
	        if (folderId == null) {throw new NoFolderByTheIdException(folderId);}
	       
	        try {
	        	Folder folder = (Folder)getHibernateTemplate().get(Folder.class, folderId);
	        	if (folder == null) {throw new NoFolderByTheIdException(folderId);}
	        	if (!folder.getZoneId().equals(zoneId)) {
	        		throw new NoFolderByTheIdException(folderId);
	        	}
	            return folder;
	        } catch (ClassCastException ce) {
	        	throw new NoFolderByTheIdException(folderId);
	        }
    	}
    	finally {
    		end(begin, "loadFolder(Long,Long)");
    	}	        
    }
	

	/**
	 * Delete the folder object and its assocations.
	 * Folder entries and child binders should already have been deleted
	 */
   public void delete(final Folder folder) {
		long begin = System.nanoTime();
		try {
		   //cleanup entries - 
	       	getHibernateTemplate().execute(
	           	new HibernateCallback() {
	           		public Object doInHibernate(Session session) throws HibernateException {
			   			//delete ratings/visits for these entries
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where entityId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.FolderEntry p where " +
			   			  			" p.parentBinder=:folder) and entityType=:entityType")
			   			  	.setEntity("folder", folder)
			   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
			   				.executeUpdate();
			   			//delete subscriptions to these entries
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where entityId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.FolderEntry p where " +
			   			  			" p.parentBinder=:folder) and entityType=:entityType")
			   			  	.setEntity("folder", folder)
			   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
			   				.executeUpdate();
			   			//delete tags for these entries
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where entity_id in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.FolderEntry p where " +
			   			  			" p.parentBinder=:folder) and entity_type=:entityType")
			   			  	.setEntity("folder", folder)
			   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
			   				.executeUpdate();
			   			//delete shares for these entries
	 		   			session.createQuery("Delete org.kablink.teaming.domain.SharedEntity where entityId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.FolderEntry p where " +
			   			  			" p.parentBinder=:folder) and entityType=:entityType")
			   			  	.setEntity("folder", folder)
			   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.name())
			   				.executeUpdate();
	 		   			//brute force delete of jbpm data structures
	   		   			//load top level tokens
	 		   		   	Set tokenIds = new HashSet(session.createQuery("select w.tokenId from org.kablink.teaming.domain.WorkflowState w where w.owner.owningBinderId=:id")
	 		   		   			.setLong("id", folder.getId().longValue())
	 		   		   			.list());
	 		   		   	workflowDelete(tokenIds, session);
	 		   			//remove foreign keys or mysql complains
	        	  		session.createQuery("Update org.kablink.teaming.domain.FolderEntry set parentEntry=null,topEntry=null where parentBinder=:parent")
	        	  			.setEntity("parent", folder)
	   	   					.executeUpdate();
	        	  		//the delete of the folder in coreDao will handle associations through owningBinderId + LibraryEntries + entries connected by parentBinder
	        	  	   getCoreDao().delete(folder, FolderEntry.class);
	      	  			//if these are ever cached in secondary cache, clear them out.
			   	   		return null;
	        	   		}
	        	   	}
	        	 );    	
    	}
    	finally {
    		end(begin, "delete(Folder)");
    	}	        
    	
    }
    //mark entries deleted - used when deleting entries in bulk and want
    //to exclude some from future queries
    //entries evicted from cache
    public void markEntriesDeleted(final Folder folder, final Collection<FolderEntry> entries) {
		long begin = System.nanoTime();
		try {
		   if (entries.isEmpty()) return;
		   getHibernateTemplate().execute(
	        	   	new HibernateCallback() {
	        	   		public Object doInHibernate(Session session) throws HibernateException {
	               	   	   	Set ids = new HashSet();
	               			
	            			for (FolderEntry p:entries) {
	            	    		ids.add(p.getId());
	            	    		session.evict(p);
	            	    	}
	       		   			session.createQuery("Update org.kablink.teaming.domain.FolderEntry set deleted=:delete where id in (:pList)")
	       		   			.setBoolean("delete", Boolean.TRUE)
	    	   				.setParameterList("pList", ids)
	    	   				.executeUpdate();
	               	   		return null;
	        	   		}
	        	   	}
	        	 );    	
    	}
    	finally {
    		end(begin, "markEntriesDeleted(Folder,Collection<FolderEntry>)");
    	}	        
             		 
    }
    public void deleteEntries(final Folder folder, final Collection<FolderEntry> entries) {
		long begin = System.nanoTime();
		try {
	    	if (entries.isEmpty()) return;
	 
	    	getHibernateTemplate().execute(
	        	   	new HibernateCallback() {
	        	   		public Object doInHibernate(Session session) throws HibernateException {
	               	   	   	Set ids = new HashSet();
	               			StringBuffer inList = new StringBuffer();
	               			for (FolderEntry p:entries) {
	            	    		ids.add(p.getId());
	            	    		inList.append(p.getId().toString() + ",");
	            	    		session.evict(p);
	            	    	}
	             			inList.deleteCharAt(inList.length()-1);
	             			String entityString = "ownerId in (" + inList.toString() + ") and ownerType='" +
			   					EntityType.folderEntry.name() + "'";
	            			//need to use ownerId, cause versionattachments/customattributeList sets not indexed by folderentry
	    		   			getCoreDao().deleteEntityAssociations(entityString);
	        	   			//delete workflow history
	    		   			session.createQuery("Delete org.kablink.teaming.domain.WorkflowHistory where entityId in (:pList) and entityType=:entityType")
	         	   				.setParameterList("pList", ids)
	    		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.name())
	    		   				.executeUpdate();
	        	   			//delete ratings/visits for these entries
	     		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where entityId in (:pList) and entityType=:entityType")
	         	   				.setParameterList("pList", ids)
	    		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
	    		   				.executeUpdate();
	       		   			//delete shares for these entries
	     		   			session.createQuery("Delete org.kablink.teaming.domain.SharedEntity where entityId in (:pList) and entityType=:entityType")
	         	   				.setParameterList("pList", ids)
	    		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.name())
	    		   				.executeUpdate();
	       		   			//delete subscriptions to these entries
	     		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where entityId in (:pList) and entityType=:entityType")
	         	   				.setParameterList("pList", ids)
	    		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
	    		   				.executeUpdate();
	    		   			//delete tags for these entries
	     		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where entity_id in (:pList) and entity_type=:entityType")
	         	   				.setParameterList("pList", ids)
	    		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
	    		   				.executeUpdate();
	     		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where owner_id in (:pList) and owner_type=:entityType")
	     	   					.setParameterList("pList", ids)
	     	   					.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
	     	   					.executeUpdate();
	    		   			//delete any reserved names
	     		   			session.createQuery("Delete org.kablink.teaming.domain.LibraryEntry where binderId=:binderId and entityId in (:pList)")
	    		   				.setParameterList("pList", ids)
	    		   				.setLong("binderId", folder.getId())
	    		   				.executeUpdate();
	    		   			//load top level tokens
	    		   			Set tokenIds = new HashSet(session.createQuery("select w.tokenId from org.kablink.teaming.domain.WorkflowState w where w.owner.ownerId in (:pList) and w.owner.ownerType=:type")
	     	    	   			.setParameterList("pList", ids)
	    	    	   			.setString("type", EntityType.folderEntry.name())
	        	   				.list());
	    		   			workflowDelete(tokenIds, session);
	    		   			//remove foreign key or mysql complains
	    		   			session.createQuery("Update org.kablink.teaming.domain.FolderEntry set parentEntry = null, topEntry=null where id in (:pList)")
	    	   				.setParameterList("pList", ids)
	    	   				.executeUpdate();
	     		   			session.createQuery("Delete org.kablink.teaming.domain.FolderEntry where id in (:pList)")
	        	   				.setParameterList("pList", ids)
	        	   				.executeUpdate();
	           	  			//if these are ever cached in secondary cache, clear them out.      	   				
	           	   		return null;
	        	   		}
	        	   	}
	        	 );    	
    	}
    	finally {
    		end(begin, "deleteEntries(Folder,Collection<FolderEntry>)");
    	}	        
       	
    }

    /** 
     * Update the owningBinderKeys for all entries and their associations.
     * Moving all of the folderEntries with the folder.  The folder has been updated.
     * The owningBinderKeys of the entries must change, 
     * but the owningBinderId remains the same.   
     * Sub folder and their entries must be handled separetly
     */
    public void move(final Folder folder) {
		long begin = System.nanoTime();
		try {
	    	getCoreDao().move(folder);  //handles most generic associations
		   	getHibernateTemplate().execute(
		     	new HibernateCallback() {
		       		public Object doInHibernate(Session session) throws HibernateException {
		 	   			session.createQuery("update org.kablink.teaming.domain.FolderEntry set owningBinderKey=:sortKey where parentBinder=:id")
	      	   				.setString("sortKey", folder.getBinderKey().getSortKey())
	      	   				.setLong("id", folder.getId().longValue())
	      	   				.executeUpdate();
	 	       	   		return null;
		    	   		}
		    	   	}
		    	 );    	
    	}
    	finally {
    		end(begin, "move(Folder)");
    	}	        
   	
    }

    /** 
     * Update the owningFolderSortkeys/owingingBinder for all entry associations
     * Moving entries to new folder. 
     * 
     */
    public void moveEntries(final Folder folder, final List<Long> ids) {
		long begin = System.nanoTime();
		try {
	    	if (ids.isEmpty()) return;
		   	getHibernateTemplate().execute(
		     	new HibernateCallback() {
		       		public Object doInHibernate(Session session) throws HibernateException {
	        			//need to use ownerId, cause versionattachments/customattributeList sets not indexed by folderentry
		    	   		session.createQuery("update org.kablink.teaming.domain.Attachment set owningBinderKey=:sortKey,owningBinderId=:id where " +
		    	   				"ownerId in (:pList) and ownerType=:type")
	 	    	   			.setString("sortKey", folder.getBinderKey().getSortKey())
		    	   			.setLong("id", folder.getId().longValue())
		    	   			.setParameterList("pList", ids)
		    	   			.setString("type", EntityType.folderEntry.name())
		    	   			.executeUpdate();
		    	   		session.createQuery("update org.kablink.teaming.domain.Event set owningBinderKey=:sortKey,owningBinderId=:id where " +
		    	   				"ownerId in (:pList) and ownerType=:type")
	 	    	   			.setString("sortKey", folder.getBinderKey().getSortKey())
		    	   			.setLong("id", folder.getId().longValue())
		    	   			.setParameterList("pList", ids)
		    	   			.setString("type", EntityType.folderEntry.name())
		       	   			.executeUpdate();
		       	   		session.createQuery("update org.kablink.teaming.domain.CustomAttribute set owningBinderKey=:sortKey,owningBinderId=:id where " +
		    	   				"ownerId in (:pList) and ownerType=:type")
	 	    	   			.setString("sortKey", folder.getBinderKey().getSortKey())
		    	   			.setLong("id", folder.getId().longValue())
		    	   			.setParameterList("pList", ids)
		    	   			.setString("type", EntityType.folderEntry.name())
		  	   				.executeUpdate();
	       	   			session.createQuery("update org.kablink.teaming.domain.WorkflowState set owningBinderKey=:sortKey,owningBinderId=:id where " +
		    	   				"ownerId in (:pList) and ownerType=:type")
	 	    	   			.setString("sortKey", folder.getBinderKey().getSortKey())
		    	   			.setLong("id", folder.getId().longValue())
		    	   			.setParameterList("pList", ids)
		    	   			.setString("type", EntityType.folderEntry.name())
		       	   			.executeUpdate();
	      	   			session.createQuery("update org.kablink.teaming.domain.WorkflowResponse set owningBinderKey=:sortKey,owningBinderId=:id where " +
	      	   				"ownerId in (:pList) and ownerType=:type")
	      	   				.setString("sortKey", folder.getBinderKey().getSortKey())
	      	   				.setLong("id", folder.getId().longValue())
	      	   				.setParameterList("pList", ids)
	      	   				.setString("type", EntityType.folderEntry.name())
	      	   				.executeUpdate();
	      	   			session.createQuery("update org.kablink.teaming.domain.WorkflowHistory set owningBinderKey=:sortKey,owningBinderId=:id where " +
	      	   				"entityId in (:pList) and entityType=:type")
	      	   				.setString("sortKey", folder.getBinderKey().getSortKey())
	      	   				.setLong("id", folder.getId().longValue())
	      	   				.setParameterList("pList", ids)
	      	   				.setString("type", EntityType.folderEntry.name())
	      	   				.executeUpdate();
	      	   			session.createQuery("update org.kablink.teaming.domain.NotifyStatus set owningBinderKey=:sortKey,owningBinderId=:id where " +
	      	   				"ownerId in (:pList) and ownerType=:type")
	      	   				.setString("sortKey", folder.getBinderKey().getSortKey())
	      	   				.setLong("id", folder.getId().longValue())
	      	   				.setParameterList("pList", ids)
	      	   				.setString("type", EntityType.folderEntry.name())
	      	   				.executeUpdate();
	      	   			session.createQuery("update org.kablink.teaming.domain.FolderEntry set owningBinderKey=:sortKey,parentBinder=:id where " +
		    	   				"id in (:pList)")
	 	    	   			.setString("sortKey", folder.getBinderKey().getSortKey())
		    	   			.setLong("id", folder.getId().longValue())
		    	   			.setParameterList("pList", ids)
	      	   				.executeUpdate();
		    			//update binder key 
		    			session.createQuery("update org.kablink.teaming.domain.ChangeLog set owningBinderKey=:sortKey,owningBinderId=:id where " +
		    	   				"entityId in (:pList) and entityType=:type")
	 	    	   			.setString("sortKey", folder.getBinderKey().getSortKey())
		    	   			.setLong("id", folder.getId().longValue())
		    	   			.setParameterList("pList", ids)
		    	   			.setString("type", EntityType.folderEntry.name())
		       	   			.executeUpdate();
		    			session.createQuery("update org.kablink.teaming.domain.AuditTrail set owningBinderKey=:sortKey,owningBinderId=:id where " +
		   					"entityId in (:pList) and entityType=:type")
		   					.setString("sortKey", folder.getBinderKey().getSortKey())
		   					.setLong("id", folder.getId().longValue())
		   					.setParameterList("pList", ids)
		   					.setString("type", EntityType.folderEntry.name())
		   					.executeUpdate();
		       	   		return null;
		    	   		}
		    	   	}
		    	 );    	
    	}
    	finally {
    		end(begin, "moveEntries(Folder,List<Long>)");
    	}	        
   	
    }
    //load public and private tags for a list of folder entries
    //order by id and name
    public List<Tag> loadEntryTags(final EntityIdentifier ownerIdentifier, final Collection<Long> ids) {
		long begin = System.nanoTime();
		try {
	    	if (ids.isEmpty()) return new ArrayList();
		   	return (List<Tag>)getHibernateTemplate().execute(
			     	new HibernateCallback() {
			       		public Object doInHibernate(Session session) throws HibernateException {
		                 	return session.createCriteria(Tag.class)
	       					.add(Expression.eq("entityIdentifier.type", EntityIdentifier.EntityType.folderEntry.getValue()))
	                 		.add(Expression.in("entityIdentifier.entityId", ids))
	                        .add(Expression.disjunction()
	              					.add(Expression.eq("public",true))
	              					.add(Expression.conjunction()
	              							.add(Expression.eq("ownerIdentifier.entityId", ownerIdentifier.getEntityId()))
	              							.add(Expression.eq("ownerIdentifier.type", ownerIdentifier.getEntityType().getValue()))
	              					)
	              			)
	              			.addOrder(Order.asc("entityIdentifier.entityId"))
	                 		.addOrder(Order.asc("name"))
	                 		.setCacheable(true)
		                 	.list();
		    	   		}
		    	   	}
		    	 );    	
    	}
    	finally {
    		end(begin, "loadEntryTags(EntityIdentifier,Collection<Long>)");
    	}	        
   	
    }
    //All of this code is dependent on the JBPM data structures.
    private void workflowDelete(Set tokenIds, Session session) {
		//now get process instances 
		if (tokenIds.isEmpty()) return;
    	Set pIs = new HashSet(session.createQuery("select p.id from org.jbpm.graph.exe.ProcessInstance p where p.rootToken.id in (:pList)")
 				.setParameterList("pList", tokenIds)
			.list());
		
    	if(pIs.isEmpty()) return;
		//start down the tree with ProcessInstances
		tokenIds.clear();
		List subTokens;
		List subPIs = new ArrayList(pIs);
		while (true) {
  			//start down tree from here
			subTokens = session.createQuery("select t.id from org.jbpm.graph.exe.Token t where t.processInstance.id in (:pList)")
   				.setParameterList("pList", subPIs)
  				.list();
  			if (subTokens.isEmpty()) break;
  			tokenIds.addAll(subTokens);

   			subPIs = session.createQuery("select p.id from org.jbpm.graph.exe.ProcessInstance p where p.superProcessToken.id in (:pList)")
						.setParameterList("pList", subTokens)
						.list();
   			if (subPIs.isEmpty()) break;
   			pIs.addAll(subPIs);
				
		}
			
		if (!tokenIds.isEmpty()) {
			//delete logs
			logger.debug("Cleaning jbpm tables for FolderDaoImpl.workflowdelete()...");
			if (logger.isDebugEnabled()) {
				for (java.util.Iterator tIT = tokenIds.iterator(); tIT.hasNext(); ) {
					Object t = tIT.next();
					logger.debug("...cleaning token:  '" + ((null == t) ? "<null>" : t.toString()) + "'");
				}
			}
			
			//Update the rows of JBPM_LOG table where the token id is in the list and set the parent field to null
			//This will remove the foreign key constraint
			logger.debug("Updating the parent field to null in the org.jbpm.logging.log.ProcessLog...");
			session.createQuery("Update org.jbpm.logging.log.ProcessLog set parent=null where token.id in (:pList)")
	  			.setParameterList("pList", tokenIds)
	 			.executeUpdate();
			
			//Then remove the rows from the table that have the tokens in the lists 
			logger.debug("Cleaning tokens from org.jbpm.logging.log.ProcessLog...");
			session.createQuery("Delete org.jbpm.logging.log.ProcessLog where token.id in (:pList)")
	  			.setParameterList("pList", tokenIds)
	 			.executeUpdate();
			
			//delete comments
			logger.debug("Cleaning tokens from org.jbpm.graph.exe.Comment...");
			session.createQuery("Delete org.jbpm.graph.exe.Comment where token.id in (:pList)")
	   			.setParameterList("pList", tokenIds)
	   			.executeUpdate();
			//delete variables
			logger.debug("Cleaning tokens from org.jbpm.context.exe.VariableInstance...");
			session.createQuery("Delete org.jbpm.context.exe.VariableInstance where token.id in (:pList)")
				.setParameterList("pList", tokenIds)
				.executeUpdate();
			logger.debug("Cleaning tokens from org.jbpm.context.exe.TokenVariableMap...");
			session.createQuery("Delete org.jbpm.context.exe.TokenVariableMap where token.id in (:pList)")
	  			.setParameterList("pList", tokenIds)
	   			.executeUpdate();
		}
		
		session.createQuery("Delete org.jbpm.graph.exe.RuntimeAction where processInstance.id in (:pList)")
			.setParameterList("pList", pIs)
   			.executeUpdate();
		session.createQuery("Delete org.jbpm.module.exe.ModuleInstance where processInstance.id in (:pList)")
			.setParameterList("pList", pIs)
			.executeUpdate();

		session.createQuery("Delete org.jbpm.scheduler.exe.Timer where processInstance.id in (:pList)")
			.setParameterList("pList", pIs)
			.executeUpdate();

		//break token =>process connection
		session.createQuery("Update org.jbpm.graph.exe.Token set processInstance=null,parent=null where id in (:pList)")
			.setParameterList("pList", tokenIds)
			.executeUpdate();
		session.createQuery("Delete org.jbpm.graph.exe.ProcessInstance where id in (:pList)")
			.setParameterList("pList", pIs)
			.executeUpdate();
		session.createQuery("Delete org.jbpm.graph.exe.Token where id in (:pList)")
			.setParameterList("pList", tokenIds)
			.executeUpdate();
    	
    }

    /**
     * Used to find the folder entries associated to a given workflow state and definition
     * @param defId - Definition Id
     * @param stateValue - State of the workflow
     * @return List of folder entry ids
     */
    public List<Long> findFolderIdsFromWorkflowState(final String defId, final String stateValue) {
		long begin = System.nanoTime();
		try {
	       	final Long thisZoneId = RequestContextHolder.getRequestContext().getZoneId();
	       	return (List<Long>)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                	List<Long> result = new ArrayList<Long>();
	                	List readObjs = new ArrayList();
	                	// "SELECT owner From org.kablink.teaming.domain.WorkflowState WHERE state='" + stateValue + "' AND definition='" + defId + "' AND ownerType='folderEntry' AND zoneId='" + thisZoneId + "'"
	                	Criteria crit = session.createCriteria(WorkflowState.class)
	                	.setProjection(Projections.property("owner"))
	                	.add(Restrictions.eq("state", stateValue))
	                	.add(Restrictions.eq("definition.id", defId))
	                	.add(Restrictions.eq("owner.ownerType", "folderEntry"))
	                	.add(Restrictions.eq("zoneId", thisZoneId));
	                	List objs = crit.list();
	                	readObjs.add(objs);
				      	HashMap tMap;
				       	for (int i=0; i < objs.size(); ++i) {
				       		AnyOwner owner = (AnyOwner) objs.get(i);
				       		result.add(owner.getEntity().getId());
				       	}
				       	return result;
	                }
	            }
			);  
    	}
    	finally {
    		end(begin, "findFolderIdsFromWorkflowState(String,String)");
    	}	        
	}	
	
}