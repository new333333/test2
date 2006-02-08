package com.sitescape.ef.ldap.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.hibernate.SessionFactory;
import org.hibernate.Interceptor;
import com.sitescape.ef.util.DirtyInterceptor;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.util.CollectionUtil;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.util.SessionUtil;

/**
 * Provides transaction-enabled operations for use by synchronizing users/groups with ldap 
 */
public class LdapHelper {
	private CoreDao coreDao;
	private ProcessorManager processorManager;
	
    protected CoreDao getCoreDao() {
		return coreDao;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	/**
	 * Loaded by Spring context
	 */
	protected ProcessorManager getProcessorManager() {
		return processorManager;
	}
	public void setProcessorManager(ProcessorManager processorManager) {
	    this.processorManager = processorManager;
	} 	
	/**
	 * Update a user with attributes specified in the map.
	 *
	 * @param zoneName
	 * @param loginName
	 * @param mods
	 * @throws NoUserByTheNameException
	 */
	public void updateUser(String zoneName, String loginName, Map mods) throws NoUserByTheNameException {
		User profile = coreDao.findUserByName(loginName, zoneName); 
		EntryBuilder.updateEntry(profile, mods);
		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	profile.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
		processor.indexEntry(profile);
	}	
	/**
	 * Update users with their own map of updates
	 * @param users - Map indexed by user id, value is map of updates for a user
	 */
	public void updateUsers(String zoneName, Map users) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
	   	List foundEntries = coreDao.loadUsers(users.keySet(), zoneName);
	    Interceptor dirty = SessionUtil.getInterceptor();
	    if ((dirty != null) && (dirty instanceof DirtyInterceptor)) {
	    	((DirtyInterceptor)dirty).getDirtyList().clear();
	    }
	    EntryBuilder.updateEntries(foundEntries, users);
	    if ((dirty != null) && (dirty instanceof DirtyInterceptor)) {
	    	getCoreDao().flush();
	    	List changes = ((DirtyInterceptor)dirty).getDirtyList();
	    	foundEntries.clear();
	    	for (int i=0; i<changes.size(); ++i) {
	    		Object o = changes.get(i);
	    		if (o instanceof User)
	    			foundEntries.add(o);
	    	}
	    }
	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
    	processor.indexEntry(foundEntries);
	}

    /**
     * Update groups with their own updates
     * @param groups - Map keyed by group id, value is map of updates for a group
     */    
    public void updateGroups(String zoneName, Map groups) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
    	List foundEntries = coreDao.loadGroups(groups.keySet(), RequestContextHolder.getRequestContext().getZoneName());
	    Interceptor dirty = SessionUtil.getInterceptor();
	    if ((dirty != null) && (dirty instanceof DirtyInterceptor)) {
	    	((DirtyInterceptor)dirty).getDirtyList().clear();
	    }
      	EntryBuilder.updateEntries(foundEntries, groups);
	    if ((dirty != null) && (dirty instanceof DirtyInterceptor)) {
	    	getCoreDao().flush();
	    	List changes = ((DirtyInterceptor)dirty).getDirtyList();
	    	foundEntries.clear();
	    	for (int i=0; i<changes.size(); ++i) {
	    		Object o = changes.get(i);
	    		if (o instanceof Group)
	    			foundEntries.add(o);
	    	}
	    }
	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    processor.indexEntry(foundEntries);
    }
    public void updateMembership(Long groupId, Collection newMembers, Collection reservedIds) {
		//have a list of users, now compare with what exists already
		List oldMembers = getCoreDao().getMembership(groupId);
		Set newM = CollectionUtil.differences(newMembers, oldMembers);
		Set remM = CollectionUtil.differences(oldMembers, newMembers);

		//only remove entries that are not reserved
		for (Iterator iter=remM.iterator(); iter.hasNext();) {
			Membership c = (Membership)iter.next();
			if (!reservedIds.contains(c.getUserId())) {
				getCoreDao().delete(c);
			}
		}
		
		getCoreDao().save(newM);
		SessionFactory sF = (SessionFactory)SpringContextUtil.getBean("sessionFactory");
		sF.evictCollection("com.sitescape.ef.domain.Principal.HMemberOf");
		
    }
    /**
     * Create users.  
     * @param zoneName
     * @param users - Map keyed by user id, value is map of attributes
     * @return
     */
    public List createUsers(String zoneName, Map users) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
		try {
    		List result = EntryBuilder.buildEntries(ReflectHelper.classForName("com.sitescape.ef.domain.User"), users.values());
    		for (int i=0; i<result.size(); ++i) {
    			User u = (User)result.get(i);
    			u.setParentBinder(pf);
    		}
    		getCoreDao().save(result);
    	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
                	pf, ProfileCoreProcessor.PROCESSOR_KEY);
    	    processor.indexEntry(result);
    		return result;
    	} catch (ClassNotFoundException ce) {
    		throw new ConfigurationException(ce);
    	} catch (Exception ex) {
    		System.out.println(ex);
    		return new ArrayList();
    	}
    	
    }
    /**
     * Create groups.
     * @param zoneName
     * @param groups - Map keyed by user id, value is map of attributes
     * @return
     */
    public List createGroups(String zoneName, Map groups) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
       	try {
       		List result = EntryBuilder.buildEntries(ReflectHelper.classForName("com.sitescape.ef.domain.Group"), groups.values());
       		getCoreDao().save(result);
       		for (int i=0; i<result.size(); ++i) {
    			Group g = (Group)result.get(i);
    			g.setParentBinder(pf);
    		}

       		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
                	pf, ProfileCoreProcessor.PROCESSOR_KEY);
    	    processor.indexEntry(result);
       		return result;
    	} catch (ClassNotFoundException ce) {
    		throw new ConfigurationException(ce);
    	}
    }
    public void disableUsers(String zoneName, Collection ids) {
    	coreDao.disablePrincipals(ids, zoneName);
    }
    public void disableGroups(String zoneName, Collection ids) {
    	coreDao.disablePrincipals(ids, zoneName);
   }
}
