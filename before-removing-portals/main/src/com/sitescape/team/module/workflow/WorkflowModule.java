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
package com.sitescape.team.module.workflow;

import java.util.Map;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;

public interface WorkflowModule {
	   // Defines variable names
    public final static String ENTRY_TYPE = "__entryType";
    public final static String ENTRY_ID = "__entryId";
    public final static String BINDER_ID = "__binderId";
    public final static String FORCE_STATE = "__forceState";
	/**
	 * Start workflow on entry.
	 * @param entry
	 * @param id
	 * @param workflowDef
	 * @param  Optional
	 */
	public void addEntryWorkflow(WorkflowSupport entry, EntityIdentifier id, Definition workflowDef, Map options);
	/**
	 * Delete all workflows associated with an entry
	 * @param entry
	 */
	public void deleteEntryWorkflow(WorkflowSupport entry);
	/**
	 * Delete a specific workflow token
	 * @param wEntry
	 * @param state
	 */
	public void deleteEntryWorkflow(WorkflowSupport wEntry, WorkflowState state);
	/**
	 * Delete all tokens associated with a definition
	 * @param wEntry
	 * @param def
	 */
	public void deleteEntryWorkflow(WorkflowSupport wEntry, Definition def);
	/**
	 * Delete a process definition by name.  
	 * Use use UUID as the name
	 * @param name
	 */
	public void deleteProcessDefinition(String name);
	
	/**
	 * Update process definition
	 * @param definitionName
	 * @param def
	 */
	public void modifyProcessDefinition(String definitionName, Definition def);
	/**
	 * Change the name of a state
	 * @param definitionName
	 * @param oldName
	 * @param newName
	 */
	public void modifyStateName(String definitionName, String oldName, String newName);
	/**
	 * Transition to a new state
	 * @param entry
	 * @param state
	 * @param toState
	 */
	public void modifyWorkflowState(WorkflowSupport entry, WorkflowState state, String toState);
	/**
	 * A reply was entered.  
	 * See if that triggers a transition and process
	 * @param entry
	 * @return
	 */
	public boolean modifyWorkflowStateOnReply(WorkflowSupport entry);
	/**
	 * A response to a workflow question was supplied.
	 * See if that triggers a transition and process
	 * @param entry
	 * @return
	 */
	public boolean modifyWorkflowStateOnResponse(WorkflowSupport entry);
	/**
	 * An update has occured on the entry.
	 * See if that triggers a transition and process
	 * @param entry
	 * @return
	 */
	public boolean modifyWorkflowStateOnUpdate(WorkflowSupport entry);
	/**
	 * An external event occured on the entry.
	 * See if that triggers a transition and process
	 * @param entry
	 * @return
	 */
	public void  modifyWorkflowStateOnChange(WorkflowSupport entry);
	public void processTimers();
	/**
	 * Set context variables and continue processing.
	 * @param entry
	 * @param state
	 * @param variables
	 */
	public void setWorkflowVariables(WorkflowSupport entry, WorkflowState state, Map<String, Object> variables);

}
