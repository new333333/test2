package com.sitescape.team.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderText extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	Object obj = attribute.getValue();
	    	if (obj != null) {
	    		element.setText(obj.toString());
	    	}
	    	return true;
	    }
}