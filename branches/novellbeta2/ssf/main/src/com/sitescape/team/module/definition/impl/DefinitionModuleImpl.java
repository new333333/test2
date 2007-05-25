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
package com.sitescape.team.module.definition.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CommaSeparatedValue;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinitionInvalidException;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.EventHelper;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;

/**
 * @author hurley
 *
 */
public class DefinitionModuleImpl extends CommonDependencyInjection implements DefinitionModule, InitializingBean  {
	private Document definitionConfig;
	private Element configRoot;
	private DefinitionConfigurationBuilder definitionBuilderConfig;
	private static final String[] defaultDefAttrs = new String[]{"internalId", "zoneId", "type"};
	private String release;
	
	private WorkflowModule workflowModule;
	
	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	protected WorkflowModule getWorkflowModule() {
		return workflowModule;
	}
    public void afterPropertiesSet() {
		this.definitionConfig = definitionBuilderConfig.getAsMergedDom4jDocument();
		this.configRoot = this.definitionConfig.getRootElement();
		this.release = SPropsUtil.getString("release.type", "");

    }
    /*
     *  (non-Javadoc)
 	 * Use method names as operation so we can keep the logic out of application
     * @see com.sitescape.team.module.definition.DefinitionModule#testAccess(java.lang.String)
     */
   	public boolean testAccess(int type, String operation) {
   		if (type == Definition.WORKFLOW && release.equals("open")) return false;
   		try {
   			checkAccess(type, operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		} catch (NotSupportedException ac) {
   			return false;
   		}
   		
   	}
   	protected void checkAccess(int type, String operation) throws AccessControlException {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
   		if (type == Definition.FOLDER_ENTRY) {
   			if (getAccessControlManager().testOperation(top, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS)) return;
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   		} else if (type == Definition.WORKFLOW) {
   			if (release.equals("open")) throw new NotSupportedException();
   			if (getAccessControlManager().testOperation(top, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS)) return;
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);  			
   		} else {
			accessControlManager.checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		}
   	}
    public String addDefinition(Document doc, boolean replace) {
    	Element root = doc.getRootElement();
		String type = root.attributeValue("type");
    	checkAccess(Integer.valueOf(type), "addDefinition");
    	return doAddDefinition(doc, replace).getId();
    }
    protected Definition doAddDefinition(Document doc, boolean replace) {
    	Element root = doc.getRootElement();
		String name = root.attributeValue("name");
		String caption = root.attributeValue("caption");
		String type = root.attributeValue("type");
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		String id = root.attributeValue("databaseId", "");
		String internalId = root.attributeValue("internalId", null);
		Definition def=null;
		if (!Validator.isNull(internalId)) {
			//make sure doesn't exist
			try {
				def = getCoreDao().loadReservedDefinition(internalId, zoneId);
				if (replace) {
					def.setName(name);
					def.setTitle(caption);
					def.setType(Integer.parseInt(type));
					def.setInternalId(internalId);	
					setDefinition(def, doc);
					return def;
				}
				//alread exists
				throw new DefinitionInvalidException("definition.error.internalAlreadyExists", new Object[]{internalId});
			} catch (NoDefinitionByTheIdException nd) {}
			
		}
		if (Validator.isNull(id)) {
			//doesn't have an id, so generate one
			def = new Definition();
			def.setZoneId(zoneId);
			def.setName(name);
			def.setTitle(caption);
			def.setType(Integer.parseInt(type));
			def.setInternalId(internalId);
			getCoreDao().save(def);
			root.addAttribute("databaseId", def.getId());
			setDefinition(def,doc);
			id = def.getId();
		} else {
			//import - try reusing existing guid
			// see if already exist in any zone
			List oldDefs = getCoreDao().loadObjects(Definition.class, new FilterControls("id", id));
			if (oldDefs.isEmpty()) {
				def = new Definition();
				def.setId(id);
				def.setZoneId(zoneId);
				def.setName(name);
				def.setTitle(caption);
				def.setType(Integer.parseInt(type));
				def.setInternalId(internalId);	
				root.addAttribute("databaseId", def.getId());
				setDefinition(def,doc);
				getCoreDao().replicate(def);				
			} else {
				if (!replace) throw new DefinitionInvalidException("definition.error.alreadyExists", new Object[]{id});

				//see if matches zone
				boolean found=false;
				for (int i=0; i<oldDefs.size(); ++i) {
					Definition oldDef = (Definition)oldDefs.get(i);
					if (oldDef.getZoneId().equals(zoneId)) {
						found=true;
						//update it
						oldDef.setName(name);
						oldDef.setTitle(caption);
						oldDef.setType(Integer.parseInt(type));
						oldDef.setInternalId(internalId);	
						setDefinition(oldDef, doc);
						def = oldDef;
						break;
					}
				}
				if (!found) {
					//generate a new one
					def = new Definition();
					def.setZoneId(zoneId);
					def.setName(name);
					def.setTitle(caption);
					def.setType(Integer.parseInt(type));
					def.setInternalId(internalId);	
					getCoreDao().save(def);
					root.addAttribute("databaseId", def.getId());
					setDefinition(def,doc);
					id = def.getId();						
				}
			}			
		}
		return def;
	}
	public Definition getDefinition(String id) {
		// Controllers need access to definitions.  Allow world read        
 		return coreDao.loadDefinition(id, RequestContextHolder.getRequestContext().getZoneId());
	}
	
	protected DefinitionConfigurationBuilder getDefinitionBuilderConfig() {
        return definitionBuilderConfig;
    }
    public void setDefinitionBuilderConfig(DefinitionConfigurationBuilder definitionBuilderConfig) {
        this.definitionBuilderConfig = definitionBuilderConfig;
    }
    
	public Definition addDefinition(String name, String title, int type, InputDataAccessor inputData) {
    	checkAccess(type, "addDefinition");

		Definition newDefinition = new Definition();
		newDefinition.setName(name);
		newDefinition.setTitle(title);
		newDefinition.setType(type);
		newDefinition.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		getCoreDao().save(newDefinition);
		Document doc = getInitialDefinition(name, title, type, inputData);
		Element root = doc.getRootElement();
		root.addAttribute("databaseId", newDefinition.getId());
		setDefinition(newDefinition, doc);
		return newDefinition;
	}
	
	public void modifyDefinitionName(String id, String name, String title) {
		Definition def = getDefinition(id);
    	checkAccess(def.getType(), "modifyDefinitionName");
		if (def != null) {
			
			//Store the name and title (If they are not blank) in the definition object and in the definition document
			boolean defChanged = false;
			Document defDoc = def.getDefinition();
			if (!name.equals("") && !defDoc.getRootElement().attributeValue("name", "").equals(name)) {
				defDoc.getRootElement().addAttribute("name", name);
				defChanged = true;
			}
			if (!title.equals("") && !defDoc.getRootElement().attributeValue("caption", "").equals(title)) {
				defDoc.getRootElement().addAttribute("caption", title);
				defChanged = true;
			}
			
			//set definition name after we get definition, so definition doc file will be found before the name is changed
			if (!name.equals("") && !def.getName().equals(name)) {
				def.setName(name);
				defChanged = true;
			}
			if (!title.equals("") && !def.getName().equals(title)) {
				def.setTitle(title);
				defChanged = true;
			}
			
			//Write out the changed definition (if it actually changed)
			if (defChanged) setDefinition(def, defDoc);

			//When any change is made, validate the the definition is at the same level as the configuration file
			validateDefinitionAttributes(def);
}
	}
	
	public void modifyDefinitionAttribute(String id, String key, String value) {
		Definition def = getDefinition(id);
	   	checkAccess(def.getType(), "modifyDefinitionAttribute");
		//Store this attribute in the definition document (but only if it is different)
		Document defDoc = def.getDefinition();
		Element docRoot = defDoc.getRootElement();
		if (value.equals("") || !docRoot.attributeValue(key, "").equals(value)) {
			defDoc.getRootElement().addAttribute(key, value);
			setDefinition(def, defDoc);
		}
		//When any change is made, validate the the definition is at the same level as the configuration file
		validateDefinitionAttributes(def);
		validateDefinitionTree(def);
	}
	
    protected void setDefinition(Definition def, Document doc) {
    	//Write out the new definition file
    	def.setDefinition(doc);
    	
    	//If this is a workflow definition, build the corresponding JBPM workflow definition
    	if (def.getType() == Definition.WORKFLOW) {
    		//Use the definition id as the workflow process name
    		getWorkflowModule().modifyProcessDefinition(def.getId(), def);
    	}
   }

	
	public void modifyDefinitionProperties(String id, InputDataAccessor inputData) {
		Definition def = getDefinition(id);
	   	checkAccess(def.getType(), "modifyDefinitionProperties");
		if (def != null) {			
			String definitionName = "";
			if (inputData.exists("propertyId_name")) {
				definitionName = inputData.getSingleValue("propertyId_name");
			}
			if (definitionName.equals("")) definitionName = def.getName();
			String definitionCaption = "";
			if (inputData.exists("propertyId_caption")) {
				definitionCaption = inputData.getSingleValue("propertyId_caption");
			}
			if (definitionCaption.equals("")) definitionCaption = def.getTitle();
			modifyDefinitionName(id, definitionName, definitionCaption);
			
			//Store the properties in the definition document
			Document defDoc = def.getDefinition();
			
			String type = String.valueOf(def.getType());
			Element definition = (Element) this.configRoot.selectSingleNode("item[@definitionType='"+type+"']");
			if (definition != null) {
				//Add the properties
				processProperties(def.getId(), definition, defDoc.getRootElement(), inputData);
				//Make sure the definition name and caption remain consistent
				Element newPropertiesEle = (Element)defDoc.getRootElement().selectSingleNode("./properties/property[@name='name']");
				if (newPropertiesEle != null) newPropertiesEle.addAttribute("value", def.getName());
				newPropertiesEle = (Element)defDoc.getRootElement().selectSingleNode("./properties/property[@name='caption']");
				if (newPropertiesEle != null) newPropertiesEle.addAttribute("value", def.getTitle());
			}
			setDefinition(def, defDoc);
			
			//When any change is made, validate the the definition is at the same level as the configuration file
			validateDefinitionAttributes(def);
			validateDefinitionTree(def);

		}
	}
	
	//Rouitine to make sure a definition has all of the proper attributes as defined in the config file
	//  This is useful to propagate new attributes added to the config definition xml file
	private void validateDefinitionAttributes(Definition def) {
		Document defDoc = def.getDefinition();
		if (updateDefinitionAttributes(defDoc)) setDefinition(def, defDoc);
	}
	private boolean updateDefinitionAttributes(Document defDoc) {
		boolean defChanged = false;
		Element defRoot = defDoc.getRootElement();
		
		//Look at all of the items to see if any of their attributes are missing
		Iterator itDefItems = defRoot.elementIterator("item");
		while (itDefItems.hasNext()) {
			Element defItem = (Element) itDefItems.next();
			//Find the matching element in the configuration xml file
			Element configItem = (Element) this.configRoot.selectSingleNode("item[@name='"+defItem.attributeValue("name", "")+"']");
			if (configItem != null) {
				//Check to see if there are new attributes from the config file that should be copied into the definition
				Iterator itConfigItemAttributes = configItem.attributeIterator();
				while (itConfigItemAttributes.hasNext()) {
					Attribute attr = (Attribute) itConfigItemAttributes.next();
					//If the attribute does not exist in the definition item, copy it from the config file
					if (defItem.attributeValue(attr.getName()) == null)
					{
						// (rsordillo) Do not add non-required Attributes to new item
						if (attr.getName().equals("canBeDeleted") ||
								attr.getName().equals("category") ||
								attr.getName().equals("multipleAllowed"))
							continue;
						defItem.addAttribute(attr.getName(), attr.getValue());
						defChanged = true;
					}
				}
			}
		}
		return defChanged;
	}
	
	public void setDefinitionLayout(String id, InputDataAccessor inputData) {
		Definition def = getDefinition(id);
		checkAccess(def.getType(), "setDefinitionLayout");
		Document defDoc = def.getDefinition();
		
		if (inputData.exists("xmlData") && def != null) {
			Document appletDef;
			try {
				appletDef = DocumentHelper.parseText(inputData.getSingleValue("xmlData"));
			} catch(Exception e) {
				return;
			}
			if (appletDef == null) return;
			
	    	//Iterate through the current definition looking for states
			List states = defDoc.getRootElement().selectNodes("//item[@name='state']");
	    	if (states == null) return;
	    	
	    	Iterator itStates = states.iterator();
	        while (itStates.hasNext()) {
	            Element state = (Element) itStates.next();
	            Element stateName = (Element) state.selectSingleNode("properties/property[@name='name']");
	            if (stateName != null) {
	            	String name = stateName.attributeValue("value", "");
	            	if (!name.equals("")) {
			            Element appletState = (Element) appletDef.getRootElement().selectSingleNode("//item[@name='state']/properties/property[@name='name' and @value='"+name+"']");
			            if (appletState != null) {
			            	String x = appletState.getParent().getParent().attributeValue("x", "");
			            	String y = appletState.getParent().getParent().attributeValue("y", "");
			            	if (!x.equals("") && !y.equals("")) {
			            		state.addAttribute("x", x);
			            		state.addAttribute("y", y);
			            	}
			            }
	            	}
	            }
	        }
			setDefinition(def, defDoc);
		}
	}
	
	public void deleteDefinition(String id) {
		Definition def = getDefinition(id);
		checkAccess(def.getType(), "deleteDefinition");
		getCoreDao().delete(def);
		if (def.getType() == Definition.WORKFLOW) {
			//jbpm defs are named with the string id of the ss definitions
			getWorkflowModule().deleteProcessDefinition(def.getId());
		} 
	}
	/**
	 * Load the default definition for a definition type.  If it doesn't exist, create it
	 * @param type
	 * @return
	 */
	public Definition addDefaultDefinition(int type) {
		// no access needed, just fills indefaults
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		String definitionTitle=null;
		String internalId=null;
		String definitionName=null;
		switch (type) {
			case Definition.FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class, 
							new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FOLDER_DEF, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_folder";
				definitionName="_discussionFolder";
				internalId = ObjectKeys.DEFAULT_FOLDER_DEF;
				break; 
			}
			case Definition.FOLDER_ENTRY: {
				List result = getCoreDao().loadObjects(Definition.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FOLDER_ENTRY_DEF, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_folder_entry";
				internalId = ObjectKeys.DEFAULT_FOLDER_ENTRY_DEF;
				definitionName="_discussionEntry";
				break;
			}
			case Definition.WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_WORKSPACE_DEF, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_workspace";
				internalId = ObjectKeys.DEFAULT_WORKSPACE_DEF;
				definitionName="_workspace";
				break;				
			}
			
			case Definition.USER_WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_USER_WORKSPACE_DEF, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_user_workspace";
				internalId = ObjectKeys.DEFAULT_USER_WORKSPACE_DEF;
				definitionName="_userWorkspace";
				break;				
			}
			
			case Definition.PROFILE_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_PROFILES_DEF, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (Definition)result.get(0);
				internalId = ObjectKeys.DEFAULT_PROFILES_DEF;
				definitionTitle = "__definition_default_profiles";
				definitionName="_profiles";
				break;				
			}
			case Definition.PROFILE_ENTRY_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_USER_DEF, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (Definition)result.get(0);
				internalId = ObjectKeys.DEFAULT_USER_DEF;
				definitionTitle = "__definition_default_user";
				definitionName="_user";
				break;				
			}
			case Definition.PROFILE_GROUP_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_GROUP_DEF, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (Definition)result.get(0);
				internalId = ObjectKeys.DEFAULT_GROUP_DEF;
				definitionTitle = "__definition_default_group";
				definitionName="_group";
				break;				
			}
		}
		Document doc = getInitialDefinition(definitionName, definitionTitle, type, new MapInputData(new HashMap()));
		doc.getRootElement().addAttribute("internalId", internalId);
		return doAddDefinition(doc, true);
	}
	
/*
	private Definition loadDef(String key, String internalId, String zoneName) {
		try {
	        Resource resource =  new ClassPathResource("config"  + File.separator +  key);
			InputStream fIn = resource.getInputStream();
	        SAXReader xIn = new SAXReader();
			Document doc = xIn.read(fIn);   
			fIn.close();
			String id = addDefinition(doc);
			Definition def = getCoreDao().loadDefinition(id, zoneName);
			def.setInternalId(internalId);
			return def;
		} catch (Exception ex) {
			logger.error("Error creating default definition:" + ex.getLocalizedMessage());
			return null;
		}

	}
*/
	
	protected Document getInitialDefinition(String name, String title, int type, InputDataAccessor inputData) {
		Element definition = (Element) this.configRoot.selectSingleNode("item[@definitionType='"+type+"']");
		if (definition == null) {return null;}
		
		//We found the definition. Now build the default definition
		Document newTree = DocumentHelper.createDocument();
		Element ntRoot = newTree.addElement("definition");
		ntRoot.addAttribute("name", name);
		ntRoot.addAttribute("caption", title);
		ntRoot.addAttribute("type", String.valueOf(type));
		int id = 1;
		id = populateNewDefinitionTree(definition, ntRoot, this.configRoot, id, true);
		ntRoot.addAttribute("nextId", Integer.toString(id));
		
		//Add the properties
		processProperties("0", definition, ntRoot, inputData);

		//Copy any additional attributes from the configuration file
		updateDefinitionAttributes(newTree);
		
		return newTree;
	}
	public Definition setDefaultBinderDefinition(Binder binder) {
		//no access - fixing up stuff
		//Create an empty binder definition
		int definitionType;
		if (binder.getEntityType().equals(EntityType.workspace)) {			
			if ((binder.getDefinitionType() != null) &&
					(binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW)) {
				definitionType = Definition.USER_WORKSPACE_VIEW;
			} else {
				definitionType = Definition.WORKSPACE_VIEW;
			}
		} else if (binder.getEntityType().equals(EntityType.profiles)) {
			definitionType = Definition.PROFILE_VIEW;
		} else {
				definitionType = Definition.FOLDER_VIEW;
		}
		Definition def = addDefaultDefinition(definitionType);
		binder.setEntryDef(def);
		binder.setDefinitionType(definitionType);
		if (Validator.isNull(binder.getIconName())) {
			String icon = DefinitionUtils.getPropertyValue(def.getDefinition().getRootElement(), "icon");
			if (Validator.isNotNull(icon)) binder.setIconName(icon);
		}
		return def;
	}
	public Definition setDefaultEntryDefinition(Entry entry) {
		//no access - fixing up stuff
		//Create an empty entry definition
		int definitionType;
		if (entry instanceof Principal) {
			definitionType = Definition.PROFILE_ENTRY_VIEW;
		} else {
			definitionType = Definition.FOLDER_ENTRY;
		}
		Definition def = addDefaultDefinition(definitionType);
		entry.setEntryDef(def);
		entry.setDefinitionType(definitionType);
		if (Validator.isNull(entry.getIconName())) {
			String icon = DefinitionUtils.getPropertyValue(def.getDefinition().getRootElement(), "icon");
			if (Validator.isNotNull(icon)) entry.setIconName(icon);
		}
		return def;
	}

	/**
	 * Adds an item to an item in a definition tree.
	 *
	 * @param This call takes 4 parameters: def, itemId, itemNameToAdd, formData
	 *        def - contains the definition that is being modified
	 *        itemId - the id of the item being added to
	 *        itemNameToAdd - the name of the item to be added
	 *        formData - a Map of the values to e set in the newly added item
	 *                   The Map should contain each property value indexed by the 
	 *                     property name prefixed by "propertyId_".
	 * 
	 * @return the next element in the iteration.
	 * @exception NoSuchElementException iteration has no more elements.
	 */
	public Element addItem(String defId, String itemId, String itemNameToAdd, InputDataAccessor inputData) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
		checkAccess(def.getType(), "addItem");
		
		Document definitionTree = def.getDefinition();
			
		Element newItem = addItemToDefinitionDocument(def.getId(), definitionTree, itemId, itemNameToAdd, inputData);
		if (newItem != null) {
			//Save the updated document
			setDefinition(def, definitionTree);
			//definitionTree.asXML();
		}
		return newItem;
	}
	
	protected Element addItemToDefinitionDocument(String defId, Document definitionTree, String itemId, String itemNameToAdd, InputDataAccessor inputData) throws DefinitionInvalidException {
	
		Element newItem = null;
		
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			Map uniqueNames = getUniqueNameMap(this.configRoot, root, itemNameToAdd);
			if (inputData.exists("propertyId_name")) {
				String name = inputData.getSingleValue("propertyId_name");
				if (uniqueNames.containsKey(name)) {
					//This name is not unique
					throw new DefinitionInvalidException("definition.error.nameNotUnique", new Object[] {defId, name});
				}
			}

			//Find the element to add to
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				//Find the requested new item in the configuration document
				Element itemEleToAdd = (Element) this.configRoot.selectSingleNode("item[@name='"+itemNameToAdd+"']");
				if (itemEleToAdd != null) {
					//Add the item 
					newItem = item.addElement("item");
					//Copy the attributes from the config item to the new item
					Iterator attrs = itemEleToAdd.attributeIterator();
					while (attrs.hasNext()) {
						Attribute attr = (Attribute) attrs.next();
						
						// (rsordillo) Do not add non-required Attributes to new item
						if (attr.getName().equals("canBeDeleted")
						|| attr.getName().equals("multipleAllowed"))
							continue;
						newItem.addAttribute(attr.getName(), attr.getValue());
					}
					
					//Get the next id number from the root
					int nextId = Integer.valueOf(root.attributeValue("nextId")).intValue();
					newItem.addAttribute("id", (String) Integer.toString(nextId));
					root.addAttribute("nextId", (String) Integer.toString(++nextId));
					
					//Process the properties (if any)
					processProperties(defId, itemEleToAdd, newItem, inputData);
					
					//Copy the jsps (if any)
					//(rsordillo) Don't copy JSP tags
/*
					Element configJsps = itemEleToAdd.element("jsps");
					if (configJsps != null) {
						Element newJspsEle = configJsps.createCopy();
						newItem.add(newJspsEle);
					}
*/										
					//See if this is a "dataView" type
					if (newItem.attributeValue("type", "").equals("dataView")) {
						checkDataView(root, newItem);
					}
					int nextItemId = Integer.valueOf(root.attributeValue("nextId")).intValue();;
					nextItemId = populateNewDefinitionTree(itemEleToAdd, newItem, this.configRoot, nextItemId, false);
					root.addAttribute("nextId", Integer.toString(nextItemId));
				}
			}
		}
		return newItem;
	}
	
	private void processProperties(String defId, Element configEle, Element newItem, InputDataAccessor inputData) {
		//Check to see if there are new attributes from the config file that should be copied into the definition
		Iterator itAttributes = configEle.attributeIterator();
		while (itAttributes.hasNext()) {
			Attribute attr = (Attribute) itAttributes.next();
			//If the attribute does not exist in the new item, copy it from the config file
			if (newItem.attributeValue(attr.getName()) == null) 
			{
				// (rsordillo) Do not add non-required Attributes to new item
				if (attr.getName().equals("canBeDeleted")
				|| attr.getName().equals("multipleAllowed"))
					continue;
				newItem.addAttribute(attr.getName(), attr.getValue());
			}
		}
		
		//Copy the properties from the definition
		Element configProperties = configEle.element("properties");
		if (configProperties != null) {
			//Remove the previous list of properties
			Iterator itProperties = newItem.selectNodes("properties").iterator();
			while (itProperties.hasNext()) {
				newItem.remove((Element) itProperties.next());
			}
			//Add a fresh "properties" element
			Element newPropertiesEle = newItem.addElement("properties");
		
			//Set the values of each property from the form data
			Iterator itConfigProperties = configProperties.elementIterator("property");
			while (itConfigProperties.hasNext()) {
				Element configProperty = (Element) itConfigProperties.next();
				String attrName = configProperty.attributeValue("name");
				String type = configProperty.attributeValue("type", "");
				String characterMask = configProperty.attributeValue("characterMask", "");
				if (inputData.exists("propertyId_"+attrName)) {
					String[] values = (String[]) inputData.getValues("propertyId_"+attrName);
					for (int i = 0; i < values.length; i++) { 
						String value = values[i];
						if (!characterMask.equals("")) {
							//See if the user entered a valid name
							if (!value.equals("") && !value.matches(characterMask)) {
								//The value is not well formed, go complain to the user
								throw new DefinitionInvalidException("definition.error.invalidCharacter", new Object[] {defId, value});
							}
						}
						
						Element newPropertyEle = newPropertiesEle.addElement("property");
						//just copy name and value
						newPropertyEle.addAttribute("name", attrName);
						if (type.equals("text")) {
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("textarea")) {
							newPropertyEle.setText(value);
						} else if (type.equals("integer")) {
							if (value.matches("[^0-9]+?")) {
								//The value is not a valid integer
								throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {defId, configProperty.attributeValue("caption")});
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("selectbox") || type.equals("itemSelect") || 
								type.equals("radio") || type.equals("replyStyle") || 
								type.equals("iconList") || type.equals("repositoryList")) {
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("boolean") || type.equals("checkbox")) {
							if (value == null) {value = "false";}
							if (value.equalsIgnoreCase("on")) {
								value = "true";
							} else {
								value = "false";
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("userGroupSelect")) {
							String [] v= StringUtil.split(value);
							for (int vals=0; vals < v.length; ++vals) {
								if (v[vals].matches("[^0-9]+?")) {
									//The value is not a valid integer
									throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {defId, configProperty.attributeValue("caption")});
								}
							}
							newPropertyEle.addAttribute("value", value);
						}
							
					}
				} else if (type.equals("workflowCondition")) {
					//Workflow conditions typically have 4 bits of data to capture: 
					//  the definition id, the element name, the operation, and the operand value
					if (inputData.exists("conditionDefinitionId") && 
							inputData.exists("conditionElementName") &&
							inputData.exists("conditionElementOperation")) {
						Element newPropertyEle = configProperty.createCopy();
						newPropertiesEle.add(newPropertyEle);
						String conditionDefinitionId = inputData.getSingleValue("conditionDefinitionId");
						String conditionElementName = inputData.getSingleValue("conditionElementName");
						String conditionElementOperation = inputData.getSingleValue("conditionElementOperation");
						Element workflowCondition = newPropertyEle.addElement("workflowCondition");
						workflowCondition.addAttribute("definitionId", conditionDefinitionId);
						workflowCondition.addAttribute("elementName", conditionElementName);
						workflowCondition.addAttribute("operation", conditionElementOperation);
						if (inputData.exists("operationDuration") && 
								inputData.exists("operationDurationType")) {
							String operationDuration = inputData.getSingleValue("operationDuration");
							String operationDurationType = inputData.getSingleValue("operationDurationType");
							workflowCondition.addAttribute("duration", operationDuration);
							workflowCondition.addAttribute("durationType", operationDurationType);
						}
						if (inputData.exists("conditionElementValue")) {
							String[] conditionValues = (String[])inputData.getValues("conditionElementValue");
							for (int j = 0; j < conditionValues.length; j++) { 
								String conditionValue = conditionValues[j];
								workflowCondition.addElement("value").setText(conditionValue);
							}
						}
					}
				} else {
					if (type.equals("boolean") || type.equals("checkbox")) {
						String value = "false";
						Element newPropertyEle = newPropertiesEle.addElement("property");
						newPropertyEle.addAttribute("name", attrName);
						newPropertyEle.addAttribute("value", value);
					}
				}
			}
		}		
	}
	
	public void modifyItem(String defId, String itemId, InputDataAccessor inputData) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
		checkAccess(def.getType(), "modifyItem");
		Document definitionTree = def.getDefinition();
		
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to modify
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				String itemNamePropertyValue = DefinitionUtils.getPropertyValue(item, "name");
				if (itemNamePropertyValue == null) itemNamePropertyValue="";

				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				Map uniqueNames = getUniqueNameMap(this.configRoot, root, itemType);
				if (inputData.exists("propertyId_name")) {
					String name = inputData.getSingleValue("propertyId_name");
					if (Validator.isNull(name)) throw new DefinitionInvalidException("definition.error.nullname");
					//See if the item name is being changed
					if (!name.equals(itemNamePropertyValue) && 
							uniqueNames.containsKey(name)) {
						//This name is not z
						throw new DefinitionInvalidException("definition.error.nameNotUnique", new Object[] {defId, name});
					} else if (!name.equals(itemNamePropertyValue)) {
						//The name is being changed. Check if this is a workflow state
						if (itemType.equals("state") && "workflowProcess".equals(item.getParent().attributeValue("name"))) {
							if (checkStateInUse(def, itemNamePropertyValue)) throw new DefinitionInvalidException("definition.error.cannotModifyState", new Object[] {def.getId()});
						} else if (itemType.equals("parallelThread") && "workflowProcess".equals(item.getParent().attributeValue("name"))) {
							if (checkThreadInUse(def, itemNamePropertyValue)) throw new DefinitionInvalidException("definition.error.cannotModifyThread", new Object[] {def.getId()});
						}
					}
				}
				Element itemTypeEle = (Element) this.configRoot.selectSingleNode("item[@name='"+itemType+"']");
				if (itemTypeEle != null) {
					//Set the values of each property from the form data
					processProperties(defId, itemTypeEle, item, inputData);
										
					//See if this is a "dataView" type
					if ("dataView".equals(item.attributeValue("type"))) {
						checkDataView(root, item);
					}
					setDefinition(def, definitionTree);
				}
			}
		}
	}
	private void checkDataView(Element root, Element item) {
		//This item is shadowing one of the form data items. Capture its form item name
		String newItemNamePropertyValue = DefinitionUtils.getPropertyValue(item, "name");
		if (!Validator.isNull(newItemNamePropertyValue)) {
			//Find the form item with this name
			Iterator itFormItems = root.selectNodes("//item/properties/property[@value='"+newItemNamePropertyValue+"']").iterator();
			while (itFormItems.hasNext()) {
				//Look for the form item with a "name" property
				Element formItemProperty = (Element) itFormItems.next();
				if (formItemProperty.attributeValue("name", "").equals("name")) {
					//This is a "name" property. Now see if it under the form tree
					Element parentElement = formItemProperty.getParent();
					while (parentElement != null) {
						if (parentElement.getName().equals("item") && parentElement.attributeValue("type", "").equals("form")) {
							//Found it. This item is part of the "form" tree.
							break;
						}
						parentElement = parentElement.getParent();
					}
					if (parentElement != null) {
						//Get the type of the item that is being shadowed
						String shadowItemName = formItemProperty.getParent().getParent().attributeValue("name", "");
						item.addAttribute("formItem", shadowItemName);
					}
				}
			}
		}
		
	}
	private boolean checkStateInUse(Definition def, String state) {
		//This is a workflow state. Make sure no entries are using that state
		FilterControls fc = new FilterControls();
		fc.add("definition", def);
		fc.add("state", state);
		List inUse = getCoreDao().loadObjects(WorkflowState.class, fc);
		if (!inUse.isEmpty()) return true;
		return false;
		
	}
	private boolean checkThreadInUse(Definition def, String threadName) {
		//This is a workflow state. Make sure no entries are using that state
		FilterControls fc = new FilterControls();
		fc.add("definition", def);
		fc.add("threadName", threadName);
		List inUse = getCoreDao().loadObjects(WorkflowState.class, fc);
		if (!inUse.isEmpty()) return true;
		return false;
	}

	public void deleteItem(String defId, String itemId) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
		checkAccess(def.getType(), "deleteItem");

		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to delete
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				if (itemType.equals("state") && "workflowProcess".equals(item.getParent().attributeValue("name"))) { 
					//This is a workflow state. Make sure no entries are using that state
					String state = DefinitionUtils.getPropertyValue(item, "name");
					if (checkStateInUse(def, state)) throw new DefinitionInvalidException("definition.error.cannotModifyState", new Object[] {def.getId()});
				}
				if (itemType.equals("parallelThread") && "workflowProcess".equals(item.getParent().attributeValue("name"))) { 
					//This is a workflow state. Make sure no entries are using that state
					String threadName = DefinitionUtils.getPropertyValue(item, "name");
					if (checkThreadInUse(def, threadName)) throw new DefinitionInvalidException("definition.error.cannotModifyThread", new Object[] {def.getId()});
				}

				
				Element itemTypeEle = (Element) this.configRoot.selectSingleNode("item[@name='"+itemType+"']");
				//Check that this element is allowed to be deleted
				if (itemTypeEle == null || !itemTypeEle.attributeValue("canBeDeleted", "true").equalsIgnoreCase("false")) {
					Element parent = item.getParent();
					//Delete the item from the definition tree
					item.detach();
					//Check to make sure there are any items marked as "cannot be deleted"
					Iterator itItems = item.selectNodes("./descendant::item").listIterator();
					if (itItems != null) {
						while (itItems.hasNext()) {
							Element item2 = (Element) itItems.next();
							String itemType2 = item2.attributeValue("name", "");
							//Element itemTypeEle2 = (Element) this.definitionConfig.getRootElement().selectSingleNode("item[@name='"+itemType2+"']");
							Element itemTypeEle2 = (Element) this.configRoot.selectSingleNode("item[@name='"+itemType2+"']");
							if (itemTypeEle2 != null && itemTypeEle2.attributeValue("canBeDeleted", "true").equalsIgnoreCase("false")) {
								//This item cannot be deleted. Add it back on the parent
								parent.add(item2.detach());
							}
						}
					}
					setDefinition(def, definitionTree);
				}
			}
		}
	}

	public void modifyItemLocation(String defId, String sourceItemId, String targetItemId, String position) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
		checkAccess(def.getType(), "modifyItemLocation");
		if (!sourceItemId.equals(targetItemId.toString())) {
			Document definitionTree = def.getDefinition();
			if (definitionTree != null) {
				Element root = definitionTree.getRootElement();
				//Find the element to move
				Element sourceItem = (Element) root.selectSingleNode("//item[@id='"+sourceItemId+"']");
				if (sourceItem != null) {
					Element targetItem = (Element) root.selectSingleNode("//item[@id='"+targetItemId+"']");
					if (targetItem != null) {
						//We have found both the source and the target ids; do the move
						if (position.equals("into")) {
							//Check that the target area is allowed to receive one of these types
							String sourceItemType = sourceItem.attributeValue("name", "");
							//See if this is a dataView mirroring another element
							if (sourceItem.attributeValue("type", "").equals("dataView")) {
								//Get the actual element type being tracked
								sourceItemType = sourceItem.attributeValue("formItem", "");
							}
							if (!sourceItemType.equals("") && checkTargetOptions(definitionTree, targetItem.attributeValue("name"), sourceItemType)) {
								//Detach the source item
								sourceItem.detach();
								//Add it to the target element
								targetItem.add(sourceItem);
							} else {
								//The target item is not designed to accept this item as a child
								throw new DefinitionInvalidException("definition.error.illegalMoveInto", new Object[] {defId});
							}
						} else if (position.equals("above")) {
							//Get the parent of the target item
							Element sourceParent = (Element) sourceItem.getParent();
							//Detach the source item
							sourceItem.detach();
							List sourceParentContent = sourceParent.content();
							int i = sourceParentContent.indexOf(targetItem);
							if (i < 0) {i = 0;}
							sourceParentContent.add(i,sourceItem);
						} else if (position.equals("below")) {
							//Get the parent of the target item
							Element sourceParent = (Element) sourceItem.getParent();
							//Detach the source item
							sourceItem.detach();
							List sourceParentContent = sourceParent.content();
							int i = sourceParentContent.indexOf(targetItem);
							sourceParentContent.add(i+1,sourceItem);
						}
						//Write the new document back into the definition
						setDefinition(def, definitionTree);
					} else {
						//Target item is no longer defined as a valid item
						throw new DefinitionInvalidException("definition.error.noElement", new Object[] {defId});
					}
				} else {
					//The item to be moved is no longer defined as a valid item
					throw new DefinitionInvalidException("definition.error.noElement", new Object[] {defId});
				}
			}
		}
	}
	
	//Routine to check that the source item is allowed to be added to the target item type
	private boolean checkTargetOptions(Document definitionTree, String targetItemType, String sourceItemType) {
		//check against base config document
		Element targetItem = (Element) this.configRoot.selectSingleNode("item[@name='"+targetItemType+"']");
		Element sourceItem = (Element) this.configRoot.selectSingleNode("item[@name='"+sourceItemType+"']");
		if (targetItem == null || sourceItem == null) {return false;}
		
		//Check the list of options (types: "option" and "option_select")
		Element options = targetItem.element("options");
		if (options != null) {
			Iterator itOptions = options.elementIterator("option");
			while (itOptions.hasNext()) {
				Element option = (Element) itOptions.next();
				if (option.attributeValue("name").equals(sourceItemType)) {
					return true;
				}
			}
			
			itOptions = options.elementIterator("option_select");
			while (itOptions.hasNext()) {
				Element option = (Element) itOptions.next();
				String optionPath = option.attributeValue("path", "");
				Iterator itOptionsSelect = this.configRoot.selectNodes(optionPath).iterator();
				while (itOptionsSelect.hasNext()) {
					Element optionSelect = (Element) itOptionsSelect.next();
					if (optionSelect.attributeValue("name").equals(sourceItemType)) {
						return true;
					}
				}
			}
		}
		//None found, this isn't allowed
		return false;
	}

	private int populateNewDefinitionTree(Element source, Element target, final Element configRoot, int id, boolean includeDefault) {
		//See if the source has any options that are required
		Element options = source.element("options");
		if (options == null) return id;
		List lOptions = options.selectNodes("option");
		if (lOptions == null) return id;
		
		Iterator iOptions = lOptions.iterator();
		while (iOptions.hasNext()) {
			Element nextOption = (Element)iOptions.next();
			if (nextOption.attributeValue("initial", "").equals("true") || 
					(includeDefault && nextOption.attributeValue("default", "").equals("true"))) {
				//This option is required. Copy it to the target
				Element item = target.addElement("item");
				String name = nextOption.attributeValue("name");
				item.addAttribute("name", name);
				Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+name+"']");
				if (itemElement == null) {continue;}
				//Copy all of the attributes that should be in the definition
				String caption = itemElement.attributeValue("caption", nextOption.attributeValue("name"));
				
				item.addAttribute("caption", caption);
				
				String itemType = itemElement.attributeValue("type", "");
				if (!itemType.equals("")) item.addAttribute("type", itemType);
				item.addAttribute("id", Integer.toString(id));
				
				// Get the properties to be copied
				// (rsordillo) will add each 'property' Element 1 at a time. We want to remove some property
				// Attributes that are not needed for runtime.
				setDefinitionProperties(item, itemElement);
				
				//Bump up the unique id
				id++;
				
				//Now see if this item has some required options of its own
				id = populateNewDefinitionTree(itemElement, item, configRoot, id, includeDefault);
			}
		}
		Iterator iOptionSelects = options.selectNodes("option_select").iterator();
		while (iOptionSelects.hasNext()) {
			Element nextOptionSelect = (Element)iOptionSelects.next();
			if (nextOptionSelect.attributeValue("initial", "").equals("true") || 
					(includeDefault && nextOptionSelect.attributeValue("default", "").equals("true"))) {
				//This option_select is required. Process it and copy its items to the target
				String optionPath = nextOptionSelect.attributeValue("path", "");
				Iterator itOptionSelectItems = configRoot.selectNodes(optionPath).iterator();
				while (itOptionSelectItems.hasNext()) {
					nextOptionSelect = (Element)itOptionSelectItems.next();
					Element item = target.addElement("item");
					item.addAttribute("name", (String)nextOptionSelect.attributeValue("name"));
					Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+nextOptionSelect.attributeValue("name")+"']");
					if (itemElement == null) {continue;}
					String caption = itemElement.attributeValue("caption", nextOptionSelect.attributeValue("name"));
					item.addAttribute("caption", caption);
					item.addAttribute("id", Integer.toString(id));
					
					// Get the properties to be copied
					// (rsordillo) will add each 'property' Element 1 at a time. We want to remove some property
					// Attributes that are not needed for runtime.
					setDefinitionProperties(item, itemElement);
					

					id++;
					
					//Now see if this item has some required options of its own
					id = populateNewDefinitionTree(itemElement, item, configRoot, id, includeDefault);
				}
			}
		}
		return id;
	}
	
	//Rouitine to make sure a definition has all of the proper options as defined in the config file
	//  This is useful to propagate new items added to the config definition xml file
	private void validateDefinitionTree(Definition def) {
		Document defDoc = def.getDefinition();
		if (updateDefinitionTree(defDoc)) setDefinition(def, defDoc);
	}
	private boolean updateDefinitionTree(Document defDoc) {
		boolean defChanged = false;
		Element defRoot = defDoc.getRootElement();
		int startingId = Integer.valueOf(defRoot.attributeValue("nextId", "1")).intValue();
		int nextId = startingId;

		
		//Get the definition root element to check it
		Element configRootDefinitionEle = (Element) this.configRoot.selectSingleNode("//definition[@definitionType='"+
				defRoot.attributeValue("definitionType")+"']");
		if (configRootDefinitionEle != null) {
			//See if there are any items missing from the top definition item
			nextId = updateDefinitionTreeElement("definition", defRoot, defRoot, this.configRoot, nextId);
			
			//Look at all of the items to see if any of their options are missing
			Iterator itDefItems = defRoot.elementIterator("item");
			while (itDefItems.hasNext()) {
				Element defItem = (Element) itDefItems.next();
				nextId = updateDefinitionTreeElement("item", defItem, defItem, this.configRoot, nextId);
			}
			if (nextId != startingId) defChanged = true;
		}
		return defChanged;
	}
	private int updateDefinitionTreeElement(String elementType, Element source, Element target, Element configRoot, int id) {
		//Find the element type
		Element configItemElement;
		if (elementType.equals("definition")) {
			configItemElement = (Element) configRoot.selectSingleNode("item[@definitionType='"+
					source.attributeValue("definitionType")+"']");
		} else {
			configItemElement = (Element) configRoot.selectSingleNode("item[@name='"+
					source.attributeValue("name")+"']");			
		}
		//See if the source has any required options that are missing
		Element options = configItemElement.element("options");
		if (options == null) return id;
		List lOptions = options.selectNodes("option");
		if (lOptions == null) return id;
		Iterator iOptions = lOptions.iterator();
		while (iOptions.hasNext()) {
			Element nextOption = (Element)iOptions.next();
			Element nextOptionConfigItem = (Element) configRoot.selectSingleNode("item[@name='"+
					nextOption.attributeValue("name")+"']");
			if (nextOption.attributeValue("initial", "").equals("true") &&
					nextOptionConfigItem.attributeValue("canBeDeleted", "").equals("false")) {
				//This option is required. See if it exists
				if (source.selectSingleNode("./item[@name='"+nextOptionConfigItem.attributeValue("name")+"']") == null) {
					//This option is missing. Copy it to the target
					Element item = target.addElement("item");
					item.addAttribute("name", (String)nextOption.attributeValue("name"));
					Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+nextOption.attributeValue("name")+"']");
					if (itemElement == null) {continue;}
					//Copy all of the attributes that should be in the definition
					String caption = itemElement.attributeValue("caption", nextOption.attributeValue("name"));
					item.addAttribute("caption", caption);
					String itemType = itemElement.attributeValue("type", "");
					if (!itemType.equals("")) item.addAttribute("type", itemType);
					item.addAttribute("id", Integer.toString(id));
					
					// Get the properties to be copied
					// (rsordillo) will add each 'property' Element 1 at a time. We want to remove some property
					// Attributes that are not needed for runtime.
					setDefinitionProperties(item, itemElement);
					
					//Bump up the unique id
					id++;
					
					//Now see if this item has some required options of its own
					id = updateDefinitionTreeElement("item", itemElement, item, configRoot, id);
				}
			}
		}
		Iterator iOptionSelects = options.selectNodes("option_select").iterator();
		while (iOptionSelects.hasNext()) {
			Element nextOptionSelect = (Element)iOptionSelects.next();
			if (nextOptionSelect.attributeValue("initial", "").equals("true") &&
					nextOptionSelect.attributeValue("canBeDeleted", "").equals("false")) {
				//This option_select is required. See if it exists
				if (source.selectSingleNode("./item[@name='"+nextOptionSelect.attributeValue("name")+"']") == null) {
					//This option_select is missing. Process it and copy its items to the target
					String optionPath = nextOptionSelect.attributeValue("path", "");
					Iterator itOptionSelectItems = configRoot.selectNodes(optionPath).iterator();
					while (itOptionSelectItems.hasNext()) {
						nextOptionSelect = (Element)itOptionSelectItems.next();
						Element item = target.addElement("item");
						item.addAttribute("name", (String)nextOptionSelect.attributeValue("name"));
						Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+nextOptionSelect.attributeValue("name")+"']");
						if (itemElement == null) {continue;}
						String caption = itemElement.attributeValue("caption", nextOptionSelect.attributeValue("name"));
						item.addAttribute("caption", caption);
						item.addAttribute("id", Integer.toString(id));
						
						// Get the properties to be copied
						// (rsordillo) will add each 'property' Element 1 at a time. We want to remove some property
						// Attributes that are not needed for runtime.
						setDefinitionProperties(item, itemElement);
						

						id++;
						
						//Now see if this item has some required options of its own
						id = updateDefinitionTreeElement("item", itemElement, item, configRoot, id);
					}
				}
			}
		}
		return id;
	}
	
	private Map getUniqueNameMap(final Element configRoot, Element definitionTree, String itemType) {
		Map uniqueNames = new HashMap();

		Element itemTypeEle = (Element) configRoot.selectSingleNode("item[@name='"+itemType+"']");
		if (itemTypeEle != null) {
			//See if this item requires a unique name
			String uniquePath = DefinitionUtils.getPropertyValue(itemTypeEle, "name", "unique");
			if (!Validator.isNull(uniquePath)) {
				//There is a request for uniqueness, so get the list from the definition file
				Iterator itNames = definitionTree.selectNodes(uniquePath).iterator();
				while (itNames.hasNext()) {
					//Find the name property of all items in the specified path
					String itemEleNameValue = DefinitionUtils.getPropertyValue((Element)itNames.next(), "name");
					if (!Validator.isNull(itemEleNameValue)) {
						//We found a name, so add it to the map
						uniqueNames.put(itemEleNameValue, itemEleNameValue);
					}
				}
			}
		}
		return uniqueNames;
	}

	
    public Document getDefinitionConfig() {
    	return this.definitionConfig;
    }

    public Map getEntryData(Document definitionTree, InputDataAccessor inputData, Map fileItems) {
		//access check not needed = have tree already
		
    	// entryData will contain the Map of entry data as gleaned from the input data
		Map entryDataAll = new HashMap();
		Map entryData = new HashMap();
		List fileData = new ArrayList();
		entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA, entryData);
		entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA, fileData);
		
		if (definitionTree != null) {
			//root is the root of the entry's definition
			Element root = definitionTree.getRootElement();
			
			//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
			if (entryFormItem != null) {
				//While going through the entry's elements, keep track of the current form name (needed to process date elements)
				List<Element> itItems = entryFormItem.selectNodes(".//item[@type='data']");
				//see if title is generated and save source
				boolean titleGenerated = false;
				String titleSource = null;
				Element titleEle = (Element)entryFormItem.selectSingleNode(".//item[@name='title']");
				if (titleEle != null) {
					titleGenerated = GetterUtil.get(DefinitionUtils.getPropertyValue(titleEle, "generated"), false);
					if (titleGenerated) {
						titleSource=DefinitionUtils.getPropertyValue(titleEle, "itemSource");
					}
				}
				for (Element nextItem: itItems) {
					String itemName = (String) nextItem.attributeValue("name", "");
					//Get the form element name (property name)
					String nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");									
					if (Validator.isNull(nameValue)) {nameValue = nextItem.attributeValue("name");}
								
					//We have the element name, see if it has a value in the input data
					if (itemName.equals("description") || itemName.equals("htmlEditorTextarea")) {
						//Use the helper routine to parse the date into a date object
						Description description = new Description();
						if (inputData.exists(nameValue)) {
							description.setText(inputData.getSingleValue(nameValue));
							//Deal with any markup language transformations before storing the description
							WebHelper.scanDescriptionForUploadFiles(description, fileData);
							WebHelper.scanDescriptionForAttachmentFileUrls(description);
							entryData.put(nameValue, description);
						}
					} else if (itemName.equals("date")) {
						//Use the helper routine to parse the date into a date object
						Date date = DateHelper.getDateFromInput(inputData, nameValue);
						if (date != null) {entryData.put(nameValue, date);}
					} else if (itemName.equals("event")) {
					    //Ditto for event helper routine
					    Boolean hasDur = Boolean.FALSE;
					    if (GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "hasDuration"), false)) {
					    	hasDur = Boolean.TRUE;
					    }
					    Boolean hasRecur = Boolean.FALSE;
					    if (GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "hasRecurrence"), false)) {
					    	hasRecur = Boolean.TRUE;
					    }
					    Event event = EventHelper.getEventFromMap(inputData, nameValue, hasDur, hasRecur);
					    if (event != null) {
					        event.setName(nameValue);
					        entryData.put(nameValue, event);
					    }
					} else if (itemName.equals("user_list")) {
						if (inputData.exists(nameValue)) {
							String[] userIds = inputData.getSingleValue(nameValue).trim().split(" ");
							Set users = new HashSet();
							for (int i = 0; i < userIds.length; i++) {
								try {
									Long.parseLong(userIds[i]);
									users.add(userIds[i]);
								} catch (NumberFormatException ne) {}
							}
							if (!users.isEmpty()) {
								CommaSeparatedValue v = new CommaSeparatedValue();
								v.setValue((String[])users.toArray(userIds));
								entryData.put(nameValue, v);
							}
						}
					} else if (itemName.equals("userListSelectbox")) {
						if (inputData.exists(nameValue)) {
							String[] userIds = inputData.getValues(nameValue);
							Set users = new HashSet();
							for (int i = 0; i < userIds.length; i++) {
								try {
									Long.parseLong(userIds[i]);
									users.add(userIds[i]);
								} catch (NumberFormatException ne) {}
							}
							if (!users.isEmpty()) {
								CommaSeparatedValue v = new CommaSeparatedValue();
								v.setValue((String[])users.toArray(userIds));
								entryData.put(nameValue, v);
							}
						}
					} else if (itemName.equals("selectbox")) {
						if (inputData.exists(nameValue)) entryData.put(nameValue, inputData.getValues(nameValue));
					} else if (itemName.equals("checkbox")) {
						if (inputData.exists(nameValue) && inputData.getSingleValue(nameValue).equals("on")) {
							entryData.put(nameValue, Boolean.TRUE);
						} else {
							entryData.put(nameValue, Boolean.FALSE);
						}
					} else if (itemName.equals("profileTimeZone")) {
						if (inputData.exists(nameValue)) {
							Object val = inputData.getSingleObject(nameValue);
							if (val == null) {
								entryData.put(nameValue, null);
							} else if (val instanceof TimeZone) {
								entryData.put(nameValue, (TimeZone)val);
							} else {
								String sVal = inputData.getSingleValue(nameValue);
								if (Validator.isNull(sVal)) entryData.put(nameValue, null);
								else entryData.put(nameValue, TimeZone.getTimeZone(sVal));
							}
						}
					} else if (itemName.equals("file") || itemName.equals("graphic") || 
							itemName.equals("profileEntryPicture")) {
					    if (fileItems != null && fileItems.containsKey(nameValue)) {
					    	MultipartFile myFile = (MultipartFile)fileItems.get(nameValue);
					    	String fileName = myFile.getOriginalFilename();
					    	if (fileName.equals("")) continue;
					    	String repositoryName = DefinitionUtils.getPropertyValue(nextItem, "storage");
					    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
					    	FileUploadItem fui;
					    	if (titleGenerated && nameValue.equals(titleSource) && 
					    			(itemName.equals("file") || itemName.equals("graphic")))
					    		fui = new FileUploadItem(FileUploadItem.TYPE_TITLE, nameValue, myFile, repositoryName);
					    	else fui = new FileUploadItem(FileUploadItem.TYPE_FILE, nameValue, myFile, repositoryName);
						    	//See if there is a scaling request for this graphic file. If yes, pass along the hieght and width
			    			fui.setMaxWidth(GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "maxWidth"), 0));
			    			fui.setMaxHeight(GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "maxHeight"), 0));
					    	// TODO The following piece of code may need a better conditional
					    	// statement than this, since we probably do not want to generate
					    	// thumbnails for all graphic-type file uploads. Or do we? 
					    	if (itemName.equals("graphic")) {
					    		fui.setGenerateThumbnail(true);
					    		fui.setIsSquareThumbnail(true);
					    	} else if (itemName.equals("profileEntryPicture")) {
					    		fui.setGenerateThumbnail(true);
					    	} else if (fileName.endsWith(".jpg")) {
					    		fui.setGenerateThumbnail(true);
					    		fui.setIsSquareThumbnail(true);						    		
					    	}
					    	if(inputData.exists(ObjectKeys.PI_SYNCH_TO_SOURCE)) {
					    		fui.setSynchToRepository(Boolean.parseBoolean(inputData.getSingleValue(ObjectKeys.PI_SYNCH_TO_SOURCE)));
					    	}
					    	fileData.add(fui);
					    }
					} else if (itemName.equals("attachFiles")) {
					    if (fileItems != null) {
							int number = GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "number"), 1);
							for (int i=1;i <= number;i++) {
								String fileEleName = nameValue + Integer.toString(i);
								if (fileItems.containsKey(fileEleName)) {												
							    	MultipartFile myFile = (MultipartFile)fileItems.get(fileEleName);
							    	String fileName = myFile.getOriginalFilename();
							    	if (fileName.equals("")) continue;
							    	// Different repository can be specified for each file uploaded.
							    	// If not specified, use the statically selected one.  
							    	String repositoryName = null;
							    	if (inputData.exists(nameValue + "_repos" + Integer.toString(i))) 
							    		repositoryName = inputData.getSingleValue(nameValue + "_repos" + Integer.toString(i));
							    	if (repositoryName == null) {
								    	repositoryName = DefinitionUtils.getPropertyValue(nextItem, "storage");
								    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
							    	}
							    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
							    	if(inputData.exists(ObjectKeys.PI_SYNCH_TO_SOURCE)) {
							    		fui.setSynchToRepository(Boolean.parseBoolean(inputData.getSingleValue(ObjectKeys.PI_SYNCH_TO_SOURCE)));
							    	}
							    	fileData.add(fui);
								}
							}
					    }
					} else {
						if (inputData.exists(nameValue)) entryData.put(nameValue, inputData.getSingleValue(nameValue));
					}
				}
			}
		}
   	
    	return entryDataAll;
    }
    public List getDefinitions() {
		// Controllers need access to definitions.  Allow world read        
    	List defs = coreDao.loadDefinitions(RequestContextHolder.getRequestContext().getZoneId());
    	return defs;
    }
    
    public List getDefinitions(int type) {
		// Controllers need access to definitions.  Allow world read        
    	List defs = coreDao.loadDefinitions(RequestContextHolder.getRequestContext().getZoneId(), type);
    	return defs;
    }
    private Map getOptionalArgs(Element indexingElem) {
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

	//Routine to get the data elements for use in search queries
    public Map getEntryDefinitionElements(String id) {
		//Get a map for the results
		//access doesn't seem needed
    	Map dataElements = new TreeMap();		
		Definition def = getDefinition(id);
		
		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			//root is the root of the entry's definition
			Element root = definitionTree.getRootElement();
			
			//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
			if (entryFormItem != null) {
				List<Element> itItems = entryFormItem.selectNodes(".//item[@type='data']");
				for (Element nextItem: itItems) {
					//Get a map to store the results in
					Map itemData = new HashMap();
					
					String itemName = (String) nextItem.attributeValue("name", "");
					itemData.put("type", itemName);
					
					String nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");	
					if (Validator.isNull(nameValue)) nameValue = itemName;
						
					String captionValue = DefinitionUtils.getPropertyValue(nextItem, "caption");							
					if (Validator.isNull(captionValue)) captionValue = nameValue;							
					itemData.put("caption", NLT.getDef(captionValue));
								
					//We have the element name, see if it has option values
					if (itemName.equals("selectbox")) {
						Map valueMap = new LinkedHashMap();
						Iterator itSelectionItems = nextItem.selectNodes("item[@name='selectboxSelection']").iterator();
						while (itSelectionItems.hasNext()) {
							Element selection = (Element) itSelectionItems.next();
							//Get the element name (property name)
							String selectionNameValue = DefinitionUtils.getPropertyValue(selection, "name");
							String selectionCaptionValue = DefinitionUtils.getPropertyValue(selection, "caption");
							if (Validator.isNotNull(selectionNameValue)) {
								if (Validator.isNull(selectionCaptionValue)) {selectionCaptionValue = selectionNameValue;}
								valueMap.put(selectionNameValue, NLT.getDef(selectionCaptionValue));
							}
						}
						itemData.put("length", new Integer(valueMap.size()).toString());
						if (valueMap.size() > 10) itemData.put("length", "10");
						itemData.put("values", valueMap);
						
					} else if (itemName.equals("radio")) {
						Map valueMap = new TreeMap();
						Iterator itSelectionItems = nextItem.selectNodes("item[@name='radioSelection']").iterator();
						while (itSelectionItems.hasNext()) {
							Element selection = (Element) itSelectionItems.next();
							//Get the element name (property name)
							String selectionNameValue = DefinitionUtils.getPropertyValue(selection, "name");
							String selectionCaptionValue = DefinitionUtils.getPropertyValue(selection, "caption");
							if (Validator.isNotNull(selectionNameValue)) {
								if (Validator.isNull(selectionCaptionValue)) {selectionCaptionValue = selectionNameValue;}
								valueMap.put(selectionNameValue, NLT.getDef(selectionCaptionValue));
							}
						}
						itemData.put("length", new Integer(valueMap.size()).toString());
						if (valueMap.size() > 10) itemData.put("length", "10");
						itemData.put("values", valueMap);
					}
						
					//Add this element to the results
					dataElements.put(nameValue, itemData);
				}
			}
		}
   	
    	return dataElements;
    }

	//Routine to get the data elements for use in search queries
    public Map getWorkflowDefinitionStates(String id) {
		//Get a map for the results
    	Map dataStates = new TreeMap();
		//TODO: access doesn't seem needed
		Definition def = getDefinition(id);
		
		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			//root is the root of the entry's definition
			Element root = definitionTree.getRootElement();
			
			//Get a list of all of the state items in the definition 
			Iterator itItems = root.selectNodes("item[@name='workflowProcess']/item[@name='state']").listIterator();
			while (itItems.hasNext()) {
				//Get a map to store the results in
				Map itemData = new HashMap();
				
				Element nextItem = (Element) itItems.next();
				String itemName = (String) nextItem.attributeValue("name", "");
				itemData.put("type", itemName);
					
				String nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");	
				if (Validator.isNull(nameValue)) nameValue = itemName;
						
				String captionValue = DefinitionUtils.getPropertyValue(nextItem, "caption");							
				if (Validator.isNull(captionValue)) captionValue = nameValue;							
				itemData.put("caption", NLT.getDef(captionValue));
								
				//Add this state to the results
				dataStates.put(nameValue, itemData);
			}
		}
   	
    	return dataStates;
    }

    /**
     * Manipulate base configuration properties/property Elements before adding to entry configuration.
     * This will reduce the size of the entry configuration by not adding un-required Element data.
     * 
     * @param parent		What Element we will add the properties Element too
     * @param configItem	Base configuration Item Element that we are copying properties Element from
     * 
     */
    private void setDefinitionProperties(Element parent, final Element configItem)    {
    	Element properties = parent.addElement("properties");
    	List<Element> propertyItems = propertyItems = configItem.selectNodes("properties/property");
		for (Element configProperty:propertyItems)
		{
			Element property = properties.addElement("property");
			property.addAttribute("name", configProperty.attributeValue("name"));
			property.addAttribute("value", configProperty.attributeValue("value", ""));
		}
		
		return;
    }

	public void walkDefinition(DefinableEntity entry, DefinitionVisitor visitor) {
		SimpleProfiler.startProfiler("DefinitionModuleImpl.walkDefinition");
		//access check not needed = assumed okay from entry
        Definition def = entry.getEntryDef();
        if(def == null) return;
        String flagElementPath = "./" + visitor.getFlagElementName();
        Document definitionTree = def.getDefinition();
        if (definitionTree != null) {
            Element root = definitionTree.getRootElement();

            //Get a list of all of the items in the definition
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
            if (entryFormItem != null) {
                List<Element> items = entryFormItem.selectNodes(".//item[@type='data']");
                if (items != null) {
                    for (Element nextItem:items) {

                    	Element flagElem = (Element) nextItem.selectSingleNode(flagElementPath);
                    	if (flagElem == null) {
                        	 // The current item in the entry definition does not contain
                        	 // the flag element. Check the corresponding item in the default
                        	 // config definition to see if it has it.
                        	 // This two level mechanism allows entry definition (more specific
                        	 // one) to override the settings in the default config definition
                        	 // (more general one). This overriding works in its 
                        	 // entirity only, that is, partial overriding is not supported.
     						//Find the item in the base configuration definition to see if it is a data item
                    		String itemName = (String) nextItem.attributeValue("name");						
     						Element configItem = this.definitionBuilderConfig.getItem(this.definitionConfig, itemName);
     						if (configItem != null) flagElem = (Element) configItem.selectSingleNode(flagElementPath);
                    	}

                    	if (flagElem != null) {
                        	 Map args = getOptionalArgs(flagElem);
                        	 visitor.visit(nextItem, flagElem, args);
                        }
                    }
                }
            }
        }
		SimpleProfiler.stopProfiler("DefinitionModuleImpl.walkDefinition");
    }
}
