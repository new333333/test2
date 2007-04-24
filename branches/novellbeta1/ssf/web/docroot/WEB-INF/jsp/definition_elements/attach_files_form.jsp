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
<% //File form for attaching files %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${empty property_hide || !property_hide}">
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String number = (String) request.getAttribute("property_number");
	String width = (String) request.getAttribute("property_width");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size='"+width+"'";
	}
	if (number == null || number.equals("")) {
		number = "1";
	}
	int count = Integer.parseInt(number);
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = caption;
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
	</ssf:url>"
</script>

<div class="ss_entryContent" >
<div id="ss_duplicateFileCheck" style="display:none"><span class="ss_formError"/></div>
<span class="ss_labelAbove">${property_caption}</span>
<%
	for (int i = 1; i <= count; i++) {
%>
<input type="file" class="ss_text" name="<%= elementName + Integer.toString(i) %>" <%= width %> onchange="javascript:ss_checkForDuplicateFileAjax(this)"/><br>
<%
	}
%>
</div>
</c:if>
