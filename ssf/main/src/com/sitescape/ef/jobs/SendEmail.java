package com.sitescape.ef.jobs;

import java.util.Map;

public interface SendEmail {
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_sendEmailJob";
	public static final String SEND_MAIL_GROUP="send-email";
	public static final String SUBJECT="SUBJECT";
	//List of InternetAddress
	public static final String TO="TO";
	public static final String TEXT_MSG="TEXT";
	public static final String HTML_MSG="HTML";
	//InternetAddress
	public static final String FROM="FROM";
	//List of InternetAddress
	public static final String CC="CC";
	
    public void schedule(String mailSenderName, Map message, String comment);

}
