package com.sitescape.ef.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;

import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.Criteria;
import org.hibernate.ReplicationMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.CustomAttributeListElement;
import com.sitescape.ef.util.LongIdComparator;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.UserPropertiesPK;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.NoGroupByTheIdException;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.NoPrincipalByTheIdException;
import com.sitescape.ef.domain.NoWorkspaceByTheNameException;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.NoEmailAliasByTheIdException;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.EmailAlias;
import com.sitescape.ef.domain.PostingDef;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.util.Constants;
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
	public void delete(Object obj) {
        getHibernateTemplate().delete(obj);
    }
    public void delete(final Collection objs) {
        getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                    	 Iterator iter = objs.iterator();
                     	 while (iter.hasNext()) {
                     	 	session.delete(iter.next());                     	 	
                     	 }
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
	public List loadObjects(final ObjectControls objs, final FilterControls filter) {
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
 	                return q.list();
	            }
	        }
	     );
	}
	/**
	 * Load a list of objects, OR'ing ids
	 * @param ids
	 * @param className
	 * @return
	 */
   private List loadObjects(final Collection ids, final Class className, final String zoneName) {
        if ((ids == null) || ids.isEmpty()) return new ArrayList();
        List result = (List)getHibernateTemplate().execute(
            new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        //Hibernate doesn't like the ? in the in clause
                        //List list = session.createCriteria(Principal.class)
                        //     		.add(Expression.in(Constants.ID, ids))
                        //    		.list();
                        //return list;
                        Criteria crit = session.createCriteria(className);
                        Disjunction dis = Expression.disjunction();
                        Iterator iter = ids.iterator();
                        Object id;
                        while (iter.hasNext()) {
                            id = iter.next();
                            if (id != null) {
                                dis.add(Expression.eq(Constants.ID, id));
                            }
                        }
                        if (Validator.isNull(zoneName))
                        	crit.add(dis);
                        else {
                        	Conjunction con = Expression.conjunction();
                        	con.add(Expression.eq("zoneName", zoneName));
                        	con.add(dis);
                        	crit.add(con);
                        	
                        }
                        return crit.list();
                        
                    }
            }
        );
        return result;
        
    }	
	public int countObjects(final Class clazz, final FilterControls filter) {
		Integer result = (Integer)getHibernateTemplate().execute(
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
                  	 Iterator itr = result.iterator();

                	 if (itr.hasNext()) {
                	 	Integer count = (Integer)itr.next();
                	 	return count;
             		}
                	
                	return null;
               }
            }
		);
       if (result==null) return 0;
	   return result.intValue();	
	}
	public List findCompanies() {
		return (List)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
                 	return session.createQuery("select distinct x.zoneName from com.sitescape.ef.domain.Principal x")
                 	.list();
               }
            }
		);
	}
	public Workspace findTopWorkspace(final String zoneName) {
       return (Workspace)getHibernateTemplate().execute(
               new HibernateCallback() {
                   public Object doInHibernate(Session session) throws HibernateException {
                       Workspace workspace = (Workspace)session.createQuery("from com.sitescape.ef.domain.Workspace w" +
                       		" where w.zoneName=? and w.name=? and w.owningWorkspace is null")
                            .setParameter(0, zoneName)
                            .setParameter(1, zoneName)
                            .setCacheable(true)
                            .uniqueResult();
                       if (workspace == null) {
                           throw new NoWorkspaceByTheNameException(zoneName); 
                       }
                       return workspace;
                   }
               }
            );

	}
	
	/**
	 * Load binder and validate it belongs to the zone
	 * @param binderId
	 * @param zoneName
	 * @return
	 */
    public Binder loadBinder(Long binderId, String zoneName) {  
		Binder binder = (Binder)load(Binder.class, binderId);
        if (binder == null) {throw new NoBinderByTheIdException(binderId);};
        if ((zoneName != null ) && !binder.getZoneName().equals(zoneName)) {
        	throw new NoBinderByTheIdException(binderId);
        }
        return binder;
    }

    public Binder findBinderByName(final String binderName, final String zoneName) {
        return (Binder)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        Binder binder = (Binder)session.getNamedQuery("find-Binder-Company")
                             		.setString(ParameterNames.BINDER_NAME, binderName)
                             		.setString(ParameterNames.COMPANY_ID, zoneName)
                             		.uniqueResult();
                        if (binder == null) {
                            throw new NoBinderByTheNameException(binderName); 
                        }
                        return binder;
                    }
                }
             );
    }
    /**
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
    /**
     * Load 1 Principal and all its collections
     * @param entryId
     * @param zoneName
     * @return
     * @throws DataAccessException
     */
    public Principal loadFullPrincipal(final Long entryId, final String zoneName) throws DataAccessException {
    	return loadPrincipal(entryId, zoneName);
    	/*        return (Principal)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List results = session.createCriteria(Principal.class)
                        	.add(Expression.eq("id", entryId))
                        	.setFetchMode("HCustomAttributes", FetchMode.JOIN)
                        	.setFetchMode("HAttachments", FetchMode.JOIN)
                        	.setFetchMode("HWorkflowStates", FetchMode.JOIN)	
                        	.setFetchMode("entryDef", FetchMode.SELECT)	
                        	.setFetchMode("parentBinder", FetchMode.SELECT)	
                            .list();
                        if (results.size() == 0)  throw new NoPrincipalByTheIdException(entryId);
                        //because of join may get non-distinct results (wierd)
                        Principal entry = (Principal)results.get(0);
                        if ((zoneName != null ) && !entry.getZoneName().equals(zoneName)) {
                           	throw new NoPrincipalByTheIdException(entryId);
                        }
                        return entry;
                    }
                }
             );
*/
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
    	User user = (User)load(User.class, userId);
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
		return loadObjects(ids, User.class, zoneName);
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
    public int countUsers(FilterControls filter) {
    	return countObjects(User.class, filter);
    }    

 	
    public UserProperties loadUserProperties(Long userId) {
    	UserPropertiesPK id = new UserPropertiesPK(userId);
        UserProperties uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);         
 		if (uProps == null) {
 			uProps = new UserProperties(id);
  			saveNewSession(uProps);
  			uProps = (UserProperties)getHibernateTemplate().get(UserProperties.class, id);
        }
 		return uProps;
    }
 
 
	public Group loadGroup(final Long groupId, String zoneName)  {
		Group group = (Group)load(Group.class, groupId);
		if (group == null) {throw new NoGroupByTheIdException(groupId);}
        //make sure from correct zone
        if ((zoneName != null ) && !group.getZoneName().equals(zoneName)) {throw new NoGroupByTheIdException(groupId);}
		return group;
	}
	public List loadGroups(Collection ids, String zoneName) {
		return loadObjects(ids, Group.class, zoneName);
	}
    /**
     * Return count of users matching filter
     */
    public int countGroups(FilterControls filter) {
       	return countObjects(Group.class, filter);
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
	public Definition loadDefinition(String defId, String zoneName) {
  		Definition def = (Definition)load(Definition.class, defId);
        if (def == null) {throw new NoDefinitionByTheIdException(defId);}
        //make sure from correct zone
        if (!def.getZoneName().equals(zoneName)) {throw new NoDefinitionByTheIdException(defId);}
  		return def;
	}
	/**
	 * Get definitions for the specified binder
	 * @param binder
	 * @param objectDesc - only the attributes are used
	 * @param filter
	 * @return
	 */
	public List loadDefinitions(final Binder binder, final ObjectControls objectDesc, final FilterControls filter) {      
		return (List)getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                    StringBuffer query = objectDesc.getSelect("this");
                	filter.appendFilter("this", query);
                    List results = session.createFilter(binder.getDefinitions(),
                            query.toString()) 
					.list();
                    return results;                           
                }
            }
        );                
	}
	public List loadDefinitions(String zoneName) {
    	return loadObjects(new ObjectControls(Definition.class), new FilterControls("zoneName", zoneName));
	}
	public List loadEmailAliases(String zoneName) {
    	return loadObjects(new ObjectControls(EmailAlias.class), new FilterControls("zoneName", zoneName));
	}
	public EmailAlias loadEmailAlias(String aliasId, String zoneName) {
 		EmailAlias alias = (EmailAlias)load(EmailAlias.class, aliasId);
        if (alias == null) {throw new NoEmailAliasByTheIdException(aliasId);}
        //make sure from correct zone
        if (!alias.getZoneName().equals(zoneName)) {throw new NoEmailAliasByTheIdException(aliasId);}
  		return alias;
		
	}
	
	/**
	 * Perform a write of a new object now using a new Session so we can commit it fast
	 * @param obj
	 */
	public void saveNewSession(Object obj) {
       	SessionFactory sf = getSessionFactory();
    	Session s = sf.openSession();
    	try {
    		s.save(obj);
    		s.flush();
    	} finally {
    		s.close();
    	}
		
	}
	public List loadPostings(String zoneName) {
    	return loadObjects(new ObjectControls(PostingDef.class), new FilterControls("zoneName", zoneName));
	}
	public SeenMap loadSeenMap(Long userId) {
   		SeenMap seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, userId);
   		if (seen == null) {
   			seen = new SeenMap(userId);
   			saveNewSession(seen);
   			//quick write
   			seen =(SeenMap)getHibernateTemplate().get(SeenMap.class, userId);   			
   		}
   		return seen;
	}
	//build collections manually as an optimization for indexing
	//evict from session cache, so not longer available to everyone else
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
                	String type = entry.getAnyOwnerType();
                	//Load workflow states
                	List objs = session.createCriteria(WorkflowState.class)
                    					.add(Expression.eq("owner.ownerType", type))
                    					.add(Expression.in("owner.ownerId", ids))
                    					.addOrder(Order.asc("owner.ownerId"))
										.list();
                   
                   	readObjs.addAll(objs);
                	HashSet tSet;
                   	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (Entry)iter.next();
                   		tSet = new HashSet();
                   		for (int i=0; i<objs.size(); ++i) {
                   			WorkflowState obj = (WorkflowState)objs.get(i);
                   			if (entry.equals(obj.getOwner().getEntry())) {
                   				tSet.add(obj);
                   			} else break;
                   		}
                   		entry.setIndexWorkflowStates(tSet);
                   		objs.removeAll(tSet);
                    }
                	//Load attachments
                   	objs = session.createCriteria(Attachment.class)
                     	.add(Expression.eq("owner.ownerType", type))
                    	.add(Expression.in("owner.ownerId", ids))
                  		.addOrder(Order.asc("owner.ownerId"))
                  		.list();
                   	readObjs.addAll(objs);
                   	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (Entry)iter.next();
                   		tSet = new HashSet();
                   		while (objs.size() > 0) {
                   			Attachment obj = (Attachment)objs.get(0);
                  			if (entry.equals(obj.getOwner().getEntry())) {
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
                     	.add(Expression.eq("owner.ownerType", type))
                    	.add(Expression.in("owner.ownerId", ids))
                 		.addOrder(Order.asc("owner.ownerId"))
                  		.list();
                   	readObjs.addAll(objs);
                 	for (Iterator iter=sorted.iterator(); iter.hasNext();) {
                   		entry = (Entry)iter.next();
                   		tSet = new HashSet();
                   		for (int i=0; i<objs.size(); ++i) {
                   			Event obj = (Event)objs.get(i);
                   			if (entry.equals(obj.getOwner().getEntry())) {
                   				tSet.add(obj);
                   			} else break;
                   		}
                   		entry.setIndexEvents(tSet);
                   		objs.removeAll(tSet);
                    }
                	//Load customAttributes
                 	objs = session.createCriteria(CustomAttribute.class)
                 		.add(Expression.eq("owner.ownerType", type))
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
                   			if (entry.equals(obj.getOwner().getEntry())) {
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
 }
