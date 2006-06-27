<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.Date" %>

<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />

<taconite-root>
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_search_form_status_message" parseInBrowser="true">
		<div id="ss_search_form_status_message">
		  <script type="text/javascript">
		    if (self.ss_notLoggedIn) self.ss_notLoggedIn();
		  </script>
		</div
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_search_form_status_message" parseInBrowser="true">
		<div id="ss_search_form_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>

	<taconite-replace contextNodeID="valueData<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	  id="valueData<c:out value="${ss_filterTermNumber}"/>" 
	  style="visibility:visible; display:inline;">
	  
	  <c:if test="${ss_filterValueType == 'onOrBefore'}">
	      <ssf:datepicker id="elementValue${ss_filterTermNumber}" 
            formName="searchFormData" showSelectors="false" 
            initDate="<%= new Date() %>" />
	  </c:if>
	  
	  <c:if test="${ss_filterValueType == 'onOrAfter'}">
	  </c:if>
	  
	  <c:if test="${ss_filterValueType == 'withinNextFewDays'}">
	    <ssf:nlt tag="searchForm.days" text="Days" />: <input type="text" 
	    name="elementValue${ss_filterTermNumber}" size="4" />
	  </c:if>
	  
	  <c:if test="${ss_filterValueType == 'withinPastFewDays'}">
	    <ssf:nlt tag="searchForm.days" text="Days" />: <input type="text" 
	    name="elementValue${ss_filterTermNumber}" size="4" />
	  </c:if>
	  
	</div></taconite-replace>
       
<%
	}
%>	
</taconite-root>
