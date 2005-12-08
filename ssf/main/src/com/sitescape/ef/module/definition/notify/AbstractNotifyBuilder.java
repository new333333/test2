package com.sitescape.ef.module.definition.notify;
import java.util.Map;
import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.util.InvokeUtil;
import com.sitescape.ef.util.ObjectPropertyNotFoundException;

/**
 *
 * @author Jong Kim
 */
public abstract class AbstractNotifyBuilder implements NotifyBuilder {
    
    public boolean buildElement(Element element, Entry entry, Notify notifyDef, String dataElemName, Map args) {
    	element.addAttribute("name", dataElemName);
    	element.addAttribute("caption", (String)args.get("_caption"));
        element.addAttribute("type", (String)args.get("_itemName"));
        CustomAttribute attribute = entry.getCustomAttribute(dataElemName);
    	if (attribute != null) return build(element, notifyDef, attribute, args);
    	else return build(element, notifyDef, entry, dataElemName, args);
    }
	protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	   	Object obj = attribute.getValue();
	   	if (obj != null) {
	   		element.setText(obj.toString());
	   	}
	   	return true;
	}   
    protected boolean build(Element element, Notify notifyDef, Entry entry, String dataElemName, Map args) {
	   	try {
	   		Object obj = InvokeUtil.invokeGetter(entry, dataElemName);
		   	if (obj != null) {
		   		element.setText(obj.toString());
		   	}
		} catch (com.sitescape.ef.util.ObjectPropertyNotFoundException ex) {
	   		return false;
	   	}
	   	return true;
    }
}
