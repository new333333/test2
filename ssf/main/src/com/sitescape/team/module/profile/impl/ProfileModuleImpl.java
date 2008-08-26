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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectExistsException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.PrincipalComparator;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.ApplicationGroup;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.GroupPrincipal;
import com.sitescape.team.domain.IndividualPrincipal;
import com.sitescape.team.domain.NoApplicationByTheNameException;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.NoGroupByTheNameException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.SharedEntity;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.UserPropertiesPK;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.profile.processor.ProfileCoreProcessor;
import com.sitescape.team.module.shared.AccessUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.template.TemplateModule;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.EventHelper;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;

public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
	private static final int DEFAULT_MAX_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	private String[] userDocType = {Constants.ENTRY_TYPE_USER};
	private String[] groupDocType = {Constants.ENTRY_TYPE_GROUP};
	private String[] applicationDocType = {Constants.ENTRY_TYPE_APPLICATION};
	private String[] applicationGroupDocType = {Constants.ENTRY_TYPE_APPLICATION_GROUP};
	private String[] individualPrincipalDocType = {Constants.ENTRY_TYPE_USER, Constants.ENTRY_TYPE_APPLICATION};
	private String[] groupPrincipalDocType = {Constants.ENTRY_TYPE_GROUP, Constants.ENTRY_TYPE_APPLICATION_GROUP};
	
	private String[] allPrincipalDocType = {Constants.ENTRY_TYPE_USER, Constants.ENTRY_TYPE_GROUP, Constants.ENTRY_TYPE_APPLICATION, Constants.ENTRY_TYPE_APPLICATION_GROUP};
	private List<String> guestSavedProps = Arrays.asList(new String[]{ObjectKeys.USER_PROPERTY_PERMALINK_URL});
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
    
    protected TemplateModule templateModule;
    protected TemplateModule getTemplateModule() {
    	return templateModule;
    }
    public void setTemplateModule(TemplateModule templateModule) {
    	this.templateModule = templateModule;
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
    private ProfileCoreProcessor loadProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.team.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
	    return (ProfileCoreProcessor) getProcessorManager().getProcessor(binder, ProfileCoreProcessor.PROCESSOR_KEY);
	}
	private ProfileBinder loadProfileBinder() {
	   return (ProfileBinder)getProfileDao().getProfileBinder(RequestContextHolder.getRequestContext().getZoneId());
	}
	private User getUser(Long userId, boolean modify) {
  		User currentUser = RequestContextHolder.getRequestContext().getUser();
   		User user;
		if (userId == null) user = currentUser;
		else if (userId.equals(currentUser.getId())) user = currentUser;
		else {
			user = getProfileDao().loadUser(userId, currentUser.getZoneId());
			if (modify) AccessUtils.modifyCheck(user);
			else AccessUtils.readCheck(user);
		}
		return user;		
	}
	private UserProperties getProperties(User user, Long binderId) {
		UserProperties uProps=null;
		if (user.isShared()) { //better be the current user
			UserPropertiesPK key = new UserPropertiesPK(user.getId(), binderId);
			uProps = (UserProperties)RequestContextHolder.getRequestContext().getSessionContext().getProperty(key);
			if (uProps == null) {
				//load any saved props
				UserProperties gProps = getProfileDao().loadUserProperties(user.getId(), binderId);
				uProps = new GuestProperties(gProps);
				RequestContextHolder.getRequestContext().getSessionContext().setProperty(key, uProps);					
			}
		} else {
			uProps = getProfileDao().loadUserProperties(user.getId(), binderId);
		}
		return uProps;
	}
	private UserProperties getProperties(User user) {
		UserProperties uProps=null;
		if (user.isShared()) { //better be the current user
			UserPropertiesPK key = new UserPropertiesPK(user.getId());
			uProps = (UserProperties)RequestContextHolder.getRequestContext().getSessionContext().getProperty(key);
			if (uProps == null) {
				//load any saved props
				UserProperties gProps = getProfileDao().loadUserProperties(user.getId());
				uProps = new GuestProperties(gProps);
				RequestContextHolder.getRequestContext().getSessionContext().setProperty(key, uProps);				
			}
		} else {
			uProps = getProfileDao().loadUserProperties(user.getId());
		}
		return uProps;
	}
	//RO transaction
	public ProfileBinder getProfileBinder() {
	   ProfileBinder binder = loadProfileBinder();
		// Check if the user has "read" access to the folder.
	   checkReadAccess(binder);		
	   return binder;
    }

    public Long getEntryWorkspaceId(Long principalId) {
        Principal p = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), false);              
        return  p.getWorkspaceId();
    }

    //RW transaction
	public UserProperties setUserProperty(Long userId, Long binderId, String property, Object value) {
   		User user = getUser(userId, true);
		UserProperties uProps=getProperties(user, binderId);
		uProps.setProperty(property, value); 
		if (user.isShared() && guestSavedProps.contains(property)) {
			//get real props and save value
			UserProperties gProps = getProfileDao().loadUserProperties(user.getId(), binderId);
			gProps.setProperty(property, value);
		}
 		return uProps;
   }
    //RW transaction
	public UserProperties setUserProperties(Long userId, Long binderId, Map<String, Object> values) {
   		User user = getUser(userId, true);
		UserProperties uProps=getProperties(user, binderId);
		UserProperties gProps=null;
		for (Map.Entry<String, Object> me: values.entrySet()) {
 			uProps.setProperty(me.getKey(), me.getValue()); //saved in requestContext
 			if (user.isShared() && guestSavedProps.contains(me.getKey())) {
 				//get real props and save value
 				if (gProps == null) gProps = getProfileDao().loadUserProperties(user.getId(), binderId);
 				gProps.setProperty(me.getKey(), me.getValue());
 			}
		}

  		return uProps;		   
	}
	   
	//RO transaction
   public UserProperties getUserProperties(Long userId, Long binderId) {
  		User user = getUser(userId, false);
		return getProperties(user, binderId);
   }

   //RW transaction
   public UserProperties setUserProperty(Long userId, String property, Object value) {
 		User user = getUser(userId, true);
		UserProperties uProps = getProperties(user);
		uProps.setProperty(property, value); 	
		if (user.isShared() && guestSavedProps.contains(property)) {
			//get real props and save value
			UserProperties gProps = getProfileDao().loadUserProperties(user.getId());
			gProps.setProperty(property, value);
		}
		return uProps;
    }
   //RW transaction
   public UserProperties setUserProperties(Long userId, Map<String, Object> values) {
		User user = getUser(userId, true);
		UserProperties uProps = getProperties(user);
		UserProperties gProps = null;
		for (Map.Entry<String, Object> me: values.entrySet()) {
 			uProps.setProperty(me.getKey(), me.getValue()); 
			if (user.isShared() && guestSavedProps.contains(me.getKey())) {
				//get real props and save value
				if (gProps == null) gProps = getProfileDao().loadUserProperties(user.getId());
 				gProps.setProperty(me.getKey(), me.getValue());
 			}
 		}
		return uProps;
	  
   }
	//RO transaction
   public UserProperties getUserProperties(Long userId) {
		User user = getUser(userId, false);
		return getProperties(user);
   }
 	//RO transaction
   public SeenMap getUserSeenMap(Long userId) {
		User user = getUser(userId, false);
		if (user.isShared()) return new SharedSeenMap(user.getId());
 		return getProfileDao().loadSeenMap(user.getId());
   }
   //RW transaction
   public void setSeen(Long userId, Entry entry) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap seen = getProfileDao().loadSeenMap(user.getId());
		seen.setSeen(entry);
  }
   //RW transaction
   public void setSeen(Long userId, Collection<Entry> entries) {
		User user = getUser(userId, true);
		if (user.isShared()) return;
		SeenMap	seen = getProfileDao().loadSeenMap(user.getId());
		for (Entry reply:entries) {
			seen.setSeen(reply);
		}
   }  	

   //RW transaction
   public void setStatus(String status) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    user.setStatus(status);
   }  	
   //RW transaction
   public void setStatusDate(Date statusDate) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    user.setStatusDate(statusDate);
   }  	
   //RO transaction
   public Group getGroup(String name) {
	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
	  if (!(p instanceof Group)) throw new NoGroupByTheNameException(name);
	  checkReadAccess(p);			
	  return (Group)p;
   }
	//RO transaction
   public Map getGroups() {
	   Map options = new HashMap();
	   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
	   return getGroups(options);
   }
 
	//RO transaction
   public Map getGroups(Map options) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
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
	public Principal getEntry(String name) {
		return getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
	}
	//RO transaction
	public Principal getEntry(Long principalId) {
        Principal p = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), false);              
    	checkReadAccess(p);			
        return p;
    }

	public SortedSet<Principal> getPrincipals(Collection<Long> ids) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
 	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Principal> result = new TreeSet(c);
 		result.addAll(getProfileDao().loadPrincipals(ids, user.getZoneId(), false));
 		return result;
	}
    
    //***********************************************************************************************************	
	public void indexEntry(Principal entry) {
        ProfileCoreProcessor processor=loadProcessor((ProfileBinder)entry.getParentBinder());
        processor.indexEntry(entry);

	}


    //NO transaction
    public void modifyEntry(Long id, InputDataAccessor inputData) 
	throws AccessControlException, WriteFilesException {
    	modifyEntry(id, inputData, null, null, null, null);
    }
    //NO transaction
   public void modifyEntry(Long entryId, InputDataAccessor inputData, 
		   Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
   		throws AccessControlException, WriteFilesException {
	   Principal entry = getProfileDao().loadPrincipal(entryId, RequestContextHolder.getRequestContext().getZoneId(), false);              
       Binder binder = entry.getParentBinder();
       ProfileCoreProcessor processor=loadProcessor(binder);
       //user can set their own display style and theme
       int noCheckCount = 0;
       if (inputData.exists(ObjectKeys.FIELD_USER_DISPLAYSTYLE)) ++noCheckCount;
       if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_THEME)) ++noCheckCount;
   	        
       if (!RequestContextHolder.getRequestContext().getUserId().equals(entryId) ||
    		   (inputData.getCount() == -1) ||
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
       processor.modifyEntry(binder, entry, inputData, fileItems, atts, fileRenamesTo, options);
      }

   //NO transaction
    public void addEntries(Document doc, Map options) {
       ProfileBinder binder = loadProfileBinder();
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
    		   deleteEntries(binder, deleteUsers, options);
    	   }
       }
    		   //add entries grouped by definitionId
       for (Iterator iter=userLists.entrySet().iterator(); iter.hasNext();) {
    	   Map.Entry me = (Map.Entry)iter.next();
    	   List users = (List)me.getValue();
    	   Definition userDef = (Definition)me.getKey();
    	   addEntries(users, User.class, binder, userDef, options); 
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
    		   deleteEntries(binder, deleteGroups, options);
    	   }
       }

	   Group temp = new Group();
	   getDefinitionModule().setDefaultEntryDefinition(temp);
	   Definition defaultGroupDef = temp.getEntryDef();
   	   addEntries(groupList, Group.class, binder, defaultGroupDef, options);  	   
    }
  	//no transaction
    private void deleteEntries(final ProfileBinder binder, final Collection<Principal> entries, final Map options) {
		getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		   try {
        			   for (Principal p:entries) {
        				   deleteEntry(p.getId(), options);
        			   }
        		   } catch  (AccessControlException ac) {
        			   //can't do one, can't do any
        			   logger.error(ac.getLocalizedMessage());
        		   }
			return null;
	     }});

    }
    private void addEntries(List elements, Class clazz, ProfileBinder binder, Definition def, Map options) {
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
					processor.syncEntries(oldEntries, options);
					//processor commits entries - so update indexnow
					IndexSynchronizationManager.applyChanges();
				} catch (Exception ex) {
					IndexSynchronizationManager.discardChanges();
					logger.error("Error updating principals:", ex);
				}
				//flush from cache
				getCoreDao().evict(exists);
				//returns list of user objects
				try {
					if (logger.isInfoEnabled()) {
						logger.info("Creating principals:");
						for (Iterator iter=newEntries.keySet().iterator(); iter.hasNext();) {
							logger.info("'" + iter.next() + "'");
						}
					}
					List addedEntries = processor.syncNewEntries(binder, def, clazz, new ArrayList(newEntries.values()), options);
					//processor commits entries - so update indexnow
					IndexSynchronizationManager.applyChanges();
					//flush from cache
					getCoreDao().evict(addedEntries);			
				} catch (Exception ex) {
					IndexSynchronizationManager.discardChanges();
					logger.error("Error creating principals:", ex);
				}
			}
  	   }
    }
    //NO transaction
    public Workspace addUserWorkspace(User entry, Map options) throws AccessControlException {
        if (entry.getWorkspaceId() != null) {
        	try {
        		return (Workspace)getCoreDao().loadBinder(entry.getWorkspaceId(), entry.getZoneId()); 
        	} catch (Exception ex) {};
        }
		Workspace ws = null;
		String wsTitle = entry.getTitle() + " ("+ entry.getName()+")";
        RequestContext oldCtx = RequestContextHolder.getRequestContext();
        //want the user to be the creator
        RequestContextUtil.setThreadContext(entry).resolve();
 		try {	
  			if (!entry.isReserved() || (!ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID.equals(entry.getInternalId()) &&
 					!ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(entry.getInternalId()))) {
  				List templates = getCoreDao().loadTemplates(entry.getZoneId(), Definition.USER_WORKSPACE_VIEW);

  				if (!templates.isEmpty()) {
  					//	pick the first
  					TemplateBinder template = (TemplateBinder)templates.get(0);
  					Long wsId = getTemplateModule().addBinder(template.getId(), entry.getParentBinder().getId(), wsTitle, entry.getName());
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
  				ws = (Workspace)processor.addBinder(entry.getParentBinder(), userDef, Workspace.class, new MapInputData(updates), null, options);				
  			}
   		} catch (WriteFilesException wf) {
   			logger.error("Error create user workspace: ", wf);
   			
   		} finally {
   			//	leave new context for indexing
   			RequestContextHolder.setRequestContext(oldCtx);				
   		}
  		
        return ws;
   }


    //RW transaction
    public void deleteEntry(Long principalId, Map options) {
        Principal entry = getProfileDao().loadPrincipal(principalId, RequestContextHolder.getRequestContext().getZoneId(), true);
        checkAccess(entry, ProfileOperation.deleteEntry);
       	if (entry.isReserved()) 
    		throw new NotSupportedException("errorcode.principal.reserved", new Object[]{entry.getName()});       	
        Binder binder = entry.getParentBinder();
        ProfileCoreProcessor processor=loadProcessor(binder);
       	processor.deleteEntry(binder, entry, true, options); 
       	boolean delWs = Boolean.FALSE;
       	if (options != null) delWs = (Boolean)options.get(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE);
       	if (Boolean.TRUE.equals(delWs) && (entry instanceof User)) {
        	//delete workspace
        	User u = (User)entry;
        	Long wsId = u.getWorkspaceId();
        	if (wsId != null) {
        		try {
        			getBinderModule().deleteBinder(wsId, true, options);
        			u.setWorkspaceId(null);       		
        		} catch (Exception ue) {}    
        	}
        }
     }
    
    //RO transaction
    public User getUser(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof User)) throw new NoUserByTheNameException(name);
 	  return (User)p;
    }
    
    //RO transaction
   public Map getUsers() {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getUsers(options);
    }
	//RO transaction
    public Map getUsers(Map options) {
		//does read check
		ProfileBinder binder = getProfileBinder();
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
	  
	//RO transaction
	public SortedSet<User> getUsersFromPrincipals(Collection<Long> principalIds) {
		//does read check
		ProfileBinder profile = getProfileBinder();
		Set ids = getProfileDao().explodeGroups(principalIds, profile.getZoneId());
		return getUsers(ids);
	}

	public User findUserByName(String username)  throws NoUserByTheNameException {
		return getProfileDao().findUserByName(username, RequestContextHolder.getRequestContext().getZoneId());
	}
	
	public Collection<Principal> getPrincipalsByName(Collection<String> names) throws AccessControlException {
		Map params = new HashMap();
		params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());
		params.put("name", names);
		return getCoreDao().loadObjects("from com.sitescape.team.domain.Principal where zoneId=:zoneId and name in (:name)", params);

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
	public User addUserFromPortal(String userName, String password, Map updates, Map options) {
		if(updates == null)
			updates = new HashMap();
		
		// The minimum we require is the last name. If it isn't available,
		// we use the user's login name as the last name just for now.
		// User can change it later if desired.
		if(updates.get("lastName") == null)
			updates.put("lastName", userName);
		
		// build user
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		Binder top = getCoreDao().findTopWorkspace(RequestContextHolder.getRequestContext().getZoneName());
		RequestContextUtil.setThreadContext(getProfileDao().getReservedUser(ObjectKeys.JOB_PROCESSOR_INTERNALID, top.getZoneId())).resolve();
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
			User user = (User)processor.syncNewEntries(profiles, userDef, User.class, accessors, options).get(0);
			// flush user before adding workspace
			IndexSynchronizationManager.applyChanges();
			
			addUserWorkspace(user, options);
			//do now, with request context set
			IndexSynchronizationManager.applyChanges();
			return user;
		} finally {
			// leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);
		}
	}

    //No transaction
	public void modifyUserFromPortal(User user, Map updates, Map options) {
		if (updates == null)
			return; // nothing to update with
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(user).resolve();
		try {
			//transaction handled in processor
			//use processor to handle title changes
			ProfileCoreProcessor processor = (ProfileCoreProcessor)getProcessorManager().getProcessor(user.getParentBinder(), 
					user.getParentBinder().getProcessorKey(ProfileCoreProcessor.PROCESSOR_KEY));
			processor.syncEntry(user, new MapInputData(updates), options);
			// flush user before adding workspace
			IndexSynchronizationManager.applyChanges();
			if (user.getWorkspaceId() == null) addUserWorkspace(user, options);

			//do now, with request context set
			IndexSynchronizationManager.applyChanges();
		} finally {
			//leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);				
		};
	}
	protected class SharedSeenMap extends SeenMap {
		public SharedSeenMap(Long principalId) {
			super(principalId);
		}
	    public void setSeen(Entry entry) {	    	
	    }
	    public void setSeen(FolderEntry entry) {
	    }
	    public boolean checkIfSeen(FolderEntry entry) {
	    	return true;
	    }
		protected boolean checkAndSetSeen(FolderEntry entry, boolean setIt) {
			return true;
		}
		public boolean checkAndSetSeen(Map entry, boolean setIt) {
			return true;
		}	
	    public boolean checkIfSeen(Map entry) {
	    	return true;
	    }   
	    
		public boolean checkAndSetSeen(Long id, Date modDate, boolean setIt) {
			return true;
		}
    }
	protected class GuestProperties extends UserProperties implements java.io.Serializable {
		static final long serialVersionUID = 12345;
	    protected GuestProperties(UserProperties uProps) {
	    	setId(uProps.getId());
	    	//session access may be concurrent, so use synchronzied map
	    	Map props = java.util.Collections.synchronizedMap(new HashMap());
	    	props.putAll(uProps.getProperties());
	    	setProperties(props);	    	
	    }
	}

    //RW transaction
	public void deleteUserByName(String userName,  Map options) {
		try {
			User user = getProfileDao().findUserByName(userName, 
					RequestContextHolder.getRequestContext().getZoneName());
			deleteEntry(user.getId(),options);
		}
		catch(NoUserByTheNameException thisIsOk) {}
	}
	//RW transaction
	public void addUserToGroup(Long userId, String username, Long groupId) {
		Group group = (Group)getProfileDao().loadGroup(groupId, RequestContextHolder.getRequestContext().getZoneId());		
		checkAccess(group, ProfileOperation.modifyEntry);   
		Principal user;
		if (Validator.isNotNull(username)) {	
			//could be user or group
			user = getProfileDao().findPrincipalByName(username, RequestContextHolder.getRequestContext().getZoneId());
		} else {
			user = getProfileDao().loadUserPrincipal(userId, RequestContextHolder.getRequestContext().getZoneId(), true);
		}
		group.addMember(user);
	}
	
	//NO transaction
	public Long addUser(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
	throws AccessControlException, WriteFilesException {
		return addIndividualPrincipal(definitionId, inputData, fileItems, options, User.class);
	}
	//NO transaction
	public Long addApplication(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options) 
	throws AccessControlException, WriteFilesException {
    	return addIndividualPrincipal(definitionId, inputData, fileItems, options, Application.class);
	}
    //NO transaction
    public Long addGroup(String definitionId, InputDataAccessor inputData, Map fileItems, Map options) 
    	throws AccessControlException, WriteFilesException {
    	return addGroupPrincipal(definitionId, inputData, fileItems, options, Group.class);
    }
	
    //NO transaction
	public Long addApplicationGroup(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options) 
	throws AccessControlException, WriteFilesException {
    	return addGroupPrincipal(definitionId, inputData, fileItems, options, ApplicationGroup.class);
	}
	
	protected Long addIndividualPrincipal(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options, Class clazz) 
	throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.addEntry);
        Definition definition;
        if (!Validator.isNull(definitionId))
        	definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        else {
        	// get the default
           	if(clazz.equals(User.class))
        		definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_ENTRY_VIEW);
           	else
           		definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_APPLICATION_VIEW);
        }
        try {
        	return loadProcessor(binder).addEntry(binder, definition, clazz, inputData, fileItems, options).getId();
        } catch (DataIntegrityViolationException de) {
        	if(clazz.equals(User.class))
        		throw new ObjectExistsException("errorcode.user.exists", (Object[])null, de);
        	else
            	throw new ObjectExistsException("errorcode.application.exists", (Object[])null, de);
        }
	}

	protected Long addGroupPrincipal(String definitionId, 
			InputDataAccessor inputData, Map fileItems, Map options, Class clazz) 
	throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadProfileBinder();
        checkAccess(binder, ProfileOperation.addEntry);
        Definition definition;
        if (!Validator.isNull(definitionId))
        	definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        else {
        	//get the default
        	if(clazz.equals(Group.class))
        		definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_GROUP_VIEW);
        	else
        		definition = getDefinitionModule().addDefaultDefinition(Definition.PROFILE_APPLICATION_GROUP_VIEW);        		
        }
        try {
        	return loadProcessor(binder).addEntry(binder, definition, clazz, inputData, fileItems, options).getId();
        } catch (DataIntegrityViolationException de) {
        	if(clazz.equals(Group.class))
        		throw new ObjectExistsException("errorcode.group.exists", (Object[])null, de);
        	else
        		throw new ObjectExistsException("errorcode.applicationgroup.exists", (Object[])null, de);        		
        }
	}
    //RO transaction
    public ApplicationGroup getApplicationGroup(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof ApplicationGroup)) throw new NoGroupByTheNameException(name);
 	  return (ApplicationGroup)p;
    }

	//RO transaction
	public Map getApplicationGroups() throws AccessControlException {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getApplicationGroups(options);
	}
	
	//RO transaction
	public Map getApplicationGroups(Map searchOptions) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, applicationGroupDocType, searchOptions);        
	}
	
	//RO transaction
	public SortedSet<ApplicationGroup> getApplicationGroups(Collection<Long> groupIds) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<ApplicationGroup> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadApplicationGroups(groupIds, user.getZoneId()));
		return result;	
	}
	
	//RO transaction
	public Map getGroupPrincipals() throws AccessControlException {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getGroupPrincipals( options);	
	}
	
	//RO transaction
	public Map getGroupPrincipals(Map searchOptions) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, groupPrincipalDocType, searchOptions);        
	}
	
	//RO transaction
	public SortedSet<GroupPrincipal> getGroupPrincipals(Collection<Long> groupIds) throws AccessControlException {
		//does read access check
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<GroupPrincipal> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadGroupPrincipals(groupIds, user.getZoneId(), true));
		return result;	
	}
	
    //RO transaction
    public Application getApplication(String name) {
 	  Principal p = getProfileDao().findPrincipalByName(name, RequestContextHolder.getRequestContext().getZoneId());
 	  if (!(p instanceof Application)) throw new NoApplicationByTheNameException(name);
 	  return (Application)p;
    }
	//RO transaction
	public Map getApplications() {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getApplications( options);
	}
	//RO transaction
	public Map getApplications(Map searchOptions) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, applicationDocType, searchOptions);
	}
	//RO transaction
	public SortedSet<Application> getApplications(Collection<Long> applicationIds) {
		//does read check
		ProfileBinder profile = getProfileBinder();
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Application> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadApplications(applicationIds, profile.getZoneId()));
 		return result;
	}
	
	//RO transaction
	public Map getIndividualPrincipals() {
		   Map options = new HashMap();
		   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
		   return getIndividualPrincipals(options);	
	}
	//RO transaction
	public Map getIndividualPrincipals( Map searchOptions) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, individualPrincipalDocType, searchOptions);        
	}
	//RO transaction
	public SortedSet<IndividualPrincipal> getIndividualPrincipals(Collection<Long> individualIds) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<IndividualPrincipal> result = new TreeSet(c);
       	result.addAll(getProfileDao().loadIndividualPrincipals(individualIds, user.getZoneId(), true));
		return result;	
	}
	//RO transaction
	public Map getPrincipals( Map searchOptions) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
        return loadProcessor(binder).getBinderEntries(binder, allPrincipalDocType, searchOptions);        
	}
    //RO transaction
    public List<SharedEntity> getShares(Long userId, Date after) {
	    User user = getUser(userId, false);
	    //get list of all groups user is a member of.
	    Set<Long> accessIds = getProfileDao().getPrincipalIds(user);
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId());
		Set<Long>binderIds = new HashSet();
		for(Map binder : myTeams) {
			try {
				binderIds.add(Long.valueOf((String)binder.get(Constants.DOCID_FIELD)));
			} catch (Exception ignore) {};
		}

	  List<SharedEntity> shares = getProfileDao().loadSharedEntities(accessIds, binderIds, after, user.getZoneId());
	  //need to check access
	  for (int i=0; i<shares.size();) {
		  SharedEntity se = shares.get(i);
		  try {
			  if (se.getEntity().isDeleted()) {
				  shares.remove(i);
				  continue;
			  }
		  } catch(Exception skipThis) {
			  shares.remove(i);
			  continue;
		  }
		  if (se.getEntity() instanceof Binder) {
				if (!getAccessControlManager().testOperation(user, (Binder)se.getEntity(), WorkAreaOperation.READ_ENTRIES)) {
					shares.remove(i);
				} else {
					++i;
				}
		  } else {
			  try {
				  AccessUtils.readCheck((Entry)se.getEntity());
				  ++i;
			  } catch (Exception ex) {
					shares.remove(i);
			  }
		  }
	  }
	  return shares;
    }
    //RW transaction
    public void setShares(DefinableEntity entity, Collection<Long> principalIds, Collection<Long> binderIds) {
	    User user = RequestContextHolder.getRequestContext().getUser();
    	if (principalIds != null) {
    		for (Long p: principalIds) {
    			SharedEntity shared = new SharedEntity(user, entity, p, SharedEntity.ACCESS_TYPE_PRINCIPAL);
   	   			getCoreDao().save(shared);
      		}
    	}
    	if (binderIds != null) {
    		for (Long p: binderIds) {
    			SharedEntity shared = new SharedEntity(user, entity, p, SharedEntity.ACCESS_TYPE_TEAM);
    			getCoreDao().save(shared);
    		}
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

}

