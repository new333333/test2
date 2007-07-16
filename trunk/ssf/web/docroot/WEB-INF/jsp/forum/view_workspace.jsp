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
<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

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
	<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
	<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

	<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer">

		<% // Navigation bar %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

		<% // Tabs %>
		<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />

		<div class="ss_clear"></div>

		<div class="ss_tab_canvas">
			<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
							<div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">				
								<% // Workspace toolbar %>
								<div class="ss_content_inner">
									<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar1 ss_actions_bar" />
								</div>
	
								<div class="ss_content_inner">
								  	<c:choose>
								  		<c:when test="${ss_showTeamMembers}">
											<% // Navigation links %>
											<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
											
											<%@ include file="/WEB-INF/jsp/forum/list_team_members.jsp" %>
											
											<c:if test="${!empty ss_reloadUrl}">
												<div style="text-align: right; "><a href="<c:out value="${ss_reloadUrl}" />"><ssf:nlt tag="__return_to" /> <ssf:nlt tag="__workspace_view" /></a></div>
											</c:if>
											
										</c:when>
										<c:otherwise>
											<% // Navigation links %>
											<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
											
											<% // Show the workspace according to its definition %>
											<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
											  processThisItem="true"
											  configElement="${ssConfigElement}" 
											  configJspStyle="${ssConfigJspStyle}"
											  entry="${ssBinder}" />
										</c:otherwise>
									</c:choose>
								</div>
	
							</div>
						</div>

			<% // Footer toolbar %>
			<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
			
			<c:if test="${ss_userWorkspace}">
				<div align="right" width="100%" style="margin:10px;">
				  <ssHelpSpot helpId="workspaces_folders/misc_tools/accessible_mode" offsetX="-17" 
                  offsetY="-12" title="<ssf:nlt tag="helpSpot.accessibleMode" text="My Workspace"/>">
				  </ssHelpSpot>
				<c:if test="${ss_displayStyle == 'accessible'}">
				  <a href="${ss_accessibleUrl}">
				    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.disableAccessibleMode"/></span>
				  </a>
				</c:if>
				<c:if test="${ss_displayStyle != 'accessible'}">
				  <a href="${ss_accessibleUrl}">
				    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.enableAccessibleMode"/></span>
				  </a>
				</c:if>
				</div>
			</c:if>


		</div>
	</div>
</c:if>

<ssf:ifadapter>
	</body>
</html>
</ssf:ifadapter>
