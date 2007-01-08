package com.sitescape.ef.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
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
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.sitescape.ef.ErrorCodes;
import com.sitescape.ef.NoObjectByTheIdException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.dao.util.SFQuery;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.CustomAttributeListElement;
import com.sitescape.ef.domain.Dashboard;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinitionInvalidOperation;
import com.sitescape.ef.domain.EntityDashboard;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.LibraryEntry;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.domain.NoConfigurationByTheIdException;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.TitleException;
import com.sitescape.ef.domain.UserDashboard;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.LongIdComparator;
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
     public SFQuery queryObjects(final ObjectControls objs, final FilterControls filter) { 
        Query query = (Query)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
    	            	StringBuffer query = objs.getSelectAndFrom("x");
                     	filter.appendFilter("x", query);
                      	Query q = session.createQuery(query.toString());
                		List filterValues = filter.getFilterValues();
               			for (int i=0; i<filterValues.size(); ++i) {
               				q.setParameter(i, filterValues.get(i));
                		}
     	                return q;
    	            }
                }
            );  
       return new SFQuery(query);
    }	
	/* 
	 * Becuse we have relationships within the same table, 
	 * not all databases handle on-delete correctly.  
	 * We are forced to do it ourselves
	 */
 	public void deleteEntityAssociations(final String whereClause, final Class clazz) {
	   	getHibernateTemplate().execute(
	    	   	new HibernateCallback() {
	    	   		public Object doInHibernate(Session session) throws HibernateException {
	    	   			//mysql won't delete these in 1 statement cause of foreign key constraints
	    	   		session.createQuery("DELETE com.sitescape.ef.domain.VersionAttachment where " + whereClause)
	    	   			.executeUpdate();
	    	   		session.createQuery("DELETE com.sitescape.ef.domain.Attachment where " + whereClause)
    	   			.executeUpdate();
	       	   		session.createQuery("DELETE com.sitescape.ef.domain.CustomAttributeListElement where " + whereClause)
	  	   				.executeUpdate();
	       	   		session.createQuery("DELETE com.sitescape.ef.domain.CustomAttribute where " + whereClause)
  	   				.executeUpdate();
/*
 * hibernate can deal with these cause on-delete cascade will work
 * 	    	   		session.createQuery("DELETE com.sitescape.ef.domain.Event where " + whereClause)
       	   			.executeUpdate();
	       	   		try {
	       	   			if (clazz.newInstance() instanceof WorkflowSupport) {
	       	   			session.createQuery("DELETE com.sitescape.ef.domain.WorkflowState where " + whereClause)
	       	   				.executeUpdate();
	       	   			session.createQuery("DELETE com.sitescape.ef.domain.WorkflowResponse where " + whereClause)
       	   				.executeUpdate();
	       	   			}
	       	   		} catch (Exception ex) {};
*/
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

		   			//free alias for someone else
	    			PostingDef def = binder.getPosting();
	    			if (def != null) def.setBinder(null);
	    			session.flush();
	    			Connection connect = session.connection();
		   			try {
		   				Statement s = connect.createStatement();
		   				s.executeUpdate("delete from SS_DefinitionMap where binder=" + binder.getId());
		   				s.executeUpdate("delete from SS_WorkflowMap where binder=" + binder.getId());
		   				s.executeUpdate("delete from SS_Notifications where binderId=" + binder.getId());
		   			} catch (SQLException sq) {
		   				throw new HibernateException(sq);
		   			}
		   			session.createQuery("DELETE com.sitescape.ef.domain.UserProperties where binderId=:owner")
		   				.setLong("owner", binder.getId())
		   				.executeUpdate();
		   			//delete reserved names for entries/subfolders
		   			session.createQuery("DELETE com.sitescape.ef.domain.LibraryEntry where binderId=:binderId")
		   				.setLong("binderId", binder.getId())
		   				.executeUpdate();
		   			//delete reserved names for self which is registered in parent space
		   			if (binder.getParentBinder() != null)
		   			session.createQuery("DELETE com.sitescape.ef.domain.LibraryEntry where binderId=:binderId")
		   				.setLong("binderId", binder.getParentBinder().getId())
		   				.executeUpdate();
		   			delete((DefinableEntity)binder);
		   			session.getSessionFactory().evictCollection("com.sitescape.ef.domain.Binder.binders", binder.getParentBinder().getId());
		   			session.evict(binder);
		   			
		   			return null;
    	   		}
    	   	}
    	 );    	
 
	    			
	}


	/**
     * Delete an object.  Delete associations not maintained with foreign-keys.
      * @param entry
     */
    protected void delete(final DefinableEntity entity) {
    	getHibernateTemplate().execute(
    	   	new HibernateCallback() {
    	   		public Object doInHibernate(Session session) throws HibernateException {
     	   		EntityIdentifier id = entity.getEntityIdentifier();
     	   		String whereClause = "ownerId=" + id.getEntityId() + " and ownerType='" + id.getEntityType().name() + "'";
     	   		deleteEntityAssociations(whereClause, entity.getClass());

	   			//delete ratings/visits for these entries
	   			session.createQuery("Delete com.sitescape.ef.domain.Rating where entityId=:entityId and entityType=:entityType")
	   				.setLong("entityId", entity.getId())
	   			  	.setParameter("entityType", entity.getEntityIdentifier().getEntityType().getValue())
	   				.executeUpdate();
	   			//delete subscriptions to these entries
	   			session.createQuery("Delete com.sitescape.ef.domain.Subscription where entityId=:entityId and entityType=:entityType")
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
	public List loadObjects(ObjectControls objs, FilterControls filter) {
		return loadObjects(objs, filter, false);
	}
	public List loadObjectsCacheable(ObjectControls objs, FilterControls filter) {
		return loadObjects(objs, filter, true);
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
		return (List)getHibernateTemplate().execute(
		        new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
	                  	Query q = session.createQuery(query);
	                  	if (values != null) {
	                  		for (Iterator iter=values.entrySet().iterator(); iter.hasNext();) {
	                  			Map.Entry me = (Map.Entry)iter.next();
	                  			Object val = me.getValue();
	                  			if (val instanceof Collection) {
	                  				q.setParameterList((String)me.getKey(), (Collection)val);
	                  			} else if (val instanceof Object[]) {
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
	/**
	 * Return a list ob objects
	 */
	public List loadObjects(Class className, FilterControls filter) {
		return loadObjects(new ObjectControls(className), filter);
	}
	public List loadObjectsCacheable(Class className, FilterControls filter) {
		return loadObjects(new ObjectControls(className), filter, true);
	}
	/**
	 * Load a list of objects, OR'ing ids
	 * @param ids
	 * @param className
	 * @return
	 */
   public List loadObjects(final Collection ids, final Class className, final Long zoneId) {
        if ((ids == null) || ids.isEmpty()) return new ArrayList();
        List result = (List)getHibernateTemplate().execute(
            new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                         Criteria crit = session.createCriteria(className)
                        	.add(Expression.in(Constants.ID, ids));
 
                        if (zoneId != null)
                        	crit.add(Expression.eq("zoneId", zoneId));
                        return crit.list();
                        
                    }
            }
        );
        return result;
        
    }	
   public List loadObjects(final Collection ids, final Class className, final Long zoneId, final List collections) {
       if ((ids == null) || ids.isEmpty()) return new ArrayList();
       List result = (List)getHibernateTemplate().execute(
           new HibernateCallback() {
                   public Object doInHibernate(Session session) throws HibernateException {
                        Criteria crit = session.createCriteria(className)
                       	.add(Expression.in(Constants.ID, ids));

                        if (zoneId != null)
                        	crit.add(Expression.eq("zoneId", zoneId));
                       for (int i=0; i<collections.size(); ++i) {
                    	   crit.setFetchMode((String)collections.get(i), FetchMode.JOIN);
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
 	                 if (result.isEmpty()) return null;
               	 	 return result.get(0);
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

    // This code is used to reserve a name.  The binder field is used to check
	// uniqueness within a binder so it can be viewed by webdav.  The name is either an attachment
	// name belonging to an entry in the binder, or the title of a sub-folder.  Note, attachments on
	// the binder itself are not visible hear and not visible through webdav.
	// the entity field combined with the binder field lets us get the the entry that
	// owns the attachment.  (also allows cleanup on delete entries)
	// Create our own session cause failures clear the existing session and don't want to 
	// necessarily cancel the running transaction.
	// It assumes the combination of binderId and entityId is enough to identify an entry
    public void registerLibraryEntry(Binder binder, DefinableEntity entity, String name) throws TitleException {
        if (Validator.isNull(name)) throw new TitleException("");
      	SessionFactory sf = getSessionFactory();
    	Session s = sf.openSession();
    	try {
    		LibraryEntry le = new LibraryEntry(binder.getId(), name);
    		if (entity != null) le.setEntityId(entity.getId());
    		s.save(le);
    		s.flush();
    	} catch (Exception ex) {
    		throw new TitleException(name, ex);
    	} finally {
    		s.close();
    	}    	
    }
    //create our own session cause failures clear the existing session
    public void unRegisterLibraryEntry(Binder binder, String name) {
      	SessionFactory sf = getSessionFactory();
    	Session s = sf.openSession();
    	try {
    		LibraryEntry le = new LibraryEntry(binder.getId(), name);
			LibraryEntry exist = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, le);
			if (exist != null) delete(le);
    		s.flush();
    	} catch (Exception ex) {
				logger.error("Error removeing library entry for: " + binder + " file" +  ex.getMessage());
	   	} finally {
    		s.close();
    	}    	
    }
    //done in the current transaction on a title rename
    public void updateLibraryName(Binder binder, DefinableEntity entity, String oldName, String newName) throws TitleException {
        if (Validator.isNull(newName)) throw new TitleException("");
        if (newName.equalsIgnoreCase(oldName)) return;
        LibraryEntry le=null;
 		if (oldName != null) {
	        LibraryEntry newLe = new LibraryEntry(binder.getId(), oldName);
			le = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, newLe);
			if (le != null) {
				//it exists, is it ours?
				if (!binder.equals(entity)) {
					if (entity.getId().equals(le.getEntityId())) {
						//delete the old one; delete cause changing primary key
						delete(le);
						flush();
					}
					newLe.setEntityId(entity.getId());
				} else if (le.getEntityId() == null) {
					//belongs to this binder; delete cause changing primary key
					delete(le);
					flush();
				}

				newLe.setName(newName);
				save(newLe);
				return;
			}
		}
		if (le == null) {
			try {
				le = new LibraryEntry(binder.getId(), newName);
				LibraryEntry exist = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, le);
				if (exist == null) {
					if (!binder.equals(entity)) le.setEntityId(entity.getId());
					save(le);
				}
				else throw new TitleException(newName);
			} catch (Exception ex) {
				throw new TitleException(newName);
			}
		}   	
    }
    public  Long findLibraryEntryId(Binder binder, String name) {
    	LibraryEntry le = (LibraryEntry)getHibernateTemplate().get(LibraryEntry.class, new LibraryEntry(binder.getId(), name));
    	if (le == null) throw new NoObjectByTheIdException(ErrorCodes.NoLibraryEntryByTheIdException, new Object[]{binder.getId(), name});
    	return le.getEntityId();

    }
    public void clearLibraryEntries(Binder binder) {
    	executeUpdate("delete from com.sitescape.ef.domain.LibraryEntry where binderId=" +
    			binder.getId() + " and not entityId is null");
    	
    }
    public List findCompanies() {
		return (List)getHibernateTemplate().execute(
		    new HibernateCallback() {
		        public Object doInHibernate(Session session) throws HibernateException {
                 	return session.createCriteria(Workspace.class)
             				.add(Expression.eq("internalId", ObjectKeys.TOP_WORKSPACE_ID))
             				.list();
               }
            }
		);
	}
	public Workspace findTopWorkspace(final String zoneName) {
        return (Workspace)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List results = session.createCriteria(Workspace.class)
                             		.add(Expression.eq("internalId", ObjectKeys.TOP_WORKSPACE_ID))
                             		.add(Expression.eq("name", zoneName))
                             		.setCacheable(true)
                             		.list();
                        if (results.isEmpty()) {
                            throw new NoBinderByTheNameException(ObjectKeys.TOP_WORKSPACE_ID); 
                        }
                        return (Workspace)results.get(0);
                    }
                }
             );

	}
	
	/**
	 * Load binder and validate it belongs to the zone
	 * @param binderId
	 * @param zoneId
	 * @return
	 */
    public Binder loadBinder(Long binderId, Long zoneId) {  
		Binder binder = (Binder)load(Binder.class, binderId);
        if (binder == null) {throw new NoBinderByTheIdException(binderId);};
        if (!binder.getZoneId().equals(zoneId)) {
        	throw new NoBinderByTheIdException(binderId);
        }
        return binder;
    }

    public Binder loadReservedBinder(final String reservedId, final Long zoneId) {
        return (Binder)getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException {
                        List results = session.createCriteria(Binder.class)
                             		.add(Expression.eq("internalId", reservedId))
                             		.add(Expression.eq("zoneId", zoneId))
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
 
 
	public Definition loadDefinition(String defId, Long zoneId) {
  		Definition def = (Definition)load(Definition.class, defId);
        if (def == null) {throw new NoDefinitionByTheIdException(defId);}
        //make sure from correct zone
        if (!def.getZoneId().equals(zoneId)) {throw new NoDefinitionByTheIdException(defId);}
  		return def;
	}

	public List loadDefinitions(Long zoneId) {
		OrderBy order = new OrderBy();
		order.addColumn("type");
		order.addColumn("name");
		FilterControls filter = new FilterControls("zoneId", zoneId);
		filter.setOrderBy(order);
    	return loadObjects(new ObjectControls(Definition.class), filter);
	}
	public List loadDefinitions(Long zoneId, int type) {
		OrderBy order = new OrderBy();
		order.addColumn("name");
		FilterControls filter = new FilterControls(new String[]{"zoneId", "type"}, new Object[]{zoneId, Integer.valueOf(type)});
		filter.setOrderBy(order);
    	return loadObjectsCacheable(new ObjectControls(Definition.class), filter);
	}
	
	public BinderConfig loadConfiguration(String defId, Long zoneId) {
		BinderConfig def = (BinderConfig)load(BinderConfig.class, defId);
        if (def == null) {throw new NoConfigurationByTheIdException(defId);}
        //make sure from correct zone
        if (!def.getZoneId().equals(zoneId)) {throw new NoConfigurationByTheIdException(defId);}
  		return def;
	}

	public List loadConfigurations(Long zoneId) {
		OrderBy order = new OrderBy();
		order.addColumn("definitionType");
		order.addColumn("title");
		FilterControls filter = new FilterControls("zoneId", zoneId);
		filter.setOrderBy(order);
    	return loadObjects(new ObjectControls(BinderConfig.class), filter);
	}
	public List loadConfigurations(Long zoneId, int type) {
		OrderBy order = new OrderBy();
		order.addColumn("title");
		FilterControls filter = new FilterControls(new String[]{"zoneId", "definitionType"}, new Object[]{zoneId, Integer.valueOf(type)});
		filter.setOrderBy(order);
    	return loadObjects(new ObjectControls(BinderConfig.class), filter);
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
	
	/**
	 * Perform a write of a new object now using a new Session so we can commit it fast
	 * @param obj
	 */
	public Object saveNewSession(Object obj) {
       	SessionFactory sf = getSessionFactory();
    	Session s = sf.openSession();
    	try {
    		s.save(obj);
    		s.flush();
    	} finally {
    		s.close();
    	}
    	//attach to current session. This will fail if read only
    	// by that should mean no-one will update it so that is okay
    	try {
    		update(obj);
    	} catch (InvalidDataAccessApiUsageException da) {};
    	
    	return obj;
		
	}
	public List loadPostings(Long zoneId) {
    	return loadObjects(new ObjectControls(PostingDef.class), new FilterControls("zoneId", zoneId));
	}
	public PostingDef loadPosting(String postingId, Long zoneId) {
		PostingDef post = (PostingDef)load(PostingDef.class, postingId);
        if (post == null) {throw new NoObjectByTheIdException(ErrorCodes.NoPostingByTheIdException, postingId);}
        //make sure from correct zone
        if (!post.getZoneId().equals(zoneId)) {throw new NoObjectByTheIdException(ErrorCodes.NoPostingByTheIdException, postingId);}
  		return post;
		
	}

	//build collections manually as an optimization for indexing
	//evict from session cache, so not longer available to everyone else
	//The entries must be of the same type
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
        Tag t =(Tag)getHibernateTemplate().get(Tag.class, tagId);
        if (t != null) return t;
        throw new NoObjectByTheIdException(ErrorCodes.NoTagByTheIdException, tagId);
	}
	//The entries must be of the same type
	public Map loadAllTagsByEntity(final Collection entityIds) {
		if (entityIds.isEmpty()) return new HashMap();
		
		List<Tag> tags = (List)getHibernateTemplate().execute(
	            new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException {
	                	List ids = new ArrayList();
	                	EntityIdentifier id =null;
	                	for (Iterator iter=entityIds.iterator(); iter.hasNext();) {
		                	id = (EntityIdentifier)iter.next();
		                	ids.add(id.getEntityId());
	                	}
	                	
	                	return session.createCriteria(Tag.class)
                 		.add(Expression.in("entityIdentifier.entityId", ids))
       					.add(Expression.eq("entityIdentifier.type", id.getEntityType().getValue()))
                 		.addOrder(Order.asc("entityIdentifier.entityId"))
	                 	.list();
	                }
	            }
	        );
		Map result = new HashMap();
		for (Iterator iter=entityIds.iterator(); iter.hasNext();) {
			EntityIdentifier id = (EntityIdentifier)iter.next();
			List tList = new ArrayList();
			
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
	public List loadSubscriptionByEntity(final EntityIdentifier entityId) {
		return (List)getHibernateTemplate().execute(
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
	private List loadObjects(final ObjectControls objs, final FilterControls filter, final boolean cacheable) {
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
           			q.setCacheable(cacheable);
 	                return q.list();
	            }
	        }
	     );
	}
	public UserDashboard loadUserDashboard(final EntityIdentifier ownerId, final Long binderId) {
		List result = (List)getHibernateTemplate().execute(
				new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
	            		return session.createCriteria(UserDashboard.class)
	            		.add(Expression.eq("binderId", binderId))
	            		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
	            		.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
	            		.setCacheable(true)
	            		.list();
		            }
		        }
		 );
		if (result.isEmpty()) return null;
		return (UserDashboard)result.get(0);
	}
	public EntityDashboard loadEntityDashboard(final EntityIdentifier ownerId) {
		List result = (List)getHibernateTemplate().execute(
				new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException {
		            		return session.createCriteria(EntityDashboard.class)
		            		.add(Expression.eq("ownerIdentifier.entityId", ownerId.getEntityId()))
		            		.add(Expression.eq("ownerIdentifier.type", ownerId.getEntityType().getValue()))
		            		.setCacheable(true)
		            		.list();
		            		
		            }
		        }
		 );
		if (result.isEmpty()) return null;
		return (EntityDashboard)result.get(0);
	}

	public Dashboard loadDashboard(String id) {
		Dashboard d = (Dashboard)getHibernateTemplate().get(Dashboard.class, id);
        if (d != null) return d;
        throw new NoObjectByTheIdException(ErrorCodes.NoDashboardByTheIdException, id);
	}
	public void executeUpdate(final String query) {
    	getHibernateTemplate().execute(
        	   	new HibernateCallback() {
        	   		public Object doInHibernate(Session session) throws HibernateException {
    		   			session.createQuery(query)
		   				.executeUpdate();
          	   		
          	   			return null;

        	   		}
        	   	}
       	   	);
	}
	
}
