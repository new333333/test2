package com.sitescape.ef.module.workflow;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.def.Event;
import org.jbpm.context.exe.ContextInstance;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.mail.MimeMessagePreparator;
import com.sitescape.util.Validator;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WfNotify;

public class Notify extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(getClass());
	 public void execute( ExecutionContext executionContext ) throws Exception {
		 ContextInstance ctx = executionContext.getContextInstance();
		 Token token = executionContext.getToken();
		 Node current = token.getNode();
		 String stateName = current.getName();
		 AclControlledEntry entry = loadEntry(ctx);
		 logger.info("Start workflow notification:" + stateName); 
		 MimeHelper mHelper = new MimeHelper(entry, token);
		 WorkflowState ws = entry.getWorkflowState(new Long(token.getId()));
		 //record event may not have happened yet
		 ws.setState(stateName);

		 if (ws == null) {
			 logger.error("Workflow notify: Cannot find state for token:" + token.getId());
			 return;
		 }
		 List notifications;
		 if (Event.EVENTTYPE_NODE_ENTER.equals(executionContext.getEvent().getEventType())) {
			 notifications = ws.getWfEnterNotifications();
		 } else {
			 notifications = ws.getWfExitNotifications();
		 }
		 for (int i=0; i<notifications.size(); ++i) {
			 WfNotify n = (WfNotify)notifications.get(i);
			 mHelper.setNotify(n);
			 getMailModule().sendMail(entry.getParentBinder(), mHelper);
			 
		 }
		 logger.info("End workflow notification:" + stateName); 
	 }

	 private class MimeHelper implements MimeMessagePreparator {
			AclControlledEntry entry;
			Token token;
			MimeMessage message;
			String from;
			WfNotify notify;

			private MimeHelper(AclControlledEntry entry, Token token) {
				this.entry = entry;
				this.token = token;				
			}
			protected void setNotify(WfNotify notify) {
				this.notify = notify;
			}
			public MimeMessage getMessage() {
				return message;
			}
			public void setDefaultFrom(String from) {
				this.from = from;
			}
			public void prepare(MimeMessage mimeMessage) throws MessagingException {
				//make sure nothing saved yet
				message = null;
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
				String s = notify.getSubject();
				if (notify.isAppendTitle()) {
					s = s + " " + entry.getTitle();
				}
				helper.setSubject(s);
				helper.setFrom(from);
				if (notify.isCreatorEnabled()) {
					User user = getCoreDao().loadUser(entry.getCreation().getPrincipal().getId(), entry.getCreation().getPrincipal().getZoneName());
					String email = user.getEmailAddress();
					try	{
						if (!Validator.isNull(email)) helper.addTo(email);
					} catch (AddressException ae) {
						logger.error("Skipping email notifications for " + user.getTitle() + " Bad email address");
					}
					
				}
/*				List users = getCoreDao().loadEnabledUsers(toIds, entry.getParentBinder().getZoneName());
				if (!users.isEmpty()) {
					for (Iterator iter=users.iterator();iter.hasNext();) {
						User user = (User)iter.next();
						String email = user.getEmailAddress();
						try	{
							if (!Validator.isNull(email)) helper.addTo(email);
						} catch (AddressException ae) {
							logger.error("Skipping email notifications for " + user.getTitle() + " Bad email address");
						}
					}
					
				} 
*/
				StringBuffer tMsg = new StringBuffer(notify.getBody());
				if (notify.isAppendBody()) {
					tMsg.append("\n");
					tMsg.append(entry.getDescription().getStrippedText());
					tMsg.append("\n");
				}
				StringBuffer hMsg = new StringBuffer(notify.getBody());
				if (notify.isAppendBody()) {
					hMsg.append("<p>");
					hMsg.append(entry.getDescription().getText());
					hMsg.append("</p>");
				}
				mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
				helper.setText(tMsg.toString(), hMsg.toString());
				//save message incase cannot connect and need to resend;
				message = mimeMessage;
			}

		}

}
