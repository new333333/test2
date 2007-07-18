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
<% // The default profile listing view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />

<div class="ss_style ss_portlet">

<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

<% // Tabs %>
<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />

<% // Toolbar %>
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar2 ss_actions_bar" />

<% // List of users %>
<%@ include file="/WEB-INF/jsp/definition_elements/profile_list.jsp" %>

</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

