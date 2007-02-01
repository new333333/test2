<% //Icon form element %>
<%@ page import="com.sitescape.ef.util.SPropsUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}
%>
<div style="display:inline;"><%= caption %>

<%
	String iconValue = (String)request.getAttribute("iconValue");
	String iconListPath = (String)request.getAttribute("iconListPath");
	String[] iconList = SPropsUtil.getCombinedPropertyList(iconListPath, ObjectKeys.CUSTOM_PROPERTY_PREFIX);
	if (iconValue == null) iconValue = "";
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
/><img border="0" src="<html:imagesPath/>.<%= iconListValue %>" /><br/>
<%
	}
    
%>

</div>