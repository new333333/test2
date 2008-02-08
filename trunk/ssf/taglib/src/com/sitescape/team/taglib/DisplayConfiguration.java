/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.taglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Document;
import org.dom4j.Element;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.util.Validator;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Peter Hurley
 *
 */
public class DisplayConfiguration extends TagSupport {
	private static final long serialVersionUID=1L;
    private Document configDefinition;
    private Element configElement;
    private String configJspStyle;
    private boolean processThisItem = false;
    private DefinableEntity entry;
    
	public int doStartTag() throws JspException {
		DefinitionConfigurationBuilder configBuilder=DefinitionHelper.getDefinitionBuilderConfig();
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			
			if (this.configDefinition == null) {
					throw new JspException("No configuration definition available for this item.");
			} else if (this.configElement != null) {
				
				List<Element> itemList;
				if (processThisItem == true) {
					itemList = new ArrayList();
					itemList.add(this.configElement);
				} else {
					itemList = this.configElement.elements("item");
				}
				if (itemList != null) {										
					for (Element nextItem:itemList) {
						
						//Find the jsp to run. Look in the definition configuration for this.
						//Get the item type of the current item being processed 
						String itemType = nextItem.attributeValue("name", "");
						String formItem = nextItem.attributeValue("formItem", "");
						String customJsp = null; 
						Boolean inherit=Boolean.FALSE;
						Element jspEle= (Element)nextItem.selectSingleNode("./jsps/jsp[@name='custom']");
						if (jspEle != null) {
							String jspName = jspEle.attributeValue("value");
							if ("true".equals(jspEle.attributeValue("inherit"))) inherit=Boolean.TRUE;							
							if (!inherit && Validator.isNotNull(jspName)) customJsp = "/WEB-INF/jsp/custom_jsps/" + jspName;
						}

						//get Item from main config document
						Element itemDefinition = configBuilder.getItem(configDefinition, itemType);
						if (itemDefinition != null) {
							String jspName;
							String defaultJsp=configBuilder.getItemJspByStyle(itemDefinition, itemType, this.configJspStyle);
							if (itemType.equals("customJsp")) {
								jspName = DefinitionUtils.getPropertyValue(nextItem, "formJsp");
								if (Validator.isNotNull(jspName)) customJsp = "/WEB-INF/jsp/custom_jsps/" + jspName;
							} else if (customJsp == null && "dataView".equals(nextItem.attributeValue("type")) &&
									(inherit || formItem.equals("customJsp"))) { //wraps a form element
								Element entryFormItem = (Element)configDefinition.getRootElement().selectSingleNode("item[@type='form']");
								if (entryFormItem != null) {
									//see if item is generated and save source
									String nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");
									if (Validator.isNotNull(nameValue)) {
										Element itemEle = (Element)entryFormItem.selectSingleNode(".//item/properties/property[@name='name' and @value='" + nameValue + "']");
										if (itemEle != null) {
											itemEle = itemEle.getParent().getParent();
											if (formItem.equals("customJsp")) {
												String jspType = "viewJsp";
												if (configJspStyle.equals(Definition.JSP_STYLE_MAIL)) jspType = "mailJsp";
												else if (configJspStyle.equals(Definition.JSP_STYLE_MOBILE)) jspType = "mobileJsp";
												jspName = DefinitionUtils.getPropertyValue(nextItem, jspType);
												if (Validator.isNotNull(jspName)) customJsp = "/WEB-INF/jsp/custom_jsps/" + jspName;
											}
											if (Validator.isNull(customJsp) && inherit) {
												jspEle= (Element)itemEle.selectSingleNode("./jsps/jsp[@name='custom']");
												if (jspEle != null) {
													jspName = jspEle.attributeValue("value");
													if (Validator.isNotNull(jspName) ) customJsp = "/WEB-INF/jsp/custom_jsps/" + jspName;
												}
												
											}
										}
									}
								}
							} else if (itemType.equals("customJspView")) {
								String jspType = "viewJsp";
								if (configJspStyle.equals(Definition.JSP_STYLE_MAIL)) jspType = "mailJsp";
								else if (configJspStyle.equals(Definition.JSP_STYLE_MOBILE)) jspType = "mobileJsp";
								jspName = DefinitionUtils.getPropertyValue(nextItem, jspType);
								if (Validator.isNotNull(jspName)) customJsp = "/WEB-INF/jsp/custom_jsps/" + jspName;
							}
							String jsp = customJsp;
							if (Validator.isNull(jsp)) jsp = defaultJsp;
							
							if (!Validator.isNull(jsp)) {
								RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
									
								ServletRequest req = null;
								req = new DynamicServletRequest(
										(HttpServletRequest)pageContext.getRequest());
									
								req.setAttribute("item", nextItem);
								req.setAttribute(WebKeys.CONFIG_DEFINITION, this.configDefinition);
								req.setAttribute(WebKeys.CONFIG_ELEMENT, this.configElement);
								req.setAttribute(WebKeys.CONFIG_JSP_STYLE, this.configJspStyle);
								req.setAttribute(WebKeys.CONFIG_FALLBACK_JSP, defaultJsp);  //pass to any custom jsps if they cannot handle configStyle
								//Each item property that has a value is added as a "request attribute". 
								//  The key name is "property_xxx" where xxx is the property name.
								//At a minimum, make sure the name and caption variables are defined
								req.setAttribute("property_name", "");
								req.setAttribute("property_caption", "");
									
								//Also set up the default values for all properties defined in the definition configuration
								//  These will be overwritten by the real values (if they exist) below
								List<Element> itemDefinitionProperties = itemDefinition.selectNodes("properties/property");
								Map propertyValuesMap = new HashMap();
								Map savedReqAttributes = new HashMap();
								for (Element property:itemDefinitionProperties) {
									String propertyName = property.attributeValue("name", "");
									if (Validator.isNull(propertyName)) continue;
									//Get the type from the config definition
									String propertyConfigType = property.attributeValue("type", "text");
									String propertyValue = "";	
									//Get the value(s) from the actual definition
									if (propertyConfigType.equals("selectbox")) {
										//get all items with same name
										List<Element> selProperties = nextItem.selectNodes("properties/property[@name='"+propertyName+"']");
										if (selProperties == null) continue;
										//There might be multiple values so bulid a list
										List propertyValues = new ArrayList();
										for (Element selItem:selProperties) {
											String selValue = NLT.getDef(selItem.attributeValue("value", ""));
											if (Validator.isNotNull(selValue)) propertyValues.add(selValue);
											
										}
										propertyValuesMap.put("propertyValues_"+propertyName, propertyValues);
										propertyValuesMap.put("property_"+propertyName, "");
									} else {
										Element selItem = (Element)nextItem.selectSingleNode("properties/property[@name='"+propertyName+"']");
										if (selItem == null) selItem=property;
										if (propertyConfigType.equals("textarea")) {
											propertyValue = selItem.getText();
										} else {										
											propertyValue = NLT.getDef(selItem.attributeValue("value", ""));
										}
										//defaults don't apply here
										//Set up any "setAttribute" values that need to be passed along. Save the old value so it can be restored
										String reqAttrName = property.attributeValue("setAttribute", "");
										if (Validator.isNotNull(reqAttrName)) {
											//Find this property in the current config
											savedReqAttributes.put(reqAttrName, req.getAttribute(reqAttrName));
											req.setAttribute(reqAttrName, propertyValue);
										}
										if (Validator.isNull(propertyValue)) {
											propertyValue = property.attributeValue("default", "");
											if (!Validator.isNull(propertyValue)) propertyValue = NLT.getDef(propertyValue);
										}
										propertyValuesMap.put("property_"+propertyName, propertyValue);
									
									}
										
								}
								
									
								//not sure if this is necessary
//								List<Element> itProperties = nextItem.selectNodes("properties/property");
//								for (Element property:itProperties) {
//									String propertyName = property.attributeValue("name", "");
//									if (Validator.isNull(propertyName)) continue;
//									if (!propertyValuesMap.containsKey("property_"+propertyName)) 
//										propertyValuesMap.put("property_"+propertyName, "");
//								}
								
								Iterator itPropertyValuesMap = propertyValuesMap.entrySet().iterator();
								while (itPropertyValuesMap.hasNext()) {
									Map.Entry entry = (Map.Entry)itPropertyValuesMap.next();
									req.setAttribute((String)entry.getKey(), entry.getValue());
								}
									
									
								//Store the entry object
								if (this.entry != null) {
									req.setAttribute(WebKeys.DEFINITION_ENTRY, this.entry);
								}
									
								StringServletResponse res = new StringServletResponse(httpRes);
								rd.include(req, res);
								pageContext.getOut().print("<!-- " + jsp + " -->");
								pageContext.getOut().print(res.getString());
								pageContext.getOut().print("<!-- end " + jsp.substring(jsp.lastIndexOf('/')+1) + " -->");

								//Restore the saved properties
								for (Element property:itemDefinitionProperties) {
									String reqAttrName = property.attributeValue("setAttribute", "");
									if (Validator.isNotNull(reqAttrName)) {
										savedReqAttributes.put(reqAttrName, req.getAttribute(reqAttrName));
										req.setAttribute(reqAttrName, req.getAttribute(reqAttrName));
									}
								}
							} else {
								if (!"mail".equals(configJspStyle)) {
									/*
									 pageContext.getOut().print("<br><i>[No jsp for configuration element: "
											+NLT.getDef(nextItem.attributeValue("caption", "unknown"))+"]</i><br>");
									*/
								}
							}
						} else {
							/*
							 pageContext.getOut().print("<br><i>[No configuration element: "
										+NLT.getDef(nextItem.attributeValue("caption", "unknown"))+"]</i><br>");
							 */
						}
					} //end for
				} //end no itemlist
			}
		}  catch(Exception e) {
	        throw new JspException(e);
	    }  finally {
	    	this.configDefinition = null;
	    	this.configElement = null;
	    	this.configJspStyle = null;
	    	this.processThisItem = false;
	    	this.entry = null;
	    }
	    
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setConfigDefinition(Document configDefinition) {
	    this.configDefinition = configDefinition;
	}

	public void setConfigElement(Element configElement) {
	    this.configElement = configElement;
	}

	public void setConfigJspStyle(String configJspStyle) {
	    this.configJspStyle = configJspStyle;
	}

	public void setProcessThisItem(boolean flag) {
	    this.processThisItem = flag;
	}


	public void setEntry(DefinableEntity entry) {
	    this.entry = entry;
	}

}


