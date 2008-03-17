package com.sitescape.team.module.mail;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.util.Calendars;

import org.apache.commons.logging.Log;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.team.calendar.TimeZoneHelper;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.definition.notify.Notify;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.util.Validator;


public class MimeNotifyPreparator extends AbstractMailPreparator {
	FolderEmailFormatter processor;
	Folder folder;
	Collection toAddrs;
	Collection entries;
	FolderEntry entry;
	Notify.NotifyType messageType;
	Date startDate;
	Locale locale;
	TimeZone timezone;
	boolean sendAttachments=false;
	boolean sendVTODO;
	IcalModule icalModule;
	public MimeNotifyPreparator(FolderEmailFormatter processor, Folder folder, Date startDate, Log logger, boolean sendVTODO) {
		super(logger);
		this.processor = processor;
		this.folder = folder;
		this.startDate = startDate;	
		this.sendVTODO = sendVTODO;
		icalModule = (IcalModule)SpringContextUtil.getBean("icalModule");
	}
	public void setToAddrs(Collection toAddrs) {
		this.toAddrs = toAddrs;			
	}
	public void setEntry(FolderEntry entry) {
		this.entry = entry;		
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setSendAttachments(boolean sendAttachments) {
		this.sendAttachments = sendAttachments;
	}
	public void setEntries(Collection entries) {
		this.entries = entries;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public void setTimeZone(String timezone) {			
		this.timezone = TimeZoneHelper.getTimeZone(timezone);
	}
	public void setType(Notify.NotifyType type) {
		messageType = type;
	}
	public void prepare(MimeMessage mimeMessage) throws MessagingException {
		//make sure nothing saved yet
		Notify notify = new Notify(messageType, locale, timezone, startDate);
		notify.setAttachmentsIncluded(sendAttachments);
				
		message = null;
		Map result=null;
		//set up events here
		if (!messageType.equals(Notify.NotifyType.text)) {
			if (entry != null) {
				result = processor.buildNotificationMessage(folder, (FolderEntry)entry, notify);
			} else {
				result = processor.buildNotificationMessage(folder, entries, notify);
			}
		}
		int multipartMode = MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

		if (notify.getEvents() != null && notify.getEvents().entrySet().size() > 0) {
			// Need to attach icalendar as alternative content,
			// if there is more then one icals then
			// all are merged and add ones to email as alternative content
			multipartMode = MimeMessageHelper.MULTIPART_MODE_MIXED;
		}
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipartMode);
		mimeMessage.addHeader(MailModule.HEADER_CONTENT_TRANSFER_ENCODING, MailModule.HEADER_CONTENT_TRANSFER_ENCODING_8BIT);
		helper.setSubject(processor.getSubject(folder, (FolderEntry)entry, notify));
		for (Iterator iter=toAddrs.iterator();iter.hasNext();) {
			String email = (String)iter.next();
			try	{
				if (!Validator.isNull(email)) helper.addTo(email);
			} catch (AddressException ae) {
				logger.error("Skipping email notifications for " + email + " Bad email address");
			}
		}
		String from = processor.getFrom(folder, notify);
		if (Validator.isNull(from)) {
			from = defaultFrom;
		}
		helper.setFrom(from);

		if (!messageType.equals(Notify.NotifyType.text)) {
			//use MailHelper so alternative part added for calendars
			setText(null, (String)result.get(FolderEmailFormatter.HTML), helper);
			if (sendAttachments) prepareAttachments(notify.getAttachments(), helper);
			notify.clearAttachments();
			prepareICalendars(notify, helper);
		} else {
			//just a subject line
			setText("", "", helper);
		}
		
		//save message incase cannot connect and need to resend;
		message = mimeMessage;
	}
	
	private void prepareICalendars(Notify notify, MimeMessageHelper helper) throws MessagingException {
		int c = 0;
		int eventsSize = notify.getEvents().size();
		Calendar margedCalendars = null;
		for (Iterator entryEventsIt = notify.getEvents().entrySet().iterator(); entryEventsIt.hasNext();) { 
				Map.Entry mapEntry = (Map.Entry)entryEventsIt.next();
				DefinableEntity entry = (DefinableEntity)mapEntry.getKey();
				List events = (List)mapEntry.getValue();
				Calendar iCal = icalModule.generate(entry, events, timezone.getID());
				
				String fileName = entry.getTitle() + MailModule.ICAL_FILE_EXTENSION;
				if (eventsSize > 1) {
					fileName = entry.getTitle() + c + MailModule.ICAL_FILE_EXTENSION;
				}
				
				String component = getICalComponentType(iCal);
				//If okay to send todo or not a todo build alternatative
				if (false) {
				//	if (sendVTODO || !Component.VTODO.equals(component)) {
					// 	attach alternative iCalendar content
					if (eventsSize == 1 && Notify.NotifyType.full.equals(messageType)) {
						//always send as attachment and alternative text
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
			prepareICalendar(margedCalendars, folder.getTitle() + MailModule.ICAL_FILE_EXTENSION, getICalComponentType(margedCalendars), false, true, helper);
		}
		
		notify.clearEvents();
	}
		

}
