package com.sitescape.ef.module.definition.impl;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.module.impl.CommonDependencyInjection;
import com.sitescape.ef.module.workflow.WorkflowModule;
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
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Workspace;

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
		setDefinition(def,doc);
		coreDao.save(def);
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
		String companyId = RequestContextHolder.getRequestContext().getZoneName();

		Definition newDefinition = new Definition();
		newDefinition.setName(name);
		newDefinition.setTitle(title);
		newDefinition.setType(type);
		newDefinition.setZoneName(companyId);
		coreDao.save(newDefinition);
		setDefinition(newDefinition, getDefaultDefinition(name, title, type, formData));
		return newDefinition;
	}
	
	public void modifyDefinitionName(String id, String name, String title) {
		Definition def = getDefinition(id);
		if (def != null) {
			
			//Store the name and title (If they are not blank) in the definition object and in the definition document
			Document defDoc = def.getDefinition();
			if (!name.equals("")) defDoc.getRootElement().addAttribute("name", name);
			if (!title.equals("")) defDoc.getRootElement().addAttribute("caption", title);
			
			//set definition name after we get definition, so definition doc file will be found before the name is changed
			if (!name.equals("")) def.setName(name);
			if (!title.equals("")) def.setTitle(title);
			setDefinition(def, defDoc);
		}
	}
	
	public void modifyDefinitionAttribute(String id, String key, String value) {
		Definition def = getDefinition(id);
		//Store this attribute in the definition document
		Document defDoc = def.getDefinition();
		defDoc.getRootElement().addAttribute(key, value);
		setDefinition(def, defDoc);
	}
	
    protected void setDefinition(Definition def, Document doc) {
    	//If this is a workflow definition, build the corresponding JBPM workflow definition
    	// Try to do this first before the changed definition gets modified on disk
    	// Thus, if an error occurs in JBPM, the user gets told before writing out the def
    	if (def.getType() == Definition.WORKFLOW) {
    		//Make sure there is actually a definition to use
    		if (def.getDefinition() == null) def.setDefintion(doc);
    		
    		//Use the definition id as the workflow process name
    		getWorkflowModule().buildProcessDefinition(def.getId(), def);
    	}
    	
    	//Write out the new definition file
    	def.setDefintion(doc);
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
		}
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
					throw new DefinitionInvalidException(defId, "Error: name not unique - "+name);
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
					int nextItemId = Integer.valueOf(root.attributeValue("nextId")).intValue();;
					nextItemId = populateNewDefinitionTree(itemEleToAdd, newItem, configRoot, nextItemId);
					root.addAttribute("nextId", Integer.toString(nextItemId));
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
						} else if (type.equals("selectbox") || type.equals("itemSelect") || type.equals("replyStyle")) {
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
				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				Map uniqueNames = getUniqueNameMap(configRoot, root, itemType);
				if (formData.containsKey("propertyId_name")) {
					String name = ((String[]) formData.get("propertyId_name"))[0];
					if (uniqueNames.containsKey(name)) {
						//This name is not unique
						throw new DefinitionInvalidException(defId, "Error: name not unique - "+name);
					}
				}
				Element itemTypeEle = (Element) configRoot.selectSingleNode("item[@name='"+itemType+"']");
				if (itemTypeEle != null) {
					//Set the values of each property from the form data
					processProperties(defId, itemTypeEle, item, formData);
										
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
						setDefinition(def, definitionTree);
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
			Iterator iOptions = options.selectNodes("option").iterator();
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
			Iterator iOptionSelects = options.selectNodes("option_select").iterator();
			while (iOptionSelects.hasNext()) {
				Element nextOptionSelect = (Element)iOptionSelects.next();
				if (nextOptionSelect.attributeValue("initial", "").equals("true")) {
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
						
						//Bump up the unique id
						id++;
						
						//Now see if this item has some required options of its own
						id = populateNewDefinitionTree(itemElement, item, configRoot, id);
					}
				}
			}
		}
		catch (Exception e) {
			//If there are no options, just return
			return id;
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
								    	String fileName = myFile.getOriginalFilename();
								    	if (fileName.equals("")) continue;
								    	
								    	Element storageElem = (Element) nextItem.selectSingleNode("./properties/property[@name='storage']");
								    	String repositoryServiceName = storageElem.attributeValue("value",
								    			RepositoryServiceUtil.getDefaultRepositoryServiceName());
								    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_FILE, nameValue, myFile, repositoryServiceName);
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
										    	
										    	Element storageElem = (Element) nextItem.selectSingleNode("./properties/property[@name='storage']");
										    	String repositoryServiceName = storageElem.attributeValue("value",
										    			RepositoryServiceUtil.getDefaultRepositoryServiceName());
										    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryServiceName);
										    	fileData.add(fui);
											}
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
            org.apache.lucene.document.Document indexDoc, Binder binder,
            Entry entry) {

        Element configRoot = getDefinitionConfig().getRootElement();
        Definition def = entry.getEntryDef();
        if (def != null) {
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
	public void addNotifyElementForEntry(Element element, Notify notifyDef, Entry entry) {

        Element configRoot = getDefinitionConfig().getRootElement();
        Definition def = entry.getEntryDef();

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
                                    if (!notifyArgs.containsKey("caption")) {
                                    	Element captionProperty = (Element) nextItem
                                    		.selectSingleNode("./properties/property[@name='caption']");
                                    	String captionValue = captionProperty
                                    		.attributeValue("value", "");
                                    	if (captionValue.equals("")) {
                                    		captionValue = nextItem
                                    			.attributeValue("caption");
                                    	} 
                                    	notifyArgs.put("_caption", NLT.getDef(captionValue));
                                    	notifyArgs.put("_itemName", itemName);
                                    }
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
}
