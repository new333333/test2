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
import org.jbpm.context.exe.ContextInstance;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.ef.domain.AclControlledEntry;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.mail.MimeMessagePreparator;
import com.sitescape.util.Validator;

public class Notify extends AbstractActionHandler {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(getClass());
	private String ids;
	private String subject, body;
	public void setPrincipals(String ids) {
		//need to split into list of Longs for loading
		this.ids = ids;
	 }
	 public void setSubject(String subject) {
		 this.subject = subject;
	 }
	 public void setBody(String body) {
		 this.body = body;
	 }
	 public void execute( ExecutionContext executionContext ) throws Exception {
		 ContextInstance ctx = executionContext.getContextInstance();
		 Token token = executionContext.getToken();
		 Node current = token.getNode();
		 AclControlledEntry entry = loadEntry(ctx);
		 logger.info("Start workflow notification:" + current.getName()); 
		 MimeHelper mHelper = new MimeHelper(entry, token);
		 getMailModule().sendMail(entry.getParentBinder(), mHelper);
		 logger.info("End workflow notification:" + current.getName()); 
	 }

	 private class MimeHelper implements MimeMessagePreparator {
			AclControlledEntry entry;
			Token token;
			Collection toIds;
			String[] addrs;
			MimeMessage message;
			String subject;
			String from;
			String text;
			String html;

			private MimeHelper(AclControlledEntry entry, Token token) {
				this.entry = entry;
				this.token = token;				
			}
			protected void setToIds(Collection toIds) {
				this.toIds = toIds;				
			}
			protected void setAddrs(String[] addrs) {
				this.addrs = addrs;
			}
			protected void setSubject(String subject) {
				this.subject = subject;
			}
			protected void setText(String text) {
				this.text = text;
			}
			protected void setHtml(String html) {
				this.html = html;
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
				helper.setSubject(subject);
				helper.setFrom(from);
				List users = getCoreDao().loadEnabledUsers(toIds, entry.getParentBinder().getZoneName());
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
				if (addrs != null) { 
					for (int i=0; i<addrs.length; ++i) {
						String email = addrs[i];
						try {
							if (!Validator.isNull(email)) helper.addTo(email);
						} catch (AddressException ae) {
							logger.error("Skipping email notifications for " + email + " Bad email address");
						}
					}
				}
				mimeMessage.addHeader("Content-Transfer-Encoding", "8bit");
				helper.setText((String)text, (String)html);
				//save message incase cannot connect and need to resend;
				message = mimeMessage;
			}

		}

}
