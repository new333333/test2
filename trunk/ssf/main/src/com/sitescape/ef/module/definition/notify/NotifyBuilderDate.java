package com.sitescape.ef.module.definition.notify;

import java.util.Date;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;

/**
 *
 * @author Janet McCann
 */
public class NotifyBuilderDate extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	Object obj = attribute.getValue();
	    	if (obj instanceof Date) {
	    		Date date = (Date)obj;
	    		element.setText(notifyDef.getDateFormat().format(date));
	    	} else if (obj != null) {
	    		element.setText(obj.toString());
	    	}
	    	return true;
	    }
}
