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
package org.kablink.teaming.module.workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jbpm.JbpmContext;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.Duration;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.exe.Timer;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WfAcl;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.WorkflowHistory;
import org.kablink.teaming.domain.WorkflowResponse;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.extension.ExtensionCallback;
import org.kablink.teaming.extension.ZoneClassManager;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.workflow.impl.WorkflowFactory;
import org.kablink.teaming.module.workflow.jbpm.CalloutHelper;
import org.kablink.teaming.module.workflow.support.WorkflowCondition;
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.ObjectPropertyNotFoundException;
import org.kablink.util.GetterUtil;
import org.kablink.util.Validator;


public class WorkflowProcessUtils extends CommonDependencyInjection {
	protected static Log logger = LogFactory.getLog(WorkflowProcessUtils.class);
	protected static boolean debugEnabled=logger.isDebugEnabled();
	protected static BusinessCalendar businessCalendar = new BusinessCalendar();
	private static WorkflowProcessUtils instance; // A singleton instance
	private ZoneClassManager zoneClassManager;
	public WorkflowProcessUtils() {
		if(instance != null)
			throw new SingletonViolationException(WorkflowProcessUtils.class);
		
		instance = this;
	}
    public static WorkflowProcessUtils getInstance() {
    	return instance;
    }
	protected ZoneClassManager getZoneClassManager() {
		return zoneClassManager;
	}
	public void setZoneClassManager(ZoneClassManager zoneClassManager) {
		this.zoneClassManager = zoneClassManager;
	}
    public static WfNotify getNotification(Element notifyElement, WorkflowSupport wfEntry) {
    	List<Element> props;
    	String name, value;
    	WfNotify n = new WfNotify();
    	props = notifyElement.selectNodes("./properties/property");
 		if (props != null) {
	    	for (Element prop:props) {
	    		name = prop.attributeValue("name","");
	    		value = prop.attributeValue("value","");
	    		if ("subjText".equals(name)) {
		    		n.subject = value;
		    	} else if ("appendTitle".equals(name)) {
		    		n.appendTitle = GetterUtil.getBoolean(value, false);
		    	} else if ("bodyText".equals(name)) {
		    		n.body = value;
		    	} else if ("appendBody".equals(name)) {
		    		n.appendBody = GetterUtil.getBoolean(value, false);
		    	} 
	    	}
	 	}
    	n.toUsers = getUsers(props, wfEntry); 		
    	n.ccUsers = getUsers(notifyElement.selectNodes("./item[@name='ccNotifications']/properties/property"), wfEntry);
    	n.bccUsers = getUsers(notifyElement.selectNodes("./item[@name='bccNotifications']/properties/property"), wfEntry);

 	  	return n;    	
    }
    private static List<User> getUsers(List<Element> props, WorkflowSupport wfEntry) {
 		if ((props != null) && !props.isEmpty()) {
 	    	Set<Long>ids = new HashSet();
	    	DefinableEntity entity = (DefinableEntity)wfEntry;
	    	for (Element prop:props) {
	    		String name = prop.attributeValue("name","");
	    		String value = prop.attributeValue("value","");
	    		if ("entryCreator".equals(name) &&  GetterUtil.getBoolean(value, false)) {
	    			ids.add(wfEntry.getOwnerId());
	 	    	} else if ("team".equals(name) &&  GetterUtil.getBoolean(value, false)) {
	 	    		ids.addAll(entity.getParentBinder().getTeamMemberIds());
		    	} else if ("userGroupNotification".equals(name)) {
		    		ids.addAll(LongIdUtil.getIdsAsLongSet(value));
		    	} else if ("condition".equals(name)) {
		    		if (entity.getEntryDef() != null) {
		    			List<Element> userLists  = prop.selectNodes("./workflowEntryDataUserList[@definitionId='" +
		    					entity.getEntryDef().getId() + "']");
		    			if (userLists != null && !userLists.isEmpty()) {
		    				for (Element element:userLists) {
		    					String userListName = element.attributeValue("elementName"); //custom attribute name
		    					if (Validator.isNull(userListName)) continue;
		    					//elementName can have the element type pre-pended (e.g., user_list:attr_name)
		    					String listType = "user_list";
		    					if (userListName.indexOf(":") >= 0) {
		    						listType = userListName.substring(0, userListName.indexOf(":"));
		    						userListName = userListName.substring(userListName.indexOf(":")+1);
		    					}
		    					CustomAttribute attr = entity.getCustomAttribute(userListName); 
		    					if (attr != null) {
		    						//comma separated value
		    						if (listType.equals("user_list")) {
		    							ids.addAll(LongIdUtil.getIdsAsLongSet(attr.getValue().toString(), ","));
		    						}
		    					}
		    				}
		    			}
		    		}
		    	}
	    	}
	    	return getUsers(ids);
 		} else return null;

    }
 	public static void endWorkflow(WorkflowSupport wEntry, WorkflowState state, boolean deleteIt) {
		JbpmContext context=WorkflowFactory.getContext();
		try {
			Token current = context.loadToken(state.getTokenId().longValue());
			if (!current.isRoot()) {
				current.end(false);
			} else {
				current.getProcessInstance().end();
			}
			HistoryStamp endit = new HistoryStamp(RequestContextHolder.getRequestContext().getUser());
			// cleanup any children - should only have children if token is root
			Map children = current.getChildren();
			if (children != null) {
				for (Iterator iter=children.values().iterator();iter.hasNext();) {
					Token child = (Token)iter.next();
					WorkflowState w = wEntry.getWorkflowState(new Long(child.getId()));
					if (w != null) {
						WorkflowHistory history = new WorkflowHistory(w, endit, true);
						getInstance().getCoreDao().save(history);
						wEntry.removeWorkflowState(w);
					}
				}
			}
			//log end
			WorkflowHistory history = new WorkflowHistory(state, endit, true);
			getInstance().getCoreDao().save(history);
			wEntry.setWorkflowChange(endit);
			if (!current.isRoot()) {
				wEntry.removeWorkflowState(state);
				//check all other threads
				WorkflowProcessUtils.processConditions(wEntry, current);
			} else if (deleteIt) {
				wEntry.removeWorkflowState(state);
				context.getGraphSession().deleteProcessInstance(current.getProcessInstance());
			}

	    } finally {
	    	context.close();
	    }		
	
	}
    public static List getOnEntry(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {  	
			List items = (List)stateEle.selectNodes("./item[@name='onEntry']/item");
			return items;
		}
		return new ArrayList();

    }
    public static List getOnExit(Definition wfDef, String stateName) {
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {  	
			List items = (List)stateEle.selectNodes("./item[@name='onExit']/item");
			return items;
		}
		return new ArrayList();
    } 
 
    public static String getInitialState(Definition wfDef) {
		Document workflowDoc = wfDef.getDefinition();
		String initialState="";
		if (workflowDoc != null) {
			Element workflowRoot = workflowDoc.getRootElement();
			Element initialStateProperty = (Element) workflowRoot.selectSingleNode("./item[@name='workflowProcess']/properties/property[@name='initialState']");
			if (initialStateProperty != null) {
				initialState = initialStateProperty.attributeValue("value", "");
				//Validate that this is an existing state
				if (!Validator.isNull(initialState)) {
					Element state = DefinitionUtils.getItemByPropertyName(workflowRoot, "state", initialState);
					if (state == null) initialState = "";
				}
			}
			
			//See if the workflow definition actually defined an initial state
			if (Validator.isNull(initialState)) {
				//There is no defined initial state, so use the first state in the list
				initialStateProperty = (Element) workflowRoot.selectSingleNode("./item[@name='workflowProcess']/item[@name='state']/properties/property[@name='name']");
				
				if(initialStateProperty != null)
					initialState = initialStateProperty.attributeValue("value", "");
			}
		}
		return initialState;
    }
    public static boolean isThreadEndState(Definition wfDef, String stateName, String threadName) {
		Document wfDoc = wfDef.getDefinition();
		if (Validator.isNull(threadName)) {
			List ends = (List)wfDoc.getRootElement().selectNodes("./item[@name='workflowProcess']/properties/property[@name='endState' and @value='"+stateName+"']");
    		if ((ends == null) || ends.isEmpty()) return false;
    		return true;
    	} else {
    		Element threadEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "parallelThread", threadName);
    		if (threadEle != null) {
    			List endStates = DefinitionUtils.getPropertyValueList(threadEle, "endState");
    			for (int i = 0; i < endStates.size(); i++) {
    				if (stateName.equals((String)endStates.get(i))) return true;
    			}
			}
		}
		return false;
    }     

    /**
     * Get transitions triggered by a condition, ie) not manual
     * 
     * @param wfDef
     * @param stateName
     * @return Return the Dom elements
     */
    public static List getConditionElements(Definition wfDef, String stateName) {
    	List conditions=null;
		Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			//Build a list of all conditional transitions for this state
			conditions = stateEle.selectNodes("./item[@name='transitions']/item[@name!='transitionManual']");
		}
		if (conditions == null) conditions = new ArrayList();
		return conditions;
    }    
    public static Set<String> getQuestionNames(Definition wfDef, String stateName) {
    	Set<String> qNames = new HashSet();
    	Document wfDoc = wfDef.getDefinition();
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			//Build a list of all questions for this state
			List questions = stateEle.selectNodes("./item[@name='workflowQuestion']");
			if (questions != null) {
				for (int j = 0; j < questions.size(); j++) {
					String questionName = DefinitionUtils.getPropertyValue((Element)questions.get(j), "name");
					qNames.add(questionName);
				}
			}
		}
		return qNames;	
    }  
 	/**
	 * Process the conditions related to a state.  
	 * @param executionContext
	 * @param entry
	 * @param state
	 * @param isModify
	 * @return
	 */
	public static String processConditions(ExecutionContext executionContext, WorkflowSupport entry, WorkflowState state) {
		return processConditions(executionContext, entry, state, false, false);
	}

	/**
	 * Check conditions on tokens, skipping the current
	 * @param executionContext
	 * @param entry
	 * @param current
	 */
	public static void processConditions(WorkflowSupport entry, Token current) {
		processConditions(entry, current, false, false);
	}
	/**
	 * A condition check is triggered by either a modify or a reply
	 * @param entry
	 * @param isModify
	 * @param isReply
	 */
	public static boolean processConditions(WorkflowSupport entry, boolean isModify, boolean isReply) {
		return processConditions(entry, (Token)null, isModify, isReply);
	}
	public static void processManualTransition(WorkflowSupport entry, WorkflowState ws, String newState) {
		JbpmContext context=WorkflowFactory.getContext();
	    try {
	    	List manuals=null;
			//Find the current state in the definition
			Element stateEle = DefinitionUtils.getItemByPropertyName(ws.getDefinition().getDefinition().getRootElement(), "state", ws.getState());
			if (stateEle != null) {
				//Build a list of all conditional transitions for this state
				manuals = stateEle.selectNodes("./item[@name='transitions']/item[@name='transitionManual']");
			}
			if (manuals != null) {
				for (int i=0; i<manuals.size(); ++i) {
					Element transition = (Element)manuals.get(i);
					String toState = DefinitionUtils.getPropertyValue(transition, "toState");
					if (!Validator.isNull(toState)) {
						if (toState.equals(newState)) {
							Token t = context.loadTokenForUpdate(ws.getTokenId().longValue());
							ExecutionContext ctx = new ExecutionContext(t);
							setVariables(transition, ctx, entry, ws);
							if (debugEnabled) logger.debug("Take manual transition " + ws.getState() + "." + toState);
							ctx.leaveNode(ws.getState() + "." + toState);
							context.save(t);						
							//	see if other nodes need to transition
							processConditions(entry, t);
							break;
						}
					}
				}
			}
        	
	    } finally {
	    	context.close();
	    }
	    
	}	
	/**
	 * Set a variable for the process instance.  Variables are set on 
	 * the root token and therefore available to all child tokens.
	 * 
	 * @param item
	 * @param executionContext
	 * @param entry
	 * @param currentWs
	 */
	public static boolean setVariables(Element item, ExecutionContext executionContext, WorkflowSupport entry, WorkflowState currentWs) {
		List variables = item.selectNodes("./item[@name='variable']");
		if ((variables == null) || variables.isEmpty()) return false;
		for (int i=0; i<variables.size(); ++i) {
			Element variableEle = (Element)variables.get(i);
			setVariable(variableEle, executionContext, entry, currentWs);
		}
		return true;

	}	
	public static boolean setVariable(Element variableEle, ExecutionContext executionContext, WorkflowSupport entry, WorkflowState currentWs) {
		String name = DefinitionUtils.getPropertyValue(variableEle, "name");
		if (name == null) return false;
		String value = DefinitionUtils.getPropertyValue(variableEle, "value");

		ContextInstance cI = executionContext.getContextInstance();
		cI.setVariable(name, value);
		if (debugEnabled) logger.debug("Set variable " + name + "=" + value);
		return true;

	}	
	//check for conditions when don't have a running execution context.  Called after change to entry.
	private static boolean processConditions(WorkflowSupport entry, Token current, boolean isModify, boolean isReply) {

		boolean found = true;
		boolean stateChange=false;
		//loop until we get through states without any changes occuring.  Each change could trigger another
		JbpmContext context=WorkflowFactory.getContext();
		try {
			//not sure if it is necessary to run through the states multiple times
			while (found) {
				//assume no conditions will be met
				found = false;
				//	copy set because may change as we process each state
				Set states = new HashSet(entry.getWorkflowStates());
	
				for (Iterator iter=states.iterator(); iter.hasNext(); ) {
					WorkflowState ws = (WorkflowState)iter.next();
					if ((current != null) && (ws.getTokenId().longValue() == current.getId())) continue;
					Token t = context.loadTokenForUpdate(ws.getTokenId().longValue());
					//	make sure state hasn't been removed as the result of another thread
					if (t.hasEnded() || (ws.getOwner() == null)) continue;
					ExecutionContext ctx = new ExecutionContext(t);
					String toState =WorkflowProcessUtils.processConditions(ctx, entry, ws, isModify, isReply); 
					if (Validator.isNotNull(toState)) {
						ctx.leaveNode(ws.getState() + "." + toState);
						context.save(t);
						stateChange=true;
						found = true;
					}
				}
				//don't trigger onModify conditions after the first time through
				isModify = false;
				isReply = false;
			}
		} finally {
			context.close();
		}
		return stateChange;
	}
	/**
	 * Look for the first condition that is met and return the state to transition to.  If
	 * no condition is fully satisfied, set timeouts if requested.   
	 * @param executionContext
	 * @param entry
	 * @param state
	 * @param isModify
	 * @param isReply
	 * @return
	 */
	private static String processConditions(final ExecutionContext executionContext, final WorkflowSupport entry, final WorkflowState state, 
			boolean isModify, boolean isReply) {
		//First, make sure this entry is not pre-deleted. If so, don't do anything.
		if (entry instanceof FolderEntry && ((FolderEntry)entry).isPreDeleted()) return null;
		
		List conditions = getConditionElements(state.getDefinition(), state.getState());
//		Date currentDate = new Date();
		Date minDate = new Date(0);
		GregorianCalendar currentCal = new GregorianCalendar();
		if (state.getWorkflowChange() == null) state.setWorkflowChange(new HistoryStamp(RequestContextHolder.getRequestContext().getUser(), currentCal.getTime()));
		boolean debug = true;
		for (int i=0; i<conditions.size(); ++i) {
			Element condition = (Element)conditions.get(i);
			//any modify triggers this
			String toState = DefinitionUtils.getPropertyValue(condition, "toState");
			if (!Validator.isNull(toState)) {
				String type = condition.attributeValue("name", "");
				if (type.equals("transitionOnModify")) {
					if (isModify) {
						setVariables(condition, executionContext, entry, state);
						if (debugEnabled) logger.debug("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
						return toState;
					}
					
				} else if (type.equals("transitionOnReply")) {
					if (isReply) {
						setVariables(condition, executionContext, entry, state);
						if (debugEnabled) logger.debug("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
						return toState;
					}
				} else if (type.equals("transitionOnResponse")) {
					String question = DefinitionUtils.getPropertyValue(condition, "question");
					String response = DefinitionUtils.getPropertyValue(condition, "response");
					if (!Validator.isNull(question) && !Validator.isNull(response)) {
						Set responses = entry.getWorkflowResponses();
						for (Iterator iter=responses.iterator(); iter.hasNext(); ) {
							WorkflowResponse wr = (WorkflowResponse)iter.next();
							if (state.getDefinition().getId().equals(wr.getDefinitionId()) &&
									question.equals(wr.getName()) && response.equals(wr.getResponse())) {
								setVariables(condition, executionContext, entry, state);
								if (debugEnabled) logger.debug("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
								return toState;
							}
						}
					}
				} else if (type.equals("transitionOnEntryData")) {
					Object currentVal=null;
					DefinableEntity dEntry = null;
					if (entry instanceof DefinableEntity) dEntry = (DefinableEntity)entry;
					boolean allMatch = GetterUtil.getBoolean(DefinitionUtils.getPropertyValue(condition, "allMustMatch"));
					List entryConditions = condition.selectNodes(".//workflowCondition");
					if (entryConditions == null) continue;
					boolean currentMatch = true;
					for (int j=0; j<entryConditions.size(); ++j) {
						currentMatch=true;
						Element eCondition = (Element)entryConditions.get(j);
						String defId = eCondition.attributeValue("definitionId", "");
						if (!Validator.isNull(defId)) {
							if (dEntry == null) currentMatch = false;
							else if (!defId.equals(dEntry.getEntryDef().getId())) {
								currentMatch = false;
							}
						}
						String cName = eCondition.attributeValue("elementName", "");
						String operation = eCondition.attributeValue("operation", "");
						//if elementName or operation are null we will treat the condition as a match
						if (currentMatch && !Validator.isNull(cName) && !Validator.isNull(operation)) {
							Element vEle = (Element)eCondition.selectSingleNode("./value");
							String value = null;
							if (vEle != null) value=vEle.getText();
							
							try {
								currentVal = InvokeUtil.invokeGetter(entry, cName);
							} catch (ObjectPropertyNotFoundException pe) {
								if (dEntry != null) {
									CustomAttribute attr = dEntry.getCustomAttribute(cName);
									if (attr != null) currentVal = attr.getValue();
								}
							}
							if (currentVal == null) {
								if (!Validator.isNull(value)) currentMatch = false;
								else if (!operation.equals("equals")) currentMatch = false;
							} else {
								if ("equals".equals(operation)) {
									if (currentVal instanceof Collection) {
										Collection c = collectionToStrings((Collection)currentVal);
										if ((c.size() != 1) || !c.contains(value)) currentMatch=false;										
									} else {
										if (!currentVal.toString().equals(value)) currentMatch=false;
									}
								} else if ("checked".equals(operation)) {
									if (currentVal.toString().equals("false")) currentMatch=false;									
								} else if ("checkedNot".equals(operation)) {
									if (currentVal.toString().equals("true")) currentMatch=false;
								} else if ("datePassed".equals(operation)) {
									if (!passedDate(eCondition, currentVal, currentCal, minDate)) currentMatch = false;
								} else if ("beforeDate".equals(operation)) {
									if (!beforeDate(eCondition, currentVal, currentCal, minDate)) currentMatch = false;
								} else if ("afterDate".equals(operation)) {
									if (!afterDate(eCondition, currentVal, currentCal, minDate)) currentMatch = false;

								} else if (currentVal instanceof Event) {
									Event e = (Event)currentVal;
									if (e.getFrequency() == Event.NO_RECURRENCE) {
										if ("beforeStart".equals(operation)) {
											if (!beforeDate(eCondition, e.getDtStart().getTime(), currentCal, minDate)) currentMatch = false;									
										} else if ("afterStart".equals(operation)) {
											if (!afterDate(eCondition, e.getDtStart().getTime(), currentCal, minDate)) currentMatch = false;							
										} else if ("started".equals(operation)) {
											if (!passedDate(eCondition, e.getDtStart().getTime(), currentCal, minDate)) currentMatch = false;
										} else if ("ended".equals(operation)) {
											if (!passedDate(eCondition, e.getDtEnd().getTime(), currentCal, minDate)) currentMatch = false;
										} else if ("afterEnd".equals(operation)) {
											if (!afterDate(eCondition, e.getDtEnd().getTime(), currentCal, minDate)) currentMatch = false;							
										} else if ("beforeEnd".equals(operation)) {
											if (!beforeDate(eCondition, e.getDtEnd().getTime(), currentCal, minDate)) currentMatch = false;									
										}
									} else {
										//on repeating events, get the next recurrence since we entered the state
										Calendar candidate = new GregorianCalendar();
										candidate.setTime(state.getWorkflowChange().getDate());
										Calendar next = e.getCandidateStartTime(candidate, true, true);
										if (next == null) next = e.getCandidateStartTime(candidate, false, true);
										if (next == null) currentMatch = false;
										else {
											if (debugEnabled) logger.debug("Candidate:" + candidate.getTime().toString() + " Next:" + next.getTime().toString());
											if ("beforeStart".equals(operation)) {
												if (!beforeDate(eCondition, next.getTime(), currentCal, minDate)) currentMatch = false;									
											} else if ("afterStart".equals(operation)) {
												if (!afterDate(eCondition, next.getTime(), currentCal, minDate)) currentMatch = false;							
											} else if ("started".equals(operation)) {
												if (!passedDate(eCondition, next.getTime(), currentCal, minDate)) currentMatch = false;
											} else if ("ended".equals(operation)) {
												if (!passedDate(eCondition, new Date(next.getTime().getTime()+e.getDuration().getInterval()), currentCal, minDate)) currentMatch = false;
											} else if ("afterEnd".equals(operation)) {
												if (!afterDate(eCondition, new Date(next.getTime().getTime()+e.getDuration().getInterval()), currentCal, minDate)) currentMatch = false;							
											} else if ("beforeEnd".equals(operation)) {
												if (!beforeDate(eCondition, new Date(next.getTime().getTime()+e.getDuration().getInterval()), currentCal, minDate)) currentMatch = false;									
											}
										}
											
									}
								} else currentMatch=false;

							}
								
							
						}
						//check if this was an and condition
						if ((currentMatch == false) && (allMatch == true)) break;
						//check if this was an or and we are done
						if ((currentMatch == true) && (allMatch == false)) break;
					}
					//either they all matched, or the this is an or condition and last one matched
					if (currentMatch == true) {
						setVariables(condition, executionContext, entry, state);
						if (debugEnabled) logger.debug("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
						return toState;
					}
						
				} else if (type.equals("waitForParallelThread")) {
					//	get names of threads we are waiting for
					List threads = DefinitionUtils.getPropertyValueList(condition, "name");
					boolean done = true;
					for (int j=0; j<threads.size(); ++j) {
						String threadName = (String)threads.get(j);
						if (!Validator.isNull(threadName)) {
							//See if child has ended
							WorkflowState child = entry.getWorkflowStateByThread(state.getDefinition(), threadName);
							//if found - still running
							if (child != null) {
								done = false;
								break;
							}
						}
					}
					if (done) {
						setVariables(condition, executionContext, entry, state);
						if (debugEnabled) logger.debug("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
						return toState;
					}
				} else if (type.equals("transitionOnVariable")) {
					String name = DefinitionUtils.getPropertyValue(condition, "name");
					if (!Validator.isNull(name)) {
						String value = DefinitionUtils.getPropertyValue(condition, "value");
						Object currentVal = executionContext.getVariable(name);
						boolean areEqual = false;
						if (Validator.isNull(value)) {
							if (currentVal == null || Validator.isNull(currentVal.toString())) areEqual=true;
						} else if (currentVal != null) {
							if (value.equals(currentVal.toString())) areEqual = true;
						}
						if (areEqual) {
							setVariables(condition, executionContext, entry, state);
							if (debugEnabled) logger.debug("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
							return toState;

						}
					}
				} else if (type.equals("transitionOnCondition")) {
					String conditionName = DefinitionUtils.getPropertyValue(condition, "class");
					if (Validator.isNotNull(conditionName)) {
						try {
							Boolean result = (Boolean)getInstance().getZoneClassManager().execute(new ExtensionCallback() {
								public Object execute(Object action) {
									WorkflowCondition job = (WorkflowCondition)action;
									job.setHelper(new CalloutHelper(executionContext));
									return Boolean.valueOf(job.execute(entry, state));						
								}
							}, conditionName);
							if (Boolean.TRUE.equals(result)) return toState;
						} catch (ClassNotFoundException e) {
							logger.error("Invalid Workflow Transition/Condition class name '" + conditionName + "'");
							throw new ConfigurationException("Invalid Workflow Action class name '" + conditionName + "'",
									e);
						} 
					}
				} else if (type.equals("transitionImmediate")) {
					setVariables(condition, executionContext, entry, state);
					if (debugEnabled) logger.debug("Take auto transition " + state.getState() + "." + toState);
					return toState;					
				}

			}
					
		}
		//if Time is null, didn't have timeout to process
		if (minDate.getTime() == 0) return null;
	   	Long timerId = state.getTimerId();
	   	Timer timer = null;
	   	if (timerId != null) {
    		try {
    			timer = (Timer)executionContext.getJbpmContext().getSession().load(Timer.class, timerId);
	    		if (minDate.getTime() != timer.getDueDate().getTime()) {
	    			timer.setDueDate(minDate);
	    		}
    		} catch (Exception ex) {};
    	} else {
    		timer = new Timer(executionContext.getToken());
    		timer.setDueDate(minDate);
    		timer.setName("onDataValue");
    		executionContext.getJbpmContext().getSession().save(timer);
    		state.setTimerId(timer.getId());
    		timer.setAction(executionContext.getProcessDefinition().getAction("timerAction"));
    	}
    	if (debugEnabled && timer != null) logger.debug("Timer set for " + timer.getDueDate().toString() +" at state:" + state.getState());
    	return null;
	}
	
	private static boolean passedDate(Element condition, Object currentVal, Calendar currentCal, Date minDate) {
		if (currentVal instanceof Date) {
			Date c = (Date)currentVal;
			//if already passed, don't need to update minDate
			if (!currentCal.getTime().before(c)) return true;
			updateMinimum(minDate,c);
			return false;
		} 
		return false;
	}
	private static boolean beforeDate(Element condition, Object currentVal, Calendar currentCal, Date minDate) {
		if (currentVal instanceof Date) {
			Date c = (Date)currentVal;
			//if already passed, don't need to update minDate
			Date cDate = adjustDate(condition, c, false);
			if (currentCal.getTime().after(cDate)) return true;
			updateMinimum(minDate,cDate);
			return false;
		} 
		return false;
	}
	private static boolean afterDate(Element condition, Object currentVal, Calendar currentCal, Date minDate) {
		if (currentVal instanceof Date) {
			Date c = (Date)currentVal;
			//if already passed, don't need to update minDate
			Date cDate = adjustDate(condition, c, true);
			if (currentCal.getTime().after(cDate)) return true;
			updateMinimum(minDate,cDate);
			return false;
		} 
		return false;
	}
	private static Date adjustDate(Element condition, Date current, boolean forward) {
		String duration = condition.attributeValue("duration", "").trim();
		String type = condition.attributeValue("durationType", "").trim();
		Duration d;
		//skip bad values
		try {
			if (forward)
				d = new Duration(duration + " " + type);
			else
				d = new Duration("-" + duration + " " + type);
			Date result = businessCalendar.add(current, d);
			return result;
		} catch (Exception ex) {};
		return new Date(current.getTime());
	}
	private static void updateMinimum(Date min, Date c) {
		if (min.getTime() == 0) min.setTime(c.getTime());
		else if (min.after(c)) min.setTime(c.getTime());
	}

	private static Collection collectionToStrings(Collection currentVal) {
		Set c = new HashSet();
		for (Iterator iter=currentVal.iterator(); iter.hasNext();) {
			Object o = iter.next();
			c.add(o.toString());
		}
		return c;
	}
	public static WfAcl getStateAcl(Definition wfDef, DefinableEntity entity, String stateName, WfAcl.AccessType type) {
		Document wfDoc = wfDef.getDefinition();
		WfAcl acl = null;
		//Find the current state in the definition
		Element stateEle = DefinitionUtils.getItemByPropertyName(wfDoc.getRootElement(), "state", stateName);
		if (stateEle != null) {
			String nodeString=null;
			if (WfAcl.AccessType.read.equals(type)) {
				nodeString = "item[@name='readAccess']";
			} else if (WfAcl.AccessType.write.equals(type)) {  
				nodeString = "item[@name='modifyAccess']";
			} else if (WfAcl.AccessType.modifyField.equals(type)) {  
				nodeString = "item[@name='modifyFieldAccess']";
			} else if (WfAcl.AccessType.delete.equals(type)) {
				nodeString = "item[@name='deleteAccess']";
			} else if (WfAcl.AccessType.transitionOut.equals(type)) {
				nodeString = "item[@name='transitionOutAccess']";
			} else if (WfAcl.AccessType.transitionIn.equals(type)) {
				nodeString = "item[@name='transitionInAccess']";
			} 
			if (nodeString != null) {
				acl = getAcl((Element)stateEle.selectSingleNode("./item[@name='accessControls']/" + nodeString), entity, type);
			}
			if (acl == null) {
				//check global settings
				acl = getAcl((Element)wfDoc.getRootElement().selectSingleNode("./item[@name='workflowProcess']/item[@name='accessControls']/" + nodeString), entity, type);
			}
			if (acl != null) return acl;
		} 
		acl = new WfAcl(type);
		acl.setUseDefault(true);
		return acl;
	}

	private static WfAcl getAcl(Element aclElement, DefinableEntity entity, WfAcl.AccessType type) {
		if (aclElement == null) return null;
		List<Element>props = aclElement.selectNodes("./properties/property");
		String name, value;
		if ((props == null) || props.isEmpty()) return null;
		WfAcl result = new WfAcl(type);
		for (Element prop:props) {
			name = prop.attributeValue("name","");
			value = prop.attributeValue("value","");
			if ("folderDefault".equals(name)) {
				result.setUseDefault(GetterUtil.getBoolean(value, false));
			} else if ("userGroupAccess".equals(name)) {
				result.addPrincipalIds(LongIdUtil.getIdsAsLongSet(value));
			} else if ("team".equals(name) &&  GetterUtil.getBoolean(value, false)) {
				result.addPrincipalId(ObjectKeys.TEAM_MEMBER_ID);
			} else if ("condition".equals(name)) {
				if (entity.getEntryDef() != null) {
			        User user = RequestContextHolder.getRequestContext().getUser();
					List<Element> userLists  = prop.selectNodes("./workflowEntryDataUserList[@definitionId='" +
							entity.getEntryDef().getId() + "']");
					if (userLists != null && !userLists.isEmpty()) {
						for (Element element:userLists) {
							String userListName = element.attributeValue("elementName"); //custom attribute name
							if (Validator.isNull(userListName)) continue;
	    					//elementName can have the element type pre-pended (e.g., user_list:attr_name)
	    					String listType = "user_list";
	    					if (userListName.indexOf(":") >= 0) {
	    						listType = userListName.substring(0, userListName.indexOf(":")).trim();
	    						userListName = userListName.substring(userListName.indexOf(":")+1);
	    					}
							CustomAttribute attr = entity.getCustomAttribute(userListName); 
							if (attr != null) {
								//comma separated value
								if (listType.equals("user_list") || listType.equals("group_list")) {
									result.addPrincipalIds(LongIdUtil.getIdsAsLongSet(attr.getValue().toString(), ","));
								} else if(listType.equals("team_list")) {
									//The team list is a list of binder ids. We have to get the teams from each binder
									Set<Long> binderIds = LongIdUtil.getIdsAsLongSet(attr.getValue().toString(), ",");
									if (binderIds != null) {
										for (Long binderId : binderIds) {
											Binder binder =  getInstance().getCoreDao().loadBinder(binderId, user.getZoneId());
											result.addPrincipalIds(binder.getTeamMemberIds());
										}
									}
								}
							}
						}
					}
				}
			} else if ("entryCreator".equals(name) && GetterUtil.getBoolean(value, false)) {
				//	add special owner to allow list
				result.addPrincipalId(ObjectKeys.OWNER_USER_ID);
			}
		}
		//see if nothing was actually set
		if (result.getPrincipalIds().isEmpty() && !result.isUseDefault()) return null;
		return result;
	}

	protected static List<User> getUsers(final Set<Long>ids) {
		Set userIds = getInstance().getProfileDao().explodeGroups(ids, RequestContextHolder.getRequestContext().getZoneId());
		return getInstance().getProfileDao().loadUsers(userIds, RequestContextHolder.getRequestContext().getZoneId());
	}
	public static Long getRunAsUser(Element item, WorkflowSupport wfEntry, WorkflowState currentWs) {
		String ctxType = DefinitionUtils.getPropertyValue(item, "runAs");
		//currentuser exists, so process first
		if ("currentuser".equals(ctxType)) {
			return RequestContextHolder.getRequestContext().getUserId();
		}
		Long runAsId=null;
		Set ids = new HashSet();
		if ("entryowner".equals(ctxType)) {
			runAsId = wfEntry.getOwnerId();
			ids.add(runAsId);
			if (!getUsers(ids).isEmpty()) return runAsId;
		} 
		//default to binderowner
		runAsId = currentWs.getOwner().getEntity().getParentBinder().getOwnerId();			
		ids.clear();
		ids.add(runAsId);
		if (!getUsers(ids).isEmpty()) return runAsId;
		//binder owner must be deleted; try entry owner
		//don't want to run as current user if it is backgroundProcessor; since gets all privs
		runAsId = wfEntry.getOwnerId();
		ids.add(runAsId);
		if (!getUsers(ids).isEmpty()) return runAsId;
		return null;

	}
	protected static class WfNotify {
		private String subject="";
		private String body="";
		private boolean appendTitle=false;
		private boolean appendBody=false;
		private Collection<User> toUsers;
		private Collection<User> ccUsers;
		private Collection<User> bccUsers;
		
		public String getSubject() {
			return subject;
		}
		public String getBody() {
			return body;
		}
		public boolean isAppendTitle() {
			return appendTitle;
		}
		public boolean isAppendBody() {
			return appendBody;
		}
		public Collection<User> getToUsers() {
			return toUsers;
		}
		public Collection<User> getCCUsers() {
			return ccUsers;
		}
		public Collection<User> getBCCUsers() {
			return bccUsers;
		}

	}

}
