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
<div id="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}" class="ss_muted_tag_cloud">
<c:forEach var="ptag" items="${ssPersonalTags}">
 <span class="ss_muted_cloud_tag"><c:out value="${ptag.name}"/></span>
</c:forEach>
<c:forEach var="tag" items="${ssCommunityTags}">
 <span class="ss_muted_cloud_tag"><c:out value="${tag.name}"/></span>
</c:forEach>
</div>
