<% // The main forum view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<%
String op = (String) renderRequest.getAttribute(WebKeys.ACTION);
String displayStyle = ssUser.getDisplayStyle();
if (displayStyle == null || displayStyle.equals("")) {
	displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
}

String ssLoadEntryUrl = (String) renderRequest.getAttribute("ssLoadEntryUrl");
if (ssLoadEntryUrl == null) ssLoadEntryUrl = "";
String ssLoadEntryId = (String) renderRequest.getAttribute("ssLoadEntryId");
if (ssLoadEntryId == null) ssLoadEntryId = "";
//this jsp should is not included when reloadurl is set.  This is left here
//until we get after a reply to work - it may be needed then
String ssReloadUrl = (String) renderRequest.getAttribute("ssReloadUrl");
if (ssReloadUrl == null) ssReloadUrl = "";
boolean reloadCaller = false;
if (!ssReloadUrl.equals("")) reloadCaller = true;

boolean isViewEntry = false;
if (op != null && !op.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING) && !op.equals(WebKeys.ACTION_VIEW_PROFILE_LISTING)) {
	isViewEntry = true;
}
	
int entryWindowWidth = 0;
if (ssUserProperties.containsKey("folderEntryWidth")) {
	entryWindowWidth = Integer.parseInt((String) ssUserProperties.get("folderEntryWidth"));
}
int entryWindowTop = 0;
if (ssUserProperties.containsKey("folderEntryTop")) {
	entryWindowTop = Integer.parseInt((String) ssUserProperties.get("folderEntryTop"));
}
int entryWindowLeft = 0;
if (ssUserProperties.containsKey("folderEntryLeft")) {
	entryWindowLeft = Integer.parseInt((String) ssUserProperties.get("folderEntryLeft"));
}
int entryWindowHeight = 0;
if (ssUserProperties.containsKey("folderEntryHeight")) {
	entryWindowHeight = Integer.parseInt((String) ssUserProperties.get("folderEntryHeight"));
}
String autoScroll = "true";
renderRequest.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
renderRequest.setAttribute("ss_entryWindowTop", new Integer(entryWindowTop));
renderRequest.setAttribute("ss_entryWindowLeft", new Integer(entryWindowLeft));
renderRequest.setAttribute("ss_entryWindowHeight", new Integer(entryWindowHeight));
%>
<c:if test="<%= !isViewEntry %>">
<c:set var="showEntryCallbackRoutine" value="ss_showEntryInDiv" scope="request"/>


<script type="text/javascript">
var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";
function ss_showForumEntryInPopupWindow() {
    ss_debug('popup width = ' + ss_viewEntryPopupWidth)
    ss_debug('popup height = ' + ss_viewEntryPopupHeight)
    var wObj = self.document.getElementById('ss_showfolder')
	if (ss_viewEntryPopupWidth == "0px") ss_viewEntryPopupWidth = ss_getObjectWidth(wObj);
	if (ss_viewEntryPopupHeight == "0px") ss_viewEntryPopupHeight = parseInt(ss_getWindowHeight()) - 50;
    self.window.open(menuLinkAdapterURL, '_blank', 'width='+ss_viewEntryPopupWidth+',height='+ss_viewEntryPopupHeight+',resizable,scrollbars');
    return false;
}
</script>

<script type="text/javascript">

//Define the url of this page in case the entry needs to reload this page
var ss_reloadUrl = "${ss_reloadUrl}";
var ssLoadEntryUrl = "<%= ssLoadEntryUrl %>";
var autoScroll = "<%= autoScroll %>";
var ss_displayStyle = "<%= displayStyle %>";
<%
	if (!ssLoadEntryUrl.equals("")) {
%>
function ss_showEntryOnLoad() {
	ss_loadEntryUrl("<%= ssLoadEntryUrl %>", "<%= ssLoadEntryId %>");
}
ss_createOnLoadObj('ss_showEntryOnLoad', ss_showEntryOnLoad);
<%
	}
%>

function ss_showForumEntry(url, callbackRoutine, isDashboard) {
<%
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) || 
		displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP) ||
		displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
	if (isDashboard == "yes") {
<%		
		if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
%>		
			return ss_showForumEntryInIframe_Overlay(url);
<%		
		} else if ( displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)  ||
					displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL) ) {
%>		
			return ss_showForumEntryInIframe_Popup(url);			
<%
		}
%>
	} else {
		return ss_showForumEntryInIframe(url);
	}
<%
	}
%>
	ss_fetch_url(url, callbackRoutine);
}

var ss_highlightBgColor = "${ss_folder_line_highlight_color}"
var ss_highlightedLine = null;
var ss_highlightedColLine = null;
var ss_savedHighlightedLineBgColor = null;
var ss_highlightClassName = "ss_highlightEntry";
var ss_savedHighlightClassName = null;
var ss_highlightColClassName = "ss_highlightEntry";
var ss_savedHighlightColClassName = null;

//Called when one of the "Add entry" toolbar menu options is selected
function ss_addEntry(obj) {
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

var ss_currentEntryId = "";
function ss_loadBinder(obj,id, entityType) {
	if (ss_linkMenu.showingMenu && ss_linkMenu.showingMenu == 1) {
		//The user wants to see the drop down options, don't show the binder
		ss_linkMenu.showingMenu = 0;
		return false;
	} else {
		return true;
	}
}

function ss_loadEntry(obj, id, binderId, entityType, isDashboard) {
	if (ss_displayStyle == "accessible") {
		self.location.href = obj.href;
		return false;
	}
	if (ss_linkMenu.showingMenu && ss_linkMenu.showingMenu == 1) {
		//The user wants to see the drop down options, don't show the entry
		if (binderId != null && binderId != "") ss_linkMenu.binderId = binderId;
		if (entityType != null && entityType != "") ss_linkMenu.entityType = entityType;
		ss_linkMenu.showingMenu = 0;
		return false;
	}
	ss_linkMenu.showingMenu = 0;
	
	if (id == "") return false;
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	if (window.ss_highlightLineById) {
		ss_highlightLineById(folderLine);
		if (window.swapImages && window.restoreImages) {
			restoreImages(id);
		}
	}
	
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>, isDashboard);
	return false;
}

function ss_loadEntryUrl(url,id) {
	if (ss_displayStyle == "accessible") {
		self.location.href = url;
		return false;
	}
	if (id == "") return false;
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	if (window.ss_highlightLineById) {
		ss_highlightLineById(folderLine);
	}
	
	ss_showForumEntry(url, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}
</script>
</c:if>

<c:if test="<%= reloadCaller %>">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>
</c:if>


<% // View the entry  %>

<c:if test="<%= !reloadCaller %>">
  <c:if test="<%= isViewEntry %>">
<script type="text/javascript">
var ss_saveViewEntryWidthUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="save_entry_width" />
	</ssf:url>";
if (self.parent && self.parent.ss_highlightLineById) {
	self.parent.ss_highlightLineById("folderLine_<c:out value="${ssEntry.id}"/>");
}
//Define the url of this page in case the entry needs to reload this page
var ss_reloadUrl = "${ss_reloadUrl}";

</script>

<ssf:ifnotadapter>
<div id="ss_portlet_content" class="ss_style ss_portlet">
<% // Navigation bar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

<% // Tabs %>
<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
<div class="ss_clear"></div>

<div class="ss_tab_canvas">
<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
   <div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">

</ssf:ifnotadapter>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  processThisItem="true" 
  entry="${ssEntry}" />

<ssf:ifnotadapter>
	</div>
</div>

</ssf:ifnotadapter>

<div align="left" name="ss_subscription_entry" id="ss_subscription_entry" 
  style="display:none; visibility:hidden; padding:4px;" class="ss_popupMenu ss_indent_medium">
<form class="ss_style ss_form" method="post" action="" style="display:inline;">
  <span class="ss_bold"><ssf:nlt tag="subscribe.select.type"/></span><br/><br/>
  <input type="radio" name="notifyType" value="2" id="notifyType_${ssEntry.id}_2"
  <c:if test="${ssSubscription.style=='2'}"> checked="checked"</c:if>
  /><label for="notifyType_${ssEntry.id}_2"><ssf:nlt tag="subscribe.message"/></label><br/>
  <input type="radio" name="notifyType" value="3" id="notifyType_${ssEntry.id}_3"
  <c:if test="${ssSubscription.style=='3'}"> checked="checked"</c:if>
  /><label for="notifyType_${ssEntry.id}_3"><ssf:nlt tag="subscribe.noattachments"/></label><br/>
<c:if test="${!empty ssSubscription}">
  <input type="radio" name="notifyType" id="notifyType_${ssEntry.id}_delete" value="-1"/><label for="notifyType_${ssEntry.id}_delete"><ssf:nlt tag="subscribe.delete"/></label><br/>
</c:if>
  <br/>
  <input type="submit" name="subscribeBtn" value="<ssf:nlt tag="button.ok"/>">
 &nbsp;&nbsp;&nbsp;
  <input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('ss_subscription_entry');return false;">
</form>
</div>	

<% // Footer toolbar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />

<ssf:ifnotadapter>
</div>
</div>
</div>
</ssf:ifnotadapter>

<%
	//See if this is the Popup view
	if (ssUser.getDisplayStyle() != null && 
	    ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>
<script type="text/javascript">
var ss_viewEntryResizeHappened = 0;
var ss_viewEntryResizeTimeout = null;
function ss_viewEntryResize() {
	ss_viewEntryResizeHappened = 1
	if (ss_viewEntryResizeTimeout != null) {
		clearTimeout(ss_viewEntryResizeTimeout)
		ss_viewEntryResizeTimeout = null;
	}
	ss_viewEntryResizeTimeout = setTimeout('ss_viewEntrySaveSize()', 250);
}
function ss_viewEntrySaveSize() {
	ss_setupStatusMessageDiv()
	clearTimeout(ss_viewEntryResizeTimeout);
	ss_viewEntryResizeTimeout = null;
	if (ss_viewEntryResizeHappened == 1) {
		//See if the user is finished resizing; wait for activity to stop
		ss_viewEntryResizeHappened = 0;
		ss_viewEntryResizeTimeout = setTimeout('ss_viewEntrySaveSize()', 250);
	} else {
		//Resizing must have finished, save the new size
		if (self.opener) {
			//Write the current size back onto the opener page for future use
			self.opener.ss_viewEntryPopupHeight = ss_getWindowHeight()
			self.opener.ss_viewEntryPopupWidth = ss_getWindowWidth()
		}
	 	var url = ss_saveViewEntryWidthUrl;
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addKeyValue("entry_height", ss_getWindowHeight())
		ajaxRequest.addKeyValue("entry_width", ss_getWindowWidth())
		//ajaxRequest.setEchoDebugInfo();
		//ajaxRequest.setPreRequest(ss_preRequest);
		//ajaxRequest.setPostRequest(ss_postRequest);
		ajaxRequest.setUseGET();
		ajaxRequest.sendRequest();  //Send the request
	}
}
function ss_viewEntryUnload() {
}
function ss_positionEntryOnLoad() {
}
ss_createOnLoadObj('ss_positionEntryOnLoad', ss_positionEntryOnLoad);
ss_createOnResizeObj('ss_viewEntryResize', ss_viewEntryResize);
ss_createEventObj('ss_viewEntryUnload', 'unload');
</script>
<%
	}
%>
  </c:if>
</c:if>
