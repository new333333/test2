<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
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

<!-- Start of global toolbar -->
<div class="ss_global_toolbar">
  <ul class="ss_global_toolbar_links ss_font-x-small" style="float:left;">

<!-- Find user form -->
	<li class="ss_global_toolbar_findUser"  id="ss_navbarFindUserButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';"
	>
		<form method="post" id="ss_findUserForm<portlet:namespace/>" name="ss_findUserForm<portlet:namespace/>" 
		  action="<portlet:actionURL windowState="maximized">
			<portlet:param name="action" value="findUser"/>
			</portlet:actionURL>">
		  <ssHelpSpot helpId="personal_toolbar/findUser_button" offsetX="40" offsetY="10"
		    title="<ssf:nlt tag="helpSpot.findUserButton"/>">
			  <ssf:find formName="ss_findUserForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="user"
			    width="70px" singleItem="true"/> 
		      <div class="ss_global_toolbar_findUser_text">
		        <span class="ss_fineprint"><ssf:nlt tag="navigation.findUser"/></span>
		      </div>
		  </ssHelpSpot>
		</form>
	</li>

<!-- Find places form -->
	<li class="ss_global_toolbar_findUser"  id="ss_navbarFindPlacesButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';"
	>
		<form method="post" id="ss_findPlacesForm<portlet:namespace/>" name="ss_findPlacesForm<portlet:namespace/>" 
		  action="<portlet:actionURL windowState="maximized"><portlet:param 
		  	name="action" value="findUser"/></portlet:actionURL>">
		  <ssHelpSpot helpId="personal_toolbar/findPlaces_button" offsetX="40" offsetY="10"
		    title="<ssf:nlt tag="helpSpot.findPlacesButton"/>">
			  <ssf:find 
			    formName="ss_findPlacesForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="places"
			    width="70px" singleItem="true"/> 
		      <div class="ss_global_toolbar_findUser_text">
		        <span class="ss_fineprint"><ssf:nlt tag="navigation.findPlace"/></span>
		      </div>
		  </ssHelpSpot>
		</form>
	</li>

<!-- Find tags form -->
	<li class="ss_global_toolbar_findUser"  id="ss_navbarFindTagsButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';"
	>
		<form method="post" id="ss_findTagsForm<portlet:namespace/>" name="ss_findTagsForm<portlet:namespace/>" 
		  action="<portlet:actionURL windowState="maximized"><portlet:param 
		  	name="action" value="findUser"/></portlet:actionURL>">
		  <ssHelpSpot helpId="personal_toolbar/findPlaces_button" offsetX="40" offsetY="10"
		    title="<ssf:nlt tag="helpSpot.findTagsButton"/>">
			  <ssf:find 
			    formName="ss_findTagsForm${renderResponse.namespace}" 
			    formElement="searchText" 
			    type="tags"
			    width="70px" singleItem="true"/> 
		      <div class="ss_global_toolbar_findUser_text">
		        <span class="ss_fineprint"><ssf:nlt tag="navigation.findTag"/></span>
		      </div>
		  </ssHelpSpot>
		</form>
	</li>

<!-- Search form -->
	<li class="ss_global_toolbar_search"  id="ss_navbarSearchButton<portlet:namespace/>"
     onMouseOver="this.style.cursor = 'pointer';"
	>
		<form method="post" id="ss_simpleSearchForm<portlet:namespace/>" 
		  name="ss_simpleSearchForm<portlet:namespace/>" 
		  action="<portlet:actionURL windowState="maximized">
			<portlet:param name="action" value="search"/>
			<portlet:param name="newTab" value="1"/>
			</portlet:actionURL>">
		  <ssHelpSpot helpId="personal_toolbar/search_button" offsetX="40" offsetY="10"
		    title="<ssf:nlt tag="helpSpot.searchButton"/>">
			  <input name="searchText" type="text" class="form-text" /> 
			  <a class="ss_linkButton ss_smallprint" href="javascript: ;" 
			    onClick="document.ss_simpleSearchForm<portlet:namespace/>.submit();return false;"
			  ><ssf:nlt tag="button.go"/></a>
			    <input type="hidden" name="searchBtn" value="searchBtn"/>
		      <div class="ss_global_toolbar_search_text">
		        <span class="ss_fineprint"><ssf:nlt tag="navigation.search"/></span>
		      </div>
		  </ssHelpSpot>
		</form>
	</li>

<c:if test="${ss_navbar_style == 'portlet'}">
  </ul>
</div>
<div class="ss_global_toolbar">
  <ul class="ss_global_toolbar_links ss_font-x-small" style="float:left;">
</c:if>

<!-- Favorites -->
    <li class="ss_global_toolbar_favs" onClick="ss_showFavoritesPane('<portlet:namespace/>');"
      onMouseOver="this.style.cursor = 'pointer';"
    >
      <ssHelpSpot helpId="personal_toolbar/favorites_button" offsetX="-15" offsetY="10" xAlignment="left" 
          title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
	    <div id="ss_navbarFavoritesButton<portlet:namespace/>">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.favorites"/></span>
	    </div>
	    <div id="ss_navbar_favorites<portlet:namespace/>" style="visibility:hidden;margin:0px;padding:0px;"
	    ><img border="0" src="<html:imagesPath/>pics/1pix.gif"></div>
	  </ssHelpSpot>
	</li>

<!-- My workspace -->
	<li class="ss_global_toolbar_myworkspace" 
      onClick="self.location.href='<portlet:renderURL 
      	windowState="maximized"><portlet:param 
      	name="action" value="view_ws_listing"/><portlet:param 
      	name="binderId" value="${ssUser.parentBinder.id}"/><portlet:param 
      	name="entryId" value="${ssUser.id}"/><portlet:param 
      	name="newTab" value="1"/></portlet:renderURL>';"
     onMouseOver="this.style.cursor = 'pointer';"
    >
	  <ssHelpSpot helpId="personal_toolbar/my_workspace_button" offsetY="10"
	      title="<ssf:nlt tag="helpSpot.myWorkspaceButton" text="My Workspace"/>">
	    <div id="ss_navbarMyWorkspaceButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.myWorkspace"/></span>
	    </div>
	  </ssHelpSpot>
	</li>

<!-- Clipboard -->
<c:if test="">
	<li class="ss_global_toolbar_clipboard" onClick="alert('Show the clipboard (tbd)');"
     onMouseOver="this.style.cursor = 'pointer';"
	>
	  <ssHelpSpot helpId="personal_toolbar/clipboard_button" offsetX="-10" offsetY="10"
	      title="<ssf:nlt tag="helpSpot.clipboardButton"/>">
	    <div id="ss_navbarClipboardButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.clipboard" text="Clipboard"/></span>
	    </div>
	  </ssHelpSpot>
	</li>
</c:if>

<!-- Show/hide portal -->
<c:if test="${ss_navbar_style != 'portlet'}">
	<li class="ss_global_toolbar_hide_portal" onClick="ss_toggleShowHidePortal(this);return false;"
     onMouseOver="this.style.cursor = 'pointer';"
	>
	  <ssHelpSpot helpId="personal_toolbar/maximize_button" offsetX="-10" offsetY="10"
	      title="<ssf:nlt tag="helpSpot.maximizeButton"/>">
	    <div id="ss_navbarHideShowPortalButton">
	      <span id="ss_navbarHideShowPortalText" class="ss_fineprint"><ssf:nlt tag="navigation.maximize"/></span>
	    </div>
	  </ssHelpSpot>
	</li>
</c:if>

<!-- Help button -->
	<li class="ss_global_toolbar_help"  onClick="ss_helpSystem.run();"
     onMouseOver="this.style.cursor = 'pointer';"
	>
	  <ssHelpSpot helpId="personal_toolbar/help_button" offsetX="-10" offsetY="10"
	      title="<ssf:nlt tag="helpSpot.helpButton"/>">
	    <div id="ss_navbarHelpButton">
	      <span class="ss_fineprint"><ssf:nlt tag="navigation.help" text="Help"/></span>
	    </div>
	  </ssHelpSpot>
	</li>

  </ul>


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
		name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>";


</script>

<div id="ss_navbar_bottom<portlet:namespace/>"></div>

</c:if>
</div>
<div class="ss_clear"></div>

<!-- Start of favorites pane -->
<div class="ss_style" id="ss_favorites_pane<portlet:namespace/>" 
  style="position:absolute; visibility:hidden;
  border:solid 1px black; height:200px;">
  <div>
  <div class="ss_style" id="ss_favorites<portlet:namespace/>" align="left">
	<table id="ss_favorites_table<portlet:namespace/>" cellspacing="0" cellpadding="0">
	<tbody>
	<tr>
	  <td align="left" class="ss_bold ss_largerprint"><ssf:nlt tag="favorites" text="Favorites"/></td>
	  <td align="right"><a onClick="ss_hideFavoritesPane('<portlet:namespace/>');return false;"
        ><img border="0" src="<html:imagesPath/>box/close_off.gif"/></a></td>
	</tr>
	<tr><td colspan="2"></td></tr>
	<tr>
	  <td colspan="2"><ssf:nlt tag="Loading"/></td>
	</tr>
	<tr>
	  <td colspan="2">
	  <c:set var="namespace" value="${renderResponse.namespace}"/>
	  <ssf:tree treeName="favTree${namespace}" treeDocument="${ss_favoritesTree}"
  		rootOpen="true" displayStyle="sortable" nowrap="true" showIdRoutine="ss_treeShowId"
  		initOnly="true" />
	  
	  </td>
	</tr>

	</tbody>
	</table>
  </div>
  
  <div id="ss_favorites2<portlet:namespace/>">
	<table id="ss_favorites_table2<portlet:namespace/>">
	<tbody>
	<tr><td><hr/></td></tr>
	<c:if test="${!empty ssBinder}">
		<tr>
		<td nowrap="nowrap">
		<a href="javascript: ;" 
		 onClick="ss_addForumToFavorites('<portlet:namespace/>');return false;"
		><span class="ss_bold"><ssf:nlt tag="favorites.addCurrentPage" 
			text="Add the current page to the favorites list..."/></span></a>
		</td>
		</tr>
		<tr><td> </td></tr>
	</c:if>
	<tr>
	<td nowrap="nowrap">
	  <a href="#" onClick="ss_showObjBlock('ss_favorites_form_div<portlet:namespace/>');ss_setFavoritesPaneSize('<portlet:namespace/>');return false;">
	    <span class="ss_bold"><ssf:nlt tag="favorites.addCategory" 
		  	text="Add a new favorites category..."/></span>
	  </a>
	  <br />
	  <div id="ss_favorites_form_div<portlet:namespace/>" style="visibility:hidden; display:none; margin:4px;">
		<form class="ss_style ss_style_color" id="ss_favorites_form<portlet:namespace/>" 
		  method="post" onSubmit="return false;" >
		  <span class="ss_style_color ss_labelAbove"><ssf:nlt tag="favorites.categoryName" 
		  	text="Category name:"/></span>
		  <input class="ss_style_color" type="text" size="20" name="new_favorites_category" />
		  <input class="ss_style_color" type="submit" name="add_favorites_category" 
		   value="<ssf:nlt tag="button.ok" text="OK"/>" 
		   onClick="ss_addFavoriteCategory('<portlet:namespace/>');return false;" />
		</form>
	  </div>
	</td>
	</tr>
	</tbody>
	</table>
  </div>
  </div>
</div>
<!-- End of favorites pane -->

<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
