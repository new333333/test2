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
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
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
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.profile.ProfileCoreProcessor;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
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

    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
    /**
     * Setup by spring
     * @param transactionTemplate
     */
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
    
    protected AdminModule adminModule;
    protected AdminModule getAdminModule() {
    	return adminModule;
    }
    public void setAdminModule(AdminModule adminModule) {
    	this.adminModule = adminModule;
    }
    
	/*
	 * Check access to folder.  If operation not listed, assume read_entries needed
	 * @see com.sitescape.team.module.binder.BinderModule#checkAccess(com.sitescape.team.domain.Binder, java.lang.String)
	 */
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

	public ProfileBinder getProfileBinder() {
	   ProfileBinder binder = loadBinder();
		// Check if the user has "read" access to the folder.
	   checkAccess(binder, "getProfileBinder");		
	   return binder;
    }

	public Principal getEntry(Long binderId, Long principaId) {
        ProfileBinder binder = loadBinder(binderId);
        Principal p = getProfileDao().loadPrincipal(principaId, binder.getZoneId());        
        checkAccess(p, "getEntry");
        return p;
    }
   public UserProperties setUserProperty(Long userId, Long binderId, String property, Object value) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
  		if (userId == null) userId = user.getId();
   		//TODO: probably need access checks, but how?
  		if (user.getId().equals(userId)) {
    		uProps = getProfileDao().loadUserProperties(userId, binderId);
			uProps.setProperty(property, value); 	
  		}
  		return uProps;
   }
   public UserProperties getUserProperties(Long userId, Long binderId) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
  		if (userId == null) userId = user.getId();
		//TODO: probably need access checks, but how?
  		if (user.getId().equals(userId)) {
			uProps = getProfileDao().loadUserProperties(userId, binderId);
		}
		return uProps;
}

   public UserProperties setUserProperty(Long userId, String property, Object value) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
 		//TODO: probably need access checks, but how?
  		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
 			uProps = getProfileDao().loadUserProperties(user.getId());
			uProps.setProperty(property, value);			
  		}
		return uProps;
    }
   public UserProperties getUserProperties(Long userId) {
   		UserProperties uProps=null;
   		User user = RequestContextHolder.getRequestContext().getUser();
 		if (userId == null) userId = user.getId();
 		  		//TODO: probably need access checks, but how?
  		if (user.getId().equals(userId)) {
    		uProps = getProfileDao().loadUserProperties(userId);
  		}
  		return uProps;
   }
   public SeenMap getUserSeenMap(Long userId) {
		User user = RequestContextHolder.getRequestContext().getUser();
		SeenMap seen = null;
		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
 			 seen = getProfileDao().loadSeenMap(user.getId());
 		}
   		return seen;
   }
   public void setSeen(Long userId, Entry entry) {
   		User user = RequestContextHolder.getRequestContext().getUser();
   		SeenMap seen;
   		if (userId == null) userId = user.getId();
  		if (user.getId().equals(userId)) {
			 seen = getProfileDao().loadSeenMap(user.getId());
			 seen.setSeen(entry);
		}
   }
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
   		}
   }  	
  
   public Map getGroups(Long binderId) {
	   Map options = new HashMap();
	   options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
	   return getGroups(binderId, options);
   }
 
    public Map getGroups(Long binderId, Map options) {
        ProfileBinder binder = loadBinder(binderId);
		checkAccess(binder, "getEntries");
        return loadProcessor(binder).getBinderEntries(binder, groupDocType, options);        
    }
	public Collection getGroups(Set entryIds) {
		//does read access check
		ProfileBinder binder = getProfileBinder();
		checkAccess(binder, "getEntries");
	    User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new UserComparator(user.getLocale());
       	TreeSet<Group> result = new TreeSet<Group>(c);
		for (Iterator iter=entryIds.iterator(); iter.hasNext();) {
			try {
				// assuming users are cached
				result.add(getProfileDao().loadGroup((Long)iter.next(), binder.getZoneId()));
			} catch (NoGroupByTheIdException ex) {
			} catch (AccessControlException ax) {
			}
			
		}
		return result;
	}
    
    //***********************************************************************************************************	
    public Long addUser(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        // This default implementation is coded after template pattern. 
        ProfileBinder binder = loadBinder(binderId);
        checkAccess(binder, "addEntry");
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        return loadProcessor(binder).addEntry(binder, definition, User.class, inputData, fileItems).getId();
    }

    public boolean checkUserSeeCommunity() {
    	return getAccessControlManager().testOperation(this.getProfileBinder(), WorkAreaOperation.USER_SEE_COMMUNITY);        
    }

    public boolean checkUserSeeAll() {
    	return getAccessControlManager().testOperation(this.getProfileBinder(), WorkAreaOperation.USER_SEE_ALL);        
    }

    public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
	throws AccessControlException, WriteFilesException {
    	modifyEntry(binderId, id, inputData, new HashMap(), null, null);
    }
   public void modifyEntry(Long binderId, Long entryId, InputDataAccessor inputData, 
		   Map fileItems, Collection deleteAttachments, Map<FileAttachment,String> fileRenamesTo) 
   		throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, entryId);
        checkAccess(entry, "modifyEntry");
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

     public void addEntries(Long binderId, Document doc) {
       ProfileBinder binder = loadBinder(binderId);
       checkAccess(binder, "addEntries");
       //process the document
       Element root = doc.getRootElement();
       List defList = root.selectNodes("/profiles/users");
       //users are grouped by defintion
       for (int i=0; i<defList.size(); ++i) {
    	   //get default definition to use
    	   Element userG = (Element)defList.get(i);
    	   String defId = userG.attributeValue("definition");
    	   Definition userDef=null;
    	   if (!Validator.isNull(defId)) {
    		   try {
    			   userDef = getDefinitionModule().getDefinition(defId);
    		   } catch (NoDefinitionByTheIdException nd) {};
    		   
    	   } 
    	   if (userDef == null) userDef = binder.getDefaultEntryDef();		
    	   if (userDef == null) {
    		   User temp = new User();
    		   getDefinitionModule().setDefaultEntryDefinition(temp);
    		   userDef = temp.getEntryDef();
    	   }
    	   //add users in groups of 100
    	   List<Element> userList = userG.selectNodes("./user");
    	   addEntries(userList, User.class, binder, userDef);
   	   
    	}
       defList = root.selectNodes("/profiles/groups");
       List allGroups = new ArrayList();
       //users are grouped by definition
       for (int i=0; i<defList.size(); ++i) {
    	   //get default definition to use
    	   Element groupG = (Element)defList.get(i);
    	   String defId = groupG.attributeValue("definition");
    	   Definition groupDef=null;
    	   if (!Validator.isNull(defId)) {
    		   try {
    			   groupDef = getDefinitionModule().getDefinition(defId);
    		   } catch (NoDefinitionByTheIdException nd) {};
    		   
    	   } 
    	   if (groupDef == null) {
    		   Group temp = new Group();
    		   getDefinitionModule().setDefaultEntryDefinition(temp);
    		   groupDef = temp.getEntryDef();
    	   }
    	   //add users in groups of 100
    	   List<Element> groupList = groupG.selectNodes("./group");
    	   allGroups.addAll(addEntries(groupList, Group.class, binder, groupDef));
    	   
    	}
    }
    private List addEntries(List elements, Class clazz, ProfileBinder binder, Definition def) {
       ProfileCoreProcessor processor=loadProcessor(binder);
       List allEntries = new ArrayList();
   	   Map newEntries = new HashMap();
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
				newEntries.remove(p.getName());
				logger.error("Principal exists: " + p.getName());
			}
			if (!newEntries.isEmpty()) {
				IndexSynchronizationManager.begin();
				//returns list of user objects
				allEntries.addAll(processor.syncNewEntries(binder, def, clazz, new ArrayList(newEntries.values())));
				IndexSynchronizationManager.applyChanges();
			}
  	   }
   	   return allEntries;
  
    }

    public Workspace addUserWorkspace(User entry) throws AccessControlException {
        if (entry.getWorkspaceId() != null) {
        	try {
        		return (Workspace)getCoreDao().loadBinder(entry.getWorkspaceId(), entry.getZoneId()); 
        	} catch (Exception ex) {};
        }
   		List templates = getCoreDao().loadConfigurations(entry.getZoneId(), Definition.USER_WORKSPACE_VIEW);
   		try {
   			if (!templates.isEmpty()) {
   				TemplateBinder template = (TemplateBinder)templates.get(0);
   				Long wsId = getAdminModule().addBinderFromTemplate(template.getId(), entry.getParentBinder().getId(), entry.getName());
   				Binder ws = getCoreDao().loadBinder(wsId, entry.getZoneId());
   				entry.setWorkspaceId(wsId);
   				ws.setOwner(entry);
   				return (Workspace)ws;
   			}
   		} catch (WriteFilesException wf) {
   			logger.error("Cannot create user workspace: ", wf);
   			FilterControls fc = new FilterControls();
   			fc.add(ObjectKeys.FIELD_ENTITY_PARENTBINDER, entry.getParentBinder());
   			fc.add(ObjectKeys.FIELD_PRINCIPAL_NAME, entry.getName());
   			List results = getCoreDao().loadObjects(Workspace.class, fc);
   			if (!results.isEmpty()) return (Workspace)results.get(0);
   		}
   		
        return null;
   }

    public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        checkAccess(binder, "addEntry");
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneId());
        return loadProcessor(binder).addEntry(binder, definition, Group.class, inputData, fileItems).getId();
    }


    public void deleteEntry(Long binderId, Long principalId) {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, principalId);
        checkAccess(entry, "deleteEntry");
       	if (entry.isReserved()) 
    		throw new NotSupportedException(NLT.get("errorcode.group.reserved", new Object[]{entry.getName()}));
        processor.deleteEntry(binder, entry);    	
    }
    
    public Map getUsers(Long binderId) {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getUsers(binderId, options);
    }
    public Map getUsers(Long binderId, Map options) {
        ProfileBinder binder = loadBinder(binderId);
		checkAccess(binder, "getEntries");
        return loadProcessor(binder).getBinderEntries(binder, userDocType, options);
        
   }
 
	public Collection getUsers(Set entryIds) {
		checkAccess(getProfileBinder(), "getEntries");
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new UserComparator(user.getLocale());
       	TreeSet<User> result = new TreeSet<User>(c);
       	result.addAll(getProfileDao().loadUsers(entryIds, user.getZoneId()));
 		return result;
	}
   
	public Collection getUsersFromPrincipals(Set principalIds) {
		ProfileBinder profile = getProfileBinder();
		checkAccess(profile, "getEntries");
		Set ids = getProfileDao().explodeGroups(principalIds, profile.getZoneId());
		return getUsers(ids);
	}
	public class UserComparator implements Comparator {
	   	private Collator c;
		public UserComparator(Locale locale) {
			c = Collator.getInstance(locale);		
		}
		public int compare(Object obj1, Object obj2) {
			User f1,f2;
			f1 = (User)obj1;
			f2 = (User)obj2;
					
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
			return null;
		}		
	}
	
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
			processor.reindexEntry(user);
			// do now, with request context set
			IndexSynchronizationManager.applyChanges();
			return user;
		} finally {
			// leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);
		}
	}

	public void modifyUserFromPortal(User user, Map updates) {
		if (updates == null)
			return; // nothing to update with
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		RequestContextUtil.setThreadContext(user);
		try {
			if (EntryBuilder.updateEntry(user, updates) == true) {
				ProfileCoreProcessor processor = (ProfileCoreProcessor)getProcessorManager().getProcessor(user.getParentBinder(), 
						user.getParentBinder().getProcessorKey(ProfileCoreProcessor.PROCESSOR_KEY));
				processor.reindexEntry(user);
				//do now, with request context set
				IndexSynchronizationManager.applyChanges();
			}
		} finally {
			//leave new context for indexing
			RequestContextHolder.setRequestContext(oldCtx);				
		};
	}

}

