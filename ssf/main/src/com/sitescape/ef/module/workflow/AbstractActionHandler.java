package com.sitescape.ef.module.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.ProfileDao;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.mail.MailManager;

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

}
