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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
  <?xml version="1.0" encoding="UTF-8" ?> 
  <changes>
<c:forEach var="change" items="${changeLogs}">
	<c:out value="${change.xmlNoHeader}" escapeXml="false"/>
</c:forEach>
	</changes>