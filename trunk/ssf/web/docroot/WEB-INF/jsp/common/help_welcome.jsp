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
<c:if test="${empty ssf_help_files_loaded}">
<c:set var="ssf_support_help_loaded" value="1" scope="request"/>
<c:if test="${!empty ss_windowState && ss_windowState == 'maximized'}">
<div style="visibility:hidden; display:none;" id="ss_helpMenuAnchor"></div>
</c:if>
<script type="text/javascript">
ss_helpSystem.outputHelpWelcomeHtml();

var ss_helpSpotGifSrc = "<html:imagesPath/>icons/toolbar_help.gif";

</script>
</c:if>
