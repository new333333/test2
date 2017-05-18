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
<%@ page import="org.kablink.teaming.util.NLT"%>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp"%>
<c:set var="ss_windowTitle"
	value='<%= NLT.get("administration.configure_ldap") %>' scope="request" />
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>

<%
	User currentUser = null;
%>

<script type="text/javascript">

// m_searchCount is used to keep track of how many search filters have been created.
var m_searchCount = 0;

</script>

<body class="ss_style_body tundra" onUnload="onUnloadEventHandler();">
<div class="ss_pseudoPortal">
	<div class="ss_style ss_portlet">
		<ssf:form titleTag="ldap.title">
			<c:if test="${!empty ssException}">
				<span class="ss_largerprint"><ssf:nlt
					tag="administration.errors" /> (<c:out value="${ssException}" />)</span>
				<br/>
			</c:if>
		
			<form class="ss_style ss_form" name="${renderResponse.namespace}fm"
				method="post"
				action="<ssf:url action="configure_ldap" actionUrl="true"/>">


				<button id="ldapAddConnection" class="ss_submit margintop2"><ssf:nlt tag="ldap.connection.add" /></button>
		
				
				<div class="margintop3">
					<div id="funkyDiv">
						<div id="ulDiv">
							<ul>
								<li>
									<a href='#wah'>Wah</a>
								</li>
							</ul>
						</div>
						<div class="ss_buttonBarRight margintop1">
							<input type="submit" class="ss_submit" name="okBtn"
								value="<ssf:nlt tag="button.apply"/>">
							<input type="button"
								class="ss_submit" name="closeBtn"
								value="<ssf:nlt tag="button.close" text="Close"/>"
								onClick="handleCloseBtn();return false;" />
						</div>

						<div id="funkyDiv2" class="margintop2"></div>
						<div id="wah" class="margintop2"></div>
					</div>
				</div>
		
				<table class="ss_style margintop3" border="0" cellspacing="0" cellpadding="3">
					<tr>
						<td>
							<!-- This hidden input is used to store the ids of the configs that need their guid syncd -->
							<input id="listOfLdapConfigsToSyncGuid" name="listOfLdapConfigsToSyncGuid" type="hidden" value="" />

							<input type="checkbox" id="runnow" name="runnow"
							<c:if test="${runnow}"> checked="checked" </c:if> /> <label
							for="runnow"><span class="ss_labelRight ss_normal"><ssf:nlt
							tag="ldap.schedule.now" /></span>

						</label></td>
					</tr>
					<tr>
						<td><input type="checkbox" id="enabled" name="enabled"
							<c:if test="${ssLdapConfig.enabled}">checked</c:if> /> <label
							for="enabled"><span class="ss_labelRight ss_normal"><ssf:nlt
							tag="ldap.schedule.enable" /></span>
						</label></td>
					</tr>
				</table>
		
				<div class="margintop2" style="margin-left: 2.5em;">
					<ssf:expandableArea title='<%= NLT.get("ldap.schedule") %>' initOpen="true">
						<c:set var="schedule" value="${ssLdapConfig.schedule}" />
						<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
					</ssf:expandableArea>
				</div>	
			
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
							<td>
								<input type="radio" name="notInLdap" id="userDisable" value="false"
									<c:if test="${ssLdapConfig.userDelete == 'false'}">checked</c:if> />
								<label for="userDisable">
									<span class="ss_labelRight ss_normal">
										<ssf:nlt tag="ldap.schedule.user.disable" />
									</span>
								</label>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="notInLdap" id="userDelete" value="true"
									<c:if test="${ssLdapConfig.userDelete}">checked</c:if> />
								<label for="userDelete">
									<span class="ss_labelRight ss_normal">
										<ssf:nlt tag="ldap.schedule.user.delete" />
									</span>
								</label>
							</td>
						</tr>
						<tr>
							<td>
								<div style="margin-left: 34px;">
									<input type="checkbox" name="userWorkspaceDelete"
										id="userWorkspaceDelete"
										<c:if test="${ssLdapConfig.userWorkspaceDelete}">checked</c:if> />
									<label for="userWorkspaceDelete">
										<span class="ss_labelRight ss_normal">
											<ssf:nlt tag="ldap.schedule.user.workspace.delete" />
										</span>
									</label>
								</div>
							</td>
						</tr>

						<!-- Create a <select> control to hold the list of time zones. -->
						<tr>
							<td>
								<label for="ssDefaultTimeZone">
									<div style="margin-top: .25em;" class="ss_labelAbove"><ssf:nlt tag="ldap.config.default.timezone" /></div>
								</label>
								<select name="ssDefaultTimeZone" id="ssDefaultTimeZone">
								<%
									TreeMap<String, String> tzones;

									currentUser = (User)request.getAttribute( "ssUser" );
									
									// Get all of the time zones.
									tzones = org.kablink.teaming.calendar.TimeZoneHelper.getTimeZoneIdDisplayStrings( currentUser );
									
									for ( Map.Entry me : tzones.entrySet() )
									{
										String tz;
										String checked = "";
									
										tz = (String) me.getValue();
								%>
									<c:set var="nextTimeZone" value="<%= tz %>" />
									<option value="<%= tz %>" <c:if test="${ssDefaultTimeZone == nextTimeZone}">selected</c:if> ><%= (String) me.getKey() %></option>
								<%
									};
								%>
								</select>
							</td>
						</tr>

						<!-- Create a <select> control to hold the list of locales. -->
						<tr>
							<td>
								<label for="ssDefaultLocale">
									<div style="margin-top: .25em;" class="ss_labelAbove"><ssf:nlt tag="ldap.config.default.locale" /></div>
								</label>
								<select name="ssDefaultLocaleId" id="ssDefaultLocaleId">
								<%
									TreeMap<String,Locale> localeMap = null;

									currentUser = (User)request.getAttribute( "ssUser" );
									
									// Get a sorted list of all the locales.
									localeMap = NLT.getSortedLocaleList( currentUser );

									// Add an <option> to the <select> control for every locale.
									for ( Map.Entry<String, Locale> me: localeMap.entrySet() )
									{
										String localeDisplayName;
										String localeId;
										
										// Get the name of the next locale.
										localeDisplayName = me.getKey();
										
										// Get the id of the next locale.
										localeId = me.getValue().toString();
								%>
										<c:set var="nextLocaleId" value="<%= localeId %>" />
										<option value="<%= localeId %>" <c:if test="${ssDefaultLocaleId == nextLocaleId}">selected</c:if> ><%= localeDisplayName %></option>
								<%
									}
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
							<td><input type="checkbox" name="groupSync" id="groupSync"
								<c:if test="${ssLdapConfig.groupSync}">checked</c:if> /> <label
								for="groupSync"><span class="ss_labelRight ss_normal"><ssf:nlt
								tag="ldap.schedule.group.sync" /></span></label></td>
						</tr>
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
				<fieldset class="ss_fieldset">
					<legend class="ss_legend"><ssf:nlt tag="ldap.localLogin" /></legend>
					<input type="checkbox" id="allowLocalLogin"
							name="allowLocalLogin"
					<c:if test="${ssAuthenticationConfig.allowLocalLogin}">checked</c:if> />
					<label for="allowLocalLogin"><span
						class="ss_labelRight ss_normal"><ssf:nlt
						tag="ldap.config.allowLocalLogin" /></span>
					</label>
				</fieldset>
			
				<br />
				<div class="ss_buttonBarLeft">
					<input type="submit"
							class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
					<input type="button" class="ss_submit" name="closeBtn"
						value="<ssf:nlt tag="button.close" text="Close"/>"
						onClick="handleCloseBtn();return false;" />
				</div>
				<input type="hidden" name="ldapConfigDoc" id="ldapConfigDoc" value="" />
				<sec:csrfInput />
			</form>
		</ssf:form>
	</div>


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
	margin: 0 0 0 2px;
	font-weight: bold;
	list-style: none !important;
}

.ui-tabs-nav a,.ui-tabs-nav a span {
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
	padding: 10px;
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
var LDAP_SYNC_STATUS_SYNC_ALREADY_IN_PROGRESS = 4;

var m_ldapSyncResultsId = null;
var m_syncAllUsersAndGroups = false;
var m_listOfLdapConfigsToSyncGuid = new Array();
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
	var url;
	var obj;

	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'getLdapSyncResults'
	obj.ldapSyncResultsId = m_ldapSyncResultsId;

	// Build the url used in the ajax request.
	url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );

	// Issue the ajax request.  The function handleResponseToGetSyncResults() will be called
	// when we get the response to the request.
	ss_get_url( url, handleResponseToGetSyncResults );
}// end getSyncResults()

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
<% 	}
	else { %>
		ss_cancelButtonCloseWindow();
<%	} %>
	
}// end handleCloseBtn()


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
	else if ( responseData.status == LDAP_SYNC_STATUS_SYNC_ALREADY_IN_PROGRESS )
	{
		var msg;
		
		// Tell the user that a sync is already in progress.
		msg = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.syncResults.cantStartSyncSyncAlreadyInProgress"/></ssf:escapeJavaScript>';
		alert( msg );

		status = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.syncResults.status.syncAlreadyInProgress"/></ssf:escapeJavaScript>';
		setSyncResultsStatus( status, m_syncStatusCompletedImg );
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
	// Currently the ajax request to start the ldap sync does not return until the ldap sync
	// has completed.  When we implement the ldap sync as a thread on the server we should
	// remove the following return statement.
	m_ldapSyncStatus = LDAP_SYNC_STATUS_COMPLETED;
	return;
	
	// Start a timer.  Whenever the timer goes off we will issue an ajax request
	// to get the latest results from the ldap sync.
	m_syncResultsTimerId = setTimeout( getSyncResults, 2000 );

	m_ldapSyncStatus = LDAP_SYNC_STATUS_IN_PROGRESS;
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
	var url;
	var obj;

	// Do we have an ldap sync results?
	if ( m_ldapSyncResultsId != null && m_ldapSyncResultsId.length > 0 )
	{
		// Yes
		// Set up the object that will be used in the ajax request.
		obj = new Object();
		obj.operation = 'removeLdapSyncResults'
		obj.ldapSyncResultsId = m_ldapSyncResultsId;
	
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
	m_ldapSyncResultsId = Math.random();

	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'startLdapSync'
	obj.ldapSyncResultsId = m_ldapSyncResultsId;
	obj.syncUsersAndGroups = m_syncAllUsersAndGroups;
	obj.listOfLdapConfigsToSyncGuid = m_listOfLdapConfigsToSyncGuid;

	// Build the url used in the ajax request.
	url = ss_buildAdapterUrl( ss_AjaxBaseUrl, obj );
	
	// Issue the ajax request.  The function handleResponseToStartLdapSync() will be called
	// when we get the response to the request.
	//~JW:  _get_url( url, handleResponseToStartLdapSync );
	var bindArgs =
	{
	   	url: url,
		error: function( err )
		{
			// We are going to ignore any errors.  The ajax request to get the
			// status of the sync process will report the errors.
		},
		load: function( data )
		{
			if ( data.failure )
			{
				alert( data.failure );
			}
			else
			{ 
				handleResponseToStartLdapSync( data );
			}
		},
		preventCache: true,				
		handleAs: "json"
	};   
	dojo.xhrGet( bindArgs );

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
	var url;
	var obj;

	// Set up the object that will be used in the ajax request.
	obj = new Object();
	obj.operation = 'stopCollectingLdapSyncResults'
	obj.ldapSyncResultsId = m_ldapSyncResultsId;

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
	m_invalidBaseDnMsg : '<ssf:escapeJavaScript><ssf:nlt tag="ldap.error.invalidBaseDn"/></ssf:escapeJavaScript>',
	m_invalidUserFilterMsg : '<ssf:escapeJavaScript><ssf:nlt tag="ldap.error.invalidUserFilter"/></ssf:escapeJavaScript>',
	m_isBaseDnValid : true,
	m_isUserFilterValid : true,
	m_invalidCtrl : null,
	m_idOfInvalidConfiguration : null,
	m_invalidMappingsMsg : null,
	m_areMappingsValid : true,
	m_ldapGuidAttributeNameChanged : false,
	m_origLdapGuidAttributeNames : new Array(),
	m_listOfLdapConfigsIsExisting : new Array(),
	nextId : 1,
	currentTab : 0,
	defaultUserFilter: "${ssDefaultUserFilter}",
	defaultGroupFilter: "${ssDefaultGroupFilter}",

	createBindings : function($container)
	{
		jQuery(".ldapUrl", $container).change(function() {
			jQuery(this).parent().parent().find(".ldapTitle span").text(jQuery(this).val());
			var id = jQuery(this).parent().parent().attr("id");
			jQuery("#ulDiv > ul li > a[href='#" + id + "']").text(jQuery(this).val());
		});
		jQuery(".ldapDelete", $container).click(function() {
			var title = jQuery("#ulDiv > ul li").eq(ssPage.currentTab).find("span").text();
			var prompt;

			prompt = "<ssf:nlt tag="ldap.connection.delete.confirm" quoteDoubleQuote="true" />";
			prompt = prompt.replace( '{0}', title );
			if( ss_confirm( prompt, null )) {
				var id = jQuery(this).parent().parent().attr("id");
				jQuery("#ulDiv > ul").tabs("remove", ssPage.currentTab);
				jQuery(this).parent().parent().remove();
			}
			return false;
		});	
		jQuery(".ldapUserSearches button.addSearch", $container).click(function() {
			ssPage.createSearchEntry(jQuery(this).prev(), "", ssPage.defaultUserFilter, "true");
			return false;
		});
		jQuery(".ldapGroupSearches button.addSearch", $container).click(function() {
			ssPage.createSearchEntry(jQuery(this).prev(), "", ssPage.defaultGroupFilter, "true");
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
		
		// Give the "Search subtree" checkbox a unique id and update the <label for="xxx">
		// so it is tied to the "Search subtree" checkbox.  This is the fix for bug 684965
		{
			var newId;
			
			// Give the "Search subtree" checkbox a unique id.
			newId = 'ldapSearchSubtree' + m_searchCount;
			$newSearch.find( '#ldapSearchSubtree' ).attr( 'id', newId );
			
			// Associate the <label> with the id of the "Search subtree" checkbox.
			$newSearch.find( '#ldapSearchSubtreeLabel' ).attr( 'for', newId );
			
			++m_searchCount;
		}
		
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

	createConnection : function(url, userIdAttribute, mappings, userSearches, groupSearches, principal, credentials, ldapGuidAttribute, existingSrc )
	{
		var label = "<ssf:nlt tag="ldap.connection.newConnection"/>";
		if(url != "") { label = url; }

		var id = "ldapConn" + ssPage.nextId++;
		var $pane = jQuery('#ldapTemplate').children().clone().hide().attr("id", id);
		$pane.find(".ldapTitle span").text(label).end()
		 	 .find( ".ldapGuidAttribute" ).val( ldapGuidAttribute ).end()
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

		// Add a link the user can click on that will display this ldap configuration.
		var index = jQuery("#ulDiv > ul").tabs("length");
		jQuery("#ulDiv > ul").tabs("add", '#'+ id, label);
		ssPage.selectLdapConfigurationByIndex( index );
		
		// The call to jQuery( "#ulDiv > ul").tabs( "add", '#' + id, label ) moves the <fieldset...> into
		// the div that holds the <ul>.  This causes display problems on IE, see bug 491677.
		// We need to move the <fieldset> out of the <div id="ulDiv"> and into the <div id="funkyDiv2">
		{
			var fldset;
			var funkyDiv;

			fldset = document.getElementById( id );
			fldset.parentNode.removeChild( fldset )
			funkyDiv = document.getElementById( 'funkyDiv2' );
			funkyDiv.appendChild( fldset );
		}

		// Remember the original name of the ldap guid attribute.
		ssPage.rememberLdapGuidAttributeName( id, ldapGuidAttribute );
		
		// Remember whether this ldap config is existing
		ssPage.setIsLdapConfigExisting( id, existingSrc );
		
		ssPage.createBindings($pane);
		$pane.show();
		return $pane;
	},


	/**
	 * Remember the original name of the ldap guid attribute for the given ldap configuration.
	 */
	rememberLdapGuidAttributeName : function( ldapConfigId, name )
	{
		// Save away the name using the ldap config id as the index into the associative array.
		ssPage.m_origLdapGuidAttributeNames[ldapConfigId] = name;
	},// end rememberLdapGuidAttributeName()
	
	
	/**
	 * Get the original name of the ldap guid attribute for the given ldap configuration.
	 */
	getOriginalLdapGuidAttributeName : function( ldapConfigId )
	{
		// The names are stored in an associative array with the config id as the index into the array.
		return ssPage.m_origLdapGuidAttributeNames[ldapConfigId];
	},// end getOriginalLdapGuidAttributeName()
	
	/**
	 * Return whether the given ldap config is existing
	 */
	 getIsExistingSource : function( ldapConfigId )
	 {
		return ssPage.m_listOfLdapConfigsIsExisting[ldapConfigId]; 
	 },
	 
	 /**
	  * Remember whether this ldap config is existing
	  */
	 setIsLdapConfigExisting : function( ldapConfigId, existing )
	 {
		 ssPage.m_listOfLdapConfigsIsExisting[ldapConfigId] = existing;
	 },
	
	/**
	 * Mark the given ldap config as needing to sync the guid
	 */
	 markConfigAsNeedingToSyncGuid : function( ldapConfigId )
	 {
		if ( ldapConfigId != null && ldapConfigId.length > 0 )
			m_listOfLdapConfigsToSyncGuid[m_listOfLdapConfigsToSyncGuid.length] = ldapConfigId;
	 },
	
	
	/**
	 * Find all base dns in all ldap configurations and make sure the user has entered something for the base dn.
	 */
	validateAllLdapConfigurations : function()
	{
		var validateBaseDn;
		var validateUserFilter;
		var validateMappings;
		
		validateBaseDn = function()
		{
			var $this;
			var $baseDn;
			var baseDn;

			// If we already found an invalid base dn, there is no need to continue.
			if ( !ssPage.m_isBaseDnValid )
				return;
			
			$this = jQuery( this );
			$baseDn = jQuery('.ldapBaseDn', $this);
			baseDn = $baseDn.val();

			if ( baseDn == null || baseDn.length == 0 )
			{
				// Get the id of the invalid configuration
				ssPage.m_idOfInvalidConfiguration = $this.parent().parent().parent().parent().parent().attr( "id" );				
				ssPage.m_invalidCtrl = $baseDn;
				ssPage.m_isBaseDnValid = false;
			}
		};
		
		// Make sure the user has entered something for every base dn.
		ssPage.m_idOfInvalidConfiguration = null;
		ssPage.m_isBaseDnValid = true;
		ssPage.m_invalidCtrl = null;
		jQuery( '#funkyDiv .ldapUserSearches .ldapSearch' ).each( validateBaseDn );
		if ( !ssPage.m_isBaseDnValid )
		{
			// No, tell the user the base dn cannot be empty.
			alert( ssPage.m_invalidBaseDnMsg );

			// Show the configuration that has the error.
			if ( ssPage.m_idOfInvalidConfiguration != null )
				setTimeout( ssPage.showInvalidLdapConfig, 50 );

			return false;
		}

		validateUserFilter = function()
		{
			var $this;
			var $userFilter;
			var userFilter;

			// If we already found an invalid user filter, there is no need to continue.
			if ( !ssPage.m_isUserFilterValid )
				return;
			
			$this = jQuery( this );
			$userFilter = jQuery( '.ldapFilter', $this );
			userFilter = $userFilter.val();

			if ( userFilter == null || userFilter.length == 0 )
			{
				// Get the id of the invalid configuration
				ssPage.m_idOfInvalidConfiguration = $this.parent().parent().parent().parent().parent().attr( "id" );				
				ssPage.m_invalidCtrl = $userFilter;
				ssPage.m_isUserFilterValid = false;
			}
		};
		
		// Make sure the user has entered something for every user filter.
		ssPage.m_idOfInvalidConfiguration = null;
		ssPage.m_isUserFilterValid = true;
		ssPage.m_invalidCtrl = null;
		jQuery( '#funkyDiv .ldapUserSearches .ldapSearch' ).each( validateUserFilter );
		if ( !ssPage.m_isUserFilterValid )
		{
			// No, tell the user the user filter cannot be empty.
			alert( ssPage.m_invalidUserFilterMsg );

			// Show the configuration that has the error.
			if ( ssPage.m_idOfInvalidConfiguration != null )
				setTimeout( ssPage.showInvalidLdapConfig, 50 );

			return false;
		}

		// This function will check the ldap mappings.  The "title" field cannot be sync'd.
		validateMappings = function()
		{
			var $mappingsTextarea;
			var mappings;
			
			// If we already found an invalid mapping, there is no need to continue.
			if ( ssPage.m_areMappingsValid == false )
				return;

			// Get the mappings the user entered.
			$mappingsTextarea = jQuery( this );
			mappings = $mappingsTextarea.val();
			if ( mappings != null && mappings.length > 0 )
			{
				var arrayOfMaps;

				// Split the mappings into their key=value pairs.
				arrayOfMaps = mappings.split( '\n' );
				if ( arrayOfMaps != null && arrayOfMaps.length > 0 )
				{
					var i;

					for (i = 0; i < arrayOfMaps.length && ssPage.m_areMappingsValid == true; ++i)
					{
						var map;

						map = arrayOfMaps[i];
						if ( map != null && map.length > 0 )
						{
							var value;

							value = map.split( '=' );
							if ( value != null && value.length == 2 )
							{
								var fldName;
								
								// Remove whitespace from the front and end of the field name.
								fldName = value[0].toLowerCase();
								fldName = fldName.replace(/^\s\s*/, '').replace(/\s\s*$/, '');

								// Is the user trying to sync the "title" field?
								if ( fldName == 'title' )
								{
									// Yes
									// Get the id of the invalid configuration
									ssPage.m_idOfInvalidConfiguration = $mappingsTextarea.parent().parent().parent().attr( "id" );
									ssPage.m_invalidCtrl = $mappingsTextarea;
									ssPage.m_areMappingsValid = false;
									ssPage.m_invalidMappingsMsg = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.error.cant.sync.title" /></ssf:escapeJavaScript>';
								}
							}
						}
					}
				}
			}
		};

		// Make sure the ldap mappings are valid.
		ssPage.m_idOfInvalidConfiguration = null;
		ssPage.m_invalidMappingsMsg = null;
		ssPage.m_areMappingsValid = true;
		ssPage.m_invalidCtrl = null;
		jQuery( '#funkyDiv .ldapMappings' ).each( validateMappings );
		if ( ssPage.m_areMappingsValid == false)
		{
			// No, tell the user the mappings are not valid.
			alert( ssPage.m_invalidMappingsMsg );

			// Show the configuration that has the error.
			if ( ssPage.m_idOfInvalidConfiguration != null )
				setTimeout( ssPage.showInvalidLdapConfig, 50 );

			return false;
		}

		return true;
	},// end validateAllLdapConfigurations()
	
	/**
	 * This function gets called when the user clicks on the "Add a new ldap connection" button.
	 */
	addConnection : function() {
		var msg;
		
		// Tell the user that adding an ldap connection is intended to add a new ldap directory
		// as a source of users.  It is not intended to be a fallback or failover server.
		msg = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.connection.add.warning"><ssf:param name="value" value="${productName}" /></ssf:nlt></ssf:escapeJavaScript>';
		alert( msg );

		var $pane = ssPage.createConnection("", "uid", ssPage.defaultUserMappings, [], [], "", "", "", "false" );
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
	 * This function gets called when the user clicks on the "Delete users that are not in LDAP" checkbox.
	 * If the user is checking this checkbox we will warn them about the consequences.
	 */
	onClickDeleteUsersNotInLdap : function()
	{
		var msg;
		var input;
		
		// Is the "delete users that are not in ldap" checkbox checked?
		input = document.getElementById( 'userDelete' );
		if ( input.checked )
		{
			// Yes
			// Tell the user that selecting this option is dangerous.
			msg = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.delete.users.not.in.ldap.warning"><ssf:param name="value" value="${productName}" /></ssf:nlt></ssf:escapeJavaScript>';
			alert( msg );
		}
	},

	/**
	 * Select the given ldap configuration.
	 */
	selectLdapConfigurationByIndex : function( index )
	{
		jQuery("#ulDiv > ul").tabs( "select", index );
	},// end selectLdapConfigurationByIndex()


	/**
	 * Select the ldap configuration by id.
	 */
	selectLdapConfigurationById : function( id )
	{
		jQuery( "#ulDiv > ul").tabs( "select", '#' + id );
	},// end selectLdapConfigurationById()

	
	/**
	 * Select the tab for the ldap configuration that is invalid.
	 */
	showInvalidLdapConfig : function()
	{
		if ( ssPage.m_idOfInvalidConfiguration != null )
		{
			ssPage.selectLdapConfigurationById( ssPage.m_idOfInvalidConfiguration );
		
			// Give the focus to the appropriate control.
			if ( ssPage.m_invalidCtrl != null )
				ssPage.m_invalidCtrl.focus();
		}
	},// end showInvalidLdapConfig()


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
					ssPage.selectLdapConfigurationById( fldset.id );
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
	
	jQuery( '#userDelete' ).click( ssPage.onClickDeleteUsersNotInLdap );
	
	jQuery("form").submit(function() {
		var ldapDoc="<ldapConfigs>";
		var valid = true;

		// Make sure the user has entered a base dn for every ldap configuration.
		if ( ssPage.validateAllLdapConfigurations() == false )
		{
			// If we get here it means that a base dn was empty.  validateAllLdapConfigurations() will tell the user about the problem.
			return false;
		}

		// Go through each ldap configuration and check to see if the name of the
		// ldap guid attribute has changed.  If it has changed
		// ssPage.m_ldapGuidAttributeNameChanged will be set to true
		ssPage.m_ldapGuidAttributeNameChanged = false;
		m_listOfLdapConfigsToSyncGuid = new Array();
		jQuery('#funkyDiv .ldapConfig').each(function()
		{
			var $this = jQuery( this );
			var id;
			var origName;
			var newName;
			var ldapUrl;
			var existingSrc;

			// Get the id of this ldap configuration.
			id = $this.attr("id");

			// Get the ldap url
			ldapUrl = $this.find( '.ldapUrl' ).val();
			
			// Is this an existing ldap source?
			existingSrc = ssPage.getIsExistingSource( id );
			
			// Get the original name of the ldap guid attribute.
			origName = ssPage.getOriginalLdapGuidAttributeName( id );

			// Get the new name of the ldap guid attribute.
			newName = $this.find( '.ldapGuidAttribute' ).val();

			// Are we dealing with an existing ldap source.
			if ( existingSrc == 'true' )
			{
				// Yes
				// Was there a prior value for guid?
				if ( origName == null || origName.length == 0 )
				{
					// No
					// Do we have a new value for guid?
					if ( newName != null && newName.length > 0 )
					{
						// Yes
						ssPage.m_ldapGuidAttributeNameChanged = true;
						ssPage.markConfigAsNeedingToSyncGuid( ldapUrl );
					}
				}
				else
				{
					// Yes
					if ( origName != newName )
					{
						ssPage.m_ldapGuidAttributeNameChanged = true;
						ssPage.markConfigAsNeedingToSyncGuid( ldapUrl );
					}
				}
			}
		});

		// Did the ldap guid change?
		if ( ssPage.m_ldapGuidAttributeNameChanged )
		{
			var input;
			
			// Yes
			// Did the user check the 'Run Immediately' checkbox.
			input = document.getElementById( 'runnow' );
			if ( input.checked == false )
			{
				var msg;
				
				// No, Tell the user we need to sync the ldap guid because the ldap guid attribute name changed.
				msg = '<ssf:escapeJavaScript><ssf:nlt tag="ldap.syncGuids.Msg"><ssf:param name="value" value="${productName}" /></ssf:nlt></ssf:escapeJavaScript>';
				alert( msg );
			}
			
			input = document.getElementById( 'listOfLdapConfigsToSyncGuid' );
			input.value = m_listOfLdapConfigsToSyncGuid;
		}

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

			// Add information about the ldap attribute that uniquely identifies a user or group.
			ldapDoc += "<ldapGuidAttribute>" + wrapWithCDATA( $this.find( '.ldapGuidAttribute' ).val() ) + "</ldapGuidAttribute>";
			
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
	
		jQuery("#ulDiv > ul").tabs();
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
		var tmp;
		<c:forEach var="userSearch" items="${config.userSearches}">
			initialUserSearches.push({ baseDn: "<ssf:escapeJavaScript>${userSearch.baseDn}</ssf:escapeJavaScript>", filter: "<ssf:escapeJavaScript>${userSearch.filter}</ssf:escapeJavaScript>", searchSubtree: "${userSearch.searchSubtree}" });
		</c:forEach>
		var initialGroupSearches = [ ];
		<c:forEach var="groupSearch" items="${config.groupSearches}">
			initialGroupSearches.push({ baseDn: "<ssf:escapeJavaScript>${groupSearch.baseDn}</ssf:escapeJavaScript>", filter: "<ssf:escapeJavaScript>${groupSearch.filter}</ssf:escapeJavaScript>", searchSubtree: "${groupSearch.searchSubtree}" });
		</c:forEach>

		// Replace the string "\n" with the carriage return character.
		tmp = "<ssf:escapeJavaScript><%= mapText.toString() %></ssf:escapeJavaScript>";
		tmp = tmp.replace( /\\n/g, '\x0A' );
		var $pane = ssPage.createConnection(
									"<ssf:escapeJavaScript>${config.url}</ssf:escapeJavaScript>",
									"<ssf:escapeJavaScript>${config.userIdAttribute}</ssf:escapeJavaScript>",
									tmp,
									initialUserSearches,
									initialGroupSearches,
									"<ssf:escapeJavaScript>${config.principal}</ssf:escapeJavaScript>",
									"<ssf:escapeJavaScript>${config.credentials}</ssf:escapeJavaScript>",
									"<ssf:escapeJavaScript>${config.ldapGuidAttribute}</ssf:escapeJavaScript>",
									"true" );
		$pane.append(jQuery('<span id="${config.id}" style="display:none" class="ldapId">${config.id}</span>'));
	</c:forEach>
	
		jQuery('#ulDiv > ul').bind('tabsshow', function(event, ui) {
			ssPage.currentTab = ui.index;
		});

		// Remove the <li>Wah</li>
		ssPage.selectLdapConfigurationByIndex( 0 );
		jQuery("#ulDiv > ul").tabs("remove", 0);
		
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
			if ( '${syncAllUsersAndGroups}' == 'true' )
				m_syncAllUsersAndGroups = true;

			<c:if test="${!empty listOfLdapConfigsToSyncGuid}">
				<c:set var="listOfLdapConfigsToSyncGuid" value="${listOfLdapConfigsToSyncGuid}"/>
				<jsp:useBean id="listOfLdapConfigsToSyncGuid" type="java.util.ArrayList"/>
				<%
					int cnt;
				
					for ( cnt = 0; cnt < listOfLdapConfigsToSyncGuid.size(); ++cnt )
					{
						String nextConfigUrl;
						
						nextConfigUrl = (String) listOfLdapConfigsToSyncGuid.get( cnt );
						%>
						m_listOfLdapConfigsToSyncGuid[<%=cnt%>] = '<%=nextConfigUrl%>';
						<%
					}
				%>
			</c:if>
			
			// Start an ldap sync.
			setTimeout( startLdapSync, 500 );
		</c:if>
	});
</script>
</div>

<!-- The following <div> is the Sync Results dialog.  This dialog will display -->
<!-- all of the results from the ldap sync -->
<div id="syncResultsDlg" class="syncResultsDialog" style="left: 200px; top: 100px; display: none;">
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

<div id="ldapTemplate" style="display: none; margin: 15px;">
	<div class="ldapConfig ss_tertiaryTabs">
		<span class="ldapTitle ss_size_16px"><ssf:nlt tag="ldap.connection.title" /> <span class="ldapTitle ss_bold"></span></span><button class="ldapDelete ss_submit marginleft1"><ssf:nlt tag="ldap.connection.delete" /></button>
		<div>
			<table class="margintop3">
				<tr>
					<td nowrap></td>
					<td nowrap><span class="ss_fineprint ss_bright"><ssf:nlt tag="ldap.user.url.title" /></span></td>
				</tr>
				<tr>
					<td nowrap><label for="ldapUrl"><ssf:nlt tag="ldap.user.url" />&nbsp;</label></td>
					<td><input class="ldapUrl" id="ldapUrl" type="text" value="" size="70" /></td>
				</tr>
				<tr>
					<td nowrap><label for="ldapPrincipal"><ssf:nlt tag="ldap.user.principal" /></label></td>
					<td><input class="ldapPrincipal" id="ldapPrincipal" type="text" value="" size="70" /></td>
				</tr>
				<tr>
					<td nowrap><label for="ldapCredentials"><ssf:nlt tag="ldap.user.credential" /></label></td>
					<td><input class="ldapCredentials" id="ldapCredentials" type="password" value="" size="70" /></td>
				</tr>
			</table>

			<div style="margin-top: 1.25em; margin-bottom: 1.25em;">
				<table>
					<tr>
						<td></td>
						<td nowrap><span class="ss_fineprint ss_bright"><ssf:nlt tag="ldap.guid.hint" /></span></td>
					</tr>
					<tr>
						<td>
							<label for="ldapGuidAttribute" style="margin-right: .5em;"><ssf:nlt tag="ldap.guid" /></label>
						</td>
						<td>
							<input class="ldapGuidAttribute" id="ldapGuidAttribute" type="text" value="" size="30" />
						</td>
					</tr>
				</table>
			</div>

			<fieldset class="ss_fieldset">
				<legend class="ss_legend ss_bold"><ssf:nlt tag="ldap.users" /></legend>
				<label for="ldapUserIdAttribute"><ssf:nlt tag="ldap.user.idmapping"><ssf:param name="value" value="${productName}" /></ssf:nlt>&nbsp;&nbsp;</label>
				<input class="ldapUserIdAttribute" id="ldapUserIdAttribute" type="text" value="" size="40" />
				<br />
				<br />
				<label for="ldapMappings"><ssf:nlt tag="ldap.user.mappings" /></label>
				<br />
				<textarea class="ldapMappings" id="ldapMappings" style="height: 150px; width: 400px" wrap="off"></textarea>
				<br />
				<br />
				<div class="ldapUserSearches">
					<div class="ldapSearchList"></div>
					<button class="addSearch ss_submit" style="margin: 5px;"><ssf:nlt tag="ldap.search.add" /></button>
				</div>
			</fieldset>

			<fieldset class="ss_fieldset">
				<legend class="ss_legend ss_bold"><ssf:nlt tag="ldap.groups" /></legend>
				<div class="ldapGroupSearches">
					<div class="ldapSearchList"></div>
					<button class="addSearch ss_submit" style="margin: 5px;"><ssf:nlt tag="ldap.search.add" /></button>
				</div>
			</fieldset>
		</div>
	</div>
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
					<td><input class="ldapBaseDn" id="ldapBaseDn" value="" size="70" />
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
					<td><textarea class="ldapFilter" id="ldapFilter" wrap="off" rows="6" cols="70"></textarea>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="checkbox" class="ldapSearchSubtree" id="ldapSearchSubtree" value="true" />
						<label for="ldapSearchSubtree" id="ldapSearchSubtreeLabel"><span style="padding-left: 4px;"><ssf:nlt tag="ldap.search.searchSubtree" /></span></label>
					</td>
				</tr>
			</table>
		</div>
		<button class="deleteSearch ss_submit" style="margin: 5px;"><ssf:nlt tag="ldap.search.delete" /></button>
	</div>
</div>
</body>
</html>
