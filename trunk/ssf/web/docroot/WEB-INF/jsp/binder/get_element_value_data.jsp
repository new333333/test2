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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.Date" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="valueData<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	  id="valueData<c:out value="${ss_filterTermNumber}"/>" 
	  style="visibility:visible; display:inline;">
	  
	  <c:if test="${ss_filterValueType == 'onOrBefore'}">
	      <ssf:datepicker id="elementValue${ss_filterTermNumber}" 
            formName="filterData" showSelectors="false" 
            initDate="<%= new Date() %>" />
	  </c:if>
	  
	  <c:if test="${ss_filterValueType == 'onOrAfter'}">
	  </c:if>
	  
	  <c:if test="${ss_filterValueType == 'withinNextFewDays'}">
	    <ssf:nlt tag="filter.days" text="Days" />: <input type="text" 
	    name="elementValue${ss_filterTermNumber}" size="4" />
	  </c:if>
	  
	  <c:if test="${ss_filterValueType == 'withinPastFewDays'}">
	    <ssf:nlt tag="filter.days" text="Days" />: <input type="text" 
	    name="elementValue${ss_filterTermNumber}" size="4" />
	  </c:if>
	  
	</div></taconite-replace>
       
<c:/if>
</taconite-root>
