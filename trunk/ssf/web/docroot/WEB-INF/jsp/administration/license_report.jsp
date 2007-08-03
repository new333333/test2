<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ page import="java.util.ArrayList" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="formName"><ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm</c:set>

<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="license_report"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/><portlet:param 
		name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>" 
	method="post" 
	name="${formName}">
<input type="hidden" name="ss_reportType" value="license"/>
<div class="ss_buttonBarRight">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
   <br/>
   <br/>
   <span class="ss_bold"><ssf:nlt tag="administration.report.license"/></span>
   <br/>
   <br/>
   <ssf:nlt tag="administration.report.dates"/>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_startPopup" id="ss_startDate" initDate="${startDate}"
				 immediateMode="false" altText="<%= com.sitescape.team.util.NLT.get("calendar.view.popupAltText") %>"
				 />
   </div>
   <div id="ss_startPopup" class="ss_calPopupDiv"></div>
   <ssf:nlt tag="smallWords.and"/>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				 immediateMode="false" altText="<%= com.sitescape.team.util.NLT.get("calendar.view.popupAltText") %>"
				 />
   </div>
   <br/>
   <div id="ss_endPopup" class="ss_calPopupDiv"></div>
   <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
   </div>
</form>
<c:if test="${not empty ssLicenseData}">
<hr>
<p class="ss_bold">
ICEcore Enterprise Version 1.0<br/>
License Audit Report  -- <fmt:formatDate value="${ssCurrentDate}" pattern="yyyy-MM-dd HH:mm:ss z" timeZone="${ssUser.timeZone.ID}"/>
</p>
<p>
License Information<br/>
  Key id:    ${ssLicenseKey}<br/>
  Issued:    ${ssLicenseIssued}<br/>
  Effective: ${ssLicenseEffective}<br/>
  Users:     ${ssLicenseUsers}<br/>
</p><%--
--%><c:set var="highWater" value="-1"/><%--
--%><c:set var="highWaterDate" value=""/><%--
--%><c:set var="currentUser" value="0"/><%--
	--%><c:forEach var="datum" items="${ssLicenseData}" ><%--
	--%><c:if test="${datum.internalUserCount > highWater}"><%--
	    --%><c:set var="highWater" value="${datum.internalUserCount}"/><%--
	    --%><c:set var="highWaterDate" value="${datum.snapshotDate}"/><%--
	--%></c:if><%--
	--%><c:set var="currentUser" value="${datum.internalUserCount}"/><%--
--%></c:forEach>
<p>
Users highwater mark: ${highWater} (${highWaterDate})<br/>
Current user count: ${currentUser}
</p>
<p class="ss_bold">
Usage History
</p>
<table cellspacing="2"><tbody>
<tr>
<th>Date</th><th>Registered</th><th>External</th><th>Check</th>
</tr>
<c:forEach var="datum" items="${ssLicenseData}" >
<tr>
<td><fmt:formatDate value="${datum.snapshotDate}" pattern="yyyy-MM-dd" timeZone="${ssUser.timeZone.ID}"/></td>
<td style="text-align: right">${datum.internalUserCount}</td>
<td style="text-align: right">${datum.externalUserCount}</td>
<td style="text-align: right">${datum.checksum}</td>
</tr>
</c:forEach>
</table>
<p>  
Report checksum: 41fec13e30afd9cffe48a20ba5ce55982f95c0cf
</p>
<p>
${ssLicenseContact}
</p>   
</c:if>
</td></tr></table>

