package com.sitescape.ef.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Event;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderEvent extends AbstractNotifyBuilder {
	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	Object obj = attribute.getValue();
	    	if (obj == null) return true;
	    	if (obj instanceof Event) {
	    		Event event = (Event)obj;
	    		Element value = element.addElement("startDate");
	    		value.setText(notifyDef.getDateFormat().format(event.getDtStart().getTime()));
	    		value = element.addElement("endDate");
	    		value.setText(notifyDef.getDateFormat().format(event.getDtEnd().getTime()));
	    		
	    	} else { 
	    		element.setText(obj.toString());
	    	}
	    	return true;
	    }
}
