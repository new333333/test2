/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.GetterUtil;
import org.kablink.util.Validator;



public class DefinitionUtils {
	
	private static Log logger = LogFactory.getLog(DefinitionUtils.class);
	
   public static String getPropertyValue(Element element, String name) {
		long startTime = System.nanoTime();
		Element variableEle = (Element)element.selectSingleNode("./properties/property[@name='" + name + "']");
		String value = null;
		if (variableEle != null) 
			value = variableEle.attributeValue("value");
		Utils.end(logger, startTime, "getPropertyValue(Element,String)", name);
		return value;
    }
   public static String getPropertyValue(Element element, String name, String attribute) {
		long startTime = System.nanoTime();
		Element variableEle = (Element)element.selectSingleNode("./properties/property[@name='" + name + "']");
		String value = null;
		if (variableEle != null) 
			value = variableEle.attributeValue(attribute);
		Utils.end(logger, startTime, "getPropertyValue(Element,String,String)", name, attribute);
		return value;
   }
   public static boolean getPropertyBooleanValue(Element element, String name) {
		String value = getPropertyValue(element, name);
		if (value != null) {
			if (value.equals("true")) return true;
		}
		return false;
   }
    public static List getPropertyValueList(Element element, String name) {
		long startTime = System.nanoTime();
		List resultElements = element.selectNodes("./properties/property[@name='" + name + "']");
    	List results = new ArrayList();
    	for (int i=0; i<resultElements.size(); ++i) {
    		Element variableEle = (Element)resultElements.get(i);
    		results.add(variableEle.attributeValue("value",  ""));
    	}
		Utils.end(logger, startTime, "getPropertyValueList", name);
		return results;   	
    }
    public static boolean isSourceItem(Document definitionTree, String itemSource, String itemTarget) {
		long startTime = System.nanoTime();
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
    public static String getViewType(Binder binder) {
    	Map model = new HashMap();
        User user = RequestContextHolder.getRequestContext().getUser();
    	final ProfileModule profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
		UserProperties userFolderProperties = profileModule.getUserProperties(user.getId(), binder.getId());
    	DefinitionHelper.getDefinitions(binder, model, 
				(String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));
    	return getViewType((Document)model.get(WebKeys.CONFIG_DEFINITION));
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
   public static String getEntryBlogViewType(Document definitionTree) {
	   	if (definitionTree == null) return null;
		Element root = definitionTree.getRootElement();
		if (root == null) return null;
		Element viewItem = (Element)root.selectSingleNode("//item[@name='entryBlogView']");
		if (viewItem == null) return null;
		String viewType = DefinitionUtils.getPropertyValue(viewItem, "type");
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
