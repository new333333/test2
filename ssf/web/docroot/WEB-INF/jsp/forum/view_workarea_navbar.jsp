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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
<c:set var="ss_urlWindowState" value="maximized"/>
<c:if test="${ss_displayType == 'ss_workarea' || ss_displayType == 'ss_forum'}">
  <c:set var="ss_urlWindowState" value=""/>
</c:if>
<!-- <script type="text/javascript" src="/ssf/js/tree/tree_widget.js"></script> -->
<script type="text/javascript">
var	ss_savedSearchTitle = "<ssf:nlt tag="searchResult.savedSearchTitle"/>";
var ssMyFavorites${renderResponse.namespace} = new ssFavorites('${renderResponse.namespace}');
var ssMyTeams${renderResponse.namespace} = new ssTeams('${renderResponse.namespace}');
var ss_displayType = "${ss_displayType}";
</script>
<c:if test="${ssUserProperties.debugMode}">
<!-- Start of debug window -->
  <div style="border:1px solid black;">
  <div style="background-color:#CECECE; border-bottom:1px solid black; width:100%;">
    <table cellspacing="0" cellpadding="0" style="background-color:#CECECE; width:100%;">
    <tbody>
    <tr>
    <td>Debug window</td>
    <td align="right">
      <a href="" onClick="ss_turnOffDebugMode();return false;">
        <img <ssf:alt tag="alt.hide"/> border="0" src="<html:imagesPath/>pics/sym_s_delete.gif">
      </a>
<script type="text/javascript">
function ss_turnOffDebugMode() {
	var url = self.location.href + "&enableDebug=off"
	self.location.href = url;
}
var ss_debugTextareaId = "debugTextarea${renderResponse.namespace}"

</script>
    </td>
    </tr>
    </tbody>
    </table>
  </div>
  <div>
  <textarea id="debugTextarea${renderResponse.namespace}" style="width:100%;" rows="6"></textarea>
  </div>
  </div>
  <br/>
<!-- End of debug window -->
</c:if>

<!-- Start of global toolbar -->
<script type="text/javascript">
function ss_workarea_showPsuedoPortal${renderResponse.namespace}(obj) {
	//See if we are in an iframe inside a portlet 
	var windowName = self.window.name    
	if (windowName.indexOf("ss_workareaIframe") == 0) {
		//We are running inside a portlet iframe; do nothing
	} else {
		//Show the psuedo portal
		var divObj = self.document.getElementById('ss_psuedoPortalDiv${renderResponse.namespace}');
		if (divObj != null) {
			divObj.className = "ss_psuedoPortal"
		}
	}
}

function ss_workarea_showPortal${renderResponse.namespace}(obj) {
	//See if we are in an iframe inside a portlet 
	var windowName = self.window.name    
	if (windowName.indexOf("ss_workareaIframe") == 0) {
		//We are running inside a portlet iframe
		if (obj.href != "") self.parent.location.href = obj.href;
	} else {
		self.location.href = obj.href;
	}
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
	self.location.href = url;
	return false;
}
if (typeof ss_workarea_showId == "undefined") 
	ss_workarea_showId = ss_workarea_showId${renderResponse.namespace};

function ss_goToMyParentPortletMaximizedView${renderResponse.namespace}(obj) {
	//See if we are in an iframe inside a portlet 
	var windowName = self.window.name    
	if (windowName.indexOf("ss_workareaIframe") == 0) {
		//We are running inside an iframe
		self.parent.location.href = obj.href;
	} else {
		self.location.href = obj.href;
	}
}
</script>

<div id="ss_top_nav_wrapper" style="width:100%;">
<table width="100%" align="center" cellspacing="0" cellpadding="0" border="0" >
<tbody>
<tr>
<td align="center">
<table border="0" cellpadding="0" cellspacing="0" >
<tbody>
  <tr>
    <td align="center" valign="top">
      <div id="ss_top_nav_buttontwo">
        <ul>
          <li>
			  <ssHelpSpot helpId="navigation_bar/my_portal_button" offsetY="-10" offsetX="-5" 
			      title="<ssf:nlt tag="helpSpot.myPortalButton" text="My Portal"/>">
			  </ssHelpSpot>
	          <a href="${ss_portalUrl}" 
	            onClick="ss_workarea_showPortal${renderResponse.namespace}(this);return false;"
	            title="<ssf:nlt tag="navigation.goToPortalView"/>"
	          ><ssf:nlt tag="navigation.portalView"/></a></li>
          <li>
          	  <a
	 			  <c:if test="${ssBinder.entityType == 'folder'}">
	 			    href="<ssf:url adapter="true" portletName="ss_forum" 
			    		action="view_folder_listing"
			    		binderId="${ssBinder.id}">
			    		<ssf:param name="newTab" value="1"/>
						</ssf:url>"
	 			  </c:if>
	 			  <c:if test="${ssBinder.entityType == 'workspace'}">
	 			    href="<ssf:url adapter="true" portletName="ss_forum" 
			    		action="view_ws_listing"
			    		binderId="${ssBinder.id}">
			    		<ssf:param name="newTab" value="1"/>
						</ssf:url>"
	 			  </c:if>
	 			  <c:if test="${ssBinder.entityType == 'profiles'}">
	 			    href="<ssf:url adapter="true" portletName="ss_forum" 
			    		action="view_profile_listing"
			    		binderId="${ssBinder.id}">
			    		<ssf:param name="newTab" value="1"/>
						</ssf:url>"
	 			  </c:if>
	            onClick="ss_goToMyParentPortletMaximizedView${renderResponse.namespace}(this);return false;"
	          title="<ssf:nlt tag="navigation.goToMaximizedView"/>"
              ><ssf:nlt tag="navigation.expandedView"/></a>
          </li>
          <li>
			  <ssHelpSpot helpId="navigation_bar/my_workspace_button" offsetY="-10" offsetX="-5" 
			      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
			  </ssHelpSpot>
              <a title="<ssf:nlt tag="navigation.myWorkspace"/>"
				  href="<ssf:url 
				    windowState="${ss_urlWindowState}"
			      	action="view_ws_listing"
			      	binderId="${ssUser.workspaceId}"/>"
              ><ssf:nlt tag="navigation.myWorkspace"/> </a>
          </li>
          <li><a title="<ssf:nlt tag="navigation.myTeams"/>"
			  href="javascript:;" 
			  onClick="ssMyFavorites${renderResponse.namespace}.hideFavoritesPane();ssMyTeams${renderResponse.namespace}.show();"
              ><ssf:nlt tag="navigation.myTeams"/> <img border="0" 
              src="<html:imagesPath/>pics/menudown.gif" style="padding-left: 2px;"/> </a>
		      <ssHelpSpot helpId="navigation_bar/my_teams" offsetX="3" offsetY="13"  
		          title="<ssf:nlt tag="helpSpot.myTeamsButton"/>">
			    <div id="ss_navbarMyTeamsButton${renderResponse.namespace}">
			      	
			    </div>
			  </ssHelpSpot>
			  <div id="ss_navbar_myteams${renderResponse.namespace}"
			      style="visibility:hidden;margin:20px 0px 0px -130px;padding:0px;"></div>

          </li>
          <li><a title="<ssf:nlt tag="navigation.favorites"/>"
	  			href="javascript: ;" 
	  			onClick="ssMyTeams${renderResponse.namespace}.hide();ssMyFavorites${renderResponse.namespace}.showFavoritesPane();"
              ><ssf:nlt tag="navigation.favorites"/> <img border="0" 
              src="<html:imagesPath/>pics/menudown.gif" style="padding-left: 2px;"/> </a>
		      <ssHelpSpot helpId="navigation_bar/favorites_button" offsetX="3" offsetY="13"  
		          title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
			  </ssHelpSpot>
		      <div align="right" id="ss_navbar_favorites${renderResponse.namespace}" 
		      style="visibility:hidden;margin:20px 0px 0px -150px;padding:0px;"
		      ></div>
          </li>
        </ul>
      </div><div class="ss_clear_float"></div>
    </td>
  </tr>
</tbody>
</table>
<div class="ss_clear_float"></div>

</td>
</tr>
<tr>
<td align="center" style="padding:5px 0px 2px 0px;">
<table class="ss_global_toolbar_maximized" cellspacing="0" cellpadding="0" border="0">
<tbody>
<tr>

<td>
<ssf:ifnotaccessible>
<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findUser"/></span>
</ssf:ifnotaccessible>
	<!-- Find People -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_findUser"  id="ss_navbarFindUserButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
			<form method="post" id="ss_findUserForm${renderResponse.namespace}" 
			  name="ss_findUserForm${renderResponse.namespace}" 
			  style="display:inline;"
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="user"
				    width="70px" singleItem="true"/> 
			</form>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible"  id="ss_navbarFindUserButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findUserForm${renderResponse.namespace}" 
			  name="ss_findUserForm${renderResponse.namespace}" 
			  style="display:inline;"
			  action="<ssf:url windowState="maximized" action="findUser" actionUrl="true"/>">
				  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="user"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findUser"/>
				  </ssf:param>
				  </ssf:find>
			</form>
		</div>
	</div>
</ssf:ifaccessible>
</td>
<td style="padding-left:0px;">
<ssf:ifnotaccessible>
<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findPlace"/></span>
</ssf:ifnotaccessible>
	<!-- Find Place -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_findUser"  id="ss_navbarFindPlacesButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
		<div>
			<form method="post" id="ss_findPlacesForm${renderResponse.namespace}" 
			  name="ss_findPlacesForm${renderResponse.namespace}" 
			  style="display:inline;"
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findPlacesForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="places"
				    width="70px" singleItem="true"/> 
			</form>
		</div>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible"  id="ss_navbarFindPlacesButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findPlacesForm${renderResponse.namespace}" 
			  name="ss_findPlacesForm${renderResponse.namespace}" 
			  style="display:inline;"
			  action="<ssf:url windowState="maximized" action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findPlacesForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="places"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findPlace"/>
				  </ssf:param>
				  </ssf:find>
			</form>
		</div>
	</div>
</ssf:ifaccessible>
</td>
<td style="padding-left:4px;">
<ssf:ifnotaccessible>
<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findTag"/></span>
</ssf:ifnotaccessible>
		<!-- Find Tag -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_findUser" id="ss_navbarFindTagsButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
		<div>
			<form method="post" id="ss_findTagsForm${renderResponse.namespace}" 
			  name="ss_findTagsForm${renderResponse.namespace}" 
			  style="display:inline;"
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findTagsForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="tags"
				    width="70px" singleItem="true"/> 
			</form>
		</div>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible" id="ss_navbarFindTagsButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findTagsForm${renderResponse.namespace}" 
			  name="ss_findTagsForm${renderResponse.namespace}" 
			  style="display:inline;"
			  action="<ssf:url windowState="maximized" action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findTagsForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="tags"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findTag"/>
				  </ssf:param>
				  </ssf:find>
			</form>
		</div>
	</div>
</ssf:ifaccessible>
</td>

<td style="padding-left:20px;">

	<!-- Search -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_quick">
		<div >
			<span class="ss_global_toolbar_label_text_quickSearch"><ssf:nlt tag="navigation.search"/></span>
			<span class="ss_global_toolbar_quick_advanced"><a class="ss_advanced ss_fineprint" 
			  href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
			  	name="binderId" value="${ssBinder.id}"/><ssf:param 
			  	name="tabTitle" value="SEARCH FORM"/><ssf:param 
			  	name="newTab" value="0"/></ssf:url>"
				><ssf:nlt tag="navigation.search.advanced"/></a></span>
		</div>
		<div class="ss_global_toolbar_search"  id="ss_navbarSearchButton${renderResponse.namespace}" 
		  onMouseOver="this.style.cursor = 'pointer';">
     		<form class="ss_form" method="post" id="ss_simpleSearchForm${renderResponse.namespace}" 
		  		name="ss_simpleSearchForm${renderResponse.namespace}" 
		  		style="display:inline;"
		  		action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
		  			name="newTab" value="1"/></ssf:url>">
			  <ssHelpSpot helpId="navigation_bar/search_button" offsetY="-12" 
                 <c:if test="<%= BrowserSniffer.is_ie(request) %>">
                   offsetX="159" 
                 </c:if>
                 <c:if test="<%= !BrowserSniffer.is_ie(request) %>">
                   offsetX="147" 
                 </c:if>
			    title="<ssf:nlt tag="helpSpot.searchButton"/>">
					<input name="searchText" style="width: 140px;" type="text" /> 
					<a class="ss_searchButton" 
					  href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" ><img 
					  <ssf:alt tag="alt.search"/> src="<html:imagesPath/>pics/1pix.gif" /></a>
					<input type="hidden" name="searchBtn" value="searchBtn"/>
					<input type="hidden" name="quickSearch" value="true"/>					
					<input type="hidden" name="operation" value="ss_searchResults"/>
			  </ssHelpSpot>
				<a class="ss_savedQueries" alt="<ssf:nlt tag="searchResult.savedSearchTitle"/>" 
				  title="<ssf:nlt tag="searchResult.savedSearchTitle"/>" href="javascript: // ;" 
				  onclick="ss_showSavedQueriesList(this, 'ss_navbarPopupPane${renderResponse.namespace}',
				  '<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
				  name="newTab" value="1"/></ssf:url>');"><img 
				  src="<html:imagesPath/>pics/menudown.gif" /></a>
				<div id="ss_navbarPopupPane${renderResponse.namespace}" class="ss_navbarPopupPane"></div>
			</form>
     	</div>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible">
		<div >
			<span class="ss_global_toolbar_label_text_quickSearch"><label for="ss_searchSearchText${renderResponse.namespace}"
			  ><ssf:nlt tag="navigation.search"/></label></span>
			<span class="ss_global_toolbar_quick_advanced"><a class="ss_advanced ss_fineprint" 
				  href="<ssf:url windowState="maximized" actionUrl="true"><ssf:param 
				  name="action" value="advanced_search"/><ssf:param 
				  name="tabTitle" value="SEARCH FORM"/><ssf:param 
				  name="newTab" value="0"/></ssf:url>"
				><ssf:nlt tag="navigation.search.advanced"/></a></span>
		</div>
		<div class="ss_global_toolbar_search"  id="ss_navbarSearchButton${renderResponse.namespace}" >
		  <ssHelpSpot helpId="navigation_bar/search_button" offsetY="-12" 
                 <c:if test="<%= BrowserSniffer.is_ie(request) %>">
                   offsetX="159" 
                 </c:if>
                 <c:if test="<%= !BrowserSniffer.is_ie(request) %>">
                   offsetX="147" 
                 </c:if>
		    title="<ssf:nlt tag="helpSpot.searchButton"/>">
		  </ssHelpSpot>
     		<form method="post" id="ss_simpleSearchForm${renderResponse.namespace}" 
		  		name="ss_simpleSearchForm${renderResponse.namespace}" 
		  		style="display:inline;"
		  		action="<ssf:url windowState="maximized" actionUrl="true"><ssf:param 
		  			name="action" value="advanced_search"/><ssf:param 
		  			name="newTab" value="1"/></ssf:url>">
					<input name="searchText" type="text" id="ss_searchSearchText${renderResponse.namespace}" /> 
					<a class="ss_searchButton" 
					  href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" ><img 
					  <ssf:alt tag="alt.search"/> src="<html:imagesPath/>pics/search_icon.gif" /></a>
					<input type="hidden" name="searchBtn" value="searchBtn"/>
					<input type="hidden" name="quickSearch" value="true"/>					
					<input type="hidden" name="operation" value="ss_searchResults"/>
			</form>
     	</div>
	</div>
</ssf:ifaccessible>
</td>
</tr>
</tbody>
</table>
</td>
</tr>

<!-- Start of Status line -->
<ssf:ifLoggedIn>
<tr>
<td align="center" style="padding:5px 0px 0px 0px;">
<table class="ss_global_toolbar_maximized" cellspacing="0" cellpadding="0" border="0">
<tbody>
<tr>
<td>
<script type="text/javascript">
ss_statusCurrent = "${ssUser.status}";
</script>
<span class="ss_smallprint"><ssf:nlt tag="relevance.userStatus"/></span>
<input type="text" size="40" style="font-size:8;" value="${ssUser.status}"
  onKeyPress="ss_updateStatusSoon(this);"
  onChange="ss_updateStatusNow(this);"
  />
</td>
<td style="padding-left:20px;">
<c:if test="${!empty ssBinder && ssBinder.entityType != 'profiles'}">
<a class="ss_linkButton ss_smallprint" href="javascript: ;" 
  onClick="ss_trackThisBinder('${ssBinder.id}', '${renderResponse.namespace}');return false;">
<c:if test="${ssBinder.entityType == 'workspace'}">
  <c:if test="${ssBinder.definitionType != 12}"><span class="ss_smallprint"><ssf:nlt tag="relevance.trackThisWorkspace"/></span></c:if>
  <c:if test="${ssBinder.definitionType == 12}"><span class="ss_smallprint"><ssf:nlt tag="relevance.trackThisPerson"/></span></c:if>
</c:if>
<c:if test="${ssBinder.entityType == 'folder'}">
  <c:if test="${ssDefinitionFamily != 'calendar'}"><span class="ss_smallprint"><ssf:nlt tag="relevance.trackThisFolder"/></span></c:if>
  <c:if test="${ssDefinitionFamily == 'calendar'}"><span class="ss_smallprint"><ssf:nlt tag="relevance.trackThisCalendar"/></span></c:if>
</c:if>
</a>
</c:if>
<div id="ss_track_this_anchor${renderResponse.namespace}"> </div>
<div id="ss_track_this_ok${renderResponse.namespace}" 
  style="position:absolute;display:none;visibility:hidden;border:1px solid black;padding:10px;background-color:#ffffff;"></div>
</td>
<td style="padding-left:20px;">
<c:if test="${!empty ssBinder && ssBinder.entityType != 'profiles'}">
<a class="ss_linkButton ss_smallprint" 
  href="<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_relevance" actionUrl="false"><ssf:param 
		name="operation" value="share_this_binder" /><ssf:param 
		name="binderId" value="${ssBinder.id}" /></ssf:url>" 
  onClick="ss_openUrlInWindow(this, '_blank', '450px', '600px');return false;">
<c:if test="${ssBinder.entityType == 'workspace'}"><span class="ss_smallprint"><ssf:nlt tag="relevance.shareThisWorkspace"/></span></c:if>
<c:if test="${ssBinder.entityType == 'folder'}"><span class="ss_smallprint"><ssf:nlt tag="relevance.shareThisFolder"/></span></c:if>
</a>
</c:if>
</td>
</tr>
</tbody>
</table>
</td>
</tr>
</ssf:ifLoggedIn>

<tr><td style="padding-bottom:4px;"></td></tr>
</tbody>
</table>
</div>

<c:if test="${empty ss_navbarBottomSeen}">
<c:set var="ss_navbarBottomSeen" value="1"/>

<div id="ss_navbar_bottom${renderResponse.namespace}"></div>

</c:if>

<!-- Start of favorites pane -->
<div class="ss_style_trans" id="ss_favorites_pane${renderResponse.namespace}" 
  style="position:absolute; visibility:hidden;">

<ssf:popupPane width="250px" titleTag="favorites"
      closeScript="ssMyFavorites${renderResponse.namespace}.hideFavoritesPane();return false;">


<div style="padding: 5px 10px 5px 10px;">

  <c:if test="${ssBinder != null && ssEntry.entityType != 'folderEntry'}">
  	<div class="ss_style_trans">
		<a href="javascript: ;" 
		 onClick="ssMyFavorites${renderResponse.namespace}.addBinderToFavorites('<ssf:url 
		    adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true" >
			<ssf:param name="operation" value="add_favorite_binder" />
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="viewAction" value="${action}" /></ssf:url>');return false;"
		><img <ssf:alt tag="favorites.addCurrentPage"/> src="<html:brandedImagesPath/>icons/button_new_bookmark.gif" border="0" />
		<span class="ss_bold ss_smallprint"><ssf:nlt tag="favorites.addCurrentPage" 
			text="Add the current page to the favorites list..."/></span></a>
  	</div>
  </c:if>
  <div class="ss_style_trans">
		<a href="javascript: ;" 
		 onClick="ssMyFavorites${renderResponse.namespace}.showhideFavoritesEditor()"
		><img <ssf:alt tag="favorites.edit"/> src="<html:brandedImagesPath/>icons/button_edit_bookmark.gif" border="0" />
		<span class="ss_bold ss_smallprint"><ssf:nlt tag="favorites.edit" 
			text="Edit Favorites"/></span></a>
  </div>
  <hr style="width: 90%" class="ss_att_divider"/>
  <div class="ss_style_trans" id="ss_favorites${renderResponse.namespace}" align="left">
	  <div style="float: right;" id="ss_favorites_loading${renderResponse.namespace}"><ssf:nlt tag="Loading"/></div>
	  <div id="ss_favorites_list${renderResponse.namespace}">&nbsp;</div>
  </div>
  <div class="ss_style_trans" style="display: none;" id="ss_favorites_editor${renderResponse.namespace}">
     <div style="padding: 10px 0px 7px 0px;">

	<table cellspacing="0" cellpadding="0" border="0">
	<tbody>
	<tr>
	  <td>
		<a class="ss_inlineButton" onClick="ssMyFavorites${renderResponse.namespace}.moveSelectedFavorites('down')"
		><img <ssf:alt tag="favorites.movedown"/> src="<html:imagesPath/>icons/button_move_down.gif" 
		/><span><ssf:nlt tag="favorites.movedown"/></span></a>
  	  </td>	
	  <td>		
		<a class="ss_inlineButton" onClick="ssMyFavorites${renderResponse.namespace}.moveSelectedFavorites('up')"
		><img <ssf:alt tag="favorites.moveup"/> src="<html:imagesPath/>icons/button_move_up.gif" 
		/><span><ssf:nlt tag="favorites.moveup"/></span></a>
  	  </td>
	  <td>
		
		<a class="ss_inlineButton" onClick="ssMyFavorites${renderResponse.namespace}.deleteSelectedFavorites()"
		><img <ssf:alt tag="favorites.delete"/> src="<html:imagesPath/>icons/button_delete.gif" 
		/><span><ssf:nlt tag="favorites.delete"/></span></a>
	  </td>
	 </tr>
	 </tbody>
	 </table>	
     </div>
     <div style="padding: 3px 0px 0px 135px; width: 40px;">
		<a class="ss_inlineButton" href="javascript: ;" 
		 onClick="ssMyFavorites${renderResponse.namespace}.saveFavorites()"
		><span><ssf:nlt tag="button.ok"/></span></a>
	 </div>

  </div>

  </div>
  </div>

</ssf:popupPane>

</div>

<!-- End of favorites pane -->
<!-- Start of myteams pane -->
<div class="ss_style_trans" id="ss_myteams_pane${renderResponse.namespace}" 
  style="position:absolute; visibility:hidden;">
<ssf:popupPane width="230px" titleTag="navigation.myTeams"
      closeScript="ssMyTeams${renderResponse.namespace}.hide();return false;">
<div style="padding: 5px 10px 5px 10px;">
  <div class="ss_style_trans" id="ss_myteams${renderResponse.namespace}" align="left">
	  <div style="float: right;" id="ss_myteams_loading${renderResponse.namespace}"><ssf:nlt tag="Loading"/></div>
	  <div id="ss_myteams_list${renderResponse.namespace}">&nbsp;</div>
  </div>
</div>
</ssf:popupPane>
</div>
<!-- End of favorites pane -->
<c:if test="${empty ssUser.displayStyle || ssUser.displayStyle == 'iframe' || (!empty ssFolderActionVerticalOverride && ssFolderActionVerticalOverride == 'yes')}" >
<!-- iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- end of iframe div -->
</c:if>
<script type="text/javascript">
function ss_showRecentPlacesDiv${renderResponse.namespace}() {
	var divObjTarget = self.document.getElementById('ss_recentPlacesDiv${renderResponse.namespace}');
	var divObjSource = self.document.getElementById('ss_recentPlaces${renderResponse.namespace}');
	if (divObjTarget.style.display == 'block') {
		divObjTarget.style.display = 'none';
	} else {
		divObjTarget.style.display = 'block';
		ss_setObjectLeft(divObjTarget, ss_getObjectLeftAbs(divObjSource))
	}
}
function ss_hideRecentPlacesDiv${renderResponse.namespace}() {
	var divObjTarget = self.document.getElementById('ss_recentPlacesDiv${renderResponse.namespace}');
	divObjTarget.style.display = 'none';
}
</script>

<div style="padding-bottom:4px;"></div>
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
<div style="padding-bottom:4px;"></div>

<script type="text/javascript">
ss_createOnLoadObj('ss_workarea_showPsuedoPortal${renderResponse.namespace}', ss_workarea_showPsuedoPortal${renderResponse.namespace});
</script>
