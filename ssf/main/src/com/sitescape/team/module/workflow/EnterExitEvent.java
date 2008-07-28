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
package com.sitescape.team.module.workflow;
/**
 * Handle setting variables, starting/stoping threads and recording the state when
 * a new node is entered or cancelling timers when a node is exitted.
 * This is done as part on one action so we can maintain the ordering
 * specified in the definition and reduce the amount of synchronization needed between the
 * JBPM definition and the Sitescape definition.
 * @author Janet McCann
 *
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.dom4j.Element;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WfNotify;
import com.sitescape.team.domain.NoPrincipalByTheIdException;
import com.sitescape.team.domain.WorkflowResponse;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.WorkflowSupport;
import com.sitescape.team.jobs.WorkflowProcess;
import com.sitescape.team.modelprocessor.ProcessorManager;
import com.sitescape.team.module.binder.processor.EntryProcessor;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.workflow.jbpm.CalloutHelper;
import com.sitescape.team.module.workflow.support.WorkflowAction;
import com.sitescape.team.module.workflow.support.WorkflowScheduledAction;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Validator;

public class EnterExitEvent extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	  
	//Indexing the entry is handled by the code that initiates a transition/nodeEnter/nodeExit
	//Because mutiple states can be effected, we don't want to re-index
	//each time.  Only need one at the end of the transaction

	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		String state = token.getNode().getName();
		WorkflowSupport entry = loadEntry(ctx);
		WorkflowState ws = entry.getWorkflowState(id);
		
		if (debugEnabled) logger.debug("Workflow event (" + executionContext.getEvent().getEventType() + ")");
		if (ws != null) {
			List items;
			if (Event.EVENTTYPE_NODE_ENTER.equals(executionContext.getEvent().getEventType())) {
				if (!state.equals(executionContext.getContextInstance().getTransientVariable(WorkflowModule.FORCE_STATE))) {
					Date current = new Date();
					HistoryStamp stamp = new HistoryStamp(RequestContextHolder.getRequestContext().getUser(), current);
					if (entry.getWorkflowChange() == null) {
						entry.setWorkflowChange(stamp);
					} else if ((entry.getWorkflowChange().getDate() != null) && current.after(entry.getWorkflowChange().getDate())) {
						entry.setWorkflowChange(stamp);
					}
					//	record when we enter the state
					ws.setWorkflowChange(stamp);
				}
				ws.setState(state);
				entry.setStateChange(ws);
				if (debugEnabled) logger.debug("Workflow event (" + executionContext.getEvent().getEventType() + ") recorded: " + state);
				//remove old responses associated with this state
				Set names = TransitionUtils.getQuestionNames(ws.getDefinition(), ws.getState());
				if (!names.isEmpty()) {
					//now see if response to this question from this user exists
					Set<WorkflowResponse> responses = new HashSet<WorkflowResponse>(entry.getWorkflowResponses());
					for (WorkflowResponse wr:responses) {
						if (ws.getDefinition().getId().equals(wr.getDefinitionId())) {
							String name = wr.getName();
							//if question is defined here, clear any old answers
							if (names.contains(name)) entry.removeWorkflowResponse(wr);
						}			
					}

				}
				items  = TransitionUtils.getOnEntry(ws.getDefinition(), state);
			} else {
				//cancel timers associated with this state.
				executionContext.getJbpmContext().getSchedulerSession().cancelTimersByName("onDataValue", token);
				items  = TransitionUtils.getOnExit(ws.getDefinition(), state);				
			}
			boolean check = false;
			for (int i=0; i<items.size(); ++i) {
				Element item = (Element)items.get(i);
	   			String name = item.attributeValue("name","");
	   			if ("variable".equals(name)) {
	   				if (TransitionUtils.setVariable(item, executionContext, entry, ws)) {
	   					check = true;
	   				}
	   			} else if ("moveEntry".equals(name)) {
	   				moveEntry(item, executionContext, entry, ws);
	   				
	   			} else if ("copyEntry".equals(name)) {
	   				copyEntry(item, executionContext, entry, ws);
	   				
	   			} else if ("startParallelThread".equals(name)) {
	   				startThread(item, executionContext, entry, ws);
	   			} else if ("stopParallelThread".equals(name)) {
	   				if (stopThread(item, executionContext, entry, ws)) check = true;
	   			} else if ("workflowAction".equals(name)) {
	   				startProcess(item, executionContext, entry, ws);
	   			} else if ("workflowRemoteApp".equals(name)) {
	   				startRemoteApp(item, executionContext, entry, ws);
	   			}
	   		}
			if (Event.EVENTTYPE_NODE_LEAVE.equals(executionContext.getEvent().getEventType())) {
				//leaving a state - logit
				getReportModule().addWorkflowStateHistory(ws, new HistoryStamp(RequestContextHolder.getRequestContext().getUser()), false);
			}
			//do notifications last incase the entry has moved.  This will make the link valid; at least until the next move
			doNotifications(executionContext, entry, ws);
			//See if other threads conditions are now met.
			if (check) TransitionUtils.processConditions(entry, token);
		}
	}
	protected void startProcess(Element action, ExecutionContext executionContext, WorkflowSupport wfEntry, WorkflowState currentWs) {
		String actionName = DefinitionUtils.getPropertyValue(action, "class");
		if (Validator.isNull(actionName)) return;
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		try {
			Class actionClass = ReflectHelper.classForName(actionName);
			Object job = (Object)actionClass.newInstance();
			String ctxType = DefinitionUtils.getPropertyValue(action, "runAs");
			if ("entryowner".equals(ctxType)) {
				RequestContextUtil.setThreadContext(currentWs.getZoneId(), wfEntry.getOwnerId());
			} else if ("binderowner".equals(ctxType)) {
				RequestContextUtil.setThreadContext(currentWs.getZoneId(), currentWs.getOwner().getEntity().getParentBinder().getOwnerId());						
			}
			if (job instanceof WorkflowScheduledAction) {
				WorkflowProcess schedJob = (WorkflowProcess)SZoneConfig.getObject(RequestContextHolder.getRequestContext().getZoneName(), 
							"workflowConfiguration/property[@name='" + WorkflowProcess.PROCESS_JOB + "']", com.sitescape.team.jobs.DefaultWorkflowProcess.class);
				String secsString = (String)SZoneConfig.getString(RequestContextHolder.getRequestContext().getZoneName(), "workflowConfiguration/property[@name='" + WorkflowProcess.PROCESS_SECONDS + "']");
				int seconds = 300;
				try {
					seconds = Integer.parseInt(secsString);
				} catch (Exception ex) {};
				schedJob.schedule(wfEntry, currentWs, actionName, buildParams(wfEntry, currentWs), seconds);
					
			} else {
				((WorkflowAction)job).setHelper(new CalloutHelper(executionContext));
				((WorkflowAction)job).execute(wfEntry, currentWs);
			}
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(
					"Invalid Workflow Action class name '" + actionName + "'",
					e);
		} catch (InstantiationException e) {
			throw new ConfigurationException(
						"Cannot instantiate Workflow Action of type '"
						+ actionName + "'");
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(
						"Cannot instantiate Workflow Action of type '"
						+ actionName + "'");
		} finally {
			RequestContextHolder.setRequestContext(oldCtx);
		}
	}
		
	
	protected void startRemoteApp(Element action, ExecutionContext executionContext, WorkflowSupport wfEntry, WorkflowState currentWs) {
		String application = DefinitionUtils.getPropertyValue(action, "remoteApp");
		if (Validator.isNull(application)) return;
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		try {
			Application app = getProfileDao().loadApplication(Long.valueOf(application), RequestContextHolder.getRequestContext().getZoneId());
			String ctxType = DefinitionUtils.getPropertyValue(action, "runAs");
			if ("entryowner".equals(ctxType)) {
				RequestContextUtil.setThreadContext(currentWs.getZoneId(), wfEntry.getOwnerId());
			} else if ("binderowner".equals(ctxType)) {
				RequestContextUtil.setThreadContext(currentWs.getZoneId(), currentWs.getOwner().getEntity().getParentBinder().getOwnerId());						
			}
			WorkflowProcess schedJob = (WorkflowProcess)SZoneConfig.getObject(RequestContextHolder.getRequestContext().getZoneName(), 
							"workflowConfiguration/property[@name='" + WorkflowProcess.PROCESS_JOB + "']", com.sitescape.team.jobs.DefaultWorkflowProcess.class);
			String secsString = (String)SZoneConfig.getString(RequestContextHolder.getRequestContext().getZoneName(), "workflowConfiguration/property[@name='" + WorkflowProcess.PROCESS_SECONDS + "']");
			int seconds = 300;
			try {
				seconds = Integer.parseInt(secsString);
			} catch (Exception ex) {};
			Map params = buildParams(wfEntry, currentWs);
			params.put(WorkflowScheduledAction.WORKFLOW_APPLICATION_ID, app.getId().toString());
			params.put(WorkflowScheduledAction.WORKFLOW_APPLICATION_NAME, app.getName());
			
			schedJob.schedule(wfEntry, currentWs, StartRemoteApp.class.getName(), params, seconds);
					
		} catch (NoPrincipalByTheIdException e) {
			throw new ConfigurationException(
					"Invalid remote application id '" + application + "'", e);
		} finally {
			RequestContextHolder.setRequestContext(oldCtx);
		}
	}
		
	protected Map buildParams(WorkflowSupport wfEntry, WorkflowState currentWs) {
		HashMap params = new HashMap();
		params.put(WorkflowScheduledAction.WORKFLOW_ENTRY_ID, currentWs.getOwner().getEntity().getId().toString());
		params.put(WorkflowScheduledAction.WORKFLOW_BINDER_ID, currentWs.getOwner().getEntity().getParentBinder().getId().toString());
		params.put(WorkflowScheduledAction.WORKFLOW_STATE_ID, currentWs.getId().toString());
		params.put(WorkflowScheduledAction.WORKFLOW_STATE_NAME, currentWs.getState());
		params.put(WorkflowScheduledAction.WORKFLOW_THREAD_NAME, currentWs.getThreadName()==null?"":currentWs.getThreadName());
		return params;
	}
	protected void moveEntry(Element item, ExecutionContext executionContext, WorkflowSupport wfEntry, WorkflowState currentWs) {
		Entry entry = (Entry)wfEntry;
		Binder parent = entry.getParentBinder();
		Binder destination  = getDestination(item, entry);
		EntryProcessor processor = (EntryProcessor)((ProcessorManager)SpringContextUtil.getBean("modelProcessorManager")).getProcessor(parent, parent.getProcessorKey(EntryProcessor.PROCESSOR_KEY));
		processor.moveEntry(parent, entry, destination, null);
	}
	protected void copyEntry(Element item, ExecutionContext executionContext, WorkflowSupport wfEntry, WorkflowState currentWs) {
		Entry entry = (Entry)wfEntry;
		Binder parent = entry.getParentBinder();
		Binder destination  = getDestination(item, entry);
		EntryProcessor processor = (EntryProcessor)((ProcessorManager)SpringContextUtil.getBean("modelProcessorManager")).getProcessor(parent, parent.getProcessorKey(EntryProcessor.PROCESSOR_KEY));
		processor.copyEntry(parent, entry, destination, null);

	}
	protected Binder getDestination(Element item, Entry entry) {
		String destinationId = DefinitionUtils.getPropertyValue(item, "folder");
		if (Validator.isNull(destinationId)) return null;
		Binder destination=null;
		try {
			Long id = Long.parseLong(destinationId);
			destination = getCoreDao().loadBinder(id, entry.getZoneId());
		} catch (Exception ex) {
			logger.error("Error loading workflow destination binder: " + ex.getLocalizedMessage());
			return null;
		}
		//make sure binder owner has write access to destination.  Since the binder owner set up the workflow destination,
		//they need write access.  The current user may not have the rights, but this shouldn't stop the process.
		AccessControlManager accessManager = (AccessControlManager)SpringContextUtil.getBean("accessControlManager");
		User user = getProfileDao().loadUser(entry.getParentBinder().getOwnerId(), entry.getZoneId());
		accessManager.checkOperation(user, destination, WorkAreaOperation.CREATE_ENTRIES);
		return destination;
		
	}
	protected void startThread(Element item, ExecutionContext executionContext, WorkflowSupport entry, WorkflowState currentWs) {
		//Get the "startState" property
		Element threadEle = (Element)item.selectSingleNode("./properties/property[@name='name']");
		if (threadEle == null) return;
		String threadName = threadEle.attributeValue("value", "");
		if (Validator.isNull(threadName)) return;
		if (threadName.equals(currentWs.getThreadName())) return; //cannot start self - will have loop
		threadEle = (Element) item.getDocument().getRootElement().selectSingleNode("//item[@name='parallelThread']/properties/property[@name='name' and @value='"+threadName+"']");
		if (threadEle == null) return;
		threadEle = threadEle.getParent().getParent();
		Element startStateEle = (Element) threadEle.selectSingleNode("./properties/property[@name='startState']");
		if (startStateEle == null) return;
		String startState = startStateEle.attributeValue("value", "");
		if (Validator.isNull(startState)) return;
		
		if (debugEnabled) logger.debug("Starting thread: " + threadName);
		
		//	if thread exists, terminate it
		WorkflowState thread = entry.getWorkflowStateByThread(currentWs.getDefinition(), threadName);
		if (thread != null) {
			Token childToken = executionContext.getJbpmContext().loadToken(thread.getTokenId().longValue());
			childToken.end(false);
			entry.removeWorkflowState(thread);
		}
		ProcessInstance pI = executionContext.getToken().getProcessInstance();
		ProcessDefinition pD = pI.getProcessDefinition();
		//Now start a thread - since threads can be restarted and we don't delete old
		//tokens, each thread instance needs a unique name
		//This also implies we cannot look child tokens up by name cause we don't know it
		//the 'real' thread name is kept in WorkflowState
		Token subToken = new Token(pI.getRootToken(), threadName + "-" + new Date());
		executionContext.getJbpmContext().getSession().save(subToken);
		//Track state of thread
		thread = (WorkflowState) new WorkflowState();
		thread.setThreadName(threadName);
		thread.setTokenId(new Long(subToken.getId()));
		thread.setState(startState);
		//Use the same workflow definition as the current workflow state
		thread.setDefinition(currentWs.getDefinition());
		//need to save explicitly - actions called by the node.enter may look it up 
		getCoreDao().save(thread);
		entry.addWorkflowState(thread);
			
		Node node = pD.findNode(startState);
		node.enter(new ExecutionContext(subToken));
	}
	protected boolean stopThread(Element item, ExecutionContext executionContext, WorkflowSupport entry, WorkflowState currentWs) {
		Element threadEle = (Element)item.selectSingleNode("./properties/property[@name='name']");
		if (threadEle == null) return false;
		String threadName = threadEle.attributeValue("value", "");
		if (Validator.isNull(threadName)) return false;

		//See if child has ended
		WorkflowState thread = entry.getWorkflowStateByThread(currentWs.getDefinition(), threadName);
		if (thread != null) {
			//child is active, end it
			Token childToken = executionContext.getJbpmContext().loadToken(thread.getTokenId().longValue());
			if (childToken != null)	{
				childToken.end();
			}
			//leaving a state - logit
			getReportModule().addWorkflowStateHistory(thread, new HistoryStamp(RequestContextHolder.getRequestContext().getUser()), true);
			entry.removeWorkflowState(thread);
			if (debugEnabled) logger.debug("Stoping thread: " + threadName);
			return true;
		}
		return false;
	}
	protected void doNotifications(ExecutionContext executionContext, WorkflowSupport wEntry, WorkflowState currentWs) {
		List notifications;
		Entry entry = (Entry)wEntry;
		if (Event.EVENTTYPE_NODE_ENTER.equals(executionContext.getEvent().getEventType())) {
			if (currentWs.getState().equals(executionContext.getContextInstance().getTransientVariable(WorkflowModule.FORCE_STATE))) return;
			notifications = NotificationUtils.getEnterNotifications(currentWs.getDefinition(), entry, currentWs.getState());
		} else {
			notifications = NotificationUtils.getExitNotifications(currentWs.getDefinition(), entry, currentWs.getState());
		}
		for (int i=0; i<notifications.size(); ++i) {
			WfNotify notify = (WfNotify)notifications.get(i);
			HashMap details = new HashMap();
			String s = notify.getSubject();
			if (notify.isAppendTitle()) {
				s = s + " " + entry.getTitle();
			}
			details.put(MailModule.SUBJECT, s);
			ArrayList addrs = new ArrayList();
			Set<Long> ids = notify.getPrincipalIds();
			if (ids.remove(ObjectKeys.OWNER_USER_ID)) {
				ids.add(entry.getCreation().getPrincipal().getId());
			}
			if (ids.remove(ObjectKeys.TEAM_MEMBER_ID)) {
				ids.addAll(entry.getParentBinder().getTeamMemberIds());
			}
			Set<User> users = getProfileModule().getUsersFromPrincipals(ids);
			for (User u: users)  {
				String email = u.getEmailAddress();
				try	{
					if (!Validator.isNull(email)) {
						InternetAddress ia = new InternetAddress(email);
						ia.validate();
						addrs.add(ia);
					}
				} catch (AddressException ae) {
					logger.error("Skipping email notifications for " + u.getTitle() + " Bad email address");
				}
			} 

			AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, entry.getParentBinder().getId().toString());
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
			adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entry.getEntityType().toString());

			details.put(MailModule.TO, addrs);
			StringBuffer tMsg = new StringBuffer();
			tMsg.append(adapterUrl.toString());
			tMsg.append("\n\n");
			tMsg.append(notify.getBody());
			if (notify.isAppendBody() && entry.getDescription() != null) {
				tMsg.append("\n");
				tMsg.append(entry.getDescription().getStrippedText());
				tMsg.append("\n");
			}
			details.put(MailModule.TEXT_MSG, tMsg.toString());
			StringBuffer hMsg = new StringBuffer();
			hMsg.append("<a href=\"");
			hMsg.append(adapterUrl.toString());
			hMsg.append("\">");
			hMsg.append(entry.getTitle());
			hMsg.append("</a>");
			hMsg.append("<br/><br/>");
			hMsg.append(notify.getBody());
			if (notify.isAppendBody()&& entry.getDescription() != null) {
				hMsg.append("<p>");
				hMsg.append(entry.getDescription().getText());
				hMsg.append("</p>");
			}
			details.put(MailModule.HTML_MSG, hMsg.toString());
			try {
				//to keep transaction small, schedule it
				getMailModule().scheduleMail(entry.getParentBinder(), details, "Workflow notify for binder " + 
					entry.getParentBinder().getId() + " entry " + entry.getId());
			} catch (Exception ex) {
				logger.error("Failed workflow notification: " + wEntry.toString() + " " + ex.getLocalizedMessage()!=null?ex.getLocalizedMessage():ex.getMessage());
			}
			 
		}
	}
}
