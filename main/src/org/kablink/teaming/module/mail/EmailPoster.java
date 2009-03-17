package org.kablink.teaming.module.mail;

import java.util.List;

import javax.mail.Message;
import javax.mail.Session;

import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.PostingDef;


public interface EmailPoster {
    public static final String PROCESSOR_KEY = "processorKey_emailPoster";
	public List postMessages(Folder folder, String recipient, Message[] msgs, Session session);

}
