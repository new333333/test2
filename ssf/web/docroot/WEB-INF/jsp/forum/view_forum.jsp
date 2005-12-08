<% // The main forum view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssHistoryMap" type="com.sitescape.ef.domain.HistoryMap" scope="request" />
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<%

String op = (String) renderRequest.getAttribute(WebKeys.ACTION);
if (op == null) op = WebKeys.FORUM_ACTION_VIEW_FORUM;
String displayStyle = ssUser.getDisplayStyle();
if (displayStyle == null || displayStyle.equals("")) {
	displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
}

String ssReloadUrl = (String) renderRequest.getAttribute("ssReloadUrl");
if (ssReloadUrl == null) ssReloadUrl = "";
boolean reloadCaller = false;
if (!ssReloadUrl.equals("")) reloadCaller = true;

boolean isViewEntry = false;
if (op.equals(WebKeys.FORUM_ACTION_VIEW_ENTRY)) {
	isViewEntry = true;
}
	
int entryWindowWidth = 0;
String autoScroll = "true";
if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_HORIZONTAL) && !isViewEntry) {
	autoScroll = "false";
}
renderRequest.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
%>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<c:if test="<%= !isViewEntry %>">
<c:set var="showEntryCallbackRoutine" value="ss_showEntryInDiv" scope="request"/>
<c:set var="showEntryMessageRoutine" value="ss_showMessageInDiv" scope="request"/>
<script language="javascript">
var autoScroll = "<%= autoScroll %>";

function ss_showMessageInDiv(str) {
    //Remember the scroll position so we can come back to this exact point
    savedScrollPositionTop = self.document.body.scrollTop;
    
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

var historyBack = new Array();
var historyForward = new Array();
var historyBackLine = new Array();
var historyForwardLine = new Array();
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
	historyBack.push(url)
	historyBackLine.push(highlightedLine)
	historyForward = new Array();
	historyForwardLine = new Array();
	fetch_url(url, callbackRoutine);
}

function ss_showForumEntryBack(callbackRoutine) {
	var currentUrl = historyBack.pop();
	var currentLine = historyBackLine.pop();
	if (currentUrl != "") {
		var lastUrl = historyBack.pop();
		var lastLine = historyBackLine.pop();
		if (lastUrl == "") {
			historyBack.push(currentUrl)
			historyBackLine.push(currentLine)
		} else {
			historyBack.push(lastUrl)
			historyBackLine.push(lastLine)
			historyForward.push(currentUrl)
			historyForwardLine.push(currentLine)
			highlightLine(lastLine)
			fetch_url(lastUrl, callbackRoutine)
		}
	}
}

function ss_showForumEntryForward(callbackRoutine) {
	var nextUrl = historyForward.pop();
	var nextLine = historyForwardLine.pop();
	if (nextUrl != "") {
		historyBack.push(nextUrl)
		historyBackLine.push(nextLine)
		highlightLine(nextLine)
		fetch_url(nextUrl, callbackRoutine)
	}
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
	    var entryY = getDivTop('ss_showentrydiv')
	    var entryH = getDivHeight('ss_showentrydiv')
	    var bodyY = self.document.body.scrollTop
	    var windowH = getWindowHeight()
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
}

var savedScrollPositionTop = null;
function scrollToSavedLocation() {
	if (autoScroll == "true") {
		if (savedScrollPositionTop != null) {
			smoothScroll(0,savedScrollPositionTop);
		}
	}
}

// var highlightBgColor = "<%= betaColor %>"
var highlightBgColor = "#efefef"
var highlightedLine = null;
var savedHighlightedLineBgColor = null;
var highlightClassName = "highlightEntry";
var savedHighlightClassName = null;
function highlightLine(obj) {
	if (highlightedLine != null) {
		if (highlightedLine.offsetParent.parentElement) {
			highlightedLine.offsetParent.parentElement.className = savedHighlightClassName;
		} else {
			highlightedLine.offsetParent.className = savedHighlightClassName;
		}
	}
	highlightedLine = obj;
	if (obj.offsetParent.parentElement) {
		savedHighlightClassName = highlightedLine.offsetParent.parentElement.className;
		highlightedLine.offsetParent.parentElement.className = highlightClassName;
	} else {
		savedHighlightClassName = highlightedLine.offsetParent.className;
		highlightedLine.offsetParent.className = highlightClassName;
	}
}

function highlightLineById(id) {
    if (id == "") {return;}
    var obj = null
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(id)
    } else {
        obj = self.document.all[id]
    }
    
	if (highlightedLine != null) {
		highlightedLine.className = savedHighlightClassName;
	}
	if (obj != null) {
		highlightedLine = obj;
		savedHighlightedLineClassName = highlightClassName;
		highlightedLine.className = highlightClassName;
	}
}


</script>
</c:if>

<c:if test="<%= !isViewEntry %>">

<div id="ss_showentryhighwatermark" style="position:absolute; visibility:visible;">
<img src="<html:imagesPath/>pics/1pix.gif">
</div>
<%
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_HORIZONTAL)) {
%>
<%@ include file="/WEB-INF/jsp/forum/view_forum_horizontal.jsp" %>
<%
	} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
%>
<%@ include file="/WEB-INF/jsp/forum/view_forum_iframe.jsp" %>
<%
	} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>
<%@ include file="/WEB-INF/jsp/forum/view_forum_popup.jsp" %>
<%
	} else {
%>
<%@ include file="/WEB-INF/jsp/forum/view_forum_vertical.jsp" %>
<%
	}
%>
</c:if>
<c:if test="<%= isViewEntry %>">
  <c:if test="<%= reloadCaller %>">
<script language="javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<%= ssReloadUrl %>')
</script>
  </c:if>
  <c:if test="<%= !reloadCaller %>">
<jsp:useBean id="ssFolderEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
<script language="javascript">
if (self.parent && self.parent.highlightLineById) {
	self.parent.highlightLineById("folderLine_<c:out value="${ssFolderEntry.id}"/>");
}
</script>
<%
	//Horizontal view
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_HORIZONTAL)) {
%>
    <ssf:box top="/WEB-INF/jsp/box/box_top.jsp" bottom="/WEB-INF/jsp/box/box_bottom.jsp">
      <ssf:param name="box_width" value="<%= new Integer(entryWindowWidth).toString() %>" />
      <ssf:param name="box_show_close_icon" value="true" />
	<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
	  configElement="<%= ssConfigElement %>" 
	  configJspStyle="<%= ssConfigJspStyle %>"
	  processThisItem="true" 
	  folderEntry="<%= ssFolderEntry %>" />
    </ssf:box>
<%
	
	//Iframe view
	} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
%>
	<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
	  configElement="<%= ssConfigElement %>" 
	  configJspStyle="<%= ssConfigJspStyle %>"
	  processThisItem="true" 
	  folderEntry="<%= ssFolderEntry %>" />
<%
	
	//Popup view
	} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
%>
	<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
	  configElement="<%= ssConfigElement %>" 
	  configJspStyle="<%= ssConfigJspStyle %>"
	  processThisItem="true" 
	  folderEntry="<%= ssFolderEntry %>" />
<%
	
	//Vertical view
	} else {
%>
	<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
	  configElement="<%= ssConfigElement %>" 
	  configJspStyle="<%= ssConfigJspStyle %>"
	  processThisItem="true" 
	  folderEntry="<%=  ssFolderEntry %>" />
<%
	}
%>
  </c:if>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

