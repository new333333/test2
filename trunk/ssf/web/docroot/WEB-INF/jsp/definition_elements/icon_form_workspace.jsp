<% //Icon form for folders %>
<%@ page import="com.sitescape.ef.util.SPropsUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}

	Binder binder = (Binder) request.getAttribute("ssDefinitionEntry");
	String iconListPath = "icons.workspace";
	String[] iconList = (String[]) SPropsUtil.getString(iconListPath, "").split(",");
	if (binder != null) {
		String iconValue = binder.getIconName();
		if (iconValue == null) iconValue = "";
%>
<div style="display:inline;"><%= caption %>
<%
		for (int i = 0; i < iconList.length; i++) {
			String iconListValue = iconList[i].trim();
			if (iconListValue.equals("")) continue;
			String checked = "";
			if (iconValue.equals(iconListValue)) {
				checked = " checked=\"checked\"";
			}
%>
<input type="radio" class="ss_text" name="${property_name}" 
  value="<%= iconListValue %>" <%= checked %>
/><img src="<html:imagesPath/>.<%= iconListValue %>" /><br/>
<%
		}
%>
</div>
<%
	}
%>

