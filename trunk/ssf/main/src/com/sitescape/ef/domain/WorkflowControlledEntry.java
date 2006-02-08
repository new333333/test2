package com.sitescape.ef.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.security.acl.AccessType;
import com.sitescape.ef.security.acl.AclControlled;
import com.sitescape.ef.security.acl.AclSet;

/**
 *
 * @author Jong Kim
 */
public abstract class WorkflowControlledEntry extends Entry implements AclControlled {
	private Set readMemberIds,writeMemberIds,deleteMemberIds,changeAclMemberIds;
    /**
     * 
     */
    public boolean getInheritAclFromParent() {
    	return !hasAclSet();
    }

    /**
     * Determined by current workflowState
     */
    public void setInheritAclFromParent(boolean inherit) {
		throw new InternalError("Method not supported");
    }
    
    public Long getCreatorId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }
	public boolean hasAclSet() {
		Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return false;
	    	return true;
	}
	public void setAclSet(AclSet aclSet) {
		throw new InternalError("Method not supported");
	}
	public AclSet getAclSet() {
	    return new WfAclSet();	        
	}   

	public boolean checkWorkArea(AccessType type) {
	    Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return true; 
	     //If any state uses parent, use it
	    for (Iterator iter=states.iterator(); iter.hasNext();) {
	    	WorkflowState state = (WorkflowState)iter.next();
	    	WfAcl a = state.getAcl(type);
	       	if ((a != null) && a.isUseDefault()) return true;
	       }
	       return false;
	   }
	public boolean checkOwner(AccessType type) {
	   Set states = getWorkflowStates();
	   if ((states == null) || states.isEmpty()) return true; 
	   //If any state allows creator, use it
	   for (Iterator iter=states.iterator(); iter.hasNext();) {
	    	WorkflowState state = (WorkflowState)iter.next();
	    	WfAcl a = state.getAcl(type);
	       	if ((a != null) && a.isCreator()) return true;
	   }
	   return false;
	}
	public Set getStateMembers(AccessType type) {
	   	Set result = new HashSet();
	   	Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return result; 
	    //If any state allows creator, use it
	    for (Iterator iter=states.iterator(); iter.hasNext();) {
	       	WorkflowState state = (WorkflowState)iter.next();
	       	WfAcl a = state.getAcl(type);
	       	if (a != null) {
	       		result.addAll(a.getPrincipals());
	       	}
	    }
	    return result;
	    	
	}

	public class WfAclSet implements AclSet {
			
	    public Set getReadMemberIds() {
	        if(readMemberIds == null) readMemberIds = getStateMembers(AccessType.READ);
	        return readMemberIds;
	    }
	    public Set getWriteMemberIds() {
	        if(writeMemberIds == null) writeMemberIds = getStateMembers(AccessType.WRITE);
	        return writeMemberIds;
	    }
	    public Set getDeleteMemberIds() {
	        if(deleteMemberIds == null) deleteMemberIds = getStateMembers(AccessType.DELETE);
	        return deleteMemberIds;
	    }

	    public Set getChangeAclMemberIds() {
	        if(changeAclMemberIds == null) changeAclMemberIds = getStateMembers(AccessType.CHANGE_ACL);
	        return changeAclMemberIds;
	    }
	        
	    public Set getMemberIds(AccessType accessType) {
	        if(accessType == AccessType.READ)
	            return getReadMemberIds();
	        else if(accessType == AccessType.WRITE)
	            return getWriteMemberIds();
	        else if(accessType == AccessType.DELETE)
	            return getDeleteMemberIds();
	        else if(accessType == AccessType.CHANGE_ACL)
	            return getChangeAclMemberIds();
	        else
	            throw new IllegalArgumentException("Illegal access type: " + accessType.toString());
	    }
	    
	    public void addMemberId(AccessType accessType, Long memberId) {
			throw new InternalError("Method not supported");
	    }
	    
	    public boolean removeMemberId(AccessType accessType, Long memberId) {
			throw new InternalError("Method not supported");
	    }
	    
	    public Object clone() {
			throw new InternalError("Method not supported");
	    }
	    
	    public void clear() {	        
	        readMemberIds = null;
	        writeMemberIds = null;
	        deleteMemberIds = null;
	        changeAclMemberIds = null;
	    }

	}
}
