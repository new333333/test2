/**
 * Utility routines to deal with definitions.
 */
package com.sitescape.ef.module.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.repository.RepositoryUtil;

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
		Element root = definitionTree.getRootElement();
		
		//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
		Element entryFormItem = (Element)root.selectSingleNode("item[@type='form' or @name='entryForm' or @name='profileEntryForm']");
		if (entryFormItem == null) return false;
		//see if item is generated and save source
		Element itemEle = (Element)entryFormItem.selectSingleNode(".//item[@name='" + itemTarget + "']");
		boolean generated = GetterUtil.get(DefinitionUtils.getPropertyValue(itemEle, "generated"), false);
		if (generated) {
			String source = getPropertyValue(itemEle, "itemSource");
			if (!Validator.isNull(source) && source.equals(itemSource)) return true;
		}
		return false;

    }
    
    public static String getWebDAVURL(Folder folder, FolderEntry entry) {
    	String strEntryURL = "";
		Definition entryDef = entry.getEntryDef();
		Document entryDefDocTree = entryDef.getDefinition();
		String strRepositoryName = "";
		if (entryDefDocTree != null) {
			//root is the root of the entry's definition
			Element root = entryDefDocTree.getRootElement();
			Element entryFormItem = (Element)root.selectSingleNode("item[@name='entryForm']");			
			
			//if root is not null
			if (entryFormItem != null) {
				//get the attachFiles
				Element entryAttachFileItem = (Element)entryFormItem.selectSingleNode(".//item[@name='attachFiles']");
				//if there is a attachFiles item entry
				if (entryAttachFileItem != null) {
					strRepositoryName = DefinitionUtils.getPropertyValue(entryAttachFileItem, "storage");
					if (Validator.isNull(strRepositoryName)) strRepositoryName = RepositoryUtil.getDefaultRepositoryName();
				}
				
			}
		}
		strEntryURL = SsfsUtil.getEntryUrl(folder, entry, strRepositoryName);
		
		return strEntryURL;
    }
    
    
}
