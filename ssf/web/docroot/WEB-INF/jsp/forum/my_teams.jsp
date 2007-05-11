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
<ssf:ifaccessible>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<body class="ss_style_body" onLoad="window.focus();">
</ssf:ifaccessible>

<div class="ss_indent_medium">
<c:forEach var="binder" items="${ss_myTeams}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${binder.id}">
		    <ssf:param name="entityType" value="${binder.entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" 
<ssf:ifnotaccessible>
  onClick="return ss_gotoPermalink('${binder.id}', '${binder.id}', '${binder.entityType}', '', '1')"
</ssf:ifnotaccessible>
<ssf:ifaccessible>
  onClick="return parent.ss_gotoPermalink('${binder.id}', '${binder.id}', '${binder.entityType}', '', '1')"
</ssf:ifaccessible>
>${binder.title}</a><br/>
</c:forEach>
</div>

<ssf:ifaccessible>
</body>
</html>
</ssf:ifaccessible>
