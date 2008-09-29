/**
 * 
 */
package com.sitescape.team.taglib.extension;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.util.CollectionUtil.Func1;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;

/**
 * 
 * Adds an attribute to the enclosed element with the URL of a
 * {@link FileAttachment}.
 * 
 * @author dml
 * 
 */
public class AttachmentUrlTag extends XmlTransformTag {

	private static final long serialVersionUID = 3068146026176191702L;
	private final Func1<Element, Element> transform = new Func1<Element, Element>() {
		public <E extends Element> Element apply(E e) {
			Entry entry = (Entry) pageContext.getRequest().getAttribute(
					WebKeys.ENTRY);
			e.add(new DefaultAttribute(attr, prefix
					+ WebUrlUtil.getAttachmentUrls(
							(HttpServletRequest) pageContext.getRequest(),
							entry).get(0) + suffix));
			return e;
		}
	};
	private String attr = "href";
	private String prefix = "";
	private String suffix = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitescape.team.taglib.extension.XmlTransformTag#getTransform()
	 */
	@Override
	protected Func1<Element, Element> getTransform() {
		return transform;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#release()
	 */
	@Override
	public void release() {
		super.release();
		attr = null;
		prefix = "";
		suffix = "";
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}
