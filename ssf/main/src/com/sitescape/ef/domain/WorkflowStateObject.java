package com.sitescape.ef.domain;
/**
 * This is a persistent class that is defined externally.
 * Since it inherits from WorkflowState, I couldn't get XDoclet
 * to generate the correct mapping for the tokenId
 */
public class WorkflowStateObject extends WorkflowState {
    protected AnyOwner owner;
    protected long lockVersion;

 	public Long getTokenId() {
 		//duplicated to specify hibernate key.
 		return super.getTokenId();
 	}

    public AnyOwner getOwner() {
    	return owner;
    }
    public void setOwner(AnyOwner owner) {
    	this.owner = owner;
    } 
 	public void setOwner(Entry entry) {
  		owner = new AnyOwner(entry);
  	}
 
    public long getLockVersion() {
        return this.lockVersion;
    }
    public void setLockVersion(long lockVersion) {
        this.lockVersion = lockVersion;
    } 	 	
}
