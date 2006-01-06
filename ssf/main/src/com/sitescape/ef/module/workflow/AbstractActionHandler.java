package com.sitescape.ef.module.workflow;

import org.jbpm.graph.def.ActionHandler;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.util.SpringContextUtil;
import com.sitescape.util.Validator;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.AnyOwner;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;

public abstract class AbstractActionHandler implements ActionHandler {

	protected CoreDao getCoreDao() {
		return (CoreDao)SpringContextUtil.getBean("coreDao");
	};
	protected WorkflowFactory getWorkflowFactory() {
		return (WorkflowFactory)SpringContextUtil.getBean("workflowFactory");
	};
	protected Entry loadEntry(String type, Long id) {
		if (Validator.isNull(type)) return null;
		if (id == null) return null;
		Entry entry = null;
		if (type.equals(AnyOwner.PRINCIPAL)) {
			entry = (Entry)getCoreDao().load(Principal.class, id);
		} else if (type.equals(AnyOwner.FOLDERENTRY)) {
			entry = (Entry)getCoreDao().load(FolderEntry.class, id);
		}
		return entry;
	}

}
