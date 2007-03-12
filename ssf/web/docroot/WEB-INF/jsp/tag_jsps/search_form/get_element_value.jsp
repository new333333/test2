<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
	  <c:forEach var="element" items="${ssEntryDefinitionElementData}">
		<c:if test="${element.key == ss_filter_entry_element_name}">
	       
	       <c:if test="${element.value.type == 'title' || element.value.type == 'text'}">
	         <ssf:nlt tag="searchForm.searchText" text="Search text"/>: <input 
	         type="text" class="ss_text" style="width:150px;" 
	         name="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'event'}">
	         <ssf:nlt tag="searchForm.date" text="Date"/>: ...
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'date'}">
	         <select name="elementValueDateType<c:out value="${ss_filterTermNumber}"/>" 
	         onChange="ss_getFilterSelectionBox(this, 'elementValueDateType', 'get_searchForm_element_value_data', 'date')" >
	           <option value=""><ssf:nlt tag="searchForm.selectDateOption"/></option>
	           <option value="onOrBefore"><ssf:nlt tag="searchForm.onOrBefore"/></option>
	           <option value="onOrAfter"><ssf:nlt tag="searchForm.onOrAfter"/></option>
	           <option value="withinNextFewDays"><ssf:nlt tag="searchForm.withinNextFewDays"/></option>
	           <option value="withinPastFewDays"><ssf:nlt tag="searchForm.withinPastFewDays"/></option>
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
	         <ssf:nlt tag="searchForm.users" text="Users"/>: 
	         
	          <ssf:find formName="" 
    formElement="elementValue${ss_filterTermNumber}" 
    type="user"
    width="170px" 
    clickRoutine="ss_rewriteValueIntoFormElement"
    clickRoutineArgs="${ss_filterTermNumber}"
    leaveResultsVisible="false"
    singleItem="true"/> 
	         
	         <input type="hidden" name="_elementValue<c:out value="${ss_filterTermNumber}"/>" id="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	         <input type="hidden" name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
		</c:if>
	  </c:forEach>
</c:if>

