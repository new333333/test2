
package com.sitescape.ef.module.mail.impl;

import java.util.Collection;
import java.util.Iterator;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.module.mail.FolderEmailFormatter;
/**
 * @author Janet McCann
 *
 */
public class DefaultFolderEmailFormatter implements FolderEmailFormatter {

	/* (non-Javadoc)
	 * @see com.sitescape.ef.apps.mail.MailHelper#getLookupOrder()
	 */
	public OrderBy getLookupOrder() {
		return new OrderBy("HKey.sortKey");
	}

	/* (non-Javadoc)
	 * @see com.sitescape.ef.apps.mail.MailHelper#buildMailMessage(com.sitescape.ef.domain.DataForum, java.util.Collection, java.util.Collection)
	 */
	public String buildNotificationMessage(Folder forum, Collection entries) {
	    StringBuffer buf = new StringBuffer();
		for (Iterator i=entries.iterator();i.hasNext();) {
			FolderEntry entry = (FolderEntry)i.next();	
	    	buf.append("Entry:  " + entry.getDocNumber() + " " + entry.getTitle() + "\n");
		}
		return buf.toString();
	}
	public Object[] validateIdList(Collection entries, Collection userIds) {
	   	Object[] result = new Object[1];
    	Object[] row = new Object[2];
    	result[0] = row;
    	row[0] = entries;
    	row[1] = userIds;
    	return result;
	}
	public String getNotificationSubject(Folder forum) {
		return "Schedule mail notification for forum " + forum.getName();
	}
}
