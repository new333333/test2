package com.sitescape.ef.dao.impl;

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
import org.hibernate.FetchMode;

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
	private String[] cfAttrs = new String[]{"parentFolder", "HKey.level"};
	private OrderBy cfOrder = new OrderBy("HKey.sortKey", OrderBy.DESCENDING);
	private CoreDao coreDao;
	
	public void setCoreDao(CoreDao coreDao) {
	   this.coreDao = coreDao;
	}
	private CoreDao getCoreDao() {
	    return coreDao;
	}
	 	
    public Folder loadFolder(Long folderId, String zoneName) throws DataAccessException {
        if (folderId == null) {throw new NoFolderByTheIdException(folderId);}
       
        Folder folder = (Folder)getHibernateTemplate().get(Folder.class, folderId);
        if (folder == null) {throw new NoFolderByTheIdException(folderId);}
        if ((zoneName != null ) && !folder.getZoneName().equals(zoneName)) {
        	throw new NoFolderByTheIdException(folderId);
        }
        return folder;
    }
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
    /*
     * Find first level child docshare entries of the folder.
     */
    public Iterator queryEntries(final FilterControls filter) throws DataAccessException { 
        Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        //sqlqueries, filters and criteria don't help with frontbase problem
                        //
                        Query query = session.createQuery("from FolderEntry d " + filter.getFilterString("d"));
                		Object [] filterValues = filter.getFilterValues();
                		if (filterValues != null) {
                			for (int i=0; i<filterValues.length; ++i) {
                				query.setParameter(i, filterValues[i]);
                			}
                		}
                       return query;
                    }
                }
            );  
       return new SFQuery(query);
    }
 
    public Iterator queryChildEntries(Folder parentFolder) throws DataAccessException {
    	Object[] cfValues = new Object[]{parentFolder, new Integer(1)};
    	// use default query
     	return queryEntries(new FilterControls(cfAttrs, cfValues, cfOrder));
    }
    public List loadEntryAncestors(final FolderEntry entry) throws DataAccessException { 
        List result = (List)getHibernateTemplate().execute(
             new HibernateCallback() {
                 public Object doInHibernate(Session session) throws HibernateException {
                     String[] keys = entry.getHKey().getAncestorKeys();  
                     List query = session.createCriteria(entry.getClass())
                     .add(Expression.eq("parentFolder", entry.getParentFolder().getId()))
                     .add(Expression.in("HKey.sortKey", keys))
                     .list();
  //TODO: add order by when get new frontbase                      
                    return query;
                 }
             }
         );  
         return result;
     }  
    public List loadEntryDescendants(final FolderEntry entry) throws DataAccessException { 
        List result = (List)getHibernateTemplate().execute(
             new HibernateCallback() {
                 public Object doInHibernate(Session session) throws HibernateException {
                     //sqlqueries, filters and criteria don't help with frontbase problem
                     //
                     List query = session.createQuery("from FolderEntry d where d.HKey.level>:docLevel and d.HKey.sortKey like :sortKey")
//don't need anymore                     .setLong("parentFolder", entry.getParentFolder().getId().longValue())
                     .setInteger("docLevel", entry.getDocLevel())
                     .setString("sortKey", ((FolderEntry)entry).getHKey().getSortKey() + "%")
                     .list();
   //TODO: add order by when get new frontbase                      
                   return query;
                 }
             }
         );  
         return result;
     } 
    /*
     * In one call load the ancestors and descendants of an entry
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
 //not needed anymore                           crit.add(Expression.eq("parentFolder", entry.getParentFolder()));
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
                           List query = crit.list();
  //TODO: add order by when get new frontbase   
                            return query;
                       }
                  }
                  
       		);
       	}
        return result;
     }     

    public List loadFolderAncestors(final Folder folder) throws DataAccessException { 
        final String[] keys = folder.getFolderHKey().getAncestorKeys();  
        if (keys == null) return new ArrayList();
         List result = (List)getHibernateTemplate().execute(
             new HibernateCallback() {
                 public Object doInHibernate(Session session) throws HibernateException {
                     //Hibernate doesn't like the ? in the in clause
                     Disjunction dis = Expression.disjunction();
                      for (int i=0; i<keys.length; ++i) {
                          dis.add(Expression.eq("folderHKey.sortKey", keys[i]));
                      };
                     return session.createCriteria(Folder.class)
 //don't need with new sortkey                    .add(Expression.eq("topFolder", folder.getTopFolder()))
                     .add(dis)
                     //                  .add(Expression.in("folderHKey.sortKey", keys))
                     .list();
  //TODO: add order by when get new frontbase  
                 }
             }
         );  
         return result;
     }  
 
    public Folder loadFolders(final Long folderId, final String zoneName) throws DataAccessException {
        if (folderId == null) {throw new NoFolderByTheIdException(folderId);}
        
        //Load folder and sub-folders. 
        return (Folder)getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                	 List result  = session.createCriteria(Folder.class)
                     	.add(Expression.eq(Constants.ID, folderId))
                     	.setFetchMode("subFolders", FetchMode.JOIN)
                     	.list();
					 if (result.isEmpty()) {throw new NoFolderByTheIdException(folderId);}
					 
                     Folder folder = (Folder)result.get(0);
                     if ((zoneName != null ) && !folder.getZoneName().equals(zoneName)) {
                    	throw new NoFolderByTheIdException(folderId);
                     }
                     return folder;
                }
            }
        );
    }

    public int allocateEntryNumbers(Folder folder, int count) throws StaleObjectStateException {
    	return allocateNumbers(folder, count, 0);
    }
    public int allocateFolderNumbers(Folder folder, int count) {
    	return allocateNumbers(folder, count, 1);
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
	public SeenMap loadSeenMap(Long userId, Long folderId) {
   		UserPerFolderPK id = new UserPerFolderPK(userId, folderId);
   		SeenMap seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, id);
   		if (seen == null) {
   			seen = new SeenMap(id);
   			getCoreDao().saveNewSession(seen);
   			//quick write
   			seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, id);   			
   		}
   		return seen;
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
}