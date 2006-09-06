<% // The default workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />

<div class="ss_style ss_portlet">

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>

<% // Toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar" />
</c:if>

<% // Show the workspace default parts %>

<% // Title %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_workspace_title.jsp" %>

<% // List of workspaces and folders %>
<%@ include file="/WEB-INF/jsp/definition_elements/workspace_binder_list.jsp" %>

</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

