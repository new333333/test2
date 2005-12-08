package com.sitescape.ef.portletadapter.taglib;

import com.sitescape.util.Validator;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class PortletURLTei extends TagExtraInfo {

	public VariableInfo[] getVariableInfo(TagData data) {
		String var = data.getAttributeString("var");

		if (Validator.isNotNull(var)) {
			return new VariableInfo[] {
				new VariableInfo(
					var, String.class.getName(), true, VariableInfo.AT_END),
			};
		}
		else {
			return null;
		}
	}

}