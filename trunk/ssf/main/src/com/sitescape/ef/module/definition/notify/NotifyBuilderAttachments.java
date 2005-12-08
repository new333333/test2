package com.sitescape.ef.module.definition.notify;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebUrlUtil;

/**
* Handle attachments in mail notification.  This implememtation will
* send the file name only a notification 
* See <code>NotifyBuilderAttachmentsSend</code>to send the actual file.
* @author Janet McCann
*/
public class NotifyBuilderAttachments extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, Entry entry, String dataElemName, Map args) {
	    	List atts = entry.getFileAttachments();
    		for (int i=0; i<atts.size(); ++i) {
		    	Element value = element.addElement("file");		    		
		    	FileAttachment att = (FileAttachment)atts.get(i);
		    	if (att != null && att.getFileItem() != null) {
		    		value.setText(att.getFileItem().getName());
		    		if (entry instanceof FolderEntry) {
		    			FolderEntry fEntry = (FolderEntry)entry;
		    		
		    			String webUrl = WebUrlUtil.getServletRootURL() + WebKeys.SERVLET_VIEW_FILE + "?" +
		    			WebKeys.FORUM_URL_FORUM_ID + "=" + fEntry.getParentFolder().getId().toString() +
		    			"&" + WebKeys.FORUM_URL_ENTRY_ID + "=" + fEntry.getId().toString() +
		    			"&" + WebKeys.FORUM_URL_FILE_ID + "=" + att.getId(); 
		    			value.addAttribute("href", webUrl);
		    		}
		    	}
    		}
	    	return true;
	   }
}
