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

import java.util.Date;

/**
 * 
 * @author Janet McCann
 * Log workflow history for reporting
 * We only update rows in this table if a binder moves
 *
 */
public class  WorkflowHistory  extends ZonedObject {
    protected String state;
    protected String threadName;
    protected Long tokenId;
    protected String definitionId;
	protected boolean ended=false;
    protected String id;
	protected Date startDate,endDate;
    protected Long startBy,endBy;
	protected Long entityId;
	protected String entityType;
	protected Long owningBinderId;
	protected String owningBinderKey;  //used for queries
    
 	protected WorkflowHistory() {
	}
 	public WorkflowHistory(WorkflowStateHistory old) {
 		state = old.state;
 		threadName = old.threadName;
 		tokenId = old.tokenId;
 		definitionId = old.definitionId;
 		ended = old.ended;
 		id = old.id;
 		startDate = old.startDate;
 		startBy = old.startBy;
 		endDate = old.endDate;
 		endBy = old.endBy;
 		entityId = old.entityId;
 		entityType = old.entityType;
 		owningBinderId = old.owningBinderId;
 		owningBinderKey = old.owningBinderKey;
 		zoneId = old.zoneId;
 	}
	public WorkflowHistory(WorkflowState ws, HistoryStamp exitStamp, boolean ended) {
    	setEntityId(ws.getOwner().getOwnerId());
    	setEntityType(ws.getOwner().getOwnerType());
		setOwningBinderId(ws.getOwner().getOwningBinderId());
		setOwningBinderKey(ws.getOwner().getOwningBinderKey());
		setStart(ws.getWorkflowChange());
		setEnd(exitStamp);
		setState(ws.getState());
		setThreadName(ws.getThreadName());
		setTokenId(ws.getTokenId());
		setDefinitionId(ws.getDefinition().getId());	
		setEnded(ended);
	}

	/**
	 * Audit database id. Automatically generated
	 * @hibernate.id generator-class="uuid.hex" unsaved-value="null" node="@id"
	 * @hibernate.column name="id" sql-type="char(32)"
	 */    
    public String getId() {
        return id;
    }
    protected void setId(String id) {
        this.id = id;
    }
    /**
     * Return the id of the entity
     * @hibernate.property
     * @return
     */
    public Long getEntityId() {
    	return entityId;
    }
    protected void setEntityId(Long entityId) {
    	this.entityId = entityId;
    }
    /**
     * The entity type.  {@link org.kablink.teaming.domain.EntityIdentifier.EntityType EntityType}
     * @hibernate.property length="16"
     * @return
     */
    public String getEntityType() {
    	return entityType;
    }
    protected void setEntityType(String entityType) {
    	this.entityType = entityType;
    }
    /**
     * Return the id of the binder owning the entity.
     * If the entity is a binder, the id is the binder's id.
     * @hibernate.property 
     * @return
     */
    public Long getOwningBinderId() {
    	return this.owningBinderId;
    }
    protected void setOwningBinderId(Long owningBinderId) {
    	this.owningBinderId = owningBinderId;
    }
    /**
     * Return the sort key for the owning binder.
     * If the entity is a binder, the key is the binder's sort key.
     * @hibernate.property length="255" 
     * @return
     */
    public String getOwningBinderKey() {
        return owningBinderKey;
    }
    protected void setOwningBinderKey(String owningBinderKey) {
        this.owningBinderKey = owningBinderKey;
    } 
    
	/**
	 * Start time of event
     * @hibernate.property
     */
    public Date getStartDate() {
        return this.startDate;
    }
    protected void setStartDate(Date start) {
        this.startDate = start;
    }

	/**
	 * Return id of <code>Principal</code> that started the audit
     * @hibernate.property
     */
    public Long getStartBy() {
        return this.startBy;
    }
    protected void setStartBy(Long startBy) {
        this.startBy = startBy;
    }
    protected void setStart(HistoryStamp start) {
    	this.startDate = start.getDate();
    	this.startBy = start.getPrincipal().getId();
    }
 	/**
 	 * Return the end date of the event.
     * May be <code>null</code>
     * @hibernate.property
     */
    public Date getEndDate() {
        return this.endDate;
    }
    protected void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
 	/**
 	 * Return the id of the <code>Principal</code> that ended the audit.
     * May be <code>null</code>
     * @hibernate.property
     */
    public Long getEndBy() {
        return this.endBy;
    }
    protected void setEndBy(Long endBy) {
        this.endBy = endBy;
    }
    protected void setEnd(HistoryStamp end) {
    	this.endDate = end.getDate();
    	this.endBy = end.getPrincipal().getId();
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
