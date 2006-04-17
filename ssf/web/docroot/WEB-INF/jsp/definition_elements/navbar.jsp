<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div id="debugLog">
</div>

<div class="ss_toolbar">
<table cellspacing="0" cellpadding="0" width="100%">
<tr>

<% // Favorites link %>
<td align="center"><a href="javascript: ;"
  onClick="ss_showFavoritesPane();return false;"
  ><ssf:nlt tag="navigation.workspace" text="Favorites"/></a></td>

<% // Workspace link %>
<td align="center"><ssf:nlt tag="navigation.workspace" text="Workspace"/></td>

<% // Search form %>
<td align="center"><ssf:nlt tag="navigation.workspace" text="Search"/></td>

<% // Clipboard %>
<td align="center"><ssf:nlt tag="navigation.workspace" text="Clipboard"/></td>

<% // Help toggle %>
<td align="center"><ssf:nlt tag="navigation.workspace" text="Help"/></td>

</tr>
</table>
<c:if test="${empty ss_navbarBottomSeen}">
<c:set var="ss_navbarBottomSeen" value="1"/>

<script type="text/javascript">

var ss_favoritesListArray = new Array();
var ss_favoritesListCount = 0;
function ss_enableFavoritesList(id) {
	ss_favoritesListArray[ss_favoritesListCount] = id;
	ss_favoritesListCount++;
	ss_DragDrop.makeListContainer( document.getElementById(id));
	document.getElementById(id).onDragDrop = function() {ss_saveFavorites();};
}

var ss_pauseFavoriteClick = 0;
var ss_pauseFavoriteClickTimer = null;
function ss_noClickFavorite() {
	ss_pauseFavoriteClick = 1;
	if (ss_pauseFavoriteClickTimer != null) clearTimeout(ss_pauseFavoriteClickTimer);
	ss_pauseFavoriteClickTimer = setTimeout("ss_clickFavorite();", 200)
}
function ss_clickFavorite() {
	ss_pauseFavoriteClick = 0;
	if (ss_pauseFavoriteClickTimer != null) clearTimeout(ss_pauseFavoriteClickTimer);
	ss_pauseFavoriteClickTimer = null;
}

function ss_favorite_clicked(obj) {
	if (ss_pauseFavoriteClick == 1) return;
	alert('Clicked '+obj.parentNode.id)
}

function ss_addForumToFavorites() {
	var binderId = '${ssBinder.id}';
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="add_favorite_binder" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("binderId", binderId)
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postFavoritesRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_addFavoriteCategory() {
	var formObj = self.document.getElementById('ss_favorites_form');
	var s = formObj.new_favorites_category.value;
	if (s == "") return;
	formObj.new_favorites_category.value = "";
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="add_favorites_category" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("category", s)
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postFavoritesRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_saveFavorites() {
	//The list was sorted, so turn off the click
	ss_noClickFavorite();
	
	var s = "";
	for (var i = 0; i < ss_favoritesListCount; i++) {
		var ulObj = self.document.getElementById(ss_favoritesListArray[i]);
    	var items = ulObj.getElementsByTagName( "li" );
		for (var j = 0; j < items.length; j++) {
			s += items[j].parentNode.id + "/" + items[j].id + " "
		}
	}
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="save_favorites" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("favorites", s)
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postFavoritesRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

var ss_favoritesPaneTopOffset = 10;
var ss_favoritesPaneLeftOffset = 4;
var ss_favoritesMarginW = 4;
var ss_favoritesMarginH = 6;
function ss_showFavoritesPane() {
	var fObj = self.document.getElementById("ss_favorites_pane");
	fObj.style.visibility = "visible";
	fObj.style.display = "none";
	fObj.style.display = "block";
	var fObj2 = self.document.getElementById("ss_favorites_table")
	//ss_setObjectWidth(fObj, parseInt(ss_getObjectWidth(fObj2) + ss_favoritesMarginW));
	//ss_setObjectHeight(fObj, parseInt(ss_getObjectHeight(fObj2) + ss_favoritesMarginH));
	var w = ss_getObjectWidth(fObj)
	ss_setObjectTop(fObj, parseInt(ss_getDivTop("ss_navbar_bottom") + ss_favoritesPaneTopOffset))
	ss_setObjectLeft(fObj, parseInt(ss_favoritesPaneLeftOffset - w))
	var leftEnd = parseInt(ss_getDivLeft("ss_navbar_bottom") + ss_favoritesPaneLeftOffset);
	ss_slideOpenDivHorizontal("ss_favorites_pane", leftEnd, 6);

	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="get_favorites_tree" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postFavoritesRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postFavoritesRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_favorites_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	}

	var fObj = self.document.getElementById("ss_favorites_pane")
	var fObj2 = self.document.getElementById("ss_favorites")
	ss_setObjectWidth(fObj, parseInt(ss_getObjectWidth(fObj2) + ss_favoritesMarginW));
	ss_setObjectHeight(fObj, parseInt(ss_getObjectHeight(fObj2) + ss_favoritesMarginH));

	ss_favoritesListArray = new Array();
	ss_favoritesListCount = 0;
	var uls = self.document.getElementsByTagName("ul");
	for (var i = 0; i < uls.length; i++) {
		if (uls[i].id.indexOf("ss_favorites") == 0) {
			ss_enableFavoritesList(uls[i].id)
		}
	}
}

var ss_slideOpenDivHorizontalTimer = null;
function ss_slideOpenDivHorizontal(id, leftEnd, steps) {
	if (ss_slideOpenDivHorizontalTimer != null) {
		clearTimeout(ss_slideOpenDivHorizontalTimer);
		ss_slideOpenDivHorizontalTimer = null;
	}
	var obj = self.document.getElementById(id);
	var left = ss_getDivLeft(id);
	var w = ss_getObjectWidth(obj)
	steps--;
	if (steps <= 0) {
		ss_setObjectLeft(obj, leftEnd);
		return;
	}
	var newLeft = parseInt(((leftEnd - left) / steps) + left);
	ss_setObjectLeft(obj, newLeft);
	ss_slideOpenDivHorizontalTimer = setTimeout("ss_slideOpenDivHorizontal('"+id+"', "+leftEnd+", "+steps+")", 20);
}

</script>

<div id="ss_navbar_bottom"></div>

</c:if>
</div>
<div id="ss_favorites_status_message"></div>
<div class="ss_style" id="ss_favorites_pane" 
  style="position:absolute; visibility:hidden; z-index:200;
  border:solid 1px black; height:200px;">
<form class="ss_form_no_color" name="ss_favorites_form" method="post" onSubmit="return false;" >
  <div class="ss_style" id="ss_favorites">
	<table class="ss_form_no_color" id="ss_favorites_table" cellspacing="0" cellpadding="0">
	<tbody>
	<tr>
	  <td colspan="2" class="ss_bold ss_largerprint"><ssf:nlt tag="favorites" text="Favorites"/></td>
	  <td align="right"><a onClick="ss_hideDiv('ss_favorites_pane');return false;"
        ><img border="0" src="<html:imagesPath/>box/close_off.gif"/></a></td>
	</tr>
	<tr><td colspan="2"></td></tr>
	<tr>
	  <td colspan="2"><ssf:nlt tag="Loading"/></td>
	</tr>
	</tbody>
	</table>
  </div>
<br/>
<br/>

<table class="ss_form_no_color ss_sortableList">
<tbody>
<tr>
<td nowrap="nowrap">
<span class="ss_bold">Add a new favorites category:</span><br />
<input type="text" size="20" name="new_favorites_category" />
<input type="submit" name="add_favorites_category" 
 value="<ssf:nlt tag="button.ok" text="OK"/>" 
 onClick="ss_addFavoriteCategory();return false;" />
</td></tr></table>
</form>
</div>
