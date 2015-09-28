/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Element;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.cache.impl.HashMapCache;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WfAcl;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.fi.connection.acl.AclItemPermissionMapper;
import org.kablink.teaming.fi.connection.acl.AclItemPermissionMappingException;
import org.kablink.teaming.fi.connection.acl.AclItemPrincipalMappingException;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.workflow.WorkflowProcessUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.acl.AclAccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionManager;
import org.kablink.teaming.security.function.OperationAccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaFunctionMembershipManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.function.ConditionalClause;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class AccessUtils  {
	private static final Log logger = LogFactory.getLog(AccessUtils.class);
	
	private static AccessUtils instance; // A singleton instance
	protected AccessControlManager accessControlManager;
	protected FunctionManager functionManager;
	protected ProfileDao profileDao;
	protected CoreDao coreDao;
	protected BinderModule binderModule;
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
	public static AccessControlManager getAccessControlManager() {
		return getInstance().accessControlManager;
	}
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
	protected FunctionManager getFunctionManager() {
		if (functionManager != null) {
			return functionManager;
		} else {
			return (FunctionManager) SpringContextUtil.getBean("functionManager");
		}
	}
	protected WorkAreaFunctionMembershipManager getWorkAreaFunctionMembershipManager() {
		return (WorkAreaFunctionMembershipManager) SpringContextUtil.getBean("workAreaFunctionMembershipManager");
	}

	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	protected CoreDao getCoreDao() {
		if (coreDao != null) {
			return coreDao;
		} else {
			return (CoreDao) SpringContextUtil.getBean("coreDao");
		}
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	protected BinderModule getBinderModule() {
		if (binderModule != null) {
			return binderModule;
		} else {
			return (BinderModule) SpringContextUtil.getBean("binderModule");
		}
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

	public static Set<String> getReadAccessIds(Binder binder) {
		return getReadAccessIds(binder, false);
	}
	public static Set<String> getReadAccessIds(Binder binder, boolean includeTitleAcl) {
		//Build a list of all ids that can read this entry (including function conditions)
        Set<String> readEntries = getReadIds(binder, WorkAreaOperation.READ_ENTRIES);

        if (includeTitleAcl) {
        	Set<String> readTitles = getReadIds(binder, WorkAreaOperation.VIEW_BINDER_TITLE);
        	readEntries.addAll(readTitles);
        }
        return readEntries;
	}     	
	public static Set<String> getReadAccessIds(Entry entry) {
        //Build a list of all ids that can read this entry (including function conditions)
        Set<String> readEntries = getReadIds(entry, WorkAreaOperation.READ_ENTRIES);

        return readEntries;
	} 
	
	/**
	 * Returns the IDs that have CREATOR_READ access to the binder.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static Set<String> getReadOwnedEntriesIds(Binder binder) {
		//Build a list of all ids that can read the their own entries (including function conditions)
        Set<String> readOwnedEntries = getReadIds(binder, WorkAreaOperation.CREATOR_READ);
        return readOwnedEntries;
	}     	
	
	//Routine to get the expanded list of ids who can read an entity (including function conditions)
	private static Set<String> getReadIds(DefinableEntity entity, WorkAreaOperation operation) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	Long allUsersId = Utils.getAllUsersGroupId();
	    boolean personal = Utils.isWorkareaInProfilesTree((WorkArea)entity);
		
	    //Find the workArea that actually defines the ACL
	    
	    boolean externallyControlledRight = false;
	    if((entity instanceof Binder) && ((Binder)entity).getExternallyControlledRights().contains(operation))
	    	externallyControlledRight = true;
	  
	    WorkArea workArea = (WorkArea) entity;
	    if(externallyControlledRight) {
			while (workArea.isExtFunctionMembershipInherited() && workArea.getParentWorkArea() != null && workArea.getParentWorkArea().isAclExternallyControlled()) {
				workArea = workArea.getParentWorkArea();
		    	if (workArea == null) {
		    		//Not found, just use the original (which will return an empty ACL)
		    		workArea = (WorkArea) entity;
		    		break;
		    	}
			}
	    }
	    else {
			while (workArea.isFunctionMembershipInherited()) {
				workArea = workArea.getParentWorkArea();
		    	if (workArea == null) {
		    		//Not found, just use the original (which will return an empty ACL)
		    		workArea = (WorkArea) entity;
		    		break;
		    	}
			}
	    }
			
		//Start with a list of the functions (aka Roles) that are used in this workArea
		List<WorkAreaFunctionMembership> wfms = getInstance().getWorkAreaFunctionMembershipManager()
        	.findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, operation);
        
		//Look at each function (aka Role) to get its read membership and to see if it has any conditions
		Set<String> readEntries = new HashSet<String>();
        for (WorkAreaFunctionMembership wfm:wfms) {
        	Long fId = wfm.getFunctionId();
        	Function f = getInstance().getFunctionManager().getFunction(zoneId, fId);
        	if (f.isConditional()) {
        	    List<Long> conditionIds = f.getConditionIds(ConditionalClause.Meet.MUST);
        	    if (conditionIds.size() > 0) {
        	        // This role has MUST type conditions. 
        	    	// We need to build a single combined term value from this for the acl field (e.g. 5c1c2)
        	    	StringBuffer cond = new StringBuffer();
        	    	for (Long cId : conditionIds) {
        	    		cond.append(Constants.CONDITION_ACL_PREFIX).append(String.valueOf(cId));
        	    	}
	        	    for (Long mId : (Set<Long>)wfm.getMemberIds()) {
	        	    	String sId = String.valueOf(mId);
	        	    	if (mId.equals(ObjectKeys.TEAM_MEMBER_ID)) sId = Constants.READ_ACL_TEAM;
	        	    	if (mId.equals(ObjectKeys.OWNER_USER_ID)) sId = Constants.READ_ACL_BINDER_OWNER;
	        	    	readEntries.add(sId + cond.toString());
	        	       	if (entity instanceof FolderEntry && ((FolderEntry)entity).isIncludeFolderAcl()) {
	        	       		if (!(entity instanceof WorkflowSupport) || 
	        	       				((WorkflowControlledEntry)entity).isWorkAreaAccess(WfAcl.AccessType.read)) {
	        	       			//If this is an entry and it includes the folder ACL, add "all" and "global"
	        	       			readEntries.add(Constants.READ_ACL_ALL + cond.toString());
	        	       			if (!personal) {
	        	       				readEntries.add(Constants.READ_ACL_GLOBAL + cond.toString());
	        	       			}
	        	       		}
	        	        }
	        	    	if (mId.equals(allUsersId)) {
	        	    		readEntries.add(Constants.READ_ACL_ALL + cond.toString());
	        	    	}
	        	    	if (mId.equals(allUsersId) && !personal) {
	        	    		readEntries.add(Constants.READ_ACL_GLOBAL + cond.toString());
	        	    	}
	        	    	if (mId.equals(allUsersId) && personal) {
	        				//For personal entities that allow AllUsers, add in the groups of the binder owner and the team
	        	        	Set<Long> userGroupIds = getInstance().getProfileDao()
	        	        		.getAllGroupMembership(entity.getCreation().getPrincipal().getId(), zoneId);
	        				userGroupIds.addAll( getInstance().getBinderModule().getTeamMemberIds( entity.getParentBinder() ) );
	        				if (entity instanceof FolderEntry) {
	        					userGroupIds.addAll(getInstance().getProfileDao()
	        		    			.getAllGroupMembership(entity.getParentBinder().getOwner().getId(), zoneId));
	        				}
	        				for (Long id : userGroupIds) {
	        					readEntries.add(String.valueOf(id) + cond.toString());
	        				}
	        	    	}
	        	    }
        	    	
        	    } else {
        	        conditionIds = f.getConditionIds(ConditionalClause.Meet.SHOULD);
        	        // We need to build one term per condition ID (e.g., 5c1, 5c2)
        	    	for (Long cId : conditionIds) {
		        	    for (Long mId : (Set<Long>)wfm.getMemberIds()) {
		        	    	String sId = String.valueOf(mId);
		        	    	if (mId.equals(ObjectKeys.TEAM_MEMBER_ID)) sId = Constants.READ_ACL_TEAM;
		        	    	if (mId.equals(ObjectKeys.OWNER_USER_ID)) sId = Constants.READ_ACL_BINDER_OWNER;
		        	    	readEntries.add(sId + Constants.CONDITION_ACL_PREFIX + String.valueOf(cId));
		        	       	if (entity instanceof FolderEntry && ((FolderEntry)entity).isIncludeFolderAcl()) {
		        	       		if (!(entity instanceof WorkflowSupport) || 
		        	       				((WorkflowControlledEntry)entity).isWorkAreaAccess(WfAcl.AccessType.read)) {
		        	       			//If this is an entry and it includes the folder ACL, add "all" and "global"
		        	       			readEntries.add(Constants.READ_ACL_ALL + Constants.CONDITION_ACL_PREFIX + String.valueOf(cId));
		        	       			if (!personal) {
		        	       				readEntries.add(Constants.READ_ACL_GLOBAL + Constants.CONDITION_ACL_PREFIX + String.valueOf(cId));
		        	       			}
		        	       		}
		        	        }
		        	    	if (mId.equals(allUsersId)) {
		        	    		readEntries.add(Constants.READ_ACL_ALL + Constants.CONDITION_ACL_PREFIX + String.valueOf(cId));
		        	    	}
		        	    	if (mId.equals(allUsersId) && !personal) {
		        	    		readEntries.add(Constants.READ_ACL_GLOBAL + Constants.CONDITION_ACL_PREFIX + String.valueOf(cId));
		        	    	}
		        	    	if (mId.equals(allUsersId) && personal) {
		        				//For personal entities that allow AllUsers, add in the groups of the binder owner and the team
		        	        	Set<Long> userGroupIds = getInstance().getProfileDao()
		        	        		.getAllGroupMembership(entity.getCreation().getPrincipal().getId(), zoneId);
		        				userGroupIds.addAll( getInstance().getBinderModule().getTeamMemberIds( entity.getParentBinder() ) );
		        				if (entity instanceof FolderEntry) {
		        					userGroupIds.addAll(getInstance().getProfileDao()
		        		    			.getAllGroupMembership(entity.getParentBinder().getOwner().getId(), zoneId));
		        				}
		        				for (Long id : userGroupIds) {
		        					readEntries.add(String.valueOf(id) + Constants.CONDITION_ACL_PREFIX + String.valueOf(cId));
		        				}
		        	    	}
		        	    }
        	    	}
        	    }
        	} else {
        		for (Long mId : (Set<Long>)wfm.getMemberIds()) {
        	    	String sId = String.valueOf(mId);
        	    	if (mId.equals(ObjectKeys.TEAM_MEMBER_ID)) sId = Constants.READ_ACL_TEAM;
        	    	if (mId.equals(ObjectKeys.OWNER_USER_ID)) sId = Constants.READ_ACL_BINDER_OWNER;
        			readEntries.add(sId);
        	       	if (entity instanceof FolderEntry && ((FolderEntry)entity).isIncludeFolderAcl()) {
        	       		if (!(entity instanceof WorkflowSupport) || 
        	       				((WorkflowControlledEntry)entity).isWorkAreaAccess(WfAcl.AccessType.read)) {
        	       			//If this is an entry and it includes the folder ACL, add "all" and "global"
        	       			readEntries.add(Constants.READ_ACL_ALL);
        	       			if (!personal) {
        	       				readEntries.add(Constants.READ_ACL_GLOBAL);
        	       			}
        	       		}
        	        }
        	    	if (mId.equals(allUsersId)) {
        	    		readEntries.add(Constants.READ_ACL_ALL);
        	    	}
        	    	if (mId.equals(allUsersId) && !personal) {
        	    		readEntries.add(Constants.READ_ACL_GLOBAL);
        	    	}
        	    	if (mId.equals(allUsersId) && personal) {
        				//For personal entities that allow AllUsers, add in the groups of the binder owner and the team
        	        	Set<Long> userGroupIds = getInstance().getProfileDao()
        	        		.getAllGroupMembership(entity.getCreation().getPrincipal().getId(), zoneId);
        				userGroupIds.addAll( getInstance().getBinderModule().getTeamMemberIds( entity.getParentBinder() ) );
        				if (entity instanceof FolderEntry) {
        					userGroupIds.addAll(getInstance().getProfileDao()
        		    			.getAllGroupMembership(entity.getParentBinder().getOwner().getId(), zoneId));
        				}
        				for (Long id : userGroupIds) {
        					readEntries.add(String.valueOf(id));
        				}
        	    	}
        		}
        	}
        }
        return readEntries;
	}

	//Routine to get the top folder in a folder chain
	//Returns null if this is not in a folder
	public static Binder getRootFolder(DefinableEntity entity) {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		
	    WorkArea workArea = (WorkArea) entity;
	    Binder topFolder = null;
		if (workArea instanceof FolderEntry) {
			topFolder = ((FolderEntry)workArea).getParentBinder();
		} else if (workArea instanceof Folder) {
			topFolder = (Folder)workArea;
		} else {
			//This is some other type of entity. Return null
			return null;
		}
		while (topFolder != null) {
			if (topFolder.getParentBinder() != null &&
					!topFolder.getParentBinder().getEntityType().name().equals(EntityType.folder.name())) {
				//We have found the top folder (i.e., the net folder root)
				break;
			}
			//Go up a level looking for the root
			topFolder = topFolder.getParentBinder();
		}
		return topFolder;
	}
	
	//Routine to get the expanded list of ids who can read an entity (including function conditions)
	public static Set<String> getRootIds(DefinableEntity entity) {
		Set<String> rootIds = new HashSet<String>();
		if (!((WorkArea)entity).isAclExternallyControlled()) {
			//If this is not a net folder, then allow all
			rootIds.add(Constants.ROOT_FOLDER_ALL);
			return rootIds;
		}
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		
	    //Find the workArea that actually defines the ACL
	    WorkArea workArea = (WorkArea) entity;
	    Binder topFolder = null;
		if (workArea instanceof FolderEntry) {
			topFolder = ((FolderEntry)workArea).getParentBinder();
		} else if (workArea instanceof Folder) {
			topFolder = (Folder)workArea;
		}
		while (topFolder != null) {
			if (topFolder.getParentBinder() != null &&
					!topFolder.getParentBinder().getEntityType().name().equals(EntityType.folder.name())) {
				//We have found the top folder (i.e., the net folder root)
				break;
			}
			//Go up a level looking for the root
			topFolder = topFolder.getParentBinder();
		}
		//See if the top folder is inheriting from its parent
		while (topFolder != null && topFolder.isFunctionMembershipInherited()) {
			topFolder = topFolder.getParentBinder();
		}
		if (topFolder == null) {
			//This shouldn't happen; assume no access
			return new HashSet<String>();
		}
		workArea = topFolder;
		Long ownerId = topFolder.getOwnerId();		//The owner of the folder supplying the acl
			
		//Start with a list of the functions (aka Roles) that are used in this workArea
		List<WorkAreaFunctionMembership> wfms = getInstance().getWorkAreaFunctionMembershipManager()
        	.findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER);
        
		//Look at each function (aka Role) to get its read membership. (Note, conditions don't affect root acl)
        for (WorkAreaFunctionMembership wfm:wfms) {
        	Long fId = wfm.getFunctionId();
        	Function f = getInstance().getFunctionManager().getFunction(zoneId, fId);
    		for (Long mId : (Set<Long>)wfm.getMemberIds()) {
    	    	String sId = String.valueOf(mId);
    	    	if (mId.equals(ObjectKeys.TEAM_MEMBER_ID)) sId = Constants.READ_ACL_TEAM + String.valueOf(topFolder.getId());
    	    	if (mId.equals(ObjectKeys.OWNER_USER_ID)) sId = String.valueOf(ownerId);
    			rootIds.add(sId);
    		}
    	}
        return rootIds;
	}
	
	//Routine to see if the current user has access to at least one id in a list of user or group ids (as returned from a search)
	public static boolean checkIfUserHasAccessToRootId(User user, String binderId) {
		try {
			DefinableEntity binder = getInstance().getBinderModule().getBinder(Long.valueOf(binderId));
			Set<String> rootIds = getRootIds(binder);
			if (rootIds.contains(Constants.ROOT_FOLDER_ALL)) return true;
			Set principalIds = getInstance().getProfileDao().getAllPrincipalIds(user);
			for (String sId : rootIds) {
				try {
					Long id = Long.valueOf(sId);
					if (principalIds.contains(id)) return true;
				} catch(Exception e) {}
			}
		} catch(Exception e) {}
		return false;
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
		getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.READ_ENTRIES);
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
     	if (allowedIds.remove(ObjectKeys.TEAM_MEMBER_ID)) allowedIds.addAll( getInstance().getBinderModule().getTeamMemberIds( binder ) );
        if (testAccess(user, allowedIds)) return;
        throw new AclAccessControlException(user.getName(), type.toString());
    }
    private static boolean testAccess(User user, Set allowedIds) {
     	Set principalIds = getInstance().getProfileDao().getAllPrincipalIds(user);
        return !Collections.disjoint(principalIds, allowedIds);
    }
    
    public static void modifyCheck(Entry entry) {
    	operationCheck(entry, WorkAreaOperation.MODIFY_ENTRIES);
    }
    public static void modifyCheck(User user, Entry entry) {
    	operationCheck(user, entry, WorkAreaOperation.MODIFY_ENTRIES);
    }
    public static void renameCheck(Entry entry) {
    	operationCheck(entry, WorkAreaOperation.RENAME_ENTRIES);
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
        
        if (entry instanceof FolderEntry) {
			// Yes!  Special case handle those operations on a comment
			// that require it.
			switch (CommentAccessUtils.checkCommentAccess(((FolderEntry) entry), operation, user)) {
			case ALLOWED:   return;
			case REJECTED:  throw new AccessControlException(operation.toString(), new Object[] {});
			
			default:
			case PROCESS_ACLS:
				break;
			}
        }
        
    	boolean widen = SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false);
    	AccessControlException ace = null;
    	OperationAccessControlExceptionNoName ace2 = null;
       	//First, check the entry ACL
       	try {
       		if (entry.hasEntryAcl()) {
       			getAccessControlManager().checkOperation(user, entry, operation);
       			if (!widen) {
       				//"Widening" is not allowed, so also check for read access to the folder
       				getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.readEntries);
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
  					getAccessControlManager().checkOperation(user, entry, WorkAreaOperation.CREATOR_READ);
  	       			if (!widen) {
  	       				//"Widening" is not allowed, so also check for read access to the folder
  	       				getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.readEntries);
  	       			}
  					return;
  				}
  			} catch(OperationAccessControlException ex2) {}
       	} else if (WorkAreaOperation.MODIFY_ENTRIES.equals(operation) && entry.getCreation() != null && 
       			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
  			try {
  				if (entry.hasEntryAcl()) {
  					getAccessControlManager().checkOperation(user, entry, WorkAreaOperation.CREATOR_MODIFY);
  	       			if (!widen) {
  	       				//"Widening" is not allowed, so also check for read access to the folder
  	       				getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.readEntries);
  	       			}
  					return;
  				}
  			} catch(OperationAccessControlException ex2) {}
       	} else if (WorkAreaOperation.RENAME_ENTRIES.equals(operation) && entry.getCreation() != null && 
       			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
  			try {
  				if (entry.hasEntryAcl()) {
  					getAccessControlManager().checkOperation(user, entry, WorkAreaOperation.CREATOR_RENAME);
  	       			if (!widen) {
  	       				//"Widening" is not allowed, so also check for read access to the folder
  	       				getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.readEntries);
  	       			}
  					return;
  				}
  			} catch(OperationAccessControlException ex2) {}
       	} else if (WorkAreaOperation.DELETE_ENTRIES.equals(operation) && entry.getCreation() != null && 
       			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
  			try {
  				if (entry.hasEntryAcl()) {
  					getAccessControlManager().checkOperation(user, entry, WorkAreaOperation.CREATOR_DELETE);
  	       			if (!widen) {
  	       				//"Widening" is not allowed, so also check for read access to the folder
  	       				getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.readEntries);
  	       			}
  					return;
  				}
  			} catch(OperationAccessControlException ex2) {}
       	}
       	
       	//Make sure this entity has an acl
		if (entry instanceof FolderEntry && entry.isAclExternallyControlled() && ((FolderEntry)entry).noAclDredged()) {
	       	try {
       			getAccessControlManager().checkOperation(user, entry, operation);
       			if (!widen) {
       				//"Widening" is not allowed, so also check for read access to the folder
       				getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.readEntries);
       			}
       			return;
	       	} catch (OperationAccessControlException ex) {
	       		ace = ex;
	       	} catch (OperationAccessControlExceptionNoName ex2) {
	       		ace2 = ex2;
	       	}
		} else if (!entry.hasEntryAcl() || entry.isIncludeFolderAcl()) {
			//Next, try if the binder allows access
	       	try {
	       		if (operation.equals(WorkAreaOperation.READ_ENTRIES)) {
	       			getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.readEntries);
	       		} else if (operation.equals(WorkAreaOperation.VIEW_BINDER_TITLE)) {
	       			getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.viewBinderTitle);
	       		} else {
	       			getAccessControlManager().checkOperation(user, binder, operation);
	       		}
	       		return;
	       	} catch (OperationAccessControlException ex3) {
	       		ace = ex3;
       		} catch (OperationAccessControlExceptionNoName ex4) {
       			ace2 = ex4;
       		} catch (AccessControlException ex5) {
       			ace = ex5;
       		}
	       	
	      //Next, see if binder allows other operations such as CREATOR_MODIFY
	       	if (WorkAreaOperation.READ_ENTRIES.equals(operation) && entry.getCreation() != null && 
	       			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
      			try {
      				getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_READ);
	      			return;
     			} catch (AccessControlException ex3) {}
	      	} else if (WorkAreaOperation.MODIFY_ENTRIES.equals(operation) && entry.getCreation() != null && 
	      			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
      			try {
      				getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_MODIFY);
	      			return;
      			} catch (AccessControlException ex3) {}
	      	} else if (WorkAreaOperation.RENAME_ENTRIES.equals(operation) && entry.getCreation() != null && 
	      			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
      			try {
      				getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_RENAME);
	      			return;
      			} catch (AccessControlException ex3) {}
	      	} else if (WorkAreaOperation.DELETE_ENTRIES.equals(operation) && entry.getCreation() != null && 
	      			user.getId().equals(entry.getCreation().getPrincipal().getId())) {
      			try {
      				getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_DELETE);
	      			return;
      			} catch (AccessControlException ex3) {}
	      	}
       	}
       	
		// Bugzilla 939041:  Although the fix for this bug involved
		//    coding changes in AccessControlManagerImpl (see the bug
		//    related comments in that module), it's the following
		//    try/catch block those changes necessary.  This try/catch
		//    did NOT exist in Vibe 3 and and pending ace/ace2
		//    exception would simply be thrown.
		
       	//See if the entry was shared 
       	try {
			//Start by trying to see if the entry allows access
   			getAccessControlManager().checkOperation(user, entry, operation);
       		//It did, but now check if widening is allowed. 
       		//  If widening is not allowed, then sharing cannot go beyond the current folder without the ability to also read the folder
       		//  This is somewhat useless since if you can read the folder, you could have seen this entry already
 			if (!widen) {
   				//"Widening" is not allowed, so also check for read access to the folder
 				getInstance().getBinderModule().checkAccess(user, binder, BinderOperation.readEntries);
   			}
			return;
       	} catch (AccessControlException ex) {
       		ace = ex;
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
		if (WorkAreaOperation.ADD_REPLIES.equals(operation)) {
			//See if workflow is disallowing Add Reply. Then do normal check
			checkIfAddRepliesAllowed(user, binder, entry);
		}
 		if (accessType == null || !entry.hasAclSet()) {
			//This entry does not have a workflow ACL set or this check is not covered by workflow access checks, 
 			//  so just go check for entry level access
 			operationCheck(user, binder, (Entry)entry, operation);
		} else if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//"Widening" is allowed, we need to only check workflow ACL
 			try {
 				//check explicit users
 				checkAccess(user, binder, entry, accessType);
 			} catch (AccessControlException ex) {
 				if (entry.isWorkAreaAccess(accessType)) { 		
 					//The workflow ACL did not allow this operation but the acl specifies "forum default", 
 					//  so now see if the binder affords this operation
 					getInstance().accessControlManager.checkOperation(user, binder, operation);
 				} else throw ex;
 			}
 			
 		} else {
 			//"Widening" is not allowed, so we must also have READ access to binder
 			if (entry.isWorkAreaAccess(accessType)) {
 				//The workflow specifies "forum default" for this access. 
 				// So, just do the regular binder level check. If that fails check the workflow acl.
 				try {
 					getInstance().accessControlManager.checkOperation(user, binder, operation);
					return;
 				} catch (AccessControlException ex) {} //move on to next checks
 			}
 			//see if pass binder READ test
 			operationCheck(user, binder, (Entry)entry, WorkAreaOperation.READ_ENTRIES);
 			//This basically AND's the binder and entry, since we already passed the binder
 			checkAccess(user, binder, entry, accessType);
  		}
	}

	public static void checkIfAddRepliesAllowed(User user, Binder binder, WorkflowSupport entry) 
			throws AccessControlException {
		if (entry.isAddRepliesDisallowed()) {
			//Adding replies has been disallowed
			throw new AccessControlException();
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
        	getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.MODIFY_ENTRY_FIELDS);
        } catch (OperationAccessControlException ex) {
        	try {
        		//See if this user has modify right instead
        		operationCheck(user, binder, entry, WorkAreaOperation.MODIFY_ENTRIES);
        	} catch (OperationAccessControlException ex2) {
	       		if (user.getId().equals(entry.getCreation().getPrincipal().getId())) 
	       			getAccessControlManager().checkOperation(user, binder, WorkAreaOperation.CREATOR_MODIFY);
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
     		getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getUser(), entry.getParentBinder(), WorkAreaOperation.BINDER_ADMINISTRATION);
     	} catch (OperationAccessControlException ex) {
    		throw ex;
    	}
     }          
     
     public static void overrideReserveEntryCheck(Binder binder) {
        try {
        	getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getUser(), binder, WorkAreaOperation.BINDER_ADMINISTRATION);
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
     public static boolean checkIfTransitionInAclExists(Binder binder, Entry entry, Definition definition, String toState)  
  			throws AccessControlException {
    	if (!(entry instanceof WorkflowSupport)) return false;
	 	//build a fake state
	 	WorkflowState ws = new WorkflowState();
	 	ws.setDefinition(definition);
	 	ws.setState(toState);
	 	ws.setOwner((Entry)entry);
	 	WfAcl acl = ws.getAcl(WfAcl.AccessType.transitionIn);
	 	if (acl != null && !acl.isUseDefault()) {
	 		return true;
	 	} else {
	 		return false;
	 	}
     }
     
     public static void checkTransitionOut(Binder binder, Entry entry, Definition definition, WorkflowState ws)  
     	throws AccessControlException {
    	 if (!(entry instanceof WorkflowSupport)) return;
    	 checkTransitionAcl(binder, (WorkflowSupport)entry, ws, WfAcl.AccessType.transitionOut);
     }
 	 public static void checkManualTransitionAccess(Binder binder, WorkflowSupport entry, WorkflowState state, 
 			Element accessEle) throws AccessControlException {
 		User user = RequestContextHolder.getRequestContext().getUser();
 		WfAcl acl = WorkflowProcessUtils.getAcl(accessEle, (DefinableEntity)entry, WfAcl.AccessType.manualTransition);
 		checkTransitionAcl(binder, entry, acl);
 	 }

 	 public static boolean doesManualTransitionAclExist(Binder binder, WorkflowSupport entry, WorkflowState state, 
 			Element accessEle) throws AccessControlException {
 		User user = RequestContextHolder.getRequestContext().getUser();
 		WfAcl acl = WorkflowProcessUtils.getAcl(accessEle, (DefinableEntity)entry, WfAcl.AccessType.manualTransition);
 		if (acl != null) {
 			return true;
 		} else {
 			return false;
 		}
 	 }

     private static void checkTransitionAcl(Binder binder, WorkflowSupport entry, WorkflowState state, WfAcl.AccessType type)  
      	throws AccessControlException {
		WfAcl acl = state.getAcl(type);
		checkTransitionAcl(binder, entry, acl);
     }
     
     private static void checkTransitionAcl(Binder binder, WorkflowSupport entry, WfAcl acl) 
   			throws AccessControlException {     
       	User user = RequestContextHolder.getRequestContext().getUser();
        if (user.isSuper()) return;
		if (acl == null) {
			operationCheck(user, binder, (Entry)entry, WorkAreaOperation.MODIFY_ENTRIES);
			return;
		}
		if (SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false)) {
 			//just check entry acl, ignore binder
 			//check explicit users
 			Set allowedIds = acl.getPrincipalIds();   
 			if (allowedIds.remove(ObjectKeys.OWNER_USER_ID)) allowedIds.add(entry.getOwnerId());
        	if (allowedIds.remove(ObjectKeys.TEAM_MEMBER_ID)) allowedIds.addAll( getInstance().getBinderModule().getTeamMemberIds( binder ) );
			if (testAccess(user, allowedIds)) return;
 			
 			if (acl.isUseDefault()) { 		
 				operationCheck(user, binder, (Entry)entry, WorkAreaOperation.MODIFY_ENTRIES);
 			} else throw new AclAccessControlException(user.getName(), acl.getType().toString());

 			
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
        	if (allowedIds.remove(ObjectKeys.TEAM_MEMBER_ID)) allowedIds.addAll( getInstance().getBinderModule().getTeamMemberIds( binder ) );
 			if (testAccess(user, allowedIds)) return;
 			throw new AclAccessControlException(user.getName(), acl.getType().toString());
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
			if (!getAccessControlManager().testOperation((WorkArea) workArea, o)) return false;
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
    
	public static boolean testReadAccess(Binder binder) {
		// Check if the user has "read" access to the binder.
		return (getAccessControlManager().testOperation(binder, WorkAreaOperation.READ_ENTRIES) || 
				getAccessControlManager().testOperation(binder, WorkAreaOperation.VIEW_BINDER_TITLE));

	}

	/**
	 * Ask external system which role the user has on the specified net folder file.
	 * This method returns the database ID of the Function object corresponding to
	 * one of the following roles defined in Filr, or null if the user has no access
	 * on the entry.
	 * 
	 * ObjectKeys.ROLE_TITLE_FILR_VIEWER
	 * ObjectKeys.ROLE_TITLE_FILR_EDITOR
	 * ObjectKeys.ROLE_TITLE_FILR_CONTRIBUTOR
	 * 
	 * @param netFolderFile
	 * @return
	 */
	public static Long askExternalSystemForRoleId(FolderEntry netFolderFile) {
		Folder parentFolder = netFolderFile.getParentFolder();
		if(!parentFolder.noAclDredgedWithEntries())
			throw new IllegalArgumentException("Invalid entry '" + netFolderFile.getId() + "' for this method");
		AclResourceDriver driver = (AclResourceDriver) parentFolder.getResourceDriver();
		AclResourceSession session = SearchUtils.openAclResourceSession(parentFolder.getResourceDriver(), FolderUtils.getNetFolderOwnerId(parentFolder));
		if(session == null)
			return null; // cannot obtain session for the user
		try {
			//See if the answer has been cached
			HttpSession httpSession = ZoneContextHolder.getHttpSession();
			if (httpSession != null) {
				//This must be coming from a web client. So, use the httpSession to store the cache
				HashMapCache<Long, Long> dredgedAclCache = (HashMapCache<Long, Long>)httpSession.getAttribute(ObjectKeys.SESSION_DREDGED_ROLE_ID_CACHE);
				if (dredgedAclCache != null) {
					Long vibeRoleId = dredgedAclCache.get(netFolderFile.getId());
					if (vibeRoleId != null) {
						//Return the cached value
						//logger.warn("Using cached roleId ("+vibeRoleId+") for file: "+netFolderFile.getTitle()+", at time "+String.valueOf(System.currentTimeMillis()));
						return vibeRoleId;
					}
				}
			} else {
				//This must be a REST call. So use the request context to hold the cache
				RequestContext context = RequestContextHolder.getRequestContext();
				if (context != null) {
					HashMapCache<Long, Long> dredgedAclCache = (HashMapCache<Long, Long>)context.getCacheEntry(ObjectKeys.SESSION_DREDGED_ROLE_ID_CACHE);
					if (dredgedAclCache != null) {
						Long vibeRoleId = dredgedAclCache.get(netFolderFile.getId());
						if (vibeRoleId != null) {
							//Return the cached value
							//logger.warn("Using REST cached roleId ("+vibeRoleId+") for file: "+netFolderFile.getTitle()+", at time "+String.valueOf(System.currentTimeMillis()));
							return vibeRoleId;
						}
					}
				}
			}
			
			Map<String, List<String>> groupIds = getFileSystemGroupIds(driver);
			session.setPath(parentFolder.getResourcePath(), parentFolder.getResourceHandle(), netFolderFile.getTitle(), netFolderFile.getResourceHandle(), Boolean.FALSE);
			String permissionName = null;
			try {
				permissionName = session.getPermissionName(groupIds);
			}
			catch(Exception e) {
				logger.error("Error getting permission name on file '" + netFolderFile.getTitle() + "' in folder [" + parentFolder.getPathName() + "]", e);
				// (Bug #890315)
				if(e instanceof org.kablink.teaming.fi.FileNotFoundException)
					throw e;
			}
			if(permissionName == null)
				return null;
			AclItemPermissionMapper permissionMapper = driver.getAclItemPermissionMapper();
			Long vibeRoleId = permissionMapper.toVibeRoleId(permissionName);
			
			//Cache this answer if needed again within a few seconds
			if (httpSession != null) {
				HashMapCache<Long, Long> dredgedAclCache = (HashMapCache<Long, Long>)httpSession.getAttribute(ObjectKeys.SESSION_DREDGED_ROLE_ID_CACHE);
				if (dredgedAclCache == null) {
					dredgedAclCache = new HashMapCache<Long, Long>(ObjectKeys.SESSION_DREDGED_ROLE_ID_CACHE_TIMEOUT);
				}
				dredgedAclCache.put(netFolderFile.getId(), vibeRoleId);
				httpSession.setAttribute(ObjectKeys.SESSION_DREDGED_ROLE_ID_CACHE, dredgedAclCache);
				//logger.warn("Setting cached roleId ("+vibeRoleId+") for file: "+netFolderFile.getTitle()+", at time "+String.valueOf(System.currentTimeMillis()));
			} else {
				//This must be a REST call. So use the request context to hold the cache
				RequestContext context = RequestContextHolder.getRequestContext();
				if (context != null) {
					HashMapCache<Long, Long> dredgedAclCache = (HashMapCache<Long, Long>)context.getCacheEntry(ObjectKeys.SESSION_DREDGED_ROLE_ID_CACHE);
					if (dredgedAclCache == null) {
						dredgedAclCache = new HashMapCache<Long, Long>(ObjectKeys.SESSION_DREDGED_ROLE_ID_CACHE_TIMEOUT);
					}
					dredgedAclCache.put(netFolderFile.getId(), vibeRoleId);
					context.setCacheEntry(ObjectKeys.SESSION_DREDGED_ROLE_ID_CACHE, dredgedAclCache);
					//logger.warn("Setting REST cached roleId ("+vibeRoleId+") for file: "+netFolderFile.getTitle()+", at time "+String.valueOf(System.currentTimeMillis()));
				}
			}

			return vibeRoleId;
		}
		catch (AclItemPrincipalMappingException e) {
			logger.error("Error mapping principal on file '" + netFolderFile.getTitle() + "' in folder [" + parentFolder.getPathName() + "]", e);
			return null;
		}
		catch (AclItemPermissionMappingException e) {
			logger.error("Error mapping permission on file '" + netFolderFile.getTitle() + "' in folder [" + parentFolder.getPathName() + "]", e);
			return null;
		} 
		finally {
			session.close();
		}
	}

	public static Map<String, List<String>> getFileSystemGroupIds(String resourceDriverName) throws AclItemPrincipalMappingException {
		ResourceDriver driver;
		
		try {
			driver = getResourceDriverManager().getDriver(resourceDriverName);
		}
		catch(Exception e) {
			logger.warn("Can not find resource driver by name '" + resourceDriverName + "'", e);
			return Collections.EMPTY_MAP;
		}

		return getFileSystemGroupIds(driver);
	}
	
	public static Map<String, List<String>> getFileSystemGroupIds(ResourceDriver driver) throws AclItemPrincipalMappingException {
		if(!(driver instanceof AclResourceDriver)) {
			return Collections.EMPTY_MAP;
		}
		else {
			Map<String, List<String>> result = null;		
			RequestContext rc = RequestContextHolder.getRequestContext();
			if(rc != null) {
				String cacheKey = "fileSystemGroupIds_" + driver.getClass().getName();
				result = (Map<String, List<String>>) rc.getCacheEntry(cacheKey);
				if(result == null) {
					result = ((AclResourceDriver)driver).getAclItemPrincipalMapper().toFileSystemGroupIds(RequestContextHolder.getRequestContext().getUser());
					if(result != null)
						rc.setCacheEntry(cacheKey, result);
				}
			}
			else {
				result = ((AclResourceDriver)driver).getAclItemPrincipalMapper().toFileSystemGroupIds(RequestContextHolder.getRequestContext().getUser());
			}
			return result;
		}
	}
	
	private static ResourceDriverManager getResourceDriverManager() {
		return (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
	}
}
