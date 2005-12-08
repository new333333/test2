package com.sitescape.ef.module.definition.notify;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderSelect extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	Object obj = attribute.getValue();
	    	if (obj instanceof Set) {
	    		Set set = (Set)obj;
	    		for (Iterator iter=set.iterator();iter.hasNext();) {
		    		Element value = element.addElement("value");		    		
		    		obj = iter.next();
		    		value.setText(obj.toString());
	    		}
	    	} else if (obj != null) {
		    	Element value = element.addElement("value");
	    		value.setText(obj.toString());
	    	} else {
	    		element.addElement("value");
	    	}
	    	return true;
	    }
}

