<%
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
%>
<% //Text form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String width = (String) request.getAttribute("property_width");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "width=\""+width+"\"";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<span class=\"ss_bold\">"+caption+"</span><br/>";
	}
%>
<div ><%= caption %>
<input type="text" class="ss_text" name="<%= elementName %>" <%= width %> 
 value="<c:out value="${ssDefinitionEntry.customAttributes[property_name].value}"/>"/>
</div>
