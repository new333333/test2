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
package com.sitescape.team.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.TreeMap;
import java.util.SortedMap;

import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.SingletonViolationException;
import com.sitescape.team.comparator.StringComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.module.admin.AdminModule.AdminOperation;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;

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
	

	/**
	 * Helper to get definitions available to a definition for cross reference
	 * @param defType
	 */	
	public static SortedMap<String, Definition> getAvailableDefinitions(Long binderId, Integer defType) {
		List<Definition> definitions = getInstance().getDefinitionModule().getDefinitions(binderId, Boolean.TRUE, defType);
		return orderDefinitions(definitions, true);
	}
	public static TreeMap orderDefinitions(Collection<Definition> defs, Boolean includeDefinitionName) {
		TreeMap<String, Definition> orderedDefinitions = new TreeMap(new StringComparator(RequestContextHolder.getRequestContext().getUser().getLocale()));
		for (Definition def:defs) {
			if (def.getBinderId() != null) continue;  //do global defs first
			String title = NLT.getDef(def.getTitle());
			if (includeDefinitionName) title = title + " (" + def.getName()  + ")";
			if (Definition.VISIBILITY_DEPRECATED.equals(def.getVisibility())) {
				title += " **" + NLT.get("__definition_deprecated");
			}
			orderedDefinitions.put(title, def);
		}
		for (Definition def:defs) {
			if (def.getBinderId() == null) continue;  //now do binder level defs
			String title = NLT.getDef(def.getTitle()) + " (" + def.getName()  + ")";
			if (Definition.VISIBILITY_DEPRECATED.equals(def.getVisibility())) {
				title += " **" + NLT.get("__definition_deprecated");
			}
			if (orderedDefinitions.containsKey(title)) {
				title += " | id:" + def.getId();
			}
			orderedDefinitions.put(title, def);
		}
		return orderedDefinitions;
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
				Element familyProperty = (Element) forumViewDoc.getRootElement().selectSingleNode("//properties/property[@name='family']");
				if (familyProperty != null) {
					String family = familyProperty.attributeValue("value", "");
					model.put(WebKeys.DEFINITION_FAMILY, family);
				}
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
		Map replyDefs = getReplyDefinitions(defaultEntryDefinitions.values());
		model.put(WebKeys.REPLY_DEFINITION_MAP, replyDefs);
		model.put(WebKeys.WORKFLOW_DEFINITION_MAP, getWorkflowDefsAsMap(binder)); 
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_VIEW);
	}
	
	public static Map getReplyDefinitions(Collection<Definition> entryDefinitions) {
		Map resultMap = new HashMap<String,Definition>();
		for (Definition def:entryDefinitions) {
			List temp = getReplyListFromEntry(def);
			if(temp == null)
				continue;
			for(int i = 0; i < temp.size(); i++) {
				String key = (String)temp.get(i);
				if (resultMap.get(key) == null) {
					Definition replyDef = getDefinition(key);
					if (!entryDefinitions.contains(replyDef))
						resultMap.put(key, replyDef );
				}
			}
		}
		return resultMap;
	}
	
	public static List getReplyListFromEntry(Definition def) {
		List replyStyles = DefinitionUtils.getPropertyValueList(def.getDefinition().getRootElement(), "replyStyle");
		if (!replyStyles.isEmpty()) {
			if (replyStyles.size() == 1) {
				String replyStyleId = (String)replyStyles.get(0);
				if (Validator.isNotNull(replyStyleId)) {
					return replyStyles;
				}
			} else {
				List result = new ArrayList();
				for (int i = 0; i < replyStyles.size(); i++) {
					String replyStyleId = (String)replyStyles.get(i);
					if (Validator.isNotNull(replyStyleId)) {
						result.add(replyStyleId);
					}
				}
				return result;
			}
		}
		return null;
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

	public static Map<String, Definition> getEntryDefsAsMap(Binder binder) {
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
    public static String getWebDAVURL(PortletRequest req, Folder folder, FolderEntry entry) {
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
		strEntryURL = SsfsUtil.getEntryUrl(req, folder, entry, strRepositoryName);
		
		return strEntryURL;
    }
	
    public static String findCaptionForValue(Document definitionConfig, Element configElement, String value)
    {
    	String dataType = configElement.attributeValue("formItem");
    	Node valueNode = configElement.selectSingleNode("properties/property[@name='name']/@value");
    	if(valueNode == null) {
    			return value;
    	}
    	String fieldName = valueNode.getStringValue();
    	Node captionNode = definitionConfig.selectSingleNode("//item[@type='form']//item[@type='data' and @name='" + 
    														dataType + "' and properties/property[@name='name' and @value='" +
    														fieldName + "']]//item/properties[property[@name='name' and @value='" +
    														value + "']]/property[@name='caption']/@value");
    	String result = value;
    	if(captionNode != null) { result = captionNode.getStringValue();}
    	return result;
    }
    
    public static String findFormType(Document definitionConfig) {
    	Node formTypeNode = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm']/properties/property[@name='type']/@value");
    	if(formTypeNode != null) { return formTypeNode.getStringValue();}
    	return null;
    }
    
    public static String findCaptionForAttribute(String attributeName, Document definitionConfig)
    {
    	Node valueNode = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm']//item[@type='data' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]/properties/property[@name='caption']/@value");
    	if (valueNode == null) return "";
    	return valueNode.getStringValue();
    }
    
    public static String findAttributeType(String attributeName, Document definitionConfig) {
    	Element node = (Element)definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm']//item[@type='data' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
    	if (node == null) return "";
    	return node.attributeValue("name");
    }
    
    public static Map findSelectboxSelectionsAsMap(String attributeName, String definitionId) {
    	Definition definition = DefinitionHelper.getDefinition(definitionId);
    	if (definition == null) {
    		return Collections.EMPTY_MAP;
    	}
    	Document definitionConfig = definition.getDefinition();
    	return findSelectboxSelectionsAsMap(attributeName, definitionConfig);
     	
    }
    public static Map findSelectboxSelectionsAsMap(String attributeName, Document definitionConfig) {
  	if (definitionConfig == null) {
    		return Collections.EMPTY_MAP;
    	}
    	Node node = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm']//item[@name='selectbox' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
    	if (node == null) {
    		return Collections.EMPTY_MAP;
    	}    	
    	return findSelectboxSelectionsAsMap((org.dom4j.Element)node);
    }
    
    public static Map findSelectboxSelectionsAsMap(Element element) {
    	Map result = new HashMap();
    	if (element == null) {
    		return result;
    	}
		Iterator attributeValuesIt = DefinitionHelper.findSelectboxSelections(element).iterator();
		while (attributeValuesIt.hasNext()) {
			
			String name = null;
			String caption = null;
			
			Iterator attributeValueIt = ((Map)attributeValuesIt.next()).entrySet().iterator();
			while (attributeValueIt.hasNext()) {
				Map.Entry mapEntry = (Map.Entry)attributeValueIt.next();
				if ("name".equals(mapEntry.getKey())) {
					name = (String)mapEntry.getValue();
				} else if ("caption".equals(mapEntry.getKey())) {
					caption = (String)mapEntry.getValue();
				}
			}
			result.put(name, caption);
			
		}
		return result;
    }
    
    public static List findSelectboxSelections(String attributeName, Document definitionConfig) {
    	Node node = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm']//item[@name='selectbox' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
    	if (node == null) {
    		return Collections.EMPTY_LIST;
    	}
    	return findSelectboxSelections((org.dom4j.Element)node);
    }
    
    public static List findSelectboxSelections(Element element) {
    	if (element == null) {
    		return Collections.EMPTY_LIST;
    	}    	
    	List nodes = element.selectNodes("item[@name='selectboxSelection']/properties");
    	if (nodes == null) {
    		return Collections.EMPTY_LIST;
    	}
    	
    	List result = new ArrayList();
    	Iterator nodesIt = nodes.iterator();
    	while (nodesIt.hasNext()) {
    		Map property = new HashMap();
    		
    		Iterator selectionPropertiesIt = ((Element)nodesIt.next()).elementIterator();
    		while (selectionPropertiesIt.hasNext()) {
    			Element propertyEl = (Element)selectionPropertiesIt.next();
    			String propertyName = propertyEl.attributeValue("name");
   				property.put(propertyName, propertyEl.attributeValue("value"));
    		}
    		result.add(property);
    	}
    	
    	return result;
    }
    
    public static List findRadioSelections(String attributeName, String definitionId) {
    	return DefinitionHelper.findRadioSelections(attributeName, 
    			DefinitionHelper.getDefinition(definitionId).getDefinition());
    }
    
    public static List findRadioSelections(String attributeName, Document definitionConfig) {
    	Node node = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm']//item[@name='radio' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
    	if (node == null) {
    		return Collections.EMPTY_LIST;
    	}
    	return findRadioSelections((Element)node);
    }
    
    public static List findRadioSelections(Element element) {
       	if (element == null) {
    		return Collections.EMPTY_LIST;
    	}    	
    	List nodes = element.selectNodes("item[@name='radioSelection']/properties");
    	if (nodes == null) {
    		return Collections.EMPTY_LIST;
    	}
   	
    	List result = new ArrayList();
    	Iterator nodesIt = nodes.iterator();
    	while (nodesIt.hasNext()) {
    		Map property = new HashMap();
    		
    		Iterator selectionPropertiesIt = ((Element)nodesIt.next()).elementIterator();
    		while (selectionPropertiesIt.hasNext()) {
    			Element propertyEl = (Element)selectionPropertiesIt.next();
    			String propertyName = propertyEl.attributeValue("name");
   				property.put(propertyName, propertyEl.attributeValue("value"));
    		}
    		result.add(property);
    	}
    	
    	return result;
    }
    public static Map findRadioSelectionsAsMap(String attributeName, Document definitionConfig) {
       	Node node = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm']//item[@name='radio' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
    	if (node == null) {
    		return Collections.EMPTY_MAP;
    	}
    	return findRadioSelectionsAsMap((Element)node);
    }
    public static Map findRadioSelectionsAsMap(Element element) {
    	Map result = new HashMap();
    	if (element == null) {
    		return result;
    	}
		Iterator attributeValuesIt = DefinitionHelper.findRadioSelections(element).iterator();
		while (attributeValuesIt.hasNext()) {
			
			String name = null;
			String caption = null;
			
			Iterator attributeValueIt = ((Map)attributeValuesIt.next()).entrySet().iterator();
			while (attributeValueIt.hasNext()) {
				Map.Entry mapEntry = (Map.Entry)attributeValueIt.next();
				if ("name".equals(mapEntry.getKey())) {
					name = (String)mapEntry.getValue();
				} else if ("caption".equals(mapEntry.getKey())) {
					caption = (String)mapEntry.getValue();
				}
			}
			result.put(name, caption);
			
		}
		return result;
    }

    public static Map findAttributeSelectionsAsMap(String attributeName, DefinableEntity entity) {
    	Map result = new HashMap();
    	if (entity == null) {
    		return result;
    	}
    	Binder binder = entity.getParentBinder();
    	result.put(attributeName, binder.getCustomAttribute(attributeName).getValueSet());
		Iterator attributeSetsIt = binder.getCustomAttribute(attributeName).getValueSet().iterator();
		while (attributeSetsIt.hasNext()) {
			String attributeSetName = (String)attributeSetsIt.next();
			if (!attributeSetName.equals("")) {
				attributeSetName = attributeName + DefinitionModule.ENTRY_ATTRIBUTES_SET + attributeSetName;
				result.put(attributeSetName, binder.getCustomAttribute(attributeSetName).getValueSet());
			}
		}
		return result;
    }
      
    
	public static List findPlacesAttributes(Document definitionConfig) {
		List result = new ArrayList();
		
    	List nodes = definitionConfig.selectNodes("//item[@type='form']//item[@name='entryFormForm']//item[@type='data' and @name='places']/properties/property[@name='name']/@value");
    	if (nodes == null) {
    		return result;
    	}
    	
    	Iterator it = nodes.iterator();
    	while (it.hasNext()) {
    		Node node = (Node)it.next();
    		result.add(node.getStringValue());
    	}
    	return result;
	}
    
	
	public static String getCaptionFromElement(Definition def, String eleName, String type, 
			String defaultCaption) {
		Document defDoc = def.getDefinition();
		Element rootEle = defDoc.getRootElement();
		String retVal = defaultCaption;
		Element itemEle = (Element) rootEle.selectSingleNode("//item[@type='"+type+"']//item/properties/property[@name='name' and @value='"+eleName+"']");
		if (itemEle != null) {
			itemEle = itemEle.getParent().getParent();
			Element captionEle = (Element) itemEle.selectSingleNode("./properties/property[@name='caption']");
			if (captionEle != null) {
				String caption = (String)captionEle.attributeValue("value");
				if (caption != null && !caption.equals("")) retVal = caption;
			}
		}
		return retVal;
	}
	
	public static String getCaptionsFromValues(Definition def, String eleName, String values) {
		Document defDoc = def.getDefinition();
		Element rootEle = defDoc.getRootElement();
		String retVal = "";
		Element itemEle = (Element) rootEle.selectSingleNode("//item[@type='form']//item/properties/property[@name='name' and @value='"+eleName.replaceAll("'", "")+"']");
		if (itemEle == null) return values;
		itemEle = itemEle.getParent().getParent();
		String itemType = itemEle.attributeValue("name");
		if (itemType != null && itemType.equals("selectbox")) {
			String[] valueList = values.split(",");
			for (int i = 0; i < valueList.length; i++) {
				String value = valueList[i].trim();
				if (value.equals("")) continue;
				//We have a value, now find the selectboxSelection item that corresponds to this value
				Element selectboxSelectionEle = (Element) itemEle.selectSingleNode("./item[@name='selectboxSelection']/properties/property[@name='name' and @value='"+value.replaceAll("'","")+"']");
				if (selectboxSelectionEle != null) {
					selectboxSelectionEle = selectboxSelectionEle.getParent().getParent();
					Element selectboxSelectionCaptionEle = (Element) selectboxSelectionEle.selectSingleNode("./properties/property[@name='caption']");
					if (selectboxSelectionCaptionEle != null) {
						value = selectboxSelectionCaptionEle.attributeValue("value", value);
						if (!retVal.equals("")) retVal += ", ";
						retVal += NLT.getDef(value);						
					} else {
						if (!retVal.equals("")) retVal += ", ";
						retVal += value;
					}
				} else {
					if (!retVal.equals("")) retVal += ", ";
					retVal += value;
				}
			}
				
		} else if (itemType != null && itemType.equals("radio")) {
			String value = values.trim();
			if (value.equals("")) return values;
			//We have a value, now find the radioSelection item that corresponds to this value
			Element radioSelectionEle = (Element) itemEle.selectSingleNode("./item[@name='radioSelection']/properties/property[@name='name' and @value='"+value.replaceAll("'","")+"']");
			if (radioSelectionEle != null) {
				radioSelectionEle = radioSelectionEle.getParent().getParent();
				Element radioSelectionCaptionEle = (Element) radioSelectionEle.selectSingleNode("./properties/property[@name='caption']");
				if (radioSelectionCaptionEle != null) {
					value = radioSelectionCaptionEle.attributeValue("value", value);
					value = NLT.getDef(value);
				}
			}
			retVal = value;
			
		} else if (itemType != null && itemType.equals("checkbox")) {
			String value = values.trim();
			if (value.equals("true")) {
				retVal = NLT.get("general.Yes");
			} else {
				retVal = NLT.get("general.No");
			}
			
		} else {
			return values;
		}
		return retVal;
	}
	
	public static void buildMashupBeans(AllModulesInjected bs, DefinableEntity entity, 
			Document definitionConfig, Map model) {
		Map mashupEntries = new HashMap();
		Map mashupBinders = new HashMap();
		Map mashupBinderEntries = new HashMap();
    	List nodes = definitionConfig.selectNodes("//item[@type='form']//item[@type='data' and @name='mashupCanvas']/properties/property[@name='name']/@value");
    	if (nodes == null) {
    		return;
    	}
    	
    	Iterator it = nodes.iterator();
    	while (it.hasNext()) {
    		Node node = (Node)it.next();
    		String attrName = node.getStringValue();
    		CustomAttribute attr = entity.getCustomAttribute(attrName);
    		if (attr != null) {
	    		String mashupValue = (String) attr.getValue();
	        	String[] mashupValues = mashupValue.split(";");
	        	for (int i = 0; i < mashupValues.length; i++) {
	        		String[] mashupItemValues = mashupValues[i].split(",");
					Map mashupItemAttributes = new HashMap();
					if (mashupItemValues.length > 0) {
						//Build a map of attributes
						for (int j = 0; j < mashupItemValues.length; j++) {
							String[] valueSet = mashupItemValues[j].split("=");
							if (valueSet.length == 2) {
								mashupItemAttributes.put(valueSet[0], valueSet[1]);
							}
						}
					}
	        		String type = mashupItemValues[0];
	        		if (ObjectKeys.MASHUP_TYPE_ENTRY.equals(type) && 
	        				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRY_ID) &&
	        				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID).equals("")) {
	        			try {
	        				FolderEntry entry = bs.getFolderModule().getEntry(null, 
	        						Long.valueOf((String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID)));
	        				mashupEntries.put(entry.getId().toString(), entry);
	        			} catch(Exception e) {}
	        		} else if (ObjectKeys.MASHUP_TYPE_FOLDER.equals(type) && 
	        				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_FOLDER_ID) && 
	        				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_FOLDER_ID).equals("")) {
	        			try {
	        				Binder binder = bs.getBinderModule().getBinder(
	        						Long.valueOf((String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_FOLDER_ID)));
	        				mashupBinders.put(binder.getId().toString(), binder);
	        				Map options = new HashMap();
	        				Integer searchMaxHits = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
	        				options.put(ObjectKeys.SEARCH_MAX_HITS, searchMaxHits);
	        				try {
	        					if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRIES_TO_SHOW)) {
	        						Integer entriesToShow = Integer.valueOf(
	        								(String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRIES_TO_SHOW));
	        						options.put(ObjectKeys.SEARCH_MAX_HITS, entriesToShow);
	        					}
	        				} catch(Exception e) {}
	        				options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
	        				options.put(ObjectKeys.SEARCH_SORT_BY, Constants.LASTACTIVITY_FIELD);
	        				Map folderEntries = bs.getFolderModule().getEntries(binder.getId(), options);
	        				mashupBinderEntries.put(binder.getId().toString(), 
	        						folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
	        			} catch(Exception e) {}
	        		}
	        	}
    		}
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_SHOW_BRANDING);
    		if (attr != null) model.put(WebKeys.MASHUP_SHOW_BRANDING, attr.getValue());
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_MASTHEAD);
    		if (attr != null) model.put(WebKeys.MASHUP_HIDE_MASTHEAD, attr.getValue());
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_SIDEBAR);
    		if (attr != null) model.put(WebKeys.MASHUP_HIDE_SIDEBAR, attr.getValue());
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_TOOLBAR);
    		if (attr != null) model.put(WebKeys.MASHUP_HIDE_TOOLBAR, attr.getValue());
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_FOOTER);
    		if (attr != null) model.put(WebKeys.MASHUP_HIDE_FOOTER, attr.getValue());
    	}
    	if (entity instanceof Binder && 
    			bs.getBinderModule().testAccess((Binder)entity, BinderOperation.manageConfiguration)) {
    		//Don't hide the toolbar from the manager
    		model.put(WebKeys.MASHUP_SHOW_ALTERNATE_TOOLBAR, true);
    	}
		if (bs.getAdminModule().testAccess(AdminOperation.manageFunction)) {
			//This user can do site admin functions
			model.put(WebKeys.MASHUP_SITE_ADMINISTRATOR, true);
		}
    	model.put(WebKeys.MASHUP_BINDERS, mashupBinders);
    	model.put(WebKeys.MASHUP_BINDER_ENTRIES, mashupBinderEntries);
    	model.put(WebKeys.MASHUP_ENTRIES, mashupEntries);
	}
}
