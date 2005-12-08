package com.sitescape.ef.module.workflow.impl;

import java.util.Iterator;
import java.util.List;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclManager;

import org.hibernate.HibernateException;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

public class WorkflowModuleImpl extends CommonDependencyInjection implements WorkflowModule {
	protected DefinitionModule definitionModule;
	protected WorkflowFactory workflowFactory;
	

	public void setWorkflowFactory(WorkflowFactory workflowFactory) {
		this.workflowFactory = workflowFactory;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule=definitionModule;
	}
 

	public List getAllDefinitions() {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	        return session.getGraphSession().findAllProcessDefinitions();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public List getLatestDefinitions() {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	        return session.getGraphSession().findLatestProcessDefinitions();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public List getNodes(Long id) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	        ProcessDefinition pD = session.getGraphSession().loadProcessDefinition(id.longValue());
	        return pD.getNodes();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public List getProcessInstances(Long id) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	        return session.getGraphSession().findProcessInstances(id.longValue());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public void deleteProcessInstance(Long processInstanceId) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	       	ProcessInstance pI = session.getGraphSession().loadProcessInstance(processInstanceId.longValue());
	       	if (pI != null) session.getGraphSession().deleteProcessInstance(pI);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};
	
	public ProcessDefinition getWorkflow(Long id) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	        return session.getGraphSession().loadProcessDefinition(id.longValue());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public ProcessInstance getProcessInstance(Long id) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
        	ProcessInstance pI = session.getGraphSession().loadProcessInstance(id.longValue());
	        return pI;
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
        	Token token = pI.getRootToken();
        	Transition transition = token.getNode().getDefaultLeavingTransition();
        	if (transition == null) {
        		List transitions = token.getNode().getLeavingTransitions();
        		if (transitions.size() <= 0) return pI;
        		transition = (Transition) transitions.get((int) 0);
        	}
            token.signal(transition);
            session.getGraphSession().saveProcessInstance(pI);
            return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }	
	}
	public ProcessInstance setNode(Long processInstanceId, String nodeId) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
        	ProcessInstance pI = session.getGraphSession().loadProcessInstance(processInstanceId.longValue());
        	Token token = pI.getRootToken();
            ProcessDefinition pD = pI.getProcessDefinition();
            Node node = pD.findNode(nodeId);
        	token.setNode(node);
            ExecutionContext executionContext = new ExecutionContext(token);
            node.enter(executionContext);
            session.getGraphSession().saveProcessInstance(pI);
            return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }	
	}
	public void deleteProcessDefinition(Long id) {
       	JbpmSession session = workflowFactory.getSession();
        session.getGraphSession().deleteProcessDefinition(id.longValue());
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
