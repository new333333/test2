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
/**
 * Utility routines to deal with definitions.
 */
package org.kablink.teaming.module.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.GetterUtil;
import org.kablink.util.Validator;



public class DefinitionUtils {
   public static String getPropertyValue(Element element, String name) {
		Element variableEle = (Element)element.selectSingleNode("./properties/property[@name='" + name + "']");
		if (variableEle == null) return null;
		return variableEle.attributeValue("value");   	
    }
   public static String getPropertyValue(Element element, String name, String attribute) {
		Element variableEle = (Element)element.selectSingleNode("./properties/property[@name='" + name + "']");
		if (variableEle == null) return null;
		return variableEle.attributeValue(attribute);   	
   }
    public static List getPropertyValueList(Element element, String name) {
		List resultElements = element.selectNodes("./properties/property[@name='" + name + "']");
    	List results = new ArrayList();
    	for (int i=0; i<resultElements.size(); ++i) {
    		Element variableEle = (Element)resultElements.get(i);
    		results.add(variableEle.attributeValue("value",  ""));
    	}
		return results;   	
    }
    public static boolean isSourceItem(Document definitionTree, String itemSource, String itemTarget) {
    	if (definitionTree == null) return false;
		Element root = definitionTree.getRootElement();
		
		//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
		Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
		if (entryFormItem == null) return false;
		//see if item is generated and save source
		Element itemEle = (Element)entryFormItem.selectSingleNode(".//item[@name='" + itemTarget + "']");
		if (itemEle == null) return false;
		boolean generated = GetterUtil.get(DefinitionUtils.getPropertyValue(itemEle, "generated"), false);
		if (generated) {
			String source = getPropertyValue(itemEle, "itemSource");
			if (!Validator.isNull(source) && source.equals(itemSource)) return true;
		}
		return false;
    }
   public static String getViewType(Document definitionTree) {
	   	if (definitionTree == null) return null;
		Element root = definitionTree.getRootElement();
		if (root == null) return null;
		Element viewItem = (Element)root.selectSingleNode("//item[@name='forumView' or @name='profileView' or @name='workspaceView' or @name='userWorkspaceView']");
		if (viewItem == null) return null;
		
		boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
		String viewType = DefinitionUtils.getPropertyValue(viewItem, "type");
		
		//Check if accessible mode
        User user = RequestContextHolder.getRequestContext().getUser();
		String strUserDisplayStyle = user.getDisplayStyle();
		if (strUserDisplayStyle == null) { strUserDisplayStyle = ""; }
		
		if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle) && accessible_simple_ui) {
			//In accessible mode, force the view type to accessible mode (but only for folders)
			if (root.attributeValue("type").equals(String.valueOf(Definition.FOLDER_VIEW)))
				viewType = Definition.VIEW_STYLE_ACCESSIBLE;
		}
		return viewType;
   }
   public static Element getItemByPropertyName(Element item, String itemType, String nameValue) {
		//Find the item in the definition
		Element propertyEle = (Element) item.selectSingleNode(
				"//item[@name='" + itemType + "']/properties/property[@name='name' and @value='"+nameValue+"']");
		if (propertyEle != null) {
			return propertyEle.getParent().getParent();
		}
		return null;

   }
   public static String getFamily(Document document) {
	   return getPropertyValue(document.getRootElement(), "family"); 
   }

   public static Map getOptionalArgs(Element indexingElem) {
       Map map = new HashMap();
       for (Iterator it = indexingElem.selectNodes("./args/arg")
               .listIterator(); it.hasNext();) {
           Element argElem = (Element) it.next();
           String key = argElem.attributeValue("name");
           String type = argElem.attributeValue("type");
           String valueStr = argElem.attributeValue("value");
           Object value = null;

           if (type.equals("boolean")) {
               if (valueStr.equals("true"))
                   value = Boolean.TRUE;
               else if (valueStr.equals("false"))
                   value = Boolean.FALSE;
               else
                   throw new ConfigurationException("Invalid value '"
                           + valueStr + "' for boolean type: ["
                           + indexingElem.toString() + "]");
           } else if (type.equals("text") || type.equals("string")) {
               value = valueStr;
           } else {
               throw new ConfigurationException("Illegal type '" + type
                       + "': [" + indexingElem.toString() + "]");
           }

           map.put(key, value);
       }
       return map;
   }



}
