<% // The default folder view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssFolder" type="com.sitescape.team.domain.Binder" scope="request" />

<div class="ss_style ss_portlet">

<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

<% // Tabs %>
<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
<div class="ss_clear"></div>

<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>

<% // Toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar1 ss_actions_bar" />
</c:if>

<% // Show the folder default parts %>

<% // Folder listing %>

<%@ include file="/WEB-INF/jsp/definition_elements/folder_list.jsp" %>

</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

