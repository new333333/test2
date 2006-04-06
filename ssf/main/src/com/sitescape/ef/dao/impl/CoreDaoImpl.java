package com.sitescape.ef.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;

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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowControlledEntry;
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
import com.sitescape.ef.domain.NoTagByTheIdException;
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
	/**
	 * Delete the binder object and its assocations.
	 * Entries and child binders should already have been deleted
	 */	
	public void delete(final Binder binder) {
	   	getHibernateTemplate().execute(
	    	new HibernateCallback() {
	    		public Object doInHibernate(Session session) throws HibernateException {
	    		session.createQuery("DELETE com.sitescape.ef.domain.PostingDef where binder=:owner")
	       			.setLong("owner", binder.getId().longValue())
    	   			.executeUpdate();
  	   			Connection connect = session.connection();
   	   			try {
   	   				Statement s = connect.createStatement();
   	   				s.executeUpdate("delete from SS_DefinitionMap where forum=" + binder.getId());
   	   				s.executeUpdate("delete from SS_WorkflowMap where binder=" + binder.getId());
   	   				s.executeUpdate("delete from SS_UserProperties where folderId=" + binder.getId());
   	   			} catch (SQLException sq) {
   	   				throw new HibernateException(sq);
   	   			}
 	       	   		return null;
    	   		}
    	   	}
    	 );    	
	    			
	}
    /**
     * Delete an object and its assocations more efficiently then letting hibernate do it.
      * @param entry
     */
    public void delete(final DefinableEntity entity) {
    	getHibernateTemplate().execute(
    	   	new HibernateCallback() {
    	   		public Object doInHibernate(Session session) throws HibernateException {
     	   		EntityIdentifier id = entity.getEntityIdentifier();
    	   		session.createQuery("DELETE com.sitescape.ef.domain.Attachment where ownerId=:owner and ownerType=:type")
       	   			.setLong("owner", id.getEntityId().longValue())
       	   			.setString("type", id.getEntityType().name())
    	   			.executeUpdate();
       	   		//need to remove event assignments
 /*      	   		List eventIds = session.createQuery("select id from com.sitescape.ef.domain.Event where ownerId=:owner and ownerType=:type")
           	   			.setLong("owner", entry.getId().longValue())
           	   			.setString("type", AnyOwner.FOLDERENTRY)
           	   			.list();
       	   		if (!eventIds.isEmpty()) {
       	   			StringBuffer ids = new StringBuffer();
       	   			ids.append("(");
       	   			for (int i=0; i<eventIds.size(); ++i) {
       	   				ids.append("'" + eventIds.get(i) + "',");
       	   			}
       	   			ids.replace(ids.length()-1, ids.length(), ")");
       	   			Connection connect = session.connection();
       	   			try {
       	   				Statement s = connect.createStatement();
       	   				s.executeUpdate("delete from SS_AssignmentsMap where event in " + ids);
       	   			} catch (SQLException sq) {
       	   				throw new HibernateException(sq);
       	   			}
       	   		}
*/
       	   		session.createQuery("DELETE com.sitescape.ef.domain.Event where ownerId=:owner and ownerType=:type")
       	   			.setLong("owner", id.getEntityId().longValue())
       	   			.setString("type", id.getEntityType().name())
       	   			.executeUpdate();
       	   		session.createQuery("DELETE com.sitescape.ef.domain.CustomAttribute where ownerId=:owner and ownerType=:type")
        	   			.setLong("owner", id.getEntityId().longValue())
       	   			.setString("type", id.getEntityType().name())
  	   				.executeUpdate();
       	   		if (entity instanceof WorkflowSupport) {
       	   			session.createQuery("DELETE com.sitescape.ef.domain.WorkflowState where ownerId=:owner and ownerType=:type")
       	   				.setLong("owner", id.getEntityId().longValue())
       	   					.setString("type", id.getEntityType().name())
       	   						.executeUpdate();
       	   		}
       	   		//will this be a problem if the entry is proxied??
    	   		session.createQuery("DELETE  " + entity.getClass().getName() +   " where id=:id")
    	   			.setLong("id", id.getEntityId().longValue())
    	   			.executeUpdate();
    	   		session.getSessionFactory().evict(entity.getClass(), id.getEntityId());
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
   public List loadObjects(final Collection ids, final Class className, final String zoneName) {
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
                       		" where w.zoneName=? and w.name=? and w.parentBinder is null")
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
	public Tag loadTagByOwner(String id, EntityIdentifier owner) {
		Tag tag =(Tag)load(Tag.class, id);
		if (tag == null) throw new NoTagByTheIdException(id);
	   	if (!tag.isOwner(owner)) 
	   		throw new NoTagByTheIdException(id);
	   	return tag;

	}
	public List loadTagsByOwner(final EntityIdentifier ownerId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
                 		.addOrder(Order.asc("name"))
                  		.list();
	                }
	            }
	        );
		
	}
	public List loadTagsByEntity(final EntityIdentifier entityId) {
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
}
