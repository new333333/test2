package com.sitescape.ef.portlet.definition_builder;

import java.lang.Integer;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.WindowState;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinitionInvalidOperation;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.portlet.support.ModelUtil;


/**
 * @author hurley
 *
 */
public class DefinitionBuilderController extends SAbstractController {
	
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		
		try {
			Map model = new HashMap();
			User user = RequestContextHolder.getRequestContext().getUser();
			Map formData = request.getParameterMap();
			Long folderId;
			try {
				folderId = ActionUtil.getForumId(formData, (PortletRequest)request);
			} catch (NoFolderByTheIdException nf) {
				model = new HashMap();
				model.put(ObjectKeys.WORKSPACE_DOM_TREE, getWorkspaceModule().getDomWorkspaceTree());
				
				//Make the tree available to the jsp
				ModelUtil.processModel(request,model);
				if (request.getWindowState().equals(WindowState.NORMAL)) {
				    request.setAttribute("action", "view_workspacetree");
				    return;
				} else {
					//Show the workspace tree maximized
					request.setAttribute("action", "view_workspacetree_maximized");
					return;
				}

			}
			
			String selectedItem = "";
			
			//See if there is an operation to perform
			if (formData.containsKey("cancelBtn")) {
				//The operation was canceled. Go back to the top screen
				if (formData.containsKey("sourceDefinitionId")) {
					selectedItem = ActionUtil.getStringValue(formData, "sourceDefinitionId");
				}
				
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
							selectedItem = getDefinitionModule().addDefinition(name, caption, type.intValue()).getId();							
						}
						
					} else if (operation.equals("modifyDefinition")) {
						//Modify the name of the selected item
						selectedItem = ActionUtil.getStringValue(formData, "selectedId");
						if (!selectedItem.equals("") ) {
							String definitionName = ActionUtil.getStringValue(formData, "modifyDefinitionName");
							String definitionCaption = ActionUtil.getStringValue(formData,"modifyDefinitionCaption");
							getDefinitionModule().modifyDefinitionName(selectedItem, definitionName, definitionCaption);
							if (formData.containsKey("modifyDefinitionReplyStyle")) {
								String definitionReplyStyle = ((String[])formData.get("modifyDefinitionReplyStyle"))[0];
								getDefinitionModule().modifyDefinitionAttribute(selectedItem, "replyStyle", definitionReplyStyle);
							}
							selectedItem = "";
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
	        //TODO: do you need all this??
	        //model = getForumActionModule().getDefinitionBuilder(formData, request, folderId, selectedItem);

			Map data = new HashMap();
			
			Document definitionConfig = (Document)model.get("ss_forum_config_definition");
			
			//Open the item that was selected
			String nodeOpen = "";
			if (formData.containsKey("selectedId")) {
				nodeOpen = ActionUtil.getStringValue(formData, "selectedId");
			}
			data.put("nodeOpen", nodeOpen);
			
			Map idData = new HashMap();
			Map idDataNames = new HashMap();
			Map idDataCaptions = new HashMap();
			Map idDataReplyStyles = new HashMap();
			data.put("idData", idData);
			idData.put("names",idDataNames);
			idData.put("captions",idDataCaptions);
			idData.put("replyStyles",idDataReplyStyles);
			
			Document definitionTree;
			if (!selectedItem.equals("") ) {
				//A definition was selected, go view it
				Definition def = (Definition)model.get("ss_forum_entry_definition");
				idDataNames.put(def.getId(), def.getName());
				Document sourceDefinition = def.getDefinition();
				data.put("sourceDefinition", sourceDefinition);
				Element sourceRoot = null;
				if (sourceDefinition != null) {
					sourceRoot = sourceDefinition.getRootElement();
				}

				//Build the definition tree
				definitionTree = DocumentHelper.createDocument();
				Element dtRoot = definitionTree.addElement("root");
				String title = def.getName();
				String caption = "";
				String replyStyle = "";
				if (sourceRoot != null) {
					caption = sourceRoot.attributeValue("caption", "");
					replyStyle = sourceRoot.attributeValue("replyStyle", "");
				}
				if (!caption.equals("")) {title = caption + " (" + title + ")";}
				dtRoot.addAttribute("title", title);
				dtRoot.addAttribute("id", def.getId());
				idDataCaptions.put(def.getId(), caption.replaceAll("'", "\'"));
				idDataReplyStyles.put(def.getId(), replyStyle);
				
				if (sourceRoot != null) {
					buildDefinitionTree(sourceRoot, dtRoot);
				}
				
			} else {
				//No definition is selected. Show the initial tree
				List currentDefinitions = (List)model.get("ss_definition_public");
				
				//Build the definition tree
				definitionTree = DocumentHelper.createDocument();
				Element dtRoot = definitionTree.addElement("root");
				dtRoot.addAttribute("title", "Definitions");
				dtRoot.addAttribute("id", "");
				Element root = definitionConfig.getRootElement();
				
				Iterator definitions = root.elementIterator("definition");
				while (definitions.hasNext()) {
					Element defEle = (Element) definitions.next();
					Element treeEle = dtRoot.addElement("child");
					treeEle.addAttribute("type", "definition");
					treeEle.addAttribute("title", defEle.attributeValue("caption"));
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
							String title = curDef.getName();
							//TODO get the caption from the definition meta data
							//String caption = curDef.getCaption();
							String caption = curDef.getDefinition().getRootElement().attributeValue("caption", "");
							if (!caption.equals("")) {
								title = caption + " (" + title + ")";
							}
							curDefEle.addAttribute("title", title);
							curDefEle.addAttribute("id", curDef.getId());

							idDataNames.put(curDef.getId(), curDef.getName());
							idDataCaptions.put(curDef.getId(), curDefDocRoot.attributeValue("caption", "").replaceAll("'", "\'"));
							idDataReplyStyles.put(curDef.getId(), curDefDocRoot.attributeValue("replyStyle", ""));
						}
					}
				}
			}
			
	        //There is a forum specified, so get the forum object
			model.put("definitionConfig", definitionConfig);
			model.put("definitionTree", definitionTree);
			data.put("selectedItem", selectedItem);
			model.put("data", data);
			ModelUtil.processModel(request, model);

			request.setAttribute("action", "definition_builder");
			return;
		}
		catch (Exception e) {
			//request.setAttribute(PageContext.EXCEPTION, e);

			//return mapping.findForward(Constants.COMMON_ERROR);
		}
	}

    private void buildDefinitionTree(Element sourceElement, Element targetElement) {
		Iterator items = sourceElement.elementIterator("item");
		while (items.hasNext()) {
			Element sourceEle = (Element) items.next();
			Element properties = sourceEle.element("properties");
			String caption = sourceEle.attributeValue("caption");
			if (properties != null) {
				Element captionProp = (Element) properties.selectSingleNode("property[@name='caption']");
				if (captionProp != null && !captionProp.attributeValue("value", "").equals("")) {
					caption += " - " + captionProp.attributeValue("value", "");
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
