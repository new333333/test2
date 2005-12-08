package com.sitescape.ef.portletadapter.taglib;

import javax.servlet.jsp.tagext.TagSupport;

public class DefineObjects1Tag extends TagSupport {

	public int doStartTag() {

		// Do nothing! - This tag definition exists merely to allow the 
		// JSP translation engine to use accompanying Tag Extra Info to 
		// declare required variables appropriately. The actual runtime
		// execution of this tag is irrelevant (as long as it does not
		// cause any stupid side effect).  

		return SKIP_BODY;
	}
}