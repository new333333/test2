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
<% //Custom jsp element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
	//Get the jsp to be called
	String formJsp = (String) request.getAttribute("property_formJsp");
	String viewJsp = (String) request.getAttribute("property_viewJsp");
	String mailJsp = (String) request.getAttribute("property_mailJsp");
%>
<c:if test="${ssConfigJspStyle == 'form' && !empty property_formJsp}">
<%@include file="/WEB-INF/jsp/custom_jsps/<%=formJsp%>" %>
</c:if>
<c:if test="${ssConfigJspStyle == 'view' && !empty property_viewJsp}">
<%@include file="/WEB-INF/jsp/custom_jsps/<%=viewJsp%>" %>
</c:if>
<c:if test="${ssConfigJspStyle == 'mail' && !empty property_mailJsp}">
<%@include file="/WEB-INF/jsp/custom_jsps/<%=mailJsp%>" %>
</c:if>
