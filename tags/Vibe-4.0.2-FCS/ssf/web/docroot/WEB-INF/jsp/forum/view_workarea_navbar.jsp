<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
<c:set var="accessibility_simple_ui" value='<%= org.kablink.teaming.util.SPropsUtil.getBoolean("accessibility.simple_ui", false) %>'/>
<c:set var="ss_urlWindowState" value="maximized"/>
<c:set var="ss_urlWindowState" value=""/>

<% if (GwtUIHelper.isGwtUIActive(request)) { %>
	<script type="text/javascript">
		/*
		 * onload event handler.
		 *
		 * Calls into the GWT code to notify it that a new context has
		 * been loaded into the content frame.
		 */
		function notifyGwtUI_ContextLoaded() {
			var inSearch = ("function" == typeof ss_initSearchOptions);
			var searchTabId = "";
			if (inSearch && ("function" == typeof ss_getSearchTabId)) {
				searchTabId = ss_getSearchTabId();
			}
			if ((typeof window.top.ss_contextLoaded != "undefined") &&
					((window.name == "gwtContentIframe") || (window.name == "ss_showentryframe"))) {
				window.top.ss_contextLoaded("${ssBinder.id}", String(inSearch), searchTabId);
			}
		}

		/*
		 * onload event handler.
		 *
		 * This function will call into the gwt code and show/hide the
		 * masthead and workspace tree control appropriately.
		 */
		function handleLandingPageOptions()
		{
			<c:if test="${ !empty ss_mashupHideMasthead && !empty ss_mashupHideSidebar && !empty ss_mashupShowBranding && !empty ss_mashupHideMenu}">
				var hideMasthead;
				var hideSidebar;
				var showBranding;
				var hideMenu;

				hideMasthead = ${ss_mashupHideMasthead};
				hideSidebar = ${ss_mashupHideSidebar};
				showBranding = ${ss_mashupShowBranding};
				hideMenu = ${ss_mashupHideMenu};
				if ( window.parent.ss_handleLandingPageOptions ) {
					if (window.name == "gwtContentIframe") {
						window.parent.ss_handleLandingPageOptions( "${ssBinder.id}", hideMasthead, hideSidebar, showBranding, hideMenu );
					}
				}
			</c:if>
		}// end showLandingPageOptions()

		ss_createOnLoadObj( "notifyGwtUI_ContextLoaded",            notifyGwtUI_ContextLoaded);
		ss_createOnLoadObj( "notifyGwtUI_handleLandingPageOptions", handleLandingPageOptions );
	</script>
<% } %>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<!-- <script type="text/javascript" src="/ssf/js/tree/tree_widget.js"></script> -->
<script type="text/javascript">
var ss_viewType = "${ss_viewType}";
var	ss_savedSearchTitle = "<ssf:nlt tag="searchResult.savedSearchTitle"/>";
var ssMyFavorites${renderResponse.namespace} = new ssFavorites('${renderResponse.namespace}');
var ssMyTeams${renderResponse.namespace} = new ssTeams('${renderResponse.namespace}');
var ss_displayType = "${ss_displayType}";
</script>
<noscript>
<h1>
  <span class="ss_errorLabel"><ssf:nlt tag="error.noscript"/></span>
</h1>
</noscript>
<c:if test="${!empty ss_watermark}">
<div id="ss_mastheadWatermark${renderResponse.namespace}" style="position:absolute;white-space:nowrap;">
${ss_watermark}
</div>
<script type="text/javascript">
ss_setOpacity(document.getElementById("ss_mastheadWatermark${renderResponse.namespace}"), 0.5);
ss_setObjectTop(document.getElementById("ss_mastheadWatermark${renderResponse.namespace}"), "0px");
ss_setObjectLeft(document.getElementById("ss_mastheadWatermark${renderResponse.namespace}"), parseInt((parseInt(ss_getWindowWidth()) - parseInt(ss_getObjectWidth(document.getElementById("ss_mastheadWatermark${renderResponse.namespace}"))))/2));
</script>
</c:if>

<!-- Start of global toolbar -->
<script type="text/javascript">
var m_isLandingPage = false;

<c:if test="${ !empty ss_mashupHideMasthead && !empty ss_mashupHideSidebar && !empty ss_mashupShowBranding }">
	m_isLandingPage = true;
</c:if>

if (self != self.parent) {
	//Check if this page is a full Teaming page inside a frame inside Teaming
	try {
		if ( window.name == 'gwtContentIframe' || window.name == 'ss_showentryframe')
		{
			//alert( 'window being loaded window.name: ' + window.name );
			//Check all the way up to the top to see if there are more than one frame with these names
			var windowObj = self;
			var counter = 20;
			while (counter > 0 && windowObj != null && windowObj != windowObj.parent) {
				if (windowObj.parent.window.name == 'gwtContentIframe' || windowObj.parent.window.name == 'ss_showentryframe') {
					//alert( 'windowObj.parent.window.name: ' + windowObj.parent.window.name );
					if (typeof self.parent.ss_urlBase != "undefined") {
						if ( typeof window.top.ss_gotoContentUrl != "undefined" )
						{
							// The gwtContentIFrame or the ss_showentryframe is being loaded inside a
							// gwtContentIFrame or a ss_showentryframe.  We don't want that to happen.
							// Take the url that is being loaded and load it into the top-level
							// gwtContentIFrame.
							window.top.ss_gotoContentUrl( self.location.href );
							//~JW:  .parent.location.href = self.location.href;
							break;
						}
					}
				}
				counter = counter - 1;
				windowObj = self.parent;
			}
			// Nothing to do.  We are running inside the GWT frame and that is ok.
		}
		else if (typeof window.name == "undefined" || window.name.indexOf("ss_iframeAccessory") != 0) {
			if (typeof self.parent.ss_urlBase != "undefined") {
				self.parent.location.href = self.location.href;
			}
		}
	} catch(e) {}
}
function ss_workarea_showId${renderResponse.namespace}(id, action, entryId) {
	if (typeof entryId == "undefined") entryId = "";
	//Build a url to go to
	var url = "<ssf:url 
	             action="ssActionPlaceHolder"
			     binderId="ssBinderIdPlaceHolder"
			     entryId="ssEntryIdPlaceHolder" >
	    	   <ssf:param name="namespace" value="${renderResponse.namespace}"/>
			   </ssf:url>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	if (typeof window.top.ss_gotoContentUrl != "undefined") {
		window.top.ss_gotoContentUrl(url);
	}
	else {
		setTimeout("self.location.href = '"+url+"';", 100);
	}
	return false;
}
if (typeof ss_workarea_showId == "undefined") 
	ss_workarea_showId = ss_workarea_showId${renderResponse.namespace};

</script>

<c:if test="${empty ssUser.currentDisplayStyle || ssUser.currentDisplayStyle == 'iframe' || 
  ssUser.currentDisplayStyle == 'vertical' || 
  (!empty ssFolderActionVerticalOverride && ssFolderActionVerticalOverride == 'yes') || 
  !accessibility_simple_ui}" >
<!-- iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- end of iframe div -->
</c:if>

<!-- The following form is used by the simple search available -->
<!-- from the GWT menu bar.                                    -->
<!--                                                           -->
<!-- See GwtClientHelper.jsInvokeSimpleSearch().               -->
<form
		action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param name="newTab" value="1"/><ssf:param name="quickSearch" value="true"/><ssf:param name="operation" value="ss_searchResults"/></ssf:url>"
		method="post"
		id="gwtSimpleSearchForm"
		name="gwtSimpleSearchForm">
	<input type="hidden" id="gwtSimpleSearchText" name="searchText" value="" />
</form>

<div id="ss_aboutBoxDiv" style="position:absolute; display:none;">
	<c:if test="<%= org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>">
		<img src="<html:rootPath/>images/pics/masthead/teaming_about_screen.png" border="0"/>
 	</c:if>
	<c:if test="<%= !org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>">
		<img src="<html:rootPath/>images/pics/masthead/kablink_about_screen.png" border="0"/>
	</c:if>
</div>
