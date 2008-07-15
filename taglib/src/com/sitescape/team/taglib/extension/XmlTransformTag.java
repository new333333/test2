/**
 * 
 */
package com.sitescape.team.taglib.extension;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.CollectionUtil.Func1;
import com.sitescape.team.util.CollectionUtil.Id;

/**
 * 
 * A base class providing a simplified interface for JSP tags which manipulate
 * their enclosed XML elements.
 * 
 * @author dml
 * 
 */
public class XmlTransformTag extends BodyTagSupport implements Func1<List<Element>, List<Element>> {
	
	private static final long serialVersionUID = 8579239128949025982L;
	private static final SAXReader reader = new SAXReader(false);
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
	public final int doAfterBody() throws JspException {
		StringBuilder body = new StringBuilder("<blink:seq xmlns:blink='http://www.kablink.org/tags'>");
		body.append(getBodyContent().getString());
		body.append("</blink:seq>");
		try {
			@SuppressWarnings("unchecked")
			List<Element> elements = reader.read(new StringReader(body.toString())).getRootElement().elements();
			elements = apply(elements);
			for (Element e : elements) {
				getPreviousOut().write(e.asXML());
			}
		} catch (DocumentException e) {
			throw new JspException("Unable to parse enclosed XML: \""
					+ getBodyContent().getString() + "\".  " + e.getMessage(), e);
		} catch (IOException e) {
			throw new JspException("Exception writing results to JSP.  " + e.getMessage(), e);
		}
		return Tag.SKIP_BODY;
	}
	
	/* (non-Javadoc)
	 * @see com.sitescape.team.util.CollectionUtil.Func1#apply(java.lang.Object)
	 */
	public <Elements extends List<Element>> List<Element> apply(Elements es) {
		return CollectionUtil.map(getTransform(), es);
	}
	
	/**
	 * Returns a {link Func1} to be applied to the contained elements of this
	 * {@link Tag}. The default implementation is {@link Id}, the identity
	 * function. Sub-classes are expected to override this method for more
	 * complex behaviors.
	 * 
	 * @return Returns a {link Func1} to be applied to the contained elements of
	 *         this {@link Tag}
	 */
	protected Func1<Element, Element> getTransform() {
		return new Id<Element>(); 
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public final int doEndTag() throws JspException {
		return super.doEndTag();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doInitBody()
	 */
	@Override
	public final void doInitBody() throws JspException {
		super.doInitBody();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public final int doStartTag() throws JspException {
		return super.doStartTag();
	}

}
