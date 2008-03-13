package com.sitescape.team.module.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.commons.logging.Log;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.ical.util.ICalUtils;
import com.sitescape.team.mail.MailHelper;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.ByteArrayResource;

public class AbstractMailPreparator implements MimeMessagePreparator {
	protected Log logger;
	protected String defaultFrom;
	protected MimeMessage message;

	protected AbstractMailPreparator(Log logger) {
		this.logger = logger;
	}
	public void prepare(MimeMessage mimeMessage) throws MessagingException {};
	public void setDefaultFrom(String from) {
		this.defaultFrom = from;
	}
	public MimeMessage getMessage() {
		return message;
	}
	protected String getICalComponentType(Calendar iCal) {
		if (!iCal.getComponents(Component.VTODO).isEmpty()) {
			return Component.VTODO;
		}
		return null;
	}
		
	protected void prepareICalendar(Calendar iCal, String fileName, String componentType, boolean addAttachment, boolean addAlternative, MimeMessageHelper helper) throws MessagingException {
		try {				
			ByteArrayOutputStream icalOutputStream = ICalUtils.toOutputStraem(iCal);				
			String contentType = MailHelper.getCalendarContentType(componentType, ICalUtils.getMethod(iCal));
			DataSource dataSource = MailHelper.createDataSource(new ByteArrayResource(icalOutputStream.toByteArray()), contentType, fileName);
			//always send ICAL attachments, not bound by "file attachment" flag	cause part of entry fields
			if (addAttachment) MailHelper.addAttachment(fileName, new DataHandler(dataSource), helper);			
			if (addAlternative) MailHelper.addAlternativeBodyPart(new DataHandler(dataSource), helper);
		} catch (IOException e) {
			logger.error(e);
		} catch (ValidationException e) {
			logger.error("Invalid calendar", e);
		}
	}


	protected void prepareAttachments(Collection<FileAttachment> fileAttachments, MimeMessageHelper helper) throws MessagingException {
		for (FileAttachment fAtt: fileAttachments) {
			FolderEntry entry = (FolderEntry)fAtt.getOwner().getEntity();
			DataSource ds = RepositoryUtil.getDataSourceVersioned(fAtt.getRepositoryName(), entry.getParentFolder(), 
						entry, fAtt.getFileItem().getName(), fAtt.getHighestVersion().getVersionName(), helper.getFileTypeMap());

			helper.addAttachment(fAtt.getFileItem().getName(), ds);
		}
	}

}
