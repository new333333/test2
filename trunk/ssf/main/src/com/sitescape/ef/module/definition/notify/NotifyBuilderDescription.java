package com.sitescape.ef.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Description;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.util.InvokeUtil;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderDescription extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	Object obj = attribute.getValue();
	    	doElement(element, obj);
	    	return true;
	    }
	    protected boolean build(Element element, Notify notifyDef, DefinableEntity entity, String dataElemName, Map args) {
		   	try {
		   		Object obj = InvokeUtil.invokeGetter(entity, dataElemName);
		    	doElement(element, obj);
			} catch (com.sitescape.ef.util.ObjectPropertyNotFoundException ex) {
		   		return false;
		   	}
			return true;
	    }
	    private void doElement(Element parent, Object obj) {
	    	if (obj instanceof Description) {
	    		Description desc = (Description)obj;
	    		parent.setText(desc.getText());
	    		parent.addAttribute("format",String.valueOf(desc.getFormat()));
	    	} else if (obj != null) {
	    		parent.setText(obj.toString());
	    		parent.addAttribute("format", String.valueOf(Description.FORMAT_NONE));
	    	}
	    	
	    }
	   
}