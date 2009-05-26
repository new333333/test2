<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<% // The main forum view - for viewing folder listings and for viewing entries %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
<%
String op = (String) renderRequest.getAttribute(WebKeys.ACTION);
String displayStyle = ssUser.getDisplayStyle();
if (displayStyle == null || displayStyle.equals("")) {
	displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
}

String ssLoadEntryUrl = (String) renderRequest.getAttribute("ssLoadEntryUrl");
if (ssLoadEntryUrl == null) ssLoadEntryUrl = "";
String ssLoadEntryId = (String) renderRequest.getAttribute("ssLoadEntryId");
if (ssLoadEntryId == null) ssLoadEntryId = "";
//this jsp should is not included when reloadurl is set.  This is left here
//until we get after a reply to work - it may be needed then
String ssReloadUrl = (String) renderRequest.getAttribute("ssReloadUrl");
if (ssReloadUrl == null) ssReloadUrl = "";
boolean reloadCaller = false;
if (!ssReloadUrl.equals("")) reloadCaller = true;

boolean isViewEntry = false;
if (op != null && !op.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING) && !op.equals(WebKeys.ACTION_VIEW_PROFILE_LISTING)) {
	isViewEntry = true;
}
	
int entryWindowWidth = 0;
if (ssUserProperties.containsKey("folderEntryWidth")) {
	entryWindowWidth = Integer.parseInt((String) ssUserProperties.get("folderEntryWidth"));
}
int entryWindowTop = 0;
if (ssUserProperties.containsKey("folderEntryTop")) {
	entryWindowTop = Integer.parseInt((String) ssUserProperties.get("folderEntryTop"));
}
int entryWindowLeft = 0;
if (ssUserProperties.containsKey("folderEntryLeft")) {
	entryWindowLeft = Integer.parseInt((String) ssUserProperties.get("folderEntryLeft"));
}
int entryWindowHeight = 0;
if (ssUserProperties.containsKey("folderEntryHeight")) {
	entryWindowHeight = Integer.parseInt((String) ssUserProperties.get("folderEntryHeight"));
}
String autoScroll = "true";
renderRequest.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
renderRequest.setAttribute("ss_entryWindowTop", new Integer(entryWindowTop));
renderRequest.setAttribute("ss_entryWindowLeft", new Integer(entryWindowLeft));
renderRequest.setAttribute("ss_entryWindowHeight", new Integer(entryWindowHeight));
%>
<c:if test="<%= !isViewEntry %>">

<script type="text/javascript">
var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";

</script>

<script type="text/javascript">

//Define the url of this page in case the entry needs to reload this page
var ss_reloadUrl = "${ss_reloadUrl}";
var ss_reloadUrl${ssBinder.id} = ss_reloadUrl;
var ssLoadEntryUrl = "<%= ssLoadEntryUrl %>";
var autoScroll = "<%= autoScroll %>";
<%
	if (!ssLoadEntryUrl.equals("")) {
%>
function ss_showEntryOnLoad() {
	ss_loadEntryUrl("<%= ssLoadEntryUrl %>", "<%= ssLoadEntryId %>");
}
ss_createOnLoadObj('ss_showEntryOnLoad', ss_showEntryOnLoad);
<%
	}
%>


var ss_highlightBgColor = "${ss_folder_line_highlight_color}"
var ss_highlightedLine = null;
var ss_highlightedColLine = null;
var ss_savedHighlightedLineBgColor = null;
var ss_highlightClassName = "ss_highlightEntry";
var ss_savedHighlightClassName = null;
var ss_highlightColClassName = "ss_highlightEntry";
var ss_savedHighlightColClassName = null;

//Called when one of the "Add entry" toolbar menu options is selected
function ss_addEntry(obj) {
	ss_showForumEntry(obj.href);
	return false;
}

var ss_currentEntryId = "";



function ss_loadEntryUrl(url,id) {
	if (ss_userDisplayStyle == "accessible") {
		self.location.href = url;
		return false;
	}
	if (id == "") return false;
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	if (window.ss_highlightLineById) {
		ss_highlightLineById(folderLine);
	}
	
	ss_showForumEntry(url);
	return false;
}
</script>
</c:if>

<c:if test="<%= reloadCaller %>">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>
</c:if>


<% // View the entry  %>

<c:if test="<%= !reloadCaller %>">
  <c:if test="<%= isViewEntry %>">
	<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
	<div id="ss_entryTop_${renderResponse.namespace}"></div>
	<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view_init.jsp" />
	<script type="text/javascript">
	
	try {
		if (self.parent && self.parent.ss_highlightLineById) {
			self.parent.ss_highlightLineById("folderLine_<c:out value="${ssEntry.id}"/>");
		}
	} catch(e) {}
	
	//Define the url of this page in case the entry needs to reload this page
	var ss_reloadUrl = "${ss_reloadUrl}";
	var ss_reloadUrl${ssBinder.id} = ss_reloadUrl;
	
	ss_createOnLayoutChangeObj("ss_setCurrentIframeHeight", ss_setCurrentIframeHeight);
	</script>
	<c:set var="ss_viewEntryNavbar" value="false"/>
	<ssf:ifnotadapter><c:set var="ss_viewEntryNavbar" value="true"/></ssf:ifnotadapter>
	<c:if test="${ss_entryViewStyle == 'full'}"><c:set var="ss_viewEntryNavbar" value="true"/></c:if>
	<ssf:ifaccessible><c:set var="ss_viewEntryNavbar" value="true"/></ssf:ifaccessible>
	<c:set var="ss_entryViewStyle2" value="${ss_folderViewStyle}" scope="request"/>
	
	<c:if test="${ss_viewEntryNavbar}">
		<c:set var="ss_sidebarVisibility" value="${ssUserProperties.sidebarVisibility}" scope="request"/>
		<c:if test="${empty ss_sidebarVisibility}"><c:set var="ss_sidebarVisibility" value="block" scope="request"/></c:if>
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
		<div id="ss_portlet_content" class="ss_style ss_portlet">
		<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
		<div class="ss_actions_bar1_pane ss_sidebarImage">
		<table cellspacing="0" cellpadding="0">
		<tr>
		<ssf:ifnotaccessible>
		<td valign="middle">
		<a href="javascript: ;" 
		  onClick="ss_showHideSidebar('${renderResponse.namespace}');return false;"
		><span style="padding-left:12px; display:${ss_sidebarVisibilityShow};"
		  id="ss_sidebarHide${renderResponse.namespace}" 
		  class="ss_fineprint ss_sidebarSlidesm ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.show"/></span><span 
		  style="padding-left:12px; display:${ss_sidebarVisibilityHide};"
		  id="ss_sidebarShow${renderResponse.namespace}" 
		  class="ss_fineprint ss_sidebarSlide ss_sidebarSlidetext"><ssf:nlt tag="toolbar.sidebar.hide"/></span></a>
		</td>
		</ssf:ifnotaccessible>
		<td valign="middle">
		</td></tr>
		</table>
		</div>
		
		<% // BEGIN SIDEBAR LAYOUT  %>
		<ssf:ifnotaccessible>
		    <table cellpadding="0" cellspacing="0" border="0" width="100%">
		    <tbody>
		    <tr>
		    <c:if test="${!ss_mashupHideSidebar && (empty ss_captive || !ss_captive)}">
		    <td valign="top" class="${ss_sidebarTdStyle}" id="ss_sidebarTd${renderResponse.namespace}">
				<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
			</td>
			</c:if>
		
			<td valign="top" class="ss_view_info">
		</ssf:ifnotaccessible>
	
		<div class="ss_style_color">
	</c:if>
	<c:if test="${!ss_viewEntryNavbar}">
		<div class="ss_entryPopupWrapper">
	</c:if>
		
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${ssConfigElement}" 
	  configJspStyle="${ssConfigJspStyle}"
	  processThisItem="true" 
	  entry="${ssEntry}" />
	
	<c:if test="${ss_viewEntryNavbar}">
		</div>
	</c:if>
	<c:if test="${!ss_viewEntryNavbar}">
		</div>
	</c:if>
	
	<% // Footer toolbar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
		
	<c:if test="${ss_viewEntryNavbar}">
		<ssf:ifnotaccessible>
			</td>
			</tr>
			</tbody>
			</table>
		</ssf:ifnotaccessible>
	
		<ssf:ifaccessible>
		  <c:if test="${!ss_mashupHideSidebar && (empty ss_captive || !ss_captive)}">
		  <div>
			<jsp:include page="/WEB-INF/jsp/sidebars/sidebar.jsp" />
		  </div>
		  </c:if>
		</ssf:ifaccessible>
	
	</div>
	</c:if>
	
<%
	//See if this is the Popup view
	if (ObjectKeys.USER_DISPLAY_STYLE_POPUP.equals(ssUser.getDisplayStyle())) {
%>
		<script type="text/javascript">
		var ss_viewEntryResizeHappened = 0;
		var ss_viewEntryResizeTimeout = null;
		function ss_viewEntryResize() {
			ss_viewEntryResizeHappened = 1
			if (ss_viewEntryResizeTimeout != null) {
				clearTimeout(ss_viewEntryResizeTimeout)
				ss_viewEntryResizeTimeout = null;
			}
			ss_viewEntryResizeTimeout = setTimeout('ss_viewEntrySaveSize()', 250);
		}
		function ss_viewEntrySaveSize() {
			ss_setupStatusMessageDiv()
			clearTimeout(ss_viewEntryResizeTimeout);
			ss_viewEntryResizeTimeout = null;
			if (ss_viewEntryResizeHappened == 1) {
				//See if the user is finished resizing; wait for activity to stop
				ss_viewEntryResizeHappened = 0;
				ss_viewEntryResizeTimeout = setTimeout('ss_viewEntrySaveSize()', 250);
			} else {
				//Resizing must have finished, save the new size
				if (self.opener) {
					//Write the current size back onto the opener page for future use
					self.opener.ss_viewEntryPopupHeight = ss_getWindowHeight()
					self.opener.ss_viewEntryPopupWidth = ss_getWindowWidth()
				}
				var urlParams = {operation:"save_entry_width", entry_height:ss_getWindowHeight(), entry_width:ss_getWindowWidth()};
				ss_get_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams));
			}
		}
		function ss_viewEntryUnload() {
		}
		function ss_positionEntryOnLoad() {
		}
		ss_createOnLoadObj('ss_positionEntryOnLoad', ss_positionEntryOnLoad);
		ss_createOnResizeObj('ss_viewEntryResize', ss_viewEntryResize);
		ss_createEventObj('ss_viewEntryUnload', 'unload');
		</script>
<%
	}
%>
  </c:if>
</c:if>
