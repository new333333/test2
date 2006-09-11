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
