package com.sitescape.team.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.ssfs.util.SsfsUtil;

public class EditorTypeToUseForEditInPlace extends TagSupport {

	private String browserType;
	private String editorType;
	
	public int doStartTag() throws JspException {
		
		if (browserType == null || "".equals(browserType)) throw new JspException("Browser Type must be specified");
		if (editorType == null || "".equals(editorType)) throw new JspException("Editor Type must be specified");

		String strEditorType = "";
		
		if (browserType.equals("ie")) {
			strEditorType = SsfsUtil.attachmentEditTypeForIE();
		} else if (browserType.equals("nonie")) {
			strEditorType = SsfsUtil.attachmentEditTypeForNonIE();
		} else {
			throw new JspException("Browser Type must be 'ie' or 'nonie'");
		}
		
		if (editorType.equals(strEditorType)) {
			return EVAL_BODY_INCLUDE;
		} else {
			return SKIP_BODY;
		}
	}
	
	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}
}