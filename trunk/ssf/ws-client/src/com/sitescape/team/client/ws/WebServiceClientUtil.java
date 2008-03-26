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
package com.sitescape.team.client.ws;

import java.io.File;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.soap.SOAPException;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.message.token.UsernameToken;

public class WebServiceClientUtil {
	
	/**
	 * Sets user credential appropriate for Basic Authentication.
	 * 
	 * @param stub
	 * @param username user login name
	 * @param password password in clear text
	 */
	public static void setUserCredentialBasicAuth(Stub stub, String username, String password) {
		stub.setUsername(username);
		stub.setPassword(password);
	}
	
	/**
	 * Sets user credential appropriate for WS-Security authentication.
	 * 
	 * @param stub
	 * @param username user login name
	 * @param password password in clear text
	 * @param passwordText specifies the type of password to transmit; <code>true</code>
	 * if the actual password to be used (wsse:PasswordText), <code>false</code> if
	 * the digest of the password to be used (wsse:PasswordDigest).
	 */
	public static void setUserCredentialWSSecurity(Stub stub, String username, String password, boolean passwordText) {
		if(passwordText) {
			stub._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
			stub._setProperty(WSHandlerConstants.PW_CALLBACK_REF,
					new PasswordCallbackText(password));
		}
		else {
			stub._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_DIGEST);
			stub._setProperty(WSHandlerConstants.PW_CALLBACK_REF,
					new PasswordCallbackDigest(password));
		}
		
		stub._setProperty(WSHandlerConstants.USER, username);
	}

	/**
	 * Attach a file to the outbound message.
	 * 
	 * @param stub
	 * @param file
	 */
	public static void attachFile(Stub stub, File file) {
		DataHandler dhSource = new DataHandler(new FileDataSource(file));
		
		stub.addAttachment(dhSource); // add the file
    
		// Use DIME for the attachment format (the default is MIME in Axis)
		stub._setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
	}
	
	/**
	 * Extract attached file(s), if any, from the inbound message.
	 * 
	 * @param stub
	 * @throws SOAPException 
	 */
	public static int extractFiles(Stub stub, File attachmentDir) throws SOAPException {
		Object[] atts = stub.getAttachments();
		int count = 0;
		if(atts != null) {
			DataHandler[] dhTab = new DataHandler[atts.length];
			for(int i = 0; i < atts.length; i++) {
				AttachmentPart ap = (AttachmentPart) atts[i];
				dhTab[i] = ap.getDataHandler();
				if(ap.getMimeHeader("Content-Disposition") != null) {
					if(attachmentDir != null)
						attachmentDir.mkdirs();
					String s = ap.getMimeHeader("Content-Disposition")[0];
					s = s.substring(s.indexOf('"')+1, s.lastIndexOf('"'));
					//System.out.println("Attachment:" + s);
					File src = new File(dhTab[i].getName());
					src.renameTo((attachmentDir == null)? new File(s) : new File(attachmentDir, s));
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Sets user credential appropriate for Basic Authentication.
	 * 
	 * @param stub
	 * @param username user login name
	 * @param password password in clear text
	 */
	public static void setUserCredentialBasicAuth(Call call, String username, String password) {
		call.setUsername(username);
		call.setPassword(password);
	}
	
	/**
	 * Sets user credential appropriate for WS-Security authentication.
	 * 
	 * @param stub
	 * @param username user login name
	 * @param password password in clear text
	 * @param passwordText specifies the type of password to transmit; <code>true</code>
	 * if the actual password to be used (wsse:PasswordText), <code>false</code> if
	 * the digest of the password to be used (wsse:PasswordDigest).
	 */
	public static void setUserCredentialWSSecurity(Call call, String username, String password, boolean passwordText) {
		if(passwordText) {
			call.setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_TEXT);
			call.setProperty(WSHandlerConstants.PW_CALLBACK_REF,
					new PasswordCallbackText(password));
		}
		else {
			call.setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PASSWORD_DIGEST);
			call.setProperty(WSHandlerConstants.PW_CALLBACK_REF,
					new PasswordCallbackDigest(password));
		}
		
		call.setProperty(WSHandlerConstants.USER, username);
	}

	/**
	 * Attach a file to the outbound message.
	 * 
	 * @param stub
	 * @param file
	 */
	public static void attachFile(Call call, File file) {
		DataHandler dhSource = new DataHandler(new FileDataSource(file));
		
		call.addAttachmentPart(dhSource); // add the file
    
		// Use DIME for the attachment format (the default is MIME in Axis)
		call.setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
	}
	
	/**
	 * Extract attached file(s), if any, from the inbound message.
	 * 
	 * @param stub
	 * @param attachmentDir directory into which to extract the attached files. 
	 * if the directory doesn't exist, it will be created;
	 * if <code>null</code> is passed, current directory is assumed.
	 * @throws SOAPException 
	 */
	public static int extractFiles(Call call, File attachmentDir) throws SOAPException {
		org.apache.axis.MessageContext messageContext = call.getMessageContext();
		org.apache.axis.Message returnedMessage = messageContext.getResponseMessage();
		Iterator iteAtta = returnedMessage.getAttachments();
		DataHandler[] dhTab = new DataHandler[returnedMessage.countAttachments()];
		int count = 0;
		for (int i=0;iteAtta.hasNext();i++) {
			AttachmentPart ap = (AttachmentPart) iteAtta.next();
			dhTab[i] = ap.getDataHandler();
			if(ap.getMimeHeader("Content-Disposition") != null) {
				if(attachmentDir != null)
					attachmentDir.mkdirs();
				String s = ap.getMimeHeader("Content-Disposition")[0];
				s = s.substring(s.indexOf('"')+1, s.lastIndexOf('"'));
				//System.out.println("Attachment:" + s);
				File src = new File(dhTab[i].getName());
				src.renameTo((attachmentDir == null)? new File(s) : new File(attachmentDir, s));
				count++;
			}
		}
		return count;
	}

}
