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
package org.kablink.teaming.taglib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.SortedMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionConfigurationBuilder;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.LocaleUtils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Html;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;

/**
 * ?
 * 
 * @author Peter Hurley
 */
@SuppressWarnings({"unchecked", "unused"})
public class BuildDefinitionDivs extends TagSupport {
    private String title;
    private Document configDocument;
    private Document sourceDocument;
	private Map divNames;
//    private Map entryDefinitions;
    private String option = "";
    private String itemId = "";
    private String itemName = "";
    private String refItemId="";
	private int helpDivCount;
	
	private String helpImgUrl = "";
	private Element rootElement;
	private String rootElementId;
	private String rootElementName;
	private Element rootConfigElement;
	private String contextPath;
	private String imagesPath;
	private DefinitionConfigurationBuilder configBuilder=DefinitionHelper.getDefinitionBuilderConfig();
	private Long binderId;
    private ProfileModule profileModule;
    private DefinitionModule definitionModule;
    
	@Override
	public int doStartTag() throws JspException {
		profileModule = (ProfileModule)SpringContextUtil.getBean("profileModule");
		definitionModule = (DefinitionModule)SpringContextUtil.getBean("definitionModule");
	    if(this.title == null)
	        throw new JspException("ssf:buildDefinitionDivs: The title must be specified.");
	    
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		    User user = RequestContextHolder.getRequestContext().getUser();
			contextPath = MiscUtil.getFullStaticPath(req);
			if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
			
			imagesPath = contextPath + "/images/";

			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			StringBuffer hb = new StringBuffer();
			
			//Clear the list of div names that have been created
			this.divNames = new HashMap();

			if (this.sourceDocument == null) {this.sourceDocument = this.configDocument;}

			Element configRoot = this.configDocument.getRootElement();
			Element root = this.sourceDocument.getRootElement();
			
			//See if we are just building a single option, or the whole page
			if (Validator.isNull(this.option)) {
				//Build the selection instructions div (only do this if building the whole page)
				sb.append("\n<div id=\"info_select\" class=\"ss_definitionBuilder\">\n");
				sb.append("<span class=\"ss_titlebold\">" + this.title + "</span>\n");
				sb.append("</div>\n");
				sb.append("<script type=\"text/javascript\">\n");
				//sb.append("    self.ss_setDeclaredDiv('info_select')\n");
				sb.append("    var idMap;\n");
				sb.append("    if (!idMap) {idMap = new Array();}\n");
				sb.append("    var idMapCaption;\n");
				sb.append("    if (!idMapCaption) {idMapCaption = new Array();}\n");
				sb.append("</script>\n");
				//Build the divs for each element
				buildDivs(configRoot, root, sb, hb, "");
				buildDivs(root, root, sb, hb, "item");
				buildPropertyInfoDivs(root, sb, hb);
				buildDefaultDivs(sb, hb);
			} else {	
				buildDivs(root, root, sb, hb, "item");
			}
			jspOut.print(hb.toString());
			jspOut.print(sb.toString());
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
	    finally {
	        option = "";
	        itemId = "";
	        itemName = "";
	        refItemId="";
	        binderId = null;
	    }
	    
		return SKIP_BODY;
	}
	
	private void buildDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		Iterator itRootElements;
		helpImgUrl = contextPath + "/images/pics/sym_s_help.gif";

		//See if this is a request for just one item
		if (Validator.isNotNull(this.option)) {
			if (Validator.isNotNull(this.itemId)) {
				//We are looking for a single item, so only do that item
				itRootElements = root.selectNodes("//item[@id='"+this.itemId+"'] | //definition[@name='"+this.itemId+"']").iterator();
			} else if (Validator.isNotNull(this.itemName)) {
				//	We are looking for an item in the definition config
				itRootElements = this.configDocument.getRootElement().selectNodes("//item[@name='"+this.itemName+"'] | //definition[@name='"+this.itemName+"']").iterator();
			} else  {
				//We are looking for the definition itself
				String definitionType = root.attributeValue("type", "");
				if (Validator.isNotNull(definitionType)) {
					itRootElements = root.selectNodes(".").iterator();
				} else {
					itRootElements = root.elementIterator();
				}
			}
		} else if (Validator.isNull(filter)) {
			itRootElements = root.elementIterator();
			Iterator itRootElements2 = root.elementIterator();
			//Only do this routine once
			sb.append("<script type=\"text/javascript\">\n");
			while (itRootElements2.hasNext()) {
				rootElement = (Element) itRootElements2.next();
				rootElementId = rootElement.attributeValue("id", rootElement.attributeValue("name"));
				rootElementName = rootElement.attributeValue("name");
					
				//Get the config version of this item
				rootConfigElement = null;
				if (rootElement.getDocument().getRootElement() == rootElement) {
					//This is the root node
					String definitionType = rootElement.attributeValue("type", "");
					if (!definitionType.equals("")) {
						rootConfigElement = (Element) this.configDocument.getRootElement().selectSingleNode("//item[@definitionType='"+definitionType+"']");
					}
				} else {
					rootConfigElement = configBuilder.getItem(this.configDocument, rootElementName);
				}
				if (rootConfigElement == null) {rootConfigElement = rootElement;}
					
				//Build the javascript map info
				buildMaps(root, sourceRoot, sb, hb, filter);
			}
			sb.append("</script>\n");
		} else {
			itRootElements = root.elementIterator(filter);
		}
		
		while (itRootElements.hasNext()) {
			rootElement = (Element) itRootElements.next();
			rootElementName = rootElement.attributeValue("name", "");
			rootElementId = rootElement.attributeValue("id", rootElementName);
			helpDivCount = 0;
			
			//Get the config version of this item
			rootConfigElement = null;
			if (rootElement.getDocument().getRootElement() == rootElement) {
				//This is the root node
				String definitionType = rootElement.attributeValue("type");
				if (Validator.isNotNull(definitionType)) {
					rootConfigElement = (Element) this.configDocument.getRootElement().selectSingleNode("//item[@definitionType='"+definitionType+"']");
				}
			} else {
				rootConfigElement = configBuilder.getItem(this.configDocument, rootElementName);
			}
			if (rootConfigElement == null) {rootConfigElement = rootElement;}
			
			//Build the information divs
			buildInfoDivs(root, sourceRoot, sb, hb, filter);


			//Build the operations divs
			buildOperationsDivs(root, sourceRoot, sb, hb, filter);

			//Build the options divs
			buildOptionsDivs(root, sourceRoot, sb, hb, filter);
			
			//Build the properties divs
			buildPropertiesDivs(root, sourceRoot, sb, hb, filter);
			
			//Build the help divs
//			buildHelpDivs(root, sourceRoot, sb, hb, filter);

			//See if this element has any sub elements to do
			if (Validator.isNull(this.option)) {
				buildDivs(rootElement, sourceRoot, sb, hb, "item");
			}
		}
	}

	private void buildMaps(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (Validator.isNull(this.option) && !this.divNames.containsKey("maps_"+rootElementId)) {
			this.divNames.put("maps_"+rootElementId, "1");
			String propertyCaptionValue = DefinitionUtils.getPropertyValue(rootElement, "caption");
			if (Validator.isNull(propertyCaptionValue)) {
				propertyCaptionValue = rootElement.attributeValue("caption", "");
			}
			//sb.append("self.ss_setDeclaredDiv('info_" + rootElementId + "')\n");
			sb.append("idMap['"+rootElementId+"'] = '"+rootElementName+"';\n");
			sb.append("idMapCaption['"+rootElementId+"'] = '"+NLT.getDef(propertyCaptionValue)
					.replaceAll("'", "\\\\'").replaceAll("&", "&amp;").replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;")+"';\n");
		}

	}
	
	private void buildInfoDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("info") && !this.divNames.containsKey("info_"+rootElementId)) {
			this.divNames.put("info_"+rootElementId, "1");
			sb.append("<span class=\"ss_bold\">" + NLT.getDef(rootElement.attributeValue("caption")));
			String propertyCaptionValue = DefinitionUtils.getPropertyValue(rootElement, "caption");
			if (Validator.isNull(propertyCaptionValue)) {
				propertyCaptionValue = rootElement.attributeValue("caption", "");
			}
			sb.append(" - " + propertyCaptionValue.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
			sb.append("</span>\n<br/><br/>\n");
		}

	}


	private void buildOperationsDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("operations") && !this.divNames.containsKey("operations_"+rootElementId)) {
			this.divNames.put("operations_"+rootElementId, "1");

			//Add the list of operations
			Element operations = rootConfigElement.element("operations");
			Element operationElement;
			if (operations == null) {
				//There are no operations listed in the definition file, so add in the standard operations
				//See if there are any options. If so, add the "add" command.
				operations = rootElement.addElement("operations");
				if (rootConfigElement.element("options") != null) {
					operationElement = operations.addElement("operation");
					operationElement.addAttribute("name", "addOption");
					operationElement.addAttribute("caption", "__add");
				}
				//if view element or no name properties(which are most likely unique
				if ("view".equals(rootElement.attributeValue("display","")) ||
						rootElement.selectNodes(".//properties/property[@name='name']").isEmpty()) {
					operationElement = operations.addElement("operation");
					operationElement.addAttribute("name", "copyItem");
					operationElement.addAttribute("caption", "__copy");
				}
				if (rootConfigElement.attributeValue("canBeDeleted", "true").equalsIgnoreCase("true")) {
					operationElement = operations.addElement("operation");
					operationElement.addAttribute("name", "deleteItem");
					operationElement.addAttribute("caption", "__delete");
				}
				if (rootConfigElement.element("properties") != null) {
					operationElement = operations.addElement("operation");
					operationElement.addAttribute("name", "modifyItem");
					operationElement.addAttribute("caption", "__modify");
				}
				
				operationElement = operations.addElement("operation");
				operationElement.addAttribute("name", "moveItem");
				operationElement.addAttribute("caption", "__move");
				
			} 
			
			Iterator itOperations = operations.elementIterator("operation");
			
			//Add a title for the div
			sb.append("<span class=\"ss_titlebold\">");
			String itemCaption = rootConfigElement.attributeValue("caption", "");
			if (!itemCaption.equals("")) sb.append(NLT.getDef(itemCaption));
			sb.append("</span>\n<br/><br/>\n");

			if (!itOperations.hasNext()) return;  //empty is legal and different then none at all
			sb.append("<table cellpadding=\"0\" cellspacing=\"0\"><tbody>\n");
			while (itOperations.hasNext()) {
				sb.append("<tr><td>\n");
				operationElement = (Element) itOperations.next();
				String operationElementId = operationElement.attributeValue("id", operationElement.attributeValue("name"));
				String operationElementName = operationElement.attributeValue("name");
				
				sb.append("<a href=\"javascript: ;\" "); 
				sb.append("onClick=\"self."+operationElementId+"('" + operationElementId + "', '" + 
						operationElementName + "', '" + 
						operationElement.attributeValue("item", "") + "'); return false;\">");
				sb.append(NLT.getDef(operationElement.attributeValue("caption", ""))
						.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
				sb.append("</a>\n");
				sb.append("</td></tr>\n");
			}

			sb.append("</tbody></table>\n");
		}
	}
	
	private void buildOptionsDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("options") && !this.divNames.containsKey("options_"+rootElementId)) {
			this.divNames.put("options_"+rootElementId, "1");

			//Add a title for the div
			sb.append("<span class=\"ss_titlebold\">");
			String itemCaption = rootConfigElement.attributeValue("caption", "");
			if (!itemCaption.equals("")) sb.append(NLT.getDef(itemCaption));
			sb.append("</span>\n<br/><br/>\n");

			//Add the list of options
			Element e_options = rootConfigElement.element("options");
			if (e_options == null) return;
			Element e_option;
			Map optionsSeen = new HashMap();
			//keep sorted list of options
			Map<String, StringBuffer[]> optionsMap = new TreeMap();
			String optionCaption = "";
			if (e_options != null) {
				optionCaption = NLT.getDef(e_options.attributeValue("optionCaption", ""))
					.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				if (Validator.isNotNull(optionCaption)) {
					sb.append("<b>").append(optionCaption).append("</b>\n");
				}
			}

			sb.append("<ul>\n");
			if (e_options != null) {
				Iterator itOptions = e_options.elementIterator();
				while (itOptions.hasNext()) {
					e_option = (Element) itOptions.next();
					//See if this search is limited to a particular definition type (e.g., COMMAND or PROFILE_ENTRY_VIEW)
					String optionDefinitionType = e_option.attributeValue("definitionType");
					if (Validator.isNotNull(optionDefinitionType)) {
						//This request is for a specific definition type.
						//Check that the definition type from the actual definition matches the desired type.
						if (!optionDefinitionType.equals(sourceRoot.attributeValue("type", ""))) continue;
					}
					String optionFormat = e_option.getName();
					if ("option".equals(optionFormat)) {
						String optionName = e_option.attributeValue("name");
						//Find this item in the definition config
						Element optionItem = (Element) configBuilder.getItem(configDocument, optionName);
						if (optionItem == null) continue;
						if (optionsSeen.containsKey(optionName)) continue;
						//if the option item is a dataView item, process the select now
						String optionType = optionItem.attributeValue("type");
						Element optionSelect = null;
						if ("dataView".equals(optionType)) optionSelect = (Element)optionItem.selectSingleNode("./option_entry_data");
						//this is the item
						if (optionSelect == null) {
							if (testOption(optionItem, sourceRoot, optionName)) {
								addOption(optionsMap, optionItem, optionName);
								optionsSeen.put(optionName, optionName);
							}
						
						} else {
							//flag that already handled
							optionsSeen.put(optionName, optionName);
							//build list of names to exclude
							List<Element> excludes = optionSelect.selectNodes("./exclude");
							List excludeNames = new ArrayList();
							for (Element ex:excludes) {
								excludeNames.add(ex.attributeValue("name"));
							}
							String optionPath = optionSelect.attributeValue("path");
							optionType = optionSelect.attributeValue("select_type");
							if (Validator.isNull(optionType)) optionType = null; //get rid of null string
							//search for items to include
							List<Element> selectedItems = sourceRoot.selectNodes(optionPath);
							for (Element selItem:selectedItems) {
								if (optionType != null) {
									//check if item is the correct type
									if (!optionType.equals(selItem.attributeValue("type"))) continue;
								}
								//this won't work if these items are nested
								String selName = selItem.attributeValue("name");
								if (excludeNames.contains(selName)) continue;
								String optionId = selItem.attributeValue("id", optionName);
								//	See if multiple are allowed
								if (testOption(selItem, sourceRoot, optionName)) {
									addDataOption(optionsMap, selItem, optionName, optionId);
								}
							}	
						}					
					}  else {				
					
						String optionPath = e_option.attributeValue("path", "");
						Iterator itOptionsSelect = rootConfigElement.selectNodes(optionPath).iterator();
						if (!itOptionsSelect.hasNext()) continue;

						//build list of names to exclude
						List<Element> excludes = e_option.selectNodes("./exclude");
						List excludeNames = new ArrayList();
						for (Element ex:excludes) {
							excludeNames.add(ex.attributeValue("name"));
						}

						optionCaption = NLT.getDef(e_option.attributeValue("optionCaption", ""))
							.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
						if (!optionCaption.equals("")) {
							//flush contents of current buffers to make way for new header
							for (Map.Entry<String, StringBuffer[]> me: optionsMap.entrySet()) {
								sb.append(me.getValue()[0]);
								hb.append(me.getValue()[1]);
							}
							optionsMap.clear();
							sb.append("</ul>\n<br/>\n<b>").append(optionCaption).append("</b>\n<ul>\n");
						}
						while (itOptionsSelect.hasNext()) {
							Element optionSelectItem = (Element) itOptionsSelect.next();
							String optionSelectName = optionSelectItem.attributeValue("name");
							//this won't work if these items are nested
							if (excludeNames.contains(optionSelectName)) continue;
							//See if multiple are allowed
							if (optionsSeen.containsKey(optionSelectName)) continue;
							//flag that already handled
							optionsSeen.put(optionSelectName, optionSelectName);
							String optionType = optionSelectItem.attributeValue("type");
							Element optionSelect = null;
							if ("dataView".equals(optionType)) optionSelect = (Element)optionSelectItem.selectSingleNode("./option_entry_data");
							if (optionSelect == null) {
								if (testOption(optionSelectItem, sourceRoot, optionSelectName)) {
									addOption(optionsMap, optionSelectItem, optionSelectName);
									optionsSeen.put(optionSelectName, optionSelectName);
								}
							} else {
								//build list of names to exclude
								excludes = optionSelect.selectNodes("./exclude");
								excludeNames = new ArrayList();
								for (Element ex:excludes) {
									excludeNames.add(ex.attributeValue("name"));
								}
								optionPath = optionSelect.attributeValue("path");
								optionType = optionSelect.attributeValue("select_type");
								if (Validator.isNull(optionType)) optionType = null; //get rid of null string
								//search for items to include
								List<Element> selectedItems = sourceRoot.selectNodes(optionPath);
								for (Element selItem:selectedItems) {
									if (optionType != null) {
										//	check if item is the correct type
										if (!optionType.equals(selItem.attributeValue("type"))) continue;
									}
									//this won't work if these items are nested
									String selName = selItem.attributeValue("name");
									if (excludeNames.contains(selName)) continue;
									String optionId = selItem.attributeValue("id", optionSelectName);
									//	See if multiple are allowed
									if (testOption(selItem, sourceRoot, optionSelectName)) {
										addDataOption(optionsMap, selItem, optionSelectName, optionId);
									}
								}
							}	
						}
					}
				}
			}
			//flush contents of current buffers to make way for new header
			for (Map.Entry<String, StringBuffer[]> me: optionsMap.entrySet()) {
				sb.append(me.getValue()[0]);
				hb.append(me.getValue()[1]);
			}

			sb.append("</ul>\n");
			sb.append("<br/>\n");
		}
	}
	//see if new item will meet uniqueness constaints
	protected boolean testOption(Element item, Element sourceRoot, String name) {
		if (!DefinitionHelper.checkIfMultipleAllowed(rootElement, configDocument.getRootElement())) return false;

		if (rootElement == null) return true;

		if (item.attributeValue("multipleAllowedInParent", "").equalsIgnoreCase("false") && 
				rootElement.selectSingleNode("item[@name='"+name+"']") != null) return false;
				
		String unq = item.attributeValue("unique");
		if (Validator.isNull(unq)) return true; 
		List results = rootElement.selectNodes(unq);
		if ((results == null) || results.isEmpty()) return true;
		return false;
		
	}
	//item points to config
	protected void addOption(Map<String, StringBuffer[]> optionsMap, Element item, String name) {
		StringBuffer sb=new StringBuffer();
		StringBuffer hb = new StringBuffer();
		sb.append("<li>");
		sb.append("<a href=\"javascript: ;\" onClick=\"showProperties('"+name+"', '');return false;\">");
		String caption = NLT.getDef(item.attributeValue("caption",name));
		sb.append(caption);
		sb.append("</a>");
		addOptionHelp(item, sb, hb);
		sb.append("</li>\n");
		//build sorted list 
		optionsMap.put(caption, new StringBuffer[]{sb,hb});
		
	}
	//item points to instance definition
	protected void addDataOption(Map<String, StringBuffer[]> optionsMap, Element item, String name, String refId) {
		StringBuffer sb=new StringBuffer();
		StringBuffer hb = new StringBuffer();
		sb.append("<li>");
		sb.append("<a href=\"javascript: ;\" onClick=\"showProperties('"+name+"', '"+refId+"');return false;\">");
		String caption = NLT.getDef(DefinitionUtils.getPropertyValue(item, "caption"));
		if (Validator.isNull(caption)) {
			caption = DefinitionUtils.getPropertyValue(item, "name");
		} else {
			if (name.equals("entryDataItem")) {
				caption += " (" + DefinitionUtils.getPropertyValue(item, "name") + ")";
			}
		}
		sb.append(caption.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
		sb.append("</a>");
		addOptionHelp(item, sb, hb);
		sb.append("</li>\n");
		//build sorted list 
		optionsMap.put(caption, new StringBuffer[]{sb,hb});
		
	}
	private void addOptionHelp(Element item, StringBuffer sb, StringBuffer hb) {
		//See if this item has any help
		Element help = (Element) item.selectSingleNode("./help");
		if (help != null) {
			helpDivCount++;
			hb.append("<div align=\"left\" id=\"help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
			hb.append(Integer.toString(helpDivCount));
			hb.append("\" class=\"ss_helpPopUp\" style=\"visibility:hidden;\">\n");
			hb.append("<span>");
			hb.append(NLT.get(help.getText()));
			hb.append("</span>\n</div>\n");
			sb.append(" <a name=\"help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
			sb.append(Integer.toString(helpDivCount));
			sb.append("_a\" onClick=\"ss_activateMenuLayerMove('help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
			sb.append(Integer.toString(helpDivCount));
			sb.append("');return false;\"><img alt=\"" + NLT.get("alt.help") + "\" border=\"0\" src=\""+helpImgUrl+"\"/></a>\n");
		}
		
	}
	private void buildPropertiesDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		//Build the properties div
		if ((this.option.equals("properties")) && 
				!this.divNames.containsKey("properties_"+rootElementId)) {
			this.divNames.put("properties_"+rootElementId, "1");
			
			//Add a title for the div
			sb.append("<span class=\"ss_titlebold\">");
			String itemCaption = rootConfigElement.attributeValue("caption", "");
			if (!itemCaption.equals("")) sb.append(NLT.getDef(itemCaption));
			sb.append("</span>\n<br/><br/>\n");
			//Add the list of properties
			Element propertiesConfig = rootConfigElement.element("properties");
			if (propertiesConfig == null) return;
			String  itemType = rootConfigElement.attributeValue("type");
			//	see if we are adding a "dataView" element
			Element properties=null;
			//if adding a new dataView item, see if we have a reference to the data item
			if (Validator.isNotNull(this.refItemId) && "dataView".equals(itemType)) {
				Element refItem = (Element)sourceRoot.selectSingleNode("//item[@id='"+this.refItemId+"'] ");
				if (refItem != null) properties = refItem.element("properties");
			}	 

			if (properties == null) properties = rootElement.element("properties");
			Iterator itProperties = propertiesConfig.elementIterator("property");
				while (itProperties.hasNext()) {
					//Get the next property from the base config file
					Element propertyConfig = (Element) itProperties.next();
					//See if this search is limited to a particular definition type (e.g., COMMAND or PROFILE_ENTRY_VIEW)
					String propDefinitionType = propertyConfig.attributeValue("definitionType");
					if (Validator.isNotNull(propDefinitionType)) {
						//This request is for a specific definition type.
						//Check that the definition type from the actual definition matches the desired type.
						if (!propDefinitionType.equals(sourceRoot.attributeValue("type", ""))) continue;
					}
					//Get the name and id (if any) from the config file
					String propertyId = propertyConfig.attributeValue("id", propertyConfig.attributeValue("name", ""));
					String propertyName = propertyConfig.attributeValue("name", "");
					String readonly = "";
					if (propertyConfig.attributeValue("readonly", "false").equalsIgnoreCase("true")) {
						readonly = "readonly=\"true\" disabled=\"disabled\"";
					}
					String propertyConfigCaption = NLT.getDef(propertyConfig.attributeValue("caption", ""))
						.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
					List propertyValues = new ArrayList();
					if (properties != null) {
						//See if there are already values for this property in the actual definition file
						List propertyNameElements = (List) properties.selectNodes("property[@name='"+propertyId+"']");
						if (propertyNameElements != null) {
							for (int i = 0; i < propertyNameElements.size(); i++) {
								String propertyValue = "";
								if (((Element)propertyNameElements.get(i)).hasContent() &&
										((Element)propertyNameElements.get(i)).isTextOnly()) {
									propertyValue = ((Element)propertyNameElements.get(i)).getText();
								} else {
									propertyValue = ((Element)propertyNameElements.get(i)).attributeValue("value", "");
								}
								if (!propertyValue.equals("")) {
									//Add this value to the list so it can be used to set "selected" or "checked" values later
									propertyValues.add(propertyValue);
								}
							}
						}
					}
					String propertyValue0 = "";
					if (propertyValues.size() > 0) {
						propertyValue0 = (String) propertyValues.get(0);
					} else {
						//if no value assigned and deprecated, don't show it
						if (propertyConfig.attributeValue("status", "").equals("deprecated")) continue;
					}
					String propertyValueDefault = propertyConfig.attributeValue("default", "");
					String type = propertyConfig.attributeValue("type", "text");
					if (type.equals("textarea")) {
						sb.append(propertyConfigCaption);
						sb.append("\n<br/>\n");
						sb.append("<textarea name=\"propertyId_" + propertyId + "\" rows=\"6\" cols=\"45\" "+readonly+">"+Html.formatTo(propertyValue0)+"</textarea>\n<br/>\n");
					
					} else if (type.equals("transitionOnDate")) {
						sb.append(propertyConfigCaption);
						HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
						HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
						RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/definition_builder/date_select.jsp");

						ServletRequest req = null;
						req = new DynamicServletRequest(httpReq);
						req.setAttribute("propertyId", propertyId);
						req.setAttribute("propertyValue", propertyValue0);
						
						StringServletResponse res = new StringServletResponse(httpRes);
						try {
							rd.include(req, res);
							sb.append(res.getString().replaceAll("&", "&amp;"));
						} catch(Exception e) {}
						
					} else if (type.equals("boolean") || type.equals("checkbox")) {
						String checked = "";
						if (propertyValue0.equals("")) {
							if (propertyConfig.attributeValue("default", "false").equalsIgnoreCase("true")) {
								checked = "checked=\"checked\"";
							}
						} else if (propertyValue0.equalsIgnoreCase("true")) {
							checked = "checked=\"checked\"";
						}
						sb.append("<div><input type=\"checkbox\" class=\"ss_text\" name=\"propertyId_" + propertyId + "\" "+checked+" "+readonly+"/> ");
						sb.append(NLT.getDef(propertyConfig.attributeValue("caption", ""))
								.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
						sb.append("</div>");
					
					} else if (type.equals("selectbox") || type.startsWith("familySelectbox")|| 
							type.equals("radio")) {
						int optionCount = 0;
						boolean selectedSeen = false;
						if (type.equals("selectbox") || type.startsWith("familySelectbox")) {
							sb.append("<table>\n<tbody>\n<tr>\n<td>\n");
							sb.append("<span class=\"ss_bold\">" + propertyConfigCaption + "</span>");
							sb.append("\n</td>\n<td>\n");
							//See if multiple selections are allowed
							String multipleText = "";
							String sizeText = "";
							if (propertyConfig.selectNodes("option").size() > 1) {
								int count = 0;
								Iterator  itSelections = propertyConfig.elementIterator("option");
								while (itSelections.hasNext()) {
									Element selection = (Element) itSelections.next();
									if (type.startsWith("familySelectbox") && 
											Utils.checkIfFilr()) {
										if (Utils.checkIfFilrFamily(type, selection.attributeValue("name", ""))) {
											count++;
											for (int i = 0; i < propertyValues.size(); i++) {
												if (((String)propertyValues.get(i)).equals(selection.attributeValue("name", ""))) {
													//Remember that one of these properties is selected
													selectedSeen = true;
													break;
												}
											}
										}
									} else {
										count++;
										for (int i = 0; i < propertyValues.size(); i++) {
											if (((String)propertyValues.get(i)).equals(selection.attributeValue("name", ""))) {
												//Remember that one of these properties is selected
												selectedSeen = true;
												break;
											}
										}
									}
								}
								if (count > 0) {
									sizeText = " size=\"" + String.valueOf(count) + "\"";
								}
							}
							if (propertyConfig.attributeValue("multipleAllowed", "").equals("true")) multipleText = "multiple=\"multiple\"";
							sb.append("<select name=\"propertyId_" + propertyId + "\" " + multipleText + sizeText + ">\n");
						} else if (type.equals("radio")) {
							sb.append("<span class=\"ss_bold\">" + propertyConfigCaption + "</span>");
							sb.append("<table><tbody>");
						}
						//See if there are any built-in options
						Iterator  itSelections = propertyConfig.elementIterator("option");
						while (itSelections.hasNext()) {
							Element selection = (Element) itSelections.next();
							String checked = "";
							for (int i = 0; i < propertyValues.size(); i++) {
								if (((String)propertyValues.get(i)).equals(selection.attributeValue("name", ""))) {
									checked = " selected=\"selected\"";
									if (type.equals("radio")) checked = " checked=\"checked\"";
									break;
								}
							}
							if ((propertyValues.size() == 0 && !propertyValueDefault.equals("") && 
									propertyValueDefault.equals(selection.attributeValue("name", "")))) {
								checked = " selected=\"selected\"";
								if (type.equals("radio")) checked = " checked=\"checked\"";							}
							if (type.equals("selectbox") || type.startsWith("familySelectbox")) {
								boolean allowed = true;
								if (type.startsWith("familySelectbox") && 
										Utils.checkIfFilr()) {
									allowed = Utils.checkIfFilrFamily(type, selection.attributeValue("name", ""));
								}
								if (allowed) {
									if (!selectedSeen && checked.equals("")) {
										//Nothing has been selected, so select the first one in the list
										checked = " selected=\"selected\"";
									}
									selectedSeen = true;
									sb.append("<option value=\"").append(selection.attributeValue("name", "")).append("\"").append(checked).append(">");
									sb.append(NLT.getDef(selection.attributeValue("caption", selection.attributeValue("name", "")))
											.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
									sb.append("</option>\n");
									optionCount++;
								}
							} else if (type.equals("radio")) {
								sb.append("<tr><td valign='top'>\n");
								sb.append("<input type=\"radio\" class=\"ss_text\" name=\"propertyId_" + propertyId + "\" value=\"");
								sb.append(selection.attributeValue("name", ""));
								sb.append("\"").append(checked).append("/>\n</td><td>");
								sb.append(NLT.getDef(selection.attributeValue("caption", selection.attributeValue("name", "")))
									.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
								sb.append("\n</td></tr>\n");
							}
						}
						//See if there are any items to be shown from the "sourceRoot"
						itSelections = propertyConfig.elementIterator("option_entry_data");
						while (itSelections.hasNext()) {
							Element selection = (Element) itSelections.next();
							String selectionSelectType = selection.attributeValue("select_type", "");
							String selectionPath = selection.attributeValue("path", "");
							//Build a list of items to not include in the select box
							List excludeList = new ArrayList();
							Iterator itExcludes = selection.selectNodes("./exclude").iterator();
							while (itExcludes.hasNext()) {
								String excludeName = ((Element)itExcludes.next()).attributeValue("name", "");
								if (!excludeName.equals("")) excludeList.add(excludeName);
							}
							//Select the data items from the actual definition, not from the base configuration definition
							Iterator itEntryFormElements = sourceRoot.selectNodes(selectionPath).iterator();
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
								//Is this item to be excluded?
								if (excludeList.contains(entryFormItemNamePropertyName)) continue;
								
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
										for (int i = 0; i < propertyValues.size(); i++) {
											if (((String)propertyValues.get(i)).equals(entryFormItemNamePropertyName)) {
												checked = " selected=\"selected\"";
												if (type.equals("radio")) checked = " checked=\"checked\"";
												break;
											}
										}
										if ((propertyValues.size() == 0 && !propertyValueDefault.equals("") && 
												propertyValueDefault.equals(entryFormItemNamePropertyName))) {
											checked = " selected=\"selected\"";
											if (type.equals("radio")) checked = " checked=\"checked\"";
										}

										if (type.equals("selectbox") || type.startsWith("familySelectbox")) {
											sb.append("<option value=\"").append(entryFormItemNamePropertyName).append("\"").append(checked).append(">");
											sb.append(NLT.getDef(entryFormItemCaptionPropertyValue));
											sb.append("</option>\n");
											optionCount++;
										} else if (type.equals("radio")) {
											sb.append("<tr><td valign='top'>\n");
											sb.append("<input type=\"radio\" class=\"ss_text\" name=\"propertyId_" + propertyId + "\" value=\"");
											sb.append(entryFormItemNamePropertyName);
											sb.append("\"").append(checked).append("/>\n</td><td>");
											sb.append(NLT.getDef(selection.attributeValue("caption", selection.attributeValue("name", "")))
													.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
											sb.append("\n</td></tr>\n");
										}
									}
								}
							}
						}
						
						if (type.equals("selectbox") || type.startsWith("familySelectbox")) {
							if (optionCount == 0) {
								//No options were output, show something to avoid having an empty select box
								sb.append("<option value=\"\">"+NLT.get("definition.noOptions")+"</option>\n");
							}
							sb.append("</select>\n</td>\n</tr>\n</tbody>\n</table>\n<br/>\n");
						} else if (type.equals("radio")) {
							sb.append("</tbody></table><br/>\n");
						}
					
					} else if (type.equals("itemSelect")) {
						sb.append(propertyConfigCaption);
						//Get the list of items in this definition
						String itemSelectPath = propertyConfig.attributeValue("path", "");
						if (!itemSelectPath.equals("")) {
							int size = this.sourceDocument.getRootElement().selectNodes(itemSelectPath).size();
							if (size > 0) {
								if (size > 10) size = 10;
								String sizeAttr = " size=\"" + String.valueOf(size+1) + "\" ";
								Iterator itItems = this.sourceDocument.getRootElement().selectNodes(itemSelectPath).iterator();
								String multiple = "";
								if (propertyConfig.attributeValue("multipleAllowed", "").equalsIgnoreCase("true")) multiple = "multiple=\"multiple\"";
								sb.append("<select name=\"propertyId_" + propertyId + "\" " + multiple + sizeAttr + ">\n");
								sb.append("<option value=\"\">").append(NLT.get("definition.select_item_select")).append("</option>\n");
								
								while (itItems.hasNext()) {
									//Build a list of the items
									Element selectedItem = (Element) itItems.next();
									Element selectedItemNameEle = (Element)selectedItem.selectSingleNode("properties/property[@name='name']");
									if (selectedItemNameEle == null) {continue;}
									Element selectedItemCaptionEle = (Element)selectedItem.selectSingleNode("properties/property[@name='caption']");
									if (selectedItemCaptionEle == null) {continue;}
									String selectedItemName = selectedItemNameEle.attributeValue("value", "");
									String selectedItemCaption = selectedItemCaptionEle.attributeValue("value", "")
										.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
									sb.append("<option value=\"").append(selectedItemName).append("\"");
									for (int i = 0; i < propertyValues.size(); i++) {
										if (((String)propertyValues.get(i)).equals(selectedItemName)) {
											sb.append(" selected=\"selected\"");
											break;
										}
									}
									sb.append(">").append(selectedItemCaption).append(" (").append(selectedItemName).append(")</option>\n");
								}
								sb.append("</select>\n<br/><br/>\n");
							} else {
								sb.append("<i>").append(NLT.get("definition.selectNoneDefined")).append("</i>\n<br/><br/>");
							}
						}
					
					} else if (type.equals("replyStyle")) {
						sb.append("<label for=\"propertyId_" + 
								propertyId + "\">" + propertyConfigCaption + "</label>");

						SortedMap<String, Definition> defs = DefinitionHelper.getAvailableReplyDefinitions(binderId);
						int size = defs.size();
						if (size <= 0) size = 1;
						sb.append("<select multiple=\"multiple\" name=\"propertyId_" + 
								propertyId + "\" id=\"propertyId_" + 
								propertyId + "\" size=\"" + String.valueOf(size+1) + "\">\n");
						sb.append("<option value=\"\">").append(NLT.get("definition.select_reply_styles")).append("</option>\n");
						List<Element> replyStyles = sourceRoot.selectNodes("properties/property[@name='replyStyle']");
						List<String>replyIds = new ArrayList();
						for (Element reply:replyStyles) {
							String id = reply.attributeValue("value");
							if (Validator.isNotNull(id)) replyIds.add(id);
						}
						
						for (Map.Entry<String, Definition> me:defs.entrySet()) {
							//Build a list of the entry definitions
							Definition entryDef = me.getValue();
							sb.append("<option value=\"").append(entryDef.getId()).append("\"");
							if (replyIds.contains(entryDef.getId())) {
								sb.append(" selected=\"selected\"");
							}
							sb.append(">").append(me.getKey().replaceAll("&", "&amp;")
									.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</option>\n");								
						}

						sb.append("</select>\n<br/><br/>\n");
					
					} else if (type.equals("iconList")) {
						sb.append(propertyConfigCaption);
						String iconListPath = propertyConfig.attributeValue("path", "");
						String[] iconList = SPropsUtil.getCombinedPropertyList(iconListPath, ObjectKeys.CUSTOM_PROPERTY_PREFIX);
						Element iconValueEle = (Element)sourceRoot.selectSingleNode("properties/property[@name='icon']");
						String iconValue = "";
						if (iconValueEle != null) iconValue = iconValueEle.attributeValue("value", "");
						for (int i = 0; i < iconList.length; i++) {
							String iconListValue = iconList[i].trim();
							if (iconListValue.equals("")) continue;
							String checked = "";
							if (iconValue.equals(iconListValue)) {
								checked = " checked=\"checked\"";
							}
							sb.append("<input type=\"radio\" class=\"ss_text\" name=\"propertyId_" + propertyId + "\" value=\"");
							sb.append(iconListValue);
							sb.append("\"").append(checked).append("/>");
							sb.append("<img alt=\"\" border=\"0\" src=\"").append(imagesPath).append(iconListValue).append("\"/>");
							sb.append("<br/><br/>\n");
						}
					
					} else if (type.equals("locale")) {
						sb.append(propertyConfigCaption);
						Element localeValueEle = (Element)rootElement.selectSingleNode("properties/property[@name='default']");
						sb.append("<br/><select name=\"propertyId_" + 
								propertyId + "\" id=\"propertyId_" + 
								propertyId + "\" >\n");
						Set<java.util.Locale> ids = NLT.getLocales();
						User user = RequestContextHolder.getRequestContext().getUser();
						TreeMap<String,Locale> map = new TreeMap(new StringComparator(user.getLocale())); //sort
						for (Locale lc:ids) {
							map.put(lc.getDisplayName(user.getLocale()), lc);
						}
						if (user != null) { //make sure current users locale appears
							map.put(user.getLocale().getDisplayName(user.getLocale()), user.getLocale());
						}
						Locale userLocale = null;
			    		String language = LocaleUtils.getLocaleLanguage();
			    		String country = LocaleUtils.getLocaleCountry();
			    		if (!language.equals("")) {
			    			if (!country.equals("")) userLocale = new Locale(language, country);
			    			else userLocale = new Locale(language);
			    		}
						if (userLocale == null) {
							if (user != null) userLocale = user.getLocale();
							else userLocale = NLT.getTeamingLocale();
						}
						Locale defLocale = new Locale(userLocale.toString());
						if (localeValueEle != null && !localeValueEle.attributeValue("value", "").equals(""))
							defLocale = new Locale((String)localeValueEle.attributeValue("value"));
						for (Map.Entry<String, Locale> me : map.entrySet()) {
							String checked = "";
							if (me.getValue().toString().toLowerCase().equals(defLocale.toString().toLowerCase())) checked="selected=\"selected\"";
							sb.append("<option value=\""+me.getValue().toString()+"\" "+checked+">").append(me.getKey()).append("</option>\n");
						};
						sb.append("</select>\n<br/><br/>\n");
					
					} else if (type.equals("repositoryList")) {
						int optionCount = 0;
						sb.append(propertyConfigCaption);
						//See if multiple selections are allowed
						String multipleText = "";
						if (propertyConfig.attributeValue("multipleAllowed", "").equals("true")) multipleText = "multiple=\"multiple\"";
						sb.append("<select name=\"propertyId_" + propertyId + "\" " + multipleText + ">\n");

						//Get the default value for the repository
						propertyValueDefault = RepositoryUtil.getDefaultRepositoryName();
						if (propertyValueDefault.equals("")) propertyConfig.attributeValue("default", "");

						String[] repositoryList = SPropsUtil.getCombinedPropertyList(
								ObjectKeys.CONFIG_PROPERTY_REPOSITORIES, ObjectKeys.CUSTOM_PROPERTY_PREFIX);
						for (int i = 0; i < repositoryList.length; i++) {
							String repository = repositoryList[i];
							String checked = "";
							for (int j = 0; j < propertyValues.size(); j++) {
								if (((String)propertyValues.get(j)).equals(repository)) {
									checked = " selected=\"selected\"";
								}
							}
							if ((propertyValues.size() == 0 && !propertyValueDefault.equals("") && 
									propertyValueDefault.equals(repository))) {
								checked = " selected=\"selected\"";
							}
							sb.append("<option value=\"").append(repository).append("\"").append(checked).append(">");
							sb.append(NLT.get(ObjectKeys.CONFIG_PROPERTY_REPOSITORY + "." + repository));
							sb.append("</option>\n");
							optionCount++;
						}
						if (optionCount == 0) {
							//No options were output, show something to avoid having an empty select box
							sb.append("<option value=\"\">"+NLT.get("definition.noOptions")+"</option>\n");
						}
						sb.append("</select><br/><br/>\n");
					
					} else if (type.equals("workflowCondition")) {
						Element workflowConditionProperty = (Element)rootElement.selectSingleNode("properties/property[@name='condition']");
						if (workflowConditionProperty != null) {
							Element workflowConditionEle = (Element) workflowConditionProperty.selectSingleNode("workflowCondition");
							if (workflowConditionEle != null) {
								//We have the current condition element; print out its values
								String definitionId = workflowConditionEle.attributeValue("definitionId", "");
								String elementName = workflowConditionEle.attributeValue("elementName", "");
								String operation = workflowConditionEle.attributeValue("operation", "");
								String duration = workflowConditionEle.attributeValue("duration", "");
								String durationType = workflowConditionEle.attributeValue("durationType", "");
								//Get the entry definition itself
								Definition def = DefinitionHelper.getDefinition(definitionId);
								if (def != null) {
									//Put the previous values into hidden fields in case these are not changed
									sb.append("<input type=\"hidden\" name=\"previous_conditionDefinitionId\"");
									sb.append(" value=\""+definitionId+"\" />\n");
									sb.append("<input type=\"hidden\" name=\"previous_conditionElementName\"");
									sb.append(" value=\""+elementName+"\" />\n");
									sb.append("<input type=\"hidden\" name=\"previous_conditionElementOperation\"");
									sb.append(" value=\""+operation+"\" />\n");
									if (!duration.equals("") && !durationType.equals("")) {
										sb.append("<input type=\"hidden\" name=\"previous_operationDuration\"");
										sb.append(" value=\""+duration+"\" />\n");
										sb.append("<input type=\"hidden\" name=\"previous_operationDurationType\"");
										sb.append(" value=\""+durationType+"\" />\n");
									}
									//See if there are values
									Iterator it_workflowConditionValues = workflowConditionEle.elementIterator("value");
									while (it_workflowConditionValues.hasNext()) {
										sb.append("<input type=\"hidden\" name=\"previous_conditionElementValue\"");
										sb.append(" value=\""+((Element)it_workflowConditionValues.next()).getText()+"\" />\n");
									}
									
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.workflowCondition"));
									sb.append("</span><br/><br/>");
									sb.append("<table class=\"ss_form\"><tbody>");
									sb.append("<tr>");
									sb.append("<td valign=\"top\">");
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.currentWorkflowConditionEntryType"));
									sb.append("</span>");
									sb.append("</td>");
									sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
									if (Definition.VISIBILITY_DEPRECATED.equals(def.getVisibility())) {
										sb.append("<del>").append(NLT.getDef(def.getTitle()).replaceAll("&", "&amp;")
											.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</del>");
									} else {
										sb.append(NLT.getDef(def.getTitle()).replaceAll("&", "&amp;")
												.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
									}
									sb.append("</td>");
									sb.append("</tr>");
									
									sb.append("<tr>");
									sb.append("<td valign=\"top\">");
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.currentWorkflowConditionElementName"));
									sb.append("</span>");
									sb.append("</td>");
									sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
									sb.append(elementName);
									sb.append("</td>");
									sb.append("</tr>");
									
									sb.append("<tr>");
									sb.append("<td valign=\"top\">");
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.currentWorkflowConditionOperation"));
									sb.append("</span>");
									sb.append("</td>");
									sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
									if (operation.equals("equals")) {
										sb.append(NLT.get("definition.operation_equals"));
									} else if (operation.equals("contains")) {
										sb.append(NLT.get("definition.operation_containsText"));
									} else if (operation.equals("greaterThan")) {
										sb.append(NLT.get("definition.operation_greaterThan"));
									} else if (operation.equals("lessThan")) {
										sb.append(NLT.get("definition.operation_lessThan"));
									} else if (operation.equals("started")) {
										sb.append(NLT.get("definition.operation_started"));
									} else if (operation.equals("ended")) {
										sb.append(NLT.get("definition.operation_ended"));
									} else if (operation.equals("datePassed")) {
										sb.append(NLT.get("definition.operation_datePassed"));
									} else if (operation.equals("beforeStart")) {
										sb.append(NLT.get("definition.operation_beforeStart"));
									} else if (operation.equals("afterStart")) {
										sb.append(NLT.get("definition.operation_afterStart"));
									} else if (operation.equals("beforeEnd")) {
										sb.append(NLT.get("definition.operation_beforeEnd"));
									} else if (operation.equals("afterEnd")) {
										sb.append(NLT.get("definition.operation_afterEnd"));
									} else if (operation.equals("beforeDate")) {
										sb.append(NLT.get("definition.operation_beforeDate"));
									} else if (operation.equals("afterDate")) {
										sb.append(NLT.get("definition.operation_afterDate"));
									} else if (operation.equals("checked")) {
										sb.append(NLT.get("definition.operation_checked"));
									} else if (operation.equals("checkedNot")) {
										sb.append(NLT.get("definition.operation_checkedNot"));
									} else {
										sb.append(operation);
									}
									sb.append("</td>");
									sb.append("</tr>");
									
									//See if there is a duration
									if (!duration.equals("") && !durationType.equals("")) {
										sb.append("<tr>");
										sb.append("<td valign=\"top\">");
										sb.append("<span class=\"ss_bold\">");
										sb.append(NLT.get("definition.currentWorkflowConditionDuration"));
										sb.append("</span>");
										sb.append("</td>");
										sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
										sb.append(duration);
										sb.append(" ");
										if (durationType.equals("minutes")) {
											sb.append(NLT.get("definition.currentWorkflowConditionDurationMinutes"));
										} else if (durationType.equals("hours")) {
											sb.append(NLT.get("definition.currentWorkflowConditionDurationHours"));
										} else if (durationType.equals("days")) {
											sb.append(NLT.get("definition.currentWorkflowConditionDurationDays"));
										}
										sb.append("</td>");
										sb.append("</tr>");
									}
									
									//See if there are values
									it_workflowConditionValues = workflowConditionEle.elementIterator("value");
									if (it_workflowConditionValues.hasNext()) {
										sb.append("<tr>");
										sb.append("<td valign=\"top\">");
										sb.append("<span class=\"ss_bold\">");
										sb.append(NLT.get("definition.currentWorkflowConditionValues"));
										sb.append("</span>");
										sb.append("</td>");
										sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
										while (it_workflowConditionValues.hasNext()) {
											sb.append(((Element)it_workflowConditionValues.next()).getText());
											if (it_workflowConditionValues.hasNext()) sb.append("<br/>");
										}
										sb.append("</td>");
										sb.append("</tr>");
									}
										
									sb.append("</tbody></table>");
									sb.append("");
									sb.append("");
									sb.append("<br/>");
								}
							}
						}
						
						sb.append("<span class=\"ss_bold\">");
						sb.append(NLT.get("definition.selectEntryType"));
						sb.append("</span><br/>");
						sb.append("<select name=\"conditionDefinitionId\" ");
						sb.append("onChange=\"getConditionSelectbox(this, 'get_condition_entry_elements')\" ");
						sb.append(">\n");
						sb.append("<option value=\"-\">").append(NLT.get("definition.select_conditionDefinition")).append("</option>\n");
						sb.append("<option value=\"\">").append(NLT.get("common.select.none")).append("</option>\n");
						
						SortedMap<String, Definition> defs = DefinitionHelper.getAvailableDefinitions(this.binderId, Definition.FOLDER_ENTRY);
						for (Map.Entry<String, Definition> me:defs.entrySet()) {
							//Build a list of the entry definitions
							Definition entryDef = me.getValue();
							sb.append("<option value=\"").append(entryDef.getId()).append("\"");
							sb.append(">").append(me.getKey().replaceAll("&", "&amp;")
									.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</option>\n");								
						}
						sb.append("</select>\n<br/><br/>\n");
						sb.append("<div id=\"conditionEntryElements\"></div><br/>\n");
						sb.append("<div id=\"conditionOperations\"></div><br/>\n");
						sb.append("<div id=\"conditionOperand\"></div>\n");
						
					
					} else if (type.equals("workflowEntryDataUserList")) {
						Element workflowConditionProperty = (Element)rootElement.selectSingleNode("properties/property[@name='condition']");
						if (workflowConditionProperty != null) {
							List<Element> workflowConditionElements = workflowConditionProperty.selectNodes("workflowEntryDataUserList");
							if (workflowConditionElements != null && !workflowConditionElements.isEmpty()) {
								Element workflowConditionEle = workflowConditionElements.get(0);  //all the same definition
								//We have the current condition element; print out its values
								String definitionId = workflowConditionEle.attributeValue("definitionId", "");
								StringBuffer elementName = new StringBuffer();
								StringBuffer elementNameView = new StringBuffer();
								for (Element element:workflowConditionElements) {
									if (definitionId.equals(element.attributeValue("definitionId"))) {
										String name = element.attributeValue("elementName", "");
										if (Validator.isNull(name)) continue;
										if (elementName.length() > 0) elementName.append(",");
										elementName.append(name);
										if (elementNameView.length() > 0) elementNameView.append(", ");
										String nameText = name;
										if (name.indexOf(":") >= 0) nameText = name.substring(name.indexOf(":")+1);
										elementNameView.append(nameText);
									}
								}
								//Get the entry definition itself
								Definition def = DefinitionHelper.getDefinition(definitionId);
								if (def != null && elementName.length()>0) {
									//Put the previous values into hidden fields in case these are not changed
									sb.append("<input type=\"hidden\" name=\"previous_conditionDefinitionId\"");
									sb.append(" value=\""+definitionId+"\" />\n");
									sb.append("<input type=\"hidden\" name=\"previous_conditionElementName\"");
									sb.append(" value=\""+elementName+"\" />\n");
									
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.workflowEntryDataUserList"));
									sb.append("</span><br/><br/>");
									sb.append("<table class=\"ss_form\"><tbody>");
									sb.append("<tr>");
									sb.append("<td valign=\"top\">");
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.currentWorkflowConditionEntryType"));
									sb.append("</span>");
									sb.append("</td>");
									sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
									if (Definition.VISIBILITY_DEPRECATED.equals(def.getVisibility())) {
										sb.append("<del>").append(NLT.getDef(def.getTitle()).replaceAll("&", "&amp;")
												.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</del>");
									} else {
										sb.append(NLT.getDef(def.getTitle()).replaceAll("&", "&amp;")
												.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
									}
									sb.append("</td>");
									sb.append("</tr>");
									
									sb.append("<tr>");
									sb.append("<td valign=\"top\">");
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.currentWorkflowConditionElementName"));
									sb.append("</span>");
									sb.append("</td>");
									sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
									sb.append(elementNameView.toString());
									sb.append("</td>");
									sb.append("</tr>");
																		
									sb.append("</tbody></table>");
									sb.append("");
									sb.append("");
									sb.append("<br/>");
								}
							}
						}
						
						sb.append("<span class=\"ss_bold\">");
						sb.append(NLT.get("definition.selectEntryType"));
						sb.append("</span><br/>");
						sb.append("<select name=\"conditionDefinitionId\" ");
						sb.append("onChange=\"getConditionSelectbox(this, 'get_condition_entry_user_list_elements')\" ");
						sb.append(">\n");
						sb.append("<option value=\"-\">").append(NLT.get("definition.select_conditionDefinition")).append("</option>\n");
						sb.append("<option value=\"\">").append(NLT.get("common.select.none")).append("</option>\n");
						//GET both entry and file Entry definitions
						SortedMap<String, Definition> defs = DefinitionHelper.getAvailableDefinitions(this.binderId, Definition.FOLDER_ENTRY);
						for (Map.Entry<String, Definition> me:defs.entrySet()) {
							//Build a list of the entry definitions
							Definition entryDef = me.getValue();
							sb.append("<option value=\"").append(entryDef.getId()).append("\"");
							sb.append(">").append(me.getKey().replaceAll("&", "&amp;")
									.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</option>\n");								
						}
						sb.append("</select>\n<br/><br/>\n");
						sb.append("<div id=\"conditionEntryElements\"></div><br/>\n");
						
					
					} else if (type.equals("workflowSetEntryDataValue")) {
						Element workflowConditionProperty = (Element)rootElement.selectSingleNode("properties/property[@name='dataValue']");
						if (workflowConditionProperty != null) {
							List<Element> workflowConditionElements = workflowConditionProperty.selectNodes("workflowSetEntryDataValue");
							if (workflowConditionElements != null && !workflowConditionElements.isEmpty()) {
								Element workflowConditionEle = workflowConditionElements.get(0);  //all the same definition
								//We have the current condition element; print out its values
								String definitionId = workflowConditionEle.attributeValue("definitionId", "");
								String operation = workflowConditionEle.attributeValue("operation", "");
								String duration = workflowConditionEle.attributeValue("duration", "");
								String durationType = workflowConditionEle.attributeValue("durationType", "");
								StringBuffer elementName = new StringBuffer();
								StringBuffer elementNameView = new StringBuffer();
								for (Element element:workflowConditionElements) {
									if (definitionId.equals(element.attributeValue("definitionId"))) {
										String name = element.attributeValue("elementName", "");
										if (Validator.isNull(name)) continue;
										if (elementName.length() > 0) elementName.append(",");
										elementName.append(name);
										if (elementNameView.length() > 0) elementNameView.append(", ");
										String nameText = name;
										if (name.indexOf(":") >= 0) nameText = name.substring(name.indexOf(":")+1);
										elementNameView.append(nameText);
									}
								}
								//Get the entry definition itself
								Definition def = DefinitionHelper.getDefinition(definitionId);
								if (def != null && elementName.length()>0) {
									//Put the previous values into hidden fields in case these are not changed
									sb.append("<input type=\"hidden\" name=\"previous_conditionDefinitionId\"");
									sb.append(" value=\""+definitionId+"\" />\n");
									sb.append("<input type=\"hidden\" name=\"previous_conditionElementName\"");
									sb.append(" value=\""+elementName+"\" />\n");
									sb.append("<input type=\"hidden\" name=\"previous_conditionElementOperation\"");
									sb.append(" value=\""+operation+"\" />\n");
									if (!duration.equals("") && !durationType.equals("")) {
										sb.append("<input type=\"hidden\" name=\"previous_operationDuration\"");
										sb.append(" value=\""+duration+"\" />\n");
										sb.append("<input type=\"hidden\" name=\"previous_operationDurationType\"");
										sb.append(" value=\""+durationType+"\" />\n");
									}
									//See if there are values
									Iterator it_workflowConditionValues = workflowConditionEle.elementIterator("value");
									while (it_workflowConditionValues.hasNext()) {
										sb.append("<input type=\"hidden\" name=\"previous_conditionElementValue\"");
										sb.append(" value=\""+((Element)it_workflowConditionValues.next()).getText()+"\" />\n");
									}
									
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.workflowSetEntryData"));
									sb.append("</span><br/><br/>");
									sb.append("<table class=\"ss_form\"><tbody>");
									sb.append("<tr>");
									sb.append("<td valign=\"top\">");
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.currentWorkflowConditionEntryType"));
									sb.append("</span>");
									sb.append("</td>");
									sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
									if (Definition.VISIBILITY_DEPRECATED.equals(def.getVisibility())) {
										sb.append("<del>").append(NLT.getDef(def.getTitle()).replaceAll("&", "&amp;")
												.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</del>");
									} else {
										sb.append(NLT.getDef(def.getTitle()).replaceAll("&", "&amp;")
												.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
									}
									sb.append("</td>");
									sb.append("</tr>");
									
									sb.append("<tr>");
									sb.append("<td valign=\"top\">");
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.currentWorkflowConditionElementName"));
									sb.append("</span>");
									sb.append("</td>");
									sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
									sb.append(elementNameView.toString());
									sb.append("</td>");
									sb.append("</tr>");
																		
									sb.append("<tr>");
									sb.append("<td valign=\"top\">");
									sb.append("<span class=\"ss_bold\">");
									sb.append(NLT.get("definition.currentWorkflowConditionOperation"));
									sb.append("</span>");
									sb.append("</td>");
									sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
									if (operation.equals("set")) {
										sb.append(NLT.get("definition.operation_set"));
									} else if (operation.equals("increment")) {
										sb.append(NLT.get("definition.operation_increment"));
									} else if (operation.equals("decrement")) {
										sb.append(NLT.get("definition.operation_decrement"));
									} else if (operation.equals("setCurrentDate")) {
										sb.append(NLT.get("definition.operation_setCurrentDate"));
									} else if (operation.equals("setCurrentDateStart")) {
										sb.append(NLT.get("definition.operation_setCurrentDateStart"));
									} else if (operation.equals("incrementStart")) {
										sb.append(NLT.get("definition.operation_incrementStart"));
									} else if (operation.equals("setCurrentDateEnd")) {
										sb.append(NLT.get("definition.operation_setCurrentDateEnd"));
									} else if (operation.equals("incrementEnd")) {
										sb.append(NLT.get("definition.operation_incrementEnd"));
									} else {
										sb.append(operation);
									}
									sb.append("</td>");
									sb.append("</tr>");
									
									//See if there is a duration
									if (!duration.equals("") && !durationType.equals("")) {
										sb.append("<tr>");
										sb.append("<td valign=\"top\">");
										sb.append("<span class=\"ss_bold\">");
										sb.append(NLT.get("definition.currentWorkflowConditionAmount"));
										sb.append("</span>");
										sb.append("</td>");
										sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
										sb.append(duration);
										sb.append(" ");
										if (durationType.equals("minutes")) {
											sb.append(NLT.get("definition.currentWorkflowConditionDurationMinutes"));
										} else if (durationType.equals("hours")) {
											sb.append(NLT.get("definition.currentWorkflowConditionDurationHours"));
										} else if (durationType.equals("days")) {
											sb.append(NLT.get("definition.currentWorkflowConditionDurationDays"));
										}
										sb.append("</td>");
										sb.append("</tr>");
									}
									
									//See if there are values
									it_workflowConditionValues = workflowConditionEle.elementIterator("value");
									if (it_workflowConditionValues.hasNext()) {
										sb.append("<tr>");
										sb.append("<td valign=\"top\">");
										sb.append("<span class=\"ss_bold\">");
										sb.append(NLT.get("definition.currentWorkflowConditionValues"));
										sb.append("</span>");
										sb.append("</td>");
										sb.append("<td valign=\"top\" style=\"padding-left:4px;\">");
										while (it_workflowConditionValues.hasNext()) {
											sb.append(((Element)it_workflowConditionValues.next()).getText());
											if (it_workflowConditionValues.hasNext()) sb.append("<br/>");
										}
										sb.append("</td>");
										sb.append("</tr>");
									}
										
									sb.append("</tbody></table>");
									sb.append("");
									sb.append("");
									sb.append("<br/>");
								}
							}
						}
						
						sb.append("<span class=\"ss_bold\">");
						sb.append(NLT.get("definition.selectEntryType"));
						sb.append("</span><br/>");
						sb.append("<select name=\"conditionDefinitionId\" ");
						sb.append("onChange=\"getConditionSelectbox(this, 'get_condition_entry_data_elements')\" ");
						sb.append(">\n");
						sb.append("<option value=\"-\">").append(NLT.get("definition.select_conditionDefinition")).append("</option>\n");
						sb.append("<option value=\"\">").append(NLT.get("common.select.none")).append("</option>\n");
						//GET both entry and file Entry definitions
						SortedMap<String, Definition> defs = DefinitionHelper.getAvailableDefinitions(this.binderId, Definition.FOLDER_ENTRY);
						for (Map.Entry<String, Definition> me:defs.entrySet()) {
							//Build a list of the entry definitions
							Definition entryDef = me.getValue();
							sb.append("<option value=\"").append(entryDef.getId()).append("\"");
							sb.append(">").append(me.getKey().replaceAll("&", "&amp;")
									.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</option>\n");								
						}
						sb.append("</select>\n<br/><br/>\n");
						sb.append("<div id=\"conditionEntryElements\"></div><br/>\n");
						sb.append("<div id=\"conditionOperations\"></div><br/>\n");
						sb.append("<div id=\"conditionOperand\"></div>\n");
						
					
					} else if (type.equals("workflowStatesList")) {
						String selectedId ="";
						List<String> previousStates = new ArrayList();
						Element processProperty = (Element)rootElement.selectSingleNode("properties/property[@name='states']");
						if (processProperty != null) {
							selectedId = processProperty.attributeValue("workflowDefinitionId", "");
							sb.append("<input type=\"hidden\" name=\"previous_workflowDefinitionId\" value=\""+selectedId+"\" />\n");
							//Add the list of previously selected state names as hidden fields
							List<Element> workflowStateElements = processProperty.selectNodes("workflowState");
							if (workflowStateElements != null && !workflowStateElements.isEmpty()) {
								for (Element workflowStateEle : workflowStateElements) {
									String state = workflowStateEle.attributeValue("name", "");
									previousStates.add(state);
									sb.append("<input type=\"hidden\" name=\"previous_workflowState\" value=\""+state+"\" />\n");
								}
							}
						}
						sb.append(propertyConfigCaption);
						sb.append("<select name=\"propertyId_" + 
								propertyId + "\"");
						sb.append(" onChange=\"getConditionSelectbox(this, 'get_entry_workflow_states')\" ");
						sb.append(">\n");
						sb.append("<option value=\"\">").append(NLT.get("definition.select_workflowProcess")).append("</option>\n");
						SortedMap<String, Definition> defs = DefinitionHelper.getAvailableDefinitions(binderId, Definition.WORKFLOW);
						for (Map.Entry<String, Definition> me:defs.entrySet()) {
							//Build a list of the entry definitions
							Definition entryDef = me.getValue();
							if (entryDef.getId().equals(sourceDocument.getRootElement().attributeValue("databaseId"))) continue;
							sb.append("<option value=\"").append(entryDef.getId()).append("\"");
							if (entryDef.getId().equals(selectedId)) {
								sb.append(" selected=\"selected\"");
							}
							sb.append(">").append(me.getKey().replaceAll("&", "&amp;")
									.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</option>\n");								
						}

						sb.append("</select>\n<br/><br/>\n");
						sb.append("<input type=\"hidden\" name=\""+WebKeys.PROPERTY_ID+"\" value=\""+propertyId+"\" />");
						sb.append("<div id=\"entryWorkflowStates\">");
						if (!selectedId.equals("")) {
							sb.append("<span class=\"ss_bold\">"+NLT.get("definition.selectWorkflowStates")+"</span><br/>");
							Map<String,Map> stateData = definitionModule.getWorkflowDefinitionStates(selectedId);
							sb.append("<select multiple=\"multiple\" name=\"workflowStateNames\">\n");
							sb.append("<option value=\"\">"+NLT.get("filter.selectStates")+"</option>\n");
							for (Map.Entry<String, Map> me : stateData.entrySet()) {
								sb.append("<option value=\""+me.getKey()+"\"");
								if (previousStates.contains(me.getKey())) {
									sb.append(" selected=\"selected\" ");
								}
								sb.append(">"+me.getValue().get("caption")+"</option>\n");
							}
							sb.append("</select>\n");
						}
						sb.append("</div><br/>\n");						
					
					} else if (type.equals("userGroupSelect")) {
						sb.append(propertyConfigCaption);
						HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
						HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
						RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/definition_builder/user_group_select.jsp");

						ServletRequest req = null;
						req = new DynamicServletRequest(httpReq);
						req.setAttribute("propertyId", propertyId);
						req.setAttribute("propertyValue", propertyValue0);
						Set ids = LongIdUtil.getIdsAsLongSet((String)propertyValue0);
						ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
					    User user = RequestContextHolder.getRequestContext().getUser();

						Set userListSet = new HashSet();
						List userList = profileDao.loadUserPrincipals(ids, user.getZoneId(), false);
						for (int i = 0; i < userList.size(); i++) {
							if (((Principal)userList.get(i)).getEntityType().equals(EntityType.user)) userListSet.add(userList.get(i));
						}
						Set groupListSet = new HashSet();
						List groupList = profileDao.loadGroups(ids, user.getZoneId());
						for (int i = 0; i < groupList.size(); i++) groupListSet.add(groupList.get(i));
						req.setAttribute(WebKeys.USER_LIST, userListSet);
						req.setAttribute(WebKeys.GROUP_LIST, groupListSet);
						
						StringServletResponse res = new StringServletResponse(httpRes);
						try {
							rd.include(req, res);
							sb.append(res.getString().replaceAll("&", "&amp;"));
						} catch(Exception e) {}
						
					} else if (type.equals("folderSelect")) {
						HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
						HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
						RequestDispatcher rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/definition_builder/folder_select.jsp");

						ServletRequest req = null;
						req = new DynamicServletRequest(httpReq);
						req.setAttribute("propertyId", propertyId);
						req.setAttribute("propertyValue", propertyValue0);
						Long binderId = null;
						if (!propertyValue0.equals("")) binderId = Long.valueOf(propertyValue0);
						req.setAttribute(WebKeys.BINDER_ID, binderId);
						BinderModule binderModule = (BinderModule)SpringContextUtil.getBean("binderModule");
						try {
							Binder binder = binderModule.getBinder(binderId);
							req.setAttribute(WebKeys.BINDER_TITLE, binder.getTitle());
						} catch (Exception ex) {};
						
						StringServletResponse res = new StringServletResponse(httpRes);
						try {
							rd.include(req, res);
							sb.append(res.getString().replaceAll("&", "&amp;"));
						} catch(Exception e) {}
						
					} else if (type.equals("remoteApp")) {
						sb.append(propertyConfigCaption);
					
						Map options = new HashMap();
						options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
						options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
						//get them all
						options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1);

						Document searchFilter = DocumentHelper.createDocument();
						Element field = searchFilter.addElement(Constants.FIELD_ELEMENT);
				    	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_TYPE_FIELD);
				    	Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
				    	child.setText(Constants.ENTRY_TYPE_APPLICATION);
				    	options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter);
						Map searchResults = profileModule.getApplications(options);
						List remoteAppList = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);
						int size = remoteAppList.size();
						if (size <= 0) size = 1;
						sb.append("<select name=\"propertyId_" + 
								propertyId + "\">\n");
						sb.append("<option value=\"\">").append(NLT.get("definition.select_remote_app")).append("</option>\n");
						for (int i=0; i<remoteAppList.size(); ++i) {
							//Build a list of the remote apps
							Map remoteApp = (Map)remoteAppList.get(i);
							sb.append("<option value=\"").append(remoteApp.get("_docId")).append("\"");
							if (remoteApp.get("_docId").equals(propertyValue0)) {
								sb.append(" selected=\"selected\"");
							}
							sb.append(">").append(((String)remoteApp.get("title"))
									.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"))
									.append(" (").append(remoteApp.get("_applicationName")).append(")</option>\n");
						}
						sb.append("</select>\n<br/><br/>\n");

					} else if (type.equals("subProcess")) {
						String selectedId ="";
						Element processProperty = (Element)rootElement.selectSingleNode("properties/property[@name='definitionId']");
						if (processProperty != null) selectedId = processProperty.attributeValue("value", "");
						sb.append(propertyConfigCaption);
						sb.append("<select name=\"propertyId_" + 
								propertyId + "\">\n");
						sb.append("<option value=\"\">").append(NLT.get("definition.select_subProcess")).append("</option>\n");
						SortedMap<String, Definition> defs = DefinitionHelper.getAvailableDefinitions(binderId, Definition.WORKFLOW);
						for (Map.Entry<String, Definition> me:defs.entrySet()) {
							//Build a list of the entry definitions
							Definition entryDef = me.getValue();
							if (entryDef.getId().equals(sourceDocument.getRootElement().attributeValue("databaseId"))) continue;
							sb.append("<option value=\"").append(entryDef.getId()).append("\"");
							if (entryDef.getId().equals(selectedId)) {
								sb.append(" selected=\"selected\"");
							}
							sb.append(">").append(me.getKey().replaceAll("&", "&amp;")
									.replaceAll("<", "&lt;").replaceAll(">", "&gt;")).append("</option>\n");								
						}

						sb.append("</select>\n<br/><br/>\n");

					} else {
						sb.append(propertyConfigCaption);
						if (readonly.equals("")) {
							sb.append("<input type=\"text\" class=\"ss_text\" name=\"propertyId_" + propertyId + "\" size=\"40\" ");
							sb.append("value=\""+Html.formatTo(propertyValue0)+"\" "+readonly+"/>\n");
						} else {
							sb.append("<input type=\"text\" class=\"ss_text\" name=\"propertyId_readonly_" + propertyId + "\" size=\"40\" ");
							sb.append("value=\""+Html.formatTo(propertyValue0)+"\" "+readonly+"/>\n");
							sb.append("<input type=\"hidden\" name=\"propertyId_" + propertyId + "\" ");
							sb.append("value=\""+Html.formatTo(propertyValue0)+"\" />\n");
						}
					}
					//See if this property has any help
					Element help = (Element) propertyConfig.selectSingleNode("./help");
					if (help != null) {
						helpDivCount++;
						hb.append("<div align=\"left\" id=\"help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
						hb.append(Integer.toString(helpDivCount));
						hb.append("\" class=\"ss_helpPopUp\" style=\"visibility:hidden;\">\n");
						hb.append("<span>");
						hb.append(NLT.get(help.getText()));
						hb.append("</span>\n</div>\n");
						sb.append("<a name=\"help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
						sb.append(Integer.toString(helpDivCount));
						sb.append("_a\" onClick=\"ss_activateMenuLayerMove('help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
						sb.append(Integer.toString(helpDivCount));
						sb.append("');return false;\"><img alt=\"" + NLT.get("alt.help") + "\" border=\"0\" src=\""+helpImgUrl+"\"/></a>\n");
					}
					
					sb.append("<input type=\"hidden\" name=\"propertyName_" + propertyId + "\" ");
					sb.append("value=\""+Html.formatTo(propertyName)+"\"/>\n");
					sb.append("<br/>\n");
				}
			
			sb.append("<br/>");
			sb.append("<input type=\"hidden\" name=\"definitionType_"+rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", "")+"\" value=\""+ rootElement.attributeValue("definitionType", "") +"\"/>\n");

			String customValue = "";
			String inherit="";
			if (rootConfigElement != rootElement) { //if adding new item, don't copy system jsps
				Element custom = (Element)rootElement.selectSingleNode("./jsps/jsp[@name='custom']");
				if (custom != null) {
					if ("true".equals(custom.attributeValue("inherit"))) inherit="checked=\"on\"";
					else customValue = custom.attributeValue("value", "");

				}
			} 
			
			//remove the customJsp field only for the _user definition with profile GWT View
			boolean bCustomJSP = true;
			Node userNode = sourceRoot.selectSingleNode("//definition[@name='_user']");
			if(userNode != null){
				Node typeNode = sourceRoot.selectSingleNode("//definition/properties/property[@name='type']");
				if(typeNode != null) {
					String value = ((Element)typeNode).attributeValue("value");
					if(value != null && value.equals("profileGWT")){
						bCustomJSP = false;
					}
				}
			} 
			
			if(!bCustomJSP){
				return;
			}
			
			String display=rootConfigElement.attributeValue("display");
			if (!Validator.isNull(display)) {
				if ("form".equals(display)) {
					sb.append(NLT.get("__custom_form_jsp")); 					
				} else {
					sb.append(NLT.get("__custom_view_jsp")); 
				}
				sb.append("<br/> <input type=\"text\" size=\"40\" name=\"jspName_custom\" value=\"" +
						customValue + "\"/>");
				if ("form".equals(display)) {
					sb.append("<a name=\"help_div_customFormJsp_a\" onClick=\"ss_activateMenuLayerMove('help_div_customFormJsp');return false;\"><img alt=\"" + NLT.get("alt.help") + "\" border=\"0\" src=\""+helpImgUrl+"\"/></a>\n");
				} else {
					sb.append("<a name=\"help_div_customViewJsp_a\" onClick=\"ss_activateMenuLayerMove('help_div_customViewJsp');return false;\"><img alt=\"" + NLT.get("alt.help") + "\" border=\"0\" src=\""+helpImgUrl+"\"/></a>\n");					
					if ("dataView".equals(rootElement.attributeValue("type")) ) { //wraps a form element
						sb.append("<br/><input type=\"checkbox\" name=\"jspName_custom_inherit\" " +
								inherit + "/> " + NLT.get("__custom_view_jsp_inherit") );
						sb.append("<a name=\"help_div_customInheritJsp_a\" onClick=\"ss_activateMenuLayerMove('help_div_customInheritJsp');return false;\"><img alt=\"" + NLT.get("alt.help") + "\" border=\"0\" src=\""+helpImgUrl+"\"/></a>\n");
						
					}
				}
			}
		}
	}
	private void buildDefaultDivs(StringBuffer sb, StringBuffer hb) {
		if (!this.divNames.containsKey("delete_item")) {
			this.divNames.put("delete_item", "1");
			sb.append("\n<div id=\"delete_item\" ");
			sb.append("class=\"ss_definitionBuilder\">\n");
			sb.append("<span>"+NLT.get("definition.deleteSelectedItem")+"</span>\n");
			sb.append("<br/>\n");
			sb.append("<span>"+NLT.get("definition.deleteSelectedItemConfirm")+"</span>\n");
			sb.append("<br/>\n");
			sb.append("</div>\n");
			//sb.append("<script type=\"text/javascript\">\n");
			//sb.append("    self.ss_setDeclaredDiv('delete_item')\n");
			//sb.append("</script>\n");
		}
		
		//Build the move_item divs
		if (!this.divNames.containsKey("move_item")) {
			this.divNames.put("move_item", "1");
			sb.append("\n<div id=\"move_item\" ");
			sb.append("class=\"ss_definitionBuilder\">\n");
			sb.append("<span class=\"ss_bold\">"+NLT.get("definition.selectNewLocation")+"</span><br/>\n");
			sb.append("</div>\n");
			//sb.append("<script type=\"text/javascript\">\n");
			//sb.append("    self.ss_setDeclaredDiv('move_item')\n");
			//sb.append("</script>\n");
			sb.append("\n<div id=\"move_item_confirm\" ");
			sb.append("class=\"ss_definitionBuilder\">\n");
			sb.append("<span class=\"ss_titlebold\">"+NLT.get("definition.move")+" </span><div id=\"moveItemSelection\" style=\"display:inline;\"></div>\n");
			sb.append("<br/>\n");
			sb.append("<input type=\"radio\" class=\"ss_text\" name=\"moveTo\" value=\"above\"/>");
			sb.append("<span>"+NLT.get("definition.moveToAbove")+"</span><br/>");
			sb.append("<input type=\"radio\" class=\"ss_text\" name=\"moveTo\" value=\"below\"/>");
			sb.append("<span>"+NLT.get("definition.moveToBelow")+"</span><br/>");
			sb.append("<input type=\"radio\" class=\"ss_text\" name=\"moveTo\" value=\"into\"/>");
			sb.append("<span>"+NLT.get("definition.moveInto")+"</span><br/>");
			sb.append("</div>\n");
			//sb.append("<script type=\"text/javascript\">\n");
			//sb.append("    self.ss_setDeclaredDiv('move_item_confirm')\n");
			//sb.append("</script>\n");
		}
		//Build the copy_item divs
		if (!this.divNames.containsKey("copy_item")) {
			this.divNames.put("copy_item", "1");
			sb.append("\n<div id=\"copy_item\" ");
			sb.append("class=\"ss_definitionBuilder\">\n");
			sb.append("<span class=\"ss_bold\">"+NLT.get("definition.selectNewLocation")+"</span><br/>\n");
			sb.append("</div>\n");
		}
		hb.append("<div align=\"left\" id=\"help_div_customFormJsp");
		hb.append("\" class=\"ss_helpPopUp\" style=\"visibility:hidden;\">\n");
		hb.append("<span>");
		hb.append(NLT.get("__custom_form_jsp_help"));
		hb.append("</span>\n</div>\n");
		hb.append("<div align=\"left\" id=\"help_div_customViewJsp");
		hb.append("\" class=\"ss_helpPopUp\" style=\"visibility:hidden;\">\n");
		hb.append("<span>");
		hb.append(NLT.get("__custom_view_jsp_help"));
		hb.append("</span>\n</div>\n");
		
		hb.append("</span>\n</div>\n");
		hb.append("<div align=\"left\" id=\"help_div_customInheritJsp");
		hb.append("\" class=\"ss_helpPopUp\" style=\"visibility:hidden;\">\n");
		hb.append("<span>");
		hb.append(NLT.get("__custom_view_jsp_inherit_help"));
		hb.append("</span>\n</div>\n");
	}
	
	private void buildHelpDivs(Element root, Element sourceRoot, StringBuffer sb, StringBuffer hb, String filter) {
		if (this.option.equals("") && !this.divNames.containsKey("helpOptionsDivs_"+rootElementId)) {
			this.divNames.put("helpOptionsDivs_"+rootElementId, "1");

			//Add the list of options
			Element e_options = rootConfigElement.element("options");
			Element e_option;
			Map optionsSeen = new HashMap();
			
			if (e_options != null) {
				Iterator itOptions = e_options.elementIterator("option");
				while (itOptions.hasNext()) {
					e_option = (Element) itOptions.next();
					String optionName = e_option.attributeValue("name");
					//See if this search is limited to a particular definition type (e.g., COMMAND or PROFILE_ENTRY_VIEW)
					String optionDefinitionType = e_option.attributeValue("definitionType", "");
					if (!optionDefinitionType.equals("")) {
						//This request is for a specific definition type.
						//Check that the definition type from the actual definition matches the desired type.
						if (!optionDefinitionType.equals(sourceRoot.attributeValue("type", ""))) continue;
					}
					//Find this item in the definition config
					Element optionItem = (Element) configBuilder.getItem(configDocument, optionName);
					if (optionItem != null) {
						//See if multiple are allowed
						if (!optionItem.attributeValue("multipleAllowed", "").equalsIgnoreCase("false") || 
								sourceRoot.selectSingleNode("//item[@name='"+optionName+"']") == null) {
							if (!optionsSeen.containsKey(optionName)) {
								//See if this item has any help
								Element help = (Element) optionItem.selectSingleNode("./help");
								if (help != null) {
									helpDivCount++;
									hb.append("<div align=\"left\" id=\"help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
									hb.append(Integer.toString(helpDivCount));
									hb.append("\" class=\"ss_helpPopUp\" style=\"visibility:hidden;\">\n");
									hb.append("<span>");
									hb.append(NLT.get(help.getText()));
									hb.append("</span>\n</div>\n");
								}
								optionsSeen.put(optionName, optionName);
							}
						}
					}
				}
				
				itOptions = e_options.elementIterator("option_select");
				while (itOptions.hasNext()) {
					e_option = (Element) itOptions.next();
					
					//See if this search is limited to a particular definition type (e.g., COMMAND or PROFILE_ENTRY_VIEW)
					String optionDefinitionType = e_option.attributeValue("definitionType", "");
					if (!optionDefinitionType.equals("")) {
						//This request is for a specific definition type.
						//Check that the definition type from the actual definition matches the desired type.
						if (!optionDefinitionType.equals(sourceRoot.attributeValue("type", ""))) continue;
					}
					String optionPath = e_option.attributeValue("path", "");
					Iterator itOptionsSelect = rootConfigElement.selectNodes(optionPath).iterator();
					if (!itOptionsSelect.hasNext()) continue;
					
					while (itOptionsSelect.hasNext()) {
						Element optionSelect = (Element) itOptionsSelect.next();
						String optionSelectName = optionSelect.attributeValue("name");
						//See if multiple are allowed
						if (!optionSelect.attributeValue("multipleAllowed", "").equalsIgnoreCase("false") ||
								sourceRoot.selectSingleNode("//item[@name='"+optionSelectName+"']") == null) {
							if (!optionsSeen.containsKey(optionSelectName)) {
								//See if this item has any help
								Element help = (Element) optionSelect.selectSingleNode("./help");
								if (help != null) {
									helpDivCount++;
									hb.append("<div align=\"left\" id=\"help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
									hb.append(Integer.toString(helpDivCount));
									hb.append("\" class=\"ss_helpPopUp\" style=\"visibility:hidden;\">\n");
									hb.append("<span>");
									hb.append(NLT.get(help.getText()));
									hb.append("</span>\n</div>\n");
								}
								optionsSeen.put(optionSelectName, optionSelectName);
							}
						}
					}
				}
			}
		}

		//Build the properties div
		if (Validator.isNull(this.option) && !this.divNames.containsKey("helpPropertiesDivs_"+rootElementId)) {
			this.divNames.put("helpPropertiesDivs_"+rootElementId, "1");
			//Get the list of properties
			Element propertiesConfig = rootConfigElement.element("properties");
			if (propertiesConfig != null) {
				Iterator itProperties = propertiesConfig.elementIterator("property");
				while (itProperties.hasNext()) {
					//Get the next property from the base config file
					Element propertyConfig = (Element) itProperties.next();
					//Get the name and id (if any) from the config file
					//See if this property has any help
					Element help = (Element) propertyConfig.selectSingleNode("./help");
					if (help != null) {
						helpDivCount++;
						hb.append("<div id=\"help_div_" + rootElementId.replaceAll("<", "").replaceAll(">", "").replaceAll("&", ""));
						hb.append(Integer.toString(helpDivCount));
						hb.append("\" class=\"ss_helpPopUp\" style=\"visibility:hidden;\">\n");
						hb.append("<span>");
						hb.append(NLT.get(help.getText()));
						hb.append("</span>\n</div>\n");
					}
				}
			}
		}
	}
	
	public void buildPropertyInfoDivs(Element root, StringBuffer sb, StringBuffer hb) {
		Element configRoot = this.configDocument.getRootElement();
		Iterator itElements = root.elementIterator();
		while (itElements.hasNext()) {
			Element item = (Element) itElements.next();
			if (item.getName().equals("item")) {
				String itemName = item.attributeValue("name", "");
				Element configItem = (Element)configRoot.selectSingleNode("item[@name='"+itemName+"']");
				String itemId = item.attributeValue("id", "");
				List propertyNodes = item.selectNodes("./properties/property");
				if (propertyNodes != null && !propertyNodes.isEmpty()) {
					ListIterator itProperties = propertyNodes.listIterator();
					String startDivText = "\n<div class='ss_infoDiv' id='info_div_" + itemId + "'>\n<ul>";
					String endDivText = "";
					while (itProperties.hasNext()) {
						Element property = (Element) itProperties.next();
						String name = property.attributeValue("name", "");
						String caption = name;
						String type = "";
						if (configItem != null) {
							//Get the property caption from the config definitions
							Element configPropertyEle = (Element)configItem.selectSingleNode("./properties/property[@name='"+name+"']");
							if (configPropertyEle != null) {
								String configCaption = configPropertyEle.attributeValue("caption", "");
								type = configPropertyEle.attributeValue("type", "");
								if (!configCaption.equals("")) caption = configCaption;
							}
						}
						if (!caption.equals("")) name = NLT.getDef(caption);
						String value = property.attributeValue("value", "");
						if (!name.equals("") && !value.equals("")) {
							if (!startDivText.equals("")) {
								sb.append(startDivText);
								startDivText = "";
								endDivText = "</ul>\n</div>\n";
							}
							sb.append("<li>");
							sb.append("<span class='ss_infoDivName'>"+name+":</span> <span class='ss_infoDivValue'>" + buildValueString(value) + "</span>");
							sb.append("</li>");
						} else if (!name.equals("") && type.equals("workflowSetEntryDataValue")) {
							Element setEntryDataValueEle = (Element)item.selectSingleNode("./properties/property[@name='dataValue']");
							if (setEntryDataValueEle != null) {
								Element workflowSetEntryDataValueEle = (Element)setEntryDataValueEle.selectSingleNode("workflowSetEntryDataValue");
								//Output the "set data" params
								if (!startDivText.equals("")) {
									sb.append(startDivText);
									startDivText = "";
									endDivText = "</ul>\n</div>\n";
								}
								sb.append("<li>");
								name = workflowSetEntryDataValueEle.attributeValue("operation", "?");
								if (!name.equals("?")) {
									name = NLT.get("definition.operation_"+name);
								}
								value = workflowSetEntryDataValueEle.attributeValue("elementName", "?");
								sb.append("<span class='ss_infoDivName'>"+name+":</span> <span class='ss_infoDivValue'>" + buildValueString(value) + "</span>");
								sb.append("</li>");
							}
						}
					}
					sb.append(endDivText);
				}
			}
		}
		ListIterator itItems = root.selectNodes("./item").listIterator();
		while (itItems.hasNext()) buildPropertyInfoDivs((Element)itItems.next(), sb, hb);
	}
	@Override
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


	public void setOption(String option) {
	    this.option = option;
	}
	
	public void setItemId(String itemId) {
	    this.itemId = itemId;
	}
	
	public void setItemName(String itemName) {
	    this.itemName = itemName;
	}
	
	public String getRefItemId() {
	    return this.refItemId;
	}
	
	public void setRefItemId(String refItemId) {
	    this.refItemId = refItemId;
	}
	public void setOwningBinderId(Long binderId) {
	    this.binderId = binderId;
	}
	
	private String buildValueString(String value) {
		String reply;
		if ((null == value) || (0 == value.length())) {
			reply = value;
		}
		else {
			if (value.startsWith("__"))
			     reply = NLT.getDef(value);
			else reply = Html.formatTo(value);
		}
		return reply;
	}
}
