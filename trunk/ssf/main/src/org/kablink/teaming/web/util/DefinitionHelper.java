/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CommaSeparatedValue;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DefinitionInvalidException;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SSBlobSerializable;
import org.kablink.teaming.domain.SSClobString;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionConfigurationBuilder;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

import org.springframework.core.io.ClassPathResource;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class DefinitionHelper {
	protected static final Log logger = LogFactory.getLog(Definition.class);
	
	private static DefinitionHelper instance; // A singleton instance
	private DefinitionModule definitionModule;
	private BinderModule binderModule;
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
    public void setBinderModule(BinderModule binderModule) {
    	this.binderModule = binderModule;
    }
    public BinderModule getBinderModule() {
    	return binderModule;
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
	public static SortedMap<String, Definition> getAvailableReplyDefinitions(Long binderId) {
		List<Definition> defs = getInstance().getDefinitionModule().getDefinitions(binderId, Boolean.TRUE, Definition.FOLDER_ENTRY);
		Binder binder = null;
		if (binderId != null) binder = getInstance().getBinderModule().getBinder(binderId);
		defs = Utils.validateDefinitions(defs, binder, Definition.FOLDER_ENTRY);
		return orderDefinitions(defs, true);
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
	 * @param name
	 */
	public static Definition getDefinitionByName(String name) {
		try {
			return getInstance().getDefinitionModule().getDefinitionByName(name);
		} catch (NoDefinitionByTheIdException nd) {
			logger.debug("DefinitionHelper.getDefinitionByName(NoDefinitionByTheIdException):  '" + ((null == name) ? "<null>" : name) + "':  :  Ignored");
			return null;
		}
		
	}
	
	/**
	 * Helper to get definition for other helpers
	 * @param id
	 */
	public static Definition getDefinition(String id) {
		try {
			return getInstance().getDefinitionModule().getDefinition(id);
		} catch (NoDefinitionByTheIdException nd) {
			logger.debug("DefinitionHelper.getDefinition(NoDefinitionByTheIdException):  '" + ((null == id) ? "<null>" : id) + "':  :  Ignored");
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
		Document currentDefDoc = null;
		if(currentDef != null)
			currentDefDoc = currentDef.getDefinition();
		return getDefinition(currentDefDoc, model, node);
	}

	public static boolean getDefinition(Document currentDefDoc, Map model, String node) {
		Document configDoc = currentDefDoc;
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
		Document currentDefDoc = null;
		if(currentDef != null)
			currentDefDoc = currentDef.getDefinition();
		return getDefinitionElement(currentDefDoc, model, elementName);
	}

	public static boolean getDefinitionElement(Document currentDefDoc, Map model, String elementName) {
		Document configDoc = currentDefDoc;
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
		Document defDoc = def.getDefinition();
		if (defDoc == null) return null;
		List replyStyles = DefinitionUtils.getPropertyValueList(defDoc.getRootElement(), "replyStyle");
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
		String path = "//item[@name='entryView' or @name='profileEntryView' or @name='groupEntryView']";
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
	//Routine to see if an item is inside a "conditional" element
	public static boolean checkIfMultipleAllowed(Element item, Element root) {
		if ("false".equals(item.attributeValue("multipleAllowed"))) {
			Element parentItem = item;
			while (root != null && !parentItem.equals(root)) {
				String name = parentItem.attributeValue("name", "");
				if (name.equals("conditional") || 
						name.equals("conditionalView") || 
						name.equals("conditionalProfileFormItem") || 
						name.equals("conditionalProfileViewItem")) return true;
				parentItem = parentItem.getParent();
				if (parentItem == null) break;
			}
			return false;
		}
		return true;
	}

    private static String getWebDAVURL(PortletRequest pReq, HttpServletRequest hReq, Folder folder, FolderEntry entry) {
    	String strEntryURL = "";
		if (entry.getEntryDefId() == null) {
			getInstance().getDefinitionModule().setDefaultEntryDefinition(entry);
		}
		Document entryDefDocTree = entry.getEntryDefDoc();
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
		if (null != pReq)
		     strEntryURL = SsfsUtil.getEntryUrl(pReq, folder, entry, strRepositoryName);
		else strEntryURL = SsfsUtil.getEntryUrl(hReq, folder, entry, strRepositoryName);
		
		return strEntryURL;
    }
    public static String getWebDAVURL(PortletRequest pReq, Folder folder, FolderEntry entry) {
    	return getWebDAVURL(pReq, null, folder, entry);
    }
    
    public static String getWebDAVURL(HttpServletRequest hReq, Folder folder, FolderEntry entry) {
    	return getWebDAVURL(null, hReq, folder, entry);
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
    	Node formTypeNode = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']/properties/property[@name='type']/@value");
    	if(formTypeNode != null) { return formTypeNode.getStringValue();}
    	return null;
    }
    
    public static String findCaptionForAttribute(String attributeName, Document definitionConfig)
    {
    	Node valueNode = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@type='data' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]/properties/property[@name='caption']/@value");
    	if (valueNode == null) return "";
    	return valueNode.getStringValue();
    }
    
    public static Element findAttribute(String attributeName, Document definitionConfig) {
    	Element node = (Element)definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@type='data' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
    	return node;
    }
    
    public static Element findDataItem(String itemName, Document definitionConfig) {
    	Element node = (Element)definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@type='data' and @name='"+itemName+"']");
    	return node;
    }
    
    public static String findAttributeType(String attributeName, Document definitionConfig) {
    	Element node = (Element)definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@type='data' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
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
  		Node node = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@name='selectbox' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
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
    	Node node = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@name='selectbox' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
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
    	Node node = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@name='radio' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
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
       	Node node = definitionConfig.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@name='radio' and properties/property[@name='name' and @value='"+attributeName.replaceAll("'","")+"']]");
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
		
    	List nodes = definitionConfig.selectNodes("//item[@type='form']//item[@name='entryFormForm' or @name='profileEntryFormForm' or @name='customJsp']//item[@type='data' and @name='places']/properties/property[@name='name']/@value");
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
    
	public static String getItemProperty(Element item, String name) {
		String result = "";
		Element propertyEle = (Element)item.selectSingleNode("./properties/property[@name='"+name+"']");
		if (propertyEle != null) {
			result = propertyEle.attributeValue("value", "");
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
	
	
	//Routine to figure out which attached file is the primary file (if any)
	public static void getPrimaryFile(FolderEntry entry, Map model) {
		if(entry.supportsCustomFields()) {
			Document defDoc = entry.getEntryDefDoc();
			Element root = defDoc.getRootElement();
			
			//See if there is a title element getting its source from some other element
	       	Element titleEle = (Element) root.selectSingleNode("//item[@type='form']//item[@name='entryFormForm' or @name='customJsp']//item[@name='title']");
	       	if (titleEle != null) {
	       		Element itemSource = (Element) titleEle.selectSingleNode("properties/property[@name='itemSource']");
	       		if (itemSource != null) {
	       			String itemDataName = itemSource.attributeValue("value", "");
	       			if (!itemDataName.equals("") && !"ss_none".equals(itemDataName)) {
	       				//Found the data element that is the source for the title
						model.put(WebKeys.PRIMARY_FILE_ATTRIBUTE, itemDataName);
	       			}
	       		}
	       	}
		}
		else {
	       	// Short circuit the use of definition facility which is slow and expensive.
			model.put(WebKeys.PRIMARY_FILE_ATTRIBUTE, "upload");
		}
	}

	
	public static void buildMashupBeans(AllModulesInjected bs, DefinableEntity entity, 
			Document definitionConfig, Map model, RenderRequest request ) {
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		String mashupViewType = "";
		Map mashupEntries = new HashMap();
		Map mashupEntryReplies = new HashMap();
		Map mashupBinders = new HashMap();
		Map mashupBinderEntries = new HashMap();
		List mashupMyCalendarEntries = new ArrayList();
		List mashupMyTaskEntries = new ArrayList();
		Map mashupMyTaskBinders = new HashMap();
    	List nodes = definitionConfig.selectNodes("//item[@type='form']//item[@type='data' and @name='mashupCanvas']/properties/property[@name='name']/@value");
    	if (nodes == null) {
    		return;
    	}
		ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
		String zoneInfoId = zoneInfo.getId();
		if (zoneInfoId == null) zoneInfoId = "";
    	
    	Iterator it = nodes.iterator();
    	while (it.hasNext()) {
    		Node node = (Node)it.next();
    		String attrName = node.getStringValue();
    		CustomAttribute attr = entity.getCustomAttribute(attrName);
    		if (attr != null) {
	    		String mashupValue = (String) attr.getValue();
	        	String[] mashupValues = mashupValue.split(";");
	        	for (int i = 0; i < mashupValues.length; i++) {
	        		String	attrValue;
	        		String[] mashupItemValues = mashupValues[i].split(",");
					Map mashupItemAttributes = new HashMap();
					attrValue = null;
					if (mashupItemValues.length > 0) {
						//Build a map of attributes
						for (int j = 0; j < mashupItemValues.length; j++) {
							int k = mashupItemValues[j].indexOf("=");
							if (k > 0) {
								String a = mashupItemValues[j].substring(0, k);
								String v = mashupItemValues[j].substring(k+1, mashupItemValues[j].length());
								attrValue = v;
								mashupItemAttributes.put(a, v);
							}
						}
					}

					String type = mashupItemValues[0];
	        		if ( (ObjectKeys.MASHUP_TYPE_ENTRY.equals(type) || ObjectKeys.MASHUP_TYPE_CUSTOM_JSP.equals(type) || ObjectKeys.MASHUP_TYPE_ENHANCED_VIEW.equals( type ) ) && 
	        				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRY_ID) &&
	        				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID).equals("")) {
	        			try {
	        				Long entryId = Long.valueOf((String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID));
	        				String zoneUUID = "";
	        				if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
	        					zoneUUID = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ZONE_UUID);
	        				}
	        				String zoneEntryId = "";
	        				if (!zoneUUID.equals("")) zoneEntryId = zoneUUID + "." + entryId.toString();
	        				if (!zoneInfoId.equals(zoneUUID)) {
	        					entryId = bs.getFolderModule().getZoneEntryId(entryId, zoneUUID);
	        				}
		        			if (entryId == null) continue;
	        				FolderEntry entry = bs.getFolderModule().getEntry(null, entryId);
	        				mashupEntries.put(entry.getId().toString(), entry);
	        				//If this is from another zone, put a "zone.entryId" link to the real entry in this zone
	        				if (!zoneEntryId.equals("")) mashupEntries.put(zoneEntryId, entry);
	        				
	        				Map entryAccessMap = BinderHelper.getAccessControlEntityMapBean(model, entry);
	        				if (bs.getFolderModule().testAccess(entry, FolderOperation.addReply)) 
	        					entryAccessMap.put("addReply", new Boolean(true));
	        				if (bs.getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) 
	        					entryAccessMap.put("modifyEntry", new Boolean(true));
	        				
	        				//If this is a custom jsp, or an enhanced view, give it the replies, too
	        				if (ObjectKeys.MASHUP_TYPE_CUSTOM_JSP.equals(type) || ObjectKeys.MASHUP_TYPE_ENHANCED_VIEW.equals( type ) ) {
	        					boolean isPreDeleted = entry.isPreDeleted();
	        					if (!isPreDeleted) {
	        						Map folderEntries  = bs.getFolderModule().getEntryTree(entry.getParentFolder().getId(), entryId, true);
	        						mashupEntryReplies.put(entry.getId().toString(), folderEntries);
	        					}
	        				}
	        			} catch(Exception e) {
	        				logger.debug("DefinitionHelper.buildMashupBeans(Exception:  '" + MiscUtil.exToString(e) + "'):  1:  Ignored");
	        			}
	        		} else if ((ObjectKeys.MASHUP_TYPE_FOLDER.equals(type) || ObjectKeys.MASHUP_TYPE_CUSTOM_JSP.equals(type) || ObjectKeys.MASHUP_TYPE_ENHANCED_VIEW.equals( type ) ) && 
	        				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_FOLDER_ID) && 
	        				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_FOLDER_ID).equals("")) {
	        			try {
	        				Long binderId = Long.valueOf((String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_FOLDER_ID));
	        				String zoneUUID = "";
	        				if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
	        					zoneUUID = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ZONE_UUID);
	        				}
	        				String zoneBinderId = "";
	        				if (!zoneUUID.equals("")) zoneBinderId = zoneUUID + "." + binderId.toString();
	        				if (!zoneInfoId.equals(zoneUUID))
	        						binderId = bs.getBinderModule().getZoneBinderId(binderId, zoneUUID, EntityType.folder.name());
	        				if (binderId == null) continue;
	        				Binder binder = bs.getBinderModule().getBinder(binderId);
	        				mashupBinders.put(binder.getId().toString(), binder);
	        				//If this is from another zone, put a "zone.entryId" link to the real entry in this zone
	        				if (!zoneBinderId.equals("")) mashupBinders.put(zoneBinderId, binder);
	        				Map options = new HashMap();
	        				Integer searchMaxHits = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
	        				options.put(ObjectKeys.SEARCH_MAX_HITS, searchMaxHits);
	        				try {
	        					if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRIES_TO_SHOW)) {
	        						Integer entriesToShow = Integer.valueOf(
	        								(String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRIES_TO_SHOW));
	        						options.put(ObjectKeys.SEARCH_MAX_HITS, entriesToShow);
		        					//See if there was a search already done for this folder
		        					if (mashupBinderEntries.containsKey(binder.getId().toString())) {
		        						List fEntries = (List)mashupBinderEntries.get(binder.getId().toString());
		        						if (fEntries.size() > entriesToShow) {
		        							//Make sure not to truncate the existing list
		        							options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(fEntries.size()));
		        						}
		        					}
	        					}
	        				} catch(Exception e) {
		        				logger.debug("DefinitionHelper.buildMashupBeans(Exception:  '" + MiscUtil.exToString(e) + "'):  2:  Ignored");
	        				}
	        				options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
	        				options.put(ObjectKeys.SEARCH_SORT_BY, Constants.LASTACTIVITY_FIELD);
	        				Map folderEntries = bs.getFolderModule().getEntries(binder.getId(), options);
	        				mashupBinderEntries.put(binder.getId().toString(), 
	        						folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
	        				//If this is from another zone, put a "zone.entryId" link to the real entry in this zone
	        				if (!zoneBinderId.equals("")) {
	        					mashupBinderEntries.put(zoneBinderId, folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
	        				}
	        				//If this is a custom jsp, or an enhanced view, get the entry replies, too
	        				if (ObjectKeys.MASHUP_TYPE_CUSTOM_JSP.equals(type) || ObjectKeys.MASHUP_TYPE_ENHANCED_VIEW.equals( type ) ) {
	        					List entries = (List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
	        					Iterator itEntries = entries.iterator();
	        					while (itEntries.hasNext()) {
	        						Map entry = (Map)itEntries.next();
	        						String entryBinderId = (String)entry.get(Constants.BINDER_ID_FIELD);
	        						String entryEntryId = (String)entry.get(Constants.DOCID_FIELD);
	        						if (entryBinderId != null && entryEntryId != null) {
	        							FolderEntry fe = bs.getFolderModule().getEntry(Long.valueOf(entryBinderId), Long.valueOf(entryEntryId));
	        							mashupEntries.put(entryEntryId, fe);
	        							
	        	        				Map entryAccessMap = BinderHelper.getAccessControlEntityMapBean(model, fe);
	        	        				if (bs.getFolderModule().testAccess(fe, FolderOperation.addReply)) 
	        	        					entryAccessMap.put("addReply", new Boolean(true));
	        	        				if (bs.getFolderModule().testAccess(fe, FolderOperation.modifyEntry)) 
	        	        					entryAccessMap.put("modifyEntry", new Boolean(true));

		        						Map entryEntries  = bs.getFolderModule().getEntryTree(Long.valueOf(entryBinderId), Long.valueOf(entryEntryId), false);
		        						mashupEntryReplies.put(entryEntryId, entryEntries);
		        					}
	        					}
	        				}
	        						
	        			} catch(Exception e) {
	        				logger.debug("DefinitionHelper.buildMashupBeans(Exception:  '" + MiscUtil.exToString(e) + "'):  3:  Ignored");
	        			}
	        		} else if (ObjectKeys.MASHUP_TYPE_ENHANCED_VIEW.equals( type ) &&
	        				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENHANCED_VIEW_JSP_NAME) && 
	        				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENHANCED_VIEW_JSP_NAME).equals("")) {
	        			//See what type of view this is
	        			String jsp = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENHANCED_VIEW_JSP_NAME);
	        			if (jsp.equals(ObjectKeys.MASHUP_ATTR_ENHANCED_VIEW_MY_CALENDAR_EVENTS)) {
	        			} else if (jsp.equals(ObjectKeys.MASHUP_ATTR_ENHANCED_VIEW_MY_TASKS)) {
	        				mashupViewType = ObjectKeys.MASHUP_VIEW_TYPE_MY_TASKS;
	        				Map options = new HashMap();
	        				options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES));
	        				
	        				Integer searchUserOffset = 0;
	        				Integer searchLuceneOffset = 0;
	        				options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
	        				options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
	        				
	        				Integer maxHits = new Integer(ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES);
	        				options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
	        				
	        				Integer summaryWords = new Integer(20);
	        				options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
	        				
	        				Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
	        				if (searchUserOffset > 0) {
	        					intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
	        				}
	        				options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

	        				int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
	        				int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
	        				
	        				User user = RequestContextHolder.getRequestContext().getUser();
	        				UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());

	        				List groups = new ArrayList();
	        				List groupsS = new ArrayList();
	        				List teams = new ArrayList();
	        				
	        				ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
	        				groups.addAll(profileDao.getApplicationLevelGroupMembership(user.getId(), user.getZoneId()));
	        				Iterator itG = groups.iterator();
	        				while (itG.hasNext()) {
	        					groupsS.add(itG.next().toString());
	        				}
	        				Iterator teamMembershipsIt = bs.getBinderModule().getTeamMemberships(user.getId(), org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD)).iterator();
	        				while (teamMembershipsIt.hasNext()) {
	        					teams.add(((Map)teamMembershipsIt.next()).get(Constants.DOCID_FIELD));
	        				}
	        				
	        				//Get the tasks irrespective of due dates
	        				Criteria crit = SearchUtils.tasksForUser(user.getId(), 
	        									(String[])groupsS.toArray(new String[groupsS.size()]), 
	        									(String[])teams.toArray(new String[teams.size()]));
	        				Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults,null);

        					mashupMyTaskEntries.addAll((List)results.get(ObjectKeys.SEARCH_ENTRIES));

        					//Get the task binders so the title can be shown
    				    	Iterator it2 = mashupMyTaskEntries.iterator();
    				    	while (it2.hasNext()) {
    				    		Map entry = (Map)it2.next();
    							String id = (String)entry.get(Constants.BINDER_ID_FIELD);
    							if (id != null && !mashupMyTaskBinders.containsKey(id)) {
    								Long bId = new Long(id);
    								try {
    									Binder place = bs.getBinderModule().getBinder(bId);
    									mashupMyTaskBinders.put(id, place);
    								} catch(Exception e) {}
    							}
    				    	}

        					mashupViewType = ObjectKeys.MASHUP_VIEW_TYPE_MY_TASKS;
	        			}
	        		} else if (ObjectKeys.MASHUP_TYPE_BINDER_URL.equals(type) && 
	        				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_BINDER_ID) && 
	        				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_BINDER_ID).equals("")) {
	        			try {
	        				Long binderId = Long.valueOf((String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_BINDER_ID));
	        				String zoneUUID = "";
	        				if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
	        					zoneUUID = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ZONE_UUID);
	        				}
	        				String zoneBinderId = "";
	        				if (!zoneUUID.equals("")) zoneBinderId = zoneUUID + "." + binderId.toString();
	        				if (!zoneInfoId.equals(zoneUUID))
	        					binderId = bs.getBinderModule().getZoneBinderId(binderId, zoneUUID, EntityType.folder.name());
	        				if (binderId == null) continue;
	        				Binder binder = bs.getBinderModule().getBinder(binderId);
	        				mashupBinders.put(binder.getId().toString(), binder);
	        				//If this is from another zone, put a "zone.entryId" link to the real entry in this zone
	        				if (!zoneBinderId.equals("")) mashupBinders.put(zoneBinderId, binder);
	        			} catch(Exception e) {
	        				logger.debug("DefinitionHelper.buildMashupBeans(Exception:  '" + MiscUtil.exToString(e) + "'):  4:  Ignored");
	        			}
	        		} else if (ObjectKeys.MASHUP_TYPE_ENTRY_URL.equals(type) && 
	        				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRY_ID) && 
	        				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID).equals("")) {
	        			try {
	        				Long entryId = Long.valueOf((String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID));
	        				String zoneUUID = "";
	        				if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
	        					zoneUUID = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ZONE_UUID);
	        				}
	        				String zoneEntryId = "";
	        				if (!zoneUUID.equals("")) zoneEntryId = zoneUUID + "." + entryId.toString();
	        				if (!zoneInfoId.equals(zoneUUID))
	        					entryId = bs.getFolderModule().getZoneEntryId(entryId, zoneUUID);
	        				if (entryId == null) continue;
	        				FolderEntry entry = bs.getFolderModule().getEntry(null, entryId);
	        				mashupEntries.put(entry.getId().toString(), entry);
	        				//If this is from another zone, put a "zone.entryId" link to the real entry in this zone
	        				if (!zoneEntryId.equals("")) mashupEntries.put(zoneEntryId, entry);
	        			} catch(Exception e) {
	        				logger.debug("DefinitionHelper.buildMashupBeans(Exception:  '" + MiscUtil.exToString(e) + "'):  5:  Ignored");
	        			}
	        		}
	        		else if ( type != null && type.equals( "utility" ) ) {
	        			// We are working with a utility element in the mashup.
	        			// Are we dealing with a "sign in" widget?
	        			if ( attrValue != null && attrValue.equals( "signInForm" ) )
	        			{
	        				// Yes
	        		    	// Is self registration permitted?
	        		    	if ( MiscUtil.canDoSelfRegistration( bs ) )
	        		    	{
	        		    		// Yes.
	        		    		// Add the information needed to support the "Create new account" ui to the response.
	        		    		MiscUtil.addCreateNewAccountDataToResponse( bs, request, model );
	        		    	}
	        			}
	        		}
	        	}
    		}
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_SHOW_BRANDING);
    		if (attr != null) model.put(WebKeys.MASHUP_SHOW_BRANDING, attr.getValue());
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_SHOW_NAVIGATION);
    		if (attr != null) model.put(WebKeys.MASHUP_SHOW_NAVIGATION, attr.getValue());
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_SHOW_FAVORITES_AND_TEAMS);
    		if (attr != null) model.put(WebKeys.MASHUP_SHOW_FAVORITES_AND_TEAMS, attr.getValue());
    		attr = entity.getCustomAttribute(attrName + DefinitionModule.MASHUP_HIDE_TOOLBAR);
    		if (attr != null) model.put(WebKeys.MASHUP_HIDE_TOOLBAR, attr.getValue());

    		// Handle the mashup properties.
    		{
    			LandingPageProperties lpProperties;
    			
    			lpProperties = getLandingPageProperties( request, entity );
    			
    			if ( lpProperties != null )
					model.put( WebKeys.MASHUP_PROPERTIES, lpProperties.getPropertiesAsXMLString() );
    		}
    		
    		// Add the language the tinyMCE editor should use.
    		model.put( "tinyMCELang", GwtUIHelper.getTinyMCELanguage() );
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
		model.put(WebKeys.MASHUP_VIEW_TYPE, mashupViewType);
		model.put(WebKeys.MASHUP_TOP_BINDER_ID, bs.getWorkspaceModule().getTopWorkspaceId());
		model.put( WebKeys.MASHUP_BINDER, entity );
    	model.put(WebKeys.MASHUP_BINDERS, mashupBinders);
    	model.put(WebKeys.MASHUP_BINDER_ENTRIES, mashupBinderEntries);
    	model.put(WebKeys.MASHUP_MY_CALENDAR_ENTRIES, mashupMyCalendarEntries);
    	model.put(WebKeys.MASHUP_MY_TASK_ENTRIES, mashupMyTaskEntries);
    	model.put(WebKeys.MASHUP_MY_TASK_BINDERS, mashupMyTaskBinders);
    	model.put(WebKeys.MASHUP_ENTRIES, mashupEntries);
    	model.put(WebKeys.MASHUP_ENTRY_REPLIES, mashupEntryReplies);
    	if ("form".equals(model.get(WebKeys.CONFIG_JSP_STYLE))) {
    		//Force the css style to "light" for displaying the form so we only have one style for viewing the form
    		model.put(WebKeys.MASHUP_CSS, "css/mashup.css");
    	}
	}
	
	/**
	 * 
	 */
	public static LandingPageProperties getLandingPageProperties(
			PortletRequest request,
			DefinableEntity entity )
	{
		HttpServletRequest servletRequest;
		
		servletRequest = WebHelper.getHttpServletRequest( request );
	
		return getLandingPageProperties( servletRequest, entity );
	}
	
	/**
	 * Read the custom attribute named "mashup__properties".  The xml for this looks like the following 
		<landingPageData>
		  <background color="black" imgName="" repeat="repeat"/>
		  <pageLayout hideMasthead="true" hideSidebar="true" hideFooter="true" hideMenu="true" />
		  <header bgColor="green" textColor=""/>
		  <content textColor=""/>
		  <border color="" width=""/>
		</landingPageData>
	 */
	public static LandingPageProperties getLandingPageProperties(
		HttpServletRequest request,
		DefinableEntity entity )
	{
		LandingPageProperties lpProperties = null;
		CustomAttribute attr;
		
		
		attr = entity.getCustomAttribute( "mashup__properties" );
		if ( attr != null )
		{
			Document doc;
			
			if ( attr.getValue() instanceof Document )
			{
				doc = (Document) attr.getValue();
				if ( doc != null )
				{
					Element bgElement;
					Element pgLayoutElement;
					Element headerElement;
					Element contentElement;
					Element borderElement;
					
					lpProperties = new LandingPageProperties();

					// Get the <background ...> element.
					bgElement = (Element) doc.selectSingleNode( "//landingPageData/background" );
					if ( bgElement != null )
					{
						String bgColor;
						String bgImgName;
						
						bgColor = bgElement.attributeValue( "color" );
						lpProperties.setBackgroundColor( bgColor );
						
						bgImgName = bgElement.attributeValue( "imgName" );
						if ( bgImgName != null && bgImgName.length() > 0 )
						{
							String fileUrl;
							String webPath;
							
							lpProperties.setBackgroundImgName( bgImgName );

							webPath = WebUrlUtil.getServletRootURL( request );
	    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, entity, bgImgName );
	    					lpProperties.setBackgroundImgUrl( fileUrl );
						}

						// Get the background image repeat value.
						{
							String repeat;
							
							repeat = bgElement.attributeValue( "repeat" );
							if ( repeat != null )
								lpProperties.setBackgroundRepeat( repeat );
						}
					}
					
					// Get the <pageLayout /> element.
					// The values for "hide masthead", "hide sidebar" and "hide footer"
					// used to be stored as individual custom attributes.  They are now stored as part
					// of the "mashup_properties" custom attribute.
					// We need to read these values from the individual custom attributes if they are
					// not already part of the the "mashup_properties" custom attribute.
					pgLayoutElement = (Element) doc.selectSingleNode( "//landingPageData/pageLayout" );
					if ( pgLayoutElement != null )
					{
						String value;
						Boolean boolValue;
						
						// Get the value of "hide menu"
						{
							boolValue = null;
							
    						value = pgLayoutElement.attributeValue( "hideMenu" );
    						if ( value != null )
    						{
    							boolValue = new Boolean( value );
    							lpProperties.setHideMenu( boolValue );
    						}
						}
						
						// Get the value of "hide masthead"
						{
							boolValue = null;
						
							// Does the "hideMasthead" attribute exist?
							value = pgLayoutElement.attributeValue( "hideMasthead" );
							if ( value == null || value.length() == 0 )
							{
								// No, read it as a custom attribute.
					    		attr = entity.getCustomAttribute( "mashup" + DefinitionModule.MASHUP_HIDE_MASTHEAD );
					    		if ( attr != null )
					    			boolValue = (Boolean)attr.getValue();
							}
							else
								boolValue = new Boolean( value );
							
							if ( boolValue != null )
								lpProperties.setHideMasthead( boolValue );
						}
						
						// Get the value of "hide sidebar"
						{
							boolValue = null;
							
							// Does the "hideSidebar" attribute exist?
							value = pgLayoutElement.attributeValue( "hideSidebar" );
							if ( value == null || value.length() == 0 )
							{
								// No, read it as a custom attribute.
					    		attr = entity.getCustomAttribute( "mashup" + DefinitionModule.MASHUP_HIDE_SIDEBAR );
					    		if ( attr != null )
					    			boolValue = (Boolean)attr.getValue();
							}
							else
								boolValue = new Boolean( value );
							
							if ( boolValue != null )
								lpProperties.setHideSidebar( boolValue );
						}
						
						// Get the value of "hide footer"
						{
							boolValue = null;
							
							// Does the "hideFooter" attribute exist?
							value = pgLayoutElement.attributeValue( "hideFooter" );
							if ( value == null || value.length() == 0 )
							{
								// No, read it as a custom attribute.
					    		attr = entity.getCustomAttribute( "mashup" + DefinitionModule.MASHUP_HIDE_FOOTER );
					    		if ( attr != null )
					    			boolValue = (Boolean)attr.getValue();
							}
							else
								boolValue = new Boolean( value );
							
							if ( boolValue != null )
								lpProperties.setHideFooter( boolValue );
						}
						
						// Get the value of "style"
						{
							String style = "mashup_dark.css";
							
							// Does the "style" attribute exist?
							value = pgLayoutElement.attributeValue( "pageStyle" );
							if ( value == null || value.length() == 0 )
							{
								// No, read it as a custom attribute
					    		attr = entity.getCustomAttribute( "mashup" + DefinitionModule.MASHUP_STYLE );
					    		if ( attr != null && attr.getValueType() == CustomAttribute.STRING )
					    		{
					    			style = (String) attr.getValue();
					    			if ( style == null || style.length() == 0 )
					    				style = "mashup_dark.css";
					    		}
							}
							else
								style= value;
							
							lpProperties.setStyle( style );
						}
					}

					// Get the <header...> element.
					headerElement = (Element) doc.selectSingleNode( "//landingPageData/header" );
					if ( headerElement != null )
					{
						String color;
						
						// Get the header background color
						color = headerElement.attributeValue( "bgColor" );
						if ( color != null && color.length() > 0 )
							lpProperties.setHeaderBgColor( color );
						
						// Get the header text color
						color = headerElement.attributeValue( "textColor" );
						if ( color != null && color.length() > 0 )
							lpProperties.setHeaderTextColor( color );
					}
					
					// Get the <content ...> element
					contentElement = (Element) doc.selectSingleNode( "//landingPageData/content" );
					if ( contentElement != null )
					{
						String color;
						
						// Get the text color
						color = contentElement.attributeValue( "textColor" );
						if ( color != null && color.length() > 0 )
							lpProperties.setContentTextColor( color );
					}
					
					// Get the <border ...> element
					borderElement = (Element) doc.selectSingleNode( "//landingPageData/border" );
					if ( borderElement != null )
					{
						String color;
						String width;
						
						// Get the border color
						color = borderElement.attributeValue( "color" );
						if ( color != null && color.length() > 0 )
							lpProperties.setBorderColor( color );
						
						// Get the border width
						width = borderElement.attributeValue( "width" );
						if ( width != null && width.length() > 0 )
							lpProperties.setBorderWidth( width );
					}
				}
			}
		}

		return lpProperties;
	}
	
    //Routine to perform any translations on the mashup config string
    public static String fixUpMashupConfiguration( String mashupValue, String nameValue, List fileData ) {
    	String[] mashupValues = mashupValue.split(";");
    	for (int i = 0; i < mashupValues.length; i++) {
    		String	attrValue;
    		String[] mashupItemValues = mashupValues[i].split(",");
			Map mashupItemAttributes = new HashMap();
			attrValue = null;
			if (mashupItemValues.length > 0) {
				//Build a map of attributes
				for (int j = 0; j < mashupItemValues.length; j++) {
					int k = mashupItemValues[j].indexOf("=");
					if (k > 0) {
						String a = mashupItemValues[j].substring(0, k);
						String v = mashupItemValues[j].substring(k+1, mashupItemValues[j].length());
						attrValue = v;
						mashupItemAttributes.put(a, v);
					}
				}
			}

			String type = mashupItemValues[0];
    		if (ObjectKeys.MASHUP_TYPE_CUSTOM_JSP.equals(type) && 
    				!mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_PATH_TYPE)) {
    			String pathType = ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_PATH_TYPE_CUSTOM_JSP;
    			//See if the jsp is part of an extension or really a custom_jsp
    			String jspName = "";
    			if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_NAME)) {
    				jspName = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_NAME);
    			}
				if (jspName.contains("./") || jspName.contains(".\\") || jspName.contains("~")) {
					//Illegal value, ignore it
					throw new DefinitionInvalidException("definition.error.invalidJspPath", new Object[] {"\""+jspName+"\""});
				}
    			if (!jspName.equals("")) {
    				String jspName2 = jspName;
    				if (File.separator.equals("\\")) {
    					jspName2 = jspName.replaceAll("[\\\\/]", "\\\\");
    				} else {
    					jspName2 = jspName.replaceAll("[\\\\/]", File.separator);
    				}
    				String fileName = DirPath.getExtensionBasePath() + File.separator + Utils.getZoneKey() + File.separator + jspName2;
    				File f = new File(fileName);
    				if (f.isFile()) pathType = ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_PATH_TYPE_EXTENSION;
    			}
    			mashupValues[i] = mashupValues[i].replaceFirst(",", ",pathType="+pathType+",");

    		} else if (ObjectKeys.MASHUP_TYPE_IFRAME.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_URL)) {
    			//This is a url. It must be checked for xss
    			String url = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_URL);
    			StringCheckUtil.checkUrl(url);
    		}
    		
    		// Is this an html configuration?
    		if ( ObjectKeys.MASHUP_TYPE_HTML.equalsIgnoreCase( type ) )
    		{
    			String html;
    			
    			// Yes
    			// Get the html this html widget uses.
    			html = (String) mashupItemAttributes.get( ObjectKeys.MASHUP_ATTR_DATA );
    			if ( html != null && html.length() > 0 )
    			{
    				Description desc;
    				
    				// ',', '=' and ';' characters have been replaced with "%2c", "%3d" and "%3b".
    				// We need to unescape the html.
   					html = decodeSeparators( html );
    					
    				desc = new Description( html, Description.FORMAT_HTML );
    				
					// Deal with any markup language transformations before storing the html
					MarkupUtil.scanDescriptionForUploadFiles( desc, nameValue, fileData);
					MarkupUtil.scanDescriptionForAttachmentFileUrls( desc );
					MarkupUtil.scanDescriptionForICLinks( desc );
					MarkupUtil.scanDescriptionForYouTubeLinks( desc );
					MarkupUtil.scanDescriptionForExportTitleUrls( desc );

					// Replace ',' with "%2c", '=' with "%3d" and ';' with "%3b"
					html = encodeSeparators( desc.getText() );
					mashupValues[i] = ObjectKeys.MASHUP_TYPE_HTML + ",data=" + html;
    			}
    		}
    	}
    	String result = "";
    	for (int i = 0; i < mashupValues.length; i++) {
    		if (!result.equals("")) result += ";";
    		result += mashupValues[i];
    	}
    	return result;
    }

	/**
	 * Replace all occurrences of "%2c" with ',' and all occurrences of "%3b" with ';'
	 */
	public static String decodeSeparators( String encodedData )
	{
		String results = null;
		
		if ( encodedData != null )
		{
			results = encodedData.replaceAll( "%2c", "," );
			results = results.replaceAll( "%3b", ";" );
			results = results.replaceAll( "%3d", "=" );
		}
		
		return results;
	}
	
	
	/**
	 * Replace all occurrences of ',' with "%2c" and all occurrences of ';' with "%3b" and
	 * all occurrences of '=' with "%3d".
	 */
	public static String encodeSeparators( String configData )
	{
		StringBuffer finalStr;
		int i;
		
		finalStr = new StringBuffer();
		for (i = 0; i < configData.length(); ++i)
		{
			char nextCh;
			
			nextCh = configData.charAt( i );
			if ( nextCh == ',' )
				finalStr.append( "%2c" );
			else if ( nextCh == ';' )
				finalStr.append( "%3b" );
			else if ( nextCh == '=' )
				finalStr.append( "%3d" );
			else
				finalStr.append( nextCh );
		}

		return finalStr.toString();
	}


    public static String fixupMashupCanvasForExport(String mashupValue) {
		ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
		String zoneInfoId = zoneInfo.getId();
		if (zoneInfoId == null) zoneInfoId = "";
    	String[] mashupValues = mashupValue.split(";");
    	for (int i = 0; i < mashupValues.length; i++) {
    		String[] mashupItemValues = mashupValues[i].split(",");
			Map mashupItemAttributes = new HashMap();
			if (mashupItemValues.length > 0) {
				//Build a map of attributes
				for (int j = 0; j < mashupItemValues.length; j++) {
					int k = mashupItemValues[j].indexOf("=");
					if (k > 0) {
						String a = mashupItemValues[j].substring(0, k);
						String v = mashupItemValues[j].substring(k+1, mashupItemValues[j].length());
						mashupItemAttributes.put(a, v);
					}
				}
			}

			String type = mashupItemValues[0];
    		if (ObjectKeys.MASHUP_TYPE_ENTRY.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRY_ID) &&
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID).equals("") &&
    				!mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
    			mashupValues[i] = mashupValues[i].replaceFirst(",", ",zoneUUID="+zoneInfoId+",");
    		} else if (ObjectKeys.MASHUP_TYPE_FOLDER.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_FOLDER_ID) && 
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_FOLDER_ID).equals("") &&
    				!mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
    			mashupValues[i] = mashupValues[i].replaceFirst(",", ",zoneUUID="+zoneInfoId+",");
    		} else if (ObjectKeys.MASHUP_TYPE_BINDER_URL.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_BINDER_ID) && 
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_BINDER_ID).equals("") &&
    				!mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
    			mashupValues[i] = mashupValues[i].replaceFirst(",", ",zoneUUID="+zoneInfoId+",");
    		} else if (ObjectKeys.MASHUP_TYPE_ENTRY_URL.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRY_ID) && 
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID).equals("") &&
    				!mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
    			mashupValues[i] = mashupValues[i].replaceFirst(",", ",zoneUUID="+zoneInfoId+",");
    		} else if (ObjectKeys.MASHUP_TYPE_ENHANCED_VIEW.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_FOLDER_ID) && 
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_FOLDER_ID).equals("") &&
    				!mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
    			mashupValues[i] = mashupValues[i].replaceFirst(",", ",zoneUUID="+zoneInfoId+",");
    		} else if (ObjectKeys.MASHUP_TYPE_ENHANCED_VIEW.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRY_ID) && 
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID).equals("") &&
    				!mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ZONE_UUID)) {
    			mashupValues[i] = mashupValues[i].replaceFirst(",", ",zoneUUID="+zoneInfoId+",");
    		}
    	}
    	String result = "";
    	for (int i = 0; i < mashupValues.length; i++) {
    		if (!result.equals("")) result += ";";
    		result += mashupValues[i];
    	}
    	return result;
	}

    public static String fixupMashupCanvasGraphics(String mashupValue, DefinableEntity entity) {
    	String[] mashupValues = mashupValue.split(";");
    	for (int i = 0; i < mashupValues.length; i++) {
    		String[] mashupItemValues = mashupValues[i].split(",");
			Map mashupItemAttributes = new HashMap();
			if (mashupItemValues.length > 0) {
				//Build a map of attributes
				for (int j = 0; j < mashupItemValues.length; j++) {
					int k = mashupItemValues[j].indexOf("=");
					if (k > 0) {
						String a = mashupItemValues[j].substring(0, k);
						String v = mashupItemValues[j].substring(k+1, mashupItemValues[j].length());
						mashupItemAttributes.put(a, v);
					}
				}
			}

			String type = mashupItemValues[0];
    		if (ObjectKeys.MASHUP_TYPE_GRAPHIC.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_GRAPHIC) && 
    				!mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_TITLE)) {
    			String graphicId = (String) mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_GRAPHIC);
    			SortedSet<FileAttachment> fileAtts = entity.getFileAttachments();
    			for (FileAttachment fileAtt : fileAtts) {
    				if (graphicId.equals(fileAtt.getId())) {
    					mashupValues[i] = mashupValues[i].replaceFirst(",", ",title="+fileAtt.getFileItem().getName()+",");
    				}
    			}
    		}
    	}
    	String result = "";
    	for (int i = 0; i < mashupValues.length; i++) {
    		if (!result.equals("")) result += ";";
    		result += mashupValues[i];
    	}
    	return result;
	}

	public static String fixupMashupCanvasForImport(String mashupValue, 
			Map<Long, Long> binderIdMap, Map<Long, Long> entryIdMap) {
    	String[] mashupValues = mashupValue.split(";");
    	for (int i = 0; i < mashupValues.length; i++) {
    		String[] mashupItemValues = mashupValues[i].split(",");
			Map mashupItemAttributes = new HashMap();
			if (mashupItemValues.length > 0) {
				//Build a map of attributes
				for (int j = 0; j < mashupItemValues.length; j++) {
					int k = mashupItemValues[j].indexOf("=");
					if (k > 0) {
						String a = mashupItemValues[j].substring(0, k);
						String v = mashupItemValues[j].substring(k+1, mashupItemValues[j].length());
						mashupItemAttributes.put(a, v);
					}
				}
			}

			String type = mashupItemValues[0];
    		if (ObjectKeys.MASHUP_TYPE_ENTRY.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRY_ID) &&
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID).equals("")) {
    			String entryId = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID);
    			if (entryIdMap.containsKey(Long.valueOf(entryId))) {
    				String newEntryId = entryIdMap.get(Long.valueOf(entryId)).toString();
    				mashupValues[i] = mashupValues[i].replaceFirst(",entryId="+entryId+",", ",entryId="+newEntryId+",");
    				mashupValues[i] = mashupValues[i].replaceFirst(",zoneUUID=[^,;$]*", "");
    			}
    		} else if (ObjectKeys.MASHUP_TYPE_FOLDER.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_FOLDER_ID) && 
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_FOLDER_ID).equals("")) {
    			String folderId = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_FOLDER_ID);
    			if (binderIdMap.containsKey(Long.valueOf(folderId))) {
    				String newFolderId = binderIdMap.get(Long.valueOf(folderId)).toString();
    				mashupValues[i] = mashupValues[i].replaceFirst(",folderId="+folderId+",", ",folderId="+newFolderId+",");
    				mashupValues[i] = mashupValues[i].replaceFirst(",zoneUUID=[^,;$]*", "");
    			}
    		} else if (ObjectKeys.MASHUP_TYPE_BINDER_URL.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_BINDER_ID) && 
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_BINDER_ID).equals("")) {
    			String binderId = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_BINDER_ID);
    			if (binderIdMap.containsKey(Long.valueOf(binderId))) {
    				String newBinderId = binderIdMap.get(Long.valueOf(binderId)).toString();
    				mashupValues[i] = mashupValues[i].replaceFirst(",binderId="+binderId+",", ",binderId="+newBinderId+",");
    				mashupValues[i] = mashupValues[i].replaceFirst(",zoneUUID=[^,;$]*", "");
    			}
    		} else if (ObjectKeys.MASHUP_TYPE_ENTRY_URL.equals(type) && 
    				mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_ENTRY_ID) && 
    				!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID).equals("")) {
    			String entryId = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_ENTRY_ID);
    			if (entryIdMap.containsKey(Long.valueOf(entryId))) {
    				String newEntryId = entryIdMap.get(Long.valueOf(entryId)).toString();
    				mashupValues[i] = mashupValues[i].replaceFirst(",entryId="+entryId+",", ",entryId="+newEntryId+",");
    				mashupValues[i] = mashupValues[i].replaceFirst(",zoneUUID=[^,;$]*", "");
    			}
    		}
    	}
    	String result = "";
    	for (int i = 0; i < mashupValues.length; i++) {
    		if (!result.equals("")) result += ";";
    		result += mashupValues[i];
    	}
    	return result;
	}

	public static Document getDefinitionTree(AllModulesInjected bs, Long binderId) {
		List currentDefinitions;
		currentDefinitions = bs.getDefinitionModule().getDefinitions(binderId, Boolean.FALSE);
		return getDefinitionTree(bs, binderId, currentDefinitions);
	}
	public static Document getDefinitionTree(AllModulesInjected bs, Long binderId, List currentDefinitions) {
		//Build the definition tree
		Document definitionTree = DocumentHelper.createDocument();
		Element dtRoot = definitionTree.addElement(DomTreeBuilder.NODE_ROOT);
		dtRoot.addAttribute("title", NLT.getDef("__definitions"));
		dtRoot.addAttribute("id", "definitions");
		dtRoot.addAttribute("displayOnly", "true");
		dtRoot.addAttribute("url", "");
		Element root = 	bs.getDefinitionModule().getDefinitionConfig().getRootElement();
		
		Iterator definitions = root.elementIterator("definition");
		Map designers = new TreeMap(new StringComparator(RequestContextHolder.getRequestContext().getUser().getLocale()));
		while (definitions.hasNext()) {
			Element defEle = (Element) definitions.next();
			Element treeEle = DocumentHelper.createElement("child");
			treeEle.addAttribute("type", "definition");
			treeEle.addAttribute("title", NLT.getDef(defEle.attributeValue("caption")));
			treeEle.addAttribute("id", defEle.attributeValue("name"));	
			treeEle.addAttribute("displayOnly", "true");
			treeEle.addAttribute("url", "");
			//Add the current definitions (if any)
			ListIterator li = currentDefinitions.listIterator();
			while (li.hasNext()) {
				Definition curDef = (Definition)li.next();
				Document curDefDoc = curDef.getDefinition();
				if (curDefDoc == null) continue;
				if (curDef.getType() == Integer.valueOf(defEle.attributeValue("definitionType", "0")).intValue()) {
					Element curDefEle = treeEle.addElement("child");
					curDefEle.addAttribute("type", defEle.attributeValue("name"));
					String title = NLT.getDef(curDef.getTitle());
					if (Validator.isNull(title)) title = curDef.getName();
					title += "  (" + curDef.getName() + ")";
					if (Definition.VISIBILITY_DEPRECATED.equals(curDef.getVisibility())) {
						curDefEle.addAttribute("image", "/pics/delete.gif");
					} 
					curDefEle.addAttribute("title", title);
					
					curDefEle.addAttribute("id", curDef.getId());
					curDefEle.addAttribute("url", "");
				}
			}
			if (treeEle.hasContent()) designers.put(treeEle.attributeValue("title"), treeEle);
		}
		//	add sorted elements
		for (Iterator iter=designers.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry me = (Map.Entry)iter.next();
				dtRoot.add((Element)me.getValue());
		}
		
		return definitionTree;

	}

	public static List getDefaultDefinitions(AllModulesInjected bs) {
		List definitions = new ArrayList();
		Workspace top = (Workspace)bs.getWorkspaceModule().getTopWorkspace();
		
		//default definitions stored in separate config file
		String startupConfig = SZoneConfig.getString(top.getName(), "property[@name='startupConfig']", "config/startup.xml");
		SAXReader reader = XmlUtil.getSAXReader(false);  
		InputStream in=null;
		try {
			in = new ClassPathResource(startupConfig).getInputStream();
			Document cfg = reader.read(in);
			in.close();
			List<Element> elements = cfg.getRootElement().selectNodes("definitionFile");
			for (Element element:elements) {
				String file = element.getTextTrim();
				//Get the definition name from the file name
				Pattern nameP = Pattern.compile("/([^/\\.]*)\\.xml$");
				Matcher m = nameP.matcher(file);
				if (m.find()) {
					String name = m.group(1);
					if (name != null && !name.equals("")) {
						Definition def = null;
						try {
							def = bs.getDefinitionModule().getDefinitionByName(null, false, name);
						} catch(NoDefinitionByTheIdException e) {
							logger.debug("DefinitionHelper.getDefaultDefinitions(NoDefinitionByTheIdException):  '" + ((null == name) ? "<null>" : name) + "':  Ignored");
						}
						if (def != null) definitions.add(def);
					}
				}
			}

		} catch (Exception ex) {
			logger.debug("DefinitionHelper.getDefaultDefinitions(Exception:  '" + MiscUtil.exToString(ex) + "'):  Cannot read startup configuration:  Ignored");
		}
		return definitions;
	}	

	public static Element addPrincipalToDocument(Branch doc, Principal entry) {
		Element entryElem = doc.addElement("value");

		// Handle structured fields of the entry known at compile time.
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("name", entry.getName());
		entryElem.addAttribute("title", Utils.getUserTitle(entry));
		entryElem.addAttribute("emailAddress", entry.getEmailAddress());
		entryElem.setText(entry.getName());

		return entryElem;
	}

	public static Set<String> getTextualInputDataKeys(String definitionId, InputDataAccessor inputData) {
		Definition definition = null;
		if(definitionId != null)
			definition = getDefinition(definitionId);
    	Document definitionTree = null;
    	if(definition != null)
    		definitionTree = definition.getDefinition();
    	String[] dataTypes = SPropsUtil.getStringArray("definition.textual.datatype.names", ",");
    	return getInstance().getDefinitionModule().filterInputDataKeysByDataType(definitionTree, inputData, Arrays.asList(dataTypes));
	}

	public static Set<String> getTextualInputDataKeys(Long folderId, Long entryId,InputDataAccessor inputData) {
		FolderModule fm = (FolderModule) SpringContextUtil.getBean("folderModule");
		FolderEntry entry = fm.getEntry(folderId, entryId);
		return getTextualInputDataKeys(entry.getEntryDefId(), inputData);
	}
	
	public static Set<String> getTextualInputDataKeys(Long binderId,InputDataAccessor inputData) {
		BinderModule bm = (BinderModule) SpringContextUtil.getBean("binderModule");
		Binder binder = bm.getBinder(binderId);
		return getTextualInputDataKeys(binder.getEntryDef().getId(), inputData);
	}
	
	public static String GetCustomAttrAsString(CustomAttribute attr) {
    	try {
	    	//Get the definition being used
	    	DefinableEntity entity = attr.getOwner().getEntity();
	    	Document defDoc = entity.getEntryDefDoc();
	    	Element attrDefEle = DefinitionHelper.findAttribute(attr.getName(), defDoc);
	    	String attrType = attrDefEle.attributeValue("name");
		    if (attr.getValueType() == CustomAttribute.SET || attr.getValueType() == CustomAttribute.ORDEREDSET) {
		    	StringBuffer result = new StringBuffer();
		    	boolean firstItem = true;
		    	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    		for (Iterator iter=attr.getValueSet().iterator(); iter.hasNext();) {
	    			if (!firstItem) {
	    				result.append(", ");
	    			}
	    			firstItem = false;
		    		Object value = iter.next();
		    		if (value instanceof String) {
		    			result.append(getAttrString(attrDefEle, attrType, (String) value));
		    			
		    		} else if (value instanceof Boolean) {
		    			result.append(String.valueOf((Boolean)value));
		    		} else if (value instanceof Long) {
		    			result.append(String.valueOf((Long)value));
		    		} else if (value instanceof Date) {
		    			result.append(dateFormatter.format((Date)value));
		    		} else if (value instanceof Event) {
		    			result.append(((Event)value).toString().replaceAll("'", ";"));
		    		}
	      	    }
	    		return result.toString();
	 	    } else {
		    	switch(attr.getValueType()) {
	       			case CustomAttribute.STRING:
	       				String textStr = "";
	       				if (attr.getValue() instanceof String && !Validator.isNull((String)attr.getValue())) {
	       					textStr = (String)attr.getValue();
	       				} else if (attr.getValue() instanceof Description && !Validator.isNull(((Description)attr.getValue()).getText())) {
	       					textStr = ((Description)attr.getValue()).getText();
	       				}
	       				return getAttrString(attrDefEle, attrType, textStr);
	       			case CustomAttribute.DESCRIPTION:
	       				if (!Validator.isNull(((Description)attr.getValue()).getText())) {
	       					return((Description)attr.getValue()).getText();
	       				}
	       			case CustomAttribute.COMMASEPARATEDSTRING:
	       				CommaSeparatedValue textCSV = (CommaSeparatedValue)attr.getValue();
	       				return getAttrString(attrDefEle, attrType, textCSV.getValueString());
	       			case CustomAttribute.BOOLEAN:		
	       				return ((Boolean)attr.getValue()).toString();    	
	       			case CustomAttribute.LONG:
	       				return ((Long)attr.getValue()).toString();  
	       			case CustomAttribute.DATE:
	       				return ((Date)attr.getValue()).toString();  
	       			case CustomAttribute.SERIALIZED:
	       				SSBlobSerializable serializedValue = new SSBlobSerializable(attr.getValue());
	       				return serializedValue.toBase64String();
	        		case CustomAttribute.XML:
	        			SSClobString xmlValue = new SSClobString(XmlFileUtil.writeString((Document)attr.getValue(), OutputFormat.createPrettyPrint()));
	        			return xmlValue.getText(); 
	       			case CustomAttribute.EVENT:
	       				Event e = (Event)attr.getValue();
	       				return e.toCsvString();
	       			case CustomAttribute.SURVEY:
	       				if (attr.getValue() instanceof String && !Validator.isNull((String)attr.getValue())) {
	       					return (String)attr.getValue();
	       				} else if (attr.getValue() instanceof Description && !Validator.isNull(((Description)attr.getValue()).getText())) {
	       					return ((Description)attr.getValue()).getText();
	       				}
	       				break;     				
	       			case CustomAttribute.ATTACHMENT:
	       				//attachments aren't handled
	      				break;
	      			default:
	      				break;	
	      	    }
	    	}
    	} catch(Exception e) {}
	    return "";
	}
	
	public static String getAttrString(Element attrDefEle, String attrType, String text) {
		try {
			Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			if (!text.equals("")) {
	    		switch (attrType) {
					case "user_list":
					case "userListSelectbox":
					case "external_user_list":
					case "group_list":
						String[] ids = text.trim().split(",");
						StringBuffer buf = new StringBuffer();
						for (int i = 0; i < ids.length; i++) {
							Long id = Long.valueOf(ids[i]);
							ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
							Principal p = profileDao.loadPrincipal(id, zoneId, true);
							if (buf.length() > 0) {
								buf.append(", ");
							}
							buf.append(p.getName());
						}
						return buf.toString();
						
					case "selectbox":
						Map<String,String> selections = findSelectboxSelectionsAsMap(attrDefEle);
						String s1 = text;
						if (selections.containsKey(text)) {
							s1 = selections.get(text);
						}
						return NLT.get(s1);
						
					case "radio":
						Map<String,String> radioButtons = findRadioSelectionsAsMap(attrDefEle);
						String s2 = text;
						if (radioButtons.containsKey(text)) {
							s2 = radioButtons.get(text);
						}
						return NLT.get(s2);
						
	    			default:
	    				return text;
	    		}
			}
		} catch(Exception e) {}
		return text;
	}

	public static Map<String, String> getDefinitionProperties(Element itemDefinition) {
		List<Element> propNodes = itemDefinition.selectNodes("properties/property");
		Map<String, String> props = new HashMap<String, String>();
		for (Element property : propNodes) {
			String propertyName = property.attributeValue("name", "");
			if (!Validator.isNull(propertyName)) {
				props.put(propertyName, property.attributeValue("value", ""));
			}
		}
		return props;
	}
}
