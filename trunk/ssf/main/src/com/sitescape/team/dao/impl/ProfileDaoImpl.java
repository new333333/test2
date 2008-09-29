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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.LongIdComparator;
import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.ObjectControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.ApplicationGroup;
import com.sitescape.team.domain.ApplicationPrincipal;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.EmailAddress;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.GroupPrincipal;
import com.sitescape.team.domain.IndividualPrincipal;
import com.sitescape.team.domain.Membership;
import com.sitescape.team.domain.NoApplicationByTheIdException;
import com.sitescape.team.domain.NoGroupByTheIdException;
import com.sitescape.team.domain.NoGroupByTheNameException;
import com.sitescape.team.domain.NoPrincipalByTheIdException;
import com.sitescape.team.domain.NoPrincipalByTheNameException;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.Rating;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.SharedEntity;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserEntityPK;
import com.sitescape.team.domain.UserPrincipal;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.UserPropertiesPK;
import com.sitescape.team.domain.Visits;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.SPropsUtil;
/**
 * @author Jong Kim
 *
 */
public class ProfileDaoImpl extends HibernateDaoSupport implements ProfileDao {
	protected int inClauseLimit=1000;
	protected Log logger = LogFactory.getLog(getClass());
	private CoreDao coreDao;
	Map reservedIds = new HashMap();
	
	public void setCoreDao(CoreDao coreDao) {
		   this.coreDao = coreDao;
		}
	private CoreDao getCoreDao() {
	    return coreDao;
	}
    /**
     * Called after bean is initialized.  
     */
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
	protected synchronized void setReservedId(String internalId, Long zoneId, Long id) {
		String key = internalId + "-" + zoneId;
   		reservedIds.put(key, id);	
	}
	
	/**
	 * Delete the binder object and its assocations.
	 * Child binders should already have been deleted
	 * This code assumes all users/groups are stored
	 */	
	public void delete(final ProfileBinder binder) {
		//cleanup entries - the delete of the folder in coreDao will handle associations through owningBinderId + LibraryEntries
	    getHibernateTemplate().execute(
	    	new HibernateCallback() {
	       		public Object doInHibernate(Session session) throws HibernateException {
    	   			Statement s = null;
       	   			try {
       	   				s = session.connection().createStatement();
      	   				s.executeUpdate("delete from SS_WorkAreaFunctionMembers where memberId in " + 
      	   						"(select p.id from SS_Principals p where  p.parentBinder=" + binder.getId() + ")");
      	   				s.executeUpdate("delete from SS_Notifications where principalId in " + 
      	   						"(select p.id from SS_Principals p where  p.parentBinder=" + binder.getId() + ")");
     	   			} catch (SQLException sq) {
       	   				throw new HibernateException(sq);
      	   			} finally {
       	   				try {if (s != null) s.close();} catch (Exception ex) {};
       	   			}
 		   			session.createQuery("Delete com.sitescape.team.domain.Membership where groupId in " +
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
			  				" p.parentBinder=:profile)")
				   			.setEntity("profile", binder)
	 		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.team.domain.Membership where userId in " +
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
			  				" p.parentBinder=:profile)")
				   			.setEntity("profile", binder)
 	 		   				.executeUpdate();
      	   			//If we are here the user space is being deleted, so get rid of things they own
		   			session.createQuery("Delete com.sitescape.team.domain.UserProperties where zoneId=" + binder.getZoneId())
			         	.executeUpdate();
		   			session.createQuery("Delete com.sitescape.team.domain.SeenMap where zoneId=" + binder.getZoneId())
			         	.executeUpdate();
		   			//delete ratings/visits owned or on hese principals
		   			session.createQuery("Delete com.sitescape.team.domain.Rating where zoneId=" + binder.getZoneId())
		   				.executeUpdate();
		   			//delete subscriptions owned by these principals
		   			session.createQuery("Delete com.sitescape.team.domain.Subscription where zoneId=" + binder.getZoneId())
		   				.executeUpdate();
		   			//delete tags owned or on these principals
		   			session.createQuery("Delete com.sitescape.team.domain.Tag where zoneId=" + binder.getZoneId())
		   				.executeUpdate();

		   			//delete dahsboards in Zone = not totally correct, but if we are here the zone is being deleted
		   			session.createQuery("Delete com.sitescape.team.domain.Dashboard where zoneId=" + binder.getZoneId())
		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.team.domain.EmailAddress where zoneId=" + binder.getZoneId())
		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.team.domain.SharedEntity where zoneId=" + binder.getZoneId())
	   				.executeUpdate();
		   		/*Pre zoneId on each item
  
 		   			session.createQuery("Delete com.sitescape.team.domain.SeenMap where principalId in " + 
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
 		   					" p.parentBinder=:profile)")
		   			  	.setEntity("profile", binder)
		   				.executeUpdate();

		   			session.createQuery("Delete com.sitescape.team.domain.UserProperties where principalId in " +
			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
			  				" p.parentBinder=:profile)")
 				   			.setEntity("profile", binder)
 				         	.executeUpdate();
		   			//delete ratings/visits owned by these principals
 		   			session.createQuery("Delete com.sitescape.team.domain.Rating where principalId in " + 
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
		   			  			" p.parentBinder=:profile)")
				   		.setEntity("profile", binder)
		   				.executeUpdate();
		   			//delete subscriptions owned by these principals
 		   			session.createQuery("Delete com.sitescape.team.domain.Subscription where principalId in " + 
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
		   			  			" p.parentBinder=:profile)")
				   		.setEntity("profile", binder)
		   				.executeUpdate();

		   			List types = new ArrayList();
	       			types.add(EntityIdentifier.EntityType.user.getValue());
	       			types.add(EntityIdentifier.EntityType.group.getValue());

	       			//delete subscriptions to these principals
 		   			session.createQuery("Delete com.sitescape.team.domain.Subscription where entityId in " + 
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
		   			  			" p.parentBinder=:profile) and entityType in (:tList)")
				   		.setEntity("profile", binder)
	   			  		.setParameterList("tList", types)
		   				.executeUpdate();
 	       			
		   			//delete ratings/visits for these principals
 		   			session.createQuery("Delete com.sitescape.team.domain.Rating where entityId in " + 
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
		   			  			" p.parentBinder=:profile) and entityType in (:tList)")
			   			.setEntity("profile", binder)
	   			  		.setParameterList("tList", types)
	   			  		.executeUpdate();
	     	   		//delete dashboards owned
	     	   		session.createQuery("Delete com.sitescape.team.domain.Dashboard where owner_id in " +
			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
	   			  			" p.parentBinder=:profile) and owner_type in (:tList)")
 			   			.setEntity("profile", binder)
	   					.setParameterList("tList", types)
	     	   			.executeUpdate();
		   			//delete tags owned by these users
 		   			session.createQuery("Delete com.sitescape.team.domain.Tag where owner_id in " + 
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
		   			  			" p.parentBinder=:profile) and owner_type in (:tList)")
		   			  	.setEntity("profile", binder)
		   			  	.setParameterList("tList", types)
		   				.executeUpdate();
	       			
		   			//delete tags for these principals - not likely to exist
 		   			session.createQuery("Delete com.sitescape.team.domain.Tag where entity_id in " + 
 			   				"(select p.id from com.sitescape.team.domain.Principal p where " +
			  			" p.parentBinder=:profile) and entity_type in (:tList)")
		   			  	.setEntity("profile", binder)
		   			  	.setParameterList("tList", types)
		   				.executeUpdate();
 */
		   			session.createQuery("Update com.sitescape.team.domain.Principal set creation.principal=null, modification.principal=null " +
 		   					"where parentBinder=:profile")
 		   			  	.setEntity("profile", binder)
		   				.executeUpdate();
 		   			session.createQuery("Update com.sitescape.team.domain.Binder set creation.principal=null, modification.principal=null, owner=null, entryDef=null " +
	   					"where id=" + binder.getId())
	   			  		.executeUpdate();
 		   			
 		   			//the delete of the binder in coreDao will handle associations through owningBinderId + LibraryEntries + entries
 		   			getCoreDao().delete((Binder)binder, Principal.class);			
	       	   		return null;
	       		}
	       	}
	    );  

	    	
	 }
	 /**
     * Delete an object and its assocations 
     * @param entry
     */
    public void delete(Principal entry) {
    	List entries = new ArrayList();
    	entries.add(entry);
    	deleteEntries(entries);
             	
     } 
    /*
     * Delete a list of entries
     * We only actually delete groups.  Users are left forever, but
     * there associations are deleted
     */
    public void deleteEntries(final Collection<Principal> entries) {
    	if (entries == null || entries.size() == 0) return;
      	getHibernateTemplate().execute(
        	   	new HibernateCallback() {
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
      	   				s.executeUpdate("delete from SS_WorkAreaFunctionMembers where memberId in (" + inList.toString() + ")");
      	   				s.executeUpdate("delete from SS_Notifications where principalId in (" + inList.toString() + ")");
       	   			} catch (SQLException sq) {
       	   				throw new HibernateException(sq);
     	   			} finally {
       	   				try {if (s != null) s.close();} catch (Exception ex) {};
       	   			}
        			//need to use ownerId, cause versionattachments/customattributeList sets not indexed by principal
		   			getCoreDao().deleteEntityAssociations("ownerId in (" + inList.toString() + ") and (ownerType='" +
		   					EntityType.user.name() + "' or ownerType='" + EntityType.group.name() + "')");
		   			session.createQuery("Delete com.sitescape.team.domain.SeenMap where principalId in (:pList)")
		   				.setParameterList("pList", ids)
       	   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.team.domain.Membership where userId in (:uList) or groupId in (:gList)")
		   				.setParameterList("uList", ids)
		   				.setParameterList("gList", ids)
       	   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.team.domain.UserProperties where principalId in (:pList)")
	   					.setParameterList("pList", ids)
	   					.executeUpdate();

		   			//delete ratings/visits owned by these users
 		   			session.createQuery("Delete com.sitescape.team.domain.Rating where principalId in (:pList)")
	   					.setParameterList("pList", ids)
		   				.executeUpdate();

		   			//delete subscriptions owned by these users
 		   			session.createQuery("Delete com.sitescape.team.domain.Subscription where principalId in (:pList)")
	   					.setParameterList("pList", ids)
		   				.executeUpdate();

		   			//delete email addresses associated with these users
 		   			session.createQuery("Delete com.sitescape.team.domain.EmailAddress where principal in (:pList)")
	   					.setParameterList("pList", entries)
		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.team.domain.SharedEntity where accessId in (:pList) and accessType=:accessType")
	   					.setParameterList("pList", entries)
	   				.setParameter("accessType", SharedEntity.ACCESS_TYPE_PRINCIPAL)
	   				.executeUpdate();

 		   			List types = new ArrayList();
	       			types.add(EntityIdentifier.EntityType.user.getValue());
	       			types.add(EntityIdentifier.EntityType.group.getValue());
	       			//delete subscriptions to these principals - not likely to exist
 		   			session.createQuery("Delete com.sitescape.team.domain.Subscription where entityId in (:pList) and entityType in (:tList)")
 	   					.setParameterList("pList", ids)
	   					.setParameterList("tList", types)
		   				.executeUpdate();
 	       			
		   			//delete ratings/visits at these principals - not likely to exist
 		   			session.createQuery("Delete com.sitescape.team.domain.Rating where entityId in (:pList) and entityType in (:tList)")
 	   					.setParameterList("pList", ids)
	   					.setParameterList("tList", types)
		   				.executeUpdate();
	     	   		//delete dashboards owned
	     	   		session.createQuery("Delete com.sitescape.team.domain.Dashboard where owner_id in (:pList) and owner_type in (:tList)")
 	   					.setParameterList("pList", ids)
	   					.setParameterList("tList", types)
	     	   			.executeUpdate();

 		   			//delete tags owned by this entity
 		   			session.createQuery("Delete com.sitescape.team.domain.Tag where owner_id in (:pList) and owner_type in (:tList)")
	   					.setParameterList("pList", ids)
	   					.setParameterList("tList", types)
	   					.executeUpdate();
 		   			//delete tags on this entity - not likely to exist
		   			session.createQuery("Delete com.sitescape.team.domain.Tag where entity_id in (:pList) and entity_type in (:tList)")
   						.setParameterList("pList", ids)
   						.setParameterList("tList", types)
   						.executeUpdate();
 		   			//only delete groups; leave users around
 		   			session.createQuery("Delete com.sitescape.team.domain.Principal where id in (:pList) and type='group'")
            			.setParameterList("pList", ids)
       	   				.executeUpdate();
 		   			//leave users around; just mark deleted
 		   			session.createQuery("update com.sitescape.team.domain.Principal set deleted=:deleted where id in (:pList) and type='user'")
            			.setBoolean("deleted", Boolean.TRUE)
            			.setParameterList("pList", ids)
       	   				.executeUpdate();
 		   			//delete applications
 		   			session.createQuery("Delete com.sitescape.team.domain.Principal where id in (:pList) and type='application'")
            			.setParameterList("pList", ids)
       	   				.executeUpdate();
 		   			//delete application groups
 		   			session.createQuery("Delete com.sitescape.team.domain.Principal where id in (:pList) and type='applicationGroup'")
            			.setParameterList("pList", ids)
       	   				.executeUpdate();
		   			//this flushes secondary cache
		   			session.getSessionFactory().evict(Principal.class);			   			
		   			session.getSessionFactory().evictCollection("com.sitescape.team.domain.UserPrincipal.memberOf");
		   			session.getSessionFactory().evictCollection("com.sitescape.team.domain.ApplicationPrincipal.memberOf");
      	   				
           	   		return null;
        	   		}
        	   	}
        	 );    	
       	
    }
        
    public void disablePrincipals(final Collection<Long> ids, final Long zoneId) {
    	getHibernateTemplate().execute(
        	new HibernateCallback() {
        		public Object doInHibernate(Session session) throws HibernateException {
        			session.createQuery("UPDATE com.sitescape.team.domain.Principal set disabled = :disable where zoneId = :zoneId and internalId is null and id in (:pList)")
        			.setBoolean("disable", true)
        			.setLong("zoneId", zoneId)
        			.setParameterList("pList", ids)
        			.executeUpdate();
        			return null;
        		}
        	}
        );

    }
    //used for login
    public User findUserByName(final String userName, String zoneName) {
    	final Binder top = getCoreDao().findTopWorkspace(zoneName);
    	return findUserByName(userName, top.getId());
    }
    
 	public User findUserByName(final String userName, final Long zoneId) 
	throws NoUserByTheNameException {
        return (User)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                 	   //only returns active users
                 	   User user = (User)session.getNamedQuery("find-User-Company")
                             		.setString(ParameterNames.USER_NAME, userName.toLowerCase())
                             		.setLong(ParameterNames.ZONE_ID, zoneId)
                             		.setCacheable(true)
                             		.uniqueResult();
                        //query ensures user is not deleted and not disabled
                 	   if (user == null) {
                            throw new NoUserByTheNameException(userName); 
                        }
                        return user;
                    }
                }
             );		
 	}

 	public Principal findPrincipalByName(final String name, final Long zoneId) 
 		throws NoPrincipalByTheNameException {
        return (Principal)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                 	   //only returns active users
                    	Principal p= (Principal)session.getNamedQuery("find-Principal-Company")
                             		.setString(ParameterNames.USER_NAME, name.toLowerCase())
                             		.setLong(ParameterNames.ZONE_ID, zoneId)
                             		.setCacheable(true)
                             		.uniqueResult();
                        //query ensures user is not deleted and not disabled
                 	   if (p == null) {
                            throw new NoPrincipalByTheNameException(name); 
                 	   }
                 	   return resolveProxy(session, p);
                    }
                }
             );		
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

    public ProfileBinder getProfileBinder(Long zoneId) {
    	return (ProfileBinder)getCoreDao().loadReservedBinder(ObjectKeys.PROFILE_ROOT_INTERNALID, zoneId);
    }

    /*
     *  (non-Javadoc)
     * @see com.sitescape.team.dao.CoreDao#loadPrincipal(java.lang.Long, java.lang.Long)
     */
    public UserPrincipal loadUserPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
        UserPrincipal principal = (UserPrincipal)getHibernateTemplate().execute(
                new HibernateCallback() {
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
       
        return principal;
              
    }
    public List<UserPrincipal> loadUserPrincipals(final Collection ids, final Long zoneId, boolean checkActive) {
    	List<UserPrincipal> result = loadPrincipals(ids, zoneId, UserPrincipal.class, true, checkActive);
		//remove proxies
		for (int i=0; i<result.size(); ++i) {
			UserPrincipal p = result.get(i);
			if (!(p instanceof User) && !(p instanceof Group)) {
				UserPrincipal principal = (UserPrincipal)getHibernateTemplate().get(User.class, p.getId());
				if (principal==null) principal = (UserPrincipal)getHibernateTemplate().get(Group.class, p.getId());
   				result.set(i, principal);
            }
        }
		return result;
    }
    public List<Principal> loadPrincipalByEmail(final String email, final Long zoneId) {
        List result = (List)getHibernateTemplate().execute(
               	new HibernateCallback() {
                		public Object doInHibernate(Session session) throws HibernateException {
    	                  	Query q = session.createQuery("select x.principal from com.sitescape.team.domain.EmailAddress x where " +
    	                  					"lower(x.address)=:address");
    	                  	q.setParameter("address", email.toLowerCase());
    	                  	List result = q.list();
    	                  	for (int i=0; i<result.size();) { //check zone here, so index is used
    	                  		if (!((Principal)result.get(i)).getZoneId().equals(zoneId)) result.remove(i);
    	                  		else ++i;
    	                  	}
    	                  	return result;
                		}
               	}
            );
        return result;
        	   	
    }
    private List loadPrincipals(final Collection ids, final Long zoneId, final Class clazz, final boolean cacheable, final boolean checkActive) {
        if ((ids == null) || ids.isEmpty()) return new ArrayList();
        List result = (List)getHibernateTemplate().execute(
           	new HibernateCallback() {
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
        return result;
    	
    }
    private List loadPrincipals(final FilterControls filter, Long zoneId, final Class clazz) throws DataAccessException { 
       	filter.add(ObjectKeys.FIELD_ZONE, zoneId);
    	filter.add(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE);
    	filter.add(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE);
        return (List)getHibernateTemplate().execute(
                new HibernateCallback() {
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
    }    
  
	public Group loadGroup(final Long groupId, Long zoneId)  {
		try {
			Group group = (Group)getHibernateTemplate().get(Group.class, groupId);
			if (group == null) {throw new NoGroupByTheIdException(groupId);}
			//make sure from correct zone
			if (!group.getZoneId().equals(zoneId) || !group.isActive()) {throw new NoGroupByTheIdException(groupId);}
			return group;
		} catch (ClassCastException ce) {
			throw new NoGroupByTheIdException(groupId);
		}
	}
	public ApplicationGroup loadApplicationGroup(final Long groupId, Long zoneId)  {
		try {
			ApplicationGroup group = (ApplicationGroup)getHibernateTemplate().get(ApplicationGroup.class, groupId);
			if (group == null) {throw new NoGroupByTheIdException(groupId);}
			//make sure from correct zone
			if (!group.getZoneId().equals(zoneId) || !group.isActive()) {throw new NoGroupByTheIdException(groupId);}
			return group;
		} catch (ClassCastException ce) {
			throw new NoGroupByTheIdException(groupId);
		}
	}
	public List<Group> loadGroups(Collection<Long> ids, Long zoneId) {
		return loadPrincipals(ids, zoneId, Group.class, false, true);
	}

    public List<Group> loadGroups(FilterControls filter, Long zoneId) throws DataAccessException { 
    	return loadPrincipals(filter, zoneId, Group.class);
    }  
	public User loadUser(Long userId, String zoneName) {
		Binder top = getCoreDao().findTopWorkspace(zoneName);
		return loadUser(userId, top.getZoneId());
    }
    public User loadUser(Long userId, Long zoneId) {
    	try {
    		User user = (User)getHibernateTemplate().get(User.class, userId);
    		if (user == null) {throw new NoUserByTheIdException(userId);}
    		//	make sure from correct zone
    		if (!user.getZoneId().equals(zoneId) || !user.isActive()) {
    			throw new NoUserByTheIdException(userId);
    		}
    		return user;
    	} catch (ClassCastException ce) {
   			throw new NoUserByTheIdException(userId);   		
    	}
    }
	
	public List<User> loadUsers(Collection<Long> ids, Long zoneId) {
		return loadPrincipals(ids, zoneId, User.class, false, true);
    }
    public List<User> loadUsers(FilterControls filter, Long zoneId) throws DataAccessException { 
     	return loadPrincipals(filter, zoneId, User.class);
    }

     
    public SFQuery queryUsers(FilterControls filter, Long zoneId) throws DataAccessException { 
       	return queryPrincipals(filter, zoneId, User.class);
    }
    public SFQuery queryGroups(FilterControls filter, Long zoneId) throws DataAccessException { 
    	return queryPrincipals(filter, zoneId, Group.class);
    }  
    public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId) throws DataAccessException { 
       	return queryPrincipals(filter, zoneId, Principal.class);
    }
    
    private SFQuery queryPrincipals(FilterControls filter, Long zoneId, final Class clazz) throws DataAccessException { 
		final FilterControls myFilter = filter==null?new FilterControls():filter;
		if (myFilter.isZoneCheck()) myFilter.add(ObjectKeys.FIELD_ZONE, zoneId);
		myFilter.add(ObjectKeys.FIELD_ENTITY_DELETED, Boolean.FALSE);
		myFilter.add(ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.FALSE);
        Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
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
 	public void bulkLoadCollections(final Collection<Principal> entries) {
 		if (entries.size() > inClauseLimit) throw new IllegalArgumentException("Collection to large");
  	    getHibernateTemplate().execute(
            new HibernateCallback() {
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
 	
    public UserProperties loadUserProperties(Long userId) {
    	UserPropertiesPK id = new UserPropertiesPK(userId);
    	UserProperties uProps=null;
    	try {
    		uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);         
 		} catch (HibernateSystemException se) {
    		//old dom trees cause errors with the new dom4j.  These properties should be stored as strings, and
    		//this problem was fixed before ship
			if (se.getRootCause() instanceof java.io.InvalidClassException) {
				//bad serialized data - get rid of it
    			executePropertiesDelete(id);
			} else throw se;
		}
   		if (uProps == null) {
			uProps = new UserProperties(id);
			uProps=(UserProperties)getCoreDao().saveNewSession(uProps);
		}
		return uProps;
    }
 
    public UserProperties loadUserProperties(Long userId, Long binderId) {
    	UserPropertiesPK id = new UserPropertiesPK(userId, binderId);
    	UserProperties uProps=null;
    	try {
    		uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
    	} catch (HibernateSystemException se) {
    		//old dom trees cause errors with the new dom4j.  These properties should be stored as strings, and
    		//	this problem was fixed before ship
    		if (se.getRootCause() instanceof java.io.InvalidClassException) {
    			//	bad serialized data - get rid of it
    			executePropertiesDelete(id);
    		} else throw se;
    	}
		if (uProps == null) {
        	uProps = new UserProperties(id);
        	//quick write
        	uProps=(UserProperties)getCoreDao().saveNewSession(uProps);
    	}
		return uProps;
    }
	//At one point dom4j (1.5) objects where serialized,  don4j(1.6) does not recognize them
	//Somewhere along the way we changed it so we saved the string value instead of the serialized object, but 
	//occasionally they pop up at home.
	private void executePropertiesDelete(final UserPropertiesPK id) {
		//need to execute in new session, so it gets commited
       	SessionFactory sf = getSessionFactory();
    	Session session = sf.openSession();
		Statement statement = null;
    	try {
    		statement = session.connection().createStatement();
    		statement.executeUpdate("delete from SS_UserProperties where principalId=" + id.getPrincipalId() + 
    				" and binderId=" + id.getBinderId());;
   	} catch (SQLException sq) {
    		throw new HibernateException(sq);
    	} finally {
    		try {if (statement != null) statement.close();} catch (Exception ex) {};
    		session.close();
    	}

	}
    public Group getReservedGroup(String internalId, Long zoneId) {
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
    public ApplicationGroup getReservedApplicationGroup(String internalId, Long zoneId) {
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
    public User getReservedUser(String internalId, Long zoneId) {
    	Long id = getReservedId(internalId, zoneId);
    	if (id == null) {
    		List<User>objs = getCoreDao().loadObjects(User.class, new FilterControls(ObjectKeys.FIELD_INTERNALID, internalId), zoneId);
    		if ((objs == null) || objs.isEmpty()) throw new NoUserByTheNameException(internalId);
    		User u = objs.get(0);
    		setReservedId(internalId, zoneId, u.getId());
    		return u;
    	}
    	return loadUser(id, zoneId);
   }
    
    public Set<Long> getPrincipalIds(Principal p) {
    	if (p instanceof IndividualPrincipal) {
	    	if(((IndividualPrincipal)p).isAllIndividualMember()) {
	        	GroupPrincipal gp;
	        	if(p instanceof User)
	        		gp = getReservedGroup(ObjectKeys.ALL_USERS_GROUP_INTERNALID, p.getZoneId());
	        	else if(p instanceof Application)
	        		gp = getReservedApplicationGroup(ObjectKeys.ALL_APPLICATIONS_GROUP_INTERNALID, p.getZoneId());
	        	else
	        		throw new IllegalArgumentException(p.getClass().getName());
	        	
	    		return new HashSet(p.computePrincipalIds(gp));
	    	}
	    	else {
	    		return new HashSet(p.computePrincipalIds(null));
	    	}
    	} else {
    		return new HashSet(p.computePrincipalIds(null));
    	}
    }
	/**
	 * Given a set of principal ids, return all userIds that represent userIds in 
	 * the original list, or members of groups and their nested groups.
	 * This is used to turn a distribution list into usersIds only.
	 * Use when don't need to load the entire object
	 * @param Set of principalIds
	 * @returns Set of userIds
	 */
	public Set<Long> explodeGroups(final Collection<Long> ids, Long zoneId) {   
		if ((ids == null) || ids.isEmpty()) return new TreeSet();
		Set users = (Set)getHibernateTemplate().execute(
            new HibernateCallback() {
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
		Long allId = getReservedId(ObjectKeys.ALL_USERS_GROUP_INTERNALID, zoneId);
		if (ids.contains(allId) || users.contains(allId)) {
			//need to remove some users from the all users group.  Original list may add them back in.
			//so need to do this step last
			List<Object[]> result = getCoreDao().loadObjects(
					new ObjectControls(User.class, new String[]{ObjectKeys.FIELD_ID}), null, zoneId);
			//remove users not included
			result.remove(getReservedId(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, zoneId));
			result.remove(getReservedId(ObjectKeys.GUEST_USER_INTERNALID, zoneId));
			result.remove(getReservedId(ObjectKeys.JOB_PROCESSOR_INTERNALID, zoneId));
			users.addAll(result);
		} 
		users.remove(allId);
		return users;		
	}
	/**
	 * Get the Membership of a group.  Does not explode nested groups.
	 * Does not load the group object
	 * @param groupId
	 * @result List of <code>Membership</code>
	 */
	public List<Long> getMembership(final Long groupId, Long zoneId) {
		if (groupId == null) return new ArrayList();
	    List membership = (List)getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                   	Query query = session.createQuery("from com.sitescape.team.domain.Membership m where m.groupId=?");
                   	query.setParameter(0, groupId);
                    return query.list();
                 }
            }
        );
		return membership;		
		
	}
	/**
	 * Get all groups a principal is a member of, either directly or through nested group
	 * membership.
	 * Use when don't want to load the entire group object
	 * @param principalId
	 * @return Set of groupIds
	 */
	public Set<Long> getAllGroupMembership(final Long principalId, Long zoneId) {
		if (principalId == null)  return new TreeSet();
		return (Set)getHibernateTemplate().execute(
            new HibernateCallback() {
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

	public SeenMap loadSeenMap(Long userId) {
   		SeenMap seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, userId);
   		if (seen == null) {
   			seen = new SeenMap(userId);
   			//quick write
   			seen = (SeenMap)getCoreDao().saveNewSession(seen);
   		}
   		return seen;
	}
	public Visits loadVisit(Long userId, EntityIdentifier entityId) {
    	UserEntityPK id = new UserEntityPK(userId, entityId);
        return (Visits)getHibernateTemplate().get(Visits.class, id);	
	}
	public Rating loadRating(Long userId, EntityIdentifier entityId) {
    	UserEntityPK id = new UserEntityPK(userId, entityId);
        return (Rating)getHibernateTemplate().get(Rating.class, id);	
	}
	public Subscription loadSubscription(Long userId, EntityIdentifier entityId) {
    	UserEntityPK id = new UserEntityPK(userId, entityId);
        return (Subscription)getHibernateTemplate().get(Subscription.class, id);	
	}
	//mark entries deleted - used when deleting entries in bulk and want
	//to exclude some from future queries
	//entries evicted from cache
	public void markEntriesDeleted(final ProfileBinder binder, final Collection<Principal> entries) {
		if (entries.isEmpty()) return;
		getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException {
						Set ids = new HashSet();	               			
						for (Principal p:entries) {
							ids.add(p.getId());
							session.evict(p);
	            	    }
						session.createQuery("Update com.sitescape.team.domain.Principal set deleted=:delete where id in (:pList)")
						.setBoolean("delete", Boolean.TRUE)
						.setParameterList("pList", ids)
						.executeUpdate();
						return null;
					}
				}
		);    		             		 
	}
	public Application loadApplication(Long applicationId, Long zoneId) throws NoUserByTheIdException {
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
	}
	public Application loadApplication(Long applicationId, String zoneName) throws NoUserByTheIdException {
		Binder top = getCoreDao().findTopWorkspace(zoneName);
		return loadApplication(applicationId, top.getZoneId());
	}

    public Principal loadPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
        Principal principal = (Principal)getHibernateTemplate().execute(
                new HibernateCallback() {
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
       
        return principal;
              
    }
    
	public List<ApplicationGroup> loadApplicationGroups(Collection<Long> groupsIds, Long zoneId) {
		return loadPrincipals(groupsIds, zoneId, ApplicationGroup.class, false, true);
	}

	public ApplicationPrincipal loadApplicationPrincipal(final Long prinId, final Long zoneId, final boolean checkActive) {
		ApplicationPrincipal principal = (ApplicationPrincipal)getHibernateTemplate().execute(
                new HibernateCallback() {
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
	
	public List<ApplicationPrincipal> loadApplicationPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
    	List<ApplicationPrincipal> result = loadPrincipals(ids, zoneId, ApplicationPrincipal.class, true, checkActive);
		//remove proxies
		for (int i=0; i<result.size(); ++i) {
			ApplicationPrincipal p = result.get(i);
			if (!(p instanceof Application) && !(p instanceof ApplicationGroup)) {
				ApplicationPrincipal principal = (ApplicationPrincipal)getHibernateTemplate().get(Application.class, p.getId());
				if (principal==null) principal = (ApplicationPrincipal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
   				result.set(i, principal);
            }
        }
		return result;
	}
	
	public List loadGroupPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, false, checkActive);
		//remove proxies
		for (int i=0; i<result.size(); ++i) {
			Principal p = result.get(i);
			if (!(p instanceof Group) && !(p instanceof ApplicationGroup)) {
				Principal principal = (Principal)getHibernateTemplate().get(Group.class, p.getId());
				if (principal==null) principal = (Principal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
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
	
	public List loadIndividualPrincipals(Collection<Long> ids, Long zoneId, boolean checkActive) {
    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, false, checkActive);
		//remove proxies
		for (int i=0; i<result.size(); ++i) {
			Principal p = result.get(i);
			if (!(p instanceof User) && !(p instanceof Application)) {
				Principal principal = (Principal)getHibernateTemplate().get(User.class, p.getId());
				if (principal==null) principal = (Principal)getHibernateTemplate().get(Application.class, p.getId());
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

    public List<Application> loadApplications(Collection<Long> applicationIds, Long zoneId) {
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
    
    public List<Principal> loadPrincipals(Collection<Long> ids, Long zoneId,  boolean checkActive) {
    	List<Principal> result = loadPrincipals(ids, zoneId, Principal.class, true, checkActive);
		//remove proxies
		for (int i=0; i<result.size(); ++i) {
			Principal p = result.get(i);
			if (!(p instanceof User) && !(p instanceof Group) && !(p instanceof Application) && !(p instanceof ApplicationGroup)) {
				Principal principal = (Principal)getHibernateTemplate().get(User.class, p.getId());
				if (principal==null) principal = (Principal)getHibernateTemplate().get(Group.class, p.getId());
				if (principal==null) principal = (Principal)getHibernateTemplate().get(Application.class, p.getId());
				if (principal==null) principal = (Principal)getHibernateTemplate().get(ApplicationGroup.class, p.getId());
   				result.set(i, principal);
            }
        }
		return result;
	
    }
    
    private List loadAllPrincipals(final Long zoneId, final Class clazz, final boolean cacheable, final boolean checkActive) {
        List result = (List)getHibernateTemplate().execute(
           	new HibernateCallback() {
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
    public List<SharedEntity> loadSharedEntities(final Collection ids, final Collection binderIds, final Date after, Long zoneId) {   	
      	List result = (List)getHibernateTemplate().execute(
                new HibernateCallback() {
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
                    		crit.add(Expression.in("accessId", ids))
                  				.add(Expression.eq("accessType", SharedEntity.ACCESS_TYPE_PRINCIPAL));
                        		
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
}
