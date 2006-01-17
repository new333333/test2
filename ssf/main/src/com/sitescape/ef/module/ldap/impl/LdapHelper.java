package com.sitescape.ef.module.ldap.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.shared.EntryBuilder;
import com.sitescape.ef.util.ReflectHelper;

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
	 * Update a ssf user with attributes specified in the map.
	 *
	 * @param zoneName
	 * @param loginName
	 * @param mods
	 * @throws NoUserByTheNameException
	 */
	public void syncUser(String zoneName, String loginName, Map mods) throws NoUserByTheNameException {
		User profile = coreDao.findUserByName(loginName, zoneName); 
		EntryBuilder.updateEntry(profile, mods);
		ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	profile.getParentBinder(), ProfileCoreProcessor.PROCESSOR_KEY);
		processor.indexEntry(profile);
	}	
	/**
	 * Update users with their own updates
	 * @param users - Map indexed by user id, value is map of updates
	 */
	public void updateUsers(String zoneName, Map users) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
	   	List foundEntries = coreDao.loadUsers(users.keySet(), zoneName);
	   	EntryBuilder.updateEntries(foundEntries, users);
	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
	            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    processor.indexEntry(foundEntries);
	    
	}

    /**
     * Update groups with their own updates
     * @param groups - Map indexed by group id, value is map of updates
     */    
    public void updateGroups(String zoneName, Map groups) {
		ProfileBinder pf = getCoreDao().getProfileBinder(zoneName);
    	List foundEntries = coreDao.loadGroups(groups.keySet(), RequestContextHolder.getRequestContext().getZoneName());
       	EntryBuilder.updateEntries(foundEntries, groups);
	    ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager().getProcessor(
            	pf, ProfileCoreProcessor.PROCESSOR_KEY);
	    processor.indexEntry(foundEntries);
    }
 
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
    	}
    	
    }
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
