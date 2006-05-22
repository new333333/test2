<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<jsp:useBean id="ssEntryDefinitionElementData" type="java.util.Map" scope="request" />

<taconite-root>
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_condition_status_message" parseInBrowser="true">
		<div id="ss_condition_status_message">
		  <script type="text/javascript">
		    if (self.ss_notLoggedIn) self.ss_notLoggedIn();
		  </script>
		</div
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_condition_status_message" parseInBrowser="true">
		<div id="ss_condition_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>

	<taconite-replace contextNodeID="conditionEntryElements" 
	parseInBrowser="true"><div 
	   id="conditionEntryElements" ><select
	   name="conditionDefinitionId" 
	   onChange="ss_getFilterSelectionBox(this, 'elementName', 'get_element_values')">
	     <option value="" selected="selected"><ssf:nlt 
	       tag="filter.selectElement" text="--select an element--"/></option>
	     <c:forEach var="element" items="${ssEntryDefinitionElementData}">
	       <c:if test="${element.value.type == 'title' || element.value.type == 'event' || 
	                     element.value.type == 'text'  || element.value.type == 'selectbox' || 
	                     element.value.type == 'radio' || element.value.type == 'checkbox' || 
	       				 element.value.type == 'date'  || element.value.type == 'user_list'}">
	         <option value="<c:out value="${element.key}"/>"><c:out value="${element.value.caption}"/></option>
	       </c:if>
	     </c:forEach>
	   </select></div></taconite-replace>

	<taconite-replace contextNodeID="conditionValueList" 
	parseInBrowser="true"><div 
	id="conditionValueList" 
	style="visibility:visible; display:inline;"></div></taconite-replace>

	<taconite-replace contextNodeID="conditionValueData" 
	parseInBrowser="true"><div 
	  id="conditionValueData" 
	  style="visibility:visible; display:inline;"></div></taconite-replace>

<%
	}
%>	
</taconite-root>
