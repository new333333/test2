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
/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.team.module.profile.impl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.SortedSet;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.PrincipalComparator;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.profile.ProfileCoreProcessor;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.EventHelper;
import com.sitescape.util.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.ObjectExistsException;

public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
	private static final int DEFAULT_MAX_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	private String[] userDocType = {EntityIndexUtils.ENTRY_TYPE_USER};
	private String[] groupDocType = {EntityIndexUtils.ENTRY_TYPE_GROUP};
	private String[] principalDocType = {EntityIndexUtils.ENTRY_TYPE_USER, EntityIndexUtils.ENTRY_TYPE_GROUP};

    protected DefinitionModule definitionModule;
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
    
    protected AdminModule adminModule;
    protected AdminModule getAdminModule() {
    	return adminModule;
    }
    public void setAdminModule(AdminModule adminModule) {
    	this.adminModule = adminModule;
    }
    
    protected BinderModule binderModule;
    protected BinderModule getBinderModule() {
    	return binderModule;
    }
    public void setBinderModule(BinderModule binderModule) {
    	this.binderModule = binderModule;
    }
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	/*
	 * Check access to folder.  If operation not listed, assume read_entries needed
	 * @see com.sitescape.team.module.binder.BinderModule#checkAccess(com.sitescape.team.domain.Binder, java.lang.String)
	 */
    //NO transaction
    public boolean testAccess(ProfileBinder binder, ProfileOperation operation) {
		try {
			checkAccess(binder, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	public void checkAccess(ProfileBinder binder, ProfileOperation operation) throws AccessControlException {
		switch (operation) {
			case addEntry:
		    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
		    	break;
			default:
		    	throw new NotSupportedException(operation.toString(), "checkAccess");				    		
		}
	}
	protected void checkReadAccess(ProfileBinder binder) {
    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);		
	}
	protected void checkReadAccess(Principal principal) {
    	AccessUtils.readCheck(principal);

	}
	/*
 	 * Check access to folder.  If operation not listed, assume read_entries needed
  	 * Use method names as operation so we can keep the logic out of application
	 * and easisly change the required rights
	 * @see com.sitescape.team.module.profile.ProfileModule#testAccess(com.sitescape.team.domain.Principal, java.lang.String)
	 */
    //NO transaction
	public boolean testAccess(Principal entry, ProfileOperation operation) {
		try {
			checkAccess(entry, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	public void checkAccess(Principal entry, ProfileOperation operation) throws AccessControlException {
		switch (operation) {
			case modifyEntry:
				//give users modify access to their own entry
				if (RequestContextHolder.getRequestContext().getUser().equals(entry)) return;
				AccessUtils.modifyCheck(entry);   		
				break;
			case deleteEntry:
				AccessUtils.deleteCheck(entry);   		
				break;
			default:
		    	throw new NotSupportedException(operation.toString(), "checkAccess");				    		
		}

	}
    private ProfileCoreProcessor loadProcessor(ProfileBinder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.team.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
	    return (ProfileCoreProcessor) getProcessorManager().getProcessor(binder, ProfileCoreProcessor.PROCESSOR_KEY);
	}
	private ProfileBinder loadBinder(Long binderId) {
		return (ProfileBinder)getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
	}
	private ProfileBinder loadBinder() {
	   return (ProfileBinder)getProfileDao().getProfileBinder(RequestContextHolder.getRequestContext().getZoneId());
	}

	//RO transaction
	public ProfileBinder getProfileBinder() {
	   ProfileBinder binder = loadBinder();
		// Check if the user has "read" access to the folder.
	   checkReadAccess(binder);		
	   return binder;
    }

	//RO transaction
	public Principal getEntry(Long binderId, Long principaId) {
        ProfileBinder binder = loadBinder(binderId);
        Principal p = (Principal)loadProcessor(binder).getEntry(binder, principaId);        
    	checkReadAccess(p);			
        return p;
    }
    //RW transaction
	public UserProperties setUserProperty(Long userId, Long binderId, String property, Object value) {
   		User currentUser = RequestContextHolder.getRequestContext().getUser();
   		User user;
		if (userId == null) user = currentUser;
		else user = getProfileDao().loadUser(userId, currentUser.getZoneId());
		if (!RequestContextHolder.getRequestContext().getUser().equals(user)) AccessUtils.modifyCheck(user);   		
 		UserProperties uProps = getProfileDao().loadUserProperties(user.getId(), binderId);
		uProps.setProperty(property, value); 	
  		return uProps;
   }
	//RO transaction
   public UserProperties getUserProperties(Long userId, Long binderId) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
  		if (userId == null) userId = user.getId();
 		if (user.getId().equals(userId)) {
			uProps = getProfileDao().loadUserProperties(userId, binderId);
  		} else throw new NotSupportedException("getUserProperties", "user");
		return uProps;
}

   //RW transaction
   public UserProperties setUserProperty(Long userId, String property, Object value) {
  		User currentUser = RequestContextHolder.getRequestContext().getUser();
   		User user;
		if (userId == null) user = currentUser;
		else user = getProfileDao().loadUser(userId, currentUser.getZoneId());
		if (!RequestContextHolder.getRequestContext().getUser().equals(user)) AccessUtils.modifyCheck(user);   		
 		UserProperties uProps = getProfileDao().loadUserProperties(user.getId());
		uProps.setProperty(property, value); 	
		return uProps;
    }
	//RO transaction
   public UserProperties getUserProperties(Long userId) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
 		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
    		uProps = getProfileDao().loadUserProperties(userId);
  		} else throw new NotSupportedException("getUserProperties", "user");
  		return uProps;
   }
	//RO transaction
   public SeenMap getUserSeenMap(Long userId) {
		User user = RequestContextHolder.getRequestContext().getUser();
		SeenMap seen = null;
		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
 			 seen = getProfileDao().loadSeenMap(user.getId());
  		} else throw new NotSupportedException("getUserSeenMap", "user");
   		return seen;
   }
   //RW transaction
   public void setSeen(Long userId, Entry entry) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		SeenMap seen;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
			 seen = getProfileDao().loadSeenMap(user.getId());
			 seen.setSeen(entry);
  		} else throw new NotSupportedException("setSeen", "user");
  }
   //RW transaction
   public void setSeen(Long userId, Collection<Entry> entries) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		SeenMap seen;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
			seen = getProfileDao().loadSeenMap(user.getId());
   			for (Entry reply:entries) {
   				seen.setSeen(reply);
   			}
  		} else throw new NotSupportedException("setSeen", "user");
   }  	
  
	//RO transaction
   public Map getGroups(Long binderId) {
	   Map options = new HashMap();
	   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
	   return getGroups(binderId, options);
   }
 
	//RO transaction
   public Map getGroups(Long binderId, Map options) {
        ProfileBinder binder = loadBinder(binderId);
    	checkReadAccess(binder);
        return loadProcessor(binder).getBinderEntries(binder, groupDocType, options);        
    }
	//RO transaction
	public SortedSet<Group> getGroups(Collection<Long> entryIds) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Group> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadGroups(entryIds, user.getZoneId()));
		return result;
	}
	//RO transaction
	public Map getPrincipals(Long binderId, Map options) {
        ProfileBinder binder = loadBinder(binderId);
    	checkReadAccess(binder);
        return loadProcessor(binder).getBinderEntries(binder, principalDocType, options);        
    }

	public SortedSet<Principal> getPrincipals(Collection<Long> ids, Long zoneId) {
		ProfileBinder binder = getProfileBinder();
 	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Principal> result = new TreeSet(c);
 		result.addAll(getProfileDao().loadPrincipals(ids, zoneId, false));
 		return result;
	}
	public Collection<Principal> getPrincipalsByName(Collection<String> names) throws AccessControlException {
		Map params = new HashMap();
		params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());
		params.put("name", names);
		return getCoreDao().loadObjects("from com.sitescape.team.domain.Principal where zoneId=:zoneId and name in (:name)", params);

	}

    //***********************************************************************************************************	
    //NO transaction
    public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        ProfileBinder binder = loadBinder(binderId);
        checkAccess(binder, ProfileOperation.addEntry);
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        try {
        	return loadProcessor(binder).addEntry(binder, definition, User.class, inputData, fileItems, null).getId();
        } catch (DataIntegrityViolationException de) {
        	throw new ObjectExistsException("errorcode.user.exists", (Object[])null, de);
        }
        	
    }

/* HOLD OFF - need better implementation */
      public boolean checkUserSeeCommunity() {
    	  return false;
  //   	return getAccessControlManager().testOperation(this.getProfileBinder(), WorkAreaOperation.USER_SEE_COMMUNITY);        
    }

    public boolean checkUserSeeAll() {
    	return true;
 //  	return getAccessControlManager().testOperation(this.getProfileBinder(), WorkAreaOperation.USER_SEE_ALL);        
    }

    //NO transaction
    public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
	throws AccessControlException, WriteFilesException {
    	modifyEntry(binderId, id, inputData, new HashMap(), null, null);
    }
    //NO transaction
   public void modifyEntry(Long binderId, Long entryId, InputDataAccessor inputData, 
		   Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo) 
   		throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, entryId);
        //user can set their own display style and theme
        int noCheckCount = 0;
        if (inputData.exists(ObjectKeys.FIELD_USER_DISPLAYSTYLE)) ++noCheckCount;
        if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_THEME)) ++noCheckCount;
   	        
        if (!RequestContextHolder.getRequestContext().getUserId().equals(entryId) ||
        		(inputData.getCount() > noCheckCount)) {
        	checkAccess(entry, ProfileOperation.modifyEntry);
        }
       	List atts = new ArrayList();
    	if (deleteAttachments != null) {
    		for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    			String id = (String)iter.next();
    			Attachment a = entry.getAttachment(id);
    			if (a != null) atts.add(a);
    		}
    	}
    	processor.modifyEntry(binder, entry, inputData, fileItems, atts, fileRenamesTo, null);
      }

   //NO transaction
    public void addEntries(Long binderId, Document doc) {
       ProfileBinder binder = loadBinder(binderId);
       checkAccess(binder, ProfileOperation.addEntry);
       //process the document
       Element root = doc.getRootElement();
       List defList = root.selectNodes("/profiles/user");
       Map userLists = new HashMap();
       List<String> deleteNames = new ArrayList();
	   Definition defaultUserDef = binder.getDefaultEntryDef();		
	   if (defaultUserDef == null) {
		   User temp = new User();
		   getDefinitionModule().setDefaultEntryDefinition(temp);
		   defaultUserDef = temp.getEntryDef();
	   }
	   List defaultUserList = new ArrayList();
	   userLists.put(defaultUserDef, defaultUserList);
       //group users by defintion
       for (int i=0; i<defList.size(); ++i) {
    	   //get default definition to use
    	   Element user = (Element)defList.get(i);
    	   if ("delete".equals(user.attributeValue("operation"))) {
    		   Element nameEle = (Element)user.selectSingleNode("./attribute[@name='" + ObjectKeys.XTAG_PRINCIPAL_NAME + "']");
    		   if (nameEle == null) continue;
    		   String name = nameEle.getTextTrim();
    		   if (Validator.isNotNull(name)) deleteNames.add(name.toLowerCase());
    		   continue;
    	   }
    	   String defId = user.attributeValue("entryDef");
    	   Definition userDef=null;
    	   if (!Validator.isNull(defId)) {
    		   try {
    			   userDef = getDefinitionModule().getDefinition(defId);
    		   } catch (NoDefinitionByTheIdException nd) {};
    		   
    	   }
    	   if (userDef == null) defaultUserList.add(user);
    	   else {
    		   //see if it exists
    		   List userL = (List)userLists.get(userDef);
    		   if (userL == null) {
    			   userL = new ArrayList();
    			   userLists.put(userDef, userL);
    		   }
    		   userL.add(user);
    	   }
       }
       //delete users first
       if (!deleteNames.isEmpty()) {
    	   Map params = new HashMap();
    	   params.put("plist", deleteNames);
    	   params.put("zoneId", binder.getZoneId());
    	   List<Principal> deleteUsers = getCoreDao().loadObjects("from com.sitescape.team.domain.User where zoneId=:zoneId and name in (:plist)", params);
    	   if (!deleteUsers.isEmpty()) {
    		   deleteEntries(binder, deleteUsers);
    	   }
       }
    		   //add entries grouped by definitionId
       for (Iterator iter=userLists.entrySet().iterator(); iter.hasNext();) {
    	   Map.Entry me = (Map.Entry)iter.next();
    	   List users = (List)me.getValue();
    	   Definition userDef = (Definition)me.getKey();
    	   addEntries(users, User.class, binder, userDef); 
    	}
       
       defList = root.selectNodes("/profiles/group");
       deleteNames.clear();
       List groupList = new ArrayList();
       for (int i=0; i<defList.size(); ++i) {
    	   //get default definition to use
    	   Element group = (Element)defList.get(i);
    	   if ("delete".equals(group.attributeValue("operation"))) {
    		   Element nameEle = (Element)group.selectSingleNode("./attribute[@name='" + ObjectKeys.XTAG_PRINCIPAL_NAME + "']");
    		   if (nameEle == null) continue;
    		   String name = nameEle.getTextTrim();
    		   if (Validator.isNotNull(name)) deleteNames.add(name.toLowerCase());
    		   continue;
    	   }
    	   groupList.add(group);
        }
       //delete groups first
       if (!deleteNames.isEmpty()) {
    	   Map params = new HashMap();
    	   params.put("plist", deleteNames);
    	   params.put("zoneId", binder.getZoneId());
    	   List<Principal> deleteGroups = getCoreDao().loadObjects("from com.sitescape.team.domain.Group where zoneId=:zoneId and name in (:plist)", params);
    	   if (!deleteGroups.isEmpty()) {
    		   deleteEntries(binder, deleteGroups);
    	   }
       }

	   Group temp = new Group();
	   getDefinitionModule().setDefaultEntryDefinition(temp);
	   Definition defaultGroupDef = temp.getEntryDef();
   	   addEntries(groupList, Group.class, binder, defaultGroupDef);  	   
    }
  	//no transaction
    private void deleteEntries(final ProfileBinder binder, final Collection<Principal> entries) {
		getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		   try {
        			   for (Principal p:entries) {
        				   deleteEntry(binder.getId(), p.getId(), true);
        			   }
        		   } catch  (AccessControlException ac) {
        			   //can't do one, can't do any
        			   logger.error(ac.getLocalizedMessage());
        		   }
			return null;
	     }});

    }
    private void addEntries(List elements, Class clazz, ProfileBinder binder, Definition def) {
       ProfileCoreProcessor processor=loadProcessor(binder);
   	   Map newEntries = new TreeMap(String.CASE_INSENSITIVE_ORDER);
   	   Map oldEntries = new HashMap();
   	   Map foundNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);
   	   for (int j=0; j<elements.size();) {
   		   newEntries.clear();
   		   for (int k=j; k < elements.size() && k-j<100; ++k) {
				ElementInputData e = new ElementInputData((Element)elements.get(k)); 
				String n = e.getSingleValue(ObjectKeys.XTAG_PRINCIPAL_NAME);
				if (Validator.isNull(n)) {
					logger.error("Name attribute missing: " + e.toString());
					continue;
				}
				if (foundNames.containsKey(n)) {
					logger.error("Duplicate name found: " + n);
				} else {
					newEntries.put(n, e);
					foundNames.put(n, Boolean.TRUE); //save as processed
				}
			}
			j+= 100;
			if (newEntries.isEmpty()) continue;
			//make sure don't exist
			Map params = new HashMap();
			params.put("plist", newEntries.keySet());
			params.put("zoneId", binder.getZoneId());
			List<Principal> exists = getCoreDao().loadObjects("from com.sitescape.team.domain.Principal where zoneId=:zoneId and name in (:plist)", params);
			
			for (int x=0;x<exists.size(); ++x) {
				Principal p = (Principal)exists.get(x);
				ElementInputData data = (ElementInputData)newEntries.get(p.getName());
				if (data != null && !p.isDeleted()) {
					newEntries.remove(p.getName());
					oldEntries.put(p,data);
				}
				if(logger.isDebugEnabled())
					logger.debug("Principal exists: " + p.getName());
			}
			if (!newEntries.isEmpty() || !oldEntries.isEmpty()) {
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Updating principals:");
						for (Iterator iter=oldEntries.keySet().iterator(); iter.hasNext();) {
							logger.info("'" + iter.next() + "'");
						}
					}
					processor.syncEntries(oldEntries);
					//processor commits entries - so update indexnow
					IndexSynchronizationManager.applyChanges();
				} catch (Exception ex) {
					IndexSynchronizationManager.discardChanges();
					logger.error("Error updating principals:", ex);
				}
				//flush from cache
				for (int i=0; i<exists.size(); ++i) getCoreDao().evict(exists.get(i));
				//returns list of user objects
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Creating principals:");
						for (Iterator iter=newEntries.keySet().iterator(); iter.hasNext();) {
							logger.info("'" + iter.next() + "'");
						}
					}
					List addedEntries = processor.syncNewEntries(binder, def, clazz, new ArrayList(newEntries.values()));
					//processor commits entries - so update indexnow
					IndexSynchronizationManager.applyChanges();
					//flush from cache
					for (int i=0; i<addedEntries.size(); ++i) getCoreDao().evict(addedEntries.get(i));			
				} catch (Exception ex) {
					IndexSynchronizationManager.discardChanges();
					logger.error("Error creating principals:", ex);
				}
			}
  	   }
    }
    //NO transaction
    public Workspace addUserWorkspace(User entry) throws AccessControlException {
        if (entry.getWorkspaceId() != null) {
        	try {
        		return (Workspace)getCoreDao().loadBinder(entry.getWorkspaceId(), entry.getZoneId()); 
        	} catch (Exception ex) {};
        }
		Workspace ws = null;
		String wsTitle = entry.getTitle() + " ("+ entry.getName()+")";
        RequestContext oldCtx = RequestContextHolder.getRequestContext();
        //want the user to be the creator
        RequestContextUtil.setThreadContext(entry);
 		try {	
  			if (!entry.isReserved() || (!ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID.equals(entry.getInternalId()) &&
 					!ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(entry.getInternalId()))) {
  				List templates = getCoreDao().loadConfigurations(entry.getZoneId(), Definition.USER_WORKSPACE_VIEW);

  				if (!templates.isEmpty()) {
  					//	pick the first
  					TemplateBinder template = (TemplateBinder)templates.get(0);
  					Long wsId = getAdminModule().addBinderFromTemplate(template.getId(), entry.getParentBinder().getId(), wsTitle, entry.getName());
  					ws = (Workspace)getCoreDao().loadBinder(wsId, entry.getZoneId());
  				}
  			}
  			if (ws == null) {
  				//just load a workspace without all the stuff underneath
  				//processor handles transaction
  				Definition userDef = getDefinitionModule().addDefaultDefinition(Definition.USER_WORKSPACE_VIEW);
  				ProfileCoreProcessor processor=loadProcessor((ProfileBinder)entry.getParentBinder());
  				Map updates = new HashMap();
  				updates.put(ObjectKeys.FIELD_BINDER_NAME, entry.getName());
  				updates.put(ObjectKeys.FIELD_ENTITY_TITLE, wsTitle);
        		updates.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);
  				ws = (Workspace)processor.addBinder(entry.getParentBinder(), userDef, Workspace.class, new MapInputData(updates), null, null);				
  			}
   		} catch (WriteFilesException wf) {
   			logger.error("Error create user workspace: ", wf);
   			
   		} finally {
   			//	leave new context for indexing
   			RequestContextHolder.setRequestContext(oldCtx);				
   		}
  		
        return ws;
   }

    //NO transaction
    public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        checkAccess(binder, ProfileOperation.addEntry);
        Definition definition;
        if (!Validator.isNull(definitionId))
        	definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        else {
        	//get the default
        	definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_GROUP_VIEW);
        }
        try {
        	return loadProcessor(binder).addEntry(binder, definition, Group.class, inputData, fileItems, null).getId();
        } catch (DataIntegrityViolationException de) {
        	throw new ObjectExistsException("errorcode.group.exists", (Object[])null, de);
        }
    }

    //RW transaction
    public void deleteEntry(Long binderId, Long principalId, boolean deleteWS) {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, principalId);
        checkAccess(entry, ProfileOperation.deleteEntry);
       	if (entry.isReserved()) 
    		throw new NotSupportedException("errorcode.principal.reserved", new Object[]{entry.getName()});       	
       	processor.deleteEntry(binder, entry, true); // third arg is irrelevant 
        if (deleteWS && (entry instanceof User)) {
        	//delete workspace
        	User u = (User)entry;
        	Long wsId = u.getWorkspaceId();
        	if (wsId != null) {
        		try {
        			getBinderModule().deleteBinder(wsId, true);
        			u.setWorkspaceId(null);       		
        		} catch (Exception ue) {}    
        	}
        }
     }
    
    //RO transaction
   public Map getUsers(Long binderId) {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getUsers(binderId, options);
    }
	//RO transaction
    public Map getUsers(Long binderId, Map options) {
        ProfileBinder binder = loadBinder(binderId);
		checkReadAccess(binder);
        return loadProcessor(binder).getBinderEntries(binder, userDocType, options);
        
   }
	//RO transaction 
	public SortedSet<User> getUsers(Collection<Long> entryIds) {
		//does read check
		ProfileBinder profile = getProfileBinder();
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<User> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadUsers(entryIds, profile.getZoneId()));
 		return result;
	}
	
	public String getUserIds(SortedSet<User> users, String strSeparator) {
		StringBuffer buf = new StringBuffer();
		if (users == null) return "";
		for (Iterator iter=users.iterator(); iter.hasNext();) {
			User user = (User) iter.next();
			Long lngUserId = user.getId();
			buf.append(lngUserId.toString());
			buf.append(strSeparator);
		}
		return buf.toString();
	}
   
	//RO transaction
	public SortedSet<User> getUsersFromPrincipals(Collection<Long> principalIds) {
		//does read check
		ProfileBinder profile = getProfileBinder();
		Set ids = getProfileDao().explodeGroups(principalIds, profile.getZoneId());
		return getUsers(ids);
	}

	public class ElementInputData implements InputDataAccessor {
		private Element source;
		public ElementInputData(Element source) {
			this.source = source;
		}
		public String getSingleValue(String key) {
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if (result == null) return null;
			else return result.getTextTrim();
		}

		public String[] getValues(String key) {
			List<Element> result = source.selectNodes("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if ((result == null) || result.isEmpty()) return null;
			String [] resultVals = new String[result.size()];
			for (int i=0; i<resultVals.length; ++i) {
				resultVals[i] = result.get(i).getTextTrim();
			}
			return resultVals;
		}

		public Date getDateValue(String key) {
			return DateHelper.getDateFromInput(this, key);
		}

		public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence)
		{
			return EventHelper.getEventFromMap(this, key, hasDuration, hasRecurrence);
		}

		public Survey getSurveyValue(String key)
		{
			return new Survey(key);
		}
		
		public boolean exists(String key) {
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
			if (result == null) return false;
			return true;
		}

		public Object getSingleObject(String key) {
			return (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./property[@name='" + key + "']");
		}
		public int getCount() {
			return source.nodeCount();
		}

	}
	
    //NO transaction
	public User addUserFromPortal(String zoneName, String userName, String password, Map updates) {
		if(updates == null)
			updates = new HashMap();
		
		// The minimum we require is the last name. If it isn't available,
		// we use the user's login name as the last name just for now.
		// User can change it later if desired.
		if(updates.get("lastName") == null)
			updates.put("lastName", userName);
		
		// build user
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		Binder top = getCoreDao().findTopWorkspace(zoneName);
		RequestContextUtil.setThreadContext(getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, top.getZoneId()));
		try {
			ProfileBinder profiles = getProfileDao().getProfileBinder(top.getZoneId());
			// indexing needs the user
			ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager()
					.getProcessor(profiles, profiles.getProcessorKey(ProfileCoreProcessor.PROCESSOR_KEY));
			Map newUpdates = new HashMap(updates);
			newUpdates.put(ObjectKeys.FIELD_PRINCIPAL_NAME, userName);
			if (Validator.isNotNull(password)) newUpdates.put(ObjectKeys.FIELD_USER_PASSWORD, password);
			//get default definition to use
			Definition userDef = profiles.getDefaultEntryDef();		
			if (userDef == null) userDef = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_ENTRY_VIEW);
			List<InputDataAccessor>accessors = new ArrayList();
			accessors.add(new MapInputData(newUpdates));
			User user = (User)processor.syncNewEntries(profiles, userDef, User.class, accessors).get(0);
			// flush user before adding workspace
			IndexSynchronizationManager.applyChanges();
			
			addUserWorkspace(user);
			//do now, with request context set
			IndexSynchronizationManager.applyChanges();
			return user;
		} finally {
			// leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);
		}
	}

    //No transaction
	public void modifyUserFromPortal(User user, Map updates) {
		if (updates == null)
			return; // nothing to update with
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(user);
		try {
			//transaction handled in processor
			//use processor to handle title changes
			ProfileCoreProcessor processor = (ProfileCoreProcessor)getProcessorManager().getProcessor(user.getParentBinder(), 
					user.getParentBinder().getProcessorKey(ProfileCoreProcessor.PROCESSOR_KEY));
			processor.syncEntry(user, new MapInputData(updates));
			// flush user before adding workspace
			IndexSynchronizationManager.applyChanges();
			if (user.getWorkspaceId() == null) addUserWorkspace(user);

			//do now, with request context set
			IndexSynchronizationManager.applyChanges();
		} finally {
			//leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);				
		};
	}

}

