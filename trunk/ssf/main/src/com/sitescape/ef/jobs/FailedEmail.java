package com.sitescape.ef.jobs;

import javax.mail.internet.MimeMessage;
import java.io.File;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.mail.JavaMailSender;

public interface FailedEmail {
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_failedEmailJob";
	public static final String RETRY_GROUP="retry-send-email";

    public void schedule(Binder binder, String zoneName, JavaMailSender mailSender, MimeMessage mail, File fileDir);

}
