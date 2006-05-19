package com.sitescape.ef.module.definition.impl;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.definition.index.FieldBuilderUtil;
import com.sitescape.ef.module.definition.notify.NotifyBuilderUtil;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import com.sitescape.ef.security.function.WorkAreaOperation;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.MergeableXmlClassPathConfigFiles;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Description;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.CommaSeparatedValue;
import com.sitescape.util.Validator;

import com.sitescape.ef.web.util.DateHelper;
import com.sitescape.ef.web.util.EventHelper;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.workflow.WorkflowModule;

import org.apache.lucene.document.Field;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import com.sitescape.ef.domain.DefinitionInvalidException;

/**
 * @author hurley
 *
 */
public class DefinitionModuleImpl extends CommonDependencyInjection implements DefinitionModule {
	private Document definitionConfig;
	private MergeableXmlClassPathConfigFiles definitionBuilderConfig;
	
	private WorkflowModule workflowModule;
	    
	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	protected WorkflowModule getWorkflowModule() {
		return workflowModule;
	}
	public String addDefinition(Document doc) {
		Definition def = new Definition();
		Element root = doc.getRootElement();
		def.setName(root.attributeValue("name"));
		def.setTitle(root.attributeValue("caption"));
		String type = root.attributeValue("type");
		def.setType(Integer.parseInt(type));
		def.setZoneName(RequestContextHolder.getRequestContext().getZoneName());
		String id = root.attributeValue("databaseId", "");
		if (Validator.isNull(id)) {
			//doesn't have an it, so generate one
			getCoreDao().save(def);
			root.addAttribute("databaseId", def.getId());
			setDefinition(def,doc);
		} else {
			//import - try reusing existing guid
			def.setId(id);
			setDefinition(def,doc);
			getCoreDao().replicate(def);
		}
		return def.getId();
	}
	public Definition getDefinition(String id) {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
		Workspace workspace = getCoreDao().findTopWorkspace(companyId);
        accessControlManager.checkOperation(workspace, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS);        
 		return coreDao.loadDefinition(id, companyId);
	}
	
	public MergeableXmlClassPathConfigFiles getDefinitionBuilderConfig() {
        return definitionBuilderConfig;
    }
    public void setDefinitionBuilderConfig(MergeableXmlClassPathConfigFiles definitionBuilderConfig) {
        this.definitionBuilderConfig = definitionBuilderConfig;
    }
    
	public Definition addDefinition(String name, String title, int type, Map formData) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();

		Definition newDefinition = new Definition();
		newDefinition.setName(name);
		newDefinition.setTitle(title);
		newDefinition.setType(type);
		newDefinition.setZoneName(zoneName);
		getCoreDao().save(newDefinition);
		Document doc = getDefaultDefinition(name, title, type, formData);
		Element root = doc.getRootElement();
		root.addAttribute("databaseId", newDefinition.getId());
		setDefinition(newDefinition, doc);
		return newDefinition;
	}
	
	public void modifyDefinitionName(String id, String name, String title) {
		Definition def = getDefinition(id);
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
		//Store this attribute in the definition document (but only if it is different)
		Document defDoc = def.getDefinition();
		if (value.equals("") || !defDoc.getRootElement().attributeValue(key, "").equals(value)) {
			defDoc.getRootElement().addAttribute(key, value);
			setDefinition(def, defDoc);
		}
		//When any change is made, validate the the definition is at the same level as the configuration file
		validateDefinitionAttributes(def);
	}
	
    protected void setDefinition(Definition def, Document doc) {
    	//Write out the new definition file
    	def.setDefintion(doc);
    	
    	//If this is a workflow definition, build the corresponding JBPM workflow definition
    	if (def.getType() == Definition.WORKFLOW) {
    		//Use the definition id as the workflow process name
    		getWorkflowModule().modifyProcessDefinition(def.getId(), def);
    	}
   }

	
	public void modifyDefinitionProperties(String id, Map formData) {
		Definition def = getDefinition(id);
		if (def != null) {			
			String definitionName = "";
			if (formData.containsKey("propertyId_name")) {
				definitionName = ((String[]) formData.get("propertyId_name"))[0];
			}
			if (definitionName.equals("")) definitionName = def.getName();
			String definitionCaption = "";
			if (formData.containsKey("propertyId_caption")) {
				definitionCaption = ((String[]) formData.get("propertyId_caption"))[0];
			}
			if (definitionCaption.equals("")) definitionCaption = def.getTitle();
			modifyDefinitionName(id, definitionName, definitionCaption);
			
			//Store the properties in the definition document
			Document defDoc = def.getDefinition();
			this.getDefinitionConfig();
			Element configRoot = definitionConfig.getRootElement();
			String type = String.valueOf(def.getType());
			Element definition = (Element) configRoot.selectSingleNode("item[@definitionType='"+type+"']");
			if (definition != null) {
				//Make sure the definition name and caption remain consistent
				Map formData2 = new HashMap(formData);
				formData2.put("propertyId_name", new String[]{def.getName()});
				formData2.put("propertyId_caption", new String[]{def.getTitle()});
				//Add the properties
				processProperties(def.getId(), definition, defDoc.getRootElement(), formData2);
			}
			setDefinition(def, defDoc);
			
			//When any change is made, validate the the definition is at the same level as the configuration file
			validateDefinitionAttributes(def);
		}
	}
	
	//Rouitine to make sure a definition has all of the proper attributes as defined in the config file
	//  This is useful to propagate new attributes added to the config definition xml file
	public void validateDefinitionAttributes(Definition def) {
		Document defDoc = def.getDefinition();
		if (updateDefinitionAttributes(defDoc)) setDefinition(def, defDoc);
	}
	public boolean updateDefinitionAttributes(Document defDoc) {
		boolean defChanged = false;
		Element defRoot = defDoc.getRootElement();
		this.getDefinitionConfig();
		Element configRoot = definitionConfig.getRootElement();
		
		//Look at all of the items to see if any of their attributes are missing
		Iterator itDefItems = defRoot.elementIterator("item");
		while (itDefItems.hasNext()) {
			Element defItem = (Element) itDefItems.next();
			//Find the matching element in the configuration xml file
			Element configItem = (Element) configRoot.selectSingleNode("item[@name='"+defItem.attributeValue("name", "")+"']");
			if (configItem != null) {
				//Check to see if there are new attributes from the config file that should be copied into the definition
				Iterator itConfigItemAttributes = configItem.attributeIterator();
				while (itConfigItemAttributes.hasNext()) {
					Attribute attr = (Attribute) itConfigItemAttributes.next();
					//If the attribute does not exist in the definition item, copy it from the config file
					if (defItem.attributeValue(attr.getName()) == null) {
						defItem.addAttribute(attr.getName(), attr.getValue());
						defChanged = true;
					}
				}
			}
		}
		return defChanged;
	}
	
	public void saveDefinitionLayout(String id, Map formData) {
		Definition def = getDefinition(id);
		Document defDoc = def.getDefinition();
		
		if (formData.containsKey("xmlData") && def != null) {
			Document appletDef;
			try {
				appletDef = DocumentHelper.parseText(((String[])formData.get("xmlData"))[0]);
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
		getCoreDao().delete(def);
		if (def.getType() == Definition.WORKFLOW) {
			//jbpm defs are named with the string id of the ss definitions
			getWorkflowModule().deleteProcessDefinition(def.getId());
		} 
	}

	public Document getDefaultDefinition(String name, String title, int type, Map formData) {
		this.getDefinitionConfig();
		Element configRoot = definitionConfig.getRootElement();
		Element definition = (Element) configRoot.selectSingleNode("item[@definitionType='"+type+"']");
		if (definition == null) {return null;}
		
		//We found the definition. Now build the default definition
		Document newTree = DocumentHelper.createDocument();
		Element ntRoot = newTree.addElement("definition");
		ntRoot.addAttribute("name", name);
		ntRoot.addAttribute("caption", title);
		ntRoot.addAttribute("type", String.valueOf(type));
		int id = 1;
		id = populateNewDefinitionTree(definition, ntRoot, configRoot, id, true);
		ntRoot.addAttribute("nextId", Integer.toString(id));
		
		//Add the properties
		processProperties("0", definition, ntRoot, formData);

		//Copy any additional attributes from the configuration file
		updateDefinitionAttributes(newTree);
		
		return newTree;
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
	public Element addItem(String defId, String itemId, String itemNameToAdd, Map formData) throws DefinitionInvalidException {
		Definition def = getCoreDao().loadDefinition(defId, RequestContextHolder.getRequestContext().getZoneName());
		this.getDefinitionConfig();
		Document definitionTree = def.getDefinition();
		Element newItem = addItemToDefinitionDocument(def.getId(), definitionTree, itemId, itemNameToAdd, formData);
		if (newItem != null) {
			//Save the updated document
			setDefinition(def, definitionTree);
		}
		return newItem;
	}
	
	public Element addItemToDefinitionDocument(String defId, Document definitionTree, String itemId, String itemNameToAdd, Map formData) throws DefinitionInvalidException {
		this.getDefinitionConfig();
		Element configRoot = this.definitionConfig.getRootElement();
		Element newItem = null;
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			Map uniqueNames = getUniqueNameMap(configRoot, root, itemNameToAdd);
			if (formData.containsKey("propertyId_name")) {
				String name = ((String[]) formData.get("propertyId_name"))[0];
				if (uniqueNames.containsKey(name)) {
					//This name is not unique
					throw new DefinitionInvalidException(defId, NLT.get("definition.error.nameNotUnique")+ " ("+name+")");
				}
			}

			//Find the element to add to
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				//Find the requested new item in the configuration document
				Element itemEleToAdd = (Element) this.definitionConfig.getRootElement().selectSingleNode("item[@name='"+itemNameToAdd+"']");
				if (itemEleToAdd != null) {
					//Add the item 
					newItem = item.addElement("item");
					//Copy the attributes from the config item to the new item
					Iterator attrs = itemEleToAdd.attributeIterator();
					while (attrs.hasNext()) {
						Attribute attr = (Attribute) attrs.next();
						newItem.addAttribute(attr.getName(), attr.getValue());
					}
					
					//Get the next id number from the root
					int nextId = Integer.valueOf(root.attributeValue("nextId")).intValue();
					newItem.addAttribute("id", (String) Integer.toString(nextId));
					root.addAttribute("nextId", (String) Integer.toString(++nextId));
					
					//Process the properties (if any)
					processProperties(defId, itemEleToAdd, newItem, formData);
					
					//Copy the jsps (if any)
					Element configJsps = itemEleToAdd.element("jsps");
					if (configJsps != null) {
						Element newJspsEle = configJsps.createCopy();
						newItem.add(newJspsEle);
					}
					
					
					//See if this is a "dataView" type
					if (newItem.attributeValue("type", "").equals("dataView")) {
						//This item is shadowing one of the form data items. Capture its form item name
						Element newItemNameProperty = (Element) newItem.selectSingleNode("./properties/property[@name='name']");
						if (newItemNameProperty != null) {
							String newItemNamePropertyValue = newItemNameProperty.attributeValue("value", "");
							if (!newItemNamePropertyValue.equals("")) {
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
											String shodowItemName = formItemProperty.getParent().getParent().attributeValue("name", "");
											newItem.addAttribute("formItem", shodowItemName);
										}
									}
								}
							}
						}
					}
					int nextItemId = Integer.valueOf(root.attributeValue("nextId")).intValue();;
					nextItemId = populateNewDefinitionTree(itemEleToAdd, newItem, configRoot, nextItemId, false);
					root.addAttribute("nextId", Integer.toString(nextItemId));
				}
			}
		}
		return newItem;
	}
	
	private void processProperties(String defId, Element configEle, Element newItem, Map formData) {
		//Check to see if there are new attributes from the config file that should be copied into the definition
		Iterator itAttributes = configEle.attributeIterator();
		while (itAttributes.hasNext()) {
			Attribute attr = (Attribute) itAttributes.next();
			//If the attribute does not exist in the new item, copy it from the config file
			if (newItem.attributeValue(attr.getName()) == null) 
				newItem.addAttribute(attr.getName(), attr.getValue());
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
				if (formData.containsKey("propertyId_"+attrName)) {
					String[] values = (String[]) formData.get("propertyId_"+attrName);
					for (int i = 0; i < values.length; i++) { 
						String value = values[i];
						if (!characterMask.equals("")) {
							//See if the user entered a valid name
							if (!value.equals("") && !value.matches(characterMask)) {
								//The value is not well formed, go complain to the user
								throw new DefinitionInvalidException(defId, NLT.get("definition.error.invalidCharacter") + " - " + value);
							}
						}
						Element newPropertyEle = configProperty.createCopy();
						newPropertiesEle.add(newPropertyEle);
						if (type.equals("text")) {
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("textarea")) {
							newPropertyEle.setText(value);
						} else if (type.equals("integer")) {
							if (value.matches("[^0-9]")) {
								//The value is not a valid integer
								throw new DefinitionInvalidException(defId, NLT.get("definition.error.notAnInteger") + " (" +configProperty.attributeValue("caption") + ")");
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("selectbox") || type.equals("itemSelect") || 
								type.equals("replyStyle") || type.equals("iconList")) {
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("boolean") || type.equals("checkbox")) {
							if (value == null) {value = "false";}
							if (value.equalsIgnoreCase("on")) {
								value = "true";
							} else {
								value = "false";
							}
							newPropertyEle.addAttribute("value", value);
						}
					}
				} else {
					if (type.equals("boolean") || type.equals("checkbox")) {
						String value = "false";
						Element newPropertyEle = configProperty.createCopy();
						newPropertiesEle.add(newPropertyEle);
						newPropertyEle.addAttribute("value", value);
					}
				}
			}
		}		
	}
	
	public void modifyItem(String defId, String itemId, Map formData) throws DefinitionInvalidException {
		Definition def = getCoreDao().loadDefinition(defId, RequestContextHolder.getRequestContext().getZoneName());
		Document definitionTree = def.getDefinition();
		this.getDefinitionConfig();
		Element configRoot = this.definitionConfig.getRootElement();

		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to modify
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				Element itemNameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
				String itemNamePropertyValue = "";
				if (itemNameProperty != null) itemNamePropertyValue = itemNameProperty.attributeValue("value", "");

				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				Map uniqueNames = getUniqueNameMap(configRoot, root, itemType);
				if (formData.containsKey("propertyId_name")) {
					String name = ((String[]) formData.get("propertyId_name"))[0];
					//See if the item name is being changed
					if (!name.equals("") && 
							!name.equals(itemNamePropertyValue) && 
							uniqueNames.containsKey(name)) {
						//This name is not unique
						throw new DefinitionInvalidException(defId, NLT.get("definition.error.nameNotUnique")+" ("+name+")");
					} else if (!name.equals("") && !name.equals(itemNamePropertyValue)) {
						//The name is being changed. Check if this is a workflow state
						if (item.getParent().attributeValue("name", "").equals("workflowProcess") && 
								item.attributeValue("name", "").equals("state")) {
							//This is a workflow state. Make sure no entries are using that state
							//TODO ???Add code to check if any entries are in this state
							//  If code is added to support changing state names, make sure to fix up
							//  the toState property, the initialState property, and the startState property.
							throw new DefinitionInvalidException(defId, 
									"Error: this state name cannot be changed because some entries are in this state.");
						}
					}
				}
				Element itemTypeEle = (Element) configRoot.selectSingleNode("item[@name='"+itemType+"']");
				if (itemTypeEle != null) {
					//Set the values of each property from the form data
					processProperties(defId, itemTypeEle, item, formData);
										
					//See if this is a "dataView" type
					if (item.attributeValue("type", "").equals("dataView")) {
						//This item is shadowing one of the form data items. Capture its form item name
						itemNameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
						if (itemNameProperty != null) {
							itemNamePropertyValue = itemNameProperty.attributeValue("value", "");
							if (!itemNamePropertyValue.equals("")) {
								//Find the form item with this name
								Iterator itFormItems = root.selectNodes("//item/properties/property[@value='"+itemNamePropertyValue+"']").iterator();
								while (itFormItems.hasNext()) {
									//Look for the entryForm item with a "name" property
									Element formItemProperty = (Element) itFormItems.next();
									if (formItemProperty.attributeValue("name", "").equals("name")) {
										//This is a "name" property. Now see if it under the "form" tree
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
											String shodowItemName = formItemProperty.getParent().getParent().attributeValue("name", "");
											item.addAttribute("formItem", shodowItemName);
										}
									}
								}
							}
						}
					}
					setDefinition(def, definitionTree);
				}
			}
		}
	}
	
	public void deleteItem(String defId, String itemId) throws DefinitionInvalidException {
		Definition def = getCoreDao().loadDefinition(defId, RequestContextHolder.getRequestContext().getZoneName());
		this.getDefinitionConfig();
		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to delete
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				if (item.getParent().attributeValue("name", "").equals("workflowProcess") && 
						item.attributeValue("name", "").equals("state")) {
					//This is a workflow state. Make sure no entries are using that state
					//TODO ???Add code to check if any entries are in this state
					throw new DefinitionInvalidException(defId, 
							"Error: this state name cannot be deleted because some entries are in this state.");
				}

				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				Element itemTypeEle = (Element) this.definitionConfig.getRootElement().selectSingleNode("item[@name='"+itemType+"']");
				//Check that this element is allowed to be deleted
				if (itemTypeEle == null || (itemTypeEle != null && !itemTypeEle.attributeValue("canBeDeleted", "true").equalsIgnoreCase("false"))) {
					Element parent = item.getParent();
					//Delete the item from the definition tree
					item.detach();
					//Check to make sure there are any items marked as "cannot be deleted"
					Iterator itItems = item.selectNodes("./descendant::item").listIterator();
					if (itItems != null) {
						while (itItems.hasNext()) {
							Element item2 = (Element) itItems.next();
							String itemType2 = item2.attributeValue("name", "");
							Element itemTypeEle2 = (Element) this.definitionConfig.getRootElement().selectSingleNode("item[@name='"+itemType2+"']");
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

	public void moveItem(String defId, String sourceItemId, String targetItemId, String position) throws DefinitionInvalidException {
		Definition def = getCoreDao().loadDefinition(defId, RequestContextHolder.getRequestContext().getZoneName());
		if (!sourceItemId.equals(targetItemId.toString())) {
			this.getDefinitionConfig();
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
							if (!sourceItemType.equals("") && checkTargetOptions(targetItem.attributeValue("name"), sourceItemType)) {
								//Detach the source item
								sourceItem.detach();
								//Add it to the target element
								targetItem.add(sourceItem);
							} else {
								//The target item is not designed to accept this item as a child
								throw new DefinitionInvalidException(defId, NLT.get("definition.error.illegalMoveInto"));
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
							List sourceParentContent2 = sourceParent.content();
						}
						//Write the new document back into the definition
						setDefinition(def, definitionTree);
					} else {
						//Target item is no longer defined as a valid item
						throw new DefinitionInvalidException(defId, NLT.get("definition.error.noElement"));
					}
				} else {
					//The item to be moved is no longer defined as a valid item
					throw new DefinitionInvalidException(defId, NLT.get("definition.error.noElement"));
				}
			}
		}
	}
	
	//Routine to check that the source item is allowed to be added to the target item type
	private boolean checkTargetOptions(String targetItemType, String sourceItemType) {
		Element configRoot = definitionConfig.getRootElement();
		Element targetItem = (Element) configRoot.selectSingleNode("item[@name='"+targetItemType+"']");
		Element sourceItem = (Element) configRoot.selectSingleNode("item[@name='"+sourceItemType+"']");
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
				Iterator itOptionsSelect = configRoot.selectNodes(optionPath).iterator();
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

	private int populateNewDefinitionTree(Element source, Element target, Element configRoot, int id, boolean includeDefault) {
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
				item.addAttribute("name", (String)nextOption.attributeValue("name"));
				Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+nextOption.attributeValue("name")+"']");
				if (itemElement == null) {continue;}
				//Copy all of the attributes that should be in the definition
				String caption = itemElement.attributeValue("caption", nextOption.attributeValue("name"));
				item.addAttribute("caption", caption);
				String itemType = itemElement.attributeValue("type", "");
				if (!itemType.equals("")) item.addAttribute("type", itemType);
				item.addAttribute("id", Integer.toString(id));
				
				//Get the properties to be copied
				List itemElementList = itemElement.elements("properties");
				if (!itemElementList.isEmpty()) {
					Element itemProperties = (Element) itemElementList.get(0);
					item.add(itemProperties.createCopy());
				}

				//Get the jsps to be copied
				itemElementList = itemElement.elements("jsps");
				if (!itemElementList.isEmpty()) {
					Element itemJsps = (Element) itemElementList.get(0);
					item.add(itemJsps.createCopy());
				}

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
					
					//Get the properties to be copied
					List itemElementList = itemElement.elements("properties");
					if (!itemElementList.isEmpty()) {
						Element itemProperties = (Element) itemElementList.get(0);
						item.add(itemProperties.createCopy());
					}
					
					//Get the jsps to be copied
					itemElementList = itemElement.elements("jsps");
					if (!itemElementList.isEmpty()) {
						Element itemJsps = (Element) itemElementList.get(0);
						item.add(itemJsps.createCopy());
					}
					
					//Bump up the unique id
					id++;
					
					//Now see if this item has some required options of its own
					id = populateNewDefinitionTree(itemElement, item, configRoot, id, includeDefault);
				}
			}
		}
		return id;
	}
	
	private Map getUniqueNameMap(Element configRoot, Element definitionTree, String itemType) {
		Map uniqueNames = new HashMap();

		Element itemTypeEle = (Element) configRoot.selectSingleNode("item[@name='"+itemType+"']");
		if (itemTypeEle != null) {
			Element itemTypeName = (Element) itemTypeEle.selectSingleNode("properties/property[@name='name']");
			if (itemTypeName != null) {
				//See if this item requires a unique name
				String uniquePath = itemTypeName.attributeValue("unique", "");
				if (!uniquePath.equals("")) {
					//There is a request for uniqueness, so get the list from the definition file
					Iterator itNames = definitionTree.selectNodes(uniquePath).iterator();
					while (itNames.hasNext()) {
						//Find the name property of all items in the specified path
						Element itemEleName = (Element)((Element) itNames.next()).selectSingleNode("properties/property[@name='name']");
						String itemEleNameValue = itemEleName.attributeValue("value", "");
						if (!itemEleNameValue.equals("")) {
							//We found a name, so add it to the map
							uniqueNames.put(itemEleNameValue, itemEleNameValue);
						}
					}
				}
			}
		}
		return uniqueNames;
	}

	
    public Document getDefinitionConfig() {
    	//if (this.definitionConfig == null) {
	    	try {
	    		//TODO - Fix this to use a file in the html tree
	    		this.definitionConfig = definitionBuilderConfig.getAsMergedDom4jDocument();
	    	} catch (Exception fe) {
	    		fe.printStackTrace();
	    	}
    	//}
    	return this.definitionConfig;
    }
    
    public Map getEntryData(Document definitionTree, InputDataAccessor inputData, Map fileItems) {
		this.getDefinitionConfig();
		//Get the base configuration definition file root (i.e., not the entry's definition file)
		Element configRoot = this.definitionConfig.getRootElement();
		
    	// entryData will contain the Map of entry data as gleaned from the input data
		Map entryDataAll = new HashMap();
		Map entryData = new HashMap();
		List fileData = new ArrayList();
		entryDataAll.put("entryData", entryData);
		entryDataAll.put("fileData", fileData);
		
		if (definitionTree != null) {
			if (definitionTree != null) {
				//root is the root of the entry's definition
				Element root = definitionTree.getRootElement();
				
				//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
				Element entryFormItem = (Element)root.selectSingleNode("item[@type='form' or @name='entryForm' or @name='profileEntryForm']");
				if (entryFormItem != null) {
					//While going through the entry's elements, keep track of the current form name (needed to process date elements)
					Iterator itItems = entryFormItem.selectNodes(".//item").listIterator();
					while (itItems.hasNext()) {
						Element nextItem = (Element) itItems.next();
						String itemName = (String) nextItem.attributeValue("name", "");
						
						//Get the form element name (property name)
						Element nameProperty = (Element) nextItem.selectSingleNode("./properties/property[@name='name']");
						if (nameProperty != null) {
							//Find the item in the base configuration definition to see if it is a data item
							Element configItem = (Element) configRoot.selectSingleNode("//item[@name='" + itemName + "']");
							if (configItem != null) {
								if (configItem.attributeValue("type", "").equals("data")) {
									String nameValue = nameProperty.attributeValue("value", "");									
									if (nameValue.equals("")) {nameValue = nextItem.attributeValue("name");}
									
									//We have the element name, see if it has a value in the input data
									if (itemName.equals("description") || itemName.equals("htmlEditorTextarea")) {
										//Use the helper routine to parse the date into a date object
										Description description = new Description();
										if (inputData.exists(nameValue)) {
											description.setText(inputData.getSingleValue(nameValue));
											description.setFormat(Description.FORMAT_HTML);
											entryData.put(nameValue, description);
										}
									} else if (itemName.equals("date")) {
										//Use the helper routine to parse the date into a date object
										Date date = DateHelper.getDateFromInput(inputData, nameValue);
										if (date != null) {entryData.put(nameValue, date);}
									} else if (itemName.equals("event")) {
									    //Ditto for event helper routine
									    Element hasDurElem = (Element) nextItem.selectSingleNode("./properties/property[@name='hasDuration']");
									    Boolean hasDur = new Boolean(true);
									    if (hasDurElem != null && hasDurElem.attributeValue("value", "").equals("false")) {
									        hasDur = Boolean.FALSE;
									    }
									    Element hasRecurElem = (Element) nextItem.selectSingleNode("./properties/property[@name='hasRecurrence']");
									    Boolean hasRecur = new Boolean(true);
									    if (hasRecurElem != null && hasRecurElem.attributeValue("value", "").equals("false")) {
									        hasRecur = Boolean.FALSE;
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
									} else if (itemName.equals("selectbox")) {
										if (inputData.exists(nameValue)) entryData.put(nameValue, inputData.getValues(nameValue));
									} else if (itemName.equals("checkbox")) {
										if (inputData.exists(nameValue) && inputData.getSingleValue(nameValue).equals("on")) {
											entryData.put(nameValue, new Boolean(true));
										} else {
											entryData.put(nameValue, new Boolean(false));
										}
									} else if (itemName.equals("file") || itemName.equals("graphic")) {
									    if(fileItems != null && fileItems.containsKey(nameValue)) {
									    	MultipartFile myFile = (MultipartFile)fileItems.get(nameValue);
									    	String fileName = myFile.getOriginalFilename();
									    	if (fileName.equals("")) continue;
	
									    	Element storageElem = (Element) nextItem.selectSingleNode("./properties/property[@name='storage']");
									    	String repositoryServiceName = storageElem.attributeValue("value",
									    			RepositoryServiceUtil.getDefaultRepositoryServiceName());
									    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_FILE, nameValue, myFile, repositoryServiceName);
	
									    	//See if there is a scaling request for this graphic file. If yes, pass along the hieght and width
									    	Element maxWidthEle = (Element) nextItem.selectSingleNode("./properties/property[@name='maxWidth']");
									    	if (maxWidthEle != null) {
									    		String maxWidth = maxWidthEle.attributeValue("value", "");
									    		if (!maxWidth.equals("")) {
									    			fui.setMaxWidth(Integer.parseInt(maxWidth));
									    		}
									    	}
									    	Element maxHeightEle = (Element) nextItem.selectSingleNode("./properties/property[@name='maxHeight']");
									    	if (maxHeightEle != null) {
									    		String maxHeight = maxHeightEle.attributeValue("value", "");
									    		if (!maxHeight.equals("")) {
									    			fui.setMaxHeight(Integer.parseInt(maxHeight));
									    		}
									    	}
									    	// TODO The following piece of code may need a better conditional
									    	// statement than this, since we probably do not want to generate
									    	// thumbnails for all graphic-type file uploads. Or do we? 
									    	if(itemName.equals("graphic")) {
									    		fui.setGenerateThumbnail(true);
									    		fui.setThumbnailDirectlyAccessible(true);
									    	}
									    	
									    	fileData.add(fui);
										}
									} else if (itemName.equals("attachFiles")) {
									    if(fileItems != null) {
											int number = 1;
											Element attachmentNumber = (Element) nextItem.selectSingleNode("./properties/property[@name='number']");
											if (attachmentNumber != null) {
												if (!attachmentNumber.attributeValue("value", "").equals("")) {
													number = Integer.parseInt(attachmentNumber.attributeValue("value"));
												}
											}
											for (int i=1;i <= number;i++) {
												String fileEleName = nameValue + Integer.toString(i);
												if (fileItems.containsKey(fileEleName)) {												
											    	MultipartFile myFile = (MultipartFile)fileItems.get(fileEleName);
											    	String fileName = myFile.getOriginalFilename();
											    	if (fileName.equals("")) continue;
											    	// Different repository can be specified for each file uploaded.
											    	// If not specified, use the statically selected one.  
											    	String repositoryServiceName = null;
											    	if (inputData.exists(nameValue + "_repos" + Integer.toString(i))) 
											    		repositoryServiceName = inputData.getSingleValue(nameValue + "_repos" + Integer.toString(i));
											    	if (repositoryServiceName == null) {
												    	Element storageElem = (Element) nextItem.selectSingleNode("./properties/property[@name='storage']");
												    	repositoryServiceName = storageElem.attributeValue("value",
												    			RepositoryServiceUtil.getDefaultRepositoryServiceName());
											    	}
											    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryServiceName);
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
					}
				}
			}
		}
   	
    	return entryDataAll;
    }
    public List getDefinitions() {
    	String companyId = RequestContextHolder.getRequestContext().getZoneName();
    	List defs = coreDao.loadDefinitions(companyId);
    	return defs;
    }
    
	public void addIndexFieldsForEntity(
            org.apache.lucene.document.Document indexDoc,
            DefinableEntity entry) {

        Element configRoot = getDefinitionConfig().getRootElement();
        Definition def = entry.getEntryDef();
        if (def != null) {
	        Field[] fields;
	
	        Document definitionTree = def.getDefinition();
	        if (definitionTree != null) {
	            Element root = definitionTree.getRootElement();
	
	            //Get a list of all of the items in the definition
				Element entryFormItem = (Element) 
					root.selectSingleNode("item[@type='form' or @name='entryForm' or @name='profileEntryForm']");
	            if (entryFormItem != null) {
	                Iterator itItems = entryFormItem.selectNodes("//item")
	                        .listIterator();
	                if (itItems != null) {
	                    while (itItems.hasNext()) {
	                        Element nextItem = (Element) itItems.next();
	
	                        //Get the form element name (property name)
	                        Element nameProperty = (Element) nextItem
	                                .selectSingleNode("./properties/property[@name='name']");
	                        if (nameProperty != null) {
	                            //Find the item in the configuration definition
	                            // to see if it is a data item
	                            String itemName = (String) nextItem
	                                    .attributeValue("name");
	                            Element configItem = (Element) configRoot
	                                    .selectSingleNode("//item[@name='"
	                                            + itemName + "']");
	                            if (configItem != null) {
	                                if (configItem.attributeValue("type", "")
	                                        .equals("data")) {
	                                    String nameValue = nameProperty
	                                            .attributeValue("value", "");
	                                    if (nameValue.equals("")) {
	                                        nameValue = nextItem
	                                                .attributeValue("name");
	                                    }
	
	                                    boolean applyIndexing = false;
	
	                                    Element indexingElem = (Element) nextItem
	                                            .selectSingleNode("./index");
	                                    if (indexingElem == null) {
	                                        // The current item in the entry
	                                        // definition does not contain
	                                        // indexing information. Check the
	                                        // corresponding item in the default
	                                        // config definition to see if it
	                                        // has it.
	                                        // This two level mechanism allows
	                                        // entry definition (more specific
	                                        // one) to override the settings
	                                        // in the default config definition
	                                        // (more general one). This
	                                        // overriding
	                                        // works in its entirity only, that
	                                        // is, partial overriding is not
	                                        // supported.
	                                        indexingElem = (Element) configItem
	                                                .selectSingleNode("./index");
	                                    }
	
	                                    if (indexingElem == null)
	                                        continue;
	
	                                    if (indexingElem.attributeValue("apply")
	                                            .equals("true"))
	                                        applyIndexing = true;
	
	                                    if (!applyIndexing)
	                                        continue;
	
	                                    String fieldBuilder = indexingElem
	                                            .attributeValue("fieldBuilder");
	                                    Map indexingArgs = getOptionalArgs(indexingElem);
	                                    fields = FieldBuilderUtil.buildField(entry,
	                                            nameValue, fieldBuilder,
	                                            indexingArgs);
	                                    if (fields != null) {
	                                        for (int i = 0; i < fields.length; i++) {
	                                            indexDoc.add(fields[i]);
	                                        }
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
        }
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
	public void addNotifyElementForEntity(Element element, Notify notifyDef, 
			DefinableEntity entry) {

        Element configRoot = getDefinitionConfig().getRootElement();
        Definition def = entry.getEntryDef();

        Document definitionTree = def.getDefinition();
        if (definitionTree != null) {
            Element root = definitionTree.getRootElement();

            //Get a list of all of the items in the definition
			Element entryFormItem = (Element) 
				root.selectSingleNode("item[@type='form' or @name='entryForm' or @name='profileEntryForm']");
            if (entryFormItem != null) {
                Iterator itItems = entryFormItem.selectNodes("//item")
                        .listIterator();
                if (itItems != null) {
                    while (itItems.hasNext()) {
                        Element nextItem = (Element) itItems.next();

                        //Get the form element name (property name)
                        Element nameProperty = (Element) nextItem
                                .selectSingleNode("./properties/property[@name='name']");
                        if (nameProperty != null) {
                            //Find the item in the configuration definition
                            // to see if it is a data item
                            String itemName = (String) nextItem
                                    .attributeValue("name");
                            Element configItem = (Element) configRoot
                                    .selectSingleNode("//item[@name='"
                                            + itemName + "']");
                            if (configItem != null) {
                            	if (configItem.attributeValue("type", "").equals("data")) {
                                    String nameValue = nameProperty
                                            .attributeValue("value", "");
                                    if (nameValue.equals("")) {
                                        nameValue = nextItem
                                                .attributeValue("name");
                                    }

                                    boolean applyNotify = false;

                                    Element notifyElem = (Element) nextItem
                                            .selectSingleNode("./notify");
                                    if (notifyElem == null) {
                                        // The current item in the entry
                                        // definition does not contain
                                        // indexing information. Check the
                                        // corresponding item in the default
                                        // config definition to see if it
                                        // has it.
                                        // This two level mechanism allows
                                        // entry definition (more specific
                                        // one) to override the settings
                                        // in the default config definition
                                        // (more general one). This
                                        // overriding
                                        // works in its entirity only, that
                                        // is, partial overriding is not
                                        // supported.
                                    	notifyElem = (Element) configItem
                                                .selectSingleNode("./notify");
                                    }

                                    if (notifyElem == null)
                                        continue;

                                    if (notifyDef.isFull() && notifyElem.attributeValue("full")
                                            .equals("true"))
                                        applyNotify = true;
                                    else if (notifyDef.isSummary() && notifyElem.attributeValue("summary")
                                            .equals("true"))
                                        applyNotify = true;

                                    if (!applyNotify)
                                        continue;

                                    String fieldBuilder = notifyElem
                                            .attributeValue("notifyBuilder");
                                    
                               
                                    Map notifyArgs = getOptionalArgs(notifyElem);
                                    String captionValue = null;
                                    if (!notifyArgs.containsKey("caption")) {
                                    	Element captionProperty = (Element) nextItem
                                    		.selectSingleNode("./properties/property[@name='caption']");
                                    	captionValue = captionProperty
                                    		.attributeValue("value", "");
                                    	if (captionValue.equals("")) {
                                    		captionValue = nextItem
                                    			.attributeValue("caption");
                                    	} 
                                    }
                                    else {
                                    	captionValue = (String) notifyArgs.get("caption");
                                    }
                                	notifyArgs.put("_caption", NLT.getDef(captionValue));
                                	notifyArgs.put("_itemName", itemName);
                                    NotifyBuilderUtil.buildElement(element, notifyDef, entry,
                                            nameValue, fieldBuilder,
                                            notifyArgs);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
        
    private boolean matchCategory(String value, String[] categories) {
    	for(int i = 0; i < categories.length; i++) {
    		if(categories[i].equals(value))
    			return true; // match
    	}
    	return false; // no match
    }
    
	//Routine to get the data elements for use in search queries
    public Map getEntryDefinitionElements(String id) {
		//Get a map for the results
    	Map dataElements = new TreeMap();
		
		Definition def = getDefinition(id);
		this.getDefinitionConfig();
		//Get the base configuration definition file root (i.e., not the entry's definition file)
		Element configRoot = this.definitionConfig.getRootElement();
		
		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			//root is the root of the entry's definition
			Element root = definitionTree.getRootElement();
			
			//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form' or @name='entryForm' or @name='profileEntryForm']");
			if (entryFormItem != null) {
				Iterator itItems = entryFormItem.selectNodes(".//item").listIterator();
				while (itItems.hasNext()) {
					//Get a map to store the results in
					Map itemData = new HashMap();
					
					Element nextItem = (Element) itItems.next();
					String itemName = (String) nextItem.attributeValue("name", "");
					itemData.put("type", itemName);
					
					//Get the element name (property name)
					Element nameProperty = (Element) nextItem.selectSingleNode("./properties/property[@name='name']");
					Element captionProperty = (Element) nextItem.selectSingleNode("./properties/property[@name='caption']");
					if (nameProperty != null && captionProperty != null) {
						//Find the item in the base configuration definition to see if it is a data item
						Element configItem = (Element) configRoot.selectSingleNode("//item[@name='" + itemName + "']");
						if (configItem != null) {
							if (configItem.attributeValue("type", "").equals("data")) {
								String nameValue = nameProperty.attributeValue("value", "");									
								if (nameValue.equals("")) {nameValue = nextItem.attributeValue("name");}
								String captionValue = captionProperty.attributeValue("value", "");									
								if (captionValue.equals("")) {captionValue = nameValue;}
								itemData.put("caption", NLT.getDef(captionValue));
								
								//We have the element name, see if it has option values
								if (itemName.equals("selectbox")) {
									Map valueMap = new TreeMap();
									Iterator itSelectionItems = nextItem.selectNodes("item[@name='selectboxSelection']").iterator();
									while (itSelectionItems.hasNext()) {
										Element selection = (Element) itSelectionItems.next();
										//Get the element name (property name)
										Element selectionNameProperty = (Element) selection.selectSingleNode("./properties/property[@name='name']");
										Element selectionCaptionProperty = (Element) selection.selectSingleNode("./properties/property[@name='caption']");
										if (selectionNameProperty != null && selectionCaptionProperty != null) {
											String selectionNameValue = selectionNameProperty.attributeValue("value", "");									
											if (!selectionNameValue.equals("")) {
												String selectionCaptionValue = selectionCaptionProperty.attributeValue("value", "");									
												if (selectionCaptionValue.equals("")) {selectionCaptionValue = selectionNameValue;}
												valueMap.put(selectionNameValue, NLT.getDef(selectionCaptionValue));
											}
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
										Element selectionNameProperty = (Element) selection.selectSingleNode("./properties/property[@name='name']");
										Element selectionCaptionProperty = (Element) selection.selectSingleNode("./properties/property[@name='caption']");
										if (selectionNameProperty != null && selectionCaptionProperty != null) {
											String selectionNameValue = selectionNameProperty.attributeValue("value", "");									
											if (!selectionNameValue.equals("")) {
												String selectionCaptionValue = selectionCaptionProperty.attributeValue("value", "");									
												if (selectionCaptionValue.equals("")) {selectionCaptionValue = selectionNameValue;}
												valueMap.put(selectionNameValue, NLT.getDef(selectionCaptionValue));
											}
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
				}
			}
		}
   	
    	return dataElements;
    }

}
