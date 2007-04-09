/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.binder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

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
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.search.BasicIndexUtils;
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
import com.sitescape.util.Validator;

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

	/**
	 * Check Access based on the acl that the search engine would store.
	 * This is used for Rss and changeLogs which store those acls
	 * @param parent
	 * @param userIds - List of String ids
	 * @return
	 */
	public static boolean checkAccess(Element parent, Set<String> userIds) {			
		/*
		 * if !widen, then acl query is: (folderACL:1,2,3 AND entryAcl:all,1,2,3)
		 * 
		 * else ((folderAcl:1,2,3 AND entryAcl:all) OR (entryAcl:1,2,3))
		 */
		Element acl = (Element)parent.selectSingleNode(BasicIndexUtils.FOLDER_ACL_FIELD);
		String folderAcl = null;
		if (acl != null) folderAcl = acl.getText();
		if (folderAcl == null) folderAcl = "";
		String[] folderAclArray = folderAcl.split(" ");
	 
		acl = (Element)parent.selectSingleNode(BasicIndexUtils.ENTRY_ACL_FIELD);   	
		String entryAcl=null;
		if (acl != null) entryAcl = acl.getText();
		if (Validator.isNull(entryAcl)) entryAcl = BasicIndexUtils.READ_ACL_ALL;
		String []entryAclArray = entryAcl.split(" ");
		boolean widen = SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false);
		if (widen) {
			//((folderAcl:1 2 3 AND entryAcl:all) OR (entryAcl:1 2 3))
			if (entryAcl.equals(BasicIndexUtils.READ_ACL_ALL)) {
				//check folder for match
				for (int i=0; i<folderAclArray.length; ++i) {
					if (userIds.contains(folderAclArray[i])) return true;
				}
				return false;				
			} else {
				//check entry for match
				for (int i=0; i<entryAclArray.length; ++i) {
					if (userIds.contains(entryAclArray[i])) return true;
				}
				return false;
			}
		} else {
			//(folderACL:1,2,3 AND entryAcl:all,1,2,3)
			//check folder for match
			boolean found = false;
			for (int i=0; i<folderAclArray.length; ++i) {
				if (userIds.contains(folderAclArray[i])) {
					found = true;
					break;
				}
			}
			//done if didn't pass folderAcl
			if (!found) return false;				
			//check entry for match
			for (int i=0; i<entryAclArray.length; ++i) {
				if (userIds.contains(entryAclArray[i])) return true;
			}
			return false;
				
		}
	}	

	public static Set getReadAccessIds(Binder binder) {
        return getInstance().getAccessControlManager().getWorkAreaAccessControl(binder, WorkAreaOperation.READ_ENTRIES);     	 
	}     	
	
	public static void readCheck(Entry entry) throws AccessControlException {
		readCheck(RequestContextHolder.getRequestContext().getUser(), entry);
	}
	public static void readCheck(User user, Entry entry) throws AccessControlException {
        if (user.isSuper()) return;
    	if (entry instanceof WorkflowSupport)
    		readCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
    	else 
    		readCheck(user, entry.getParentBinder(), (Entry)entry);
    		
	}
	private static void readCheck(User user, Binder binder, Entry entry) {
		getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
    }
    private static void readCheck(User user, Binder binder, WorkflowSupport entry) throws AccessControlException {
		if (!entry.hasAclSet()) {
			readCheck(user, binder, (Entry)entry);
		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			try {
 				checkAccess(user, entry, WfAcl.AccessType.read);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(WfAcl.AccessType.read)) { 		
 					readCheck(user, binder, (Entry)entry);
 				} else throw ex;
 			}
 			
 		} else {
 			//must have READ access to binder AND entry
 			//see if pass binder test
 			readCheck(user, binder, (Entry)entry);
 			//	see if binder default is enough
 			if (entry.isWorkAreaAccess(WfAcl.AccessType.read)) return;
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, entry, WfAcl.AccessType.read);
  		}
	}
    
    private static void checkAccess(User user, WorkflowSupport entry, WfAcl.AccessType type) {
         Set allowedIds = entry.getStateMembers(type);
        if (testAccess(user, allowedIds)) return;
        throw new AclAccessControlException(user.getName(), type.toString());
    }
    private static boolean testAccess(User user, Set allowedIds) {
     	Set principalIds = getInstance().getProfileDao().getPrincipalIds(user);
        for(Iterator i = principalIds.iterator(); i.hasNext();) {
            if (allowedIds.contains(i.next())) return true;
        }
        return false;
    }
    public static void modifyCheck(Entry entry) throws AccessControlException {
		modifyCheck(RequestContextHolder.getRequestContext().getUser(), entry);
    }
    public static void modifyCheck(User user, Entry entry) throws AccessControlException {
        if (user.isSuper()) return;
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
 		if (!entry.hasAclSet()) {
			modifyCheck(user, binder, (Entry)entry);
		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			try {
 				//check explicit users
 				checkAccess(user, entry, WfAcl.AccessType.write);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(WfAcl.AccessType.write)) { 		
 					modifyCheck(user, binder, (Entry)entry);
 				} else throw ex;
 			}
 			
 		} else {
 			//must have READ access to binder AND modify to the entry
 			if (entry.isWorkAreaAccess(WfAcl.AccessType.write)) {
 				//optimzation: if pass modify binder check, don't need to do read binder check
 				// all done if binder access is enough
 				try {
					modifyCheck(user, binder, (Entry)entry);
					return;
 				} catch (AccessControlException ex) {} //move on to next checks
 			}
 			//see if pass binder READ test
 			readCheck(user, binder, (Entry)entry);
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, entry, WfAcl.AccessType.write);
  		}
    }

     public static void deleteCheck(Entry entry) throws AccessControlException {
    	 deleteCheck(RequestContextHolder.getRequestContext().getUser(), entry);
     }
     public static void deleteCheck(User user, Entry entry) throws AccessControlException {
        if (user.isSuper()) return;
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
		if (!entry.hasAclSet()) {
			deleteCheck(user, binder, (Entry)entry);
		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			try {
 				//check explicit users
 				checkAccess(user, entry, WfAcl.AccessType.delete);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(WfAcl.AccessType.delete)) { 		
 					deleteCheck(user, binder, (Entry)entry);
 				} else throw ex;
 			}
 			
 		} else {
 			//must have READ access to binder AND delete to the entry
 			if (entry.isWorkAreaAccess(WfAcl.AccessType.delete)) {
 				//optimzation: if pass delete binder check, don't need to do read binder check
 				// all done if binder access is enough
				try {
 					deleteCheck(user, binder, (Entry)entry);
					return;
 				} catch (AccessControlException ex) {} //move on to next checks
 			}
 			//see if pass binder READ test
 			readCheck(user, binder, (Entry)entry);
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, entry, WfAcl.AccessType.delete);
  		}
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
    	 WorkflowState ws = new WorkflowState();
    	 ws.setDefinition(definition);
    	 ws.setState(toState);
    	 checkTransitionAcl(binder, entry, ws, WfAcl.AccessType.transitionIn);
     }
     public static void checkTransitionOut(Binder binder, WorkflowSupport entry, Definition definition, String toState)  
     	throws AccessControlException {
       	 WorkflowState ws = new WorkflowState();
    	 ws.setDefinition(definition);
    	 ws.setState(toState);
    	 checkTransitionAcl(binder, entry, ws, WfAcl.AccessType.transitionOut);
     }
     private static void checkTransitionAcl(Binder binder, WorkflowSupport entry, WorkflowState state, WfAcl.AccessType type)  
      	throws AccessControlException {
      	User user = RequestContextHolder.getRequestContext().getUser();
        if (user.isSuper()) return;
		WfAcl acl = state.getAcl(type);
		if (acl == null) {
			modifyCheck(user, binder, (Entry)entry);
			return;
		}
		if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			//check explicit users
 			Set allowedIds = acl.getPrincipals();   
 			if (allowedIds.remove(ObjectKeys.OWNER_USER_ID)) allowedIds.add(entry.getOwnerId());
 			if (testAccess(user, allowedIds)) return;
 			
 			if (acl.isUseDefault()) { 		
 				modifyCheck(user, binder, (Entry)entry);
 			} else throw new AclAccessControlException(user.getName(), type.toString());

 			
 		} else {
 			//must have READ access to binder AND modify to the entry
			if (acl.isUseDefault()) { 		
				 //optimzation: if pass modify binder check, don't need to do read binder check
 				// all done if binder access is enough
 				try {
					modifyCheck(user, binder, (Entry)entry);
					return;
 				} catch (AccessControlException ex) {} //move on to next checks
 			}
 			//see if pass binder READ test
 			readCheck(user, binder, (Entry)entry);
 			//This basically AND's the binder and entry, since we already passed the binder
			Set allowedIds = acl.getPrincipals();   
 			if (allowedIds.remove(ObjectKeys.OWNER_USER_ID)) allowedIds.add(entry.getOwnerId());
 			if (testAccess(user, allowedIds)) return;
 			 throw new AclAccessControlException(user.getName(), type.toString());
   		}
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