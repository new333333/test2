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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:if test="${empty ssErrorList}">
<script type="text/javascript">
var url_str = '<portlet:renderURL windowState="normal" portletMode="view"/>';
var timeout = 0;
<c:if test="${!empty ssDownloadURL}">
timeout = 200;
</c:if>
setTimeout("self.location.replace(url_str)", 0);
</script>
</c:if>
<c:if test="${!empty ssErrorList}">
<form class="ss_style ss_form" action="<portlet:renderURL windowState="normal" portletMode="view"/>"
		 method="post" name="<portlet:namespace />fm">
<br/>
<br/>
<span class="ss_bold"><ssf:nlt tag="administration.errors"/></span>
<br/>
<br/>
<ul>
<c:forEach var="err" items="${ssErrorList}">
	<li>${err}</li>
</c:forEach>
</ul>
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">

</form>
</c:if>
