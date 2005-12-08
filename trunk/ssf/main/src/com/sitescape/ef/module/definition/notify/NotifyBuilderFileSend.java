package com.sitescape.ef.module.definition.notify;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebUrlUtil;
import com.sitescape.ef.domain.Entry;
/**
* Handle file type fields in mail notification.  This implememtation will
* send the file name on a summary type notification and will send the actual
* file on a full notification.
* @author Janet McCann
*/
public class NotifyBuilderFileSend extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
		   Entry entry = attribute.getOwner().getEntry();
		   Set files = attribute.getValueSet();
		   for (Iterator iter=files.iterator(); iter.hasNext();) {
		    	Element value = element.addElement("file");		    		
		    	FileAttachment att = (FileAttachment)iter.next();
		    	if (att != null && att.getFileItem() != null) {
		    		value.setText(att.getFileItem().getName());
		    		if (notifyDef.isFull())	
		    			notifyDef.addAttachment(att);
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
