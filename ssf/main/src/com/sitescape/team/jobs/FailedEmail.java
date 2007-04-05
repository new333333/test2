/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.jobs;

import javax.mail.internet.MimeMessage;
import java.io.File;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.mail.JavaMailSender;

public interface FailedEmail {
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_failedEmailJob";
	public static final String RETRY_GROUP="retry-send-email";

    public void schedule(Binder binder, JavaMailSender mailSender, MimeMessage mail, File fileDir);

}
