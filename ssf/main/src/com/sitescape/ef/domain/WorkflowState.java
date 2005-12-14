package com.sitescape.ef.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.util.NLT;

/**
 * Use this object as component object.  This would be useful for objects that
 * implement SingletonWorkflowSupport.  Objects that maintain multiple workflows,
 * should use WorkflowStateObject.
 * @author Janet McCann
 *
 */
public class WorkflowState {
    protected String state;
    protected Long tokenId;
    protected Definition definition;
 	
    /**
 	 * @hibernate.property type="long" 
 	 * @return
 	 */
 	public Long getTokenId() {
 		return tokenId;
 	}
 	public void setTokenId(Long tokenId) {
 		this.tokenId = tokenId;
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
        if (this.tokenId.equals(o.getTokenId()))
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
 	
}
