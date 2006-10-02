package com.sitescape.ef.module.workflow;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.def.Event;
import org.jbpm.context.exe.ContextInstance;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.User;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WfNotify;
import com.sitescape.ef.jobs.SendEmail;

public class Notify extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	public void execute( ExecutionContext executionContext ) throws Exception {
		ContextInstance ctx = executionContext.getContextInstance();
		Token token = executionContext.getToken();
		Node current = token.getNode();
		String stateName = current.getName();
		WorkflowSupport wEntry = loadEntry(ctx);
		Entry entry = (Entry)wEntry;
		if (infoEnabled) logger.info("Workflow notify start at:" + stateName); 
		WorkflowState ws = wEntry.getWorkflowState(new Long(token.getId()));
		//record event may not have happened yet
		ws.setState(stateName);

		if (ws == null) {
			if (infoEnabled) logger.error("Workflow notify: Cannot find state for token:" + token.getId());
			return;
		}
		List notifications;
		if (Event.EVENTTYPE_NODE_ENTER.equals(executionContext.getEvent().getEventType())) {
			notifications = WorkflowUtils.getEnterNotifications(ws.getDefinition(), ws.getState());
		} else {
			notifications = WorkflowUtils.getExitNotifications(ws.getDefinition(), ws.getState());
		}
		for (int i=0; i<notifications.size(); ++i) {
			WfNotify notify = (WfNotify)notifications.get(i);
			HashMap details = new HashMap();
			String s = notify.getSubject();
			if (notify.isAppendTitle()) {
				s = s + " " + entry.getTitle();
			}
			details.put(SendEmail.SUBJECT, s);
			ArrayList addrs = new ArrayList();
			if (notify.isCreatorEnabled()) {
				User user = getProfileDao().loadUser(entry.getCreation().getPrincipal().getId(), entry.getCreation().getPrincipal().getZoneName());
				String email = user.getEmailAddress();
				try	{
					if (!Validator.isNull(email)) {
						InternetAddress ia = new InternetAddress(email);
						ia.validate();
						addrs.add(ia);							
					}
				} catch (AddressException ae) {
					logger.error("Workflow notify: Skipping email notifications for " + user.getTitle() + " Bad email address");
				}
				
			}
			List<User> users = getProfileDao().loadEnabledUsers(notify.getPrincipalIds(), entry.getParentBinder().getZoneName());
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

			details.put(SendEmail.TO, addrs);
			StringBuffer tMsg = new StringBuffer(notify.getBody());
			if (notify.isAppendBody()) {
				tMsg.append("\n");
				tMsg.append(entry.getDescription().getStrippedText());
				tMsg.append("\n");
			}
			details.put(SendEmail.TEXT_MSG, tMsg.toString());
			StringBuffer hMsg = new StringBuffer(notify.getBody());
			if (notify.isAppendBody()) {
				hMsg.append("<p>");
				hMsg.append(entry.getDescription().getText());
				hMsg.append("</p>");
			}
			details.put(SendEmail.HTML_MSG, hMsg.toString());
			getMailManager().scheduleMail(entry.getParentBinder(), details, "Workflow notify for binder " + 
					entry.getParentBinder().getId() + " entry " + entry.getId());
			 
		}
		if (infoEnabled) logger.info("Workflow notify end at:" + stateName); 
	}

}
