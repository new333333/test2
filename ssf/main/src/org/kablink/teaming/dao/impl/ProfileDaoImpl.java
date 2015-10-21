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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.security.authentication.AuthenticationContextHolder;
import org.kablink.teaming.comparator.LongIdComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.KablinkDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.GroupSelectSpec;
import org.kablink.teaming.dao.util.ObjectControls;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.ApplicationPrincipal;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EmailAddress;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.IndividualPrincipal;
import org.kablink.teaming.domain.Membership;
import org.kablink.teaming.domain.NoApplicationByTheIdException;
import org.kablink.teaming.domain.NoGroupByTheIdException;
import org.kablink.teaming.domain.NoGroupByTheNameException;
import org.kablink.teaming.domain.NoPrincipalByTheIdException;
import org.kablink.teaming.domain.NoPrincipalByTheNameException;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.Rating;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.SharedEntity;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserEntityPK;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.UserPropertiesPK;
import org.kablink.teaming.domain.Visits;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.util.Validator;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateSystemException;

/**
 * ?
 * 
 * @author Jong Kim
 */
@SuppressWarnings({"unchecked", "deprecation"})
public class ProfileDaoImpl extends KablinkDao implements ProfileDao {
	protected int inClauseLimit=1000;
	private CoreDao coreDao;
	Map reservedIds = new HashMap();
	
	private static final String FAKE_NAME_PREFIX = "__unavailable_";
	
	public void setCoreDao(CoreDao coreDao) {
		   this.coreDao = coreDao;
		}
	private CoreDao getCoreDao() {
	    return coreDao;
	}
    /**
     * Called after bean is initialized.  
     */
	@Override
	protected void initDao() throws Exception {
		//some database limit the number of terms 
		inClauseLimit=SPropsUtil.getInt("db.clause.limit", 1000);
	}
	/*
	 * In most cases this will be initialized at startup and won't change,
	 * but adding a zone or changing the ids could do it.
	 */
	protected synchronized Long getReservedId(String internalId, Long zoneId) {
		String key = internalId + "-" + zoneId;
   		return (Long)reservedIds.get(key);	
	}
	protected synchronized Collection<Long> getReservedIds(Collection<String> internalIds, Long zoneId) {
		int c = ((null == internalIds) ? 0 : internalIds.size());
		Collection<Long> reply = new ArrayList<Long>();
		if (0 == c) {
			return reply;
		}
		Iterator<String> ii = internalIds.iterator();
		while (ii.hasNext()) {
			Long iid = getReservedId(ii.next(), zoneId);
			if (null != iid) {
				reply.add(iid);
			}
			else {
				reply.clear();
				break;
			}
		}
   		return reply;	
	}
	protected synchronized void setReservedId(String internalId, Long zoneId, Long id) {
		String key = internalId + "-" + zoneId;
   		reservedIds.put(key, id);	
	}
	
	/**
	 * Delete the binder object and its associations.
	 * Child binders should already have been deleted
	 * This code assumes all users/groups are stored
	 */	
	@Override
	public void delete(final ProfileBinder binder) {
		long begin = System.nanoTime();
		try {
			//cleanup entries - the delete of the folder in coreDao will handle associations through owningBinderId + LibraryEntries
		    getHibernateTemplate().execute(
		    	new HibernateCallback() {
		       		@Override
					public Object doInHibernate(Session session) throws HibernateException {
	    	   			Statement s = null;
	       	   			try {
	       	   				s = session.connection().createStatement();
			   				String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
			   				if (Validator.isNotNull(schema)) schema = schema+".";
			   				else schema = "";
	     	   				s.executeUpdate("delete from " + schema + "SS_WorkAreaFunctionMembers where memberId in " + 
	      	   						"(select p.id from " + schema + "SS_Principals p where  p.parentBinder=" + binder.getId() + ")");
	      	   				s.executeUpdate("delete from " + schema + "SS_Notifications where principalId in " + 
	      	   						"(select p.id from " + schema + "SS_Principals p where  p.parentBinder=" + binder.getId() + ")");
	     	   			} catch (SQLException sq) {
	       	   				throw new HibernateException(sq);
	      	   			} finally {
	       	   				try {if (s != null) s.close();} catch (Exception ex) {};
	       	   			}
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Membership where groupId in " +
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
				  				" p.parentBinder=:profile)")
					   			.setEntity("profile", binder)
		 		   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.Membership where userId in " +
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
				  				" p.parentBinder=:profile)")
					   			.setEntity("profile", binder)
	 	 		   				.executeUpdate();
	      	   			//If we are here the user space is being deleted, so get rid of things they own
			   			session.createQuery("Delete org.kablink.teaming.domain.UserProperties where zoneId=" + binder.getZoneId())
				         	.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.SeenMap where zoneId=" + binder.getZoneId())
				         	.executeUpdate();
			   			//delete ratings/visits owned or on hese principals
			   			session.createQuery("Delete org.kablink.teaming.domain.Rating where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
			   			//delete subscriptions owned by these principals
			   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
			   			//delete tags owned or on these principals
			   			session.createQuery("Delete org.kablink.teaming.domain.Tag where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
	
			   			//delete dahsboards in Zone = not totally correct, but if we are here the zone is being deleted
			   			session.createQuery("Delete org.kablink.teaming.domain.Dashboard where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.EmailAddress where zoneId=" + binder.getZoneId())
			   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.SharedEntity where zoneId=" + binder.getZoneId())
		   				.executeUpdate();
			   			//delete share items		   			
			   			session.createQuery("Delete org.kablink.teaming.domain.ShareItem where zoneId=" + binder.getZoneId())
		   				.executeUpdate();
			   			
			   		/*Pre zoneId on each item
	  
	 		   			session.createQuery("Delete org.kablink.teaming.domain.SeenMap where principalId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
	 		   					" p.parentBinder=:profile)")
			   			  	.setEntity("profile", binder)
			   				.executeUpdate();
	
			   			session.createQuery("Delete org.kablink.teaming.domain.UserProperties where principalId in " +
				   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
				  				" p.parentBinder=:profile)")
	 				   			.setEntity("profile", binder)
	 				         	.executeUpdate();
			   			//delete ratings/visits owned by these principals
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where principalId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile)")
					   		.setEntity("profile", binder)
			   				.executeUpdate();
			   			//delete subscriptions owned by these principals
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where principalId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile)")
					   		.setEntity("profile", binder)
			   				.executeUpdate();
	
			   			List types = new ArrayList();
		       			types.add(EntityIdentifier.EntityType.user.getValue());
		       			types.add(EntityIdentifier.EntityType.group.getValue());
	
		       			//delete subscriptions to these principals
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where entityId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile) and entityType in (:tList)")
					   		.setEntity("profile", binder)
		   			  		.setParameterList("tList", types)
			   				.executeUpdate();
	 	       			
			   			//delete ratings/visits for these principals
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where entityId in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile) and entityType in (:tList)")
				   			.setEntity("profile", binder)
		   			  		.setParameterList("tList", types)
		   			  		.executeUpdate();
		     	   		//delete dashboards owned
		     	   		session.createQuery("Delete org.kablink.teaming.domain.Dashboard where owner_id in " +
				   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
		   			  			" p.parentBinder=:profile) and owner_type in (:tList)")
	 			   			.setEntity("profile", binder)
		   					.setParameterList("tList", types)
		     	   			.executeUpdate();
			   			//delete tags owned by these users
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where owner_id in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
			   			  			" p.parentBinder=:profile) and owner_type in (:tList)")
			   			  	.setEntity("profile", binder)
			   			  	.setParameterList("tList", types)
			   				.executeUpdate();
		       			
			   			//delete tags for these principals - not likely to exist
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where entity_id in " + 
	 			   				"(select p.id from org.kablink.teaming.domain.Principal p where " +
				  			" p.parentBinder=:profile) and entity_type in (:tList)")
			   			  	.setEntity("profile", binder)
			   			  	.setParameterList("tList", types)
			   				.executeUpdate();
	 */
			   			session.createQuery("Update org.kablink.teaming.domain.Principal set creation.principal=null, modification.principal=null " +
	 		   					"where parentBinder=:profile")
	 		   			  	.setEntity("profile", binder)
			   				.executeUpdate();
	 		   			session.createQuery("Update org.kablink.teaming.domain.Binder set creation.principal=null, modification.principal=null, owner=null, entryDef=null " +
		   					"where id=" + binder.getId())
		   			  		.executeUpdate();
	 		   			
	 		   			//the delete of the binder in coreDao will handle associations through owningBinderId + LibraryEntries + entries
	 		   			getCoreDao().delete((Binder)binder, Principal.class);			
		       	   		return null;
		       		}
		       	}
		    );  
    	}
    	finally {
    		end(begin, "delete(ProfileBinder)");
    	}	        
	    	
	 }
	 /**
     * Delete an object and its assocations 
     * @param entry
     */
    @Override
	public void delete(Principal entry) {
		long begin = System.nanoTime();
		try {
	    	List entries = new ArrayList();
	    	entries.add(entry);
	    	deleteEntries(entries);
    	}
    	finally {
    		end(begin, "delete(Principal)");
    	}	        
             	
     } 
    /*
     * Delete a list of entries
     * We only actually delete groups.  Users are left forever, but
     * there associations are deleted
     */
    @Override
	public void deleteEntries(final Collection<Principal> entries) {
		long begin = System.nanoTime();
		try {
	    	if (entries == null || entries.size() == 0) return;
	      	getHibernateTemplate().execute(
	        	   	new HibernateCallback() {
	        	   		@Override
						public Object doInHibernate(Session session) throws HibernateException {
	        	   	   	Set ids = new HashSet();
	           			StringBuffer inList = new StringBuffer();
	        			for (Principal p: entries) {
	        	    		ids.add(p.getId());
	        	    		inList.append(p.getId().toString() + ",");
	        	    	}
	        			inList.deleteCharAt(inList.length()-1);
	        			//these associations are link tables, no class exists to access them
	     	   			Statement s = null;
	       	   			try {
	       	   				s = session.connection().createStatement();
			   				String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
			   				if (Validator.isNotNull(schema)) schema = schema+".";
			   				else schema = "";
	      	   				s.executeUpdate("delete from " + schema + "SS_WorkAreaFunctionMembers where memberId in (" + inList.toString() + ")");
	      	   				s.executeUpdate("delete from " + schema + "SS_Notifications where principalId in (" + inList.toString() + ")");
	       	   			} catch (SQLException sq) {
	       	   				throw new HibernateException(sq);
	     	   			} finally {
	       	   				try {if (s != null) s.close();} catch (Exception ex) {};
	       	   			}
	        			//need to use ownerId, cause versionattachments/customattributeList sets not indexed by principal
			   			getCoreDao().deleteEntityAssociations("ownerId in (" + inList.toString() + ") and ownerType in  ('" +
			   					EntityType.user.name() + "','" + 
			   					EntityType.group.name() + "','" +
			   					EntityType.application.name() +"','" + 
			   					EntityType.applicationGroup.name() + "')");
	 		   			session.createQuery("Delete org.kablink.teaming.domain.SeenMap where principalId in (:pList)")
			   				.setParameterList("pList", ids)
	       	   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.Membership where userId in (:uList) or groupId in (:gList)")
			   				.setParameterList("uList", ids)
			   				.setParameterList("gList", ids)
	       	   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.UserProperties where principalId in (:pList)")
		   					.setParameterList("pList", ids)
		   					.executeUpdate();
	
			   			//delete ratings/visits owned by these users
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where principalId in (:pList)")
		   					.setParameterList("pList", ids)
			   				.executeUpdate();
	
			   			//delete subscriptions owned by these users
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where principalId in (:pList)")
		   					.setParameterList("pList", ids)
			   				.executeUpdate();
	
			   			//delete email addresses associated with these users
	 		   			session.createQuery("Delete org.kablink.teaming.domain.EmailAddress where principal in (:pList)")
		   					.setParameterList("pList", entries)
			   				.executeUpdate();
			   			session.createQuery("Delete org.kablink.teaming.domain.SharedEntity where accessId in (:pList) and accessType=:accessType")
		   					.setParameterList("pList", ids)
		   				.setParameter("accessType", SharedEntity.ACCESS_TYPE_PRINCIPAL)
		   				.executeUpdate();
			   			
			   			//mark share items as deleted where recipients are these principals
			   			List<Short> accessTypes = new ArrayList<Short>();
			   			accessTypes.add(ShareItem.RecipientType.user.getValue());
			   			accessTypes.add(ShareItem.RecipientType.group.getValue());		   			
			   			session.createQuery("Update org.kablink.teaming.domain.ShareItem set deletedDate=:deletedDate where deletedDate is null and recipientId in (:pList) and recipientType in (:aList)")
			   			.setTimestamp("deletedDate", new Date())
	   					.setParameterList("pList", ids)
	   					.setParameterList("aList", accessTypes)
	   					.executeUpdate();
			   			
			   			//delete mobile devices where the device belongs to the principal.
	 		   			session.createQuery("Delete org.kablink.teaming.domain.MobileDevice where userId in (:pList)")
			   				.setParameterList("pList", ids)
	       	   				.executeUpdate();
	
	 		   			List types = new ArrayList();
		       			types.add(EntityIdentifier.EntityType.user.name());
		       			types.add(EntityIdentifier.EntityType.group.name());
		       			types.add(EntityIdentifier.EntityType.application.name());
		       			types.add(EntityIdentifier.EntityType.applicationGroup.name());
	      	   			//delete workflow history
			   			session.createQuery("Delete org.kablink.teaming.domain.WorkflowHistory where entityId in (:pList) and entityType in (:tList)")
	 	   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
			   				.executeUpdate();
			   			types.clear();
		       			types.add(EntityIdentifier.EntityType.user.getValue());
		       			types.add(EntityIdentifier.EntityType.group.getValue());
		       			types.add(EntityIdentifier.EntityType.application.getValue());
		       			types.add(EntityIdentifier.EntityType.applicationGroup.getValue());
		       			//delete subscriptions to these principals - not likely to exist
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Subscription where entityId in (:pList) and entityType in (:tList)")
	 	   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
			   				.executeUpdate();
	 	       			
			   			//delete ratings/visits at these principals - not likely to exist
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Rating where entityId in (:pList) and entityType in (:tList)")
	 	   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
			   				.executeUpdate();
		     	   		//delete dashboards owned
		     	   		session.createQuery("Delete org.kablink.teaming.domain.Dashboard where owner_id in (:pList) and owner_type in (:tList)")
	 	   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
		     	   			.executeUpdate();
	
	 		   			//delete tags owned by this entity
	 		   			session.createQuery("Delete org.kablink.teaming.domain.Tag where owner_id in (:pList) and owner_type in (:tList)")
		   					.setParameterList("pList", ids)
		   					.setParameterList("tList", types)
		   					.executeUpdate();
	 		   			//delete tags on this entity - not likely to exist
			   			session.createQuery("Delete org.kablink.teaming.domain.Tag where entity_id in (:pList) and entity_type in (:tList)")
	   						.setParameterList("pList", ids)
	   						.setParameterList("tList", types)
	   						.executeUpdate();
			   			
	
	 		   			//only delete groups,applications; leave users around
	 		   			types.clear();
		       			types.add(EntityIdentifier.EntityType.group.name());
		       			types.add(EntityIdentifier.EntityType.application.name());
		       			types.add(EntityIdentifier.EntityType.applicationGroup.name());
			   			session.createQuery("Delete org.kablink.teaming.domain.Principal where id in (:pList) and type in (:tList)")
	            			.setParameterList("pList", ids)
	   						.setParameterList("tList", types)
	      	   				.executeUpdate();
			   			
	 		   			//leave users around; just mark deleted
	 		   			session.createQuery("update org.kablink.teaming.domain.Principal set deleted=:deleted where id in (:pList) and type='user'")
	            			.setBoolean("deleted", Boolean.TRUE)
	            			.setParameterList("pList", ids)
	       	   				.executeUpdate();
			   			//this flushes secondary cache
			   			session.getSessionFactory().evict(Principal.class);			   			
			   			session.getSessionFactory().evictCollection("org.kablink.teaming.domain.UserPrincipal.memberOf");
			   			session.getSessionFactory().evictCollection("org.kablink.teaming.domain.ApplicationPrincipal.memberOf");
	      	   				
	           	   		return null;
	        	   		}
	        	   	}
	        	 );    	
    	}
    	finally {
    		end(begin, "deleteEntries(Collection<Principal>)");
    	}	        
       	
    }

    /**
     * Make the necessary updates to the various db tables needed when a user has been renamed.
     * At this point the user has already been renamed.
     */
    @Override
	public void renameUser( final User user )
    {
		long begin = System.nanoTime();
	
		try
		{
	    	getHibernateTemplate().execute(
	        	new HibernateCallback()
	        	{
	        		@Override
					public Object doInHibernate( Session session ) throws HibernateException
					{
	        			Long workspaceId;
						
						workspaceId = user.getWorkspaceId();
						if ( workspaceId != null )
						{
							Query query;
							String sql;

							sql = "UPDATE org.kablink.teaming.domain.Binder"
									+ " set name = :newName"
									+ " where id = :workspaceId";
		
							query = session.createQuery( sql )
					                   	.setString( "newName", user.getName() )
					                   	.setLong( "workspaceId", workspaceId );
					        query.executeUpdate();
						}
						
	        			return null;
	        		}
	        	}
	        );
    	}
    	finally
    	{
    		end( begin, "renameUser( User, String )" );
    	}	        
    }

    @Override
	public void disablePrincipals(final Collection<Long> ids, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	getHibernateTemplate().execute(
	        	new HibernateCallback() {
	        		@Override
					public Object doInHibernate(Session session) throws HibernateException {
	        			session.createQuery("UPDATE org.kablink.teaming.domain.Principal set disabled = :disable where zoneId = :zoneId and internalId is null and id in (:pList)")
	        			.setBoolean("disable", true)
	        			.setLong("zoneId", zoneId)
	        			.setParameterList("pList", ids)
	        			.executeUpdate();
	        			return null;
	        		}
	        	}
	        );
    	}
    	finally {
    		end(begin, "disablePrincipals(Collection<Long>,Long)");
    	}	        

    }

    /**
     * 
     */
 	@Override
	public List<Group> findGroups( final GroupSelectSpec groupSelectSpec, final Long zoneId)
	{
		long begin = System.nanoTime();
		try
		{
			HibernateCallback callback;
			
			callback = new HibernateCallback() 
            {
                @Override
				public Object doInHibernate( Session session ) throws HibernateException
				{
                	Criteria crit;
                	String filter;

                	crit = session.createCriteria( Group.class );
                	
                	// We only want groups that have not been deleted
                	crit.add( Restrictions.eq( ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE  ) );
                	
                	// Should we exclude the "all users" group?
                	if ( groupSelectSpec.getExcludeAllUsersGroup() )
                		crit.add( Restrictions.ne( ObjectKeys.FIELD_PRINCIPAL_NAME, "allusers" ) );
                	
                	// Should we exclude the "all external users group?
                	if ( groupSelectSpec.getExcludeAllExternalUsersGroup() )
                		crit.add( Restrictions.ne( ObjectKeys.FIELD_PRINCIPAL_NAME, "allextusers" ) );

                	// Should we exclude "team groups"?
                	if ( groupSelectSpec.getExcludeTeamGroups() )
                		crit.add( Restrictions.isNull( ObjectKeys.FIELD_GROUP_TYPE ) );
                	
                	// Don't include "ldap container" groups.
                	crit.add( Restrictions.isNull( ObjectKeys.FIELD_GROUP_LDAP_CONTAINER ) );
                	
                	// Do we have a filter?
                	filter = groupSelectSpec.getFilter();
                	if ( filter != null && filter.length() > 0 )
                	{
                		Criterion criterion1;
                		Criterion criterion2;
                		
                		criterion1 = Restrictions.ilike( ObjectKeys.FIELD_ENTITY_TITLE, filter, MatchMode.ANYWHERE );
                		criterion2 = Restrictions.ilike( ObjectKeys.FIELD_PRINCIPAL_NAME, filter, MatchMode.ANYWHERE ); 
                		crit.add( Restrictions.or( criterion1, criterion2 ) );
                	}
                	
                	crit.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId));

                	return crit.list();
                }
            };
 
	      	List result = (List)getHibernateTemplate().execute( callback );
	      	
	      	return result;   	
    	}
    	finally 
    	{
    		end( begin, "findGroups(ShareItemSelectSpec)");
    	}	              	
 	}

    //used for login
    @Override
	public User findUserByName(final String userName, String zoneName) {
		long begin = System.nanoTime();
		try {
	    	final Binder top = getCoreDao().findTopWorkspace(zoneName);
	    	return findUserByName(userName, top.getId());
    	}
    	finally {
    		end(begin, "findUserByName(String,String)");
    	}	        
    }
    
    @Override
	public User findUserByNameDeadOrAlive(final String userName, String zoneName) {
		long begin = System.nanoTime();
		try {
	    	final Binder top = getCoreDao().findTopWorkspace(zoneName);
	    	return findUserByNameDeadOrAlive(userName, top.getId());
    	}
    	finally {
    		end(begin, "findUserByNameDeadOrAlive(String,String)");
    	}	        
    }
    
 	@Override
	public User findUserByName(final String userName, final Long zoneId) 
	throws NoUserByTheNameException {
	   long begin = System.nanoTime();
	   try {
	       User user = (User)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                 	   	//only returns active users
	                    	Query query = session.getNamedQuery("find-User-Company")
                         		.setString(ParameterNames.USER_NAME, userName.toLowerCase());
	                 	   	User user = (User)query
	                             		.setLong(ParameterNames.ZONE_ID, zoneId)
	                             		.setCacheable(isPrincipalQueryCacheable())
	                             		.uniqueResult();
	                        //query ensures user is not deleted and not disabled
	                 	   	if (user == null) {
	                            throw new NoUserByTheNameException(userName); 
	                        }
	                        return user;
	                    }
	                }
	             );		
	        user = (User) filterInaccessiblePrincipal(user);
	        if (user == null) 
	        	throw new NoUserByTheNameException(userName); 
	        return user;
	   	}
	   	finally {
	   		end(begin, "findUserByName(String,Long)");
	   	}	        
 	}

    @Override
 	public User findUserByForeignName(final String foreignName, final Long zoneId) 
		throws NoUserByTheNameException {
 	   long begin = System.nanoTime();
 	   try {
 	       User user = (User)getHibernateTemplate().execute(
 	                new HibernateCallback() {
 	                    @Override
 						public Object doInHibernate(Session session) throws HibernateException {
 	                	   //only returns active users
 	                 	   User user = (User)session.getNamedQuery( "find-User-By-ForeignName" )
 	                             		.setString( ParameterNames.FOREIGN_NAME, foreignName.toLowerCase() )
 	                             		.setLong( ParameterNames.ZONE_ID, zoneId )
 	                             		.setCacheable( isPrincipalQueryCacheable() )
 	                             		.uniqueResult();
 	                        //query ensures user is not deleted and not disabled
 	                 	   	if (user == null)
 	                            throw new NoUserByTheNameException(foreignName); 
 	                 	   	else 
 	                 	   		return user;
 	                    }
 	                }
 	             );		
 	        user = (User) filterInaccessiblePrincipal(user);
 	        if (user == null) 
 	        	throw new NoUserByTheNameException(foreignName); 
 	        return user;
 	   	}
 	   	finally {
 	   		end(begin, "findUserByForeignName(String,Long)");
 	   	}	        
 	}

 	/**
 	 * Find an user in the ss_principals table using the ldap guid.
 	 */
 	@Override
	public User findUserByLdapGuid( final String ldapGuid, final Long zoneId ) 
		throws NoUserByTheNameException
	{
	   long begin = System.nanoTime();
	   try
	   {
	       User user = (User)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active users
                 	   User user = (User)session.getNamedQuery( "find-User-By-LdapGuid-Company" )
                             		.setString( ParameterNames.LDAP_UGID, ldapGuid )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                 	   
                        //query ensures user is not deleted and not disabled
                 	   if ( user == null )
                 	   {
                 		   throw new NoUserByTheNameException( ldapGuid ); 
                       }
                       
                 	   return user;
                    }
                }
             );		

	       user = (User) filterInaccessiblePrincipal(user);
	       if (user == null) 
	    	   throw new NoUserByTheNameException( ldapGuid ); 

	       return user;
	   }
	   finally
	   {
		   end(begin, "findUserByLdapGuid(String,Long)");
	   }	        
	}// end findUserByLdapGuid()

 	@Override
	public Long findPrincipalIdByLdapGuid( final String ldapGuid, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active principals
                  	   return session.getNamedQuery( "find-Principal-id-By-LdapGuid-Company" )
                             		.setString( ParameterNames.LDAP_UGID, ldapGuid )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findPrincipalIdByLdapGuid(String,Long)");
	   }	        
	}

 	@Override
	public Long findUserIdByObjectSid( final String objectSid, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active user
                  	   return session.getNamedQuery( "find-User-id-By-ObjectSid" )
                             		.setString( ParameterNames.OBJECT_SID,  objectSid.toLowerCase())
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findUserIdByObjectSid(String,Long)");
	   }	        
	}

 	@Override
	public Long findPrincipalIdByObjectSid( final String objectSid, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active principals
                  	   return session.getNamedQuery( "find-Principal-id-By-ObjectSid" )
                             		.setString( ParameterNames.OBJECT_SID,  objectSid.toLowerCase())
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findPrincipalIdByObjectSid(String,Long)");
	   }	        
	}

 	@Override
	public Long findUserIdByForeignName( final String foreignName, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active user
                       // We store foreign names in lower case in the database.
                  	   return session.getNamedQuery( "find-User-id-By-ForeignName" )
                             		.setString( ParameterNames.FOREIGN_NAME, foreignName.toLowerCase() )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findUserIdByForeignName(String,Long)");
	   }	        
	}

 	@Override
	public Long findPrincipalIdByForeignName( final String foreignName, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active principals
                       // We store foreign names in lower case in the database.
                  	   return session.getNamedQuery( "find-Principal-id-By-ForeignName" )
                             		.setString( ParameterNames.FOREIGN_NAME, foreignName.toLowerCase() )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findPrincipalIdByForeignName(String,Long)");
	   }	        
	}

 	@Override
	public Long findUserIdByTypelessDN( final String typelessDN, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active users
                       // We store typeless DNs in lower case in the database.
                  	   return session.getNamedQuery( "find-User-id-By-TypelessDN" )
                             		.setString( ParameterNames.TYPELESS_DN, typelessDN.toLowerCase() )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findUserIdByTypelessDN(String,Long)");
	   }	        
	}

 	@Override
	public Long findPrincipalIdByTypelessDN( final String typelessDN, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active principals
                       // We store typeless DNs in lower case in the database.
                  	   return session.getNamedQuery( "find-Principal-id-By-TypelessDN" )
                             		.setString( ParameterNames.TYPELESS_DN, typelessDN.toLowerCase() )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findPrincipalIdByTypelessDN(String,Long)");
	   }	        
	}

 	@Override
	public Long findUserIdByName( final String name, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active users
                       // We store names in lower case in the database.
                  	   return session.getNamedQuery( "find-User-id-By-Name" )
                             		.setString( ParameterNames.NAME, name.toLowerCase() )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findUserIdByName(String,Long)");
	   }	        
	}

 	@Override
	public Long findPrincipalIdByName( final String name, final Long zoneId ) 
	{
	   long begin = System.nanoTime();
	   try
	   {
	       Long id = (Long)getHibernateTemplate().execute(
                new HibernateCallback()
                {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException
                    {
                 	   //only returns active principals
                       // We store names in lower case in the database.
                  	   return session.getNamedQuery( "find-Principal-id-By-Name" )
                             		.setString( ParameterNames.NAME, name.toLowerCase() )
                             		.setLong( ParameterNames.ZONE_ID, zoneId )
                             		.setCacheable( isPrincipalQueryCacheable() )
                             		.uniqueResult();
                    }
                }
             );		
	       return id;
	   }
	   finally
	   {
		   end(begin, "findPrincipalIdByName(String,Long)");
	   }	        
	}

 	@Override
 	public Long findUserIdByDomainAndSamaccount(final String domainName, final String samaccountName, final Long zoneId) {
 	   long begin = System.nanoTime();
 	   try
 	   {
 	       Long id = (Long)getHibernateTemplate().execute(
                 new HibernateCallback()
                 {
                     @Override
 					public Object doInHibernate(Session session) throws HibernateException
                     {
                  	   //only returns active users
                        // We store names in lower case in the database.
                   	   return session.getNamedQuery( "find-User-id-By-Domain-Sam" )
                         		.setString( ParameterNames.DOMAIN_NAME, domainName.toLowerCase() )
                              	.setString( ParameterNames.SAMACCOUNTNAME, samaccountName.toLowerCase() )
                              	.setLong( ParameterNames.ZONE_ID, zoneId )
                              	.setCacheable( isPrincipalQueryCacheable() )
                              	.uniqueResult();
                     }
                 }
              );		
 	       return id;
 	   }
 	   finally
 	   {
 		   end(begin, "findUserIdByDomainAndSamaccount(String,String,Long)");
 	   }	        
 	}
 	
 	@Override
 	public Long findPrincipalIdByDomainAndSamaccount(final String domainName, final String samaccountName, final Long zoneId) {
 	   long begin = System.nanoTime();
 	   try
 	   {
 	       Long id = (Long)getHibernateTemplate().execute(
                 new HibernateCallback()
                 {
                     @Override
 					public Object doInHibernate(Session session) throws HibernateException
                     {
                  	   //only returns active principals
                        // We store names in lower case in the database.
                   	   return session.getNamedQuery( "find-Principal-id-By-Domain-Sam" )
                         		.setString( ParameterNames.DOMAIN_NAME, domainName.toLowerCase() )
                              	.setString( ParameterNames.SAMACCOUNTNAME, samaccountName.toLowerCase() )
                              	.setLong( ParameterNames.ZONE_ID, zoneId )
                              	.setCacheable( isPrincipalQueryCacheable() )
                              	.uniqueResult();
                     }
                 }
              );		
 	       return id;
 	   }
 	   finally
 	   {
 		   end(begin, "findPrincipalIdByDomainAndSamaccount(String,String,Long)");
 	   }	        
 	}
 	
 	@Override
	public Principal findPrincipalByName(final String name, final Long zoneId) 
 		throws NoPrincipalByTheNameException {
 		if(name.startsWith(FAKE_NAME_PREFIX)) {
 			Long id = Long.valueOf(name.substring(FAKE_NAME_PREFIX.length()));
 			if(logger.isDebugEnabled())
 				logger.debug("Turning findPrincipalByName with '" + name + "' into loadPrincipal with '" + id + "'");
 			return loadPrincipal(id, zoneId, false);
 		}
 		else {
			long begin = System.nanoTime();
			try {
		        Principal principal = (Principal)getHibernateTemplate().execute(
		                new HibernateCallback() {
		                    @Override
							public Object doInHibernate(Session session) throws HibernateException {
		                 	   //only returns active users
		                    	Query query = session.getNamedQuery("find-Principal-Company")
                             		.setString(ParameterNames.USER_NAME, name.toLowerCase());
		                    	Principal p= (Principal)query
		                             		.setLong(ParameterNames.ZONE_ID, zoneId)
		                             		.setCacheable(isPrincipalQueryCacheable())
		                             		.uniqueResult();
		                        //query ensures user is not deleted and not disabled
		                 	   if (p == null) {
		                            throw new NoPrincipalByTheNameException(name); 
		                 	   }
		                 	   return resolveProxy(session, p);
		                    }
		                }
		             );
		        principal = filterInaccessiblePrincipal(principal);
		        if (principal == null) 
		        	throw new NoPrincipalByTheNameException(name); 
		        return principal;
	    	}
	    	finally {
	    		end(begin, "findPrincipalByName(String,Long)");
	    	}	
 		}
 	}
 	private Principal resolveProxy(Session session, Principal proxy) {
        Principal p = null;
        try {
       		p = (Principal)session.get(User.class, proxy.getId());
            if (p != null) return  p;
       } catch (Exception ex) {};  // proxies will force an exception, didn't expect with session.get?
       try {
    	   p = (Principal)session.get(Application.class, proxy.getId());
           if (p != null) return  p;
       } catch (Exception ex) {};
       try {
    	   p = (Principal)session.get(Group.class, proxy.getId());
           if (p != null) return  p;
       } catch (Exception ex) {};
       
       return (Principal)session.get(ApplicationGroup.class, proxy.getId());
 	}

    @Override
	public ProfileBinder getProfileBinder(Long zoneId) {
		long begin = System.nanoTime();
		try {
			return (ProfileBinder)getCoreDao().loadReservedBinder(ObjectKeys.PROFILE_ROOT_INTERNALID, zoneId);
    	}
    	finally {
    		end(begin, "getProfileBinder(Long)");
    	}	        
    }

    /*
     *  (non-Javadoc)
     * @see org.kablink.teaming.dao.CoreDao#loadPrincipal(java.lang.Long, java.lang.Long)
     */
    @Override
	public UserPrincipal loadUserPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
		long begin = System.nanoTime();
		try {
	        UserPrincipal principal = (UserPrincipal)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	//hoping for cache hit
	                    	UserPrincipal principal = (UserPrincipal)session.get(UserPrincipal.class, prinId);
	                        if (principal == null) {throw new NoPrincipalByTheIdException(prinId);}
	                        //Get the real object, not a proxy to abstract class
	                        try {
	                        	principal = (UserPrincipal)session.get(User.class, prinId);
	                        } catch (Exception ex) {};  // group proxies will force an exception, didn't expect with session.get? 
	                        if (principal==null) 
	                            principal = (UserPrincipal)session.get(Group.class, prinId);
	                        //make sure from correct zone
	                        if (!principal.getZoneId().equals(zoneId) ||
	                        		(checkActive && !principal.isActive())) {throw new NoPrincipalByTheIdException(prinId);}
	                        return principal;
	                    }
	                }
	        );
	       
	        principal = (UserPrincipal)filterInaccessiblePrincipal(principal);
	        if (principal == null) throw new NoPrincipalByTheIdException(prinId); 
	        return principal;
    	}
    	finally {
    		end(begin, "loadUserPrincipal(Long,Long,boolean)");
    	}	        
              
    }
    @Override
	public List<UserPrincipal> loadUserPrincipals(final Collection ids, final Long zoneId, boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<UserPrincipal> result = loadPrincipals(ids, zoneId, UserPrincipal.class, true, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				UserPrincipal p = result.get(i);
				if (!(p instanceof User) && !(p instanceof Group)) {
					UserPrincipal principal = null;
					try {
						principal = (UserPrincipal)getHibernateTemplate().get(User.class, p.getId());
					} catch(Exception e) {}; // get throws exception rather than returning null if the object was already loaded into Hibernate as proxy and it is of different type
					if (principal==null) principal = (UserPrincipal)getHibernateTemplate().get(Group.class, p.getId());
	   				result.set(i, principal);
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadUserPrincipals(Collection,Long,boolean)");
    	}	        
    }
    @Override
	public List<Principal> loadPrincipalByEmail(final String email, final String emailType, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        List result = (List)getHibernateTemplate().execute(
	               	new HibernateCallback() {
	                		@Override
							public Object doInHibernate(Session session) throws HibernateException {
	                			String queryStr = "select x.principal from org.kablink.teaming.domain.EmailAddress x where x.address=:address";
	                			if(Validator.isNotNull(emailType))
	                				queryStr += " and x.type=:type";
	    	                  	Query q = session.createQuery(queryStr);
	    	                  	q.setParameter("address", email.toLowerCase());
	    	                  	q.setCacheable(true);	                			
	                			if(Validator.isNotNull(emailType))
	                				q.setParameter("type", emailType);
	    	                  	List result = q.list();
	    	                  	for (int i=0; i<result.size();) { //check zone here, so index is used
	    	                  		if (!((Principal)result.get(i)).getZoneId().equals(zoneId)) result.remove(i);
	    	                  		else ++i;
	    	                  	}
	    	                  	return result;
	                		}
	               	}
	            );
	        return filterInaccessiblePrincipals(result);
    	}
    	finally {
    		end(begin, "loadPrincipalByEmail(String,String,Long)");
    	}	        
        	   	
    }
    private List loadPrincipals(final Collection ids, final Long zoneId, final Class clazz, final boolean cacheable, final boolean checkActive) {
        if ((ids == null) || ids.isEmpty()) return new ArrayList();
        List result = (List)getHibernateTemplate().execute(
           	new HibernateCallback() {
            		@Override
					public Object doInHibernate(Session session) throws HibernateException {
       					//some databases restrict the size of the inList
            			int useLimit = inClauseLimit-3;  //account for the 3 we add
            			if (ids.size() <= useLimit) {
           					Criteria crit = session.createCriteria(clazz)
        					.add(Expression.in(Constants.ID, ids))
        					.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
        					.setFetchMode("emailAddresses", FetchMode.JOIN)
                    		// Unlike some other query caches used for reference type objects,
                    		// this cache is not very useful in the sense that the result of
                    		// this query is very unlikely to be shared across users.
                    		// However, some WebDAV usage patterns make it useful because it
                    		// can repeatedly asks for the same set of information (for a 
                    		// request from the same user). We can use this as a short-lived
                    		// temporary cache. 
                    		.setCacheable(cacheable);
        					if (checkActive) {
        						crit.add(Expression.eq(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE));
        						crit.add(Expression.eq(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE));
        					}  
        					List result = crit.list();
                            //eagar select results in duplicates
                            Set res = new HashSet(result);
                            result.clear();
                            result.addAll(res);
        					return result;
            			} else {
            				List idList = new ArrayList(ids);
            				Set results = new HashSet(); //fetch returns duplicates, se weed them out
            				for (int i=0; i<idList.size(); i+=useLimit) {
            					List subList = idList.subList(i, Math.min(idList.size(), i+useLimit));
            					Criteria crit = session.createCriteria(clazz)
            					.add(Expression.in(Constants.ID, subList))
            					.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
            					.setFetchMode("emailAddresses", FetchMode.JOIN)
            					// Unlike some other query caches used for reference type objects,
                        		// this cache is not very useful in the sense that the result of
                        		// this query is very unlikely to be shared across users.
                        		// However, some WebDAV usage patterns make it useful because it
                        		// can repeatedly asks for the same set of information (for a 
                        		// request from the same user). We can use this as a short-lived
                        		// temporary cache. 
                        		.setCacheable(cacheable);
            					if (checkActive) {
            						crit.add(Expression.eq(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE));
            						crit.add(Expression.eq(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE));
            					}            				
            					results.addAll(crit.list());
            				}
            				return new ArrayList(results);
            			}
            		}
           	}
        );
        return filterInaccessiblePrincipals(result);
    	
    }
    private List loadPrincipals(final FilterControls filter, Long zoneId, final Class clazz) throws DataAccessException { 
       	filter.add(ObjectKeys.FIELD_ZONE, zoneId);
    	filter.add(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE);
    	filter.add(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE);
        List result = (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException {
                        //sqlqueries, filters and criteria don't help with frontbase problem
                        //
                        Query query = session.createQuery("from " + clazz.getName() + " u " + filter.getFilterString("u"));
                		List filterValues = filter.getFilterValues();
               			for (int i=0; i<filterValues.size(); ++i) {
                			query.setParameter(i, filterValues.get(i));
                		}
               			
                       return query.list();
                    }
                }
            );  
        return filterInaccessiblePrincipals(result);
    }    
  
	@Override
	public Group loadGroup(final Long groupId, Long zoneId)  {
		long begin = System.nanoTime();
		try {
			Group group = (Group)getHibernateTemplate().get(Group.class, groupId);
			if (group == null) {throw new NoGroupByTheIdException(groupId);}
			//make sure from correct zone
			if (!group.getZoneId().equals(zoneId) || !group.isActive()) {throw new NoGroupByTheIdException(groupId);}
			return group;
		} catch (ClassCastException ce) {
			throw new NoGroupByTheIdException(groupId);
		}
		finally {
			end(begin, "loadGroup(Long,Long)");
		}	        
	}
	@Override
	public ApplicationGroup loadApplicationGroup(final Long groupId, Long zoneId)  {
		long begin = System.nanoTime();
		try {
			ApplicationGroup group = (ApplicationGroup)getHibernateTemplate().get(ApplicationGroup.class, groupId);
			if (group == null) {throw new NoGroupByTheIdException(groupId);}
			//make sure from correct zone
			if (!group.getZoneId().equals(zoneId) || !group.isActive()) {throw new NoGroupByTheIdException(groupId);}
			return group;
		} catch (ClassCastException ce) {
			throw new NoGroupByTheIdException(groupId);
		}
		finally {
			end(begin, "loadApplicationGroup(Long,Long)");
		}	        
	}
	@Override
	public List<Group> loadGroups(Collection<Long> ids, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadPrincipals(ids, zoneId, Group.class, false, true);
    	}
    	finally {
    		end(begin, "loadGroups(Collection<Long>,Long)");
    	}	        
	}

    @Override
	public List<Group> loadGroups(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return loadPrincipals(filter, zoneId, Group.class);
    	}
    	finally {
    		end(begin, "loadGroups(FilterControls,Long)");
    	}	        
    }  
	@Override
	public User loadUser(Long userId, String zoneName) {
		long begin = System.nanoTime();
		try {
			Long zoneId = null;
			if(zoneName != null)
				zoneId = getCoreDao().findTopWorkspace(zoneName).getZoneId();
			return loadUser(userId, zoneId);
    	}
    	finally {
    		end(begin, "loadUser(Long,String)");
    	}	        
    }
    @Override
	public User loadUser(Long userId, Long zoneId) {
		long begin = System.nanoTime();
    	try {
    		User user = (User)getHibernateTemplate().get(User.class, userId);
    		if (user == null) {throw new NoUserByTheIdException(userId);}
    		//	make sure from correct zone
    		if((zoneId != null && !user.getZoneId().equals(zoneId)) || !user.isActive()) {
    			throw new NoUserByTheIdException(userId);
    		}
    		// Redact user ONLY IF this call isn't being made during authentication process (bug #930603)
    		// Normally, authentication is performed under guest context, and therefore, it is necessary
    		// for guest account to be able to load other user objects including admin account.
    		if(AuthenticationContextHolder.getAuthenticator() == null) {
	    		user = (User) filterInaccessiblePrincipal(user);
	            if (user == null) 
	            	throw new NoUserByTheIdException(userId); 
    		}
    		return user;
    	} catch (ClassCastException ce) {
   			throw new NoUserByTheIdException(userId);   		
    	}
		finally {
			end(begin, "loadUser(Long,Long)");
		}	        
    }
    @Override
	public User loadUserDeadOrAlive(Long userId, Long zoneId) {
		long begin = System.nanoTime();
    	// STOP!!! THIS METHOD IS FOR INTERNAL/SPECIAL USE ONLY. USE THE REGULAR loadUser() METHOD INSTEAD.
    	try {
    		User user = (User)getHibernateTemplate().get(User.class, userId);
    		if (user == null) {throw new NoUserByTheIdException(userId);}
    		//	make sure from correct zone
    		if (!user.getZoneId().equals(zoneId)) {
    			throw new NoUserByTheIdException(userId);
    		}
    		return user;
    	} catch (ClassCastException ce) {
   			throw new NoUserByTheIdException(userId);   		
    	}
		finally {
			end(begin, "loadUserDeadOrAlive(Long,Long)");
		}	        
    }
    
	@Override
	public List<User> loadUsers(Collection<Long> ids, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadPrincipals(ids, zoneId, User.class, false, true);
    	}
    	finally {
    		end(begin, "loadUsers(Collection<Long>,Long)");
    	}	        
    }
    @Override
	public List<User> loadUsers(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return loadPrincipals(filter, zoneId, User.class);
    	}
    	finally {
    		end(begin, "loadUsers(FilterControls,Long)");
    	}	        
    }

     
    @Override
	public SFQuery queryUsers(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return queryPrincipals(filter, zoneId, User.class, false);
    	}
    	finally {
    		end(begin, "queryUsers(FilterControls,Long)");
    	}	        
    }
    @Override
	public SFQuery queryGroups(FilterControls filter, Long zoneId) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return queryPrincipals(filter, zoneId, Group.class, false);
    	}
    	finally {
    		end(begin, "queryGroups(FilterControls,Long)");
    	}	        
    }  
    @Override
	public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId, boolean includeDisabled) throws DataAccessException { 
		long begin = System.nanoTime();
		try {
			return queryPrincipals(filter, zoneId, Principal.class, includeDisabled);
    	}
    	finally {
    		end(begin, "queryAllPrincipals(FilterControls,Long)");
    	}	        
    }
    
    @Override
	public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId) throws DataAccessException {
    	return queryAllPrincipals(filter, zoneId, false);	// false -> Don't include disabled.
    }
    
    private SFQuery queryPrincipals(FilterControls filter, Long zoneId, final Class clazz, boolean includeDisabled) throws DataAccessException {
		final FilterControls myFilter = filter==null?new FilterControls():filter;
		if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
		myFilter.add(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE);
		if (!includeDisabled) {
			myFilter.add(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE);
		}
        Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException {
                        //sqlqueries, filters and criteria don't help with frontbase problem
                        //
                        Query query = session.createQuery("from " + clazz.getName() + " u " + myFilter.getFilterString("u"));
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
    
    @Override
    public List<Long> getAllPrincipalIds(final Long zoneId, final boolean includeDisabled) {
		long begin = System.nanoTime();
		try {
	    	List<Long> result = (List<Long>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(Principal.class)
	                    			.setProjection(Projections.property("id"))
	                    			.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId))
	                    			.add(Restrictions.eq(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE))
	                    			.setCacheable(false);
	                    	if(!includeDisabled)
	                    		crit.add(Restrictions.eq(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE));
	                    	return crit.list();
	                    }
	                }
	            );  
	    	return result;
    	}
    	finally {
    		end(begin, "getAllPrincipalIds()");
    	}	        
    }
    
 	@Override
	public void bulkLoadCollections(final Collection<Principal> entries) {
		long begin = System.nanoTime();
		try {
	 		if (entries.size() > inClauseLimit) 
	 			throw new IllegalArgumentException("Collection to large");
	  	    getHibernateTemplate().execute(
	            new HibernateCallback() {
	                @Override
					public Object doInHibernate(Session session) throws HibernateException {
	                	//ids will be sorted
	                 	TreeSet<Principal> sorted = new TreeSet(new LongIdComparator());
	      		    	sorted.addAll(entries);
	                   	List<Long> ids = new ArrayList();
	      		    	//ids will now be in order
	      		    	for (Principal p : sorted) {
	      		    		ids.add(p.getId());
	       		    	}
	      		    	List<EmailAddress> objs = session.createCriteria(EmailAddress.class)
	      		    		.add(Expression.in("principal", entries))
	      		    		.addOrder(Order.asc("principal"))
	      		    		.list();
	      		    	//now build EMAIL collection
	 		    		HashSet tSet = new HashSet();
	 		    		for (Principal p:sorted) {
	      		    		HashMap tMap = new HashMap();
	      		    		for (EmailAddress email:objs) {
	      		    			if (p.equals(email.getPrincipal())) {
	      		    				tMap.put(email.getType(), email);
	      		    				tSet.add(email);    
	      		    			} else break;
	      		    		}
	      		    		p.setIndexEmailAddresses(tMap);
	      		    		objs.removeAll(tSet);
	      		    		tSet.clear();
	      		    	}
	     		    	
	      		    	/* not cacheing membership
	        	    	List<Membership> result = session.createCriteria(Membership.class)
	                 		.add(Expression.in("userId", ids))
							.addOrder(Order.asc("userId"))
							.list();
	       		    	//now build memberOf collection
	       		    	for (Long pId: ids) {
			    			try {
			    				//skip if it is a group
			       				User u = (User)getCoreDao().load(User.class, pId);
			       				List groups = new ArrayList();
			       				while (!result.isEmpty()) {
			       					Membership m = result.get(0);
			       					if (m.getUserId().equals(pId)) {
			       						try {
			       							// skip if error
			       							groups.add(getCoreDao().load(Group.class, m.getGroupId()));
			       						} finally {
			       							//clear up memory
			       							getCoreDao().evict(m);
			       							result.remove(0);
			       						}
			       					} else {break;}
			       				}
			       				u.setIndexMemberOf(groups);
			    			} catch (Exception ex) {
			    				//remove membership if belongs to this it
			    				while (!result.isEmpty() && pId.equals(result.get(0).getUserId())) {
			    					result.remove(0);
			    				}
			    			};
	      		    		
	       		    	}
	*/
						return null;
	                 }
	            }
	        );
	
	 		getCoreDao().bulkLoadCollections(entries);
    	}
    	finally {
    		end(begin, "bulkLoadCollections(Collection<Principal>)");
    	}	        
		
	}
 	
    @Override
	public UserProperties loadUserProperties(Long userId) {
		long begin = System.nanoTime();
		try {
	    	UserPropertiesPK id = new UserPropertiesPK(userId);
	    	UserProperties uProps=null;
	    	try {
	    		uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);         
	 		} catch (HibernateSystemException se) {
	    		//old dom trees cause errors with the new dom4j.  These properties should be stored as strings, and
	    		//this problem was fixed before ship
	    		if (se.getRootCause() instanceof java.io.InvalidClassException || 
	    				se.getRootCause() instanceof ClassNotFoundException) {
					//bad serialized data - get rid of it
	    			executePropertiesDelete(id);
				} 
	    		else if(se.getRootCause() instanceof java.io.EOFException) {
	    			// the data in the blob is corrupt; this most likely means last update failed because
	    			// the data exceeded the size limit - there's no way we can fix up this data, get rid of it
	    			// (this is meant to be one time only cleanup on a system that already has corrupt lobs)
	    			logger.error("User properties for " + id.toString() + " seems corrupt: Resetting it to empty value", se);
	    			executePropertiesDelete(id);	    			
	    		}
	    		else {
	    			throw se;
	    		}
			}
	   		if (uProps == null) {
				uProps = new UserProperties(id);
				try {
					uProps=(UserProperties)getCoreDao().saveNewSession(uProps);
				} catch (Exception ex) {
					//contension
					uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);   
	   				if (null == uProps) {
	   					logger.error("Exception in loadUserProperties(userId)", ex);
	   					throw ex;
	   				}
				}
			}
			return uProps;
    	}
    	finally {
    		end(begin, "loadUserProperties(Long)");
    	}	        
    }
 
    @Override
	public UserProperties loadUserProperties(Long userId, Long binderId) {
		long begin = System.nanoTime();
		try {
	    	UserPropertiesPK id = new UserPropertiesPK(userId, binderId);
	    	UserProperties uProps=null;
	    	try {
	    		uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
	    	} catch (HibernateSystemException se) {
	    		//old dom trees cause errors with the new dom4j.  These properties should be stored as strings, and
	    		//	this problem was fixed before ship
	    		if (se.getRootCause() instanceof java.io.InvalidClassException || 
	    				se.getRootCause() instanceof ClassNotFoundException) {
	    			//	bad serialized data - get rid of it
	    			executePropertiesDelete(id);
	    		} 
	    		else if(se.getRootCause() instanceof java.io.EOFException) {
	    			// the data in the blob is corrupt; this most likely means last update failed because
	    			// the data exceeded the size limit - there's no way we can fix up this data, get rid of it
	    			// (this is meant to be one time only cleanup on a system that already has corrupt lobs)
	    			logger.error("User properties for " + id.toString() + " seems corrupt: Resetting it to empty value", se);
	    			executePropertiesDelete(id);	    			
	    		}
	    		else {
	    			throw se;
	    		}
	    	}
			if (uProps == null) {
	        	uProps = new UserProperties(id);
	        	//quick write
	        	try {
	        		uProps=(UserProperties)getCoreDao().saveNewSession(uProps);
	   			} catch (Exception ex) {
	   				//contension?
	   				uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
	   				if (null == uProps) {
	   					logger.error("Exception in loadUserProperties(userId, binderId)", ex);
	   					throw ex;
	   				}
	   			}
	    	}
			return uProps;
    	}
    	finally {
    		end(begin, "loadUserProperties(Long,Long)");
    	}	        
    }
	//At one point dom4j (1.5) objects where serialized,  donm4j(1.6) does not recognize them
	//Somewhere along the way we changed it so we saved the string value instead of the serialized object, but 
	//occasionally they pop up at home.
	private void executePropertiesDelete(final UserPropertiesPK id) {
		//need to execute in new session, so it gets commited
       	SessionFactory sf = getSessionFactory();
    	Session session = sf.openSession();
		Statement statement = null;
    	try {
    		String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
    		if (Validator.isNotNull(schema)) schema = schema+".";
    		else schema = "";
    		statement = session.connection().createStatement();
    		statement.executeUpdate("delete from " + schema + "SS_UserProperties where principalId=" + id.getPrincipalId() + 
    				" and binderId=" + id.getBinderId());
	   	} catch (SQLException sq) {
	    		throw new HibernateException(sq);
	   	} finally {
	   		try {if (statement != null) statement.close();} catch (Exception ex) {};
	   		session.close();
	   	}

	}
	private void executeSeenmapDelete(final Long userId) {
		//need to execute in new session, so it gets commited
       	SessionFactory sf = getSessionFactory();
    	Session session = sf.openSession();
		Statement statement = null;
    	try {
    		String schema = ((SessionFactoryImplementor)session.getSessionFactory()).getSettings().getDefaultSchemaName();
    		if (Validator.isNotNull(schema)) schema = schema+".";
    		else schema = "";
    		statement = session.connection().createStatement();
    		statement.executeUpdate("delete from " + schema + "SS_SeenMap where principalId=" + userId);
	   	} catch (SQLException sq) {
	    		throw new HibernateException(sq);
	   	} finally {
	   		try {if (statement != null) statement.close();} catch (Exception ex) {};
	   		session.close();
	   	}

	}
    @Override
	public Group getReservedGroup(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<Group>objs = getCoreDao().loadObjects(Group.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoGroupByTheNameException(internalId);
	    		Group g = objs.get(0);
	    		setReservedId(internalId, zoneId, g.getId());
	    		return g;
	    	}
	    	return loadGroup(id, zoneId);
    	}
    	finally {
    		end(begin, "getReservedGroup(String,Long)");
    	}	        
    }
    
    @Override
	public Long getReservedGroupId(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<Group>objs = getCoreDao().loadObjects(Group.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoGroupByTheNameException(internalId);
	    		Group g = objs.get(0);
	    		setReservedId(internalId, zoneId, g.getId());
	    		return g.getId();
	    	}
	    	return id;
    	}
    	finally {
    		end(begin, "getReservedGroup(String,Long)");
    	}	        
    }
    @Override
	public ApplicationGroup getReservedApplicationGroup(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<ApplicationGroup>objs = getCoreDao().loadObjects(ApplicationGroup.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoGroupByTheNameException(internalId);
	    		ApplicationGroup g = objs.get(0);
	    		setReservedId(internalId, zoneId, g.getId());
	    		return g;
	    	}
	    	return loadApplicationGroup(id, zoneId);
    	}
    	finally {
    		end(begin, "getReservedApplicationGroup(String,Long)");
    	}	        
    }
    
    @Override
	public User getReservedUser(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<User>objs = getCoreDao().loadObjects(User.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoUserByTheNameException(internalId);
	    		User u = objs.get(0);
	    		setReservedId(internalId, zoneId, u.getId());
	    		if (!u.isActive())
	    			throw new NoUserByTheIdException(u.getId());
	    		return u;
	    	}
	    	return loadUser(id, zoneId);
    	}
    	finally {
    		end(begin, "getReservedUser(String,Long)");
    	}	        
    }
    
    protected Long getReservedUserId(String internalId, Long zoneId) {
    	Long id = getReservedId(internalId, zoneId);
    	if (id == null) {
    		List<User>objs = getCoreDao().loadObjects(User.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
    		if ((objs == null) || objs.isEmpty()) throw new NoUserByTheNameException(internalId);
    		User u = objs.get(0);
    		setReservedId(internalId, zoneId, u.getId());
    		return u.getId();
    	}
    	return id;
    }
    
    @Override
	public Collection<User> getReservedUsers(Collection<String> internalIds, Long zoneId) {
    	int iiCountS = ((null == internalIds) ? 0 : internalIds.size());
    	if (0 == iiCountS) {
    		return new ArrayList<User>();
    	}
		long begin = System.nanoTime();
		try {
	    	Collection<Long> ids = getReservedIds(internalIds, zoneId);
	    	int iiCountL = ((null == ids) ? 0 : ids.size());
	    	if (iiCountS == iiCountL) {
		    	return loadUsers(ids, zoneId);
	    	}
	    	else {
	    		Collection<User> reply = new ArrayList<User>();
	    		Iterator<String> ii = internalIds.iterator();
	    		while (ii.hasNext()) {
	    			String internalId = ii.next();
		    		List<User>objs = getCoreDao().loadObjects(User.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
		    		if ((objs == null) || objs.isEmpty()) {
		    			continue;
		    		}
		    		User u = objs.get(0);
		    		setReservedId(internalId, zoneId, u.getId());
		    		if (u.isActive()) {
		    			reply.add(u);
		    		}
	    		}
	    		return reply;
	    	}
    	}
    	finally {
    		end(begin, "getReservedUsers(Collection<String>,Long)");
    	}	        
    }
    
    @Override
	public User getReservedUserDeadOrAlive(String internalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	Long id = getReservedId(internalId, zoneId);
	    	if (id == null) {
	    		List<User>objs = getCoreDao().loadObjects(User.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
	    		if ((objs == null) || objs.isEmpty()) throw new NoUserByTheNameException(internalId);
	    		User u = objs.get(0);
	    		setReservedId(internalId, zoneId, u.getId());
	    		return u;
	    	}
	    	return loadUserDeadOrAlive(id, zoneId);
    	}
    	finally {
    		end(begin, "getReservedUserDeadOrAlive(String,Long)");
    	}	        
   }
    
    /*
     * This method walks down the principal membership object graph to compute the IDs
     * which could result in lazy loading of associated entities and/or collections.
     */
    @Override
	public Set<Long> getApplicationLevelPrincipalIds(Principal p) {
		long begin = System.nanoTime();
		try {
	    	if (p instanceof IndividualPrincipal) {
		    	if(((IndividualPrincipal)p).isAllIndividualMember()) {
		        	GroupPrincipal gp;
		        	if(p instanceof User) {
		        		if (!((User) p).getIdentityInfo().isInternal()) {
		        			gp = getReservedGroup(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID, p.getZoneId());
		        		} else {
		        			gp = getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, p.getZoneId());
		        		}
		        	} else if(p instanceof Application) {
		        		gp = getReservedApplicationGroup(ObjectKeys.ALL_APPLICATIONS_GROUP_INTERNALID, p.getZoneId());
		        	} else {
		        		throw new IllegalArgumentException(p.getClass().getName());
		        	}
		        	
		    		return new HashSet(p.computePrincipalIds(gp));
		    	}
		    	else {
		    		return new HashSet(p.computePrincipalIds(null));
		    	}
	    	} else {
	    		return new HashSet(p.computePrincipalIds(null));
	    	}
    	}
    	finally {
    		end(begin, "getApplicationLevelPrincipalIds(Principal)");
    	}	        
    }
    
    @Override
	public Set<Long> getAllPrincipalIds(Principal principal) {
    	Set<Long> result = getApplicationLevelPrincipalIds(principal);
    	result.addAll(getMemberOfLdapContainerGroupIds(principal.getId(), principal.getZoneId()));
    	return result;
	}
    
	/**
	 * Given a set of principal ids, return all userIds that represent userIds in 
	 * the original list, or members of groups and their nested groups.
	 * This is used to turn a distribution list into usersIds only.
	 * Use when don't need to load the entire object
	 * @param Set of principalIds
	 * @returns Set of userIds
	 */
    @Override
	public Set<Long> explodeGroups(final Collection<Long> ids, Long zoneId) {
    	return explodeGroups(ids, zoneId, true);
    }
	@Override
	public Set<Long> explodeGroups(final Collection<Long> ids, Long zoneId, boolean allowAllUsersGroup) {   
		long begin = System.nanoTime();
		try {
			if ((ids == null) || ids.isEmpty()) return new TreeSet();
			Set users = (Set)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                @Override
					public Object doInHibernate(Session session) throws HibernateException {
	                    Set<Long> result = new TreeSet(ids);
	                    List mems;
	                    Set loopDectector = new HashSet(ids);
	                    Set currentIds = new HashSet(ids);
	                    while (!currentIds.isEmpty()) {
	                    	if (currentIds.size() <= inClauseLimit) {
	                           	mems  = session.createCriteria(Membership.class)
	                           			.add(Expression.in("groupId", currentIds))
	                           			.list();                   		
	                    	} else {
	                    		List idsList = new ArrayList(currentIds);
	                    		mems = new ArrayList();
	                    		for (int i=0; i<idsList.size(); i+=inClauseLimit) {
	                    			List partial  = session.createCriteria(Membership.class)
	                    				.add(Expression.in("groupId", idsList.subList(i, Math.min(idsList.size(), i+inClauseLimit))))
	                    				.list();
	                    			mems.addAll(partial);
	                    		}
	           				}
	                       	currentIds.clear();
							for (int i=0; i<mems.size(); ++i) {
								Membership m = (Membership)mems.get(i);
								result.remove(m.getGroupId());
								//potential user - may be another group
								if (!result.contains(m.getUserId()) && !loopDectector.contains(m.getUserId())) {
									result.add(m.getUserId());
									currentIds.add(m.getUserId());
								}
							}
							loopDectector.addAll(currentIds);
							
							//note: empty groups may appear in the resultant list
	                    }
	                    return result;
	                }
	            }
	        );
			Long allId = getReservedGroupId(ObjectKeys.ALL_USERS_GROUP_INTERNALID, zoneId);
			if (allowAllUsersGroup && (ids.contains(allId) || users.contains(allId))) {
				//need to remove some users from the all users group.  Original list may add them back in.
				//so need to do this step last
				FilterControls filter = new FilterControls("identityInfo.internal", Boolean.TRUE);
				List<Object[]> result = getCoreDao().loadObjects(
						new ObjectControls(User.class, new String[]{ObjectKeys.FIELD_ID}), filter, zoneId);
				//remove users not to be included
				result.remove(getReservedUserId(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zoneId));
				result.remove(getReservedUserId(ObjectKeys.GUEST_USER_INTERNALID, zoneId));
				result.remove(getReservedUserId(ObjectKeys.JOB_PROCESSOR_INTERNALID, zoneId));
				result.remove(getReservedUserId(ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID, zoneId));
				result.remove(getReservedUserId(ObjectKeys.FILE_SYNC_AGENT_INTERNALID, zoneId));
				users.addAll(result);
			} 
			users.remove(allId);
			Long allExtId = getReservedGroupId(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID, zoneId);
			if (allowAllUsersGroup && (ids.contains(allExtId) || users.contains(allExtId))) {
				FilterControls filter = new FilterControls("identityInfo.internal", Boolean.FALSE);
				List<Object[]> result = getCoreDao().loadObjects(
						new ObjectControls(User.class, new String[]{ObjectKeys.FIELD_ID}), filter, zoneId);
				//remove users not to be included
				result.remove(getReservedUserId(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zoneId));
				result.remove(getReservedUserId(ObjectKeys.GUEST_USER_INTERNALID, zoneId));
				result.remove(getReservedUserId(ObjectKeys.JOB_PROCESSOR_INTERNALID, zoneId));
				result.remove(getReservedUserId(ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID, zoneId));
				result.remove(getReservedUserId(ObjectKeys.FILE_SYNC_AGENT_INTERNALID, zoneId));
				users.addAll(result);
			} 
			users.remove(allExtId);
			return users;		
    	}
    	finally {
    		end(begin, "explodeGroups(Collection<Long>,<Long>)");
    	}	        
	}
	/**
	 * Get the Membership of a group.  Does not explode nested groups.
	 * Does not load the group object
	 * @param groupId
	 * @result List of <code>Membership</code>
	 */
	@Override
	public List<Long> getMembership(final Long groupId, Long zoneId) {
		long begin = System.nanoTime();
		try {
			if (groupId == null) return new ArrayList();
		    List membership = (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                @Override
					public Object doInHibernate(Session session) throws HibernateException {
	                   	Query query = session.createQuery("from org.kablink.teaming.domain.Membership m where m.groupId=?");
	                   	query.setParameter(0, groupId);
	                    return query.list();
	                 }
	            }
	        );
			return membership;		
    	}
    	finally {
    		end(begin, "getMembership(Long,Long)");
    	}	        
		
	}
	
	@Override
	public List<Long> getOwnedBinders(final Set<Principal> users) {
		long begin = System.nanoTime();
		try {
			if (users == null || (users.size() < 1)) return new ArrayList<Long>();
		    List membership = (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                @Override
					public Object doInHibernate(Session session) throws HibernateException {
	                   	Query query = session.createQuery("select w.id from org.kablink.teaming.domain.Binder w where w.owner in (:pList)")
	                   	.setParameterList("pList", users);
	                    return query.list();
	                 }
	            }
	        );
			return membership;		
    	}
    	finally {
    		end(begin, "getOwnedBinders(Set<Principal>)");
    	}	        
		
	}
	
    //        		Set binderIds = new HashSet(session.createQuery("select w.binderId from org.kablink.teaming.domain.Binder w where w.ownerId in (:pList)")
//            
	/**
	 * Get all groups a principal is a member of, either directly or through nested group
	 * membership.
	 * Use when don't want to load the entire group object
	 * @param principalId
	 * @return Set of groupIds
	 */
	@Override
	public Set<Long> getApplicationLevelGroupMembership(final Long principalId, Long zoneId) {
		long begin = System.nanoTime();
		try {
			if (principalId == null)  return new TreeSet();
			return (Set)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                @Override
					public Object doInHibernate(Session session) throws HibernateException {
	                    Principal principal = (Principal)session.get(Principal.class, principalId);
	                    if (principal == null) {throw new NoPrincipalByTheIdException(principalId);}
	                    Set result = new TreeSet();
	                    Set currentIds = new HashSet();
	                    currentIds.add(principalId);
	                    while (!currentIds.isEmpty()) {
	                    	List mems = session.createCriteria(Membership.class)
	                    					.add(Expression.in("userId", currentIds))
											.list();
	                       	currentIds.clear();
							for (int i=0; i<mems.size(); ++i) {
								Membership m = (Membership)mems.get(i);
								//prevent infinite loops
								if (result.add(m.getGroupId())) {
									currentIds.add(m.getGroupId());
								}
							}                       	
	                    }
	                    return result;
	                }
	            }
	        );
    	}
    	finally {
    		end(begin, "getApplicationLevelGroupMembership(Long,Long)");
    	}	        
	}

	@Override
	public Set<Long> getAllGroupMembership(final Long principalId, Long zoneId) {
		Set<Long> result = getApplicationLevelGroupMembership(principalId, zoneId);
		result.addAll(getMemberOfLdapContainerGroupIds(principalId, zoneId));
		return result;
	}
	
	@Override
	public SeenMap loadSeenMap(Long userId) {
		long begin = System.nanoTime();
		try {
			SeenMap seen = null;
			try {
				seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, userId);
			}
			catch(HibernateSystemException se) {
				if(se.getRootCause() instanceof java.io.EOFException) {
	    			// the data in the blob is corrupt; this most likely means last update failed because
	    			// the data exceeded the size limit - there's no way we can fix up this data, get rid of it
	    			// (this is meant to be one time only cleanup on a system that already has corrupt lobs)
	    			logger.error("Seen map for user " + userId + " seems corrupt: Resetting it to empty value", se);
	    			executeSeenmapDelete(userId);	    								
				}
				else {
					throw se;
				}
			}
	   		if (seen == null) {
	   			seen = new SeenMap(userId);
	   			//quick write
	   			try {
	   				seen = (SeenMap)getCoreDao().saveNewSession(seen);
	   			} catch (Exception ex) {
	   				//contension?
	   				seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, userId);
	   			}
	   		}
	   		return seen;
    	}
    	finally {
    		end(begin, "loadSeenMap(Long)");
    	}	        
	}
	@Override
	public Visits loadVisit(Long userId, EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
	    	UserEntityPK id = new UserEntityPK(userId, entityId);
	        return (Visits)getHibernateTemplate().get(Visits.class, id);	
    	}
    	finally {
    		end(begin, "loadVisit(Long,EntityIdentifier)");
    	}	        
	}
	@Override
	public Rating loadRating(Long userId, EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
	    	UserEntityPK id = new UserEntityPK(userId, entityId);
	        return (Rating)getHibernateTemplate().get(Rating.class, id);	
    	}
    	finally {
    		end(begin, "loadRating(Long,EntityIdentifier)");
    	}	        
	}
	@Override
	public Subscription loadSubscription(Long userId, EntityIdentifier entityId) {
		long begin = System.nanoTime();
		try {
	    	UserEntityPK id = new UserEntityPK(userId, entityId);
	        return (Subscription)getHibernateTemplate().get(Subscription.class, id);	
    	}
    	finally {
    		end(begin, "loadSubscription(Long,EntityIdentifier)");
    	}	        
	}

	/**
	 * Returns a List<Subscription> of the Subscription's associated
	 * with the users from a List<Long> of user IDs.  Optionally only
	 * returns the Subscription's for a given entity.
	 * 
	 * @param userIds	User IDs of the users whose Subscription's are being queried.
	 * @param entityId	Option ID of the entity whose Subscription's are being queried.
	 * @param zoneId	The zone the Subscription's are being queried from. 
	 * 
	 * @return
	 */
	@Override
	public List<Subscription> loadSubscriptions(final Collection<Long> userIds, final Long entityId, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	        List result = (List)getHibernateTemplate().execute(
               	new HibernateCallback() {
                		@Override
						public Object doInHibernate(Session session) throws HibernateException {
                			String queryStr = "from org.kablink.teaming.domain.Subscription where principalId in (:uList)";
                			if (null != entityId) {
                				queryStr += " and entityId=:entityId";
                			}
    	                  	Query q = session.createQuery(queryStr);
    	                  	q.setParameterList("uList", userIds);
    	                  	if (null != entityId) {
    	                  		q.setParameter("entityId", entityId);
    	                  	}
    	                  	q.setCacheable(true);	                			
    	                  	List result = q.list();
    	                  	for (int i = 0; i < result.size(); ) {
    	                  		// Check zone here, so index is used.
    	                  		if (!((Subscription)result.get(i)).getZoneId().equals(zoneId)) {
    	                  			result.remove(i);
    	                  		}
    	                  		else {
    	                  			i += 1;
    	                  		}
    	                  	}
    	                  	return result;
                		}
               	}
            );
	        return result;
    	}
    	finally {
    		end(begin, "loadSubscriptions(Collection<Long>, Long, Long)");
    	}	        
	}
	
	@Override
	public List<Subscription> loadSubscriptions(final Collection<Long> userIds, final Long zoneId) {
		// Always use the initial form of the method.
		return loadSubscriptions(userIds, null, zoneId);	// null -> All subscriptions for all the users.
	}
	
	@Override
	public List<Subscription> loadSubscriptions(final Long userId, final Long entityId, final Long zoneId) {
		// Construct a List<Long> from the user ID...
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		
		// ...and always use the initial form of the method.
		return loadSubscriptions(userIds, entityId, zoneId);
	}
	
	@Override
	public List<Subscription> loadSubscriptions(final Long userId, final Long zoneId) {
		// Always use the previous form of the method.
		return loadSubscriptions(userId, null, zoneId);	// null -> All subscriptions for the user.
	}
	
	//mark entries deleted - used when deleting entries in bulk and want
	//to exclude some from future queries
	//entries evicted from cache
	@Override
	public void markEntriesDeleted(final ProfileBinder binder, final Collection<Principal> entries) {
		long begin = System.nanoTime();
		try {
			if (entries.isEmpty()) return;
			getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session) throws HibernateException {
							Set ids = new HashSet();	               			
							for (Principal p:entries) {
								ids.add(p.getId());
								session.evict(p);
		            	    }
							session.createQuery("Update org.kablink.teaming.domain.Principal set deleted=:delete where id in (:pList)")
							.setBoolean("delete", Boolean.TRUE)
							.setParameterList("pList", ids)
							.executeUpdate();
							return null;
						}
					}
			);    		             		 
    	}
    	finally {
    		end(begin, "markEntriesDeleted(ProfileBinder,Collection<Principal>)");
    	}	        
	}
	@Override
	public Application loadApplication(Long applicationId, Long zoneId) throws NoApplicationByTheIdException {
		long begin = System.nanoTime();
    	try {
    		Application application = (Application)getHibernateTemplate().get(Application.class, applicationId);
    		if (application == null) {throw new NoApplicationByTheIdException(applicationId);}
    		//	make sure from correct zone
    		if (!application.getZoneId().equals(zoneId) || !application.isActive()) {
    			throw new NoApplicationByTheIdException(applicationId);
    		}
    		return application;
    	} catch (ClassCastException ce) {
   			throw new NoApplicationByTheIdException(applicationId);   		
    	}
		finally {
			end(begin, "loadApplication(Long,Long)");
		}	        
	}
	@Override
	public Application loadApplication(Long applicationId, String zoneName) throws NoUserByTheIdException {
		long begin = System.nanoTime();
		try {
			Binder top = getCoreDao().findTopWorkspace(zoneName);
			return loadApplication(applicationId, top.getZoneId());
    	}
    	finally {
    		end(begin, "loadApplication(Long,String)");
    	}	        
	}

    @Override
	public Principal loadPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
		long begin = System.nanoTime();
		try {
	        Principal principal = (Principal)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	//hoping for cache hit
	                    	Principal principal = (Principal)session.get(Principal.class, prinId);
	                        if (principal == null) {throw new NoPrincipalByTheIdException(prinId);}
	                        //Get the real object, not a proxy to abstract class
	                        principal = resolveProxy(session, principal);
	                         //make sure from correct zone
	                        if (!principal.getZoneId().equals(zoneId) ||
	                        		(checkActive && !principal.isActive())) {throw new NoPrincipalByTheIdException(prinId);}
	                        return principal;
	                    }
	                }
	        );
	       
	        principal = filterInaccessiblePrincipal(principal);
	        if (principal == null) 
	        	throw new NoPrincipalByTheIdException(prinId); 
	        return principal;
    	}
    	finally {
    		end(begin, "loadPrincipal(Long,Long,boolean)");
    	}	        
              
    }
    
	@Override
	public List<ApplicationGroup> loadApplicationGroups(Collection<Long> groupsIds, Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadPrincipals(groupsIds, zoneId, ApplicationGroup.class, false, true);
    	}
    	finally {
    		end(begin, "loadApplicationGroups(Collection<Long>,Long)");
    	}	        
	}

	@Override
	public ApplicationPrincipal loadApplicationPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
		long begin = System.nanoTime();
		try {
			ApplicationPrincipal principal = (ApplicationPrincipal)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	//hoping for cache hit
	                    	ApplicationPrincipal principal = (ApplicationPrincipal)session.get(ApplicationPrincipal.class, prinId);
	                        if (principal == null) {throw new NoPrincipalByTheIdException(prinId);}
	                        //Get the real object, not a proxy to abstract class
	                        try {
	                        	principal = (ApplicationPrincipal)session.get(Application.class, prinId);
	                        } catch (Exception ex) {};  // group proxies will force an exception, didn't expect with session.get? 
	                        if (principal==null) 
	                            principal = (ApplicationPrincipal)session.get(ApplicationGroup.class, prinId);
	                        //make sure from correct zone
	                        if (!principal.getZoneId().equals(zoneId) ||
	                        		(checkActive && !principal.isActive())) {throw new NoPrincipalByTheIdException(prinId);}
	                        return principal;
	                    }
	                }
	        );
	       
	        return principal;
    	}
    	finally {
    		end(begin, "loadApplicationPrincipal(Long,Long,boolean)");
    	}	        
	}
	
	@Override
	public List<ApplicationPrincipal> loadApplicationPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<ApplicationPrincipal> result = loadPrincipals(ids, zoneId, ApplicationPrincipal.class, true, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				ApplicationPrincipal p = result.get(i);
				if (!(p instanceof Application) && !(p instanceof ApplicationGroup)) {
					ApplicationPrincipal principal = null;
					try {
						principal = (ApplicationPrincipal)getHibernateTemplate().get(Application.class, p.getId());
					} catch(Exception ex) {}
					if (principal==null) principal = (ApplicationPrincipal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
	   				result.set(i, principal);
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadApplicationPrincipals(Collection<Long>,Long,boolean)");
    	}	        
	}
	
	@Override
	public List loadGroupPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, false, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				Principal p = result.get(i);
				if (!(p instanceof Group) && !(p instanceof ApplicationGroup)) {
					Principal principal = null;
					try {
						principal = (Principal)getHibernateTemplate().get(Group.class, p.getId());
					} catch(Exception ex) {}
					if (principal==null) {
						try {
							principal = (Principal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
						} catch(Exception ex) {}
					}
					if(principal != null) {					
		   				result.set(i, principal);
					}
					else { // what we got is not of the right type
						result.remove(i);
						--i;
					}
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadGroupPrincipals(Collection<Long>,Long,boolean)");
    	}	        
	}
	
	@Override
	public List loadIndividualPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, false, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				Principal p = result.get(i);
				if (!(p instanceof User) && !(p instanceof Application)) {
					Principal principal = null;
					try {
						principal = (Principal)getHibernateTemplate().get(User.class, p.getId());
					} catch(Exception ex) {}
					if (principal==null) {
						try {
							principal = (Principal)getHibernateTemplate().get(Application.class, p.getId());
						} catch(Exception ex) {}
					}
					if(principal != null) {					
		   				result.set(i, principal);
					}
					else { // what we got is not of the right type
						result.remove(i);
						--i;
					}
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadIndividualPrincipals(Collection<Long>,Long,boolean)");
    	}	        
	}

    @Override
	public List<Application> loadApplications(Collection<Long> applicationIds, Long zoneId) {
		long begin = System.nanoTime();
		try {
	    	// If applicationIds is null, load all principals of the specified type rather
	    	// than returning an empty list. This symantics differs from the counterpart 
	    	// methods returning users and groups. I decided to introduce this inconsistency
	    	// rather than risking breaking the existing system by changing the symantics of 
	    	// the other methods for the sake of consistency. 
	    	// 
	    	if(applicationIds != null)
	    		return loadPrincipals(applicationIds, zoneId, Application.class, false, true);
	    	else
	    		return loadAllPrincipals(zoneId, Application.class, true, true);
    	}
    	finally {
    		end(begin, "loadApplications(Collection<Long>,Long)");
    	}	        
    }
    
    @Override
	public List<Principal> loadPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive) {
		long begin = System.nanoTime();
		try {
	    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, true, checkActive);
			//remove proxies
			for (int i=0; i<result.size(); ++i) {
				Principal p = result.get(i);
				if (!(p instanceof User) && !(p instanceof Group) && !(p instanceof Application) && !(p instanceof ApplicationGroup)) {
					Principal principal = null;
					try {
						principal = (Principal)getHibernateTemplate().get(User.class, p.getId());
					} catch(Exception ex) {}
					if (principal==null) {
						try {
							principal = (Principal)getHibernateTemplate().get(Group.class, p.getId());
						} catch(Exception ex) {}
					}
					if (principal==null) {
						try {
							principal = (Principal)getHibernateTemplate().get(Application.class, p.getId());
						} catch(Exception ex) {}
					}
					if (principal==null) principal = (Principal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
	   				result.set(i, principal);
	            }
	        }
			return result;
    	}
    	finally {
    		end(begin, "loadPrincipals(Collection<Long>,Long,boolean)");
    	}	        
	
    }
    
    private List loadAllPrincipals(final Long zoneId, final Class clazz, final boolean cacheable, final boolean checkActive) {
        List result = (List)getHibernateTemplate().execute(
           	new HibernateCallback() {
        		@Override
				public Object doInHibernate(Session session) throws HibernateException {
       					Criteria crit = session.createCriteria(clazz)
    					.add(Expression.eq(ObjectKeys.FIELD_ZONE, zoneId))
    					.setFetchMode("emailAddresses", FetchMode.JOIN)
                		.setCacheable(cacheable);
    					if (checkActive) {
    						crit.add(Expression.eq(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE));
    						crit.add(Expression.eq(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE));
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


    //get list representing entities that have been shared with ids and binderIds
    @Override
	public List<SharedEntity> loadSharedEntities(final Collection ids, final Collection binderIds, final Date after, Long zoneId) {   	
		long begin = System.nanoTime();
		try {
	      	List result = (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(SharedEntity.class)
	                    	.add(Expression.gt("sharedDate", after))
	             			.addOrder(Order.desc("sharedDate"));
	                    	if (!binderIds.isEmpty() && !ids.isEmpty() ) {
	                    		crit.add(Expression.disjunction()
	              					.add(Expression.conjunction()
	              							.add(Expression.in("accessId", ids))
	              							.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_PRINCIPAL)))
	              					.add(Expression.conjunction()
	              							.add(Expression.in("accessId", binderIds))
	              							.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_TEAM))));
	                    	} else if (binderIds.isEmpty()) {
	                    		//not a member of a team
	                    		crit.add(Expression.disjunction()
	                  				.add(Expression.conjunction()
	                  						.add(Expression.in("accessId", ids))
	                  						.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_PRINCIPAL))));
	                        		
	                    	} else {
	                    		//should never happen
	                    		crit.add(Expression.in("accessId", binderIds))
	                  				.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_TEAM));
	                    		
	                    	}
	               			
	                       return crit.list();
	                    }
	                }
	            );
	      	
	       return result;   	
    	}
    	finally {
    		end(begin, "loadSharedEntities(Collection,Collection,Date,Long)");
    	}	              	
    	
    }
    
    //get list representing entities that have been shared with ids and binderIds
    @Override
	public List<Long> getShareItemIdsByEntity(final DefinableEntity entity) {   	
		long begin = System.nanoTime();
		try {
	      	List<Object> list = (List<Object>)getHibernateTemplate().execute(
      			new HibernateCallback() {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException {
                    	// Don't use alias of the first table to refer to property/column name associated with entity, 
                    	// since HQL won't treat it as nicely as it does without alias. 
				   		return session.createQuery("select id from org.kablink.teaming.domain.ShareItem where sharedEntity_type=:sharedEntityType and sharedEntity_id=:sharedEntityId and zoneId=:zoneId")
								.setInteger("sharedEntityType", entity.getEntityIdentifier().getEntityType().getValue())
								.setLong("sharedEntityId", entity.getEntityIdentifier().getEntityId())
								.setLong("zoneId", entity.getZoneId())
								.list();
                	}
      			}
      		);
	      	return shareItemIdResultListToShareItemList(list);
		}
    	finally {
    		end(begin, "getShareItemsByEntity");
    	}	              	
    }
    
	private  List<Long> shareItemIdResultListToShareItemList(List<Object> list) {
      	List<Long> shareItemIds = new ArrayList<Long>();
       	for(Object o:list) {
       		Long shareItemId = (Long) o;
       		shareItemIds.add(shareItemId);
       	}
      	
       	return shareItemIds;
	}

	//Change shareItems to point to a new entity
	@Override
	public void changeSharedEntityId(final Collection<Long> shareItemIds, final DefinableEntity entity) {
		long begin = System.nanoTime();
		try {
			if (shareItemIds.isEmpty()) return;
			getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session) throws HibernateException {
							session.createQuery("Update org.kablink.teaming.domain.ShareItem set sharedEntity_id=:sharedEntityId where sharedEntity_type=:sharedEntityType and id in (:shareItemIds) and zoneId=:zoneId")
							.setLong("sharedEntityId", entity.getId())
							.setInteger("sharedEntityType", entity.getEntityIdentifier().getEntityType().getValue())
							.setParameterList("shareItemIds", shareItemIds)
							.setLong("zoneId", entity.getZoneId())
							.executeUpdate();
							return null;
						}
					}
			);    		             		 
    	}
    	finally {
    		end(begin, "markEntriesDeleted(ProfileBinder,Collection<Principal>)");
    	}	        
	}

    
 	private User findUserByNameDeadOrAlive(final String userName, final Long zoneId) 
	throws NoUserByTheNameException {
    	// STOP!!! THIS METHOD IS FOR INTERNAL/SPECIAL USE ONLY. USE THE REGULAR findUserByName() METHOD INSTEAD.
        return (User)getHibernateTemplate().execute(
                new HibernateCallback() {
                    @Override
					public Object doInHibernate(Session session) throws HibernateException {
                 	   	//only returns active users
                    	Query query = session.getNamedQuery("find-User-Company-DeadOrAlive")
                     		.setString(ParameterNames.USER_NAME, userName.toLowerCase());
                 	   	User user = (User)query
                             		.setLong(ParameterNames.ZONE_ID, zoneId)
                             		.setCacheable(isPrincipalQueryCacheable())
                             		.uniqueResult();
                        //this query doesn't care if user is deleted or disabled
                 	   if (user == null) {
                            throw new NoUserByTheNameException(userName); 
                        }
                        return user;
                    }
                }
             );		
 	}

	//If this user cannot see all users, then filter out the ones not allowed
	@Override
	public Principal filterInaccessiblePrincipal(Principal principal) {
		long begin = System.nanoTime();
		try {
			List principals = new ArrayList();
			principals.add(principal);
			principals = filterInaccessiblePrincipals(principals);
			if (principals.isEmpty()) return null;
			return (Principal)principals.get(0);
    	}
    	finally {
    		end(begin, "filterInaccessiblePrincipal(Principal)");
    	}	        
	}
	@Override
	public List filterInaccessiblePrincipals(List principals) {
		if (RequestContextHolder.getRequestContext() == null) return principals;
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		long begin = System.nanoTime();
		try {
			User user = null;
			try {
				if(RequestContextHolder.getRequestContext() != null)
					user = RequestContextHolder.getRequestContext().getUser();
			} catch(Exception e) {}
			if (user == null) return principals;
	
			// DRF (20141211):  Reworked this to use the Utils method
			// to take advantage of the caching, not the brute force way
			// commented out right below.
			if (!Utils.canUserOnlySeeCommonGroupMembers(user)) {
				return principals;
			}
/*
	      	WorkArea zone = getCoreDao().loadZoneConfig(user.getZoneId());
			AccessControlManager accessControlManager = (AccessControlManager)SpringContextUtil.getBean("accessControlManager");
			try {
				boolean canOnlySeeGroupMembers = accessControlManager.testOperation(user, zone, WorkAreaOperation.ONLY_SEE_GROUP_MEMBERS);
				if (!canOnlySeeGroupMembers || accessControlManager.testOperation(user, zone, WorkAreaOperation.OVERRIDE_ONLY_SEE_GROUP_MEMBERS)) return principals;
			} catch(Exception e) {
				//If any error occurs, just deny access to them all
				return principals;
			}
*/
			
			//This user does not have the right to see all users, so filter out those not allowed
			Set groupIds = getApplicationLevelGroupMembership(user.getId(), RequestContextHolder.getRequestContext().getZoneId());
			List result = new ArrayList();
			for (int i = 0; i < principals.size(); i++) {
				Principal principal = (Principal)principals.get(i);
				if (EntityType.user.equals(principal.getEntityType())) {
					if (user.getId() == principal.getId()) {
						//Always allow a user to see his/her own profile
						if (!result.contains(principal)) result.add(principal);
					} else {
						Set<Long> userGroupIds = getApplicationLevelGroupMembership(principal.getId(), zoneId);
						for (Long id : userGroupIds) {
							if (groupIds.contains(id)) {
								result.add(principal);
								break;
							}
						}
						if (!result.contains(principal)) {
							//The current user cannot see this principal, so add a dummy principal object
							result.add(makeItFake(principal));
						}
					}
				} else {
					result.add(principal);
				}
			}
			return result;
    	}
    	finally {
    		end(begin, "filterInaccessiblePrincipals(List)");
    	}	        
	}
	
	private Principal makeItFake(Principal p) {
		User u = new User((p instanceof UserPrincipal)? ((UserPrincipal)p).getIdentityInfo() : new IdentityInfo());
		u.setId(p.getId());
		u.setZoneId(p.getZoneId());
		u.setParentBinder(p.getParentBinder());
		u.setName(FAKE_NAME_PREFIX + p.getId().toString());
		String title = NLT.get("user.redacted.title");
		u.setLastName(title);
		u.setDescription("");
		u.setEntryDefId(p.getEntryDefId());
		u.setWorkspaceId(p.getWorkspaceId());
		u.setInternalId(p.getInternalId());
		u.setIdentityInfo( p.getIdentityInfo() );
		u.setEmailAddress( p.getEmailAddress() );
		if ( p instanceof User )
		{
			u.setExtProvState( ((User) p).getExtProvState() );
			u.setExtProvSeed( ((User) p).getExtProvSeed() );
		}
		
		return u;
	}
	
	//Initialize (or reset) the disk space used by each user
	@Override
	public void resetDiskUsage(Long zone) {
		long begin = System.nanoTime();
		try {
			final Long zoneId = zone;
			getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session) throws HibernateException {
							String sql ="Update SS_Principals " +
										"SET diskSpaceUsed = " + 
										"COALESCE( (SELECT SUM(fileLength) FROM SS_Attachments b " +
										"WHERE b.creation_principal = SS_Principals.id " + 
										" AND SS_Principals.type = 'user' " +
										" AND SS_Principals.zoneId = " + zoneId +
										" AND b.versionNumber > 0" +
										" AND b.zoneId = " + zoneId +
										" AND b.repositoryName != \'" + ObjectKeys.FI_ADAPTER + "\'), 0)" +
										" WHERE zoneId = " + zoneId;
							session.createSQLQuery(sql).executeUpdate();
							return null;
						}
					}
			);    		             		 
    	}
    	finally {
    		end(begin, "filterInaccessiblePrincipals(List)");
    	}	        
	}

	// this returns non-zero quotas (any quota which has been set by the admin)
	@Override
	public List getNonDefaultQuotas(String type, final long zoneId) {
		long begin = System.nanoTime();
		try {
			final String principalType = type;
			List userList = new ArrayList();
			List results = null;
			results = (List) getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session)
								throws HibernateException {
							
							String sql = "Select w.id "
								+ " FROM org.kablink.teaming.domain.UserPrincipal w "
								+ " WHERE w.diskQuota IS NOT NULL "
								+ " AND w.diskQuota != 0 "
								+ " AND w.type = :principalType" 
								+ " AND w.zoneId = :zoneId";
	
							Query query = session.createQuery(sql)
		                   	.setString("principalType", principalType)
		                   	.setLong("zoneId", zoneId);
							
							List l = null;
							l = query.list();
							return l;
						}
					});
	
			for (int i = 0; i < results.size(); i++) {
				userList.add(results.get(i));
			}
	
			return userList;
    	}
    	finally {
    		end(begin, "getNonDefaultQuotas(String,long)");
    	}	        
	}

	// this returns non-zero file size limits (any limit which has been set by the admin)
	@Override
	public List getNonDefaultFileSizeLimits(String type, final long zoneId) {
		long begin = System.nanoTime();
		try {
			final String principalType = type;
			List userList = new ArrayList();
			List results = null;
			results = (List) getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session)
								throws HibernateException {
							
							String sql = "Select w.id "
								+ " FROM org.kablink.teaming.domain.UserPrincipal w "
								+ " WHERE w.fileSizeLimit IS NOT NULL "
								+ " AND w.type = :principalType" 
								+ " AND w.zoneId = :zoneId";
	
							Query query = session.createQuery(sql)
		                   	.setString("principalType", principalType)
		                   	.setLong("zoneId", zoneId);
							
							List l = null;
							l = query.list();
							return l;
						}
					});
	
			for (int i = 0; i < results.size(); i++) {
				userList.add(results.get(i));
			}
	
			return userList;
    	}
    	finally {
    		end(begin, "getNonDefaultQuotas(String,long)");
    	}	        
	}

	// This returns a list of all disabled user accounts
	@Override
	public List<Long> getDisabledUserAccounts(final long zoneId) {
		long begin = System.nanoTime();
		try {
			final String principalType = ObjectKeys.PRINCIPAL_TYPE_USER;
			List<Long> userList = new ArrayList<Long>();
			List results = null;
			results = (List) getHibernateTemplate().execute(
					new HibernateCallback() {
						@Override
						public Object doInHibernate(Session session)
								throws HibernateException {
							
							String sql = "Select w.id "
								+ " FROM org.kablink.teaming.domain.UserPrincipal w "
								+ " WHERE w.disabled = true "
								+ " AND w.deleted = false "
								+ " AND w.type = :principalType" 
								+ " AND w.zoneId = :zoneId";
	
							Query query = session.createQuery(sql)
		                   	.setString("principalType", principalType)
		                   	.setLong("zoneId", zoneId);
							
							List l = null;
							l = query.list();
							return l;
						}
					});
	
			for (int i = 0; i < results.size(); i++) {
				userList.add((Long) results.get(i));
			}
	
			return userList;
    	}
    	finally {
    		end(begin, "getDisabledUserAccounts(long)");
    	}	        
	}
	
	@Override
 	public ShareItem loadShareItem(Long shareItemId) {
		if(shareItemId == null)
			throw new IllegalArgumentException("id must be specified");
		long begin = System.nanoTime();
		try {
			ShareItem shareItem = (ShareItem)getHibernateTemplate().get(ShareItem.class, shareItemId);
			if (shareItem == null) {
				throw new NoShareItemByTheIdException(shareItemId);
			}
			return shareItem;
		}
		finally {
			end(begin, "loadShareItem(Long)");
		}	        
 	}
 	
	@Override
 	public List<ShareItem> loadShareItems(final Collection<Long> shareItemIds) {
		if(shareItemIds == null || shareItemIds.isEmpty())
			throw new IllegalArgumentException("ids must be specified");
		long begin = System.nanoTime();
		try {
	        return (List)getHibernateTemplate().execute(
		            new HibernateCallback() {
		                    @Override
							public Object doInHibernate(Session session) throws HibernateException {
	                            return session.createCriteria(ShareItem.class)
	                            	.add(Restrictions.in("id", shareItemIds))
	                            	.list();
		                    }
		            }
		        );
		}
		finally {
			end(begin, "loadShareItems(Collection<Long>)");
		}	        
 	}
 	 	
	@Override
 	public Map<ShareItem.RecipientType, Set<Long>> getRecipientIdsWithGrantedRightToSharedEntity(final EntityIdentifier sharedEntityIdentifier, final String rightName, final Long zoneId) {
		if(sharedEntityIdentifier == null)
			throw new IllegalArgumentException("shared entity identifier must be specified");
		long begin = System.nanoTime();
		try {
	      	List<Object[]> list = (List<Object[]>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	// Don't use alias of the first table to refer to property/column name associated with entity, 
	                    	// since HQL won't treat it as nicely as it does without alias. 
                    		return session.createQuery("select distinct recipientType, recipientId from org.kablink.teaming.domain.ShareItem where latest=:latestValue and sharedEntity_type=:sharedEntityType and sharedEntity_id=:sharedEntityId and rightSet." + rightName + "=:rightValue")
                    				.setBoolean("latestValue", true)
                    				.setInteger("sharedEntityType", sharedEntityIdentifier.getEntityType().getValue())
                    				.setLong("sharedEntityId", sharedEntityIdentifier.getEntityId())
                    				.setBoolean("rightValue", true)
                    				.setLong(ObjectKeys.FIELD_ZONE, zoneId)
                    				.list();
	                    }
	                }
	            );
	      	return recipientResultListToMap(list);
    	}
    	finally {
    		end(begin, "getMemberIdsWithReadAccessToSharedEntity(EntityIdentifier,String)");
    	}	              	
	}
	
	private  Map<ShareItem.RecipientType, Set<Long>> recipientResultListToMap(List<Object[]> list) {
      	Map<ShareItem.RecipientType, Set<Long>> result = new HashMap<ShareItem.RecipientType, Set<Long>>();
      	result.put(ShareItem.RecipientType.user, new TreeSet<Long>());
      	result.put(ShareItem.RecipientType.group, new TreeSet<Long>());
      	result.put(ShareItem.RecipientType.team, new TreeSet<Long>());
      	Short recipientType;
      	Long recipientId;
       	for(Object[] o:list) {
       		recipientType = (Short) o[0];
       		recipientId = (Long) o[1];
       		try {
       			result.get(ShareItem.RecipientType.valueOf(recipientType)).add(recipientId);
       		}
       		catch(Exception e) {
       			// This means that we encountered an invalid recipient type value in the database. Skip it.
       		}
       	}
       	return result;
	}

	private Set<Long> sharerResultListToList(List<Long> list) {
      	Set<Long> result = new TreeSet<Long>();
       	for(Long sharerId : list) {
       		result.add(sharerId);
       	}
       	return result;
	}

	@Override
 	public Map<ShareItem.RecipientType, Set<Long>> getRecipientIdsWithGrantedRightToSharedEntities(final Collection<EntityIdentifier> sharedEntityIdentifiers, final String rightName) {
		return getRecipientIdsWithGrantedRightsToSharedEntities(sharedEntityIdentifiers, new String[]{rightName});
	}

	@Override
 	public Map<ShareItem.RecipientType, Set<Long>> getRecipientIdsWithGrantedRightsToSharedEntities(final Collection<EntityIdentifier> sharedEntityIdentifiers, final String[] rightNames) {
		if(sharedEntityIdentifiers == null || sharedEntityIdentifiers.isEmpty())
			throw new IllegalArgumentException("shared entity identifiers must be specified");
		long begin = System.nanoTime();
		try {
	      	List<Object[]> list = (List<Object[]>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(ShareItem.class);
	                    	crit.setProjection(Projections.projectionList()
	                    			.add(Projections.property("recipientType"))
	                    			.add(Projections.property("recipientId")));
	                    	crit.add(Restrictions.eq("latest", Boolean.TRUE));
	                    	crit.add(Restrictions.isNull("deletedDate"));
                    		org.hibernate.criterion.Disjunction disjunction = Restrictions.disjunction();
                    		disjunction.add(Restrictions.isNull("endDate"));
                    		disjunction.add(Restrictions.gt("endDate", new Date()));
                    		crit.add(disjunction);
	                    	if(rightNames.length > 0) {
		                    	disjunction = Restrictions.disjunction();
		                    	for(String rightName:rightNames)
		                    		disjunction.add(Restrictions.eq("rightSet."+rightName, Boolean.TRUE));
		                    	crit.add(disjunction);
	                    	}
	                    	if(sharedEntityIdentifiers.size() > 0) {
		                    	disjunction = Restrictions.disjunction();
		                    	for(EntityIdentifier sharedEntityIdentifier:sharedEntityIdentifiers)
		                    		disjunction.add(Restrictions.conjunction()
		                    				.add(Restrictions.eq("sharedEntityIdentifier.type", sharedEntityIdentifier.getEntityType().getValue()))
		                    				.add(Restrictions.eq("sharedEntityIdentifier.entityId", sharedEntityIdentifier.getEntityId())));
		                    	crit.add(disjunction);
	                    	}
	                    	return crit.list();
	                    }
	                }
	            );
	      	
	      	return recipientResultListToMap(list);
    	}
    	finally {
    		end(begin, "getRecipientIdsWithGrantedRightsToSharedEntities(Collection<EntityIdentifier>,String[])");
    	}	              	
	}

	@Override
 	public Set<Long> getSharerIdsToSharedEntities(final Collection<EntityIdentifier> sharedEntityIdentifiers) {
		if(sharedEntityIdentifiers == null || sharedEntityIdentifiers.isEmpty())
			throw new IllegalArgumentException("shared entity identifiers must be specified");
		long begin = System.nanoTime();
		try {
	      	List<Long> list = (List<Long>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(ShareItem.class);
	                    	crit.setProjection(Projections.projectionList()
	                    			.add(Projections.property("sharerId")));
	                    	crit.add(Restrictions.eq("latest", Boolean.TRUE));
	                    	crit.add(Restrictions.isNull("deletedDate"));
                    		org.hibernate.criterion.Disjunction disjunction = Restrictions.disjunction();
                    		disjunction.add(Restrictions.isNull("endDate"));
                    		disjunction.add(Restrictions.gt("endDate", new Date()));
                    		crit.add(disjunction);
	                    	if(sharedEntityIdentifiers.size() > 0) {
		                    	disjunction = Restrictions.disjunction();
		                    	for(EntityIdentifier sharedEntityIdentifier:sharedEntityIdentifiers)
		                    		disjunction.add(Restrictions.conjunction()
		                    				.add(Restrictions.eq("sharedEntityIdentifier.type", sharedEntityIdentifier.getEntityType().getValue()))
		                    				.add(Restrictions.eq("sharedEntityIdentifier.entityId", sharedEntityIdentifier.getEntityId())));
		                    	crit.add(disjunction);
	                    	}
	                    	return crit.list();
	                    }
	                }
	            );
	      	
	      	return sharerResultListToList(list);
    	}
    	finally {
    		end(begin, "getSharerIdsToSharedEntities(Collection<EntityIdentifier>)");
    	}	              	
	}

 	@Override
	public List<ShareItem> findShareItems(final ShareItemSelectSpec selectSpec, final Long zoneId) {
 		// This method utilizes sub-criteria which requires relationship to be expressed using
 		// association rather than collection of values.
		long begin = System.nanoTime();
		try {
	      	List result = (List)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(ShareItem.class);
	                    	if(selectSpec.latest != null) {
	                    		crit.add(Restrictions.eq("latest", selectSpec.latest));
	                    	}
	                    	if(selectSpec.sharerIds != null && !selectSpec.sharerIds.isEmpty()) {
	                    		org.hibernate.criterion.Disjunction disjunction = Restrictions.disjunction();
	                    		for(Long sharerId:selectSpec.sharerIds)
	                    			disjunction.add(Restrictions.eq("sharerId", sharerId));
	                    		crit.add(disjunction);
	                    	}
	                    	if(selectSpec.sharedEntityIdentifiers != null && !selectSpec.sharedEntityIdentifiers.isEmpty()) {
	                    		org.hibernate.criterion.Disjunction disjunction = Restrictions.disjunction();                    		
	                    		for(EntityIdentifier sharedEntityIdentifier:selectSpec.sharedEntityIdentifiers) {
	                    			disjunction.add(Restrictions.conjunction()
		                    		.add(Restrictions.eq("sharedEntityIdentifier.type", sharedEntityIdentifier.getEntityType().getValue()))
		                    		.add(Restrictions.eq("sharedEntityIdentifier.entityId", sharedEntityIdentifier.getEntityId())));
	                    		}
	                    		crit.add(disjunction);
	                    	}
	                    	if(selectSpec.startDateMin != null) {
	                    		if(selectSpec.startDateMinInclusive)
	                    			crit.add(Restrictions.ge("startDate", selectSpec.startDateMin));
	                    		else
	                    			crit.add(Restrictions.gt("startDate", selectSpec.startDateMin));
	                    	}
	                    	if(selectSpec.startDateMax != null) {
	                    		if(selectSpec.startDateMaxInclusive)
	                    			crit.add(Restrictions.le("startDate", selectSpec.startDateMax));
	                    		else
	                    			crit.add(Restrictions.lt("startDate", selectSpec.startDateMax));
	                    	}
                            if(selectSpec.deleted != null) {
                                if (selectSpec.deleted) {
                                    crit.add(Restrictions.isNotNull("deletedDate"));
                                } else {
                                    crit.add(Restrictions.isNull("deletedDate"));
                                }
                            }
                            if (selectSpec.recipientType != null) {
                                crit.add(Restrictions.eq("recipientType", selectSpec.recipientType.getValue()));
                            }
	                    	if(selectSpec.orderByFieldName != null) {
	                    		if(selectSpec.orderByDescending)
	                    			crit.addOrder(Order.desc(selectSpec.orderByFieldName));
	                    		else
	                    			crit.addOrder(Order.asc(selectSpec.orderByFieldName));
	                    	}
	                    	
	                    	if(selectSpec.commentLikes != null && selectSpec.commentLikes.length > 0) {
	                    		org.hibernate.criterion.Junction junction;
	                    		if(selectSpec.commentLikesDisjunctive) 
	                    			junction =  Restrictions.disjunction(); 
	                    		else
	                    			junction =  Restrictions.conjunction();
	                    		for(String commentLike:selectSpec.commentLikes) {
	                    			junction.add(Restrictions.like("comment", commentLike, MatchMode.ANYWHERE));
	                    		}
	                    		crit.add(junction);
	                    	}
	                    	
	                    	if(selectSpec.endDateMin != null) {
	                    		org.hibernate.criterion.Disjunction disjunction = Restrictions.disjunction();
	                    		disjunction.add(Restrictions.isNull("endDate"));
	                    		if(selectSpec.endDateMinInclusive)
	                    			disjunction.add(Restrictions.ge("endDate", selectSpec.endDateMin));
	                    		else
	                    			disjunction.add(Restrictions.gt("endDate", selectSpec.endDateMin));
	                    		crit.add(disjunction);
	                    	}
	                    	if(selectSpec.endDateMax != null) {
	                    		org.hibernate.criterion.Conjunction conjunction = Restrictions.conjunction();
	                    		conjunction.add(Restrictions.isNotNull("endDate"));
	                    		if(selectSpec.endDateMaxInclusive)
	                    			conjunction.add(Restrictions.le("endDate", selectSpec.endDateMax));
	                    		else
	                    			conjunction.add(Restrictions.lt("endDate", selectSpec.endDateMax));
	                    		crit.add(conjunction);
	                    	}
	                    	if((selectSpec.recipientUserIds != null && !selectSpec.recipientUserIds.isEmpty()) ||
	                    			(selectSpec.recipientGroupIds != null && !selectSpec.recipientGroupIds.isEmpty()) ||
	                    			(selectSpec.recipientTeamIds != null && !selectSpec.recipientTeamIds.isEmpty())) {
	                    		org.hibernate.criterion.Disjunction disjunction = Restrictions.disjunction();
	                    		if(selectSpec.recipientUserIds != null && !selectSpec.recipientUserIds.isEmpty()) {
	                    			disjunction.add(Restrictions.conjunction()
	              							.add(Restrictions.in("recipientId", selectSpec.recipientUserIds))
	              							.add(Restrictions.eq("recipientType", ShareItem.RecipientType.user.getValue())));
	                    		}
	                    		if(selectSpec.recipientGroupIds != null && !selectSpec.recipientGroupIds.isEmpty()) {
	                    			disjunction.add(Restrictions.conjunction()
	              							.add(Restrictions.in("recipientId", selectSpec.recipientGroupIds))
	              							.add(Restrictions.eq("recipientType", ShareItem.RecipientType.group.getValue())));
	                    		}
	                    		if(selectSpec.recipientTeamIds != null && !selectSpec.recipientTeamIds.isEmpty()) {
	                    			disjunction.add(Restrictions.conjunction()
	              							.add(Restrictions.in("recipientId", selectSpec.recipientTeamIds))
	              							.add(Restrictions.eq("recipientType", ShareItem.RecipientType.team.getValue())));
	                    		}
	                    		crit.add(disjunction);
	                    	}	               			
	                    	if(selectSpec.onRights != null && !selectSpec.onRights.isEmpty()) {
	                    		org.hibernate.criterion.Junction junction;
	                    		if(selectSpec.onRightsDisjunctive)
	                    			junction = Restrictions.disjunction();
	                    		else
	                    			junction = Restrictions.conjunction();
	                    		for(String rightName:selectSpec.onRights)
	                    			junction.add(Restrictions.eq("rightSet." + rightName, true));
	                    		crit.add(junction);
	                    	}
	                    	crit.add(Restrictions.eq(ObjectKeys.FIELD_ZONE, zoneId));
	                    	return crit.list();
	                    }
	                }
	            );
	      	
	       return result;   	
    	}
    	finally {
    		end(begin, "findShareItems(ShareItemSelectSpec)");
    	}	              	
 	}

 	@Override
 	public List<ShareItem> findExpiredAndNotYetHandledShareItems() {
		long begin = System.nanoTime();
		try {
	      	List<ShareItem> result = (List<ShareItem>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                   		org.hibernate.criterion.Disjunction disjunction = Restrictions.disjunction();                    		
                    		disjunction.add(Restrictions.isNull("expirationHandled"))
	                    	.add(Restrictions.eq("expirationHandled", false));
	                    	Criteria crit = session.createCriteria(ShareItem.class)
	                    			.add(Restrictions.eq("latest", Boolean.TRUE))
	                    			.add(Restrictions.lt("endDate", new Date()))
	                    			.add(disjunction);
	                    	return crit.list();
	                    }
	                }
	            );
	      	return result;
    	}
    	finally {
    		end(begin, "findExpiredAndNotYetHandledShareItems()");
    	}	              	

 	}
 	
 	@Override
 	public List<Principal> getLdapContainerGroupMembers(final Group containerGroup) {
		long begin = System.nanoTime();
		try {
	      	List<Principal> result = (List<Principal>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
	                    	Criteria crit = session.createCriteria(UserPrincipal.class)
	                    			.add(Restrictions.eq("zoneId", containerGroup.getZoneId()))
	                    			.add(Restrictions.eq("fromLdap", Boolean.TRUE))
	                    			.add(Restrictions.ne("ldapContainer", Boolean.TRUE))
	                    			.add(Restrictions.like("foreignName", "," + containerGroup.getForeignName(), MatchMode.END))
	                    			.add(Restrictions.ne("deleted", Boolean.TRUE))
	                    			.add(Restrictions.ne("disabled", Boolean.TRUE));
	                    	return crit.list();
	                    }
	                }
	            );
	      	return result;
    	}
    	finally {
    		end(begin, "loadLDAPContainerGroupMembers()");
    	}	              	
 	}
 	
 	@Override
 	public List<Long> getMemberOfLdapContainerGroupIds(final Long principalId, final Long zoneId) {
		long begin = System.nanoTime();
		try {
	      	List<Long> result = (List<Long>)getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    @Override
						public Object doInHibernate(Session session) throws HibernateException {
		                    Principal principal = (Principal)session.get(Principal.class, principalId);
		            		final String[] foreignNames = principal.getLdapContainerForeignNames();
		            		if(foreignNames.length == 0)
		            			return new ArrayList<Long>();
	                    	Criteria crit = session.createCriteria(UserPrincipal.class)
	                    			.setProjection(Projections.property("id"))
	                    			.add(Restrictions.eq("zoneId", zoneId))
	                    			.add(Restrictions.eq("ldapContainer", Boolean.TRUE))
	                    			.add(Restrictions.in("foreignName", foreignNames))
	                    			.add(Restrictions.ne("deleted", Boolean.TRUE))
	                    			.add(Restrictions.ne("disabled", Boolean.TRUE))
	                    			.setCacheable(isPrincipalQueryCacheable());
	                    	return crit.list();
	                    }
	                }
	            );
	      	return result;
    	}
    	finally {
    		end(begin, "loadLDAPContainerGroups()");
    	}	              	
 	}

    @Override
	public List<Group> loadLdapContainerGroups(Long zoneId) {
		long begin = System.nanoTime();
		try {
			return loadPrincipals(new FilterControls("ldapContainer", true), zoneId, Group.class);
    	}
    	finally {
    		end(begin, "loadLdapContainerGroups(Long)");
    	}	        
    }
}
