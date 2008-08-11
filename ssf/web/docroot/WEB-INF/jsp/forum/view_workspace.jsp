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
<ssf:ifnotadapter>
  <c:set var="showWorkspacePage" value="false"/>
</ssf:ifnotadapter>

<ssf:ifadapter>
<body class="ss_style_body">
<div id="ss_pseudoPortalDiv${renderResponse.namespace}">
</ssf:ifadapter>
<ssf:ifLoggedIn><c:if test="${empty ss_noEnableAccessibleLink && !empty ss_accessibleUrl && (empty ss_displayStyle || ss_displayStyle != 'accessible')}">
  <a class="ss_skiplink" href="${ss_accessibleUrl}"><img border="0"
    <ssf:alt tag="accessible.enableAccessibleMode"/> 
    src="<html:imagesPath/>pics/1pix.gif" /></a><%--
		--%></c:if></ssf:ifLoggedIn>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />

<c:if test="${!empty ssReloadUrl}">
	<script type="text/javascript">
		//Open the current url in the opener window
		ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
	</script>
</c:if>

<c:if test="${empty ssReloadUrl}">
<script type="text/javascript">
if (self.parent) {
	//We are in an iframe inside a portlet (maybe?)
	var windowName = self.window.name    
	if (windowName.indexOf("ss_workareaIframe") == 0) {
		//We are running inside an iframe, get the namespace name of that iframe's owning portlet
		var namespace = windowName.substr("ss_workareaIframe".length)
		//alert('namespace = '+namespace+', binderId = ${ssBinder.id}, entityType = ${ssBinder.entityType}')
		var url = "<ssf:url
					adapter="true"
					portletName="ss_forum" 
					action="__ajax_request" 
					binderId="${ssBinder.id}">
				  <ssf:param name="entityType" value="${ssBinder.entityType}"/>
				  <ssf:param name="namespace" value="ss_namespacePlaceholder" />
				  <ssf:param name="operation" value="set_last_viewed_binder" />
				  <ssf:param name="rn" value="ss_randomNumberPlaceholder"/>
				  </ssf:url>"
		url = ss_replaceSubStr(url, 'ss_namespacePlaceholder', namespace);
		url = ss_replaceSubStr(url, 'ss_randomNumberPlaceholder', ss_random++);
		ss_fetch_url(url);
	}
}

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
	    	   <ssf:param name="namespace" value="${renderResponse.namespace}"/>
	           </ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
<ssf:ifnotadapter>
	var iframeDivObj = document.getElementById('ss_workareaIframe${renderResponse.namespace}')
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
	var iframeDiv = document.getElementById('ss_workareaIframe${renderResponse.namespace}')
	if (window.frames['ss_workareaIframe${renderResponse.namespace}'] != null) {
		eval("var iframeHeight = parseInt(window.ss_workareaIframe${renderResponse.namespace}" + ".document.body.scrollHeight);")
		if (iframeHeight > 100) {
			iframeDiv.style.height = iframeHeight + ss_workareaIframeOffset + "px"
		}
	}
}
ss_createOnResizeObj('ss_setWorkareaIframeSize${renderResponse.namespace}', ss_setWorkareaIframeSize${renderResponse.namespace});
ss_createOnLayoutChangeObj('ss_setWorkareaIframeSize${renderResponse.namespace}', ss_setWorkareaIframeSize${renderResponse.namespace});

//If this is the first definition of ss_setWorkareaIframeSize, remember its name in case we need to find it later
if (typeof ss_setWorkareaIframeSize == "undefined") 
	var ss_setWorkareaIframeSize = ss_setWorkareaIframeSize${renderResponse.namespace};

var ss_portal_view_normal_url${renderResponse.namespace} = "<ssf:url windowState="normal"/>";
var ss_portal_view_maximized_url${renderResponse.namespace} = "<ssf:url windowState="maximized"/>";
var ss_portal_view_window_state${renderResponse.namespace} = "${ss_windowState}"
</script>
<iframe id="ss_workareaIframe${renderResponse.namespace}" 
    name="ss_workareaIframe${renderResponse.namespace}" 
    style="width:100%; height:400px; display:block; position:relative;"
	src="<ssf:url     
    		adapter="true" 
    		portletName="ss_workarea" 
    		binderId="${ssBinder.id}" 
    		action="view_ws_listing" 
    		actionUrl="false" >
        <ssf:param name="namespace" value="${renderResponse.namespace}"/>
        </ssf:url>" 
	onLoad="ss_setWorkareaIframeSize${renderResponse.namespace}();" 
	frameBorder="0" >xxx</iframe>

<!-- portlet iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- portlet iframe div -->	

</ssf:ifnotadapter>

<c:if test="${showWorkspacePage}">
	<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
	<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

	<script type="text/javascript">
		var ss_reloadUrl = "${ss_reloadUrl}";
		var ss_reloadUrl${ssBinder.id} = ss_reloadUrl;
	</script>

	<div id="ss_showfolder${renderResponse.namespace}" class="ss_style ss_portlet ss_content_outer">
<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
<c:set var="ss_sidebarVisibility" value="${ssUserProperties.sidebarVisibility}"/>
<c:if test="${empty ss_sidebarVisibility}"><c:set var="ss_sidebarVisibility" value="block"/></c:if>
<c:if test="${ss_sidebarVisibility == 'none'}">
  <c:set var="ss_sidebarVisibilityShow" value="block"/>
  <c:set var="ss_sidebarVisibilityHide" value="none"/>
  <c:set var="ss_sidebarTdStyle" value=""/>
</c:if>
<c:if test="${ss_sidebarVisibility != 'none'}">
  <c:set var="ss_sidebarVisibilityShow" value="none"/>
  <c:set var="ss_sidebarVisibilityHide" value="block"/>
  <c:set var="ss_sidebarTdStyle" value="ss_view_sidebar"/>
</c:if>
<div class="ss_actions_bar1_pane ss_sidebarImage" width="100%">
<table cellspacing="0" cellpadding="0" width="100%">
<tr>
<ssf:ifnotaccessible>
<td valign="middle">
<a href="javascript: ;" 
  onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
><span style="padding-left:9px; display:${ss_sidebarVisibilityShow};"
  id="ss_sidebarHide${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlidesm"><ssf:nlt tag="toolbar.sidebar.show"/></span><span 
  style="padding-left:9px; display:${ss_sidebarVisibilityHide};"
  id="ss_sidebarShow${renderResponse.namespace}" 
  class="ss_fineprint ss_sidebarSlide"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
</td>
</ssf:ifnotaccessible>

<td valign="top">
<jsp:include page="/WEB-INF/jsp/definition_elements/folder_toolbar.jsp" />
</td></tr>
</table>
</div>
<ssf:ifnotaccessible>
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
    <tr>
    <td valign="top" class="${ss_sidebarTdStyle}" id="ss_sidebarTd${renderResponse.namespace}">
		<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
	</td>

	<td valign="top" class="ss_view_info">
</ssf:ifnotaccessible>
		<div class="ss_tab_canvas">
			<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
				<div class="ss_style_color">				
					<div class="ss_content_inner">
					  	<c:choose>
					  		<c:when test="${ss_showTeamMembers}">
								<% // Navigation links %>
								<%@ include file="/WEB-INF/jsp/forum/list_team_members.jsp" %>
								
								<c:if test="${!empty ss_reloadUrl}">
									<div style="text-align: right; "><a href="<c:out value="${ss_reloadUrl}" />"><ssf:nlt tag="__return_to" /> <ssf:nlt tag="__workspace_view" /></a></div>
								</c:if>
								
							</c:when>
							<c:otherwise>
								<% // Navigation links %>
<a class="ss_linkButton" href="<ssf:url 
		action="view_ws_listing" binderId="${ssBinder.id}"><ssf:param
		name="type" value="whatsNew"/><ssf:param
		name="page" value="0"/><ssf:param
		name="namespace" value="${ss_namespace}"/></ssf:url>"
	onClick="ss_showWhatsNewPage(this, '${ssBinder.id}', 'whatsNew', '0', '', 'ss_whatsNewDiv', '${ss_namespace}');return false;"
><ssf:nlt tag="workspace.whatsNew"/></a>
<a class="ss_linkButton" href="<ssf:url 
		action="view_ws_listing" binderId="${ssBinder.id}"><ssf:param
		name="type" value="unseen"/><ssf:param
		name="page" value="0"/><ssf:param
		name="namespace" value="${ss_namespace}"/></ssf:url>"
	onClick="ss_showWhatsNewPage(this, '${ssBinder.id}', 'unseen', '0', '', 'ss_whatsNewDiv', '${ss_namespace}');return false;"
><ssf:nlt tag="workspace.listUnseen"/></a>



<div id="ss_whatsNewDiv${ss_namespace}">
<c:if test="${!empty ss_whatsNewBinder || ss_pageNumber > '0'}">
<%@ include file="/WEB-INF/jsp/forum/whats_new_page.jsp" %>
</c:if>
</div>

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
				<ssf:ifLoggedIn>
				 <ssf:ifaccessible>
				  <a href="${ss_accessibleUrl}">
				    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.disableAccessibleMode"/></span>
				  </a>
				 </ssf:ifaccessible>
				 <ssf:ifnotaccessible>
				  <a href="${ss_accessibleUrl}"
				  onClick='if (!confirm("<ssf:nlt tag="accessible.confirm"/>"))return false;'>
				    <span class="ss_smallprint ss_light"><ssf:nlt tag="accessible.enableAccessibleMode"/></span>
				  </a>
				 </ssf:ifnotaccessible>
				</ssf:ifLoggedIn>
				</div>
			</c:if>

		</div>
<ssf:ifnotaccessible>
	</td>
	</tr>
	</tbody>
	</table>
</ssf:ifnotaccessible>
  </div>
<script type="text/javascript">
ss_createOnLoadObj('ss_initShowFolderDiv${renderResponse.namespace}', ss_initShowFolderDiv('${renderResponse.namespace}'));
</script>
</c:if>
	
</c:if>

<ssf:ifadapter>
</div>
	</body>
</html>
</ssf:ifadapter>
