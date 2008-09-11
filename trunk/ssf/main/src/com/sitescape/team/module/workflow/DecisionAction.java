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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.dom4j.Element;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.jobs.WorkflowProcess;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.workflow.jbpm.CalloutHelper;
import com.sitescape.team.module.workflow.support.WorkflowAction;
import com.sitescape.team.module.workflow.support.WorkflowScheduledAction;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Validator;

public class DecisionAction extends AbstractActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
    private static final long serialVersionUID = 1L;

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token current = executionContext.getToken();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(new Long(current.getId()));
		if (ws != null) {
			if (debugEnabled) logger.debug("Decision begin: at state " + ws.getState() + " thread " + ws.getThreadName());
			if (WorkflowProcessUtils.isThreadEndState(ws.getDefinition(), ws.getState(), ws.getThreadName())) {
				if (debugEnabled) logger.debug("Decision: end thread");
				WorkflowProcessUtils.endWorkflow(entry, ws, false);
				return;
			}
			//Check for conditions on this threads
			String toState = WorkflowProcessUtils.processConditions(executionContext, entry, ws);
			if (toState != null) {
				if (debugEnabled) logger.debug("Decision transition("+ ws.getThreadName() + "): " + ws.getState() + "." + toState);
					executionContext.leaveNode(ws.getState() + "." + toState);
					return;
			}

			
			//wait for external event to trigger a transition
			if (debugEnabled) logger.debug("Decision wait: at state " + ws.getState() + " thread " + ws.getThreadName());
		}
	}


}
