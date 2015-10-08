/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.TreeMap;
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
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.exception.ConstraintViolationException;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.LongIdComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.KablinkDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.MobileDeviceSelectSpec;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.dao.util.ProxyIdentitySelectSpec;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.AnyOwner;
import org.kablink.teaming.domain.AppNetFolderSyncSettings;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.BasicAudit;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderQuota;
import org.kablink.teaming.domain.BinderState;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.CustomAttributeListElement;
import org.kablink.teaming.domain.Dashboard;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DefinitionInvalidOperation;
import org.kablink.teaming.domain.DeletedBinder;
import org.kablink.teaming.domain.EntityDashboard;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.FolderEntryStats;
import org.kablink.teaming.domain.HKey;
import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.domain.KeyShieldConfig;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LibraryEntry;
import org.kablink.teaming.domain.LoginAudit;
import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoBinderByTheNameException;
import org.kablink.teaming.domain.NoBinderQuotaByTheIdException;
import org.kablink.teaming.domain.NoDashboardByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoLdapConnectionConfigByTheIdException;
import org.kablink.teaming.domain.NoLibraryEntryByTheIdException;
import org.kablink.teaming.domain.NoNetFolderConfigByTheIdException;
import org.kablink.teaming.domain.NoNetFolderConfigByTheNameException;
import org.kablink.teaming.domain.NoNetFolderServerByTheIdException;
import org.kablink.teaming.domain.NoNetFolderServerByTheNameException;
import org.kablink.teaming.domain.NoOpenIDProviderByTheIdException;
import org.kablink.teaming.domain.NoPostingByTheIdException;
import org.kablink.teaming.domain.NoTagByTheIdException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.NoZoneByTheIdException;
import org.kablink.teaming.domain.NotifyStatus;
import org.kablink.teaming.domain.OpenIDProvider;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.ProxyIdentity;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.SharedEntity;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserDashboard;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.BinderState.FullSyncStatus;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.SimpleName.SimpleNamePK;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Validator;
import org.kablink.util.dao.hibernate.DynamicDialect;
import org.kablink.util.search.Junction;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * ?
 * 
 * @author Jong Kim
 */
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public class CoreDaoImpl extends KablinkDao implements CoreDao {
	protected int inClauseLimit=1000;
	protected Log logger = LogFactory.getLog(getClass());

    /**
     * Called after bean is initialized.  
     */
	@Override
	protected void initDao() throws Exception {
		//some database limit the number of terms 
		inClauseLimit=SPropsUtil.getInt("db.clause.limit", 1000);
	}

	@Override
	public boolean isDirty() {
		long begin = System.nanoTime();
		try {
			return getSession().isDirty();
    	}
    	finally {
    		end(begin, "isDirty()");
    	}	        
	}
	@Override
	public void flush() {
		long begin = System.nanoTime();
		try {
			getSession().flush();
    	}
    	finally {
    		end(begin, "flush()");
    	}	        
	}
	@Override
	public void clear() {
		long begin = System.nanoTime();
		try {
			getSession().clear();
    	}
    	finally {
    		end(begin, "clear()");
    	}	        
	}
	@Override
	public boolean contains(Object obj) {
		long begin = System.nanoTime();
		try {
			return getSession().contains(obj);
    	}
    	finally {
    		end(begin, "contains(Object)");
    	}	        
	}
	@Override
	public void evict(Object obj) {
		long begin = System.nanoTime();
		try {
			if (obj instanceof Collection) {
				final Collection objs = (Collection)obj;
				getHibernateTemplate().execute(
						new HibernateCallback() {
							@Override
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
	@Override
	public void refresh(Object obj) {
		if(obj == null) return;
		long begin = System.nanoTime();
		try {
			getSession().refresh(obj);
    	}
    	finally {
    		end(begin, "refresh(Object)");
    	}	        
	}
	@Override
	public void lock(Object obj) {
		long begin = System.nanoTime();
		try {
			getSession().refresh(obj, LockMode.UPGRADE);
			//getSession().lock(obj, LockMode.WRITE);
    	}
    	finally {
    		end(begin, "lock(Object)");
    	}	        
	}
	@Override
	public void save(Object obj) {
		long begin = System.nanoTime();
		try {
			if (obj instanceof Collection) {
				final Collection objs = (Collection)obj;
			       getHibernateTemplate().execute(
			                new HibernateCallback() {
			                    @Override
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
	@Override
	public void update(Object obj) {
		long begin = System.nanoTime();
		try {
			getHibernateTemplate().update(obj);
    	}
    	finally {
    		end(begin, "update(Object)");
    	}	        
	}
	
	/*
	 * This method differs from update(Object) in that this does not go through the
	 * Spring's HibernateTemplate class which performs additional checking on the flush
	 * mode. This allows us to work around the issue with OpenSessionInViewFilter where 
	 * its flush mode is set to NEVER/MANUAL when not in a transaction which prevents
	 * us from re-attaching an object to the session when not in an update transaction. 
	 */
	@Override
	public void updateWithoutUsingHibernateTemplate(Object obj) {
		long begin = System.nanoTime();
		try {
			getSession().update(obj);
    	}
    	finally {
    		end(begin, "update(Object)");
    	}	        
	}
	
	@Override
	public Object merge(Object obj) {
		long begin = System.nanoTime();
		try {
			return getHibernateTemplate().merge(obj);
    	}
    	finally {
    		end(begin, "merge(Object)");
    	}	        
	}
	@Override
	public void replicate(final Object obj) {
		long begin = System.nanoTime();
		try {
	      getHibernateTemplate().execute(
                new HibernateCallback() {
                    @Override
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
	@Override
	public void replicate(final Object obj, final ReplicationMode replicationMode) {
		long begin = System.nanoTime();
		try {
	      getHibernateTemplate().execute(
                new HibernateCallback() {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException {
                    	 session.replicate(obj, replicationMode);
                    	 return null;
                    }
                }
            );
    	}
    	finally {
    		end(begin, "replicate(Object, ReplicationMode)");
    	}	        
	}
	@Override
	public SFQuery queryObjects(final ObjectControls objs, FilterControls filter, final Long zoneId) { 
		long begin = System.nanoTime();
		try {
			final FilterControls myFilter = filter==null?new FilterControls():filter;
			if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
	       Query query = (Query)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
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
 	@Override
	public void deleteEntityAssociations(final String whereClause) {
		long begin = System.nanoTime();
		try {
		   	getHibernateTemplate().execute(
		    	   	new HibernateCallback() {
		    	   		@Override
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
	@Override
	public void delete(Object obj) {
		long begin = System.nanoTime();
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
	@Override
	public void delete(final Binder binder) {
		long begin = System.nanoTime();
		try {
			delete(binder, null);
    	}
    	finally {
    		end(begin, "delete(Binder)");
    	}	        
	}
	@Override
	public void delete(final Binder binder, final Class entryClass) {
		long begin = System.nanoTime();
		try {
		   	getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
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
		    			Date now = new Date();
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
			   			//Mark share items as deleted where shared entity is this binder
			   			session.createQuery("Update org.kablink.teaming.domain.ShareItem set deletedDate=:deletedDate where deletedDate is null and sharedEntity_type=:sharedEntityType and sharedEntity_id=:sharedEntityId")
			   			.setDate("deletedDate", now)
                    	.setInteger("sharedEntityType", binder.getEntityType().getValue())
                    	.setLong("sharedEntityId", binder.getId())
		   				.executeUpdate();
			   			//Mark share items as deleted where recipient is this team
			   			session.createQuery("Update org.kablink.teaming.domain.ShareItem set deletedDate=:deletedDate where deletedDate is null and recipient_type=:recipientType AND recipient_id=:recipientId")
			   			.setDate("deletedDate", now)
                    	.setShort("recipientType", ShareItem.RecipientType.team.getValue())
                    	.setLong("recipientId", binder.getId())
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
			   			//delete associated binder state
			   			session.createQuery("DELETE org.kablink.teaming.domain.BinderState where binderId=:binderId")
		   				.setLong("binderId", binder.getId())
		   				.executeUpdate();
			   			//delete associated net folder config ONLY IF this binder represents a net folder top.
			   			if(binder.getNetFolderConfigId() != null) {
				   			session.createQuery("DELETE org.kablink.teaming.domain.NetFolderConfig where id=:netFolderConfigId and topFolderId=:topFolderId")
			   				.setLong("netFolderConfigId", binder.getNetFolderConfigId())
			   				.setLong("topFolderId", binder.getId())
			   				.executeUpdate();			   				
			   			}
	
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
			   				
		    	   			//delete folderentrystats associated with those affected entries
		    	   			List<Long> entryIds = getFolderEntryIds(binder);
		    	   			delete(FolderEntryStats.class, ObjectKeys.FIELD_ID, entryIds);
		    	   			
			   				//finally delete the entries
		    	   			try {		    	   				
				   				session.createQuery("Delete " + entryClass.getName() + " where parentBinder=:parent")
				       	   				.setEntity("parent", binder)
				       	   				.executeUpdate();	
		    	   			}
		    	   			catch(ConstraintViolationException e) {
		    	   				if(entryClass.equals(FolderEntry.class)) {
			    	   				// This means that there are one or more records in the database that still point to
			    	   				// one or more folder entries whose parents is the binder being deleted. We will need
			    	   				// to perform some additional fix-up in order to be able to proceed from this state.
			    	   				if(logger.isDebugEnabled())
			    	   					logger.debug("Error deleting folder entries in binder " + binder.getId(), e);
			    	   				logger.warn("Encountered constraint violation while deleting folder entries in binder " + binder.getId() +
			    	   						": Will clear references to those entries and give it another try");
			    	   				List<Long> folderEntryIds = getFolderEntryIds(binder);
			    	   				StringBuilder inList = new StringBuilder();
			    	   				int count = 0;
			    	   				for(Long folderEntryId:folderEntryIds) {
			    	   					if(inList.length() > 0)
			    	   						inList.append(",");
			    	   					inList.append(folderEntryId);
			    	   					count++;
			    	   					if(count >= 500) { 
			    	   						// Clear associations in batch every 500 entries
			    	             			String entityString = "ownerId in (" + inList.toString() + ") and ownerType='" + EntityType.folderEntry.name() + "'";
			    		    		   		deleteEntityAssociations(entityString);
			    		    		   		// Reset variables
			    		    		   		count = 0;
			    		    		   		inList = new StringBuilder();
			    	   					}
			    	   				}
			    	   				if(inList.length() > 0) {
			    	   					// Process the remainder
		    	             			String entityString = "ownerId in (" + inList.toString() + ") and ownerType='" + EntityType.folderEntry.name() + "'";
		    		    		   		deleteEntityAssociations(entityString);
			    	   				}
			    	   				// Now that we cleared the association, let's try it again.
					   				session.createQuery("Delete " + entryClass.getName() + " where parentBinder=:parent")
			       	   				.setEntity("parent", binder)
			       	   				.executeUpdate();	
					   				logger.info("Successfully re-executed the statement after clearing associations for up to " + folderEntryIds.size() + " child entries");
		    	   				}
		    	   				else {
		    	   					// Don't know how to fix this up. Rethrow.
		    	   					throw e;
		    	   				}
		    	   			}	 		   				
			   			}

			   			//delete customAttributeListElement definitions on this binder
	    	   			session.createQuery("DELETE org.kablink.teaming.domain.CustomAttributeListElement where ownerId=:ownerId and ownerType=:entityType")
			   				.setLong("ownerId", binder.getId())
			   				.setParameter("entityType", binder.getEntityType().name())
			   				.executeUpdate();
		   				
	    	   		    //delete customAttributeListElement definitions on this binder
	    	   			session.createQuery("DELETE org.kablink.teaming.domain.CustomAttribute where ownerId=:ownerId and ownerType=:entityType")
			   				.setLong("ownerId", binder.getId())
			   				.setParameter("entityType", binder.getEntityType().name())
			   				.executeUpdate();

			   			//delete mashup definitions on this binder
	    	   			session.createQuery("DELETE org.kablink.teaming.domain.CustomAttribute where binder=:binder")
			   				.setLong("binder", binder.getId())
			   				.executeUpdate();
			   			
	    	   			//delete binder quota on this binder
	    	   			session.createQuery("DELETE org.kablink.teaming.domain.BinderQuota where binderId=:binder")
		   				.setLong("binder", binder.getId())
		   				.executeUpdate();
	    	   			
	    	   			//create a log for the binder being deleted.
	    	   			/* This is instead being done in AbstractBinderProcessor.deleteBinder_preDelete() method.
	    	   			 * From logical stand point of view, it makes more sense to do this in the first phase
	    	   			 * rather than in the second GC phase.
	    	   			try {
		    	   			DeletedBinder deletedBinder = new DeletedBinder(binder);
							getCoreDao().replicate(deletedBinder, ReplicationMode.OVERWRITE);
	    	   			}
	    	   			catch(Exception e) {
	    	   				logger.error("Error creating DeletedBinder for binder " + binder.getId(), e);
	    	   			}
	    	   			*/
	    	   			
	    	   			//delete binder itself
	    	   			try {
				   			//do ourselves or hibernate will flsuh
				   			session.createQuery("Delete org.kablink.teaming.domain.Binder where id=:id")
				   		    	.setLong("id", binder.getId().longValue())
				   		    	.executeUpdate();
	    	   			}
	    	   			catch(ConstraintViolationException e) {
	    	   				// This almost surely means that the table still contains one or more child rows that still point to the binder as parent.
	    	   				// We need to clear the association in order to be able to delete the binder. Also, it doesn't make any sense to
	    	   				// delete the parent alone while leaving the child as orphan in a tree hierarchy. So we mark the child appropriately
	    	   				// so that they can also be garbage collected by the system in subsequent cycles.
	    	   				if(logger.isDebugEnabled())
	    	   					logger.debug("Error deleting binder " + binder.getId(), e);
	    	   				logger.warn("Encountered constraint violation while deleting binder " + binder.getId() + ": Will clear references from children and give it another try");
	    	   				session.createQuery("update org.kablink.teaming.domain.Binder set parentBinder=null, topFolder=null, deleted=:delete where parentBinder=:binder1 or topFolder=:binder2")
	    	   			    .setBoolean("delete", Boolean.TRUE)
			   				.setLong("binder1", binder.getId())	   				
			   				.setLong("binder2", binder.getId())	   				
	    	   				.executeUpdate();
	    	   				// Now that we cleared the association, let's try it again.
				   			session.createQuery("Delete org.kablink.teaming.domain.Binder where id=:id")
			   		    	.setLong("id", binder.getId().longValue())
			   		    	.executeUpdate();
				   			logger.info("Successfully re-executed the statement after clearing associations from child binders");
	    	   			}
			   			
			   			if (!binder.isRoot()) {
			   				session.getSessionFactory().evictCollection("org.kablink.teaming.domain.Binder.binders", binder.getParentBinder().getId());
			   			}
			   			//find definitions owned by this binder
			   			List<Definition> defs = session.createCriteria(Definition.class)
			   				.add(Expression.eq("binderId", binder.getId()))
			   				.add(Expression.eq(ObjectKeys.FIELD_ZONE, binder.getZoneId())).
			   				list();
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
	
	@Override
	public void move(final Binder binder) {
		long begin = System.nanoTime();
		try {
			//this should handle entries also
			getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
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


    @Override
	public Object load(Class clazz, String id) {
		long begin = System.nanoTime();
		try {
			return getHibernateTemplate().get(clazz, id);
    	}
    	finally {
    		end(begin, "load(Class,String)");
    	}	        
    }
    @Override
	public Object load(Class clazz, Long id) {
		long begin = System.nanoTime();
		try {
			return getHibernateTemplate().get(clazz, id);         
    	}
    	finally {
    		end(begin, "load(Class,Long)");
    	}	        
    }
    
    @Override
	public Object loadLocked(Class clazz, String id) {
		long begin = System.nanoTime();
		try {
			return getHibernateTemplate().get(clazz, id, LockMode.PESSIMISTIC_WRITE);
    	}
    	finally {
    		end(begin, "loadLocked(Class,String)");
    	}	        
    }
    @Override
	public Object loadLocked(Class clazz, Long id) {
		long begin = System.nanoTime();
		try {
			return getHibernateTemplate().get(clazz, id, LockMode.PESSIMISTIC_WRITE);         
    	}
    	finally {
    		end(begin, "loadLocked(Class,Long)");
    	}	        
    }
	/**
	 * Return a list containing an object array, where each object in a row representing the value of the requested attribute
	 * This is used to return a subset of object attributes
	 */
	@Override
	public List loadObjects(ObjectControls objs, FilterControls filter, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadObjects(objs, filter, zoneId, false);
    	}
    	finally {
    		end(begin, "loadObjects(ObjectControls,FilterControls,Long)");
    	}	        
	}
	@Override
	public List loadObjectsCacheable(ObjectControls objs, FilterControls filter, Long zoneId) {
		long begin = System.nanoTime();
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
	@Override
	public List loadObjects(final String query, final Map values) {
		long begin = System.nanoTime();
		try {
			return loadObjects(query, values, null);
    	}
    	finally {
    		end(begin, "loadObjects(String,Map)");
    	}	        
	}
	
	@Override
	public List loadObjects(final String query, final Map values, final Integer maxResults) {
		long begin = System.nanoTime();
		try {
			return (List)getHibernateTemplate().execute(
			        new HibernateCallback() {
			            @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                  	Query q = session.createQuery(query);
		                  	if(maxResults != null)
		                  		q.setMaxResults(maxResults.intValue());
		                  	if (values != null) {
		                  		for (Iterator iter=values.entrySet().iterator(); iter.hasNext();) {
		                  			Map.Entry me = (Map.Entry)iter.next();
		                  			Object val = me.getValue();
		                  			if (val instanceof Collection) {
		                  				if (((Collection)val).size() > inClauseLimit) 
		                  					throw new IllegalArgumentException("Collection to large");
		                  				if (((Collection)val).size() == 0) 
		                  					throw new IllegalArgumentException("Collection to small");
		                  				q.setParameterList((String)me.getKey(), (Collection)val);
		                  			} else if (val instanceof Object[]) {
		                  				if (((Object[])val).length > inClauseLimit) 
		                  					throw new IllegalArgumentException("Collection to large");
		                  				if (((Object[])val).length == 0) 
		                  					throw new IllegalArgumentException("Collection to small");
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
	@Override
	public List loadObjects(Class className, FilterControls filter, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadObjects(new ObjectControls(className), filter, zoneId);
    	}
    	finally {
    		end(begin, "loadObjecs(Class,FilterControls,Long)");
    	}	        
	}
	@Override
	public List loadObjectsCacheable(Class className, FilterControls filter, Long zoneId) {
		long begin = System.nanoTime();
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
   @Override
public List loadObjects(final Collection ids, final Class className, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        if ((ids == null) || ids.isEmpty()) return new ArrayList();
	        List result = (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                    @Override
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
   @Override
public List loadObjects(final Collection ids, final Class className, final Long zoneId, final List collections) {
		long begin = System.nanoTime();
		try {
	       if ((ids == null) || ids.isEmpty()) return new ArrayList();
	       List result = (List)getHibernateTemplate().execute(
	           new HibernateCallback() {
	                   @Override
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
   @Override
public long countObjects(final Class clazz, FilterControls filter, Long zoneId) {
		long begin = System.nanoTime();
		try {
			final FilterControls myFilter = filter==null?new FilterControls():filter;
			if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
			Long result = (Long)getHibernateTemplate().execute(
			    new HibernateCallback() {
			        @Override
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
   
   @Override
public long countObjects(final Class clazz, FilterControls filter, Long zoneId, final StringBuffer sbuf) {
		long begin = System.nanoTime();
		try {
			final FilterControls myFilter = filter==null?new FilterControls():filter;
			if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
			Long result = (Long)getHibernateTemplate().execute(
			    new HibernateCallback() {
			        @Override
					public Object doInHibernate(Session session) throws HibernateException {
			        	StringBuffer query = new StringBuffer();
	                  	query.append(" select count(*) from x in class " + clazz.getName());
	                  	myFilter.appendFilter("x", query);
	                  	if(sbuf != null)
	                  		query.append(sbuf.toString());
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
	
	@Override
	public double averageColumn(final Class clazz, final String column, FilterControls filter, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	final FilterControls myFilter = filter==null?new FilterControls():filter;
	    	if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
			Double result = (Double)getHibernateTemplate().execute(
			    new HibernateCallback() {
			        @Override
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
	@Override
	public long sumColumn(final Class clazz, final String column, FilterControls filter, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	final FilterControls myFilter = filter==null?new FilterControls():filter;
	    	if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
	    	Long result = (Long)getHibernateTemplate().execute(
			    new HibernateCallback() {
			        @Override
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
    @Override
	public void registerFileName(Binder binder, DefinableEntity entity, String name) throws TitleException {
		long begin = System.nanoTime();
		try {
	    	//Folderentries or binders only
	        if (Validator.isEmptyString(name)) throw new TitleException("");
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
    @Override
	public void registerTitle(Binder binder, DefinableEntity entity) throws TitleException {
		long begin = System.nanoTime();
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
    @Override
	public void unRegisterFileName(Binder binder, String name) {
		long begin = System.nanoTime();
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
    @Override
	public void unRegisterTitle(Binder binder, String name) {
		long begin = System.nanoTime();
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
    @Override
	public void updateFileName(Binder binder, DefinableEntity entity, String oldName, String newName) throws TitleException {
		long begin = System.nanoTime();
		try {
	    	//Folderentries or binders only
	       if (Validator.isNotEmptyString(newName) && newName.equalsIgnoreCase(oldName)) return;
	 		if (oldName != null) {
		        LibraryEntry oldLe = new LibraryEntry(binder.getId(), LibraryEntry.FILE, oldName);
				if (!(entity instanceof Binder)) oldLe.setEntityId(entity.getId());
		        removeOldName(oldLe, entity);
	 		}
	 		//this was a remove
			if (Validator.isEmptyString(newName)) return;
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
    @Override
	public void updateTitle(Binder binder, DefinableEntity entity, String oldName, String newName) throws TitleException {
		long begin = System.nanoTime();
		try {
	    	//Folderentries or binders only
	       if (Validator.isNotEmptyString(newName) && newName.equalsIgnoreCase(oldName)) return;
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
			if (Validator.isEmptyString(newName)) return;
			//register new name
			LibraryEntry le = new LibraryEntry(binder.getId(), LibraryEntry.TITLE, newName);
			if (!(entity instanceof Binder)) le.setEntityId(entity.getId());
			addNewName(le, entity);
    	}
    	finally {
    		end(begin, "updateTitle(Binder,DefinableEntity,String,String)");
    	}	        
    }
    @Override
	public Long getEntityIdForMatchingTitle(Long binderId, String title) {
		long begin = System.nanoTime();
		try {
	    	LibraryEntry le = new LibraryEntry(binderId, LibraryEntry.TITLE, title);
			LibraryEntry exist = loadLibraryEntry(le);
			if(exist != null)
				return exist.getEntityId();
			else
				return null;
    	}
    	finally {
    		end(begin, "getEntityIdForMatchingTitle(Long,String)");
    	}	        
    }
    @Override
	public LibraryEntry getRegisteredTitle(Long binderId, String title) {
		long begin = System.nanoTime();
		try {
	    	LibraryEntry le = new LibraryEntry(binderId, LibraryEntry.TITLE, title);
			LibraryEntry exist = loadLibraryEntry(le);
			return exist;
    	}
    	finally {
    		end(begin, "getRegisteredTitle(Long,String)");
    	}	        
    }
    @Override
	public LibraryEntry getRegisteredFileName(Long binderId, String fileName) {
		long begin = System.nanoTime();
		try {
	    	LibraryEntry le = new LibraryEntry(binderId, LibraryEntry.FILE, fileName);
			LibraryEntry exist = loadLibraryEntry(le);
			return exist;
    	}
    	finally {
    		end(begin, "getRegisteredFileName(Long,String)");
    	}	        
    }
    @Override
	public boolean isTitleRegistered(Long binderId, String title) {
		long begin = System.nanoTime();
		try {
			return(null != getRegisteredTitle(binderId, title));
    	}
    	finally {
    		end(begin, "isTitleRegistered(Long,String)");
    	}	        
    }
    @Override
	public boolean isFileNameRegistered(Long binderId, String fileName) {
		long begin = System.nanoTime();
		try {
			return(null != getRegisteredFileName(binderId, fileName));
    	}
    	finally {
    		end(begin, "isFileNameRegistered(Long,String)");
    	}	        
    }
    protected void removeOldName(LibraryEntry oldLe, DefinableEntity entity) {
		LibraryEntry le = loadLibraryEntry(oldLe);
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
			LibraryEntry exist = loadLibraryEntry(newLe);
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
    @Override
	public void addExistingName(LibraryEntry le, DefinableEntity entity) {
		long begin = System.nanoTime();
		try {
			LibraryEntry exist = loadLibraryEntry(le);
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
    @Override
	public  Long findFileNameEntryId(Binder binder, String name) {
		long begin = System.nanoTime();
		try {
	    	LibraryEntry le = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, new LibraryEntry(binder.getId(),LibraryEntry.FILE, name));
	    	if (le == null) throw new NoLibraryEntryByTheIdException(binder.getId(), name);
	    	return le.getEntityId();
    	}
    	finally {
    		end(begin, "findFileNameEntryId(Binder,String)");
    	}	        

    }
	//Clears only folderentries. sub-folder remain since they must always be unique for webdav to traverse the tree
    @Override
	public void clearFileNames(Binder binder) {
		long begin = System.nanoTime();
		try {
	    	executeUpdate("delete from org.kablink.teaming.domain.LibraryEntry where binderId=" +
	    			binder.getId() + " and type=" + LibraryEntry.FILE.toString() + " and not entityId is null", null);
    	}
    	finally {
    		end(begin, "clearFileNames(Binder)");
    	}	        
    	
    }
    //Clear all titles, don't need if uniqueTitles not enabled.
    @Override
	public void clearTitles(Binder binder) {
		long begin = System.nanoTime();
		try {
	    	executeUpdate("delete from org.kablink.teaming.domain.LibraryEntry where binderId=" +
	    			binder.getId() + " and type=" + LibraryEntry.TITLE.toString(), null);
    	}
    	finally {
    		end(begin, "clearTitles(Binder)");
    	}	        
    	
    }
    @Override
	public List<Workspace> findCompanies() {
		long begin = System.nanoTime();
		try {
			return (List)getHibernateTemplate().execute(
			    new HibernateCallback() {
			        @Override
					public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Workspace.class)
	             				.add(Expression.eq("internalId", ObjectKeys.TOP_WORKSPACE_INTERNALID))
	             				.setCacheable(isBinderQueryCacheable())
	             				.list();
	               }
	            }
			);
    	}
    	finally {
    		end(begin, "findCompanies()");
    	}	        
	}
	@Override
	public Workspace findTopWorkspace(final String zoneName) {
		long begin = System.nanoTime();
		try {
	        return (Workspace)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                        List results = session.createCriteria(Workspace.class)
	                             		.add(Expression.eq("internalId", ObjectKeys.TOP_WORKSPACE_INTERNALID))
	                             		.add(Expression.eq("name", zoneName))
	                             		.setCacheable(isReservedBinderQueryCacheable())
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
	
	@Override
	public Long findTopWorkspaceId(final String zoneName) {
		long begin = System.nanoTime();
		try {
	        return (Long)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                        List results = session.createCriteria(Workspace.class)
                                        .setProjection(
                                                Projections.projectionList()
                                                    .add(Projections.property("id"), "id")
                                        )
	                             		.add(Expression.eq("internalId", ObjectKeys.TOP_WORKSPACE_INTERNALID))
	                             		.add(Expression.eq("name", zoneName))
	                             		.setCacheable(isBinderQueryCacheable())
	                             		.list();
	                        if (results.isEmpty()) {
	                            throw new NoWorkspaceByTheNameException(zoneName);
	                        }
	                        return (Long)results.get(0);
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
    @Override
	public Binder loadBinder(Long binderId, Long zoneId) {  
		long begin = System.nanoTime();
		try {
			Binder binder = (Binder)load(Binder.class, binderId);
	        if (binder == null) {
	        	throw new NoBinderByTheIdException(binderId);
	        }
	        if (!binder.getZoneId().equals(zoneId)) {
	        	throw new NoBinderByTheIdException(binderId);
	        }
	        return binder;
    	}
    	finally {
    		end(begin, "loadBinder(Long,Long)");
    	}	        
    }

    @Override
	public Binder loadReservedBinder(final String reservedId, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        return (Binder)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                        List results = session.createCriteria(Binder.class)
	                             		.add(Expression.eq("internalId", reservedId))
	                             		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                             		.setCacheable(isReservedBinderQueryCacheable())
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
 
    @Override
	public Definition loadReservedDefinition(final String reservedId, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        return (Definition)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                        List results = session.createCriteria(Definition.class)
	                             		.add(Expression.eq("internalId", reservedId))
	                             		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                             		.setCacheable(true)
	                             		.setCacheRegion("query.ReferenceQueryCache")
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
	@Override
	public Definition loadDefinition(String defId, Long zoneId) {
		long begin = System.nanoTime();
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

	@Override
	public Definition loadDefinitionByName(final Binder binder, final String name, final Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (Definition)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	Criteria crit =session.createCriteria(Definition.class)
	                 		.add(Expression.eq("zoneId", zoneId))
	                 		.add(Expression.eq("name", name));
	                 		if (binder != null) crit.add(Expression.eq("binderId", binder.getId()));
	                 		else crit.add(Expression.eq("binderId", ObjectKeys.RESERVED_BINDER_ID));
	                 		crit.setCacheable(true)
	                 		.setCacheRegion("query.ReferenceQueryCache");
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
	@Override
	public List loadDefinitions(FilterControls filter, Long zoneId) {
		long begin = System.nanoTime();
		try {
			List list = loadObjects(new ObjectControls(Definition.class), filter, zoneId);
			filterDefinitions(list);
			return list;
    	}
    	finally {
    		end(begin, "loadDefinitions(FilterControls, Long)");
    	}	        
	}
	@Override
	public List loadDefinitions(Long zoneId) {
		long begin = System.nanoTime();
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
	@Override
	public List loadTemplates(final Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (List)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	Criteria criteria = session.createCriteria(TemplateBinder.class)
	                 		.add(Expression.isNull("parentBinder"))
	                 		.add(Expression.isNull(ObjectKeys.FIELD_ENTITY_TEMPLATE_OWNING_BINDER_ID))
	                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                 		.addOrder(Order.asc("definitionType"))
	                 		.addOrder(Order.asc("templateTitle"));
		                 	criteria = filterCriteriaForTemplates(criteria);
		                 	criteria.setCacheable(isBinderQueryCacheable());
		                 	return criteria.list();
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadTemplates(Long)");
    	}	        
	}
	// return binder level templates
	@Override
	public List loadTemplates(final Binder binder, final Long zoneId, boolean includeAncestors) {
		if (includeAncestors) {
			//This request is for a list of templates from this binder and all of its ancestor binders
			Map<String,Binder> templates = new TreeMap<String,Binder>();
			Binder parentBinder = binder;
			while (parentBinder != null) {
				final Binder tb = parentBinder;
				List<TemplateBinder> templateBinders = loadTemplates(tb, zoneId, false);
				for (Binder b : templateBinders) {
					templates.put(b.getTitle().toLowerCase(), b);					
				}
				parentBinder = parentBinder.getParentBinder();
			}
			List results = new ArrayList();
			for (String title : (Set<String>)templates.keySet()) {
				results.add(templates.get(title));
			}
			return results;
		} else {
			//Just return a list of the local templates owned by this binder
			long begin = System.nanoTime();
			try {
				return (List)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	Criteria criteria = session.createCriteria(TemplateBinder.class)
	                 		.add(Expression.eq(ObjectKeys.FIELD_ENTITY_TEMPLATE_OWNING_BINDER_ID, binder.getId()))
	                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                 		.addOrder(Order.asc("definitionType"))
	                 		.addOrder(Order.asc("templateTitle"));
		                 	criteria = filterCriteriaForTemplates(criteria);
		                 	criteria.setCacheable(isBinderQueryCacheable());
		                 	return criteria.list();
		                }
		            }
		        );
	    	}
	    	finally {
	    		end(begin, "loadTemplates(Long)");
	    	}	  
		}
	}

	@Override
	public List<TemplateBinder> loadTemplates(final Binder binder, final Long zoneId, final int type, boolean includeAncestors) {
		if (includeAncestors) {
			//This request is for a list of templates from this binder and all of its ancestor binders
			Map<String,Binder> templates = new TreeMap<String,Binder>();
			Binder parentBinder = binder;
			while (parentBinder != null) {
				final Binder tb = parentBinder;
				List<TemplateBinder> templateBinders = loadTemplates(tb, zoneId, type, false);
				for (Binder b : templateBinders) {
					templates.put(b.getTitle().toLowerCase(), b);					
				}
				parentBinder = parentBinder.getParentBinder();
			}
			List results = new ArrayList();
			for (String title : (Set<String>)templates.keySet()) {
				results.add(templates.get(title));
			}
			return results;
		} else {
			long begin = System.nanoTime();
			try {
				return (List)getHibernateTemplate().execute(
			            new HibernateCallback() {
			                @Override
							public Object doInHibernate(Session session) throws HibernateException {
			                	Criteria criteria = session.createCriteria(TemplateBinder.class)
		                 		.add(Expression.isNull(ObjectKeys.FIELD_ENTITY_PARENTBINDER))
		                 		.add(Expression.eq(ObjectKeys.FIELD_ENTITY_TEMPLATE_OWNING_BINDER_ID, binder.getId()))
		                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
		                 		.add(Expression.eq("definitionType", type))
		                 		.addOrder(Order.asc(ObjectKeys.FIELD_TEMPLATE_TITLE));
			                 	criteria = filterCriteriaForTemplates(criteria);
			                 	criteria.setCacheable(isBinderQueryCacheable());
			                 	return criteria.list();
			                }
			            }
			        );
	    	}
	    	finally {
	    		end(begin, "loadTemplates(Long,int)");
	    	}	
		}
	}
	
	@Override
	public List loadTemplates(final Long zoneId, final int type) {
		long begin = System.nanoTime();
		try {
			return (List)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                	Criteria criteria = session.createCriteria(TemplateBinder.class)
	                 		.add(Expression.isNull(ObjectKeys.FIELD_ENTITY_PARENTBINDER))
	                 		.add(Expression.isNull(ObjectKeys.FIELD_ENTITY_TEMPLATE_OWNING_BINDER_ID))
	                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                 		.add(Expression.eq("definitionType", type))
	                 		.addOrder(Order.asc(ObjectKeys.FIELD_TEMPLATE_TITLE));
		                 	criteria = filterCriteriaForTemplates(criteria);
		                 	criteria.setCacheable(isBinderQueryCacheable());
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
	
	@Override
	public TemplateBinder loadTemplate(Long templateId, Long zoneId) {
		long begin = System.nanoTime();
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

	@Override
	public TemplateBinder loadTemplateByName(final String name, final Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (TemplateBinder)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                	TemplateBinder template = (TemplateBinder)session.createCriteria(TemplateBinder.class)
	                 		.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                  		.add(Expression.isNull(ObjectKeys.FIELD_ENTITY_PARENTBINDER))
	                		.add(Expression.eq(ObjectKeys.FIELD_BINDER_NAME, name))
	                		.setCacheable(isBinderQueryCacheable())
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
	@Override
	public void delete(final Definition def) {
		long begin = System.nanoTime();
		try {
			getHibernateTemplate().execute(
		        new HibernateCallback() {
		            @Override
					public Object doInHibernate(Session session) throws HibernateException {
		            	//see if in use
		            	List results;
		               	if (def.getType() != Definition.WORKFLOW) {
		               		long count = countObjects(org.kablink.teaming.domain.FolderEntry.class, new FilterControls("entryDefId", def.getId()), def.getZoneId());
		               		if (count > 0) throw new DefinitionInvalidOperation();
		               		count = countObjects(org.kablink.teaming.domain.Principal.class, new FilterControls("entryDefId", def.getId()), def.getZoneId());
		               		if (count > 0) throw new DefinitionInvalidOperation();
		               		count = countObjects(org.kablink.teaming.domain.Binder.class, new FilterControls("entryDef", def), def.getZoneId());
		               		if (count > 0) throw new DefinitionInvalidOperation();
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
		               		if (count > 0) throw new DefinitionInvalidOperation();
	
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
	
	@Override
	public void delete(final Class clazz, final String idPropertyName, final List<Long> ids) {
		long begin = System.nanoTime();
		try {
			getHibernateTemplate().execute(
			        new HibernateCallback() {
			            @Override
						public Object doInHibernate(Session session) throws HibernateException {
			            	delete(session, clazz, idPropertyName, ids);
			            	return null;
			            }
			        }
			     );
    	}
    	finally {
    		end(begin, "delete(Class,String,List)");
    	}	        
	}
	
	private void delete(Session session, Class clazz, String idPropertyName, List<Long> ids) {
		String queryStr = "Delete " + clazz.getName() + " where " + idPropertyName + " in (:idList)";
		Query query = session.createQuery(queryStr);
		List<Long> idList;
		for(int i = 0; i < ids.size(); i += inClauseLimit) {
			idList = ids.subList(i, Math.min(ids.size(), i + inClauseLimit));
			query.setParameterList("idList", idList)
			.executeUpdate();
		}		
	}
	
	@Override
	public boolean checkInUse(final Definition def){
		long begin = System.nanoTime();
		try {
			Boolean inUse = (Boolean) getHibernateTemplate().execute(
			        new HibernateCallback() {
			            @Override
						public Object doInHibernate(Session session) throws HibernateException {
			            	//see if in use
			               	if (def.getType() != Definition.WORKFLOW) {
			               		long count = countObjects(org.kablink.teaming.domain.FolderEntry.class, new FilterControls("entryDefId", def.getId()), def.getZoneId());
			               		if (count > 0) return new Boolean(true);
			               		count = countObjects(org.kablink.teaming.domain.Principal.class, new FilterControls("entryDefId", def.getId()), def.getZoneId());
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
	@Override
	public Object saveNewSession(Object obj) {
		long begin = System.nanoTime();
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
    @Override
	public Object saveNewSessionWithoutUpdate(Object obj) {
		long begin = System.nanoTime();
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

    @Override
	public Object updateNewSessionWithoutUpdate(Object obj) {
		long begin = System.nanoTime();
		try {
	      	SessionFactory sf = getSessionFactory();
	    	Session s = sf.openSession();
	    	try {
	    		s.update(obj);
	    		s.flush();
	    	} finally {
	    		s.close();
	    	}
	    	return obj;
    	}
    	finally {
    		end(begin, "updateNewSessionWithoutUpdate(Object)");
    	}	        
    }

	@Override
	public List loadPostings(Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadObjects(new ObjectControls(PostingDef.class), null, zoneId);
    	}
    	finally {
    		end(begin, "loadPostings(Long)");
    	}	        
	}
	@Override
	public PostingDef loadPosting(String postingId, Long zoneId) {
		long begin = System.nanoTime();
		try {
			PostingDef post = (PostingDef)load(PostingDef.class, postingId);
	        if (post == null) {throw new NoPostingByTheIdException(postingId);}
	        //make sure from correct zone
	        if (!post.getZoneId().equals(zoneId)) {throw new NoPostingByTheIdException(postingId);}
	  		return post;
    	}
    	finally {
    		end(begin, "loadPosting(String,Long)");
    	}	        		
	}
	@Override
	public PostingDef findPosting(final String emailAddress, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (PostingDef)getHibernateTemplate().execute(
			        new HibernateCallback() {
			            @Override
						public Object doInHibernate(Session session) throws HibernateException {
		               		return session.createCriteria(PostingDef.class)
		               						.add(Expression.eq("emailAddress", emailAddress))
		               						.setCacheable(true)
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
	@Override
	public void bulkLoadCollections(Collection entries) {
		long begin = System.nanoTime();
		try {
			if ((entries == null) || entries.isEmpty())  return;
	 		if (entries.size() > inClauseLimit) 
	 			throw new IllegalArgumentException("Collection to large");
	       	final TreeSet sorted = new TreeSet(new LongIdComparator());
	       	sorted.addAll(entries);
			getHibernateTemplate().execute(
	            new HibernateCallback() {
	                @Override
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
	
	@Override
	public List<Long> findZoneEntityIds(Long entityId, String zoneUUID, String entityType) {
		long begin = System.nanoTime();
		try {
	    	if (Validator.isNull(zoneUUID)) return new ArrayList<Long>();
	    	if (zoneUUID.matches("[^a-zA-Z0-9.]")) {
	    		//If the zoneUUID contains anything other than letters, numbers and a "." then it is bogus and can't be used
	    		return new ArrayList<Long>();
	    	}
	    	//Load customAttributes
	     	//Cannot criteria query, cause different order-by is specified in mapping files and it appears to take precedence
	       	final String id = zoneUUID + "." + entityId;
	       	final String type = entityType;
	       	final String key = org.kablink.util.search.Constants.ZONE_UUID_FIELD;
	       	final String thisZoneId = String.valueOf(RequestContextHolder.getRequestContext().getZoneId());
	       	return (List<Long>)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                @Override
					public Object doInHibernate(Session session) throws HibernateException {
	                	List<Long> result = new ArrayList<Long>();
	                	List readObjs = new ArrayList();
	                	List objs = null;
	                	if (type.equals(EntityType.folderEntry.name())) {
	    					objs = session.createQuery("SELECT owner From org.kablink.teaming.domain.CustomAttribute WHERE name='" + key + "' AND stringValue='" + id + "' AND ownerType='folderEntry' AND zoneId='" + thisZoneId + "'")
				   			.list();
	                	} else {
	    					objs = session.createQuery("SELECT owner From org.kablink.teaming.domain.CustomAttribute WHERE name='" + key + "' AND stringValue='" + id + "' AND (ownerType='folder' OR ownerType='workspace') AND zoneId='" + thisZoneId + "'")
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
	
	@Override
	public Tag loadTag(final String tagId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	        Tag t =(Tag)getHibernateTemplate().get(Tag.class, tagId);
	        if (t != null && t.getZoneId().equals(zoneId)) return t;
	        throw new NoTagByTheIdException(tagId);
    	}
    	finally {
    		end(begin, "loadTag(String,Long)");
    	}	        
	}
	//The entries must be of the same type
	//Used by indexing bulk load

	@Override
	public Map<EntityIdentifier, List<Tag>> loadAllTagsByEntity(final Collection<EntityIdentifier> entityIds) {
		
		
		long begin = System.nanoTime();
		try {
			if (entityIds.isEmpty()) return new HashMap();
			if (entityIds.size() > inClauseLimit) 
				throw new IllegalArgumentException("Collection to large");
			List<Tag> tags = (List)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
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
	                 		.setCacheable(true)
		                 	.list();
		                }
		            }
		        );
			Map<EntityIdentifier, List<Tag>> result = new HashMap();
			// Sort entityIds by EntityIdentifier
			TreeSet<EntityIdentifier> entityIdSet = new TreeSet<EntityIdentifier>(entityIds);
			for (EntityIdentifier id :entityIdSet) {
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
	@Override
	public List<Tag> loadAllTagsByEntity(final EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
			return (List<Tag>)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	return session.createCriteria(Tag.class)
	                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
	       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
	       					.setCacheable(true)
	 	                 	.list();
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadAllTagsByEntity(EntityIdentifier)");
    	}	        
		
	}

	@Override
	public List<Tag> loadCommunityTagsByEntity(final EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
			return (List<Tag>)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	return session.createCriteria(Tag.class)
	                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
	       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
	       					.add(Expression.eq("public", true))
	       					.setCacheable(true)
	 	                 	.list();
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadCommunityTagsByEntity(EntityIdentifier)");
    	}	        
		
	}
	@Override
	public List<Tag> loadPersonalTagsByEntity(final EntityIdentifier entityId, final EntityIdentifier ownerId) {
		long begin = System.nanoTime();
		try {
			return (List<Tag>)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	return session.createCriteria(Tag.class)
	                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
	       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
	                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
	       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
	       					.add(Expression.eq("public",false))
	       					.setCacheable(true)
		                 	.list();
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadPersonalTagsByEntity(EntityIdentifier,EntityIdentifier)");
    	}	        
		
	}
	@Override
	public List<Tag> loadPersonalTagsByOwner(final EntityIdentifier ownerId) {
		long begin = System.nanoTime();
		try {
			return (List<Tag>)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	return session.createCriteria(Tag.class)
	                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
	       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
	       					.add(Expression.eq("public",false))
	       					.setCacheable(true)
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
    @Override
	public List<Tag> loadEntityTags(final EntityIdentifier entityIdentifier, final EntityIdentifier ownerIdentifier) {
		long begin = System.nanoTime();
		try {
		   	return (List<Tag>)getHibernateTemplate().execute(
			     	new HibernateCallback() {
			       		@Override
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
	              			.setCacheable(true)
		                 	.list();
		    	   		}
		    	   	}
		    	 );    	
    	}
    	finally {
    		end(begin, "loadEntityTags(EntityIdentifier,EntityIdentifier)");
    	}	        
   	
    }
	
	@Override
	public List<Subscription> loadSubscriptionByEntity(final EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
			return (List<Subscription>)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	return session.createCriteria(Subscription.class)
	                 		.add(Expression.eq("id.entityId", entityId.getEntityId()))
	       					.add(Expression.eq("id.entityType", entityId.getEntityType().getValue()))
	       					.setCacheable(true)
		                 	.list();
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadSubscriptionByEntity(EntityIdentifier)");
    	}	        
		
	}
	
	@Override
	public boolean subscriptionExistsOnEntity(final EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
			Subscription subscription = (Subscription)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                 	return session.createCriteria(Subscription.class)
	                 		.add(Expression.eq("id.entityId", entityId.getEntityId()))
	       					.add(Expression.eq("id.entityType", entityId.getEntityType().getValue()))
	       					.setMaxResults(1)
	       					.setCacheable(true)
		                 	.uniqueResult();
		                }
		            }
		        );
			return subscription != null;
    	}
    	finally {
    		end(begin, "subscriptionExistsOnEntity(EntityIdentifier)");
    	}	        
		
	}
	
	private List loadObjects(final ObjectControls objs, FilterControls filter, Long zoneId, final boolean cacheable) {
	   	final FilterControls myFilter = filter==null?new FilterControls():filter;
		if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
		return (List)getHibernateTemplate().execute(
	        new HibernateCallback() {
	            @Override
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
	@Override
	public UserDashboard loadUserDashboard(final EntityIdentifier ownerId, final Long binderId) {		
		long begin = System.nanoTime();
		try {
			return (UserDashboard)getHibernateTemplate().execute(
				new HibernateCallback() {
		            @Override
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
	@Override
	public EntityDashboard loadEntityDashboard(final EntityIdentifier ownerId) {
		long begin = System.nanoTime();
		try {
			return (EntityDashboard)getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
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

	@Override
	public Dashboard loadDashboard(String id, Long zoneId) {
		long begin = System.nanoTime();
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
			throw new NoDashboardByTheIdException(id);
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
	@Override
	public void executeUpdate(final String queryStr) {
		long begin = System.nanoTime();
		try {
			executeUpdate(queryStr, null);
    	}
    	finally {
    		end(begin, "executeUpdate(String)");
    	}	        
	}
	@Override
	public void executeUpdate(final String queryStr, final Map values) {
		long begin = System.nanoTime();
		try {
	    	getHibernateTemplate().execute(
	        	   	new HibernateCallback() {
	        	   		@Override
						public Object doInHibernate(Session session) throws HibernateException {
	    		   			Query query = session.createQuery(queryStr);
		                  	if (values != null) {
		                  		for (Iterator iter=values.entrySet().iterator(); iter.hasNext();) {
		                  			Map.Entry me = (Map.Entry)iter.next();
		                  			Object val = me.getValue();
		                  			if (val instanceof Collection) {
		                  				if (((Collection)val).size() > inClauseLimit) 
		                  					throw new IllegalArgumentException("Collection to large");
		                  				if (((Collection)val).size() == 0) 
		                  					throw new IllegalArgumentException("Collection to small");
		                  				query.setParameterList((String)me.getKey(), (Collection)val);
		                  			} else if (val instanceof Object[]) {
		                  				if (((Object[])val).length > inClauseLimit) 
		                  					throw new IllegalArgumentException("Collection to large");
		                  				if (((Object[])val).length == 0) 
		                  					throw new IllegalArgumentException("Collection to small");
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
	
	@Override
	public int daysSinceInstallation()
	{
		long begin = System.nanoTime();
		try {
			final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;
			List dates = (List) getHibernateTemplate().execute(
		            new HibernateCallback() {
		                @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                	Conjunction conj = Restrictions.conjunction();
		                	conj.add(Restrictions.isNull("netFolderConfigId"))
		                	.add(Restrictions.isNull("legacyMirroredDriverNameHash"));
		                 	return session.createCriteria(Binder.class)
		                 		.add(conj)
	                 			.setProjection(Projections.projectionList()
								.add(Projections.min("creation.date"))
								.add(Projections.max("creation.date")))
	 	                 	.list();
		                }
		            }
		        );
			if (!dates.isEmpty()) {
				Object[] row = (Object[]) dates.get(0);
				Date earliest = (Date) row[0];
				Date latest = (Date) row[1];
				Date now = new Date();
				if (latest == null || latest.before(now)) {
					//If the max creation date is in the future, then use it; otherwise use today's date
					latest = now;
				}
				if(earliest != null && latest != null) {
					long millis = latest.getTime() - earliest.getTime();
					return (int) ((millis + MILLIS_PER_DAY + 1)/MILLIS_PER_DAY);
				}
			}
			
			return 0;
    	}
    	finally {
    		end(begin, "daysSinceInstallation()");
    	}	        
	}
	@Override
	public NotifyStatus loadNotifyStatus(Binder binder, DefinableEntity entity) {
		long begin = System.nanoTime();
		try {
			//currently only the id is used in the key cause folderEntries are the only users
			NotifyStatus status = (NotifyStatus)getHibernateTemplate().get(NotifyStatus.class, entity.getEntityIdentifier().getEntityId());
	        if (status != null) return status;
	        //create one and return
	        status = new NotifyStatus(binder, entity);
	        save(status);
	        logger.debug("CoreDaoImpl.loadNotifyStatus( New NotifyStatus ): Binder: " + binder.getId() + ", Entity: " + entity.getId() + " (" + entity.getTitle() + ")");
	        status.traceStatus(logger);
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
    @Override
	public List<NotifyStatus> loadNotifyStatus(final String sinceField, final Date begin, final Date end, final int maxResults, final Long zoneId) {
		long beginMS = System.nanoTime();
		try {
	       	List result = (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
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
    @Override
	public List<NotifyStatus> loadNotifyStatus(final Binder binder, final String sinceField, final Date begin, final Date end, final int maxResults, final Long zoneId) {
		long beginMS = System.nanoTime();
		try {
	       	List result = (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(NotifyStatus.class)
	                    	.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                    	.add(Expression.ge("lastModified", begin))
	                    	.add(Expression.lt("lastModified", end))
	                    	.add(Expression.geProperty("lastModified", sinceField))
	                    	.add(Expression.like("owningBinderKey", binder.getBinderKey().getSortKey() + "%"))
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

	@Override
	public SimpleName loadSimpleName(String name, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (SimpleName) getHibernateTemplate().get(SimpleName.class, new SimpleNamePK(zoneId, name));
    	}
    	finally {
    		end(begin, "loadSimpleName(String,Long)");
    	}	        
	}

	@Override
	public SimpleName loadSimpleNameByEmailAddress(final String emailAddress, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        return (SimpleName)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                        return session.createCriteria(SimpleName.class)
	                        	.add(Expression.eq("id.zoneId", zoneId))
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
	
	@Override
	public List<SimpleName> loadSimpleNames(final Long binderId, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        return (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                        List<SimpleName> results = session.createCriteria(SimpleName.class)
	                        	.add(Expression.eq("id.zoneId", zoneId))
	                        	.add(Expression.eq("binderId", binderId))
	                        	.setCacheable(true)
	                        	.addOrder(Order.asc("id.name"))
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

	@Override
	public IndexNode findIndexNode(final String nodeName, final String indexName) {
		long begin = System.nanoTime();
		try {
			return (IndexNode)getHibernateTemplate().execute(
			        new HibernateCallback() {
			            @Override
						public Object doInHibernate(Session session) throws HibernateException {
		               		return session.createCriteria(IndexNode.class)
		               						// Use of the component wrapper causes IllegalArgumentException within
		               						// Hibernate cache. So, use individual fields instead.
		               						//.add(Expression.eq("name", new IndexNode.Name(nodeName, indexName)))
		               						.add(Expression.eq("name.nodeName", nodeName))
		               						.add(Expression.eq("name.indexName", indexName))
		               						.setCacheable(true)
		               						.setCacheRegion("query.ReferenceQueryCache")
		               						.uniqueResult();
			            }
			        }
			     );
    	}
    	finally {
    		end(begin, "findIndexNode(String,String)");
    	}	        
	}
		
	@Override
	public void purgeIndexNodeByIndexName(final String indexName) {
		long begin = System.nanoTime();
		try {
		   	getHibernateTemplate().execute(
		    	   	new HibernateCallback() {
		    	   		@Override
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
	@Override
	public List<LdapConnectionConfig> loadLdapConnectionConfigs(final Long zoneId) {
		long begin = System.nanoTime();
		try {
		   return (List<LdapConnectionConfig>)getHibernateTemplate().execute(
		    	   	new HibernateCallback() {
		    	   		@Override
						public Object doInHibernate(Session session) throws HibernateException {
		                       return session.createCriteria(LdapConnectionConfig.class)
		                       .add(Expression.eq("zoneId", zoneId))
		                       .setCacheable(true)
		                       .setCacheRegion("query.ReferenceQueryCache")
		                       .list();
	 	       	   		}
		       	   	}
		    	 );		
    	}
    	finally {
    		end(begin, "loadLdapConnectionConfigs(Long)");
    	}	        
	}
	@Override
	public LdapConnectionConfig loadLdapConnectionConfig(final String configId, final Long zoneId) {
		long begin = System.nanoTime();
		try {
            LdapConnectionConfig o =(LdapConnectionConfig)getHibernateTemplate().get(LdapConnectionConfig.class, configId);
            if (o != null && o.getZoneId().equals(zoneId)) return o;
            throw new NoLdapConnectionConfigByTheIdException(configId);
    	}
    	finally {
    		end(begin, "loadLdapConnectionConfig(String,Long)");
    	}
	}
    @Override
    public int getMaxLdapConnectionConfigPosition(final Long zoneId) {
        long begin = System.nanoTime();
        try {
            Integer position = (Integer) getHibernateTemplate().execute(
                    new HibernateCallback() {
                        @Override
                        public Object doInHibernate(Session session) throws HibernateException {
                            session.createQuery("select max(position) from org.kablink.teaming.domain.LdapConnectionConfig where zoneId=:zoneId")
                                    .setLong("zoneId", zoneId)
                                    .uniqueResult();
                            return null;
                        }
                    }
            );
            if (position==null) {
                position = 0;
            }
            return position;
        }
        finally {
            end(begin, "purgeIndexNodeByIndexName(String)");
        }
    }
	@Override
	public ZoneConfig loadZoneConfig(Long zoneId) {
		long begin = System.nanoTime();
		try {
			ZoneConfig zoneConfig = (ZoneConfig)load(ZoneConfig.class, zoneId);
			if (zoneConfig != null) return zoneConfig;
			throw new NoZoneByTheIdException(zoneId);
    	}
    	finally {
    		end(begin, "loadZoneConfig(Long)");
    	}	        
	}
	
	@Override
	public int getLoginCount(final Date startDate) {
		List result = new ArrayList();
		result = (List) getHibernateTemplate().execute(new HibernateCallback() {
			@Override
			public Object doInHibernate(Session session) throws HibernateException {
	
				List auditTrail = session.createCriteria(LoginAudit.class)
					.setProjection(Projections.projectionList()
									.add(Projections.groupProperty("userId"))
									.add(Projections.max("loginTime"))
									.add(Projections.rowCount()))
						.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId()))
						.add(Restrictions.ge("loginTime", startDate))
					.list();
				return auditTrail;
			}});
		return result.size();
	}
	
	@Override
	public List<String> getLoginInfoIds(final Long zoneId, final Long userId, final String authenticatorName, final Date startDate, final Integer maxResult) {
		long begin = System.nanoTime();
		try {
			List result = new ArrayList();
			result = (List) getHibernateTemplate().execute(new HibernateCallback() {
				@Override
				public Object doInHibernate(Session session) throws HibernateException {
					Criteria crit = session.createCriteria(LoginAudit.class)
						.setProjection(Projections.property("id"))
							.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
							.add(Restrictions.eq("userId", userId))
							.add(Restrictions.eq("authenticator", LoginAudit.toAuthenticatorDbValue(authenticatorName)))
							.add(Restrictions.ge("loginTime", startDate))
							.setCacheable(false);
					if(maxResult != null)
						crit.setMaxResults(maxResult);
					return crit.list();
				}});;
			return result;
		}
		finally {
			end(begin, "getLoginInfoIds()");
		}
	}
	
	
	@Override
	public List getOldFileVersions(final Long zoneId, final Date ageDate) {
		long begin = System.nanoTime();
		try {
			List result = new ArrayList();
			result = (List) getHibernateTemplate().execute(new HibernateCallback() {
				@Override
				public Object doInHibernate(Session session) throws HibernateException {
					ProjectionList proj = Projections.projectionList()
						.add(Projections.groupProperty("id"))
						.add(Projections.groupProperty("owner.entity"));
					Criteria crit = session.createCriteria(FileAttachment.class)
						.setProjection(proj)
							.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
							.add(Restrictions.isNotNull("agingEnabled"))
							.add(Restrictions.isNull("agingDate"))
							.add(Restrictions.eq("agingEnabled", true))
							.add(Restrictions.lt("creation.date", ageDate))
							.setCacheable(false);
					return crit.list();
				}});
			return result;
		}
		finally {
			end(begin, "getOldFileVersions()");
		}
	}
	
	@Override
	public List getOldBinderFileVersions(final Long zoneId, final Date now) {
		long begin = System.nanoTime();
		try {
			List result = new ArrayList();
			result = (List) getHibernateTemplate().execute(new HibernateCallback() {
				@Override
				public Object doInHibernate(Session session) throws HibernateException {
					ProjectionList proj = Projections.projectionList()
						.add(Projections.groupProperty("id"))
						.add(Projections.groupProperty("owner.entity"));
					Criteria crit = session.createCriteria(FileAttachment.class)
						.setProjection(proj)
							.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
							.add(Restrictions.isNotNull("agingEnabled"))
							.add(Restrictions.eq("agingEnabled", true))
							.add(Restrictions.isNotNull("agingDate"))
							.add(Restrictions.lt("agingDate", now))
							.setCacheable(false);
					return crit.list();
				}});
			return result;
		}
		finally {
			end(begin, "getOldBinderFileVersions()");
		}
	}
	
	@Override
	public Long computeDiskSpaceUsed(final Long zoneId, final Long binderId) {
		long begin = System.nanoTime();
		try {
			List<Long> result = (List) getHibernateTemplate().execute(new HibernateCallback() {
				@Override
				public Object doInHibernate(Session session) throws HibernateException {
					Criteria crit = session.createCriteria(VersionAttachment.class)
					.setProjection(Projections.projectionList().add(Projections.sum("fileItem.length")))
					.add(Restrictions.eq("owner.owningBinderId", binderId))
					.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
					.add(Restrictions.ne("repositoryName", ObjectKeys.FI_ADAPTER));
					return crit.list();
				}});;
			if(result != null && result.size() > 0 && result.get(0) != null)
				return result.get(0);
			else
				return 0L;
		}
		finally {
			end(begin, "computeDiskSpaceUsed(Long,Long)");
		}
	}
	
	@Override
	public BinderQuota loadBinderQuota(Long zoneId, Long binderId) {
		long begin = System.nanoTime();
		try {
			BinderQuota bq = (BinderQuota) load(BinderQuota.class, binderId);
			if(bq == null)
				throw new NoBinderQuotaByTheIdException(binderId);
	        //make sure from correct zone
	        if (!bq.getZoneId().equals(zoneId)) 
	        	throw new NoBinderQuotaByTheIdException(binderId);
	  		return bq;
    	}
    	finally {
    		end(begin, "loadBinderQuota(Long, Long)");
    	}	        		
	}

	@Override
	public List<Binder> loadBindersByPathName(final String pathName, final Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (List)getHibernateTemplate().execute(
				    new HibernateCallback() {
				        @Override
						public Object doInHibernate(Session session) throws HibernateException {
				        	if(lookupByRange()) {
								Criteria crit = session.createCriteria(Binder.class)
								.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
								.add(Restrictions.ge("pathName", pathName.toLowerCase()))
								.add(Restrictions.le("pathName", pathName.toUpperCase()))
								.setCacheable(isBinderQueryCacheable());
								return crit.list();
				        	}
				        	else {
				        		Query q = session.createQuery("from org.kablink.teaming.domain.Binder x where lower(x.pathName)=:pathName and x.zoneId=:zoneId");
				        		q.setParameter("pathName", pathName.toLowerCase())
				        		.setParameter("zoneId", zoneId)
				        		.setCacheable(isBinderQueryCacheable());
				        		return q.list();
				        	}
		               }
		            }
				);
    	}
    	finally {
    		end(begin, "loadBindersByPathName()");
    	}
	}

	@Override
	public Binder loadBinderByParentAndName(final Long parentBinderId, final String title, final Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (Binder)getHibernateTemplate().execute(
				    new HibernateCallback() {
				        @Override
						public Object doInHibernate(Session session) throws HibernateException {
                            Query q = session.createQuery("from org.kablink.teaming.domain.Binder x where x.parentBinder.id=:parentId and lower(x.title)=:title and x.zoneId=:zoneId and x.preDeletedWhen is null");
                            q.setParameter("parentId", parentBinderId)
                            .setParameter("title", title.toLowerCase())
                            .setParameter("zoneId", zoneId)
                            .setCacheable(isBinderQueryCacheable());
                            return q.uniqueResult();
		               }
		            }
				);
    	}
    	finally {
    		end(begin, "loadBindersByPathName()");
    	}
	}

	private LibraryEntry loadLibraryEntry(LibraryEntry le) {
		// As far as Hibernate is concerned, the input LibraryEntry instance is merely a primary key
		// for the table/class, and it ends up returning the same instance from the get() call
		// rather than creating a new instance. This causes trouble for the layer above because
		// the application layer may depend on the value of the entityId field that exists on 
		// the input instance (eg. Bug #760515)
		LibraryEntry primaryKeyValue = new LibraryEntry(le.getBinderId(), le.getType(), le.getName());
		return (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, primaryKeyValue);
	}
	
	@Override
	public OpenIDProvider loadOpenIDProvider(Long zoneId, String openIDProviderId) {
		long begin = System.nanoTime();
		try {
			OpenIDProvider o =(OpenIDProvider)getHibernateTemplate().get(OpenIDProvider.class, openIDProviderId);
	        if (o != null && o.getZoneId().equals(zoneId)) return o;
	        throw new NoOpenIDProviderByTheIdException(openIDProviderId);
    	}
    	finally {
    		end(begin, "loadOpenIDProvider(String,Long)");
    	}	        
	}

	@Override
	public List<OpenIDProvider> findOpenIDProviders(final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        return (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                        return session.createCriteria(OpenIDProvider.class)
	                        	.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                        	.setCacheable(true)
	                        	.setCacheRegion("query.ReferenceQueryCache")
	                        	.addOrder(Order.asc("title"))
	                        	.list();
	                    }
	                }
	            );
    	}
    	finally {
    		end(begin, "findOpenIDProviders(Long)");
    	}	

	}

    @Override
    public Date getAuditTrailPurgeDate(final Long zoneId) {
        Date purgeBeforeDate = null;
        ZoneConfig zoneConfig = loadZoneConfig(zoneId);
        if (zoneConfig.getAuditTrailKeepDays() > 0) {
            purgeBeforeDate = new Date(System.currentTimeMillis() - ((long)zoneConfig.getAuditTrailKeepDays())*1000L*60L*60L*24L);
        }
        return purgeBeforeDate;
    }

	@Override
	public List getBasicAuditEntries(final Long zoneId, final Date purgeBeforeDate) {
		long begin = System.nanoTime();
		try {
			List results = (List) getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
					public Object doInHibernate(Session session) throws HibernateException {
                        return session.createCriteria(BasicAudit.class)
							.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
							.add(Restrictions.isNotNull("eventDate"))
							.add(Restrictions.lt("eventDate", purgeBeforeDate))
							.setCacheable(false)
	                    	.addOrder(Order.asc("eventDate"))
	                    	.list();
		    		}
		    	}
		   	);
			return results;
		}
		finally {
    		end(begin, "getBasicAuditEntries()");
		}
	}

    @Override
    public List getBasicAuditEntries(final Long zoneId, final Date sinceDate,
                                     final List<HKey> parentBinderKeys, final boolean recursive,
                                     final AuditType[] types,
                                     final EntityType [] entityTypes, final int maxResults) {
        long begin = System.nanoTime();
        try {
            List results = (List) getHibernateTemplate().execute(
                    new HibernateCallback() {
                        @Override
						public Object doInHibernate(Session session) throws HibernateException {
                            Conjunction typeExpr = Restrictions.conjunction();
                            if (types.length==1) {
                                typeExpr.add(Restrictions.eq("eventType", types[0].getValue()));
                            } else {
                                List<Short> vals = new ArrayList<Short>();
                                for (AuditType type : types) {
                                    vals.add(type.getValue());
                                }
                                typeExpr.add(Restrictions.in("eventType", vals));
                            }
                            if (entityTypes!=null) {
                                List<Short> vals = new ArrayList<Short>();
                                for (EntityType type : entityTypes) {
                                    vals.add((short)type.getValue());
                                }
                                typeExpr.add(Restrictions.in("entityType", vals));
                            }
                            Criterion parentKeysExpr;
                            Disjunction or = Restrictions.disjunction();
                            if (parentBinderKeys!=null && parentBinderKeys.size()>0) {
                                for (HKey key : parentBinderKeys) {
                                    if (recursive) {
                                        or.add(Restrictions.like("owningBinderKey", key.getSortKey() + "%"));
                                    } else {
                                        or.add(Restrictions.eq("owningBinderKey", key.getSortKey()));
                                    }
                                }
                            }
                            parentKeysExpr = or;
                            return session.createCriteria(BasicAudit.class)
                                    .add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
                                    .add(Restrictions.isNotNull("eventDate"))
                                    .add(Restrictions.ge("eventDate", sinceDate))
                                    .add(parentKeysExpr)
                                    .add(typeExpr)
                                    .setCacheable(false)
                                    .setMaxResults(maxResults)
                                    .addOrder(Order.asc("eventDate"))
                                    .list();
                        }
                    }
            );
            return results;
        }
        finally {
            end(begin, "getBasicAuditEntries2()");
        }
    }

    @Override
    public List getBasicAuditEntries(final Long zoneId, final Date sinceDate,
                                     final List<Long> entityIds,
                                     final AuditType[] types,
                                     final EntityType [] entityTypes, final int maxResults) {
        long begin = System.nanoTime();
        try {
            List results = (List) getHibernateTemplate().execute(
                    new HibernateCallback() {
                        @Override
						public Object doInHibernate(Session session) throws HibernateException {
                            Conjunction typeExpr = Restrictions.conjunction();
                            if (types.length==1) {
                                typeExpr.add(Restrictions.eq("eventType", types[0].getValue()));
                            } else {
                                List<Short> vals = new ArrayList<Short>();
                                for (AuditType type : types) {
                                    vals.add(type.getValue());
                                }
                                typeExpr.add(Restrictions.in("eventType", vals));
                            }
                            if (entityTypes!=null) {
                                List<Short> vals = new ArrayList<Short>();
                                for (EntityType type : entityTypes) {
                                    vals.add((short) type.getValue());
                                }
                                typeExpr.add(Restrictions.in("entityType", vals));
                            }
                            Criterion entityExpr = null;
                            if (entityIds!=null && entityIds.size()>0) {
                                if (entityIds.size()==1) {
                                	entityExpr = Restrictions.eq("entityId", entityIds.get(0));
                                } else {
                                	entityExpr = Restrictions.in("entityId", entityIds);
                                }
                            }
                            Criteria crit = session.createCriteria(BasicAudit.class)
                                    .add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
                                    .add(Restrictions.isNotNull("eventDate"))
                                    .add(Restrictions.ge("eventDate", sinceDate))
                                    .add(typeExpr);
                            if(entityExpr != null)
                            	crit.add(entityExpr);
                            return crit.setCacheable(false)
                                    .setMaxResults(maxResults)
                                    .addOrder(Order.asc("eventDate"))
                                    .list();
                        }
                    }
            );
            return results;
        }
        finally {
            end(begin, "getBasicAuditEntries3()");
        }
    }

    @Override
	public int purgeBasicAudit(final Long zoneId, final Date purgeBeforeDate) {
		long begin = System.nanoTime();
		try {
			Integer c = (Integer) getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
					public Object doInHibernate(Session session) throws HibernateException {
		     	   		int count = session.createQuery("Delete org.kablink.teaming.domain.BasicAudit where zoneId=:zoneId and eventDate<:purgeBeforeDate")
			   				.setLong("zoneId", zoneId)
			   				.setDate("purgeBeforeDate", purgeBeforeDate)
		     	   			.executeUpdate();
		     	   		return Integer.valueOf(count);
		    		}
		    	}
		   	);
			return c.intValue();
		}
		finally {
    		end(begin, "purgeBasicAudit()");
		}
	}
	
    @Override
	public int purgeLoginAudit(final Long zoneId, final Date purgeBeforeDate) {
		long begin = System.nanoTime();
		try {
			Integer c = (Integer) getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
					public Object doInHibernate(Session session) throws HibernateException {
		     	   		int count = session.createQuery("Delete org.kablink.teaming.domain.LoginAudit where zoneId=:zoneId and loginTime<:purgeBeforeDate")
			   				.setLong("zoneId", zoneId)
			   				.setDate("purgeBeforeDate", purgeBeforeDate)
		     	   			.executeUpdate();
		     	   		return Integer.valueOf(count);
		    		}
		    	}
		   	);
			return c.intValue();
		}
		finally {
    		end(begin, "purgeLoginAudit()");
		}
	}
	
    @Override
	public int purgeSharingAudit(final Long zoneId, final Date purgeBeforeDate) {
		long begin = System.nanoTime();
		try {
			Integer c = (Integer) getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
					public Object doInHibernate(Session session) throws HibernateException {
		     	   		int count = session.createQuery("Delete org.kablink.teaming.domain.SharingAudit where zoneId=:zoneId and actionDate<:purgeBeforeDate")
			   				.setLong("zoneId", zoneId)
			   				.setDate("purgeBeforeDate", purgeBeforeDate)
		     	   			.executeUpdate();
		     	   		return Integer.valueOf(count);
		    		}
		    	}
		   	);
			return c.intValue();
		}
		finally {
    		end(begin, "purgeSharingAudit()");
		}
	}
	
    @Override
	public int purgeDeletedBinder(final Long zoneId, final Date purgeBeforeDate) {
		long begin = System.nanoTime();
		try {
			Integer c = (Integer) getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
					public Object doInHibernate(Session session) throws HibernateException {
		     	   		int count = session.createQuery("Delete org.kablink.teaming.domain.DeletedBinder where zoneId=:zoneId and deletedDate<:purgeBeforeDate")
			   				.setLong("zoneId", zoneId)
			   				.setDate("purgeBeforeDate", purgeBeforeDate)
		     	   			.executeUpdate();
		     	   		return Integer.valueOf(count);
		    		}
		    	}
		   	);
			return c.intValue();
		}
		finally {
    		end(begin, "purgeDeletedBinder()");
		}
	}
	
	@Override
	public List getChangeLogEntries(final Long zoneId, final Date purgeBeforeDate) {
		long begin = System.nanoTime();
		try {
			List results = (List) getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
					public Object doInHibernate(Session session) throws HibernateException {
                        return session.createCriteria(ChangeLog.class)
							.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
							.add(Restrictions.isNotNull("operationDate"))
							.add(Restrictions.lt("operationDate", purgeBeforeDate))
							.setCacheable(false)
	                    	.addOrder(Order.asc("operationDate"))
	                    	.list();
		    		}
		    	}
		   	);
			return results;
		}
		finally {
    		end(begin, "getChangeLogEntries()");
		}
	}
	
	@Override
	public int purgeChangeLogs(final Long zoneId, final Date purgeBeforeDate) {
		long begin = System.nanoTime();
		try {
		   	Integer c = (Integer) getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
					public Object doInHibernate(Session session) throws HibernateException {
		     	   		int count = session.createQuery("Delete org.kablink.teaming.domain.ChangeLog where zoneId=:zoneId and operationDate<:purgeBeforeDate")
			   				.setLong("zoneId", zoneId)
			   				.setDate("purgeBeforeDate", purgeBeforeDate)
		     	   			.executeUpdate();
		     	   		return Integer.valueOf(count);
		    		}
		    	}
		   	);
		   	return c.intValue();
		}
		finally {
    		end(begin, "purgeChangeLogs()");
		}
	}

	@Override
	public void deleteShares(final Binder binder, final boolean includeEntryShares) {
		long begin = System.nanoTime();
		try {
		   	getHibernateTemplate().execute(
		    	new HibernateCallback() {
		    		@Override
					public Object doInHibernate(Session session) throws HibernateException {
		    			Date now = new Date();
			   			//delete share items where shared entity is this binder
			   			session.createQuery("Update org.kablink.teaming.domain.ShareItem set deletedDate=:deletedDate where deletedDate is null and sharedEntity_type=:sharedEntityType and sharedEntity_id=:sharedEntityId")
			   			.setDate("deletedDate", now)
                    	.setInteger("sharedEntityType", binder.getEntityType().getValue())
                    	.setLong("sharedEntityId", binder.getId())
		   				.executeUpdate();
			   			
			   			//delete share items where recipient is this team
			   			session.createQuery("Update org.kablink.teaming.domain.ShareItem set deletedDate=:deletedDate where deletedDate is null and recipient_type=:recipientType AND recipient_id=:recipientId")
			   			.setDate("deletedDate", now)
                    	.setShort("recipientType", ShareItem.RecipientType.team.getValue())
                    	.setLong("recipientId", binder.getId())
		   				.executeUpdate();
			   					
			   			if(includeEntryShares && EntityIdentifier.EntityType.folder == binder.getEntityType()) {
		 		   			//delete share items whose shared entities are entries in the specified binder
			   				// (Bug #875322) Do NOT use nested select statement, as it tends to cause deadlock.
			   				
			   				// First, get the IDs of the entries in the binder
	    	   				List<Long> folderEntryIds = getFolderEntryIds(binder);
	    	   				
	    	   				// Second, delete all share items where shared entity matches one of the entries.
	    	   				// Break list into chunks as necessary.
	    	   				List<Long> idList;
	    	   				for(int i = 0; i < folderEntryIds.size(); i += inClauseLimit) {
	    	   					idList = folderEntryIds.subList(i, Math.min(folderEntryIds.size(), i + inClauseLimit));
			 		   			session.createQuery("Update org.kablink.teaming.domain.ShareItem set deletedDate=:deletedDate where deletedDate is null and sharedEntity_type=:sharedEntityType and sharedEntity_id in (:idList)")
			 		   			.setDate("deletedDate", now)
			 		   			.setParameter("sharedEntityType", EntityIdentifier.EntityType.folderEntry.getValue())
			 		   			.setParameterList("idList", idList)
			 		   			.executeUpdate();
	    	   				}
			   			}

			   			return null;
	    	   		}
	    	   	}
	    	 );    	
    	}
    	finally {
    		end(begin, "purgeShares");
    	}	         

	}

	@Override
	public Long peekFullSyncTask() {
		long begin = System.nanoTime();
		try {
			return getHibernateTemplate().execute(
		            new HibernateCallback<Long>() {
		                @Override
						public Long doInHibernate(Session session) throws HibernateException {
		                	return (Long) session.createCriteria(BinderState.class)
		                			.setProjection(Projections.property("binderId"))
		                			.add(Restrictions.eq("fullSyncStats.statusStr", FullSyncStatus.ready.name()))
		                			.addOrder(Order.asc("fullSyncStats.statusDate"))
		                			.setMaxResults(1)
                					.setCacheable(false)
                					.uniqueResult();
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "peekFullSyncTask()");
    	}	        
	}

	@Override
	public List<Long> getFolderEntryIds(final Binder binder) {
		// Return a list of IDs of folder entries whose parents are the specified folder
		long begin = System.nanoTime();
		try {
	    	List<Long> result = (List<Long>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(FolderEntry.class)
	                    			.setProjection(Projections.property("id"))
	                    			.add(Restrictions.eq("parentBinder", binder))
	                    			.setCacheable(false);
	                    	return crit.list();
	                    }
	                }
	            );  
	    	return result;
    	}
    	finally {
    		end(begin, "getFolderEntryIds(binder)");
    	}	        
	}
	
	/**
	 * Used to find all MobileDevice's that meet the specifications.
	 * 
	 * Returns a Map containing:
	 * 		Key:  ObjectKeys.SEARCH_ENTRIES:      List<MobileDevice> of the MobileDevice's.
	 *		Key:  ObjectKeys.SEARCH_COUNT_TOTAL:  Long of the total entries available that satisfy the selection specifications.
	 * 
	 * @param selectSpec
	 * @param zoneId
	 * 
	 * @return
	 */
	@Override
	public Map findMobileDevices(final MobileDeviceSelectSpec selectSpec, final Long zoneId) {
        Map result = null;
        long begin  = System.nanoTime();
		try {
			HibernateCallback callback = new HibernateCallback() {
                @Override
				public Object doInHibernate( Session session ) throws HibernateException {
                	// Create the base Criteria's for the queries.
                	Criteria critQuery = session.createCriteria(MobileDevice.class);	// Criteria for reading the list.
                	Criteria critTotal = session.createCriteria(MobileDevice.class);	// Criteria for determining the total count when paging.
                	critTotal.setProjection(Projections.rowCount());

                	// Factor in the sorting required.
                	String sortBy = selectSpec.getSortBy();
                	if (!(MiscUtil.hasString(sortBy))) {
                		sortBy = ObjectKeys.FIELD_MOBILE_DEVICE_DESCRIPTION;
                	}
                	Order order = (selectSpec.isSortAscend() ? Order.asc(sortBy) : Order.desc(sortBy));
                	critQuery.addOrder(order);	// Not used for the total count.
                	
                	// Factor in the paging required.
                	int     startIndex = selectSpec.getStartIndex();
                	boolean hasStart   = (0 < startIndex);
                	if (hasStart) {
                		critQuery.setFirstResult(startIndex);	// Not used for the total count.
                	}
                	int     pageSize = selectSpec.getPageSize();
                	boolean hasSize  = (((-1) != pageSize) && (Integer.MAX_VALUE != pageSize));
                	if (hasSize)  {
                		critQuery.setMaxResults(pageSize);	// Not used for the total count.
                	}
                	boolean paging = (hasStart || hasSize);
                	
                	// If we're querying for a specific user...
                	Long userId = selectSpec.getUserId();
                	if (null != userId) {
                		// ...add their ID to the criteria.
                    	critQuery.add(Restrictions.eq(ObjectKeys.FIELD_MOBILE_DEVICE_USER_ID, userId));
                    	critTotal.add(Restrictions.eq(ObjectKeys.FIELD_MOBILE_DEVICE_USER_ID, userId));
                	}

                	// If we're querying for a specific device ID...
                	String deviceId = selectSpec.getDeviceId();
                	if (MiscUtil.hasString(deviceId)) {
                		// ...add its ID to the criteria.
                		critQuery.add(Restrictions.eq(ObjectKeys.FIELD_MOBILE_DEVICE_DEVICE_ID, deviceId));
                		critTotal.add(Restrictions.eq(ObjectKeys.FIELD_MOBILE_DEVICE_DEVICE_ID, deviceId));
                	}
                	
                	// Do we have a quick filter?
                	String quickFilter = selectSpec.getQuickFilter();
                	if (MiscUtil.hasString(quickFilter)) {
                		// Yes!  See if it's in the description or
                		// userTitle columns.
                		Criterion description = Restrictions.ilike(ObjectKeys.FIELD_MOBILE_DEVICE_DESCRIPTION, quickFilter, MatchMode.ANYWHERE);
                		Criterion userTitle   = Restrictions.ilike(ObjectKeys.FIELD_MOBILE_DEVICE_USER_TITLE,  quickFilter, MatchMode.ANYWHERE);
                		critQuery.add(Restrictions.or(description, userTitle));
                		critTotal.add(Restrictions.or(description, userTitle));
                	}

                	// Get the results.
                	Map reply = new HashMap();
                	List<MobileDevice> mdList = critQuery.list();
                	if (null == mdList) {
                		mdList = new ArrayList<MobileDevice>();
                	}
                	reply.put(ObjectKeys.SEARCH_ENTRIES, mdList);
                	
                	Long mdTotal;
                	if (paging)
                	     mdTotal = ((Long) critTotal.uniqueResult());	// If we're paging, we have to obtain the total separately...
                	else mdTotal = new Long(mdList.size());				// ...otherwise, the size of the list to the total.
                	reply.put(ObjectKeys.SEARCH_COUNT_TOTAL, mdTotal);
                	return reply;
                }
            };

            // Issue the database query.
            result = ((Map) getHibernateTemplate().execute(callback));
    	}
		
		catch (Exception ex) {
			logger.error("findMobileDevices() caught an exception: " + ex.toString());
		}
		
    	finally {
    		end(begin, "findMobileDevices(MobileDeviceSelectSpec)");
    	}	              	

      	return result;   	
	}
	
	/**
	 * Used to find all ProxyIdentity's that meet the specifications.
	 * 
	 * Returns a Map containing:
	 * 		Key:  ObjectKeys.SEARCH_ENTRIES:      List<ProxyIdentity> of the ProxyIdentity's.
	 *		Key:  ObjectKeys.SEARCH_COUNT_TOTAL:  Long of the total entries available that satisfy the selection specifications.
	 * 
	 * @param selectSpec
	 * @param zoneId
	 * 
	 * @return
	 */
	@Override
	public Map findProxyIdentities(final ProxyIdentitySelectSpec selectSpec, final Long zoneId) {
        Map result = null;
        long begin  = System.nanoTime();
		try {
			HibernateCallback callback = new HibernateCallback() {
                @Override
				public Object doInHibernate( Session session ) throws HibernateException {
                	// Create the base Criteria's for the queries.
                	Criteria critQuery = session.createCriteria(ProxyIdentity.class);	// Criteria for reading the list.
                	Criteria critTotal = session.createCriteria(ProxyIdentity.class);	// Criteria for determining the total count when paging.
                	critTotal.setProjection(Projections.rowCount());

                	// Factor in the sorting required.
                	String sortBy = selectSpec.getSortBy();
                	if (!(MiscUtil.hasString(sortBy))) {
                		sortBy = ObjectKeys.FIELD_PROXY_IDENTITY_TITLE;
                	}
                	Order order = (selectSpec.isSortAscend() ? Order.asc(sortBy) : Order.desc(sortBy));
                	critQuery.addOrder(order);	// Not used for the total count.
                	
                	// Factor in the paging required.
                	int     startIndex = selectSpec.getStartIndex();
                	boolean hasStart   = (0 < startIndex);
                	if (hasStart) {
                		critQuery.setFirstResult(startIndex);	// Not used for the total count.
                	}
                	int     pageSize = selectSpec.getPageSize();
                	boolean hasSize  = (((-1) != pageSize) && (Integer.MAX_VALUE != pageSize));
                	if (hasSize)  {
                		critQuery.setMaxResults(pageSize);	// Not used for the total count.
                	}
                	boolean paging = (hasStart || hasSize);
                	
                	// If we're querying for a proxy name...
                	String proxyName = selectSpec.getProxyName();
                	if (MiscUtil.hasString(proxyName)) {
                		// ...add it to the criteria.
                		critQuery.add(Restrictions.eq(ObjectKeys.FIELD_PROXY_IDENTITY_NAME, proxyName));
                		critTotal.add(Restrictions.eq(ObjectKeys.FIELD_PROXY_IDENTITY_NAME, proxyName));
                	}
                	
                	// If we're querying for a title...
                	String title = selectSpec.getTitle();
                	if (MiscUtil.hasString(title)) {
                		// ...add it to the criteria.
                		critQuery.add(Restrictions.eq(ObjectKeys.FIELD_PROXY_IDENTITY_TITLE, title));
                		critTotal.add(Restrictions.eq(ObjectKeys.FIELD_PROXY_IDENTITY_TITLE, title));
                	}
                	
                	// Do we have a quick filter?
                	String quickFilter = selectSpec.getQuickFilter();
                	if (MiscUtil.hasString(quickFilter)) {
                		// Yes!  See if it's in the name or title
                		// columns.
                		Criterion proxyNameCrit = Restrictions.ilike(ObjectKeys.FIELD_PROXY_IDENTITY_NAME,  quickFilter, MatchMode.ANYWHERE);
                		Criterion titleCrit     = Restrictions.ilike(ObjectKeys.FIELD_PROXY_IDENTITY_TITLE, quickFilter, MatchMode.ANYWHERE);
                		critQuery.add(Restrictions.or(proxyNameCrit, titleCrit));
                		critTotal.add(Restrictions.or(proxyNameCrit, titleCrit));
                	}

                	// Get the results.
                	Map reply = new HashMap();
                	List<ProxyIdentity> mdList = critQuery.list();
                	if (null == mdList) {
                		mdList = new ArrayList<ProxyIdentity>();
                	}
                	reply.put(ObjectKeys.SEARCH_ENTRIES, mdList);
                	
                	Long mdTotal;
                	if (paging)
                	     mdTotal = ((Long) critTotal.uniqueResult());	// If we're paging, we have to obtain the total separately...
                	else mdTotal = new Long(mdList.size());				// ...otherwise, the size of the list to the total.
                	reply.put(ObjectKeys.SEARCH_COUNT_TOTAL, mdTotal);
                	return reply;
                }
            };

            // Issue the database query.
            result = ((Map) getHibernateTemplate().execute(callback));
    	}
		
		catch (Exception ex) {
			logger.error("findProxyIdentities() caught an exception: " + ex.toString());
		}
		
    	finally {
    		end(begin, "findProxyIdentities(ProxyIdentitySelectSpec)");
    	}	              	

      	return result;   	
	}
	
	@Override
	public List<Long> getSubBinderIds(final Binder binder) {
		// Return a list of IDs of binders whose parents are the specified binder
		long begin = System.nanoTime();
		try {
	    	List<Long> result = (List<Long>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(Binder.class)
	                    			.setProjection(Projections.property("id"))
	                    			.add(Restrictions.eq(ObjectKeys.FIELD_ENTITY_PARENTBINDER, binder))
	                    			.add(Restrictions.eq(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE))
	                    			.setCacheable(false);
	                    	return crit.list();
	                    }
	                }
	            );  
	    	return result;
    	}
    	finally {
    		end(begin, "getSubBinderIds(binder)");
    	}	        
	}
	
	@Override
	public void executeHeartbeatQuery(final String heartbeatQuery) {
		long begin = System.nanoTime();
		try {
			getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session) throws HibernateException {
							session.createSQLQuery(heartbeatQuery).list();
							return null;
						}
					}
			);    		             		 
    	}
    	finally {
    		end(begin, "executeHearbeatQuery(String)");
    	}	        
	}

	/**
	 * 
	 */
	@Override
	public KeyShieldConfig loadKeyShieldConfig( Long zoneId )
	{
		KeyShieldConfig keyShieldConfig = null;
		long begin = System.nanoTime();
	
		try
		{
			keyShieldConfig = (KeyShieldConfig)load( KeyShieldConfig.class, zoneId );
    	}
    	finally
    	{
    		end( begin, "loadKeyShieldConfig(Long)" );
    	}
		
		return keyShieldConfig;
	}
	
	@Override
	public void nullifyUserPassword(final Long userId) {
		// Work with a new short-lived session on its own connection so that it won't interfere 
		// with the pre-bound (thread-bound) session and its associated connection/transaction.
		
		// IMPORTANT: Do NOT use executeWithNewSession() method on HibernateTemplate object
		// for this purpose. While it creates a new session, it still reuses the same connection
		// sharing the same transaction already in progress on that connection. That approach
		// fails if the current transaction happens to be read-only. We need not only a new
		// session but also a new connection/transaction for this.
      	SessionFactory sf = getSessionFactory();
    	Session session = sf.openSession();
    	try {
			User user = (User) session.get(User.class, userId);
			user.setPassword(null);
    		session.flush();
    	} finally {
    		session.close();
    	}    	    		             		 
	}
	
	@Override
	public int purgeShareItems(final Long zoneId, final Date purgeBeforeDate) {
		// There are four different use cases that can lead to share item becoming 
		// candidate for purge (= permanent delete from the table).
		// 
		// case 1) User deletes a share item - System immediately marks the share
		//         item as "deleted" by setting "deletedDate" field.
		// => In this case, the purge function will purge the record if its "deletedDate"
		//    is before the "purgeBeforeDate".
		//
		// case 2) User deletes the entity associated with a share item - System
		//         marks the share item as "deleted" by setting "deletedDate" field.
		// => In this case, the purge function will purge the record if its "deletedDate"
		//    is before the "purgeBeforeDate" (same as first case).
		//
		// case 3) A share item expires - System (via a background job running every
		//         5 minutes) re-indexes the share item and marks the share item as 
		//         "expiration handled" 
		// => In this case, the purge function should NOT purge the record regardless
		//    of how long it has been expired. This is because some users may choose
		//    to hold on to them for record keeping or archive purpose.
		// 
		// case 4) All three cases above deal only with those share items that are
		//         flagged as "latest". This use case deals with those that are not
		//         the latest. 
		// => The purge function will locate and purge those share items whose "latest"
		//    field is false and their "startDate" is before the "purgeBeforeDate"
		//    (i.e., this object represents a historical state of the sharing at a 
		//    particular time rather than the current state and the action represented
		//    by this object took place prior to the "purgeBeforeDate").   
		//    Although not exactly precise, this should be good enough policy for
		//    managing stale data.
		
		long begin = System.nanoTime();
		try {
		   	return getHibernateTemplate().execute(
		    	new HibernateCallback<Integer>() {
		    		@Override
					public Integer doInHibernate(Session session) throws HibernateException {
		    			// Deal with case 1 and 2.
		     	   		int count = session.createQuery("Delete org.kablink.teaming.domain.ShareItem where zoneId=:zoneId and deletedDate<:purgeBeforeDate")
			   				.setLong("zoneId", zoneId)
			   				.setDate("purgeBeforeDate", purgeBeforeDate)
		     	   			.executeUpdate();
		     	   		
		     	   		// Deal with case 4.
		     	   		int count2 = session.createQuery("Delete org.kablink.teaming.domain.ShareItem where zoneId=:zoneId and latest=:latest and startDate<:purgeBeforeDate")
			   				.setLong("zoneId", zoneId)
			   				.setBoolean("latest", Boolean.FALSE)
			   				.setDate("purgeBeforeDate", purgeBeforeDate)
		     	   			.executeUpdate();

			   			return count + count2;
	    	   		}
	    	   	}
	    	 );    	
    	}
    	finally {
    		end(begin, "purgeShareItems");
    	}	          
	}
	
    @Override
	public NetFolderConfig loadNetFolderConfig(Long netFolderConfigId) throws NoNetFolderConfigByTheIdException {  
		long begin = System.nanoTime();
		try {
			NetFolderConfig nf = (NetFolderConfig)load(NetFolderConfig.class, netFolderConfigId);
			if(nf != null)
				return nf;
			else
				throw new NoNetFolderConfigByTheIdException(netFolderConfigId);
    	}
    	finally {
    		end(begin, "loadNetFolderConfig(Long)");
    	}	        
    }
    
    @Override
    public NetFolderConfig loadNetFolderConfigByName(final String netFolderName) throws NoNetFolderConfigByTheNameException {
		long begin = System.nanoTime();
		try {
			return (NetFolderConfig)getHibernateTemplate().execute(
		            new HibernateCallback<NetFolderConfig>() {
		                @Override
						public NetFolderConfig doInHibernate(Session session) throws HibernateException {
		                	NetFolderConfig netFolder = (NetFolderConfig)session.createCriteria(NetFolderConfig.class)
	                 		.add(Restrictions.eq("name", netFolderName))
	                		.setCacheable(true)
	                		.uniqueResult();
		                    if (netFolder == null)
		                    	throw new NoNetFolderConfigByTheNameException(netFolderName);
		                    else
		                    	return netFolder;
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadNetFolderConfigByName(String)");
    	}	        

    }

    @Override
	public  ResourceDriverConfig loadNetFolderServer(Long netFolderServerId) throws NoNetFolderServerByTheIdException {
		long begin = System.nanoTime();
		try {
			ResourceDriverConfig rdc = (ResourceDriverConfig)load(ResourceDriverConfig.class, netFolderServerId);
			if(rdc != null)
				return rdc;
			else
				throw new NoNetFolderServerByTheIdException(netFolderServerId);
    	}
    	finally {
    		end(begin, "loadNetFolderServer(Long)");
    	}	        
	}

    @Override
    public ResourceDriverConfig loadNetFolderServerByName(final String netFolderServerName) throws NoNetFolderServerByTheNameException {
		long begin = System.nanoTime();
		try {
			return (ResourceDriverConfig)getHibernateTemplate().execute(
		            new HibernateCallback<ResourceDriverConfig>() {
		                @Override
						public ResourceDriverConfig doInHibernate(Session session) throws HibernateException {
		                	ResourceDriverConfig rdc = (ResourceDriverConfig)session.createCriteria(ResourceDriverConfig.class)
	                 		.add(Restrictions.eq("name", netFolderServerName))
	                		.setCacheable(true)
	                		.uniqueResult();
		                    if (rdc == null)
		                    	throw new NoNetFolderServerByTheNameException(netFolderServerName);
		                    else
		                    	return rdc;
		                }
		            }
		        );
    	}
    	finally {
    		end(begin, "loadNetFolderServerByName(String)");
    	}	        

    }

	@Override
	public List<AppNetFolderSyncSettings> getAppNetFolderSyncSettings(final List<Long> netFolderIds) {
		long begin = System.nanoTime();
		try {
			return (List<AppNetFolderSyncSettings>)getHibernateTemplate().execute(
					new HibernateCallback<List<AppNetFolderSyncSettings>>() {
						@Override
						public List<AppNetFolderSyncSettings> doInHibernate(Session session) throws HibernateException {
							List rows = session.createCriteria(NetFolderConfig.class)
									.add(Restrictions.in("topFolderId", netFolderIds))
									.setProjection(Projections.projectionList()
										.add(Projections.property("topFolderId"))
										.add(Projections.property("allowDesktopAppToSyncData"))
										.add(Projections.property("allowMobileAppsToSyncData"))
										)
									.list();
							List<AppNetFolderSyncSettings> settings = new ArrayList<AppNetFolderSyncSettings>();
							for (Object row : rows) {
								Object [] values = (Object[]) row;
							    settings.add(new AppNetFolderSyncSettings((Long) values[0], (Boolean) values[1], (Boolean) values[2]));
							}
							return settings;
						}
					}
			);
		}
		finally {
			end(begin, "loadNetFolderServerByName(String)");
		}
	}
}
