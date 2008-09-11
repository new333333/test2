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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ActionHandler;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.util.SpringContextUtil;

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

	protected WorkflowModule getWorkflowModule() {
		return (WorkflowModule)SpringContextUtil.getBean("workflowModule");
	};
	protected MailModule getMailModule() {
		return (MailModule)SpringContextUtil.getBean("mailModule");
	};

	protected WorkflowSupport loadEntry(String type, Long id) {
		if ((id == null) || (type == null)) return null;
		if (type.equals(EntityType.folderEntry.name())) {
			FolderEntry entry = (FolderEntry)getCoreDao().load(FolderEntry.class, id);
			if (entry.getZoneId().equals(RequestContextHolder.getRequestContext().getZoneId())) return entry;
		}
		return null;
	}
	protected Definition loadDefinition(String id) {
		return getCoreDao().loadDefinition(id, RequestContextHolder.getRequestContext().getZoneId());	
	}
	protected WorkflowSupport loadEntry(ContextInstance ctx) {
		return loadEntry((String)ctx.getVariable(WorkflowModule.ENTRY_TYPE),
				(Long)ctx.getVariable(WorkflowModule.ENTRY_ID));
	}

}
