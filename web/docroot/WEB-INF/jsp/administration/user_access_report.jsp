<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<c:set var="ss_windowTitle" value='<%= NLT.get( "administration.report.title.user_access" ) %>' scope="request"/>

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
var m_noAccess;
var m_cantInvokeAccessControl;
var m_accessControlUrl;

ss_createOnLoadObj( 'userAccessReport', onLoadEventHandler );


/**
 * Add an entry to the page for the given object the user has access to.
 */
function addAccessReportDataToPage( entry )
{
	var table;
	var tr;
	var td;
	var typeName;
	var anchor;

	// Get the table that holds the list of entries the user has access to.
	table = document.getElementById( 'accessReportTable' );

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
							// Invoke the Access Control page to let the user change the access control
							// on this item.
							invokeAccessControlPage( tr.n_entry );
						}
	span = document.createElement( 'span' );
	span.className = '';
	updateElementsTextNode( span, entry.path );
	anchor.appendChild( span );
	td.appendChild( anchor );

	// Add the entry's type to the table.
	td = tr.insertCell( 1 );
	td.style.whiteSpace = 'nowrap';
	span = document.createElement( 'span' );
	span.className = '';
	typeName = m_unknownEntryType;
	if ( entry.entryType == 'workspace' )
		typeName = m_workspaceEntryType;
	else if ( entry.entryType == 'folder' )
		typeName = m_folderEntryType;
	else if ( entry.entryType == 'profiles' )
		typeName = m_profilesEntryType;
	updateElementsTextNode( span, typeName );
	td.appendChild( span );
}// end addAccessReportDataToPage()


/**
 * Issue an ajax request to get an access report for the given user.
 */
function getAccessReport( userId )
{
	var	input;
	var	id;
	var url;
	var obj;

	// Display the wait indicator.
	showWaitIndicator();
	
	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'getUserAccessReport'
	obj.userId = userId;

	// Build the url used in the ajax request.
	url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );

	// Issue the ajax request.  The function handleResponseToGetAccessReport() will be called
	// when we get the response to the request.
	ss_get_url( url, handleResponseToGetAccessReport );
}// end getAccessReport()


/**
 * This function gets called when we get a response to an ajax request to get an access report.
 */
 function handleResponseToGetAccessReport( responseData )
 {
 	// Hide the wait indicator.
 	hideWaitIndicator();
 	
 	// Remove any previous access data from the page.
 	removeAccessReportDataFromPage();
 	if ( responseData.status == 1 )
 	{
 		// If we get here then things worked.
 		// Do we have any data?
 		if ( responseData.reportData != null && responseData.reportData.length > 0 )
 		{
 			var i;
 			var div;

 			// Yes
 			// Show the table that holds the list of objects the user has access to.
 			div = document.getElementById( 'accessReportDiv1' );
 			div.style.display = 'block';
 			div = document.getElementById( 'accessReportDiv2' );
 			div.style.display = 'block';
 			// responseData.reportData is an array of objects.  Each object holds information about
 			// the object the user has access to.
 			for (i = 0; i < responseData.reportData.length; ++i)
 			{
 				var nextEntry;

 				// Get the next entry in the report.
 				nextEntry = responseData.reportData[i];

 				// Add this entry to the table.
 				addAccessReportDataToPage( nextEntry );
 			}// end for() 
 		}
 		else
 		{
 			// Tell the administrator that the selected user doesn't have access to anything.
 			alert( m_noAccess );
 		}
 	}
 	else if ( responseData.status == -1 )
 	{
 		// If we get here an error happened retrieving the access report.
 		// Display the error.
 		if ( responseData.errDesc != null )
 		{
 			var msg;

 			alert( responseData.errDesc );
 		}
 		else
 		{
 			// We should never get here.
 			alert( 'Unknown access report status: ' + responseData.status );
 		}
 	}
 	else
 	{
 		// We should never get here.
 		alert( 'Unknown access report status: ' + responseData.status );
 	}
 }// end handleResponseToGetAccessReport()


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
 * Invoke the Access Control page for the given item.
 */
function invokeAccessControlPage( entry )
{
	 // We can only invoke the Access Control Page on a workspace, folder or profiles binder
	 if ( entry.entryType == 'workspace' || entry.entryType == 'folder' || entry.entryType == 'profiles' )
	 {
		 var url;

		 // Construct the url needed to invoke the Access Control page.
		 url = m_accessControlUrl + '&workAreaId=' + entry.id + '&workAreaType=' + entry.entryType;

		 // Invoke the Access Control page.
		 ss_openUrlInPortlet( url, true, '', '');
	 }
	 else
	 {
		 // Tell the user we can't bring up the Access Control page for the selected item.
		 alert( m_cantInvokeAccessControl );
	 }
}// end invokeAccessControlPage()


/**
 * This function gets called when the page is loaded.
 */
function onLoadEventHandler()
{
	var form;
	
	// Load the various strings we will need.
	m_unknownEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.unknownEntryType" /></ssf:escapeJavaScript>';
	m_workspaceEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.workspaceEntryType" /></ssf:escapeJavaScript>';
	m_folderEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.folderEntryType" /></ssf:escapeJavaScript>';
	m_profilesEntryType = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.profilesEntryType" /></ssf:escapeJavaScript>';
	m_cantInvokeAccessControl = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.cantInvokeAccessControl" /></ssf:escapeJavaScript>';
	m_noAccess = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.userAccess.noAccess" /></ssf:escapeJavaScript>';

	// Get the url we need to invoke the access control page.
	m_accessControlUrl = '${access_control_page_url}';
}// end onLoadEventHandler()



/**
 * Remove any access report data from the page.
 */
function removeAccessReportDataFromPage()
{
	var table;
	var i;

	// Get the table that holds list of objects the user has access to.
	table = document.getElementById( 'accessReportTable' );

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
}// end removeAccessReportDataFromPage()


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
	getAccessReport( id );
}// end ss_selectUser()


</script>

	<div class="ss_pseudoPortal">
		<div class="ss_style ss_portlet">
			<c:set var="formName">${renderResponse.namespace}fm</c:set>

			<ssf:form titleTag="administration.report.title.user_access">
				<form class="ss_style ss_form" 
					action="<ssf:url webPath="reportDownload"/>" 
					method="post" 
					name="reportForm"
					id="reportForm">

					<div id="header1Span" class="margintop3"><ssf:nlt tag="administration.report.userAccess.header1" /></div>
					<div class="margintop3">
						<span style="padding-right: 5px;"><ssf:nlt tag="administration.report.userAccess.selectUser" /></span>

					  	<ssf:find formName="${formName}" formElement="user" 
									type="user" 
									singleItem="true"
									width="80"
									clickRoutine="ss_selectUser${renderResponse.namespace}" />

						<div id="progressIndicator" class="margintop3 marginbottom3" style="display: none;">
							<ssf:nlt tag="administration.report.userAccess.retrievingReport" />
							<img src="<html:imagesPath/>pics/spinner_small.gif" align="absmiddle" border="0" >
						</div>
					</div>
					<sec:csrfInput />
				</form>

				<div id="accessReportDiv1" class="margintop3" style="display: none;"><ssf:nlt tag="administration.report.userAccess.header2" /></div>
				<div id="accessReportDiv2" class="margintop1 roundcornerSM" style="display: none; height: 600px; width: 600px; overflow: auto; border: 1px solid #cccccc;">
					<table class="objlist2" id="accessReportTable" cellspacing="0" cellpadding="3">
						<tr class="columnhead" style="font-family: arial, sans-serif;">
							<td align="left">
								<span>&nbsp;<ssf:nlt tag="administration.report.userAccess.col1" /></span>
							</td>
							<td align="left">
								<span>&nbsp;<ssf:nlt tag="administration.report.userAccess.col2" /></span>
							</td>
							<td align="left" width="100%">&nbsp;</td>
						</tr>
					</table>
				</div>

	   			<div class="ss_buttonBarRight" style="margin-top: 10px;">
					<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
						onClick="return handleCloseBtn();" />
				</div>
			</ssf:form>
		</div>
	</div>
</body>
</html>
