/**
 * 
 */
package com.sitescape.team.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.web.WebKeys;
/**
 * 
 * A JSP {@link Tag} which generates a servlet context relative URL to an
 * extension resource as an attribute of the first child element of the
 * <code>tag</code>.
 * 
 * 
 */
public class ExtensionUrlTag extends BodyTagSupport   {

	private static final long serialVersionUID = -3776561358357108883L;

	private static Log logger = LogFactory.getLog(UrlTag.class);

	private String url=null;
	
	public ExtensionUrlTag() {
		setup();
	}
	/** 
	 * Initalize params at end of call and creation
	 * 
	 *
	 */
	protected void setup() {
		url = null;
	}
	public int doEndTag() throws JspException {
		try {
			String extensionName = ((Document) pageContext.getRequest()
							.getAttribute(WebKeys.CONFIG_DEFINITION)).getRootElement()
							.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_EXTENSION, "");
			String fullUrl = ((HttpServletRequest)pageContext.getRequest()).getContextPath() +  "/ext/" + RequestContextHolder.getRequestContext().getZoneName() +
							"/" + extensionName + "/" + url;

			pageContext.getOut().print(fullUrl);
			return SKIP_BODY;
		}  catch(Exception e) {
	    	// Sometimes this tag is called from an error jsp page to render information
	    	// about the previous error occured during the normal flow of control.
	    	// Often in time, the state is such that the code in this method fails because
	    	// an output stream or reader was previously obtained from the same request
	    	// object by the code executing the normal flow. Throwing another error from
	    	// this place is the last thing we want to do in that case, since it can 
	    	// make the bad situation even uglier. For that reason, we don't want to 
	    	// propogate the exception up the call stack. Instead, just log the error
	    	// and return normally from here.  
	        //throw new JspException(e);
	        logger.warn(e.toString());
	        return SKIP_BODY;
	    } finally {
			setup();
		}
	}

	public void setUrl(String url) {
	    this.url = url;
	}



}
