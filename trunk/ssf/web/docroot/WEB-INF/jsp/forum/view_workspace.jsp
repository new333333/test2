<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="showWorkspacePage" value="true"/>
<c:if test="${ss_displayType == 'ss_workarea'}">
  <ssf:ifnotadapter>
    <c:set var="showWorkspacePage" value="false"/>
  </ssf:ifnotadapter>
</c:if>

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
<c:if test="${ss_displayType == 'ss_workarea'}">
<script type="text/javascript">
function ss_workarea_showId${renderResponse.namespace}(id, action, entryId) {
	if (typeof entryId == "undefined") entryId = "";
	//Build a url to go to
	var url = "<ssf:url     
	    		  adapter="true" 
	    		  portletName="ss_workarea" 
	    		  binderId="ssBinderIdPlaceHolder" 
    			  entryId="ssEntryIdPlaceHolder" 
	    		  action="ssActionPlaceHolder" 
	    		  actionUrl="false" >
	           </ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
<ssf:ifnotadapter>
	var iframeDivObj = document.getElementById('ss_viewListingIframe${renderResponse.namespace}')
	if (iframeDivObj != null) {
		iframeDivObj.src = url;
	} else {
		return true;
	}
</ssf:ifnotadapter>
<ssf:ifadapter>
	self.location.href = url;
</ssf:ifadapter>
	return false;
}
if (typeof ss_workarea_showId == "undefined") 
	ss_workarea_showId = ss_workarea_showId${renderResponse.namespace};
</script>
<ssf:ifnotadapter>
<script type="text/javascript">
var ss_workareaIframeOffset = 50;
function ss_setWorkareaIframeSize${renderResponse.namespace}() {
	var iframeDiv = document.getElementById('ss_viewListingIframe${renderResponse.namespace}')
	if (window.frames['ss_viewListingIframe${renderResponse.namespace}'] != null) {
		eval("var iframeHeight = parseInt(window.ss_viewListingIframe${renderResponse.namespace}" + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_workareaIframeOffset + "px"
		}
	}
}
ss_createOnResizeObj('ss_setWorkareaIframeSize${renderResponse.namespace}', ss_setWorkareaIframeSize${renderResponse.namespace});
ss_createOnLayoutChangeObj('ss_setWorkareaIframeSize${renderResponse.namespace}', ss_setWorkareaIframeSize${renderResponse.namespace});
</script>
<iframe id="ss_viewListingIframe${renderResponse.namespace}" 
    name="ss_viewListingIframe${renderResponse.namespace}" 
    style="width:100%; height:400px; display:block; position:relative;"
	src="<ssf:url     
    		adapter="true" 
    		portletName="ss_workarea" 
    		binderId="${ssBinder.id}" 
    		action="view_ws_listing" 
    		actionUrl="false" >
        </ssf:url>" 
	onLoad="ss_setWorkareaIframeSize${renderResponse.namespace}();" 
	frameBorder="0" >xxx</iframe>

<!-- portlet iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- portlet iframe div -->	

</ssf:ifnotadapter>
</c:if>

<c:if test="${showWorkspacePage}">
	<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
	<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

	<script type="text/javascript">
		var ss_reloadUrl${ssBinder.id} = "${ss_reloadUrl}";
		var ss_reloadUrl = ss_reloadUrl${ssBinder.id};
	</script>

	<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">
<c:if test="${ss_displayType == 'ss_workarea'}">
	<%@ include file="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" %>
</c:if>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
<c:if test="${ss_displayType != 'ss_workarea'}">
    <td valign="top" class="ss_view_sidebar">

	<% // Navigation bar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

	<% // Tabs %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />

	<% // Folder Sidebar %>

    <%@ include file="/WEB-INF/jsp/sidebars/sidebar_dispatch.jsp" %>

    <ssf:sidebarPanel title="__definition_default_workspace" id="ss_workspace_sidebar"
        initOpen="true" sticky="true">
		<c:if test="${!empty ssSidebarWsTree}">
		<ssf:tree treeName="sidebarWsTree${renderResponse.namespace}" 
		  treeDocument="${ssSidebarWsTree}" 
		  highlightNode="${ssBinder.id}" 
		  showIdRoutine="ss_treeShowIdNoWS"
		  namespace="${renderResponse.namespace}"
		  rootOpen="true"
		  nowrap="true"/>
		</c:if>
	</ssf:sidebarPanel>

	</td>
</c:if>
	<td valign="top" class="ss_view_info">
		<div class="ss_tab_canvas">
			<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
				<div class="ss_style_color">				
					<% // Workspace toolbar %>
					<%@ include file="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" %>

					<div class="ss_content_inner">
					  	<c:choose>
					  		<c:when test="${ss_showTeamMembers}">
								<% // Navigation links %>
<c:if test="${ss_displayType != 'ss_workarea'}">
								<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
</c:if>								
								<%@ include file="/WEB-INF/jsp/forum/list_team_members.jsp" %>
								
								<c:if test="${!empty ss_reloadUrl}">
									<div style="text-align: right; "><a href="<c:out value="${ss_reloadUrl}" />"><ssf:nlt tag="__return_to" /> <ssf:nlt tag="__workspace_view" /></a></div>
								</c:if>
								
							</c:when>
							<c:otherwise>
								<% // Navigation links %>
<c:if test="${ss_displayType != 'ss_workarea'}">
								<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
</c:if>								
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

			<% // Footer toolbar %>
			<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
			
			<c:if test="${ss_userWorkspace}">
				<div width="100%" style="margin:10px;">
				  <ssHelpSpot helpId="workspaces_folders/misc_tools/accessible_mode" offsetX="-17" 
                  offsetY="-12" title="<ssf:nlt tag="helpSpot.accessibleMode" text="My Workspace"/>">
				  </ssHelpSpot>
				<ssf:ifaccessible>
				  <a href="${ss_accessibleUrl}">
				    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.disableAccessibleMode"/></span>
				  </a>
				</ssf:ifaccessible>
				<ssf:ifnotaccessible>
				  <a href="${ss_accessibleUrl}">
				    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.enableAccessibleMode"/></span>
				  </a>
				</ssf:ifnotaccessible>
				</div>
			</c:if>

		</div>
	</td>
	</tr>
	</tbody>
	</table>
  </div>
<script type="text/javascript">
ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));
</script>
</c:if>
	
</c:if>

<ssf:ifadapter>
	</body>
</html>
</ssf:ifadapter>
