package com.sitescape.ef.module.workflow.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclManager;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.State;
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.xml.JpdlXmlWriter;
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

	public List getAllDefinitions(String name) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	        return session.getGraphSession().findAllProcessDefinitionVersions(name);
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
	public Token addWorkflowSubToken(Long processInstanceId, String name) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
        	ProcessInstance pI = session.getGraphSession().loadProcessInstance(processInstanceId.longValue());
        	Token token = pI.getRootToken();
        	Token subToken = new Token(token, name);
		    session.getGraphSession().saveProcessInstance(pI);
        	return subToken;
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
       	try {
       		JbpmSession session = workflowFactory.getSession();
            session.getGraphSession().deleteProcessDefinition(id.longValue());
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
	
	//Routine to build (or modify) a workflow process definition from a Definition
	public void buildProcessDefinition(String definitionName, Definition def) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
	        ProcessDefinition pD = session.getGraphSession().findLatestProcessDefinition(definitionName);
	        if (pD == null) {
	        	//The process definition doesn't exist yet, go create one
	        	pD = ProcessDefinition.createNewProcessDefinition();
	    		pD.setName(definitionName);
	        }
	        updateProcessDefinition(pD, def);
	    	session.getGraphSession().saveProcessDefinition(pD);

	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
		
	}

	public void updateProcessDefinition(ProcessDefinition pD, Definition def) {
		Document defDoc = def.getDefinition();
		Element defRoot = defDoc.getRootElement();
		
		//Start by deleting all of the nodes
		List nodes = pD.getNodes();
		if (nodes != null) {
			List nodes1 = new ArrayList(nodes);
			Iterator itNodes = nodes1.iterator();
			while (itNodes.hasNext()) {
				Node node = (Node) itNodes.next();
				if (node != null) pD.removeNode(node);
			}
		}
		//Delete all of the actions and events
		Map actions = pD.getActions();
		if (actions != null) {
			Iterator itActions = actions.entrySet().iterator();
			while (itActions.hasNext()) {
				Map.Entry me = (Map.Entry) itActions.next();
				Action action = (Action) me.getValue();
				if (action != null) pD.removeAction(action);
			}
		}
		Map events = pD.getEvents();
		if (events != null) {
			Iterator itEvents = events.entrySet().iterator();
			while (itEvents.hasNext()) {
				Map.Entry me = (Map.Entry) itEvents.next();
				Event event = (Event) me.getValue();
				if (event != null) pD.removeEvent(event);
			}
		}
		
		//Add the standard events
		Action recordEvent = new Action();
		recordEvent.setName("recordEvent");
		Delegation recordDelegation = new Delegation("com.sitescape.ef.module.workflow.RecordEvent");
		recordDelegation.setConfigType("bean");
		recordEvent.setActionDelegation(recordDelegation);
		
		Event enterEvent = new Event("node-enter");
		enterEvent.addAction(recordEvent);
		pD.addEvent(enterEvent);
		
		Event exitEvent = new Event("node-exit");
		exitEvent.addAction(recordEvent);
		pD.addEvent(exitEvent);
		
		//Add our common start and end states
		StartState startState = new StartState(ObjectKeys.WORKFLOW_START_STATE);
		pD.addNode(startState);
		EndState endState = new EndState(ObjectKeys.WORKFLOW_END_STATE);
		pD.addNode(endState);
		
		//Add all of the states in the definition
		Iterator itStates = defRoot.selectNodes("//item[@name='state']").iterator();
		while (itStates.hasNext()) {
			Element state = (Element) itStates.next();
			Element stateNameProperty = (Element) state.selectSingleNode("./properties/property[@name='name']");
			String stateName = stateNameProperty.attributeValue("value", "");
			if (!stateName.equals("")) {
				State pdState = new State(stateName);
				pD.addNode(pdState);
			}
		}
		
    	Writer writer = new StringWriter();
	    JpdlXmlWriter jpdl = new JpdlXmlWriter(writer);
	    jpdl.write(pD);
	    System.out.println("");
	    System.out.println("Workflow process definition created: " + pD.getName());
	    System.out.println(writer.toString());
	}
}
