/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.module.workflow.WorkflowUtils;
import com.sitescape.team.util.NLT;
import com.sitescape.util.Validator;

/**
 * @hibernate.class table="SS_WorkflowStates" dynamic-update="true"
 * @author Janet McCann
 *
 */
public class WorkflowState extends ZonedObject {
    protected String state;
    protected String threadName;
    protected Long tokenId;
    protected Definition definition;
    protected AnyOwner owner;
    protected long lockVersion;
    protected Long timerId=null;
    protected HistoryStamp workflowChange;


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
      * @hibernate.component class="com.sitescape.team.domain.HistoryStamp" prefix="wrk_" 
     */
    public HistoryStamp getWorkflowChange() {
        return this.workflowChange;
    }
    public void setWorkflowChange(HistoryStamp workflowChange) {
        this.workflowChange = workflowChange;
    }

    /**
     * @hibernate.component class="com.sitescape.team.domain.AnyOwner"
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


    public WfAcl getAcl(WfAcl.AccessType type) {
    	WfAcl acl=null;
    	if (wfAcls == null) wfAcls = new HashMap();
    	else acl = (WfAcl)wfAcls.get(type);
    	if (acl != null) return acl;
    	acl = WorkflowUtils.getStateAcl(definition, getOwner().getEntity(), state, type);
    	wfAcls.put(type, acl);
    	return acl;
    }
   
	public Element addChangeLog(Element parent) {
		Element element = parent.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_WORKFLOWSTATE);
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_DATABASEID, getId().toString());
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, getState());
		
		XmlUtils.addProperty(element, ObjectKeys.XTAG_WFS_DEFINITION, getDefinition().getId());
		if (getTimerId() != null)
			XmlUtils.addProperty(element, ObjectKeys.XTAG_WFS_TIMER, getTimerId());
		if (!Validator.isNull(getThreadName())) 
			element.addAttribute(ObjectKeys.XTAG_WFS_THREAD, getThreadName());
		
		String wfTitle = NLT.getDef(definition.getTitle());
		element.addAttribute(ObjectKeys.XTAG_WFS_PROCESS, wfTitle);
		
		if (getWorkflowChange() != null) getWorkflowChange().addChangeLog(element, ObjectKeys.XTAG_WF_CHANGE);
		return element;
    	
    }
}
