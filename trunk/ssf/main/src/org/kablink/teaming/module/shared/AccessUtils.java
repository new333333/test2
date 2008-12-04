/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.module.shared;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WfAcl;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.acl.AclAccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionManager;
import org.kablink.teaming.security.function.OperationAccessControlException;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;


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
		//KEEP IN SYNC WITH QUERYBUILDER.GETACLCLAUSE 
		//Don't exactly duplicate all the optimizations in the search engine.
		//The folder owner is stored in toe folderAcl field, not a separate field cause bulk updates are never done
		/* if widen(the default), then acl query is:
		 * access to folder ((entryAcl:all and folderAcl:1,2,3) OR (entryAcl:all and folderAcl:team and teamAcl:1,2,3) OR
		 * access to entry (entryAcl:1,2,3) OR (entryAcl:team AND teamAcl:1,2,3)) 
		 * 
		 * if !widen, then acl query is: 
		 * access to folder (((folderAcl:1,2,3) OR (folderAcl:team and teamAcl:1,2,3)) AND
		 * access to entry ((entryAcl:all,1,2,3) OR (entryAcl:team and teamAcl:1,2,3)))
		 */
		Element acl = (Element)parent.selectSingleNode(Constants.FOLDER_ACL_FIELD);
		String folderAcl = null;
		if (acl != null) folderAcl = acl.getText();
		if (folderAcl == null) folderAcl = "";
		String[] folderAclArray = folderAcl.split(" ");
	 
		acl = (Element)parent.selectSingleNode(Constants.ENTRY_ACL_FIELD);   	
		String entryAcl=null;
		if (acl != null) entryAcl = acl.getText();
		if (Validator.isNull(entryAcl)) entryAcl = Constants.READ_ACL_ALL;
		String []entryAclArray = entryAcl.split(" ");

		acl = (Element)parent.selectSingleNode(Constants.TEAM_ACL_FIELD);   	
		String teamAcl=null;
		if (acl != null) teamAcl = acl.getText();
		if (Validator.isNull(teamAcl)) teamAcl = "";
		String []teamAclArray = teamAcl.split(" ");
		boolean widen = SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false);
		if (widen) {
			/* access to folder ((entryAcl:all and folderAcl:1,2,3) OR (entryAcl:all and folderAcl:team and teamAcl:1,2,3)
			 * access to entry  OR (entryAcl:1,2,3) OR (entryAcl:team AND teamAcl:1,2,3))
			 * */ 
			if (entryAcl.equals(Constants.READ_ACL_ALL)) {
				//(entryAcl:all and folderAcl:1,2,3)
				for (int i=0; i<folderAclArray.length; ++i) {
					if (userIds.contains(folderAclArray[i])) return true;
				}
				//(entryAcl:all and folderAcl:team and teamAcl:1,2,3)
				if (folderAcl.contains(Constants.READ_ACL_TEAM)) {
					for (int i=0; i<teamAclArray.length; ++i) {
						if (userIds.contains(teamAclArray[i])) return true;
					}
				}				
			} 
			
			//OR check entry for match
			//(entryAcl:1,2,3)
			for (int i=0; i<entryAclArray.length; ++i) {
				if (userIds.contains(entryAclArray[i])) return true;
			}
			//(entryAcl:team AND teamAcl:1,2,3))
			if (entryAcl.contains(Constants.READ_ACL_TEAM)) {
				for (int i=0; i<teamAclArray.length; ++i) {
					if (userIds.contains(teamAclArray[i])) return true;
				}
			}				
			return false;
			
		} else {
			//(folderACL:1,2,3 AND entryAcl:all,1,2,3)
			//check folder for match
			/* access to folder (((folderAcl:1,2,3) OR (folderAcl:team and teamAcl:1,2,3)) 
			 * access to entry AND ((entryAcl:all,1,2,3) OR (entryAcl:team and teamAcl:1,2,3)))
			 * */
			boolean found = false;
			//(folderAcl:1,2,3)
			for (int i=0; i<folderAclArray.length; ++i) {
				if (userIds.contains(folderAclArray[i])) {
					found = true;
					break;
				}
			}
			if (!found) {
				//folderAcl:team and teamAcl:1,2,3
				if (folderAcl.contains(Constants.READ_ACL_TEAM)) {
					for (int i=0; i<teamAclArray.length; ++i) {
						if (userIds.contains(teamAclArray[i])){
							found =true;
							break;
						}
					}
				}
			}
			//done if didn't pass folderAcl
			if (!found) return false;				
			//check entry for match ((entryAcl:all,1,2,3) OR (entryAcl:team and teamAcl:1,2,3)))
			//(entryAcl:all,1,2,3)
			if (entryAcl.equals(Constants.READ_ACL_ALL)) return true;
			for (int i=0; i<entryAclArray.length; ++i) {
				if (userIds.contains(entryAclArray[i])) return true;
			}
			//(entryAcl:team and teamAcl:1,2,3)
			if (entryAcl.contains(Constants.READ_ACL_TEAM)) {
				for (int i=0; i<teamAclArray.length; ++i) {
					if (userIds.contains(teamAclArray[i])) return true;
				}
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
 				checkAccess(user, binder, entry, WfAcl.AccessType.read);
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
 			checkAccess(user, binder, entry, WfAcl.AccessType.read);
  		}
	}
    
    private static void checkAccess(User user, Binder binder, WorkflowSupport entry, WfAcl.AccessType type) {
        Set allowedIds = entry.getStateMembers(type);
        if (allowedIds.remove(ObjectKeys.OWNER_USER_ID)) allowedIds.add(entry.getOwnerId());
     	if (allowedIds.remove(ObjectKeys.TEAM_MEMBER_ID)) allowedIds.addAll(binder.getTeamMemberIds());
        if (testAccess(user, allowedIds)) return;
        throw new AclAccessControlException(user.getName(), type.toString());
    }
    private static boolean testAccess(User user, Set allowedIds) {
     	Set principalIds = getInstance().getProfileDao().getPrincipalIds(user);
        return !Collections.disjoint(principalIds, allowedIds);
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
 				checkAccess(user, binder, entry, WfAcl.AccessType.write);
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
 			checkAccess(user, binder, entry, WfAcl.AccessType.write);
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
 				checkAccess(user, binder, entry, WfAcl.AccessType.delete);
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
 			checkAccess(user, binder, entry, WfAcl.AccessType.delete);
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
     
     public static void checkTransitionIn(Binder binder, Entry entry, Definition definition, String toState)  
     	throws AccessControlException {
    	 if (!(entry instanceof WorkflowSupport)) return;
    	 //build a fake state
    	 WorkflowState ws = new WorkflowState();
    	 ws.setDefinition(definition);
    	 ws.setState(toState);
    	 ws.setOwner((Entry)entry);
    	 checkTransitionAcl(binder, (WorkflowSupport)entry, ws, WfAcl.AccessType.transitionIn);
     }
     public static void checkTransitionOut(Binder binder, Entry entry, Definition definition, WorkflowState ws)  
     	throws AccessControlException {
    	 if (!(entry instanceof WorkflowSupport)) return;
    	 checkTransitionAcl(binder, (WorkflowSupport)entry, ws, WfAcl.AccessType.transitionOut);
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
 			Set allowedIds = acl.getPrincipalIds();   
 			if (allowedIds.remove(ObjectKeys.OWNER_USER_ID)) allowedIds.add(entry.getOwnerId());
        	if (allowedIds.remove(ObjectKeys.TEAM_MEMBER_ID)) allowedIds.addAll(binder.getTeamMemberIds());
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
			Set allowedIds = acl.getPrincipalIds();   
 			if (allowedIds.remove(ObjectKeys.OWNER_USER_ID)) allowedIds.add(entry.getOwnerId());
        	if (allowedIds.remove(ObjectKeys.TEAM_MEMBER_ID)) allowedIds.addAll(binder.getTeamMemberIds());
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
    
    public static User getZoneSuperUser(Long zoneId) {
		User superUser = getInstance().getProfileDao().getReservedUser(ObjectKeys.SUPER_USER_INTERNALID, zoneId);
		return superUser;
    }
    
    public static User getZoneGuestUser(Long zoneId) {
		User guestUser = getInstance().getProfileDao().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zoneId);
		return guestUser;
    }
}