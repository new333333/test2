package com.sitescape.ef.module.mail;
import java.util.Collection;
import java.util.Map;
import java.util.Date;
import java.util.Locale;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.dao.util.FilterControls;
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
	public Map buildNotificationMessage(Folder folder, Collection entries, Locale locale);
	public String getSubject(Folder folder, Locale locale);
	public String getFrom(Folder folder);
	public Object[] validateIdList(Collection entries, Collection userIds);
}
