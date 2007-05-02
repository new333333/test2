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

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
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
						//get Item from main config document
						Element itemDefinition = configBuilder.getItem(configDefinition, itemType);
						if (itemDefinition != null) {
							// (rsordillo) Jsps contained in configDefaultDefinition only, removed code to check Definition
							String jsp = configBuilder.getItemJspByStyle(itemDefinition, itemType, this.configJspStyle);
							if (!Validator.isNull(jsp)) {
								RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
									
								ServletRequest req = null;
								req = new DynamicServletRequest(
										(HttpServletRequest)pageContext.getRequest());
									
								req.setAttribute("item", nextItem);
								req.setAttribute(WebKeys.CONFIG_DEFINITION, this.configDefinition);
								req.setAttribute(WebKeys.CONFIG_ELEMENT, this.configElement);
								req.setAttribute(WebKeys.CONFIG_JSP_STYLE, this.configJspStyle);
									
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
									pageContext.getOut().print("<br><i>[No jsp for configuration element: "
											+NLT.getDef(nextItem.attributeValue("caption", "unknown"))+"]</i><br>");
								}
							}
						} else {
							pageContext.getOut().print("<br><i>[No configuration element: "
										+NLT.getDef(nextItem.attributeValue("caption", "unknown"))+"]</i><br>");
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


