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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.util.Pair;
import com.sitescape.util.search.Constants;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

/**
 * @author Peter Hurley
 * 
 */
public class DisplayConfiguration extends TagSupport {
	private static final String CUSTOM_JSP_DIR = "/WEB-INF/jsp/custom_jsps/";
	private static final String VIEW_JSP_TYPE = "viewJsp";
	private static final String MOBILE_JSP_TYPE = "mobileJsp";
	private static final String DATA_VIEW_ATTR = "dataView";
	private static final String CUSTOM_JSP_ATTR = "customJsp";
	private static final String CUSTOM_JSP_NAME_ATTR = "customJspName";
	private static final String FORM_ATTR = "formItem";
	private static final String PROP_LIST_KEY = "propertyValues_";
	private static final String PROP_KEY = "property_";
	private static final long serialVersionUID = 1L;
	private Document configDefinition;
	private Element configElement;
	private String configJspStyle;
	/**
	 * This attribute should be removed so we can treat item processing
	 * uniformly. Either process a single element or all children elements,
	 * *not* both.
	 */
	@Deprecated
	private boolean processThisItem = false;
	private DefinableEntity entry;

	private ProfileModule profileModule;

	public int doStartTag() throws JspException {
		profileModule = (ProfileModule) SpringContextUtil
				.getBean("profileModule");
		DefinitionConfigurationBuilder configBuilder = DefinitionHelper
				.getDefinitionBuilderConfig();

		if (this.configDefinition == null) {
			release();
			throw new JspException("No configuration definition available.");
		}
		List<Element> items = new ArrayList<Element>();
		if (processThisItem) {
			items.add(this.configElement);
		} else {
			@SuppressWarnings("unchecked")
			// assigning to temp list to avoid unchecked cast warning
			// XXX wrap Dom4j with type-ful interfaces
			List<Element> elems = this.configElement.elements("item");
			items.addAll(elems);
		}
		for (Element item : items) {
			String name = item.attributeValue("name", "");
			Element itemDef = configBuilder.getItem(configDefinition, name);
			Pair<String, String> jsps = determineJsps(item, itemDef);

			if (StringUtils.isBlank(jsps.getFirst())) {
				throw new JspException("Unable to determine primary view for "
						+ item.getName());
			}
			RequestDispatcher rd = pageContext.getRequest()
					.getRequestDispatcher(jsps.getFirst());

			ServletRequest req = new DynamicServletRequest(
					(HttpServletRequest) pageContext.getRequest());

			setRequestAttributes(req, item, itemDef, jsps);

			StringServletResponse res = new StringServletResponse(
					(HttpServletResponse) pageContext.getResponse());
			try {
				// FIXME ???!!!
				rd.include(req, res);
				pageContext.getOut().print("<!-- " + jsps.getFirst() + " -->");
				pageContext.getOut().print(res.getString());
				pageContext.getOut().print(
						"<!-- end "
								+ jsps.getFirst().substring(
										jsps.getFirst().lastIndexOf('/') + 1)
								+ " -->");
			} catch (ServletException e) {
				throw new JspException(
						"Servlet exception when attempting to include JSP "
								+ jsps.getFirst() + " in "
								+ configElement.getQualifiedName(), e);
			} catch (IOException e) {
				throw new JspException("IO error writing to JSPWriter for JSP "
						+ jsps.getFirst() + " in "
						+ configElement.getQualifiedName(), e);
			} finally {
				release();
			}
		} // end for
		release();
		return SKIP_BODY;
	}

	/**
	 * Returns a {@link Pair} of the proper JSP and a fall-back option. The
	 * complexity of this algorithm seems a bit off-balance with its function...
	 * 
	 * @return a {@link Pair} of the proper JSP and a fall-back option,
	 *         respectively
	 */
	private Pair<String, String> determineJsps(Element item, Element itemDef)
			throws JspException {
		String name = item.attributeValue("name", "");
		DefinitionConfigurationBuilder configBuilder = DefinitionHelper
				.getDefinitionBuilderConfig();
		if (itemDef == null) {
			throw new JspException(
					"Unable to determine item definition for item "
							+ item.getQualifiedName() + " with name " + name);
		}
		String result = null;
		String fallback = configBuilder.getItemJspByStyle(itemDef, name,
				this.configJspStyle);

		if (CUSTOM_JSP_ATTR.equals(name)) {
			// item[@name='customJsp']
			result = DefinitionUtils.getPropertyValue(item, "formJsp");
			if (StringUtils.isNotBlank(result)) {
				return new Pair<String, String>(CUSTOM_JSP_DIR + result,
						fallback);
			}
		}

		String jspType = configJspStyle.equals(Definition.JSP_STYLE_MOBILE) ? MOBILE_JSP_TYPE
				: VIEW_JSP_TYPE;
		if (CUSTOM_JSP_NAME_ATTR.equals(name)) {
			// item[@name='customJspName']
			result = DefinitionUtils.getPropertyValue(item, jspType);
			if (StringUtils.isNotBlank(result)) {
				return new Pair<String, String>(CUSTOM_JSP_DIR + result,
						fallback);
			}
		}

		if (!"true".equals(item.selectSingleNode(
				"jsps/jsp[@name='custom']/@inherit").getText())) {
			// <jsp> defined, inherit is false or undefined
			result = item.selectSingleNode("jsps/jsp[@name='custom']/@value")
					.getText();
			if (StringUtils.isNotBlank(result)) {
				return new Pair<String, String>(CUSTOM_JSP_DIR + result,
						fallback);
			}
		}

		String form = item.attributeValue(FORM_ATTR, "");
		if (!DATA_VIEW_ATTR.equals(item.attributeValue("type"))) {
			return new Pair<String, String>(fallback, fallback);
		}
		if (CUSTOM_JSP_ATTR.equals(form)) {
			// item[@formItem='customJsp']
			result = DefinitionUtils.getPropertyValue(item, jspType);
			if (StringUtils.isNotBlank(result)) {
				return new Pair<String, String>(CUSTOM_JSP_DIR + result,
						fallback);
			}
		}
		// maybe inherit jsp definition
		String nameProp = item.selectSingleNode(
				".//item/properties/property[@name='name']/@value").getText();
		result = this.configDefinition
				.selectSingleNode(
						"//item[@type='form']//item/properties/property[@name='name' and @value='"
								+ nameProp
								+ "']/../../jsps/jsp[@name='custom']/@value")
				.getText();
		result = StringUtils.isNotBlank(result) ? CUSTOM_JSP_DIR + result
				: fallback;
		return new Pair<String, String>(result, fallback);
	}
	
	@SuppressWarnings("unchecked")
	private void setRequestAttributes(ServletRequest req, Element item,
			Element itemDef, Pair<String, String> jsps) {
		String name = item.attributeValue("name", "");
		req.setAttribute("item", item);
		req.setAttribute(WebKeys.CONFIG_DEFINITION, this.configDefinition);
		req.setAttribute(WebKeys.CONFIG_ELEMENT, this.configElement);
		req.setAttribute(WebKeys.CONFIG_JSP_STYLE, this.configJspStyle);
		req.setAttribute(WebKeys.CONFIG_FALLBACK_JSP, jsps.getSecond());
		// Each item property that has a value is added as a
		// "request attribute".
		// The key name is "property_xxx" where xxx is the
		// property name.
		// At a minimum, make sure the name and caption
		// variables are defined
		req.setAttribute("property_name", "");
		req.setAttribute("property_caption", "");

		// Also set up the default values for all properties
		// defined in the definition configuration
		// These will be overwritten by the real values (if
		// they exist) below
		List<Element> itemDefProperties = itemDef
				.selectNodes("properties/property");
		for (Element property : itemDefProperties) {
			String propertyName = property.attributeValue("name", "");
			String propertyType = property.attributeValue("type", "text");
			String val = NLT.getDef(property.attributeValue("value",
					""));
			// Get the value(s) from the actual definition
			if (propertyType.equals("selectbox")) {
				String requestKey = PROP_LIST_KEY + propertyName;
				req.setAttribute(requestKey, CollectionUtil.map(
						new CollectionUtil.Func1<String, String>() {
							public String apply(String x) {
								return NLT.get(x);
							}
						}, item.selectNodes("properties/property[@name='"
								+ propertyName + "']/@value")));
				continue;
			}
			Element selItem = (Element) item
					.selectSingleNode("properties/property[@name='"
							+ propertyName + "']");
			val = (selItem != null && "textarea".equals(propertyType)) ? val
					: selItem.getText();
			req.setAttribute(property.attributeValue("setAttribute",
					PROP_KEY + propertyName), val);
		}
		req.setAttribute(WebKeys.DEFINITION_ENTRY, this.entry);

		// Set up any item specific beans
		if (name.equals(ObjectKeys.DEFINITION_WORKSPACE_REMOTE_APPLICATION)
				|| name
						.equals(ObjectKeys.DEFINITION_FOLDER_REMOTE_APPLICATION)
				|| name
						.equals(ObjectKeys.DEFINITION_ENTRY_REMOTE_APPLICATION)) {
			Map<String, Object> options = new HashMap<String, Object>();
			options.put(ObjectKeys.SEARCH_SORT_BY,
					Constants.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
			// get them all
			options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE - 1);

			Document searchFilter = DocumentHelper.createDocument();
			Element field = searchFilter
					.addElement(Constants.FIELD_ELEMENT);
			field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,
					Constants.ENTRY_TYPE_FIELD);
			Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
			child.setText(Constants.ENTRY_TYPE_APPLICATION);
			options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter);

			req.setAttribute(WebKeys.REMOTE_APPLICATION_LIST, profileModule
					.getApplications(
							profileModule.getProfileBinder().getId(),
							options).get(ObjectKeys.SEARCH_ENTRIES));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#release()
	 */
	@Override
	public void release() {
		super.release();
		this.configDefinition = null;
		this.configElement = null;
		this.configJspStyle = null;
		this.processThisItem = false;
		this.entry = null;
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
