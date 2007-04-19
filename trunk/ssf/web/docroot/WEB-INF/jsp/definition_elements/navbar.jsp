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
<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
<script type="text/javascript" src="/ssf/js/tree/tree_widget.js"></script>
<c:if test="${ssUserProperties.debugMode}">
<!-- Start of debug window -->
  <div style="border:1px solid black;">
  <div style="background-color:#CECECE; border-bottom:1px solid black; width:100%;">
    <table cellspacing="0" cellpadding="0" style="background-color:#CECECE; width:100%;">
    <tr>
    <td>Debug window</td>
    <td align="right">
      <a href="" onClick="ss_turnOffDebugMode();return false;">
        <img border="0" src="<html:imagesPath/>pics/sym_s_delete.gif">
      </a>
<script type="text/javascript">
function ss_turnOffDebugMode() {
	var url = self.location.href + "&enableDebug=off"
	self.location.href = url;
}
var ss_debugTextareaId = "debugTextarea<portlet:namespace/>"

</script>
    </td>
    </tr>
    </table>
  </div>
  <div>
  <textarea id="debugTextarea<portlet:namespace/>" style="width:100%;" rows="6"></textarea>
  </div>
  </div>
  <br/>
<!-- End of debug window -->
</c:if>
<script type="text/javascript">

// global variable for tag search
var ss_tagSearchResultUrl = "<portlet:actionURL windowState="maximized" portletMode="view">
			<portlet:param name="action" value="search"/>
			<portlet:param name="searchCommunityTags" value="ss_tagPlaceHolder"/>
			<portlet:param name="searchPersonalTags" value="ss_tagPlaceHolder"/>
			<portlet:param name="searchTags" value="searchTagsOr"/>
			<portlet:param name="tabTitle" value="ss_tagPlaceHolder"/>
			<portlet:param name="newTab" value="1"/>
			</portlet:actionURL>";
	
</script>

<!-- Start of global toolbar -- MAXIMIZED PORTAL WINDOW MODE -->
<c:if test="${ss_navbar_style != 'portlet'}">
<div class="ss_global_toolbar ss_global_toolbar_maximized">

	<!-- My workspace -->
	<div class="ss_global_toolbar_myworkspace" 
      onClick="self.location.href='<portlet:renderURL 
      	windowState="maximized"><portlet:param 
      	name="action" value="view_ws_listing"/><portlet:param 
      	name="binderId" value="${ssUser.parentBinder.id}"/><portlet:param 
      	name="entryId" value="${ssUser.id}"/><portlet:param 
      	name="newTab" value="1"/></portlet:renderURL>';"
     onMouseOver="this.style.cursor = 'pointer';"
    >
	  <ssHelpSpot helpId="personal_toolbar/my_workspace_button" offsetY="8"
	      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
	    <div id="ss_navbarMyWorkspaceButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.myWorkspace"/></span>
	    </div>
	  </ssHelpSpot>
	</div>
	
	
	<div class="ss_global_toolbar_favs" onClick="ss_showFavoritesPane('<portlet:namespace/>');"
      onMouseOver="this.style.cursor = 'pointer';"
    >
      <ssHelpSpot helpId="personal_toolbar/favorites_button" offsetX="-12" offsetY="8"  
          title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
	    <div id="ss_navbarFavoritesButton<portlet:namespace/>">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.favorites"/></span>
	    </div>
	    <div id="ss_navbar_favorites<portlet:namespace/>" style="visibility:hidden;margin:0px;padding:0px;clear:both;"
	    ><img border="0" src="<html:imagesPath/>pics/1pix.gif"></div>
	  </ssHelpSpot>
	</div>
	
	<div class="ss_global_toolbar_divider"></div>

	<div class="ss_global_toolbar_quick">
		<div >
			<span class="ss_global_toolbar_label_text_quickSearch"><ssf:nlt tag="navigation.search"/></span>
			<span class="ss_global_toolbar_quick_advanced"><a class="ss_advanced" href="<portlet:actionURL windowState="maximized" portletMode="view">
				<portlet:param name="action" value="advanced_search"/>
				<portlet:param name="tabTitle" value="SEARCH FORM"/>
				<portlet:param name="newTab" value="0"/>
				</portlet:actionURL>"
				><ssf:nlt tag="navigation.search.advanced"/></a></span>
		</div>
		<div class="ss_global_toolbar_search"  id="ss_navbarSearchButton<portlet:namespace/>" onMouseOver="this.style.cursor = 'pointer';">
     		<form method="post" id="ss_simpleSearchForm<portlet:namespace/>" 
		  		name="ss_simpleSearchForm<portlet:namespace/>" 
		  		action="<portlet:actionURL windowState="maximized">
						<portlet:param name="action" value="advanced_search"/>
						<portlet:param name="newTab" value="1"/>
						</portlet:actionURL>">
			  <ssHelpSpot helpId="personal_toolbar/search_button" offsetX="65" 
			    title="<ssf:nlt tag="helpSpot.searchButton"/>">
					<input name="searchText" type="text" /> 
					<a class="ss_searchButton" href="javascript: document.ss_simpleSearchForm<portlet:namespace/>.submit();" ><img src="<html:imagesPath/>pics/1pix.gif" /></a>
					<input type="hidden" name="searchBtn" value="searchBtn"/>
					<input type="hidden" name="quickSearch" value="true"/>					
					<input type="hidden" name="operation" value="ss_searchResults"/>
			  </ssHelpSpot>
			</form>
     	</div>
	</div>

	<div class="ss_global_toolbar_divider"></div>
	
	<div class="ss_global_toolbar_findUser"  id="ss_navbarFindUserButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';">
		<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findUser"/></span>
		<div>
			<form method="post" id="ss_findUserForm<portlet:namespace/>" name="ss_findUserForm<portlet:namespace/>" 
			  action="<portlet:actionURL windowState="maximized">
				<portlet:param name="action" value="findUser"/>
				</portlet:actionURL>">
			  <ssHelpSpot helpId="personal_toolbar/findUser_button" offsetX="26" 
			    title="<ssf:nlt tag="helpSpot.findUserButton"/>">
				  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="user"
				    width="90px" singleItem="true"/> 
			  </ssHelpSpot>
			</form>
		</div>
	</div>
	<div class="ss_global_toolbar_findUser"  id="ss_navbarFindPlacesButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';">
		<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findPlace"/></span>
		<div>
			<form method="post" id="ss_findPlacesForm<portlet:namespace/>" name="ss_findPlacesForm<portlet:namespace/>" 
			  action="<portlet:actionURL windowState="maximized"><portlet:param 
			  	name="action" value="findUser"/></portlet:actionURL>">
			  <ssHelpSpot helpId="personal_toolbar/findPlaces_button" offsetX="24" 
			    title="<ssf:nlt tag="helpSpot.findPlacesButton"/>">
				  <ssf:find 
				    formName="ss_findPlacesForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="places"
				    width="90px" singleItem="true"/> 
			  </ssHelpSpot>
			</form>
		</div>
	</div>
	
	
	<div class="ss_global_toolbar_findUser" id="ss_navbarFindTagsButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';">
		<span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findTag"/></span>
		<div>
			<ssHelpSpot helpId="personal_toolbar/findTags_button" offsetX="30" offsetY="-1" 
			    title="<ssf:nlt tag="helpSpot.findTagsButton"/>">
			<form method="post" id="ss_findTagsForm<portlet:namespace/>" name="ss_findTagsForm<portlet:namespace/>" 
			  action="<portlet:actionURL windowState="maximized"><portlet:param 
			  	name="action" value="findUser"/></portlet:actionURL>">
				  <ssf:find 
				    formName="ss_findTagsForm${renderResponse.namespace}" 
				    formElement="searchText" 
				    type="tags"
				    width="90px" singleItem="true"/> 
			</form>
			</ssHelpSpot>
		
		</div>
	</div>
	
	<div class="ss_global_toolbar_divider"></div>
	
	<div class="ss_global_toolbar_help" onClick="ss_helpSystem.run();return false;"
      onMouseOver="this.style.cursor = 'pointer';">
		<ssHelpSpot helpId="personal_toolbar/help_button" offsetX="-22" offsetY="6"
		      title="<ssf:nlt tag="helpSpot.helpButton"/>">
	        <img src="<html:imagesPath/>pics/1pix.gif" />
		    <div id="ss_navbarHelpButton">
		      <span class="ss_fineprint"><ssf:nlt tag="navigation.help" text="Help"/></span>
		    </div>
		</ssHelpSpot>
	</div>

</div>
<div class="ss_clear"></div>
</c:if>
<!-- Start of global toolbar -- PORTLET WINDOW MODE -->
<c:if test="${ss_navbar_style == 'portlet'}">
<div class="ss_global_toolbar ss_global_toolbar_in_portlet">
<table width="100%" cellpadding="1" cellspacing="0" border="0">
<tr class="ss_row_txt">
  <td width="75" rowspan="4"><!-- My workspace -->
	<div class="ss_global_toolbar_myworkspace_big" 
      onClick="self.location.href='<portlet:renderURL 
      	windowState="maximized"><portlet:param 
      	name="action" value="view_ws_listing"/><portlet:param 
      	name="binderId" value="${ssUser.parentBinder.id}"/><portlet:param 
      	name="entryId" value="${ssUser.id}"/><portlet:param 
      	name="newTab" value="1"/></portlet:renderURL>';"
     onMouseOver="this.style.cursor = 'pointer';"
    >
	  <ssHelpSpot helpId="personal_toolbar/my_workspace_button" offsetY="25" offsetX="5" 
	      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
	    <div id="ss_navbarMyWorkspaceButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.myWorkspace"/></span>
	    </div>
	  </ssHelpSpot>
	</div>
  </td>
  <td width="75" rowspan="4"><!-- Favorites -->
    <div class="ss_global_toolbar_favs_big" onClick="ss_showFavoritesPane('<portlet:namespace/>');"
      onMouseOver="this.style.cursor = 'pointer';"
    >
      <ssHelpSpot helpId="personal_toolbar/favorites_button"  offsetY="25" offsetX="5"  
          title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
	    <div id="ss_navbarFavoritesButton<portlet:namespace/>">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.favorites"/></span>
	    </div>
	    <div id="ss_navbar_favorites<portlet:namespace/>" style="visibility:hidden;margin:0px;padding:0px;"
	    ><img border="0" src="<html:imagesPath/>pics/1pix.gif"></div>
	  </ssHelpSpot>
	</div>
  </td>
  <td class="ss_global_toolbar_portlet_box" colspan="3"><span class="ss_global_toolbar_label_text_quickSearch"><ssf:nlt tag="navigation.search"/></span></td>
  <td rowspan="4" width="100%"></td>
  <td valign="top" rowspan="4"><!-- Help button -->
	<div class="ss_global_toolbar_help"  onClick="ss_helpSystem.run();return false;"
      onMouseOver="this.style.cursor = 'pointer';">
	  <ssHelpSpot helpId="personal_toolbar/help_button" offsetX="-22" offsetY="6"
	      title="<ssf:nlt tag="helpSpot.helpButton"/>">
        <img src="<html:imagesPath/>pics/1pix.gif" />
	    <div id="ss_navbarHelpButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.help" text="Help"/></span>
	    </div>
	  </ssHelpSpot>
	</div>
  </td>
</tr>
<tr>
  <td class="ss_global_toolbar_portlet_box" colspan="3"><!-- Search form -->
	<div class="ss_global_toolbar_search"  id="ss_navbarSearchButton<portlet:namespace/>">
		<form method="post" id="ss_simpleSearchForm<portlet:namespace/>" 
		  name="ss_simpleSearchForm<portlet:namespace/>" 
		  action="<portlet:actionURL windowState="maximized">
			<portlet:param name="action" value="advanced_search"/>
			<portlet:param name="newTab" value="1"/>
			</portlet:actionURL>" style="display: inline;">
		  <ssHelpSpot helpId="personal_toolbar/search_button" offsetX="47" 
		    title="<ssf:nlt tag="helpSpot.searchButton"/>">
			<input name="searchText" type="text" />
			<input type="hidden" name="quickSearch" value="true"/>					
			<input type="hidden" name="operation" value="ss_searchResults"/>
			  
			  <a class="ss_searchButton" href="javascript: document.ss_simpleSearchForm<portlet:namespace/>.submit();" ><img src="<html:imagesPath/>pics/1pix.gif" /></a>
		    <input type="hidden" name="searchBtn" value="searchBtn"/>
		  </ssHelpSpot>
		</form>
		
		<a class="ss_advanced" href="<portlet:actionURL windowState="maximized" portletMode="view">
			<portlet:param name="action" value="advanced_search"/>
			<portlet:param name="tabTitle" value="SEARCH FORM"/>
			<portlet:param name="newTab" value="0"/>
			</portlet:actionURL>"
			><ssf:nlt tag="navigation.search.advanced"/></a>
	</div>
  </td>
</tr>
<tr class="ss_row_txt">
  <td class="ss_global_toolbar_portlet_box" width="75px"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findUser"/></span></td>
  <td class="ss_global_toolbar_portlet_box" width="75px"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findPlace"/></span></td>
  <td class="ss_global_toolbar_portlet_box" width="75px"><span class="ss_global_toolbar_label_text"><ssf:nlt tag="navigation.findTag"/></span></td>
</tr>
<tr class="ss_row_last">
  <td class="ss_global_toolbar_portlet_box" align="left" valign="top"><!-- Find people-->
	<div class="ss_global_toolbar_findUser"  id="ss_navbarFindUserButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';">
		<form method="post" id="ss_findUserForm<portlet:namespace/>" name="ss_findUserForm<portlet:namespace/>" 
		  action="<portlet:actionURL windowState="maximized">
			<portlet:param name="action" value="findUser"/>
			</portlet:actionURL>">
		  <ssHelpSpot helpId="personal_toolbar/findUser_button" offsetX="26" 
		    title="<ssf:nlt tag="helpSpot.findUserButton"/>">
			  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="user"
			    width="70px" singleItem="true"/> 
		  </ssHelpSpot>
		</form>
	</div>
  </td>
  <td class="ss_global_toolbar_portlet_box" align="left" valign="top"><!-- Find places form 
    --><div class="ss_global_toolbar_findUser"  id="ss_navbarFindPlacesButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';">
		<form method="post" id="ss_findPlacesForm<portlet:namespace/>" name="ss_findPlacesForm<portlet:namespace/>" 
		  action="<portlet:actionURL windowState="maximized"><portlet:param 
		  	name="action" value="findUser"/></portlet:actionURL>">
		  <ssHelpSpot helpId="personal_toolbar/findPlaces_button" offsetX="24" 
		    title="<ssf:nlt tag="helpSpot.findPlacesButton"/>">
			  <ssf:find 
			    formName="ss_findPlacesForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="places"
			    width="70px" singleItem="true"/> 
		  </ssHelpSpot>
		</form>
	</div>
  </td>
  <td class="ss_global_toolbar_portlet_box" align="left" valign="top"><!-- Find tags form 
   --><div class="ss_global_toolbar_findUser" id="ss_navbarFindTagsButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';">
		<ssHelpSpot helpId="personal_toolbar/findTags_button" offsetX="30" offsetY="-1" 
		    title="<ssf:nlt tag="helpSpot.findTagsButton"/>">
		<form method="post" id="ss_findTagsForm<portlet:namespace/>" name="ss_findTagsForm<portlet:namespace/>" 
		  action="<portlet:actionURL windowState="maximized"><portlet:param 
		  	name="action" value="findUser"/></portlet:actionURL>">
			  <ssf:find 
			    formName="ss_findTagsForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="tags"
			    width="70px" singleItem="true"/> 
		</form>
		</ssHelpSpot>
	</div>
  </td>
</tr>
</table>
</div>
</c:if>
<c:if test="${empty ss_navbarBottomSeen}">
<c:set var="ss_navbarBottomSeen" value="1"/>

<script type="text/javascript">
var ss_addFavoriteBinderUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="add_favorite_binder" />
	<ssf:param name="binderId" value="${ssBinder.id}" />
	<ssf:param name="viewAction" value="${action}" />
	</ssf:url>"

var ss_addFavoritesCategoryUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="add_favorites_category" />
	</ssf:url>";

var ss_saveFavoritesUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="save_favorites" />
	</ssf:url>";

var ss_getFavoritesTreeUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="get_favorites_tree" />
	</ssf:url>";

var ss_treeShowIdUrl = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="ssActionPlaceHolder"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
		name="newTab" value="1"/></portlet:renderURL>";

</script>

<div id="ss_navbar_bottom<portlet:namespace/>"></div>

</c:if>

<!-- Start of favorites pane -->
<div class="ss_style_trans" id="ss_favorites_pane<portlet:namespace/>" 
  style="position:absolute; visibility:hidden;">

<ssf:popupPane width="250px" titleTag="favorites"
      closeScript="ss_hideFavoritesPane('${renderResponse.namespace}');return false;">



<div style="padding: 5px 10px 5px 10px;">
  <div class="ss_style_trans">
		<a href="javascript: ;" 
		 onClick="ss_addBinderToFavorites('<portlet:namespace/>');return false;"
		><img src="<html:imagesPath/>icons/button_new_bookmark.gif" />
		<span class="ss_bold ss_smallprint"><ssf:nlt tag="favorites.addCurrentPage" 
			text="Add the current page to the favorites list..."/></span></a>
  </div>
  <div class="ss_style_trans">
		<a href="javascript: ;" 
		 onClick="ss_showhideFavoritesEditor('<portlet:namespace/>')"
		><img src="<html:imagesPath/>icons/button_edit_bookmark.gif" />
		<span class="ss_bold ss_smallprint"><ssf:nlt tag="favorites.edit" 
			text="Edit Favorites"/></span></a>

  </div>
  <hr style="width: 90%" class="ss_att_divider"/>
  <div class="ss_style_trans" id="ss_favorites<portlet:namespace/>" align="left">
	  <div style="float: right;" id="ss_favorites_loading<portlet:namespace/>"><ssf:nlt tag="Loading"/></div>
	  <div id="ss_favorites_list<portlet:namespace/>"></div>
  </div>
  <div class="ss_style_trans" style="display: none;" id="ss_favorites_editor<portlet:namespace/>">
     <div style="padding: 10px 0px 7px 0px;">

	<table cellspacing="0" cellpadding="0" border="0"><tbody><tr>
	  <td>
		<a class="ss_inlineButton" onClick="ss_moveSelectedFavorites('<portlet:namespace/>', 'down')"
		><img src="<html:imagesPath/>icons/button_move_down.gif" /><span><ssf:nlt tag="favorites.movedown"/></span></a>
  	  </td>	
	  <td>		
		<a class="ss_inlineButton" onClick="ss_moveSelectedFavorites('<portlet:namespace/>', 'up')"
		><img src="<html:imagesPath/>icons/button_move_up.gif" /><span><ssf:nlt tag="favorites.moveup"/></span></a>
  	  </td>
	  <td>
		
		<a class="ss_inlineButton" onClick="ss_deleteSelectedFavorites('<portlet:namespace/>')"
		><img src="<html:imagesPath/>icons/button_delete.gif" /><span><ssf:nlt tag="favorites.delete"/></span></a>
	  </td>
	 </tr></tbody></table>	
     </div>
     <div style="padding: 3px 0px 0px 135px; width: 40px;">
		<a class="ss_inlineButton" href="javascript: ;" 
		 onClick="ss_saveFavorites('<portlet:namespace/>')"
		><span><ssf:nlt tag="button.ok"/></span></a>
	 </div>

  </div>

  </div>
  </div>

</ssf:popupPane>

</div>
<!-- End of favorites pane -->
