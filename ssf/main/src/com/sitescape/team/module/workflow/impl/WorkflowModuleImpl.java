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
package com.sitescape.team.module.workflow.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Calendar;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.jbpm.JbpmContext;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.Duration;
import org.jbpm.context.exe.ContextInstance;
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
import org.jbpm.scheduler.def.CancelTimerAction;
import org.jbpm.scheduler.def.CreateTimerAction;
import org.jbpm.scheduler.exe.Timer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.jobs.ZoneSchedule;
import com.sitescape.team.ConfigurationException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinitionInvalidException;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.WorkflowTimeout;
import com.sitescape.team.module.binder.processor.EntryProcessor;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.workflow.NotificationUtils;
import com.sitescape.team.module.workflow.TransitionUtils;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Validator;

public class WorkflowModuleImpl extends CommonDependencyInjection implements WorkflowModule, ZoneSchedule, InitializingBean {
   static BusinessCalendar businessCalendar;
   protected TransactionTemplate transactionTemplate;
   protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

   /**
    * Called after bean is initialized.  Use this to make sure
    * scheduler has workflowtimeout job active
    * Use named method instead of initializingBean signature, so
    * junit test work without SZoneConfig.
    *
    */
   public void afterPropertiesSet() {
	   businessCalendar = new BusinessCalendar();
   }
	protected WorkflowTimeout getProcessor(Workspace zone) {
		String jobClass = SZoneConfig.getString(zone.getName(), "workflowConfiguration/property[@name='" + WorkflowTimeout.TIMEOUT_JOB + "']");
		if (Validator.isNull(jobClass)) jobClass = "com.sitescape.team.jobs.DefaultWorkflowTimeout";
		try {
			Class processorClass = ReflectHelper.classForName(jobClass);
			WorkflowTimeout job = (WorkflowTimeout)processorClass.newInstance();
			return job;
		} catch (ClassNotFoundException e) {
			   throw new ConfigurationException(
					"Invalid WorkflowTimeout class name '" + jobClass + "'",
					e);
		} catch (InstantiationException e) {
			   throw new ConfigurationException(
					"Cannot instantiate WorkflowTimeout of type '"
	                    	+ jobClass + "'");
		} catch (IllegalAccessException e) {
			   throw new ConfigurationException(
					"Cannot instantiate WorkflowTimeout of type '"
					+ jobClass + "'");
		} 
		   		
	}
	//called on zone delete
	public void stopScheduledJobs(Workspace zone) {
		WorkflowTimeout job =getProcessor(zone);
   		job.remove(zone.getId());
	}
	//called on zone startup
   public void startScheduledJobs(Workspace zone) {
	   if (zone.isDeleted()) return;
		WorkflowTimeout job =getProcessor(zone);
	   //make sure a timeout job is scheduled for the zone
	   String secsString = (String)SZoneConfig.getString(zone.getName(), "workflowConfiguration/property[@name='" + WorkflowTimeout.TIMEOUT_SECONDS + "']");
	   int seconds = 5*60;
	   try {
		   seconds = Integer.parseInt(secsString);
	   } catch (Exception ex) {};
	   job.schedule(zone.getId(), seconds);

    }

	public void deleteProcessDefinition(String name) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        ProcessDefinition pD = context.getGraphSession().findLatestProcessDefinition(name);
	        context.getGraphSession().deleteProcessDefinition(pD);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	};
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
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        ProcessDefinition pD = context.getGraphSession().findLatestProcessDefinition(definitionName);
	        if (pD == null) {
	        	//The process definition doesn't exist yet, go create one
	        	pD = ProcessDefinition.createNewProcessDefinition();
	    		pD.setName(definitionName);        	
	        }
	        modifyProcessDefinition(pD, def);
	    	context.getGraphSession().saveProcessDefinition(pD);

	    } catch (DefinitionInvalidException dx) {
	    	throw dx;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
		
	}
	public void modifyStateName(String definitionName, String oldName, String newName) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	        ProcessDefinition pD = context.getGraphSession().findLatestProcessDefinition(definitionName);
	        if (pD == null) return;
	        Node stateNode = pD.getNode(oldName);
	        if (stateNode == null) return;
	        Set<Transition> incoming = stateNode.getArrivingTransitions();
	        //change name on incomming
	        for (Transition in:incoming) {
	        	String prefix = in.getName().substring(0, in.getName().length()-oldName.length());
	        	in.setName(prefix + newName);
	        }
	        List<Transition>outgoing = stateNode.getLeavingTransitions();
	        //change name on incomming
	        for (Transition out:outgoing) {
	        	String suffix = out.getName().substring(oldName.length());
	        	out.setName(newName + suffix);
	        }
	        stateNode.setName(newName);
	        context.getGraphSession().saveProcessDefinition(pD);

	    } catch (DefinitionInvalidException dx) {
	    	throw dx;
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
		
	}
	protected void modifyProcessDefinition(ProcessDefinition pD, Definition def) {
		JbpmContext context = WorkflowFactory.getContext();
	    try {
	    	Document defDoc = def.getDefinition();
	    	Element defRoot = defDoc.getRootElement();
	        //make sure end state doesn't have transitions out
			Element wf = (Element)defRoot.selectSingleNode("./item[@name='workflowProcess']");
			if (wf != null) {
				String endState = DefinitionUtils.getPropertyValue(wf, "endState");
				if (Validator.isNotNull(endState)) {
					if (!TransitionUtils.getAllTransitions(def, endState).isEmpty()) {
						throw new DefinitionInvalidException("errorcode.transitions.notallowed.endstate");
					}
				}
			}
	        
		
	    	//Start by remembering all of the nodes
	    	Map nodesMap = pD.getNodesMap();
	    	if (nodesMap == null) nodesMap = new HashMap();
	    	nodesMap = new HashMap(nodesMap);
		
	    	Map events = pD.getEvents();
	    	if (events == null) events = new HashMap();
		
	    	Map actions = pD.getActions();
	    	if (actions == null) actions = new HashMap();
		
	    	//	Add the standard events (if they aren't there already)
	    	Action enterNodeEvent = null;
	    	if (!actions.containsKey("enterNodeEvent")) {
	    		enterNodeEvent = setupAction(pD, "enterNodeEvent", "com.sitescape.team.module.workflow.EnterExitEvent");
	    	} else {
	    		enterNodeEvent = (Action) actions.get("enterNodeEvent");
	    	}
		
	       	//	Add the standard events (if they aren't there already)
	    	Action leaveNodeEvent = null;
	    	if (!actions.containsKey("leaveNodeEvent")) {
	    		leaveNodeEvent = setupAction(pD, "leaveNodeEvent", "com.sitescape.team.module.workflow.EnterExitEvent");
	    	} else {
	    		leaveNodeEvent = (Action) actions.get("leaveNodeEvent");
	    	}

	    	Action decisionAction = null;
	    	if (!actions.containsKey("decisionAction")) {
	    		decisionAction = setupAction(pD, "decisionAction", "com.sitescape.team.module.workflow.DecisionAction");
	    		pD.addAction(decisionAction);
	    	} else {
	    		decisionAction = (Action) actions.get("decisionAction");
	    	}
	    	
			Action timerAction = null;
			if (!actions.containsKey("timerAction")) {
				timerAction = setupAction(pD, "timerAction", "com.sitescape.team.module.workflow.TimerAction");
				pD.addAction(timerAction);
			} else {
				timerAction = (Action) actions.get("timerAction");
			}


			/** obsolete in V1.1
			Action notifyAction = null;
	    	if (!actions.containsKey("notifyAction")) {
	    		notifyAction = setupAction(pD, "notifyAction", "com.sitescape.team.module.workflow.Notify");
	    		pD.addAction(notifyAction);
	    	} else {
	    		notifyAction = (Action) actions.get("notifyAction");
	    	}
	    	**/
	    	//add global named events - will fire on every node
	    	if (!events.containsKey("node-enter")) {
	    		Event enterEvent = new Event("node-enter");
	    		enterEvent.addAction(enterNodeEvent);
	    		pD.addEvent(enterEvent);
	    	}
		
	    	//add global named events - will fire on every node
	    	if (!events.containsKey("node-leave")) {
	    		Event leaveEvent = new Event("node-leave");
	    		leaveEvent.addAction(leaveNodeEvent);
	    		pD.addEvent(leaveEvent);
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
	    		String stateName = DefinitionUtils.getPropertyValue(state, "name");
	    		if (!Validator.isNull(stateName)) {
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
    				Element timer = (Element)state.selectSingleNode("./item[@name='transitions']/item[@name='transitionOnElapsedTime']");
    				if (timer != null) {
    					String toState = DefinitionUtils.getPropertyValue(timer, "toState");
    					long total = 0;
    					//	get days and convert to minutes
    					String val=DefinitionUtils.getPropertyValue(timer, "days");
    					if (!Validator.isNull(val)) total += Long.parseLong(val)*24*60;
    				
    					val=DefinitionUtils.getPropertyValue(timer, "hours");
    					if (!Validator.isNull(val)) total += Long.parseLong(val)*60;				    	
    				
    					val=DefinitionUtils.getPropertyValue(timer, "mins");
	    					if (!Validator.isNull(val)) total += Long.parseLong(val);
    					addTimer(context, stateNode, "onElapsedTime", String.valueOf(total) + " minutes", toState);
    				} else {
    					// 	remove any old timers for this node
    					removeTimer(context, stateNode, "onElapsedTime");
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
	    		Action a = delNode.getAction();
	    		if (a != null) a.setReferencedAction(null);
		        Set<Transition> incoming = delNode.getArrivingTransitions();
		        //delete incomming transitions
		        for (Transition in:incoming) {
		        	Node from = in.getFrom();
		        	if (from != null) {
			        	TransitionUtils.setTransition(def, from.getName(), delNode.getName(), "");
		        		from.removeLeavingTransition(in);
		        	}
		        	context.getSession().delete(in);
		        }
		        
		        List<Transition>outgoing = delNode.getLeavingTransitions();
		        //delete outgoing transitions
		        for (Transition out:outgoing) {
		        	Node to = out.getTo();
		        	if (to != null) {
		        		//delNode and its transitions out have already been removed from definition
		        		to.removeArrivingTransition(out);
		        	}
		        	context.getSession().delete(out);
		        }
	    		context.getSession().delete(delNode);
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
	    		Set allTransitions = TransitionUtils.getAllTransitions(def, fromNode.getName());
	    		for (Iterator iter=allTransitions.iterator(); iter.hasNext();) {
	    			String toNodeName = (String)iter.next();
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
	    				} else {
	    					//update definition
	    					TransitionUtils.setTransition(def, fromNode.getName(), toNodeName, "");
	    				}
	    				
	    			}
	    		}
	    		Iterator itTrans = oldTransitions.entrySet().iterator();
	    		while (itTrans.hasNext()) {
	    			Map.Entry me2 = (Map.Entry) itTrans.next();
	    			Transition delTrans = (Transition)me2.getValue();
	    			fromNode.removeLeavingTransition(delTrans);
	    			context.getSession().delete(delTrans);
	    		}
	    	}
		
	    } finally {
	    	context.close();
	    }
//    	Writer writer = new StringWriter();
//	    JpdlXmlWriter jpdl = new JpdlXmlWriter(writer);
//	    jpdl.write(pD);
//	    logger.info("Workflow process definition created: " + pD.getName());
//	    logger.info(writer.toString());
	}
	private Event addTimer(JbpmContext context, Node node, String name, String timeout, String toState) {
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
			cancelAction.setName("cancelTimer");
		}
			
		return event;
	
	}
	private void removeTimer(JbpmContext context, Node node, String name) {
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
							context.getSession().delete(a);	
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
							context.getSession().delete(a);	
							break;
						}
					}
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
	public void addEntryWorkflow(WorkflowSupport entry, EntityIdentifier id, Definition workflowDef, Map options) {
		String startState=null;
		if (options != null) startState = (String)options.get(ObjectKeys.INPUT_OPTION_FORCE_WORKFLOW_STATE);
		String initialState = startState;
		if (Validator.isNull(initialState)) initialState = TransitionUtils.getInitialState(workflowDef);
		if (!Validator.isNull(initialState)) {
			//Now start the workflow at the desired initial state
			JbpmContext context=WorkflowFactory.getContext();
			ProcessDefinition pD;
			try {
		        pD = context.getGraphSession().findLatestProcessDefinition(workflowDef.getId());
		        Node node = pD.getNode(initialState);
			    if (node != null) {
		        	ProcessInstance pI = new ProcessInstance(pD);
					Token token = pI.getRootToken();
					ContextInstance cI = (ContextInstance) pI.getInstance(ContextInstance.class);
					cI.setVariable(WorkflowModule.ENTRY_ID, id.getEntityId(), token);
					cI.setVariable(WorkflowModule.ENTRY_TYPE, id.getEntityType().name(), token);
					//If initial state is specified, assume starting in the middle on an import entry.
					cI.setTransientVariable(WorkflowModule.FORCE_STATE, startState);
					//doesn't exist, add a new one
					WorkflowState ws = new WorkflowState();
					if (Validator.isNotNull(startState) && options != null && options.containsKey(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE)) { 
						// Used to import entries into system.  Preserved when used with SKIP_NOTIFY_ON_ENTER
						User user;
						Calendar date = (Calendar)options.get(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE);
						String name = (String)options.get(ObjectKeys.INPUT_OPTION_MODIFICATION_NAME);
						if (Validator.isNull(name)) {
							user = RequestContextHolder.getRequestContext().getUser();
						} else {
							user = getProfileDao().findUserByName(name, RequestContextHolder.getRequestContext().getZoneName());
						}
						entry.setWorkflowChange(new HistoryStamp(user, date.getTime()));
						ws.setWorkflowChange(new HistoryStamp(user, date.getTime()));
					}
					ws.setTokenId(new Long(token.getId()));
					ws.setState(initialState);
					ws.setDefinition(workflowDef);
					//need to save explicitly - actions called by the node.enter may look it up 
					getCoreDao().save(ws);
					entry.addWorkflowState(ws);
					//Start the workflow process at the initial state
				    ExecutionContext executionContext = new ExecutionContext(token);
		            node.enter(executionContext);
		            context.save(pI);
			    }
		    } catch (Exception ex) {
		        throw convertJbpmException(ex);
		    } finally {
		    	context.close();
		    }
		}
	}

	public void deleteEntryWorkflow(WorkflowSupport wEntry, WorkflowState state) {
	    try {
	    	TransitionUtils.endWorkflow(wEntry, state, true);
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } 
	}

	public void deleteEntryWorkflow(WorkflowSupport wEntry, Definition def) {
		Set<WorkflowState> states = wEntry.getWorkflowStates();
		for (WorkflowState state: states) {
			if (def.equals(state.getDefinition()) && Validator.isNull(state.getThreadName())) {
				//have the root
				try {
			    	TransitionUtils.endWorkflow(wEntry, state, true);
			    } catch (Exception ex) {
			        logger.error("Error deleting workflow", ex);
			    } 
			    break;
			}
		}
		states = new HashSet(wEntry.getWorkflowStates());
		//make sure all threads are cleaned up
		for (WorkflowState state: states) {
			if (state.getOwner() == null) continue; //already removed
			if (def.equals(state.getDefinition())) {
				try {
			    	TransitionUtils.endWorkflow(wEntry, state, true);
			    } catch (Exception ex) {
			    	wEntry.removeWorkflowState(state);
			        logger.error("Error deleting workflow", ex);
			    } 								
			}
		}
		
	}
	//cleanup when deleteing an entry.  Caller takes care of states
	public void deleteEntryWorkflow(WorkflowSupport entry) {
		//Delete all JBPM tokens and process instances associated with this entry
		JbpmContext context=WorkflowFactory.getContext();
	    try {
			Set processInstances = new HashSet();
	  		Set workflowStates = entry.getWorkflowStates();
   			for (Iterator iter=workflowStates.iterator(); iter.hasNext();) {
				WorkflowState ws = (WorkflowState)iter.next();
				Token t = context.loadToken(ws.getTokenId().longValue());
				//Remember all of the unique process instances that we have to delete
				//tokens may belong to the same PI
				processInstances.add(t.getProcessInstance());
			}
			//Now delete the process instances used by this entry
			for (Iterator iter=processInstances.iterator(); iter.hasNext();) {
				ProcessInstance pI = (ProcessInstance)iter.next();
				pI.end();
				context.getGraphSession().deleteProcessInstance(pI);
			}
	    } catch (Exception ex) {
	        throw convertJbpmException(ex);
	    } finally {
	    	context.close();
	    }
	}
	/**
	 * Signal a transition.  The caller is responsible for updating the index.
	 */
	public void modifyWorkflowState(WorkflowSupport entry, WorkflowState state, String toState) {
		TransitionUtils.processManualTransition(entry, state, toState);
	}
	/**
	 * Set some workflow variables and continue processing.
	 */
	public void setWorkflowVariables(WorkflowSupport entry, WorkflowState state, Map<String, Object> variables) {
		if (variables.isEmpty()) return;
		JbpmContext context=WorkflowFactory.getContext();
		try {
			Long tokenId = state.getTokenId();
			if (tokenId == null) return;
			Token token = context.loadTokenForUpdate(tokenId);
			//	make sure state hasn't been removed as the result of another thread
			if (token.hasEnded() || (state.getOwner() == null)) return;
			ExecutionContext executionContext = new ExecutionContext(token);
			ContextInstance cI = executionContext.getContextInstance();
			for (Iterator iter=variables.entrySet().iterator(); iter.hasNext();) {
				Map.Entry me = (Map.Entry)iter.next();	
				cI.setVariable((String)me.getKey(), me.getValue());
				if (debugEnabled) logger.debug("Set variable " + me.getKey() + "=" + me.getValue());
			}
		} finally {
			context.close();
		}
	}
	public void processTimers() {
    	JbpmContext jContext = WorkflowFactory.getContext();
   		HashSet<Long>timers = new HashSet();
   	   	try {
    		SchedulerSession schedulerSession = jContext.getSchedulerSession();
    	      
    		logger.debug("checking for timers");
    		//collect timer info and close context,
    		//otherwise something messes up with closing the iterator.
    		Iterator iter = schedulerSession.findTimersByDueDate(100);
    		boolean isDueDateInPast=true; 
    		while( (iter.hasNext()) && (isDueDateInPast)) {
    			Timer timer = (Timer) iter.next();
    			if(logger.isDebugEnabled())
    				logger.debug("found timer "+timer);
    			//Do work inside a transaction in the workflowModule
    			// if this timer is due
    			if (timer.isDue()) {
    				if(logger.isDebugEnabled())
    					logger.debug("executing timer '"+timer+"'");
    				timers.add(new Long(timer.getId()));
 
    			} else { // this is the first timer that is not yet due
    				isDueDateInPast = false;
    			}
   	      	}
    	} finally {
    		jContext.close();
    	}
		final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		for (Long timer:timers) {
   			final JbpmContext context=WorkflowFactory.getContext();	   	
   			try {
     			final Long timerId=timer;
    			getTransactionTemplate().execute(new TransactionCallback() {
    				public Object doInTransaction(TransactionStatus status) {
	        			SchedulerSession schedulerSession = context.getSchedulerSession();
	        			Timer timer = (Timer)context.getSession().load(Timer.class, timerId);
	        			if (timer == null) return null;
	        			Token token = timer.getToken();
	        			Entry entry = null;
	        			if (token != null) {
	        				//	token id is id of workflowState
	        				WorkflowState ws = (WorkflowState)getCoreDao().load(WorkflowState.class, new Long(token.getId()));
	        				entry = (Entry)ws.getOwner().getEntity();
	        				if (entry.isDeleted() || entry.getParentBinder().isDeleted()) {
	        					schedulerSession.deleteTimer(timer);
	        					return null;
	        				}
	        				//	only process timers in current zone
	        				if (!ws.getDefinition().getZoneId().equals(zoneId)) return null;
	        			}
  
	        			if (timer.getName().equals("onDataValue")) {
							//this is a sitescape addition to timer processing
							// execute
							timer.execute();
							//re-index for state changes
							if (entry != null) {
								EntryProcessor processor = loadEntryProcessor(entry.getParentBinder());
								entry.incrLogVersion();
								processor.processChangeLog(entry, ChangeLog.WORKFLOWTIMEOUT);
								processor.indexEntry(entry);
							}
						} else {
							// execute
							timer.execute();
							//re-index for state changes
							if (entry != null) {
								entry.incrLogVersion();
								EntryProcessor processor = loadEntryProcessor(entry.getParentBinder()); 
								processor.processChangeLog(entry, ChangeLog.WORKFLOWTIMEOUT);
								processor.indexEntry(entry);
							}
							// if there was an exception, just save the timer
							if (timer.getException()== null) {
								// 	if repeat is specified
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
									if(logger.isDebugEnabled())
										logger.debug("saving updated timer for repetition '"+timer+"' in '"+(dueDate.getTime()-System.currentTimeMillis())+"' millis");
									schedulerSession.saveTimer(timer);
								} else {
									// 	delete this timer
									if(logger.isDebugEnabled())
										logger.debug("deleting timer '"+timer+"'");
									schedulerSession.deleteTimer(timer);
								}
							} else {
								schedulerSession.saveTimer(timer);
							}
						}
						if (token != null) context.save(token);
						return null;
    	        	}});
    			
    		} finally {
				context.close();
			}
		}
		
	}
	/**
	 * See if any conditions have been met for a transition to a new state.
	 * This would be triggered by a modify.  The caller is responsible for
	 * updating the index.
	 * @param entry
	 */
	public boolean modifyWorkflowStateOnUpdate(WorkflowSupport entry) {
		return TransitionUtils.processConditions(entry, true, false);
	}
	/**
	 * See if any conditions have been met for a transition to a new state.
	 * This would be triggered by workflow response.  The caller is responsible for
	 * updating the index.
	 * @param entry
	 */
	public boolean modifyWorkflowStateOnResponse(WorkflowSupport entry) {
		return TransitionUtils.processConditions(entry, false, false);
	}
	/**
	 * See if reply will trigger a transition to a new state.
	 * The caller is responsible for updating the index.
	 * @param entry
	 */
	public boolean modifyWorkflowStateOnReply(WorkflowSupport entry) {
		return TransitionUtils.processConditions(entry, false, true);	
	}	

	//see if anything to do after some external event
    public void  modifyWorkflowStateOnChange(WorkflowSupport wfEntry) {
		boolean changed = TransitionUtils.processConditions(wfEntry, false, false);
		if (changed) {
			Entry entry = (Entry)wfEntry;
			EntryProcessor processor = loadEntryProcessor(entry.getParentBinder());
			entry.incrLogVersion();
			processor.processChangeLog(entry, ChangeLog.WORKFLOWTIMEOUT);
			processor.indexEntry(entry);
		}
	
    }
	
	private EntryProcessor loadEntryProcessor(Binder binder) {
        // This is nothing but a dispatcher to an appropriate processor. 
        // Shared logic, if exists, must be put into the corresponding method in 
        // com.sitescape.team.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
		return (EntryProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(EntryProcessor.PROCESSOR_KEY));			
	}	
}
