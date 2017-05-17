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

<c:set var="ss_windowTitle" value='<%= NLT.get( "administration.report.title.email", "E-mail Report" ) %>' scope="request"/>

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
var m_noRecordsFound = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.email.noRecordsFound" /></ssf:escapeJavaScript>';

ss_createOnLoadObj( 'emailReport', onLoadEventHandler );


/**
 * Add an entry to the page for the given object the user has access to.
 */
function addEmailReportDataToPage( entry )
{
	var table;
	var tr;
	var td;
	var div;
	var span;
	var a;
	var typeName;
	var anchor;
	var br;
	var text;

	// Get the table that holds the list of entries the user has access to.
	table = document.getElementById( 'emailReportTable' );

	// Create a <tr> for the entry.
	tr = table.insertRow( table.rows.length );

	// Remember the entry this row is dealing with.
	//  This is used to be able to delete this row later
	tr.n_entry = entry;

	// Add the entry's date to the table.
	td = tr.insertCell( 0 );
	td.style.whiteSpace = 'nowrap';
	td.style.verticalAlign = 'top';
	td.style.paddingRight = '15px';
	td.setAttribute("width", "5%");
	span = document.createElement( 'span' );
	span.className = '';
	text = entry.sendDate;
	updateElementsTextNode( span, text );
	td.appendChild( span );
	
	// Add the from address to the table.
	td = tr.insertCell( 1 );
	td.style.whiteSpace = 'nowrap';
	td.style.verticalAlign = 'top';
	td.style.paddingRight = '15px';
	td.setAttribute("width", "5%");
	span = document.createElement( 'span' );
	span.className = '';
	text = entry.from;
	updateElementsTextNode( span, text );
	td.appendChild( span );

	// Add the to addresses to the table.
	td = tr.insertCell( 2 );
	td.style.verticalAlign = 'top';
	td.style.paddingRight = '15px';
	td.setAttribute("width", "5%");
	div = document.createElement( 'div' );
	div.className = '';
	text = entry.toAddresses;
	text = ss_replaceSubStrAll(text, ",", "<br/>")
	//If text has is a "/" in it, then this is a path name
	if (text.indexOf("/") < 0) {
		td.style.whiteSpace = 'nowrap'; 
	} else {
		text = ss_replaceSubStrAll(text, " ", "&nbsp;")
		text = ss_replaceSubStrAll(text, "/", " /")
	}
	div.innerHTML = text;
	td.appendChild( div );

	// Add the type to the table.
	td = tr.insertCell( 3 );
	td.style.whiteSpace = 'nowrap';
	td.style.verticalAlign = 'top';
	td.style.paddingRight = '15px';
	td.setAttribute("width", "5%");
	span = document.createElement( 'span' );
	span.className = '';
	text = entry.logType;
	updateElementsTextNode( span, text );
	td.appendChild( span );

	// Add the status to the table.
	td = tr.insertCell( 4 );
	td.style.whiteSpace = 'nowrap';
	td.style.verticalAlign = 'top';
	td.style.paddingRight = '15px';
	td.setAttribute("width", "5%");
	span = document.createElement( 'span' );
	span.className = '';
	text = entry.logStatus;
	updateElementsTextNode( span, text );
	td.appendChild( span );

	// Add the subject to the table.
	td = tr.insertCell( 5 );
	td.style.verticalAlign = 'top';
	td.style.paddingRight = '15px';
	td.setAttribute("width", "35%");
	span = document.createElement( 'span' );
	span.className = '';
	text = entry.subject;
	span.setAttribute("title", text);
	if (text.length > 80) text = text.substring(0, 80) + "...";
	updateElementsTextNode( span, text );
	td.appendChild( span );

	// Add the attached files to the table.
	td = tr.insertCell( 6 );
	td.style.whiteSpace = 'nowrap';
	td.style.verticalAlign = 'top';
	td.style.paddingRight = '15px';
	td.setAttribute("width", "5%");
	div = document.createElement( 'div' );
	div.className = '';
	text = entry.attachedFiles;
	text = ss_replaceSubStrAll(text, "\"", "&quot;");
	text = ss_replaceSubStrAll(text, "<", "&lt;")
	text = ss_replaceSubStrAll(text, ">", "&gt;")
	text = ss_replaceSubStrAll(text, ",", "<br/>")
	div.innerHTML = text;
	td.appendChild( div );

	// Add the comment to the table.
	td = tr.insertCell( 7 );
	td.style.verticalAlign = 'top';
	td.setAttribute("width", "30%");
	span = document.createElement( 'span' );
	span.className = '';
	text = entry.comment;
	updateElementsTextNode( span, text );
	td.appendChild( span );

}// end addEmailReportDataToPage()


/**
 * Issue an ajax request to get the email report.
 */
function getEmailReport()
{
	var	input;
	var	id;
	var url;
	var obj;

	// Remove any previous access data from the page.
	removeEmailReportDataFromPage();
	
	// Display the wait indicator.
	showWaitIndicator();
	
	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'getEmailReport'

	// Build the url used in the ajax request.
	url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );

	// Issue the ajax request.  The function handleResponseToGetEmailReport() will be called
	// when we get the response to the request.
	ss_post(url, "reportForm", handleResponseToGetEmailReport, "", "")
}// end getEmailReport()


/**
 * This function gets called when we get a response to an ajax request to get an access report.
 */
function handleResponseToGetEmailReport( responseData ) {
	// Hide the wait indicator.
	hideWaitIndicator();
	ss_stopSpinner();

	// Remove any previous access data from the page.
	removeEmailReportDataFromPage();
	
	if ( responseData.status == 1 )
	{
		// If we get here then things worked.
		// Do we have any data?
		if ( responseData.reportData != null && responseData.reportData.length > 0 )
		{
			var i;
			var div;

			// Yes
			// Show the table that holds the list of email problems.
			div = document.getElementById( 'emailReportDiv' );
			div.style.display = 'block';

			// responseData.reportData is an array of objects.  Each object holds information about
			// the email object.
			for (i = 0; i < responseData.reportData.length; ++i)
			{
				var nextEntry;

				// Get the next entry in the report.
				nextEntry = responseData.reportData[i];

				// Add this entry to the table.
				addEmailReportDataToPage( nextEntry );
			}// end for() 
		}
		else
		{
			// Tell the administrator that there are no email problems.
			alert( m_noRecordsFound );
		}
	}
	else if ( responseData.status == -1 )
	{
		// If we get here an error happened retrieving the email report.
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
		alert( 'Unknown email status: ' + responseData.status );
	}
}// end handleResponseToGetEmailReport()


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
 * This function gets called when the page is loaded.
 */
function onLoadEventHandler()
{
	var form;
	
}// end onLoadEventHandler()



/**
 * Remove any access report data from the page.
 */
function removeEmailReportDataFromPage()
{
	var table;
	var i;

	// Get the table that holds list of objects the user has access to.
	table = document.getElementById( 'emailReportTable' );

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
}// end removeEmailReportDataFromPage()


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
	getEmailReport( id );
}// end ss_selectUser()


</script>

<div class="ss_pseudoPortal">
	<div class="ss_style ss_portlet">
		<c:set var="formTitle"><%= NLT.get( "administration.report.title.email", "E-mail Report" ) %></c:set>

		<ssf:form title="${formTitle}">
			<form class="ss_style ss_form" 
				action="<ssf:url webPath="reportDownload"/>" 
				method="post" 
				name="reportForm"
				id="reportForm">

		  <input type="hidden" name="ss_reportType" id="ss_reportType" value="activityByUser"/>
		  <div class="ss_buttonBarRight margintop2 marginbottom3">
		    <span><input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" 
		      		onclick="return handleCloseBtn();"></span>
		  </div>
		  <div class="ss_largeprint ss_bold marginbottom1"><ssf:nlt tag="administration.report.dates"/></div>
		  
		  <div class="roundcornerSM" style="border: 1px solid #cccccc; padding: 5px; background-color: #ededed;">
			  <div class="n_date_picker" style="display:inline; vertical-align: middle;">
				<ssf:datepicker formName="reportForm" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
				 immediateMode="false" 
				 altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
				 />
			  </div>
			  <div id="ss_startPopup" class="ss_calPopupDiv"></div>
			  <span style="padding:0px 10px;"><ssf:nlt tag="smallWords.and"/></span>
			  
			  <div class="n_date_picker" style="display:inline;">
				<ssf:datepicker formName="reportForm" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				 immediateMode="false" 
				 altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
				 />
			  </div>
			  <div id="ss_endPopup" class="ss_calPopupDiv"></div>
			  
			  <br/>				 
			  <br/>				 
			  <div id="ss_report_panel_forum">
				<span style="padding-right: 10px">
				   <input type="radio" class="ss_radio" name="reportType" value="send" id="sendReport" checked="checked" />
				   <label class="ss_radio_label" for="sendReport"><ssf:nlt tag="administration.report.label.emailSend"/></label>
			   </span>
				<span style="padding-right: 10px">
				   <input type="radio" class="ss_radio" name="reportType" value="receive" id="receiveReport" />
				   <label class="ss_radio_label" for="receiveReport"><ssf:nlt tag="administration.report.label.emailReceive"/></label>
			   </span>
				<span style="padding-right: 10px">
					<input type="radio" class="ss_radio" name="reportType" value="errors" id="errorsReport" />
					<label class="ss_radio_label" for="errorsReport"><ssf:nlt tag="administration.report.label.emailErrors"/></label>
			   </span>
			   <div class="margintop3 marginbottom1" style="margin-left: 5px;">
					<input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="administration.create.report" />"
					onclick="getEmailReport();ss_startSpinner();return false;">
				</div>
			  </div>
			</div> 
			<div style="margin-top: 1em;">
				<span id="progressIndicator" style="margin-left: 4em; display: none;">
						<ssf:nlt tag="administration.report.retrievingReport" text="Retrieving report..." />
						<img src="<html:imagesPath/>pics/spinner_small.gif" align="absmiddle" border="0" >
				</span>
			</div>

			<div class="roundcornerSM" id="emailReportDiv" style="display: none; border: 1px solid #cccccc;">
				<table class="objlist2" id="emailReportTable" width="100%" class="ss_tableheader_style" cellspacing="0" cellpadding="2">
					<tr class="columnhead" style="font-family: arial, sans-serif;">
						<td>
							<span>&nbsp;<ssf:nlt tag="administration.report.email.col1"/><span>
						</td>
						<td>
							<span><ssf:nlt tag="administration.report.email.col2"/><span>
						</td>
						<td>
							<span><ssf:nlt tag="administration.report.email.col3"/><span>
						</td>
						<td>
							<span><ssf:nlt tag="administration.report.email.col4"/><span>
						</td>
						<td>
							<span><ssf:nlt tag="administration.report.email.col5"/><span>
						</td>
						<td style="white-space: normal">
							<span><ssf:nlt tag="administration.report.email.col6"/><span>
						</td>
						<td>
							<span><ssf:nlt tag="administration.report.email.col7"/><span>
						</td>
						<td>
							<span><ssf:nlt tag="administration.report.email.col8"/><span>
						</td>
					</tr>
				</table>
			</div>

				  <div class="ss_buttonBarRight marginbottom3" style="margin-top: 1em;">
					<span><input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" 
							onclick="return handleCloseBtn();"></span>
				  </div>
				<sec:csrfInput />
			  </form>
			</ssf:form>
		</div>
	</div>
</body>
</html>
