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
<script type="text/javascript">
var ss_findEntryForFileUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="find_entry_for_file" />
	<ssf:param name="folderId" value="${ssFolder.id}" />
	</ssf:url>";

ss_addValidator("ss_duplicateFileCheck_<%= elementName %>", ss_ajax_result_validator);
var <%= elementName %>_ok = 1;
</script>
<div class="ss_entryContent" style="display:<%= inline %>;">
<span id="<%= elementName %>_label" class="ss_labelAbove"><%= caption %><%= required %></span>
<div class="needed-because-of-ie-bug">
<div id="ss_duplicateFileCheck_<%= elementName %>" style="display:none; visibility:hidden;" ss_ajaxResult="ok"><span class="ss_formError"></span></div>
</div>
<input type="file" class="ss_text" name="<%= elementName %>" id="<%= elementName %>" <%= width %> onchange="ss_ajaxValidate(ss_findEntryForFileUrl, this,'<%= elementName %>_label', 'ss_duplicateFileCheck_<%= elementName %>');" />
</div>
