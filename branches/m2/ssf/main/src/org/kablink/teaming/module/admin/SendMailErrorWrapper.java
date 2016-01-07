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
package org.kablink.teaming.module.admin;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.web.util.MiscUtil;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;

/**
 * Class use to encapsulate information about an error sending email. 
 * 
 * @author drfoster@novell.com
 */
public class SendMailErrorWrapper {
	private Exception	m_exception;	//
	private String		m_errorMessage;	//

	/**
	 * Constructor method.
	 * 
	 * @param exception
	 * @param errorMessage
	 */
	public SendMailErrorWrapper(Exception exception, String errorMessage) {
		// Initialize the super class...
		super();

		// ...and store the parameters.
		setException(   exception   );
		setErrorMessage(errorMessage);
	}

	/**
	 * Constructor method.
	 * 
	 * @param errorMessage
	 */
	public SendMailErrorWrapper(String errorMessage) {
		// Always use the initial form of the constructor.
		this(((Exception) null), errorMessage);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Exception getException()     {return m_exception;   }
	public String    getErrorMessage()  {return m_errorMessage;}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setException(   Exception exception)    {m_exception    = exception;   }
	public void setErrorMessage(String    errorMessage) {m_errorMessage = errorMessage;}

	/**
	 * Extract the error messages out of a List<SendMailErrorWrapper>
	 * and returns them as a List<String>.
	 * 
	 * @param sendMailErrors
	 * 
	 * @return
	 */
	public static List<String> getErrorMessages(List<SendMailErrorWrapper> sendMailErrors) {
		List<String> reply = new ArrayList<String>();
		if (MiscUtil.hasItems(sendMailErrors)) {
			for (Object sendMailErrorO:  sendMailErrors) {
				String error;
				if      (sendMailErrorO instanceof SendMailErrorWrapper) error = ((SendMailErrorWrapper) sendMailErrorO).getErrorMessage();
				else if (sendMailErrorO instanceof String)               error = ((String)               sendMailErrorO);
				else                                                     error =                         sendMailErrorO.toString();
				reply.add(error);
			}
		}
		return reply;
	}
	
	/**
	 * If the exception causing the error is a
	 * MailAuthenticationException, it's returned.  Otherwise, null is
	 * returned.
	 * 
	 * @return
	 */
	public MailAuthenticationException getMailAuthenticationException() {
		if (isMailAuthenticationException()) {
			return ((MailAuthenticationException) m_exception);
		}
		return null;
	}
	
	/**
	 * If the exception causing the error is a MailSendException, it's
	 * returned.  Otherwise, null is returned.
	 * 
	 * @return
	 */
	public MailSendException getMailSendException() {
		if (isMailSendException()) {
			return ((MailSendException) m_exception);
		}
		return null;
	}

	/**
	 * Returns true if the exception causing the error is a
	 * MailAuthenticationException and false otherwise.
	 * 
	 * @return
	 */
	public boolean isMailAuthenticationException() {
		return ((null != m_exception) && (m_exception instanceof MailAuthenticationException));
	}
	
	/**
	 * Returns true if the exception causing the error is a
	 * MailSendException and false otherwise.
	 * 
	 * @return
	 */
	public boolean isMailSendException() {
		return ((null != m_exception) && (m_exception instanceof MailSendException));
	}
}
