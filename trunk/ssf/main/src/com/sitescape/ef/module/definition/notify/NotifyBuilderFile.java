package com.sitescape.ef.module.definition.notify;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebUrlUtil;
import com.sitescape.ef.domain.DefinableEntity;
/**
* Handle file field in mail notification.  This implememtation will
* send the file name only in a notification 
* See <code>NotifyBuilderFileSend</code>to send the actual file.
* @author Janet McCann
*/
public class NotifyBuilderFile extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
		   DefinableEntity entry = attribute.getOwner().getEntity();
		   Set files = attribute.getValueSet();
		   for (Iterator iter=files.iterator(); iter.hasNext();) {
		    	Element value = element.addElement("file");		    		
		    	FileAttachment att = (FileAttachment)iter.next();
		    	if (att != null && att.getFileItem() != null) {
		    		value.setText(att.getFileItem().getName());
		    		if (entry instanceof FolderEntry) {
		    			FolderEntry fEntry = (FolderEntry)entry;
		    		
		    			String webUrl = WebUrlUtil.getServletRootURL() + WebKeys.SERVLET_VIEW_FILE + "?" +
		    			WebKeys.URL_BINDER_ID + "=" + fEntry.getParentFolder().getId().toString() +
		    			"&" + WebKeys.URL_ENTRY_ID + "=" + fEntry.getId().toString() +
		    			"&" + WebKeys.URL_FILE_ID + "=" + att.getId(); 
		    			value.addAttribute("href", webUrl);
		    		}
		    	}
	    	}
	    	return true;
	    }
}
