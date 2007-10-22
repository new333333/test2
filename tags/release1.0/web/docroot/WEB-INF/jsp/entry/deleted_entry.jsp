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
<% // Displayed when the user tries to list a deleted folder %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>
<ssf:ifnotadapter>
<div id="ss_portlet_content" class="ss_style ss_portlet">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

<% // Tabs %>
<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
<div class="ss_clear"></div>

<div class="ss_tab_canvas">
<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
   <div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">

</ssf:ifnotadapter>

<div id="ss_portlet_content" class="ss_style ss_portlet ss_content_outer" style="margin:0px; padding:0px;">
<p style="text-align:center;">
<c:if test="${!empty entryMoved}">
<ssf:nlt tag="entry.moved">
<ssf:param name="value" value="${entryMoved.pathName}"/>
</ssf:nlt>
</c:if>
<c:if test="${empty entryMoved}">
<ssf:nlt tag="entry.deleted"/>
</c:if>
</p>

</div>

<ssf:ifnotadapter>
	</div>
</div>

</ssf:ifnotadapter>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

