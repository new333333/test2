
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
<%@ page import="org.kablink.teaming.util.NLT"%>
<%@ page import="java.util.Locale" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp"%>
<c:set var="ss_windowTitle"
	value='<%= NLT.get("administration.configure_ldap") %>' scope="request" />
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body class="ss_style_body tundra" onunload="onUnloadEventHandler();">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet"><ssf:form titleTag="ldap.title">

	<c:if test="${!empty ssException}">
		<span class="ss_largerprint"><ssf:nlt
			tag="administration.errors" /> (<c:out value="${ssException}" />)</span>
		</br>
	</c:if>

	<form class="ss_style ss_form" name="${renderResponse.namespace}fm"
		method="post"
		action="<ssf:url action="configure_ldap" actionUrl="true"/>">

	<div class="ss_buttonBarRight"><br />
		<input type="submit" class="ss_submit" name="okBtn"
			value="<ssf:nlt tag="button.apply"/>">
		<input type="button"
			class="ss_submit" name="closeBtn"
			value="<ssf:nlt tag="button.close" text="Close"/>"
			onClick="self.window.close();return false;" /></div>
	<div>
	<div id="funkyDiv" style="width: 500px">
	<ul>
		<li><a href='#wah'>Wah</a></li>
	</ul>
	<div id='wah'></div>
	</div>
	<button id="ldapAddConnection" class="ss_submit"><ssf:nlt
		tag="ldap.connection.add" /></button>
	</div>
	<div class="ss_divider"></div>
	<br />

	<table class="ss_style" border="0" cellspacing="0" cellpadding="3">
		<tr>
			<td><input type="checkbox" id="enabled" name="enabled"
				<c:if test="${ssLdapConfig.enabled}">checked</c:if> /> <label
				for="enabled"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.enable" /></span><br />
			</label></td>
		</tr>
		<tr>
			<td>
				<!-- This hidden input is used to store a unique id used to identify the sync results. -->
				<input id="ldapSyncResultsId" name="ldapSyncResultsId" type="hidden" value="" />

				<input type="checkbox" id="runnow" name="runnow"
				<c:if test="${runnow}"> checked="checked" </c:if> /> <label
				for="runnow"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.now" /></span><br />
			</label></td>
		</tr>
	</table>

	<br />
	<ssf:expandableArea title='<%= NLT.get("ldap.schedule") %>' initOpen="true">
		<c:set var="schedule" value="${ssLdapConfig.schedule}" />
		<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
		<div class="ss_divider">
		</div>
	</ssf:expandableArea> <br />

	<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
		tag="ldap.users" /></legend>
		<table class="ss_style" border="0" cellspacing="0" cellpadding="3">
			<tr>
				<td><input type="checkbox" name="userSync" id="userSync"
					<c:if test="${ssLdapConfig.userSync}">checked</c:if> /> <label
					for="userSync"><span class="ss_labelRight ss_normal"><ssf:nlt
					tag="ldap.schedule.user.sync" /></span></label></td>
			</tr>
			<tr>
				<td><input type="checkbox" name="userRegister" id="userRegister"
					<c:if test="${ssLdapConfig.userRegister}">checked</c:if> /> <label
					for="userRegister"><span class="ss_labelRight ss_normal"><ssf:nlt
					tag="ldap.schedule.user.register" /></span></label></td>
			</tr>
			<tr>
				<td><input type="checkbox" name="userDelete" id="userDelete"
					<c:if test="${ssLdapConfig.userDelete}">checked</c:if> /> <label
					for="userDelete"><span class="ss_labelRight ss_normal"><ssf:nlt
					tag="ldap.schedule.user.delete" /></span></label></td>
			</tr>
			<tr>
				<td><input type="checkbox" name="userWorkspaceDelete"
					id="userWorkspaceDelete"
					<c:if test="${ssLdapConfig.userWorkspaceDelete}">checked</c:if> /> <label
					for="userWorkspaceDelete"><span
					class="ss_labelRight ss_normal"><ssf:nlt
					tag="ldap.schedule.user.workspace.delete" /></span></label></td>
			</tr>
			<tr>
				<td>
					<label for="ssDefaultTimeZone">
						<div style="margin-top: .25em;" class="ss_labelAbove"><ssf:nlt tag="ldap.config.default.timezone" /></div>
					</label>
					<select name="ssDefaultTimeZone" id="ssDefaultTimeZone">
					<%
						java.util.Set<String> tzones = null;
						java.util.Set<String> map = null;
						String defaultTimeZone	= null;
						Locale locale = null;
	
						// Get all of the time zone ids.  These are always returned in English.
						tzones = org.kablink.teaming.calendar.TimeZoneHelper.getTimeZoneIds();
						
						// We can use English as the language in the constructor of Locale because the time zones are always
						// displayed in English.
						locale = new Locale( "en" );
						map = new java.util.TreeSet(new org.kablink.teaming.comparator.StringComparator( locale ) ); //sort
						map.addAll( tzones );
	
						// Add an <option> for every time zone.
						for (String tz:map)
						{
					%>
						<c:set var="nextTimeZone" value="<%= tz %>" />
						<option value="<%= tz %>" <c:if test="${ssDefaultTimeZone == nextTimeZone}">selected</c:if> ><%= tz %></option>
					<%
						}// end for()
					%>
					</select>
				</td>
			</tr>
		</table>
	</fieldset>

	<br />
	<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
		tag="ldap.groups" /></legend>
	<table class="ss_style" border="0" cellspacing="0" cellpadding="3">
		<tr>
			<td><input type="checkbox" name="groupRegister"
				id="groupRegister"
				<c:if test="${ssLdapConfig.groupRegister}">checked</c:if> /> <label
				for="groupRegister"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.group.register" /></span></label></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="membershipSync"
				id="membershipSync"
				<c:if test="${ssLdapConfig.membershipSync}">checked</c:if> /> <label
				for="membershipSync"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.membership.sync" /></span></label></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="groupDelete" id="groupDelete"
				<c:if test="${ssLdapConfig.groupDelete}">checked</c:if> /> <label
				for="groupDelete"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.group.delete" /></span></label></td>
		</tr>
	</table>
	</fieldset>

	<br />
	<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
		tag="ldap.localLogin" /></legend> <input type="checkbox" id="allowLocalLogin"
		name="allowLocalLogin"
		<c:if test="${ssAuthenticationConfig.allowLocalLogin}">checked</c:if> />
	<label for="allowLocalLogin"><span
		class="ss_labelRight ss_normal"><ssf:nlt
		tag="ldap.config.allowLocalLogin" /></span><br />
	</label></fieldset>

	<br />
	<div class="ss_buttonBarLeft"><input type="submit"
		class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn"
		value="<ssf:nlt tag="button.close" text="Close"/>"
		onClick="self.window.close();return false;" /></div>
	<input type="hidden" name="ldapConfigDoc" id="ldapConfigDoc" value="" />
	</form>
</ssf:form></div>
<script type="text/javascript" src="<html:rootPath/>js/jquery/jquery.js"></script>
<script type="text/javascript">
     jQuery.noConflict();
</script> <script type="text/javascript"
	src="<html:rootPath/>js/jquery/jquery-ui-personalized.js"></script>

<style type="text/css">
.ui-tabs-hide {
	display: none;
}

.invalid .errorMessage {
	display: inline;
}

.errorMessage {
	display: none;
	color: red;
}

.invalid label {
	color: red;
}

@import "flora.css";

/* Caution! Ensure accessibility in print and other media types... */
@media projection , screen {
	/* Use class for showing/hiding tab content, so that visibility can be better controlled in different media types... */
	.ui-tabs-hide {
		display: none;
	}
}

/* Hide useless elements in print layouts... */
@media print {
	.ui-tabs-nav {
		display: none;
	}
}

/* Skin */
.ui-tabs-nav,.ui-tabs-panel {
	
}

.ui-tabs-nav {
	list-style: none;
	margin: 0;
	padding: 0 0 0 3px;
}

.ui-tabs-nav:after {
	/* clearing without presentational markup, IE gets extra treatment */
	display: block;
	clear: both;
	content: " ";
}

.ui-tabs-nav li {
	float: left;
	margin: 0 0 0 2px;
	font-weight: bold;
	list-style: none !important;
}

.ui-tabs-nav a,.ui-tabs-nav a span {
	float: left; /* fixes dir=ltr problem and other quirks IE */
	padding: 0 12px;
}

.ui-tabs-nav a {
	margin: 5px 0 0;
	/* position: relative makes opacity fail for disabled tab in IE */
	padding-left: 0;
	background-position: 100% 0;
	text-decoration: none;
	white-space: nowrap; /* @ IE 6 */
	outline: 0; /* @ Firefox, prevent dotted border after click */
	font-size: 12px;
}

.ui-tabs-nav a:link,.ui-tabs-nav a:visited {
	
}

.ui-tabs-nav .ui-tabs-selected a {
	position: relative;
	z-index: 2;
	font-size: 14px;
}

.ui-tabs-nav a span {
	padding-top: 1px;
	padding-right: 0;
	height: 20px;
	line-height: 20px;
	font-size: 12px;
	color: #666;
}

.ui-tabs-nav .ui-tabs-selected a span {
	padding-top: 0;
	color: #000;
}

.ui-tabs-nav .ui-tabs-selected a:link,.ui-tabs-nav .ui-tabs-selected a:visited
	{ /* @ Opera, use pseudo classes otherwise it confuses cursor... */
	cursor: text;
}

.ui-tabs-nav a:hover,.ui-tabs-nav a:focus,.ui-tabs-nav a:active,.ui-tabs-nav .ui-tabs-unselect a:hover,.ui-tabs-nav .ui-tabs-unselect a:focus,.ui-tabs-nav .ui-tabs-unselect a:active
	{ /* @ Opera, we need to be explicit again here now... */
	cursor: pointer;
}

.ui-tabs-panel {
	border: 1px solid #519e2d;
	padding: 10px;
	background: #fff;
	/* declare background color for container to avoid distorted fonts in IE while fading */
}

/* Additional IE specific bug fixes... */
* html .ui-tabs-nav { /* auto clear @ IE 6 & IE 7 Quirks Mode */
	display: inline-block;
}

* :first-child+html .ui-tabs-nav {
	/* auto clear @ IE 7 Standards Mode - do not group selectors, otherwise IE 6 will ignore complete rule (because of the unknown + combinator)... */
	display: inline-block;
}
</style>

<style media="screen" type="text/css">
.margintop3
{
	margin-top: 1em;
}

.rowaltcolor {
	background-color: #f4f4f4;
}

.syncResultsDialog
{
	background-color: #ffffff;
	border: solid 1px black;
	font-family: arial, sans-serif;
	height: auto !important;
	position: absolute;
	z-index: 75;
}

.syncResultsTitle
{
	color: white;
	font-weight: bold;
	font-size: 1em;
	background-color: #458ab9;
	text-align: left;
	text-indent: 0.2em;
	padding: 0.3em;
}

.syncResultsSection
{
	margin-bottom: .75em;
}

.syncResultsSectionHeaderTR
{
	background-color: #efeeec;
}

.syncResultsSectionHeaderTD
{
	border-bottom: 1px solid #dfddd5;
}

.syncResultsSectionHeaderText
{
	color: black;
	font-size: 0.85em;
	text-align: left;
	margin-left: 0.4em;
	margin-top: 0.2em;
	margin-bottom: 0.2em;
}

.syncResultsSectionItem
{
	margin-left: 1.1em;
	white-space: nowrap;
}

.syncResultsSectionBottomSpace
{
	font-size: 0.85em;
}
</style>

<script type="text/javascript">
var LDAP_SYNC_STATUS_IN_PROGRESS = 0;
var LDAP_SYNC_STATUS_COMPLETED = 1;
var LDAP_SYNC_STATUS_STOP_COLLECTING_RESULTS = 2;
var LDAP_SYNC_STATUS_ABORTED_BY_ERROR = 3;

var m_syncResultsTimerId = null;
var m_ldapConfigId = null;
var m_ldapSyncStatus = -1;
var m_syncStatusInProgressImg = null;
var m_syncStatusCompletedImg = null;
var m_syncStatusErrorImg = null;
var m_numAddedUsers = 0;
var m_numModifiedUsers = 0;
var m_numDeletedUsers = 0;
var m_numAddedGroups = 0;
var m_numModifiedGroups = 0;
var m_numDeletedGroups = 0;

// Create the images that will be used in the sync results dialog.
m_syncStatusInProgressImg = new Image();
m_syncStatusInProgressImg.src = '<html:imagesPath/>pics/spinner.gif';
m_syncStatusCompletedImg = new Image();
m_syncStatusCompletedImg.src = '<html:imagesPath/>pics/success32.gif';
m_syncStatusErrorImg = new Image();
m_syncStatusErrorImg.src = '<html:imagesPath/>pics/error32.gif';

/**
 * This function will add the given sync results to the given table.
 */
function 	addNamesToSyncResultsDlg( table, names, noneTrId, countId, initialCount )
{
	var numAdded;

	numAdded = 0;
	
	// Do we have anything to add?
	if ( names != null && names.length > 0 )
	{
		var i;
		var tr;
		var finalCount;
		var span;
		
		// Yes
		// Remove the row from the table that says "None".
		tr = document.getElementById( noneTrId );
		if ( tr != null )
			table.deleteRow( tr.rowIndex );

		// Add each name to the given table.
		for (i = 0; i < names.length; ++i)
		{
			var	tr;
			var	td;
			var	span;

			// Create a <tr> and a <td> and a <span> for the name to live in.
			tr = table.insertRow( table.rows.length-1 );
			td = tr.insertCell( 0 );
			span = document.createElement( 'span' );
			span.className = 'syncResultsSectionItem';
			updateElementsTextNode( span, names[i] );
			td.appendChild( span );
		}// end for()

		// Update the text that displays the count for the given section.  For example, Added Users: 123
		numAdded = names.length;
		finalCount = initialCount + numAdded;
		span = document.getElementById( countId );
		updateElementsTextNode( span, finalCount );
	}

	return numAdded;
}// end addNamesToSyncResultsDlg()


/**
 * This function will close the "Sync Results" dialog and issue an ajax request to tell the ldap
 * sync process to stop collecting results.
 */
function closeSyncResultsDlg()
{
	var div;

	// Is the ldap sync running?
	if ( m_ldapSyncStatus == LDAP_SYNC_STATUS_IN_PROGRESS )
	{
		var msg;

		// Yes
		// Issue an ajax request to tell the ldap sync process to not collect any more sync results.
		stopCollectingSyncResults();

		// Inform the user that closing the dialog will not stop the sync process.
		msg = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.syncResults.closeMsg"/></ssf:escapeJavaScript>';
		alert( msg );
	}

	div = document.getElementById( 'syncResultsDlg' );
	div.style.display = 'none';

}// end closeSyncResultsDlg()


/**
 * Issue an ajax request to get the latest results of the ldap sync.
 */
function getSyncResults()
{
	var	input;
	var	id;
	var url;
	var obj;

	// Get the id of the sync results we are looking for.
	input = document.getElementById( 'ldapSyncResultsId' );
	id = input.value;

	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'getLdapSyncResults'
	obj.ldapSyncResultsId = id;

	// Build the url used in the ajax request.
	url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );

	// Issue the ajax request.  The function handleResponseToGetSyncResults() will be called
	// when we get the response to the request.
	ss_get_url( url, handleResponseToGetSyncResults );
}// end getSyncResults()


/**
 * This function gets called when we get a response to an ajax request to get the ldap sync results.
 */
function handleResponseToGetSyncResults( responseData )
{
	var table;
	var status;

	m_syncResultsTimerId = null;
	
	// Add the names of the added users to the sync results dialog.
	table = document.getElementById( 'addedUsersTable' );
	m_numAddedUsers += addNamesToSyncResultsDlg( table, responseData.addedUsers, 'noAddedUsersTR', 'numAddedUsersSpan', m_numAddedUsers );
	 
	// Add the names of the modified users to the sync results dialog.
	table = document.getElementById( 'modifiedUsersTable' );
	m_numModifiedUsers += addNamesToSyncResultsDlg( table, responseData.modifiedUsers, 'noModifiedUsersTR', 'numModifiedUsersSpan', m_numModifiedUsers );

	// Add the names of the deleted users to the sync results dialog.
	table = document.getElementById( 'deletedUsersTable' );
	m_numDeletedUsers += addNamesToSyncResultsDlg( table, responseData.deletedUsers, 'noDeletedUsersTR', 'numDeletedUsersSpan', m_numDeletedUsers );

	// Add the names of the added groups to the sync results dialog.
	table = document.getElementById( 'addedGroupsTable' );
	m_numAddedGroups += addNamesToSyncResultsDlg( table, responseData.addedGroups, 'noAddedGroupsTR', 'numAddedGroupsSpan', m_numAddedGroups );
	 
	// Add the names of the modified groups to the sync results dialog.
	table = document.getElementById( 'modifiedGroupsTable' );
	m_numModifiedGroups += addNamesToSyncResultsDlg( table, responseData.modifiedGroups, 'noModifiedGroupsTR', 'numModifiedGroupsSpan', m_numModifiedGroups );

	// Add the names of the deleted groups to the sync results dialog.
	table = document.getElementById( 'deletedGroupsTable' );
	m_numDeletedGroups += addNamesToSyncResultsDlg( table, responseData.deletedGroups, 'noDeletedGroupsTR', 'numDeletedGroupsSpan', m_numDeletedGroups );

	// Remember the state of the ldap sync process.
	m_ldapSyncStatus = responseData.status;
	
	if ( responseData.status == LDAP_SYNC_STATUS_IN_PROGRESS )
	{
		// If we get here the sync is still in progress.
		// Wait another 2 seconds and then go get some more results.
		m_syncResultsTimerId = setTimeout( getSyncResults, 2000 );
	}
	else if ( responseData.status == LDAP_SYNC_STATUS_COMPLETED )
	{
		// If we get here the ldap sync is complete.
		// Set the status displayed in the sync results dialog to indicate the sync completed.
		status = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.syncResults.status.completed"/></ssf:escapeJavaScript>';
		setSyncResultsStatus( status, m_syncStatusCompletedImg );
	}
	else if ( responseData.status == LDAP_SYNC_STATUS_STOP_COLLECTING_RESULTS )
	{
		// If we get here it means the ldap sync process isn't collecting any more results.  Nothing to do.
	}
	else if ( responseData.status == LDAP_SYNC_STATUS_ABORTED_BY_ERROR )
	{
		// If we get here an error happened during the sync.
		// Display the error.
		if ( responseData.errDesc != null )
		{
			var msg;

			msg = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.syncResults.error"/></ssf:escapeJavaScript>';
			msg += '\n\n' + responseData.errDesc;
			alert( msg );

			// Show the ldap configuration that had the error.
			if ( responseData.errLdapConfigId != null )
			{
				// Show the ldap configuration that had the error.
				m_ldapConfigId = responseData.errLdapConfigId;
				ssPage.showLdapConfig();
			}
		}

		// Set the status displayed in the sync results dialog to indicate an error happened.
		status = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.syncResults.status.stoppedByError"/></ssf:escapeJavaScript>';
		setSyncResultsStatus( status, m_syncStatusErrorImg );
	}
	else if ( responseData.status == -1 )
	{
		// If we get here it means that the sync results have been removed from the session.  Nothing else to do.
	}
	else
	{
		// We should never get here.
		alert( 'Unknown sync status: ' + responseData.status );
	}
}// end handleResponseToGetSyncResults()


/**
 * This function gets called when we get the response to our ajax request to remove the ldap sync results object from the session. 
 */
function handleResponseToRemoveExistingLdapSyncResults( responseData )
{
	// Nothing to do here.
}// end handleResponseToRemoveExistingLdapSyncResults()


/**
 * This function gets called when we get the response to our ajax request to start the ldap sync.
 */
function handleResponseToStartLdapSync( responseData )
{
	// Nothing to do here.  The function, handleResponseToGetSyncResults(), will report any errors.
}// end handleResponseToStartLdapSync()


/**
 * This functions gets called when we get the response to our ajax request to stop collecting ldap sync results.
 */
function handleResponseToStopCollectingSyncResults( responseData )
{
	// Nothing to do.
}// end handleResponseToStopCollectingSyncResults()


/**
 * This function is the event handler for the onunload event for this page.
 */
function onUnloadEventHandler()
{
	// If a previous ldap sync results exists, remove it from the session.
	removeExistingLdapSyncResults();
}// end onUnloadEventHandler()


/**
 * If a previous ldap sync results exists, remove it from the session.
 */
function removeExistingLdapSyncResults()
{
	var	input;
	var	id;
	var url;
	var obj;

	// Get the id of the sync results we are looking for.
	input = document.getElementById( 'ldapSyncResultsId' );
	id = input.value;

	// Do we have an ldap sync results?
	if ( id != null && id.length > 0 )
	{
		// Yes
		// Set up the object that will be used in the ajax request.
		obj = new Object();
		obj.operation = 'removeLdapSyncResults'
		obj.ldapSyncResultsId = id;
	
		// Build the url used in the ajax request.
		url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );
	
		// Issue an ajax request to remove the ldap sync results object from the session.
		// The function handleResponseToRemoveLdapSyncResults() will be called when we get the response to the request.
		ss_get_url( url, handleResponseToRemoveExistingLdapSyncResults );

		input.value = null;
	}
}// end removeExistingLdapSyncResults()


/**
 * This function will set the status in the sync results dialog.
 */
function setSyncResultsStatus( statusTxt, img )
{
	var span;
	var imgElement;

	// Get the <span> that holds the status text.
	span = document.getElementById( 'syncResultsStatusText' );

	// Update the status text in the sync results dialog.
	updateElementsTextNode( span, statusTxt );

	// Get the <img> that holds the status image.
	imgElement = document.getElementById( 'syncResultsStatusImg' );
	imgElement.src = img.src;
}// end setSyncResultsStatus()


/**
 * This function will display the "Sync Results" dialog.
 */
function showSyncResultsDlg()
{
	var div;

	div = document.getElementById( 'syncResultsDlg' );
	div.style.display = '';
}// end showSyncResultsDlg()


/**
 * Issue an ajax request to start the ldap sync.
 */
function startLdapSync()
{
	var input;
	var id;
	var url;
	var obj;
	var status;

	m_numAddedUsers = 0;
	m_numModifiedUsers = 0;
	m_numDeletedUsers = 0;
	m_numAddedGroups = 0;
	m_numModifiedGroups = 0;
	m_numDeletedGroups = 0;

	// Remove an existing ldap sync results if one exists.
	removeExistingLdapSyncResults();
	
	// Show the sync results dialog.
	showSyncResultsDlg();
	
	// Generate a unique id that will identify the sync results.  We will use
	// this id in subsequent ajax calls.
	input = document.getElementById( 'ldapSyncResultsId' );
	input.value = Math.random();
	id = input.value;

	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'startLdapSync'
	obj.ldapSyncResultsId = id;

	// Build the url used in the ajax request.
	url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );
	
	// Issue the ajax request.  The function handleResponseToStartLdapSync() will be called
	// when we get the response to the request.
	ss_get_url( url, handleResponseToStartLdapSync );

	// Change the title to indicate the ldap sync is in progress.
	status = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.syncResults.status.inProgress"/></ssf:escapeJavaScript>';
	setSyncResultsStatus( status, m_syncStatusInProgressImg );

	// Start a timer.  Whenever the timer goes off we will issue an ajax request
	// to get the latest results from the ldap sync.
	m_syncResultsTimerId = setTimeout( getSyncResults, 2000 );

	m_ldapSyncStatus = LDAP_SYNC_STATUS_IN_PROGRESS;
}// end startLdapSync()


/**
 * This function will issue an ajax request telling the sync process to stop collecting sync results.
 */
function stopCollectingSyncResults()
{
	var	input;
	var	id;
	var url;
	var obj;

	// Get the id of the sync results we are looking for.
	input = document.getElementById( 'ldapSyncResultsId' );
	id = input.value;

	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'stopCollectingLdapSyncResults'
	obj.ldapSyncResultsId = id;

	// Build the url used in the ajax request.
	url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );

	// Kill any timer we have.
	if ( m_syncResultsTimerId != null )
		clearTimeout( m_syncResultsTimerId );
	
	// Issue an ajax request to tell the ldap sync process to stop collecting results.
	// The function handleResponseToStopCollectingSyncResults() will be called when we get the response to the request.
	ss_get_url( url, handleResponseToStopCollectingSyncResults );
}// end stopCollectingSyncResults()


/**
 * This function will update the given element's text node with the given text.
 */
function updateElementsTextNode(
	element,	// The Element whose text Node is to be updated.
	newText)	// The text to update the Node with.
{
	var	found;
	var	i;
	var kids;
	var	numKids;

	if (null == element)
	{
		return;
	}

	// Find the text node for this element.
	kids    = element.childNodes;
	numKids = kids.length;
	found   = false;
	for (i = 0; ((i < numKids) && (!found)); i += 1)
	{
		// Is this child a text node?
		if (3 == kids[i].nodeType)
		{
			// Yes!  Replace its text with the new text.
			kids[i].data = newText;
			found        = true;
		}
	}

	// Did we find a text node?
	if ( !found )
	{
		var	textNode;

		// No!  Create one and add it to the element.
		textNode = element.ownerDocument.createTextNode( newText );
		element.appendChild( textNode );
	}
}// end updateElementsTextNode()


ssPage = {
	nextId : 1,
	currentTab : 0,
	defaultUserFilter: "${ssDefaultUserFilter}",
	defaultGroupFilter: "${ssDefaultGroupFilter}",
	
	createBindings : function($container)
	{
		jQuery(".ldapUrl", $container).change(function() {
			jQuery(this).parent().parent().find(".ldapTitle span").text(jQuery(this).val());
			var id = jQuery(this).parent().parent().attr("id");
			jQuery("#funkyDiv > ul li > a[href='#" + id + "']").text(jQuery(this).val());
		});
		jQuery(".ldapDelete", $container).click(function() {
			var title = jQuery("#funkyDiv > ul li").eq(ssPage.currentTab).find("span").text();
			var prompt;

			prompt = "<ssf:nlt tag="ldap.connection.delete.confirm" quoteDoubleQuote="true" />";
			prompt = prompt.replace( '{0}', title );
			if( ss_confirm( prompt, null )) {
				var id = jQuery(this).parent().parent().attr("id");
				jQuery("#funkyDiv > ul").tabs("remove", ssPage.currentTab);
				jQuery(this).parent().parent().remove();
			}
			return false;
		});	
		jQuery(".ldapUserSearches button.addSearch", $container).click(function() {
			ssPage.createSearchEntry(jQuery(this).prev(), "", ssPage.defaultUserFilter, "true");
			return false;
		});
		jQuery(".ldapGroupSearches button.addSearch", $container).click(function() {
			ssPage.createSearchEntry(jQuery(this).prev(), "", ssPage.defaultGroupFilter, "false");
			return false;
		});
	},

	createSearchEntry : function($listDiv, baseDn, filter, ss)
	{
		var $newSearch = jQuery('#ldapSearchTemplate').children().clone();
		$newSearch
			.find('.ldapBaseDn').val(baseDn).end()
			.find('.ldapFilter').val(filter).end()
			.find('.ldapSearchSubtree').val([ss]).end();
		jQuery("button.deleteSearch", $newSearch).click(function() {
			var		msg;

			msg = "<ssf:nlt tag="ldap.search.delete.confirm" quoteDoubleQuote="true" />";
			if(ss_confirm( msg )) {
				jQuery(this).parent().remove();
			}
			return false;
		});	

		$listDiv.append($newSearch);
	},

	createConnection : function(url, userIdAttribute, mappings, userSearches, groupSearches, principal, credentials)
	{
		var label = "<ssf:nlt tag="ldap.connection.newConnection"/>";
		if(url != "") { label = url; }

		var id = "ldapConn" + ssPage.nextId++;
		var $pane = jQuery('#ldapTemplate').children().clone().hide().attr("id", id);
		$pane.find(".ldapTitle span").text(label).end()
			 .find(".ldapUrl").val(url).end()
			 .find(".ldapUserIdAttribute").val(userIdAttribute).end()
			 .find(".ldapMappings").val(mappings).end()
			 .find(".ldapPrincipal").val(principal).end()
			 .find(".ldapCredentials").val(credentials).end();
		jQuery.each(userSearches, function() {
			ssPage.createSearchEntry($pane.find(".ldapUserSearches .ldapSearchList"), this.baseDn, this.filter, this.searchSubtree);
		});
		jQuery.each(groupSearches, function() {
			ssPage.createSearchEntry($pane.find(".ldapGroupSearches .ldapSearchList"), this.baseDn, this.filter, this.searchSubtree);
		});
		if(userSearches.length == 0 && groupSearches.length == 0) {
			ssPage.createSearchEntry($pane.find(".ldapUserSearches .ldapSearchList"), "", ssPage.defaultUserFilter, "true");
		}
		jQuery('#funkyDiv').append($pane);

		var index = jQuery("#funkyDiv > ul").tabs("length");
		jQuery("#funkyDiv > ul").tabs("add", '#'+ id, label);
		jQuery("#funkyDiv > ul").tabs("select", index);

		ssPage.createBindings($pane);
		$pane.show();
		return $pane;
	},
	
	addConnection : function() {
		var $pane = ssPage.createConnection("", "uid", ssPage.defaultUserMappings, [], [], "", "");
		return false;
	},
	
	balanced : function(input)
	{
		var length = input.length;
		var count = 0;
		var c = 0;
		
		for (var i = 0; i < length; ++i)
		{
		   c = input.charAt(i);
		   if (c == '(') {
			  ++count;
		   } else if (c == ')') {
			  --count;
			  if (count < 0) {
				 break;
			  }
		   }
		}
		
		return count == 0;
	},

	/**
	 * Select the tab for the ldap config with the id found in m_ldapConfigId.
	 */
	showLdapConfig : function()
	{
		if ( m_ldapConfigId != null && m_ldapConfigId.length > 0 )
		{
			var	span;

			// Get the <span> whose id matches the given ldap config id.
			span = document.getElementById( m_ldapConfigId );
			if ( span != null )
			{
				var	fldset;

				// Get the <fieldset> that holds the ldap configuration.
				fldset = span.parentNode;
				if ( fldset != null && fldset.id != null )
				{
					// Select the tab associated with this ldap configuration.
					jQuery( "#funkyDiv > ul").tabs( "select", '#' + fldset.id );
				}
			}
		}
	},

	
	validQuery : function(query) {
		return (query=="")||ssPage.balanced(query);
	},
	
	validDn : function(dn) {
		return true;
	}

};

jQuery(document).ready(function() {
	jQuery('#ldapAddConnection').click(ssPage.addConnection);
	
	jQuery("form").submit(function() {
		var ldapDoc="<ldapConfigs>";
		var valid = true;
		var wrapWithCDATA = function( str )
		{
			var		newStr;
			
			newStr = '<![CDATA[';
			if ( str != null && str.length > 0 )
				newStr += str;
			newStr += ']]>';

			return newStr;
		};
		var updateError = function($field, fieldValid)
		{
			if(fieldValid) {
				$field.parent().removeClass('invalid');
			} else {
				$field.parent().addClass('invalid');
			}
			return fieldValid;
		};
		var makeSearch = function()
		{
			var $this = jQuery(this);
			var $baseDn = jQuery('.ldapBaseDn', $this);
			var baseDn = $baseDn.val();
			var $filter = jQuery('.ldapFilter', $this);
			var filter = $filter.val();
			if((baseDn && baseDn != "") || (filter && filter != "")) {
				valid = updateError($baseDn, ssPage.validDn(baseDn)) && valid;
				valid = updateError($filter, ssPage.validQuery(filter)) && valid;
				ldapDoc += "<search>" +
					"<baseDn>" + wrapWithCDATA( baseDn ) + "</baseDn>" +
					"<filter>" +  wrapWithCDATA( filter ) + "</filter>";
				ldapDoc += "<searchSubtree>";
				if(jQuery('.ldapSearchSubtree:checked', $this).length > 0) {
					ldapDoc += "true";
				} else {
					ldapDoc += "false";
				}
				ldapDoc += "</searchSubtree>";

				ldapDoc += "</search>";
			}
		};
		jQuery('#funkyDiv .ldapConfig').each(function() {
			var $this = jQuery(this);
			ldapDoc += "<ldapConfig>";
			var $id = jQuery(".ldapId", $this);
			if($id.length > 0) {
				ldapDoc += "<id>" + $id.text() + "</id>";
			}
			ldapDoc += "<url>" + wrapWithCDATA( $this.find('.ldapUrl').val() ) + "</url>" +
						"<userIdAttribute>" + wrapWithCDATA( $this.find('.ldapUserIdAttribute').val() ) + "</userIdAttribute>" +
						"<mappings>" + wrapWithCDATA( $this.find('.ldapMappings').val() ) + "</mappings>";
			ldapDoc += "<principal>" + wrapWithCDATA( $this.find('.ldapPrincipal').val() ) + "</principal><credentials>" +
						wrapWithCDATA( $this.find('.ldapCredentials').val() ) + "</credentials>";
			ldapDoc += "<userSearches>";
			jQuery('.ldapUserSearches .ldapSearch', $this).each(makeSearch);
			ldapDoc += "</userSearches>";
			ldapDoc += "<groupSearches>";
			jQuery('.ldapGroupSearches .ldapSearch', $this).each(makeSearch);
			ldapDoc += "</groupSearches>";
			ldapDoc += "</ldapConfig>";
		});
		ldapDoc += "</ldapConfigs>";
		jQuery('#ldapConfigDoc').val(ldapDoc);

		return valid;
	});
});
</script> <script type="text/javascript">
	jQuery(document).ready(function() {
		<c:set var="defmappings" value="${ssUserAttributes}"/>
		<jsp:useBean id="defmappings" type="java.util.Map"/>
		<%
		StringBuffer mapText = new StringBuffer();
		for (java.util.Iterator iter=defmappings.entrySet().iterator(); iter.hasNext();) {
			java.util.Map.Entry me = (java.util.Map.Entry)iter.next();
			mapText.append(me.getValue() + "=" + me.getKey() + "\\n");
		}
		%>
		ssPage.defaultUserMappings = "<%=mapText.toString()%>";
	
		jQuery("#funkyDiv > ul").tabs();
	<c:forEach var="config" items="${ssLdapConnectionConfigs}">
		<c:set var="mappings" value="${config.mappings}"/>
		<jsp:useBean id="mappings" type="java.util.Map"/>
		<%
		mapText = new StringBuffer();
		for (java.util.Iterator iter=mappings.entrySet().iterator(); iter.hasNext();) {
			java.util.Map.Entry me = (java.util.Map.Entry)iter.next();
			mapText.append(me.getValue() + "=" + me.getKey() + "\\n");
		}
		%>
		var initialUserSearches = [ ];
		<c:forEach var="userSearch" items="${config.userSearches}">
			initialUserSearches.push({ baseDn: "${userSearch.baseDn}", filter: "${userSearch.filter}", searchSubtree: "${userSearch.searchSubtree}" });
		</c:forEach>
		var initialGroupSearches = [ ];
		<c:forEach var="groupSearch" items="${config.groupSearches}">
			initialGroupSearches.push({ baseDn: "${groupSearch.baseDn}", filter: "${groupSearch.filter}", searchSubtree: "${groupSearch.searchSubtree}" });
		</c:forEach>

		var $pane = ssPage.createConnection("${config.url}", "${config.userIdAttribute}",
										    "<%=mapText.toString()%>", initialUserSearches, initialGroupSearches,
											"${config.principal}", "${config.credentials}");
		$pane.append(jQuery('<span id="${config.id}" style="display:none" class="ldapId">${config.id}</span>'));
	</c:forEach>
	
		jQuery('#funkyDiv > ul').bind('tabsshow', function(event, ui) {
			ssPage.currentTab = ui.index;
		});
		jQuery("#funkyDiv > ul").tabs("select", 0);
		jQuery("#funkyDiv > ul").tabs("remove", 0);

		// Did an error happen while doing an ldap sync?
		<c:if test="${!empty ssException}">
			var errMsg;

			// Yes, tell the user about it.
			errMsg = '<ssf:escapeJavaScript><ssf:nlt tag="administration.errors"/>${ssException}</ssf:escapeJavaScript>';
			alert( errMsg );

			// Do we have the id of the ldap configuration that had the error?
			m_ldapConfigId = '${ssErrLdapConfigId}';
			if ( m_ldapConfigId != null && m_ldapConfigId.length > 0 )
			{
				// Yes, Show the ldap configuration that had the error.
				// We can't call ssPage.showLdapConfig() right now.  Wait for .5 second.
				setTimeout( ssPage.showLdapConfig, 500 );
			}
		</c:if>

		
		// Are we supposed to start an ldap sync?
		<c:if test="${!empty startLdapSync}">
			// Yes
			// Start an ldap sync.
			startLdapSync();
		</c:if>
	});
</script></div>

<!-- The following <div> is the Sync Results dialog.  This dialog will display -->
<!-- all of the results from the ldap sync -->
<div id="syncResultsDlg" class="syncResultsDialog" style="left: 200px; top: 200px; display: none;">
	<div class="syncResultsTitle">
		<span><ssf:nlt tag="ldap.syncResults.title" /></span>
		
		<!-- This is where the status of the sync will be displayed. -->
		<div style="margin-left: 2em;">
			<span style="margin-right: .5em;"><ssf:nlt tag="ldap.syncResults.status" /></span>
			<img id="syncResultsStatusImg" src="" align="absmiddle" border="0" >
			<span style="margin-left: .2em;" id="syncResultsStatusText"></span>
		</div>
	</div>

	<!-- This section holds all of the results. -->
	<div id="allSyncResultsDiv" style="width: 400px; height: 500px; overflow: auto;">
		<table id="addedUsersTable" width="100%" height="16%" cellspacing="0">
			<!-- This section holds the list of users that were added -->
			<tr class="syncResultsSectionHeaderTR">
				<td class="syncResultsSectionHeaderTD">
					<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.addedUsers" /><span style="margin-left: .2em;" id="numAddedUsersSpan">0</span></span>
				</td>
			</tr>
			<!-- No users were added. -->
			<tr id="noAddedUsersTR">
				<td><span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span></td>
			</tr>
			<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>
		</table>

		<table id="modifiedUsersTable" width="100%" height="16%" cellspacing="0">
			<!-- This section holds the list of users that were modified -->
			<tr class="syncResultsSectionHeaderTR">
				<td class="syncResultsSectionHeaderTD">
					<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.modifiedUsers" /><span style="margin-left: .2em;" id="numModifiedUsersSpan">0</span></span>
				</td>
			</tr>
			<!-- No users were modified. -->
			<tr id="noModifiedUsersTR">
				<td>
					<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
				</td>
			</tr>
			<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>
		</table>

		<table id="deletedUsersTable" width="100%" height="16%" cellspacing="0">
			<!-- This section holds the list of users that were deleted -->
			<tr class="syncResultsSectionHeaderTR">
				<td class="syncResultsSectionHeaderTD">
					<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.deletedUsers" /><span style="margin-left: .2em;" id="numDeletedUsersSpan">0</span></span>
				</td>
			</tr>
			<!-- No users were deleted. -->
			<tr id="noDeletedUsersTR">
				<td>
					<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
				</td>
			</tr>
			<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>
		</table>

		<table id="addedGroupsTable" width="100%" height="16%" cellspacing="0">
			<!-- This section holds the list of groups that were added -->
			<tr class="syncResultsSectionHeaderTR">
				<td class="syncResultsSectionHeaderTD">
					<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.addedGroups" /><span style="margin-left: .2em;" id="numAddedGroupsSpan">0</span></span>
				</td>
			</tr>
			<!-- No groups were added. -->
			<tr id="noAddedGroupsTR">
				<td>
					<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
				</td>
			</tr>
			<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>
		</table>

		<table id="modifiedGroupsTable" width="100%" height="16%" cellspacing="0">
			<!-- This section holds the list of groups that were modified -->
			<tr class="syncResultsSectionHeaderTR">
				<td class="syncResultsSectionHeaderTD">
					<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.modifiedGroups" /><span style="margin-left: .2em;" id="numModifiedGroupsSpan">0</span></span>
				</td>
			</tr>
			<!-- No groups were modified. -->
			<tr id="noModifiedGroupsTR">
				<td>
					<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
				</td>
			</tr>
			<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>
		</table>

		<table id="deletedGroupsTable" width="100%" height="16%" cellspacing="0">
			<!-- This section holds the list of groups that were deleted -->
			<tr class="syncResultsSectionHeaderTR">
				<td class="syncResultsSectionHeaderTD">
					<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.deletedGroups" /><span style="margin-left: .2em;" id="numDeletedGroupsSpan">0</span></span>
				</td>
			</tr>
			<!-- No groups were deleted. -->
			<tr id="noDeletedGroupsTR">
				<td>
					<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
				</td>
			</tr>
			<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>
		</table>
	</div>

	<!-- Buttons -->
	<div class="margintop3 rowaltcolor" style="padding: 0.5em; border-top: 1px solid #babdb6">
		<a id="syncResultDlg_close" title="<ssf:nlt tag="ldap.syncResults.alt.close"/> href="#" onclick="closeSyncResultsDlg()">
			<input type="button" name="closeSyncResultsDlgBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
		</a>
	</div>
</div>

<div id="ldapTemplate" style="display: none">
<fieldset class="ldapConfig ss_fieldset"><legend
	class="ldapTitle ss_legend"><ssf:nlt
	tag="ldap.connection.title" /> <span class="ldapTitle"></span></legend>
<div>
<button class="ldapDelete ss_submit"><ssf:nlt
	tag="ldap.connection.delete" /></button>
<br />
<br />
<table>
	<tr>
		<td nowrap></td>
		<td nowrap><span class="ss_fineprint ss_bright"><ssf:nlt
			tag="ldap.user.url.title" /></span></td>
	</tr>
	<tr>
		<td nowrap><label for="ldapUrl"><ssf:nlt
			tag="ldap.user.url" />&nbsp;</label></td>
		<td><input class="ldapUrl" id="ldapUrl" type="text" value=""
			size="140" /></td>
	</tr>
	<tr>
		<td nowrap><label for="ldapPrincipal"><ssf:nlt
			tag="ldap.user.principal" /></label></td>
		<td><input class="ldapPrincipal" id="ldapPrincipal" type="text"
			value="" size="140" /></td>
	</tr>
	<tr>
		<td nowrap><label for="ldapCredentials"><ssf:nlt
			tag="ldap.user.credential" /></label></td>
		<td><input class="ldapCredentials" id="ldapCredentials"
			type="password" value="" size="140" /></td>
	</tr>
</table>

<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
	tag="ldap.users" /></legend> <label for="ldapUserIdAttribute"><ssf:nlt
	tag="ldap.user.idmapping" />&nbsp;&nbsp;</label><input
	class="ldapUserIdAttribute" id="ldapUserIdAttribute" type="text"
	value="" size="40" /><br />
<br />
<label for="ldapMappings"><ssf:nlt tag="ldap.user.mappings" /></label>
<br />
<textarea class="ldapMappings" id="ldapMappings"
	style="height: 150px; width: 400px" wrap="hard"></textarea> <br />
<br />
<div class="ldapUserSearches">
<div class="ldapSearchList"></div>
<button class="addSearch ss_submit"><ssf:nlt
	tag="ldap.search.add" /></button>
</div>
</fieldset>
<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
	tag="ldap.groups" /></legend>
<div class="ldapGroupSearches">
<div class="ldapSearchList"></div>
<button class="addSearch ss_submit"><ssf:nlt
	tag="ldap.search.add" /></button>
</div>
</fieldset>
</div>
</fieldset>
</div>

<div id="ldapSearchTemplate" style="display: none">
<div class="ldapSearch" style="padding: 6px; border: 1px solid #cecece;">
<div>
<table>
	<tr>
		<td colspan="2">
		<div class="errorMessage"><ssf:nlt tag="ldap.error.invalid">
			<ssf:param name="value" value='<%=NLT.get("ldap.error.baseDn")%>' />
		</ssf:nlt></div>
		</td>
	</tr>
	<tr>
		<td nowrap><label for="ldapBaseDn"><ssf:nlt
			tag="ldap.search.baseDn" /></label></td>
		<td><input class="ldapBaseDn" id="ldapBaseDn" value="" size="120" />
		</td>
	</tr>

	<tr>
		<td colspan="2">
		<div class="errorMessage"><ssf:nlt tag="ldap.error.invalid">
			<ssf:param name="value" value='<%=NLT.get("ldap.error.filter")%>' />
		</ssf:nlt></div>
		</td>
	</tr>
	<tr>
		<td nowrap><label for="ldapFilter"><ssf:nlt
			tag="ldap.search.filter" /></label></td>
		<td><input class="ldapFilter" id="ldapFilter" value="" size="120" />
		</td>
	</tr>
</table>
</div>
<br />
<input type="checkbox" class="ldapSearchSubtree" id="ldapSearchSubtree"
	value="true" /> <label for="ldapSearchSubtree"><span
	style="padding-left: 4px;"><ssf:nlt
	tag="ldap.search.searchSubtree" /></span></label> <br />
<button class="deleteSearch ss_submit"><ssf:nlt
	tag="ldap.search.delete" /></button>
</div>
</div>
</body>
</html>