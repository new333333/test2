/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.util.Calendars;

import org.apache.commons.logging.Log;

import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.ical.util.ICalUtils;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.MiscUtil;

import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class MimeMapPreparator extends AbstractMailPreparator {
	Map details;
	boolean sendVTODO;
	
	public MimeMapPreparator(Map details, Log logger, boolean sendVTODO) {
		super(logger);
		this.details = details;
		this.sendVTODO = sendVTODO;
	}
	
	@Override
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
	
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipartMode, MailModule.CONTENT_ENCODING);
		mimeMessage.addHeader(MailModule.HEADER_CONTENT_TRANSFER_ENCODING, MailModule.HEADER_CONTENT_TRANSFER_ENCODING_8BIT);
		helper.setSubject((String)details.get(MailModule.SUBJECT));
		if (details.containsKey(MailModule.FROM)) 
			helper.setFrom((InternetAddress)details.get(MailModule.FROM));
		else
			helper.setFrom(defaultFrom);
		
		Collection<InternetAddress> addrsTo = (Collection)details.get(MailModule.TO);
		Collection<InternetAddress> addrsCc = (Collection)details.get(MailModule.CC);
		Collection<InternetAddress> addrsBcc = (Collection)details.get(MailModule.BCC);
		Collection<InternetAddress> validAddrs;
		if ((addrsTo == null || addrsTo.isEmpty()) && (addrsCc == null || addrsCc.isEmpty()) && (addrsBcc == null || addrsBcc.isEmpty())) {
			if (details.containsKey(MailModule.FROM)) 
				helper.setTo((InternetAddress)details.get(MailModule.FROM));
			else
				helper.setTo(defaultFrom);
			helper.setSubject(NLT.get("errorcode.noRecipients") + " " + (String)details.get(MailModule.SUBJECT));
		} else {
			//Using 1 set results in 1 TO: line in mime-header - GW like this better
			validAddrs = MiscUtil.validateInternetAddressCollection(MailModule.TO, addrsTo);
			helper.setTo(validAddrs.toArray(new InternetAddress[validAddrs.size()]));			
		}
		if (addrsCc != null) {
			validAddrs = MiscUtil.validateInternetAddressCollection(MailModule.CC, addrsCc);
			helper.setCc(validAddrs.toArray(new InternetAddress[validAddrs.size()]));
		}
		if (addrsBcc != null) {
			validAddrs = MiscUtil.validateInternetAddressCollection(MailModule.BCC, addrsBcc);
			helper.setBcc(validAddrs.toArray(new InternetAddress[validAddrs.size()]));
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
				// Add to alternative text, attachments handled
				// already.
				prepareICalendar(margedCalendars, ICalUtils.getSummary(margedCalendars) + MailModule.ICAL_FILE_EXTENSION, getICalComponentType(margedCalendars), false, true, helper);
			}
		}

		// Save message in case cannot connect and need to re-send.
		message = mimeMessage;
	}
}
