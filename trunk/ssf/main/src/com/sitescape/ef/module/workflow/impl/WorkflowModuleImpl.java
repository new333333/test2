package com.sitescape.ef.module.workflow.impl;

import java.io.StringWriter;
import java.util.ArrayList;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.AnyOwner;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.shared.WorkflowUtils;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.util.SZoneConfig;
import com.sitescape.ef.domain.WfWaits;
import com.sitescape.util.Validator;
import com.sitescape.ef.jobs.WorkflowTimeout;
import com.sitescape.ef.domain.WorkflowControlledEntry;
import com.sitescape.ef.module.binder.EntryProcessor;


import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.Duration;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.SchedulerSession;
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
import org.jbpm.instantiation.Delegation;
import org.jbpm.jpdl.xml.JpdlXmlWriter;
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.scheduler.exe.Timer;

import org.springframework.orm.hibernate3.SessionFactoryUtils;

public class WorkflowModuleImpl extends CommonDependencyInjection implements WorkflowModule {
   static BusinessCalendar businessCalendar = new BusinessCalendar();


	public List getAllDefinitions() {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
	        return session.getGraphSession().findAllProcessDefinitions();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public List getAllDefinitions(String name) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
	        return session.getGraphSession().findAllProcessDefinitionVersions(name);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public List getLatestDefinitions() {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
	        return session.getGraphSession().findLatestProcessDefinitions();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public List getNodes(Long id) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
	        ProcessDefinition pD = session.getGraphSession().loadProcessDefinition(id.longValue());
	        return pD.getNodes();
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public List getProcessInstances(Long id) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
	        return session.getGraphSession().findProcessInstances(id.longValue());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public void deleteProcessInstance(Long processInstanceId) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
	       	ProcessInstance pI = session.getGraphSession().loadProcessInstance(processInstanceId.longValue());
	       	if (pI != null) session.getGraphSession().deleteProcessInstance(pI);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};
	
	public ProcessDefinition getWorkflow(Long id) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
	        return session.getGraphSession().loadProcessDefinition(id.longValue());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public ProcessInstance getProcessInstance(Long id) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
        	ProcessInstance pI = session.getGraphSession().loadProcessInstance(id.longValue());
	        return pI;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	};

	public ProcessDefinition addWorkflow(String xmlString){
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
        	ProcessDefinition pD = ProcessDefinition.parseXmlString(xmlString);
        	session.getGraphSession().saveProcessDefinition(pD);
        	return pD;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
	}
	public ProcessInstance addWorkflowInstance(Long id) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
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
	       	JbpmSession session = WorkflowFactory.getSession();
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
	       	JbpmSession session = WorkflowFactory.getSession();
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
	       	JbpmSession session = WorkflowFactory.getSession();
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
       		JbpmSession session = WorkflowFactory.getSession();
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
	public void modifyProcessDefinition(String definitionName, Definition def) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
	        ProcessDefinition pD = session.getGraphSession().findLatestProcessDefinition(definitionName);
	        if (pD == null) {
	        	//The process definition doesn't exist yet, go create one
	        	pD = ProcessDefinition.createNewProcessDefinition();
	    		pD.setName(definitionName);        	
	    		String jobClass = SZoneConfig.getString(def.getZoneName(), "workflowConfiguration/property[@name='" + WorkflowTimeout.TIMEOUT_JOB + "']");
	        	if (Validator.isNull(jobClass)) jobClass = "com.sitescape.ef.jobs.DefaultWorkflowTimeout";
	        	try {
	                Class processorClass = ReflectHelper.classForName(jobClass);
	                WorkflowTimeout job = (WorkflowTimeout)processorClass.newInstance();
	                //make sure a timeout job is scheduled for the zone
	                job.schedule(def.getZoneName());
	        	
	        	} catch (ClassNotFoundException e) {
	        		throw new ConfigurationException(
	        				"Invalid LdapSynchronization class name '" + jobClass + "'",
	        				e);
	        	} catch (InstantiationException e) {
	        		throw new ConfigurationException(
	        				"Cannot instantiate LdapSynchronization of type '"
	                            	+ jobClass + "'");
	        	} catch (IllegalAccessException e) {
	        		throw new ConfigurationException(
	        				"Cannot instantiate LdapSynchronization of type '"
	        				+ jobClass + "'");
	        	}

	        }
	        modifyProcessDefinition(pD, def);
	    	session.getGraphSession().saveProcessDefinition(pD);

	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }
		
	}

	public void modifyProcessDefinition(ProcessDefinition pD, Definition def) {
       	JbpmSession session = WorkflowFactory.getSession();
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
		
		//Add the standard events (if they aren't there already)
		Action recordEvent = null;
		if (!actions.containsKey("recordEvent")) {
			recordEvent = setupAction(pD, "recordEvent", "com.sitescape.ef.module.workflow.RecordEvent");
		} else {
			recordEvent = (Action) actions.get("recordEvent");
		}
		
		Action decisionAction = null;
		if (!actions.containsKey("decisionAction")) {
			decisionAction = setupAction(pD, "decisionAction", "com.sitescape.ef.module.workflow.DecisionAction");
			pD.addAction(decisionAction);
		} else {
			decisionAction = (Action) actions.get("decisionAction");
		}
		
		Action startThreads = null;
		if (!actions.containsKey("startThreads")) {
			startThreads = setupAction(pD, "startThreads", "com.sitescape.ef.module.workflow.StartThreads");
			pD.addAction(startThreads);
		} else {
			startThreads = (Action) actions.get("startThreads");
		}
		
		Action stopThreads = null;
		if (!actions.containsKey("stopThreads")) {
			stopThreads = setupAction(pD, "stopThreads", "com.sitescape.ef.module.workflow.StopThreads");
			pD.addAction(stopThreads);
		} else {
			stopThreads = (Action) actions.get("stopThreads");
		}

		Action notifyAction = null;
		if (!actions.containsKey("notifyAction")) {
			notifyAction = setupAction(pD, "notifyAction", "com.sitescape.ef.module.workflow.Notify");
			pD.addAction(notifyAction);
		} else {
			notifyAction = (Action) actions.get("notifyAction");
		}

		//add global named events - will fire on every node
		if (!events.containsKey("node-enter")) {
			Event enterEvent = new Event("node-enter");
			enterEvent.addAction(recordEvent);
			pD.addEvent(enterEvent);
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
		List stateNodes = defRoot.selectNodes("//item[@name='state']");
		Iterator itStates = stateNodes.iterator();
		while (itStates.hasNext()) {
			Element state = (Element) itStates.next();
			Element stateNameProperty = (Element) state.selectSingleNode("./properties/property[@name='name']");
			String stateName = stateNameProperty.attributeValue("value", "");
			if (!stateName.equals("")) {
				//determine type of node needed
				Node stateNode = (Node)nodesMap.get(stateName);
				if (!nodesMap.containsKey(stateName)) {
					stateNode = new Node(stateName);
					Action action = new Action();
					action.setReferencedAction(decisionAction);
					stateNode.setAction(action);
					pD.addNode(stateNode);
				} else {
					nodesMap.remove(stateName);
				}
				//Check if need parallel threads - if so add special actgion
				List threads = (List)state.selectNodes("./item[@name='startParallelThread']");
				if (!threads.isEmpty()) {
					//make sure start threads action exists
					 addEnterEventAction(stateNode, startThreads);				
				} else {
					// remove any old startThreads for this node
					removeEnterEventAction(stateNode, startThreads);
				}
				//Check if top stop threads - if so add special node
				threads = (List)state.selectNodes("./item[@name='stopParallelThread']");
				if (!threads.isEmpty()) {
					//make sure start threads action exists
					addEnterEventAction(stateNode, stopThreads);
					
				} else {
					// remove any old stopThreads for this node
					removeEnterEventAction(stateNode, stopThreads);
				}
				List notifications = (List)state.selectNodes("./item[@name='notifications']/item[@name='entryNotification']");
				if (!notifications.isEmpty()) {
					//make sure notify action exists
					addEnterEventAction(stateNode, notifyAction);
				} else {
					// remove any old notifyAction for this node
					removeEnterEventAction(stateNode, notifyAction);
				}
				notifications = (List)state.selectNodes("./item[@name='notifications']/item[@name='exitNotification']");
				if (!notifications.isEmpty()) {
					//make sure notify action exists
					addExitEventAction(stateNode, notifyAction);
				} else {
					// remove any old notifyAction for this node
					removeExitEventAction(stateNode, notifyAction);
				}
				Element timer = (Element)state.selectSingleNode("./item[@name='transitions']/item[@name='conditionOnElapsedTime']");
				if (timer != null) {
					Element props = (Element)timer.selectSingleNode("./properties/property[@name='toState']");
					String toState = null;
					if (props != null) {
						toState = props.attributeValue("value");
					}
					//get days and convert to minutes
					props = (Element)timer.selectSingleNode("./properties/property[@name='days']");
					String val=null;
					long total = 0;
					if (props != null) {
						val= props.attributeValue("value");
						if (!Validator.isNull(val))
							total += Long.parseLong(val)*24*60;
					}
					props = (Element)timer.selectSingleNode("./properties/property[@name='hours']");
					if (props != null) {
						val= props.attributeValue("value");
						if (!Validator.isNull(val))
							total += Long.parseLong(val)*60;				    	
					}
					props = (Element)timer.selectSingleNode("./properties/property[@name='mins']");
					if (props != null) {
						val = props.attributeValue("value");
						if (!Validator.isNull(val))
							total += Long.parseLong(val);
					}
					addTimer(stateNode, "onElapsedTime", String.valueOf(total) + " minutes", toState);
 				} else {
					// remove any old timers for this node
					removeTimer(stateNode, "onElapsedTime");
				}
			}
		}
		
		//Remove any nodes that are remaining in nodesMap. 
		//  These must have been deleted from the definition.
		Iterator itNodes = nodesMap.entrySet().iterator();
		while (itNodes.hasNext()) {
			Map.Entry me = (Map.Entry) itNodes.next();
			Node delNode = (Node)me.getValue();
			pD.removeNode(delNode);
			session.getSession().delete(delNode);
		}
		//Add all of the manual transitions
		nodesMap = pD.getNodesMap();
		itNodes = nodesMap.entrySet().iterator();
		while (itNodes.hasNext()) {
			Map.Entry me = (Map.Entry) itNodes.next();
			Node fromNode = (Node) me.getValue();
			String stateName = fromNode.getName();
			//Get all existing transitions
			Map oldTransitions = fromNode.getLeavingTransitionsMap();
			if (oldTransitions == null) oldTransitions = new HashMap();
			else oldTransitions = new HashMap(oldTransitions);
			
			//Get the list of manual transitions from the workflow definition
			Map manualTransitions = WorkflowUtils.getManualTransitions(def, fromNode.getName());
			Iterator itTransitions = manualTransitions.entrySet().iterator();
			while (itTransitions.hasNext()) {
				Map.Entry me2 = (Map.Entry) itTransitions.next();
				String toNodeName = (String) me2.getKey();
				String tName = stateName + "." + toNodeName;					
				if (oldTransitions.containsKey(tName)) {
					oldTransitions.remove(tName);
				} else {
					Node toNode = (Node) pD.getNode(toNodeName);
					if (toNode != null) {
						Transition t = new Transition();
						t.setProcessDefinition(pD);
						t.setName(tName);
						t.setTo(toNode);
						fromNode.addLeavingTransition(t);
					}

				}
			}
			//Get the list of automatic transitions from the workflow definition
			List waits = WorkflowUtils.getParallelThreadWaits(def, stateName);
			for (int i=0; i<waits.size(); ++i) {
				WfWaits w = (WfWaits)waits.get(i);
				String toNodeName = w.getToStateName();
				String tName = stateName + "." + toNodeName;					
				if (oldTransitions.containsKey(tName)) {
					oldTransitions.remove(tName);
				} else {
					Node toNode = (Node) pD.getNode(toNodeName);
					if (toNode != null) {
						Transition t = new Transition();
						t.setProcessDefinition(pD);
						t.setName(tName);
						t.setTo(toNode);
						fromNode.addLeavingTransition(t);
					}

				}
			}
			Iterator itTrans = oldTransitions.entrySet().iterator();
			while (itTrans.hasNext()) {
				Map.Entry me2 = (Map.Entry) itTrans.next();
				Transition delTrans = (Transition)me2.getValue();
				fromNode.removeLeavingTransition(delTrans);
				session.getSession().delete(delTrans);
			}
		}
		
		
    	Writer writer = new StringWriter();
	    JpdlXmlWriter jpdl = new JpdlXmlWriter(writer);
	    jpdl.write(pD);
	    logger.info("Workflow process definition created: " + pD.getName());
	    logger.info(writer.toString());
	}
	private Event addTimer(Node node, String name, String timeout, String toState) {
		//	make sure start threads event exits
		Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
		if (event == null) {
			event = new Event(Event.EVENTTYPE_NODE_ENTER);
			node.addEvent(event);
		}
		List eventActions=event.getActions();
		CreateTimerAction createAction=null;
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if (a instanceof CreateTimerAction) {
					createAction = (CreateTimerAction)a;
					if (name.equals(createAction.getTimerName()))
						break;
					createAction = null;
				}
			}
		}
		if (createAction == null) {
			createAction = new CreateTimerAction();
			createAction.setTimerName(name);
			event.addAction(createAction);
			createAction.setName("createTimer");
		}
		createAction.setDueDate(timeout);
		createAction.setTransitionName(node.getName() + "." + toState);
		//add leave node action
		event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (event == null) {
			event = new Event(Event.EVENTTYPE_NODE_LEAVE);
			node.addEvent(event);
		}
		eventActions=event.getActions();
		CancelTimerAction cancelAction=null;
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if (a instanceof CancelTimerAction) {
					cancelAction = (CancelTimerAction)a;
					if (name.equals(cancelAction.getTimerName()))
						break;
					cancelAction = null;
				}
			}
		}
		if (cancelAction == null) {
			cancelAction = new CancelTimerAction();
			cancelAction.setTimerName(name);
			event.addAction(cancelAction);
			createAction.setName("cancelTimer");
		}
			
		return event;
	
	}
	private void removeTimer(Node node, String name) {
		Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
		if (event != null) {
			List eventActions=event.getActions();
			if ((eventActions != null) && !eventActions.isEmpty()) {
				for (int i=0; i<eventActions.size(); ++i) {
					Action a = (Action)eventActions.get(i);
					if (a instanceof CreateTimerAction) {
						CreateTimerAction createAction = (CreateTimerAction)a;
						if (name.equals(createAction.getTimerName())) {
							event.removeAction(a);
							WorkflowFactory.getSession().getSession().delete(a);	
							break;
						}
					}
				}
			}	
		}
		event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (event != null) {
			List eventActions=event.getActions();
			if ((eventActions != null) && !eventActions.isEmpty()) {
				for (int i=0; i<eventActions.size(); ++i) {
					Action a = (Action)eventActions.get(i);
					if (a instanceof CancelTimerAction) {
						CancelTimerAction cancelAction = (CancelTimerAction)a;
						if (name.equals(cancelAction.getTimerName())) {
							event.removeAction(a);
							WorkflowFactory.getSession().getSession().delete(a);	
							break;
						}
					}
				}
			}	
		}
		
	}
	private Event addEnterEventAction(Node node, Action action) {
		//	make sure start threads event exits
		Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
		if (event == null) {
			event = new Event(Event.EVENTTYPE_NODE_ENTER);
			node.addEvent(event);
		}
		List eventActions=event.getActions();
		boolean found = false;
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if ((a.getReferencedAction() != null)&& a.getReferencedAction().equals(action)) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			Action a = new Action();
			a.setReferencedAction(action);
			event.addAction(a);
		}
		return event;
	
	}
	private void removeEnterEventAction(Node node, Action action) {
		Event event = node.getEvent(Event.EVENTTYPE_NODE_ENTER);
		if (event == null) return;
		List eventActions = new ArrayList(event.getActions());
		if ((eventActions != null) && !eventActions.isEmpty()) {
			JbpmSession session = WorkflowFactory.getSession();
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if ((a.getReferencedAction() != null) && a.getReferencedAction().equals(action)) {
					event.removeAction(a);
					//this is necessary cause the hibernate settings try to do a cascade-delete on 
					//these associations (STUPID)
					a.setReferencedAction(null);
					a.setActionDelegation(null);
					session.getSession().delete(a);
					break;
					
				}
			}
		}
	}

	private Event addExitEventAction(Node node, Action action) {
		//	make sure start threads event exits
		Event event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (event == null) {
			event = new Event(Event.EVENTTYPE_NODE_LEAVE);
			node.addEvent(event);
		}
		List eventActions=event.getActions();
		boolean found = false;
		if ((eventActions != null) && !eventActions.isEmpty()) {
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if ((a.getReferencedAction() != null) && (a.getReferencedAction().equals(action))) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			Action a = new Action();
			a.setReferencedAction(action);
			event.addAction(a);
		}
		return event;
	
	}
	private void removeExitEventAction(Node node, Action action) {
		Event event = node.getEvent(Event.EVENTTYPE_NODE_LEAVE);
		if (event == null) return;
		List eventActions = new ArrayList(event.getActions());
		if ((eventActions != null) && !eventActions.isEmpty()) {
			JbpmSession session = WorkflowFactory.getSession();
			for (int i=0; i<eventActions.size(); ++i) {
				Action a = (Action)eventActions.get(i);
				if ((a.getReferencedAction() != null) && (a.getReferencedAction().equals(action))) {
					event.removeAction(a);
					//this is necessary cause the hibernate settings try to do a cascade-delete on 
					//these associations (STUPID)
					a.setReferencedAction(null);
					a.setActionDelegation(null);
					session.getSession().delete(a);
					break;
				}
			}
		}
	}

	private Action setupAction(ProcessDefinition pD, String name, String className) {
		Action action = new Action();
		action.setName(name);
		action.setProcessDefinition(pD);
		Delegation recordDelegation = new Delegation(className);
		recordDelegation.setConfigType("bean");
		action.setActionDelegation(recordDelegation);
		return action;

	}
	public void addEntryWorkflow(Entry entry, Definition workflowDef) {
		String entryType = AnyOwner.getType(entry);
		String initialState = WorkflowUtils.getInitialState(workflowDef);
		if (!Validator.isNull(initialState)) {
			//Now start the workflow at the desired initial state
			JbpmSession session;
			ProcessDefinition pD;
			try {
		       	session = WorkflowFactory.getSession();
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
					ws.setDefinition(workflowDef);
					//need to save explicitly - actions called by the node.enter may look it up 
					getCoreDao().save(ws);
					entry.addWorkflowState(ws);
					//Start the workflow process at the initial state
				    ExecutionContext executionContext = new ExecutionContext(token);
		            node.enter(executionContext);
		            
		            session.getGraphSession().saveProcessInstance(pI);
			    }
		    } catch (Exception ex) {
		        throw convertJbpmException(ex);
		    }
		}
	}
	
	public void modifyWorkflowState(Long tokenId, String fromState, String toState) {
	    try {
	       	JbpmSession session = WorkflowFactory.getSession();
        	Token t = session.getGraphSession().loadToken(tokenId.longValue());
            t.signal(fromState + "." + toState);
            //need to make this call to save logs
            session.getGraphSession().saveProcessInstance(t.getProcessInstance());
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    }		
	}

	public void modifyWorkflowStateOnTimeout(Long timerId) {
		//Assume RequestContext is set
		JbpmSession session = WorkflowFactory.getSession();
   		SchedulerSession schedulerSession = new SchedulerSession(session);
      	Timer timer = (Timer)session.getSession().load(Timer.class, timerId);
      	if (timer == null) return;
      	Token token = timer.getToken();
      	WorkflowControlledEntry entry = null;
      	if (token != null) {
      		//token id is id of workflowState
      		WorkflowState ws = (WorkflowState)getCoreDao().load(WorkflowState.class, new Long(token.getId()));
      		entry = (WorkflowControlledEntry)ws.getOwner().getEntry();
      	}
  
      	// execute
        timer.execute();
        //re-index for state changes
        if (entry != null) {
        	EntryProcessor processor = 
        		(EntryProcessor) getProcessorManager().getProcessor(entry.getParentBinder(), 
        					EntryProcessor.PROCESSOR_KEY);
        	processor.indexEntry(entry);
        }
        // if there was an exception, just save the timer
        if (timer.getException()==null) {
        	// if repeat is specified
        	if (timer.getRepeat()!=null) {
        		// update timer by adding the repeat duration
        		Date dueDate = timer.getDueDate();
          
        		// suppose that it took the timer runner thread a 
        		// very long time to execute the timers.
        		// then the repeat action dueDate could already have passed.
        		while (dueDate.getTime()<=System.currentTimeMillis()) {
        			dueDate = businessCalendar
        			.add(dueDate, 
        					new Duration(timer.getRepeat()));
        		}
        		timer.setDueDate( dueDate );
        		// save the updated timer in the database
        		logger.debug("saving updated timer for repetition '"+timer+"' in '"+(dueDate.getTime()-System.currentTimeMillis())+"' millis");
        		schedulerSession.saveTimer(timer);
        	} else {
        		// delete this timer
        		logger.debug("deleting timer '"+timer+"'");
        		schedulerSession.deleteTimer(timer);
        	}
        }
		
	}
	
	public void deleteEntryWorkflow(Binder parent, Entry entry) {
		//Delete all JBPM tokens and process instances associated with this entry
	    try {
			List processInstances = new ArrayList();
			List tokenIds = new ArrayList();
	       	JbpmSession session = WorkflowFactory.getSession();
	  		Set workflowStates = entry.getWorkflowStates();
   			for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
				WorkflowState ws = (WorkflowState)iter.next();
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
	
	public void deleteWorkflowToken(WorkflowState ws) {
       	JbpmSession session = WorkflowFactory.getSession();
		Token t = session.getGraphSession().loadToken(ws.getTokenId().longValue());
		t.end();
	}
	
}
