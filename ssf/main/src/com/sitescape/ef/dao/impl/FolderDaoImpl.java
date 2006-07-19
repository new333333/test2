package com.sitescape.ef.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoFolderEntryByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.domain.HKey;
import com.sitescape.ef.domain.HistoryMap;

import com.sitescape.ef.domain.UserPerFolderPK;
import com.sitescape.ef.util.Constants;
/**
 * @author Jong Kim
 *
 */
public class FolderDaoImpl extends HibernateDaoSupport implements FolderDao {
	private String[] cfAttrs = new String[]{"parentBinder", "HKey.level"};
	private OrderBy cfOrder = new OrderBy("HKey.sortKey", OrderBy.DESCENDING);
	private CoreDao coreDao;
	
	public void setCoreDao(CoreDao coreDao) {
	   this.coreDao = coreDao;
	}
	private CoreDao getCoreDao() {
	    return coreDao;
	}
	 	
	/**
    * Load 1 FolderEntry and its customAttributes collection
     * @param parentFolderId
     * @param entryId
     * @param zoneName
     * @return
     * @throws DataAccessException
	 */
	public FolderEntry loadFolderEntry(final Long parentFolderId, final Long entryId, final String zoneName) throws DataAccessException {
        return (FolderEntry)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List results = session.createCriteria(FolderEntry.class)
                    		.add(Expression.eq("id", entryId))
                         	.setFetchMode("entryDef", FetchMode.SELECT)	
                        	.setFetchMode("parentBinder", FetchMode.SELECT)	
                        	.setFetchMode("topFolder", FetchMode.SELECT)	
                           .list();
                        if (results.size() == 0)  throw new NoFolderEntryByTheIdException(entryId);
                        //because of join may get non-distinct results (wierd)
                        FolderEntry entry = (FolderEntry)results.get(0);
                        if ((zoneName != null ) && !entry.getParentFolder().getZoneName().equals(zoneName)) {
                           	throw new NoFolderEntryByTheIdException(entryId);
                        }
                        if (!parentFolderId.equals(entry.getParentFolder().getId())) {
                           	throw new NoFolderEntryByTheIdException(entryId);        	
                        }
                        return entry;
                    }
                }
             );
    }
      
     /**
     * Query for a collection of FolderEntries.  An iterator is returned.  The entries are 
     * not pre-loaded.
     */
    public Iterator queryEntries(final FilterControls filter) throws DataAccessException { 
        Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        //sqlqueries, filters and criteria don't help with frontbase problem
                        //
                        Query query = session.createQuery("from com.sitescape.ef.domain.FolderEntry d " + filter.getFilterString("d"));
                		List filterValues = filter.getFilterValues();
               			for (int i=0; i<filterValues.size(); ++i) {
               				query.setParameter(i, filterValues.get(i));
                		}
                       return query;
                    }
                }
            );  
       return new SFQuery(query);
    }
    /**
     * Load level 1 FolderEntries for 1 folder.  Replies are not loaded
     */
    public Iterator queryChildEntries(Folder parentFolder) throws DataAccessException {
    	Object[] cfValues = new Object[]{parentFolder, new Integer(1)};
    	// use default query
     	return queryEntries(new FilterControls(cfAttrs, cfValues, cfOrder));
    }
	/*
     * Load the ancestors and descendants of an entry.  Entry will be included in List
     */
    public List loadEntryTree(final FolderEntry entry) throws DataAccessException { 
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
                   		int nextPos = Integer.parseInt(entry.getHKey().getRelativeNumber(entry.getDocLevel())) + 1;
               		 	HKey next = new HKey(entry.getParentEntry().getHKey(), nextPos);    
                            return session.createCriteria(FolderEntry.class)
                            	.add(Expression.disjunction()
                                	.add(Expression.in("HKey.sortKey", keys))
                                	.add(Expression.between("HKey.sortKey", entry.getHKey().getSortKey(), next.getSortKey()))
                            	)
                            	.setFetchMode("entryDef", FetchMode.SELECT)	
                            	.setFetchMode("parentBinder", FetchMode.SELECT)	
                            	.setFetchMode("topFolder", FetchMode.SELECT)	
                            	.addOrder(Order.asc("HKey.sortKey"))
                            	.list();
                        }
                  }
                  
       		);
       	}
        return result;
     }     
    /**
     * Given a folder entry, pre-load its chain of FolderEntry ancestors 
     */
    public List loadEntryAncestors(final FolderEntry entry) throws DataAccessException { 
        List result = (List)getHibernateTemplate().execute(
             new HibernateCallback() {
                 public Object doInHibernate(Session session) throws HibernateException {
                     String[] keys = entry.getHKey().getAncestorKeys();  
                     return  session.createCriteria(entry.getClass())
                     	.add(Expression.in("HKey.sortKey", keys))
                       	.setFetchMode("entryDef", FetchMode.SELECT)	
                       	.setFetchMode("parentBinder", FetchMode.SELECT)	
                       	.setFetchMode("topFolder", FetchMode.SELECT)	
                     	.addOrder(Order.asc("HKey.sortKey"))
                     	.list();
                 }
             }
         );  
         return result;
     }  
    /**
     * Given a FolderEntry, pre-load all descendents of the entry.
     */
    public List loadEntryDescendants(final FolderEntry entry) throws DataAccessException { 
        List result = (List)getHibernateTemplate().execute(
             new HibernateCallback() {
                 public Object doInHibernate(Session session) throws HibernateException {
                	 Criteria crit;
                	 if (entry.getDocLevel() > 1) {
                		int nextPos = Integer.parseInt(entry.getHKey().getRelativeNumber(entry.getDocLevel())) + 1;
               		 	HKey next = new HKey(entry.getParentEntry().getHKey(), nextPos);    
        				crit = session.createCriteria(FolderEntry.class)
                            .add(Expression.between("HKey.sortKey", entry.getHKey().getSortKey(), next.getSortKey())
                     		);
                	 } else {
                		 //this works better as an index with the DBs
                		 crit = session.createCriteria(FolderEntry.class)
                		 	.add(Expression.eq("topEntry", entry));                 		 
                	 };
                     crit.setFetchMode("entryDef", FetchMode.SELECT);	
                     crit.setFetchMode("parentBinder", FetchMode.SELECT);	
                     crit.setFetchMode("topFolder", FetchMode.SELECT);	
                     crit.addOrder(Order.asc("HKey.sortKey"));
                     return crit.list();
                 }
             }
         );  
         return result;
     } 
    /**
     * Load all entries of a folder that have been updated with a specified range.
     */
	public List loadFolderUpdates(Folder folder, Date since, Date before) {
		return loadFolderUpdates(folder, since, before, new OrderBy(Constants.ID));
	}
	/**
	 * See <code>loadFolderUpdates</code>. Order results as specified.
	 */
	public List loadFolderUpdates(final Folder folder, final Date since, final Date before, final OrderBy order) {
        List entries = (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	List results = session.createQuery("from com.sitescape.ef.domain.FolderEntry this where parentBinder=:parent and " + 
                    			" (this.creation.date > :cDate and this.creation.date <= :c2Date) or " +
									    "(this.modification.date > :mDate and this.modification.date <= :m2Date) or " +
									    "(this.workflowChange.date > :wDate and this.workflowChange.date <= :w2Date)" +
								" order by " + order.getOrderByClause("this"))
								.setLong("parent", folder.getId().longValue())
								.setTimestamp("cDate", since)
								.setTimestamp("c2Date", before)
								.setTimestamp("mDate", since)
								.setTimestamp("m2Date", before)
								.setTimestamp("wDate", since)
								.setTimestamp("w2Date", before)
								.list();
													
                    	return results;
                    }
                }
            );
		return entries;
    }
    /**
     * Load all entries of a folder and it sub-folders that have been updated with a specified range.
     */
	public List loadFolderTreeUpdates(Folder folder, Date since, Date before) {
		return loadFolderTreeUpdates(folder, since, before, new OrderBy(Constants.ID));
	}

	/**
	 * See <code>loadFolderTreeUpdates</code>. Order results as specified.
	 */
	public List loadFolderTreeUpdates(final Folder folder, final Date since, final Date before, final OrderBy order) {
        List entries = (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	Query q  = session.createQuery("from com.sitescape.ef.domain.FolderEntry x where owningFolderSortKey like '" + 
                    			folder.getFolderHKey().getSortKey() + "%' and ((x.creation.date > ? and x.creation.date <= ?) or " +
									    "(x.modification.date > ? and x.modification.date <= ?) or " +
									    "(x.workflowChange.date > ? and x.workflowChange.date <= ?)) order by " + order.getOrderByClause("x"));
						
                		int i=0;
						q.setTimestamp(i++, since);
						q.setTimestamp(i++, before);
						q.setTimestamp(i++, since);
						q.setTimestamp(i++, before);
						q.setTimestamp(i++, since);
						q.setTimestamp(i++, before);
						return q.list();
                    }
                }
            );
		return entries;
    }	

	/**
	 * Load 1 folder
	 */
    public Folder loadFolder(Long folderId, String zoneName) throws DataAccessException {
        if (folderId == null) {throw new NoFolderByTheIdException(folderId);}
       
        Folder folder = (Folder)getHibernateTemplate().get(Folder.class, folderId);
        if (folder == null) {throw new NoFolderByTheIdException(folderId);}
        if ((zoneName != null ) && !folder.getZoneName().equals(zoneName)) {
        	throw new NoFolderByTheIdException(folderId);
        }
        return folder;
    }

	public HistoryMap loadHistoryMap(Long userId, Long folderId) {
   		UserPerFolderPK id = new UserPerFolderPK(userId, folderId);
   		HistoryMap history =(HistoryMap)getHibernateTemplate().get(HistoryMap.class, id);
   		if (history == null) {
   			history = new HistoryMap(id);
   			getCoreDao().saveNewSession(history);
   			//quick write
   			history =(HistoryMap)getHibernateTemplate().get(HistoryMap.class, id);   			
   		}
   		return history;
	}	

	/**
	 * Delete the folder object and its assocations.
	 * Folder entries and child binders should already have been deleted
	 */
   public void delete(Folder folder) {
	   //core handles everything for the folder 
	   getCoreDao().delete(folder);
   }
   /**
    * Delete all entries in a folder.  
    */
   public void deleteEntries(final Folder folder) {
       	getHibernateTemplate().execute(
           	new HibernateCallback() {
           		public Object doInHibernate(Session session) throws HibernateException {
		   			getCoreDao().deleteEntityAssociations("owningBinderId=" + folder.getId(), FolderEntry.class);
		   			//delete ratings/visits for these entries
 		   			session.createQuery("Delete com.sitescape.ef.domain.Rating where entityId in " + 
 			   				"(select p.id from com.sitescape.ef.domain.FolderEntry p where " +
		   			  			" p.parentBinder=:folder) and entityType=:entityType")
		   			  	.setEntity("folder", folder)
		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
		   				.executeUpdate();
		   			//delete tags for these entries
 		   			session.createQuery("Delete com.sitescape.ef.domain.Tag where entityId in " + 
 			   				"(select p.id from com.sitescape.ef.domain.FolderEntry p where " +
		   			  			" p.parentBinder=:folder) and entityType=:entityType")
		   			  	.setEntity("folder", folder)
		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
		   				.executeUpdate();
        	  		session.createQuery("Delete com.sitescape.ef.domain.FolderEntry where parentBinder=:parent")
       	   				.setEntity("parent", folder)
       	   				.executeUpdate();
       	  			//if these are ever cached in secondary cache, clear them out.
		   	   		return null;
        	   		}
        	   	}
        	 );    	
    	
    }
    public void deleteEntries(final List entries) {
      	getHibernateTemplate().execute(
        	   	new HibernateCallback() {
        	   		public Object doInHibernate(Session session) throws HibernateException {
               	   	   	Set ids = new HashSet();
               			StringBuffer inList = new StringBuffer();
               			FolderEntry p;
            			for (int i=0; i<entries.size(); ++i) {
            				p = (FolderEntry)entries.get(i); 
            	    		ids.add(p.getId());
            	    		inList.append(p.getId().toString() + ",");
            	    		session.evict(p);
            	    	}
            			inList.deleteCharAt(inList.length()-1);
            			//need to use ownerId, cause attachments/custom sets not indexed by folderEntry
    		   			getCoreDao().deleteEntityAssociations("ownerId in (" + inList.toString() + ") and ownerType='" +
    		   					EntityType.folderEntry.name() + "'", FolderEntry.class);
    		   			//delete ratings/visits for these entries
     		   			session.createQuery("Delete com.sitescape.ef.domain.Rating where entityId in (:pList) and entityType=:entityType")
         	   				.setParameterList("pList", ids)
    		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
    		   				.executeUpdate();
    		   			//delete tags for these entries
     		   			session.createQuery("Delete com.sitescape.ef.domain.Tag where entityId in (:pList) and entityType=:entityType")
         	   				.setParameterList("pList", ids)
    		   			  	.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.getValue())
    		   				.executeUpdate();
            	   		session.createQuery("Delete com.sitescape.ef.domain.FolderEntry where id in (:pList)")
        	   				.setParameterList("pList", ids)
        	   				.executeUpdate();
           	  			//if these are ever cached in secondary cache, clear them out.      	   				
           	   		return null;
        	   		}
        	   	}
        	 );    	
       	
    }
     public void deleteEntryWorkflows(final Folder folder) {
    	//brute force delete of jbpm data structures
	   	getHibernateTemplate().execute(
		   	new HibernateCallback() {
		   		public Object doInHibernate(Session session) throws HibernateException {
		   			//load top level tokens
		   			Set tokenIds = new HashSet(session.createQuery("select w.tokenId from com.sitescape.ef.domain.WorkflowState w where w.owner.owningBinderId=:id")
    	   				.setLong("id", folder.getId().longValue())
    	   				.list());
		   			workflowDelete(tokenIds, session);
		   			return null;		   			
		   		}
		  	}
		);
    }
    public void deleteEntryWorkflows(Folder folder, final List ids) {
    	//brute force delete of jbpm data structures
	   	getHibernateTemplate().execute(
		   	new HibernateCallback() {
		   		public Object doInHibernate(Session session) throws HibernateException {
		   			//load top level tokens
		   			Set tokenIds = new HashSet(session.createQuery("select w.tokenId from com.sitescape.ef.domain.WorkflowState w where w.owner.ownerId in (:pList) and w.owner.ownerType=:type")
 	    	   			.setParameterList("pList", ids)
	    	   			.setString("type", EntityType.folderEntry.name())
    	   				.list());
		   			workflowDelete(tokenIds, session);
	   			return null;		   			
		   		}
		  	}
		);
    }
    //All of this code is dependent on the JBPM data structures.
    private void workflowDelete(Set tokenIds, Session session) {
		//now get process instances 
		if (tokenIds.isEmpty()) return;
    	Set pIs = new HashSet(session.createQuery("select p.id from org.jbpm.graph.exe.ProcessInstance p where p.rootToken in (:pList)")
 				.setParameterList("pList", tokenIds)
			.list());
		
		//start down the tree with ProcessInstances
		tokenIds.clear();
		List subTokens;
		List subPIs = new ArrayList(pIs);
		while (true) {
  			//start down tree from here
			subTokens = session.createQuery("select t.id from org.jbpm.graph.exe.Token t where t.processInstance in (:pList)")
   				.setParameterList("pList", subPIs)
  				.list();
  			if (subTokens.isEmpty()) break;
  			tokenIds.addAll(subTokens);

   			subPIs = session.createQuery("select p.id from org.jbpm.graph.exe.ProcessInstance p where p.superProcessToken in (:pList)")
						.setParameterList("pList", subTokens)
						.list();
   			if (subPIs.isEmpty()) break;
   			pIs.addAll(subPIs);
				
		}
			
		//delete logs
		session.createQuery("Delete org.jbpm.logging.log.ProcessLog where token in (:pList)")
  			.setParameterList("pList", tokenIds)
  			.executeUpdate();
		//delete comments
		session.createQuery("Delete org.jbpm.graph.exe.Comment where token in (:pList)")
   			.setParameterList("pList", tokenIds)
   			.executeUpdate();
		//delete variables
		session.createQuery("Delete org.jbpm.context.exe.VariableInstance where token in (:pList)")
			.setParameterList("pList", tokenIds)
			.executeUpdate();
		session.createQuery("Delete org.jbpm.context.exe.TokenVariableMap where token in (:pList)")
  			.setParameterList("pList", tokenIds)
   			.executeUpdate();
		
		session.createQuery("Delete org.jbpm.graph.exe.RuntimeAction where processInstance in (:pList)")
			.setParameterList("pList", pIs)
   			.executeUpdate();
		session.createQuery("Delete org.jbpm.module.exe.ModuleInstance where processInstance in (:pList)")
			.setParameterList("pList", pIs)
			.executeUpdate();

		session.createQuery("Delete org.jbpm.scheduler.exe.Timer where processInstance in (:pList)")
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
     * Update the owningFolderSortkeys for all entries and their associations.
     * Moving all of the folderEntries with the folder.  The folder has been updated.
     * The owningFolderSortKey of the entries must change, 
     * but the owningBinderId remains the same.   
     * Sub folder and their entries must be handled separetly
     */
    public void moveEntries(final Folder folder) {
	   	getHibernateTemplate().execute(
	     	new HibernateCallback() {
	       		public Object doInHibernate(Session session) throws HibernateException {
	    	   		session.createQuery("update com.sitescape.ef.domain.Attachment set owningFolderSortKey=:sortKey where owningBinderId=:id")
	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	    	   			.executeUpdate();
	    	   		session.createQuery("update com.sitescape.ef.domain.Event set owningFolderSortKey=:sortKey where owningBinderId=:id")
	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	       	   			.executeUpdate();
	       	   		session.createQuery("update com.sitescape.ef.domain.CustomAttribute set owningFolderSortKey=:sortKey where owningBinderId=:id")
	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	  	   				.executeUpdate();
       	   			session.createQuery("update com.sitescape.ef.domain.WorkflowState set owningFolderSortKey=:sortKey where owningBinderId=:id")
	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	       	   			.executeUpdate();
      	   			session.createQuery("update com.sitescape.ef.domain.FolderEntry set owningFolderSortKey=:sortKey where parentBinder=:id")
      	   				.setString("sortKey", folder.getFolderHKey().getSortKey())
      	   				.setLong("id", folder.getId().longValue())
      	   				.executeUpdate();
	       	   		return null;
	    	   		}
	    	   	}
	    	 );    	
   	
    }

    /** 
     * Update the owningFolderSortkeys/owingingBinder for all entry associations
     * Moving entries to new folder. 
     * 
     */
    public void moveEntries(final Folder folder, final List ids) {
	   	getHibernateTemplate().execute(
	     	new HibernateCallback() {
	       		public Object doInHibernate(Session session) throws HibernateException {
	    	   		session.createQuery("update com.sitescape.ef.domain.Attachment set owningFolderSortKey=:sortKey,owningBinderId=:id where " +
	    	   				"ownerId in (:pList) and ownerType=:type")
 	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	    	   			.setParameterList("pList", ids)
	    	   			.setString("type", EntityType.folderEntry.name())
	    	   			.executeUpdate();
	    	   		session.createQuery("update com.sitescape.ef.domain.Event set owningFolderSortKey=:sortKey,owningBinderId=:id where " +
	    	   				"ownerId in (:pList) and ownerType=:type")
 	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	    	   			.setParameterList("pList", ids)
	    	   			.setString("type", EntityType.folderEntry.name())
	       	   			.executeUpdate();
	       	   		session.createQuery("update com.sitescape.ef.domain.CustomAttribute set owningFolderSortKey=:sortKey,owningBinderId=:id where " +
	    	   				"ownerId in (:pList) and ownerType=:type")
 	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	    	   			.setParameterList("pList", ids)
	    	   			.setString("type", EntityType.folderEntry.name())
	  	   				.executeUpdate();
       	   			session.createQuery("update com.sitescape.ef.domain.WorkflowState set owningFolderSortKey=:sortKey,owningBinderId=:id where " +
	    	   				"ownerId in (:pList) and ownerType=:type")
 	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	    	   			.setParameterList("pList", ids)
	    	   			.setString("type", EntityType.folderEntry.name())
	       	   			.executeUpdate();
      	   			session.createQuery("update com.sitescape.ef.domain.FolderEntry set owningFolderSortKey=:sortKey,parentBinder=:id where " +
	    	   				"id in (:pList)")
 	    	   			.setString("sortKey", folder.getFolderHKey().getSortKey())
	    	   			.setLong("id", folder.getId().longValue())
	    	   			.setParameterList("pList", ids)
      	   				.executeUpdate();
	       	   		return null;
	    	   		}
	    	   	}
	    	 );    	
   	
    }
}