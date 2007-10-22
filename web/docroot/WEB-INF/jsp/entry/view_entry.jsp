<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>

<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.ObjectKeys" %>
<%@ page import="com.sitescape.team.web.WebKeys" %>

<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssFolder" type="com.sitescape.team.domain.Binder" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
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
request.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
request.setAttribute("ss_entryWindowTop", new Integer(entryWindowTop));
request.setAttribute("ss_entryWindowLeft", new Integer(entryWindowLeft));
request.setAttribute("ss_entryWindowHeight", new Integer(entryWindowHeight));
%>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowTop" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowLeft" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowHeight" type="java.lang.Integer" scope="request" />
<c:if test="<%= !op.equals(WebKeys.ACTION_VIEW_ENTRY) %>">
<c:set var="showEntryCallbackRoutine" value="showEntryInDiv" scope="request"/>
<script type="text/javascript">
var autoScroll = "<%= autoScroll %>";

function ss_showForumEntry(url, callbackRoutine, isDashboard, entityType) {
<%
if (displayStyle == null || displayStyle.equals("") || 
    displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) || 
	displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP) ||
	displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
		if (isDashboard == "yes") {
<%		
	if (displayStyle == null || displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) 
	    || displayStyle.equals("") 
	  	|| displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL) ) {
%>		
			//Dashboard iframe or vertical; show as overlay
			return ss_showForumEntryInIframe_Overlay(url);
<%		
	} else if ( displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>		
			//Dashboard popup; popup in new window
			return ss_showForumEntryInIframe_Popup(entityType);			
<%
	}
%>
		} else {
			//Not dashboard; show normal action
			return ss_showForumEntryInIframe(url);
		}
<%
}
%>
	//Not a normal view; probably accessible; show in same window
	self.location.href=url;
}

function showEntryInDiv(str) {
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('ss_showentryhighwatermark');
    
    var wObj1 = null
    var wObj2 = null
    wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj2 = self.document.getElementById('ss_showentry')
    
    //If the entry div needs dynamic positioning, do it now
    if (self.ss_positionEntryDiv) {ss_positionEntryDiv();}
    
    if (str.indexOf('<body onLoad="self.location =') >= 0) {self.location.reload();}
    wObj1.style.display = "block";
    wObj2.innerHTML = str;
    wObj1.style.visibility = "visible";
    
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('ss_showentryhighwatermark');
    
    //Get the position of the div displaying the entry
    if (autoScroll == "true") {
	    var entryY = ss_getDivTop('ss_showentrydiv')
	    var entryH = ss_getDivHeight('ss_showentrydiv')
	    var bodyY = ss_getScrollXY()[1]
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

var ss_highlightBgColor = "${ss_folder_line_highlight_color}"
var ss_highlightedLine = null;
var ss_savedHighlightedLineBgColor = null;

//Define the url of this page in case the entry needs to reload this page
var ss_reloadUrl = "${ss_reloadUrl}";


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
if (self.parent && self.parent.ss_highlightLineById) {
	self.parent.ss_highlightLineById("folderLine_<c:out value="${ssEntry.id}"/>");
}
</script>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<%
	//Iframe view
	if (displayStyle == null || displayStyle.equals("") || 
	    displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) {
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
