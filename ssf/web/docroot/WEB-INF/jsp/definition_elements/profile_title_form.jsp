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
<% //Name form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}
%>
<div class="ss_entryContent">
<div class="ss_labelAbove"><%= caption %></div>
<input type="text" size="40" name="name" value="<c:out value="${ssDefinitionEntry.name}"/>"
	<c:if test="${empty ssDefinitionEntry.name}">
	  class="ss_text"
	</c:if>
	<c:if test="${!empty ssDefinitionEntry.name}">
	  class="ss_text ss_readonly" READONLY="true" 
	</c:if>
/>
</div>
