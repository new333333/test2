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
package org.kablink.teaming.module.workflow;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.jobs.WorkflowProcess;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.workflow.jbpm.CalloutHelper;
import org.kablink.teaming.module.workflow.support.WorkflowAction;
import org.kablink.teaming.module.workflow.support.WorkflowScheduledAction;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.util.Validator;
import org.dom4j.Element;


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
