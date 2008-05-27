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
package com.sitescape.team.dao.impl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.LongIdComparator;
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
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.LibraryEntry;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoBinderByTheNameException;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.NotifyStatus;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.SimpleName;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.TitleException;
import com.sitescape.team.domain.UserDashboard;
import com.sitescape.team.domain.VersionAttachment;
import com.sitescape.team.domain.WorkflowControlledEntry;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.util.Validator;
/**
 * @author Jong Kim
 *
 */
public class CoreDaoImpl extends HibernateDaoSupport implements CoreDao {
	protected int inClauseLimit=1000;
	protected Log logger = LogFactory.getLog(getClass());

    /**
     * Called after bean is initialized.  
     */
	protected void initDao() throws Exception {
		//some database limit the number of terms 
		inClauseLimit=SPropsUtil.getInt("db.clause.limit", 1000);
	}

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
		if (obj instanceof Collection) {
			final Collection objs = (Collection)obj;
			getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException {
							Iterator iter = objs.iterator();
							while (iter.hasNext()) {
								session.evict(iter.next());
							}
							return null;
						}
					}
			);
			
		} else getSession().evict(obj);
	}
	public void refresh(Object obj) {
		getSession().refresh(obj);
	}
	public void lock(Object obj) {
		getSession().refresh(obj, LockMode.UPGRADE);
		//getSession().lock(obj, LockMode.WRITE);
	}
	public void save(Object obj) {
		if (obj instanceof Collection) {
			final Collection objs = (Collection)obj;
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
		} else getHibernateTemplate().save(obj);
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
	public SFQuery queryObjects(final ObjectControls objs, FilterControls filter, final Long zoneId) { 
		final FilterControls myFilter = filter==null?new FilterControls():filter;
		if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
       Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
    	            	StringBuffer query = objs.getSelectAndFrom("x");
    	            	myFilter.appendFilter("x", query);
                      	Query q = session.createQuery(query.toString());
                		List filterValues = myFilter.getFilterValues();
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
	       	   		session.createQuery("DELETE com.sitescape.team.domain.NotifyStatus where " + whereClause)
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
	    			if (def != null) {
	    				binder.setPosting(null);
	    				session.delete(def);
	    			}
	    			session.flush();
	    			Statement s = null;
	    			try {
		   				s = session.connection().createStatement();
		   				s.executeUpdate("delete from SS_DefinitionMap where binder=" + binder.getId());
		   				s.executeUpdate("delete from SS_WorkflowMap where binder=" + binder.getId());
		   				s.executeUpdate("delete from SS_Notifications where binderId=" + binder.getId());
		   			} catch (SQLException sq) {
		   				throw new HibernateException(sq);
     	   			} finally {
       	   				try {if (s != null) s.close();} catch (Exception ex) {};
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
		   			//delete simple names for this binder
		   			session.createQuery("DELETE com.sitescape.team.domain.SimpleName where binderId=:binderId")
		   				.setLong("binderId", binder.getId())
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
    	   			session.createQuery("update com.sitescape.team.domain.NotifyStatus set owningBinderKey=:sortKey where owningBinderId=:id")
    	   				.setString("sortKey", binder.getBinderKey().getSortKey())
    	   				.setLong("id", binder.getId().longValue())
    	   				.executeUpdate();
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
	public List loadObjects(ObjectControls objs, FilterControls filter, Long zoneId) {
		return loadObjects(objs, filter, zoneId, false);
	}
	public List loadObjectsCacheable(ObjectControls objs, FilterControls filter, Long zoneId) {
		return loadObjects(objs, filter, zoneId, true);
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
	                  				if (((Collection)val).size() > inClauseLimit) throw new IllegalArgumentException("Collection to large");
	                  				if (((Collection)val).size() == 0) throw new IllegalArgumentException("Collection to small");
	                  				q.setParameterList((String)me.getKey(), (Collection)val);
	                  			} else if (val instanceof Object[]) {
	                  				if (((Object[])val).length > inClauseLimit) throw new IllegalArgumentException("Collection to large");
	                  				if (((Object[])val).length == 0) throw new IllegalArgumentException("Collection to small");
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
	public List loadObjects(Class className, FilterControls filter, Long zoneId) {
		return loadObjects(new ObjectControls(className), filter, zoneId);
	}
	public List loadObjectsCacheable(Class className, FilterControls filter, Long zoneId) {
		return loadObjectsCacheable(new ObjectControls(className), filter, zoneId);
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
                    	if (ids.size() <= inClauseLimit) {
                            Criteria crit = session.createCriteria(className)
                            	.add(Expression.in(Constants.ID, ids));

                            if (zoneId != null)
                            	crit.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId));
                            return crit.list();
     
                    	} 
                    	//break list into chunks
                    	List idList = new ArrayList(ids); // need list for sublist method
                    	List results = new ArrayList(); 
        				for (int i=0; i<idList.size(); i+=inClauseLimit) {
        					List subList = idList.subList(i, Math.min(idList.size(), i+inClauseLimit));
        					Criteria crit = session.createCriteria(className)
                        		.add(Expression.in(Constants.ID, subList));
 
        					if (zoneId != null)
        						crit.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId));
        					results.addAll(crit.list());
        				}
        				return results;
                        
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
                   	//break list into chunks
                   	List idList = new ArrayList(ids); // need list for sublist method
                   	Set results = new HashSet(); //fetch returns duplicates, so weed them out using a set
       				for (int i=0; i<idList.size(); i+=inClauseLimit) {
       					List subList = idList.subList(i, Math.min(idList.size(), i+inClauseLimit));
       					Criteria crit = session.createCriteria(className)
                       		.add(Expression.in(Constants.ID, subList));

                        if (zoneId != null)
                        	crit.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId));
                        for (int j=0; j<collections.size(); ++j) {
                    	   crit.setFetchMode((String)collections.get(j), FetchMode.JOIN);   
                        }
                        
                       //eagar select results in duplicates
                       results.addAll(crit.list());
       				}
       				idList.clear();
       				idList.addAll(results);
                    return idList;
                   }
           }
       );
       return result;
       
   }	
   public long countObjects(final Class clazz, FilterControls filter, Long zoneId) {
   	final FilterControls myFilter = filter==null?new FilterControls():filter;
	if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
		Long result = (Long)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	StringBuffer query = new StringBuffer();
                  	query.append(" select count(*) from x in class " + clazz.getName());
                  	myFilter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = myFilter.getFilterValues();
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
	
	public double averageColumn(final Class clazz, final String column, FilterControls filter, Long zoneId) {
    	final FilterControls myFilter = filter==null?new FilterControls():filter;
    	if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
		Double result = (Double)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	StringBuffer query = new StringBuffer();
                  	query.append(" select avg(x." + column + ") from x in class " + clazz.getName());
                  	myFilter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = myFilter.getFilterValues();
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
	public long sumColumn(final Class clazz, final String column, FilterControls filter, Long zoneId) {
    	final FilterControls myFilter = filter==null?new FilterControls():filter;
    	if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
    	Long result = (Long)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	StringBuffer query = new StringBuffer();
                  	query.append(" select sum(x." + column + ") from x in class " + clazz.getName());
                  	myFilter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = myFilter.getFilterValues();
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
    public Long getEntityIdForMatchingTitle(Long binderId, String title) {
    	LibraryEntry le = new LibraryEntry(binderId, LibraryEntry.TITLE, title);
		LibraryEntry exist = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, le);
		if(exist != null)
			return exist.getEntityId();
		else
			return null;
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
			if(ex instanceof TitleException)
				throw (TitleException) ex;
			else
				throw new TitleException(newLe.getName(),ex);
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
    			binder.getId() + " and type=" + LibraryEntry.FILE.toString() + " and not entityId is null", null);
    	
    }
    //Clear all titles, don't need if uniqueTitles not enabled.
    public void clearTitles(Binder binder) {
    	executeUpdate("delete from com.sitescape.team.domain.LibraryEntry where binderId=" +
    			binder.getId() + " and type=" + LibraryEntry.TITLE.toString(), null);
    	
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
                            throw new NoWorkspaceByTheNameException(zoneName); 
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
                             		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
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
                             		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
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
        if (zoneId != null && !def.getZoneId().equals(zoneId)) {throw new NoDefinitionByTheIdException(defId);}
  		return def;
	}

	public Definition loadDefinitionByName(final String name, final Long zoneId) {
		return (Definition)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	Definition def = (Definition)session.getNamedQuery("find-definition-by-name")
                 		.setLong(ParameterNames.ZONE_ID, zoneId)
                 		.setString(ParameterNames.NAME, name)
                 		.setCacheable(true)
                		.uniqueResult();
	                    if (def == null) {throw new NoDefinitionByTheIdException(name);}
	                    return def;
	                }
	            }
	        );
 	}
	public List loadDefinitions(Long zoneId) {
		OrderBy order = new OrderBy();
		order.addColumn("type");
		order.addColumn("name");
		FilterControls filter = new FilterControls();
		filter.setOrderBy(order);
    	return loadObjects(new ObjectControls(Definition.class), filter, zoneId);
	}
	public List loadDefinitions(Long zoneId, int type) {
		OrderBy order = new OrderBy();
		order.addColumn("name");
		FilterControls filter = new FilterControls("type", Integer.valueOf(type));
		filter.setOrderBy(order);
    	return loadObjectsCacheable(new ObjectControls(Definition.class), filter, zoneId);
	}
	
	// return top level configurations
	public List loadTemplates(final Long zoneId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(TemplateBinder.class)
                 		.add(Expression.isNull("parentBinder"))
                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
                 		.addOrder(Order.asc("definitionType"))
                 		.addOrder(Order.asc("templateTitle"))
	                 	.list();
	                }
	            }
	        );
	}
	public List loadTemplates(final Long zoneId, final int type) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(TemplateBinder.class)
                 		.add(Expression.isNull(ObjectKeys.FIELD_ENTITY_PARENTBINDER))
                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
                 		.add(Expression.eq("definitionType", type))
                 		.addOrder(Order.asc(ObjectKeys.FIELD_TEMPLATE_TITLE))
	                 	.list();
	                }
	            }
	        );
	}
	public TemplateBinder loadTemplate(Long templateId, Long zoneId) {
		TemplateBinder template = (TemplateBinder)load(TemplateBinder.class, templateId);
        if (template == null) {throw new NoBinderByTheIdException(templateId);}
        //make sure from correct zone
        if (zoneId != null && !template.getZoneId().equals(zoneId)) {throw new NoBinderByTheIdException(templateId);}
  		return template;
	}

	public TemplateBinder loadTemplateByName(final String name, final Long zoneId) {
		return (TemplateBinder)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                	TemplateBinder template = (TemplateBinder)session.createCriteria(TemplateBinder.class)
                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
                  		.add(Expression.isNull(ObjectKeys.FIELD_ENTITY_PARENTBINDER))
                		.add(Expression.eq(ObjectKeys.FIELD_BINDER_NAME, name))
                		.uniqueResult();
	                    if (template == null) {throw new NoBinderByTheNameException(name);}
	                    return template;
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
	               		long count = countObjects(com.sitescape.team.domain.FolderEntry.class, new FilterControls("entryDef", def), def.getZoneId());
	               		if (count > 0) throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));
	               		count = countObjects(com.sitescape.team.domain.Principal.class, new FilterControls("entryDef", def), def.getZoneId());
	               		if (count > 0) throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));
	               		count = countObjects(com.sitescape.team.domain.Binder.class, new FilterControls("entryDef", def), def.getZoneId());
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
	               		long count = countObjects(com.sitescape.team.domain.WorkflowState.class, new FilterControls("definition", def), def.getZoneId());
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
    	return loadObjects(new ObjectControls(PostingDef.class), null, zoneId);
	}
	public PostingDef loadPosting(String postingId, Long zoneId) {
		PostingDef post = (PostingDef)load(PostingDef.class, postingId);
        if (post == null) {throw new NoObjectByTheIdException("errorcode.no.posting.by.the.id", postingId);}
        //make sure from correct zone
        if (!post.getZoneId().equals(zoneId)) {throw new NoObjectByTheIdException("errorcode.no.posting.by.the.id", postingId);}
  		return post;
		
	}
	public PostingDef findPosting(final String emailAddress, Long zoneId) {
		return (PostingDef)getHibernateTemplate().execute(
		        new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
	               		return session.createCriteria(PostingDef.class)
	               						.add(Expression.eq("emailAddress", emailAddress))
	               						.uniqueResult();
		            }
		        }
		     );
	}

	//build collections manually as an optimization for indexing
	//evict from session cache, so no longer available to everyone else
	//The entries must be of the same type
	public void bulkLoadCollections(Collection entries) {
		if ((entries == null) || entries.isEmpty())  return;
 		if (entries.size() > inClauseLimit) throw new IllegalArgumentException("Collection to large");
       	final TreeSet sorted = new TreeSet(new LongIdComparator());
       	sorted.addAll(entries);
		getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                	List readObjs = new ArrayList();
                	DefinableEntity entry=(DefinableEntity)sorted.iterator().next();
          	   		EntityIdentifier id = entry.getEntityIdentifier();
         	   		String key = "owner.binder";
         	   		if (id.getEntityType().equals(EntityIdentifier.EntityType.folderEntry)) {
         	   			key = "owner.folderEntry";
         	   		} else if (id.getEntityType().equals(EntityIdentifier.EntityType.user) ||
         	   				id.getEntityType().equals(EntityIdentifier.EntityType.group)) {
         	   			key = "owner.principal";
         	   		}
         	   	 	List objs;
            		HashSet tSet;
            		if (entry instanceof WorkflowControlledEntry) {
                		WorkflowControlledEntry wEntry = (WorkflowControlledEntry)entry;
                	
                		//Load workflow states
                		objs = session.createCriteria(WorkflowState.class)
                    						.add(Expression.in(key, sorted))
                    						.addOrder(Order.asc(key))
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
                	//Load file attachments
            		//If there are every any others, will need to handle separately
                   	objs = session.createCriteria(FileAttachment.class)
                   		.add(Expression.in(key, sorted))
                   		.addOrder(Order.asc(key))
                  		.setFetchMode("fileVersions", FetchMode.JOIN) //needed during indexing 
                  		.list();
                   	readObjs.addAll(objs);
                   	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (DefinableEntity)iter.next();
                   		tSet = new HashSet();
                   		while (objs.size() > 0) {
                   			Attachment obj = (Attachment)objs.get(0);
                  			if (entry.equals(obj.getOwner().getEntity())) { //won't be read using foreignKey to do lookup
                  				if (!(obj instanceof VersionAttachment)) {
                  					tSet.add(obj);
                  				}
                  				objs.remove(0);//remove now so get versions out of list
                   			} else break;
                   		}
                   		entry.setIndexAttachments(tSet);
                     }
                	//Load Events states
                  	objs = session.createCriteria(Event.class)
                  		.add(Expression.in(key, sorted))
                  		.addOrder(Order.asc(key))
                  		.list();
                   	readObjs.addAll(objs);
                 	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (DefinableEntity)iter.next();
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
                 	//Cannot criteria query, cause different order-by is specified in mapping files and it appears to take precedence
                 	objs = session.createQuery("from com.sitescape.team.domain.CustomAttribute att left join fetch att.values where att." + key + "  in (:pList) order by att." + key)
   					.setParameterList("pList", sorted)
               		.list();
                   	readObjs.addAll(objs);
                  	HashMap tMap;
                   	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (DefinableEntity)iter.next();
                   		tMap = new HashMap();
                   		while (objs.size() > 0) {
                   			CustomAttribute obj = (CustomAttribute)objs.get(0);
                   			if (entry.equals(obj.getOwner().getEntity())) {
                   				if (!(obj instanceof CustomAttributeListElement)) { //won't be read using foreignKey to do lookup
                   					tMap.put(obj.getName(), obj);
                   				}
                   				objs.remove(0);  //do now to get list items out
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
	
	
	public Tag loadTag(final String tagId, Long zoneId) {
        Tag t =(Tag)getHibernateTemplate().get(Tag.class, tagId);
        if (t != null && t.getZoneId().equals(zoneId)) return t;
        throw new NoObjectByTheIdException("errorcode.no.tag.by.the.id", tagId);
	}
	//The entries must be of the same type
	//Used by indexing bulk load

	public Map<EntityIdentifier, List<Tag>> loadAllTagsByEntity(final Collection<EntityIdentifier> entityIds) {
		if (entityIds.isEmpty()) return new HashMap();
		if (entityIds.size() > inClauseLimit) throw new IllegalArgumentException("Collection to large");
		List<Tag> tags = (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                	List<Long> ids = new ArrayList();
	                	EntityIdentifier savedId=null;
	                	for (EntityIdentifier id:entityIds) {
		                	ids.add(id.getEntityId());
		                	savedId = id;
	                	}
	                	
	                	return session.createCriteria(Tag.class)
                 		.add(Expression.in("entityIdentifier.entityId", ids))
       					.add(Expression.eq("entityIdentifier.type", savedId.getEntityType().getValue()))
                 		.addOrder(Order.asc("entityIdentifier.entityId"))
	                 	.list();
	                }
	            }
	        );
		Map<EntityIdentifier, List<Tag>> result = new HashMap();
		for (EntityIdentifier id :entityIds) {
			List<Tag> tList = new ArrayList();
			
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
	public List<Tag> loadAllTagsByEntity(final EntityIdentifier entityId) {
		return (List<Tag>)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
 	                 	.list();
	                }
	            }
	        );
		
	}

	public List<Tag> loadCommunityTagsByEntity(final EntityIdentifier entityId) {
		return (List<Tag>)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
       					.add(Expression.eq("public", true))
 	                 	.list();
	                }
	            }
	        );
		
	}
	public List<Tag> loadPersonalTagsByEntity(final EntityIdentifier entityId, final EntityIdentifier ownerId) {
		return (List<Tag>)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
       					.add(Expression.eq("public",false))
	                 	.list();
	                }
	            }
	        );
		
	}
	public List<Tag> loadPersonalTagsByOwner(final EntityIdentifier ownerId) {
		return (List<Tag>)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
       					.add(Expression.eq("public",false))
	                 	.list();
	                }
	            }
	        );
		
	}	
    //load public and personal private tags for an entity.
    public List<Tag> loadEntityTags(final EntityIdentifier entityIdentifier, final EntityIdentifier ownerIdentifier) {
	   	return (List<Tag>)getHibernateTemplate().execute(
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
	                 	.list();
	    	   		}
	    	   	}
	    	 );    	
   	
    }
	
	public List<Subscription> loadSubscriptionByEntity(final EntityIdentifier entityId) {
		return (List<Subscription>)getHibernateTemplate().execute(
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
	private List loadObjects(final ObjectControls objs, FilterControls filter, Long zoneId, final boolean cacheable) {
	   	final FilterControls myFilter = filter==null?new FilterControls():filter;
		if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
		return (List)getHibernateTemplate().execute(
	        new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException {
	            	StringBuffer query = objs.getSelectAndFrom("x");
	            	myFilter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = myFilter.getFilterValues();
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
			return (UserDashboard)getHibernateTemplate().execute(
				new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
	        			Criteria crit = session.createCriteria(UserDashboard.class)
	        				.add(Expression.eq("binderId", binderId))
	        				.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
	        				.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
	        				.setCacheable(true);
		        		try {
		        			List result = crit.list();
		        			if (result.isEmpty()) return null;
		        			return result.get(0);
		        		} catch (HibernateException se) {
		        			if (se.getCause() instanceof java.io.InvalidClassException) {
		        				crit.setProjection(Projections.property("id"));
		        				crit.setCacheable(false);
		        				List objs = crit.list();
		        				if (objs.isEmpty()) return Collections.EMPTY_LIST;
		        				String id = (String)objs.get(0);
		        				//bad serialized data - get rid of it
		        				executeDashboardClear(id);
		        				return session.get(UserDashboard.class, id);
		        			} else throw se;
		        		}
		            }
				}
			);
	}
	public EntityDashboard loadEntityDashboard(final EntityIdentifier ownerId) {
		return (EntityDashboard)getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException {
						Criteria crit = session.createCriteria(EntityDashboard.class)
							.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
							.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
							.setCacheable(true);
						try {
		            		List result = crit.list();
		            		if (result.isEmpty()) return null;
		            		return result.get(0);
		            		
		        		} catch (HibernateException se) {
		        			if (se.getCause() instanceof java.io.InvalidClassException) {
								//bad serialized data - get rid of it
								crit.setProjection(Projections.property("id"));
								crit.setCacheable(false);
								List objs = crit.list();
								if (objs.isEmpty()) return Collections.EMPTY_LIST;
								String id = (String)objs.get(0);
								//bad serialized data - get rid of it
								executeDashboardClear(id);
								return session.get(EntityDashboard.class, id);
							} else throw se;
						}
					}
				}
		);
	}

	public Dashboard loadDashboard(String id, Long zoneId) {
		Dashboard d=null;
		try {
			d = (Dashboard)getHibernateTemplate().get(Dashboard.class, id);
		} catch (HibernateException se) {
			if (se.getCause() instanceof java.io.InvalidClassException) {
				//bad serialized data - get rid of it
				executeDashboardClear(id);
				d = (Dashboard)getHibernateTemplate().get(Dashboard.class, id);
				
			} else throw se;
		}
		if (d != null && d.getZoneId().equals(zoneId)) return d;
		throw new NoObjectByTheIdException("errorcode.no.dashboard.by.the.id", id);
	}
	//At one point dom4j (1.5) objects where serialized,  don4j(1.6) does not recognize them
	//Somewhere along the way we changed it so we saved the string value instead of the serialized object, but 
	//occasionally they pop up at home.
	private void executeDashboardClear(final String id) {
       	SessionFactory sf = getSessionFactory();
    	Session session = sf.openSession();
		Statement statement = null;
    	try {
    		statement = session.connection().createStatement();
    		statement.executeUpdate("update SS_Dashboards set properties=null where id='" + id + "'");
    		Dashboard d = (Dashboard)session.get(Dashboard.class, id);
    		d.setProperties(new HashMap());
    		session.flush();
    	} catch (SQLException sq) {
    		throw new HibernateException(sq);
    	} finally {
    		try {if (statement != null) statement.close();} catch (Exception ex) {};
    		session.close();
    	}

	}
	//Don't use , only for special cases
	public void executeUpdate(final String queryStr) {
		executeUpdate(queryStr, null);
	}
	public void executeUpdate(final String queryStr, final Map values) {
    	getHibernateTemplate().execute(
        	   	new HibernateCallback() {
        	   		public Object doInHibernate(Session session) throws HibernateException {
    		   			Query query = session.createQuery(queryStr);
	                  	if (values != null) {
	                  		for (Iterator iter=values.entrySet().iterator(); iter.hasNext();) {
	                  			Map.Entry me = (Map.Entry)iter.next();
	                  			Object val = me.getValue();
	                  			if (val instanceof Collection) {
	                  				if (((Collection)val).size() > inClauseLimit) throw new IllegalArgumentException("Collection to large");
	                  				if (((Collection)val).size() == 0) throw new IllegalArgumentException("Collection to small");
	                  				query.setParameterList((String)me.getKey(), (Collection)val);
	                  			} else if (val instanceof Object[]) {
	                  				if (((Object[])val).length > inClauseLimit) throw new IllegalArgumentException("Collection to large");
	                  				if (((Object[])val).length == 0) throw new IllegalArgumentException("Collection to small");
	                  				query.setParameterList((String)me.getKey(), (Object[])val);
	                  			} else {
	                  				query.setParameter((String)me.getKey(), val);
	                  			}
	                  		}
	            		}
		   				query.executeUpdate();
          	   		
          	   			return null;

        	   		}
        	   	}
       	   	);
	}
	
	public int daysSinceInstallation()
	{
		final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;
		List dates = (List) getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(FolderEntry.class)
                 		.setProjection(Projections.projectionList()
								.add(Projections.min("creation.date"))
								.add(Projections.max("lastActivity")))
 	                 	.list();
	                }
	            }
	        );
		
		Object[] row = (Object[]) dates.get(0);
		Date earliest = (Date) row[0];
		Date latest = (Date) row[1];
		if(earliest != null && latest != null) {
			long millis = latest.getTime() - earliest.getTime();
			return (int) ((millis + MILLIS_PER_DAY + 1)/MILLIS_PER_DAY);
		}
		
		return 0;
	}
	public NotifyStatus loadNotifyStatus(Binder binder, DefinableEntity entity) {
		//currently only the id is used in the key cause folderEntries are the only users
		NotifyStatus status = (NotifyStatus)getHibernateTemplate().get(NotifyStatus.class, entity.getEntityIdentifier().getEntityId());
        if (status != null) return status;
        //create one and return
        status = new NotifyStatus(binder, entity);
        save(status);
        return status;       
	}
	/**
     * Load status for entries that have been updated.
     * The begin date is really a performance optimization, cause it limits the number of records that are even checked since the key is hopefully ordered by modifyDate.
     * End date is needed to exclude for updates that happen inbetween calls or get looping
     */
    public List<NotifyStatus> loadNotifyStatus(final String sinceField, final Date begin, final Date end, final int maxResults, final Long zoneId) {
       	List result = (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	Criteria crit = session.createCriteria(NotifyStatus.class)
                    	.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
                    	.add(Expression.ge("lastModified", begin))
                    	.add(Expression.lt("lastModified", end))  
                    	.add(Expression.geProperty("lastModified", sinceField))
                    	.addOrder(Order.asc("owningBinderKey"));
                    	
                    	if (maxResults > 0) {
                    		crit.setMaxResults(maxResults);
						}
                    	
                       return crit.list();
                    }
                }
            );  
       return result;   	
    }
	/**
     * Load status for entries that have been updated in a subtree
     * The begin date is really a performance optimization, cause it limits the number of records that are even checked since the key is hopefully ordered by modifyDate.
     * End date is needed to exclude for updates that happen inbetween calls or get looping
    */
    public List<NotifyStatus> loadNotifyStatus(final Binder binder, final String sinceField, final Date begin, final Date end, final int maxResults, final Long zoneId) {
       	List result = (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	Criteria crit = session.createCriteria(NotifyStatus.class)
                    	.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
                    	.add(Expression.ge("lastModified", begin))
                    	.add(Expression.lt("lastModified", end))
                    	.add(Expression.geProperty("lastModified", sinceField))
                    	.add(Expression.like("owningBinderKey", binder.getBinderKey().getSortKey()))
                    	.addOrder(Order.asc("owningBinderKey"));
                    	
                    	if (maxResults > 0) {
                    		crit.setMaxResults(maxResults);
						}
                    	
                       return crit.list();
                    }
                }
            );  
       return result;   	
    }

	public SimpleName loadSimpleName(String name, String type, Long zoneId) {
		return (SimpleName) getHibernateTemplate().get(SimpleName.class, new SimpleName(zoneId, name, type));
	}

	public List<SimpleName> loadSimpleNames(final String type, final Long binderId, final Long zoneId) {
        return (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List<SimpleName> results = session.createCriteria(SimpleName.class)
                        	.add(Expression.eq("zoneId", zoneId))
                        	.add(Expression.eq("type", type))
                        	.add(Expression.eq("binderId", binderId))
                        	.setCacheable(true)
                        	.addOrder(Order.asc("name"))
                        	.list();
                    	return results;
                    }
                }
            );

	}
 }
