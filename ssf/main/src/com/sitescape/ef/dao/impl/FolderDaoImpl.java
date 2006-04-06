package com.sitescape.ef.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Disjunction;
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
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoFolderEntryByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.HKey;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.UserPerFolderPK;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.UserPropertiesPK;
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
	 	
	//hibernate will sometimes return duplicates when doing joins.  Filter them out
	//but maintain order.  
	private List removeDuplicates(List entries) {
		if (entries.isEmpty()) return entries;
		List<FolderEntry> result = new ArrayList<FolderEntry>(entries.size());
		FolderEntry entry;
		Long lastId=null;
		for (int i=0; i<entries.size(); ++i) {
			entry = (FolderEntry)(entries.get(i));
			if (!entry.getId().equals(lastId)) {
				result.add(entry);
				lastId=entry.getId();
			}
		}
		return result;
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
                    		.setFetchMode("HCustomAttributes", FetchMode.JOIN)
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
     * Load the ancestors and descendants of an entry
     */
    public List loadEntryTree(final FolderEntry entry) throws DataAccessException { 
        List result;
        final String[] keys = entry.getHKey().getAncestorKeys();  
       
       if (keys == null) {
           result = loadEntryDescendants(entry);
       } else {
    	   //load ancestors and descendants
           result = (List)getHibernateTemplate().execute(
                   new HibernateCallback() {
                       public Object doInHibernate(Session session) throws HibernateException {
                   		int nextPos = Integer.parseInt(entry.getHKey().getRelativeNumber(entry.getDocLevel())) + 1;
               		 	HKey next = new HKey(entry.getParentEntry().getHKey(), nextPos);    
                            List result = session.createCriteria(FolderEntry.class)
                            	.add(Expression.disjunction()
                                	.add(Expression.in("HKey.sortKey", keys))
                                	.add(Expression.between("HKey.sortKey", entry.getHKey().getSortKey(), next.getSortKey()))
                            	)
                            	.setFetchMode("entryDef", FetchMode.SELECT)	
                            	.setFetchMode("parentBinder", FetchMode.SELECT)	
                            	.setFetchMode("topFolder", FetchMode.SELECT)	
                            	.addOrder(Order.asc("HKey.sortKey"))
                            	.list();
                            return removeDuplicates(result);
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
                     List result = session.createCriteria(entry.getClass())
                     	.add(Expression.in("HKey.sortKey", keys))
                       	.setFetchMode("entryDef", FetchMode.SELECT)	
                       	.setFetchMode("parentBinder", FetchMode.SELECT)	
                       	.setFetchMode("topFolder", FetchMode.SELECT)	
                     	.addOrder(Order.asc("HKey.sortKey"))
                     	.list();
                     return removeDuplicates(result);
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
                     List result = crit.list();
                     return removeDuplicates(result);
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
                    	List results = session.createFilter(folder.getEntries(), 
                    			"where (this.creation.date > :cDate and this.creation.date <= :c2Date) or " +
									    "(this.modification.date > :mDate and this.modification.date <= :m2Date) or " +
									    "(this.workflowChange.date > :wDate and this.workflowChange.date <= :w2Date)" +
								" order by " + order.getOrderByClause("this"))
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
    /**
     * Load all the child folders of a folder.  Not restricted to 1 level.
     */
    public List loadFolderTree(final Folder folder) throws DataAccessException {
        
        //Load folder and sub-folders. 
        return (List)getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                	 return session.createCriteria(Folder.class)
                     			.add(Expression.conjunction()  
                                   	.add(Expression.like("folderHKey.sortKey", folder.getFolderHKey().getSortKey() + "%"))
                                    .add(Expression.gt("folderHKey.level", new Integer(folder.getFolderHKey().getLevel())))
                                   )
                    	.list();
					 
               }
            }
        );
    }
    
    /**
     * Load a folder and all its sub-folders.  Contains entire tree.
     */
    public List loadFolderAncestors(final Folder folder) throws DataAccessException { 
        final String[] keys = folder.getFolderHKey().getAncestorKeys();  
        if (keys == null) return new ArrayList();
         List result = (List)getHibernateTemplate().execute(
             new HibernateCallback() {
                 public Object doInHibernate(Session session) throws HibernateException {
                     //Hibernate doesn't like the ? in the in clause
                     Criteria crit = session.createCriteria(FolderEntry.class);
                      Disjunction dis = Expression.disjunction();
                      for (int i=0; i<keys.length; ++i) {
                          dis.add(Expression.eq("folderHKey.sortKey", keys[i]));
                      };
                     crit.add(dis);
                     crit.setFetchMode("topEntry", FetchMode.SELECT);
                     crit.setFetchMode("parentEntry", FetchMode.SELECT);
                     crit.setFetchMode("entryDef", FetchMode.SELECT);
                     
                     crit.addOrder(Order.asc("HKey.sortKey"));
                     return crit.list();
                 }
             }
         );  
         return result;
     }  


     public UserProperties loadUserFolderProperties(Long userId, Long folderId) {
    	UserPropertiesPK id = new UserPropertiesPK(userId, folderId);
        UserProperties uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
        if (uProps == null) {
        	uProps = new UserProperties(id);
        	getCoreDao().saveNewSession(uProps);
        	//quick write
         	uProps=(UserProperties)getHibernateTemplate().get(UserProperties.class, id);
    	}
        return uProps;
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
   public void deleteEntries(final Folder folder) {
       	getHibernateTemplate().execute(
        	   	new HibernateCallback() {
        	   		public Object doInHibernate(Session session) throws HibernateException {
        	   		session.createQuery("DELETE com.sitescape.ef.domain.Attachment where owningFolderSortKey=:key")
            	   			.setString("key", folder.getFolderHKey().getSortKey())
        	   			.executeUpdate();
           	   		//need to remove event assignments
     /*      	   		List eventIds = session.createQuery("select id from com.sitescape.ef.domain.Event where  owningFolderSortKey=:key")
            	   			.setString("key", folder.getFolderHKey().getSortKey())
               	   			.list();
           	   		if (!eventIds.isEmpty()) {
           	   			StringBuffer ids = new StringBuffer();
           	   			ids.append("(");
           	   			for (int i=0; i<eventIds.size(); ++i) {
           	   				ids.append("'" + eventIds.get(i) + "',");
           	   			}
           	   			ids.replace(ids.length()-1, ids.length(), ")");
           	   			Connection connect = session.connection();
           	   			try {
           	   				Statement s = connect.createStatement();
           	   				s.executeUpdate("delete from SS_AssignmentsMap where event in " + ids);
           	   			} catch (SQLException sq) {
           	   				throw new HibernateException(sq);
           	   			}
           	   		}
    */
           	   		session.createQuery("DELETE com.sitescape.ef.domain.Event where owningFolderSortKey=:key")
           	   			.setString("key", folder.getFolderHKey().getSortKey())
           	   			.executeUpdate();
           	   		session.createQuery("DELETE com.sitescape.ef.domain.CustomAttribute where owningFolderSortKey=:key")
            	   			.setString("key", folder.getFolderHKey().getSortKey())
      	   				.executeUpdate();
       	   			session.createQuery("DELETE com.sitescape.ef.domain.WorkflowState where owningFolderSortKey=:key")
           	   			.setString("key", folder.getFolderHKey().getSortKey())
       	   						.executeUpdate();
       	   			session.createQuery("Delete com.sitescape.ef.domain.FolderEntry where parentBinder=:parent")
       	   				.setLong("parent", folder.getId().longValue())
       	   				.executeUpdate();
       	   				
           	   		return null;
        	   		}
        	   	}
        	 );    	
    	
    }
    public void deleteEntries(List entries) {
    	Set idList = new HashSet();
    	for (int i=0; i<entries.size(); ++i) {
    		idList.add(((FolderEntry)entries.get(i)).getId());   		
    	}
    	final Set ids = idList;
      	getHibernateTemplate().execute(
        	   	new HibernateCallback() {
        	   		public Object doInHibernate(Session session) throws HibernateException {
        	   		session.createQuery("DELETE com.sitescape.ef.domain.Attachment where folderEntry in (:pList)")
            			.setParameterList("pList", ids)
        	   			.executeUpdate();
           	   		//need to remove event assignments
     /*      	   		List eventIds = session.createQuery("select id from com.sitescape.ef.domain.Event where  owningFolderSortKey=:key")
            	   			.setString("key", folder.getFolderHKey().getSortKey())
               	   			.list();
           	   		if (!eventIds.isEmpty()) {
           	   			StringBuffer ids = new StringBuffer();
           	   			ids.append("(");
           	   			for (int i=0; i<eventIds.size(); ++i) {
           	   				ids.append("'" + eventIds.get(i) + "',");
           	   			}
           	   			ids.replace(ids.length()-1, ids.length(), ")");
           	   			Connection connect = session.connection();
           	   			try {
           	   				Statement s = connect.createStatement();
           	   				s.executeUpdate("delete from SS_AssignmentsMap where event in " + ids);
           	   			} catch (SQLException sq) {
           	   				throw new HibernateException(sq);
           	   			}
           	   		}
    */
           	   		session.createQuery("DELETE com.sitescape.ef.domain.Event where folderEntry in (:pList)")
            			.setParameterList("pList", ids)
           	   			.executeUpdate();
           	   		session.createQuery("DELETE com.sitescape.ef.domain.CustomAttribute where folderEntry in (:pList)")
            			.setParameterList("pList", ids)
      	   				.executeUpdate();
       	   			session.createQuery("DELETE com.sitescape.ef.domain.WorkflowState where folderEntry in (:pList)")
            			.setParameterList("pList", ids)
       	   						.executeUpdate();
       	   			session.createQuery("Delete com.sitescape.ef.domain.FolderEntry where id in (:pList)")
            			.setParameterList("pList", ids)
       	   				.executeUpdate();
       	   				
           	   		return null;
        	   		}
        	   	}
        	 );    	
       	
    }
        
    public void deleteEntryWorkflows(Folder folder) {
    	//brute force delete of jbpm data structures
    }
    public void deleteEntryWorkflows(List entries) {
    	
    }
}