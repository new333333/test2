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
package com.sitescape.team.portletadapter.taglib;

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