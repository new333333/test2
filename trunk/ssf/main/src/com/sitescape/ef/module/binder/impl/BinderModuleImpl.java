
package com.sitescape.ef.module.binder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Tag;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoBinderByTheNameException;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.binder.EntryProcessor;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.ObjectBuilder;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.security.function.WorkAreaFunctionMembershipManager;
/**
 * @author Janet McCann
 *
 */
public class BinderModuleImpl extends CommonDependencyInjection implements BinderModule {
	private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;

	private Binder loadBinder(Long binderId) {
		return getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
	}
	private EntryProcessor loadProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
		return (EntryProcessor)getProcessorManager().getProcessor(binder, EntryProcessor.PROCESSOR_KEY);
	}
	public Binder getBinderByName(String binderName) 
   			throws NoBinderByTheNameException, AccessControlException {
		Binder binder = getCoreDao().findBinderByName(binderName, RequestContextHolder.getRequestContext().getZoneName());
	    
		// Check if the user has "read" access to the binder.
        getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
 
		return binder;
	}
   
	
	public Binder getBinder(Long binderId)
			throws NoBinderByTheIdException, AccessControlException {
		Binder binder = loadBinder(binderId);

		// Check if the user has "read" access to the binder.
        getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);

        return binder;        
	}

	public Map getFunctionMembership(Long binderId) {
		Map result = new HashMap();
		Binder binder = loadBinder(binderId);
        getAccessControlManager().checkOperation(binder, WorkAreaOperation.CHANGE_ACCESS_CONTROL);
        result.put(ObjectKeys.BINDER, binder);
        result.put(ObjectKeys.FUNCTIONS, getFunctionManager().findFunctions(binder.getZoneName())); 
        result.put(ObjectKeys.FUNCTION_MEMBERSHIP, getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(binder.getZoneName(), binder));
        return result;
	}
	public void setFunctionMembershipInherited(Long binderId, boolean inherit) 
    throws AccessControlException {
    	Binder binder = loadBinder(binderId);
    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.CHANGE_ACCESS_CONTROL);    	
    	binder.setFunctionMembershipInherited(inherit);
    } 
    public Binder modifyConfiguration(Long binderId, List definitionIds, Map workflowAssociations) 
	throws AccessControlException {
		Binder binder = modifyConfiguration(binderId, definitionIds);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS);    	
		binder.setProperty(ObjectKeys.BINDER_WORKFLOW_ASSOCIATIONS, workflowAssociations);
		return binder;
	}
	public Binder modifyConfiguration(Long binderId, List definitionIds) throws AccessControlException {
		Binder binder = loadBinder(binderId);
		String companyId = binder.getZoneName();
		List definitions = new ArrayList(); 
		Definition def;
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
		//	Build up new set - domain object will handle associations
		if (definitionIds != null) {
			for (int i=0; i<definitionIds.size(); ++i) {
				def = getCoreDao().loadDefinition((String)definitionIds.get(i), companyId);
				//	TODO:	getAccessControlManager().checkAcl(def, AccessType.READ);
				definitions.add(def);
			}
		}
	
		binder.setDefinitions(definitions);
		return binder;
	}
	/**
	 * Get tags owned by this binder
	 */
	public List getTags(Long binderId) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
		List tags = new ArrayList<Tag>();		
		getCoreDao().loadTagsByOwner(binder.getEntityIdentifier());
		return tags;		
	}
	/**
	 * Modify tag owned by this binder
	 * @see com.sitescape.ef.module.binder.BinderModule#modifyTag(java.lang.Long, java.lang.String, java.util.Map)
	 */
	public void modifyTag(Long binderId, String tagId, Map updates) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = coreDao.loadTagByOwner(tagId, binder.getEntityIdentifier());
	   	ObjectBuilder.updateObject(tag, updates);
	}
	/**
	 * Add a new tag, owned by this binder
	 */
	public void addTag(Long binderId, Map updates) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = new Tag();
	   	tag.setOwnerIdentifier(binder.getEntityIdentifier());
	  	ObjectBuilder.updateObject(tag, updates);
	  	coreDao.save(tag);   	
	}
	/**
	 * Delete a tag owned by this binder
	 */
	public void deleteTag(Long binderId, String tagId) {
		Binder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS);    	
	   	Tag tag = coreDao.loadTagByOwner(tagId, binder.getEntityIdentifier());
	   	getCoreDao().delete(tag);
	}
	/**
	 * Get tags owned by the entry
	 * @param binderId
	 * @param entryId
	 * @return
	 */
	public List getTags(Long binderId, Long entryId) {
		Binder binder = loadBinder(binderId);
		Entry entry = loadProcessor(binder).getEntry(binder, entryId, null);
		List tags = new ArrayList<Tag>();
		getCoreDao().loadTagsByOwner(entry.getEntityIdentifier());
		return tags;		
	}
	/**
	 * Modify a tag owned by this entry
	 * @param binderId
	 * @param entryId
	 * @param tagId
	 * @param updates
	 */
	public void modifyTag(Long binderId, Long entryId, String tagId, Map updates) {
		Binder binder = loadBinder(binderId);
		Entry entry = loadProcessor(binder).getEntry(binder, entryId, null);
	   	Tag tag = coreDao.loadTagByOwner(tagId, entry.getEntityIdentifier());
	   	ObjectBuilder.updateObject(tag, updates);
	}
	/**
	 * Add a tag owned by this entry
	 * @param binderId
	 * @param entryId
	 * @param updates
	 */
	public void addTag(Long binderId, Long entryId, Map updates) {
		Binder binder = loadBinder(binderId);
		Entry entry = loadProcessor(binder).getEntry(binder, entryId, null);
	   	Tag tag = new Tag();
	   	tag.setOwnerIdentifier(entry.getEntityIdentifier());
	  	ObjectBuilder.updateObject(tag, updates);
	  	coreDao.save(tag);   	
	}
	/**
	 * Delete a tag owned by this entry
	 * @param binderId
	 * @param entryId
	 * @param tagId
	 */
	public void deleteTag(Long binderId, Long entryId, String tagId) {
		Binder binder = loadBinder(binderId);
		Entry entry = loadProcessor(binder).getEntry(binder, entryId, null);
	   	Tag tag = coreDao.loadTagByOwner(tagId, entry.getEntityIdentifier());
	   	getCoreDao().delete(tag);
	}
  

}
