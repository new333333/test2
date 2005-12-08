package com.sitescape.ef.module.definition.impl;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.module.impl.AbstractModuleImpl;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.definition.index.FieldBuilderUtil;
import com.sitescape.ef.repository.RepositoryServiceNames;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.util.FileUploadItem;
import com.sitescape.ef.util.MergeableXmlClassPathConfigFiles;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Description;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FileItem;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DateHelper;
import com.sitescape.ef.web.util.EventHelper;

import org.apache.lucene.document.Field;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.sitescape.ef.domain.DefinitionInvalidException;

/**
 * @author hurley
 *
 */
public class DefinitionModuleImpl extends AbstractModuleImpl implements DefinitionModule {
	private Document definitionConfig;
	private MergeableXmlClassPathConfigFiles definitionBuilderConfig;
	    
	public Definition getDefinition(String id) {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
 		return coreDao.loadDefinition(id, companyId);
	}
	
	public MergeableXmlClassPathConfigFiles getDefinitionBuilderConfig() {
        return definitionBuilderConfig;
    }
    public void setDefinitionBuilderConfig(MergeableXmlClassPathConfigFiles definitionBuilderConfig) {
        this.definitionBuilderConfig = definitionBuilderConfig;
    }
    
	public Definition addDefinition(String name, String title, int type, Map formData) {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();

		Definition newDefinition = new Definition();
		newDefinition.setName(name);
		newDefinition.setTitle(title);
		newDefinition.setType(type);
		newDefinition.setZoneName(companyId);
		newDefinition.setDefintion(getDefaultDefinition(name, title, type, formData));
		coreDao.save(newDefinition);
		return newDefinition;
	}
	
	public void modifyDefinitionName(String id, String name, String title) {
		Definition def = getDefinition(id);
		if (def != null) {
			
			//Also store the name and title in the definition document
			Document defDoc = def.getDefinition();
			defDoc.getRootElement().addAttribute("name", name);
			defDoc.getRootElement().addAttribute("caption", title);
			def.setDefintion(defDoc);
			//set name after we get definition, so file will exist
			def.setName(name);
			def.setTitle(title);

		}
	}
	
	public void modifyDefinitionAttribute(String id, String key, String value) {
		Definition def = getDefinition(id);
		//Store this attribute in the definition document
		Document defDoc = def.getDefinition();
		defDoc.getRootElement().addAttribute(key, value);
		def.setDefintion(defDoc);
	}
	
	public void modifyDefinitionProperties(String id, Map formData) {
		Definition def = getDefinition(id);
		if (def != null) {			
			//Store the properties in the definition document
			Document defDoc = def.getDefinition();
			this.getDefinitionConfig();
			Element configRoot = definitionConfig.getRootElement();
			String type = String.valueOf(def.getType());
			Element definition = (Element) configRoot.selectSingleNode("item[@definitionType='"+type+"']");
			if (definition != null) {
				//Add the properties
				processProperties(def.getId(), definition, defDoc.getRootElement(), formData);
			}
			def.setDefintion(defDoc);
		}
	}
	
	public void deleteDefinition(String id) {
		Definition def = getDefinition(id);
		coreDao.delete(def);
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
		id = populateNewDefinitionTree(definition, ntRoot, configRoot, id);
		ntRoot.addAttribute("nextId", Integer.toString(id));
		
		//Add the properties
		processProperties("0", definition, ntRoot, formData);

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
			def.setDefintion(definitionTree);
		}
		return newItem;
	}
	
	public Element addItemToDefinitionDocument(String defId, Document definitionTree, String itemId, String itemNameToAdd, Map formData) throws DefinitionInvalidException {
		this.getDefinitionConfig();
		Element configRoot = this.definitionConfig.getRootElement();
		Map uniqueNames = getUniqueNameMap(configRoot);

		Element newItem = null;
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
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
									//Look for the entryForm item with a "name" property
									Element formItemProperty = (Element) itFormItems.next();
									if (formItemProperty.attributeValue("name", "").equals("name")) {
										//This is a "name" property. Now see if it under the entryForm tree
										Element parentElement = formItemProperty.getParent();
										while (parentElement != null) {
											if (parentElement.getName().equals("item") && parentElement.attributeValue("name", "").equals("entryForm")) {
												//Found it. This item is part of the "entryForm" tree.
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
				}
			}
		}
		return newItem;
	}
	
	private void processProperties(String defId, Element configEle, Element newItem, Map formData) {
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
								throw new DefinitionInvalidException(defId, "Error: invalid character entered - "+value);
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
								throw new DefinitionInvalidException(defId, "Error: not an integer - "+configProperty.attributeValue("caption"));
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("selectbox")) {
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("boolean") || type.equals("checkbox")) {
							if (value == null) {value = "false";}
							if (value.equalsIgnoreCase("on")) {
								value = "true";
							} else {
								value = "false";
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("replyStyle")) {
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
		this.getDefinitionConfig();
		Element configRoot = this.definitionConfig.getRootElement();
		Map uniqueNames = getUniqueNameMap(configRoot);

		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to modify
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				Element itemTypeEle = (Element) configRoot.selectSingleNode("item[@name='"+itemType+"']");
				if (itemTypeEle != null) {
					//Set the values of each property from the form data
					Element configProperties = itemTypeEle.element("properties");
					Element itemProperties = item.element("properties");
					if (itemProperties == null) {
						//There is no properties element for this definition, so add one
						itemProperties = item.addElement("properties");
					}
					if (configProperties != null) {
						Iterator itConfigProperties = configProperties.elementIterator("property");
						if (itConfigProperties != null) {
							while (itConfigProperties.hasNext()) {
								Element property = (Element) itConfigProperties.next();
								String attrName = property.attributeValue("name");
								String type = property.attributeValue("type", "");
								String characterMask = property.attributeValue("characterMask", "");
								Element newProperty = property.createCopy();
								if (formData.containsKey("propertyId_"+attrName)) {
									String value = ((String[]) formData.get("propertyId_"+attrName))[0];
									if (!characterMask.equals("")) {
										//See if the user entered a valid name
										if (!value.matches(characterMask)) {
											//The value is not well formed, go complain to the user
											throw new DefinitionInvalidException(defId, "Error: invalid character entered - "+value);
										}
									}
									if (type.equals("text")) {
										newProperty.addAttribute("value", value);
									} else if (type.equals("textarea")) {
										newProperty.setText(value);
									} else if (type.equals("integer")) {
										if (!value.matches("^[0-9]*$")) {
											//The item to be moved is no longer defined as a valid item
											throw new DefinitionInvalidException(defId, "error: not an integer - "+property.attributeValue("caption"));
										}
										newProperty.addAttribute("value", value);
									} else if (type.equals("selectbox")) {
										newProperty.addAttribute("value", value);
									} else if (type.equals("boolean") || type.equals("checkbox")) {
										if (value == null) {value = "false";}
										if (value.equalsIgnoreCase("on")) {
											value = "true";
										} else {
											value = "false";
										}
										newProperty.addAttribute("value", value);
									} else {
										newProperty.addAttribute("value", "");
									}

								} else {
									if (type.equals("boolean") || type.equals("checkbox")) {
										newProperty.addAttribute("value", "false");
									} else {
										newProperty.addAttribute("value", "");
									}
								}
								//Replace the old version of this property in the item being modified
								Element oldProperty = (Element) itemProperties.selectSingleNode("property[@name='"+attrName+"']");
								if (oldProperty != null) {
									//There is an old version of this property, delete it
									oldProperty.detach();
								}
								//Add the new version of the property
								itemProperties.add(newProperty);
							}
						}
					}
					
					//See if this is a "dataView" type
					if (item.attributeValue("type", "").equals("dataView")) {
						//This item is shadowing one of the form data items. Capture its form item name
						Element itemNameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
						if (itemNameProperty != null) {
							String itemNamePropertyValue = itemNameProperty.attributeValue("value", "");
							if (!itemNamePropertyValue.equals("")) {
								//Find the form item with this name
								Iterator itFormItems = root.selectNodes("//item/properties/property[@value='"+itemNamePropertyValue+"']").iterator();
								while (itFormItems.hasNext()) {
									//Look for the entryForm item with a "name" property
									Element formItemProperty = (Element) itFormItems.next();
									if (formItemProperty.attributeValue("name", "").equals("name")) {
										//This is a "name" property. Now see if it under the entryForm tree
										Element parentElement = formItemProperty.getParent();
										while (parentElement != null) {
											if (parentElement.getName().equals("item") && parentElement.attributeValue("name", "").equals("entryForm")) {
												//Found it. This item is part of the "entryForm" tree.
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
					
					def.setDefintion(definitionTree);
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
					def.setDefintion(definitionTree);
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
							if (checkTargetOptions(targetItem.attributeValue("name"), sourceItem.attributeValue("name"))) {
								//Detach the source item
								sourceItem.detach();
								//Add it to the target element
								targetItem.add(sourceItem);
							} else {
								//The target item is not designed to accept this item as a child
								throw new DefinitionInvalidException(defId, "error");
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
						def.setDefintion(definitionTree);
					} else {
						//Target item is no longer defined as a valid item
						throw new DefinitionInvalidException(defId, "error");
					}
				} else {
					//The item to be moved is no longer defined as a valid item
					throw new DefinitionInvalidException(defId, "error");
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

	private int populateNewDefinitionTree(Element source, Element target, Element configRoot, int id) {
		//See if the source has any options that are required
		try {
			Element options = source.element("options");
			Iterator iOptions = options.selectNodes("option|option_select").iterator();
			while (iOptions.hasNext()) {
				Element nextOption = (Element)iOptions.next();
				if (nextOption.attributeValue("initial", "").equals("true")) {
					//This option is required. Copy it to the target
					Element item = target.addElement("item");
					item.addAttribute("name", (String)nextOption.attributeValue("name"));
					Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+nextOption.attributeValue("name")+"']");
					if (itemElement == null) {continue;}
					String caption = itemElement.attributeValue("caption", nextOption.attributeValue("name"));
					item.addAttribute("caption", caption);
					item.addAttribute("id", Integer.toString(id));
					
					//Get the properties to be copied
					List itemElementList = itemElement.elements("properties");
					if (!itemElementList.isEmpty()) {
						Element itemProperties = (Element) itemElementList.get(0);
						item.add(itemProperties.createCopy());
					}
					
					//Bump up the unique id
					id++;
					
					//Now see if this item has some required options of its own
					id = populateNewDefinitionTree(itemElement, item, configRoot, id);
				}
			}
		}
		catch (Exception e) {
			//If there are no options, just return
			return id;
		}
		return id;
	}
	
	private Map getUniqueNameMap(Element configRoot) {
		Map uniqueNames = new HashMap();
		
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
    
    public Map getEntryData(Definition def, Map inputData, Map fileItems) {
		this.getDefinitionConfig();
		Element configRoot = this.definitionConfig.getRootElement();
		
    	// entryData will contain the Map of entry data as gleaned from the input data
		Map entryDataAll = new HashMap();
		Map entryData = new HashMap();
		List fileData = new ArrayList();
		entryDataAll.put("entryData", entryData);
		entryDataAll.put("fileData", fileData);
	//	entryDataAll.put("eventData", eventData);
		
		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			
			//Get a list of all of the items in the definition
			Element entryFormItem = (Element)root.selectSingleNode("item[@name='entryForm']");
			if (entryFormItem != null) {
				//Wile going through the elements, keep track of the current form name (needed to process date elements)
				String currentFormName = "";
				Iterator itItems = entryFormItem.selectNodes(".//item").listIterator();
				while (itItems.hasNext()) {
					Element nextItem = (Element) itItems.next();
					String itemName = (String) nextItem.attributeValue("name", "");
					
					//Get the form element name (property name)
					Element nameProperty = (Element) nextItem.selectSingleNode("./properties/property[@name='name']");
					if (nameProperty != null) {
						//See if this is a form element (if so, remember its element name)
						if (itemName.equals("entryFormForm") || itemName.equals("form")) {
							currentFormName = nameProperty.attributeValue("value", "");
							if (currentFormName.equals("")) currentFormName = WebKeys.DEFINITION_DEFAULT_FORM_NAME;
						}
						//Find the item in the configuration definition to see if it is a data item
						Element configItem = (Element) configRoot.selectSingleNode("//item[@name='" + itemName + "']");
						if (configItem != null) {
							if (configItem.attributeValue("category", "").equals("entryData")) {
								String nameValue = nameProperty.attributeValue("value", "");									
								if (nameValue.equals("")) {nameValue = nextItem.attributeValue("name");}
								
								//We have the element name, see if it has a value in the input data
								if (itemName.equals("description") || itemName.equals("htmlEditorTextarea")) {
									//Use the helper routine to parse the date into a date object
									if (inputData.containsKey(nameValue)) {
										Description description = new Description();
										description.setText(((String[])inputData.get(nameValue))[0]);
										description.setFormat(Description.FORMAT_HTML);
										entryData.put(nameValue, description);
									}
								} else if (itemName.equals("date")) {
									//Use the helper routine to parse the date into a date object
									Date date = DateHelper.getDateFromMap(inputData, currentFormName, nameValue);
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
								    Event event = EventHelper.getEventFromMap(inputData, currentFormName, nameValue, hasDur, hasRecur);
								    if (event != null) {
								        event.setName(nameValue);
								        entryData.put(nameValue, event);
								    }
								} else if (itemName.equals("selectbox")) {
									if (inputData.containsKey(nameValue)) {
										entryData.put(nameValue, inputData.get(nameValue));
									}
								} else if (itemName.equals("checkbox")) {
									if (inputData.containsKey(nameValue) && ((String[])inputData.get(nameValue))[0].equals("on")) {
										entryData.put(nameValue, new Boolean(true));
									} else {
										entryData.put(nameValue, new Boolean(false));
									}
								} else if (itemName.equals("checkbox")) {
									if (inputData.containsKey(nameValue) && ((String[])inputData.get(nameValue))[0].equals("on")) {
										entryData.put(nameValue, new Boolean(true));
									} else {
										entryData.put(nameValue, new Boolean(false));
									}
								} else if (itemName.equals("file")) {
								    if(fileItems != null && fileItems.containsKey(nameValue)) {
								    	MultipartFile myFile = (MultipartFile)fileItems.get(nameValue);
								    	if (myFile.getOriginalFilename().equals("")) continue;
								    	FileUploadItem fui = new FileUploadItem(myFile);
								    	Element storageElem = (Element) nextItem.selectSingleNode("./properties/property[@name='storage']");
								    	fui.setRepositoryServiceName(storageElem.attributeValue("value", RepositoryServiceNames.FILE_REPOSITORY_SERVICE));
								    	// TODO Take care of path info?
								    	FileAttachment fAtt = new FileAttachment(nameValue);
								    	FileItem fItem = new FileItem();
								    	fItem.setName(myFile.getOriginalFilename());
								    	fItem.setLength(myFile.getSize());
								    	fAtt.setFileItem(fItem);
								    	fileData.add(fui);
								    	entryData.put(nameValue, fAtt);
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
										List fAtts = new ArrayList();
										for (int i=1;i <= number;i++) {
											String fileEleName = nameValue + Integer.toString(i);
											if (fileItems.containsKey(fileEleName)) {												
										    	MultipartFile myFile = (MultipartFile)fileItems.get(fileEleName);
										    	if (myFile.getOriginalFilename().equals("")) continue;
										    	FileUploadItem fui = new FileUploadItem(myFile);
										    	Element storageElem = (Element) nextItem.selectSingleNode("./properties/property[@name='storage']");
										    	fui.setRepositoryServiceName(storageElem.attributeValue("value", RepositoryServiceNames.FILE_REPOSITORY_SERVICE));
										    	// TODO Take care of path info?
										    	FileAttachment fAtt = new FileAttachment(fileEleName);
										    	FileItem fItem = new FileItem();
										    	fItem.setName(myFile.getOriginalFilename());
										    	fItem.setLength(myFile.getSize());
										    	fAtt.setFileItem(fItem);
										    	fileData.add(fui);
										    	fAtts.add(fAtt);
											}
										}
										if (!fAtts.isEmpty()) {
											entryData.put("attachments", fAtts);
										}
								    }
								} else {
									if (inputData.containsKey(nameValue)) {
										entryData.put(nameValue, ((String[])inputData.get(nameValue))[0]);
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
    
	public void addIndexFieldsForEntry(
            org.apache.lucene.document.Document indexDoc, Folder folder,
            Entry entry) {

        Element configRoot = getDefinitionConfig().getRootElement();
        Definition def = entry.getEntryDef();

        Field[] fields;

        Document definitionTree = def.getDefinition();
        if (definitionTree != null) {
            Element root = definitionTree.getRootElement();

            //Get a list of all of the items in the definition
            Element entryFormItem = (Element) root
                    .selectSingleNode("item[@name='entryForm']");
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
                                if (configItem.attributeValue("category", "")
                                        .equals("entryData")) {
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
                                    Map indexingArgs = getIndexingArgs(indexingElem);
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

    private Map getIndexingArgs(Element indexingElem) {
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
