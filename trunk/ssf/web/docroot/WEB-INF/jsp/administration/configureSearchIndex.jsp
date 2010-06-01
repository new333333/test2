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
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_search_index") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
String wsTreeName = "" + renderResponse.getNamespace();
%>

<script type="text/javascript">
function handleCloseBtn() {
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			window.top.ss_closeAdministrationContentPanel();
			return false;
	<% 	}
		else { %>
			self.window.close();
			return true;
	<%	} %>
	
}  // end handleCloseBtn()

var currentIndexTabShowing = "manageIndex";
function ss_showIndexTab(id) {
	if (currentIndexTabShowing != null) {
		var divObj = self.document.getElementById(currentIndexTabShowing + "Div")
		divObj.style.display = "none";
		var tabObj = self.document.getElementById(currentIndexTabShowing + "Tab");
		tabObj.className = "wg-tab roundcornerSM";
		currentIndexTabShowing = null;
	}
	currentIndexTabShowing = id;
	var divObj = self.document.getElementById(currentIndexTabShowing + "Div");
	var tabObj = self.document.getElementById(currentIndexTabShowing + "Tab");
	divObj.style.display = "block";
	tabObj.className = "wg-tab roundcornerSM on";
}

var currentHoverOverTab = null;
function ss_hoverOverIndexTab(id) {
	if (currentIndexTabShowing != null) {
		var tabObj = self.document.getElementById(currentIndexTabShowing + "Tab");
		tabObj.className = "wg-tab roundcornerSM on";
	}
	if (currentHoverOverTab != null && currentHoverOverTab != currentIndexTabShowing) {
		var tabObj = self.document.getElementById(currentHoverOverTab + "Tab");
		tabObj.className = "wg-tab roundcornerSM";
		currentHoverOverTab = null;
	}
	currentHoverOverTab = id;
	var tabObj = self.document.getElementById(id + "Tab");
	if (currentHoverOverTab == currentIndexTabShowing) {
		tabObj.className = "wg-tab roundcornerSM selected-menu on";
	} else {
		tabObj.className = "wg-tab roundcornerSM selected-menu";
	}
}

function ss_hoverOverStoppedIndexTab(id) {
	if (currentHoverOverTab != null) {
		var tabObj = self.document.getElementById(currentHoverOverTab + "Tab");
		if (currentHoverOverTab == currentIndexTabShowing) {
			tabObj.className = "wg-tab roundcornerSM on";
		} else {
			tabObj.className = "wg-tab roundcornerSM";
		}
		currentHoverOverTab = null;
	}
	if (currentIndexTabShowing != null) {
		var tabObj = self.document.getElementById(currentIndexTabShowing + "Tab");
		tabObj.className = "wg-tab roundcornerSM on";
	}
}

var ss_indexTimeout = null;
var ss_indexStatusTicket = ss_random++;
var ss_checkStatusUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_status" />
	</ssf:url>";

function ss_getOperationStatus()
{
	ss_setupStatusMessageDiv();
	var ajaxRequest = new ss_AjaxRequest(ss_checkStatusUrl); //Create AjaxRequest object
	ajaxRequest.addKeyValue("ss_statusId",ss_indexStatusTicket);
	ajaxRequest.sendRequest();  //Send the request
	ss_indexTimeout = setTimeout(ss_getOperationStatus, 1000);
}

function ss_submitIndexingForm(formName) {
	var formObj = document.forms[formName];
	formObj.btnClicked.value = ss_buttonSelected;
	if (ss_buttonSelected == 'okBtn') {
		formObj.action = '<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>&ss_statusId='+ss_indexStatusTicket
		ss_submitFormViaAjax(formName, 'ss_indexingDone');
		ss_indexTimeout = setTimeout(ss_getOperationStatus, 1000);
		return false;
	} else {
		if(ss_indexTimeout) { clearTimeout(ss_indexTimeout); }
		formObj.action = '<ssf:url action="configure_index" actionUrl="true"/>'
		return true;
	}
}

function ss_indexingDone() {
	if(ss_indexTimeout) { clearTimeout(ss_indexTimeout); }
	ss_stopSpinner();
	ss_showPopupDivCentered('ss_indexing_done_div');
}


function <%= wsTreeName %>_showId(id, obj, action) {
	return ss_checkTree(obj, "ss_tree_checkbox<%= wsTreeName %>id" + id);
}

</script>

<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<ssf:form titleTag="administration.configure_search_index">
<br/>
<br/>
<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
  class="wg-tabs margintop3 marginbottom2">
  <div id="manageIndexTab" class="wg-tab roundcornerSM on" 
    onMouseOver="ss_hoverOverIndexTab('manageIndex');"
    onMouseOut="ss_hoverOverStoppedIndexTab('manageIndex');"
    onClick="ss_showIndexTab('manageIndex');">
    <ssf:nlt tag="administration.search.manage.index.tab"/>
  </div>
  <div id="manageOptimizeTab" class="wg-tab roundcornerSM" 
    onMouseOver="ss_hoverOverIndexTab('manageOptimize');"
    onMouseOut="ss_hoverOverStoppedIndexTab('manageOptimize');"
    onClick="ss_showIndexTab('manageOptimize');">
    <ssf:nlt tag="administration.search.manage.optimize.tab"/>
  </div>
</div>

<div id="manageIndexDiv" style="display:block;" class="wg-tab-content">
<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" 
	action="<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>" 
	method="post" 
	name="${renderResponse.namespace}fm"
	id="${renderResponse.namespace}fm"
	onSubmit="return ss_submitIndexingForm('${renderResponse.namespace}fm');" >
<input type="hidden" name="operation" value="index"/>

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="return handleCloseBtn();"/>
</div>
<br>
<span class="ss_largeprint ss_bold"><ssf:nlt tag="administration.configure.index.select"/></span>
<br>
<span class="ss_smallprint" style="padding-left:10px;"><ssf:nlt tag="administration.configure_search_index_hint"/></span>
<br>
<br>

<ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
  rootOpen="true" topId="${ssWsDomTreeBinderId}" 
  multiSelect="<%= new ArrayList() %>" multiSelectPrefix="id" />

<br>
<c:if test="${!empty ssSearchNodes}">
<br>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.configure.nodes.select" text="Select the nodes to apply the re-indexing to:"/></span>
<br>
<br>
<ssf:nlt tag="administration.configure.nodes.select.detail"/>
<br>
  <c:forEach var="node" items="${ssSearchNodes}">
    <input type="checkbox" name="searchNodeName" value="${node.nodeName}" <c:if test="${node.userModeAccess == 'offline' || !node.noDeferredUpdateLogRecords}">disabled</c:if>>
    ${node.title} (${node.nodeName}) - <ssf:nlt tag="administration.search.node.usermodeaccess.${node.userModeAccess}"/>, <ssf:nlt tag="administration.search.node.deferredupdatelog.enabled.${node.enableDeferredUpdateLog}"/>, <ssf:nlt tag="administration.search.node.nodeferredupdatelogrecords.${node.noDeferredUpdateLogRecords}"/>
    <br/>
  </c:forEach>
  <input type="hidden" name="searchNodesPresent" value="1"/>
</c:if>
<br>
<br>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" 
 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="return handleCloseBtn();">
</div>
<input type="hidden" name="btnClicked"/>
</form>
<br>
</td></tr></table>
</div>

<div id="manageOptimizeDiv" style="display:none;" class="wg-tab-content">
<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" 
	action="<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>" 
	method="post" 
	name="${renderResponse.namespace}fm2"
	id="${renderResponse.namespace}fm2"
	onSubmit="return ss_submitIndexingForm("${renderResponse.namespace}fm2");" >
<input type="hidden" name="operation" value="optimize"/>
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="return handleCloseBtn();"/>
</div>
<br>
<c:if test="${!empty ssSearchNodes}">
<br>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.configure.nodes.select" text="Select the nodes to apply the re-indexing to:"/></span>
<br>
<br>
<ssf:nlt tag="administration.configure.nodes.select.detail"/>
<br>
  <c:forEach var="node" items="${ssSearchNodes}">
    <input type="checkbox" name="searchNodeName" value="${node.nodeName}" <c:if test="${node.userModeAccess == 'offline' || !node.noDeferredUpdateLogRecords}">disabled</c:if>>
    ${node.title} (${node.nodeName}) - <ssf:nlt tag="administration.search.node.usermodeaccess.${node.userModeAccess}"/>, <ssf:nlt tag="administration.search.node.deferredupdatelog.enabled.${node.enableDeferredUpdateLog}"/>, <ssf:nlt tag="administration.search.node.nodeferredupdatelogrecords.${node.noDeferredUpdateLogRecords}"/>
    <br/>
  </c:forEach>
  <input type="hidden" name="searchNodesPresent" value="1"/>
</c:if>
<br>
<br>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" 
 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="return handleCloseBtn();">
</div>
<input type="hidden" name="btnClicked"/>
</form>
<br>
</td>
</tr>
</table>
</div>

<div id="ss_indexing_done_div" style="position:absolute;display:none;background-color:#fff;">
<span><ssf:nlt tag="index.finished"/></span>
<br/>
<br/>
<input type="button" value="<ssf:nlt tag="button.close"/>" onClick="return handleCloseBtn();" />
</div>
</ssf:form>
</div>
</div>
</body>
</html>