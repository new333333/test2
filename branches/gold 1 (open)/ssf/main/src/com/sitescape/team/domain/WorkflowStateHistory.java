/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

/**
 * @hibernate.subclass discriminator-value="W"
 * 
 * @author Janet McCann
 * 
 * Log workflow history for reporting
 * We only update rows in this table if a binder moves
 *
 */
public class WorkflowStateHistory extends AuditTrail {
    protected String state;
    protected String threadName;
    protected Long tokenId;
    protected String definitionId;
	protected boolean ended=false;

	public WorkflowStateHistory() {
		super();
	}
	public WorkflowStateHistory(WorkflowState ws, HistoryStamp exitStamp, boolean ended) {
		super(ws.getOwner(), ws.getWorkflowChange(), exitStamp);
		setAuditType(AuditType.workflow);
		this.state = ws.getState();
		this.threadName = ws.getThreadName();
		this.tokenId = ws.getTokenId();
		this.definitionId = ws.getDefinition().getId();	
		setEnded(ended);
	}

  
  	 /**
	 * @hibernate.property  
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
 	 * has this workflow thread ended?
 	 * @hibernate.property
 	 */
 	public boolean isEnded() {
 		return this.ended;
 	}
 	public void setEnded(boolean ended) {
 		this.ended = ended;
 	}
    /**
     * @hibernate.property
     * @return
     */
    public String getDefinitionId() {
    	return definitionId;
    }
    public void setDefinitionId(String definitionId) {
    	this.definitionId = definitionId;
    } 	


}
