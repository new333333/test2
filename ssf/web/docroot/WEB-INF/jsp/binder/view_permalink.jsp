<%
// View a permalink
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
<c:if test="${!empty ssPermalink}">
<script type="text/javascript">
self.location.replace('${ssPermalink}');
</script>
</c:if>
<c:if test="${empty ssPermalink}">
<script type="text/javascript">
self.location.replace('/c');
</script>
</c:if>
