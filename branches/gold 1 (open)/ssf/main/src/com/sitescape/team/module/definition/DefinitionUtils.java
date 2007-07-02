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
/**
 * Utility routines to deal with definitions.
 */
package com.sitescape.team.module.definition;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;


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
		return DefinitionUtils.getPropertyValue(viewItem, "type");
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

   public static String getViewURL(FolderEntry fEntry, FileAttachment att)
   {
		return WebUrlUtil.getServletRootURL() + WebKeys.SERVLET_VIEW_FILE + "?" +
		WebKeys.URL_BINDER_ID + "=" + fEntry.getParentFolder().getId().toString() +
		"&entityType=folderEntry" +
		"&" + WebKeys.URL_ENTRY_ID + "=" + fEntry.getId().toString() +
		"&" + WebKeys.URL_FILE_ID + "=" + att.getId(); 
   }
}
