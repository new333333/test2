package com.sitescape.team.mail;
import java.util.Collection;
import java.util.Map;
import java.util.Date;
import java.util.List;
import javax.mail.Message;
import javax.mail.Session;

import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.module.definition.notify.Notify;
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
    public List buildDistributionList(Folder folder, Collection entries, Collection subscriptions);
    public List buildDistributionList(Folder folder, Collection entries, Collection subscriptions, int style);
	public Map buildDistributionList(FolderEntry entry, Collection subscriptions, int style);
	public Map buildNotificationMessage(Folder folder, Collection entries, Notify notify);
	public Map buildNotificationMessage(Folder folder, FolderEntry entry, Notify notify);
	public String getSubject(Folder folder, Notify notify);
	public String getFrom(Folder folder, Notify notify);
	public List postMessages(Folder folder, PostingDef pDef, Message[] msgs, Session session);

}
