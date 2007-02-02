package com.sitescape.team.module.binder;

import java.util.HashSet;
import java.util.Set;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.SingletonViolationException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WfAcl;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.module.workflow.WorkflowUtils;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.acl.AccessType;
import com.sitescape.team.security.acl.AclControlled;
import com.sitescape.team.security.function.OperationAccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.SPropsUtil;

public class AccessUtils  {
	private static AccessUtils instance; // A singleton instance
	protected AccessControlManager accessControlManager;
	protected ProfileDao profileDao;
	public AccessUtils() {
		if(instance != null)
			throw new SingletonViolationException(AccessUtils.class);
		
		instance = this;
	}
    private static AccessUtils getInstance() {
    	return instance;
    }
	public void setAccessControlManager(AccessControlManager accessControlManager) {
		this.accessControlManager = accessControlManager;
	}
	protected AccessControlManager getAccessControlManager() {
		return accessControlManager;
	}
	protected static AccessControlManager getAccessManager() {
		return getInstance().accessControlManager;
	}
	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

    public static Set getReadAclIds(Entry entry) {
     	if (!(entry instanceof AclControlled)) return null;
        Set binderIds = getAccessManager().getWorkAreaAccessControl(entry.getParentBinder(), WorkAreaOperation.READ_ENTRIES);
	       
		Set<Long> entryIds = new HashSet<Long>();
	    if (entry instanceof WorkflowSupport) {
			WorkflowSupport wEntry = (WorkflowSupport)entry;
			if (wEntry.hasAclSet()) {
				//index binders access
				if (wEntry.checkWorkArea(AccessType.READ)) {
					entryIds.addAll(binderIds);
				}
				entryIds.addAll(wEntry.getAclSet().getMemberIds(AccessType.READ));
		        //replaces reserved ownerId with entry owner
		        if (entryIds.remove(ObjectKeys.OWNER_USER_ID)) entryIds.add(wEntry.getOwnerId());
				
	    		if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_WF_ACCESS, false)) {
	    			//only index ids in both sets ie(AND)
	    			//remove ids in entryIds but not in binderIds
	    			entryIds.removeAll(CollectionUtil.differences(entryIds, binderIds));
	    		}

				//	no access specified, add binder default
				if (entryIds.isEmpty())
					entryIds.addAll(binderIds);
        		return entryIds;
			} else {
				//doesn't have any active workflow, use binder access
				return binderIds;
			}
		} else 
			//use binder access
			return binderIds;
    	 
     }
     public static Set getReadAclIds(Binder binder) {
        Set binderIds = new HashSet(getAccessManager().getWorkAreaAccessControl(binder, WorkAreaOperation.READ_ENTRIES));
  		return binderIds;
     	 
      }     	
	
	public static void readCheck(Entry entry) throws AccessControlException {
		readCheck(RequestContextHolder.getRequestContext().getUser(), entry);
	}
	public static void readCheck(User user, Entry entry) throws AccessControlException {
    	if (entry instanceof WorkflowSupport)
    		readCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
    	else 
    		readCheck(user, entry.getParentBinder(), (Entry)entry);
    		
   }
	private static void readCheck(User user, Binder binder, Entry entry) {
      	getAccessManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
    }
    private static void readCheck(User user, Binder binder, WorkflowSupport entry) throws AccessControlException {
		if (!entry.hasAclSet()) readCheck(user, binder, (Entry)entry);
       	try {
       		//see if pass binder test
       		readCheck(user, binder, (Entry)entry);
    	    //see if binder default is enough
    	    if (entry.checkWorkArea(AccessType.READ)) return;
		} catch (OperationAccessControlException ex) {
			//at this point we can stop if workflow cannot widen access
			// because the set cannot get any bigger
	 		if (!SPropsUtil.getBoolean(SPropsUtil.WIDEN_WF_ACCESS, false)) throw ex;
     	}
       //This basically AND's the binder and entry
        getAccessManager().checkAcl(user, binder, entry, AccessType.READ, false);
	}
    public static void modifyCheck(Entry entry) throws AccessControlException {
		modifyCheck(RequestContextHolder.getRequestContext().getUser(), entry);
    }
    public static void modifyCheck(User user, Entry entry) throws AccessControlException {
		if (entry instanceof WorkflowSupport)
    		modifyCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
    	else 
    		modifyCheck(user, entry.getParentBinder(), (Entry)entry);
    		
   }
    private static void modifyCheck(User user, Binder binder, Entry entry) {
       	try {
       		getAccessManager().checkOperation(user, binder, WorkAreaOperation.MODIFY_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (user.equals(entry.getCreation().getPrincipal())) 
      			getAccessManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_MODIFY);
      		else throw ex;
      	}
    }
     private static void modifyCheck(User user, Binder binder, WorkflowSupport entry) {
        if (!entry.hasAclSet()) modifyCheck(user, binder, (Entry)entry);
		    //see if folder default is enabled.
      	try {
       		//see if pass binder test
       		modifyCheck(user, binder, (Entry)entry);
    	    //see if binder default is enough
    	    if (entry.checkWorkArea(AccessType.WRITE)) return;
		} catch (OperationAccessControlException ex) {
			//at this point we can stop if workflow cannot widen access
			// because the set cannot get any bigger
			// note: modify is defined per binder.
    		if (!SPropsUtil.getBoolean(SPropsUtil.WIDEN_WF_ACCESS, false)) throw ex;
     	}
       //This basically AND's the binder and entry
        getAccessManager().checkAcl(user, binder, entry, AccessType.WRITE, false);   	
    }

     public static void deleteCheck(Entry entry) throws AccessControlException {
    	 deleteCheck(RequestContextHolder.getRequestContext().getUser(), entry);
     }
     public static void deleteCheck(User user, Entry entry) throws AccessControlException {
     	if (entry instanceof WorkflowSupport)
     		deleteCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
     	else 
     		deleteCheck(user, entry.getParentBinder(), (Entry)entry);
     		
    }
    private static void deleteCheck(User user, Binder binder, Entry entry) throws AccessControlException {
      	try {
       		getAccessManager().checkOperation(user, binder, WorkAreaOperation.DELETE_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (user.equals(entry.getCreation().getPrincipal())) 
   				getAccessManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_DELETE);
      		else throw ex;
      	}   
    }
    private static void deleteCheck(User user, Binder binder, WorkflowSupport entry)  throws AccessControlException {
        if (!entry.hasAclSet()) deleteCheck(user, binder, (Entry)entry);
	    //see if folder default is enabled.
        try {
        	//see if pass binder test
        	deleteCheck(user, binder, (Entry)entry);
        	//see if binder default is enough
        	if (entry.checkWorkArea(AccessType.DELETE)) return;
        } catch (OperationAccessControlException ex) {
        	//at this point we can stop if workflow cannot widen access
        	// because the set cannot get any bigger
        	// note: delete is defined per binder.
    		if (!SPropsUtil.getBoolean(SPropsUtil.WIDEN_WF_ACCESS, false)) throw ex;
        }
        //This basically AND's the binder and entry
        getAccessManager().checkAcl(user, binder, entry, AccessType.DELETE, false);   	 	
    }
     public static void overrideReserveEntryCheck(Entry entry) {
     	try {
     		getAccessManager().checkOperation(RequestContextHolder.getRequestContext().getUser(), entry.getParentBinder(), WorkAreaOperation.BINDER_ADMINISTRATION);
     	} catch (OperationAccessControlException ex) {
    		throw ex;
    	}
     }          
     
     public static void overrideReserveEntryCheck(Binder binder) {
        try {
        	getAccessManager().checkOperation(RequestContextHolder.getRequestContext().getUser(), binder, WorkAreaOperation.BINDER_ADMINISTRATION);
        } catch (OperationAccessControlException ex) {
       		throw ex;
       	}
     }     
     
     public static void checkTransitionIn(Binder binder, WorkflowSupport entry, Definition definition, String toState)  
     	throws AccessControlException {
     	 WfAcl acl = WorkflowUtils.getStateTransitionInAcl(definition, toState);
     	checkTransitionAcl(acl, binder, entry, "transitionIn");
     }
     public static void checkTransitionOut(Binder binder, WorkflowSupport entry, Definition definition, String toState)  
     	throws AccessControlException {
     	 WfAcl acl = WorkflowUtils.getStateTransitionOutAcl(definition, toState);
     	checkTransitionAcl(acl, binder, entry, "transitionOut");
     }
     private static void checkTransitionAcl(WfAcl acl, Binder binder, WorkflowSupport entry, String type)  
      	throws AccessControlException {
      	User user = RequestContextHolder.getRequestContext().getUser();
    	 try {
       		//see if pass binder test
       		modifyCheck(user, binder, (Entry)entry);
    	    //see if binder default is enough
    	    if (acl.isUseDefault()) return;
		} catch (OperationAccessControlException ex) {
			//at this point we can stop if workflow cannot widen access
			// because the set cannot get any bigger
			// note: modify is defined per binder.
    		if (!SPropsUtil.getBoolean(SPropsUtil.WIDEN_WF_ACCESS, false)) throw ex;
     	}
    	 //see if user can transition
		getAccessManager().testAcl(user, entry, acl.getPrincipals());
     }
    	    
}