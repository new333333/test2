package com.sitescape.ef.web.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.SingletonViolationException;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinitionInvalidException;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.util.Validator;

public class DefinitionUtils {
	private static DefinitionUtils instance; // A singleton instance
	private DefinitionModule definitionModule;
	public DefinitionUtils() {
		if(instance != null)
			throw new SingletonViolationException(DefinitionUtils.class);
		
		instance = this;
	}
    public static DefinitionUtils getInstance() {
    	return instance;
    }
    public void setDefinitionModule(DefinitionModule definitionModule) {
    	this.definitionModule = definitionModule;
    }
    public DefinitionModule getDefinitionModule() {
    	return definitionModule;
    }
    public static void getDefinitions(Map model) {
		List defs = getInstance().getDefinitionModule().getDefinitions();
		model.put(WebKeys.PUBLIC_DEFINITIONS, defs);
		Iterator itPublicDefinitions = defs.listIterator();
		Map publicEntryDefinitions = new HashMap();
		Map publicForumDefinitions = new HashMap();
		Map publicWorkspaceDefinitions = new HashMap();
		Map publicProfileDefinitions = new HashMap();
		Map publicProfileEntryDefinitions = new HashMap();
		while (itPublicDefinitions.hasNext()) {
			Definition def = (Definition) itPublicDefinitions.next();
			if (def.getType() == Definition.COMMAND) {
				publicEntryDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.FOLDER_VIEW) {
				publicForumDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.PROFILE_VIEW) {
				publicProfileDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.PROFILE_ENTRY_VIEW) {
				publicProfileEntryDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.WORKSPACE_VIEW) {
				publicWorkspaceDefinitions.put(def.getId(), def);
			}
		}
		model.put(WebKeys.PUBLIC_ENTRY_DEFINITIONS, publicEntryDefinitions);
		model.put(WebKeys.PUBLIC_FOLDER_DEFINITIONS, publicForumDefinitions);
		model.put(WebKeys.PUBLIC_PROFILE_DEFINITIONS, publicProfileDefinitions);
		model.put(WebKeys.PUBLIC_PROFILE_ENTRY_DEFINITIONS, publicProfileEntryDefinitions);
		model.put(WebKeys.PUBLIC_WORKSPACE_DEFINITIONS, publicWorkspaceDefinitions);

	}
	
	public static void getDefinitions(int defType, String key, Map model) {
		List defs = getInstance().getDefinitionModule().getDefinitions();
		Iterator itDefinitions = defs.listIterator();
		Map definitions = new HashMap();
		while (itDefinitions.hasNext()) {
			Definition def = (Definition) itDefinitions.next();
			if (def.getType() == defType) {
				definitions.put(def.getId(), def);
			}
		}
		model.put(key, definitions);
	}
	/**
	 * Fill in the model values for a definition.  Return false if definition isn't
	 * complete, true otherwise
	 * @param currentDef
	 * @param model
	 * @param node
	 * @return
	 */
	public static  boolean getDefinition(Definition currentDef, Map model, String node) {
		model.put(WebKeys.ENTRY_DEFINITION, currentDef);
		model.put(WebKeys.CONFIG_DEFINITION, getInstance().getDefinitionModule().getDefinitionConfig());
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
				Element configEle = (Element) configRoot.selectSingleNode(node);
				model.put(WebKeys.CONFIG_ELEMENT, configEle);
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
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION_ID, defaultForumDefinition.getId());
			Document forumViewDoc = defaultForumDefinition.getDefinition();
			if (forumViewDoc != null) {
				Element forumViewElement ;
				forumViewElement = forumViewDoc.getRootElement();
				forumViewElement = (Element) forumViewElement.selectSingleNode("//item[@name='forumView' or @name='profileView' or @name='workspaceView']");
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
			getDefaultBinderDefinition(binder, model, "//item[@name='forumView' or @name='profileView' or @name='workspaceView']");
		
		}
		Map defaultFolderDefinitions = getBinderDefsAsMap(binder);
		model.put(WebKeys.FOLDER_DEFINTION_MAP, defaultFolderDefinitions);
		Map defaultEntryDefinitions = getEntryDefsAsMap(binder);
		model.put(WebKeys.ENTRY_DEFINTION_MAP, defaultEntryDefinitions);
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
	//Routine to build a definition file on the fly for viewing entries with no definition
	public static void getDefaultEntryView(Entry entry, Map model) {
		String path = "//item[@name='entryView' or @name='profileEntryView']";
		getDefaultEntryView(entry, model, path);
	}
	public static void getDefaultEntryView(Entry entry, Map model, String path) {
		//Create an empty entry definition
		Map formData = new HashMap();
		int definitionType = Definition.COMMAND;
		if (entry instanceof Principal) {
			definitionType = Definition.PROFILE_ENTRY_VIEW;
		}
		Document defDoc = getInstance().getDefinitionModule().getDefaultDefinition("ss_default_entry_view","__definition_default_entry_view", definitionType, formData);
		
		//Add the "default viewer" item
		Element entryView = (Element) defDoc.getRootElement().selectSingleNode(path);
		model.put(WebKeys.CONFIG_ELEMENT, entryView);
		model.put(WebKeys.CONFIG_DEFINITION, defDoc);
	}
		
	//Routine to build a definition file on the fly for viewing binders with no definition
	public static Document getDefaultBinderDefinition(Binder binder, Map model, String viewPath) {
		//Create an empty binder definition
		Map formData = new HashMap();
		int definitionType = Definition.WORKSPACE_VIEW;
		String definitionTitle = "__definition_default_workspace";
		if (binder instanceof Folder) {
			definitionType = Definition.FOLDER_VIEW;
			definitionTitle = "__definition_default_folder";
		} else if (binder instanceof ProfileBinder) {
			definitionType = Definition.PROFILE_VIEW;
			definitionTitle = "__definition_default_profile";
		}
		Document defDoc = getInstance().getDefinitionModule().getDefaultDefinition("ss_default_binder_def", definitionTitle, definitionType, formData);
		
		//Add the "default viewer" item
		Element entryView = (Element) defDoc.getRootElement().selectSingleNode(viewPath);
		model.put(WebKeys.CONFIG_ELEMENT, entryView);
		model.put(WebKeys.CONFIG_DEFINITION, defDoc);
		//defDoc.asXML();
		
		return defDoc;
	}
		
}
