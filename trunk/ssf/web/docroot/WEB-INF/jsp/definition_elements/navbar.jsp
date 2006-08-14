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

var ss_favoritesShowIdUrl = "<portlet:renderURL windowState="maximized">
			<portlet:param name="action" value="ssActionPlaceHolder"/>
			<portlet:param name="binderId" value="ssBinderIdPlaceHolder"/>
			</portlet:renderURL>";


</script>

<div id="ss_navbar_bottom"></div>

</c:if>
</div>
<div class="ss_style" id="ss_favorites_pane" 
  style="position:absolute; visibility:hidden;
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
