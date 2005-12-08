package com.sitescape.ef.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;

/**
 *
 * @author Janet McCann
 */
public class NotifyBuilderCheck extends AbstractNotifyBuilder {

    
    protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
     	Object obj = attribute.getValue();
    	if (obj != null) {
    		element.setText(obj.toString());
    	} else {
    		element.setText(obj.toString());
    	}
    	return true;
   }

}
