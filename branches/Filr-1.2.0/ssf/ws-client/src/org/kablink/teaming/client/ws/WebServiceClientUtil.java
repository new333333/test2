/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.client.ws;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.xml.soap.SOAPException;

import org.apache.axis.EngineConfiguration;
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
			stub._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PW_TEXT);
			stub._setProperty(WSHandlerConstants.PW_CALLBACK_REF,
					new PasswordCallbackText(password));
		}
		else {
			stub._setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PW_DIGEST);
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
		attachSource(stub, new FileDataSource(file));
	}
	
	/**
	 * Attach a data source to the outbound message.
	 * 
	 * @param stub
	 * @param source
	 */
	public static void attachSource(Stub stub, DataSource source) {
		DataHandler dhSource = new DataHandler(source);
		
		stub.addAttachment(dhSource); // add the file
    
		// Use DIME for the attachment format (the default is MIME in Axis)
		
		// 10/15/2008 - Do not use DIME format but use the default MIME.
		// Apparently, due to a bug in the tool, the attachment stream is prematurely 
		// cut off, when sent in DIME format. This seems to happen only with large
		// attachments that exceed 1M in size. When that happens, the system throws
		// an error that looks like the following: 
		// java.io.IOException: End of physical stream detected when 202 more bytes  expected.

		//stub._setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
		
	    // To use MTOM format - I don't know if this actually works.
		//stub._setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_MTOM);
	}
	
	/**
	 * Extract attached file(s), if any, from the inbound message.
	 * 
	 * @param stub
	 * @throws SOAPException 
	 * @throws IOException 
	 */
	public static int extractFiles(Stub stub, File attachmentDir) throws SOAPException, IOException {
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
					move(src, (attachmentDir == null)? new File(s) : new File(attachmentDir, s));
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
			call.setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PW_TEXT);
			call.setProperty(WSHandlerConstants.PW_CALLBACK_REF,
					new PasswordCallbackText(password));
		}
		else {
			call.setProperty(UsernameToken.PASSWORD_TYPE, WSConstants.PW_DIGEST);
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
		attachSource(call, new FileDataSource(file));
	}
	
	/**
	 * Attach a data source to the outbound message.
	 * 
	 * @param stub
	 * @param source
	 */
	public static void attachSource(Call call, DataSource source) {
		DataHandler dhSource = new DataHandler(source);
		
		call.addAttachmentPart(dhSource); // add the file
    
		// Use DIME for the attachment format (the default is MIME in Axis)
		//call.setProperty(Call.ATTACHMENT_ENCAPSULATION_FORMAT, Call.ATTACHMENT_ENCAPSULATION_FORMAT_DIME);
	}
	
	/**
	 * Extract attached file(s), if any, from the inbound message.
	 * 
	 * @param stub
	 * @param attachmentDir directory into which to extract the attached files. 
	 * if the directory doesn't exist, it will be created;
	 * if <code>null</code> is passed, current directory is assumed.
	 * @throws SOAPException 
	 * @throws IOException 
	 */
	public static int extractFiles(Call call, File attachmentDir) throws SOAPException, IOException {
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
				move(src, (attachmentDir == null)? new File(s) : new File(attachmentDir, s));
				count++;
			}
		}
		return count;
	}

	/**
	 * Returns the minimum engine configuration needed for WS-Security authentication.
	 * If you need additional or custom configuration, you can either store it in a
	 * config file and load it at runtime or supply your own version of in-line
	 * configuration.
	 * @return
	 */
	public static EngineConfiguration getMinimumEngineConfigurationWSSecurity() {
	    java.lang.StringBuilder sb = new java.lang.StringBuilder();
	    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
	    sb.append("<deployment xmlns=\"http://xml.apache.org/axis/wsdd/\" xmlns:java=\"http://xml.apache.org/axis/wsdd/providers/java\">\r\n");
	    sb.append("<transport name=\"http\" pivot=\"java:org.apache.axis.transport.http.CommonsHTTPSender\" />\r\n");
	    sb.append("<globalConfiguration >\r\n");
	    //sb.append("<parameter name=\"dotNetSoapEncFix\" value=\"true\"/>\r\n");
	    sb.append("<requestFlow >\r\n");
	    sb.append("<handler type=\"java:org.apache.ws.axis.security.WSDoAllSender\" >\r\n");
	    sb.append("<parameter name=\"action\" value=\"UsernameToken\"/>\r\n");
	    sb.append("</handler>\r\n");
	    sb.append("</requestFlow >\r\n");
	    sb.append("</globalConfiguration >\r\n");
	    sb.append("</deployment>\r\n");
	    return new org.apache.axis.configuration.XMLStringProvider(sb.toString());
	}
	
	private static void move(File source, File destination) throws IOException {
		if(!source.exists())
			throw new IOException("Could not find source file " + source.getAbsolutePath());
		
		if(rename(source, destination))
			return;
		
		// we could not rename; try copying the content
		copy(source, destination);
	}
	
	private static void delete(File file) throws IOException {
		if(!file.exists())
			return;
		
		int DELETE_MAX_TRIAL = 3;
		
		int count = 1;
		while(count <= DELETE_MAX_TRIAL) {
			if(file.delete())
				return;
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException e) {}
			count++;
		}

        throw new IOException("Could not delete file " + file.getAbsolutePath());
	}
	
	private static boolean rename(File source, File destination) throws IOException {
		delete(destination);
		
		int RENAME_MAX_TRIAL = 3;
		
		int count = 1;
		while(count <= RENAME_MAX_TRIAL) {
			if(source.renameTo(destination)) {
				//System.out.println(source.getAbsolutePath() + " renamed to " + destination.getAbsolutePath() + " on the " + count + " try");
				return true;
			}
			try {
				Thread.sleep(10);
			}
			catch(InterruptedException e) {}
			count++;
		}
		return false;
	}

	private static void copy(File in, File out) throws IOException {
		copy(new BufferedInputStream(new FileInputStream(in)),
		    new BufferedOutputStream(new FileOutputStream(out)));
		//System.out.println(in.getAbsolutePath() + " copied to " + out.getAbsoluteFile());
	}
	
	private static void copy(InputStream in, OutputStream out) throws IOException {
		int BUFFER_SIZE = 4096;
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
			return;
		}
		finally {
			try {
				in.close();
			}
			catch (IOException ex) {}
			try {
				out.close();
			}
			catch (IOException ex) {}
		}
	}

}
