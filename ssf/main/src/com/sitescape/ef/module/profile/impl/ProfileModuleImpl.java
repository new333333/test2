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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.Set;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.EntityIdentifier;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.NoGroupByTheIdException;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Rating;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Visits;
import com.sitescape.ef.module.binder.AccessUtils;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.profile.ProfileCoreProcessor;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;


public class ProfileModuleImpl extends CommonDependencyInjection implements ProfileModule {
	private static final int DEFAULT_MAX_ENTRIES = ObjectKeys.LISTING_MAX_PAGE_SIZE;
	private String[] userDocType = {EntryIndexUtils.ENTRY_TYPE_USER};
	private String[] groupDocType = {EntryIndexUtils.ENTRY_TYPE_GROUP};
	
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
        return loadProcessor(binder).addEntry(binder, definition, User.class, inputData, fileItems);
    }

    public void checkAddEntryAllowed(ProfileBinder binder) {
        getAccessControlManager().checkOperation(binder, WorkAreaOperation.CREATE_ENTRIES);        
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
        return loadProcessor(binder).addEntry(binder, definition, Group.class, inputData, fileItems);
    }


    public void deleteEntry(Long binderId, Long principalId) {
        ProfileBinder binder = loadBinder(binderId);
        ProfileCoreProcessor processor=loadProcessor(binder);
        Principal entry = (Principal)processor.getEntry(binder, principalId);
        checkDeleteEntryAllowed(entry);
        processor.deleteEntry(binder, entry);    	
    }
 
    public void checkDeleteEntryAllowed(Principal entry) {
    	AccessUtils.deleteCheck(entry);    	
    }
    
    public void indexEntries(Long binderId) {
        ProfileBinder binder = loadBinder(binderId);
		getAccessControlManager().checkOperation(binder,  WorkAreaOperation.BINDER_ADMINISTRATION);
        loadProcessor(binder).indexEntries(binder);
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
		for (Iterator iter=entryIds.iterator(); iter.hasNext();) {
			try {
				// assuming users are cached
				result.add(getProfileDao().loadUser((Long)iter.next(), user.getZoneName()));
			} catch (NoUserByTheIdException ex) {
			} catch (AccessControlException ax) {
			}
			
		}
		return result;
	}
   
	public Collection getUsersFromPrincipals(Set principalIds) {
		Set ids = getProfileDao().explodeGroups(principalIds);
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
        visit.incrReads();   	
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
}

