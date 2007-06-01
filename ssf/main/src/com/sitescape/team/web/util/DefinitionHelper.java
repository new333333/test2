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
package com.sitescape.team.web.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Validator;

public class DefinitionHelper {
	private static DefinitionHelper instance; // A singleton instance
	private DefinitionModule definitionModule;
	private DefinitionConfigurationBuilder definitionBuilderConfig;
	public DefinitionHelper() {
		if(instance != null)
			throw new SingletonViolationException(DefinitionHelper.class);
		
		instance = this;
	}
    protected static DefinitionHelper getInstance() {
    	return instance;
    }
    public void setDefinitionModule(DefinitionModule definitionModule) {
    	this.definitionModule = definitionModule;
    }
    public DefinitionModule getDefinitionModule() {
    	return definitionModule;
    }
	public static DefinitionConfigurationBuilder getDefinitionBuilderConfig() {
        return getInstance().definitionBuilderConfig;
    }
    public void setDefinitionBuilderConfig(DefinitionConfigurationBuilder definitionBuilderConfig) {
        this.definitionBuilderConfig = definitionBuilderConfig;
    }
	
	public static void getDefinitions(int defType, String key, Map model) {
		List defs = getInstance().getDefinitionModule().getDefinitions(defType);
		Iterator itDefinitions = defs.listIterator();
		
		//if already setup, add to it
		Map definitions = (Map)model.get(key);
		if (definitions == null) definitions = new HashMap();
		while (itDefinitions.hasNext()) {
			Definition def = (Definition) itDefinitions.next();
			definitions.put(def.getId(), def);
		}
		model.put(key, definitions);
	}
	/**
	 * Helper to get definition for other helpers
	 * @param id
	 */
	public static Definition getDefinition(String id) {
		try {
			return getInstance().getDefinitionModule().getDefinition(id);
		} catch (NoDefinitionByTheIdException nd) {
			return null;
		}
		
	}
	/**
	 * Helper to get definitions for other helpers
	 * @param defType
	 */	
	public static List getDefinitions(int defType) {
		return  getInstance().getDefinitionModule().getDefinitions(defType);
	}
	
	/**
	 * Fill in the model values for a definition.  Return false if definition isn't
	 * complete, true otherwise
	 * @param currentDef
	 * @param model
	 * @param node
	 * @return
	 */
	public static boolean getDefinition(Definition currentDef, Map model, String node) {
		if (currentDef == null) {
			model.put(WebKeys.CONFIG_ELEMENT, null);
			model.put(WebKeys.CONFIG_DEFINITION, getInstance().getDefinitionModule().getDefinitionConfig());
			return false;
		}
		Document configDoc = currentDef.getDefinition();
		if (configDoc == null) { 
			model.put(WebKeys.CONFIG_ELEMENT, null);
			model.put(WebKeys.CONFIG_DEFINITION, getInstance().getDefinitionModule().getDefinitionConfig());
			return false;
		} else {
			model.put(WebKeys.CONFIG_DEFINITION, configDoc);
			Element configRoot = configDoc.getRootElement();
			if (configRoot == null) {
				model.put(WebKeys.CONFIG_ELEMENT, null);
				return false;
			} else {
				Element configEle = (Element) configRoot.selectSingleNode(node);
				model.put(WebKeys.CONFIG_ELEMENT, configEle);
				if (configEle == null) return false;
			}
		}
		return true;
	}

	public static boolean getDefinitionElement(Definition currentDef, Map model, String elementName) {
		if (currentDef == null) {
			model.put(WebKeys.CONFIG_ELEMENT, null);
			return false;
		}
		Document configDoc = currentDef.getDefinition();
		if (configDoc == null) { 
			model.put(WebKeys.CONFIG_ELEMENT, null);
			return false;
		} else {
			Element configRoot = configDoc.getRootElement();
			if (configRoot == null) {
				model.put(WebKeys.CONFIG_ELEMENT, null);
				return false;
			} else {
				//Get the form in which this element lives
				Element formEle = (Element) configRoot.selectSingleNode("//item[@type='form']");
				//Find the item that has the desired name
				Element configEle = (Element) formEle.selectSingleNode(
						"//item/properties/property[@name='name' and @value='"+elementName+"']");
				model.put(WebKeys.CONFIG_ELEMENT, configEle.getParent().getParent());
				if (configEle == null) return false;
			}
		}
		return true;
	}

	public static void getDefinitions(Binder binder, Map model) {
		getDefinitions(binder, model, null);
	}
	public static void getDefinitions(Binder binder, Map model, String userSelectedDefinition) {
		List folderViewDefs = binder.getViewDefinitions();
		if (!folderViewDefs.isEmpty()) {
			//Get the default definition for this binder
			Definition defaultForumDefinition = (Definition)folderViewDefs.get(0);
			if (!Validator.isNull(userSelectedDefinition)) {
				//The user has selected a default definition for this binder; check it out.
				for (int i = 0; i < folderViewDefs.size(); i++) {
					Definition def = (Definition)folderViewDefs.get(i);
					//Is this an allowed definition?
					if (userSelectedDefinition.equals(def.getId())) {
						//Ok, this definition is allowed
						defaultForumDefinition = def;
						break;
					}
				}
			}

			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, defaultForumDefinition);
			Document forumViewDoc = defaultForumDefinition.getDefinition();
			if (forumViewDoc != null) {
				Element forumViewElement ;
				forumViewElement = forumViewDoc.getRootElement();
				forumViewElement = (Element) forumViewElement.selectSingleNode("//item[@name='forumView' or @name='profileView' or @name='workspaceView' or @name='userWorkspaceView']");
				model.put(WebKeys.CONFIG_ELEMENT, forumViewElement);
				model.put(WebKeys.CONFIG_DEFINITION, forumViewDoc);
			} else {
				model.put(WebKeys.CONFIG_ELEMENT, null);
				model.put(WebKeys.CONFIG_DEFINITION, getInstance().getDefinitionModule().getDefinitionConfig());
			}
			
		} else {
			//model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, null);
			//model.put(WebKeys.DEFAULT_FOLDER_DEFINITION_ID, "");
			//model.put(WebKeys.CONFIG_ELEMENT, null);
			getDefaultBinderDefinition(binder, model, "//item[@name='forumView' or @name='profileView' or @name='workspaceView' or @name='userWorkspaceView']");
		
		}
		Map defaultFolderDefinitions = getBinderDefsAsMap(binder);
		model.put(WebKeys.FOLDER_DEFINITION_MAP, defaultFolderDefinitions);
		Map defaultEntryDefinitions = getEntryDefsAsMap(binder);
		model.put(WebKeys.ENTRY_DEFINITION_MAP, defaultEntryDefinitions);
		model.put(WebKeys.WORKFLOW_DEFINITION_MAP, getWorkflowDefsAsMap(binder)); 
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
	}
	public static Map getBinderDefsAsMap(Binder binder) {
		Map defaultFolderDefinitions = new HashMap();
		Iterator itDefaultFolderDefinitions = binder.getViewDefinitions().listIterator();
		while (itDefaultFolderDefinitions.hasNext()) {
			Definition entryDef = (Definition) itDefaultFolderDefinitions.next();
			defaultFolderDefinitions.put(entryDef.getId(), entryDef);
		}
		return defaultFolderDefinitions;
	}

	public static Map getEntryDefsAsMap(Binder binder) {
		Map defaultEntryDefinitions = new HashMap();
		Iterator itDefaultEntryDefinitions = binder.getEntryDefinitions().listIterator();
		while (itDefaultEntryDefinitions.hasNext()) {
			Definition entryDef = (Definition) itDefaultEntryDefinitions.next();
			defaultEntryDefinitions.put(entryDef.getId(), entryDef);
		}
		return defaultEntryDefinitions;
	}
	public static Map getWorkflowDefsAsMap(Binder binder) {
		Map defaultEntryDefinitions = new HashMap();
		Iterator itDefaultEntryDefinitions = binder.getWorkflowDefinitions().listIterator();
		while (itDefaultEntryDefinitions.hasNext()) {
			Definition entryDef = (Definition) itDefaultEntryDefinitions.next();
			defaultEntryDefinitions.put(entryDef.getId(), entryDef);
		}
		return defaultEntryDefinitions;
	}
	//Routine to build a definition file on the fly for viewing entries with no definition
	public static void getDefaultEntryView(Entry entry, Map model) {
		String path = "//item[@name='entryView' or @name='profileEntryView']";
		getDefaultEntryView(entry, model, path);
	}
	public static void getDefaultEntryView(Entry entry, Map model, String path) {
		//Create an empty entry definition
		Definition def = getInstance().getDefinitionModule().setDefaultEntryDefinition(entry);
		Document defDoc = def.getDefinition();
		//Add the "default viewer" item
		Element entryView = (Element) defDoc.getRootElement().selectSingleNode(path);
		model.put(WebKeys.CONFIG_ELEMENT, entryView);
		model.put(WebKeys.CONFIG_DEFINITION, defDoc);
	}
		
	//Routine to build a definition file on the fly for viewing binders with no definition
	public static Document getDefaultBinderDefinition(Binder binder, Map model, String viewPath) {
		Definition def = getInstance().getDefinitionModule().setDefaultBinderDefinition(binder);
		Document defDoc = def.getDefinition();
		
		//Add the "default viewer" item
		Element entryView = (Element) defDoc.getRootElement().selectSingleNode(viewPath);
		model.put(WebKeys.CONFIG_ELEMENT, entryView);
		model.put(WebKeys.CONFIG_DEFINITION, defDoc);
		//defDoc.asXML();
		
		return defDoc;
	}

	public static boolean checkIfBinderShowingDashboard(Binder binder) {
		Definition def = binder.getEntryDef();
		if (def == null) getInstance().getDefinitionModule().setDefaultBinderDefinition(binder);
		Document defDoc = null;
		if (def != null) defDoc = def.getDefinition();
		if (defDoc != null) {
			if (defDoc.getRootElement().selectSingleNode("//item[@name='dashboardCanvas']") != null) {
				return true;
			}
		}
		return false;
	}
    public static String getWebDAVURL(Folder folder, FolderEntry entry) {
    	String strEntryURL = "";
		Definition entryDef = entry.getEntryDef();
		if (entryDef == null) {
			getInstance().getDefinitionModule().setDefaultEntryDefinition(entry);
			entryDef = entry.getEntryDef();
		}
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
	
    public static String findCaptionForValue(Document definitionConfig, Element configElement, String value)
    {
    	String dataType = configElement.attributeValue("formItem");
    	String fieldName = configElement.selectSingleNode("properties/property[@name='name']/@value").getStringValue();
    	Node captionNode = definitionConfig.selectSingleNode("//item[@type='form']//item[@type='data' and @name='" + 
    														dataType + "' and properties/property[@name='name' and @value='" +
    														fieldName + "']]//item/properties[property[@name='name' and @value='" +
    														value + "']]/property[@name='caption']/@value");
    	String result = value;
    	if(captionNode != null) { result = captionNode.getStringValue();}
    	return result;
    }
}
