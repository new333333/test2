<%
/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
String wsTreeName = "" + renderResponse.getNamespace();
%>

<c:set var="ss_windowTitle" value='<%= NLT.get( "administration.report.title.xss", "XSS Report" ) %>' scope="request"/>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
	/**
	 * 
	 */
	function handleCloseBtn()
	{
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
	
	}// end handleCloseBtn()
</script>

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<script type="text/javascript">
var m_workspaceEntryType;
var m_folderEnryType;
var m_profilesEntryType;
var m_unknownEntryType;
var m_noProblemsFound;
var m_cantInvokeAccessControl;
var m_modifyUrl;

// Load the various strings we will need.
m_unknownEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.unknownEntryType" /></ssf:escapeJavaScript>';
m_workspaceEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.workspaceEntryType" /></ssf:escapeJavaScript>';
m_folderEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.folderEntryType" /></ssf:escapeJavaScript>';
m_folderEntryEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.folderEntryEntryType" text="Entry" /></ssf:escapeJavaScript>';
m_userEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.userEntryType" text="User" /></ssf:escapeJavaScript>';
m_profilesEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.profilesEntryType" /></ssf:escapeJavaScript>';
m_cantInvokeAccessControl = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.cantInvokeAccessControl" /></ssf:escapeJavaScript>';
m_noProblemsFound = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.xss.noProblems" text="No XSS problems found." /></ssf:escapeJavaScript>';

m_modify = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.xss.modify" text="Modify" /></ssf:escapeJavaScript>';

// Get the url we need to invoke the page.
m_viewPermalinkUrl = "<ssf:url action="view_permalink" />";
m_modifyBinderUrl = "<ssf:url action="modify_binder" />";
m_modifyEntryUrl = "<ssf:url action="modify_folder_entry" />";

/**
 * Add an entry to the page for the given object the user has access to.
 */
function addXssReportDataToPage( entry )
{
	var table;
	var tr;
	var td;
	var span;
	var a;
	var typeName;
	var anchor;
	var br;

	// Get the table that holds the list of entries the user has access to.
	table = document.getElementById( 'xssReportTable' );

	// Create a <tr> for the entry.
	tr = table.insertRow( table.rows.length );

	// Remember the entry this row is dealing with.
	tr.n_entry = entry;

	// Add the entry's path to the table.
	td = tr.insertCell( 0 );
	td.style.whiteSpace = 'nowrap';
	anchor = document.createElement( 'a' );
	anchor.style.cursor = 'pointer';
	anchor.onclick =	function()
						{
							// Invoke the View operation for this page
							invokeXssPage( tr.n_entry );
						}
	span = document.createElement( 'span' );
	span.className = '';
	var title = entry.title;
	if (title.length > 80) {
		title = title.substr(0,79) + "...";
	}
	title = ss_replaceSubStrAll(title, "\"", "&quot;");
	title = ss_replaceSubStrAll(title, "<", "&lt;")
	title = ss_replaceSubStrAll(title, ">", "&gt;")
	updateElementsTextNode( span, title );
	anchor.appendChild( span );
	td.appendChild( anchor );
	if (entry.creatorName != null && entry.creatorName != "") {
		br = document.createElement( 'br' );
		span = document.createElement( 'span' );
		span.className = 'ss_fineprint';
		span.style.paddingLeft = "20px";
		updateElementsTextNode( span, entry.creatorName );
		td.appendChild( br );
		td.appendChild( span );
	}
	if (entry.path != null && entry.path != "") {
		anchor = document.createElement( 'a' );
		anchor.style.cursor = 'pointer';
		anchor.onclick =	function()
							{
								// Invoke the View operation for this path
								invokeXssPath( tr.n_entry );
							}
		span = document.createElement( 'span' );
		span.className = 'ss_fineprint';
		span.style.paddingLeft = "20px";
		var path = entry.path;
		// Remove the last folder name from the path
		if (path.lastIndexOf("/") >= 0) {
			path = path.substr(0, path.lastIndexOf("/"));
		}
		if (path.length > 120) {
			path = "..." + path.substr(path.length-120, path.length);
		}
		updateElementsTextNode( span, path );
		anchor.appendChild( span );
		br = document.createElement( 'br' );
		td.appendChild( br );
		td.appendChild( anchor );
	}

	// Add the entry's type to the table.
	td = tr.insertCell( 1 );
	td.style.whiteSpace = 'nowrap';
	span = document.createElement( 'span' );
	span.className = '';
	typeName = m_unknownEntryType;
	if ( entry.entityType == 'workspace' )
		typeName = m_workspaceEntryType;
	else if ( entry.entityType == 'folder' )
		typeName = m_folderEntryType;
	else if ( entry.entityType == 'profiles' )
		typeName = m_profilesEntryType;
	else if ( entry.entityType == 'folderEntry' )
		typeName = m_folderEntryEntryType;
	else if ( entry.entityType == 'user' )
		typeName = m_userEntryType;
	updateElementsTextNode( span, typeName );
	td.appendChild( span );

	// Add the entry's Modify command to the table.
	td = tr.insertCell( 2 );
	td.style.whiteSpace = 'nowrap';
	a = document.createElement( 'a' );
	a.href = "javascript: ";
	a.onclick =	function()
		{
			// Invoke the Modify operation for this page
			invokeXssModify( tr.n_entry );
		}
	span = document.createElement( 'span' );
	span.className = '';
	updateElementsTextNode( span, m_modify );
	a.appendChild( span );
	td.appendChild( a );
}// end addXssReportDataToPage()


/**
 * Issue an ajax request to get the xss report.
 */
function getXssReport()
{
	var	input;
	var	id;
	var url;
	var obj;

	// Remove any previous access data from the page.
	removeXssReportDataFromPage();
	
	// Display the wait indicator.
	showWaitIndicator();
	
	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'getXssReport'

	// Build the url used in the ajax request.
	url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );

	// Issue the ajax request.  The function handleResponseToGetXssReport() will be called
	// when we get the response to the request.
	ss_post(url, "reportForm", handleResponseToGetXssReport, "", "")
}// end getXssReport()


/**
 * This function gets called when we get a response to an ajax request to get an access report.
 */
function handleResponseToGetXssReport( responseData ) {
	// Hide the wait indicator.
	hideWaitIndicator();
	ss_stopSpinner();
	
	// Remove any previous access data from the page.
	removeXssReportDataFromPage();
	
	if ( responseData.status == 1 )
	{
		// If we get here then things worked.
		// Do we have any data?
		if ( responseData.reportData != null && responseData.reportData.length > 0 )
		{
			var i;
			var div;

			// Yes
			// Show the table that holds the list of xss problems.
			div = document.getElementById( 'xssReportDiv1' );
			div.style.display = 'block';
			div = document.getElementById( 'xssReportDiv2' );
			div.style.display = 'block';
			div.style.height = '400px';

			// responseData.reportData is an array of objects.  Each object holds information about
			// the xss object.
			for (i = 0; i < responseData.reportData.length; ++i)
			{
				var nextEntry;

				// Get the next entry in the report.
				nextEntry = responseData.reportData[i];

				// Add this entry to the table.
				addXssReportDataToPage( nextEntry );
			}// end for() 
		}
		else
		{
			// Tell the administrator that there are no xss problems.
			alert( m_noProblemsFound );
		}
	}
	else if ( responseData.status == -1 )
	{
		// If we get here an error happened retrieving the xss report.
		// Display the error.
		if ( responseData.errDesc != null )
		{
			var msg;

			alert( responseData.errDesc );
		}
	}
	else
	{
		// We should never get here.
		alert( 'Unknown xss status: ' + responseData.status );
	}
}// end handleResponseToGetXssReport()


/**
 * 
 */
function hideWaitIndicator()
{
	var span;

	// Hide the Retrieving access report...
	span = document.getElementById( 'progressIndicator' )
	span.style.display = 'none';
}// end hideWaitIndicator()


/**
 * Invoke the view operation for this page
 */
function invokeXssPage( entry ) {
	var url = m_viewPermalinkUrl + "&entityType=" + entry.entityType;
	 
	if ( entry.entityType == 'workspace' )
		url += "&binderId=" + entry.id;
	else if ( entry.entityType == 'folder' )
		url += "&binderId=" + entry.id;
	else if ( entry.entityType == 'profiles' )
		url += "&binderId=" + entry.id;
	else if ( entry.entityType == 'folderEntry' )
		url += "&entryId=" + entry.id;
	else if ( entry.entityType == 'user' )
		url += "&entryId=" + entry.id;

	// Invoke the entity page.
	if (confirm("<ssf:nlt tag='administration.report.xss.warnOnView' 
			text='Caution: Viewing an XSS infected item could trigger the XSS attack. \\n\\nViewing these items as an administrator is not recommended.\\n\\nProceed?'/>")) {
		ss_openUrlInPortlet( url, true, "", "");
	}
 
}// end invokeXssPage()

/**
 * Invoke the view operation for this page
 */
function invokeXssPath( entry ) {
	var url = m_viewPermalinkUrl + "&entityType=folder";
	url += "&binderId=" + entry.pathId;

	// Invoke the entity page.
	ss_openUrlInPortlet( url, true, "", "");
 
}// end invokeXssPath()

/**
 * Invoke the view operation for this page
 */
function invokeXssModify( entry ) {
	var url = "";
	 
	if ( entry.entityType == 'workspace' )
		url = m_modifyBinderUrl + "&binderType=workspace&binderId=" + entry.id;
	else if ( entry.entityType == 'folder' )
		url = m_modifyBinderUrl + "&binderType=folder&binderId=" + entry.id;
	else if ( entry.entityType == 'profiles' )
		url = m_modifyBinderUrl + "&binderType=profiles&binderId=" + entry.id;
	else if ( entry.entityType == 'folderEntry' )
		url = m_modifyEntryUrl + "&entryId=" + entry.id;
	else if ( entry.entityType == 'user' )
		url = m_modifyEntryUrl + "&entryId=" + entry.id;

	// Invoke the entity page.
	ss_openUrlInPortlet( url, true, "", "");
 
}// end invokeXssPage()


/**
 * Remove any access report data from the page.
 */
function removeXssReportDataFromPage()
{
	var table;
	var i;

	// Get the table that holds list of objects the user has access to.
	table = document.getElementById( 'xssReportTable' );

	// Remove all rows that have a property called 'n_entry'.
	for (i = 0; i < table.rows.length; ++i)
	{
		// Does this row have a n_entry property?
		if ( !(table.rows[i].n_entry === undefined) && table.rows[i].n_entry != null )
		{
			// Yes
			table.deleteRow( i );
			--i;
		}
	}
}// end removeXssReportDataFromPage()


/**
 * 
 */
function showWaitIndicator()
{
	var span;

	// Show the Retrieving access report...
	span = document.getElementById( 'progressIndicator' )
	span.style.display = '';
}// end showWaitIndicator()


/**
 *
 */
function ss_selectUser${renderResponse.namespace}(id, obj)
{
	// Issue an ajax request to get the access report for the given user.
	getXssReport( id );
}// end ss_selectUser()


</script>

	<div class="ss_pseudoPortal">
		<div class="ss_style ss_portlet">
			<c:set var="formName">${renderResponse.namespace}fm</c:set>
			<c:set var="formTitle"><%= NLT.get( "administration.report.title.xss", "XSS Report" ) %></c:set>

			<ssf:form title="${formTitle}" ignore="${GwtReport}">
				<form class="ss_style ss_form" 
					action="<ssf:url webPath="reportDownload"/>" 
					method="post" 
					name="reportForm"
					id="reportForm">

					<div style="margin-top: 2em;">
					    <div style="padding-bottom:20px;">
					      <span style="margin-right: 1em;">
					        <ssf:nlt tag="administration.report.xss.desc1" />
					      </span>
					    </div>
						<span style="margin-right: 1em;">
						  <ssf:nlt tag="administration.report.xss.selectBinders" text="Select the binders to be checked:" />
						</span>

<br>
<br>

<ssf:tree treeName="<%= wsTreeName %>" treeDocument="<%= ssWsDomTree %>"  
  rootOpen="true" topId="${ssWsDomTreeBinderId}" 
  multiSelect="<%= new ArrayList() %>" multiSelectPrefix="id" />

<br>

						<span id="progressIndicator" style="margin-left: 4em; display: none;">
							<ssf:nlt tag="administration.report.xss.retrievingReport" text="Retrieving report..." />
							<img src="<html:imagesPath/>pics/spinner_small.gif" align="absmiddle" border="0" >
						</span>
					</div>

				<div id="xssReportDiv1" style="display: none;">
					<table cellspacing="0" cellpadding="3">
						<tr>
							<td>
								<div style="margin-top: .7em;">
									<span id="header1Span">
									  <ssf:nlt tag="administration.report.xss.header1" text="The following items have potential XSS issues" />
									</span>
								</div>
								<div style="margin-bottom: .6em;">
									<span><ssf:nlt tag="administration.report.xss.header2" text="Clicking Modify and then OK will remove the XSS threat" /></span>
								</div>
							</td>
						</tr>
					</table>
				</div>
				<div id="xssReportDiv2" style="display: none; width: 90%; overflow: auto;">
					<table id="xssReportTable" width="100%" class="ss_style" cellspacing="0" cellpadding="3" style="border: 1px solid black;">
						<tr style="font-family: arial, sans-serif; background-color: #EDEEEC; border-bottom: 1px solid black; color: black; font-size: .75em; font-weight: bold;">
							<td align="left">
								<span>&nbsp;<ssf:nlt tag="administration.report.xss.col1" text="Title"/><span>
							</td>
							<td align="left">
								<span><ssf:nlt tag="administration.report.xss.col2" text="Type"/><span>
							</td>
							<td align="left">
								<span><ssf:nlt tag="administration.report.xss.col3" text="Modify"/><span>
							</td>
						</tr>
					</table>
				</div>

				<div style="margin-top: 2em !important;">
				<input type="submit" class="ss_submit" name="okBtn" 
				  value="<c:if test="${GwtReport == 'true'}"><ssf:nlt tag="button.runReport" text="Run Report"/></c:if><c:if test="${GwtReport != 'true'}"><ssf:nlt tag="button.ok" text="OK"/></c:if>" onclick="getXssReport();ss_startSpinner();return false;">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<c:if test="${GwtReport != 'true'}">
					<input type="submit" class="ss_submit" name="closeBtn" 
					 value="<ssf:nlt tag="button.close" text="Close"/>" onClick="return handleCloseBtn();">
				</c:if>
				</div>
					<sec:csrfInput />
			  </form>
			</ssf:form>
		</div>
	</div>
</body>
</html>
