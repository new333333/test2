package com.sitescape.ef.module.workflow.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.AnyOwner;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.module.workflow.WorkflowModule;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.jbpm.context.exe.ContextInstance;
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
		
		//Start by remembering all of the nodes
		Map nodesMap = pD.getNodesMap();
		if (nodesMap == null) nodesMap = new HashMap();
		nodesMap = new HashMap(nodesMap);
		
		Map events = pD.getEvents();
		if (events == null) events = new HashMap();
		
		Map actions = pD.getActions();
		if (actions == null) actions = new HashMap();
		/**
		 * 
		if (nodes != null) {
			nodes = new ArrayList(nodes);
			Iterator itNodes = nodes.iterator();
			while (itNodes.hasNext()) {
				Node node = (Node) itNodes.next();
				if (node != null) pD.removeNode(node);
			}
		}
		
		//Delete all of the actions and events
		Map actions = pD.getActions();
		if (actions != null) {
			actions = new HashMap(actions);
			Iterator itActions = actions.entrySet().iterator();
			while (itActions.hasNext()) {
				Map.Entry me = (Map.Entry) itActions.next();
				Action action = (Action) me.getValue();
				if (action != null) pD.removeAction(action);
			}
		}
		Map events = pD.getEvents();
		if (events != null) {
			events = new HashMap(events);
			Iterator itEvents = events.entrySet().iterator();
			while (itEvents.hasNext()) {
				Map.Entry me = (Map.Entry) itEvents.next();
				Event event = (Event) me.getValue();
				if (event != null) pD.removeEvent(event);
			}
		}
		*/
		
		//Add the standard events (if they aren't there already)
		Action recordEvent = null;
		if (!actions.containsKey("recordEvent")) {
			recordEvent = new Action();
			recordEvent.setName("recordEvent");
			recordEvent.setProcessDefinition(pD);
			Delegation recordDelegation = new Delegation("com.sitescape.ef.module.workflow.RecordEvent");
			recordDelegation.setConfigType("bean");
			recordEvent.setActionDelegation(recordDelegation);
		} else {
			recordEvent = (Action) actions.get("recordEvent");
		}
		
		if (!events.containsKey("node-enter")) {
			Event enterEvent = new Event("node-enter");
			enterEvent.addAction(recordEvent);
			pD.addEvent(enterEvent);
		}
		
		if (!events.containsKey("node-exit")) {
			Event exitEvent = new Event("node-exit");
			exitEvent.addAction(recordEvent);
			pD.addEvent(exitEvent);
		}
		
		//Add our common start and end states
		if (!nodesMap.containsKey(ObjectKeys.WORKFLOW_START_STATE)) {
			StartState startState = new StartState(ObjectKeys.WORKFLOW_START_STATE);
			pD.addNode(startState);
		} else {
			nodesMap.remove(ObjectKeys.WORKFLOW_START_STATE);
		}
		if (!nodesMap.containsKey(ObjectKeys.WORKFLOW_END_STATE)) {
			EndState endState = new EndState(ObjectKeys.WORKFLOW_END_STATE);
			pD.addNode(endState);
		} else {
			nodesMap.remove(ObjectKeys.WORKFLOW_END_STATE);
		}
		
		//Add all of the states in the definition
		Iterator itStates = defRoot.selectNodes("//item[@name='state']").iterator();
		while (itStates.hasNext()) {
			Element state = (Element) itStates.next();
			Element stateNameProperty = (Element) state.selectSingleNode("./properties/property[@name='name']");
			String stateName = stateNameProperty.attributeValue("value", "");
			if (!stateName.equals("")) {
				if (!nodesMap.containsKey(stateName)) {
					State pdState = new State(stateName);
					Node node = pD.addNode(pdState);
				} else {
					nodesMap.remove(stateName);
				}
			}
		}
		
		//Remove any nodes that are remaining in nodesMap. 
		//  These must have been deleted from the definition.
		Iterator itNodes = nodesMap.entrySet().iterator();
		while (itNodes.hasNext()) {
			Map.Entry me = (Map.Entry) itNodes.next();
			pD.removeNode((Node)me.getValue());
		}
		
		//Add all of the manual transitions
		nodesMap = pD.getNodesMap();
		itNodes = nodesMap.entrySet().iterator();
		while (itNodes.hasNext()) {
			Map.Entry me = (Map.Entry) itNodes.next();
			Node fromNode = (Node) me.getValue();
			
			//Remove all of the old transitions
			List transitions = fromNode.getLeavingTransitionsList();
			if (transitions != null) {
				for (int i = 0; i < transitions.size(); i++) {
					fromNode.removeLeavingTransition((Transition)transitions.get(i));
				}
			}
			
			//Get the list of transitions from the workflow definition
			Map manualTransitions = WorkflowUtils.getManualTransitions(def, fromNode.getName());
			Iterator itTransitions = manualTransitions.entrySet().iterator();
			while (itTransitions.hasNext()) {
				Map.Entry me2 = (Map.Entry) itTransitions.next();
				String toNodeName = (String) me2.getKey();
				Node toNode = (Node) nodesMap.get(toNodeName);
				if (toNode != null) {
					Transition t = new Transition();
					t.setProcessDefinition(pD);
					t.setName(fromNode.getName() + "." + toNodeName);
					t.setFrom(fromNode);
					t.setTo(toNode);
					fromNode.addLeavingTransition(t);
				}
			}
		}
		
    	Writer writer = new StringWriter();
	    JpdlXmlWriter jpdl = new JpdlXmlWriter(writer);
	    jpdl.write(pD);
	    System.out.println("");
	    System.out.println("Workflow process definition created: " + pD.getName());
	    System.out.println(writer.toString());
	}
	
	public void startWorkflow(Entry entry, Definition workflowDef) {
		String entryType = "";
		if (entry instanceof FolderEntry) entryType = AnyOwner.FOLDERENTRY;
		if (entry instanceof Principal) entryType = AnyOwner.PRINCIPAL;
		
		//Find the initial state of the workflow
		Document workflowDoc = workflowDef.getDefinition();
		if (workflowDoc != null) {
			Element workflowRoot = workflowDoc.getRootElement();
			Element initialStateProperty = (Element) workflowRoot.selectSingleNode("./properties/property[@name='initialState']");
			String initialState = "";
			if (initialStateProperty != null) {
				initialState = initialStateProperty.attributeValue("value", "");
				//Validate that this is an existing state
				if (!initialState.equals("")) {
					Element state = (Element) workflowRoot.selectSingleNode("./item[@name='workflowProcess']/item[@name='state']/properties/property[@name='name' and @value='"+initialState+"']");
					if (state == null) initialState = "";
				}
			}
			//See if the workflow definition actually defined an initial state
			if (initialState.equals("")) {
				//There is no defined initial state, so use the first state in the list
				initialStateProperty = (Element) workflowRoot.selectSingleNode("./item[@name='workflowProcess']/item[@name='state']/properties/property[@name='name']");
				initialState = initialStateProperty.attributeValue("value", "");
			}
			if (!initialState.equals("")) {
				//Now start the workflow at the desired initial state
				JbpmSession session;
				ProcessDefinition pD;
				try {
			       	session = workflowFactory.getSession();
			        pD = session.getGraphSession().findLatestProcessDefinition(workflowDef.getId());

			        Node node = pD.getNode(initialState);
				    if (node != null) {
			        	ProcessInstance pI = new ProcessInstance(pD);
						Token token = pI.getRootToken();
						ContextInstance cI = (ContextInstance) pI.getInstance(ContextInstance.class);
						cI.setVariable(WorkflowUtils.ENTRY_ID, entry.getId(), token);
						cI.setVariable(WorkflowUtils.ENTRY_TYPE, entryType, token);
						//doesn't exist, add a new one
						WorkflowState ws = new WorkflowState();
						ws.setTokenId(new Long(token.getId()));
						ws.setState(initialState);
						ws.setOwner(entry);
						ws.setDefinition(workflowDef);
						getCoreDao().save(ws);
						entry.addWorkflowState(ws);
						//Start the workflow process at the initial state
						token.setNode(node);
			            ExecutionContext executionContext = new ExecutionContext(token);
			            node.enter(executionContext);
			            session.getGraphSession().saveProcessInstance(pI);
				    }
			    } catch (Exception ex) {
			        throw convertJbpmException(ex);
			    }
			}
		}
	}
	
	public void modifyWorkflowState(Long tokenId, String fromState, String toState) {
	    try {
	       	JbpmSession session = workflowFactory.getSession();
        	Token t = session.getGraphSession().loadToken(tokenId.longValue());
            t.signal(fromState + "." + toState);
            session.getGraphSession().saveProcessInstance(t.getProcessInstance());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }		
	}
	
	public void deleteEntryWorkflow(Binder parent, Entry entry) {
		//Delete all JBPM tokens and process instances associated with this entry
	    try {
			List processInstances = new ArrayList();
			List tokenIds = new ArrayList();
	       	JbpmSession session = workflowFactory.getSession();
			List workflowStates = entry.getWorkflowStates();
			for (int i = 0; i < workflowStates.size(); i++) {
				WorkflowState ws = (WorkflowState) workflowStates.get(i);
				Token t = session.getGraphSession().loadToken(ws.getTokenId().longValue());
				if (!tokenIds.contains(t)) {
					//Remember all of the tokenIds that we have to end
					tokenIds.add(t);
				}
				if (!processInstances.contains(t.getProcessInstance())) {
					//Remember all of the process instances that we have to delete
					processInstances.add(t.getProcessInstance());
				}
			}
		//Now end the tokenIds used by this entry
			for (int i = 0; i < tokenIds.size(); i++) {
				Token t = (Token) tokenIds.get(i);
				t.end();
			}
			//Now delete the process instances used by this entry
			for (int i = 0; i < processInstances.size(); i++) {
				ProcessInstance pI = (ProcessInstance) processInstances.get(i);
				pI.end();
				ContextInstance cI = pI.getContextInstance();
				session.getGraphSession().deleteProcessInstance(pI);
			}
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }		
	}
	
}
