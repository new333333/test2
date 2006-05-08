package com.sitescape.ef.module.binder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sitescape.ef.SingletonViolationException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.function.OperationAccessControlException;
import com.sitescape.ef.security.function.WorkAreaOperation;


public class AccessUtils  {
	private static AccessUtils instance; // A singleton instance
	protected AccessControlManager accessControlManager;
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
	protected static AccessControlManager getAccessManager() {
		return getInstance().accessControlManager;
	}

    public static Set getReadAclIds(Entry entry) {
     	if (!(entry instanceof AclControlled)) return null;
        List readMemberIds = getAccessManager().getWorkAreaAccessControl(entry.getParentBinder(), WorkAreaOperation.READ_ENTRIES);
		Set<Long> ids = new HashSet<Long>();
		if (entry instanceof WorkflowSupport) {
			WorkflowSupport wEntry = (WorkflowSupport)entry;
			if (wEntry.hasAclSet()) {
				//index binders access
				if (wEntry.checkWorkArea(AccessType.READ)) {
					ids.addAll(readMemberIds);
				}
				//index workflow access - ignore widen for search engine - prune results later
				ids.addAll(wEntry.getAclSet().getMemberIds(AccessType.READ));
				if (wEntry.checkOwner(AccessType.READ)) {
					ids.add(wEntry.getCreatorId());
				}
				//	no access specified, add binder default
				if (ids.isEmpty())
					ids.addAll(readMemberIds);
        		
			} else {
				ids.addAll(readMemberIds);
				//TODO: this doesn't make sense on an index-need to get creator
				if (getAccessManager().testOperation(entry.getParentBinder(), WorkAreaOperation.CREATOR_READ))
					ids.add(wEntry.getCreatorId());
			}
		} else {
			ids.addAll(readMemberIds);
			//TODO: this doesn't make sense on an index-need to get creator
			if (getAccessManager().testOperation(entry.getParentBinder(), WorkAreaOperation.CREATOR_READ))
				ids.add(((AclControlled)entry).getCreatorId());			
		}
		return ids;
    	 
     }
     public static Set getReadAclIds(Binder binder) {
         List readMemberIds = getAccessManager().getWorkAreaAccessControl(binder, WorkAreaOperation.READ_ENTRIES);
 		Set<Long> ids = new HashSet<Long>();
 		ids.addAll(readMemberIds);
 		//TODO: this doesn't make sense on an index-need to get creator
 		if (getAccessManager().testOperation(binder, WorkAreaOperation.CREATOR_READ))
 			ids.add(binder.getCreatorId());			
 		return ids;
     	 
      }     	
	
	public static void readCheck(Entry entry) throws AccessControlException {
    	if (entry instanceof WorkflowSupport)
    		readCheck(entry.getParentBinder(), (WorkflowSupport)entry);
    	else 
    		readCheck(entry.getParentBinder(), (Entry)entry);
    		
   }
	public static void readCheck(Binder binder, Entry entry) {
       	try {
       		getAccessManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);
       	} catch (OperationAccessControlException ex) {
       		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreation().getPrincipal())) 
    				getAccessManager().checkOperation(binder, WorkAreaOperation.CREATOR_READ);
       		else throw ex;
       	}
    }
    public static void readCheck(Binder binder, WorkflowSupport entry) {
		if (!entry.hasAclSet()) {
	       	readCheck(binder, (Entry)entry);
	    } else {         	
	       	//entry has a workflow
	       	//see if owner can read
	       	if (entry.checkOwner(AccessType.READ)) {
	    	   if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) {
	    		   if (binder.isWidenRead()) return;
	    		   if (getAccessManager().testOperation(binder, WorkAreaOperation.CREATOR_READ)) return;
	    	   }
	       }
		    //see if folder default is enabled.
	       if (entry.checkWorkArea(AccessType.READ)) {
	    	   try {
	    		   getAccessManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES); 
	           		return;
	    	   } catch (OperationAccessControlException ex) {
	    		   //at this point we can stop if workflow cannot widen access
	    		   if (!binder.isWidenRead()) throw ex;
	    	   }
	       }
	       //if fails this test exception is thrown
	       getAccessManager().checkAcl(binder, entry, AccessType.READ, false, false);
	       if (binder.isWidenRead()) return;
	       //make sure acl list is sub-set of binder access
	       		getAccessManager().checkOperation(binder, WorkAreaOperation.READ_ENTRIES);     	   
	       }
	}
    public static void modifyCheck(Entry entry) throws AccessControlException {
    	if (entry instanceof WorkflowSupport)
    		modifyCheck(entry.getParentBinder(), (WorkflowSupport)entry);
    	else 
    		modifyCheck(entry.getParentBinder(), (Entry)entry);
    		
   }
    public static void modifyCheck(Binder binder, Entry entry) {
       	try {
       		getAccessManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreation().getPrincipal())) 
      			getAccessManager().checkOperation(binder, WorkAreaOperation.CREATOR_MODIFY);
      		else throw ex;
      	}
    }
     public static void modifyCheck(Binder binder, WorkflowSupport entry) {
        if (!entry.hasAclSet()) {
        	modifyCheck(binder, (Entry)entry);
        } else {         	
        	//entry has a workflow
        	//see if owner can modify
        	if (entry.checkOwner(AccessType.WRITE)) {
    		   if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) {
    			   if (binder.isWidenModify()) return;
    			   if (getAccessManager().testOperation(binder, WorkAreaOperation.CREATOR_MODIFY)) return;
    		   }
    	   }
		    //see if folder default is enabled.
    	   if (entry.checkWorkArea(AccessType.WRITE)) {
    		   try {
    			   getAccessManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES); 
    	       		return;
    		   } catch (OperationAccessControlException ex) {
    			   //at this point we can stop if workflow cannot widen access
    			   if (!binder.isWidenModify()) throw ex;
    		   }
    	   }
    	   //if fail this test exception is thrown
    	   getAccessManager().checkAcl(binder, (AclControlled)entry, AccessType.WRITE, false, false);
    	   if (binder.isWidenModify()) return;
    	   //make sure acl list is sub-set of binder access
    	   		getAccessManager().checkOperation(binder, WorkAreaOperation.MODIFY_ENTRIES);     	   
        }    	
    }
    public static void deleteCheck(Entry entry) throws AccessControlException {
     	if (entry instanceof WorkflowSupport)
     		deleteCheck(entry.getParentBinder(), (WorkflowSupport)entry);
     	else 
     		deleteCheck(entry.getParentBinder(), (Entry)entry);
     		
    }
    public static void deleteCheck(Binder binder, Entry entry) {
      	try {
       		getAccessManager().checkOperation(binder, WorkAreaOperation.DELETE_ENTRIES);
       	} catch (OperationAccessControlException ex) {
      		if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreation().getPrincipal())) 
   				getAccessManager().checkOperation(binder, WorkAreaOperation.CREATOR_DELETE);
      		else throw ex;
      	}   
    }
    public static void deleteCheck(Binder binder, WorkflowSupport entry) {
        if (!entry.hasAclSet()) {
        	deleteCheck(binder, (Entry)entry);
        } else {         	
        	//entry has a workflow
        	//see if owner can delete
        	if (entry.checkOwner(AccessType.DELETE)) {
    		   if (RequestContextHolder.getRequestContext().getUser().getId().equals(entry.getCreatorId())) {
    			   if (binder.isWidenDelete()) return;
    			   if (getAccessManager().testOperation(binder, WorkAreaOperation.CREATOR_DELETE)) return;
    		   }
    	   }
		    //see if folder default is enabled.
    	   if (entry.checkWorkArea(AccessType.DELETE)) {
    		   try {
    			   getAccessManager().checkOperation(binder, WorkAreaOperation.DELETE_ENTRIES); 
    	       		return;
    		   } catch (OperationAccessControlException ex) {
    			   //at this point we can stop if workflow cannot widen access
    			   if (!binder.isWidenDelete()) throw ex;
    		   }
    	   }
    	   //if fails this test exception is thrown
    	   getAccessManager().checkAcl(binder, entry, AccessType.DELETE, false, false);
    	   if (binder.isWidenDelete()) return;
    	   //make sure acl list is sub-set of binder access
    	   getAccessManager().checkOperation(binder, WorkAreaOperation.DELETE_ENTRIES);     	   
        }    	
    }
   	    
}