<% // The main forum view - for viewing folder listings and for viewing entries
%>
<%@ include file="/html/portlet/forum/init.jsp" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_user_properties" type="java.util.Map" scope="request" />
<jsp:useBean id="ss_folder_historymap" type="com.sitescape.ef.domain.HistoryMap" scope="request" />

<%

String op = (String) request.getAttribute(ObjectKeys.FORUM_URL_OPERATION);
String displayStyle = ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL;
if (ss_user_properties.containsKey(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE)) {
	displayStyle = (String) ss_user_properties.get(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE);
}

boolean statePopUp = renderRequest.getWindowState().equals(LiferayWindowState.POP_UP) ? true : false;
int boxWidth = (int)ParamUtil.get(request, "box_width", (double)RES_TOTAL);
int entryWindowWidth = (int)ParamUtil.get(request, "box_width", (double)RES_TOTAL);
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
request.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
request.setAttribute("ss_toolbarWidth", new Integer(toolbarWidth));
%>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_toolbarWidth" type="java.lang.Integer" scope="request" />
<c_rt:if test="<%= !op.equals(ObjectKeys.FORUM_OPERATION_VIEW_ENTRY) %>">
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

var highlightBgColor = "<%= GetterUtil.get(request.getParameter("body_background"), skin.getBeta().getBackground()) %>"
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
</c_rt:if>

<c_rt:if test="<%= !op.equals(ObjectKeys.FORUM_OPERATION_VIEW_ENTRY) %>">

<div id="showentryhighwatermark" style="position:absolute; visibility:visible;">
<img src="<%= contextPath %>/html/pics/1pix.gif">
</div>
<%
	if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL)) {
%>
<%@ include file="/html/portlet/forum/view_forum_horizontal.jsp" %>
<%
	} else if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME)) {
%>
<%@ include file="/html/portlet/forum/view_forum_iframe.jsp" %>
<%
	} else {
%>
<%@ include file="/html/portlet/forum/view_forum_vertical.jsp" %>
<%
	}
%>
</c_rt:if>
<c_rt:if test="<%= op.equals(ObjectKeys.FORUM_OPERATION_VIEW_ENTRY) %>">
<jsp:useBean id="ss_forum_entry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
  <c_rt:if test="<%= !statePopUp %>">
<script language="javascript">
function loadEntry(obj,id) {
	self.location.href = obj.href;
	return false;
}
</script>
    <liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
<%@ include file="/html/portlet/forum/view_forum_history_bar.jsp" %>
    </liferay:box>
    <liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	  <sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
	    configElement="<%= ss_forum_config %>" 
	    configJspStyle="<%= ss_forum_configJspStyle %>"
	    processThisItem="true" 
	    folderEntry="<%= ss_forum_entry %>" />
    </liferay:box>
  </c_rt:if>
  
  <c_rt:if test="<%= statePopUp %>">
<script language="javascript">
if (self.parent && self.parent.highlightLineById) {
	self.parent.highlightLineById("folderLine_<c:out value="${ss_forum_entry.id}"/>");
}
</script>
<%
	//Horizontal view
	if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL)) {
		toolbarWidth = entryWindowWidth - 6;
		request.setAttribute("ss_toolbarWidth", new Integer(toolbarWidth));
%>
    <liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
      <liferay:param name="box_width" value="<%= new Integer(entryWindowWidth).toString() %>" />
	<sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
	  configElement="<%= ss_forum_config %>" 
	  configJspStyle="<%= ss_forum_configJspStyle %>"
	  processThisItem="true" 
	  folderEntry="<%= ss_forum_entry %>" />
    </liferay:box>
<%
	
	//Iframe view
	} else if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_IFRAME)) {
		toolbarWidth = entryWindowWidth - 20;
		request.setAttribute("ss_toolbarWidth", new Integer(toolbarWidth));
%>
	<sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
	  configElement="<%= ss_forum_config %>" 
	  configJspStyle="<%= ss_forum_configJspStyle %>"
	  processThisItem="true" 
	  folderEntry="<%= ss_forum_entry %>" />
<%
	
	//Vertical view
	} else {
%>
	<sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
	  configElement="<%= ss_forum_config %>" 
	  configJspStyle="<%= ss_forum_configJspStyle %>"
	  processThisItem="true" 
	  folderEntry="<%=  ss_forum_entry %>" />
<%
	}
%>
  </c_rt:if>
</c_rt:if>
