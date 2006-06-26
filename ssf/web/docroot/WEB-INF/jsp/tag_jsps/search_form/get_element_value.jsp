<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<jsp:useBean id="ssEntryDefinitionElementData" type="java.util.Map" scope="request" />

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

	<taconite-replace contextNodeID="valueList<c:out value="${ss_searchFormTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_searchFormTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	  <c:forEach var="element" items="${ssEntryDefinitionElementData}">
		<c:if test="${element.key == ss_searchForm_entry_element_name}">
	       
	       <c:if test="${element.value.type == 'title' || element.value.type == 'text'}">
	         <ssf:nlt tag="searchForm.searchText" text="Search text"/>: <input 
	         type="text" class="ss_text" style="width:150px;" 
	         name="elementValue<c:out value="${ss_searchFormTermNumber}"/>" />
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_searchFormTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'event'}">
	         <ssf:nlt tag="searchForm.date" text="Date"/>: ...
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_searchFormTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'date'}">
	         <select name="elementValueDateType<c:out value="${ss_searchFormTermNumber}"/>" 
	         onChange="ss_getSearchFormSelectionBox(this, 'elementValueDateType', 'get_searchForm_element_value_data', 'date')" >
	           <option value=""><ssf:nlt tag="searchForm.selectDateOption"/></option>
	           <option value="onOrBefore"><ssf:nlt tag="searchForm.onOrBefore"/></option>
	           <option value="onOrAfter"><ssf:nlt tag="searchForm.onOrAfter"/></option>
	           <option value="withinNextFewDays"><ssf:nlt tag="searchForm.withinNextFewDays"/></option>
	           <option value="withinPastFewDays"><ssf:nlt tag="searchForm.withinPastFewDays"/></option>
	         </select>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_searchFormTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'selectbox' || element.value.type == 'radio'}">
			 <select
		       name="elementValue<c:out value="${ss_searchFormTermNumber}"/>" 
		       multiple="multiple" 
		       size="<c:out value="${element.value.length}"/>"
		     >
				<c:forEach var="option" items="${element.value.values}">
				  <option value="<c:out value="${option.key}"/>"><c:out value="${option.value}"/></option>
				</c:forEach>
		       </select>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_searchFormTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'checkbox'}">
	         <input type="checkbox" 
	         name="elementValue<c:out value="${ss_searchFormTermNumber}"/>" 
	         checked="checked"/> <c:out value="${element.value.caption}"/>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_searchFormTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'user_list'}">
	         <ssf:nlt tag="searchForm.users" text="Users"/>: ...
	       </c:if>
		</c:if>
	  </c:forEach>
	 </div></taconite-replace>
       
	<taconite-replace contextNodeID="valueData<c:out value="${ss_searchFormTermNumber}"/>" 
	parseInBrowser="true"><div 
	  id="valueData<c:out value="${ss_searchFormTermNumber}"/>" 
	  style="visibility:visible; display:inline;">
	</div></taconite-replace>

<%
	}
%>	
</taconite-root>
