/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.mail;

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
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.ical.util.ICalUtils;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.util.ByteArrayResource;
import org.kablink.teaming.util.Utils;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * ?
 * 
 * @author ?
 */
public class AbstractMailPreparator implements MimeMessagePreparator {
	protected Log logger;
	protected String defaultFrom;
	protected MimeMessage message;

	protected AbstractMailPreparator(Log logger) {
		this.logger = logger;
	}
	
	@Override
	public void prepare(MimeMessage mimeMessage) throws MessagingException {};
	
	@Override
	public void setDefaultFrom(String from) {
		this.defaultFrom = from;
	}
	
	@Override
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
			// Always send ICAL attachments, not bound by file
			// attachment flag cause part of entry fields.
			//
			// Old logic:
			//    addAttachment && addAlternative => only alternative with attachment disposition 
			//		   it prevents problem in GW: if there is alternative AND attachment then all event
			//		   Recurrences (but not the first one) occurs twice in calendar
			//    Turns out the GW only had a problem if it went as
			//    both.  If it goes only as an attachment, all seems ok.
			//
			// Current logic:
			//     addAttachment &&  addAlternative => Only attachment.
			//     addAttachment && !addAlternative => Only attachment.
			//    !addAttachment &&  addAlternative => Only alternative.
			//    !addAttachment && !addAlternative => Neither attachment or alternative.
			if (addAttachment) {
				ByteArrayOutputStream icalOutputStream = ICalUtils.toOutputStream(iCal);				
				String contentType = getCalendarContentType(componentType, ICalUtils.getMethod(iCal));
				DataSource dataSource = createDataSource(new ByteArrayResource(icalOutputStream.toByteArray()), contentType, fileName);
				addAttachment(
					fileName,
					new DataHandler(dataSource),
					helper,
					true);	// true -> We adding an iCal attachment.  Bugzilla 943599:  Disables adding of 'Content-disposition:  attachment; filename=...'.			
			}
			
			else if (addAlternative) {
				boolean asAttachmentDisposition = addAttachment;
				ByteArrayOutputStream icalOutputStream = ICalUtils.toOutputStream(iCal);				
				String contentType = getCalendarContentType(componentType, ICalUtils.getMethod(iCal));
				DataSource dataSource = createDataSource(new ByteArrayResource(icalOutputStream.toByteArray()), contentType, fileName);
				addAlternativeBodyPart(
					fileName,
					asAttachmentDisposition,
					new DataHandler(dataSource),
					helper,
					true);	// true -> We adding an iCal body part.  Bugzilla 943599:  Disables adding of 'Content-disposition:  attachment; filename=...'.
			}
		}
		
		catch (IOException e) {
			logger.error(e);
		}
		
		catch (ValidationException e) {
			logger.error("Invalid calendar", e);
		}
	}


	protected void prepareAttachments(Collection<FileAttachment> fileAttachments, MimeMessageHelper helper) throws MessagingException {
		if (Utils.testSendMailAttachmentsSize(fileAttachments)) {
			for (FileAttachment fAtt: fileAttachments) {
				FolderEntry entry = (FolderEntry)fAtt.getOwner().getEntity();
				DataSource ds = RepositoryUtil.getDataSourceVersioned(fAtt, entry.getParentFolder(), 
							entry, helper.getFileTypeMap());
				//See if this file attachment is too large to send
				if (Utils.testSendMailAttachmentSize(fAtt)) {
					//Files size is ok to send
					try {
						helper.addAttachment(MimeUtility.encodeText(fAtt.getFileItem().getName()), ds);
					} catch (java.io.UnsupportedEncodingException  ignore) {}
				}
			}
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
	
	public void addAttachment(String fileName, DataHandler dataHandler, MimeMessageHelper helper, boolean attrIsICal) throws MessagingException {
		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.setDataHandler(dataHandler);
		if (!attrIsICal) {
			// Bugzilla 943599:  We don't add 'Content-disposition:
			// attachment; filename=...' for iCal attachments.
			attachmentPart.setDisposition(MimeBodyPart.ATTACHMENT         );
			attachmentPart.setFileName(   getMimeEncodedFileName(fileName));
		}
		helper.getMimeMultipart().addBodyPart(attachmentPart);	
	}
	
	public void addAttachment(String fileName, DataHandler dataHandler, MimeMessageHelper helper) throws MessagingException {
		// Always use the initial form of the method.
		addAttachment(fileName, dataHandler, helper, false);	// false -> Not an iCal.
	}

	public void addAlternativeBodyPart(String fileName, boolean asAttachmentDisposition, DataHandler dataHandler, MimeMessageHelper helper, boolean attrIsICal) throws MessagingException, IOException {
		MimeBodyPart bodyPart = getMainPart(helper);
		MimeMultipart bodyContent = ((MimeMultipart) bodyPart.getContent());
		
		MimeBodyPart alternativePart = new MimeBodyPart();
		alternativePart.setDataHandler(dataHandler);
		if (asAttachmentDisposition && fileName != null) {
			if (!attrIsICal) {
				// Bugzilla 943599:  We don't add 'Content-disposition:
				// attachment; filename=...' for iCal attachments.
				alternativePart.setDisposition(MimeBodyPart.ATTACHMENT         );
				alternativePart.setFileName(   getMimeEncodedFileName(fileName));
			}
		}
		bodyContent.addBodyPart(alternativePart);
	}
	
	public void addAlternativeBodyPart(String fileName, boolean asAttachmentDisposition, DataHandler dataHandler, MimeMessageHelper helper) throws MessagingException, IOException {
		// Always use the initial form of the method.
		addAlternativeBodyPart(fileName, asAttachmentDisposition, dataHandler, helper, false);	// false -> Not an iCal.
	}
	
	private String getMimeEncodedFileName(String fileName) {
		try {
			fileName = MimeUtility.encodeText(fileName);
		} catch (java.io.UnsupportedEncodingException  ignore) {};
		return fileName;
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
			@Override
			public InputStream getInputStream() throws IOException {
				return inputStreamSource.getInputStream();
			}
			@Override
			public OutputStream getOutputStream() {
				throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
			}
			@Override
			public String getContentType() {
				return contentType;
			}
			@Override
			public String getName() {
				return name;
			}
		};
	}


}
