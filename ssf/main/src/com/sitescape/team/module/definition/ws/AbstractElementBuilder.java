/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.definition.ws;
import java.util.List;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.remoting.ws.model.Field;
import com.sitescape.team.remoting.ws.model.StringField;
import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ObjectPropertyNotFoundException;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractElementBuilder implements ElementBuilder {
    
	protected BuilderContext context = null;
    public boolean buildElement(Element element, com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity, String dataElemType, String dataElemName, BuilderContext context) {
    	this.context = context;
    	if(element != null) {
	    	element.addAttribute("name", dataElemName);
	    	element.addAttribute("type", dataElemType);
    	}
        CustomAttribute attribute = entity.getCustomAttribute(dataElemName);
		try {
			if (attribute != null) 
    			return build(element, entityModel, entity, dataElemType, dataElemName, attribute);
			else 
    			return build(element, entityModel, entity, dataElemType, dataElemName);
		} catch (Exception e) {
			element.setText(NLT.get("ws.error.attribute"));
			return true;
    	}
    }
	protected boolean build(Element element, com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity, String dataElemType, String dataElemName, CustomAttribute attribute) {
	   	return build(element, entityModel, attribute.getValue(), entity, dataElemType, dataElemName);
	}   
    protected boolean build(Element element, com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, DefinableEntity entity, String dataElemType, String dataElemName) {
	   	try {
	   		return build(element, entityModel, InvokeUtil.invokeGetter(entity, dataElemName), entity, dataElemType, dataElemName);
		} catch (ObjectPropertyNotFoundException ex) {
	   		return false;
	   	}
    }
    
    protected boolean build(Element element, com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, Object obj, DefinableEntity entity, String dataElemType, String dataElemName) {
    	return build(element, entityModel, obj, dataElemType, dataElemName);
    }
    
    protected boolean build(Element element, com.sitescape.team.remoting.ws.model.DefinableEntity entityModel, Object obj, String dataElemType, String dataElemName) {
	   	if (obj != null) {
	   		String value = obj.toString();
	   		if(element != null)
	   			element.setText(value);
	   		if(entityModel != null && !dataElemName.equals("title"))
	   			entityModel.addStringField(new StringField(dataElemName, dataElemType, value));
	   	}
	   	return true;
    }
}
