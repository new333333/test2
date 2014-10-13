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

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.report.title.license") %>' scope="request"/>
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
			return false;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

	<script type="text/javascript">
		/**
		 * Validate the data entered by the user.  At this time we only validate the start year and end year.
		 */
		function validateLicenseReportForm()
		{
			var input;
			var errMsg = null;

			// Validate the start year.
			input = document.getElementById( 'ss_startDate_year' );
			if ( input != null )
			{
				if ( validateYear( input.value ) == false )
					errMsg = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.error.invalidStartYear"/></ssf:escapeJavaScript>';
			}

			// Validate the end year.
			input = document.getElementById( 'ss_endDate_year' );
			if ( errMsg == null && input != null )
			{
				if ( validateYear( input.value ) == false )
					errMsg = '<ssf:escapeJavaScript><ssf:nlt tag="administration.report.error.invalidEndYear"/></ssf:escapeJavaScript>';
			}

			// Did we find an error?
			if ( errMsg != null )
			{
				// Yes
				alert( errMsg );
				return false;
			}

			// If we get here everything is fine.
			return true;
		}// end validateLicenseReportForm()


		/**
		 * Validate that the given text is a valid year.
		 */
		function validateYear( txt )
		{
			if ( txt == null )
				return false;

			// Year can only be 4 digits
			if ( txt.length > 4 )
				return false;

			// Does the text parse to a valid number?
			if ( isNaN( parseInt( txt ) ) )
				return false;

			// If we get here the text parses to a valid number.
			return true;
		}// end validateYear()
	</script>

	<div class="ss_pseudoPortal">
		<div class="ss_style ss_portlet">
			<c:set var="formName">${renderResponse.namespace}fm</c:set>

			<ssf:form titleTag="administration.report.title.license">
				<table class="ss_style" width="100%">
					<tr>
						<td>
							<form class="ss_style ss_form" 
									action="<ssf:url action="license_report" actionUrl="true"><ssf:param 
									name="binderId" value="${ssBinder.id}"/><ssf:param 
									name="binderType" value="${ssBinder.entityType}"/></ssf:url>" 
									method="post" 
									onsubmit="return validateLicenseReportForm();"
									name="${formName}">
								<input type="hidden" name="ss_reportType" value="license"/>


								<div class="ss_buttonBarRight">
							    	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
									  onClick="return handleCloseBtn();"/>
								</div>
								
								<div class="ss_largeprint ss_bold marginbottom1"><ssf:nlt tag="administration.report.dates"/></div>
								
								<div class="roundcornerSM" style="border: 1px solid #cccccc; padding: 5px; background-color: #ededed;">

									<div class="n_date_picker" style="display:inline; vertical-align: middle; padding-right: 10px;">
										<ssf:datepicker formName="${formName}" showSelectors="true" 
										popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
										immediateMode="false" altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
										/>
									</div>
									<div id="ss_startPopup" class="ss_calPopupDiv"></div>
									<ssf:nlt tag="smallWords.and"/>
									<div class="n_date_picker" style="display:inline; vertical-align: middle;">
										<ssf:datepicker formName="${formName}" showSelectors="true" 
										popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
										immediateMode="false" altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
										/>
									</div>
			   						<div class="margintop3 marginbottom1" style="margin-left: 5px;">
										<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="administration.create.report" />">
									</div>
							   	</div>
							   	<div id="ss_endPopup" class="ss_calPopupDiv"></div>
							</form>
			
				<c:if test="${not empty ssLicenseKey}">
				
				
						<div class="roundcornerSM margintop2" style="border: 1px solid #cccccc; padding: 5px;">
							<span class="ss_bold"><%= org.kablink.teaming.util.ReleaseInfo.getName() + " " + org.kablink.teaming.util.ReleaseInfo.getVersion() %>&nbsp;&nbsp;
								<ssf:nlt tag="administration.report.title.license"/>:&nbsp;&nbsp;
								<fmt:formatDate value="${ssCurrentDate}" timeZone="${ssUser.timeZone.ID}" type="both" timeStyle="short" dateStyle="medium"/>
							</span><br/>
							<br/>
							<span class="ss_bold"><ssf:nlt tag="license.current"/></span><br/>
							<table cellspacing="6" cellpadding="2">
								<tr>
									<td><ssf:nlt tag="license.product.title"/></td>
									<td>${ssLicenseProductTitle} (${ssLicenseProductVersion})</td>
								</tr>
								<tr>
									<td><ssf:nlt tag="license.key.uid"/></td>
									<td>${ssLicenseKey}</td>
								</tr>
								<tr>
									<td><ssf:nlt tag="license.key.issued"/></td>
									<td><fmt:formatDate value="${ssLicenseIssued}" timeZone="GMT" type="date" dateStyle="medium"/></td>
								</tr>
								<tr>
									<td><ssf:nlt tag="license.effective"/></td>
									<td>
										<fmt:formatDate value="${ssLicenseEffectiveStart}" timeZone="GMT" type="date" dateStyle="medium"/>
										&nbsp;-&nbsp;
										<fmt:formatDate value="${ssLicenseEffectiveEnd}" timeZone="GMT" type="date" dateStyle="medium"/>
									</td>
								</tr>
								<tr>
									<c:if test="${ssLicenseUsers < 0}">
										<td valign="top"><ssf:nlt tag="license.users.registered"/></td>
										<td><ssf:nlt tag="license.users.registered.unlimited"/></td>
									</c:if>
									<c:if test="${ssLicenseUsers >= 0}">
										<td><ssf:nlt tag="license.users.registered"/></td><td>${ssLicenseUsers}</td>
									</c:if>
								</tr>
								<tr>
									<c:if test="${ssLicenseExternalUsers < 0}">
										<td valign="top"><ssf:nlt tag="license.users.external"/></td>
										<td><ssf:nlt tag="license.users.external.unlimited"/></td>
									</c:if>
									<c:if test="${ssLicenseExternalUsers >= 0}">
										<td><ssf:nlt tag="license.users.external"/></td>
										<td>${ssLicenseExternalUsers}</td>
									</c:if>
								</tr>
								<tr>
									  <c:if test="${!empty ssLicenseOptionsList}">
										<td><ssf:nlt tag="license.options"/></td>
										<td>
											<c:forEach var="option" items="${ssLicenseOptionsList}">
									 	 		<span style="padding-left:20px;">${option}</span>
										 		<br/>
									  		</c:forEach>
									  	</td>
									  </c:if>
									  <c:if test="${!empty ssLicenseExternalAccessList}">
										<td><ssf:nlt tag="license.externalaccess"/></td>
										<td>
											<c:forEach var="prop" items="${ssLicenseExternalAccessList}">
									 	 		<span style="padding-left:20px;">${prop}</span>
										 		<br/>
									  		</c:forEach>
									  	</td>
									  </c:if>
								</tr>
							</table>
							<br/>
							<br/>
							<span class="ss_bold"><ssf:nlt tag="administration.report.dates"/></span>
							&nbsp;<fmt:formatDate value="${startDate}" timeZone="${ssUser.timeZone.ID}" type="both" timeStyle="short" dateStyle="medium"/>
							&nbsp;<ssf:nlt tag="smallWords.and"/>&nbsp;<fmt:formatDate value="${endDate}" timeZone="${ssUser.timeZone.ID}" type="both" timeStyle="short" dateStyle="medium"/>
							<br/>
			<c:if test="${not empty ssLicenseData}"><%--
						--%><c:set var="currentUser" value="0"/><%--
						--%><c:forEach var="datum" items="${ssLicenseData}" ><%--
							--%><c:set var="currentUser" value="${datum.activeUserCount}"/><%--
						--%></c:forEach>
						
							<table cellspacing="6" cellpadding="2">
								<tr>
									<td><ssf:nlt tag="license.current.users"/></td><td>${currentUser}</td>
								</tr>
							</table>
							<br/>
							<table cellspacing="6" cellpadding="2" >
								<tr>
									<th><ssf:nlt tag="license.table.date"/>&nbsp;</th>
									<th>&nbsp;<ssf:nlt tag="license.table.localUsers"/>&nbsp;</th>
									<th>&nbsp;<ssf:nlt tag="license.table.syncdUsers"/>&nbsp;</th>
									<th>&nbsp;<ssf:nlt tag="license.table.openIdUsers"/>&nbsp;</th>
									<th>&nbsp;<ssf:nlt tag="license.table.activeUsers"/>&nbsp;</th>
									<th>&nbsp;<ssf:nlt tag="license.table.check"/>&nbsp;</th>
								</tr>
								<c:forEach var="datum" items="${ssLicenseData}" >
									<tr>
										<td><fmt:formatDate value="${datum.snapshotDate}" timeZone="${ssUser.timeZone.ID}" type="date" dateStyle="medium"/></td>
										<td align="center">${datum.internalUserCount}</td>
										<td align="center">${datum.externalUserCount}</td>
										<td align="center">${datum.openIdUserCount}</td>
										<td align="center">${datum.activeUserCount}</td>
										<td align="center">${datum.checksum}</td>
									</tr>
								</c:forEach>
							</table>
							<br/>
							${ssLicenseContact}<br/>
			</c:if>
		</c:if>
						</td>
					</tr>
				</table>
			</div>	
								<div class="ss_buttonBarRight" style="padding-right: 15px;">
							    	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
									  onClick="return handleCloseBtn();"/>
								</div>

			</ssf:form>
		</div>
	</div>
</body>
</html>
