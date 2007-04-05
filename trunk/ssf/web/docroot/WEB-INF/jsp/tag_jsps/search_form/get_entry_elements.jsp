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

<%@ page contentType="text/xml; charset=UTF-8" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

 <c:if test="${ss_filterType == 'entry'}">
   <div id="elementList<c:out value="${ss_filterTermNumber}"/>" 
   style="visibility:visible; display:inline;"><select
   name="elementName<c:out value="${ss_filterTermNumber}"/>" 
   onChange="ss_getFilterSelectionBox(this, 'elementName', 'get_searchForm_element_values', '${ss_filterType}')">
     <option value="" selected="selected"><ssf:nlt 
       tag="searchForm.selectElement" text="--select an element--"/></option>
     <option value="_all_entries" ><ssf:nlt 
       tag="filter.selectEntryAny"/></option>
     <c:forEach var="element" items="${ssEntryDefinitionElementData}">
       <c:if test="${element.value.type == 'title' || element.value.type == 'event' || 
                     element.value.type == 'text'  || element.value.type == 'selectbox' || 
                     element.value.type == 'radio' || element.value.type == 'checkbox' || 
       				 element.value.type == 'date'  || element.value.type == 'user_list'}">
         <option value="<c:out value="${element.key}"/>"><c:out value="${element.value.caption}"/></option>
       </c:if>
     </c:forEach>
   </select></div>
 </c:if>
 <c:if test="${ss_filterType != 'entry'}">
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

	<taconite-replace contextNodeID="elementList<c:out value="${ss_filterTermNumber}"/>" 
	  parseInBrowser="true">
		 ${ss_filterType}
	   
		 <c:if test="${ss_filterType == 'workflow'}">
		   <c:set var="workflowSelectBoxSize" value="1"/>
		   <c:forEach var="state" items="${ssWorkflowDefinitionStateData}">
		     <c:set var="workflowSelectBoxSize" value="${workflowSelectBoxSize + 1}"/>
		   </c:forEach>
		   <div id="elementList<c:out value="${ss_filterTermNumber}"/>" 
		   style="visibility:visible; display:inline;"><select
		   name="ss_stateNameData<c:out value="${ss_filterTermNumber}"/>" multiple="multiple" 
		   size="${workflowSelectBoxSize}">
		     <option value="" selected="selected"><ssf:nlt 
		       tag="filter.selectState"/></option>
		     <c:forEach var="state" items="${ssWorkflowDefinitionStateData}">
		       <option value="<c:out value="${state.key}"/>"><c:out value="${state.value.caption}"/></option>
		     </c:forEach>
		   </select></div>
		 </c:if>
		 
	   </taconite-replace>

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;"></div></taconite-replace>

	<taconite-replace contextNodeID="valueData<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	  id="valueData<c:out value="${ss_filterTermNumber}"/>" 
	  style="visibility:visible; display:inline;"></div></taconite-replace>

</taconite-root>
</c:if>
</c:if>
