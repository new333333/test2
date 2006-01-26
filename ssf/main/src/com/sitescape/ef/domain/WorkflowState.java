package com.sitescape.ef.domain;

import java.util.Map;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.util.NLT;
import com.sitescape.util.Validator;

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
	
	//cached during transaction as needed 
	protected List wfWaits=null;
	protected List endStates=null;
	protected List wfStarts=null;
	protected List wfEnterNotify=null;
	protected List wfExitNotify = null;
   
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
 		endStates=null;
 		wfWaits=null;
 		wfStarts = null;
		wfExitNotify = null;
		wfEnterNotify = null;
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

    /**
     * Method to get the list of allowed manual transitions from this state for this user
     */
    public Map getManualTransitions() {
    	return WorkflowUtils.getManualTransitions(this.getDefinition(), this.getState());
    }
    
    public String getStateCaption() {
    	String stateCaption = "";
    	//Find the actual caption of the state
    	Definition wfDef = this.getDefinition();
    	if (wfDef != null) {
    		Document wfDefDoc = wfDef.getDefinition();
        	Element stateProperty = (Element) wfDefDoc.getRootElement().selectSingleNode("//item[@name='state']/properties/property[@name='name' and @value='"+this.getState()+"']");
        	if (stateProperty != null) {
        		Element statePropertyCaption = (Element) stateProperty.getParent().selectSingleNode("./property[@name='caption']");
        		if (statePropertyCaption != null) stateCaption = statePropertyCaption.attributeValue("value", "");
        	}
        	if (stateCaption.equals("")) {
        		stateCaption = this.getState();
        	} else {
        		stateCaption = NLT.getDef(stateCaption);
        	}
    	}
    	return stateCaption;
    }
    
    /**
     * Waits are cached in this object, but not persisted here
     * @return
     */
    public List getWfWaits() {
    	if (wfWaits == null)
    		wfWaits = WorkflowUtils.getParallelThreadWaits(definition, state);
    	return wfWaits;
    	
    }
    public List getWfStarts() {
    	if (wfStarts == null) wfStarts = WorkflowUtils.getParallelThreadStarts(definition, state);
    	return wfStarts; 
    }
    public List getWfEnterNotifications() {
    	if (wfEnterNotify == null)
    		wfEnterNotify = WorkflowUtils.getEnterNotifications(definition, state);
    	return wfEnterNotify;
    	
    }
    public List getWfExitNotifications() {
    	if (wfExitNotify == null)
    		wfExitNotify = WorkflowUtils.getExitNotifications(definition, state);
    	return wfExitNotify;
    	
    }
   
    public List getThreadEndStates() {
    	if (endStates == null) {
    		if (Validator.isNull(threadName)) {
    			endStates = WorkflowUtils.getEndState(definition);
    		} else {
    			endStates = WorkflowUtils.getThreadEndState(definition, threadName);
    		}
    	}
    	return endStates;
    	   	
    }
    public boolean isThreadEndState() {
    	if (getThreadEndStates().contains(state)) return true;
    	return false;
    }
 	
}
