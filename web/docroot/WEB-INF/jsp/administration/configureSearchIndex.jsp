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
<%
String wsTreeName = "search_" + renderResponse.getNamespace();
%>
<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" 
	action="<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>" 
	method="post" 
	name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm"
	id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm"
	onSubmit="return ss_submitIndexingForm();" >

<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" 
 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="ss_buttonSelect('closeBtn');">
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
	var formObj = document.forms['<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm'];
	formObj.btnClicked.value = ss_buttonSelected;
	if (ss_buttonSelected == 'okBtn') {
		formObj.action = '<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>&ss_statusId='+ss_indexStatusTicket
		ss_submitFormViaAjax('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm', 'ss_indexingDone');
		ss_indexTimeout = setTimeout(ss_getOperationStatus, 1000);
		return false;
	} else {
		if(ss_indexTimeout) { clearTimeout(ss_indexTimeout); }
		formObj.action = '<portlet:actionURL windowState="maximized"><portlet:param name="action" value="configure_index"/></portlet:actionURL>'
		return true;
	}
}

function ss_indexingDone() {
	if(ss_indexTimeout) { clearTimeout(ss_indexTimeout); }
	ss_buttonSelect('closeBtn');
	var formObj = document.forms['<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm'];
	formObj.btnClicked.value = 'closeBtn';
	formObj.action = '<portlet:actionURL windowState="maximized"><portlet:param name="action" value="configure_index"/></portlet:actionURL>'
	formObj.submit();
}


function <%= wsTreeName %>_showId(forum, obj, action) {
	if (obj.ownerDocument) {
		var cDocument = obj.ownerDocument;
	} else if (obj.document) {
		cDocument = obj.document;
	}
	if (cDocument) {
		var r = cDocument.getElementById("ss_tree_checkbox<%= wsTreeName %>" + action + forum);
		if (r) {
			if (r.checked !== undefined) {
				r.checked = !r.checked;
			}
			if (r.onclick !== undefined) {
				r.onclick();
			}
		}
	}
	return false;
}

</script>
<ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
  rootOpen="true" topId="${ssWsDomTreeBinderId}" 
  multiSelect="<%= new ArrayList() %>" multiSelectPrefix="$type" />

<br>
<br>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="closeBtn" 
 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="ss_buttonSelect('closeBtn');">
</div>
<input type="hidden" name="btnClicked"/>
</form>
<br>
</td></tr></table>

