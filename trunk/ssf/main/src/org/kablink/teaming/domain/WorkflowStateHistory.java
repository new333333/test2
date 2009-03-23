/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.domain;

/**
 * @hibernate.subclass discriminator-value="W"
 * 
 * @author Janet McCann
  * @deprecated As of ICEcore version 1.1, see WorkflowHistory
 * Log workflow history for reporting
 * We only update rows in this table if a binder moves
 *
 */
public class  WorkflowStateHistory extends AuditTrail {
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
