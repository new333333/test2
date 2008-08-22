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
package com.sitescape.team.module.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;

import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.report.ReportModule;

public abstract class AbstractActionHandler implements ActionHandler {
	protected Log logger = LogFactory.getLog(AbstractActionHandler.class);
	protected boolean debugEnabled=logger.isDebugEnabled();

	protected CoreDao getCoreDao() {
		return (CoreDao)SpringContextUtil.getBean("coreDao");
	};

	protected ProfileDao getProfileDao() {
		return (ProfileDao)SpringContextUtil.getBean("profileDao");
	};

	protected ProfileModule getProfileModule() {
		return (ProfileModule)SpringContextUtil.getBean("profileModule");
	};
	protected ReportModule getReportModule() {
		return (ReportModule)SpringContextUtil.getBean("reportModule");
	};

	protected MailModule getMailModule() {
		return (MailModule)SpringContextUtil.getBean("mailModule");
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
		return loadEntry((String)ctx.getVariable(WorkflowModule.ENTRY_TYPE),
				(Long)ctx.getVariable(WorkflowModule.ENTRY_ID));
	}

}
