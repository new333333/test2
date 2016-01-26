<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<c:set var="helpGuideName" value="admin" scope="request" />
<c:set var="helpPageId" value="searchindex" scope="request" />
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
String wsTreeName = "" + renderResponse.getNamespace();
%>
<body class="ss_style_body tundra">

<script type="text/javascript">
function handleCloseBtn() {
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			if ( window.parent.ss_closeAdministrationContentPanel ) {
				window.parent.ss_closeAdministrationContentPanel();
			} else {
				ss_cancelButtonCloseWindow();
			}

			return false;
	<% 	}
		else { %>
			ss_cancelButtonCloseWindow();
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
	ajaxRequest.addKeyValue("ss_style","background:#cecece;padding:10px;border: 2px solid #ababab;-moz-border-radius: 5px;border-radius: 5px;-webkit-border-radius: 5px;");
	ajaxRequest.sendRequest();  //Send the request
	ss_indexTimeout = setTimeout(ss_getOperationStatus, 1000);
	var statusMessage = ss_getStatusMessage();
	if (statusMessage == "completed") {
		try {
			ss_hideSpinnerImg();
		} catch(e) {}
	}
}

var alreadySubmitted = false;
function ss_submitIndexingForm( indexing, formName, callbackName ) {
	if (alreadySubmitted) {
		//Don't ever resubmit this form
		return false;
	}
	var formObj = document.forms[formName];
	formObj.btnClicked.value = ss_buttonSelected;
	if (ss_buttonSelected == 'okBtn') {
		// Is the form being submitted to do a re-index?
		if (indexing) {
			// Yes!  Has anything been selected to re-index?
			var haveChecks = false;
			var inputs = formObj.getElementsByTagName("input");
			var c = ((null == inputs) ? 0 : inputs.length);
			for (var i = 0; i < c; i += 1) {
				var input = inputs[i];
				if (input.type != "checkbox") {
					continue;
				}
				if (input.checked) {
					haveChecks = true;
					break;
				}
			}			
			if (!haveChecks) {
				// No!  Tell the user and bail.
				ss_stopSpinner();
				alert("<ssf:escapeJavaScript><ssf:nlt tag="administration.configure.error.nochecks"/></ssf:escapeJavaScript>");
				return false;			
			}
		}
		//Is this a request to optimize?
		if (callbackName == 'ss_optimizationDone') {
			//See if this was really just scheduling optimization
			if (!formObj['runnow'].checked && formObj['enabled'].checked) {
				callbackName = 'ss_optimizationScheduled'
			}
		}
		
		formObj.action = '<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>&ss_statusId='+ss_indexStatusTicket
		alreadySubmitted = true;
		ss_submitFormViaAjax(formName, callbackName );
		ss_indexTimeout = setTimeout(ss_getOperationStatus, 1000);
		for (var i = 0; i < formObj.length; i++) {
			if (formObj.elements[i].name = "okBtn") {
				formObj.elements[i].disabled = "true";
			}
		}

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


/**
 * Display the "Optimization has finished" div.
 */
 function ss_optimizationDone()
 {
 	if ( ss_indexTimeout )
 	{
 		clearTimeout(ss_indexTimeout);
 	}

 	ss_stopSpinner();
 	ss_showPopupDivCentered( 'ss_optimization_done_div' );
 }// end ss_optimizationDone()

 function ss_optimizationScheduled()
 {
 	if ( ss_indexTimeout )
 	{
 		clearTimeout(ss_indexTimeout);
 	}

 	ss_stopSpinner();
 	ss_showPopupDivCentered( 'ss_optimization_scheduled_div' );
 }// end ss_optimizationScheduled()


function <%= wsTreeName %>_showId(id, obj, action) {
	return ss_checkTree(obj, "ss_tree_checkbox<%= wsTreeName %>id" + id);
}

</script>

<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<ssf:form titleTag="administration.configure_search_index">
<br/>
<br/>
<div style="text-align: left; margin: 0 10px 0 0; border: 0px none;" 
  class="wg-tabs margintop3 marginbottom2">
  <table cellpadding="0" cellspacing="0"><tr><td><div id="manageIndexTab" class="wg-tab roundcornerSM on" 
    onMouseOver="ss_hoverOverIndexTab('manageIndex');"
    onMouseOut="ss_hoverOverStoppedIndexTab('manageIndex');"
    onClick="ss_showIndexTab('manageIndex');">
    <span><ssf:nlt tag="administration.search.manage.index.tab"/></span>
  </div></td><td>
  <div id="manageOptimizeTab" class="wg-tab roundcornerSM" 
    onMouseOver="ss_hoverOverIndexTab('manageOptimize');"
    onMouseOut="ss_hoverOverStoppedIndexTab('manageOptimize');"
    onClick="ss_showIndexTab('manageOptimize');">
    <span><ssf:nlt tag="administration.search.manage.optimize.tab"/></span>
  </div></td></tr></table>
</div>
<div class="ss_clear"></div>

<div id="manageIndexDiv" style="display:block;" class="wg-tab-content">
<table class="ss_style" width="100%"><tr><td>

<form class="ss_style ss_form" 
	action="<ssf:url adapter="true" portletName="ss_administration" action="configure_index" actionUrl="true"></ssf:url>" 
	method="post" 
	name="${renderResponse.namespace}fm"
	id="${renderResponse.namespace}fm"
	onSubmit="return ss_submitIndexingForm( true, '${renderResponse.namespace}fm', 'ss_indexingDone' );" >
<input type="hidden" name="operation" value="index"/>

<c:if test="${ssSearchSafeToIndex}">
	<div class="margintop3 ss_buttonBarRight">
	<input type="submit" class="ss_submit" name="okBtn" 
	  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
			  onClick="return handleCloseBtn();"/>
	</div>
	<br>
</c:if>
<c:if test="${!ssSearchSafeToIndex}">
	<span class="ss_largeprint ss_bold"><ssf:nlt tag="administration.configure.index.indexInProgress"/></span>
	<br>
	<br>
</c:if>
<c:if test="${ssSearchSafeToIndex}">
	<input type="checkbox" name="indexAll"/>
	<span class="ss_largeprint ss_bold"><ssf:nlt tag="administration.configure.index.selectAll"/></span>
	<br>
	<br>
	<br>
	<span class="ss_largeprint ss_bold"><ssf:nlt tag="administration.configure.index.select"/></span>
	<br>
	<span class="ss_smallprint" style="padding-left:10px;"><ssf:nlt tag="administration.configure_search_index_hint"/></span>
	
	<div class="marginleft1 ss_subsection">
		<ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
		  rootOpen="true" topId="${ssWsDomTreeBinderId}" 
		  multiSelect="<%= new ArrayList() %>" multiSelectPrefix="id" />
	</div>
</c:if>
<div class="margintop2">
<c:if test="${!empty ssSearchNodes}">
</div>
<div class="margintop2">
<span class="ss_bold"><ssf:nlt tag="administration.configure.nodes.select" text="Select the nodes to apply the re-indexing to:"/></span>
</div>
<br>
<ssf:nlt tag="administration.configure.nodes.select.detail"/>
<br>
<br>
  <c:forEach var="node" items="${ssSearchNodes}">
    <input type="checkbox" name="searchNodeName" value="${node.nodeName}" <c:if test="${node.userModeAccess == 'noaccess' || !node.noDeferredUpdateLogRecords || node.reindexingInProgress}">disabled</c:if>>
    ${node.title} (${node.nodeName}) - <ssf:nlt tag="administration.search.node.usermodeaccess.${node.userModeAccess}"/>, <ssf:nlt tag="administration.search.node.deferredupdatelog.enabled.${node.enableDeferredUpdateLog}"/>, <ssf:nlt tag="administration.search.node.nodeferredupdatelogrecords.${node.noDeferredUpdateLogRecords}"/>
    <br/>
  </c:forEach>
  <input type="hidden" name="searchNodesPresent" value="1"/>
</c:if>
<c:if test="${ssSearchSafeToIndex || !empty ssSearchNodes}">
	<div class="margintop3 ss_buttonBarRight">
	<input type="submit" class="ss_submit" name="okBtn" 
	  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
	<input type="submit" class="ss_submit" name="closeBtn" 
	 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="return handleCloseBtn();">
	</div>
</c:if>
<c:if test="${!ssSearchSafeToIndex && empty ssSearchNodes}">
	<div class="margintop3 ss_buttonBarRight">
	<input type="submit" class="ss_submit" name="closeBtn" 
	 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="return handleCloseBtn();">
	</div>
</c:if>
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
	onSubmit="return ss_submitIndexingForm( false, '${renderResponse.namespace}fm2', 'ss_optimizationDone' );" >
<input type="hidden" name="operation" value="optimize"/>
<br>
<br>
<div>
  <span class="ss_bold"><ssf:nlt tag="administration.search.index.optimize" /></span>
</div>
<br>
<div>
  <span><ssf:nlt tag="administration.search.index.optimize2" /></span>
</div>

<table class="ss_style margintop3" border="0" cellspacing="0" cellpadding="3" width="100%">
	<tr>
		<td>
			<input type="checkbox" id="runnow" name="runnow"
			<c:if test="${runnow}"> checked="checked" </c:if> /> 
		</td>
		<td>
		  <label for="runnow"><span class="ss_labelRight ss_normal ss_nowrap"><ssf:nlt
			tag="index.optimization.schedule.run.now" /></span><br /> </label>
		</td>
		<td width="100%">&nbsp;</td>
	</tr>
	<tr>
		<td>
		  <input type="checkbox" id="enabled" name="enabled"
			<c:if test="${ssScheduleInfo.enabled}">checked</c:if> /> 
		</td>
		<td>
		  <label for="enabled"><span class="ss_labelRight ss_normal ss_nowrap"><ssf:nlt
			tag="index.optimization.schedule.enable" /></span><br /></label>
		</td>
		<td width="100%">&nbsp;</td>
	</tr>
</table>

<div class="margintop2 ss_subsection" style="margin-left: 2.5em;">
	<ssf:expandableArea title='<%= NLT.get("index.optimization.schedule") %>' initOpen="true">
		<c:set var="schedule" value="${ssScheduleInfo.schedule}" />
		<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
	</ssf:expandableArea>
</div>	

<c:if test="${!empty ssSearchNodes}">
<br>
<br>
<div>
  <span class="ss_bold"><ssf:nlt tag="administration.configure.nodes.selectOptimize" /></span>
</div>
<br>
  <c:forEach var="node" items="${ssSearchNodes}">
    <input type="checkbox" name="searchNodeName" value="${node.nodeName}" 
    <c:if test="${node.userModeAccess == 'noaccess' || !node.noDeferredUpdateLogRecords || node.reindexingInProgress}">disabled</c:if><c:if test="${ssScheduleInfo.nodeSelectionMap[node.nodeName]}">checked</c:if>>
    ${node.title} (${node.nodeName}) - <ssf:nlt tag="administration.search.node.usermodeaccess.${node.userModeAccess}"/>, <ssf:nlt tag="administration.search.node.deferredupdatelog.enabled.${node.enableDeferredUpdateLog}"/>, <ssf:nlt tag="administration.search.node.nodeferredupdatelogrecords.${node.noDeferredUpdateLogRecords}"/>
    <br/>
    <c:if test="${node.reindexingInProgress}">
	    <span class="ss_smallprint ss_italic"><ssf:nlt tag="administration.configure.index.indexInProgressNode"/></span>
	    <br/>
    </c:if>
  </c:forEach>
  <input type="hidden" name="searchNodesPresent" value="1"/>
</c:if>

<div class="margintop3 ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" 
  value="<ssf:nlt tag="button.ok" text="OK"/>" onclick="ss_buttonSelect('okBtn');ss_startSpinner();">
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

<div id="ss_indexing_done_div" class="teamingDlgBox" style="position:absolute;display:none;">
	<div class="popupContent" style="padding: 20px;">
		<span class="ss_bold"><ssf:nlt tag="index.finished"/></span>
		<div class="margintop3" style="text-align: center;"><input type="button" value="<ssf:nlt tag="button.close"/>" onClick="return handleCloseBtn();" /></div>
	</div>
</div>

<div id="ss_optimization_done_div" class="teamingDlgBox" style="position:absolute; display:none;">
	<div class="popupContent" style="padding: 20px;">
		<span><ssf:nlt tag="index.optimization.finished"/></span>
		<div class="margintop3" style="text-align: center;"><input type="button" value="<ssf:nlt tag="button.close"/>" onClick="return handleCloseBtn();" /></div>
	</div>
</div>

<div id="ss_optimization_scheduled_div" class="teamingDlgBox" style="position:absolute; display:none;">
	<div class="popupContent" style="padding: 20px;">
		<span><ssf:nlt tag="index.optimization.scheduled"/></span>
		<div class="margintop3" style="text-align: center;"><input type="button" value="<ssf:nlt tag="button.close"/>" onClick="return handleCloseBtn();" /></div>
	</div>
</div>






</ssf:form>

<div id="ss_status_message">
</div>
</div>
</div>
</body>
</html>