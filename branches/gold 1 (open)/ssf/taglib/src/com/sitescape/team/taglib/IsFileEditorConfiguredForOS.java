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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.ssfs.util.SsfsUtil;

public class IsFileEditorConfiguredForOS extends TagSupport {

	private String relativeFilePath;
	private String operatingSystem;
	
	public int doStartTag() throws JspException {

		if(relativeFilePath == null)
			throw new JspException("File path must be specified");

		if(operatingSystem == null)
			throw new JspException("Operating system must be specified");
		
		String strOpenInEditor = SsfsUtil.openInEditor(relativeFilePath, operatingSystem);
		
		if (strOpenInEditor.equals("")) {
			return SKIP_BODY;
		} else {
			return EVAL_BODY_INCLUDE;
		}
	}
	
	public void setRelativeFilePath(String relativeFilePath) {
		this.relativeFilePath = relativeFilePath;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
}