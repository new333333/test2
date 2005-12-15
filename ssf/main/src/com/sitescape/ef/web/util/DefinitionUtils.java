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
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.module.definition.DefinitionModule;

public class DefinitionUtils {
	private static DefinitionUtils instance; // A singleton instance
	private DefinitionModule definitionModule;
	public DefinitionUtils() {
		if(instance != null)
			throw new SingletonViolationException(DefinitionUtils.class);
		
		instance = this;
	}
    private static DefinitionUtils getInstance() {
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
		Map publicProfileDefinitions = new HashMap();
		Map publicProfileEntryDefinitions = new HashMap();
		while (itPublicDefinitions.hasNext()) {
			Definition def = (Definition) itPublicDefinitions.next();
			if (def.getType() == Definition.COMMAND) {
				publicEntryDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.FORUM_VIEW) {
				publicForumDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.PROFILE_VIEW) {
				publicProfileDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.PROFILE_ENTRY_VIEW) {
				publicProfileEntryDefinitions.put(def.getId(), def);
			}
		}
		model.put(WebKeys.PUBLIC_ENTRY_DEFINITIONS, publicEntryDefinitions);
		model.put(WebKeys.PUBLIC_FOLDER_DEFINITIONS, publicForumDefinitions);
		model.put(WebKeys.PUBLIC_PROFILE_DEFINITIONS, publicProfileDefinitions);
		model.put(WebKeys.PUBLIC_PROFILE_ENTRY_DEFINITIONS, publicProfileEntryDefinitions);

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
		List folderViewDefs = binder.getBinderViewDefs();
		if (!folderViewDefs.isEmpty()) {
			Definition defaultForumDefinition = (Definition)folderViewDefs.get(0);
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, defaultForumDefinition);
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION_ID, defaultForumDefinition.getId());
			Document forumViewDoc = defaultForumDefinition.getDefinition();
			if (forumViewDoc != null) {
				Element forumViewElement ;
				forumViewElement = forumViewDoc.getRootElement();
				forumViewElement = (Element) forumViewElement.selectSingleNode("//item[@name='forumView' or @name='profileView']");
				model.put(WebKeys.CONFIG_ELEMENT, forumViewElement);
			} else {
				model.put(WebKeys.CONFIG_ELEMENT, null);
			}
			
		} else {
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, null);
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION_ID, "");
			model.put(WebKeys.CONFIG_ELEMENT, null);
		
		}
		Map defaultFolderDefinitions = getBinderDefsAsMap(binder);
		model.put(WebKeys.FOLDER_DEFINTION_MAP, defaultFolderDefinitions);
		Map defaultEntryDefinitions = getEntryDefsAsMap(binder);
		model.put(WebKeys.ENTRY_DEFINTION_MAP, defaultEntryDefinitions);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.CONFIG_DEFINITION, getInstance().getDefinitionModule().getDefinitionConfig());
	}
	public static Map getBinderDefsAsMap(Binder binder) {
		Map defaultFolderDefinitions = new HashMap();
		Iterator itDefaultFolderDefinitions = binder.getBinderViewDefs().listIterator();
		while (itDefaultFolderDefinitions.hasNext()) {
			Definition entryDef = (Definition) itDefaultFolderDefinitions.next();
			defaultFolderDefinitions.put(entryDef.getId(), entryDef);
		}
		return defaultFolderDefinitions;
	}

	public static Map getEntryDefsAsMap(Binder binder) {
		Map defaultEntryDefinitions = new HashMap();
		Iterator itDefaultEntryDefinitions = binder.getEntryDefs().listIterator();
		while (itDefaultEntryDefinitions.hasNext()) {
			Definition entryDef = (Definition) itDefaultEntryDefinitions.next();
			defaultEntryDefinitions.put(entryDef.getId(), entryDef);
		}
		return defaultEntryDefinitions;
	}
	//Routine to build a definition file on the fly for viewing entries with no definition
	public static void getDefaultEntryView(Map model) {
		//Create an empty entry definition
		Map formData = new HashMap();
		Document def = getInstance().getDefinitionModule().getDefaultDefinition("ss_default_entry_view","__definition_default_entry_view", Definition.COMMAND, formData);
		
		//Add the "default viewer" item
		Element entryView = (Element) def.getRootElement().selectSingleNode("//item[@name='entryView']");
		if (entryView != null) {
			String itemId = entryView.attributeValue("id", "");
			try {
				Element newItem = getInstance().getDefinitionModule().addItemToDefinitionDocument("default", def, itemId, "defaultEntryView", formData);
			}
			catch (DefinitionInvalidException e) {
				//An error occurred while processing the operation; pass the error message back to the jsp
				//SessionErrors.add(req, e.getClass().getName(),e.getMessage());
			}
		}
		model.put(WebKeys.CONFIG_ELEMENT, entryView);
	}
	
}
