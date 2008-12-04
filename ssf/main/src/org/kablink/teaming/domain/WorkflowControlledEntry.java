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
package org.kablink.teaming.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kablink.util.Validator;


/**
 *
 * @author Jong Kim
 */
public abstract class WorkflowControlledEntry extends Entry 
	implements WorkflowSupport {
    protected Set workflowStates; //initialized by hiberate access=field  
	protected Set iWorkflowStates;
    protected HistoryStamp workflowChange;//initialized by hiberate access=field  
    protected Set workflowResponses; //initialized by hiberate access=field  
	public WorkflowControlledEntry() {
		super();
	}
	public WorkflowControlledEntry(WorkflowControlledEntry entry) {
		super(entry);
	}
     /**
      * @hibernate.component class="org.kablink.teaming.domain.HistoryStamp" prefix="wrk_" 
     */
    public HistoryStamp getWorkflowChange() {
        return this.workflowChange;
    }
    public void setWorkflowChange(HistoryStamp workflowChange) {
        if (workflowChange != null) {
        	if (workflowChange.compareDate(this.workflowChange) > 0)
        		this.workflowChange = workflowChange;
        	if (workflowChange.compareDate(getModification()) > 0) 
        		setModification(workflowChange);
        } else this.workflowChange = null;
    }

    public Set<WorkflowResponse> getWorkflowResponses() {
    	if (workflowResponses == null) workflowResponses = new HashSet();
   	 	return workflowResponses;  
     }
    public void addWorkflowResponse(WorkflowResponse workflowResponse) {
    	getWorkflowResponses().add(workflowResponse);
    }
    public void removeWorkflowResponse(WorkflowResponse workflowResponse) {
    	getWorkflowResponses().remove(workflowResponse);
    }
    public Set<WorkflowState> getWorkflowStates() {
    	if (iWorkflowStates != null) return iWorkflowStates;
    	if (workflowStates == null) workflowStates = new HashSet();
   	 	return workflowStates;  
     }
     public WorkflowState getWorkflowState(Long id) {
     	//Make sure initialized
     	getWorkflowStates();
     	
		WorkflowState ws=null;
		for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
			ws = (WorkflowState)iter.next();
			if (ws.getId().equals(id)) return ws;
		}
		return null;
     }
     public WorkflowState getWorkflowStateByThread(Definition def, String threadName) {
      	//Make sure initialized
      	getWorkflowStates();
      	
 		WorkflowState ws=null;
 		for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
 			ws = (WorkflowState)iter.next();
 			if (!def.equals(ws.getDefinition())) continue;
 			if (Validator.isNull(ws.getThreadName())) {
 				if (Validator.isNull(threadName)) return ws; 
 			} else {
 				if (ws.getThreadName().equals(threadName)) return ws;
 			}
 		}
 		return null;
      }
     public void addWorkflowState(WorkflowState state) {
     	if (state == null) return;
    	//Make sure initialized
    	getWorkflowStates();
        workflowStates.add(state);
 	   	state.setOwner(this);
    }
    public void removeWorkflowState(WorkflowState state) {
     	if (state == null) return;
    	//Make sure initialized
    	getWorkflowStates();
        workflowStates.remove(state);
 	   	state.setOwner((AnyOwner)null);
    }
    
    public Long getOwnerId() {
    	HistoryStamp creation = getCreation();
    	if(creation != null) {
    		Principal principal = creation.getPrincipal();
    		if(principal != null)
    			return principal.getId();
    	}
    	return null;
    }
	public boolean hasAclSet() {
		Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return false;
	    return true;
	}

	public boolean isWorkAreaAccess(WfAcl.AccessType type) {
	    Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return true; 
	     //If any state uses parent, use it
	    for (Iterator iter=states.iterator(); iter.hasNext();) {
	    	WorkflowState state = (WorkflowState)iter.next();
	    	WfAcl a = state.getAcl(type);
	    	//return least restrictive
	    	if ((a != null) && a.isUseDefault()) return true;
	       }
       return false;
	}
	public Set getStateMembers(WfAcl.AccessType type) {
	   	Set result = new HashSet();
	   	Set states = getWorkflowStates();
	    if ((states == null) || states.isEmpty()) return result; 
	    for (Iterator iter=states.iterator(); iter.hasNext();) {
	       	WorkflowState state = (WorkflowState)iter.next();
	       	WfAcl a = state.getAcl(type);
	       	if (a != null) {
	       		result.addAll(a.getPrincipalIds());
	       	}
	    }
 
	    return result;
	    	
	}

    /*
     * The following methods are used for performance optimization during indexing.
     * The values of each collection are loaded and built by hand.  
     * They are not persisted.  This allows us to load greater than the 
     * hibernate "batch-size" number of collections at once.
     */
    public void setIndexWorkflowStates(Set iWorkflowStates) {
    	this.iWorkflowStates = iWorkflowStates;
    }
	
}
