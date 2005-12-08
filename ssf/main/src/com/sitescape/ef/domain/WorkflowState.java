package com.sitescape.ef.domain;
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

 	
}
