package com.sitescape.ef.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;

import org.hibernate.Hibernate;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.Criteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.UserPropertiesPK;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.NoGroupByTheIdException;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.NoPrincipalByTheIdException;
import com.sitescape.ef.domain.NoWorkspaceByTheNameException;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.Workspace;

import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.util.Constants;
/**
 * @author Jong Kim
 *
 */
public class CoreDaoImpl extends HibernateDaoSupport implements CoreDao {
	protected Log logger = LogFactory.getLog(getClass());
	private ObjectControls userControls = new ObjectControls(User.class);
	private ObjectControls groupControls = new ObjectControls(Group.class);
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
            		Object [] filterValues = filter.getFilterValues();
            		if (filterValues != null) {
            			for (int i=0; i<filterValues.length; ++i) {
            				q.setParameter(i, filterValues[i]);
            			}
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
   private List loadObjects(final Collection ids, final Class className) {
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
                        crit.add(dis);
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
            		Object [] filterValues = filter.getFilterValues();
            		if (filterValues != null) {
            			for (int i=0; i<filterValues.length; ++i) {
            				q.setParameter(i, filterValues[i]);
            			}
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
        if (binder == null) {throw new NoFolderByTheIdException(binderId);};
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
    /*
     *  (non-Javadoc)
     * @see com.sitescape.ef.dao.CoreDao#loadPrincipal(java.lang.Long, java.lang.Long)
     */
    public Principal loadPrincipal(final Long prinId, String zoneName) {
        Principal principal = (Principal)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
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
            			return session.createQuery("from com.sitescape.ef.domain.Principal p where p.zoneName = :zone and p.id in (:pList)")
            			.setString("zone", zoneName)
            			.setParameterList("pList", ids)
            			.list();
            		}
           	}
        );
        return result;
     }
  
    public void disablePrincipals(final Collection ids, final String zoneName) {
    	List result = loadPrincipals(ids, zoneName);
    	for (int i=0; i<result.size(); ++i) {
    		Principal p = (Principal)result.get(i);
    		p.setDisabled(true);
    	}
 /* TODO - this isn't working, but should      
  * getHibernateTemplate().execute(
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
 */
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

	public List loadUsers(Collection ids) {
		return loadObjects(ids, User.class);
	}
	public List loadEnabledUsers(Collection ids) {
		List users = loadUsers(ids);
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
     
    public int countUsers(FilterControls filter) {
    	return countObjects(User.class, filter);
    }    

    public List filterUsers(FilterControls filter) {
  		return loadObjects(userControls, filter);
    }
  	public List filterUsers(Principal principal, FilterControls filter) {
   		//TODO
   		return new ArrayList();
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
	public List loadGroups(Collection ids) {
		return loadObjects(ids, Group.class);
	}
    /**
     * Return count of users matching filter
     */
    public int countGroups(FilterControls filter) {
       	return countObjects(Group.class, filter);
    }   
    /**
     * Return list of users matching filter
     */
 
    public List filterGroups(final FilterControls filter) {
        return loadObjects(groupControls, filter);
    }
   	public List filterGroups(Principal principal, FilterControls filter) {
   		//TODO
   		return new ArrayList();
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
	 * Get definitions for the specified folder
	 * @param folder
	 * @param objectDesc - only the attributes are used
	 * @param filter
	 * @return
	 */
	public List loadDefinitions(final Folder folder, final ObjectControls objectDesc, final FilterControls filter) {      
		return (List)getHibernateTemplate().execute(
            new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException {
                    StringBuffer query = objectDesc.getSelect("this");
                	filter.appendFilter("this", query);
                    List results = session.createFilter(folder.getDefinitions(),
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
	public List loadPostings() {
		return getHibernateTemplate().find("from com.sitescape.ef.domain.PostingDef p order by p.emailAddress");
	}

}
