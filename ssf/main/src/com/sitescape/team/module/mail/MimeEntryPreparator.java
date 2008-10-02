package com.sitescape.team.module.mail;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.team.domain.Entry;
import com.sitescape.team.util.NLT;
import com.sitescape.util.Validator;
public class MimeEntryPreparator extends MimeNotifyPreparator {
	Map details;
	
	public MimeEntryPreparator(EmailFormatter processor, Entry entry,  Map details, Log logger, boolean sendVTODO) {
		super(processor, entry.getParentBinder(), new Date(), logger, sendVTODO);
		this.details = details;
		setEntry(entry);
	}
	protected void setSubject(MimeMessageHelper helper) throws MessagingException {
		helper.setSubject((String)details.get(MailModule.SUBJECT));
	}
	protected void setFrom(MimeMessageHelper helper) throws MessagingException {
		if (details.containsKey(MailModule.FROM)) 
			helper.setFrom((InternetAddress)details.get(MailModule.FROM));
		else
			helper.setFrom(defaultFrom);
	}
	protected void setToAddrs(MimeMessageHelper helper) throws MessagingException {
		Collection<InternetAddress> addrs = (Collection)details.get(MailModule.TO);
		for (InternetAddress a : addrs) {
			helper.addTo(a);
		}
		if (addrs.isEmpty()) {
			if (details.containsKey(MailModule.FROM)) 
				helper.addTo((InternetAddress)details.get(MailModule.FROM));
			else
				helper.addTo(defaultFrom);
			helper.setSubject(NLT.get("errorcode.noRecipients") + " " + (String)details.get(MailModule.SUBJECT));
		}
	}

	protected void setText(String plainText, String htmlText, MimeMessageHelper helper) throws MessagingException {
		String plain = (String)details.get(MailModule.TEXT_MSG);
		if (Validator.isNotNull(plain)) plainText = plain + plainText;
		String html = (String)details.get(MailModule.HTML_MSG);
		if (Validator.isNotNull(html)) htmlText = html + htmlText;
		super.setText(plainText, htmlText, helper);
	}

}

