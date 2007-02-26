<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>

<c:if test="${!empty ssReloadUrl}">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>
</c:if>

<script type="text/javascript">
	function appendUserIdsToURL(hrefObj) {
		var userIdsCheckboxes = document.getElementsByName("team_member_ids");
		for (var i = 0; i < userIdsCheckboxes.length; i++) {
			if (userIdsCheckboxes[i].checked)
				hrefObj.href += "&ssUsersIdsToAdd=" + userIdsCheckboxes[i].value;
		}
		return false;
	}	
</script>

<c:if test="${empty ssReloadUrl}">
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<script type="text/javascript">
var ss_reloadUrl = "${ss_reloadUrl}";
</script>

<div id="ss_portlet_content" class="ss_style ss_portlet ss_content_outer">

<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

<% // Tabs %>
<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
<div class="ss_clear"></div>

<div class="ss_tab_canvas">
			  <div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">
				
					<% // Workspace toolbar %>
					<c:if test="${!empty ssFolderToolbar}">
					<div class="ss_content_inner">
			<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar1 ss_actions_bar" />
					</div>
					</c:if>

					<div class="ss_content_inner">
					
					<% // Navigation links %>
					<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
					<br/>
					
					<%@ include file="/WEB-INF/jsp/forum/list_team_members.jsp" %>
							
					</div>

			  </div>
			</div>
		</div>



<% // Footer toolbar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />

</div>
</div>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

