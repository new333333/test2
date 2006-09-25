<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<c:if test="${!empty ssReloadUrl}">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>

</c:if>
<c:if test="${empty ssReloadUrl}">

<script type="text/javascript">
var ss_reloadUrl = "${ss_reloadUrl}";
</script>

<div id="ss_portlet_content" class="ss_style ss_portlet ss_content_outer">

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Tabs %>
<%@ include file="/WEB-INF/jsp/definition_elements/tabbar.jsp" %>

<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
<div class="ss_decor-round-corners-top1"><div><div></div></div></div>
	<div class="ss_decor-border3">
		<div class="ss_decor-border4">
			<div class="ss_rounden-content">
				
<% // Workspace toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<div class="ss_content_inner">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar" />
</div>
</c:if>

<div class="ss_content_inner">
<c:if test="${!ss_showSearchResults}">

<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<br/>

<% // Show the workspace according to its definition %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  processThisItem="true"
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssBinder}" />
</c:if>
<c:if test="${ss_showSearchResults}">
<%@ include file="/WEB-INF/jsp/definition_elements/search_results_view.jsp" %>
</c:if>
</div>

			</div>
		</div>
	</div>
	<div class="ss_decor-round-corners-bottom1"><div><div></div></div></div>

<% // Footer toolbar %>
<%@ include file="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" %>

</div>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

