package com.sitescape.ef.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.NoGroupByTheIdException;
import com.sitescape.ef.domain.NoGroupByTheNameException;
import com.sitescape.ef.domain.NoPrincipalByTheIdException;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Rating;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserEntityPK;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.UserPropertiesPK;
import com.sitescape.ef.domain.Visits;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.util.LongIdComparator;
/**
 * @author Jong Kim
 *
 */
public class ProfileDaoImpl extends HibernateDaoSupport implements ProfileDao {
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
	 * Delete the binder object and its assocations.
	 * Entries and child binders should already have been deleted
	 */	
	public void delete(ProfileBinder binder) {
		   //core handles everything for the profiles 
		getCoreDao().delete((Binder)binder);			
	}
	/**
	 * Delete all the entries in the folder.
	 * Don't delete the folder
	 */
    public void deleteEntries(final ProfileBinder profiles) {
    	//TODO: because groups own the membership association, this will require
    	//all groups to be flushed from the session. Since we are disabling users, won't worry about it
    	//for now
	    getHibernateTemplate().execute(
	    	new HibernateCallback() {
	       		public Object doInHibernate(Session session) throws HibernateException {
    	   			Connection connect = session.connection();
       	   			try {
       	   				Statement s = connect.createStatement();
      	   				s.executeUpdate("delete from SS_WorkAreaFunctionMembers where memberId in " + 
      	   						"(select p.id from SS_Principals p where  p.parentBinder=" + profiles.getId() + ")");
      	   				s.executeUpdate("delete from SS_Notifications where principalId in " + 
      	   						"(select p.id from SS_Principals p where  p.parentBinder=" + profiles.getId() + ")");
      	   			} catch (SQLException sq) {
       	   				throw new HibernateException(sq);
       	   			}
	       			getCoreDao().deleteEntityAssociations("owningBinderId=" + profiles.getId(), Principal.class);

 		   			session.createQuery("Delete com.sitescape.ef.domain.SeenMap where principalId in " + 
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
 		   					" p.parentBinder=:profile)")
		   			  	.setEntity("profile", profiles)
		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.ef.domain.Membership where groupId in " +
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
			  				" p.parentBinder=:profile)")
				   			.setEntity("profile", profiles)
	 		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.ef.domain.Membership where userId in " +
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
			  				" p.parentBinder=:profile)")
				   			.setEntity("profile", profiles)
 	 		   				.executeUpdate();

		   			session.createQuery("Delete com.sitescape.ef.domain.UserProperties where principalId in " +
			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
			  				" p.parentBinder=:profile)")
 				   			.setEntity("profile", profiles)
 				         	.executeUpdate();
		   			//delete ratings/visits owned by these principals
 		   			session.createQuery("Delete com.sitescape.ef.domain.Rating where principalId in " + 
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
		   			  			" p.parentBinder=:profile)")
				   		.setEntity("profile", profiles)
		   				.executeUpdate();
		   			//delete subscriptions owned by these principals
 		   			session.createQuery("Delete com.sitescape.ef.domain.Subscription where principalId in " + 
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
		   			  			" p.parentBinder=:profile)")
				   		.setEntity("profile", profiles)
		   				.executeUpdate();

		   			List types = new ArrayList();
	       			types.add(EntityIdentifier.EntityType.user.getValue());
	       			types.add(EntityIdentifier.EntityType.group.getValue());

	       			//delete subscriptions to these principals
 		   			session.createQuery("Delete com.sitescape.ef.domain.Subscription where entityId in " + 
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
		   			  			" p.parentBinder=:profile) and entityType in (:tList)")
				   		.setEntity("profile", profiles)
		   				.executeUpdate();
 	       			
		   			//delete ratings/visits for these principals
 		   			session.createQuery("Delete com.sitescape.ef.domain.Rating where entityId in " + 
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
		   			  			" p.parentBinder=:profile) and entityType in (:tList)")
			   			.setEntity("profile", profiles)
	   			  		.setParameterList("tList", types)
	   			  		.executeUpdate();
		   			//delete tags owned by these users
 		   			session.createQuery("Delete com.sitescape.ef.domain.Tag where owner_id in " + 
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
		   			  			" p.parentBinder=:profile) and owner_type in (:tList)")
		   			  	.setEntity("profile", profiles)
		   			  	.setParameterList("tList", types)
		   				.executeUpdate();
	       			
		   			//delete tags for these principals
 		   			session.createQuery("Delete com.sitescape.ef.domain.Tag where entity_id in " + 
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
			  			" p.parentBinder=:profile) and entity_type in (:tList)")
		   			  	.setEntity("profile", profiles)
		   			  	.setParameterList("tList", types)
		   				.executeUpdate();
 		   			//delete any reserved names for entries
 		   			session.createQuery("Delete com.sitescape.ef.domain.LibraryTag where binderId=:binderId and not entityId is null")
		   				.setLong("binderId", profiles.getId())
		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.ef.domain.Principal where parentBinder=" + profiles.getId())
	       				.executeUpdate();
	       			session.getSessionFactory().evict(Principal.class);		
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
    /**
     * Delete a list of entries
     */
    public void deleteEntries(final List entries) {
    	//TODO: because groups own the membership association, this will require
    	//all groups to be flushed from the session or the memberhips to be fixed up
    	//prior to this call.  Since we are disabling users, won't worry about it
    	//for now
    	if (entries == null || entries.size() == 0) return;
      	getHibernateTemplate().execute(
        	   	new HibernateCallback() {
        	   		public Object doInHibernate(Session session) throws HibernateException {
        	   	   	Set ids = new HashSet();
           			StringBuffer inList = new StringBuffer();
        			Principal p;
        			for (int i=0; i<entries.size(); ++i) {
        				p = (Principal)entries.get(i); 
        	    		ids.add(p.getId());
        	    		inList.append(p.getId().toString() + ",");
        	    	}
        			inList.deleteCharAt(inList.length()-1);
    	   			Connection connect = session.connection();
       	   			try {
       	   				Statement s = connect.createStatement();
      	   				s.executeUpdate("delete from SS_WorkAreaFunctionMembers where memberId in (" + inList.toString() + ")");
      	   				s.executeUpdate("delete from SS_Notifications where principalId in (" + inList.toString() + ")");
       	   			} catch (SQLException sq) {
       	   				throw new HibernateException(sq);
       	   			}
        			//need to use ownerId, cause attachments/custom sets not indexed by principal
		   			getCoreDao().deleteEntityAssociations("ownerId in (" + inList.toString() + ") and (ownerType='" +
		   					EntityType.user.name() + "' or ownerType='" + EntityType.group.name() + "')", Principal.class);
		   			session.createQuery("Delete com.sitescape.ef.domain.SeenMap where principalId in (:pList)")
		   				.setParameterList("pList", ids)
       	   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.ef.domain.Membership where userId in (:uList) or groupId in (:gList)")
		   				.setParameterList("uList", ids)
		   				.setParameterList("gList", ids)
       	   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.ef.domain.UserProperties where principalId in (:pList)")
	   					.setParameterList("pList", ids)
	   					.executeUpdate();

		   			//delete ratings/visits owned by these users
 		   			session.createQuery("Delete com.sitescape.ef.domain.Rating where principalId in (:pList)")
	   					.setParameterList("pList", ids)
		   				.executeUpdate();

		   			//delete subscriptions owned by these users
 		   			session.createQuery("Delete com.sitescape.ef.domain.Subscription where principalId in (:pList)")
	   					.setParameterList("pList", ids)
		   				.executeUpdate();

 		   			List types = new ArrayList();
	       			types.add(EntityIdentifier.EntityType.user.getValue());
	       			types.add(EntityIdentifier.EntityType.group.getValue());
	       			//delete subscriptions to these principals
 		   			session.createQuery("Delete com.sitescape.ef.domain.Subscription where entityId in (:pList) and entityType in (:tList)")
 	   					.setParameterList("pList", ids)
	   					.setParameterList("tList", types)
		   				.executeUpdate();
 	       			
		   			//delete ratings/visits for by these principals
 		   			session.createQuery("Delete com.sitescape.ef.domain.Rating where entityId in (:pList) and entityType in (:tList)")
 	   					.setParameterList("pList", ids)
	   					.setParameterList("tList", types)
		   				.executeUpdate();

 		   			//delete tags owned by this entity
 		   			session.createQuery("Delete com.sitescape.ef.domain.Tag where owner_id in (:pList) and owner_type in (:tList)")
	   					.setParameterList("pList", ids)
	   					.setParameterList("tList", types)
	   					.executeUpdate();

		   			session.createQuery("Delete com.sitescape.ef.domain.Tag where entity_id in (:pList) and entity_type in (:tList)")
   						.setParameterList("pList", ids)
   						.setParameterList("tList", types)
   						.executeUpdate();
   		   			//delete any reserved names
 		   			session.createQuery("Delete com.sitescape.ef.domain.LibraryEntry where binderId=:binderId and entityId in (:pList)")
		   				.setParameterList("pList", ids)
		   				.setLong("binderId", ((Principal)entries.get(0)).getParentBinder().getId())
		   				.executeUpdate();
 		   			session.createQuery("Delete com.sitescape.ef.domain.Principal where id in (:pList)")
            			.setParameterList("pList", ids)
       	   				.executeUpdate();
	       			session.getSessionFactory().evict(Principal.class);		
       	   				
           	   		return null;
        	   		}
        	   	}
        	 );    	
       	
    }
        
    public ProfileBinder getProfileBinder(Long zoneId) {
    	return (ProfileBinder)getCoreDao().loadReservedBinder(ObjectKeys.PROFILE_ROOT_ID, zoneId);
    }

    /*
     *  (non-Javadoc)
     * @see com.sitescape.ef.dao.CoreDao#loadPrincipal(java.lang.Long, java.lang.Long)
     */
    public Principal loadPrincipal(final Long prinId, Long zoneId) {
        Principal principal = (Principal)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	//hoping for cache hit
                    	Principal principal = (Principal)session.get(Principal.class, prinId);
                        if (principal == null) {throw new NoPrincipalByTheIdException(prinId);}
                        //Get the real object, not a proxy to abstract class
                        principal = (Principal)session.get(User.class, prinId);
                        if (principal==null) 
                            principal = (Principal)session.get(Group.class, prinId);
                        return principal;
                    }
                }
        );
        //make sure from correct zone
        if (!principal.getZoneId().equals(zoneId)) {throw new NoPrincipalByTheIdException(prinId);}
       
        return principal;
              
    }
   /* 
     * Optimization to load principals in bulk
     */
    public List loadPrincipals(final Collection ids, final Long zoneId) {
        if ((ids == null) || ids.isEmpty()) return new ArrayList();
        List result = (List)getHibernateTemplate().execute(
           	new HibernateCallback() {
            		public Object doInHibernate(Session session) throws HibernateException {
            			List result = session.createQuery("from com.sitescape.ef.domain.Principal p where p.zoneId = :zoneId and p.id in (:pList)")
            			.setLong("zoneId", zoneId)
            			.setParameterList("pList", ids)
            			// Unlike some other query caches used for reference type objects,
            			// this cache is not very useful in the sense that the result of
            			// this query is very unlikely to be shared across users.
            			// However, some WebDAV usage patterns make it useful because it
            			// can repeatedly asks for the same set of information (for a 
            			// request from the same user). We can use this as a short-lived
            			// temporary cache. 
            			.setCacheable(true)
            			.list();
            			//remove proxies
            			for (int i=0; i<result.size(); ++i) {
            				Principal p = (Principal)result.get(i);
            				if (!(p instanceof User) && !(p instanceof Group)) {
            					Principal principal = (Principal)session.get(User.class, p.getId());
            					if (principal==null) 
            						principal = (Principal)session.get(Group.class, p.getId());
            					result.set(i, principal);
            				}
            			}
            			return result;
            		}
           	}
        );
        return result;
     }
  
    public void disablePrincipals(final Collection ids, final Long zoneId) {
    	getHibernateTemplate().execute(
        	new HibernateCallback() {
        		public Object doInHibernate(Session session) throws HibernateException {
        			session.createQuery("UPDATE Principal set disabled = :disable where zoneId = :zoneId and internalId is null and id in (:pList)")
        			.setBoolean("disable", true)
        			.setLong("zoneId", zoneId)
        			.setParameterList("pList", ids)
        			.executeUpdate();
        			return null;
        		}
        	}
        );

    }
    /*
     *  (non-Javadoc)
     * @see com.sitescape.ef.dao.CoreDao#loadUser(java.lang.Long, java.lang.Long)
     */
    public User loadUser(Long userId, Long zoneId) {
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
    }
	public User loadUserOnlyIfEnabled(Long userId, Long zoneId) {
        User user = loadUser(userId, zoneId);
                      		
        if (user.isDisabled()) {
            throw new NoUserByTheIdException(userId);               
        }        
        return user;
    }
	
	public User loadUserOnlyIfEnabled(Long userId, String zoneName) {
		Binder top = getCoreDao().findTopWorkspace(zoneName);
		return loadUserOnlyIfEnabled(userId, top.getZoneId());
    }
	public List loadUsers(Collection ids, Long zoneId) {
		return getCoreDao().loadObjects(ids, User.class, zoneId);
	}
	public List loadEnabledUsers(Collection ids, Long zoneId) {
		List users = loadUsers(ids, zoneId);
		List result = new ArrayList();
		for (Iterator iter=users.iterator();iter.hasNext();) {
			User u = (User)iter.next();
			if (!u.isDisabled()) {
				result.add(u);
			}
		}
        return result;        
    }

    public User findUserByName(final String userName, String zoneName) {
    	final Binder top = getCoreDao().findTopWorkspace(zoneName);
        return (User)getHibernateTemplate().execute(
           new HibernateCallback() {
               public Object doInHibernate(Session session) throws HibernateException {
                   User user = (User)session.getNamedQuery("find-User-Company")
                        		.setString(ParameterNames.USER_NAME, userName)
                        		.setLong(ParameterNames.ZONE_ID, top.getId())
                        		.setCacheable(true)
                        		.uniqueResult();
                   if (user == null) {
                       throw new NoUserByTheNameException(userName); 
                   }
                   return user;
               }
           }
        );
    }
    
    public User findUserByNameOnlyIfEnabled(final String userName, final String zoneName) {
        User user = findUserByName(userName, zoneName);
        
        if (user.isDisabled()) {
            throw new NoUserByTheNameException(userName);               
        }  
        
        return user;
    }    
     
    public SFQuery queryUsers(FilterControls filter, Long zoneId) throws DataAccessException { 
    	filter.add("zoneId", zoneId);
       	return queryPrincipals(filter, User.class.getName());
    }
    public SFQuery queryGroups(FilterControls filter, Long zoneId) throws DataAccessException { 
    	filter.add("zoneId", zoneId);
    	return queryPrincipals(filter, Group.class.getName());
    }  
    public SFQuery queryAllPrincipals(FilterControls filter, Long zoneId) throws DataAccessException { 
    	filter.add("zoneId", zoneId);
       	return queryPrincipals(filter, Principal.class.getName());
    }
    
    private SFQuery queryPrincipals(final FilterControls filter, final String clazz) throws DataAccessException { 
        Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        //sqlqueries, filters and criteria don't help with frontbase problem
                        //
                        Query query = session.createQuery("from " + clazz + " u " + filter.getFilterString("u"));
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
    public List loadUsers(FilterControls filter, Long zoneId) throws DataAccessException { 
    	filter.add("zoneId", zoneId);
    	return loadPrincipals(filter, User.class.getName());
    }
    public List loadGroups(FilterControls filter, Long zoneId) throws DataAccessException { 
    	filter.add("zoneId", zoneId);
    	return loadPrincipals(filter, Group.class.getName());
    }  
    private List loadPrincipals(final FilterControls filter, final String clazz) throws DataAccessException { 
        return (List)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        //sqlqueries, filters and criteria don't help with frontbase problem
                        //
                        Query query = session.createQuery("from " + clazz + " u " + filter.getFilterString("u"));
                		List filterValues = filter.getFilterValues();
               			for (int i=0; i<filterValues.size(); ++i) {
                			query.setParameter(i, filterValues.get(i));
                		}
                       return query.list();
                    }
                }
            );  
    }    
	public void bulkLoadCollections(final Collection<Principal> entries) {
		//try loading the isMemberOf collection - we will get the Membership and assume the groups are in the secondary cache.
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
					return null;
                 }
            }
        );
	    getCoreDao().bulkLoadCollections(entries);
		
	}
 	
    public UserProperties loadUserProperties(Long userId) {
    	UserPropertiesPK id = new UserPropertiesPK(userId);
        UserProperties uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);         
 		if (uProps == null) {
 			uProps = new UserProperties(id);
 			uProps=(UserProperties)getCoreDao().saveNewSession(uProps);
         }
 		return uProps;
    }
 
    public UserProperties loadUserProperties(Long userId, Long binderId) {
    	UserPropertiesPK id = new UserPropertiesPK(userId, binderId);
        UserProperties uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
        if (uProps == null) {
        	uProps = new UserProperties(id);
        	//quick write
        	uProps=(UserProperties)getCoreDao().saveNewSession(uProps);
    	}
        return uProps;
    }
 
	public Group loadGroup(final Long groupId, Long zoneId)  {
		try {
			Group group = (Group)getHibernateTemplate().get(Group.class, groupId);
			if (group == null) {throw new NoGroupByTheIdException(groupId);}
			//make sure from correct zone
			if (!group.getZoneId().equals(zoneId)) {throw new NoGroupByTheIdException(groupId);}
			return group;
		} catch (ClassCastException ce) {
			throw new NoGroupByTheIdException(groupId);
		}
	}
	public List loadGroups(Collection ids, Long zoneId) {
		return getCoreDao().loadObjects(ids, Group.class, zoneId);
	}
   private Long getReservedGroupId(String internalId, Long zoneId) {
    	String key = internalId + "-" + zoneId;
    	Long id=null;
    	synchronized (reservedIds) {
    		id = (Long)reservedIds.get(key);
    	}
    	if (id == null) {
    		try {
    			Group g = getReservedGroup(internalId, zoneId);
    			id = g.getId();
    		} catch (NoGroupByTheNameException ng) {}
    		
        	synchronized (reservedIds) {
        		reservedIds.put(key, id);
        	}
     	}
   		return id;    	
    }
    public Group getReservedGroup(String internalId, Long zoneId) {
   		List<Group>objs = getCoreDao().loadObjects(Group.class, new FilterControls(
    					new String[]{"internalId", "zoneId"},
    					new Object[]{internalId, zoneId}));
    	if ((objs == null) || objs.isEmpty()) throw new NoGroupByTheNameException(internalId);
    	return (Group)objs.get(0);
    }
    public User getReservedUser(String internalId, Long zoneId) {
   		List<User>objs = getCoreDao().loadObjectsCacheable(User.class, new FilterControls(
    					new String[]{"internalId", "zoneId"},
    					new Object[]{internalId, zoneId}));
    	if ((objs == null) || objs.isEmpty()) throw new NoUserByTheNameException(internalId);
    	return (User)objs.get(0);
    }
    public Set getPrincipalIds(User user) {
    	return user.computePrincipalIds(getReservedGroupId(ObjectKeys.ALL_USERS_GROUP_ID, user.getZoneId()));
    }
	/**
	 * Given a set of principal ids, return all userIds that represent userIds in 
	 * the original list, or members of groups and their nested groups.
	 * This is used to turn a distribution list into usersIds only.
	 * Use when don't need to load the entire object
	 * @param Set of principalIds
	 * @returns Set of userIds
	 */
	public Set explodeGroups(final Set ids, Long zoneId) {   
		if ((ids == null) || ids.isEmpty()) return new TreeSet();
		Set users;
		if (ids.contains(getReservedGroupId(ObjectKeys.ALL_USERS_GROUP_ID, zoneId))) {
			List<Object[]> result = getCoreDao().loadObjects(new ObjectControls(User.class, 
					new String[]{"id"}), 
					new FilterControls(new String[]{"zoneId"}, new Object[]{zoneId}));
			users = new HashSet(result);
			//remove postingAgent
			User u = getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_ID, zoneId);
			users.remove(u.getId());
		} else {
			users = (Set)getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                    Set result = new TreeSet(ids);
                    List mems;
                    Set currentIds = new HashSet(ids);
                    while (!currentIds.isEmpty()) {
                    	mems  = session.createCriteria(Membership.class)
                    		.add(Expression.in("groupId", currentIds))
                        	.list();
                       	currentIds.clear();
						for (int i=0; i<mems.size(); ++i) {
							Membership m = (Membership)mems.get(i);
							result.remove(m.getGroupId());
							//potential user - may be another group
							result.add(m.getUserId());
							currentIds.add(m.getUserId());
						}
						//note: empty groups may appear in the resultant list
                    }
                    return result;
                }
            }
        );}
		return users;		
	}
	/**
	 * Get the Membership of a group.  Does not explode nested groups.
	 * Does not load the group object
	 * @param groupId
	 * @result List of <code>Membership</code>
	 */
	public List getMembership(final Long groupId, Long zoneId) {
		if (groupId == null) return new ArrayList();
	    List membership = (List)getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                   	Query query = session.createQuery("from com.sitescape.ef.domain.Membership m where m.groupId=?");
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
	public Set getAllGroupMembership(final Long principalId, Long zoneId) {
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
}
