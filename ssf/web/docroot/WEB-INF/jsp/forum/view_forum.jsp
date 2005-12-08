<% // The main forum view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>

<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssHistoryMap" type="com.sitescape.ef.domain.HistoryMap" scope="request" />
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />
<%

String op = (String) renderRequest.getAttribute(WebKeys.ACTION);
if (op == null) op = WebKeys.FORUM_ACTION_VIEW_FORUM;
String displayStyle = ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL;
if (ssUserProperties.containsKey(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE)) {
	displayStyle = (String) ssUserProperties.get(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE);
}

boolean statePopUp = false;
boolean popupValue = false;
if (!op.equals(WebKeys.FORUM_ACTION_VIEW_ENTRY)) {
	popupValue = true;
} else {
	if (statePopUp) popupValue = true;
}
	
//int boxWidth = (int)ParamUtil.get(renderRequest, "box_width", (double)RES_TOTAL);
int boxWidth = 600;
int entryWindowWidth = boxWidth;
String autoScroll = "true";
if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL) && !statePopUp) {
	autoScroll = "false";
}
if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL) ||
		displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME)) {
	entryWindowWidth = (entryWindowWidth / 3) * 2;
}

entryWindowWidth = entryWindowWidth - 4;
int toolbarWidth = boxWidth - 10;
renderRequest.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
renderRequest.setAttribute("ss_toolbarWidth", new Integer(toolbarWidth));
%>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_toolbarWidth" type="java.lang.Integer" scope="request" />
<c:if test="<%= !op.equals(WebKeys.FORUM_ACTION_VIEW_ENTRY) %>">
<c:set var="showEntryCallbackRoutine" value="showEntryInDiv" scope="request"/>
<c:set var="showEntryMessageRoutine" value="showMessageInDiv" scope="request"/>
<script language="javascript">
var autoScroll = "<%= autoScroll %>";

function showMessageInDiv(str) {
    //Remember the scroll position so we can come back to this exact point
    savedScrollPositionTop = self.document.body.scrollTop;
    
<%
	if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME)) {
%>
	return false
<%
	}
%>
	showEntryInDiv(str)
}

var historyBack = new Array();
var historyForward = new Array();
var historyBackLine = new Array();
var historyForwardLine = new Array();
function showForumEntry(url, callbackRoutine) {
<%
	if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME)) {
%>
	return showForumEntryInIframe(url);
<%
	}
%>
	historyBack.push(url)
	historyBackLine.push(highlightedLine)
	historyForward = new Array();
	historyForwardLine = new Array();
	fetch_url(url, callbackRoutine);
}

function showForumEntryBack(callbackRoutine) {
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

function showForumEntryForward(callbackRoutine) {
	var nextUrl = historyForward.pop();
	var nextLine = historyForwardLine.pop();
	if (nextUrl != "") {
		historyBack.push(nextUrl)
		historyBackLine.push(nextLine)
		highlightLine(nextLine)
		fetch_url(nextUrl, callbackRoutine)
	}
}

function showEntryInDiv(str) {
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('showentryhighwatermark');
    
    var wObj1 = null
    var wObj2 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('showentrydiv')
        wObj2 = self.document.getElementById('showentry')
    } else {
        wObj1 = self.document.all['showentrydiv']
        wObj2 = self.document.all['showentry']
    }
    
    //If the entry div needs dynamic positioning, do it now
    positionEntryDiv()
    
    if (str.indexOf('<body onLoad="self.location =') >= 0) {self.loaction.reload();}
    wObj1.style.display = "block";
    wObj2.innerHTML = str;
    wObj1.style.visibility = "visible";
    
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('showentryhighwatermark');
    
    //Get the position of the div displaying the entry
    if (autoScroll == "true") {
	    var entryY = getDivTop('showentrydiv')
	    var entryH = getDivHeight('showentrydiv')
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

//This gets overwritten if desired
function positionEntryDiv() {}

var savedScrollPositionTop = null;
function scrollToSavedLocation() {
	if (autoScroll == "true") {
		if (savedScrollPositionTop != null) {
			smoothScroll(0,savedScrollPositionTop);
		}
	}
}

var highlightBgColor = "<%= betaColor %>"
var highlightedLine = null;
var savedHighlightedLineBgCollor = null;
function highlightLine(obj) {
	alert("highlightLine")
	if (highlightedLine != null) {
		if (highlightedLine.offsetParent.parentElement) {
			highlightedLine.offsetParent.parentElement.bgColor = savedHighlightedLineBgCollor;
		} else {
			highlightedLine.offsetParent.bgColor = savedHighlightedLineBgCollor;
		}
	}
	highlightedLine = obj;
	if (obj.offsetParent.parentElement) {
		savedHighlightedLineBgCollor = highlightedLine.offsetParent.parentElement.bgColor;
		highlightedLine.offsetParent.parentElement.bgColor = highlightBgColor;
	} else {
		savedHighlightedLineBgCollor = highlightedLine.offsetParent.bgColor;
		highlightedLine.offsetParent.bgColor = highlightBgColor;
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
		highlightedLine.bgColor = savedHighlightedLineBgCollor;
	}
	if (obj != null) {
		highlightedLine = obj;
		savedHighlightedLineBgCollor = highlightedLine.bgColor;
		highlightedLine.bgColor = highlightBgColor;
	}
}


</script>
</c:if>

<c:if test="<%= !op.equals(WebKeys.FORUM_ACTION_VIEW_ENTRY) %>">

<div id="showentryhighwatermark" style="position:absolute; visibility:visible;">
<img src="<html:imagesPath/>pics/1pix.gif">
</div>
<%
	if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL)) {
%>
<%@ include file="/WEB-INF/jsp/forum/view_forum_horizontal.jsp" %>
<%
	} else if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME)) {
%>
<%@ include file="/WEB-INF/jsp/forum/view_forum_iframe.jsp" %>
<%
	} else {
%>
<%@ include file="/WEB-INF/jsp/forum/view_forum_vertical.jsp" %>
<%
	}
%>
</c:if>
<c:if test="<%= op.equals(WebKeys.FORUM_ACTION_VIEW_ENTRY) %>">
<jsp:useBean id="ssFolderEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
  <c:if test="<%= !statePopUp %>">
<script language="javascript">
function loadEntry(obj,id) {
	self.location.href = obj.href;
	return false;
}
</script>
    <liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
    </liferay:box>
    <liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	  <ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
	    configElement="<%= ssConfigElement %>" 
	    configJspStyle="<%= ssConfigJspStyle %>"
	    processThisItem="true" 
	    folderEntry="<%= ssFolderEntry %>" />
    </liferay:box>
  </c:if>
  
  <c:if test="<%= statePopUp %>">
<script language="javascript">
if (self.parent && self.parent.highlightLineById) {
	self.parent.highlightLineById("folderLine_<c:out value="${ssFolderEntry.id}"/>");
}
</script>
<%
	//Horizontal view
	if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL)) {
		toolbarWidth = entryWindowWidth - 6;
		renderRequest.setAttribute("ss_toolbarWidth", new Integer(toolbarWidth));
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
	} else if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME)) {
		toolbarWidth = entryWindowWidth - 20;
		renderRequest.setAttribute("ss_toolbarWidth", new Integer(toolbarWidth));
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
