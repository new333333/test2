<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<jsp:useBean id="ss_filter_entry_def_id" type="java.lang.String" scope="request" />
<jsp:useBean id="ssEntryDefinitionElementData" type="java.util.Map" scope="request" />

<taconite-root>
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_filter_status_message" parseInBrowser="true">
		<div id="ss_filter_status_message">
		  <script type="text/javascript" language="javascript">
		    if (self.ss_notLoggedIn) self.ss_notLoggedIn();
		  </script>
		</div
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_filter_status_message" parseInBrowser="true">
		<div id="ss_filter_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	  <c:forEach var="element" items="${ssEntryDefinitionElementData}">
		<c:if test="${element.key == ss_filter_entry_element_name}">
	       <c:if test="${element.value.type == 'title' || element.value.type == 'text'}">
	         <ssf:nlt tag="filter.searchText" text="Search text"/>: <input 
	         type="text" style="width:150px;" 
	         name="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	       </c:if>
	       <c:if test="${element.value.type == 'event' || element.value.type == 'date'}">
	         <ssf:nlt tag="filter.date" text="Date"/>: ...
	       </c:if>
	       <c:if test="${element.value.type == 'selectbox' || element.value.type == 'radio'}">
			 <select 
		       name="elementValue<c:out value="${ss_filterTermNumber}"/>" multiple="multiple" size="<c:out value="${element.value.length}"/>">
				     <c:forEach var="option" items="${element.value.values}">
				       <option value="<c:out value="${option.key}"/>"><c:out value="${option.value}"/></option>
				     </c:forEach>
		       </select>
	       </c:if>
	       <c:if test="${element.value.type == 'checkbox'}">
	         <input type="checkbox" 
	         name="elementValue<c:out value="${ss_filterTermNumber}"/>" 
	         checked="checked"/> <c:out value="${element.value.caption}"/>
	       </c:if>
	       <c:if test="${element.value.type == 'user_list'}">
	         <ssf:nlt tag="filter.users" text="Users"/>: ...
	       </c:if>
		</c:if>
	  </c:forEach>
	 </div></taconite-replace>
       
<%
	}
%>	
</taconite-root>
