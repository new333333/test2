/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.ObjectControls;
import com.sitescape.team.dao.util.OrderBy;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.CustomAttributeListElement;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinitionInvalidOperation;
import com.sitescape.team.domain.EntityDashboard;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.LibraryEntry;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoBinderByTheNameException;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.TitleException;
import com.sitescape.team.domain.UserDashboard;
import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.domain.WorkflowControlledEntry;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.LongIdComparator;
import com.sitescape.team.util.NLT;
import com.sitescape.util.Validator;
/**
 * @author Jong Kim
 *
 */
public class CoreDaoImpl extends HibernateDaoSupport implements CoreDao {
	protected Log logger = LogFactory.getLog(getClass());

	public boolean isDirty() {
		return getSession().isDirty();
	}
	public void flush() {
		getSession().flush();
	}
	public void clear() {
		getSession().clear();
	}
	public void evict(Object obj) {
		getSession().evict(obj);
	}
	public void refresh(Object obj) {
		getSession().refresh(obj);
	}
	public void save(Object obj) {
        getHibernateTemplate().save(obj);
    }
	public void save(final Collection objs) {
	       getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	 Iterator iter = objs.iterator();
                     	 while (iter.hasNext()) {
                     	 	session.save(iter.next());                     	 	
                     	 }
                     	 return null;
                    }
                }
            );
		
	}
	//re-attach object
	public void update(Object obj) {
	    getHibernateTemplate().update(obj);
	}
	public Object merge(Object obj) {
	    return getHibernateTemplate().merge(obj);
	}
	public void replicate(final Object obj) {
	      getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	 session.replicate(obj, ReplicationMode.EXCEPTION);
                    	 return null;
                    }
                }
            );
	}
     public SFQuery queryObjects(final ObjectControls objs, final FilterControls filter) { 
        Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
    	            	StringBuffer query = objs.getSelectAndFrom("x");
                     	filter.appendFilter("x", query);
                      	Query q = session.createQuery(query.toString());
                		List filterValues = filter.getFilterValues();
               			for (int i=0; i<filterValues.size(); ++i) {
               				q.setParameter(i, filterValues.get(i));
                		}
     	                return q;
    	            }
                }
            );  
       return new SFQuery(query);
    }	
	/* 
	 * Becuse we have relationships within the same table, 
	 * not all databases handle on-delete correctly.  
	 * We are forced to do it ourselves
	 */
 	public void deleteEntityAssociations(final String whereClause) {
	   	getHibernateTemplate().execute(
	    	   	new HibernateCallback() {
	    	   		public Object doInHibernate(Session session) throws HibernateException {
	    	   			//mysql won't delete these in 1 statement cause of foreign key constraints
	    	   		session.createQuery("DELETE com.sitescape.team.domain.VersionAttachment where " + whereClause)
	    	   			.executeUpdate();
	    	   		session.createQuery("DELETE com.sitescape.team.domain.Attachment where " + whereClause)
    	   			.executeUpdate();
	       	   		session.createQuery("DELETE com.sitescape.team.domain.CustomAttributeListElement where " + whereClause)
	  	   				.executeUpdate();
	       	   		session.createQuery("DELETE com.sitescape.team.domain.CustomAttribute where " + whereClause)
  	   				.executeUpdate();
/*
 * db servers can deal with these cause on-delete cascade will work
 * 	    	   		session.createQuery("DELETE com.sitescape.team.domain.Event where " + whereClause)
       	   			.executeUpdate();
	       	   			session.createQuery("DELETE com.sitescape.team.domain.WorkflowState where " + whereClause)
	       	   				.executeUpdate();
	       	   			session.createQuery("DELETE com.sitescape.team.domain.WorkflowResponse where " + whereClause)
       	   				.executeUpdate();
*/
	       	   		return null;
	       	   		}
	       	   	}
	    	 );    	
	    	
		
	}	
	public void delete(Object obj) {
        getHibernateTemplate().delete(obj);
    }
	/**
	 * Delete the binder object and its assocations.
	 * Child binders should already have been deleted
	 * This is an optimized delete.  Deletes associations directly without waiting for hibernate
	 * to query.  Also deleted entries associated by parentBinderId
	 */	
	public void delete(final Binder binder) {
		delete(binder, null);
	}
	public void delete(final Binder binder, final Class entryClass) {
	   	getHibernateTemplate().execute(
	    	new HibernateCallback() {
	    		public Object doInHibernate(Session session) throws HibernateException {

		   			//delete alias
	    			//scheduled items should delete themselves as they come due
	    			PostingDef def = binder.getPosting();
	    			if (def != null) session.delete(def);
	    			session.flush();
	    			Connection connect = session.connection();
		   			try {
		   				Statement s = connect.createStatement();
		   				s.executeUpdate("delete from SS_DefinitionMap where binder=" + binder.getId());
		   				s.executeUpdate("delete from SS_WorkflowMap where binder=" + binder.getId());
		   				s.executeUpdate("delete from SS_Notifications where binderId=" + binder.getId());
		   			} catch (SQLException sq) {
		   				throw new HibernateException(sq);
		   			}
	     	   		//delete user dashboards on this binder
	     	   		session.createQuery("Delete com.sitescape.team.domain.Dashboard where binderId=:binderId")
		   				.setLong("binderId", binder.getId())
	     	   			.executeUpdate();
		   			session.createQuery("DELETE com.sitescape.team.domain.UserProperties where binderId=:binderId")
		   				.setLong("binderId", binder.getId())
		   				.executeUpdate();
		   			//delete reserved names for entries/subfolders
		   			session.createQuery("DELETE com.sitescape.team.domain.LibraryEntry where binderId=:binderId")
		   				.setLong("binderId", binder.getId())
		   				.executeUpdate();
		   			//delete associations with binder and its entries - common key
    	   			deleteEntityAssociations("owningBinderId=" + binder.getId());

		   		    // Delete associations not maintained with foreign-keys = this just handles the binder itself
	     	   		//delete dashboard
	     	   		session.createQuery("Delete com.sitescape.team.domain.Dashboard where owner_id=:entityId and owner_type=:entityType")
  		     	   			.setLong("entityId", binder.getId())
  		     	   			.setParameter("entityType", binder.getEntityType().getValue())
   		     	   			.executeUpdate();
		   			//delete ratings/visits for this binder
		   			session.createQuery("Delete com.sitescape.team.domain.Rating where entityId=:entityId and entityType=:entityType")
		   				.setLong("entityId", binder.getId())
		   			   	.setParameter("entityType", binder.getEntityType().getValue())
		   			   	.executeUpdate();
		   			//delete subscriptions to this binder
		   			session.createQuery("Delete com.sitescape.team.domain.Subscription where entityId=:entityId and entityType=:entityType")
		   				.setLong("entityId", binder.getId())
		   				.setParameter("entityType", binder.getEntityType().getValue())
		   				.executeUpdate();
		   			//delete tags on this binder
		   			session.createQuery("Delete com.sitescape.team.domain.Tag where entity_id=:entityId and entity_type=:entityType")
		   				.setLong("entityId", binder.getId())
		   			   	.setParameter("entityType", binder.getEntityType().getValue())
		   			   	.executeUpdate();

		   			if (entryClass != null) {
		   				//finally delete the entries
		   				session.createQuery("Delete " + entryClass.getName() + " where parentBinder=:parent")
		       	   				.setEntity("parent", binder)
		       	   				.executeUpdate();		 		   				
		   			}
		   			//do ourselves or hibernate will flsuh
		   			session.createQuery("Delete  com.sitescape.team.domain.Binder where id=:id")
		   		    	.setLong("id", binder.getId().longValue())
		   		    	.executeUpdate();
		   			
		   			if (!binder.isRoot()) {
		   				session.getSessionFactory().evictCollection("com.sitescape.team.domain.Binder.binders", binder.getParentBinder().getId());
		   			}
		   			session.evict(binder);
		   			
		   			return null;
    	   		}
    	   	}
    	 );    	
 
	    			
	}
	
	public void move(final Binder binder) {
		//this should handle entries also
		getHibernateTemplate().execute(
	    	new HibernateCallback() {
	    		public Object doInHibernate(Session session) throws HibernateException {
	    			//update binder key for binders attributes only
	    	   		session.createQuery("update com.sitescape.team.domain.Attachment set owningBinderKey=:sortKey where owningBinderId=:id")
	    	   			.setString("sortKey", binder.getBinderKey().getSortKey())
	    	   			.setLong("id", binder.getId().longValue())
	    	   			.executeUpdate();
	    	   		session.createQuery("update com.sitescape.team.domain.Event set owningBinderKey=:sortKey where owningBinderId=:id")
	    	   			.setString("sortKey", binder.getBinderKey().getSortKey())
	    	   			.setLong("id", binder.getId().longValue())
	    	   			.executeUpdate();
	    	   		session.createQuery("update com.sitescape.team.domain.CustomAttribute set owningBinderKey=:sortKey where owningBinderId=:id")
    	   				.setString("sortKey", binder.getBinderKey().getSortKey())
    	   				.setLong("id", binder.getId().longValue())
    	   				.executeUpdate();
	    			//update binder key for binder and its contents
	    			session.createQuery("update com.sitescape.team.domain.ChangeLog set owningBinderKey=:sortKey where owningBinderId=:id")
	    				.setString("sortKey", binder.getBinderKey().getSortKey())
    	   				.setLong("id", binder.getId().longValue())
    	   				.executeUpdate();
	    			session.createQuery("update com.sitescape.team.domain.AuditTrail set owningBinderKey=:sortKey where owningBinderId=:id")
    				.setString("sortKey", binder.getBinderKey().getSortKey())
	   				.setLong("id", binder.getId().longValue())
	   				.executeUpdate();
		   			return null;
    	   		}
    	   	}
    	 );    	
	}


    public Object load(Class clazz, String id) {
        return getHibernateTemplate().get(clazz, id);
    }
    public Object load(Class clazz, Long id) {
        return getHibernateTemplate().get(clazz, id);         
    }
	/**
	 * Return a list containing an object array, where each object in a row representing the value of the requested attribute
	 * This is used to return a subset of object attributes
	 */
	public List loadObjects(ObjectControls objs, FilterControls filter) {
		return loadObjects(objs, filter, false);
	}
	public List loadObjectsCacheable(ObjectControls objs, FilterControls filter) {
		return loadObjects(objs, filter, true);
	}
	/**
	 * Return a lsit containing an object array, where each object in a row representing the value of the requested attribute
	 * This is used to return a subset of object.  In this case, we have a list of ids to
	 * add to the query.  We have to use named params to do this.
	 * @param objs
	 * @param filter
	 * @param ids
	 * @return
	 */
	public List loadObjects(final String query, final Map values) {
		return (List)getHibernateTemplate().execute(
		        new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
	                  	Query q = session.createQuery(query);
	                  	if (values != null) {
	                  		for (Iterator iter=values.entrySet().iterator(); iter.hasNext();) {
	                  			Map.Entry me = (Map.Entry)iter.next();
	                  			Object val = me.getValue();
	                  			if (val instanceof Collection) {
	                  				q.setParameterList((String)me.getKey(), (Collection)val);
	                  			} else if (val instanceof Object[]) {
	                  				q.setParameterList((String)me.getKey(), (Object[])val);
	                  			} else {
	                  				q.setParameter((String)me.getKey(), val);
	                  			}
	                  		}
	            		}
	            		return q.list();
		            }
		        }
		     );
	}
	/**
	 * Return a list ob objects
	 */
	public List loadObjects(Class className, FilterControls filter) {
		return loadObjects(new ObjectControls(className), filter);
	}
	public List loadObjectsCacheable(Class className, FilterControls filter) {
		return loadObjects(new ObjectControls(className), filter, true);
	}
	/**
	 * Load a list of objects, OR'ing ids
	 * @param ids
	 * @param className
	 * @return
	 */
   public List loadObjects(final Collection ids, final Class className, final Long zoneId) {
        if ((ids == null) || ids.isEmpty()) return new ArrayList();
        List result = (List)getHibernateTemplate().execute(
            new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                         Criteria crit = session.createCriteria(className)
                        	.add(Expression.in(Constants.ID, ids));
 
                        if (zoneId != null)
                        	crit.add(Expression.eq("zoneId", zoneId));
                        return crit.list();
                        
                    }
            }
        );
        return result;
        
    }	
   public List loadObjects(final Collection ids, final Class className, final Long zoneId, final List collections) {
       if ((ids == null) || ids.isEmpty()) return new ArrayList();
       List result = (List)getHibernateTemplate().execute(
           new HibernateCallback() {
                   public Object doInHibernate(Session session) throws HibernateException {
                        Criteria crit = session.createCriteria(className)
                       	.add(Expression.in(Constants.ID, ids));

                        if (zoneId != null)
                        	crit.add(Expression.eq("zoneId", zoneId));
                       for (int i=0; i<collections.size(); ++i) {
                    	   crit.setFetchMode((String)collections.get(i), FetchMode.JOIN);
                       }
                       List result = crit.list();
                       //eagar select results in duplicates
                       Set res = new HashSet(result);
                       result.clear();
                       result.addAll(res);
                       return result;
                       
                   }
           }
       );
       return result;
       
   }	
   public long countObjects(final Class clazz, final FilterControls filter) {
		Long result = (Long)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	StringBuffer query = new StringBuffer();
                  	query.append(" select count(*) from x in class " + clazz.getName());
                 	filter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = filter.getFilterValues();
            		for (int i=0; i<filterValues.size(); ++i) {
            			q.setParameter(i, filterValues.get(i));
            		}
 	                 List result = q.list();
 	                 if (result.isEmpty()) return null;
               	 	 return result.get(0);
               }
            }
		);
       if (result==null) return 0;
	   return result.longValue(); 	
	}
	
	public double averageColumn(final Class clazz, final String column, final FilterControls filter) {
		Double result = (Double)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	StringBuffer query = new StringBuffer();
                  	query.append(" select avg(x." + column + ") from x in class " + clazz.getName());
                 	filter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = filter.getFilterValues();
            		for (int i=0; i<filterValues.size(); ++i) {
            			q.setParameter(i, filterValues.get(i));
            		}
 	                 List result = q.list();
                  	 Iterator itr = result.iterator();

                	 if (itr.hasNext()) {
                		 Double count = (Double)itr.next();
                	 	return count;
             		}
                	
                	return null;
               }
            }
		);
       if (result==null) return 0;
	   return result.doubleValue();	
	}
	public long sumColumn(final Class clazz, final String column, final FilterControls filter) {
		Long result = (Long)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	StringBuffer query = new StringBuffer();
                  	query.append(" select sum(x." + column + ") from x in class " + clazz.getName());
                 	filter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = filter.getFilterValues();
            		for (int i=0; i<filterValues.size(); ++i) {
            			q.setParameter(i, filterValues.get(i));
            		}
 	                 List result = q.list();
                  	 Iterator itr = result.iterator();

                	 if (itr.hasNext()) {
                	 	Long count = (Long)itr.next();
                	 	return count;
             		}
                	
                	return null;
               }
            }
		);
       if (result==null) return 0;
	   return result.longValue();	
	}	

    // This code is used to reserve a name.  The binder field is used to check
	// uniqueness within a binder so it can be viewed by webdav.  The name is either an attachment
	// name belonging to an entry in the binder, or the title of a sub-folder.  Note, attachments on
	// the binder itself are not visible here and not visible through webdav.
	// the entity field combined with the binder field lets us get to the entry that
	// owns the attachment.  (also allows cleanup on delete entries)
	// Create our own session cause failures clear the existing session and don't want to 
	// necessarily cancel the running transaction.
	// It assumes the combination of binderId and entityId is enough to identify an entry
    public void registerFileName(Binder binder, DefinableEntity entity, String name) throws TitleException {
    	//Folderentries or binders only
        if (Validator.isNull(name)) throw new TitleException("");
  		LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.FILE, name);
		if (!(entity instanceof Binder)) le.setEntityId(entity.getId());
  		registerLibraryEntry(le);
   }
    // This code is used to reserve a name.  The binder field is used to check
	// uniqueness within a binder so it can be viewed as a wiki.  The name is the normalized title
	//  belonging to an entry in the binder, or the title of a sub-folder.  
	// Create our own session cause failures clear the existing session and don't want to 
	// necessarily cancel the running transaction.
	// It assumes the combination of binderId and entityId is enough to identify an entry
    public void registerTitle(Binder binder, DefinableEntity entity) throws TitleException {
    	//Folderentries or binders only
    	String name = entity.getNormalTitle();
   		LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.TITLE, name);
		if (!(entity instanceof Binder)) le.setEntityId(entity.getId());
   		registerLibraryEntry(le);
    }
    protected void registerLibraryEntry(LibraryEntry le) {
      	SessionFactory sf = getSessionFactory();
    	Session s = sf.openSession();
    	try {
    		s.save(le);
    		s.flush();
    	} catch (Exception ex) {
    		throw new TitleException(le.getName(), ex);
    	} finally {
    		s.close();
    	}    	
    	
    }
    //attachments or foldernames
    public void unRegisterFileName(Binder binder, String name) {
    	try {
    		unRegisterLibraryEntry(new LibraryEntry(binder.getId(), LibraryEntry.FILE, name));
    	} catch (Exception ex) {
				logger.error("Error removeing library entry for: " + binder + " file" +  ex.getLocalizedMessage());
	   	}    	
    }
    //normalized titles in parentbinder
    public void unRegisterTitle(Binder binder, String name) {
    	try {
    		unRegisterLibraryEntry(new LibraryEntry(binder.getId(), LibraryEntry.TITLE, name));
    	} catch (Exception ex) {
			logger.error("Error removeing library entry for: " + binder + " title" +  ex.getLocalizedMessage());
	   	}   	
    }
    //create our own session cause failures clear the existing session
    protected void unRegisterLibraryEntry(LibraryEntry le) {
     	SessionFactory sf = getSessionFactory();
    	Session s = sf.openSession();
    	try {
			LibraryEntry exist = (LibraryEntry)s.get(LibraryEntry.class, le);
			if (exist != null) s.delete(exist);
    		s.flush();
	   	} finally {
    		s.close();
    	}    	
     	
    }
    //done in the current transaction on a binder title rename or remove attachment
    public void updateFileName(Binder binder, DefinableEntity entity, String oldName, String newName) throws TitleException {
    	//Folderentries or binders only
       if (Validator.isNotNull(newName) && newName.equalsIgnoreCase(oldName)) return;
 		if (oldName != null) {
	        LibraryEntry oldLe = new LibraryEntry(binder.getId(), LibraryEntry.FILE, oldName);
			if (!(entity instanceof Binder)) oldLe.setEntityId(entity.getId());
	        removeOldName(oldLe, entity);
 		}
 		//this was a remove
		if (Validator.isNull(newName)) return;
		//register new name
		LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.FILE, newName);
		if (!(entity instanceof Binder)) le.setEntityId(entity.getId());
		addNewName(le, entity);
     }
    //done in the current transaction on a binder title rename or remove attachment
    public void updateTitle(Binder binder, DefinableEntity entity, String oldName, String newName) throws TitleException {
    	//Folderentries or binders only
       if (Validator.isNotNull(newName) && newName.equalsIgnoreCase(oldName)) return;
        if (entity instanceof Entry) {
        	//replies are not registered
        	if (!((Entry)entity).isTop()) return;
        }
        if (oldName != null) {
	        LibraryEntry oldLe = new LibraryEntry(binder.getId(), LibraryEntry.TITLE, oldName);
			if (!(entity instanceof Binder)) oldLe.setEntityId(entity.getId());
	        removeOldName(oldLe, entity);
 		}
		//this was a remove
		if (Validator.isNull(newName)) return;
		//register new name
		LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.TITLE, newName);
		if (!(entity instanceof Binder)) le.setEntityId(entity.getId());
		addNewName(le, entity);
    }
    protected void removeOldName(LibraryEntry oldLe, DefinableEntity entity) {
		LibraryEntry le = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, oldLe);
		if (le != null) {
			//it exists, is it ours?
			if (le.getEntityId() == null) {
				//found a sub-folder - is it mine?
				if (oldLe.getEntityId() == null) {
					//delete the old one; delete cause changing primary key
					delete(le);
					flush();
				}			
			} else if (le.getEntityId().equals(oldLe.getEntityId())) {
				//belongs to this entry; delete cause changing primary key
				delete(le);
				flush();
			}
		}
    	
    }
    protected void addNewName(LibraryEntry newLe, DefinableEntity entity) {
		try {
			LibraryEntry exist = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, newLe);
			if (exist == null) {
				save(newLe);
			}
			else throw new TitleException(newLe.getName());
		} catch (Exception ex) {
			throw new TitleException(newLe.getName());
		}  	
    	
    }
    public  Long findFileNameEntryId(Binder binder, String name) {
    	LibraryEntry le = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, new LibraryEntry(binder.getId(),LibraryEntry.FILE, name));
    	if (le == null) throw new NoObjectByTheIdException("errorcode.no.library.entry.by.the.id", new Object[]{binder.getId(), name});
    	return le.getEntityId();

    }
	//Clears only folderentries. sub-folder remain since they must always be unique for webdav to traverse the tree
    public void clearFileNames(Binder binder) {
    	executeUpdate("delete from com.sitescape.team.domain.LibraryEntry where binderId=" +
    			binder.getId() + " and type=" + LibraryEntry.FILE.toString() + " and not entityId is null");
    	
    }
    //Clear all titles, don't need if uniqueTitles not enabled.
    public void clearTitles(Binder binder) {
    	executeUpdate("delete from com.sitescape.team.domain.LibraryEntry where binderId=" +
    			binder.getId() + " and type=" + LibraryEntry.TITLE.toString());
    	
    }
    public List findCompanies() {
		return (List)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
                 	return session.createCriteria(Workspace.class)
             				.add(Expression.eq("internalId", ObjectKeys.TOP_WORKSPACE_INTERNALID))
             				.list();
               }
            }
		);
	}
	public Workspace findTopWorkspace(final String zoneName) {
        return (Workspace)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List results = session.createCriteria(Workspace.class)
                             		.add(Expression.eq("internalId", ObjectKeys.TOP_WORKSPACE_INTERNALID))
                             		.add(Expression.eq("name", zoneName))
                             		.setCacheable(true)
                             		.list();
                        if (results.isEmpty()) {
                            throw new NoWorkspaceByTheNameException(ObjectKeys.TOP_WORKSPACE_INTERNALID); 
                        }
                        return (Workspace)results.get(0);
                    }
                }
             );

	}
	
	/**
	 * Load binder and validate it belongs to the zone
	 * @param binderId
	 * @param zoneId
	 * @return
	 */
    public Binder loadBinder(Long binderId, Long zoneId) {  
		Binder binder = (Binder)load(Binder.class, binderId);
        if (binder == null) {throw new NoBinderByTheIdException(binderId);};
        if (!binder.getZoneId().equals(zoneId)) {
        	throw new NoBinderByTheIdException(binderId);
        }
        return binder;
    }

    public Binder loadReservedBinder(final String reservedId, final Long zoneId) {
        return (Binder)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List results = session.createCriteria(Binder.class)
                             		.add(Expression.eq("internalId", reservedId))
                             		.add(Expression.eq("zoneId", zoneId))
                             		.setCacheable(true)
                             		.list();
                        if (results.isEmpty()) {
                            throw new NoBinderByTheNameException(reservedId); 
                        }
                        return results.get(0);
                    }
                }
             );
    }
 
    public Definition loadReservedDefinition(final String reservedId, final Long zoneId) {
        return (Definition)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List results = session.createCriteria(Definition.class)
                             		.add(Expression.eq("internalId", reservedId))
                             		.add(Expression.eq("zoneId", zoneId))
                             		.setCacheable(true)
                             		.list();
                        if (results.isEmpty()) {
                            throw new NoDefinitionByTheIdException(reservedId); 
                        }
                        return results.get(0);
                    }
                }
             );
   	
    }
	public Definition loadDefinition(String defId, Long zoneId) {
  		Definition def = (Definition)load(Definition.class, defId);
        if (def == null) {throw new NoDefinitionByTheIdException(defId);}
        //make sure from correct zone
        if (!def.getZoneId().equals(zoneId)) {throw new NoDefinitionByTheIdException(defId);}
  		return def;
	}

	public List loadDefinitions(Long zoneId) {
		OrderBy order = new OrderBy();
		order.addColumn("type");
		order.addColumn("name");
		FilterControls filter = new FilterControls("zoneId", zoneId);
		filter.setOrderBy(order);
    	return loadObjects(new ObjectControls(Definition.class), filter);
	}
	public List loadDefinitions(Long zoneId, int type) {
		OrderBy order = new OrderBy();
		order.addColumn("name");
		FilterControls filter = new FilterControls(new String[]{"zoneId", "type"}, new Object[]{zoneId, Integer.valueOf(type)});
		filter.setOrderBy(order);
    	return loadObjectsCacheable(new ObjectControls(Definition.class), filter);
	}
	
	// return top level configurations
	public List loadConfigurations(final Long zoneId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(TemplateBinder.class)
                 		.add(Expression.isNull("parentBinder"))
                 		.add(Expression.eq("zoneId", zoneId))
                 		.addOrder(Order.asc("definitionType"))
                 		.addOrder(Order.asc("templateTitle"))
	                 	.list();
	                }
	            }
	        );
	}
	public List loadConfigurations(final Long zoneId, final int type) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(TemplateBinder.class)
                 		.add(Expression.isNull("parentBinder"))
                 		.add(Expression.eq("zoneId", zoneId))
                 		.add(Expression.eq("definitionType", type))
                 		.addOrder(Order.asc("templateTitle"))
	                 	.list();
	                }
	            }
	        );
	}
	//associations not maintained from definition to binders, only from
	//binders to definitions
	public void delete(final Definition def) {
		getHibernateTemplate().execute(
	        new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException {
	            	//see if in use
	            	List results;
	               	if (def.getType() != Definition.WORKFLOW) {
	               		long count = countObjects(com.sitescape.team.domain.FolderEntry.class, new FilterControls("entryDef", def));
	               		if (count > 0) throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));
	               		count = countObjects(com.sitescape.team.domain.Principal.class, new FilterControls("entryDef", def));
	               		if (count > 0) throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));
	               		count = countObjects(com.sitescape.team.domain.Binder.class, new FilterControls("entryDef", def));
	               		if (count > 0) throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));
	               		results = session.createCriteria(Binder.class)
	               			.createCriteria("definitions")
	               			.add(Expression.eq("id", def.getId()))
	               			.list();
		               	//definition not used by an entry, but may be part of a binder config
		               	//it is safe to remove it now.
		               	for (int i=0; i<results.size(); ++i ) {
		               		Binder b = (Binder)results.get(i);
		               		b.removeDefinition(def);
		               	}
	               	} else {
	               		long count = countObjects(com.sitescape.team.domain.WorkflowState.class, new FilterControls("definition", def));
	               		if (count > 0) throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));

	               		results = session.createCriteria(Binder.class)
	               			.createCriteria("workflowAssociations")
	               			.add(Expression.eq("id", def.getId()))
	               			.list();
		               	//definition not used by an entry, but may be part of a binder config
		               	//it is safe to remove it now.
		               	for (int i=0; i<results.size(); ++i ) {
		               		Binder b = (Binder)results.get(i);
		               		b.removeWorkflow(def);
		               	}
	               	}
	               	session.delete(def);
		   			session.getSessionFactory().evict(Definition.class, def.getId());
	               	return null;
	            }
	        }
	    );		
	}
	
	/**
	 * Perform a write of a new object now using a new Session so we can commit it fast
	 * @param obj
	 */
	public Object saveNewSession(Object obj) {
       	SessionFactory sf = getSessionFactory();
    	Session s = sf.openSession();
    	try {
    		s.save(obj);
    		s.flush();
    	} finally {
    		s.close();
    	}
    	//attach to current session. This will fail if read only
    	// by that should mean no-one will update it so that is okay
    	try {
    		update(obj);
    	} catch (InvalidDataAccessApiUsageException da) {};
    	
    	return obj;
		
	}
	public List loadPostings(Long zoneId) {
    	return loadObjects(new ObjectControls(PostingDef.class), new FilterControls("zoneId", zoneId));
	}
	public PostingDef loadPosting(String postingId, Long zoneId) {
		PostingDef post = (PostingDef)load(PostingDef.class, postingId);
        if (post == null) {throw new NoObjectByTheIdException("errorcode.no.posting.by.the.id", postingId);}
        //make sure from correct zone
        if (!post.getZoneId().equals(zoneId)) {throw new NoObjectByTheIdException("errorcode.no.posting.by.the.id", postingId);}
  		return post;
		
	}

	//build collections manually as an optimization for indexing
	//evict from session cache, so not longer available to everyone else
	//The entries must be of the same type
	public void bulkLoadCollections(Collection entries) {
		if ((entries == null) || entries.isEmpty())  return;
       	final TreeSet sorted = new TreeSet(new LongIdComparator());
       	sorted.addAll(entries);
		getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                	List ids = new ArrayList();
                	List readObjs = new ArrayList();
                	Entry entry=null;
                	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                		entry = (Entry)iter.next();
                		//initialize collections
                		entry.setIndexAttachments(new HashSet());
                		entry.setIndexCustomAttributes(new HashMap());
                		entry.setIndexEvents(new HashSet());
                 		ids.add(entry.getId());
                	}
         	   		EntityIdentifier id = entry.getEntityIdentifier();
         	   	 	List objs;
            		HashSet tSet;
            		if (entry instanceof WorkflowControlledEntry) {
                		WorkflowControlledEntry wEntry = (WorkflowControlledEntry)entry;
                	
                		//Load workflow states
                		objs = session.createCriteria(WorkflowState.class)
                    						.add(Expression.eq("owner.ownerType", id.getEntityType().name()))
                    						.add(Expression.in("owner.ownerId", ids))
                    						.addOrder(Order.asc("owner.ownerId"))
                    						.list();
                   
                		readObjs.addAll(objs);
                		for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                			wEntry = (WorkflowControlledEntry)iter.next();
                			tSet = new HashSet();
                			for (int i=0; i<objs.size(); ++i) {
                				WorkflowState obj = (WorkflowState)objs.get(i);
                				if (wEntry.equals(obj.getOwner().getEntity())) {
                					tSet.add(obj);
                				} else break;
                			}
                			wEntry.setIndexWorkflowStates(tSet);
                			objs.removeAll(tSet);
                		}
                	}
                	//Load attachments
                   	objs = session.createCriteria(Attachment.class)
   						.add(Expression.eq("owner.ownerType", id.getEntityType().name()))
                    	.add(Expression.in("owner.ownerId", ids))
                  		.addOrder(Order.asc("owner.ownerId"))
                  		.list();
                   	readObjs.addAll(objs);
                   	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (Entry)iter.next();
                   		tSet = new HashSet();
                   		while (objs.size() > 0) {
                   			Attachment obj = (Attachment)objs.get(0);
                  			if (entry.equals(obj.getOwner().getEntity())) {
                  				if (!(obj instanceof VersionAttachment)) {
                  					tSet.add(obj);
                  				}
                  				objs.remove(0);
                   			} else break;
                   		}
                   		entry.setIndexAttachments(tSet);
                     }
                	//Load Events states
                  	objs = session.createCriteria(Event.class)
                    	.add(Expression.eq("owner.ownerType", id.getEntityType().name()))
                    	.add(Expression.in("owner.ownerId", ids))
                 		.addOrder(Order.asc("owner.ownerId"))
                  		.list();
                   	readObjs.addAll(objs);
                 	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (Entry)iter.next();
                   		tSet = new HashSet();
                   		for (int i=0; i<objs.size(); ++i) {
                   			Event obj = (Event)objs.get(i);
                   			if (entry.equals(obj.getOwner().getEntity())) {
                   				tSet.add(obj);
                   			} else break;
                   		}
                   		entry.setIndexEvents(tSet);
                   		objs.removeAll(tSet);
                    }
                	//Load customAttributes
                 	objs = session.createCriteria(CustomAttribute.class)
                    	.add(Expression.eq("owner.ownerType", id.getEntityType().name()))
       					.add(Expression.in("owner.ownerId", ids))
                 		.addOrder(Order.asc("owner.ownerId"))
                  		.list();
                   	readObjs.addAll(objs);
                  	HashMap tMap;
                   	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (Entry)iter.next();
                   		tMap = new HashMap();
                   		while (objs.size() > 0) {
                   			CustomAttribute obj = (CustomAttribute)objs.get(0);
                   			if (entry.equals(obj.getOwner().getEntity())) {
                   				if (obj instanceof CustomAttributeListElement) {
                   					CustomAttributeListElement lEle = (CustomAttributeListElement)obj;
                   					lEle.getParent().addIndexValue(lEle);
                   				} else {
                   					tMap.put(obj.getName(), obj);
                   				}
                   				objs.remove(0);
                   			} else break;
                   		}
                   		entry.setIndexCustomAttributes(tMap);
                	}
                   	for (int i=0; i<readObjs.size(); ++i) {
                   		evict(readObjs.get(i));
                   	}
                   	return sorted;
                }
           }
        );  
	}
	
	
	public Tag loadTag(final String tagId) {
        Tag t =(Tag)getHibernateTemplate().get(Tag.class, tagId);
        if (t != null) return t;
        throw new NoObjectByTheIdException("errorcode.no.tag.by.the.id", tagId);
	}
	//The entries must be of the same type
	//Used by indexing bulk load
	public Map loadAllTagsByEntity(final Collection entityIds) {
		if (entityIds.isEmpty()) return new HashMap();
		
		List<Tag> tags = (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                	List ids = new ArrayList();
	                	EntityIdentifier id =null;
	                	for (Iterator iter=entityIds.iterator(); iter.hasNext();) {
		                	id = (EntityIdentifier)iter.next();
		                	ids.add(id.getEntityId());
	                	}
	                	
	                	return session.createCriteria(Tag.class)
                 		.add(Expression.in("entityIdentifier.entityId", ids))
       					.add(Expression.eq("entityIdentifier.type", id.getEntityType().getValue()))
                 		.addOrder(Order.asc("entityIdentifier.entityId"))
	                 	.list();
	                }
	            }
	        );
		Map result = new HashMap();
		for (Iterator iter=entityIds.iterator(); iter.hasNext();) {
			EntityIdentifier id = (EntityIdentifier)iter.next();
			List tList = new ArrayList();
			
			while (!tags.isEmpty()) {
				Tag tag = tags.get(0);
				if (tag.getEntityIdentifier().equals(id)) { 
					tList.add(tag);
					tags.remove(0);
				} else break;
			}
			result.put(id, tList);
		}
		return result;
		
	}
	//Used by indexing
	public List loadAllTagsByEntity(final EntityIdentifier entityId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
                 		.addOrder(Order.asc("name"))
	                 	.list();
	                }
	            }
	        );
		
	}

	public List loadCommunityTagsByEntity(final EntityIdentifier entityId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
       					.add(Expression.eq("public", true))
                 		.addOrder(Order.asc("name"))
	                 	.list();
	                }
	            }
	        );
		
	}
	public List loadPersonalTagsByEntity(final EntityIdentifier entityId, final EntityIdentifier ownerId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
       					.add(Expression.eq("public",false))
                 		.addOrder(Order.asc("name"))
	                 	.list();
	                }
	            }
	        );
		
	}
	public List loadPersonalTagsByOwner(final EntityIdentifier ownerId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
       					.add(Expression.eq("public",false))
                 		.addOrder(Order.asc("name"))
	                 	.list();
	                }
	            }
	        );
		
	}	
    //load public and personal private tags for an entity.  Optimization
    //order by id and name
    public List loadEntityTags(final EntityIdentifier entityIdentifier, final EntityIdentifier ownerIdentifier) {
	   	return (List)getHibernateTemplate().execute(
		     	new HibernateCallback() {
		       		public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
       					.add(Expression.eq("entityIdentifier.type", entityIdentifier.getEntityType().getValue()))
                 		.add(Expression.eq("entityIdentifier.entityId", entityIdentifier.getEntityId()))
                        .add(Expression.disjunction()
              					.add(Expression.eq("public",true))
              					.add(Expression.conjunction()
              							.add(Expression.eq("ownerIdentifier.entityId", ownerIdentifier.getEntityId()))
              							.add(Expression.eq("ownerIdentifier.type", ownerIdentifier.getEntityType().getValue()))
              					)
              			)
                 		.addOrder(Order.asc("name"))
	                 	.list();
	    	   		}
	    	   	}
	    	 );    	
   	
    }
	
	public List loadSubscriptionByEntity(final EntityIdentifier entityId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Subscription.class)
                 		.add(Expression.eq("id.entityId", entityId.getEntityId()))
       					.add(Expression.eq("id.entityType", entityId.getEntityType().getValue()))
	                 	.list();
	                }
	            }
	        );
		
	}
	private List loadObjects(final ObjectControls objs, final FilterControls filter, final boolean cacheable) {
		return (List)getHibernateTemplate().execute(
	        new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException {
	            	StringBuffer query = objs.getSelectAndFrom("x");
                 	filter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = filter.getFilterValues();
           			for (int i=0; i<filterValues.size(); ++i) {
           				q.setParameter(i, filterValues.get(i));
            		}
           			q.setCacheable(cacheable);
 	                return q.list();
	            }
	        }
	     );
	}
	public UserDashboard loadUserDashboard(final EntityIdentifier ownerId, final Long binderId) {
		List result = (List)getHibernateTemplate().execute(
				new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
	            		return session.createCriteria(UserDashboard.class)
	            		.add(Expression.eq("binderId", binderId))
	            		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
	            		.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
	            		.setCacheable(true)
	            		.list();
		            }
		        }
		 );
		if (result.isEmpty()) return null;
		return (UserDashboard)result.get(0);
	}
	public EntityDashboard loadEntityDashboard(final EntityIdentifier ownerId) {
		List result = (List)getHibernateTemplate().execute(
				new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
		            		return session.createCriteria(EntityDashboard.class)
		            		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
		            		.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
		            		.setCacheable(true)
		            		.list();
		            		
		            }
		        }
		 );
		if (result.isEmpty()) return null;
		return (EntityDashboard)result.get(0);
	}

	public Dashboard loadDashboard(String id) {
		Dashboard d = (Dashboard)getHibernateTemplate().get(Dashboard.class, id);
        if (d != null) return d;
        throw new NoObjectByTheIdException("errorcode.no.dashboard.by.the.id", id);
	}
	public void executeUpdate(final String query) {
    	getHibernateTemplate().execute(
        	   	new HibernateCallback() {
        	   		public Object doInHibernate(Session session) throws HibernateException {
    		   			session.createQuery(query)
		   				.executeUpdate();
          	   		
          	   			return null;

        	   		}
        	   	}
       	   	);
	}
	
}
