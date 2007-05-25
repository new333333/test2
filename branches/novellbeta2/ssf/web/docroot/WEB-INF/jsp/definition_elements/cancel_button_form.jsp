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
<% //Cancel button form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>


<c:choose>
	<c:when test="${!empty property_onClick}">
		<% // from entry defintion %>
		<c:set var="onClick" value="${property_onClick}"/>
	</c:when>
	<c:when test="${!empty onClickCancelRoutine}">
		<% // from template %>
		<c:set var="onClick" value="${onClickCancelRoutine}"/>
	</c:when>
	<c:when test="${ssOperation == 'add_folder_reply'}">
		<% // folder reply %>
		<c:set var="onClick" value="ss_cancelButtonCloseWindow();return false;"/>
	</c:when>	
	<c:otherwise>
		<c:set var="onClick" value=""/>
	</c:otherwise>
</c:choose>

<input type="submit" class="ss_submit" name="cancelBtn" value="${property_caption}" 
  onClick="ss_buttonSelect('cancelBtn'); ${onClick}" />
