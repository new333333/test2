package com.sitescape.ef.module.mail;
import java.util.Collection;
import java.util.Map;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.dao.util.OrderBy;
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
	public OrderBy getLookupOrder(Folder folder);
	public Map buildNotificationMessage(Folder folder, Collection entries, Notify notify);
	public String getSubject(Folder folder, Notify notify);
	public String getFrom(Folder folder, Notify notify);
	public Object[] validateIdList(Collection entries, Collection userIds);
}
