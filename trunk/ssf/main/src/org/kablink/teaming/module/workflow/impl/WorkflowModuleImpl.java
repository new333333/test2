/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.workflow.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.DelegationException;
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
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DefinitionInvalidException;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.jobs.WorkflowTimeout;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.module.binder.processor.EntryProcessor;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workflow.WorkflowProcessUtils;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.util.Validator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


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
    	if (Validator.isNotNull(jobClass)) {
    		try {
    			return (WorkflowTimeout)ReflectHelper.getInstance(jobClass);
    		} catch (Exception e) {
 			   logger.error("Cannot instantiate WorkflowTimeout custom class", e);
    		}
    	}
    	return (WorkflowTimeout)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultWorkflowTimeout.class);		   		
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
					if (!WorkflowUtils.getAllTransitions(def, endState).isEmpty()) {
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
	    		enterNodeEvent = setupAction(pD, "enterNodeEvent", "org.kablink.teaming.module.workflow.EnterExitEvent");
	    	} else {
	    		enterNodeEvent = (Action) actions.get("enterNodeEvent");
	    	}
		
	       	//	Add the standard events (if they aren't there already)
	    	Action leaveNodeEvent = null;
	    	if (!actions.containsKey("leaveNodeEvent")) {
	    		leaveNodeEvent = setupAction(pD, "leaveNodeEvent", "org.kablink.teaming.module.workflow.EnterExitEvent");
	    	} else {
	    		leaveNodeEvent = (Action) actions.get("leaveNodeEvent");
	    	}

	    	Action decisionAction = null;
	    	if (!actions.containsKey("decisionAction")) {
	    		decisionAction = setupAction(pD, "decisionAction", "org.kablink.teaming.module.workflow.DecisionAction");
	    		pD.addAction(decisionAction);
	    	} else {
	    		decisionAction = (Action) actions.get("decisionAction");
	    	}
	    	
			Action timerAction = null;
			if (!actions.containsKey("timerAction")) {
				timerAction = setupAction(pD, "timerAction", "org.kablink.teaming.module.workflow.TimerAction");
				pD.addAction(timerAction);
			} else {
				timerAction = (Action) actions.get("timerAction");
			}


			/** obsolete in V1.1
			Action notifyAction = null;
	    	if (!actions.containsKey("notifyAction")) {
	    		notifyAction = setupAction(pD, "notifyAction", "org.kablink.teaming.module.workflow.Notify");
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
		        		WorkflowUtils.setTransition(def, from.getName(), delNode.getName(), "");
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
	    		Set allTransitions = WorkflowUtils.getAllTransitions(def, fromNode.getName());
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
	    					WorkflowUtils.setTransition(def, fromNode.getName(), toNodeName, "");
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
		if (Validator.isNull(initialState)) initialState = WorkflowProcessUtils.getInitialState(workflowDef);
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
	    	WorkflowProcessUtils.endWorkflow(wEntry, state, true);
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
			    	WorkflowProcessUtils.endWorkflow(wEntry, state, true);
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
			    	WorkflowProcessUtils.endWorkflow(wEntry, state, true);
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
		WorkflowProcessUtils.processManualTransition(entry, state, toState);
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
		for (Long timerId:timers) {
			//this is all done outside the transaction
			final JbpmContext context=WorkflowFactory.getContext();
			//This clears the hibernate session that is shared between teaming and jbpm and is slightly dangerous
			//Should only be a problem if the current user or zone have collections that are not loaded, or
			//require update.
			//This is done to reduce memory requirements between transactions.
			context.getSession().clear();
			try {
				final SchedulerSession schedulerSession = context.getSchedulerSession();
				final Timer timer = (Timer)context.getSession().load(Timer.class, timerId);
				if (timer == null) continue;
				final Token token = timer.getToken(); 
				if (token == null) continue; //don't support
				//each timer needs its own transaction so we can rollback failures with out effecting others
 				getTransactionTemplate().execute(new TransactionCallback() {
 					public Object doInTransaction(TransactionStatus status) {
 						Long runAsId=null;
 						//token id is id of workflowState
 						WorkflowState ws = (WorkflowState)getCoreDao().load(WorkflowState.class, new Long(token.getId()));
 		   				//only process timers in current zone
 						if (ws == null || !ws.getDefinition().getZoneId().equals(zoneId)) return null;;
 						final Entry entry = (Entry)ws.getOwner().getEntity();					
 						if (entry.isDeleted() || entry.getParentBinder().isDeleted()) {
 							schedulerSession.deleteTimer(timer);
 							return null;
 						}
 						Document wfDoc = ws.getDefinition().getDefinition();
 						if (wfDoc == null || wfDoc.getRootElement() == null) {
 							schedulerSession.deleteTimer(timer);
 							return null;
 						}
 						Element process = (Element)wfDoc.getRootElement().selectSingleNode("./item[@name='workflowProcess']");
 						runAsId = WorkflowProcessUtils.getRunAsUser(process, (WorkflowSupport)entry, ws);
 						if (runAsId == null) {
 							throw new TimerException(entry, ws, "User not found to process workflow timout");
 						}
	        			try {
	        				RunasTemplate.runas(new RunasCallback() {
	        					public Object doAs() {
	        						timer.execute();
	        						//re-index for state changes
	        						EntryProcessor processor = loadEntryProcessor(entry.getParentBinder());
	        						entry.incrLogVersion();
	        						processor.processChangeLog(entry, ChangeLog.WORKFLOWTIMEOUT);
	        						processor.indexEntry(entry);
	        						return null;
	        					}
	        				}, zoneId, runAsId);
	        			} catch (DelegationException jx) {
	        				throw new TimerException(entry, ws, "Error processing workflow timeout: " + 
	        						jx.getCause().getMessage());
	        			} catch (Exception ex) {
	        				throw new TimerException(entry, ws, "Error processing workflow timeout: " + ex.getMessage());
	        			}
	        			//onDataValue takes care of itself
	        			if (!timer.getName().equals("onDataValue")) {
	        				//jbpm defined timer - we don't support repeats currently (copied from jbpm scheduler thread)
							// if there was an exception, just save the timer
							if (timer.getException()== null) {
								// 	delete this timer
								if(logger.isDebugEnabled())	logger.debug("deleting timer '"+timer+"'");
								schedulerSession.deleteTimer(timer);
							} else {
								logger.error(timer.getException());
								//want it to run again, so remove exception
								timer.setException(null);
								schedulerSession.saveTimer(timer);
							}
						}
						if (token != null) context.save(token);
						return null;
    	        	}});
   			} catch (TimerException tx) {
   				//may want to record the error in the state at some point.  Will need a transaction.
   				logger.error("Error on " +
   						tx.entry.getParentBinder().getPathName() + "/" + tx.entry.getTitle() +
   					 " " + tx.message);
   			} catch (Exception ex) {
   				logger.error("Error processing timeout: " + ex.getMessage());
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
		return WorkflowProcessUtils.processConditions(entry, true, false);
	}
	/**
	 * See if any conditions have been met for a transition to a new state.
	 * This would be triggered by workflow response.  The caller is responsible for
	 * updating the index.
	 * @param entry
	 */
	public boolean modifyWorkflowStateOnResponse(WorkflowSupport entry) {
		return WorkflowProcessUtils.processConditions(entry, false, false);
	}
	/**
	 * See if reply will trigger a transition to a new state.
	 * The caller is responsible for updating the index.
	 * @param entry
	 */
	public boolean modifyWorkflowStateOnReply(WorkflowSupport entry) {
		return WorkflowProcessUtils.processConditions(entry, false, true);	
	}	

	//see if anything to do after a deleted entry gets restored
    public void  modifyWorkflowStateOnRestore(WorkflowSupport wfEntry) {
		boolean changed = WorkflowProcessUtils.processConditions(wfEntry, false, false);
		if (changed) {
			Entry entry = (Entry)wfEntry;
			EntryProcessor processor = loadEntryProcessor(entry.getParentBinder());
			entry.incrLogVersion();
			processor.processChangeLog(entry, ChangeLog.RESTOREENTRY);
			processor.indexEntry(entry);
		}
	
    }
	
	//see if anything to do after some external event
    public void  modifyWorkflowStateOnChange(WorkflowSupport wfEntry) {
		boolean changed = WorkflowProcessUtils.processConditions(wfEntry, false, false);
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
        // org.kablink.teaming.module.folder.AbstractfolderCoreProcessor class, not 
        // in this method.
		return (EntryProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(EntryProcessor.PROCESSOR_KEY));			
	}
	class TimerException extends RuntimeException {
		protected Entry entry;
		protected WorkflowState state;
		protected String message;
		public TimerException(Entry entry, WorkflowState state, String message) {
			this.entry = entry;
			this.state = state;
			this.message = message;
		}
		
	}
}
