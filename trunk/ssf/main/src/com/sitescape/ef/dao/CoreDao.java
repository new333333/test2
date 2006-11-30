package com.sitescape.ef.dao;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.ObjectControls;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.BinderConfig;
import com.sitescape.ef.domain.Dashboard;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityDashboard;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.PostingDef;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.TitleException;
import com.sitescape.ef.domain.UserDashboard;
import com.sitescape.ef.domain.Workspace;

/**
 * @author Jong Kim
 *
 */
public interface CoreDao {
	/**
	 * 
	 * @parm obj
	 * @throws DataAccessException
	 */
	public void clear();
	public boolean isDirty();
	public void flush();
	public void refresh(Object obj);
	public void replicate(Object obj);
	public void save(Object obj);
	public void save(Collection objs);
	public Object saveNewSession(Object obj);
	public Object merge(Object obj); 
	public void evict(Object obj); 
	/**
	 * 
	 * @param obj
	 */
	public void delete(Object obj);
	public void delete(Binder binder);
    public void delete(DefinableEntity entity);
	public void delete(Definition def);
	public void deleteEntityAssociations(String whereClause, Class clazz);
	public Object load(Class className, String id);
	public Object load(Class className, Long id);
	public List loadObjects(ObjectControls objs, FilterControls filter);
	public List loadObjectsCacheable(ObjectControls objs, FilterControls filter);
	public List loadObjects(Class className, FilterControls filter);
	public List loadObjectsCacheable(Class className, FilterControls filter);
    public List loadObjects(Collection ids, Class className, String zoneName);
	public List loadObjects(String query, Map namedValues);
	/**
	 * Performance optimization.
	 * Load a list of objects and eagerly fetch listed collections
	 * 
	 * @param ids
	 * @param className
	 * @param zoneName
	 * @param collections
	 * @return
	 */
	public List loadObjects(Collection ids, Class className, String zoneName, List collections);
    public void validateTitle(Binder binder, String title) throws TitleException;
   	public List findCompanies();
	public int countObjects(Class clazz, FilterControls filter);
	public float averageColumn(Class clazz, String column, FilterControls filter);
	public long sumColumn(Class clazz, String column, FilterControls filter);

	public Workspace findTopWorkspace(String zoneName);
	/**
     * 
     * @param binderId
     * @param zoneName
     * @return
     * @throws DataAccessException
     * @throws NoFolderByTheIdException
     */
    public Binder loadBinder(Long binderId, String zoneName);
    public Binder loadReservedBinder(String reservedId, String zoneName);
    	   
	public Definition loadDefinition(String defId, String zoneName);   
    public List loadDefinitions(String zoneName);
    public List loadDefinitions(String zoneName, int type);
    
	public BinderConfig loadConfiguration(String defId, String zoneName);   
    public List loadConfigurations(String zoneName);
    public List loadConfigurations(String zoneName, int type);
 
    public List loadPostings(String zoneName);
	public PostingDef loadPosting(String aliasId, String zoneName);
	
    public void bulkLoadCollections(Collection entries);
    
	public List loadCommunityTagsByEntity(EntityIdentifier entityId);
	public List loadCommunityTagsByOwner(EntityIdentifier ownerId);
	public List loadPersonalEntityTags(EntityIdentifier entityId, EntityIdentifier ownerId);
	public List loadPersonalTags(EntityIdentifier ownerId);
	public List loadAllTagsByEntity(EntityIdentifier entityId);
	public Map loadAllTagsByEntity(Collection entityIds);
	public Tag loadTagById(String id);
	public List loadSubscriptionByEntity(final EntityIdentifier entityId);
	
	public UserDashboard loadUserDashboard(EntityIdentifier ownerId, Long binderId);
	public EntityDashboard loadEntityDashboard(EntityIdentifier ownerId);
	public Dashboard loadDashboard(String id);
 }