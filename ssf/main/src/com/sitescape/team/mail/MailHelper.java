/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.team.ical.util.ICalUtils;
import com.sitescape.team.module.mail.FolderEmailFormatter;


public class MailHelper {
	
	public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	
	public static final String HEADER_CONTENT_TRANSFER_ENCODING_8BIT = "8bit";
	
	public static final String MULTIPART_SUBTYPE_ALTERNATIVE = "alternative";
	
	public static final String CONTENT_TYPE_ALTERNATIVE = "text/alternative";
	
	public static final String CONTENT_TYPE_HTML = "text/html";
	
	public static final String CONTENT_TYPE_CALENDAR = "text/calendar";
	
	public static final String CONTENT_TYPE_CHARSET_SUFFIX = ";charset=";
	
	public static final String CONTENT_TYPE_CALENDAR_COMPONENT_SUFFIX = "; component=";
	
	public static final String CONTENT_TYPE_CALENDAR_METHOD_SUFFIX = "; method=";
	
	public static final String ICAL_FILE_EXTENSION = ".ics";
	
	/**
	 * Creates alternative content miltipart and puts text or html. Boths: text and html can be <code>null</code>
	 * but alternative part is always created.
	 * 
	 * @param plainText
	 * @param htmlText
	 * @param helper
	 * @throws MessagingException
	 */
	public static void setText(String plainText, String htmlText, MimeMessageHelper helper) throws MessagingException {
		MimeMultipart messageBody = new MimeMultipart(MULTIPART_SUBTYPE_ALTERNATIVE);
		getMainPart(helper).setContent(messageBody, CONTENT_TYPE_ALTERNATIVE);

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
	
	private static MimeBodyPart getMainPart(MimeMessageHelper helper) throws MessagingException {
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

	private static void setPlainTextToMimePart(MimePart mimePart, String text, MimeMessageHelper helper) throws MessagingException {
		if (helper.getEncoding() != null) {
			mimePart.setText(text, helper.getEncoding());
		}
		else {
			mimePart.setText(text);
		}
	}

	private static void setHtmlTextToMimePart(MimePart mimePart, String text, MimeMessageHelper helper) throws MessagingException {
		if (helper.getEncoding() != null) {
			mimePart.setContent(text, CONTENT_TYPE_HTML + CONTENT_TYPE_CHARSET_SUFFIX + helper.getEncoding());
		}
		else {
			mimePart.setContent(text, CONTENT_TYPE_HTML);
		}
	}
	
	public static DataSource createDataSource(
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

	public static String getCalendarContentType(String component, String method) {
		StringBuilder sb = new StringBuilder(MailHelper.CONTENT_TYPE_CALENDAR);
		if (component != null) {
			sb.append(MailHelper.CONTENT_TYPE_CALENDAR_COMPONENT_SUFFIX + component);
		}
		sb.append(MailHelper.CONTENT_TYPE_CALENDAR_METHOD_SUFFIX + method);
		return sb.toString();
	}
	
	public static void addAttachment(String fileName, DataHandler dataHandler, MimeMessageHelper helper) throws MessagingException {
		MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.setDisposition(MimeBodyPart.ATTACHMENT);
		attachmentPart.setFileName(fileName);
		attachmentPart.setDataHandler(dataHandler);
		helper.getMimeMultipart().addBodyPart(attachmentPart);	
	}

	public static void addAlternativeBodyPart(DataHandler dataHandler, MimeMessageHelper helper) throws MessagingException, IOException {
		MimeBodyPart bodyPart = getMainPart(helper);
		MimeMultipart bodyContent = (MimeMultipart)bodyPart.getContent();
		
		MimeBodyPart alternativePart = new MimeBodyPart();
		alternativePart.setDataHandler(dataHandler);
		
		bodyContent.addBodyPart(alternativePart);
	}

}
