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
package com.sitescape.team.module.definition.ws;
import java.util.Map;
import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ObjectPropertyNotFoundException;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractElementBuilder implements ElementBuilder {
    
    public boolean buildElement(Element element, DefinableEntity entity, String dataElemName) {
    	element.addAttribute("name", dataElemName);
    	element.addAttribute("type", element.attributeValue("name"));
        CustomAttribute attribute = entity.getCustomAttribute(dataElemName);
		try {
			if (attribute != null) 
    			return build(element, attribute);
			else 
    			return build(element, entity, dataElemName);
		} catch (Exception e) {
			element.setText(NLT.get("ws.error.attribute"));
			return true;
    	}
    }
	protected boolean build(Element element, CustomAttribute attribute) {
	   	return build(element, attribute.getValue());
	}   
    protected boolean build(Element element, DefinableEntity entity, String dataElemName) {
	   	try {
	   		return build(element, InvokeUtil.invokeGetter(entity, dataElemName));
		} catch (ObjectPropertyNotFoundException ex) {
	   		return false;
	   	}
    }
    
    protected boolean build(Element element, Object obj) {
	   	if (obj != null) {
	   		element.setText(obj.toString());
	   	}
	   	return true;
    }
}
