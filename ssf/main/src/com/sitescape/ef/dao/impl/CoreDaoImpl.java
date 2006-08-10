package com.sitescape.ef.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.Criteria;
import org.hibernate.ReplicationMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.List;
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
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.TitleException;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.CustomAttributeListElement;
import com.sitescape.ef.domain.DefinitionInvalidOperation;

import com.sitescape.ef.util.LongIdComparator;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.NoWorkspaceByTheNameException;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.NoTagByTheIdException;
import com.sitescape.ef.domain.NoEmailAliasByTheIdException;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.EmailAlias;
import com.sitescape.ef.domain.PostingDef;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.util.Constants;
import com.sitescape.util.Validator;
/**
 * @author Jong Kim
 *
 */
public class CoreDaoImpl extends HibernateDaoSupport implements CoreDao {
	protected Log logger = LogFactory.getLog(getClass());
    protected String[] binderTitleAttrs = new String[]{"parentBinder", "lower(title)"};

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
	 * This is an optimized delete.  Deletes associations directly without waiting for hibernate
	 * to query.
	 */	
	public void delete(final Binder binder) {
	   	getHibernateTemplate().execute(
	    	new HibernateCallback() {
	    		public Object doInHibernate(Session session) throws HibernateException {
        			//need to use ownerId, cause attachments/custom sets not indexed by binder
		   			deleteEntityAssociations("ownerId=" + binder.getId() + " and ownerType='" +
		   					binder.getEntityIdentifier().getEntityType().name() + "'", Binder.class);
		   			session.createQuery("DELETE com.sitescape.ef.domain.PostingDef where binder=:owner")
	       				.setLong("owner", binder.getId().longValue())
	       				.executeUpdate();
		   			session.createQuery("DELETE com.sitescape.ef.domain.Notification where binder=:owner")
		   				.setLong("owner", binder.getId().longValue())
		   				.executeUpdate();
		   			session.createQuery("DELETE com.sitescape.ef.domain.UserProperties where binderId=:owner")
		   				.setLong("owner", binder.getId().longValue())
		   				.executeUpdate();
		   			Connection connect = session.connection();
		   			try {
		   				Statement s = connect.createStatement();
		   				s.executeUpdate("delete from SS_DefinitionMap where binder=" + binder.getId());
		   				s.executeUpdate("delete from SS_WorkflowMap where binder=" + binder.getId());
		   			} catch (SQLException sq) {
		   				throw new HibernateException(sq);
		   			}
   		   			//delete ratings/visits for these entries
 		   			session.createQuery("Delete com.sitescape.ef.domain.Rating where entityId=:entityId and entityType=:entityType")
     	   				.setLong("entityId", binder.getId())
		   			  	.setParameter("entityType", binder.getEntityIdentifier().getEntityType().getValue())
		   				.executeUpdate();
   		   			
 		   			//delete tags for these entries
 		   			session.createQuery("Delete com.sitescape.ef.domain.Tag where entity_id=:entityId and entity_type=:entityType")
     	   				.setLong("entityId", binder.getId())
		   			  	.setParameter("entityType", binder.getEntityIdentifier().getEntityType().getValue())
		   				.executeUpdate();

		   			//delete tags owned by these entries
 		   			session.createQuery("Delete com.sitescape.ef.domain.Tag where owner_id=:entityId and owner_type=:entityType")
     	   				.setLong("entityId", binder.getId())
		   			  	.setParameter("entityType", binder.getEntityIdentifier().getEntityType().getValue())
		   				.executeUpdate();
 		   			//will this be a problem if the entry is proxied??
		   			session.createQuery("DELETE com.sitescape.ef.domain.Binder where id=" + binder.getId())
		   				.executeUpdate();
		   			session.getSessionFactory().evictCollection("com.sitescape.ef.domain.Binder.binders", binder.getParentBinder().getId());
		   			session.getSessionFactory().evict(Binder.class, binder.getId());
		   			session.evict(binder);
		   			return null;
    	   		}
    	   	}
    	 );    	
 
	    			
	}
	public void deleteEntityAssociations(final String whereClause, final Class clazz) {
	   	getHibernateTemplate().execute(
	    	   	new HibernateCallback() {
	    	   		public Object doInHibernate(Session session) throws HibernateException {
	    	   		session.createQuery("DELETE com.sitescape.ef.domain.Attachment where " + whereClause)
	    	   			.executeUpdate();
	    	   		session.createQuery("DELETE com.sitescape.ef.domain.Event where " + whereClause)
	       	   			.executeUpdate();
	       	   		session.createQuery("DELETE com.sitescape.ef.domain.CustomAttribute where " + whereClause)
	  	   				.executeUpdate();
	       	   		try {
	       	   			if (clazz.newInstance() instanceof WorkflowSupport) {
	       	   			session.createQuery("DELETE com.sitescape.ef.domain.WorkflowState where " + whereClause)
	       	   				.executeUpdate();
	       	   			}
	       	   		} catch (Exception ex) {};
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
     	   		String whereClause = "ownerId=" + id.getEntityId() + " and ownerType=" + id.getEntityType().name();
     	   		deleteEntityAssociations(whereClause, entity.getClass());

	   			//delete ratings/visits for these entries
	   			session.createQuery("Delete com.sitescape.ef.domain.Rating where entityId=:entityId and entityType=:entityType")
	   				.setLong("entityId", entity.getId())
	   			  	.setParameter("entityType", entity.getEntityIdentifier().getEntityType().getValue())
	   				.executeUpdate();
		   		//delete tags for these entries
		   		session.createQuery("Delete com.sitescape.ef.domain.Tag where entity_id=:entityId and entity_type=:entityType")
 	   				.setLong("entityId", entity.getId())
	   			  	.setParameter("entityType", entity.getEntityIdentifier().getEntityType().getValue())
	   				.executeUpdate();

	   			//delete tags owned by these entries
		   		session.createQuery("Delete com.sitescape.ef.domain.Tag where owner_id=:entityId and owner_type=:entityType")
 	   				.setLong("entityId", entity.getId())
	   			  	.setParameter("entityType", entity.getEntityIdentifier().getEntityType().getValue())
	   				.executeUpdate();
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
	public List loadObjects(Class className, FilterControls filter) {
		return loadObjects(new ObjectControls(className), filter);
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
                         Criteria crit = session.createCriteria(className)
                        	.add(Expression.in(Constants.ID, ids));
 
                        if (!Validator.isNull(zoneName))
                        	crit.add(Expression.eq("zoneName", zoneName));
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
	
	public float averageColumn(final Class clazz, final String column, final FilterControls filter) {
		Float result = (Float)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	StringBuffer query = new StringBuffer();
                  	query.append(" select avg(x." + column + ") from x in class " + clazz.getName());
                 	filter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = filter.getFilterValues();
            		for (int i=0; i<filterValues.size(); ++i) {
            			q.setParameter(i, filterValues.get(i));
            		}
 	                 List result = q.list();
                  	 Iterator itr = result.iterator();

                	 if (itr.hasNext()) {
                		Float count = (Float)itr.next();
                	 	return count;
             		}
                	
                	return null;
               }
            }
		);
       if (result==null) return 0;
	   return result.floatValue();	
	}
	public long sumColumn(final Class clazz, final String column, final FilterControls filter) {
		Long result = (Long)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
		        	StringBuffer query = new StringBuffer();
                  	query.append(" select sum(x." + column + ") from x in class " + clazz.getName());
                 	filter.appendFilter("x", query);
                  	Query q = session.createQuery(query.toString());
            		List filterValues = filter.getFilterValues();
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
    public void validateTitle(Binder binder, String title) throws TitleException {
    	//ensure title is unique
        if (Validator.isNull(title)) throw new TitleException("");
    	
    	Object[] cfValues = new Object[]{binder, title.toLowerCase()};	
    	FilterControls filter = new FilterControls(binderTitleAttrs, cfValues);
	
    	if (!loadObjects(new ObjectControls(Binder.class, new String[]{"id"}), filter).isEmpty()) {
    		 throw new TitleException(title);
    	}
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

	public List loadDefinitions(String zoneName) {
		OrderBy order = new OrderBy();
		order.addColumn("type");
		order.addColumn("name");
		FilterControls filter = new FilterControls("zoneName", zoneName);
		filter.setOrderBy(order);
    	return loadObjects(new ObjectControls(Definition.class), filter);
	}
	public List loadDefinitions(String zoneName, int type) {
		OrderBy order = new OrderBy();
		order.addColumn("name");
		FilterControls filter = new FilterControls(new String[]{"zoneName", "type"}, new Object[]{zoneName, Integer.valueOf(type)});
		filter.setOrderBy(order);
    	return loadObjects(new ObjectControls(Definition.class), filter);
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
	               		int count = countObjects(com.sitescape.ef.domain.FolderEntry.class, new FilterControls("entryDef", def));
	               		if (count > 0) throw new DefinitionInvalidOperation("Definition in use");
	               		count = countObjects(com.sitescape.ef.domain.Principal.class, new FilterControls("entryDef", def));
	               		if (count > 0) throw new DefinitionInvalidOperation("Definition in use");
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
	               		int count = countObjects(com.sitescape.ef.domain.WorkflowState.class, new FilterControls("definition", def));
	               		if (count > 0) throw new DefinitionInvalidOperation("Definition in use");

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
	
	public List loadCommunityTagsByOwner(final EntityIdentifier ownerId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
                 		.add(Expression.eq("public",true))
                 		.addOrder(Order.asc("name"))
                  		.list();
	                }
	            }
	        );
		
	}
	
	public Tag loadTagById(final String tagId) {
		return (Tag)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("id", tagId));
	                }
	            }
	        );
		
	}
	
	public List loadAllTagsByEntity(final EntityIdentifier entityId) {
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
	
	public List loadCommunityTagsByEntity(final EntityIdentifier entityId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
       					.add(Expression.eq("public", true))
                 		.addOrder(Order.asc("name"))
	                 	.list();
	                }
	            }
	        );
		
	}
	public List loadPersonalEntityTags(final EntityIdentifier entityId, final EntityIdentifier ownerId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("entityIdentifier.entityId", entityId.getEntityId()))
       					.add(Expression.eq("entityIdentifier.type", entityId.getEntityType().getValue()))
                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
       					.add(Expression.eq("public",false))
                 		.addOrder(Order.asc("name"))
	                 	.list();
	                }
	            }
	        );
		
	}
	public List loadPersonalTags(final EntityIdentifier ownerId) {
		return (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                 	return session.createCriteria(Tag.class)
                 		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
       					.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
       					.add(Expression.eq("public",false))
                 		.addOrder(Order.asc("name"))
	                 	.list();
	                }
	            }
	        );
		
	}	
}
