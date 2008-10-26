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
<c:set var="ss_urlWindowState" value=""/>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<!-- <script type="text/javascript" src="/ssf/js/tree/tree_widget.js"></script> -->
<script type="text/javascript">
var	ss_savedSearchTitle = "<ssf:nlt tag="searchResult.savedSearchTitle"/>";
var ssMyFavorites${renderResponse.namespace} = new ssFavorites('${renderResponse.namespace}');
var ssMyTeams${renderResponse.namespace} = new ssTeams('${renderResponse.namespace}');
var ss_displayType = "${ss_displayType}";
</script>
<c:if test="${ssUserProperties.debugMode}">
<!-- Start of debug window -->
<script type="text/javascript">
function ss_turnOffDebugMode() {
	var url = self.location.href + "&enableDebug=off"
	self.location.href = url;
}
var ss_debugTextareaId = "debugTextarea${renderResponse.namespace}"
</script>
<!-- this needs to be in a style sheet -->
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
		divObj = self.document.getElementById('ss_upperRightToolbar${renderResponse.namespace}');
		if (divObj != null) {
			divObj.style.display = "block"
			divObj.style.visibility = "visible"
		}
	}
}

function ss_setParentWorkareaIframeSize${renderResponse.namespace}() {
	ss_debug('In routine: ss_setParentWorkareaIframeSize${renderResponse.namespace}')
	if (typeof self.parent != "undefined") {
		var resizeRoutineName = "ss_setWorkareaIframeSize" + ss_parentWorkareaNamespace${renderResponse.namespace};
		eval("var resizeRoutineExists = typeof(self.parent."+resizeRoutineName+")");
		ss_debug('resizeRoutineExists = '+resizeRoutineExists)
		if (resizeRoutineExists != "undefined") {
			ss_debug('namespace = ${renderResponse.namespace}')
			eval("ss_debug(self.parent."+resizeRoutineName+")");
			eval("self.parent."+resizeRoutineName+"()");
		} else {
			//See if there is a common routine to call in case the namespaces don't match
			if (typeof self.parent.ss_setWorkareaIframeSize != "undefined") {
				self.parent.ss_setWorkareaIframeSize();
			}
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

<c:if test="${!ss_mashupHideMasthead || ss_mashupShowBranding}">
<div id="ss_top_nav_wrapper">
<!-- Begin New Header  -->  
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tbody>
  <tr class="ss_masthead_top">
    <td align="left">
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tbody>
      <tr>
        <td width="74%" rowspan="3" valign="top">
            <c:if test="${!ss_mashupHideMasthead || ss_mashupShowBranding}">
              <jsp:include page="/WEB-INF/jsp/definition_elements/view_binder_branding.jsp" />
            </c:if>
        </td>
        <td height="24" colspan="2" class="ss_mastheadtoplinks ss_masthead_portals" >
          <c:if test="${empty ssStandAlone || !ssStandAlone}">
        	  <ssHelpSpot helpId="navigation_bar/my_portal_button" offsetY="10" offsetX="0" 
			      title="<ssf:nlt tag="helpSpot.myPortalButton" text="My Portal"/>">
			  </ssHelpSpot>
	          <a href="${ss_portalUrl}" 
	            onClick="ss_workarea_showPortal${renderResponse.namespace}(this);return false;"
	            title="<ssf:nlt tag="navigation.goToPortalView"/>"
	          ><ssf:nlt tag="navigation.portalView"/></a> | 
	          <a
	 			  <c:if test="${empty ssBinder}">
	 			    href=""
	 			  </c:if>
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
		  </c:if>
              <c:if test='<%= !com.sitescape.team.util.SPropsUtil.getBoolean("form.login.auth.disallowed",false) %>' >
              <c:if test="${empty ssStandAlone || !ssStandAlone}"> | </c:if>
                <ssf:ifLoggedIn>
  				  	  <a href="${ss_logoutUrl}"><span><ssf:nlt tag="logout"/></span></a>
			    </ssf:ifLoggedIn>
			    <ssf:ifNotLoggedIn>
				    <form method="post" id="ss_loginForm${renderResponse.namespace}" 
				      action="" style="display:inline;"><a href="${ss_loginUrl}"
				      onClick="return(ss_requestLogin(this, '${ssBinder.id}', '${ssUser.workspaceId}', '${ssUser.name}'));"
				    ><span><ssf:nlt tag="login"/></span></a><input type="hidden" name="url" /></form>
			    </ssf:ifNotLoggedIn>
			  </c:if>
			  </td>
        <td width="6%" height="24" class="ss_workspace">&nbsp;</td>
        <td width="3%" height="24" class="ss_workspace" colspan="2">
        	
			<table cellspacing="0" cellpadding="0" border="0"><tbody><tr>
			<td valign="middle" style="height: 22px; white-space: nowrap;">
			<a href="javascript: window.print();"><img border="0" 
    		class="ss_print_button"
    		alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
    		src="<html:rootPath/>images/pics/masthead/masthead_printer.png" width="23" height="21" align="right" /></a></td>
			<td><a href="javascript: ss_helpSystem.run();"><img border="0" style="margin-left: 10px; margin-right: 10px;" title="<ssf:nlt tag="navigation.help"/>"
    		<ssf:alt tag="navigation.help"/> src="<html:rootPath/>images/pics/masthead/mastheadHelp.png" width="19" height="21" hspace="2" /></a></td>
			</tr></tbody></table><!-- boulder: print button and help button no ka and boulder is png change h & w on printer button -->
			
        </td>
      </tr>
    <c:if test="${!ss_mashupHideMasthead || ss_mashupShowBranding}">
      <tr>
        <td width="20%" height="19" class="ss_mastheadtoplinks ss_masthead_favorites">
          <c:if test="${!ss_mashupHideMasthead}">
        	<!-- Begin Favorites -->  
        	<div id="ss_navbar_favorites${renderResponse.namespace}" style="display:inline;"></div>
		      <ssHelpSpot helpId="navigation_bar/favorites_button" offsetX="0" offsetY="10"  
		          title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
			  </ssHelpSpot>
        	<a title="<ssf:nlt tag="navigation.favorites"/>"
	  			href="javascript: ;" 
	  			onClick="ssMyTeams${renderResponse.namespace}.hide();ssMyFavorites${renderResponse.namespace}.showFavoritesPane();"
              ><ssf:nlt tag="navigation.favorites"/> <img border="0" 
              src="<html:imagesPath/>pics/menudown.gif" style="padding-left: 2px;"/> </a>
		      &nbsp;|
		      
		    <!-- Begin Teams -->   
        	<div id="ss_navbar_myteams${renderResponse.namespace}" style="display:inline;">
		      <ssHelpSpot helpId="navigation_bar/my_teams" offsetX="0" offsetY="0"  
		          title="<ssf:nlt tag="helpSpot.myTeamsButton"/>">
		      </ssHelpSpot></div> 
        	<a title="<ssf:nlt tag="navigation.myTeams"/>"
			  href="javascript:;" onClick="ssMyFavorites${renderResponse.namespace}.hideFavoritesPane();ssMyTeams${renderResponse.namespace}.show();"
				    ><ssf:nlt tag="navigation.myTeams"/><img border="0" 
              src="<html:imagesPath/>pics/menudown.gif" style="padding-left: 2px;"/> 
		      </a>  
			    <div id="ss_navbarMyTeamsButton${renderResponse.namespace}" style="display:none;">
			    </div>
		  </c:if>
		</td>
        <td height="19" colspan="4" valign="top" align="right" >
        <c:if test="${!ss_mashupHideMasthead || ss_mashupShowBranding}">
        <span class="ss_mastheadName">
        <a title="<ssf:nlt tag="navigation.myWorkspace"/>"
				  href="<ssf:url 
				    windowState="${ss_urlWindowState}"
			      	action="view_ws_listing"
			      	binderId="${ssUser.workspaceId}"/>"
              	>${ssUser.title}</a></span>
        </c:if>
        </td>
	  </tr>
	</c:if>
      <tr>
        <td colspan="5">&nbsp;</td>
      </tr>
      </tbody>
    </table>
   </td>
  </tr>
<c:if test="${!ss_mashupHideMasthead}">
  <tr>
    <td class="ss_search_bar"> <!-- Sets background for search area table kablink = #449EFF kablink blue = #6BC5CE -->
    <!-- Start of search area with find boxes -->
    <!-- Beginning of Search Buttons -->
    <table align="center" border="0" cellpadding="0" cellspacing="0" width="88%">
       <tbody>
         <tr>
          
          <td width="34%">
			<table border="0" cellpadding="0" cellspacing="0"  align="right">
			<tbody>
			  <tr>
				<td rowspan="2">
			            <div class="ss_search_title" align="right" style=""><ssf:nlt tag="navigation.search"/></div>
			    </td>
				<td>
					<ssf:ifnotaccessible>
						<div class="ss_searchtext">		  
						<a 
							href="<ssf:url windowState="maximized" actionUrl="true"><ssf:param 
							name="action" value="advanced_search"/><ssf:param 
							name="binderId" value="${ssBinder.id}"/><ssf:param 
							name="tabTitle" value="SEARCH FORM"/><ssf:param 
							name="newTab" value="0"/></ssf:url>"
							><ssf:nlt tag="navigation.search.advanced"/>
						</a>
					</div>
					</ssf:ifnotaccessible>
				
					<ssf:ifaccessible>
						<div class="ss_searchtext">
						
							<a class="ss_advanced ss_fineprint" 
								href="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
							  	name="binderId" value="${ssBinder.id}"/><ssf:param 
							  	name="tabTitle" value="SEARCH FORM"/><ssf:param 
							  	name="newTab" value="0"/></ssf:url>"
								><ssf:nlt tag="navigation.search.advanced"/>
							</a>
						</div>
					</ssf:ifaccessible>
				</td>
				<td>
					&nbsp;
				</td>
				<td>
					&nbsp;
				</td>
			  </tr>
			  <tr>
				<td colspan="3">
					<div width="100%" class="ss_nowrap">
				
					<ssf:ifnotaccessible>
				
					  <div class="ss_global_toolbar_search" id="ss_navbarSearchButton${renderResponse.namespace}" 
						  		onMouseOver="this.style.cursor = 'pointer';">
				
						  <form class="ss_form" method="post" id="ss_simpleSearchForm${renderResponse.namespace}" 
							  	name="ss_simpleSearchForm${renderResponse.namespace}" 
							  	style="display:inline;"
							  	action="<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
							  	name="newTab" value="1"/></ssf:url>">
							  			
						  <ssHelpSpot helpId="navigation_bar/search_button" offsetY="0" 
					                offsetX="0" 
								    title="<ssf:nlt tag="helpSpot.searchButton"/>">
								
							<input name="searchText" class="ss_combobox_search" type="text" /> 
										
							<a href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" >
								
								<img title="<ssf:nlt tag="alt.search"/>"
								<ssf:alt tag="alt.search"/> src="<html:rootPath/>images/pics/masthead/search.png" width="19" height="20" border="0" align="absmiddle" />
							</a><!-- kablink: sarch_ka.png -->
									<input type="hidden" name="searchBtn" value="searchBtn"/>
									<input type="hidden" name="quickSearch" value="true"/>					
									<input type="hidden" name="operation" value="ss_searchResults"/>
						  </ssHelpSpot>
								 
							<a class="ss_savedQueries" alt="<ssf:nlt tag="searchResult.savedSearchTitle"/>" 
									title="<ssf:nlt tag="searchResult.savedSearchTitle"/>" href="javascript: // ;" 
									onclick="ss_showSavedQueriesList(this, 'ss_navbarPopupPane${renderResponse.namespace}',
									'<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
									 name="newTab" value="1"/></ssf:url>');">
									 <img border="0" src="<html:imagesPath/>pics/menudown.gif"/>
							</a>
						  <div id="ss_navbarPopupPane${renderResponse.namespace}" class="ss_navbarPopupPane"></div>
									
						  </form>
					  </div>
				  </ssf:ifnotaccessible>
						
				  <ssf:ifaccessible>
							
					<div class="ss_global_toolbar_search"  id="ss_navbarSearchButton${renderResponse.namespace}" >
						  <ssHelpSpot helpId="navigation_bar/search_button" offsetY="15" 
				                offsetX="0" 
						    	title="<ssf:nlt tag="helpSpot.searchButton"/>">
						  </ssHelpSpot>
				     	
				     	<form class="ss_form" method="post" id="ss_simpleSearchForm${renderResponse.namespace}" 
						  		name="ss_simpleSearchForm${renderResponse.namespace}" 
						  		style="display:inline;"
						  		action="<ssf:url windowState="maximized" actionUrl="true"><ssf:param 
						  		name="action" value="advanced_search"/><ssf:param 
						  		name="newTab" value="1"/></ssf:url>">
									
								<input name="searchText" style="width: 100px;" type="text" id="ss_searchSearchText${renderResponse.namespace}" /> 
								<a href="javascript: document.ss_simpleSearchForm${renderResponse.namespace}.submit();" >
									<img src="<html:rootPath/>images/pics/masthead/search.png" width="19" height="20" border="0" align="absmiddle"
									title="<ssf:nlt tag="alt.search"/>"<ssf:alt tag="alt.search"/> /></a>
									<input type="hidden" name="searchBtn" value="searchBtn"/>
									<input type="hidden" name="quickSearch" value="true"/>					
									<input type="hidden" name="operation" value="ss_searchResults"/>
						</form><!-- kablink: search_ka.png -->
				     </div>    
				  </ssf:ifaccessible>
				  </div>
				</td>
			  </tr>
			</tbody>
			</table>
       </td>
       <!-- Beginning of Find Bar:  Find People/Places/Tags  --> 
       <td width="2%">
          <div align="center">
             	<img src="<html:rootPath/>images/pics/masthead/whitepixel.jpg" width="1" height="30" hspace="6"/>
             </div>
       </td>
       <td width="25%">
	       <table align="center">
	       <tbody>
	       <tr>
		       <td>
		            	<div  class="ss_search_title" >
		            	<ssf:nlt tag="navigation.find"/>
		            	</div>
		       </td>
		       <td>
		       <!-- Find People --> 
		        <div valign="middle">     
		        		<div class="ss_searchtext">
		                  	<ssf:ifnotaccessible>
		                  	
								<span style="padding-left:5px;"><ssf:nlt tag="navigation.findUser"/></span>
								<div class="ss_global_toolbar_search" id="ss_navbarFindUserButton${renderResponse.namespace}" onMouseOver="this.style.cursor = 'pointer';">
									
									<form method="post" id="ss_findUserForm${renderResponse.namespace}" 
					  					name="ss_findUserForm${renderResponse.namespace}" 
					  					style="display:inline;"
					  					action="<ssf:url action="findUser" actionUrl="true"/>">
						  				<ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
						    			formElement="searchText" 
						    			type="user"
						    			singleItem="true"/> 
									</form>
		
								</div>	
							</ssf:ifnotaccessible>
		
		               		<ssf:ifaccessible>
			  					<span style="padding-left:5px;">
			  					<label for="ss_navbarFindPlacesButton${renderResponse.namespace}"><ssf:nlt tag="navigation.findUser"/></label>
			  					</span>
			  						<form method="post" id="ss_findUserForm${renderResponse.namespace}" 
					  					name="ss_findUserForm${renderResponse.namespace}" 
					  					style="display:inline;"
					  					action="<ssf:url action="findUser" actionUrl="true"/>">
						  				<ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
						    			formElement="searchText" 
						    			type="user"
						    			width="70px"
						    			singleItem="true"/> 
									</form>
				 			</ssf:ifaccessible>
						</div>		
					</div>				
					</td>
		            <td>
		            <!-- Find Place -->
	            	<ssf:ifnotaccessible>
	            	<div class="ss_searchtext">
						<span style="padding-left:5px;"><ssf:nlt tag="navigation.findPlace"/></span>
							<div class="ss_global_toolbar_search" id="ss_navbarFindPlacesButton${renderResponse.namespace}" 
								onMouseOver="this.style.cursor = 'pointer';">
							<form method="post" id="ss_findPlacesForm${renderResponse.namespace}" 
				  				name="ss_findPlacesForm${renderResponse.namespace}" 
				  				style="display:inline;"
				  				action="<ssf:url action="findUser" actionUrl="true"/>">
					  			<ssf:find 
					    		formName="ss_findPlacesForm${renderResponse.namespace}" 
					    		formElement="searchText" 
					    		type="places"
					    		singleItem="true"/> 
							</form>
						</div>
					</div>
					</ssf:ifnotaccessible>
					<ssf:ifaccessible>
					<div class="ss_searchtext">
			  			<span style="padding-left:5px;">
			  			<label for="ss_navbarFindPlacesButton${renderResponse.namespace}"><ssf:nlt tag="navigation.findPlace"/></label>
			  			</span>
						<form method="post" id="ss_findPlacesForm${renderResponse.namespace}" 
					  			name="ss_findPlacesForm${renderResponse.namespace}" 
					  			style="display:inline;"
					  			action="<ssf:url windowState="maximized" action="findUser" actionUrl="true"/>">
						 		<ssf:find 
						    	formName="ss_findPlacesForm${renderResponse.namespace}" 
						    	formElement="searchText" 
						    	type="places"
						    	singleItem="true"> 
						  		<ssf:param name="label" useBody="true">
						    	<ssf:nlt tag="navigation.findPlace"/>
						  		</ssf:param>
						 	</ssf:find>
						</form>
				    </div>
					</ssf:ifaccessible>    
	           </td>     
	           <td>
	            <!-- Find Tags -->
	            <div class="ss_searchtext">                
	                  <ssf:ifnotaccessible>
						<span style="padding-left:5px;"><ssf:nlt tag="navigation.findTag"/></span>
						<div class="ss_global_toolbar_search" id="ss_navbarFindTagsButton${renderResponse.namespace}"
	     					onMouseOver="this.style.cursor = 'pointer';">
							<form method="post" id="ss_findTagsForm${renderResponse.namespace}" 
								name="ss_findTagsForm${renderResponse.namespace}" 
								style="display:inline;"
					  			action="<ssf:url action="findUser" actionUrl="true"/>">
						  		<ssf:find 
						    	formName="ss_findTagsForm${renderResponse.namespace}" 
						    	formElement="searchText" 
						    	type="tags"
						    	singleItem="true"/> 
							</form>
						</div>
	                  </ssf:ifnotaccessible>				
					  <ssf:ifaccessible> 
						<span style="padding-left:5px;">
							<label for="ss_navbarFindTagsButton${renderResponse.namespace}"><ssf:nlt tag="navigation.findTag"/></label>
						</span>
							<form method="post" id="ss_findTagsForm${renderResponse.namespace}" 
					  			name="ss_findTagsForm${renderResponse.namespace}" 
					  			style="display:inline;"
					  			action="<ssf:url windowState="maximized" action="findUser" actionUrl="true"/>">
						  		<ssf:find 
						    	formName="ss_findTagsForm${renderResponse.namespace}" 
						    	formElement="searchText" 
						    	type="tags"
						    	singleItem="true"> 
						  		<ssf:param name="label" useBody="true">
						    	<ssf:nlt tag="navigation.findTag"/>
						  		</ssf:param>
						  	</ssf:find>
						</form>
	                  </ssf:ifaccessible>
				</div>
	           </td>
              </tr>
	       </tbody>
	       </table>
       </td>  
       <td width="2%">
             <div align="center">
             	<img src="<html:rootPath/>images/pics/masthead/whitepixel.jpg" width="1" height="30" hspace="6"/>
             </div>
       </td>
       <td width="25%" class="ss_workspace">
            	<ssHelpSpot helpId="navigation_bar/my_workspace_button" offsetY="15" offsetX="0" 
			      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
			  	</ssHelpSpot>
              	<a title="<ssf:nlt tag="navigation.myWorkspace"/>"
				  href="<ssf:url 
				    windowState="${ss_urlWindowState}"
			      	action="view_ws_listing"
			      	binderId="${ssUser.workspaceId}"/>"
              	><img src="<html:rootPath/>images/pics/masthead/ss_banner_guy.png" 
              	  width="30" height="34" border="0" align="absmiddle" 
              	 />&nbsp;&nbsp;<ssf:nlt tag="navigation.myWorkspace"/></a> 
       </td>
          </tr><!-- kablink: ss_banner_guy_ka.gif -->
    </tbody>
    </table>
  <!-- End of Search/Find boxes -->
    </td>
  </tr>
</c:if>
</tbody>
</table>

<c:if test="${empty ss_navbarBottomSeen}">
  <c:set var="ss_navbarBottomSeen" value="1"/>
  <div id="ss_navbar_bottom${renderResponse.namespace}"></div>
</c:if>

<!-- Start of favorites pane -->
<ssf:ifLoggedIn>
<div class="ss_style_trans" id="ss_favorites_pane${renderResponse.namespace}" 
  style="position:absolute; visibility:hidden; display:none;">

  <ssf:popupPane width="250px" titleTag=""
    closeScript="ssMyFavorites${renderResponse.namespace}.hideFavoritesPane();return false;">

    <div style="padding: 2px 5px 2px 5px;">

	  <c:if test="${ssBinder != null && ssEntry.entityType != 'folderEntry'}">
	  	<div class="ss_style_trans">
	  	<img title="<ssf:alt tag="favorites.addCurrentPage"/>" 
	  	  src="<html:brandedImagesPath/>icons/button_new_bookmark.gif" border="0" />
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
  		<img title="<ssf:alt tag="favorites.edit"/>" 
  		  src="<html:brandedImagesPath/>icons/button_edit_bookmark.gif" border="0" />
		<a href="javascript: ;" 
		 onClick="ssMyFavorites${renderResponse.namespace}.showhideFavoritesEditor()"
		><span class="ss_bold ss_smallprint"><ssf:nlt tag="favorites.edit" text="Edit Favorites"/></span></a>
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
  </ssf:popupPane>

</div>
</ssf:ifLoggedIn>
<!-- End of favorites pane -->

<!-- Start of myteams pane -->
<ssf:ifLoggedIn>
<div class="ss_style_trans" id="ss_myteams_pane${renderResponse.namespace}" 
  style="position:absolute; visibility:hidden; display:none;">
	<ssf:popupPane width="200px" titleTag=""
	      closeScript="ssMyTeams${renderResponse.namespace}.hide();return false;">
	<div style="padding: 2px 5px 2px 5px;">
	  <div class="ss_style_trans" id="ss_myteams${renderResponse.namespace}" align="left">
		  <div style="float: right;" id="ss_myteams_loading${renderResponse.namespace}"><ssf:nlt tag="Loading"/></div>
		  <div id="ss_myteams_list${renderResponse.namespace}">&nbsp;</div>
	  </div>
	</div>
	</ssf:popupPane>
</div>
</ssf:ifLoggedIn>
<!-- End of myteams pane -->

<c:if test="${empty ssUser.displayStyle || ssUser.displayStyle == 'iframe' || (!empty ssFolderActionVerticalOverride && ssFolderActionVerticalOverride == 'yes')}" >
<!-- iframe div -->
<%@ include file="/WEB-INF/jsp/entry/view_iframe_div.jsp" %>
<!-- end of iframe div -->
</c:if>

<c:if test="${!ss_mashupHideMasthead}">
<div style="padding-bottom:0px;"></div>
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
<div style="padding-bottom:2px;"></div>
</c:if>
<div class="ss_clear_float"></div>
</div>
</c:if>
<script type="text/javascript">
ss_workarea_showPseudoPortal${renderResponse.namespace}()
</script>
