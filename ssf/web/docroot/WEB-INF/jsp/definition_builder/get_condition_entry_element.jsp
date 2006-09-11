<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="conditionEntryElements" 
	parseInBrowser="true"><div 
	   id="conditionEntryElements" >
	   <span class="ss_bold"><ssf:nlt tag="definition.selectEntryElement"/></span><br/>
	   <select
	   name="conditionElementName" 
	   onChange="getConditionSelectbox(this, 'get_condition_entry_element_operations')">
	     <option value="" selected="selected"><ssf:nlt 
	       tag="filter.selectElement" text="--select an element--"/></option>
	     <c:forEach var="element" items="${ssEntryDefinitionElementData}">
	       <c:if test="${element.value.type == 'event' || 
	                     element.value.type == 'selectbox' || 
	                     element.value.type == 'radio' || 
	                     element.value.type == 'checkbox' || 
	       				 element.value.type == 'date'  || 
	       				 element.value.type == 'user_list'}">
	         <option value="<c:out value="${element.key}"/>"><c:out value="${element.value.caption}"/></option>
	       </c:if>
	     </c:forEach>
	   </select></div></taconite-replace>

	<taconite-replace contextNodeID="conditionOperations" 
	parseInBrowser="true"><div 
	  id="conditionOperations" 
	  style="visibility:visible; display:inline;"></div></taconite-replace>

	<taconite-replace contextNodeID="conditionOperand" 
	parseInBrowser="true"><div 
	  id="conditionOperand" 
	  style="visibility:visible; display:inline;"></div></taconite-replace>

</c:if>
</taconite-root>
