/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import java.util.Map;
import java.util.Set;
import java.util.Collection;

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
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoPrincipalByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WorkflowHistory;
import org.kablink.teaming.domain.WorkflowResponse;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.WorkflowSupport;
import org.kablink.teaming.extension.ExtensionCallback;
import org.kablink.teaming.jobs.WorkflowProcess;
import org.kablink.teaming.modelprocessor.ProcessorManager;
import org.kablink.teaming.module.binder.processor.EntryProcessor;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.mail.EmailUtil;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.workflow.jbpm.CalloutHelper;
import org.kablink.teaming.module.workflow.support.WorkflowAction;
import org.kablink.teaming.module.workflow.support.WorkflowScheduledAction;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TextToHtml;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.Html;
import org.kablink.util.Validator;

public class EnterExitEvent extends AbstractActionHandler {
	  
	//Indexing the entry is handled by the code that initiates a transition/nodeEnter/nodeExit
	//Because mutiple states can be effected, we don't want to re-index
	//each time.  Only need one at the end of the transaction

	private static final long serialVersionUID = -5904672789676975912L;
	public void execute(ExecutionContext executionContext) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Long id = new Long(token.getId());
		String state = token.getNode().getName();
		WorkflowSupport entry = loadEntry(ctx);
		if (entry == null || (entry instanceof FolderEntry && 
				(((FolderEntry)entry).isPreDeleted() || ((FolderEntry)entry).isDeleted()))) {
			return;
		}
		WorkflowState ws = entry.getWorkflowState(id);
		boolean isEnter = true;
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
				if (debugEnabled) logger.debug("Workflow event (" + executionContext.getEvent().getEventType() + ") recorded: " + state);
				//remove old responses associated with this state
				Set names = WorkflowProcessUtils.getQuestionNames(ws.getDefinition(), ws.getState());
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
				items  = WorkflowProcessUtils.getOnEntry(ws.getDefinition(), state);
			} else {
				isEnter = false;
				//cancel timers associated with this state.  onElapsedTime timers cancelled by jbpm
				executionContext.getJbpmContext().getSchedulerSession().cancelTimersByName("onDataValue", token);
				items  = WorkflowProcessUtils.getOnExit(ws.getDefinition(), state);				
			}
			for (int i=0; i<items.size(); ++i) {
				Element item = (Element)items.get(i);
	   			String name = item.attributeValue("name","");
	   			if ("variable".equals(name)) {
	   				WorkflowProcessUtils.setVariable(item, executionContext, entry, ws);
	   			} else if ("notifications".equals(name)) {
	   				//don't send mail if forcing state
	   				if (isEnter && ws.getState().equals(executionContext.getContextInstance().getTransientVariable(WorkflowModule.FORCE_STATE))) continue;
	   				//since permalinks no longer contain binderIds, this can be done at any time (doesn't have to worry about a move
	   				doNotification(item, executionContext, entry, ws);	   			
	   			} else if ("moveEntry".equals(name)) {
	   				moveEntry(item, executionContext, entry, ws);	   				
	   			} else if ("copyEntry".equals(name)) {
	   				copyEntry(item, executionContext, entry, ws);	   				
	   			} else if ("startParallelThread".equals(name)) {
	   				startThread(item, executionContext, entry, ws);
	   			} else if ("stopParallelThread".equals(name)) {
	   				stopThread(item, executionContext, entry, ws); 
	   			} else if ("workflowAction".equals(name)) {
	   				startCustomAction(item, executionContext, entry, ws);
	   			} else if ("workflowRemoteApp".equals(name)) {
	   				startRemoteApp(item, executionContext, entry, ws);
	   			} else if ("startProcess".equals(name)) {
	   				startProcess(item, executionContext, entry, ws);
	   			}

	   		}
			if (Event.EVENTTYPE_NODE_LEAVE.equals(executionContext.getEvent().getEventType())) {
				//leaving a state - logit
				WorkflowHistory history = new WorkflowHistory(ws, new HistoryStamp(RequestContextHolder.getRequestContext().getUser()), false);
				getCoreDao().save(history);
			}
			//See if other threads conditions are now met.
			WorkflowProcessUtils.processConditions(entry, token);
		}
	}
	protected void startCustomAction(final Element action, final ExecutionContext executionContext, final WorkflowSupport wfEntry, final WorkflowState currentWs) {
		final String actionName = DefinitionUtils.getPropertyValue(action, "class");
		if (Validator.isNull(actionName)) return;
		try {
			getZoneClassManager().execute(new ExtensionCallback() {
				public Object execute(Object action) {
					if (action instanceof WorkflowScheduledAction) {
						WorkflowProcess schedJob = null;
						String jobClass = SZoneConfig.getString(RequestContextHolder.getRequestContext().getZoneName(), 
								"workflowConfiguration/property[@name='" + WorkflowProcess.PROCESS_JOB + "']");
						if (Validator.isNotNull(jobClass)) {
							try {
								schedJob =  (WorkflowProcess)ReflectHelper.getInstance(jobClass);
							} catch (Exception e) {
								logger.error("Cannot instantiate WorkflowProcess custom class", e);
							}
						}
						if (schedJob == null) schedJob = (WorkflowProcess)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultWorkflowProcess.class);
						String secsString = (String)SZoneConfig.getString(RequestContextHolder.getRequestContext().getZoneName(), "workflowConfiguration/property[@name='" + WorkflowProcess.PROCESS_SECONDS + "']");
						int seconds = 300;
						try {
							seconds = Integer.parseInt(secsString);
						} catch (Exception ex) {};
						schedJob.schedule(wfEntry, currentWs, actionName, buildParams(wfEntry, currentWs), seconds);
								
					} else {
						((WorkflowAction)action).setHelper(new CalloutHelper(executionContext));
						((WorkflowAction)action).execute(wfEntry, currentWs);
					}
					return null;
				};
			}, actionName); 
		} catch (ClassNotFoundException e) {
			logger.error("Invalid Workflow Action class name '" + actionName + "'");
			throw new ConfigurationException("Invalid Workflow Action class name '" + actionName + "'",
				e);
		} 		

	}
		
	
	protected void startRemoteApp(final Element action, final ExecutionContext executionContext, final WorkflowSupport wfEntry, final WorkflowState currentWs) {
		final String application = DefinitionUtils.getPropertyValue(action, "remoteApp");
		if (Validator.isNull(application)) return;
		Long runAsId = WorkflowProcessUtils.getRunAsUser(action, wfEntry, currentWs);
		if (runAsId == null) throw new ConfigurationException("Remote application cannot be run, user doesn't exist");

		RunasTemplate.runas(new RunasCallback() {
			public Object doAs() {
				try {
					Application app = getProfileDao().loadApplication(Long.valueOf(application), RequestContextHolder.getRequestContext().getZoneId());
					WorkflowProcess schedJob = null;
					String jobClass = SZoneConfig.getString(RequestContextHolder.getRequestContext().getZoneName(), 
							"workflowConfiguration/property[@name='" + WorkflowProcess.PROCESS_JOB + "']");
					if (Validator.isNotNull(jobClass)) {
						try {
							schedJob =  (WorkflowProcess)ReflectHelper.getInstance(jobClass);
						} catch (Exception e) {
							logger.error("Cannot instantiate WorkflowProcess custom class", e);
						}
					}
					if (schedJob == null) schedJob = (WorkflowProcess)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultWorkflowProcess.class);
					String secsString = (String)SZoneConfig.getString(RequestContextHolder.getRequestContext().getZoneName(), "workflowConfiguration/property[@name='" + WorkflowProcess.PROCESS_SECONDS + "']");
					int seconds = 300;
					try {
						seconds = Integer.parseInt(secsString);
					} catch (Exception ex) {};
					Map params = buildParams(wfEntry, currentWs);
					params.put(WorkflowScheduledAction.WORKFLOW_APPLICATION_ID, app.getId().toString());
					params.put(WorkflowScheduledAction.WORKFLOW_APPLICATION_NAME, app.getName());
					String variable = DefinitionUtils.getPropertyValue(action, "variable");
					if (Validator.isNotNull(variable)) params.put(WorkflowScheduledAction.WORKFLOW_RESULT_NAME, variable);
					schedJob.schedule(wfEntry, currentWs, StartRemoteApp.class.getName(), params, seconds);
					
				} catch (NoPrincipalByTheIdException e) {	
					throw new ConfigurationException(
					"Invalid remote application id '" + application + "'", e);
				} 
				return null;
			}
		}, currentWs.getZoneId(), runAsId);
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
		AccessControlManager accessManager = (AccessControlManager)SpringContextUtil.getBean("accessControlManager");
		//check current user first - this covers case where binderOwner has been deleted
		if (!accessManager.testOperation(destination, WorkAreaOperation.CREATE_ENTRIES)) {
			//see if binder owner has write access to destination.  Since the binder owner set up the workflow destination,
			//they need write access.  The current user may not have the rights, but this shouldn't stop the process.
			try {
				User user = getProfileDao().loadUser(entry.getParentBinder().getOwnerId(), entry.getZoneId());
				accessManager.checkOperation(user, destination, WorkAreaOperation.CREATE_ENTRIES);
			} catch (NoUserByTheIdException nu) {	
				//owner probably deleted.
				//throw original error 
				accessManager.checkOperation(destination, WorkAreaOperation.CREATE_ENTRIES);
			}
		}
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
		//Now start a thread - Threads can be restarted which means we have to remove any old references to them.
		//tokens because they are referenced by WorkflowState unless ended, each thread instance needs a unique name
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
	protected void startProcess(Element item, ExecutionContext executionContext, WorkflowSupport entry, WorkflowState currentWs) {
		//start another process
		String definitionId = DefinitionUtils.getPropertyValue(item, "definitionId");
		if (Validator.isNull(definitionId)) return;
		Definition def = loadDefinition(definitionId);
		if (def == null) return;
		for (WorkflowState exists:entry.getWorkflowStates()) {
			if (exists.getDefinition().equals(def)) {
				if (debugEnabled) logger.debug("Process already running : " + def.getName());
				return;
			}
		}
		getWorkflowModule().addEntryWorkflow(entry, ((Entry)entry).getEntityIdentifier(), def, null);
		if (debugEnabled) logger.debug("Starting process : " + def.getName());
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
				childToken.end(false);
			}
			//leaving a state - logit
			WorkflowHistory history = new WorkflowHistory(thread, new HistoryStamp(RequestContextHolder.getRequestContext().getUser()), true);
			getCoreDao().save(history);
			entry.removeWorkflowState(thread);
			if (debugEnabled) logger.debug("Stoping thread: " + threadName);
			return true;
		}
		return false;
	}
	protected void doNotification(Element item, ExecutionContext executionContext, WorkflowSupport wEntry, WorkflowState currentWs) {
		Entry entry = (Entry)wEntry;
		WorkflowProcessUtils.WfNotify notify = WorkflowProcessUtils.getNotification(item, wEntry);
		HashMap details = new HashMap();
		List<InternetAddress>addrs = getAddrs(notify.getToUsers());
		//Add in the additional email addresses
		if (notify.getEmailAddrs() != null) {
			for (String addr : notify.getEmailAddrs()) {
				try {
					InternetAddress ia = new InternetAddress(addr);
					if (addrs == null) addrs = new ArrayList<InternetAddress>();
					if (!addrs.contains(ia)) addrs.add(ia);
				} catch(Exception e) {}
			}
		}
		
		if (addrs == null || addrs.isEmpty()) return; //need a to list
		details.put(MailModule.TO, addrs);
		addrs = getAddrs(notify.getCCUsers());
		if (addrs != null && !addrs.isEmpty()) details.put(MailModule.CC, addrs);
		addrs = getAddrs(notify.getBCCUsers());
		if (addrs != null && !addrs.isEmpty()) details.put(MailModule.BCC, addrs);
		String s = notify.getSubject();
		if (notify.isAppendTitle()) {
			s = s + " " + entry.getTitle();
		}
		s = MarkupUtil.markupStringReplacement(null, null, null, null, entry, s, WebKeys.MARKUP_VIEW);
		details.put(MailModule.SUBJECT, s);
		String permaLink = PermaLinkUtil.getPermalink(entry);
		String msgHtml = "";
		if (entry.getDescription() != null) msgHtml = MarkupUtil.markupStringReplacement(null, null, null, null, entry, entry.getDescription().getText(), WebKeys.MARKUP_VIEW);
		StringBuffer tMsg = new StringBuffer();
		tMsg.append(permaLink);
		tMsg.append("\n\n");
		
		String bodyText = MarkupUtil.markupStringReplacement(null, null, null, null, entry, notify.getBody(), WebKeys.MARKUP_VIEW);
		tMsg.append(Html.stripHtml(bodyText));
		if (notify.isAppendBody()) {
			tMsg.append("\n");
			tMsg.append(Html.stripHtml(msgHtml));
			tMsg.append("\n");
		}
		EmailUtil.putText(details, MailModule.TEXT_MSG, tMsg.toString());
		
		//Get the body text and turn it into html
		TextToHtml textToHtml = new TextToHtml();
		textToHtml.setBreakOnLines(true);
		textToHtml.setStripHtml(false);
		textToHtml.parseText(bodyText);
		String bodyTextHtml = textToHtml.toString();

		StringBuffer hMsg = new StringBuffer();
		hMsg.append("<a href=\"");
		hMsg.append(permaLink);
		hMsg.append("\">");
		hMsg.append(entry.getTitle());
		hMsg.append("</a>");
		hMsg.append("<br /><br />");
		hMsg.append(bodyTextHtml);
		if (notify.isAppendBody()) {
			hMsg.append("<p>");
			hMsg.append(msgHtml);
			hMsg.append("</p>");
		}
		try {
			String email = MiscUtil.getFromOverride();
			boolean usingOverride = MiscUtil.hasString(email);
			if (!usingOverride) {
				email = getProfileDao().loadUser(wEntry.getOwnerId(), entry.getZoneId()).getEmailAddress();
			}
			InternetAddress ia = new InternetAddress(email);
			if (!usingOverride) {
				ia.validate();
			}
			details.put(MailModule.FROM, ia);
		} catch  (Exception useDefault) {}
		
		EmailUtil.putHTML(details, MailModule.HTML_MSG, hMsg.toString());
		try {
			//to keep transaction small, schedule it
			getMailModule().scheduleMail(entry.getParentBinder(), details, "Workflow notify for binder " + 
					entry.getParentBinder().getId() + " entry " + entry.getId());
		} catch (Exception ex) {
			logger.error("Failed workflow notification: " + wEntry.toString() + " " + ex.getLocalizedMessage()!=null?ex.getLocalizedMessage():ex.getMessage());
		}
			 
	}
	protected List<InternetAddress> getAddrs(Collection<User>users) {
		if (users == null || users.isEmpty()) return null;
		ArrayList addrs = new ArrayList();
		for (User u: users)  {
			String email = u.getEmailAddress();
			try	{
				if (!Validator.isNull(email)) {
					InternetAddress ia = new InternetAddress(email);
					ia.validate();
					addrs.add(ia);
				}
			} catch (AddressException ae) {
				logger.error("Skipping email notifications for " + Utils.getUserTitle(u) + " Bad email address");
			}
		} 
		return addrs;
	}

}
