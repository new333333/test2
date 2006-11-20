/*
 * Created on Nov 16, 2004
 *
 */
package com.sitescape.ef.module.profile.impl;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.ef.NotSupportedException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.NoGroupByTheIdException;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Rating;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Visits;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.binder.AccessUtils;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.search.IndexSynchronizationManager;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.NLT;
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
    
	private ProfileCoreProcessor loadProcessor(ProfileBinder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.ef.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
	    return (ProfileCoreProcessor) getProcessorManager().getProcessor(binder, ProfileCoreProcessor.PROCESSOR_KEY);
	}
	private ProfileBinder loadBinder(Long binderId) {
		return (ProfileBinder)getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneName());
	}
	private ProfileBinder loadBinder() {
	   return (ProfileBinder)getProfileDao().getProfileBinder(RequestContextHolder.getRequestContext().getZoneName());
	}

	public ProfileBinder getProfileBinder() {
	   ProfileBinder binder = loadBinder();
		// Check if the user has "read" access to the folder.
		getAccessControlManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);		
	   return binder;
    }

	public Principal getEntry(Long binderId, Long principaId) {
        ProfileBinder binder = loadBinder(binderId);
        Principal p = getProfileDao().loadPrincipal(principaId, binder.getZoneName());        
        AccessUtils.readCheck(p);
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
		getAccessControlManager().checkOperation(binder,  WorkAreaOperation.READ_ENTRIES);
        return loadProcessor(binder).getBinderEntries(binder, groupDocType, options);        
    }
	public Collection getGroups(Set entryIds) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new UserComparator(user.getLocale());
       	TreeSet<Group> result = new TreeSet<Group>(c);
		for (Iterator iter=entryIds.iterator(); iter.hasNext();) {
			try {
				// assuming users are cached
				result.add(getProfileDao().loadGroup((Long)iter.next(), user.getZoneName()));
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
        checkAddEntryAllowed(binder);
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        return loadProcessor(binder).addEntry(binder, definition, User.class, inputData, fileItems).getId();
    }

    public void checkAddEntryAllowed(ProfileBinder binder) {
        getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);        
    }

    public boolean checkUserSeeCommunity() {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	return getAccessControlManager().testOperation(this.getProfileBinder(), WorkAreaOperation.USER_SEE_COMMUNITY);        
    }

    public boolean checkUserSeeAll() {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	return getAccessControlManager().testOperation(this.getProfileBinder(), WorkAreaOperation.USER_SEE_ALL);        
    }

    public void modifyEntry(Long binderId, Long id, InputDataAccessor inputData) 
	throws AccessControlException, WriteFilesException {
    	modifyEntry(binderId, id, inputData, new HashMap(), null);
    }
   public void modifyEntry(Long binderId, Long entryId, InputDataAccessor inputData, Map fileItems, Collection deleteAttachments) 
   		throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, entryId);
        checkModifyEntryAllowed(entry);
       	List atts = new ArrayList();
    	if (deleteAttachments != null) {
    		for (Iterator iter=deleteAttachments.iterator(); iter.hasNext();) {
    			String id = (String)iter.next();
    			Attachment a = entry.getAttachment(id);
    			if (a != null) atts.add(a);
    		}
    	}
         processor.modifyEntry(binder, entry, inputData, fileItems, atts);
     }

    public void checkModifyEntryAllowed(Principal entry) {
		AccessUtils.modifyCheck(entry);   		
    }
    public void addEntries(Long binderId, Document doc) {
       ProfileBinder binder = loadBinder(binderId);
       checkAddEntryAllowed(binder);
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
       updateMembership(root, binder.getZoneName(), allGroups);
    }
    private List addEntries(List elements, Class clazz, ProfileBinder binder, Definition def) {
       ProfileCoreProcessor processor=loadProcessor(binder);
       List allEntries = new ArrayList();
   	   Map newEntries = new HashMap();
   	   for (int j=0; j<elements.size();) {
   		   newEntries.clear();
   		   for (int k=j; k < elements.size() && k-j<100; ++k) {
				ElementInputData e = new ElementInputData((Element)elements.get(k)); 
				String n = e.getSingleValue("name");
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
			params.put("zoneName", binder.getZoneName());
			List<Principal> exists = getCoreDao().loadObjects("from com.sitescape.ef.domain.Principal where zoneName=:zoneName and name in (:plist)", params);
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
    private void updateMembership(final Element root, final String zoneName, final List newGroups) {
       // check for memberships to add
       // The following part requires update database transaction.
       getTransactionTemplate().execute(new TransactionCallback() {
    	   public Object doInTransaction(TransactionStatus status) {
    		   Set memberships = new HashSet();
    		   for (int i=0; i<newGroups.size(); ++i) {
    			   memberships.clear();
    			   //get groups of groups
    			   Group group = (Group)newGroups.get(i);
    			   Element e = (Element)root.selectSingleNode("/profiles/groups/group/attribute[@name='foreignName' and .='" + group.getForeignName() + "']");		
    			   if (e == null) continue;
    			   List<Element> members = e.selectNodes("../attribute-set[@name='members']/attribute");
    			   Set<String>names = new HashSet();
    			   for (int j=0; j<members.size(); ++j) {
    				   String fName = members.get(j).getTextTrim();
    				   if (Validator.isNull(fName)) continue;
    				   names.add(fName);
    			   }
    			   //see if they exist
    			   Map params = new HashMap();
    			   params.put("plist", names);
    			   params.put("zoneName", zoneName);
    			   List exists = getCoreDao().loadObjects("from com.sitescape.ef.domain.Principal where zoneName=:zoneName and foreignName in (:plist)", params);
    			   for (int x=0;x<exists.size(); ++x) {
    				   Principal p = (Principal)exists.get(x);
    				   memberships.add(new Membership(group.getId(), p.getId()));
    			   }
    			   getCoreDao().save(memberships);   	   
    		   }
    		   return null;
    	}});
       
    }
    public Long addWorkspace(Long binderId, Long entryId, String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, entryId);
        if (entry.getWorkspaceId() != null) {
        	//better not exist
        	Long wsId = entry.getWorkspaceId();
        	try {
        		Workspace ws = (Workspace)getCoreDao().loadBinder(wsId, RequestContextHolder.getRequestContext().getZoneName());
        		//if worked, don't create another one
        		return ws.getId(); 
        	} catch (Exception ex) {};
        }
        checkModifyEntryAllowed(entry);
        Definition definition = null;
        if (Validator.isNotNull(definitionId))
        	definition = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        else 
        	definition = getDefinitionModule().createDefaultDefinition(Definition.USER_WORKSPACE_VIEW);
        //make sure still in a transaction when return.  ApplicationContext takes care of this.
        Workspace ws = (Workspace)processor.addBinder(binder, definition, Workspace.class, inputData, fileItems);
        entry.setWorkspaceId(ws.getId());
        ws.setOwner(new HistoryStamp(entry));
 //       ws.setInheritAclFromParent(false);
 //       ws.setFunctionMembershipInherited(false);
        return ws.getId();
    }

    public void modifyWorkflowState(Long binderId, Long entryId, Long tokenId, String toState) throws AccessControlException {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, entryId);
        checkModifyEntryAllowed(entry);
        processor.modifyWorkflowState(binder, entry, tokenId, toState);
    }
    public Long addGroup(Long binderId, String definitionId, InputDataAccessor inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException {
        ProfileBinder binder = loadBinder(binderId);
        checkAddEntryAllowed(binder);
        Definition definition = getCoreDao().loadDefinition(definitionId, binder.getZoneName());
        return loadProcessor(binder).addEntry(binder, definition, Group.class, inputData, fileItems).getId();
    }


    public void deleteEntry(Long binderId, Long principalId) {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, principalId);
        checkDeleteEntryAllowed(entry);
       	if (entry.isReserved()) 
    		throw new NotSupportedException(NLT.get("errorcode.group.reserved", new Object[]{entry.getName()}));
        processor.deleteEntry(binder, entry);    	
    }
 
    public void checkDeleteEntryAllowed(Principal entry) {
    	AccessUtils.deleteCheck(entry);    	
    }
    
    public Map getUsers(Long binderId) {
    	Map options = new HashMap();
    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(DEFAULT_MAX_ENTRIES));
    	return getUsers(binderId, options);
    }
    public Map getUsers(Long binderId, Map options) {
        ProfileBinder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder,  WorkAreaOperation.READ_ENTRIES);
        return loadProcessor(binder).getBinderEntries(binder, userDocType, options);
        
   }
 
	public Collection getUsers(Set entryIds) {
        User user = RequestContextHolder.getRequestContext().getUser();
        Comparator c = new UserComparator(user.getLocale());
       	TreeSet<User> result = new TreeSet<User>(c);
       	result.addAll(getProfileDao().loadUsers(entryIds, user.getZoneName()));
 		return result;
	}
   
	public Collection getUsersFromPrincipals(Set principalIds) {
		Set ids = getProfileDao().explodeGroups(principalIds, RequestContextHolder.getRequestContext().getZoneName());
		return getUsers(ids);
	}
	public Visits getVisit(EntityIdentifier entityId) {
	    User user = RequestContextHolder.getRequestContext().getUser();
	    Visits visit = getProfileDao().loadVisit(user.getId(), entityId);
	    return visit;   	
	 }
	
    public void setVisit(EntityIdentifier entityId) {
        User user = RequestContextHolder.getRequestContext().getUser();
       	Visits visit = getProfileDao().loadVisit(user.getId(), entityId);
       	if (visit == null) {
       		visit = new Visits(user.getId(), entityId);
       		getCoreDao().save(visit);
       	}
        visit.incrReadCount();   	
    }
	public Rating getRating(EntityIdentifier entityId) {
	    User user = RequestContextHolder.getRequestContext().getUser();
       	Rating rating = getProfileDao().loadRating(user.getId(), entityId);
	    return rating;   	
	 }
    public void setRating(EntityIdentifier entityId, long value) {
        User user = RequestContextHolder.getRequestContext().getUser();
       	Rating rating = getProfileDao().loadRating(user.getId(), entityId);
       	if (rating == null) {
       		rating = new Rating(user.getId(), entityId);
       		getCoreDao().save(rating);
       	}
       	rating.setRating(value); 	
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
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "']");
			if (result == null)
				return null;
			else return result.getTextTrim();
		}

		public String[] getValues(String key) {
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./attribute-set[@name='" + key + "']");
			if (result == null)
				return null;
			if (result.getName().equals("attribute")) {
				return new String[] {result.getTextTrim()};
			}
			List<Element> vals = result.selectNodes("./attribute");
			String [] resultVals = new String[vals.size()];
			for (int i=0; i<resultVals.length; ++i) {
				resultVals[i] = vals.get(i).getTextTrim();
			}
			return resultVals;
		}

		public boolean exists(String key) {
			Element result = (Element)source.selectSingleNode("./attribute[@name='" + key + "'] | ./attribute-set[@name='" + key + "']");
			if (result == null) return false;
			return true;
		}

		public Object getSingleObject(String key) {
			return null;
		}		
	}
}

