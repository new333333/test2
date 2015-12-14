<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("teaming.live") %>' scope="request"/>
<c:if test="<%= !org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>">
  <c:set var="ss_windowTitle" value='<%= NLT.get("teaming.live.kablink") %>' scope="request"/>
</c:if>
<%@ include file="/WEB-INF/jsp/mobile/teaming_live_init.jsp" %>

<script type="text/javascript">

var updateTimer = null;
var pollTimer = null;
var newItemsCount = 0;
var newItemsCountLast = 0;
var pollIntervalMinutes = <%= String.valueOf(org.kablink.teaming.util.feed.TeamingFeedCache.feedClientUpdateInterval) %>;
var pollInterval = pollIntervalMinutes * 60 * 1000;
var titleToggleCount = -1;
var titleToggleTimer = null;

self.onfocus = 
self.document.onfocus = function(e) {
	titleToggleCount = -1
	if (titleToggleTimer != null) clearTimeout(titleToggleTimer);
	var titleText = "<ssf:nlt tag="teaming.live"/>";
	var titleObj = document.getElementsByTagName("title").item(0);
	try {
		titleObj.innerHTML = titleText;
	} catch(e) {}
};

function initiatePolling() {
	if (pollTimer != null) clearTimeout(pollTimer);
	pollTimer = setTimeout("pollForActivity();", pollInterval);
}

function pollForActivity() {
	if (pollTimer != null) clearTimeout(pollTimer);
	pollTimer = null;
	var url = "<ssf:url adapter="true" portletName="ss_forum" 
						action="__ajax_mobile" 
						operation="teaming_live_check_for_activity" 
						actionUrl="false" />";
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setPostRequest(postPollForActivity);
	ajaxRequest.sendRequest();  //Send the request
}

function postPollForActivity() {
	var pollStatusObj = self.document.getElementById("poll_status");
	
	if (pollTimer != null) clearTimeout(pollTimer);
	pollTimer = setTimeout("pollForActivity();", pollInterval);
	
	if (pollStatusObj.innerHTML != '' && pollStatusObj.innerHTML == 'reload') {
		setTimeout("self.location.reload(true);", 20);
	} else if (pollStatusObj.innerHTML != '' && pollStatusObj.innerHTML == 'update') {
		if (updateTimer != null) clearTimeout(updateTimer);
		updateTimer = setTimeout("updateFeedDisplay();", 20);
	}
}

function updateFeedDisplay() {
	if (updateTimer != null) clearTimeout(updateTimer);
	updateTimer = null;
	var pageNumber = parseInt("${ss_pageNumber}");
	if (pageNumber > 0) {
		setTimeout("self.location.reload(true);", 20);
	} else {
		var url = "<ssf:url adapter="true" portletName="ss_forum" 
						action="__ajax_mobile" 
						operation="view_teaming_live_update" 
						actionUrl="false" />";
		ss_fetch_url(url, postPollForUpdate, "teaming_live_data");
	}
}

function postPollForUpdate(s, divId) {
	var targetDiv = document.getElementById(divId);
	if (targetDiv) {
		targetDiv.innerHTML = s;
	}
	ss_executeJavascript(targetDiv);
	
	titleToggleCount = 0;
	toggleTitle();
}

function toggleTitle() {
	//scroll the title
	if (titleToggleTimer != null) clearTimeout(titleToggleTimer);
	titleToggleTimer = null;
	if (titleToggleCount < 0) return;
	
  <c:if test="<%= org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>">
	var titleText = "<ssf:nlt tag="teaming.live.items"/>... "
  </c:if>	
  <c:if test="<%= !org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition() %>">
	var titleText = "<ssf:nlt tag="teaming.live.kablink.items"/>... "
  </c:if>	

	var i = titleToggleCount%titleText.length;
	var title1 = titleText.substr(0,i);
	var title2 = titleText.substr(i,titleText.length);
	var newTitle = ss_trim(title2 + title1);
	if (title2.charAt(0) == ' ') newTitle = ss_replaceSubStr(newTitle, "...", "....");
	if (title1.charAt(title1.length -1) == ' ') newTitle = ss_replaceSubStr(newTitle, "...", "....");
	var titleObj = document.getElementsByTagName("title").item(0);
	try {
		titleObj.innerHTML = newTitle;
	} catch(e) {}
	titleToggleCount++;
	if (title2.charAt(0) == ' ') titleToggleCount++;
	titleToggleTimer = setTimeout("toggleTitle();", 400);
}

function wopen(url, name, w, h) {
    var wFudge = 40;
    var hFudge = 40;
    wleft = screen.width - w - wFudge;
    wtop = screen.height - h - hFudge;
    
    var win = window.open(url,
    name,
    'width=' + w + ', height=' + h + ', ' +
    'left=' + wleft + ', top=' + wtop + ', ' +
    'location=no, menubar=no, ' +
    'status=no, toolbar=no, scrollbars=no, resizable=no');
    // Just in case width and height are ignored
    win.resizeTo(w, h);
    // Just in case left and top are ignored
    win.moveTo(wleft, wtop);
    win.blur();
}

ss_createOnLoadObj("initiatePolling", initiatePolling);
</script>

<c:set var="ss_pageTitle" value="${ss_windowTitle}" scope="request" />
<%@ include file="/WEB-INF/jsp/mobile/teaming_live_masthead.jsp" %>

<div class="content">

<c:set var="ss_microblog_post_url" scope="request">
    <ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile" 
			operation="view_teaming_live" 
			actionUrl="true" />
</c:set>
<%@ include file="/WEB-INF/jsp/mobile/teaming_live_action_bar.jsp" %>

<div class="folders">

    <div class="folder-head">
		<form id="whatsNewForm" name="whatsNewForm" style="display:inline;"
		  method="post" 
		  action="<ssf:url adapter="true" portletName="ss_forum" 
		    action="__ajax_mobile" operation="view_teaming_live" actionUrl="true" />"
		>
          <div class="feedselect">
		  	<span class="feedlabel"><ssf:nlt tag="teaming.live.feedLabel"/></span>

		    <select size="1" name="whats_new" 
		      onChange="self.document.getElementById('whatsNewForm').submit();">
		    
		      <option value="teams" <c:if test="${ss_whatsNewType == 'teams'}">selected</c:if>
		      ><ssf:nlt tag="navigation.myTeams"/></option>
		      
		      <option value="tracked" <c:if test="${ss_whatsNewType == 'tracked'}">selected</c:if>
		      ><ssf:nlt tag="mobile.whatsNewTracked"/></option>
		      
		      <option value="site" <c:if test="${ss_whatsNewType == 'site'}">selected</c:if>
		      ><ssf:nlt tag="mobile.whatsNewSite"/></option>
		      
		      <option value="microblog" <c:if test="${ss_whatsNewType == 'microblog'}">selected</c:if>
		      ><ssf:nlt tag="mobile.whatsNewMicroBlogs"/></option>
		      
		    </select>
		    <input type="hidden" name="whatsNewBtn" value="whats_new"/>
				<span class="lastupdated-page">
				  <ssf:nlt tag="teaming.live.page">
					<ssf:param name="value" useBody="true">${ss_pageNumber+1}</ssf:param>
				  </ssf:nlt>
				</span>
			<span class="pagingbuttons">
				<c:if test="${!empty ss_prevPage}">
			  		<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${ssBinder.id}" 
						action="__ajax_mobile" 
						operation="view_teaming_live" 
						actionUrl="false" ><ssf:param 
						name="quickSearch" value="true"/><ssf:param 
						name="searchText" useBody="true">${ss_searchText}</ssf:param><ssf:param 
						name="tabId" value="${ss_tab_id}"/><ssf:param 
						name="pageNumber" value="${ss_pageNumber-1}"/><ssf:param 
						name="ss_queryName" value="${ss_queryName}" /></ssf:url>"
			  		><img border="0" src="<html:rootPath/>images/pics/nl_left_noborder_20.png"/></a>
				</c:if>
				<c:if test="${empty ss_prevPage}">
			  		  <img border="0" src="<html:rootPath/>images/pics/nl_left_dis_20.png"/>
				</c:if>
				<c:if test="${!empty ss_nextPage}">
			  		<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${ssBinder.id}" 
						action="__ajax_mobile" 
						operation="view_teaming_live" 
						actionUrl="false" ><ssf:param 
						name="quickSearch" value="true"/><ssf:param 
						name="searchText" useBody="true">${ss_searchText}</ssf:param><ssf:param 
						name="tabId" value="${ss_tab_id}"/><ssf:param 
						name="pageNumber" value="${ss_pageNumber+1}"/><ssf:param 
						name="ss_queryName" value="${ss_queryName}" /></ssf:url>"
			  		><img border="0" src="<html:rootPath/>images/pics/nl_right_noborder_20.png"/></a>
				</c:if>
				<c:if test="${empty ss_nextPage}">
			  		  <img border="0" src="<html:rootPath/>images/pics/nl_left_dis_20.png"/>
				</c:if>
				</span>
			</div>	
		</form>
    </div>

  <c:set var="now" value="<%=new java.util.Date()%>" />
	  <div class="folder-content" align="center" width="100%">
		  <span id="last_updated" 
			class="lastupdated" ><ssf:nlt tag="teaming.live.updated"><ssf:param 
			  name="value" useBody="true"><fmt:formatDate 
			  timeZone="${ssUser.timeZone.ID}"
			  value="${now}" type="time" timeStyle="short" />
			  </ssf:param></ssf:nlt></span>
		  <span class="lastupdated ss_mobile_light">
			<ssf:nlt tag="teaming.live.willBeUpdated"/>
		  </span>
	  </div>
    
  <div id="teaming_live_data" class="folder-content">
    <%@ include file="/WEB-INF/jsp/mobile/teaming_live_update_data.jsp" %>
  </div>

</div>

<div id="poll_status" style="visibility:hidden; display:none;">
</div>

</body>
</html>
