package com.sitescape.ef.jobs;

import javax.mail.internet.MimeMessage;
import java.io.File;

import com.sitescape.ef.mail.JavaMailSender;
import com.sitescape.team.domain.Binder;

public interface FailedEmail {
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_failedEmailJob";
	public static final String RETRY_GROUP="retry-send-email";

    public void schedule(Binder binder, JavaMailSender mailSender, MimeMessage mail, File fileDir);

}
