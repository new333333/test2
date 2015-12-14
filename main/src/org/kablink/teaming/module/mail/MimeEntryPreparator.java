/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Validator;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MimeEntryPreparator extends MimeNotifyPreparator {
	@SuppressWarnings("unchecked")
	Map details;
	
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	protected void setToAddrs(MimeMessageHelper helper) throws MessagingException {
		// Validate the To:, Cc: and Bcc: addresses.
		Collection<InternetAddress> toAddrs  = MiscUtil.validateInternetAddressCollection(MailModule.TO,  ((Collection) details.get(MailModule.TO)));
		Collection<InternetAddress> ccAddrs  = MiscUtil.validateInternetAddressCollection(MailModule.CC,  ((Collection) details.get(MailModule.CC)));
		Collection<InternetAddress> bccAddrs = MiscUtil.validateInternetAddressCollection(MailModule.BCC, ((Collection) details.get(MailModule.BCC)));

		// Do we have any To:, Cc: or Bcc: addresses?
		boolean hasTo  = ((null != toAddrs)  && (!(toAddrs.isEmpty())));
		boolean hasCc  = ((null != ccAddrs)  && (!(ccAddrs.isEmpty())));
		boolean hasBcc = ((null != bccAddrs) && (!(bccAddrs.isEmpty())));
		if ((!hasTo) && (!hasCc) && (!hasBcc)) {
			// No!  The send the email to the from only.
			if (details.containsKey(MailModule.FROM)) 
				 helper.setTo((InternetAddress)details.get(MailModule.FROM));
			else helper.setTo(defaultFrom);
			
		} else {
			// Yes, we have some To:'s, Cc:'s or Bcc:'s!  Handle them
			// accordingly.
			if (hasTo)  helper.setTo( toAddrs.toArray( new InternetAddress[toAddrs.size()]));
			if (hasCc)  helper.setCc( ccAddrs.toArray( new InternetAddress[ccAddrs.size()]));
			if (hasBcc) helper.setBcc(bccAddrs.toArray(new InternetAddress[bccAddrs.size()]));
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
