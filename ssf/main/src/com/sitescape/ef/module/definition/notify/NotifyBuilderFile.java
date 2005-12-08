package com.sitescape.ef.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
/**
*
* @author Janet McCann
*/
public class NotifyBuilderFile extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	FileAttachment att = (FileAttachment)attribute.getValue();
	    	if (att != null) 
	    		element.setText(att.toString());
	    	return true;
	    }
}
