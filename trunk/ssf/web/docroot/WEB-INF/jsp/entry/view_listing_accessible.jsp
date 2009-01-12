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
<% // The main accessible view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
	if (renderRequest.getAttribute(WebKeys.ACTION) == null ||
		"view_folder_listing".equals(renderRequest.getAttribute(WebKeys.ACTION)) ||
		"view_profile_listing".equals(renderRequest.getAttribute(WebKeys.ACTION))) {
%><c:set var="ss_windowTitle" value="${ssBinder.title}" scope="request"/><%
	} else {
%><c:set var="ss_windowTitle" value="${ssEntry.title}" scope="request"/><%
	}
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<c:set var="showFolderPage" value="true"/>
<ssf:ifnotadapter>
    <c:set var="showFolderPage" value="false"/>
</ssf:ifnotadapter>
<ssf:ifadapter>
<body class="ss_style_body tundra">
<ssf:ifLoggedIn><c:if test="${empty ss_noEnableAccessibleLink && !empty ss_accessibleUrl && (empty ss_displayStyle || ss_displayStyle != 'accessible')}">
  <a class="ss_skiplink" href="${ss_accessibleUrl}"><img border="0"
    <ssf:alt tag="accessible.enableAccessibleMode"/> 
    src="<html:imagesPath/>pics/1pix.gif" /></a><%--
		--%></c:if></ssf:ifLoggedIn>
<div id="ss_pseudoPortalDiv${renderResponse.namespace}">
</ssf:ifadapter>
<script type="text/javascript">
function ss_resizeTopDiv_${renderResponse.namespace}() {
	ss_resizeTopDiv('${renderResponse.namespace}');
}
ss_createOnResizeObj("ss_resizeTopDiv", ss_resizeTopDiv_${renderResponse.namespace});
ss_createOnLayoutChangeObj("ss_resizeTopDiv", ss_resizeTopDiv_${renderResponse.namespace});
</script>
<c:if test="${!empty ssReloadUrl}">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>

</c:if>
<c:if test="${empty ssReloadUrl}">
<%@ include file="/WEB-INF/jsp/entry/view_workarea_common.jsp" %>
<c:if test="${showFolderPage}">
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<%@ include file="/WEB-INF/jsp/entry/view_listing_common.jsp" %>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowHeight" type="java.lang.Integer" scope="request" />

  <c:if test="<%= !reloadCaller %>">
    <c:if test="<%= !isViewEntry %>">
 <div id="ss_portlet_content" class="ss_style ss_portlet ss_content_outer" style="margin:0px 15px 0px 0px; padding:0px; width:100%;">
 <%@ include file="/WEB-INF/jsp/entry/view_accessible.jsp" %>
 </div>
 <c:if test="${!ss_mashupHideSidebar}">
 <div>
	<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
 </div>
 </c:if>
    </c:if>
  </c:if>
  <c:if test="<%= reloadCaller %>">
 <script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
 </script>
  </c:if>
</c:if>
</c:if>
<ssf:ifadapter>

</div>
</body>
</html>
</ssf:ifadapter>

