<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<script type="text/javascript" src="/ssf/js/tree/tree_widget.js"></script>
<c:if test="${ssUserProperties.debugMode}">
  <div style="border:1px solid black;">
  <div style="background-color:#CECECE; border-bottom:1px solid black; width:100%;">
    <table cellspacing="0" cellpadding="0" style="background-color:#CECECE; width:100%;">
    <tr>
    <td>Debug window</td>
    <td align="right">
      <a href="" onClick="ss_turnOffDebugMode();return false;">
        <img src="<html:imagesPath/>pics/sym_s_delete.gif">
      </a>
<script type="text/javascript">
function ss_turnOffDebugMode() {
	var url = self.location.href + "&enableDebug=off"
	self.location.href = url;
}
</script>
    </td>
    </tr>
    </table>
  </div>
  <div>
  <textarea id="debugTextarea" style="width:100%;" rows="6"></textarea>
  </div>
  </div>
  <br/>
</c:if>

<div >
<table cellspacing="0" cellpadding="0" width="100%">
<tr>

<% // Favorites link %>
<td align="center">
  <ssHelpSpot helpId="favorites_button" align="left" offsetX="-6" offsetY="10"
    title="<ssf:nlt tag="helpSpot.favoritesButton"/>">
  <a href="javascript: ;"
  onClick="ss_showFavoritesPane();return false;"
  ><img border="0" src="<html:imagesPath/>icons/favorites.png" 
  alt="<ssf:nlt tag="navigation.favorites" text="Favorites"/>" /></a>
  <br/>
  <span class="ss_fineprint"><ssf:nlt tag="navigation.favorites" text="Favorites"/></span>
  </ssHelpSpot>
</td>

<% // History %>
<td align="center">
  <ssHelpSpot helpId="history_button" align="left" offsetX="-6" offsetY="45"
    title="<ssf:nlt tag="helpSpot.historyButton"/>">
  <div id="ss_navbarHistoryButton">
  <img border="0" src="<html:imagesPath/>icons/history.png" 
    alt="<ssf:nlt tag="navigation.history" text="History"/>" />
  <br>
  <span class="ss_fineprint"><ssf:nlt tag="navigation.history" text="History"/></span>
  </div>
  </ssHelpSpot>
</td>

<% // Search form %>
<td align="center" nowrap="nowrap">
  <ssHelpSpot helpId="search_button" align="left" offsetX="-6" offsetY="15"
    title="<ssf:nlt tag="helpSpot.searchButton"/>">
    <div id="ss_navbarSearchButton">
    <img border="0" src="<html:imagesPath/>icons/find.png" 
      alt="<ssf:nlt tag="navigation.search" text="Search"/>" /><input
      type="text" size="20"/><input type="submit" value="Go"/>
    <br>
    <span class="ss_fineprint"><ssf:nlt tag="navigation.search" text="Search"/></span>
    </div>
  </ssHelpSpot>
</td>

<% // Clipboard %>
<td align="center">
  <ssHelpSpot helpId="clipboard_button" align="left" offsetX="-6" offsetY="15"
    title="<ssf:nlt tag="helpSpot.clipboardButton"/>">
  <img border="0" src="<html:imagesPath/>icons/clipboard.png" 
    alt="<ssf:nlt tag="navigation.clipboard" text="Clipboard"/>" />
  <br>
  <span class="ss_fineprint"><ssf:nlt tag="navigation.clipboard" text="Clipboard"/></span>
  </ssHelpSpot>
</td>

<% // Help toggle %>
<td align="center"><a href="#" onClick="ss_helpSystem.run();return false;"><img border="0" 
  src="<html:imagesPath/>icons/help.png" 
  alt="<ssf:nlt tag="navigation.help" text="Help"/>" /></a>
  <br>
  <span class="ss_fineprint"><ssf:nlt tag="navigation.help" text="Help"/></span>
</td>

</tr>
</table>

<c:if test="${empty ss_navbarBottomSeen}">
<c:set var="ss_navbarBottomSeen" value="1"/>

<script type="text/javascript">

//Routine to go to a favorite when it is clicked
function favTree_showId(id, obj, action) {
	if (ss_pauseFavoriteClick == 1) return false;
	//Get the binderId from the elementId ("ss_favorites_xxx")
	var binderData = id.substr(13).split("_");
	binderId = binderData[2];
	
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized">
				<portlet:param name="action" value="ssActionPlaceHolder"/>
				<portlet:param name="binderId" value="ssBinderIdPlaceHolder"/>
				</portlet:renderURL>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}

var ss_favoritesListArray = new Array();
var ss_favoritesListCount = 0;
function ss_enableFavoritesList(id) {
	ss_favoritesListArray[ss_favoritesListCount] = id;
	ss_favoritesListCount++;

    var idObj = document.getElementById(id)
	ss_DragDrop.makeListContainer(idObj);
    eval("idObj.onDragOver = function() {ss_highlightFavorites('"+idObj.id+"');}");
    eval("idObj.onDragOut = function() {ss_unhighlightFavorites('"+idObj.id+"');}");
    eval("idObj.onDragDrop = function() {ss_saveFavorites('"+idObj.id+"');}");

    var items = idObj.getElementsByTagName( "li" );
	for (var i = 0; i < items.length; i++) {
		eval("items[i].onDragEndCallback = function() {ss_saveDragId('"+items[i].id+"');}");
	}
}
var ss_lastDropped = null;
function ss_saveDragId(id) {
    ss_lastDropped = id
    return false;
}

var ss_savedFavoriteClassNames = new Array();
var ss_lastHighlightedFavorite = null;
function ss_highlightFavorites(id) {
	ss_unhighlightFavorites();
	var idObj = document.getElementById(id);
	//document.getElementById('debugLog').innerHTML += 'Highlight '+idObj.id+'  '
	if (!ss_savedFavoriteClassNames[idObj.id] || 
			ss_savedFavoriteClassNames[idObj.id] == "undefined" || 
			ss_savedFavoriteClassNames[idObj.id] == "") {
		ss_savedFavoriteClassNames[idObj.id] = idObj.className;
	}
	idObj.className = ss_savedFavoriteClassNames[idObj.id] + " ss_sortableHighlighted"
	//document.getElementById('debugLog').innerHTML += ' ('+idObj.className+') '
	ss_lastHighlightedFavorite = idObj;
}

function ss_unhighlightFavorites() {
	if (ss_lastHighlightedFavorite != null) {
		//document.getElementById('debugLog').innerHTML += ' unHighlight '+ss_lastHighlightedFavorite.id+'  '
		var id = ss_lastHighlightedFavorite.id;
		if (ss_savedFavoriteClassNames[id] && 
				ss_savedFavoriteClassNames[id] != "undefined" && 
				ss_savedFavoriteClassNames[id] != "") {
			var idObj = document.getElementById(id);
			idObj.className = ss_savedFavoriteClassNames[id];
			//document.getElementById('debugLog').innerHTML += ' ok '
		}
	}
	ss_lastHighlightedFavorite = null;
}

function ss_saveFavorites(id) {
	ss_setupStatusMessageDiv()
	ss_unhighlightFavorites(id)
	if (ss_lastDropped == null) return;
	
	//The list was sorted, so turn off the click
	ss_noClickFavorite();
	
	var s = "";
	for (var i = 0; i < ss_favoritesListCount; i++) {
		var ulObj = self.document.getElementById(ss_favoritesListArray[i]);
    	var items = ulObj.getElementsByTagName( "li" );
		for (var j = 0; j < items.length; j++) {
			s += items[j].id + " "
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
	ajaxRequest.addKeyValue("movedItemId", ss_lastDropped)
	ss_lastDropped = null;
	ajaxRequest.addKeyValue("favorites", s)
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postFavoritesRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

var ss_pauseFavoriteClick = 0;
var ss_pauseFavoriteClickTimer = null;
function ss_noClickFavorite() {
	ss_pauseFavoriteClick = 1;
	if (ss_pauseFavoriteClickTimer != null) clearTimeout(ss_pauseFavoriteClickTimer);
	ss_pauseFavoriteClickTimer = setTimeout("ss_clickFavorite();", 500)
}
function ss_clickFavorite() {
	ss_pauseFavoriteClick = 0;
	if (ss_pauseFavoriteClickTimer != null) clearTimeout(ss_pauseFavoriteClickTimer);
	ss_pauseFavoriteClickTimer = null;
}

function ss_addForumToFavorites() {
	ss_setupStatusMessageDiv()
	var binderId = '${ssBinder.id}';
	var action = '${action}';
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="add_favorite_binder" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("binderId", binderId);
	ajaxRequest.addKeyValue("viewAction", action);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postFavoritesRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_addFavoriteCategory() {
	ss_setupStatusMessageDiv()
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
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postFavoritesRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

var ss_favoritesPaneTopOffset = 10;
var ss_favoritesPaneLeftOffset = 4;
var ss_favoritesMarginW = 4;
var ss_favoritesMarginH = 6;
function ss_showFavoritesPane() {
	ss_setupStatusMessageDiv()
	var fObj = self.document.getElementById("ss_favorites_pane");
	ss_moveObjectToBody(fObj);
	fObj.style.visibility = "visible";
	ss_setOpacity(fObj, 100)
	//fObj.style.display = "none";
	fObj.style.display = "block";
	var fObj2 = self.document.getElementById("ss_favorites_table")
	var w = ss_getObjectWidth(fObj)
	ss_setObjectTop(fObj, parseInt(ss_getDivTop("ss_navbar_bottom") + ss_favoritesPaneTopOffset))
	ss_setObjectLeft(fObj, parseInt(ss_getDivLeft("ss_navbar_bottom")))
	var leftEnd = parseInt(ss_getDivLeft("ss_navbar_bottom") + ss_favoritesPaneLeftOffset);
	ss_showDiv("ss_favorites_pane");
	ss_hideObj("ss_favorites_form_div");

	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="get_favorites_tree" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postFavoritesRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postFavoritesRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	}
	ss_hideObj("ss_favorites_form_div");
	ss_setFavoritesPaneSize();

	ss_favoritesListArray = new Array();
	ss_favoritesListCount = 0;
	var uls = self.document.getElementsByTagName("ul");
	for (var i = 0; i < uls.length; i++) {
		if (uls[i].id.indexOf("ul_ss_favorites") == 0) {
			ss_enableFavoritesList(uls[i].id)
		}
	}
	if (document.getElementById("ul_ss_delete") != null) ss_enableFavoritesList("ul_ss_delete");
}

function ss_hideFavoritesPane() {
	ss_hideDivFadeOut('ss_favorites_pane', 0);
}

function ss_setFavoritesPaneSize() {
	var fObj = self.document.getElementById("ss_favorites_pane")
	var fObj2 = self.document.getElementById("ss_favorites")
	var fObj22 = self.document.getElementById("ss_favorites2")
	ss_setObjectWidth(fObj, parseInt(ss_getObjectWidth(fObj2) + ss_favoritesMarginW));
	var height = parseInt(ss_getObjectHeight(fObj2) + ss_getObjectHeight(fObj22) + ss_favoritesMarginH * 2);
	if (height < 400) height = "400px";
	ss_setObjectHeight(fObj, height);
	var fObj3 = self.document.getElementById("ss_favorites_table")
	var fObj4 = self.document.getElementById("ss_favorites_table2")
	var tableWidth = ss_getObjectWidth(fObj3);
	var table2Width = ss_getObjectWidth(fObj4);
	if (tableWidth > table2Width) {
		ss_setObjectWidth(fObj4, ss_getObjectWidth(fObj3));
	} else {
		ss_setObjectWidth(fObj3, ss_getObjectWidth(fObj4));
	}
}

</script>

<div id="ss_navbar_bottom"></div>

</c:if>
</div>
<div class="ss_style" id="ss_favorites_pane" 
  style="position:absolute; visibility:hidden; z-index:200;
  border:solid 1px black; height:200px;">
  <div>
  <div class="ss_style" id="ss_favorites" align="left">
	<table id="ss_favorites_table" cellspacing="0" cellpadding="0">
	<tbody>
	<tr>
	  <td align="left" class="ss_bold ss_largerprint"><ssf:nlt tag="favorites" text="Favorites"/></td>
	  <td align="right"><a onClick="ss_hideFavoritesPane();return false;"
        ><img border="0" src="<html:imagesPath/>box/close_off.gif"/></a></td>
	</tr>
	<tr><td colspan="2"></td></tr>
	<tr>
	  <td colspan="2"><ssf:nlt tag="Loading"/></td>
	</tr>

	</tbody>
	</table>
  </div>
  
  <div id="ss_favorites2">
	<table id="ss_favorites_table2">
	<tbody>
	<tr><td><hr/></td></tr>
	<tr>
	<td nowrap="nowrap">
	<a href="javascript: ;" 
	 onClick="ss_addForumToFavorites();return false;"
	><span class="ss_bold"><ssf:nlt tag="favorites.addCurrentPage" 
		text="Add the current page to the favorites list..."/></span></a>
	</td>
	</tr>
	<tr><td> </td></tr>
	<tr>
	<td nowrap="nowrap">
	  <a href="#" onClick="ss_showObjBlock('ss_favorites_form_div');ss_setFavoritesPaneSize();return false;">
	    <span class="ss_bold"><ssf:nlt tag="favorites.addCategory" 
		  	text="Add a new favorites category..."/></span>
	  </a>
	  <br />
	  <div id="ss_favorites_form_div" style="visibility:hidden; display:none; margin:4px;">
		<form class="ss_style ss_style_color" id="ss_favorites_form" 
		  method="post" onSubmit="return false;" >
		  <span class="ss_style_color ss_labelAbove"><ssf:nlt tag="favorites.categoryName" 
		  	text="Category name:"/></span>
		  <input class="ss_style_color" type="text" size="20" name="new_favorites_category" />
		  <input class="ss_style_color" type="submit" name="add_favorites_category" 
		   value="<ssf:nlt tag="button.ok" text="OK"/>" 
		   onClick="ss_addFavoriteCategory();return false;" />
		</form>
	  </div>
	</td>
	</tr>
	</tbody>
	</table>
  </div>
  </div>
</div>
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
