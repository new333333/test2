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
<% // The main forum view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>
<c:if test="${!empty ssReloadUrl}">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>

</c:if>
<c:if test="${empty ssReloadUrl}">
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<%@ include file="/WEB-INF/jsp/entry/view_listing_common.jsp" %>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowHeight" type="java.lang.Integer" scope="request" />

  <c:if test="<%= !reloadCaller %>">
    <c:if test="<%= !isViewEntry %>">

<div id="ss_showentryhighwatermark" style="position:absolute; visibility:visible;">
<img border="0" <ssf:alt/> src="<html:imagesPath/>pics/1pix.gif">
</div>
<div id="ss_portlet_content" class="ss_style ss_portlet ss_content_outer" style="margin:0px; padding:0px;">
<%@ include file="/WEB-INF/jsp/entry/view_vertical.jsp" %>
</div>
     </c:if>
  </c:if>
  <c:if test="<%= reloadCaller %>">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>
  </c:if>
</c:if>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

