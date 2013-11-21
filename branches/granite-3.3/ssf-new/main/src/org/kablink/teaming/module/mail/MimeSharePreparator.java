/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.util.Collection;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.MiscUtil;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Mime preparator for sending share notification emails.
 * 
 * @author drfoster@novell
 */
@SuppressWarnings({"unchecked"})
public class MimeSharePreparator extends AbstractMailPreparator {
	private Map m_details;		//

	/**
	 * Constructor method.
	 * 
	 * @param details
	 * @param logger
	 */
	public MimeSharePreparator(Map details, Log logger) {
		// Initialize the super class...
		super(logger);
		
		// ...and store the parameter.
		m_details = details;
	}

	/**
	 * Prepares the mime message for sending.
	 * 
	 * @param mimeMessage
	 */
	@Override
	public void prepare(MimeMessage mimeMessage) throws MessagingException {
		// Make sure nothing is saved yet.
		message = null;
		
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, MailModule.CONTENT_ENCODING);
		mimeMessage.addHeader(MailModule.HEADER_CONTENT_TRANSFER_ENCODING, MailModule.HEADER_CONTENT_TRANSFER_ENCODING_8BIT);
		
		helper.setSubject((String)          m_details.get(MailModule.SUBJECT));
		helper.setFrom(   (InternetAddress) m_details.get(MailModule.FROM)   );
		
		Collection<InternetAddress> addrsTo  = ((Collection) m_details.get(MailModule.TO) );
		Collection<InternetAddress> addrsCc  = ((Collection) m_details.get(MailModule.CC) );
		Collection<InternetAddress> addrsBcc = ((Collection) m_details.get(MailModule.BCC));
		
		Collection<InternetAddress> validAddrs;
		if (((null == addrsTo) || addrsTo.isEmpty()) && ((null == addrsBcc) || addrsBcc.isEmpty())) {
			helper.setTo((InternetAddress)m_details.get(MailModule.FROM));
			helper.setSubject(NLT.get("errorcode.noRecipients") + " " + (String)m_details.get(MailModule.SUBJECT));
		}
		
		else if ((null != addrsTo) && (!(addrsTo.isEmpty()))) {
			validAddrs = MiscUtil.validateInternetAddressCollection(MailModule.TO, addrsTo);
			helper.setTo(validAddrs.toArray(new InternetAddress[validAddrs.size()]));			
		}
		
		if ((null != addrsCc) && (!(addrsCc.isEmpty()))) {
			validAddrs = MiscUtil.validateInternetAddressCollection(MailModule.CC, addrsCc);
			helper.setCc(validAddrs.toArray(new InternetAddress[validAddrs.size()]));
		}
		
		if ((null != addrsBcc) && (!(addrsBcc.isEmpty()))) {
			validAddrs = MiscUtil.validateInternetAddressCollection(MailModule.BCC, addrsBcc);
			helper.setBcc(validAddrs.toArray(new InternetAddress[validAddrs.size()]));
		}
		
		// Create the ALTERNATIVE parts. 
		setText(
			((String) m_details.get(MailModule.TEXT_MSG)),
			((String) m_details.get(MailModule.HTML_MSG)),
			helper);
		
		// Save message in case we cannot connect and need to re-send.
		message = mimeMessage;
	}
}

