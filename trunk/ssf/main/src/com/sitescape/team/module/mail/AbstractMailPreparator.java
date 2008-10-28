package com.sitescape.team.module.mail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ValidationException;

import org.apache.commons.logging.Log;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.ical.util.ICalUtils;
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
			//always send ICAL attachments, not bound by "file attachment" flag	cause part of entry fields
			
			// addAttachment && addAlternative => only alternative with attachment disposition 
			//		it prevents problem in GW: if there is alternative AND attachment then all event
			//		reccurances (but not the first one)	occure twice in calendar
			// addAttachment && !addAlternative => only attachment
			// !addAttachment && addAlternative => only alternative
			if (addAttachment && !addAlternative) {
				ByteArrayOutputStream icalOutputStream = ICalUtils.toOutputStream(iCal);				
				String contentType = getCalendarContentType(componentType, ICalUtils.getMethod(iCal));
				DataSource dataSource = createDataSource(new ByteArrayResource(icalOutputStream.toByteArray()), contentType, fileName);
				addAttachment(fileName, new DataHandler(dataSource), helper);			
			}
			if (addAlternative) {
				boolean asAttachmentDisposition = addAttachment;
				ByteArrayOutputStream icalOutputStream = ICalUtils.toOutputStream(iCal);				
				String contentType = getCalendarContentType(componentType, ICalUtils.getMethod(iCal));
				DataSource dataSource = createDataSource(new ByteArrayResource(icalOutputStream.toByteArray()), contentType, fileName);
				addAlternativeBodyPart(fileName, asAttachmentDisposition, new DataHandler(dataSource), helper);
			}
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

			try {
				helper.addAttachment(MimeUtility.encodeText(fAtt.getFileItem().getName()), ds);
			} catch (java.io.UnsupportedEncodingException  ignore) {}
		}
	}
	/**
	 * Creates alternative content miltipart and puts text or html. Boths: text and html can be <code>null</code>
	 * but alternative part is always created.
	 * 
	 * @param plainText
	 * @param htmlText
	 * @param helper
	 * @throws MessagingException
	 */
	protected void setText(String plainText, String htmlText, MimeMessageHelper helper) throws MessagingException {
		MimeMultipart messageBody = new MimeMultipart(MailModule.MULTIPART_SUBTYPE_ALTERNATIVE);
		getMainPart(helper).setContent(messageBody, MailModule.CONTENT_TYPE_ALTERNATIVE);

		if (plainText != null) {
			// Create the plain text part of the message.
			MimeBodyPart plainTextPart = new MimeBodyPart();
			setPlainTextToMimePart(plainTextPart, plainText, helper);
			messageBody.addBodyPart(plainTextPart);
		}

		if (htmlText != null) {
			// Create the HTML text part of the message.
			MimeBodyPart htmlTextPart = new MimeBodyPart();
			setHtmlTextToMimePart(htmlTextPart, htmlText, helper);
			messageBody.addBodyPart(htmlTextPart);
		}
	}
	
	private MimeBodyPart getMainPart(MimeMessageHelper helper) throws MessagingException {
		MimeMultipart mimeMultipart = helper.getMimeMultipart();
		MimeBodyPart bodyPart = null;
		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			BodyPart bp = mimeMultipart.getBodyPart(i);
			if (bp.getFileName() == null) {
				bodyPart = (MimeBodyPart) bp;
			}
		}
		if (bodyPart == null) {
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeMultipart.addBodyPart(mimeBodyPart);
			bodyPart = mimeBodyPart;
		}
		return bodyPart;
	}
	private void setPlainTextToMimePart(MimePart mimePart, String text, MimeMessageHelper helper) throws MessagingException {
		if (helper.getEncoding() != null) {
			mimePart.setText(text, helper.getEncoding());
		}
		else {
			mimePart.setText(text);
		}
	}

	private void setHtmlTextToMimePart(MimePart mimePart, String text, MimeMessageHelper helper) throws MessagingException {
		if (helper.getEncoding() != null) {
			mimePart.setContent(text, MailModule.CONTENT_TYPE_HTML + MailModule.CONTENT_TYPE_CHARSET_SUFFIX + helper.getEncoding());
		}
		else {
			mimePart.setContent(text, MailModule.CONTENT_TYPE_HTML);
		}
	}
	
	public void addAttachment(String fileName, DataHandler dataHandler, MimeMessageHelper helper) throws MessagingException {
		try {
			fileName = MimeUtility.encodeText(fileName);
		} catch (java.io.UnsupportedEncodingException  ignore) {};
		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.setDisposition(MimeBodyPart.ATTACHMENT);
		attachmentPart.setFileName(fileName);
		attachmentPart.setDataHandler(dataHandler);
		helper.getMimeMultipart().addBodyPart(attachmentPart);	
	}

	public void addAlternativeBodyPart(String fileName, boolean asAttachmentDisposition, DataHandler dataHandler, MimeMessageHelper helper) throws MessagingException, IOException {
		MimeBodyPart bodyPart = getMainPart(helper);
		MimeMultipart bodyContent = (MimeMultipart)bodyPart.getContent();
		
		MimeBodyPart alternativePart = new MimeBodyPart();
		alternativePart.setDataHandler(dataHandler);
		if (asAttachmentDisposition && fileName != null) {
			try {
				fileName = MimeUtility.encodeText(fileName);
			} catch (java.io.UnsupportedEncodingException  ignore) {};
			alternativePart.setDisposition(MimeBodyPart.ATTACHMENT);
			alternativePart.setFileName(fileName);
		}
		bodyContent.addBodyPart(alternativePart);
	}
	
	public String getCalendarContentType(String component, String method) {
		StringBuilder sb = new StringBuilder(MailModule.CONTENT_TYPE_CALENDAR);
		if (component != null) {
			sb.append(MailModule.CONTENT_TYPE_CALENDAR_COMPONENT_SUFFIX + component);
		}
		sb.append(MailModule.CONTENT_TYPE_CALENDAR_METHOD_SUFFIX + method);
		return sb.toString();
	}
	public DataSource createDataSource(
		    final InputStreamSource inputStreamSource, final String contentType, final String name) {

		return new DataSource() {
			public InputStream getInputStream() throws IOException {
				return inputStreamSource.getInputStream();
			}
			public OutputStream getOutputStream() {
				throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
			}
			public String getContentType() {
				return contentType;
			}
			public String getName() {
				return name;
			}
		};
	}


}
