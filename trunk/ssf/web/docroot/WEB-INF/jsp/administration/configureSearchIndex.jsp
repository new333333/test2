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
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<%
String wsTreeName = "" + renderResponse.getNamespace();
%>
<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" 
	action="<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>" 
	method="post" 
	name="${renderResponse.namespace}fm"
	id="${renderResponse.namespace}fm"
	onSubmit="return ss_submitIndexingForm();" >

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.configure.index.select" text="Select the forums to be re-indexed:"/></span>
<br>
<br>

<script type="text/javascript">

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

function ss_submitIndexingForm() {
	var formObj = document.forms['${renderResponse.namespace}fm'];
	formObj.btnClicked.value = ss_buttonSelected;
	if (ss_buttonSelected == 'okBtn') {
		formObj.action = '<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>&ss_statusId='+ss_indexStatusTicket
		ss_submitFormViaAjax('${renderResponse.namespace}fm', 'ss_indexingDone');
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
	ss_showPopupDivCentered('ss_indexing_done_div${renderResponse.namespace}');
	setTimeout("self.window.close();", 4000)
}


function <%= wsTreeName %>_showId(id, obj, action) {
	return ss_checkTree(obj, "ss_tree_checkbox<%= wsTreeName %>id" + id);
}

</script>
<ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
  rootOpen="true" topId="${ssWsDomTreeBinderId}" 
  multiSelect="<%= new ArrayList() %>" multiSelectPrefix="id" />

<br>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.configure.nodes.select" text="Select the nodes to apply the re-indexing to:"/></span>
<br>
<br>
<ssf:nlt tag="administration.configure.nodes.select.detail"/>
<br>
<br>
<c:if test="${!empty ssSearchNodes}">
  <c:forEach var="node" items="${ssSearchNodes}">
    <input type="checkbox" name="searchNodeId" value="${node.id}" <c:if test="${node.accessMode == 'offline'}">disabled</c:if>>${node.title} (${node.id})<br/>
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
 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="self.window.close();return false;">
</div>
<input type="hidden" name="btnClicked"/>
</form>
<br>
</td></tr></table>

<div id="ss_indexing_done_div${renderResponse.namespace}" style="position:absolute;display:none;">
<span><ssf:nlt tag="index.finished"/></span>
</div>
</div>
</div>
</body>
</html>