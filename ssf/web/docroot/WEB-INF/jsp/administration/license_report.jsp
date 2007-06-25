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
<c:set var="formName"><portlet:namespace />fm</c:set>

<table class="ss_style" width="100%"><tr><td>
<form class="ss_style ss_form" 
	action="<portlet:actionURL><portlet:param 
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
				 immediateMode="false" altText="<ssf:nlt tag='calendar.view.popupAltText'/>"
				 />
   </div>
   <div id="ss_startPopup" class="ss_calPopupDiv"></div>
   <ssf:nlt tag="smallWords.and"/>
   <div class="ss_toolbar_color" style="display:inline;"><ssf:datepicker formName="${formName}" showSelectors="true" 
				 popupDivId="ss_endPopup" id="ss_endDate" initDate="${endDate}"
				 immediateMode="false" altText="<ssf:nlt tag='calendar.view.popupAltText'/>"
				 />
   </div>
   <br/>
   <div id="ss_endPopup" class="ss_calPopupDiv"></div>
   <div class="ss_buttonBarLeft">
    <input type="submit" class="ss_submit" name="forumOkBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
   </div>
   <table>
    <thead>
      <tr>
        <th>Date</th>
        <th>Number of users</th>
      </tr>
    </thead>
    <tbody>
   <c:forEach var="datum" items="${ssLicenseData}" >
      <tr>
       <td>${datum.snapshotDate}</td>
       <td>${datum.internalUserCount}</td>
      </tr>
   </c:forEach>
     </tbody>
    </table>
</form>
<br>
</td></tr></table>
