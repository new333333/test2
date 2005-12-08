package com.sitescape.ef.module.mail.impl;
import javax.mail.Session;
import com.sitescape.util.Validator;
/**
 * This class extends the spring JavaMailSenderImpl.  It adds the bean name, so
 * we can locate the sender when mail is resent after an error.  The resend occurs
 * as a scheduled job.
 * @author Janet McCann
 *
 */
public class JavaMailSenderImpl extends
		org.springframework.mail.javamail.JavaMailSenderImpl
		implements com.sitescape.ef.module.mail.JavaMailSender {
	private String beanName;
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public String getBeanName() {
		return beanName;
	}
	
	public String getDefaultFrom() {
		Session session = getSession();
		String protocol = getProtocol();
		String from = session.getProperty("mail." + protocol + ".user");
		if (Validator.isNull(from))
			from = session.getProperty("mail.user");
		return from;
	}

}
