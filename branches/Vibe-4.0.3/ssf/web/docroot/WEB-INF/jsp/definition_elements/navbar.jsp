<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
<c:set var="ss_urlWindowState" value="maximized"/>
<c:set var="ss_urlWindowState" value=""/>
<!-- <script type="text/javascript" src="/ssf/js/tree/tree_widget.js"></script> -->
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
var	ss_savedSearchTitle = "<ssf:nlt tag="searchResult.savedSearchTitle"/>";
var ssMyFavorites${renderResponse.namespace} = new ssFavorites('${renderResponse.namespace}');
var ssMyTeams${renderResponse.namespace} = new ssTeams('${renderResponse.namespace}');
</script>
<c:if test="${ssUserProperties.debugMode}">
<!-- Start of debug window -->
  <div style="border:1px solid #333;">
  <div style="background-color:#CECECE; border-bottom:1px solid #333; width:100%;">
    <table cellspacing="0" cellpadding="0" style="background-color:#CECECE; width:100%;">
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
    </table>
  </div>
  <div>
  <textarea id="debugTextarea${renderResponse.namespace}" style="width:100%;" rows="6"></textarea>
  </div>
  </div>
  <br/>
<!-- End of debug window -->
</c:if>

<!-- Start of global toolbar - MAXIMIZED PORTAL WINDOW MODE -->
<c:if test="${ss_navbar_style != 'portlet'}">
<ssf:sidebarPanel title="sidebar.navigator" id="ss_navigator_box" 
     divClass="ss_global_toolbar ss_global_toolbar_maximized"
     initOpen="true" sticky="true">
<table class="ss_global_toolbar_maximized" width="100%" cellspacing="0" cellpadding="0" border="0"><tbody>
<tr>
<td>

<table><tbody><tr>

<td>
	<!-- My workspace -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_myworkspace" 
	  title="<ssf:nlt tag="navigation.myWorkspace"/>"
      onClick="ss_openUrlInWorkarea('<ssf:url action="view_ws_listing" ><ssf:param 
      	name="binderId" value="${ssUser.parentBinder.id}"/><ssf:param 
      	name="entryId" value="${ssUser.id}"/><ssf:param 
      	name="newTab" value="1"/></ssf:url>', '${ssUser.workspaceId}', 'view_ws_listing');return false;"
     onMouseOver="this.style.cursor = 'pointer';"
    ><ssHelpSpot helpId="navigation_bar/my_workspace_button" offsetY="13" offsetX="15" 
	      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
	    <div id="ss_navbarMyWorkspaceButton"><img src="<html:imagesPath/>pics/1pix.gif"/></div>
	  </ssHelpSpot>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible" >
	  <ssHelpSpot helpId="navigation_bar/my_workspace_button" offsetY="-10" offsetX="-5" 
	      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
	  </ssHelpSpot>
	<a href="<ssf:url action="view_ws_listing"><ssf:param 
      	name="binderId" value="${ssUser.parentBinder.id}"/><ssf:param 
      	name="entryId" value="${ssUser.id}"/><ssf:param 
      	name="newTab" value="1"/></ssf:url>"
    ><ssf:nlt tag="navigation.myWorkspace"/></a>
	  </div>
</ssf:ifaccessible>

<ssf:ifnotaccessible>
</td>
<td>
</ssf:ifnotaccessible>

<!-- Favorites -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_favs" 
	  onClick="ssMyFavorites${renderResponse.namespace}.showFavoritesPane();"
	  title="<ssf:nlt tag="navigation.favorites"/>"
      onMouseOver="this.style.cursor = 'pointer';"
    >
      <ssHelpSpot helpId="navigation_bar/favorites_button" offsetX="3" offsetY="13"  
          title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
	    <div id="ss_navbarFavoritesButton${renderResponse.namespace}">
	      	    <img src="<html:imagesPath/>pics/1pix.gif"/>
	    </div>
	  </ssHelpSpot>
	</div>
    <div id="ss_navbar_favorites${renderResponse.namespace}" 
      style="visibility:hidden;margin:0px;padding:0px;"
    ></div>

</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<a href="javascript:;" onClick="ssMyFavorites${renderResponse.namespace}.showFavoritesPane();"
	  title="<ssf:nlt tag="navigation.favorites"/>"
    ><ssf:nlt tag="navigation.favorites"/></a>
      <ssHelpSpot helpId="navigation_bar/favorites_button" offsetX="3" offsetY="13"  
          title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
	  </ssHelpSpot>
    <div id="ss_navbar_favorites${renderResponse.namespace}" 
      style="visibility:hidden;margin:0px;padding:0px;"
    ></div>
</ssf:ifaccessible>


<ssf:ifnotaccessible>
</td>

<td>
</ssf:ifnotaccessible>

<!-- My Teams -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_myteams" 
	  onClick="ssMyTeams${renderResponse.namespace}.show();return false;"
	  title="<ssf:nlt tag="navigation.myTeams"/>"
      onMouseOver="this.style.cursor = 'pointer';"
    >
      <ssHelpSpot helpId="navigation_bar/my_teams" offsetX="3" offsetY="13"  
          title="<ssf:nlt tag="helpSpot.myTeamsButton"/>">
	    <div id="ss_navbarMyTeamsButton${renderResponse.namespace}">
	      	    <img src="<html:imagesPath/>pics/1pix.gif"/>
	    </div>
	  </ssHelpSpot>
	</div>
	<div id="ss_navbar_myteams${renderResponse.namespace}"
	      style="visibility:hidden;margin:0px;padding:0px;"></div>

</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible">
	</div>
</ssf:ifaccessible>


</td>

</tr>
</tbody>
</table>


</td></tr>



<tr><td>

<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
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
		  		action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
		  			name="newTab" value="1"/></ssf:url>">
			  <ssHelpSpot helpId="navigation_bar/search_button" offsetY="-12" 
                 <c:if test="<%= isIE %>">
                   offsetX="159" 
                 </c:if>
                 <c:if test="<%= !isIE %>">
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
					<sec:csrfInput />
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
				  href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
					name="binderId" value="${ssBinder.id}"/><ssf:param 
				  name="tabTitle" value="SEARCH FORM"/><ssf:param 
				  name="newTab" value="0"/></ssf:url>"
				><ssf:nlt tag="navigation.search.advanced"/></a></span>
		</div>
		<div class="ss_global_toolbar_search"  id="ss_navbarSearchButton${renderResponse.namespace}" >
		  <ssHelpSpot helpId="navigation_bar/search_button" offsetY="-12" 
                 <c:if test="<%= isIE %>">
                   offsetX="159" 
                 </c:if>
                 <c:if test="<%= !isIE %>">
                   offsetX="147" 
                 </c:if>
		    title="<ssf:nlt tag="helpSpot.searchButton"/>">
		  </ssHelpSpot>
     		<form method="post" id="ss_simpleSearchForm${renderResponse.namespace}" 
		  		name="ss_simpleSearchForm${renderResponse.namespace}" 
		  		action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
		  			name="newTab" value="1"/></ssf:url>">
					<input name="searchText" type="text" id="ss_searchSearchText${renderResponse.namespace}" /> 
					<a class="ss_searchButton" 
					  href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" ><img 
					  <ssf:alt tag="alt.search"/> src="<html:imagesPath/>pics/search_icon.gif" /></a>
					<input type="hidden" name="searchBtn" value="searchBtn"/>
					<input type="hidden" name="quickSearch" value="true"/>					
					<input type="hidden" name="operation" value="ss_searchResults"/>
					<sec:csrfInput />
			</form>
     	</div>
	</div>
</ssf:ifaccessible>
</td></tr>

<tr><td>
<table><tbody><tr>
<ssf:ifnotaccessible>
<td>
<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findUser"/></span>
</td>
</ssf:ifnotaccessible>
<td>
	<!-- Find people -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_findUser"  id="ss_navbarFindUserButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
			<form method="post" id="ss_findUserForm${renderResponse.namespace}" 
			  name="ss_findUserForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="user"
				    width="70px" singleItem="true"/>
				<sec:csrfInput />
			</form>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible"  id="ss_navbarFindUserButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findUserForm${renderResponse.namespace}" 
			  name="ss_findUserForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="user"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findUser"/>
				  </ssf:param>
				  </ssf:find>
				<sec:csrfInput />
			</form>
		</div>
	</div>
</ssf:ifaccessible>
</td>
</tr>
<tr>
<ssf:ifnotaccessible>
<td>
<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findPlace"/></span>
</td>
</ssf:ifnotaccessible>
<td>
	<!-- Find place -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_findUser"  id="ss_navbarFindPlacesButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
		<div>
			<form method="post" id="ss_findPlacesForm${renderResponse.namespace}" 
			  name="ss_findPlacesForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findPlacesForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="places"
				    width="70px" singleItem="true"/>
				<sec:csrfInput />
			</form>
		</div>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible"  id="ss_navbarFindPlacesButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findPlacesForm${renderResponse.namespace}" 
			  name="ss_findPlacesForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findPlacesForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="places"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findPlace"/>
				  </ssf:param>
				  </ssf:find>
				<sec:csrfInput />
			</form>
		</div>
	</div>
</ssf:ifaccessible>
</td></tr>
<tr>
<ssf:ifnotaccessible>
<td>
<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findTag"/></span>
</td>
</ssf:ifnotaccessible>
<td>
		<!-- Find tag -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_findUser" id="ss_navbarFindTagsButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
		<div>
			<form method="post" id="ss_findTagsForm${renderResponse.namespace}" 
			  name="ss_findTagsForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findTagsForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="tags"
				    width="70px" singleItem="true"/>
				<sec:csrfInput />
			</form>
		</div>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible" id="ss_navbarFindTagsButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findTagsForm${renderResponse.namespace}" 
			  name="ss_findTagsForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findTagsForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="tags"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findTag"/>
				  </ssf:param>
				  </ssf:find>
				<sec:csrfInput />
			</form>
		</div>
	</div>
</ssf:ifaccessible>
</td></tr>
</tbody></table>
</td></tr>

<tr><td>
<c:if test="${empty ssSidebarCurrentWorkspace.id}">
<div style="padding:3px;"> </div>
</c:if>
<c:if test="${!empty ssSidebarCurrentWorkspace.id}">
  <div class="ss_global_toolbar_quick">
    <div style="text-align: center; padding: 3px;">
      <a href="<ssf:url action="add_binder" actionUrl="true"><ssf:param 
			  	name="operation" value="add_workspace"/><ssf:param 
			  	name="binderId" value="${ssSidebarCurrentWorkspace.id}"/></ssf:url>"
	   class="ss_linkButton"><ssf:nlt tag="toolbar.menu.addWorkspace" /></a>
    </div>
  </div>
</c:if>
</td></tr>

</tbody></table>

</ssf:sidebarPanel>

</c:if>
<!-- Start of global toolbar - PORTLET WINDOW MODE -->
<c:if test="${ss_navbar_style == 'portlet'}">
<div class="ss_global_toolbar ss_global_toolbar_in_portlet">
<table width="100%" cellpadding="1" cellspacing="0" border="0">
<tr class="ss_row_txt">
  <td valign="top" width="75" rowspan="5"><!-- My workspace -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_myworkspace_big" 
      onClick="self.location.href='<ssf:url action="view_ws_listing"><ssf:param 
      	name="binderId" value="${ssUser.parentBinder.id}"/><ssf:param 
      	name="entryId" value="${ssUser.id}"/><ssf:param 
      	name="newTab" value="1"/></ssf:url>';"
     onMouseOver="this.style.cursor = 'pointer';"
    >
	  <ssHelpSpot helpId="navigation_bar/my_workspace_button_portlet" offsetY="25" offsetX="5" 
	      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
	    <div id="ss_navbarMyWorkspaceButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.myWorkspace"/></span>
	    </div>
	  </ssHelpSpot>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible" >
	  <ssHelpSpot helpId="navigation_bar/my_workspace_button"  offsetY="-20" offsetX="0"  
	      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
	  </ssHelpSpot>
	<a href="<ssf:url action="view_ws_listing"><ssf:param 
      	name="binderId" value="${ssUser.parentBinder.id}"/><ssf:param 
      	name="entryId" value="${ssUser.id}"/><ssf:param 
      	name="newTab" value="1"/></ssf:url>"
    ><img <ssf:alt tag="navigation.myWorkspace"/> src="<html:imagesPath/>icons/toolbar_myworkspace_big.jpg"/></a>
	    <div id="ss_navbarMyWorkspaceButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.myWorkspace"/></span>
	    </div>
	  </div>
</ssf:ifaccessible>
  </td>
  <td valign="top" width="75" rowspan="5"><!-- Favorites -->
<ssf:ifnotaccessible>
    <div class="ss_global_toolbar_favs_big" onClick="ssMyFavorites${renderResponse.namespace}.showFavoritesPane();"
      onMouseOver="this.style.cursor = 'pointer';"
    >
      <ssHelpSpot helpId="navigation_bar/favorites_button_portlet" offsetY="25" offsetX="5"  
          title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
	    <div id="ss_navbarFavoritesButton${renderResponse.namespace}">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.favorites"/></span>
	    </div>
	    <div id="ss_navbar_favorites${renderResponse.namespace}" style="visibility:hidden;margin:0px;padding:0px;"
	    ><img <ssf:alt tag="navigation.favorites"/> border="0" src="<html:imagesPath/>pics/1pix.gif"></div>
	  </ssHelpSpot>
	</div>
</ssf:ifnotaccessible>
  </td>
  
  <td class="ss_global_toolbar_portlet_box" colspan="3">
    <span class="ss_global_toolbar_label_text_quickSearch">
      <label for="ss_searchSearchText${renderResponse.namespace}">
        <ssf:nlt tag="navigation.search"/>
      </label>
    </span>
		<a class="ss_advanced ss_fineprint" style="margin-left: 15px;" 
			href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
			name="binderId" value="${ssBinder.id}"/><ssf:param 
			name="tabTitle" value="SEARCH FORM"/><ssf:param 
			name="newTab" value="0"/></ssf:url>"
		><ssf:nlt tag="navigation.search.advanced"/></a>
  </td>
  <td rowspan="5" width="100%"></td>
  <td valign="top" rowspan="5"><!-- Help button -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_help"  onClick="ss_helpSystem.run();return false;"
      onMouseOver="this.style.cursor = 'pointer';">
        <img <ssf:alt tag="navigation.help"/> src="<html:imagesPath/>pics/1pix.gif" />
	    <div id="ss_navbarHelpButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.help" text="Help"/></span>
	    </div>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible">
	  <a href="javascript: ss_helpSystem.run();">
	        <img <ssf:alt tag="navigation.help"/> src="<html:imagesPath/>icons/help.png" /></a>
		    <div id="ss_navbarHelpButton">
		      <span class="ss_fineprint"><ssf:nlt tag="navigation.help" text="Help"/></span>
		    </div>
	</div>
</ssf:ifaccessible>
  </td>
</tr>
<tr>
  <td class="ss_global_toolbar_portlet_box" colspan="3"><!-- Search form -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_search"  id="ss_navbarSearchButton${renderResponse.namespace}">
		<form method="post" id="ss_simpleSearchForm${renderResponse.namespace}" 
		  name="ss_simpleSearchForm${renderResponse.namespace}" 
		  action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
		  	name="newTab" value="1"/></ssf:url>" style="display: inline;">
		  <ssHelpSpot helpId="navigation_bar/search_button_portlet" offsetX="-15" offsetY="12" xAlignment="center" 
		    title="<ssf:nlt tag="helpSpot.searchButton"/>">
			<input name="searchText" type="text" />
			<input type="hidden" name="quickSearch" value="true"/>					
			<input type="hidden" name="operation" value="ss_searchResults"/>
			  
			  <a class="ss_searchButton" 
			    href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" ><img 
			    src="<html:imagesPath/>pics/1pix.gif" <ssf:alt tag="alt.search"/> /></a>
		    <input type="hidden" name="searchBtn" value="searchBtn"/>
		  </ssHelpSpot>
			<sec:csrfInput />
		</form>
		
		<a class="ss_savedQueries" alt="<ssf:nlt tag="searchResult.savedSearchTitle"/>" 
		title="<ssf:nlt tag="searchResult.savedSearchTitle"/>" href="javascript: // ;" 
		onclick="ss_showSavedQueriesList(this, 'ss_navbarPopupPane${renderResponse.namespace}',
				  '<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
				  name="newTab" value="1"/></ssf:url>');"><img 
				  src="<html:imagesPath/>pics/menudown.gif" /></a>
		<div id="ss_navbarPopupPane${renderResponse.namespace}" class="ss_navbarPopupPane"></div>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible"  id="ss_navbarSearchButton${renderResponse.namespace}" >
	  <ssHelpSpot helpId="navigation_bar/search_button"  offsetX="-20" offsetY="0" 
	    title="<ssf:nlt tag="helpSpot.searchButton"/>">
	  </ssHelpSpot>
 		<form method="post" id="ss_simpleSearchForm${renderResponse.namespace}" 
	  		name="ss_simpleSearchForm${renderResponse.namespace}" 
	  		action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
	  			name="newTab" value="1"/></ssf:url>"
			style="display: inline;">
				<input name="searchText" type="text" id="ss_searchSearchText${renderResponse.namespace}" /> 
				<a class="ss_searchButton" 
				  href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" ><img 
				  <ssf:alt tag="alt.search"/> src="<html:imagesPath/>pics/search_icon.gif" /></a>
				<input type="hidden" name="searchBtn" value="searchBtn"/>
				<input type="hidden" name="quickSearch" value="true"/>					
				<input type="hidden" name="operation" value="ss_searchResults"/>
			<sec:csrfInput />
		</form>
		
		<a class="ss_advanced ss_fineprint" 
			href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
			name="tabTitle" value="SEARCH FORM"/><ssf:param 
			name="binderId" value="${ssBinder.id}"/><ssf:param 
			name="newTab" value="0"/></ssf:url>"
			><ssf:nlt tag="navigation.search.advanced"/></a>
 	</div>
</ssf:ifaccessible>
  </td>
</tr>
<tr class="ss_row_txt">
  <td class="ss_global_toolbar_portlet_box" width="75px"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findUser"/></span></td>
  <td class="ss_global_toolbar_portlet_box" width="75px"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findPlace"/></span></td>
  <td class="ss_global_toolbar_portlet_box" width="75px"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findTag"/></span></td>
</tr>
<tr class="ss_row_last">
  <td class="ss_global_toolbar_portlet_box" align="left" valign="top"><!-- Find people-->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_findUser"  id="ss_navbarFindUserButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
		<form method="post" id="ss_findUserForm${renderResponse.namespace}" 
		  name="ss_findUserForm${renderResponse.namespace}" 
		  action="<ssf:url action="findUser" actionUrl="true"/>">
			  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="user"
			    width="70px" singleItem="true"/>
			<sec:csrfInput />
		</form>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible"  id="ss_navbarFindUserButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findUserForm${renderResponse.namespace}" 
			  name="ss_findUserForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="user"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findUser"/>
				  </ssf:param>
				  </ssf:find>
				<sec:csrfInput />
			</form>
		</div>
	</div>
</ssf:ifaccessible>
  </td>
  <td class="ss_global_toolbar_portlet_box" align="left" valign="top"><!-- Find places form -->
<ssf:ifnotaccessible>
    <div class="ss_global_toolbar_findUser"  id="ss_navbarFindPlacesButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
		<form method="post" id="ss_findPlacesForm${renderResponse.namespace}" 
		  name="ss_findPlacesForm${renderResponse.namespace}" 
		  action="<ssf:url action="findUser" actionUrl="true"/>">
			  <ssf:find 
			    formName="ss_findPlacesForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="places"
			    width="70px" singleItem="true"/>
			<sec:csrfInput />
		</form>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible"  id="ss_navbarFindPlacesButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findPlacesForm${renderResponse.namespace}" 
			  name="ss_findPlacesForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findPlacesForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="places"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findPlace"/>
				  </ssf:param>
				  </ssf:find>
				<sec:csrfInput />
			</form>
		</div>
	</div>
</ssf:ifaccessible>
  </td>
  <td class="ss_global_toolbar_portlet_box" align="left" valign="top"><!-- Find tags form -->
<ssf:ifnotaccessible>
   <div class="ss_global_toolbar_findUser" id="ss_navbarFindTagsButton${renderResponse.namespace}"
     onMouseOver="this.style.cursor = 'pointer';">
		<form method="post" id="ss_findTagsForm${renderResponse.namespace}" 
		  name="ss_findTagsForm${renderResponse.namespace}" 
		  action="<ssf:url action="findUser" actionUrl="true"/>">
			  <ssf:find 
			    formName="ss_findTagsForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="tags"
			    width="70px" singleItem="true"/>
			<sec:csrfInput />
		</form>
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible" id="ss_navbarFindTagsButton${renderResponse.namespace}">
		<div>
			<form method="post" id="ss_findTagsForm${renderResponse.namespace}" 
			  name="ss_findTagsForm${renderResponse.namespace}" 
			  action="<ssf:url action="findUser" actionUrl="true"/>">
				  <ssf:find 
				    formName="ss_findTagsForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="tags"
				    width="70px" singleItem="true"> 
				  <ssf:param name="label" useBody="true">
				    <ssf:nlt tag="navigation.findTag"/>
				  </ssf:param>
				  </ssf:find>
				<sec:csrfInput />
			</form>
		</div>
	</div>
</ssf:ifaccessible>
  </td>
</tr>
<tr class="ss_row_last">
  <td colspan="3" class="ss_global_toolbar_portlet_box" align="left" valign="top">

<div style="float: right; padding-top:5px; padding-bottom: 5px;">
<ssHelpSpot helpId="navigation_bar/create_team_button" 
  title="<ssf:nlt tag="helpSpot.createTeam"/>"
  offsetX="-13" offsetY="-1" xAlignment="center">
<c:if test="${empty ss_inlineHelpDivIdNumber}">
  <c:set var="ss_inlineHelpDivIdNumber" value="0" scope="request"/>
</c:if>
<c:set var="ss_inlineHelpDivIdNumber" value="${ss_inlineHelpDivIdNumber + 1}" scope="request"/>
<a class="ss_advanced ss_fineprint" href="javascript: ;" 
  onClick="ss_helpSystem.showInlineHelpSpotInfo(this, 'navigation_bar/create_team_button', '', 200, 20, 'left', 'top');return false;"
><ssf:nlt tag="navigation.createTeam"/></a>
<div id="${renderResponse.namespace}ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}" class="ss_inlineHelp">
 <div align="right"><a href="javascript:;" 
  onClick="ss_hideDivNone('${renderResponse.namespace}ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}');return false;"><img 
  border="0" alt="<ssf:nlt tag="button.close"/>" 
  src="<html:imagesPath/>pics/sym_s_delete.gif"></a></div>
 <div id="${renderResponse.namespace}ss_inlineHelpDiv${ss_inlineHelpDivIdNumber}"></div>
</div>
</ssHelpSpot>
</div>

<ssHelpSpot helpId="navigation_bar/my_teams_portlet"  
  title="<ssf:nlt tag="helpSpot.myTeams"/>"
  offsetX="-11" offsetY="2" xAlignment="center">
<div style="padding-top:5px; padding-bottom: 5px;">
<a class="ss_linkButton" href="javascript: ;" onClick="ssMyTeams${renderResponse.namespace}.show();return false;">
  <ssf:nlt tag="navigation.myTeams"/> <img src="<html:imagesPath/>pics/menudown.gif"/>
</a>
<div id="ss_navbar_myteams${renderResponse.namespace}"
	      style="visibility:hidden;margin:0px;padding:0px;"></div>
<div id="${renderResponse.namespace}ss_myTeams"></div>
</div>
</ssHelpSpot>

  </td>
</tr>


</table>
</div>
</c:if>
<c:if test="${empty ss_navbarBottomSeen}">
<c:set var="ss_navbarBottomSeen" value="1"/>

<div id="ss_navbar_bottom${renderResponse.namespace}"></div>

</c:if>

<!-- Start of favorites pane -->
<div class="ss_style_trans" id="ss_favorites_pane${renderResponse.namespace}" 
  style="position:absolute; visibility:hidden;">

<ssf:popupPane width="225px" titleTag="favorites"
      closeScript="ssMyFavorites${renderResponse.namespace}.hideFavoritesPane();return false;">


<div style="padding: 5px 10px 5px 10px;">

<!-- Only show add a place in maximized view  -->
<c:if test="${ss_navbar_style != 'portlet'}">
  <c:if test="${ssBinder != null && ssEntry.entityType != 'folderEntry'}">
  	<div class="ss_style_trans">
		<a href="javascript: ;" 
		 onClick="ssMyFavorites${renderResponse.namespace}.addBinderToFavorites('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true" >
			<ssf:param name="operation" value="add_favorite_binder" />
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="viewAction" value="${action}" /></ssf:url>');return false;"
		><img <ssf:alt tag="favorites.addCurrentPage"/> src="<html:brandedImagesPath/>icons/button_new_bookmark.gif" />
		<span class="ss_bold ss_smallprint"><ssf:nlt tag="favorites.addCurrentPage" 
			text="Add the current page to the favorites list..."/></span></a>
  	</div>
  </c:if>
</c:if>
  <div class="ss_style_trans">
		<a href="javascript: ;" 
		 onClick="ssMyFavorites${renderResponse.namespace}.showhideFavoritesEditor()"
		><img <ssf:alt tag="favorites.edit"/> src="<html:brandedImagesPath/>icons/button_edit_bookmark.gif" />
		<span class="ss_bold ss_smallprint"><ssf:nlt tag="favorites.edit" 
			text="Edit Favorites"/></span></a>
  </div>
  <hr style="width: 100%" class="ss_att_divider"/>
  <div class="ss_style_trans" id="ss_favorites${renderResponse.namespace}" align="left">
	  <div style="float: right;" id="ss_favorites_loading${renderResponse.namespace}"><ssf:nlt tag="Loading"/></div>
	  <br /><div id="ss_favorites_list${renderResponse.namespace}">&nbsp;</div>
  </div>
  <div class="ss_style_trans" style="display: none;" id="ss_favorites_editor${renderResponse.namespace}">
     <div style="padding: 10px 0px 7px 0px;">

	<table cellspacing="0" cellpadding="0" border="0"><tbody><tr>
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
	 </tr></tbody></table>	
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
<ssf:popupPane width="250px" titleTag="navigation.myTeams"
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
<c:if test="${ss_navbar_style != 'portlet'}">
<c:if test="${empty ssUser.currentDisplayStyle || ssUser.currentDisplayStyle == 'iframe' || 
  (!empty ssFolderActionVerticalOverride && ssFolderActionVerticalOverride == 'yes')}" >
<!-- iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- end of iframe div -->
</c:if>
</c:if>
