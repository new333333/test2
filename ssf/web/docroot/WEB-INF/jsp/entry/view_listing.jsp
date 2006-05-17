<% // The main forum view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
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

String ssReloadUrl = (String) renderRequest.getAttribute("ssReloadUrl");
if (ssReloadUrl == null) ssReloadUrl = "";
boolean reloadCaller = false;
if (!ssReloadUrl.equals("")) reloadCaller = true;

boolean isViewEntry = false;
if (!op.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING) && !op.equals(WebKeys.ACTION_VIEW_PROFILE_LISTING)) {
	isViewEntry = true;
}
	
int entryWindowWidth = 0;
if (ssUserProperties.containsKey("folderEntryWidth")) {
	entryWindowWidth = Integer.parseInt((String) ssUserProperties.get("folderEntryWidth"));
}
int entryWindowHeight = 0;
if (ssUserProperties.containsKey("folderEntryHeight")) {
	entryWindowHeight = Integer.parseInt((String) ssUserProperties.get("folderEntryHeight"));
}
String autoScroll = "true";
renderRequest.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
renderRequest.setAttribute("ss_entryWindowHeight", new Integer(entryWindowHeight));
%>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowHeight" type="java.lang.Integer" scope="request" />
<c:if test="<%= !isViewEntry %>">
<c:set var="showEntryCallbackRoutine" value="ss_showEntryInDiv" scope="request"/>
<c:set var="showEntryMessageRoutine" value="ss_showMessageInDiv" scope="request"/>
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

function ss_showMessageInDiv(str) {
	if (ss_displayStyle == "accessible") {return false;}
    
<%
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) || 
		displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP) ||
		displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
	return false
<%
	}
%>
	ss_showEntryInDiv(str)
}

function ss_showForumEntry(url, callbackRoutine) {
<%
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) || 
		displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP) ||
		displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
	return ss_showForumEntryInIframe(url);
<%
	}
%>
	fetch_url(url, callbackRoutine);
}

function ss_showEntryInDiv(str) {
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('ss_showentryhighwatermark');
    
    var wObj1 = null
    var wObj2 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('ss_showentrydiv')
        wObj2 = self.document.getElementById('ss_showentry')
    } else {
        wObj1 = self.document.all['ss_showentrydiv']
        wObj2 = self.document.all['ss_showentry']
    }
    
    if (str.indexOf('<body onLoad="self.location =') >= 0) {self.loaction.reload();}
    wObj1.style.display = "block";
    wObj2.innerHTML = str;
    wObj1.style.visibility = "visible";

    //If the entry div needs dynamic positioning after adding the entry, do it now
    if (self.ss_positionEntryDiv) {ss_positionEntryDiv();}
        
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('ss_showentryhighwatermark');
    
    //Get the position of the div displaying the entry
    if (autoScroll == "true") {
	    var entryY = ss_getDivTop('ss_showentrydiv')
	    var entryH = ss_getDivHeight('ss_showentrydiv')
	    var bodyY = self.document.body.scrollTop
	    var windowH = ss_getWindowHeight()
	    if (entryY >= bodyY) {
	    	if (entryY >= parseInt(bodyY + windowH)) {
	    		if (entryH > windowH) {
	    			smoothScroll(0,entryY)
	    		} else {
	    			var newY = parseInt(entryY - (windowH - entryH))
	    			smoothScroll(0,newY)
	    		}
	    	} else if (parseInt(entryY + entryH) > parseInt(bodyY + windowH)) {
	    		var overhang = parseInt((entryY + entryH) - (bodyY + windowH))
	    		var newY = parseInt(bodyY + overhang)
	    		if (newY > entryY) {newY = entryY}
	    		smoothScroll(0,newY)
	    	}
	    } else {
	    	smoothScroll(0,entryY)
	    }
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

var highlightBgColor = "${ss_folder_line_highlight_color}"
var highlightedLine = null;
var highlightedColLine = null;
var savedHighlightedLineBgColor = null;
var highlightClassName = "ss_highlightEntry";
var savedHighlightClassName = null;
var highlightColClassName = "ss_highlightEntry";
var savedHighlightColClassName = null;

//Called when one of the "Add entry" toolbar menu options is selected
function ss_addEntry(obj) {
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

var ss_currentEntryId = "";
function ss_loadEntry(obj,id) {
	if (ss_displayStyle == "accessible") {
		self.location.href = obj.href;
		return false;
	}
	if (id == "") return false;
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="Loading" text="Loading..."/>");
	if (window.highlightLineById) {
		highlightLineById(folderLine);
		if (window.swapImages && window.restoreImages) {
			restoreImages(id);
		}
	}
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
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
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="Loading" text="Loading..."/>");
	if (window.highlightLineById) {
		highlightLineById(folderLine);
	}
	ss_showForumEntry(url, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

function ss_notLoggedIn() {
	alert("<ssf:nlt tag="general.notLoggedIn" text="Your session has timed out. Please log in again."/>");
}

</script>
</c:if>

<c:if test="<%= !reloadCaller %>">
  <c:if test="<%= !isViewEntry %>">

<div id="ss_showentryhighwatermark" style="position:absolute; visibility:visible;">
<img src="<html:imagesPath/>pics/1pix.gif">
</div>
<%
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
%>
<%@ include file="/WEB-INF/jsp/entry/view_iframe.jsp" %>
<%
	} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>
<%@ include file="/WEB-INF/jsp/entry/view_popup.jsp" %>
<%
	} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
%>
<%@ include file="/WEB-INF/jsp/entry/view_accessible.jsp" %>
<%
	} else {
%>
<%@ include file="/WEB-INF/jsp/entry/view_vertical.jsp" %>
<%
	}
%>
  </c:if>
</c:if>
<c:if test="<%= reloadCaller %>">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<%= ssReloadUrl %>')
</script>
</c:if>
<c:if test="<%= !reloadCaller %>">
  <c:if test="<%= isViewEntry %>">
<script type="text/javascript">
if (self.parent && self.parent.highlightLineById) {
	self.parent.highlightLineById("folderLine_<c:out value="${ssEntry.id}"/>");
}
</script>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  processThisItem="true" 
  entry="${ssEntry}" />

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
	 	var url = "<ssf:url 
	    	adapter="true" 
	    	portletName="ss_forum" 
	    	action="__ajax_request" 
	    	actionUrl="false" >
			<ssf:param name="operation" value="save_entry_width" />
	    	</ssf:url>"
		var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addKeyValue("entry_height", ss_getWindowHeight())
		ajaxRequest.addKeyValue("entry_width", ss_getWindowWidth())
		//ajaxRequest.setEchoDebugInfo();
		//ajaxRequest.setPreRequest(ss_preRequest);
		//ajaxRequest.setPostRequest(ss_postRequest);
		ajaxRequest.setUseGET();
		ajaxRequest.sendRequest();  //Send the request
	}
}
ss_createOnResizeObj('ss_viewEntryResize', ss_viewEntryResize);
</script>
<div id="ss_entry_width_status_message" style="visibility:hidden; display:none;"></div>
<%
	}
%>
  </c:if>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

