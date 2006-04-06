package com.sitescape.ef.module.workflow;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.def.Node;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.mail.MailManager;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;

public abstract class AbstractActionHandler implements ActionHandler {
	protected Log logger = LogFactory.getLog(getClass());
	protected boolean infoEnabled=logger.isInfoEnabled();

	protected CoreDao getCoreDao() {
		return (CoreDao)SpringContextUtil.getBean("coreDao");
	};

	protected ProfileDao getProfileDao() {
		return (ProfileDao)SpringContextUtil.getBean("profileDao");
	};

	protected MailManager getMailManager() {
		return (MailManager)SpringContextUtil.getBean("mailManager");
	};

	protected WorkflowSupport loadEntry(String type, Long id) {
		if ((id == null) || (type == null)) return null;
		WorkflowSupport entry = null;
		if (type.equals(EntityType.user.name()) || type.equals(EntityType.group.name())) {
			entry = (WorkflowSupport)getCoreDao().load(Principal.class, id);
		} else if (type.equals(EntityType.folderEntry.name())) {
			entry = (WorkflowSupport)getCoreDao().load(FolderEntry.class, id);
		}
		return entry;
	}
	protected WorkflowSupport loadEntry(ContextInstance ctx) {
		return loadEntry((String)ctx.getVariable(WorkflowUtils.ENTRY_TYPE),
				(Long)ctx.getVariable(WorkflowUtils.ENTRY_ID));
	}

	protected void checkForWaits(Token current, WorkflowSupport entry) {
		JbpmSession session = WorkflowFactory.getSession();
		HashMap oldStates = new HashMap();
		//save states to see if any change
		for (Iterator iter=entry.getWorkflowStates().iterator(); iter.hasNext();) {
			WorkflowState state = (WorkflowState)iter.next();
			oldStates.put(state.getTokenId(), state.getState());
		}
		
		//keep looping until nothing changes
		while (true) {
			for (Iterator iter=entry.getWorkflowStates().iterator(); iter.hasNext();) {
				WorkflowState state = (WorkflowState)iter.next();
				//See if state is waiting for thread state changes
				if (!state.getWfWaits().isEmpty()) {
					Token t = session.getGraphSession().loadToken(state.getTokenId().longValue());
					if (!t.equals(current)) {
						Node n = t.getNode();
						n.execute(new ExecutionContext(t));
					}
				}
				
			}
			
			//see if anything changed
			HashMap newStates = new HashMap();
			//save states to see if any change
			for (Iterator iter=entry.getWorkflowStates().iterator(); iter.hasNext();) {
				WorkflowState state = (WorkflowState)iter.next();
				newStates.put(state.getTokenId(), state.getState());
			}
			
			if (oldStates.equals(newStates)) break;
			oldStates = newStates;
			
		}
		
	}
}
