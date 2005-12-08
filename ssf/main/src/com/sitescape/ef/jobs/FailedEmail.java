package com.sitescape.ef.jobs;

import javax.mail.internet.MimeMessage;
import java.io.File;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.module.mail.JavaMailSender;

public interface FailedEmail {
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_failedEmailJob";
	public static final String RETRY_NOTIFICATION_GROUP="retry-send-email-notification";

    public void schedule(Folder folder, JavaMailSender mailSender, MimeMessage mail, File fileDir);

}
