package com.sitescape.ef.taglib;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Peter Hurley
 *
 */
public class DisplayConfiguration extends TagSupport {
    private Document configDefinition;
    private Element configElement;
    private String configJspStyle;
    private boolean processThisItem = false;
    private FolderEntry folderEntry;
    
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			Element definitionRoot = this.configDefinition.getRootElement();

			if (this.configElement != null) {
				Iterator itItems = null;
				if (processThisItem == true) {
					List itemList = new ArrayList();
					itemList.add(this.configElement);
					itItems = itemList.iterator();
				} else {
					itItems = this.configElement.elementIterator("item");
				}
				if (itItems != null) {
					while (itItems.hasNext()) {
						Element nextItem = (Element) itItems.next();
						
						//Find the jsp to run. Look in the definition configuration for this.
						//Get the item type of the current item being processed
						String itemType = nextItem.attributeValue("name", "");
						Element itemDefinition = (Element) definitionRoot.selectSingleNode("//item[@name='"+itemType+"']");
						if (itemDefinition != null) {
							Element jspEle = (Element) itemDefinition.selectSingleNode("jsps/jsp[@name='"+this.configJspStyle+"']");
							if (jspEle != null) {
								String jsp = jspEle.attributeValue("value", "");
								if (!jsp.equals("")) {
									RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
									
									ServletRequest req = null;
									req = new DynamicServletRequest(
										(HttpServletRequest)pageContext.getRequest());
									
									req.setAttribute("item", nextItem);
									
									//Each item property that has a value is added as a "request attribute". 
									//  The key name is "property_xxx" where xxx is the property name.
									//At a minimum, make sure the name and caption variables are defined
									req.setAttribute("property_name", "");
									req.setAttribute("property_caption", "");
									
									//Also set up the default values for all properties defined in the definition configuration
									//  These will be overwritten by the real values (if they exist) below
									Iterator itItemDefinitionProperties = itemDefinition.selectNodes("properties/property").iterator();
									while (itItemDefinitionProperties.hasNext()) {
										Element property = (Element) itItemDefinitionProperties.next();
										String propertyName = property.attributeValue("name", "");
										String propertyDefaultValue = property.attributeValue("default", "");
										//Get the value from the actual definition
										Element itemProperty = (Element) nextItem.selectSingleNode("properties/property[@name='"+propertyName+"']");
										if (itemProperty != null) {
											propertyDefaultValue = itemProperty.attributeValue("value", propertyDefaultValue);
										}
										if (!propertyName.equals("")) {
											req.setAttribute("property_"+propertyName, propertyDefaultValue);
										}
									}
									
									Iterator itProperties = nextItem.selectNodes("properties/property").iterator();
									while (itProperties.hasNext()) {
										Element property = (Element) itProperties.next();
										String propertyName = property.attributeValue("name", "");
										
										if (!propertyName.equals("")) {												
											req.setAttribute("property_"+propertyName, "");

											//Get the type from the config definition
											Element propertyConfig = (Element) itemDefinition.selectSingleNode("properties/property[@name='"+propertyName+"']");
											String propertyConfigType = "";
											if (propertyConfig != null) {
												propertyConfigType = propertyConfig.attributeValue("type", "text");
											}
											
											String propertyValue = "";
											if (propertyConfigType.equals("textarea")) {
												propertyValue = property.getText();
											} else if (propertyConfigType.equals("boolean") || propertyConfigType.equals("checkbox")) {
												propertyValue = property.attributeValue("value", "");
											} else {
												propertyValue = property.attributeValue("value", "");
											}
											req.setAttribute("property_"+propertyName, propertyValue);
										}
									}
									
									//Set up any "setAttribute" values that need to be passed along. Save the old value so it can be restored
									//Each property is added as a request attribute. The key name is "property_xxx" where xxx is the property name.
									Map savedReqAttributes = new HashMap();
									itProperties = itemDefinition.selectNodes("properties/property[@setAttribute]").iterator();
									while (itProperties.hasNext()) {
										Element property = (Element) itProperties.next();
										String reqAttrName = property.attributeValue("setAttribute", "");
										if (!reqAttrName.equals("")) {
											//Find this property in the current config
											String propertyName = property.attributeValue("name", "");
											Element configProperty = 
												(Element)nextItem.selectSingleNode("properties/property[@name='"+propertyName+"']");
											if (configProperty != null) {
												String value = configProperty.attributeValue("value", "");
												savedReqAttributes.put(reqAttrName, req.getAttribute(reqAttrName));
												req.setAttribute(reqAttrName, value);
											}
										}
									}
									//Store the entry object
									if (this.folderEntry != null) {
										req.setAttribute("ss_definition_folder_entry", this.folderEntry);
									}
				
									StringServletResponse res = new StringServletResponse(httpRes);
									rd.include(req, res);
									pageContext.getOut().print(res.getString());

									//Restore the saved properties
									itProperties = itemDefinition.selectNodes("properties/property[@name='setAttribute']").iterator();
									while (itProperties.hasNext()) {
										Element property = (Element) itProperties.next();
										String reqAttrName = property.attributeValue("setAttribute", "");
										if (!reqAttrName.equals("")) {
											savedReqAttributes.put(reqAttrName, req.getAttribute(reqAttrName));
											req.setAttribute(reqAttrName, req.getAttribute(reqAttrName));
										}
									}
								} else {
									pageContext.getOut().print("<br><i>[No jsp for configuration element: "
											+nextItem.attributeValue("caption", "unknown")+"]</i><br>");
								}
							} else {
								pageContext.getOut().print("<br><i>[No jsp for configuration element: "
										+nextItem.attributeValue("caption", "unknown")+"]</i><br>");
							}
						}
					}
				}
			}
		}
	    catch(Exception e) {
	        throw new JspException(e);
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

	public void setFolderEntry(FolderEntry folderEntry) {
	    this.folderEntry = folderEntry;
	}

}


