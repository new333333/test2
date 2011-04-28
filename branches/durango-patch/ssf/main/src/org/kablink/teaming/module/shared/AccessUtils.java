/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.shared;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
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
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
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
		//The folder owner is stored in the folderAcl field, not a separate field cause bulk updates are never done
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
			String testAcl = " "+entryAcl+" ";
			if (testAcl.contains(" "+Constants.READ_ACL_ALL+" ")) {
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
			String testAcl = " "+entryAcl+" ";
			if (testAcl.contains(" "+Constants.READ_ACL_ALL+" ")) {
				return true;
			}
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
		return getReadAccessIds(binder, false);
	}
	public static Set getReadAccessIds(Binder binder, boolean includeTitleAcl) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
        Set readEntries = getInstance().getAccessControlManager().getWorkAreaAccessControl(binder, WorkAreaOperation.READ_ENTRIES);
        if (includeTitleAcl) {
        	Set readTitles = getInstance().getAccessControlManager().getWorkAreaAccessControl(binder, WorkAreaOperation.VIEW_BINDER_TITLE);
        	readEntries.addAll(readTitles);
        }
   		//See if this binder is in the "personal workspaces" tree
        Long allUsersId = Utils.getAllUsersGroupId();
        if (allUsersId != null && readEntries.contains(allUsersId) && Utils.isWorkareaInProfilesTree(binder)) {
			//The read access ids includes AllUsers; add in the groups of the binder owner and the team
        	Set<Long> userGroupIds = getInstance().getProfileDao().getAllGroupMembership(binder.getOwner().getId(), zoneId);
			readEntries.addAll(userGroupIds);
			readEntries.addAll(binder.getTeamMemberIds());
		}
        return readEntries;
	}     	
	public static Set getReadAccessIds(Entry entry) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
        Set readEntries = getInstance().getAccessControlManager().getWorkAreaAccessControl((WorkArea) entry, WorkAreaOperation.READ_ENTRIES);
   		//See if this entry is in the "personal workspaces" tree
        Long allUsersId = Utils.getAllUsersGroupId();
        if (allUsersId != null && readEntries.contains(allUsersId) && Utils.isWorkareaInProfilesTree(entry)) {
			//The read access ids includes AllUsers; add in the groups of the binder owner and the team
        	Set<Long> userGroupIds = getInstance().getProfileDao()
        		.getAllGroupMembership(entry.getCreation().getPrincipal().getId(), zoneId);
			readEntries.addAll(userGroupIds);
			userGroupIds = getInstance().getProfileDao()
    			.getAllGroupMembership(entry.getParentBinder().getOwner().getId(), zoneId);
			readEntries.addAll(userGroupIds);
			readEntries.addAll(entry.getParentBinder().getTeamMemberIds());
		}
        return readEntries;
	}     	
	
	public static void readCheck(User user, DefinableEntity entity) throws AccessControlException {
		if (entity.getEntityType().equals(EntityIdentifier.EntityType.workspace) || 
				entity.getEntityType().equals(EntityIdentifier.EntityType.folder) || 
				entity.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
			readCheck(user, (WorkArea) entity);
		} else if (entity.getEntityType().equals(EntityIdentifier.EntityType.folderEntry)) {
			readCheck(user, (Entry) entity);
		}
	}
	public static void readCheck(User user, WorkArea binder) throws AccessControlException {
		getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
	}
	public static void readCheck(Entry entry) throws AccessControlException {
		readCheck(RequestContextHolder.getRequestContext().getUser(), entry);
	}
	public static void readCheck(User user, Entry entry) throws AccessControlException {
        if (user.isSuper()) return;
    	if (entry instanceof WorkflowSupport) {
    		readCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
    	} else {
    		readCheck(user, entry.getParentBinder(), (Entry)entry);
    	}
    	//If this is a reply, also check the readability of the top entry
    	if (entry instanceof FolderEntry && !entry.isTop()) {
    		FolderEntry topEntry = ((FolderEntry)entry).getTopEntry();
        	if (topEntry instanceof WorkflowSupport) {
        		readCheck(user, topEntry.getParentBinder(), (WorkflowSupport)topEntry);
        	} else {
        		readCheck(user, topEntry.getParentBinder(), (Entry)topEntry);
        	}
    	}
	}
	private static void readCheck(User user, Binder binder, Entry entry) {
		operationCheck(user, binder, entry, WorkAreaOperation.READ_ENTRIES);
    }
    private static void readCheck(User user, Binder binder, WorkflowSupport entry) throws AccessControlException {
    	operationCheck(user, binder, entry, WorkAreaOperation.READ_ENTRIES);
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
    
    public static void modifyCheck(Entry entry) {
    	operationCheck(entry, WorkAreaOperation.MODIFY_ENTRIES);
    }
    public static void modifyCheck(User user, Entry entry) {
    	operationCheck(user, entry, WorkAreaOperation.MODIFY_ENTRIES);
    }
    
    public static void deleteCheck(Entry entry) {
    	operationCheck(entry, WorkAreaOperation.DELETE_ENTRIES);
    }
    
    //General routines to test an entry for any access to a WorkAreaOperation
    //  These routines handle entry level ACLs and workflow ACLs
    public static void operationCheck(Entry entry, WorkAreaOperation operation) throws AccessControlException {
    	operationCheck(RequestContextHolder.getRequestContext().getUser(), entry, operation);
    }
	public static void operationCheck(User user, Entry entry, WorkAreaOperation operation) throws AccessControlException {
		if (entry instanceof WorkflowSupport)
			operationCheck(user, entry.getParentBinder(), (WorkflowSupport)entry, operation);
    	else 
    		operationCheck(user, entry.getParentBinder(), (Entry)entry, operation);
    		
	}
    private static void operationCheck(User user, Binder binder, Entry entry, WorkAreaOperation operation) {
        if (user.isSuper()) return;
    	boolean widen = SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false);
    	OperationAccessControlException ace = null;
    	OperationAccessControlExceptionNoName ace2 = null;
       	//First, check the entry ACL
       	try {
       		if (entry.hasEntryAcl()) {
       			getInstance().getAccessControlManager().checkOperation(user, entry, operation);
       			if (!widen) {
       				//"Widening" is not allowed, so also check for read access to the folder
       				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
       			}
       			return;
       		}
       	} catch (OperationAccessControlException ex) {
       		ace = ex;
       	} catch (OperationAccessControlExceptionNoName ex2) {
       		ace2 = ex2;
       	}
      	
       	//Next, see if entry allows other operations such as CREATOR_READ, CREATOR_MODIFY, CREATOR_DELETE
       	if (WorkAreaOperation.READ_ENTRIES.equals(operation) && entry.getCreation() != null && 
       			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
  			try {
  				if (entry.hasEntryAcl()) {
  					getInstance().getAccessControlManager().checkOperation(user, entry, WorkAreaOperation.CREATOR_READ);
  	       			if (!widen) {
  	       				//"Widening" is not allowed, so also check for read access to the folder
  	       				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
  	       			}
  					return;
  				}
  			} catch(OperationAccessControlException ex2) {}
       	} else if (WorkAreaOperation.MODIFY_ENTRIES.equals(operation) && entry.getCreation() != null && 
       			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
  			try {
  				if (entry.hasEntryAcl()) {
  					getInstance().getAccessControlManager().checkOperation(user, entry, WorkAreaOperation.CREATOR_MODIFY);
  	       			if (!widen) {
  	       				//"Widening" is not allowed, so also check for read access to the folder
  	       				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
  	       			}
  					return;
  				}
  			} catch(OperationAccessControlException ex2) {}
       	} else if (WorkAreaOperation.DELETE_ENTRIES.equals(operation) && entry.getCreation() != null && 
       			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
  			try {
  				if (entry.hasEntryAcl()) {
  					getInstance().getAccessControlManager().checkOperation(user, entry, WorkAreaOperation.CREATOR_DELETE);
  	       			if (!widen) {
  	       				//"Widening" is not allowed, so also check for read access to the folder
  	       				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
  	       			}
  					return;
  				}
  			} catch(OperationAccessControlException ex2) {}
       	}
       	
       	//Next, try if the binder allows access
       	if (!entry.hasEntryAcl() || entry.isIncludeFolderAcl()) {
	       	try {
	       		getInstance().getAccessControlManager().checkOperation(user, binder, operation);
	       		return;
	       	} catch (OperationAccessControlException ex3) {ace = ex3;}
	       	
	      //Next, see if binder allows other operations such as CREATOR_MODIFY
	       	if (WorkAreaOperation.READ_ENTRIES.equals(operation) && entry.getCreation() != null && 
	       			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
      			try {
      				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_READ);
	      			return;
      			} catch (OperationAccessControlException ex3) {}
	      	} else if (WorkAreaOperation.MODIFY_ENTRIES.equals(operation) && entry.getCreation() != null && 
	      			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
      			try {
      				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_MODIFY);
	      			return;
      			} catch (OperationAccessControlException ex3) {}
	      	} else if (WorkAreaOperation.DELETE_ENTRIES.equals(operation) && entry.getCreation() != null && 
	      			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
      			try {
      				getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_DELETE);
	      			return;
      			} catch (OperationAccessControlException ex3) {}
	      	}
       	}
       	//Nothing allowed the operation, so throw an error
       	if (ace != null) {
       		throw ace;
       	} else if (ace2 != null) {
       		throw ace2;
       	} else {
       		throw new AccessControlException();
       	}
	}
    
	private static void operationCheck(User user, Binder binder, WorkflowSupport entry, WorkAreaOperation operation) {
		WfAcl.AccessType accessType = null;
		if (WorkAreaOperation.READ_ENTRIES.equals(operation)) accessType = WfAcl.AccessType.read;
		if (WorkAreaOperation.MODIFY_ENTRIES.equals(operation)) accessType = WfAcl.AccessType.write;
		if (WorkAreaOperation.DELETE_ENTRIES.equals(operation)) accessType = WfAcl.AccessType.delete; 
 		if (accessType == null || !entry.hasAclSet()) {
			//This entry does not have a workflow ACL set, so just go check for entry level access
 			operationCheck(user, binder, (Entry)entry, operation);
		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//"Widening" is allowed, we need to only check workflow ACL
 			try {
 				//check explicit users
 				checkAccess(user, binder, entry, accessType);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(accessType)) { 		
 					//The workflow ACL did not allow this operation, so now see if the entry affords this operation
 					operationCheck(user, binder, (Entry)entry, operation);
 				} else throw ex;
 			}
 			
 		} else {
 			//"Widening" is not allowed, so we must also have READ access to binder
 			if (entry.isWorkAreaAccess(accessType)) {
 				//optimization: if pass modify binder check, don't need to do read binder check
 				// all done if binder access is enough
 				try {
 					operationCheck(user, binder, (Entry)entry, operation);
					return;
 				} catch (AccessControlException ex) {} //move on to next checks
 			}
 			//see if pass binder READ test
 			operationCheck(user, binder, (Entry)entry, WorkAreaOperation.READ_ENTRIES);
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, binder, entry, accessType);
  		}
	}

     public static void modifyFieldCheck(Entry entry) throws AccessControlException {
 		modifyFieldCheck(RequestContextHolder.getRequestContext().getUser(), entry);
     }
     public static void modifyFieldCheck(User user, Entry entry) throws AccessControlException {
    	 if (user.isSuper()) return;
    	 if (entry instanceof WorkflowSupport)
    		 modifyFieldCheck(user, entry.getParentBinder(), (WorkflowSupport)entry);
    	 else 
    		 modifyFieldCheck(user, entry.getParentBinder(), (Entry)entry);
    }
     private static void modifyFieldCheck(User user, Binder binder, Entry entry) {
        try {
        	getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.MODIFY_ENTRY_FIELDS);
        } catch (OperationAccessControlException ex) {
        	try {
        		//See if this user has modify right instead
        		operationCheck(user, binder, entry, WorkAreaOperation.MODIFY_ENTRIES);
        	} catch (OperationAccessControlException ex2) {
	       		if (user.getId().equals(entry.getCreation().getPrincipal().getId())) 
	       			getInstance().getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_MODIFY);
	       		else throw ex2;
        	}
       	}
     }

     private static void modifyFieldCheck(User user, Binder binder, WorkflowSupport entry) {
  		if (!entry.hasAclSet()) {
 			modifyFieldCheck(user, binder, (Entry)entry);
 		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
  			//just check entry acl, ignore binder
  			try {
  				//check explicit users
  				checkAccess(user, binder, entry, WfAcl.AccessType.modifyField);
  			} catch (AccessControlException ex) {
  				if (entry.isWorkAreaAccess(WfAcl.AccessType.modifyField)) { 		
  					modifyFieldCheck(user, binder, (Entry)entry);
  				} else if (entry.isWorkAreaAccess(WfAcl.AccessType.modify)) { 		
  					operationCheck(user, binder, (Entry)entry, WorkAreaOperation.MODIFY_ENTRIES);
  				} else throw ex;
  			}
  			
  		} else {
  			//must have READ access to binder AND modify to the entry
  			if (entry.isWorkAreaAccess(WfAcl.AccessType.modifyField)) {
  				//optimization: if pass modify binder check, don't need to do read binder check
  				// all done if binder access is enough
  				try {
 					modifyFieldCheck(user, binder, (Entry)entry);
 					return;
  				} catch (AccessControlException ex) {} //move on to next checks
  			}
  			//see if pass binder READ test
  			readCheck(user, binder, (Entry)entry);
  			//This basically AND's the binder and entry, since we already passed the binder
  			checkAccess(user, binder, entry, WfAcl.AccessType.modifyField);
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
			operationCheck(user, binder, (Entry)entry, WorkAreaOperation.MODIFY_ENTRIES);
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
 				operationCheck(user, binder, (Entry)entry, WorkAreaOperation.MODIFY_ENTRIES);
 			} else throw new AclAccessControlException(user.getName(), type.toString());

 			
 		} else {
 			//must have READ access to binder AND modify to the entry
			if (acl.isUseDefault()) { 		
				 //optimization: if pass modify binder check, don't need to do read binder check
 				// all done if binder access is enough
 				try {
 					operationCheck(user, binder, (Entry)entry, WorkAreaOperation.MODIFY_ENTRIES);
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
		superUser = (User) getInstance().getProfileDao().loadUserPrincipal(superUser.getId(), zoneId, false);
		return superUser;
    }
    
    public static User getZoneGuestUser(Long zoneId) {
		User guestUser = getInstance().getProfileDao().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zoneId);
		return guestUser;
    }
}