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
package org.kablink.teaming.dao.impl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.SessionFactoryImplementor;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.LongIdComparator;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.KablinkDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.AnyOwner;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.CustomAttributeListElement;
import org.kablink.teaming.domain.Dashboard;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DefinitionInvalidOperation;
import org.kablink.teaming.domain.EntityDashboard;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LibraryEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoBinderByTheNameException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.NotifyStatus;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.SharedEntity;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.UserDashboard;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.Validator;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * @author Jong Kim
 *
 */
public class CoreDaoImpl extends KablinkDao implements CoreDao {
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
		long begin = System.currentTimeMillis();
		try {
			return getSession().isDirty();
    	}
    	finally {
    		end(begin, "isDirty()");
    	}	        
	}
	public void flush() {
		long begin = System.currentTimeMillis();
		try {
			getSession().flush();
    	}
    	finally {
    		end(begin, "flush()");
    	}	        
	}
	public void clear() {
		long begin = System.currentTimeMillis();
		try {
			getSession().clear();
    	}
    	finally {
    		end(begin, "clear()");
    	}	        
	}
	public void evict(Object obj) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "evict(Object)");
    	}	        
	}
	public void refresh(Object obj) {
		long begin = System.currentTimeMillis();
		try {
			getSession().refresh(obj);
    	}
    	finally {
    		end(begin, "refresh(Object)");
    	}	        
	}
	public void lock(Object obj) {
		long begin = System.currentTimeMillis();
		try {
			getSession().refresh(obj, LockMode.UPGRADE);
			//getSession().lock(obj, LockMode.WRITE);
    	}
    	finally {
    		end(begin, "lock(Object)");
    	}	        
	}
	public void save(Object obj) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "save(Object)");
    	}	        
    }

	//re-attach object, this does not force a sql update
	public void update(Object obj) {
		long begin = System.currentTimeMillis();
		try {
			getHibernateTemplate().update(obj);
    	}
    	finally {
    		end(begin, "update(Object)");
    	}	        
	}
	public Object merge(Object obj) {
		long begin = System.currentTimeMillis();
		try {
			return getHibernateTemplate().merge(obj);
    	}
    	finally {
    		end(begin, "merge(Object)");
    	}	        
	}
	public void replicate(final Object obj) {
		long begin = System.currentTimeMillis();
		try {
	      getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	 session.replicate(obj, ReplicationMode.EXCEPTION);
                    	 return null;
                    }
                }
            );
    	}
    	finally {
    		end(begin, "replicate(Object)");
    	}	        
	}
	public SFQuery queryObjects(final ObjectControls objs, FilterControls filter, final Long zoneId) { 
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "queryObjects(ObjectControls,FilterControls,Long)");
    	}	        
    }	
	/* 
	 * Becuse we have relationships within the same table, 
	 * not all databases handle on-delete correctly.  
	 * We are forced to do it ourselves
	 */
 	public void deleteEntityAssociations(final String whereClause) {
		long begin = System.currentTimeMillis();
		try {
		   	getHibernateTemplate().execute(
		    	   	new HibernateCallback() {
		    	   		public Object doInHibernate(Session session) throws HibernateException {
		    	   			//mysql won't delete these in 1 statement cause of foreign key constraints
		    	   		session.createQuery("DELETE org.kablink.teaming.domain.VersionAttachment where " + whereClause)
		    	   			.executeUpdate();
		    	   		session.createQuery("DELETE org.kablink.teaming.domain.Attachment where " + whereClause)
	    	   			.executeUpdate();
		       	   		session.createQuery("DELETE org.kablink.teaming.domain.CustomAttributeListElement where " + whereClause)
		  	   				.executeUpdate();
		       	   		session.createQuery("DELETE org.kablink.teaming.domain.CustomAttribute where " + whereClause)
	  	   				.executeUpdate();
		       	   		session.createQuery("DELETE org.kablink.teaming.domain.NotifyStatus where " + whereClause)
	  	   				.executeUpdate();
		       	   		
	/*
	 * db servers can deal with these cause on-delete cascade will work
	 * 	    	   		session.createQuery("DELETE org.kablink.teaming.domain.Event where " + whereClause)
	       	   			.executeUpdate();
		       	   			session.createQuery("DELETE org.kablink.teaming.domain.WorkflowState where " + whereClause)
		       	   				.executeUpdate();
		       	   			session.createQuery("DELETE org.kablink.teaming.domain.WorkflowResponse where " + whereClause)
	       	   				.executeUpdate();
	*/
		       	   		return null;
		       	   		}
		       	   	}
		    	 );    	
    	}
    	finally {
    		end(begin, "deleteEntityAssociations(String)");
    	}	        	    	
		
	}	
	public void delete(Object obj) {
		long begin = System.currentTimeMillis();
		try {
			getHibernateTemplate().delete(obj);
    	}
    	finally {
    		end(begin, "delete(Object)");
    	}	        
    }
	/**
	 * Delete the binder object and its assocations.
	 * Child binders should already have been deleted
	 * This is an optimized delete.  Deletes associations directly without waiting for hibernate
	 * to query.  Also deleted entries associated by parentBinderId
	 */	
	public void delete(final Binder binder) {
		long begin = System.currentTimeMillis();
		try {
			delete(binder, null);
    	}
    	finally {
    		end(begin, "delete(Binder)");
    	}	        
	}
	public void delete(final Binder binder, final Class entryClass) {
		long begin = System.currentTimeMillis();
		try {
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
			   				String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
			   				if (Validator.isNotNull(schema)) schema = schema+".";
			   				else schema = "";
			   				s.executeUpdate("delete from " + schema + "SS_DefinitionMap where binder=" + binder.getId());
			   				s.executeUpdate("delete from " + schema + "SS_WorkflowMap where binder=" + binder.getId());
			   				s.executeUpdate("delete from " + schema + "SS_Notifications where binderId=" + binder.getId());
			   			} catch (SQLException sq) {
			   				throw new HibernateException(sq);
	     	   			} finally {
	       	   				try {if (s != null) s.close();} catch (Exception ex) {};
	       	   			}
	    	   		//delete user dashboards on this binder
		     	   		session.createQuery("Delete org.kablink.teaming.domain.Dashboard where binderId=:binderId")
			   				.setLong("binderId", binder.getId())
		     	   			.executeUpdate();
			   			session.createQuery("DELETE org.kablink.teaming.domain.UserProperties where binderId=:binderId")
			   				.setLong("binderId", binder.getId())
			   				.executeUpdate();
			   			//delete reserved names for entries/subfolders
			   			session.createQuery("DELETE org.kablink.teaming.domain.LibraryEntry where binderId=:binderId")
			   				.setLong("binderId", binder.getId())
			   				.executeUpdate();
			   			//delete associations with binder and its entries - common key
	    	   			deleteEntityAssociations("owningBinderId=" + binder.getId());
	
			   		    // Delete associations not maintained with foreign-keys = this just handles the binder itself
		       	   		//delete history
	    	   			session.createQuery("DELETE org.kablink.teaming.domain.WorkflowHistory where owningBinderId=:binderId")
	 		   				.setLong("binderId", binder.getId())
	 		   				.executeUpdate();
		     	   		//delete dashboard
		     	   		session.createQuery("Delete org.kablink.teaming.domain.Dashboard where owner_id=:entityId and owner_type=:entityType")
	  		     	   			.setLong("entityId", binder.getId())
	  		     	   			.setParameter("entityType", binder.getEntityType().getValue())
	   		     	   			.executeUpdate();
			   			//delete ratings/visits for this binder
			   			session.createQuery("Delete org.kablink.teaming.domain.Rating where entityId=:entityId and entityType=:entityType")
			   				.setLong("entityId", binder.getId())
			   			   	.setParameter("entityType", binder.getEntityType().getValue())
			   			   	.executeUpdate();
			   			//delete subscriptions to this binder
			   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where entityId=:entityId and entityType=:entityType")
			   				.setLong("entityId", binder.getId())
			   				.setParameter("entityType", binder.getEntityType().getValue())
			   				.executeUpdate();
			   			//delete shares of this folder
			   			session.createQuery("Delete org.kablink.teaming.domain.SharedEntity where entityId=:entityId and entityType=:entityType")
		   					.setLong("entityId", binder.getId())
		   					.setParameter("entityType", binder.getEntityType().name())
		   					.executeUpdate();
			   			//delete shared for this team
			   			session.createQuery("Delete org.kablink.teaming.domain.SharedEntity where accessId=:accessId and accessType=:accessType")
		   				.setLong("accessId", binder.getId())
		   				.setParameter("accessType", SharedEntity.ACCESS_TYPE_TEAM)
		   				.executeUpdate();
			   			//delete tags on this binder
			   			session.createQuery("Delete org.kablink.teaming.domain.Tag where entity_id=:entityId and entity_type=:entityType")
			   				.setLong("entityId", binder.getId())
			   			   	.setParameter("entityType", binder.getEntityType().getValue())
			   			   	.executeUpdate();
			   			//delete simple names for this binder
			   			session.createQuery("DELETE org.kablink.teaming.domain.SimpleName where binderId=:binderId")
			   				.setLong("binderId", binder.getId())
			   				.executeUpdate();
	
			   			if (entryClass != null) {
			   				
				   			//delete customAttributeListElement definitions on this binder
		    	   			session.createQuery("DELETE org.kablink.teaming.domain.CustomAttributeListElement where owningBinderId=:ownerBinderId and ownerType=:entityType")
				   				.setLong("ownerBinderId", binder.getId())
				   				.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.name())
				   				.executeUpdate();
			   				
		    	   		    //delete customAttributeListElement definitions on this binder
		    	   			session.createQuery("DELETE org.kablink.teaming.domain.CustomAttribute where owningBinderId=:ownerBinderId and ownerType=:entityType")
				   				.setLong("ownerBinderId", binder.getId())
				   				.setParameter("entityType", EntityIdentifier.EntityType.folderEntry.name())
				   				.executeUpdate();
			   				
			   				//finally delete the entries
			   				session.createQuery("Delete " + entryClass.getName() + " where parentBinder=:parent")
			       	   				.setEntity("parent", binder)
			       	   				.executeUpdate();		 		   				
			   			}

			   			//delete customAttributeListElement definitions on this binder
	    	   			session.createQuery("DELETE org.kablink.teaming.domain.CustomAttributeListElement where ownerId=:ownerId and ownerType=:entityType")
			   				.setLong("ownerId", binder.getId())
			   				.setParameter("entityType", EntityIdentifier.EntityType.folder.name())
			   				.executeUpdate();
		   				
	    	   		    //delete customAttributeListElement definitions on this binder
	    	   			session.createQuery("DELETE org.kablink.teaming.domain.CustomAttribute where ownerId=:ownerId and ownerType=:entityType")
			   				.setLong("ownerId", binder.getId())
			   				.setParameter("entityType", EntityIdentifier.EntityType.folder.name())
			   				.executeUpdate();

			   			//delete mashup definitions on this binder
	    	   			session.createQuery("DELETE org.kablink.teaming.domain.CustomAttribute where binder=:binder")
			   				.setLong("binder", binder.getId())
			   				.executeUpdate();
			   			
			   			//do ourselves or hibernate will flsuh
			   			session.createQuery("Delete  org.kablink.teaming.domain.Binder where id=:id")
			   		    	.setLong("id", binder.getId().longValue())
			   		    	.executeUpdate();
			   			
			   			if (!binder.isRoot()) {
			   				session.getSessionFactory().evictCollection("org.kablink.teaming.domain.Binder.binders", binder.getParentBinder().getId());
			   			}
			   			//find definitions owned by this binder
			   			List<Definition> defs = session.createCriteria(Definition.class)
			   				.add(Expression.eq("binderId", binder.getId())).list();
			   			for (Definition definition:defs) {
			   				try {
			   					delete(definition);
			   				} catch (Exception ex) {
			   					//assume in use
			   					definition.setVisibility(Definition.VISIBILITY_DEPRECATED);
			   					definition.setBinderId(null);
			   					definition.setName(definition.getId());//need a unique name
			   				}
			   			}
			   			session.evict(binder);
			   			
			   			return null;
	    	   		}
	    	   	}
	    	 );    	
    	}
    	finally {
    		end(begin, "delete(Binder,Class)");
    	}	         
	    			
	}
	
	public void move(final Binder binder) {
		long begin = System.currentTimeMillis();
		try {
			//this should handle entries also
			getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		public Object doInHibernate(Session session) throws HibernateException {
	    	   			session.createQuery("update org.kablink.teaming.domain.NotifyStatus set owningBinderKey=:sortKey where owningBinderId=:id")
	    	   				.setString("sortKey", binder.getBinderKey().getSortKey())
	    	   				.setLong("id", binder.getId().longValue())
	    	   				.executeUpdate();
		    			//update binder key for binders attributes only
		    	   		session.createQuery("update org.kablink.teaming.domain.Attachment set owningBinderKey=:sortKey where owningBinderId=:id")
		    	   			.setString("sortKey", binder.getBinderKey().getSortKey())
		    	   			.setLong("id", binder.getId().longValue())
		    	   			.executeUpdate();
		    	   		session.createQuery("update org.kablink.teaming.domain.Event set owningBinderKey=:sortKey where owningBinderId=:id")
		    	   			.setString("sortKey", binder.getBinderKey().getSortKey())
		    	   			.setLong("id", binder.getId().longValue())
		    	   			.executeUpdate();
		    	   		session.createQuery("update org.kablink.teaming.domain.CustomAttribute set owningBinderKey=:sortKey where owningBinderId=:id")
	    	   				.setString("sortKey", binder.getBinderKey().getSortKey())
	    	   				.setLong("id", binder.getId().longValue())
	    	   				.executeUpdate();
		    			//update binder key for binder and its contents
		    			session.createQuery("update org.kablink.teaming.domain.ChangeLog set owningBinderKey=:sortKey where owningBinderId=:id")
		    				.setString("sortKey", binder.getBinderKey().getSortKey())
	    	   				.setLong("id", binder.getId().longValue())
	    	   				.executeUpdate();
		    			session.createQuery("update org.kablink.teaming.domain.AuditTrail set owningBinderKey=:sortKey where owningBinderId=:id")
	    				.setString("sortKey", binder.getBinderKey().getSortKey())
		   				.setLong("id", binder.getId().longValue())
		   				.executeUpdate();
		    			session.createQuery("update org.kablink.teaming.domain.WorkflowHistory set owningBinderKey=:sortKey where owningBinderId=:id")
	    					.setString("sortKey", binder.getBinderKey().getSortKey())
	    					.setLong("id", binder.getId().longValue())
	    					.executeUpdate();
		       			//move things that are only on the entry and have binder association
	      	   			session.createQuery("update org.kablink.teaming.domain.WorkflowState set owningBinderKey=:sortKey where owningBinderId=:id")
		    	   			.setString("sortKey", binder.getBinderKey().getSortKey())
		    	   			.setLong("id", binder.getId().longValue())
		       	   			.executeUpdate();
	       	   			session.createQuery("update org.kablink.teaming.domain.WorkflowResponse set owningBinderKey=:sortKey where owningBinderId=:id")
	       	   				.setString("sortKey", binder.getBinderKey().getSortKey())
	       	   				.setLong("id", binder.getId().longValue())
	       	   				.executeUpdate();
	    		   			return null;
	    	   		}
	    	   	}
	    	 );    	
    	}
    	finally {
    		end(begin, "move(Binder)");
    	}	        
	}


    public Object load(Class clazz, String id) {
		long begin = System.currentTimeMillis();
		try {
			return getHibernateTemplate().get(clazz, id);
    	}
    	finally {
    		end(begin, "load(Class,String)");
    	}	        
    }
    public Object load(Class clazz, Long id) {
		long begin = System.currentTimeMillis();
		try {
			return getHibernateTemplate().get(clazz, id);         
    	}
    	finally {
    		end(begin, "load(Class,Long)");
    	}	        
    }
	/**
	 * Return a list containing an object array, where each object in a row representing the value of the requested attribute
	 * This is used to return a subset of object attributes
	 */
	public List loadObjects(ObjectControls objs, FilterControls filter, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			return loadObjects(objs, filter, zoneId, false);
    	}
    	finally {
    		end(begin, "loadObjects(ObjectControls,FilterControls,Long)");
    	}	        
	}
	public List loadObjectsCacheable(ObjectControls objs, FilterControls filter, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			return loadObjects(objs, filter, zoneId, true);
    	}
    	finally {
    		end(begin, "loadObjectsCacheable(ObjectControls,FilterControls,Long)");
    	}	        
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
		long begin = System.currentTimeMillis();
		try {
			return loadObjects(query, values, null);
    	}
    	finally {
    		end(begin, "loadObjects(String,Map)");
    	}	        
	}
	
	public List loadObjects(final String query, final Map values, final Integer maxResults) {
		long begin = System.currentTimeMillis();
		try {
			return (List)getHibernateTemplate().execute(
			        new HibernateCallback() {
			            public Object doInHibernate(Session session) throws HibernateException {
		                  	Query q = session.createQuery(query);
		                  	if(maxResults != null)
		                  		q.setMaxResults(maxResults.intValue());
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
    	finally {
    		end(begin, "loadObjects(String,Map,Integer)");
    	}	        
	}
	/**
	 * Return a list ob objects
	 */
	public List loadObjects(Class className, FilterControls filter, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			return loadObjects(new ObjectControls(className), filter, zoneId);
    	}
    	finally {
    		end(begin, "loadObjecs(Class,FilterControls,Long)");
    	}	        
	}
	public List loadObjectsCacheable(Class className, FilterControls filter, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			return loadObjectsCacheable(new ObjectControls(className), filter, zoneId);
    	}
    	finally {
    		end(begin, "loadObjectsCacheable(Class,FilterControls,Long)");
    	}	        
	}
	/**
	 * Load a list of objects, OR'ing ids
	 * @param ids
	 * @param className
	 * @return
	 */
   public List loadObjects(final Collection ids, final Class className, final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadObjects(Collection,Class,Long)");
    	}	        
        
    }	
   public List loadObjects(final Collection ids, final Class className, final Long zoneId, final List collections) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadObjects(Collection,Class,Long,List)");
    	}	        
       
   }	
   public long countObjects(final Class clazz, FilterControls filter, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "countObjects(Class,FilterControls,Long)");
    	}	        
	}
	
	public double averageColumn(final Class clazz, final String column, FilterControls filter, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "averageColumn(Class,String,FilterControls,Long)");
    	}	        
	}
	public long sumColumn(final Class clazz, final String column, FilterControls filter, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "sumColumn(Class,String,FilterControls,Long)");
    	}	        
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
		long begin = System.currentTimeMillis();
		try {
	    	//Folderentries or binders only
	        if (Validator.isNull(name)) throw new TitleException("");
	  		LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.FILE, name);
			if (!(entity instanceof Binder)) le.setEntityId(entity.getId());
	  		registerLibraryEntry(le);
    	}
    	finally {
    		end(begin, "registerFileName(Binder,DefinableEntity,String)");
    	}	        
   }
    // This code is used to reserve a name.  The binder field is used to check
	// uniqueness within a binder so it can be viewed as a wiki.  The name is the normalized title
	//  belonging to an entry in the binder, or the title of a sub-folder.  
	// Create our own session cause failures clear the existing session and don't want to 
	// necessarily cancel the running transaction.
	// It assumes the combination of binderId and entityId is enough to identify an entry
    public void registerTitle(Binder binder, DefinableEntity entity) throws TitleException {
		long begin = System.currentTimeMillis();
		try {
	    	//Folderentries or binders only
	    	String name = entity.getNormalTitle();
	   		LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.TITLE, name);
			if (!(entity instanceof Binder)) le.setEntityId(entity.getId());
	   		registerLibraryEntry(le);
    	}
    	finally {
    		end(begin, "registerTitle(Binder,DefinableEntity)");
    	}	        
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
		long begin = System.currentTimeMillis();
    	try {
    		unRegisterLibraryEntry(new LibraryEntry(binder.getId(), LibraryEntry.FILE, name));
    	} catch (Exception ex) {
				logger.error("Error removeing library entry for: " + binder + " file" +  ex.getLocalizedMessage());
	   	}    	
		finally {
			end(begin, "unRegisterFileName(Binder,String)");
		}	        
    }
    //normalized titles in parentbinder
    public void unRegisterTitle(Binder binder, String name) {
		long begin = System.currentTimeMillis();
    	try {
    		unRegisterLibraryEntry(new LibraryEntry(binder.getId(), LibraryEntry.TITLE, name));
    	} catch (Exception ex) {
			logger.error("Error removeing library entry for: " + binder + " title" +  ex.getLocalizedMessage());
	   	}   	
		finally {
			end(begin, "unRegisterTitle(Binder,String)");
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
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "updateFileName(Binder,DefinableEntity,String,String)");
    	}	        
     }
    //done in the current transaction on a binder title rename or remove attachment
    public void updateTitle(Binder binder, DefinableEntity entity, String oldName, String newName) throws TitleException {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "updateTitle(Binder,DefinableEntity,String,String)");
    	}	        
    }
    public Long getEntityIdForMatchingTitle(Long binderId, String title) {
		long begin = System.currentTimeMillis();
		try {
	    	LibraryEntry le = new LibraryEntry(binderId, LibraryEntry.TITLE, title);
			LibraryEntry exist = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, le);
			if(exist != null)
				return exist.getEntityId();
			else
				return null;
    	}
    	finally {
    		end(begin, "getEntityIdForMatchingTitle(Long,String)");
    	}	        
    }
    public LibraryEntry getRegisteredTitle(Long binderId, String title) {
		long begin = System.currentTimeMillis();
		try {
	    	LibraryEntry le = new LibraryEntry(binderId, LibraryEntry.TITLE, title);
			LibraryEntry exist = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, le);
			return exist;
    	}
    	finally {
    		end(begin, "getRegisteredTitle(Long,String)");
    	}	        
    }
    public LibraryEntry getRegisteredFileName(Long binderId, String fileName) {
		long begin = System.currentTimeMillis();
		try {
	    	LibraryEntry le = new LibraryEntry(binderId, LibraryEntry.FILE, fileName);
			LibraryEntry exist = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, le);
			return exist;
    	}
    	finally {
    		end(begin, "getRegisteredFileName(Long,String)");
    	}	        
    }
    public boolean isTitleRegistered(Long binderId, String title) {
		long begin = System.currentTimeMillis();
		try {
			return(null != getRegisteredTitle(binderId, title));
    	}
    	finally {
    		end(begin, "isTitleRegistered(Long,String)");
    	}	        
    }
    public boolean isFileNameRegistered(Long binderId, String fileName) {
		long begin = System.currentTimeMillis();
		try {
			return(null != getRegisteredFileName(binderId, fileName));
    	}
    	finally {
    		end(begin, "isFileNameRegistered(Long,String)");
    	}	        
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
    public void addExistingName(LibraryEntry le, DefinableEntity entity) {
		long begin = System.currentTimeMillis();
		try {
			LibraryEntry exist = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, le);
			if (exist != null) {
				delete(exist);
				flush();
			}
	    	addNewName(le, entity);
    	}
    	finally {
    		end(begin, "addExistingName(LibraryEntry,DefinableEntity)");
    	}	        
    }
    public  Long findFileNameEntryId(Binder binder, String name) {
		long begin = System.currentTimeMillis();
		try {
	    	LibraryEntry le = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, new LibraryEntry(binder.getId(),LibraryEntry.FILE, name));
	    	if (le == null) throw new NoObjectByTheIdException("errorcode.no.library.entry.by.the.id", new Object[]{binder.getId(), name});
	    	return le.getEntityId();
    	}
    	finally {
    		end(begin, "findFileNameEntryId(Binder,String)");
    	}	        

    }
	//Clears only folderentries. sub-folder remain since they must always be unique for webdav to traverse the tree
    public void clearFileNames(Binder binder) {
		long begin = System.currentTimeMillis();
		try {
	    	executeUpdate("delete from org.kablink.teaming.domain.LibraryEntry where binderId=" +
	    			binder.getId() + " and type=" + LibraryEntry.FILE.toString() + " and not entityId is null", null);
    	}
    	finally {
    		end(begin, "clearFileNames(Binder)");
    	}	        
    	
    }
    //Clear all titles, don't need if uniqueTitles not enabled.
    public void clearTitles(Binder binder) {
		long begin = System.currentTimeMillis();
		try {
	    	executeUpdate("delete from org.kablink.teaming.domain.LibraryEntry where binderId=" +
	    			binder.getId() + " and type=" + LibraryEntry.TITLE.toString(), null);
    	}
    	finally {
    		end(begin, "clearTitles(Binder)");
    	}	        
    	
    }
    public List<Workspace> findCompanies() {
		long begin = System.currentTimeMillis();
		try {
			return (List)getHibernateTemplate().execute(
			    new HibernateCallback() {
			        public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Workspace.class)
	             				.add(Expression.eq("internalId", ObjectKeys.TOP_WORKSPACE_INTERNALID))
	             				.setCacheable(true)
	             				.list();
	               }
	            }
			);
    	}
    	finally {
    		end(begin, "findCompanies()");
    	}	        
	}
	public Workspace findTopWorkspace(final String zoneName) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "findTopWorkspace(String)");
    	}	        

	}
	
	/**
	 * Load binder and validate it belongs to the zone
	 * @param binderId
	 * @param zoneId
	 * @return
	 */
    public Binder loadBinder(Long binderId, Long zoneId) {  
		long begin = System.currentTimeMillis();
		try {
			Binder binder = (Binder)load(Binder.class, binderId);
	        if (binder == null) {throw new NoBinderByTheIdException(binderId);};
	        if (!binder.getZoneId().equals(zoneId)) {
	        	throw new NoBinderByTheIdException(binderId);
	        }
	        return binder;
    	}
    	finally {
    		end(begin, "loadBinder(Long,Long)");
    	}	        
    }

    public Binder loadReservedBinder(final String reservedId, final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadReservedBinder(String,Long)");
    	}	        
    }
 
    public Definition loadReservedDefinition(final String reservedId, final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadReservedDefinition(String,Long)");
    	}	        
   	
    }
	public Definition loadDefinition(String defId, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
	 		Definition def = (Definition)load(Definition.class, defId);
	        if (def == null) {throw new NoDefinitionByTheIdException(defId);}
	        //make sure from correct zone
	        if (zoneId != null && !def.getZoneId().equals(zoneId)) {throw new NoDefinitionByTheIdException(defId);}
	  		return def;
    	}
    	finally {
    		end(begin, "loadDefinition(String,Long)");
    	}	        
	}

	public Definition loadDefinitionByName(final Binder binder, final String name, final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			return (Definition)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                public Object doInHibernate(Session session) throws HibernateException {
		                 	Criteria crit =session.createCriteria(Definition.class)
	                 		.add(Expression.eq("zoneId", zoneId))
	                 		.add(Expression.eq("name", name));
	                 		if (binder != null) crit.add(Expression.eq("binderId", binder.getId()));
	                 		else crit.add(Expression.eq("binderId", ObjectKeys.RESERVED_BINDER_ID));
	                		Definition def = (Definition)crit.uniqueResult();
		                    if (def == null) {throw new NoDefinitionByTheIdException(name);}
		                    return def;
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadDefinitionByName(Binder,String,Long)");
    	}	        
 	}
	public List loadDefinitions(FilterControls filter, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			List list = loadObjects(new ObjectControls(Definition.class), filter, zoneId);
			filterDefinitions(list);
			return list;
    	}
    	finally {
    		end(begin, "loadDefinitions(FilterControls, Long)");
    	}	        
	}
	public List loadDefinitions(Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			OrderBy order = new OrderBy();
			order.addColumn("type");
			order.addColumn("name");
			FilterControls filter = new FilterControls();
			filter.setOrderBy(order);
			List list = loadObjects(new ObjectControls(Definition.class), filter, zoneId);
			filterDefinitions(list);
			return list;
    	}
    	finally {
    		end(begin, "loadDefinitions(Long)");
    	}	        
	}
	
	private void filterDefinitions(List<Definition> definitions) {
		if(!ReleaseInfo.isLicenseRequiredEdition()) {
			for(int i = 0; i < definitions.size();) {
				Definition def = (Definition) definitions.get(i);
				if("_mirroredFileEntry".equals(def.getName()) || "_mirroredFileFolder".equals(def.getName()))
					definitions.remove(i);
				else
					i++;
			}
		}
	}
	
	// return top level configurations
	public List loadTemplates(final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			return (List)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                public Object doInHibernate(Session session) throws HibernateException {
		                 	Criteria criteria = session.createCriteria(TemplateBinder.class)
	                 		.add(Expression.isNull("parentBinder"))
	                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                 		.addOrder(Order.asc("definitionType"))
	                 		.addOrder(Order.asc("templateTitle"));
		                 	criteria = filterCriteriaForTemplates(criteria);
		                 	return criteria.list();
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadTemplates(Long)");
    	}	        
	}
	public List loadTemplates(final Long zoneId, final int type) {
		long begin = System.currentTimeMillis();
		try {
			return (List)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                public Object doInHibernate(Session session) throws HibernateException {
		                	Criteria criteria = session.createCriteria(TemplateBinder.class)
	                 		.add(Expression.isNull(ObjectKeys.FIELD_ENTITY_PARENTBINDER))
	                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                 		.add(Expression.eq("definitionType", type))
	                 		.addOrder(Order.asc(ObjectKeys.FIELD_TEMPLATE_TITLE));
		                 	criteria = filterCriteriaForTemplates(criteria);
		                 	return criteria.list();
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadTemplates(Long,int)");
    	}	        
	}
	
	private Criteria filterCriteriaForTemplates(Criteria criteria) {
		if(!ReleaseInfo.isLicenseRequiredEdition()) {
			criteria.add(Expression.ne("name", "_folder_mirrored_file"));
		}
		return criteria;
	}
	
	public TemplateBinder loadTemplate(Long templateId, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			TemplateBinder template = (TemplateBinder)load(TemplateBinder.class, templateId);
	        if (template == null) {throw new NoBinderByTheIdException(templateId);}
	        //make sure from correct zone
	        if (zoneId != null && !template.getZoneId().equals(zoneId)) {throw new NoBinderByTheIdException(templateId);}
	  		return template;
    	}
    	finally {
    		end(begin, "loadTemplate(Long,Long)");
    	}	        
	}

	public TemplateBinder loadTemplateByName(final String name, final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadTemplateByName(String,Long)");
    	}	        
 	}
	//associations not maintained from definition to binders, only from
	//binders to definitions
	public void delete(final Definition def) {
		long begin = System.currentTimeMillis();
		try {
			getHibernateTemplate().execute(
		        new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
		            	//see if in use
		            	List results;
		               	if (def.getType() != Definition.WORKFLOW) {
		               		long count = countObjects(org.kablink.teaming.domain.FolderEntry.class, new FilterControls("entryDef", def), def.getZoneId());
		               		if (count > 0) throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));
		               		count = countObjects(org.kablink.teaming.domain.Principal.class, new FilterControls("entryDef", def), def.getZoneId());
		               		if (count > 0) throw new DefinitionInvalidOperation(NLT.get("definition.errror.inUse"));
		               		count = countObjects(org.kablink.teaming.domain.Binder.class, new FilterControls("entryDef", def), def.getZoneId());
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
		               		long count = countObjects(org.kablink.teaming.domain.WorkflowState.class, new FilterControls("definition", def), def.getZoneId());
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
    	finally {
    		end(begin, "delete(Definition)");
    	}	        
	}
	
	public boolean checkInUse(final Definition def){
		long begin = System.currentTimeMillis();
		try {
			Boolean inUse = (Boolean) getHibernateTemplate().execute(
			        new HibernateCallback() {
			            public Object doInHibernate(Session session) throws HibernateException {
			            	//see if in use
			               	if (def.getType() != Definition.WORKFLOW) {
			               		long count = countObjects(org.kablink.teaming.domain.FolderEntry.class, new FilterControls("entryDef", def), def.getZoneId());
			               		if (count > 0) return new Boolean(true);
			               		count = countObjects(org.kablink.teaming.domain.Principal.class, new FilterControls("entryDef", def), def.getZoneId());
			               		if (count > 0) return new Boolean(true);;
			               		count = countObjects(org.kablink.teaming.domain.Binder.class, new FilterControls("entryDef", def), def.getZoneId());
			               		if (count > 0) return new Boolean(true);;
			               	} else {
			               		long count = countObjects(org.kablink.teaming.domain.WorkflowState.class, new FilterControls("definition", def), def.getZoneId());
			               		if (count > 0) return new Boolean(true);
			               	}
			               	return new Boolean(false);
			            }
			        }
			    );
			return inUse.booleanValue();
    	}
    	finally {
    		end(begin, "checkInUse(Definition)");
    	}	        
	}
	
	/**
	 * Perform a write of a new object now using a new Session so we can commit it fast
	 * @param obj
	 */
	public Object saveNewSession(Object obj) {
		long begin = System.currentTimeMillis();
		try {
			obj = saveNewSessionWithoutUpdate(obj);
	    	//attach to current session. This will fail if read only
	    	// by that should mean no-one will update it so that is okay
	    	try {
	    		update(obj);
	    	} catch (InvalidDataAccessApiUsageException da) {};
	    	
	    	return obj;
    	}
    	finally {
    		end(begin, "saveNewSession(Object)");
    	}	        
		
	}
    public Object saveNewSessionWithoutUpdate(Object obj) {
		long begin = System.currentTimeMillis();
		try {
	      	SessionFactory sf = getSessionFactory();
	    	Session s = sf.openSession();
	    	try {
	    		s.save(obj);
	    		s.flush();
	    	} finally {
	    		s.close();
	    	}
	    	return obj;
    	}
    	finally {
    		end(begin, "saveNewSessionWithoutUpdate(Object)");
    	}	        
    }

	public List loadPostings(Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			return loadObjects(new ObjectControls(PostingDef.class), null, zoneId);
    	}
    	finally {
    		end(begin, "loadPostings(Long)");
    	}	        
	}
	public PostingDef loadPosting(String postingId, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			PostingDef post = (PostingDef)load(PostingDef.class, postingId);
	        if (post == null) {throw new NoObjectByTheIdException("errorcode.no.posting.by.the.id", postingId);}
	        //make sure from correct zone
	        if (!post.getZoneId().equals(zoneId)) {throw new NoObjectByTheIdException("errorcode.no.posting.by.the.id", postingId);}
	  		return post;
    	}
    	finally {
    		end(begin, "loadPosting(String,Long)");
    	}	        		
	}
	public PostingDef findPosting(final String emailAddress, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "findPosting(String,Long)");
    	}	        
	}

	//build collections manually as an optimization for indexing
	//evict from session cache, so no longer available to everyone else
	//The entries must be of the same type
	public void bulkLoadCollections(Collection entries) {
		long begin = System.currentTimeMillis();
		try {
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
	                 	objs = session.createQuery("from org.kablink.teaming.domain.CustomAttribute att left join fetch att.values where att." + key + "  in (:pList) order by att." + key)
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
    	finally {
    		end(begin, "bulkLoadCollections(Collection)");
    	}	        
	}
	
	public List<Long> findZoneEntityIds(Long entityId, String zoneUUID, String entityType) {
		long begin = System.currentTimeMillis();
		try {
	    	if (Validator.isNull(zoneUUID)) return new ArrayList<Long>();
	    	//Load customAttributes
	     	//Cannot criteria query, cause different order-by is specified in mapping files and it appears to take precedence
	       	final String id = zoneUUID + "." + entityId;
	       	final String type = entityType;
	       	final String key = org.kablink.util.search.Constants.ZONE_UUID_FIELD;
	       	return (List<Long>)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                	List<Long> result = new ArrayList<Long>();
	                	List readObjs = new ArrayList();
	                	List objs = null;
	                	if (type.equals(EntityType.folderEntry.name())) {
	    					objs = session.createQuery("SELECT owner From org.kablink.teaming.domain.CustomAttribute WHERE name='" + key + "' AND stringValue='" + id + "' AND ownerType='folderEntry'")
				   			.list();
	                	} else {
	    					objs = session.createQuery("SELECT owner From org.kablink.teaming.domain.CustomAttribute WHERE name='" + key + "' AND stringValue='" + id + "' AND (ownerType='folder' OR ownerType='workspace')")
				   			.list();
	                	}
				       	readObjs.add(objs);
				      	HashMap tMap;
				       	for (int i=0; i < objs.size(); ++i) {
				       		AnyOwner owner = (AnyOwner) objs.get(i);
				       		if (type.equals(owner.getEntity().getEntityType().name())) {
				       			result.add(owner.getEntity().getId());
				       		}
				       	}
				       	return result;
	                }
	            }
			);  
    	}
    	finally {
    		end(begin, "findZoneEntityIds(Long,String,String)");
    	}	        
	}	
	
	public Tag loadTag(final String tagId, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
	        Tag t =(Tag)getHibernateTemplate().get(Tag.class, tagId);
	        if (t != null && t.getZoneId().equals(zoneId)) return t;
	        throw new NoObjectByTheIdException("errorcode.no.tag.by.the.id", tagId);
    	}
    	finally {
    		end(begin, "loadTag(String,Long)");
    	}	        
	}
	//The entries must be of the same type
	//Used by indexing bulk load

	public Map<EntityIdentifier, List<Tag>> loadAllTagsByEntity(final Collection<EntityIdentifier> entityIds) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadAllTagsByEntity(Collection<EntityIdentifier>)");
    	}	        
		
	}
	//Used by indexing
	public List<Tag> loadAllTagsByEntity(final EntityIdentifier entityId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadAllTagsByEntity(EntityIdentifier)");
    	}	        
		
	}

	public List<Tag> loadCommunityTagsByEntity(final EntityIdentifier entityId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadCommunityTagsByEntity(EntityIdentifier)");
    	}	        
		
	}
	public List<Tag> loadPersonalTagsByEntity(final EntityIdentifier entityId, final EntityIdentifier ownerId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadPersonalTagsByEntity(EntityIdentifier,EntityIdentifier)");
    	}	        
		
	}
	public List<Tag> loadPersonalTagsByOwner(final EntityIdentifier ownerId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadPersonalTagsByOwner(EntityIdentifier)");
    	}	        
		
	}	
    //load public and personal private tags for an entity.
    public List<Tag> loadEntityTags(final EntityIdentifier entityIdentifier, final EntityIdentifier ownerIdentifier) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadEntityTags(EntityIdentifier,EntityIdentifier)");
    	}	        
   	
    }
	
	public List<Subscription> loadSubscriptionByEntity(final EntityIdentifier entityId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadSubscriptionByEntity(EntityIdentifier)");
    	}	        
		
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
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadUserDashboard(EntityIdentifier,Long)");
    	}	        
	}
	public EntityDashboard loadEntityDashboard(final EntityIdentifier ownerId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadEntityDashboard(EntityIdentifier)");
    	}	        
	}

	public Dashboard loadDashboard(String id, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "loadDashboard(String,Long)");
    	}	        
	}
	//At one point dom4j (1.5) objects where serialized,  don4j(1.6) does not recognize them
	//Somewhere along the way we changed it so we saved the string value instead of the serialized object, but 
	//occasionally they pop up at home.
	private void executeDashboardClear(final String id) {
       	SessionFactory sf = getSessionFactory();
    	Session session = sf.openSession();
		Statement statement = null;
    	try {
    		String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
    		if (Validator.isNotNull(schema)) schema = schema+".";
    		else schema = "";
    		statement = session.connection().createStatement();
    		statement.executeUpdate("update " + schema + "SS_Dashboards set properties=null where id='" + id + "'");
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
		long begin = System.currentTimeMillis();
		try {
			executeUpdate(queryStr, null);
    	}
    	finally {
    		end(begin, "executeUpdate(String)");
    	}	        
	}
	public void executeUpdate(final String queryStr, final Map values) {
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "executeUpdate(String,Map)");
    	}	        
	}
	
	public int daysSinceInstallation()
	{
		long begin = System.currentTimeMillis();
		try {
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
    	finally {
    		end(begin, "daysSinceInstallation()");
    	}	        
	}
	public NotifyStatus loadNotifyStatus(Binder binder, DefinableEntity entity) {
		long begin = System.currentTimeMillis();
		try {
			//currently only the id is used in the key cause folderEntries are the only users
			NotifyStatus status = (NotifyStatus)getHibernateTemplate().get(NotifyStatus.class, entity.getEntityIdentifier().getEntityId());
	        if (status != null) return status;
	        //create one and return
	        status = new NotifyStatus(binder, entity);
	        save(status);
	        return status;       
    	}
    	finally {
    		end(begin, "loadNotifyStatus(Binder,DefinableEntity)");
    	}	        
	}
	/**
     * Load status for entries that have been updated.
     * The begin date is really a performance optimization, cause it limits the number of records that are even checked since the key is hopefully ordered by modifyDate.
     * End date is needed to exclude for updates that happen inbetween calls or get looping
     */
    public List<NotifyStatus> loadNotifyStatus(final String sinceField, final Date begin, final Date end, final int maxResults, final Long zoneId) {
		long beginMS = System.currentTimeMillis();
		try {
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
    	finally {
    		end(beginMS, "loadNotifyStatus(String,Date,Date,int,Long)");
    	}	        
    }
	/**
     * Load status for entries that have been updated in a subtree
     * The begin date is really a performance optimization, cause it limits the number of records that are even checked since the key is hopefully ordered by modifyDate.
     * End date is needed to exclude for updates that happen inbetween calls or get looping
    */
    public List<NotifyStatus> loadNotifyStatus(final Binder binder, final String sinceField, final Date begin, final Date end, final int maxResults, final Long zoneId) {
		long beginMS = System.currentTimeMillis();
		try {
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
    	finally {
    		end(beginMS, "loadNotifyStatus(Binder,String,Date,Date,int,Long)");
    	}	        
    }

	public SimpleName loadSimpleName(String name, Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			return (SimpleName) getHibernateTemplate().get(SimpleName.class, new SimpleName(zoneId, name));
    	}
    	finally {
    		end(begin, "loadSimpleName(String,Long)");
    	}	        
	}

	public SimpleName loadSimpleNameByEmailAddress(final String emailAddress, final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
	        return (SimpleName)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                        return session.createCriteria(SimpleName.class)
	                        	.add(Expression.eq("zoneId", zoneId))
	                        	.add(Expression.eq("emailAddress", emailAddress))
	                        	.setCacheable(true)
	                        	.uniqueResult();
	                    }
	                }
	            );
    	}
    	finally {
    		end(begin, "loadSimpleNameByEmailAddress(String,Long)");
    	}	        

	}
	
	public List<SimpleName> loadSimpleNames(final Long binderId, final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
	        return (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session) throws HibernateException {
	                        List<SimpleName> results = session.createCriteria(SimpleName.class)
	                        	.add(Expression.eq("zoneId", zoneId))
	                        	.add(Expression.eq("binderId", binderId))
	                        	.setCacheable(true)
	                        	.addOrder(Order.asc("name"))
	                        	.list();
	                    	return results;
	                    }
	                }
	            );
    	}
    	finally {
    		end(begin, "loadSimpleNames(Long,Long)");
    	}	        

	}

	public IndexNode findIndexNode(final String nodeName, final String indexName) {
		long begin = System.currentTimeMillis();
		try {
			return (IndexNode)getHibernateTemplate().execute(
			        new HibernateCallback() {
			            public Object doInHibernate(Session session) throws HibernateException {
		               		return session.createCriteria(IndexNode.class)
		               						.add(Expression.eq("name", new IndexNode.Name(nodeName, indexName)))
		               						.uniqueResult();
			            }
			        }
			     );
    	}
    	finally {
    		end(begin, "findIndexNode(String,String)");
    	}	        
	}
	
	public void purgeIndexNodeByIndexName(final String indexName) {
		long begin = System.currentTimeMillis();
		try {
		   	getHibernateTemplate().execute(
		    	   	new HibernateCallback() {
		    	   		public Object doInHibernate(Session session) throws HibernateException {
			     	   		session.createQuery("Delete org.kablink.teaming.domain.IndexNode where indexName=:indexName")
			   				.setString("indexName", indexName)
		     	   			.executeUpdate();
		       	   		return null;
		       	   		}
		       	   	}
		    	 );		
    	}
    	finally {
    		end(begin, "purgeIndexNodeByIndexName(String)");
    	}	        
	}
	public List<LdapConnectionConfig> loadLdapConnectionConfigs(final Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
		   return (List<LdapConnectionConfig>)getHibernateTemplate().execute(
		    	   	new HibernateCallback() {
		    	   		public Object doInHibernate(Session session) throws HibernateException {
		                       return session.createCriteria(LdapConnectionConfig.class)
		                       .add(Expression.eq("zoneId", zoneId))
		                       .list();
	 	       	   		}
		       	   	}
		    	 );		
    	}
    	finally {
    		end(begin, "loadLdapConnectionConfigs(Long)");
    	}	        
	}
	public ZoneConfig loadZoneConfig(Long zoneId) {
		long begin = System.currentTimeMillis();
		try {
			ZoneConfig zoneConfig = (ZoneConfig)load(ZoneConfig.class, zoneId);
			if (zoneConfig != null) return zoneConfig;
			throw new NoObjectByTheIdException("errorcode.no.zone.by.the.id", zoneId);
    	}
    	finally {
    		end(begin, "loadZoneConfig(Long)");
    	}	        
	}
 }
