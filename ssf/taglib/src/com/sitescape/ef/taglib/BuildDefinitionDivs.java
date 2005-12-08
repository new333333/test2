package com.sitescape.ef.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.util.NLT;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;


/**
 * @author Peter Hurley
 *
 */
public class BuildDefinitionDivs extends TagSupport {
    private String title;
    private Document configDocument;
    private Document sourceDocument;
    private Map divNames;
    private Map entryDefinitions;
    private String option = "";
    private String itemId = "";
    private String itemName = "";
	private int helpDivCount;
	
	private String helpImgUrl = "";
	private Element rootElement;
	private String rootElementId;
	private String rootElementName;
	private Element rootConfigElement;

    
	public int doStartTag() throws JspException {
	    if(this.title == null)
	        throw new JspException("The title must be specified");
	    
		try {
			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			StringBuffer hb = new StringBuffer();
			
			//Clear the list of div names that have been created
			this.divNames = new HashMap();

			if (this.sourceDocument == null) {this.sourceDocument = this.configDocument;}

			Element configRoot = this.configDocument.getRootElement();
			Element root = this.sourceDocument.getRootElement();
			
			//See if we are just building a single option, or the whole page
			if (this.option.equals("")) {
				//Build the selection instructions div (only do this if building the whole page)
				sb.append("\n<div id='info_select' class='ss_definitionBuilder'>\n");
				sb.append("<span class='ss_titlebold'>" + this.title + "</span>\n");
				sb.append("</div>\n");
				sb.append("<script language='javascript'>\n");
				sb.append("    self.ss_setDeclaredDiv('info_select')\n");
				sb.append("    var idMap;\n");
				sb.append("    if (!idMap) {idMap = new Array();}\n");
				sb.append("    var idMapCaption;\n");
				sb.append("    if (!idMapCaption) {idMapCaption = new Array();}\n");
				sb.append("</script>\n");
			}
			
			//Build the divs for each element
			if (this.option.equals("")) {
				buildDivs(configRoot, root, sb, hb, "");
			}
			buildDivs(root, root, sb, hb, "item");
			
			if (this.option.equals("")) {
				buildDefaultDivs(sb, hb);
				jspOut.print(hb.toString());
			}
			jspOut.print(sb.toString());
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
	    
		return SKIP_BODY;
	}
	
	private void buildDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		Iterator itRootElements;
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		String contextPath = req.getContextPath();
		if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
		helpImgUrl = contextPath + "/images/pics/sym_s_help.gif";

		if (filter.equals("")) {
			itRootElements = root.elementIterator();
		} else {
			itRootElements = root.elementIterator(filter);
		}
		//See if this is a request for just one item
		if (!this.option.equals("") && !this.itemId.equals("")) {
			//We are looking for a single item, so only do that item
			itRootElements = root.selectNodes("//item[@id='"+this.itemId+"']").iterator();
		} else if (!this.option.equals("") && !this.itemName.equals("")) {
			//We are looking for an item in the definition config
			itRootElements = this.configDocument.getRootElement().selectNodes("//item[@name='"+this.itemName+"']").iterator();
		} else if (!this.option.equals("") && this.itemId.equals("") && this.itemName.equals("")) {
			//We are looking for the definition itself
			String definitionType = root.attributeValue("type", "");
			if (!definitionType.equals("")) {
				itRootElements = root.selectNodes(".").iterator();
			}
		}

		while (itRootElements.hasNext()) {
			rootElement = (Element) itRootElements.next();
			rootElementId = rootElement.attributeValue("id", rootElement.attributeValue("name"));
			rootElementName = rootElement.attributeValue("name");
			helpDivCount = 0;
			
			//Get the config version of this item
			rootConfigElement = null;
			if (rootElement.getDocument().getRootElement() == rootElement) {
				//This is the root node
				String definitionType = rootElement.attributeValue("type", "");
				if (!definitionType.equals("")) {
					rootConfigElement = (Element) this.configDocument.getRootElement().selectSingleNode("//item[@definitionType='"+definitionType+"']");
				}
			} else {
				rootConfigElement = (Element) this.configDocument.getRootElement().selectSingleNode("item[@name='"+rootElementName+"']");
			}
			if (rootConfigElement == null) {rootConfigElement = rootElement;}
			
			//Build the information divs
			buildInfoDivs(root, sourceRoot, sb, hb, filter);

			//Build the infoDefinitionOptions div
			buildInfoOptionsDivs(root, sourceRoot, sb, hb, filter);

			//Build the modify_definition div
			buildModifyDefinitionDiv(root, sourceRoot, sb, hb, filter);

			//Build the delete_definition divs
			buildDeleteDefinitionDiv(root, sourceRoot, sb, hb, filter);

			//Build the operations divs
			buildOperationsDivs(root, sourceRoot, sb, hb, filter);

			//Build the options divs
			buildOptionsDivs(root, sourceRoot, sb, hb, filter);
			
			//Build the properties divs
			buildPropertiesDivs(root, sourceRoot, sb, hb, filter);
			
			//See if this element has any sub elements to do
			if (this.option.equals("")) {
				buildDivs(rootElement, sourceRoot, sb, hb, "item");
			}
		}
	}

	private void buildInfoDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("") && !this.divNames.containsKey("info_"+rootElementId)) {
			this.divNames.put("info_"+rootElementId, "1");
			sb.append("\n<div id='info_" + rootElementId + "' ");
			sb.append("class='ss_definitionBuilder'>\n");
			sb.append("<span class='ss_contentbold'>" + NLT.getDef(rootElement.attributeValue("caption")));
			Element property = (Element) rootElement.selectSingleNode("./properties/property[@name='caption']");
			String propertyCaptionValue = "";
			if (property != null) {
				propertyCaptionValue = property.attributeValue("value", "");
				if (!propertyCaptionValue.equals("")) {
					sb.append(" - " + propertyCaptionValue);
				}
			}
			if (propertyCaptionValue.equals("")) {
				propertyCaptionValue = NLT.getDef(rootElement.attributeValue("caption"));
			}
			sb.append("</span>\n<br><br>\n");
			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('info_" + rootElementId + "')\n");
			sb.append("    idMap['"+rootElementId+"'] = '"+rootElementName+"';\n");
			sb.append("    idMapCaption['"+rootElementId+"'] = '"+propertyCaptionValue.replaceAll("'", "\'")+"';\n");
			sb.append("</script>\n");
		}

	}
	
	private void buildInfoOptionsDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("") && !this.divNames.containsKey("infoDefinitionOptions")) {
			this.divNames.put("infoDefinitionOptions", "1");
			sb.append("\n<div id='infoDefinitionOptions' ");
			sb.append("class='ss_definitionBuilder'>\n");
			sb.append("<span class='ss_titlebold' id='infoDefinitionOptionsDefinitionName'></span>\n");
			
			sb.append("<table>\n");
			
			sb.append("<tr><td>\n");
			sb.append("<a class='ss_content' href='javascript: ;' "); 
			sb.append("onClick=\"return viewDefinition();\">");
			sb.append("View this definition");
			sb.append("</a>\n");
			sb.append("</td></tr>\n");
			
			sb.append("<tr><td>\n");
			sb.append("<a class='ss_content' href='javascript: ;' "); 
			sb.append("onClick=\"return modifyDefinition();\">");
			sb.append("Modify the properties of this definition");
			sb.append("</a>\n");
			sb.append("</td></tr>\n");

			sb.append("<tr><td>\n");
			sb.append("<a class='ss_content' href='javascript: ;' "); 
			sb.append("onClick=\"return deleteDefinition();\">");
			sb.append("Delete this definition");
			sb.append("</a>\n");
			sb.append("</td></tr>\n");

			sb.append("</table>\n");

			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('infoDefinitionOptions')\n");
			sb.append("</script>\n");
		}
	}

	private void buildModifyDefinitionDiv(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		//This div is only built and used dynamically
		if (!this.divNames.containsKey("modify_definition") && this.option.equals("modifyDefinition")) {
			this.divNames.put("modify_definition", "1");
			
			//String definitionType = 
			sb.append("<span class='ss_titlebold'>Modify the properties of this definition</span><br><br>\n");
			sb.append("<span>Name</span><br>\n");
			sb.append("<input type='text' size='40' value=\"");
			sb.append(sourceRoot.attributeValue("name", ""));
			sb.append("\" disabled='true'>\n");
			sb.append("<input type='hidden' name='modifyDefinitionName' value=\"");
			sb.append(sourceRoot.attributeValue("name", "").replaceAll("\"", "&quot;"));
			sb.append("\" >\n<br>\n");
			sb.append("<span>Caption</span><br>\n");
			sb.append("<input type='text' name='modifyDefinitionCaption' size='40' value=\"");
			sb.append(sourceRoot.attributeValue("caption", "").replaceAll("\"", "&quot;"));
			sb.append("\"><br>\n");

			//Append the properties form elements
			this.option = "properties";
			buildPropertiesDivs(root, sourceRoot, sb, hb, filter);
		}
	}
	
	private void buildDeleteDefinitionDiv(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("") && !this.divNames.containsKey("delete_definition")) {
			this.divNames.put("delete_definition", "1");
			sb.append("\n<div id='delete_definition' ");
			sb.append("class='ss_definitionBuilder'>\n");
			sb.append("<span class='ss_titlebold'>Select the definition to be deleted</span>\n");
			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('delete_definition')\n");
			sb.append("</script>\n");
			sb.append("\n<div id='delete_definition_confirm' ");
			sb.append("class='ss_definitionBuilder'>\n");
			sb.append("<span class='ss_titlebold'>Delete: <span id='deleteDefinitionSelection'></span></span>\n");
			sb.append("<br><br>\n");
			sb.append("<input type='submit' name='okBtn' value='  OK  '>\n");
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			sb.append("<input type='submit' name='cancelBtn' value='Cancel'>\n");
			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('delete_definition_confirm')\n");
			sb.append("</script>\n");
		}
	}

	private void buildOperationsDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("") && !this.divNames.containsKey("operations_"+rootElementId)) {
			this.divNames.put("operations_"+rootElementId, "1");
			sb.append("\n<div id='operations_" + rootElementId + "' "); 
			sb.append("class='ss_definitionBuilder'>\n");
			sb.append("<table cellpadding='0' cellspacing='0'>\n");

			//Add the list of operations
			Element operations = rootConfigElement.element("operations");
			Element operationElement;
			if (operations == null) {
				//There are no operations listed in the definition file, so add in the standard operations
				//See if there are any options. If so, add the "add" command.
				operations = rootElement.addElement("operations");
				if (rootElement.element("options") != null) {
					operationElement = operations.addElement("operation");
					operationElement.addAttribute("name", "addOption");
					operationElement.addAttribute("caption", "__add");
				}
				if (rootElement.element("properties") != null) {
					operationElement = operations.addElement("operation");
					operationElement.addAttribute("name", "modifyItem");
					operationElement.addAttribute("caption", "__modify");
				}
				if (rootElement.attributeValue("canBeDeleted", "true").equalsIgnoreCase("true")) {
					operationElement = operations.addElement("operation");
					operationElement.addAttribute("name", "deleteItem");
					operationElement.addAttribute("caption", "__delete");
				}
				
				operationElement = operations.addElement("operation");
				operationElement.addAttribute("name", "moveItem");
				operationElement.addAttribute("caption", "__move");
				
				/**
				 if (rootElement.attributeValue("multipleAllowed", "").equalsIgnoreCase("true") || 
						sourceRoot.selectSingleNode("//item[@name='"+rootElementName+"']") == null) {
					operationElement = operations.addElement("operation");
					operationElement.addAttribute("name", "cloneItem");
					operationElement.addAttribute("caption", "__clone");
					}
				 **/
				
			}
			Iterator itOperations = operations.elementIterator("operation");
			while (itOperations.hasNext()) {
				sb.append("<tr><td>\n");
				operationElement = (Element) itOperations.next();
				String operationElementId = operationElement.attributeValue("id", operationElement.attributeValue("name"));
				String operationElementName = operationElement.attributeValue("name");
				
				sb.append("<a class='ss_content' href='javascript: ;' "); 
				if (operationElement.attributeValue("item", "").equals("")) {
					sb.append("onClick=\""+operationElementId+"('" + operationElementId + "', '" + operationElementName + "', '')\">");
				} else {
					sb.append("onClick=\""+operationElementId+"('" + operationElementId + "', '" + operationElementName + "', '"+operationElement.attributeValue("item")+"')\">");
				}
				sb.append(NLT.getDef(operationElement.attributeValue("caption")));
				sb.append("</a>\n");
				sb.append("</td></tr>\n");
			}

			sb.append("</table>\n");
			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('operations_" + rootElementId + "')\n");
			sb.append("    idMap['"+rootElementId+"'] = '"+rootElementName+"';\n");
			sb.append("</script>\n");
		}
	}
	
	private void buildOptionsDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("") && !this.divNames.containsKey("options_"+rootElementId)) {
			this.divNames.put("options_"+rootElementId, "1");
			sb.append("\n<div id='options_" + rootElementId + "' "); 
			sb.append("class='ss_definitionBuilder'>\n");

			//Add the list of options
			Element e_options = rootConfigElement.element("options");
			Element e_option;
			Map optionsSeen = new HashMap();
			
			sb.append("<ul>\n");
			if (e_options != null) {
				Iterator itOptions = e_options.elementIterator("option");
				while (itOptions.hasNext()) {
					e_option = (Element) itOptions.next();
					String optionId = e_option.attributeValue("id", e_option.attributeValue("name"));
					String optionName = e_option.attributeValue("name");
					//Find this item in the definition config
					Element optionItem = (Element) this.configDocument.getRootElement().selectSingleNode("item[@name='"+optionName+"']");
					if (optionItem != null) {
						//See if multiple are allowed
						if (!optionItem.attributeValue("multipleAllowed", "").equalsIgnoreCase("false") || 
								sourceRoot.selectSingleNode("//item[@name='"+optionName+"']") == null) {
							if (!optionsSeen.containsKey(optionName)) {
								sb.append("<li>");
								sb.append("<a href=\"javascript: ;\" onClick=\"showProperties('"+optionId+"', '"+optionName+"')\">");
								sb.append(NLT.getDef(optionItem.attributeValue("caption", optionName)));
								sb.append("</a>");
								//See if this item has any help
								Element help = (Element) optionItem.selectSingleNode("./help");
								if (help != null) {
									helpDivCount++;
									hb.append("<div id='help_div_" + rootElementId);
									hb.append(Integer.toString(helpDivCount));
									hb.append("' class='ss_helpPopUp'>\n");
									hb.append("<span class='ss_content'>");
									hb.append(NLT.getDef(help.getText()));
									hb.append("</span>\n</div>\n");
									sb.append("&nbsp;<a name='help_div_" + rootElementId);
									sb.append(Integer.toString(helpDivCount));
									sb.append("_a' onClick=\"activateMenuLayer('help_div_ + rootElementId");
									sb.append(Integer.toString(helpDivCount));
									sb.append("');return false;\"><img src='"+helpImgUrl+"'></a>\n");
								}
								sb.append("</li>\n");
								optionsSeen.put(optionName, optionName);
							}
						}
					}
				}
				
				itOptions = e_options.elementIterator("option_select");
				while (itOptions.hasNext()) {
					e_option = (Element) itOptions.next();
					String optionCaption = NLT.getDef(e_option.attributeValue("optionCaption", ""));
					if (!optionCaption.equals("")) {
						sb.append("</ul>\n<br>\n<b>").append(optionCaption).append("</b>\n<ul>\n");
					}
					String optionPath = e_option.attributeValue("path", "");
					Iterator itOptionsSelect = rootConfigElement.selectNodes(optionPath).iterator();
					while (itOptionsSelect.hasNext()) {
						Element optionSelect = (Element) itOptionsSelect.next();
						String optionSelectId = optionSelect.attributeValue("id", optionSelect.attributeValue("name"));
						String optionSelectName = optionSelect.attributeValue("name");
						//See if multiple are allowed
						if (!optionSelect.attributeValue("multipleAllowed", "").equalsIgnoreCase("false") ||
								sourceRoot.selectSingleNode("//item[@name='"+optionSelectName+"']") == null) {
							if (!optionsSeen.containsKey(optionSelectName)) {
								sb.append("<li>");
								sb.append("<a href=\"javascript: ;\" onClick=\"showProperties('"+optionSelectId+"', '"+optionSelectName+"')\">");
								sb.append(NLT.getDef(optionSelect.attributeValue("caption", optionSelectName)));
								sb.append("</a>");
								//See if this item has any help
								Element help = (Element) optionSelect.selectSingleNode("./help");
								if (help != null) {
									helpDivCount++;
									hb.append("<div id='help_div_" + rootElementId);
									hb.append(Integer.toString(helpDivCount));
									hb.append("' class='ss_helpPopUp'>\n");
									hb.append("<span class='ss_content'>");
									hb.append(NLT.getDef(help.getText()));
									hb.append("</span>\n</div>\n");
									sb.append("&nbsp;<a name='help_div_" + rootElementId);
									sb.append(Integer.toString(helpDivCount));
									sb.append("_a' onClick=\"activateMenuLayer('help_div_" + rootElementId);
									sb.append(Integer.toString(helpDivCount));
									sb.append("');return false;\"><img src='"+helpImgUrl+"'></a>\n");
								}
								sb.append("</li>\n");
								optionsSeen.put(optionSelectName, optionSelectName);
							}
						}
					}
				}
			}
			sb.append("</ul>\n");
			sb.append("<br>\n");
			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('options_" + rootElementId + "')\n");
			sb.append("    idMap['"+rootElementId+"'] = '"+rootElementName+"';\n");
			sb.append("</script>\n");
		}
	}
	private void buildPropertiesDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		//Build the properties div
		if ((this.option.equals("") || this.option.equals("properties")) && 
				!this.divNames.containsKey("properties_"+rootElementId)) {
			this.divNames.put("properties_"+rootElementId, "1");
			if (this.option.equals("")) {
				sb.append("\n<div id='properties_" + rootElementId + "' "); 
				sb.append("class='ss_definitionBuilder'>\n");
			}

			//Add the list of properties
			Element property;
			Element propertiesConfig = rootConfigElement.element("properties");
			Element properties = rootElement.element("properties");
			if (propertiesConfig != null) {
				Iterator itProperties = propertiesConfig.elementIterator("property");
				while (itProperties.hasNext()) {
					//Get the next property from the base config file
					Element propertyConfig = (Element) itProperties.next();
					//Get the name and id (if any) from the config file
					String propertyId = propertyConfig.attributeValue("id", propertyConfig.attributeValue("name", ""));
					String propertyName = propertyConfig.attributeValue("name", "");
					String readonly = "";
					if (propertyConfig.attributeValue("readonly", "false").equalsIgnoreCase("true")) {
						readonly = "readonly='readonly'";
					}
					String propertyValue = "";
					String propertyValueDefault = "";
					if (properties != null) {
						//See if there is already a value for this property in the actual definition file
						property = (Element) properties.selectSingleNode("property[@name='"+propertyId+"']");
						if (property != null) {
							propertyValue = property.attributeValue("value", "");
							propertyValueDefault = property.attributeValue("default", "");
						}
					}
					String type = propertyConfig.attributeValue("type", "text");
					if (type.equals("textarea")) {
						if (!propertyConfig.attributeValue("caption", "").equals("")) {
							sb.append(NLT.getDef(propertyConfig.attributeValue("caption")));
							sb.append("\n<br>\n");
						}
						sb.append("<textarea name='propertyId_" + propertyId + "' rows='6' cols='60' "+readonly+">"+propertyValue+"</textarea>\n");
					
					} else if (type.equals("boolean") || type.equals("checkbox")) {
						String checked = "";
						if (propertyValue.equals("")) {
							if (propertyConfig.attributeValue("default", "false").equalsIgnoreCase("true")) {
								checked = "checked";
							}
						} else if (propertyValue.equalsIgnoreCase("true")) {
							checked = "checked";
						}
						sb.append("<input type='checkbox' name='propertyId_" + propertyId + "' "+checked+" "+readonly+"> ");
						sb.append(NLT.getDef(propertyConfig.attributeValue("caption")));
					
					} else if (type.equals("selectbox")) {
						if (!propertyConfig.attributeValue("caption", "").equals("")) {
							sb.append(NLT.getDef(propertyConfig.attributeValue("caption")));
							sb.append("\n<br>\n");
						}
						sb.append("<select name='propertyId_" + propertyId + "'>\n");
						//See if there are any built-in options
						Iterator  itSelections = propertyConfig.elementIterator("option");
						while (itSelections.hasNext()) {
							Element selection = (Element) itSelections.next();
							String checked = "";
							if ((!propertyValue.equals("") && propertyValue.equals(selection.attributeValue("name", ""))) || 
									(!propertyValueDefault.equals("") && propertyValueDefault.equals(selection.attributeValue("name", "")))) {
								checked = " selected";
							}
							sb.append("<option value='").append(selection.attributeValue("name", "")).append("'").append(checked).append(">");
							sb.append(NLT.getDef(selection.attributeValue("caption", selection.attributeValue("name", ""))));
							sb.append("</option>\n");
						}
						//See if there are any data items to be shown from the "sourceRoot" entry form
						itSelections = propertyConfig.elementIterator("option_entry_data");
						while (itSelections.hasNext()) {
							Element selection = (Element) itSelections.next();
							String selectionSelectType = selection.attributeValue("select_type", "");
							//Select the data items from the actual definition, not from the base configuration definition
							Element entryFormElement = (Element) sourceRoot.selectSingleNode("item[@name='entryForm']");
							if (entryFormElement != null) {
								Iterator itEntryFormElements = entryFormElement.selectNodes(".//item").iterator();
								while (itEntryFormElements.hasNext()) {
									Element entryFormItem = (Element) itEntryFormElements.next();
									Element entryFormItemNameProperty = (Element) entryFormItem.selectSingleNode("./properties/property[@name='name']");
									String entryFormItemNamePropertyValue = "";
									if (entryFormItemNameProperty != null) {
										entryFormItemNamePropertyValue = entryFormItemNameProperty.attributeValue("value", "");
									}
									String entryFormItemNamePropertyName = "";
									if (entryFormItemNameProperty != null) {
										entryFormItemNamePropertyName = entryFormItemNamePropertyValue;
									}
									if (entryFormItemNamePropertyName.equals("")) {
										entryFormItemNamePropertyName = entryFormItem.attributeValue("name", "");
									}
									Element entryFormItemCaptionProperty = (Element) entryFormItem.selectSingleNode("./properties/property[@name='caption']");
									String entryFormItemCaptionPropertyValue = "";
									if (entryFormItemCaptionProperty != null) {
										entryFormItemCaptionPropertyValue = entryFormItemCaptionProperty.attributeValue("value", "");
									}
									if (entryFormItemCaptionPropertyValue.equals("")) {
										entryFormItemCaptionPropertyValue = entryFormItemNamePropertyName;
									}
									//See if this is a data type by looking in the base configuration
									Element itemDefinition = (Element) this.configDocument.getRootElement().selectSingleNode("//item[@name='"+entryFormItem.attributeValue("name", "")+"']");
									if (itemDefinition != null) {
										if (itemDefinition.attributeValue("type", "").equalsIgnoreCase(selectionSelectType)) {
											String checked = "";
											if (entryFormItemNamePropertyName.equals(propertyValue) || 
													(propertyValue.equals("") && entryFormItemNamePropertyName.equals(propertyValueDefault))) {
												checked = " selected";
											}
											sb.append("<option value='").append(entryFormItemNamePropertyName).append("'").append(checked).append(">");
											sb.append(entryFormItemCaptionPropertyValue);
											sb.append("</option>\n");
										}
									}
								}
							}
						}
						
						sb.append("</select>\n");
					
					} else if (type.equals("itemSelect")) {
						if (!propertyConfig.attributeValue("caption", "").equals("")) {
							sb.append(NLT.getDef(propertyConfig.attributeValue("caption")));
							sb.append("\n<br>\n");
						}
						sb.append("<select name='propertyId_" + propertyId + "'>\n");
						sb.append("<option value=''>").append(NLT.get("definition.select_item_select")).append("</option>\n");
						
						//Get the list of items in this definition
						String itemSelectPath = propertyConfig.attributeValue("path", "");
						if (!itemSelectPath.equals("")) {
							Iterator itItems = this.sourceDocument.getRootElement().selectNodes(itemSelectPath).iterator();
							while (itItems.hasNext()) {
								//Build a list of the items
								Element selectedItem = (Element) itItems.next();
								Element selectedItemNameEle = (Element)selectedItem.selectSingleNode("properties/property[@name='name']");
								if (selectedItemNameEle == null) {continue;}
								Element selectedItemCaptionEle = (Element)selectedItem.selectSingleNode("properties/property[@name='caption']");
								if (selectedItemCaptionEle == null) {continue;}
								String selectedItemName = selectedItemNameEle.attributeValue("value", "");
								String selectedItemCaption = selectedItemCaptionEle.attributeValue("value", "");
								sb.append("<option value='").append(selectedItemName).append("'");
								if (selectedItemName.equals(propertyValue)) {
									sb.append(" selected");
								}
								sb.append(">").append(selectedItemCaption).append(" (").append(selectedItemName).append(")</option>\n");
							}
						}
						sb.append("</select>\n<br><br>\n");
					
					} else if (type.equals("replyStyle")) {
						if (!propertyConfig.attributeValue("caption", "").equals("")) {
							sb.append(NLT.getDef(propertyConfig.attributeValue("caption")));
							sb.append("\n<br>\n");
						}
						sb.append("<select multiple name='propertyId_" + propertyId + "'>\n");
						sb.append("<option value=''>").append(NLT.get("definition.select_reply_styles")).append("</option>\n");
						Iterator itEntryDefinitions = this.entryDefinitions.keySet().iterator();
						while (itEntryDefinitions.hasNext()) {
							//Build a list of the entry definitions
							Definition entryDef = (Definition) this.entryDefinitions.get((String) itEntryDefinitions.next());
							sb.append("<option value='").append(entryDef.getId()).append("'");
							Iterator itReplyStyles = sourceRoot.selectNodes("properties/property[@name='replyStyle']").iterator();
							while (itReplyStyles.hasNext()) {
								if (entryDef.getId().equals(((Element)itReplyStyles.next()).attributeValue("value", ""))) {
									sb.append(" selected");
									break;
								}
							}
							sb.append(">").append(entryDef.getTitle()).append(" (").append(entryDef.getName()).append(")</option>\n");
						}
						sb.append("</select>\n<br><br>\n");
					
					} else {
						if (!propertyConfig.attributeValue("caption", "").equals("")) {
							sb.append(NLT.getDef(propertyConfig.attributeValue("caption")));
							sb.append("\n<br>\n");
						}
						sb.append("<input type='text' name='propertyId_" + propertyId + "' size='40' ");
						sb.append("value=\""+propertyValue.replaceAll("\"", "&quot;")+"\" "+readonly+">\n");
					}
					//See if this property has any help
					Element help = (Element) propertyConfig.selectSingleNode("./help");
					if (help != null) {
						helpDivCount++;
						hb.append("<div id='help_div_" + rootElementId);
						hb.append(Integer.toString(helpDivCount));
						hb.append("' class='ss_helpPopUp'>\n");
						hb.append("<span class='ss_content'>");
						hb.append(NLT.getDef(help.getText()));
						hb.append("</span>\n</div>\n");
						sb.append("&nbsp;<a name='help_div_" + rootElementId);
						sb.append(Integer.toString(helpDivCount));
						sb.append("_a' onClick=\"activateMenuLayer('help_div_" + rootElementId);
						sb.append(Integer.toString(helpDivCount));
						sb.append("');return false;\"><img src='"+helpImgUrl+"'></a>\n");
					}
					
					sb.append("<input type='hidden' name='propertyName_" + propertyId + "' ");
					sb.append("value=\""+propertyName.replaceAll("\"", "&quot;")+"\">\n");
					sb.append("<br>\n");
				}
			}
			sb.append("<br>");
			sb.append("<input type='submit' name='okBtn' value=' Ok '>\n");
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			sb.append("<input type='submit' name='cancelBtn' value='Cancel'>\n");
			sb.append("<input type='hidden' name='definitionType_"+rootElementId+"' value='"+ rootElement.attributeValue("definitionType", "") +"'>\n");

			if (this.option.equals("")) {
				sb.append("</div>\n");
				sb.append("<script language='javascript'>\n");
				sb.append("    self.ss_setDeclaredDiv('properties_" + rootElementId + "')\n");
				sb.append("    idMap['"+rootElementId+"'] = '"+rootElementName+"';\n");
				sb.append("</script>\n");
			}
		}
	}
	
	private void buildDefaultDivs(StringBuffer sb, StringBuffer hb) {
		if (!this.divNames.containsKey("delete_item")) {
			this.divNames.put("delete_item", "1");
			sb.append("\n<div id='delete_item' ");
			sb.append("class='ss_definitionBuilder'>\n");
			sb.append("<span>This will delete the selected item and all of its children (if any).</span>\n");
			sb.append("<br>\n");
			sb.append("<span>Do you really want to delete the selected item?</span>\n");
			sb.append("<br>\n");
			sb.append("<input type='submit' name='okBtn' value='  OK  '>\n");
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			sb.append("<input type='submit' name='cancelBtn' value='Cancel'>\n");
			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('delete_item')\n");
			sb.append("</script>\n");
		}
		
		//Build the move_item divs
		if (!this.divNames.containsKey("move_item")) {
			this.divNames.put("move_item", "1");
			sb.append("\n<div id='move_item' ");
			sb.append("class='ss_definitionBuilder'>\n");
			sb.append("<span class='ss_titlebold'>Select the new location of the item to be moved</span><br>\n");
			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('move_item')\n");
			sb.append("</script>\n");
			sb.append("\n<div id='move_item_confirm' ");
			sb.append("class='ss_definitionBuilder'>\n");
			sb.append("<span class='ss_titlebold'>Move: <div id='moveItemSelection' style='display:inline;'></div></span>\n");
			sb.append("<br>\n");
			sb.append("<input type='radio' name='moveTo' value='above'>");
			sb.append("<span>Move to above the selected item<br>");
			sb.append("<input type='radio' name='moveTo' value='below'>");
			sb.append("<span>Move to below the selected item<br>");
			sb.append("<input type='radio' name='moveTo' value='into'>");
			sb.append("<span>Move into the selected item<br>");
			sb.append("<input type='submit' name='okBtn' value='  OK  '>\n");
			sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			sb.append("<input type='submit' name='cancelBtn' value='Cancel'>\n");
			sb.append("</div>\n");
			sb.append("<script language='javascript'>\n");
			sb.append("    self.ss_setDeclaredDiv('move_item_confirm')\n");
			sb.append("</script>\n");
		}

	}
	
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setTitle(String title) {
	    this.title = title;
	}
	
	public void setConfigDocument(Document configDocument) {
	    this.configDocument = configDocument;
	}

	public void setSourceDocument(Document sourceDocument) {
	    this.sourceDocument = sourceDocument;
	}

	public void setEntryDefinitions(Map entryDefinitions) {
	    this.entryDefinitions = entryDefinitions;
	}

	public void setOption(String option) {
	    this.option = option;
	}
	
	public void setItemId(String itemId) {
	    this.itemId = itemId;
	}
	
	public void setItemName(String itemName) {
	    this.itemName = itemName;
	}
	
}


