package com.sitescape.team.module.binder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WfAcl;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.acl.AclAccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.FunctionManager;
import com.sitescape.team.security.function.OperationAccessControlException;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.SPropsUtil;

public class AccessUtils  {
	private static AccessUtils instance; // A singleton instance
	protected AccessControlManager accessControlManager;
	protected FunctionManager functionManager;
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
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
	protected FunctionManager getFunctionManager() {
		return functionManager;
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}

    public static Set getReadAccessIds(Entry entry) {
        Set binderIds = getInstance().getAccessControlManager().getWorkAreaAccessControl(entry.getParentBinder(), WorkAreaOperation.READ_ENTRIES);
	       
		Set<Long> entryIds = new HashSet<Long>();
	    if (entry instanceof WorkflowSupport) {
			WorkflowSupport wEntry = (WorkflowSupport)entry;
			if (wEntry.hasAclSet()) {
				//index binders access
				if (wEntry.isWorkAreaAccess(WfAcl.AccessType.read)) {
					entryIds.addAll(binderIds);
				}
				entryIds.addAll(wEntry.getStateMembers(WfAcl.AccessType.read));
		        //replaces reserved ownerId with entry owner
				
	    		if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
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
		} else {
			//use binder access
			return binderIds;
		}
    	 
     }
     public static Set getReadAccessIds(Binder binder) {
        return getInstance().getAccessControlManager().getWorkAreaAccessControl(binder, WorkAreaOperation.READ_ENTRIES);     	 
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
		getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
    }
    private static void readCheck(User user, Binder binder, WorkflowSupport entry) throws AccessControlException {
		if (!entry.hasAclSet()) readCheck(user, binder, (Entry)entry);
       	try {
       		//see if pass binder test
       		readCheck(user, binder, (Entry)entry);
    	    //see if binder default is enough
    	    if (entry.isWorkAreaAccess(WfAcl.AccessType.read)) return;
		} catch (OperationAccessControlException ex) {
			//at this point we can stop if workflow cannot widen access
			// because the set cannot get any bigger
	 		if (!SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) throw ex;
     	}
       //This basically AND's the binder and entry, since we already passed the binder
		checkAccess(user, entry, WfAcl.AccessType.read);
 	}
    
    private static void checkAccess(User user, WorkflowSupport entry, WfAcl.AccessType type) {
        if (ObjectKeys.SUPER_USER_INTERNALID.equals(user.getInternalId())) return;
        Set allowedIds = entry.getStateMembers(type);
        if (testAccess(user, allowedIds)) return;
        throw new AclAccessControlException(user.getName(), type.toString());
    }
    private static boolean testAccess(User user, Set allowedIds) {
        if (ObjectKeys.SUPER_USER_INTERNALID.equals(user.getInternalId())) return true;
     	Set principalIds = getInstance().getProfileDao().getPrincipalIds(user);
        for(Iterator i = principalIds.iterator(); i.hasNext();) {
            if (allowedIds.contains(i.next())) return true;
        }
        return true;
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
       		getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.MODIFY_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (user.equals(entry.getCreation().getPrincipal())) 
      			getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_MODIFY);
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
    	    if (entry.isWorkAreaAccess(WfAcl.AccessType.write)) return;
		} catch (OperationAccessControlException ex) {
			//at this point we can stop if workflow cannot widen access
			// because the set cannot get any bigger
    		if (!SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) throw ex;
     	}
	       //This basically AND's the binder and entry, since we already passed the binder
		checkAccess(user, entry, WfAcl.AccessType.write);
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
      		getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.DELETE_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (user.equals(entry.getCreation().getPrincipal())) 
   				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_DELETE);
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
        	if (entry.isWorkAreaAccess(WfAcl.AccessType.delete)) return;
        } catch (OperationAccessControlException ex) {
        	//at this point we can stop if workflow cannot widen access
        	// because the set cannot get any bigger
    		if (!SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) throw ex;
        }
        //This basically AND's the binder and entry, since we already passed the binder
		checkAccess(user, entry, WfAcl.AccessType.delete);
    }
     public static void overrideReserveEntryCheck(Entry entry) {
     	try {
     		getInstance().getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getUser(), entry.getParentBinder(), WorkAreaOperation.BINDER_ADMINISTRATION);
     	} catch (OperationAccessControlException ex) {
    		throw ex;
    	}
     }          
     
     public static void overrideReserveEntryCheck(Binder binder) {
        try {
        	getInstance().getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getUser(), binder, WorkAreaOperation.BINDER_ADMINISTRATION);
        } catch (OperationAccessControlException ex) {
       		throw ex;
       	}
     }     
     
     public static void checkTransitionIn(Binder binder, WorkflowSupport entry, Definition definition, String toState)  
     	throws AccessControlException {
     	checkTransitionAcl(binder, entry, WfAcl.AccessType.transitionIn);
     }
     public static void checkTransitionOut(Binder binder, WorkflowSupport entry, Definition definition, String toState)  
     	throws AccessControlException {
     	checkTransitionAcl(binder, entry, WfAcl.AccessType.transitionOut);
     }
     private static void checkTransitionAcl(Binder binder, WorkflowSupport entry, WfAcl.AccessType type)  
      	throws AccessControlException {
      	User user = RequestContextHolder.getRequestContext().getUser();
    	 try {
       		//see if pass binder test
       		modifyCheck(user, binder, (Entry)entry);
    	    //see if binder default is enough
       	    if (entry.isWorkAreaAccess(type)) return;
 		} catch (OperationAccessControlException ex) {
			//at this point we can stop if workflow cannot widen access
			// because the set cannot get any bigger
			// note: modify is defined per binder.
    		if (!SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) throw ex;
     	}
    	 //see if user can transition
		checkAccess(user, entry, type);
     }
     
  	public static boolean checkIfAllOperationsAllowed(Long functionId, WorkArea workArea) {
      	User user = RequestContextHolder.getRequestContext().getUser();
  		Function f = getInstance().functionManager.getFunction(user.getZoneId(), functionId);
  		return checkIfAllOperationsAllowed(f, workArea);
  	}
  	public static boolean checkIfAllOperationsAllowed(Function f, WorkArea workArea) {
		Iterator itOperations = f.getOperations().iterator();
		while (itOperations.hasNext()) {
			WorkAreaOperation o = (WorkAreaOperation) itOperations.next();
			if (!getInstance().getAccessControlManager().testOperation((WorkArea) workArea, o)) return false;
		}
		return true;
	}
    	    
    public static User getZoneSuperUser() {
        User user = RequestContextHolder.getRequestContext().getUser();
		User superUser = getInstance().getProfileDao().getReservedUser(ObjectKeys.SUPER_USER_INTERNALID, user.getZoneId());
		return superUser;
    }
}