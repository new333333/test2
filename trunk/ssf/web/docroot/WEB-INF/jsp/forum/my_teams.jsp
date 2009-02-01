<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<ssf:ifaccessible>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<body class="ss_style_body tundra" onLoad="window.focus();">
</ssf:ifaccessible>

<div class="ss_indent_medium">
<c:forEach var="binder" items="${ss_myTeams}">
<jsp:useBean id="binder" type="java.util.Map" />
<a href="<ssf:permalink search="${binder}"/>" 
<c:if test="${!empty binder._entityPath}"> title="<%= ((String)binder.get("_entityPath")).replaceAll("&", "&amp;").replaceAll("\"", "&quot;") %>" </c:if>
<ssf:ifnotaccessible>
  onClick="return ss_gotoPermalink('${binder._docId}', '${binder._docId}', '${binder._entityType}', '${ss_namespace}', 'yes')"
</ssf:ifnotaccessible>
<ssf:ifaccessible>
  onClick="return parent.ss_gotoPermalink('${binder._docId}', '${binder._docId}', '${binder._entityType}', '${ss_namespace}', 'yes')"
</ssf:ifaccessible>
>${binder.title}</a><br/>
</c:forEach>

<c:if test="${empty ss_myTeams}">
<span class="ss_italic"><ssf:nlt tag="team.noTeams"/></span>
</c:if>
</div>

<ssf:ifaccessible>
</body>
</html>
</ssf:ifaccessible>
