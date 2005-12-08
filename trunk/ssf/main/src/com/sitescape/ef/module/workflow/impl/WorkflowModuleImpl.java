package com.sitescape.ef.module.workflow.impl;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclManager;

import org.hibernate.HibernateException;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

public class WorkflowModuleImpl implements WorkflowModule {
	protected CoreDao coreDao;
	protected FolderDao folderDao;
	protected DefinitionModule definitionModule;
	protected AccessControlManager accessControlManager;
	protected AclManager aclManager;
	protected WorkflowFactory workflowFactory;
	

	public void setWorkflowFactory(WorkflowFactory workflowFactory) {
		this.workflowFactory = workflowFactory;
	}
	public void setCoreDao(CoreDao coreDao) {
	    this.coreDao = coreDao;
	}   
    public void setFolderDao(FolderDao folderDao) {
        this.folderDao = folderDao;
    }
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule=definitionModule;
	}
    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager=accessControlManager;
    }
    public void setAclManager(AclManager aclManager) {
        this.aclManager = aclManager;
    }


	public ProcessDefinition getWorkflow(Long id) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	        return session.getGraphSession().loadProcessDefinition(id.longValue());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public ProcessDefinition addWorkflow(String xmlString){
	    try {
	       	JbpmSession session = workflowFactory.getSession();
        	ProcessDefinition pD = ProcessDefinition.parseXmlString(xmlString);
        	session.getGraphSession().saveProcessDefinition(pD);
        	return pD;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	}
	public ProcessInstance addWorkflowInstance(Long id) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
        	ProcessDefinition pD = session.getGraphSession().loadProcessDefinition(id.longValue());
        	ProcessInstance pI = new ProcessInstance(pD);
		    session.getGraphSession().saveProcessInstance(pI);
        	return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	}
	public ProcessInstance setTransition(Long processInstanceId, String transitionId) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
        	ProcessInstance pI = session.getGraphSession().loadProcessInstance(processInstanceId.longValue());
            pI.signal(transitionId);
            session.getGraphSession().saveProcessInstance(pI);
            return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }		
	}
	public ProcessInstance setNextTransition(Long processInstanceId) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
        	ProcessInstance pI = session.getGraphSession().loadProcessInstance(processInstanceId.longValue());
            pI.signal();
            session.getGraphSession().saveProcessInstance(pI);
            return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }	
	}
	private RuntimeException convertJbpmException(Exception ex) {
		// try to decode and translate HibernateExceptions
	    if (ex instanceof HibernateException) {
	        return SessionFactoryUtils.convertHibernateAccessException((HibernateException) ex);
	    }

	    if (ex.getCause() instanceof HibernateException) {
	        // todo: going to loose a message here - perhaps create a NestedDataAccessException or similar
	        return SessionFactoryUtils.convertHibernateAccessException((HibernateException) ex.getCause());
	    }

	    // todo: classify into something like UncategorizedWorkflowException
	    return new RuntimeException(ex);
	}
	
}
