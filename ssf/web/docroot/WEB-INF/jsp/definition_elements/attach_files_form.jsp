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
 <c:if test='${empty property_number}'>
  <c:set var="property_number" value="1"/>
 </c:if>
<%@ include file="/WEB-INF/jsp/definition_elements/file_browse.jsp" %>
</div>
</c:if>
