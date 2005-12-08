package com.sitescape.ef.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.util.WebUrlUtil;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.domain.Entry;
/**
* Handle file field in mail notification.  This implememtation will
* send the file name only in a notification 
* See <code>NotifyBuilderFileSend</code>to send the actual file.
* @author Janet McCann
*/
public class NotifyBuilderFile extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	FileAttachment att = (FileAttachment)attribute.getValue();
	    	if (att != null && att.getFileItem() != null) {
	    		element.setText(att.getFileItem().getName());
    			Entry owner = att.getOwner().getEntry();
    			if (owner instanceof FolderEntry) {
    				FolderEntry fEntry = (FolderEntry)owner;
	    		
    				String webUrl = WebUrlUtil.getServletRootURL() + WebKeys.SERVLET_VIEW_FILE + "?" +  
    				WebKeys.FORUM_URL_FORUM_ID + "+" + fEntry.getParentFolder().getId().toString() +
    				"&" + WebKeys.FORUM_URL_ENTRY_ID + "+" + fEntry.getId().toString() +
    				"&" + WebKeys.FORUM_URL_FILE_ID + "+" + att.getId(); 
    				element.addAttribute("href", webUrl);
	    		}
	    	}
	    	return true;
	    }
}
