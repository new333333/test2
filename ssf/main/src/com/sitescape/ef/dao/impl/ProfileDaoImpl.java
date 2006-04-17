package com.sitescape.ef.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Disjunction;
import org.hibernate.Criteria;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Group;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.UserPropertiesPK;
import com.sitescape.ef.domain.NoGroupByTheIdException;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.NoPrincipalByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.SFQuery;
/**
 * @author Jong Kim
 *
 */
public class ProfileDaoImpl extends HibernateDaoSupport implements ProfileDao {
	protected Log logger = LogFactory.getLog(getClass());
	private CoreDao coreDao;

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
      	   			} catch (SQLException sq) {
       	   				throw new HibernateException(sq);
       	   			}
	       			getCoreDao().deleteEntityAssociations("owningBinderId=" + profiles.getId(), Principal.class);

 		   			session.createQuery("Delete com.sitescape.ef.domain.SeenMap where principalId in " + 
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
		   			  			" p.parentBinder=" + profiles.getId() + ")")
		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.ef.domain.Membership where groupId in " +
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
 			   			  			" p.parentBinder=" + profiles.getId() + ")")
 	 		   				.executeUpdate();
		   			session.createQuery("Delete com.sitescape.ef.domain.Membership where userId in " +
 			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
 			   			  			" p.parentBinder=" + profiles.getId() + ")")
 	 		   				.executeUpdate();

		   			session.createQuery("Delete com.sitescape.ef.domain.UserProperties where principalId in " +
			   				"(select p.id from com.sitescape.ef.domain.Principal p where " +
		   			  			" p.parentBinder=" + profiles.getId() + ")")
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

		   			session.createQuery("Delete com.sitescape.ef.domain.Principal where id in (:pList)")
            			.setParameterList("pList", ids)
       	   				.executeUpdate();
	       			session.getSessionFactory().evict(Principal.class);		
       	   				
           	   		return null;
        	   		}
        	   	}
        	 );    	
       	
    }
        
    public void deleteEntryWorkflows(ProfileBinder profile) {
    	//brute force delete of jbpm data structures
    }
    public void deleteEntryWorkflows(List entries) {
    	
    }    /**
     * Lookup binder and cache result.  Profile binder is a fixed name
     */
    public ProfileBinder getProfileBinder(final String zoneName) {
        return (ProfileBinder)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        Binder binder = (Binder)session.getNamedQuery("find-Binder-Company")
                             		.setString(ParameterNames.BINDER_NAME, "_profiles")
                             		.setString(ParameterNames.COMPANY_ID, zoneName)
                             		.setCacheable(true)
                             		.uniqueResult();
                        if (binder == null) {
                            throw new NoBinderByTheNameException("_profiles"); 
                        }
                        return binder;
                    }
                }
             );
    }

    /*
     *  (non-Javadoc)
     * @see com.sitescape.ef.dao.CoreDao#loadPrincipal(java.lang.Long, java.lang.Long)
     */
    public Principal loadPrincipal(final Long prinId, String zoneName) {
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
        if ((zoneName != null ) && !principal.getZoneName().equals(zoneName)) {throw new NoPrincipalByTheIdException(prinId);}
       
        return principal;
              
    }
   /* 
     * Optimization to load principals in bulk
     */
    public List loadPrincipals(final Collection ids, final String zoneName) {
        if ((ids == null) || ids.isEmpty()) return new ArrayList();
        List result = (List)getHibernateTemplate().execute(
           	new HibernateCallback() {
            		public Object doInHibernate(Session session) throws HibernateException {
            			List result = session.createQuery("from com.sitescape.ef.domain.Principal p where p.zoneName = :zone and p.id in (:pList)")
            			.setString("zone", zoneName)
            			.setParameterList("pList", ids)
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
  
    public void disablePrincipals(final Collection ids, final String zoneName) {
    	getHibernateTemplate().execute(
        	new HibernateCallback() {
        		public Object doInHibernate(Session session) throws HibernateException {
        			session.createQuery("UPDATE Principal set disabled = :disable where reserved = :reserve and zoneName = :zone and id in (:pList)")
        			.setBoolean("disable", true)
        			.setBoolean("reserve", false)
        			.setString("zone", zoneName)
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
    public User loadUser(Long userId, String zoneName) {
    	User user = (User)getHibernateTemplate().get(User.class, userId);
        if (user == null) {throw new NoUserByTheIdException(userId);}
        //make sure from correct zone
        if ((zoneName != null ) && !user.isDefaultIdentity() &&
        		!user.getZoneName().equals(zoneName)) {throw new NoUserByTheIdException(userId);}
        return user;
    }
	public User loadUserOnlyIfEnabled(Long userId, String zoneName) {
        User user = loadUser(userId, zoneName);
                      		
        if (user.isDisabled()) {
            throw new NoUserByTheIdException(userId);               
        }        
        return user;
    }
	
	public List loadUsers(Collection ids, String zoneName) {
		return getCoreDao().loadObjects(ids, User.class, zoneName);
	}
	public List loadEnabledUsers(Collection ids, String zoneName) {
		List users = loadUsers(ids, zoneName);
		List result = new ArrayList();
		for (Iterator iter=users.iterator();iter.hasNext();) {
			User u = (User)iter.next();
			if (!u.isDisabled()) {
				result.add(u);
			}
		}
        return result;        
    }

    public User findUserByName(final String userName, final String zoneName) {
        return (User)getHibernateTemplate().execute(
           new HibernateCallback() {
               public Object doInHibernate(Session session) throws HibernateException {
                   User user = (User)session.getNamedQuery("find-User-Company")
                        		.setString(ParameterNames.USER_NAME, userName)
                        		.setString(ParameterNames.COMPANY_ID, zoneName)
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
     
    public SFQuery queryUsers(FilterControls filter, String zoneName) throws DataAccessException { 
    	filter.add("zoneName", zoneName);
       	return queryPrincipals(filter, User.class.getName());
    }
    public SFQuery queryGroups(FilterControls filter, String zoneName) throws DataAccessException { 
    	filter.add("zoneName", zoneName);
    	return queryPrincipals(filter, Group.class.getName());
    }  
    public SFQuery queryAllPrincipals(FilterControls filter, String zoneName) throws DataAccessException { 
    	filter.add("zoneName", zoneName);
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
    public List loadUsers(FilterControls filter, String zoneName) throws DataAccessException { 
    	filter.add("zoneName", zoneName);
    	return loadPrincipals(filter, User.class.getName());
    }
    public List loadGroups(FilterControls filter, String zoneName) throws DataAccessException { 
    	filter.add("zoneName", zoneName);
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
    public int countUsers(FilterControls filter,  String zoneName) {
    	filter.add("zoneName", zoneName);
    	return getCoreDao().countObjects(User.class, filter);
    }    

 	
    public UserProperties loadUserProperties(Long userId) {
    	UserPropertiesPK id = new UserPropertiesPK(userId);
        UserProperties uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);         
 		if (uProps == null) {
 			uProps = new UserProperties(id);
  			getCoreDao().saveNewSession(uProps);
  			uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
        }
 		return uProps;
    }
 
 
	public Group loadGroup(final Long groupId, String zoneName)  {
		Group group = (Group)getHibernateTemplate().get(Group.class, groupId);
		if (group == null) {throw new NoGroupByTheIdException(groupId);}
        //make sure from correct zone
        if ((zoneName != null ) && !group.getZoneName().equals(zoneName)) {throw new NoGroupByTheIdException(groupId);}
		return group;
	}
	public List loadGroups(Collection ids, String zoneName) {
		return getCoreDao().loadObjects(ids, Group.class, zoneName);
	}
    /**
     * Return count of users matching filter
     */
    public int countGroups(FilterControls filter, String zoneName) {
    	filter.add("zoneName", zoneName);
       	return getCoreDao().countObjects(Group.class, filter);
    }   
 
	/**
	 * Given a set of principal ids, return all userIds that represent userIds in 
	 * the original list, or members of groups and their nested groups.
	 * This is used to turn a distribution list or usersIds only.
	 * Use when don't need to load the entire object
	 * @param Set of principalIds
	 * @returns Set of userIds
	 */
	public Set explodeGroups(final Set ids) {   
		if ((ids == null) || ids.isEmpty()) return new TreeSet();
	    Set users = (Set)getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                    Set result = new TreeSet(ids);
                    List mems;
                    Set currentIds = new HashSet(ids);
                    while (!currentIds.isEmpty()) {
                    	Criteria crit = session.createCriteria(Membership.class);
                     	Disjunction dis = Expression.disjunction();
                       	Long id	;
                       	for (Iterator iter=currentIds.iterator(); iter.hasNext();) {
                       		id = (Long)iter.next();
                       		if (id != null) {
                       			dis.add(Expression.eq("groupId", id));
                       		}
                       	}
                        
                       	crit.add(dis);                       	
                       	mems = crit.list();
                       	currentIds.clear();
						for (int i=0; i<mems.size(); ++i) {
							Membership m = (Membership)mems.get(i);
							result.remove(m.getGroupId());
							//potential user - may be another group
							result.add(m.getUserId());
							currentIds.add(m.getUserId());
						}
                        	
                    }
                    return result;
                }
            }
        );
		return users;		
	}
	/**
	 * Get the Membership of a group.  Does not explode nested groups.
	 * Does not load the group object
	 * @param groupId
	 * @result List of <code>Membership</code>
	 */
	public List getMembership(final Long groupId) {
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
	public Set getAllGroupMembership(final Long principalId) {
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
   			getCoreDao().saveNewSession(seen);
   			//quick write
   			seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, userId);   			
   		}
   		return seen;
	}

}
