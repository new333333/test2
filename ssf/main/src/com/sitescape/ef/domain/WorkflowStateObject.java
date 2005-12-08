package com.sitescape.ef.domain;

/**
 * This is a persistent class that is defined externally.
 * Since it inherits from WorkflowState, I couldn't get XDoclet
 * to generate the correct mapping for the tokenId
 * 
 * Use this object as a member of a list of workflows as is done for objects 
 * that implement MultipleWorkflowSupport.  Objects that 
 * implement SingletonWorkflowSupport should use WorkflowState as a component.
 */
public class WorkflowStateObject extends WorkflowState {
    protected AnyOwner owner;
    protected long lockVersion;
    protected Definition definition;
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
}
