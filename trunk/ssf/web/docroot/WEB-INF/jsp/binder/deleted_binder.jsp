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
<ssf:nlt tag="binder.deleted"/>
</p>

<ssf:ifadapter>
<p style="text-align:center;">
<input type="button" value="<ssf:nlt tag="button.close"/>" onclick="window.close();"/>
</p>
</ssf:ifadapter>

</div>

<ssf:ifnotadapter>
	</div>
</div>

</ssf:ifnotadapter>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

