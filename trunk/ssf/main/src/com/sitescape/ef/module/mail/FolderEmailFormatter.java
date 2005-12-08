package com.sitescape.ef.module.mail;
import java.util.Collection;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.dao.util.OrderBy;
/**
 * Interface to define 
 * @author Janet McCann
 *
 */
public interface FolderEmailFormatter {
    public static final String PROCESSOR_KEY = "processorKey_folderEmailFormatter";
	public OrderBy getLookupOrder();
	public Object[] validateIdList(Collection entries, Collection userIds);
	public String buildNotificationMessage(Folder forum, Collection entries);
	public String getNotificationSubject(Folder forum);
}
