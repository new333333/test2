package com.sitescape.ef.module.mail;
import java.util.Collection;
import java.util.Map;
import java.util.Date;
import java.util.List;
import javax.mail.Message;
import javax.mail.Session;

import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.PostingDef;
/**
 * Interface to define 
 * @author Janet McCann
 *
 */
public interface FolderEmailFormatter {
    public static final String PROCESSOR_KEY = "processorKey_folderEmailFormatter";
    public static final String PLAIN="plain";
    public static final String HTML="html";
    public static final String ATTACHMENT="attachment";
    public List getEntries(Folder folder, Date start, Date until);
	public List buildDigestDistributionList(Folder folder, Collection entries);
	public List buildMessageDistributionList(Folder folder, Collection entries);
	public Map buildNotificationMessage(Folder folder, Collection entries, Notify notify);
	public String getSubject(Folder folder, Notify notify);
	public String getFrom(Folder folder, Notify notify);
	public void postMessages(Folder folder, PostingDef pDef, Message[] msgs, Session session);

}
