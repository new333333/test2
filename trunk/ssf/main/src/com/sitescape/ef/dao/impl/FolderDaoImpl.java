package com.sitescape.ef.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Conjunction;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;


import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.FolderCounts;
import com.sitescape.ef.domain.NoFolderEntryByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HKey;
import com.sitescape.ef.domain.SeenMap;
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
	 	

    /**
     * Load 1 FolderEntry
     */
    public FolderEntry loadFolderEntry(Long parentFolderId, Long entryId, String zoneName) throws DataAccessException {
        FolderEntry entry = (FolderEntry)getHibernateTemplate().get(FolderEntry.class, entryId);         
        if (entry == null) throw new NoFolderEntryByTheIdException(entryId);
        if ((zoneName != null ) && !entry.getParentFolder().getZoneName().equals(zoneName)) {
           	throw new NoFolderEntryByTheIdException(entryId);
        }
        if (!parentFolderId.equals(entry.getParentFolder().getId())) {
           	throw new NoFolderEntryByTheIdException(entryId);        	
        }
        return entry;
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
    /**
     * Given a folder entry, pre-load its chain of FolderEntry ancestors 
     */
    public List loadEntryAncestors(final FolderEntry entry) throws DataAccessException { 
        List result = (List)getHibernateTemplate().execute(
             new HibernateCallback() {
                 public Object doInHibernate(Session session) throws HibernateException {
                     String[] keys = entry.getHKey().getAncestorKeys();  
                     List query = session.createCriteria(entry.getClass())
                     .add(Expression.eq("parentBinder", entry.getParentFolder().getId()))
                     .add(Expression.in("HKey.sortKey", keys))
                     .list();
  //TODO: add order by when get new frontbase                      
                    return query;
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
                     //sqlqueries, filters and criteria don't help with frontbase problem
                     //
                     Criteria crit = session.createCriteria(FolderEntry.class);
                     crit.add(Expression.conjunction()  
                            .add(Expression.gt("HKey.level", new Integer(entry.getDocLevel())))
                            .add(Expression.like("HKey.sortKey", ((FolderEntry)entry).getHKey().getSortKey() + "%"))
                     );
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
	/*
     * Load the ancestors and descendants of an entry
     */
    public List loadEntryTree(final FolderEntry entry) throws DataAccessException { 
        List result;
        final String[] keys = entry.getHKey().getAncestorKeys();  
       
       if (keys == null) {
           result = loadEntryDescendants(entry);
       } else {
           result = (List)getHibernateTemplate().execute(
                   new HibernateCallback() {
                       public Object doInHibernate(Session session) throws HibernateException {
                           //Hibernate doesn't like the ? in the in clause
                          Disjunction dis = Expression.disjunction();
                           for (int i=0; i<keys.length; ++i) {
                               dis.add(Expression.eq("HKey.sortKey", keys[i]));
                            };
                            Criteria crit = session.createCriteria(FolderEntry.class);
                            crit.add(Expression.disjunction()
                                   .add(dis)
                                   .add(Expression.conjunction()  
                                       .add(Expression.gt("HKey.level", new Integer(entry.getDocLevel())))
                                       .add(Expression.like("HKey.sortKey", ((FolderEntry)entry).getHKey().getSortKey() + "%"))
                                   )
                            );
                            crit.setFetchMode("topEntry", FetchMode.SELECT);
                            crit.setFetchMode("parentEntry", FetchMode.SELECT);
                            crit.setFetchMode("entryDef", FetchMode.SELECT);
                            
                            crit.addOrder(Order.asc("HKey.sortKey"));
                            return crit.list();
                       }
                  }
                  
       		);
       	}
        return result;
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


    public int allocateEntryNumbers(Folder folder, int count) throws StaleObjectStateException {
    	return allocateNumbers(folder, count, 0);
    }
    public int allocateFolderNumbers(Folder folder, int count) {
    	return allocateNumbers(folder, count, 1);
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
     * Allocate folder numbers using a new session. This allows us to reserve the number and commit
     * the operation quickly.
     * @param folder
     * @param count
     * @param type
     * @return
     */
    private int allocateNumbers(Folder folder, int count, int type) {
		SessionFactory sf = getSessionFactory();
		Session s = sf.openSession();
		StaleObjectStateException lastSo=null;
		int next;		
		try {
			FolderCounts fCounts = (FolderCounts)s.get(FolderCounts.class, folder.getId());
			if (fCounts == null) fCounts = rebuildFolderCounts(s, folder.getId());
			for (int i=0; i<5; ++i) {
					try {
						if (type == 0) {
							next = fCounts.allocateEntryNumbers(count);
						} else {
							next = fCounts.allocateFolderNumbers(count);
						}
						s.flush();
						return next;
					} catch (StaleObjectStateException so) {
					//	try again
						s.refresh(fCounts);
						lastSo = so;
					}
		   		}
		   		throw lastSo;
		} finally {
			s.close();
		}
	}
    private FolderCounts rebuildFolderCounts(Session session, Long folderId) {
   		int nextFolder;
   		int nextEntry;
   		//need to get in current session
   		Folder folder = (Folder)session.get(Folder.class, folderId);
   		List results = session.createFilter(folder.getFolders(), "order by this.folderHKey.sortKey desc")
						.setMaxResults(1)
						.list();
    	if (results.size() == 0) {
    		nextFolder=1;
    	} else {
    		Folder subFolder = (Folder)results.get(0);
    		HKey key = subFolder.getFolderHKey();
    		String num = key.getRelativeNumber(key.getLevel());
    		nextFolder = Integer.parseInt(num)+1;
    	}
			
    	results = session.createFilter(folder.getEntries(), "order by this.HKey.sortKey desc")
				.setMaxResults(1)
					.list();
    	if (results.size() == 0) {
    		nextEntry=1;
    	} else {
    		FolderEntry entry = (FolderEntry)results.get(0);
    		HKey key = entry.getHKey();
    		String num = key.getRelativeNumber(key.getLevel());
    		nextEntry = Integer.parseInt(num)+1;
    	}
    	FolderCounts fCounts = new FolderCounts(folder.getId());
    	fCounts.setNextFolder(nextFolder);
    	fCounts.setNextEntry(nextEntry);
    	session.save(fCounts);
    	return fCounts;
    }

}