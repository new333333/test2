<%
// The dashboard "search" component
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
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:set var="ss_pageNumber" value="0"/>

<!-- TITLE OR DESCRIPTION?  -->
<p/>
<c:if test="${ssConfigJspStyle != 'template'}">
<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/dashboard/search_view2.jsp" %>
</div>
</c:if>