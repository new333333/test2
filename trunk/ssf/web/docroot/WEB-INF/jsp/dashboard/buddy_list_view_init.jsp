<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<script type="text/javascript">
//How long to sleep between auto refresh; 
//  set this to 0 for no auto refresh (defaulted to 0 to keep iChain session timeouts from being voided)
//  set to 300000 for 5 minutes between refreshes
var ss_presenceTimeoutTimeToSleep = 0;

function ${ss_namespace}_${componentId}_getPresence(timeout) {
//alert("1");
//	var myDiv = document.getElementById('${ss_divId}');
//	if (myDiv == null) return;
//alert("2");
	ss_setupStatusMessageDiv();
	if (${ss_namespace}_${componentId}_presenceTimer != null) {
		clearTimeout(${ss_namespace}_${componentId}_presenceTimer);
		${ss_namespace}_${componentId}_presenceTimer = null;
	}
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__view_presence" 
    	actionUrl="false" 
    	binderId="${ssBinder.id}">
    	<ssf:param name="namespace" value="${ss_namespace}"/>
    	<ssf:param name="operation2" value="${componentId}"/>
    	<ssf:param name="userList" value="${ss_userList}"/>
    	</ssf:url>"
	ss_fetch_url(url, ss_moreDashboardSearchResultsCallback, '${ss_divId}');
	if (ss_presenceTimeoutTimeToSleep > 0) {
		${ss_namespace}_${componentId}_presenceTimer = setTimeout("${ss_namespace}_${componentId}_presenceTimout()", 300000);
	}
}

var ${ss_namespace}_${componentId}_presenceTimer = null;
function ${ss_namespace}_${componentId}_presenceTimout() {
	${ss_namespace}_${componentId}_getPresence("timeout");
}	
${ss_namespace}_${componentId}_presenceTimer = setTimeout("${ss_namespace}_${componentId}_presenceTimout()", 300000);
</script>
