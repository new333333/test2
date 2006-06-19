package com.sitescape.ef.domain;

import java.util.HashMap;
import java.util.Map;

import com.sitescape.ef.module.workflow.WorkflowUtils;
import com.sitescape.ef.security.acl.AccessType;

/**
 * @hibernate.class table="SS_WorkflowStates" dynamic-update="true"
 * @author Janet McCann
 *
 */
public class WorkflowState {
    protected String state;
    protected String threadName;
    protected Long tokenId;
    protected Definition definition;
    protected AnyOwner owner;
    protected long lockVersion;
    protected Long timerId=null;
	
	//cached during transaction as needed 
	protected Map wfAcls=null;
   
 	public Long getId() {
 		return tokenId;
 	}
 	public void setId(Long tokenId) {
 		this.tokenId = tokenId;
 	}
 	 /**
	 * @hibernate.id generator-class="assigned"  
 	 * @return
 	 */
 	public Long getTokenId() {
 		return tokenId;
 	}
 	public void setTokenId(Long tokenId) {
 		this.tokenId = tokenId;
 	}
    /**
     * @hibernate.version type="long" column="lockVersion"
     */
    public long getLockVersion() {
        return this.lockVersion;
    }
    public void setLockVersion(long lockVersion) {
        this.lockVersion = lockVersion;
    } 	 
    /**
     * @hibernate.component class="com.sitescape.ef.domain.AnyOwner"
     * @return
     */

    public AnyOwner getOwner() {
    	return owner;
    }
    public void setOwner(AnyOwner owner) {
    	this.owner = owner;
    } 
 	public void setOwner(Entry entry) {
  		owner = new AnyOwner(entry);
  	}
    /**
     * @hibernate.property length="64"
     * @return
     */
 	public String getState() {
 		return state;
 	}
 	public void setState(String state) {
 		this.state = state;
 		//on state change, clear cached values associated with previous state
		wfAcls = null;
 	}

    /**
     * @hibernate.property length="64"
     * @return
     */
 	public String getThreadName() {
 		return threadName;
 	}
 	public void setThreadName(String threadName) {
 		this.threadName = threadName;
 	}

    /**
     * @hibernate.many-to-one
     * @return
     */
    public Definition getDefinition() {
    	return definition;
    }
    public void setDefinition(Definition definition) {
    	this.definition = definition;
    } 	
    /**
     * @hibernate.property
     */
 	public Long getTimerId() {
 		return timerId;
 	}
 	public void setTimerId(Long timerId) {
 		this.timerId = timerId;
 	}
      /**
 	 * Compare tokenIds.  A token can have only 1 state
 	 */
 	public boolean equals(Object obj) {
        if(this == obj)
            return true;

        //objects can be proxied so don't compare classes.
        if (obj == null)
            return false;
      
        WorkflowState o = (WorkflowState) obj;
        if (this.tokenId.equals(o.getId()))
            return true;
                
        return false;
	}
    public int hashCode() {
    	return tokenId.hashCode();
    }


    public WfAcl getAcl(AccessType type) {
    	WfAcl acl=null;
    	if (wfAcls == null) wfAcls = new HashMap();
    	else acl = (WfAcl)wfAcls.get(type);
    	if (acl != null) return acl;
    	acl = WorkflowUtils.getStateAcl(definition, state, type);
    	wfAcls.put(type, acl);
    	return acl;
    }
   
 
}
