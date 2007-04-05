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
<% //File form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String width = (String) request.getAttribute("property_width");
	String inline = (String) request.getAttribute("property_inline");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size='"+width+"'";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = caption;
	}
	if (inline == null) {inline = "block";}
	if (inline.equals("true")) {
		inline = "inline";
	} else {
		inline = "block";
	}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<div class="ss_entryContent" style="display:<%= inline %>;">
<span class="ss_labelAbove"><%= caption %><%= required %></span>
<input type="file" class="ss_text" name="<%= elementName %>" <%= width %> >
</div>
