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

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.report.title.license") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
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
								name="${formName}">
								<input type="hidden" name="ss_reportType" value="license"/>
								<div class="ss_buttonBarRight">
							    	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
							     	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							    	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
									  onClick="self.window.close();return false;"/>
								</div>
							   	<br/>
							   	<br/>
							   	<span class="ss_bold"><ssf:nlt tag="administration.report.license"/></span>
							   	<br/>
							   	<br/>
							   	<ssf:nlt tag="administration.report.dates"/>
							   	<div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
											 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
											 immediateMode="false" altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
											 />
							   	</div>
							   	<div id="ss_startPopup" class="ss_calPopupDiv"></div>
							   	<ssf:nlt tag="smallWords.and"/>
							   	<div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
											 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
											 immediateMode="false" altText='<%= org.kablink.teaming.util.NLT.get("calendar.view.popupAltText") %>'
											 />
							   	</div>
							   	<br/>
							   	<div id="ss_endPopup" class="ss_calPopupDiv"></div>
							   	<div class="ss_buttonBarLeft">
							    	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
							     	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							    	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
									  onClick="self.window.close();return false;"/>
							   	</div>
							</form>
			
			<c:if test="${not empty ssLicenseKey}">
							<hr>
							<span class="ss_bold"><%= org.kablink.teaming.util.ReleaseInfo.getName() + " " + org.kablink.teaming.util.ReleaseInfo.getVersion() %>
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
							</table>
							<br/>
							<br/>
							<span class="ss_bold"><ssf:nlt tag="administration.report.dates"/></span>
							&nbsp;<fmt:formatDate value="${startDate}" timeZone="${ssUser.timeZone.ID}" type="both" timeStyle="short" dateStyle="medium"/>
							&nbsp;<ssf:nlt tag="smallWords.and"/>&nbsp;<fmt:formatDate value="${endDate}" timeZone="${ssUser.timeZone.ID}" type="both" timeStyle="short" dateStyle="medium"/>
							<br/>
			<c:if test="${not empty ssLicenseData}"><%--
						--%><c:set var="highWater" value="-1"/><%--
						--%><c:set var="highWaterDate" value=""/><%--
						--%><c:set var="currentUser" value="0"/><%--
							--%><c:forEach var="datum" items="${ssLicenseData}" ><%--
							--%><c:if test="${(datum.internalUserCount + datum.externalUserCount) > highWater}"><%--
							    --%><c:set var="highWater" value="${datum.internalUserCount + datum.externalUserCount}"/><%--
							    --%><c:set var="highWaterDate" value="${datum.snapshotDate}"/><%--
							--%></c:if><%--
							--%><c:set var="currentUser" value="${datum.internalUserCount + datum.externalUserCount}"/><%--
						--%></c:forEach>
						
							<table cellspacing="6" cellpadding="2">
								<tr>
									<td><ssf:nlt tag="license.users.highwater"/></td>
									<td>${highWater}&nbsp;(<fmt:formatDate value="${highWaterDate}" timeZone="${ssUser.timeZone.ID}" type="both" dateStyle="medium" timeStyle="short"/>)</td>
								</tr>
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
									<th>&nbsp;<ssf:nlt tag="license.table.check"/>&nbsp;</th>
								</tr>
								<c:forEach var="datum" items="${ssLicenseData}" >
									<tr>
										<td><fmt:formatDate value="${datum.snapshotDate}" timeZone="${ssUser.timeZone.ID}" type="date" dateStyle="medium"/></td>
										<td align="center">${datum.internalUserCount}</td>
										<td align="center">${datum.externalUserCount}</td>
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
			</ssf:form>
		</div>
	</div>
</body>
</html>
