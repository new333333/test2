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
var ss_parentWorkareaNamespace${renderResponse.namespace} = "";
function ss_workarea_showPseudoPortal${renderResponse.namespace}(obj) {
	//See if we are in an iframe inside a portlet 
	var windowName = self.window.name    
	if (windowName.indexOf("ss_workareaIframe") == 0) {
		//We are running inside a portlet iframe; set up for layout changes
		ss_parentWorkareaNamespace${renderResponse.namespace} = windowName.substr("ss_workareaIframe".length)
		ss_createOnResizeObj('ss_setParentWorkareaIframeSize${renderResponse.namespace}', ss_setParentWorkareaIframeSize${renderResponse.namespace});
		ss_createOnLayoutChangeObj('ss_setParentWorkareaIframeSize${renderResponse.namespace}', ss_setParentWorkareaIframeSize${renderResponse.namespace});
	} else {
		//Show the pseudo portal
		var divObj = self.document.getElementById('ss_pseudoPortalDiv${renderResponse.namespace}');
		if (divObj != null) {
			divObj.className = "ss_pseudoPortal"
		}

	}
}

function ss_setParentWorkareaIframeSize${renderResponse.namespace}() {
	if (typeof self.parent != "undefined") {
		var resizeRoutineName = "ss_setWorkareaIframeSize" + ss_parentWorkareaNamespace${renderResponse.namespace};
		eval("var resizeRoutineExists = typeof(self.parent."+resizeRoutineName+")");
		if (resizeRoutineExists != "undefined") {
			eval("self.parent."+resizeRoutineName+"()");
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

<c:if test="${!empty ssBinder.branding}">
  <div>
    <span><ssf:markup type="view" entity="${ssBinder}"><c:out 
      value="${ssBinder.branding}" escapeXml="false"/></ssf:markup></span>
  </div>
</c:if>

<div id="ss_top_nav_wrapper">
<div class="ss_5colmask ss_fivecol">

   <div class="ss_5colmidright"> 
  <div class="ss_5colmid">
  <div class="ss_5colleftctr">
    <div class="ss_5colleft">
      <div class="ss_5col1">
      <div id="ss_top_nav_view">

<!-- Beginning of Find Bar:  Portal Full Screen Find People/Places/Search  -->      
        <ul>
    
     
          <li>
          	<a href="/c/portal/logout">
          		<ssf:nlt tag="navigation.logout" />
          	</a>
          </li>
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
          </ul>
          </div>
      </div><!-- end of col1-->
      <div class="ss_5col2">
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
	<div class="ss_global_toolbar_accessible">  
	<label for="ss_navbarFindUserButton${renderResponse.namespace}"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findUser"/></span></label>
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
      </div><!-- end of col2-->
      <div class="ss_5col3">
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
	<div class="ss_global_toolbar_accessible">
	  <label for="ss_navbarFindPlacesButton${renderResponse.namespace}"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findPlace"/></span></label>
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
      </div><!-- end of col3-->
      <div class="ss_5col4">
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
	<div class="ss_global_toolbar_accessible"> 
	<label for="ss_navbarFindTagsButton${renderResponse.namespace}"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findTag"/></span></label>
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
      </div><!-- end of col4-->
      <div class="ss_5col5">

<!-- Beginning of Search Buttons -->
<ssf:ifnotaccessible>
	<div class="ss_global_toolbar_quick">
	
			<span class="ss_global_toolbar_label_text_quickSearch"><ssf:nlt tag="navigation.search"/></span>
				  
			<span class="ss_global_toolbar_quick_advanced"><a class="ss_advanced ss_fineprint" 
				  href="<ssf:url windowState="maximized" actionUrl="true"><ssf:param 
				  name="action" value="advanced_search"/><ssf:param 
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
					<input name="searchText" style="width: 100px;" type="text" /> 
					<a class="ss_searchButton" 
					  href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" >
					  <img 
					  title="<ssf:nlt tag="alt.search"/>"
					  <ssf:alt tag="alt.search"/> src="<html:rootPath/>images/pics/1pix.gif" /></a>
					<input type="hidden" name="searchBtn" value="searchBtn"/>
					<input type="hidden" name="quickSearch" value="true"/>					
					<input type="hidden" name="operation" value="ss_searchResults"/>
			  </ssHelpSpot>
			 
				<a class="ss_savedQueries" alt="<ssf:nlt tag="searchResult.savedSearchTitle"/>" 
				  title="<ssf:nlt tag="searchResult.savedSearchTitle"/>" href="javascript: // ;" 
				  onclick="ss_showSavedQueriesList(this, 'ss_navbarPopupPane${renderResponse.namespace}',
				  '<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
				  name="newTab" value="1"/></ssf:url>');">
				  <img src="<html:imagesPath/>pics/menudown.gif" /></a>
				<div id="ss_navbarPopupPane${renderResponse.namespace}" class="ss_navbarPopupPane"></div>
				
			</form>
     	</div>		
	
		
	</div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
	<div class="ss_global_toolbar_accessible">
		
			<label for="ss_searchSearchText${renderResponse.namespace}"><span class="ss_global_toolbar_label_text_quickSearch"><ssf:nlt tag="navigation.search"/></span></label>
			  <span class="ss_global_toolbar_quick_advanced"><a class="ss_advanced ss_fineprint" 
			  href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
			  	name="binderId" value="${ssBinder.id}"/><ssf:param 
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
					  href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" >
					  <img src="<html:imagesPath/>pics/1pix.gif" 
					  title="<ssf:nlt tag="alt.search"/>"
					  <ssf:alt tag="alt.search"/> /> 
					  </a>
					<input type="hidden" name="searchBtn" value="searchBtn"/>
					<input type="hidden" name="quickSearch" value="true"/>					
					<input type="hidden" name="operation" value="ss_searchResults"/>
			</form>
     	</div>
     	

</ssf:ifaccessible>
      </div><!-- end of col5-->
   </div><!-- end of colleft-->
  </div><!-- end of colleftctr-->
</div><!-- end of colmid-->
  </div><!-- end of colmidright-->
</div>   <!-- end of colmask fivecol-->
<div class="ss_darkline"></div>
<div class="ss_medline">


<div id="ss_statusArea" class="ss_global_toolbar_maximized"><!-- beginning of other rows-->  


<!-- Beginning of Status Bar:  Status Share/Track Buttons My Teams and Favorites  -->

<!-- Start of Status line -->
<ssf:ifLoggedIn>

<li class="ss_rt_buffer">
<script type="text/javascript">
ss_statusCurrent = "${ssUser.status}";
</script>
<ssf:ifnotaccessible>
<span class="ss_statusprint"><ssf:nlt tag="relevance.userStatus"/></span>
<input type="text" size="50" style="font-size:9px; background-color:#cccccc;" value="${ssUser.status}"
  onFocus="ss_setStatusBackground(this, 'focus');"
  onKeyPress="ss_updateStatusSoon(this, event);"
  onChange="ss_updateStatusNow(this);"
  onBlur="ss_updateStatusNow(this);ss_setStatusBackground(this, 'blur')"
  onMouseover="ss_setStatusBackground(this, 'mouseOver');"
  onMouseout="ss_setStatusBackgroundCheck(this);"
  />
</ssf:ifnotaccessible>
<ssf:ifaccessible>
<div style="white-space:nowrap" >
<label for="ss_statusBoxText${renderResponse.namespace}"><span class="ss_statusprint"><ssf:nlt tag="relevance.userStatus"/></span></label>
<input type="text" id="ss_statusBoxText${renderResponse.namespace}"
  size="50" 
  style="font-size:9px; background-color:#cccccc;" 
  value="${ssUser.status}"
  />
  <input type="submit" style="font-size:9px;"
  onClick="ss_updateStatusNowAccessible('ss_statusBoxText${renderResponse.namespace}');return false;" 
  value="<ssf:nlt tag="button.ok"/>"
  /></div>
</ssf:ifaccessible>
</li>

</div><!-- end of status line area -->

<!-- Beginning of  My Teams and Favorites -->
<div id="ss_top_nav_buttontwo">
        <ul>
          

          <li><a title="<ssf:nlt tag="navigation.myTeams"/>"
			  href="javascript:;" 
<ssf:ifnotaccessible>
			  onClick="ssMyFavorites${renderResponse.namespace}.hideFavoritesPane();ssMyTeams${renderResponse.namespace}.show();"
</ssf:ifnotaccessible>
<ssf:ifaccessible>
			  onClick="ssMyTeams${renderResponse.namespace}.showAccessible()"
</ssf:ifaccessible>
              ><ssf:nlt tag="navigation.myTeams"/> <img border="0" 
              src="<html:imagesPath/>pics/menudown.gif" style="padding-left: 2px;"/> </a>
		      <ssHelpSpot helpId="navigation_bar/my_teams" offsetX="3" offsetY="13"  
		          title="<ssf:nlt tag="helpSpot.myTeamsButton"/>">
			    <div id="ss_navbarMyTeamsButton${renderResponse.namespace}">
			      	
			    </div>
			  </ssHelpSpot>
<ssf:ifnotaccessible>
  <div id="ss_navbar_myteams${renderResponse.namespace}"
      style="visibility:hidden;margin:20px 0px 0px -130px;padding:0px;">
  </div>
</ssf:ifnotaccessible>
<ssf:ifaccessible>
  <div id="ss_navbar_myteams${renderResponse.namespace}"
      style="position:relative;display:none;visibility:hidden;z-index:500;
             margin:25px 0px 0px -130px;padding:0px;">
	<iframe src="<html:rootPath/>js/forum/null.html" style="background-color:#ffffff;"
	  id="ss_myTeamsIframe${renderResponse.namespace}">xxx</iframe>
	<div style="background-color:#ffffff;">
	  <a href="javascript: ;" onClick="ssMyTeams${renderResponse.namespace}.hideAccessible();return false;">
	    <span><ssf:nlt tag="button.close"/></span>
	  </a>
	</div>
  </div>
</ssf:ifaccessible>

          </li>
<ssf:ifnotaccessible>
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
</ssf:ifnotaccessible>
        
                 
        </ul>
</div> <!-- end of My Teams and Favorites div -->

<!-- Beginning of  Share/Track Buttons -->
<div class="ss_clearSTButton">



<c:if test="${!empty ssBinder && ssBinder.entityType != 'profiles'}">
<a style="display:inline;" class="ss_buttonSTButton ss_fineprint" 
  href="<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_relevance" actionUrl="false"><ssf:param 
		name="operation" value="share_this_binder" /><ssf:param 
		name="binderId" value="${ssBinder.id}" /></ssf:url>" 
  onClick="ss_openUrlInWindow(this, '_blank', '450px', '600px');return false;"
<c:if test="${ssBinder.entityType == 'workspace'}"> 
	title="<ssf:nlt tag="relevance.shareThisWorkspace"/>" >
	<span><ssf:nlt tag="relevance.justShare"/></span></c:if>
<c:if test="${ssBinder.entityType == 'folder'}">
  <c:if test="${ssDefinitionFamily != 'calendar'}">
  	title="<ssf:nlt tag="relevance.shareThisFolder"/>" >
  	<span><ssf:nlt tag="relevance.justShare"/></span></c:if>
  <c:if test="${ssDefinitionFamily == 'calendar'}">
  	title="<ssf:nlt tag="relevance.shareThisCalendar"/>" >
  	<span><ssf:nlt tag="relevance.justShare"/></span></c:if>
</c:if>
</a>
</c:if>




<c:if test="${!empty ssBinder && ssBinder.entityType != 'profiles'}">
<a class="ss_buttonSTButton ss_fineprint" href="javascript: ;" 
  onClick="ss_trackThisBinder('${ssBinder.id}', '${renderResponse.namespace}');return false;"
<c:if test="${ssBinder.entityType == 'workspace'}">
  <c:if test="${ssBinder.definitionType != 12}">
  title="<ssf:nlt tag="relevance.trackThisWorkspace"/>" >
  	<span class="ss_fineprint"><ssf:nlt tag="relevance.justTrack"/></span></c:if>
  <c:if test="${ssBinder.definitionType == 12}">
  	title="<ssf:nlt tag="relevance.trackThisPerson"/>" >
  	<span class="ss_fineprint"><ssf:nlt tag="relevance.justTrack"/></span></c:if>
</c:if>
<c:if test="${ssBinder.entityType == 'folder'}">
  <c:if test="${ssDefinitionFamily != 'calendar'}">
  	title="<ssf:nlt tag="relevance.trackThisFolder"/>" >
  	<span class="ss_fineprint"><ssf:nlt tag="relevance.justTrack"/></span></c:if>
  <c:if test="${ssDefinitionFamily == 'calendar'}">
  	title="<ssf:nlt tag="relevance.trackThisCalendar"/>" >
  	<span class="ss_fineprint"><ssf:nlt tag="relevance.justTrack"/></span></c:if>
</c:if>
</a>
</c:if>



</div><!-- end of share and track buttons div -->
<div id="ss_track_this_ok${renderResponse.namespace}" 
  style="position:relative; display:none; visibility:hidden; top:18px; left:-40px;
         border:1px solid black; padding:10px; background-color:#ffffff;"></div>


 </div><!-- end of medline -->
 
 <div class="ss_clear_float"></div>

</ssf:ifLoggedIn>




<c:if test="${empty ss_navbarBottomSeen}">
<c:set var="ss_navbarBottomSeen" value="1"/>

<div id="ss_navbar_bottom${renderResponse.namespace}"></div>

</c:if>

<!-- Start of favorites pane -->
<div class="ss_style_trans" id="ss_favorites_pane${renderResponse.namespace}" 
  style="position:absolute; visibility:hidden;">

<ssf:popupPane width="200px" titleTag=""
      closeScript="ssMyFavorites${renderResponse.namespace}.hideFavoritesPane();return false;">


<div style="padding: 2px 5px 2px 5px;">

  <c:if test="${ssBinder != null && ssEntry.entityType != 'folderEntry'}">
  	<div class="ss_style_trans">
  	<img title="<ssf:alt tag="favorites.addCurrentPage"/>" src="<html:brandedImagesPath/>icons/button_new_bookmark.gif" border="0" />
		<a href="javascript: ;" 
		 onClick="ssMyFavorites${renderResponse.namespace}.addBinderToFavorites('<ssf:url 
		    adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true" >
			<ssf:param name="operation" value="add_favorite_binder" />
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="viewAction" value="${action}" /></ssf:url>');return false;"
		>
		<span class="ss_bold ss_smallprint"><ssf:nlt tag="favorites.addCurrentPage" 
			text="Add the current page to the favorites list..."/></span></a>
  	</div>
  </c:if>
  <div class="ss_style_trans">
  		<img title="<ssf:alt tag="favorites.edit"/>" src="<html:brandedImagesPath/>icons/button_edit_bookmark.gif" border="0" />
		<a href="javascript: ;" 
		 onClick="ssMyFavorites${renderResponse.namespace}.showhideFavoritesEditor()"
		>
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
  </div>

</ssf:popupPane>

</div>

<!-- End of favorites pane -->
<!-- Start of myteams pane -->
<div class="ss_style_trans" id="ss_myteams_pane${renderResponse.namespace}" 
  style="position:absolute; visibility:hidden;">
<ssf:popupPane width="175px" titleTag=""
      closeScript="ssMyTeams${renderResponse.namespace}.hide();return false;">
<div style="padding: 2px 5px 2px 5px;">
  <div class="ss_style_trans" id="ss_myteams${renderResponse.namespace}" align="left">
	  <div style="float: right;" id="ss_myteams_loading${renderResponse.namespace}"><ssf:nlt tag="Loading"/></div>
	  <div id="ss_myteams_list${renderResponse.namespace}">&nbsp;</div>
  </div>
</div>
</ssf:popupPane>
</div>
<!-- End of myteams pane -->
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

<div style="padding-bottom:0px;"></div>
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
<div style="padding-bottom:2px;"></div>

<script type="text/javascript">
ss_workarea_showPseudoPortal${renderResponse.namespace}()
</script>
