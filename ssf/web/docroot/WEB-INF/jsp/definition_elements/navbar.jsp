<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

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

function ss_saveFavorites() {
	var s = "";
	for (var i = 0; i < ss_favoritesListCount; i++) {
		var ulObj = self.document.getElementById(ss_favoritesListArray[i]);
    	var items = ulObj.getElementsByTagName( "li" );
		for (var j = 0; j < items.length; j++) {
			s += items[j].id + " "
		}
	}
	alert(s)
}

var ss_favoritesPaneTopOffset = 10;
var ss_favoritesPaneLeftOffset = 4;
function ss_showFavoritesPane() {
	var fObj = self.document.getElementById("ss_navbar_favorites_pane")
	var w = ss_getObjectWidth(fObj)
	ss_setObjectTop(fObj, parseInt(ss_getDivTop("ss_navbar_bottom") + ss_favoritesPaneTopOffset))
	ss_setObjectLeft(fObj, parseInt(ss_favoritesPaneLeftOffset - w))
	fObj.style.visibility = "visible";
	var leftEnd = parseInt(ss_getDivLeft("ss_navbar_bottom") + ss_favoritesPaneLeftOffset);
	ss_slideOpenDivHorizontal("ss_navbar_favorites_pane", leftEnd, 6);
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
<div class="ss_style" id="ss_navbar_favorites_pane" 
  style="position:absolute; visibility:hidden; z-index:200;
  border:solid 1px black; height:200px;">
<table cellspacing="0" cellpadding="0">
<tr>
<td><b>Favorites</b></td><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="right">
  <a onClick="ss_hideDiv('ss_navbar_favorites_pane');return false;"><b>X</b></td>
</tr>
<tr>
<td colspan="3">
<ul id="ss_favorites_1" class="ss_dragable ss_userlist">
<li id="li_1_0" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_1" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_2" class="ss_dragable ss_userlist">yada yada yada yada</li>
<li id="li_1_3" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_4" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_5" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_6" class="ss_dragable ss_userlist">yada yada</li>
</ul>
</td>
</tr>
<tr>
<td colspan="3">
<ul id="ss_favorites_2" class="ss_dragable ss_userlist">
<li id="li_2_0" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_1" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_2" class="ss_dragable ss_userlist">yada yada yada yada</li>
<li id="li_2_3" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_4" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_5" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_6" class="ss_dragable ss_userlist">yada yada</li>
</ul>
</td>
</tr>
<tr><td colspan="3">&nbsp;</td></tr>
<tr>
<td colspan="3">
<a href="javascript: ;" 
 onClick="ss_addForumToFavorites('<c:out value="ssFolder.id"/>');return false;"
><span class="ss_bold">Add the current page to the favorites list...</span></a>
</td>
</tr>
<tr><td colspan="3">&nbsp;</td></tr>
<tr>
<td colspan="3">
<form class="ss_style" method="post" action="<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="add_favorites_category" />
    	</ssf:url>">
<span class="ss_bold">Add a new favorites category:</span><br>
<input type="text" size="20" name="new_favorites_category">
<input type="submit" name="add_favorites_category" 
 value="<ssf:nlt tag="button.ok" text="OK"/>">
</form>
</td>
</tr>

</table>
</div>

<script type="text/javascript">
ss_enableFavoritesList('ss_favorites_1')
ss_enableFavoritesList('ss_favorites_2')
</script>

</c:if>
</div>
