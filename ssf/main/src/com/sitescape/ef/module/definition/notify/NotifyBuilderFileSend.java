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
* Handle file type fields in mail notification.  This implememtation will
* send the file name on a summary type notification and will send the actual
* file on a full notification.
* @author Janet McCann
*/
public class NotifyBuilderFileSend extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	FileAttachment att = (FileAttachment)attribute.getValue();
	    	if (att != null && att.getFileItem() != null) {
	    		element.setText(att.getFileItem().getName());
	    		if (notifyDef.isFull())	
	    			notifyDef.addAttachment(att);
	    		else {
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
	    	}
	    	return true;
	    }
}
