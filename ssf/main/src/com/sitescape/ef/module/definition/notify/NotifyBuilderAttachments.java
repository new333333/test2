package com.sitescape.ef.module.definition.notify;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderAttachments extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, Entry entry, String dataElemName, Map args) {
	    	List atts = entry.getFileAttachments();
    		for (int i=0; i<atts.size(); ++i) {
		    	Element value = element.addElement("file");		    		
		    	FileAttachment att = (FileAttachment)atts.get(i);
		    	value.setText(att.toString());
	    	}
	    	return true;
	   }
}
