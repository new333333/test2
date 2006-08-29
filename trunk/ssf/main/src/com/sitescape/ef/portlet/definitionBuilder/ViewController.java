package com.sitescape.ef.portlet.definitionBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinitionInvalidException;
import com.sitescape.ef.domain.DefinitionInvalidOperation;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.util.Validator;

/**
 * @author hurley
 *
 */
public class ViewController extends SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());

		Map formData = request.getParameterMap();
		String selectedItem = PortletRequestUtils.getStringParameter(request,"sourceDefinitionId", "");
			
		//See if there is an operation to perform
		if (formData.containsKey("cancelBtn")) {
			//The operation was canceled. Go back to the top screen
				
		} else if (formData.containsKey("operation")) {
			String operation = PortletRequestUtils.getStringParameter(request,"operation", "");
				
			try {
				if (operation.equals("addDefinition")) {
					//Add a new definition type
					String name = PortletRequestUtils.getStringParameter(request,"propertyId_name", "");
					String caption = PortletRequestUtils.getStringParameter(request, "propertyId_caption", "");
					String operationItem = PortletRequestUtils.getStringParameter(request, "operationItem", "");
					Integer type = PortletRequestUtils.getIntParameter(request, "definitionType_"+operationItem);
					if (!name.equals("") && type != null) {
						Definition def = getDefinitionModule().addDefinition(name, caption, type.intValue(),  new MapInputData(formData));			
						selectedItem = def.getId();
					}
					
				} else if (operation.equals("modifyDefinition")) {
					//Modify the name of the selected item
					selectedItem = PortletRequestUtils.getStringParameter(request, "selectedId", "");
					if (!selectedItem.equals("")) {
						getDefinitionModule().modifyDefinitionProperties(selectedItem,  new MapInputData(formData));
					}
					
				} else if (operation.equals("deleteDefinition")) {
					//Delete the selected item
					selectedItem = PortletRequestUtils.getStringParameter(request, "selectedId", "");
					if (!selectedItem.equals("")) {
						try {
							getDefinitionModule().deleteDefinition(selectedItem);
						} catch(NoDefinitionByTheIdException e) {
							//If the id is already deleted, ignore the error
						}
						selectedItem = "";
					}
					
				} else if (operation.equals("addItem")) {
					selectedItem = PortletRequestUtils.getStringParameter(request, "sourceDefinitionId", "");
					if (!selectedItem.equals("")) {
						//Add the new item
						String itemId = PortletRequestUtils.getStringParameter(request,"selectedId", "");
						String itemToAdd = PortletRequestUtils.getStringParameter(request, "operationItem", "");
						getDefinitionModule().addItem(selectedItem, itemId, itemToAdd,  new MapInputData(formData));
					}
						
				} else if (operation.equals("modifyItem")) {
					selectedItem = PortletRequestUtils.getStringParameter(request, "sourceDefinitionId", "");
					if (!selectedItem.equals("")) {
						//Modify the item
						String itemId = PortletRequestUtils.getStringParameter(request,"selectedId", "");
						getDefinitionModule().modifyItem(selectedItem, itemId, new MapInputData(formData));
					}
					
				} else if (operation.equals("deleteItem")) {
					selectedItem = PortletRequestUtils.getStringParameter(request, "sourceDefinitionId", "");
					if (!selectedItem.equals("")) {
						//Delete the item
						String itemId = PortletRequestUtils.getStringParameter(request,"selectedId", "");
						getDefinitionModule().deleteItem(selectedItem, itemId);
					}
					
				} else if (operation.equals("moveItem")) {
					selectedItem = PortletRequestUtils.getStringParameter(request, "sourceDefinitionId", "");
					if (!selectedItem.equals("")) {
						//Delete the item
						String itemId = PortletRequestUtils.getStringParameter(request, "operationItem", "");
						String targetItemId = PortletRequestUtils.getStringParameter(request, "selectedId", "");
						String location = PortletRequestUtils.getStringParameter(request, "moveTo", "");
						getDefinitionModule().modifyItemLocation(selectedItem, itemId, targetItemId, location);
					}
					
				} else if (operation.equals("selectId")) {
					selectedItem = PortletRequestUtils.getStringParameter(request, "selectedId", "");
				}
			} catch (DefinitionInvalidOperation e) {
				//An error occurred while processing the operation; pass the error message back to the jsp
				response.setRenderParameter("ss_configErrorMessage", e.getLocalizedMessage());
			} catch (DefinitionInvalidException e) {
				//An error occurred while processing the operation; pass the error message back to the jsp
				response.setRenderParameter("ss_configErrorMessage", e.getLocalizedMessage());
			} catch(NoDefinitionByTheIdException e) {
				//The selected id must be non-existant. Give an error message
				response.setRenderParameter("ss_configErrorMessage", e.getLocalizedMessage());
				selectedItem = "";
			}
		}
		
		//Pass the selection id to be shown on to the rendering phase
		response.setRenderParameter("selectedItem", selectedItem);
	}
		
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
			
		Map model = new HashMap();
		Map formData = request.getParameterMap();

		String selectedItem = PortletRequestUtils.getStringParameter(request, "selectedItem", "");
		if (selectedItem.equals("0")) selectedItem = "";
		String selectedItemTitle = "";

		//See if there is a definition type requested
		String definitionType = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, "");

		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.CONFIG_DEFINITION, getDefinitionModule().getDefinitionConfig());
			
		if (!selectedItem.equals("")) {
			model.put(WebKeys.DEFINITION, getDefinitionModule().getDefinition(selectedItem));
		}

		Map data = new HashMap();
			
		Document definitionConfig = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			
		//Open the item that was selected
		String nodeOpen = PortletRequestUtils.getStringParameter(request, "selectedId", "");
		
		data.put("nodeOpen", nodeOpen);
			
		Map idData = new HashMap();
		Map idDataNames = new HashMap();
		Map idDataCaptions = new HashMap();
		data.put("idData", idData);
		idData.put("names",idDataNames);
		idData.put("captions",idDataCaptions);
		
		Document definitionTree;
		if (!Validator.isNull(selectedItem)) {
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
				buildDefinitionTree(sourceRoot, dtRoot, idDataNames, idDataCaptions);
			}
			selectedItemTitle = title;

			
		} else {
			//No definition is selected. Show the initial tree
			List currentDefinitions = getDefinitionModule().getDefinitions();
			
			//Build the definition tree
			definitionTree = DocumentHelper.createDocument();
			Element dtRoot = definitionTree.addElement("root");
			dtRoot.addAttribute("title", NLT.getDef("__definitions"));
			dtRoot.addAttribute("id", "0");
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
						if (curDefDoc == null) continue;
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
		String option = PortletRequestUtils.getStringParameter(request, "option", "");
		
		data.put("option", option);
		
		String itemId = PortletRequestUtils.getStringParameter(request, "itemId", "");
		
		data.put("itemId", itemId);
		
		String itemName = PortletRequestUtils.getStringParameter(request, "itemName", "");
		
		data.put("itemName", itemName);
		
        //There is a forum specified, so get the forum object
		model.put("definitionTree", definitionTree);
		data.put("selectedItem", selectedItem);
		data.put("selectedItemTitle", selectedItemTitle);
		data.put("definitionType", definitionType);
		if (formData.containsKey("ss_configErrorMessage")) {
			model.put("ss_configErrorMessage", ((String[]) formData.get("ss_configErrorMessage"))[0]);
		}
		model.put("data", data);
		if (!option.equals("")) {
			response.setContentType("text/xml");			
			Map statusMap = new HashMap();
			if(!WebHelper.isUserLoggedIn(request)) {
				//Signal that the user is not logged in. 
				//  The code on the calling page will output the proper translated message.
				statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			}
			model.put(WebKeys.AJAX_STATUS, statusMap);
			return new ModelAndView("definition_builder/view_definition_builder_option", model);
		}
		return new ModelAndView(WebKeys.VIEW_DEFINITION, model);
		
	}

    private void buildDefinitionTree(Element sourceElement, Element targetElement, Map idDataNames, Map idDataCaptions) {
		Iterator items = sourceElement.elementIterator("item");
		while (items.hasNext()) {
			Element sourceEle = (Element) items.next();
			Element properties = sourceEle.element("properties");
			String name = NLT.getDef(sourceEle.attributeValue("name"));
			String caption = NLT.getDef(sourceEle.attributeValue("caption"));
			if (properties != null) {
				Element captionProp = (Element) properties.selectSingleNode("property[@name='caption']");
				if (captionProp != null) {
					if (!captionProp.attributeValue("value", "").equals("")) {
						caption += " - " + NLT.getDef(captionProp.attributeValue("value", ""));
					} else {
						Element nameProp = (Element) properties.selectSingleNode("property[@name='name']");
						if (nameProp != null) {
							if (!nameProp.attributeValue("value", "").equals("")) {
								caption += " - " + nameProp.attributeValue("value", "");
							}
						}
					}
				}
			}
			
			Element targetEle = targetElement.addElement("child");
			targetEle.addAttribute("type", "item");
			targetEle.addAttribute("title", caption);
			targetEle.addAttribute("id", sourceEle.attributeValue("id"));
			idDataNames.put(sourceEle.attributeValue("id"), NLT.getDef(name));
			idDataCaptions.put(sourceEle.attributeValue("id"), caption.replaceAll("'", "\'"));
			
			//See if this element has children to add
			buildDefinitionTree(sourceEle, targetEle, idDataNames, idDataCaptions);
		}
    }

}
