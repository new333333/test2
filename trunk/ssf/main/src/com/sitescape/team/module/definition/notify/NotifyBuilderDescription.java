/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.definition.notify;

import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderDescription extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	Object obj = attribute.getValue();
	    	doElement(element, obj, attribute.getOwner().getEntity());
	    	return true;
	    }
	    protected boolean build(Element element, Notify notifyDef, DefinableEntity entity, String dataElemName, Map args) {
		   	try {
		   		Object obj = InvokeUtil.invokeGetter(entity, dataElemName);
		    	doElement(element, obj, entity);
			} catch (com.sitescape.team.util.ObjectPropertyNotFoundException ex) {
		   		return false;
		   	}
			return true;
	    }
	    private void doElement(Element parent, Object obj, DefinableEntity entity) {
	    	String value = null;
	    	if (obj instanceof Description) {
	    		Description desc = (Description)obj;
	    		value = desc.getText();
	    		value = WebHelper.markupStringReplacement(null, null, null, null, entity, value, WebKeys.MARKUP_VIEW);
	    		parent.addAttribute("format",String.valueOf(desc.getFormat()));
	    	} else if (obj != null) {
	    		value = obj.toString();
	    		parent.addAttribute("format", String.valueOf(Description.FORMAT_NONE));
	    	}
    		if (Validator.isNull(value)) parent.setText("");
    		else parent.addCDATA(value);
	    	
	    }
	   
}