<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<ssf:ifadapter>
<body>
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
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

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
<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
<div class="ss_decor-round-corners-top1"><div><div></div></div></div>
	<div class="ss_decor-border3">
		<div class="ss_decor-border4">
			<div class="ss_rounden-content">
			  <div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">
				
					<% // Workspace toolbar %>
					<c:if test="${!empty ssFolderToolbar}">
					<div class="ss_content_inner">
					<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar" />
					</div>
					</c:if>

					<div class="ss_content_inner">
					
					<% // Navigation links %>
					<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
					<br/>
					
					<% // Show the team members %>
					
					<div class="ss_buddiesListHeader">
						<img border="0" src="<html:imagesPath/>icons/group.gif"/> <span class="ss_largerprint ss_bold"><ssf:nlt tag="teamMembersList.title"/></span> <span class="ss_fineprint ss_light"><ssf:nlt tag="teamMembersList.count"/></span> <span class="ss_fineprint ss_bold">${ssTeamMembersCount}</span>
					</div>
					
					<table class="ss_buddiesList" cellpadding="0" cellspacing="0">
					
						<c:if test="${ssTeamMembersCount > 0}">					
							<c:forEach var="member" items="${ssTeamMembers}">
								<tr>
									<td class="selectable"><input type="checkbox" name="team_member_ids" value="${member.id}" /></td>
									<td class="picture">
										<div class="ss_thumbnail_small_buddies_list"><div>
										  <c:set var="selections" value="${member.customAttributes['picture'].value}" />
										  <c:set var="pictureCount" value="0"/>
										  <c:forEach var="selection" items="${selections}">
										  	<c:if test="${pictureCount == 0}">
												<img border="0" src="<ssf:url 
												    webPath="viewFile"
												    folderId="${member.parentBinder.id}"
												    entryId="${member.id}" >
												    <ssf:param name="fileId" value="${selection.id}"/>
												    <ssf:param name="viewType" value="scaled"/>
												    </ssf:url>" />
											</c:if>
											<c:set var="pictureCount" value="${pictureCount + 1}"/>
										  </c:forEach>
										</div></div>
									 </td>
									<td>
										<a class="ss_bold" href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
											value="${member.parentBinder.id}"/><ssf:param name="entryId" 
											value="${member.id}"/></ssf:url>">${member.title}</a>
									</td>
									<td>${member.organization}</td>
									<td>
										<div id="ss_presenceOptions_${renderResponse.namespace}"></div>
										<ssf:presenceInfo user="${member}" 
										    showOptionsInline="false" 
										    optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
									</td>
									<td><a class="ss_bold" href="mailto:${member.emailAddress}">${member.emailAddress}</a></td>
								</tr>
							</c:forEach>
						</c:if>
					
						<c:if test="${ssTeamMembersCount > 0}">	
							<tr class="options">
								<td class="ss_light ss_fineprint selectall"><input type="checkbox" id="team_member_all_ids" /><br/><label for="team_member_all_ids"><ssf:nlt tag="button.selectAll"/></label></td>
								<td colspan="5"></td>
							</tr>
						</c:if>
						
						<c:if test="${ssTeamMembersCount == 0}">
							<tr><td><ssf:nlt tag="teamMembersList.empty"/></td></tr>
						</c:if>	
						
					</table>
							
					</div>

			  </div>
			</div>
		</div>
	</div>
	<div class="ss_decor-round-corners-bottom1"><div><div></div></div></div>

	<script type="text/javascript">
		ss_synchronizeCheckboxes("team_member_all_ids", "team_member_ids");
	</script>

<% // Footer toolbar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />

</div>
</div>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

