/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.team.module.profile.impl;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.NoGroupByTheIdException;
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
import com.sitescape.team.util.NLT;
import com.sitescape.util.Validator;


public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
	private static final int DEFAULT_MAX_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	private String[] userDocType = {EntityIndexUtils.ENTRY_TYPE_USER};
	private String[] groupDocType = {EntityIndexUtils.ENTRY_TYPE_GROUP};
	protected TransactionTemplate transactionTemplate;

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
	/*
	 * Check access to folder.  If operation not listed, assume read_entries needed
	 * @see com.sitescape.team.module.binder.BinderModule#checkAccess(com.sitescape.team.domain.Binder, java.lang.String)
	 */
    //NO transaction
    public boolean testAccess(ProfileBinder binder, String operation) {
		try {
			checkAccess(binder, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	protected void checkAccess(ProfileBinder binder, String operation) throws AccessControlException {
		if ("getProfileBinder".equals(operation)) {
			getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
		} else if ("addFolder".equals(operation)) { 	
	    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATE_BINDERS);
		} else if ("addWorkspace".equals(operation)) { 	
	    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATE_BINDERS);
		} else if (operation.startsWith("add")) {
	    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);
		} else {
	    	getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
		}
	}
	/*
 	 * Check access to folder.  If operation not listed, assume read_entries needed
  	 * Use method names as operation so we can keep the logic out of application
	 * and easisly change the required rights
	 * @see com.sitescape.team.module.profile.ProfileModule#testAccess(com.sitescape.team.domain.Principal, java.lang.String)
	 */
    //NO transaction
	public boolean testAccess(Principal entry, String operation) {
		try {
			checkAccess(entry, operation);
			return true;
		} catch (AccessControlException ac) {
			return false;
		}
	}
	protected void checkAccess(Principal entry, String operation) throws AccessControlException {
		if ("getEntry".equals(operation)) {
	    	AccessUtils.readCheck(entry);			
	    } else if ("deleteEntry".equals(operation)) {
			AccessUtils.deleteCheck(entry);   		
		} else if ("modifyEntry".equals(operation)) {
			AccessUtils.modifyCheck(entry);   		
	    } else {
	    	AccessUtils.readCheck(entry);
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
	   checkAccess(binder, "getProfileBinder");		
	   return binder;
    }

	//RO transaction
	public Principal getEntry(Long binderId, Long principaId) {
        ProfileBinder binder = loadBinder(binderId);
        Principal p = (Principal)loadProcessor(binder).getEntry(binder, principaId);        
        checkAccess(p, "getEntry");
        return p;
    }
    //RW transaction
  public UserProperties setUserProperty(Long userId, Long binderId, String property, Object value) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
  		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
    		uProps = getProfileDao().loadUserProperties(userId, binderId);
			uProps.setProperty(property, value); 	
  		} else throw new NotSupportedException();
  		return uProps;
   }
	//RO transaction
   public UserProperties getUserProperties(Long userId, Long binderId) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
  		if (userId == null) userId = user.getId();
 		if (user.getId().equals(userId)) {
			uProps = getProfileDao().loadUserProperties(userId, binderId);
  		} else throw new NotSupportedException();
		return uProps;
}

   //RW transaction
   public UserProperties setUserProperty(Long userId, String property, Object value) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
  		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
 			uProps = getProfileDao().loadUserProperties(user.getId());
			uProps.setProperty(property, value);			
  		} else throw new NotSupportedException();
		return uProps;
    }
	//RO transaction
   public UserProperties getUserProperties(Long userId) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
 		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
    		uProps = getProfileDao().loadUserProperties(userId);
  		} else throw new NotSupportedException();
  		return uProps;
   }
	//RO transaction
   public SeenMap getUserSeenMap(Long userId) {
		User user = RequestContextHolder.getRequestContext().getUser();
		SeenMap seen = null;
		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
 			 seen = getProfileDao().loadSeenMap(user.getId());
  		} else throw new NotSupportedException();
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
  		} else throw new NotSupportedException();
  }
   //RW transaction
   public void setSeen(Long userId, List entries) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		SeenMap seen;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
			seen = getProfileDao().loadSeenMap(user.getId());
   			for (int i=0; i<entries.size(); i++) {
   				Entry reply = (Entry)entries.get(i);
   				seen.setSeen(reply);
   			}
  		} else throw new NotSupportedException();
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
		checkAccess(binder, "getEntries");
        return loadProcessor(binder).getBinderEntries(binder, groupDocType, options);        
    }
	//RO transaction
	public Collection getGroups(Set entryIds) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
		checkAccess(binder, "getEntries");
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<Group> result = new TreeSet<Group>(c);
		for (Iterator iter=entryIds.iterator(); iter.hasNext();) {
			try {
				// assuming users are cached
				result.add(getProfileDao().loadGroup((Long)iter.next(), binder.getZoneId()));
			} catch (NoGroupByTheIdException ex) {
			}
			
		}
		return result;
	}
    
    //***********************************************************************************************************	
    //NO transaction
    public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        ProfileBinder binder = loadBinder(binderId);
        checkAccess(binder, "addEntry");
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        return loadProcessor(binder).addEntry(binder, definition, User.class, inputData, fileItems).getId();
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
		   Map fileItems, Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo) 
   		throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, entryId);
        //user can set their own display style
        if (!RequestContextHolder.getRequestContext().getUserId().equals(entryId) ||
        		(inputData.getCount() > 1) ||
        		!inputData.exists(ObjectKeys.FIELD_USER_DISPLAYSTYLE)) {
        	checkAccess(entry, "modifyEntry");
        }
       	List atts = new ArrayList();
    	if (deleteAttachments != null) {
    		for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    			String id = (String)iter.next();
    			Attachment a = entry.getAttachment(id);
    			if (a != null) atts.add(a);
    		}
    	}
    	processor.modifyEntry(binder, entry, inputData, fileItems, atts, fileRenamesTo);
      }

   //NO transaction
    public void addEntries(Long binderId, Document doc) {
       ProfileBinder binder = loadBinder(binderId);
       checkAccess(binder, "addEntries");
       //process the document
       Element root = doc.getRootElement();
       List defList = root.selectNodes("/profiles/user");
       Map userLists = new HashMap();
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
   	   //add users in groups of 100
       for (Iterator iter=userLists.entrySet().iterator(); iter.hasNext();) {
    	   Map.Entry me = (Map.Entry)iter.next();
    	   List users = (List)me.getValue();
    	   Definition userDef = (Definition)me.getKey();
    	   List<User> addedUsers = addEntries(users, User.class, binder, userDef);  
    	   for (int j=0; j<addedUsers.size(); ++j) {
    		   addUserWorkspace(addedUsers.get(j));
    	   }   	   
    	   IndexSynchronizationManager.applyChanges();
    	}
       defList = root.selectNodes("/profiles/group");
   	   
	   Group temp = new Group();
	   getDefinitionModule().setDefaultEntryDefinition(temp);
	   Definition defaultGroupDef = temp.getEntryDef();
   	   addEntries(defList, Group.class, binder, defaultGroupDef);  	   
    }
    private List addEntries(List elements, Class clazz, ProfileBinder binder, Definition def) {
       ProfileCoreProcessor processor=loadProcessor(binder);
       List allEntries = new ArrayList();
   	   Map newEntries = new HashMap();
   	   Map oldEntries = new HashMap();
   	   for (int j=0; j<elements.size();) {
   		   newEntries.clear();
   		   for (int k=j; k < elements.size() && k-j<100; ++k) {
				ElementInputData e = new ElementInputData((Element)elements.get(k)); 
				String n = e.getSingleValue(ObjectKeys.XTAG_PRINCIPAL_NAME);
				if (Validator.isNull(n)) {
					logger.error("Name attribute missing: " + e.toString());
					continue;
				}
				newEntries.put(n, e);
			}
			j+= 100;
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
				//returns list of user objects
				allEntries.addAll(processor.syncNewEntries(binder, def, clazz, new ArrayList(newEntries.values())));
				processor.syncEntries(oldEntries);
				//processor commits entries - so update indexnow
				IndexSynchronizationManager.applyChanges();
			}
  	   }
   	   return allEntries;
  
    }
    //RW transaction
    public Workspace addUserWorkspace(User entry) throws AccessControlException {
        if (entry.getWorkspaceId() != null) {
        	try {
        		return (Workspace)getCoreDao().loadBinder(entry.getWorkspaceId(), entry.getZoneId()); 
        	} catch (Exception ex) {};
        }
   		List templates = getCoreDao().loadConfigurations(entry.getZoneId(), Definition.USER_WORKSPACE_VIEW);
   		try {
   			if (!templates.isEmpty()) {
   				//pick the first
   				TemplateBinder template = (TemplateBinder)templates.get(0);
   				RequestContext oldCtx = RequestContextHolder.getRequestContext();
   				//want the user to be the creator
   				RequestContextUtil.setThreadContext(entry);
  				try {
   					Long wsId = getAdminModule().addBinderFromTemplate(template.getId(), entry.getParentBinder().getId(), entry.getTitle(), entry.getName());
   					Binder ws = getCoreDao().loadBinder(wsId, entry.getZoneId());
   					entry.setWorkspaceId(wsId);
   					return (Workspace)ws;
  				} finally {
  					//leave new context for indexing
  					RequestContextHolder.setRequestContext(oldCtx);				
  				}
   			}
   		} catch (WriteFilesException wf) {
   			logger.error("Cannot create user workspace: ", wf);
   			FilterControls fc = new FilterControls();
   			fc.add(ObjectKeys.FIELD_ENTITY_PARENTBINDER, entry.getParentBinder());
   			fc.add(ObjectKeys.FIELD_BINDER_NAME, entry.getName());
   			List results = getCoreDao().loadObjects(Workspace.class, fc);
   			if (!results.isEmpty()) return (Workspace)results.get(0);
   		}
   		
        return null;
   }

    //NO transaction
    public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        checkAccess(binder, "addEntry");
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        return loadProcessor(binder).addEntry(binder, definition, Group.class, inputData, fileItems).getId();
    }

    //RW transaction
    public void deleteEntry(Long binderId, Long principalId) {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, principalId);
        checkAccess(entry, "deleteEntry");
       	if (entry.isReserved()) 
    		throw new NotSupportedException(NLT.get("errorcode.group.reserved", new Object[]{entry.getName()}));       	
/* Don't automatically delete user workspace - to dangerous
 *        if (entry instanceof User) {
        	//delete workspace
        	User u = (User)entry;
        	Long wsId = u.getWorkspaceId();
        	try {
        		getBinderModule().deleteBinder(wsId);
           		u.setWorkspaceId(null);       		
        	} catch (Exception ue) {}       	
        }
*/
       	processor.deleteEntry(binder, entry); 
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
		checkAccess(binder, "getEntries");
        return loadProcessor(binder).getBinderEntries(binder, userDocType, options);
        
   }
	//RO transaction 
	public Collection getUsers(Set entryIds) {
		checkAccess(getProfileBinder(), "getEntries");
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new PrincipalComparator(user.getLocale());
       	TreeSet<User> result = new TreeSet<User>(c);
       	result.addAll(getProfileDao().loadUsers(entryIds, user.getZoneId()));
 		return result;
	}
   
	//RO transaction
	public Collection getUsersFromPrincipals(Set principalIds) {
		ProfileBinder profile = getProfileBinder();
		checkAccess(profile, "getEntries");
		Set ids = getProfileDao().explodeGroups(principalIds, profile.getZoneId());
		return getUsers(ids);
	}
	public class PrincipalComparator implements Comparator {
	   	private Collator c;
		public PrincipalComparator(Locale locale) {
			c = Collator.getInstance(locale);		
		}
		public int compare(Object obj1, Object obj2) {
			Principal f1,f2;
			f1 = (Principal)obj1;
			f2 = (Principal)obj2;
					
			if (f1 == f2) return 0;
			if (f1==null) return -1;
			if (f2 == null) return 1;
			int result = c.compare(f1.getTitle(), f2.getTitle());
			if (result != 0) return result;
			//if titles and type match - compare ids
			return f1.getId().compareTo(f2.getId());
		}
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
	
    //RW transaction
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
		RequestContextUtil.setThreadContext(zoneName, userName);
		try {
			Binder top = getCoreDao().findTopWorkspace(zoneName);
			ProfileBinder profiles = getProfileDao().getProfileBinder(top.getZoneId());
			User user = new User();
			user.setParentBinder(profiles);
			user.setZoneId(top.getZoneId());
			user.setName(userName);
			user.setForeignName(userName);
			if (password != null)
				user.setPassword(password);
			RequestContextUtil.setThreadContext(user);
			// get entry def
			getDefinitionModule().setDefaultEntryDefinition(user);
			HistoryStamp stamp = new HistoryStamp(user);
			user.setCreation(stamp);
			user.setModification(stamp);
			EntryBuilder.updateEntry(user, updates);
			// save so we have an id to work with
			getCoreDao().save(user);
			addUserWorkspace(user);

			// indexing needs the user
			ProfileCoreProcessor processor = (ProfileCoreProcessor) getProcessorManager()
					.getProcessor(
							profiles,
							profiles
									.getProcessorKey(ProfileCoreProcessor.PROCESSOR_KEY));
			processor.indexEntry(user);
			// do now, with request context set
			IndexSynchronizationManager.applyChanges();
			return user;
		} finally {
			// leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);
		}
	}

    //RW transaction
	public void modifyUserFromPortal(User user, Map updates) {
		if (updates == null)
			return; // nothing to update with
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(user);
		try {
			//use processor to handle title changes
			ProfileCoreProcessor processor = (ProfileCoreProcessor)getProcessorManager().getProcessor(user.getParentBinder(), 
					user.getParentBinder().getProcessorKey(ProfileCoreProcessor.PROCESSOR_KEY));
			processor.syncEntry(user, new MapInputData(updates));
			//do now, with request context set
			IndexSynchronizationManager.applyChanges();
		} finally {
			//leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);				
		};
	}

}

