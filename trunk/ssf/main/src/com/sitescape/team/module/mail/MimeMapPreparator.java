package com.sitescape.team.module.mail;

import java.util.Collection;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.util.Calendars;

import org.apache.commons.logging.Log;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.ical.util.ICalUtils;
import com.sitescape.team.util.NLT;
public class MimeMapPreparator extends AbstractMailPreparator {
	Map details;
	boolean sendVTODO;
	public MimeMapPreparator(Map details, Log logger, boolean sendVTODO) {
		super(logger);
		this.details = details;
		this.sendVTODO = sendVTODO;
	}
	public void prepare(MimeMessage mimeMessage) throws MessagingException {
		//make sure nothing saved yet
		message = null;
		int multipartMode = MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;
		
		Collection<net.fortuna.ical4j.model.Calendar> iCals = (Collection)details.get(MailModule.ICALENDARS);
		if (iCals != null && iCals.size() > 0) {
			// Need to attach icalendar as alternative content,
			// if there is more then one icals then
			// all are merged and add ones to email as alternative content
			multipartMode = MimeMessageHelper.MULTIPART_MODE_MIXED;
		}
	
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipartMode);
		mimeMessage.addHeader(MailModule.HEADER_CONTENT_TRANSFER_ENCODING, MailModule.HEADER_CONTENT_TRANSFER_ENCODING_8BIT);
		helper.setSubject((String)details.get(MailModule.SUBJECT));
		if (details.containsKey(MailModule.FROM)) 
			helper.setFrom((InternetAddress)details.get(MailModule.FROM));
		else
			helper.setFrom(defaultFrom);
		
		Collection<InternetAddress> addrs = (Collection)details.get(MailModule.TO);
		for (InternetAddress a : addrs) {
			helper.addTo(a);
		}
		if (addrs == null || addrs.isEmpty()) {
			if (details.containsKey(MailModule.FROM)) 
				helper.addTo((InternetAddress)details.get(MailModule.FROM));
			else
				helper.addTo(defaultFrom);
			helper.setSubject(NLT.get("errorcode.noRecipients") + " " + (String)details.get(MailModule.SUBJECT));
		}
		addrs = (Collection)details.get(MailModule.CC);
		if (addrs != null) {
			for (InternetAddress a : addrs) {
				helper.addCc(a);
			}
		}
		addrs = (Collection)details.get(MailModule.BCC);
		if (addrs != null) {
			for (InternetAddress a : addrs) {
				helper.addBcc(a);
			}
		}
		// the next line creates ALTERNATIVE part, change to setText(String, boolean)
		// which will cause error in iCalendar section (the ical is add as alternative content) 
		setText((String)details.get(MailModule.TEXT_MSG), (String)details.get(MailModule.HTML_MSG), helper);
		Collection<FileAttachment> atts = (Collection)details.get(MailModule.ATTACHMENTS);
		if (atts != null) prepareAttachments(atts, helper);
				
		if (iCals != null) {
			int c = 0;
			net.fortuna.ical4j.model.Calendar margedCalendars = null;
			for (final net.fortuna.ical4j.model.Calendar iCal : iCals) {
					String summary = ICalUtils.getSummary(iCal);
					String fileName = summary + MailModule.ICAL_FILE_EXTENSION;
					if (iCals.size() > 1) {
						fileName = summary + c + MailModule.ICAL_FILE_EXTENSION;
					}
					
					String component = getICalComponentType(iCal);
					//If okay to send todo or not a todo build alternatative
					if (sendVTODO || !Component.VTODO.equals(component)) {
						// 	attach alternative iCalendar content
						if (iCals.size() == 1 ) {
							prepareICalendar(iCal, fileName, component, true, true, helper);
						} else  {
							//always send as attachment, not alternative
							prepareICalendar(iCal, fileName, component, true, false, helper);
							if (margedCalendars == null) {
								margedCalendars = new Calendar();
							}
							margedCalendars = Calendars.merge(margedCalendars, iCal);
						}
					} else {
						//always send as attachment, not alternative
						prepareICalendar(iCal, fileName, component, true, false, helper);
					}
				
				c++;
			}
			if (margedCalendars != null) {
				//add to alternative text, attachments handled already
				prepareICalendar(margedCalendars, ICalUtils.getSummary(margedCalendars) + MailModule.ICAL_FILE_EXTENSION, getICalComponentType(margedCalendars), false, true, helper);
			}
		}

		//save message incase cannot connect and need to resend;
		message = mimeMessage;
	}

}

