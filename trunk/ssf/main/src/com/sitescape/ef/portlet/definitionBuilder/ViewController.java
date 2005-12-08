package com.sitescape.ef.portlet.definitionBuilder;

import java.lang.Integer;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinitionInvalidOperation;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;

import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.util.Validator;

/**
 * @author hurley
 *
 */
public class ViewController extends SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());

		Map formData = request.getParameterMap();

		String selectedItem = "";
		if (formData.containsKey("sourceDefinitionId")) {
			selectedItem = ActionUtil.getStringValue(formData, "sourceDefinitionId");
		}
			
		//See if there is an operation to perform
		if (formData.containsKey("cancelBtn")) {
			//The operation was canceled. Go back to the top screen
				
		} else if (formData.containsKey("operation")) {
			String operation = ActionUtil.getStringValue(formData,"operation");
				
			try {
				if (operation.equals("addDefinition")) {
					//Add a new definition type
					String name = ActionUtil.getStringValue(formData,"propertyId_name");
					String caption = ActionUtil.getStringValue(formData, "propertyId_caption");
					String operationItem = ActionUtil.getStringValue(formData, "operationItem");
					Integer type = ActionUtil.getIntegerValue(formData, "definitionType_"+operationItem);
					if (!name.equals("") && type != null) {
						selectedItem = getDefinitionModule().addDefinition(name, caption, type.intValue(), formData).getId();							
					}
					
				} else if (operation.equals("modifyDefinition")) {
					//Modify the name of the selected item
					selectedItem = ActionUtil.getStringValue(formData, "selectedId");
					if (!selectedItem.equals("") ) {
						String definitionName = ActionUtil.getStringValue(formData, "propertyId_name");
						String definitionCaption = ActionUtil.getStringValue(formData,"propertyId_caption");
						getDefinitionModule().modifyDefinitionName(selectedItem, definitionName, definitionCaption);
						getDefinitionModule().modifyDefinitionProperties(selectedItem, formData);
					}
					
				} else if (operation.equals("deleteDefinition")) {
					//Delete the selected item
					selectedItem = ActionUtil.getStringValue(formData, "selectedId");
					if (!selectedItem.equals("") ) {
						try {
							getDefinitionModule().deleteDefinition(selectedItem);
						} catch(NoDefinitionByTheIdException e) {
							//If the id is already deleted, ignore the error
						}
						selectedItem = "";
					}
					
				} else if (operation.equals("addItem")) {
					selectedItem = selectedItem = ActionUtil.getStringValue(formData, "sourceDefinitionId");
					if (!selectedItem.equals("") ) {
						//Add the new item
						String itemId = ((String[])formData.get("selectedId"))[0];
						String itemToAdd = ((String[])formData.get("operationItem"))[0];
						getDefinitionModule().addItem(selectedItem, itemId, itemToAdd, formData);
					}
						
				} else if (operation.equals("modifyItem")) {
					selectedItem = selectedItem = ActionUtil.getStringValue(formData, "sourceDefinitionId");
					if (!selectedItem.equals("") ) {
						//Modify the item
						String itemId = ActionUtil.getStringValue(formData, "selectedId");
						getDefinitionModule().modifyItem(selectedItem, itemId, formData);
					}
					
				} else if (operation.equals("deleteItem")) {
					selectedItem = selectedItem = ActionUtil.getStringValue(formData, "sourceDefinitionId");
					if (!selectedItem.equals("") ) {
						//Delete the item
						String itemId = ActionUtil.getStringValue(formData, "selectedId");
						getDefinitionModule().deleteItem(selectedItem, itemId);
					}
					
				} else if (operation.equals("moveItem")) {
					selectedItem = selectedItem = ActionUtil.getStringValue(formData, "sourceDefinitionId");
					if (!selectedItem.equals("") ) {
						//Delete the item
						String itemId = ActionUtil.getStringValue(formData, "operationItem");
						String targetItemId = ActionUtil.getStringValue(formData, "selectedId");
						String location = ActionUtil.getStringValue(formData, "moveTo");
						getDefinitionModule().moveItem(selectedItem, itemId, targetItemId, location);
					}
					
				} else if (operation.equals("selectId")) {
					selectedItem = ((String[])formData.get("selectedId"))[0];
				}
			} catch (DefinitionInvalidOperation e) {
				//An error occurred while processing the operation; pass the error message back to the jsp
				//SessionErrors.add(req, e.getClass().getName(),e.getMessage());
			} catch(NoDefinitionByTheIdException e) {
				//The selected id must be non-existant. Give an error message
				//SessionErrors.add(req, e.getClass().getName(),e.getMessage());
				selectedItem = "";
			}
		}
		
		//Pass the selection id to be shown on to the rendering phase
		response.setRenderParameters(request.getParameterMap());
		response.setRenderParameter("selectedItem", selectedItem);
	}
		
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		Map model;
		Map formData = request.getParameterMap();

		String selectedItem = ActionUtil.getStringValue(formData, "selectedItem");
		String selectedItemTitle = "";

		//See if there is a definition type requested
		String definitionType = ActionUtil.getStringValue(formData, WebKeys.FORUM_ACTION_DEFINITION_BUILDER_DEFINITION_TYPE);

        model = getForumActionModule().getDefinitionBuilder(formData, request, selectedItem);

		Map data = new HashMap();
			
		Document definitionConfig = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			
		//Open the item that was selected
		String nodeOpen = "";
		if (formData.containsKey("selectedId")) {
			nodeOpen = ActionUtil.getStringValue(formData, "selectedId");
		}
		data.put("nodeOpen", nodeOpen);
			
		Map idData = new HashMap();
		Map idDataNames = new HashMap();
		Map idDataCaptions = new HashMap();
		data.put("idData", idData);
		idData.put("names",idDataNames);
		idData.put("captions",idDataCaptions);
		
		Document definitionTree;
		if (!Validator.isNull(selectedItem) ) {
			//A definition was selected, go view it
			Definition def = (Definition)model.get(WebKeys.DEFINITION);
			idDataNames.put(def.getId(), NLT.getDef(def.getName()));
			definitionType = String.valueOf(def.getType());
			Document sourceDefinition = def.getDefinition();
			data.put("sourceDefinition", sourceDefinition);
			Element sourceRoot = null;
			if (sourceDefinition != null) {
				sourceRoot = sourceDefinition.getRootElement();
			}

			//Build the definition tree
			definitionTree = DocumentHelper.createDocument();
			Element dtRoot = definitionTree.addElement("root");
			String title = NLT.getDef(def.getName());
			String caption = "";
			if (sourceRoot != null) {
				caption = NLT.getDef(sourceRoot.attributeValue("caption", ""));
			}
			if (!caption.equals("")) {title = caption + " (" + title + ")";}
			dtRoot.addAttribute("title", title);
			dtRoot.addAttribute("id", def.getId());
			idDataCaptions.put(def.getId(), caption.replaceAll("'", "\'"));
			
			if (sourceRoot != null) {
				buildDefinitionTree(sourceRoot, dtRoot);
			}
			selectedItemTitle = title;

			
		} else {
			//No definition is selected. Show the initial tree
			List currentDefinitions = (List)model.get(WebKeys.PUBLIC_DEFINITIONS);
			
			//Build the definition tree
			definitionTree = DocumentHelper.createDocument();
			Element dtRoot = definitionTree.addElement("root");
			dtRoot.addAttribute("title", NLT.getDef("__definitions"));
			dtRoot.addAttribute("id", "");
			Element root = definitionConfig.getRootElement();
			
			Iterator definitions = root.elementIterator("definition");
			while (definitions.hasNext()) {
				Element defEle = (Element) definitions.next();
				//See if this is one of the desired definition types
				if (definitionType.equals("") || definitionType.equals(defEle.attributeValue("definitionType", ""))) {
					Element treeEle = dtRoot.addElement("child");
					treeEle.addAttribute("type", "definition");
					treeEle.addAttribute("title", NLT.getDef(defEle.attributeValue("caption")));
					treeEle.addAttribute("id", defEle.attributeValue("name"));	
					//Add the current definitions (if any)
					ListIterator li = currentDefinitions.listIterator();
					while (li.hasNext()) {
						Definition curDef = (Definition)li.next();
						Document curDefDoc = curDef.getDefinition();
						Element curDefDocRoot = curDefDoc.getRootElement();
						if (curDef.getType() == Integer.valueOf(defEle.attributeValue("definitionType", "0")).intValue()) {
							Element curDefEle = treeEle.addElement("child");
							curDefEle.addAttribute("type", defEle.attributeValue("name"));
							String title = NLT.getDef(curDef.getName());
							//TODO get the caption from the definition meta data
							String caption = curDef.getDefinition().getRootElement().attributeValue("caption", "");
							if (!caption.equals("")) {
								title = caption + " (" + title + ")";
							}
							curDefEle.addAttribute("title", title);
							curDefEle.addAttribute("id", curDef.getId());
							idDataNames.put(curDef.getId(), NLT.getDef(curDef.getName()));
							idDataCaptions.put(curDef.getId(), curDefDocRoot.attributeValue("caption", "").replaceAll("'", "\'"));
						}
					}
				}
			}
		}
		//Set up the other data items
		String option = "";
		if (formData.containsKey("option")) {
			option = ActionUtil.getStringValue(formData, "option");
		}
		data.put("option", option);
		
		String itemId = "";
		if (formData.containsKey("itemId")) {
			itemId = ActionUtil.getStringValue(formData, "itemId");
		}
		data.put("itemId", itemId);
		
		String itemName = "";
		if (formData.containsKey("itemName")) {
			itemName = ActionUtil.getStringValue(formData, "itemName");
		}
		data.put("itemName", itemName);
		
        //There is a forum specified, so get the forum object
		model.put("definitionTree", definitionTree);
		data.put("selectedItem", selectedItem);
		data.put("selectedItemTitle", selectedItemTitle);
		data.put("definitionType", definitionType);
		model.put("data", data);
		if (!option.equals("")) {
			return new ModelAndView("definition_builder/view_definition_builder_option", model);
		}
		return new ModelAndView(WebKeys.VIEW_DEFINITION, model);
		
	}

    private void buildDefinitionTree(Element sourceElement, Element targetElement) {
		Iterator items = sourceElement.elementIterator("item");
		while (items.hasNext()) {
			Element sourceEle = (Element) items.next();
			Element properties = sourceEle.element("properties");
			String caption = NLT.getDef(sourceEle.attributeValue("caption"));
			if (properties != null) {
				Element captionProp = (Element) properties.selectSingleNode("property[@name='caption']");
				if (captionProp != null && !captionProp.attributeValue("value", "").equals("")) {
					caption += " - " + NLT.getDef(captionProp.attributeValue("value", ""));
				}
			}
			
			Element targetEle = targetElement.addElement("child");
			targetEle.addAttribute("type", "item");
			targetEle.addAttribute("title", caption);
			targetEle.addAttribute("id", sourceEle.attributeValue("id"));
			
			//See if this element has children to add
			buildDefinitionTree(sourceEle, targetEle);
		}
    }

}
