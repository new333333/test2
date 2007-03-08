package com.sitescape.team.dao;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.ObjectControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityDashboard;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.NoFolderByTheIdException;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.UserDashboard;
import com.sitescape.team.domain.Workspace;

/**
 * @author Jong Kim
 *
 */
public interface CoreDao {
	public void executeUpdate(String update);
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
	public void update(Object obj);
	/**
	 * 
	 * @param obj
	 */
	public void delete(Object obj);
	public void delete(Binder binder);
//    public void delete(DefinableEntity entity);
	public void delete(Definition def);
	public void deleteEntityAssociations(String whereClause, Class clazz);

//	public void deleteEntityAssociations(String whereClause, Class clazz);
	public Object load(Class className, String id);
	public Object load(Class className, Long id);
	public List loadObjects(ObjectControls objs, FilterControls filter);
    public SFQuery queryObjects(ObjectControls objs, FilterControls filter);
	public List loadObjectsCacheable(ObjectControls objs, FilterControls filter);
	public List loadObjects(Class className, FilterControls filter);
	public List loadObjectsCacheable(Class className, FilterControls filter);
    public List loadObjects(Collection ids, Class className, Long zoneId);
	public List loadObjects(String query, Map namedValues);
	/**
	 * Performance optimization.
	 * Load a list of objects and eagerly fetch listed collections
	 * 
	 * @param ids
	 * @param className
	 * @param zoneId
	 * @param collections
	 * @return
	 */
	public List loadObjects(Collection ids, Class className, Long zoneId, List collections);
    public List findCompanies();
	public int countObjects(Class clazz, FilterControls filter);
	public float averageColumn(Class clazz, String column, FilterControls filter);
	public long sumColumn(Class clazz, String column, FilterControls filter);

	public Workspace findTopWorkspace(String zoneName);
	/**
     * 
     * @param binderId
     * @param zoneId
     * @return
     * @throws DataAccessException
     * @throws NoFolderByTheIdException
     */
    public Binder loadBinder(Long binderId, Long zoneId);
    public Binder loadReservedBinder(String reservedId, Long zoneId);
    	   
	public Definition loadDefinition(String defId, Long zoneId);   
    public List loadDefinitions(Long zoneId);
    public List loadDefinitions(Long zoneId, int type);
    
    public List loadConfigurations(Long zoneId);
    public List loadConfigurations(Long zoneId, int type);
 
    public List loadPostings(Long zoneId);
	public PostingDef loadPosting(String aliasId, Long zoneId);
	
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
 

    public void updateFileName(Binder binder, DefinableEntity entity, String oldName, String newName);
    public void registerFileName(Binder binder, DefinableEntity entity, String name);
    public void unRegisterFileName(Binder binder, String name);
    public void clearFileNames(Binder binder);
    public  Long findFileNameEntryId(Binder binder, String name);

    public void updateTitle(Binder binder, DefinableEntity entity, String oldName, String newName);
    public void registerTitle(Binder binder, DefinableEntity entity);
    public void unRegisterTitle(Binder binder, String name);
    public void clearTitles(Binder binder);

}
