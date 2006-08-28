<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	  <c:forEach var="element" items="${ssEntryDefinitionElementData}">
		<c:if test="${element.key == ss_filter_entry_element_name}">
	       
	       <c:if test="${element.value.type == 'title' || element.value.type == 'text'}">
	         <ssf:nlt tag="filter.searchText" text="Search text"/>: <input 
	         type="text" class="ss_text" style="width:150px;" 
	         name="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'event'}">
	         <ssf:nlt tag="filter.date" text="Date"/>: ...
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'date'}">
	         <select name="elementValueDateType<c:out value="${ss_filterTermNumber}"/>" 
	         onChange="ss_getFilterSelectionBox(this, 'elementValueDateType', 'get_element_value_data', 'date')" >
	           <option value=""><ssf:nlt tag="filter.selectDateOption"/></option>
	           <option value="onOrBefore"><ssf:nlt tag="filter.onOrBefore"/></option>
	           <option value="onOrAfter"><ssf:nlt tag="filter.onOrAfter"/></option>
	           <option value="withinNextFewDays"><ssf:nlt tag="filter.withinNextFewDays"/></option>
	           <option value="withinPastFewDays"><ssf:nlt tag="filter.withinPastFewDays"/></option>
	         </select>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'selectbox' || element.value.type == 'radio'}">
			 <select
		       name="elementValue<c:out value="${ss_filterTermNumber}"/>" 
		       multiple="multiple" 
		       size="<c:out value="${element.value.length}"/>"
		     >
				<c:forEach var="option" items="${element.value.values}">
				  <option value="<c:out value="${option.key}"/>"><c:out value="${option.value}"/></option>
				</c:forEach>
		       </select>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'checkbox'}">
	         <input type="checkbox" 
	         name="elementValue<c:out value="${ss_filterTermNumber}"/>" 
	         checked="checked"/> <c:out value="${element.value.caption}"/>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'user_list'}">
	         <ssf:nlt tag="filter.users" text="Users"/>: ...
	       </c:if>
		</c:if>
	  </c:forEach>
	 </div></taconite-replace>
       
	<taconite-replace contextNodeID="valueData<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	  id="valueData<c:out value="${ss_filterTermNumber}"/>" 
	  style="visibility:visible; display:inline;">
	</div></taconite-replace>

</c:if>
</taconite-root>
