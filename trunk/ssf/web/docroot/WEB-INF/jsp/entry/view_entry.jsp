<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<ssf:ifadapter>
<body>
</ssf:ifadapter>
<%@ page contentType="text/html" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>

<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.ef.ObjectKeys" %>
<%@ page import="com.sitescape.ef.web.WebKeys" %>

<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<%
String op = WebKeys.ACTION_VIEW_ENTRY;
String displayStyle = ssUser.getDisplayStyle();
if (displayStyle == null || displayStyle.equals("")) {
	displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
}

boolean statePopUp = false;
if (op.equals(WebKeys.ACTION_VIEW_ENTRY)) {
	statePopUp = true;
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
request.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
request.setAttribute("ss_entryWindowHeight", new Integer(entryWindowHeight));
%>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowHeight" type="java.lang.Integer" scope="request" />
<c:if test="<%= !op.equals(WebKeys.ACTION_VIEW_ENTRY) %>">
<c:set var="showEntryCallbackRoutine" value="showEntryInDiv" scope="request"/>
<c:set var="showEntryMessageRoutine" value="ss_showMessageInDiv" scope="request"/>
<script type="text/javascript">
var autoScroll = "<%= autoScroll %>";

function ss_showMessageInDiv(str) {
<%
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) || 
		displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>
	return false
<%
	}
%>
	showEntryInDiv(str)
}

function ss_showForumEntry(url, callbackRoutine) {
<%
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) || 
		displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>
	return ss_showForumEntryInIframe(url);
<%
	}
%>
	fetch_url(url, callbackRoutine);
}

function showEntryInDiv(str) {
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
    
    //If the entry div needs dynamic positioning, do it now
    if (self.ss_positionEntryDiv) {ss_positionEntryDiv();}
    
    if (str.indexOf('<body onLoad="self.location =') >= 0) {self.loaction.reload();}
    wObj1.style.display = "block";
    wObj2.innerHTML = str;
    wObj1.style.visibility = "visible";
    
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
var savedHighlightedLineBgColor = null;

function highlightLineById(id) {
    if (id == "") {return;}
    var obj = null
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(id)
    } else {
        obj = self.document.all[id]
    }
    
	if (highlightedLine != null) {
		highlightedLine.bgColor = savedHighlightedLineBgColor;
	}
	if (obj != null) {
		highlightedLine = obj;
		savedHighlightedLineBgColor = highlightedLine.bgColor;
		highlightedLine.bgColor = highlightBgColor;
	}
}


</script>
</c:if>

<c:if test="<%= op.equals(WebKeys.ACTION_VIEW_ENTRY) %>">
  <c:if test="<%= !statePopUp %>">
<script type="text/javascript">
function ss_loadEntry(obj,id) {
	self.location.href = obj.href;
	return false;
}
</script>
	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	    configElement="${ssConfigElement}" 
	    configJspStyle="${ssConfigJspStyle}"
	    processThisItem="true" 
	    entry="${ssEntry}" />
  </c:if>
  
  <c:if test="<%= statePopUp %>">
<script type="text/javascript">
if (self.parent && self.parent.highlightLineById) {
	self.parent.highlightLineById("folderLine_<c:out value="${ssEntry.id}"/>");
}
</script>
<%
	//Iframe view
	if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
%>
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${ssConfigElement}" 
	  configJspStyle="${ssConfigJspStyle}"
	  processThisItem="true" 
	  entry="${ssEntry}" />
<%
	
	//Popup view
	} else if (displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${ssConfigElement}" 
	  configJspStyle="${ssConfigJspStyle}"
	  processThisItem="true" 
	  entry="${ssEntry}" />
<%
	
	//Vertical view
	} else {
%>
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${ssConfigElement}" 
	  configJspStyle="${ssConfigJspStyle}"
	  processThisItem="true" 
	  entry="<%=  ssEntry %>" />
<%
	}
%>
  </c:if>
</c:if>


<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
