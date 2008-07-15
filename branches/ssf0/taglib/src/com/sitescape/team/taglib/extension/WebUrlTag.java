/**
 * 
 */
package com.sitescape.team.taglib.extension;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.Tag;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.web.WebKeys;

/**
 * 
 * A JSP {@link Tag} which generates a servlet context relative URL to an
 * extension resource as an attribute of the first child element of the
 * <code>tag</code>.
 * 
 * @author dml
 * 
 */
public class WebUrlTag extends XmlTransformTag {

	private static final long serialVersionUID = -3776561358357108883L;
	private static final String EXT_BASE_PATH = "/opt";
	private String attribute = "href";
	private String url;

	@Override
	public <Elements extends List<Element>> List<Element> apply(Elements es) {
		for (Element e : es) {
			e.addAttribute(attribute, asExtensionUrl(url));
		}
		return es;
	}

	/**
	 * Returns an extension path prefixed version of the specified
	 * <code>url</code>
	 * 
	 * @param url -
	 *            the URL {@link String} to transform
	 * @return a servlet-context relative URL to an extension resource
	 */
	private String asExtensionUrl(String url) {
		String extensionName = ((Document) pageContext.getRequest()
				.getAttribute(WebKeys.CONFIG_DEFINITION)).getRootElement()
				.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_EXTENSION, "");
		return ((HttpServletRequest) pageContext.getRequest()).getContextPath()
				+ EXT_BASE_PATH + "/" + extensionName + "/" + url;
	}

	public void setAttr(String attribute) {
		this.attribute = attribute;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
