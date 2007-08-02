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
<% // Sidebars based on the folder listing style  %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${ss_folderViewStyle == 'event'}">
<jsp:include page="/WEB-INF/jsp/sidebars/default.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'file'}">
<jsp:include page="/WEB-INF/jsp/sidebars/default.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'blog'}">
<jsp:include page="/WEB-INF/jsp/sidebars/blog.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'wiki'}">
<jsp:include page="/WEB-INF/jsp/sidebars/wiki.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'photo'}">
<jsp:include page="/WEB-INF/jsp/sidebars/photo.jsp" />
</c:if>
<c:if test="${empty ss_folderViewStyle || ss_folderViewStyle == 'folder'}">
<jsp:include page="/WEB-INF/jsp/sidebars/default.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'guestbook'}">
<jsp:include page="/WEB-INF/jsp/sidebars/default.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'task'}">
<jsp:include page="/WEB-INF/jsp/sidebars/default.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'survey'}">
<jsp:include page="/WEB-INF/jsp/sidebars/default.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'table'}">
<jsp:include page="/WEB-INF/jsp/sidebars/default.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'milestone'}">
<jsp:include page="/WEB-INF/jsp/sidebars/default.jsp" />
</c:if>