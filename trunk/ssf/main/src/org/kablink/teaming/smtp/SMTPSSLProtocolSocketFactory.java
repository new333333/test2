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
package org.kablink.teaming.smtp;

import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class SMTPSSLProtocolSocketFactory {

    // Log object for this class.
    private static final Log logger = LogFactory.getLog(SMTPSSLProtocolSocketFactory.class);

    private SSLContext sslContext = null;
    private final static String SMTP_TLS_PROTOCOL = "TLS";

    /**
     * Constructor for SMTPSSLProtocolSocketFactory.
     */
    public SMTPSSLProtocolSocketFactory() {
        super();
    }

    private static SSLContext createSMTPSSLContext() {
        try {
            SSLContext context = SSLContext.getInstance(SMTP_TLS_PROTOCOL);
            context.init(
              new KeyManager[] {new SMTPX509KeyManager()}, 
              new TrustManager[] {new SMTPX509TrustManager()}, 
              null);
            return context;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new HttpClientError(e.toString());
        }
    }

    public SSLContext getSSLContext() {
        if (this.sslContext == null) {
            this.sslContext = createSMTPSSLContext();
        }
        return this.sslContext;
    }
}
