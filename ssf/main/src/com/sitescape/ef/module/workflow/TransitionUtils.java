package com.sitescape.ef.module.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.calendar.BusinessCalendar;
import org.jbpm.calendar.Duration;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.scheduler.exe.Timer;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.module.workflow.impl.JbpmContext;
import com.sitescape.ef.module.workflow.impl.WorkflowFactory;
import com.sitescape.ef.util.InvokeUtil;
import com.sitescape.ef.util.ObjectPropertyNotFoundException;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;
import com.sitescape.ef.module.definition.DefinitionUtils;

public class TransitionUtils {
	protected static Log logger = LogFactory.getLog(TransitionUtils.class);
	protected static boolean infoEnabled=logger.isInfoEnabled();
	protected static boolean debugEnabled=logger.isDebugEnabled();
	protected static BusinessCalendar businessCalendar = new BusinessCalendar();

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
	public static void processConditions(WorkflowSupport entry, boolean isModify, boolean isReply) {
		processConditions(entry, (Token)null, isModify, isReply);
	}
	public static void processManualTransition(WorkflowSupport entry, WorkflowState ws, String newState) {
		JbpmContext context=WorkflowFactory.getContext();
	    try {
	    	List manuals = WorkflowUtils.getManualElements(ws.getDefinition(), ws.getState());
			for (int i=0; i<manuals.size(); ++i) {
				Element transition = (Element)manuals.get(i);
				String toState = DefinitionUtils.getPropertyValue(transition, "toState");
				if (!Validator.isNull(toState)) {
					if (toState.equals(newState)) {
						Token t = context.loadTokenForUpdate(ws.getTokenId().longValue());
			        	ExecutionContext ctx = new ExecutionContext(t);
			        	setVariables(transition, ctx, entry, ws);
						if (infoEnabled) logger.info("Take manual transition " + ws.getState() + "." + toState);
			            ctx.leaveNode(ws.getState() + "." + toState);
			            context.save(t);						
			            //see if other nodes need to transition
			            processConditions(entry, t);
			    	    break;
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
			String name = DefinitionUtils.getPropertyValue(variableEle, "name");
			if (name == null) continue;
			String value = DefinitionUtils.getPropertyValue(variableEle, "value");

			ContextInstance cI = executionContext.getContextInstance();
			cI.setVariable(name, value);
			if (infoEnabled) logger.info("Set variable " + name + "=" + value);
		}
		return true;

	}	
	private static void processConditions(WorkflowSupport entry, Token current, boolean isModify, boolean isReply) {

		boolean found = true;
		//loop until we get through states without any changes occuring.  Each change could trigger another
		JbpmContext context=WorkflowFactory.getContext();
//JbpmContext context = executionContext.getJbpmContext();
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
					String toState =TransitionUtils.processConditions(ctx, entry, ws, isModify, isReply); 
					if (toState != null) {
						ctx.leaveNode(ws.getState() + "." + toState);
						context.save(t);
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
	}
	/**
	 * Look for the first condition that is met and return the state to transition to.  If
	 * no condition is fully satisfied, return the earlies timeout we should wait for.  If neither, return null;   
	 * @param executionContext
	 * @param entry
	 * @param state
	 * @param isModify
	 * @param isReply
	 * @return
	 */
	private static String processConditions(ExecutionContext executionContext, WorkflowSupport entry, WorkflowState state, 
			boolean isModify, boolean isReply) {
		List conditions = WorkflowUtils.getConditionElements(state.getDefinition(), state.getState());
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
						if (infoEnabled) logger.info("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
						return toState;
					}
					
				} else if (type.equals("transitionOnReply")) {
					if (isReply) {
						setVariables(condition, executionContext, entry, state);
						if (infoEnabled) logger.info("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
						return toState;
					}
				} else if (type.equals("transitionOnResponse")) {
					if (false) {
						setVariables(condition, executionContext, entry, state);
						if (infoEnabled) logger.info("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
						return toState;
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
											if (debugEnabled) logger.info("Candidate:" + candidate.getTime().toString() + " Next:" + next.getTime().toString());
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
						if (infoEnabled) logger.info("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
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
						if (infoEnabled) logger.info("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
						return toState;
					}
				} if (type.equals("transitionOnVariable")) {
					String name = DefinitionUtils.getPropertyValue(condition, "name");
					if (!Validator.isNull(name)) {
						String value = DefinitionUtils.getPropertyValue(condition, "value");
						Object currentVal = executionContext.getVariable(name);
						if (((currentVal != null) && currentVal.equals(value)) ||
								(currentVal == value)) {
							setVariables(condition, executionContext, entry, state);
							if (infoEnabled) logger.info("Take conditional transition(" + type + ") " + state.getState() + "." + toState);
							return toState;

						}
					}
				}
			}
					
		}
		//if Time is null, didn't have timeout to process
		if (minDate.getTime() == 0) return null;
	   	Long timerId = state.getTimerId();
	   	Timer timer = null;
	   	if (timerId != null) {
    		try {
	    		timer = (Timer)WorkflowFactory.getSession().getSession().load(Timer.class, timerId);
	    		if (minDate.getTime() != timer.getDueDate().getTime()) {
	    			timer.setDueDate(minDate);
	    		}
    		} catch (Exception ex) {};
    	} else {
    		timer = new Timer(executionContext.getToken());
    		timer.setDueDate(minDate);
    		timer.setName("onDataValue");
    		WorkflowFactory.getSession().getSession().save(timer);
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
		} else if (currentVal instanceof Event) {
			
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
		} else if (currentVal instanceof Event) {
			
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
		} else if (currentVal instanceof Event) {
			
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

}
