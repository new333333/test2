package com.sitescape.ef.module.definition.notify;
import java.util.Map;
import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.util.InvokeUtil;
import com.sitescape.ef.util.ObjectPropertyNotFoundException;
import com.sitescape.ef.util.NLT;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractNotifyBuilder implements NotifyBuilder {
    
    public boolean buildElement(Element element, DefinableEntity entity, Notify notifyDef, String dataElemName, Map args) {
    	element.addAttribute("name", dataElemName);
    	element.addAttribute("caption", (String)args.get("_caption"));
        element.addAttribute("type", (String)args.get("_itemName"));
        CustomAttribute attribute = entity.getCustomAttribute(dataElemName);
		try {
			if (attribute != null) 
    			return build(element, notifyDef, attribute, args);
			else 
    			return build(element, notifyDef, entity, dataElemName, args);
		} catch (Exception e) {
			element.setText(NLT.get("notify.error.attribute", notifyDef.getLocale()));
			return true;
    	}
    }
	protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	   	Object obj = attribute.getValue();
	   	if (obj != null) {
	   		element.setText(obj.toString());
	   	}
	   	return true;
	}   
    protected boolean build(Element element, Notify notifyDef, DefinableEntity entity, String dataElemName, Map args) {
	   	try {
	   		Object obj = InvokeUtil.invokeGetter(entity, dataElemName);
		   	if (obj != null) {
		   		element.setText(obj.toString());
		   	}
		} catch (ObjectPropertyNotFoundException ex) {
	   		return false;
	   	}
	   	return true;
    }
}
