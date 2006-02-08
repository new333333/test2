package com.sitescape.ef.mail;

import javax.mail.internet.MimeMessage;

public interface MimeMessagePreparator extends
		org.springframework.mail.javamail.MimeMessagePreparator {
	public MimeMessage getMessage();
	public void setDefaultFrom(String from);

}
