<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />

<taconite-root>
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message">
		  <script type="text/javascript">
		    if (self.ss_notLoggedIn) self.ss_notLoggedIn();
		  </script>
		</div
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>

	<taconite-replace contextNodeID="entryList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div id="entryList<c:out value="${ss_filterTermNumber}"/>" 
	style="display:inline;">
	  <c:if test="${ss_filterType == 'text'}">
         <ssf:nlt tag="filter.searchText" text="Search text"/>: <input 
         type="text" class="ss_text" style="width:200px;" 
         name="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	  </c:if>
	  <c:if test="${ss_filterType == 'entry'}">
    	<select name="ss_entry_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	   id="ss_entry_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	  onChange="ss_getFilterSelectionBox(this, 'ss_entry_def_id', 'get_entry_elements')">
    	  <option value="" selected="selected"><ssf:nlt 
    	    tag="filter.selectElement" text="--select an entry type--"/></option>
    	  <option value="_common"><ssf:nlt 
    	    tag="filter.commonElements" text="--common elements (e.g., title)--"/></option>
		    <c:forEach var="item" items="${ssPublicEntryDefinitions}">
		      <c:if test="${!empty ssEntryDefinitionMap[item.key]}">
		        <option value="<c:out value="${item.value.id}"/>"><c:out 
		          value="${item.value.title}"/></option>
		      </c:if>
		    </c:forEach>
		    <c:forEach var="item" items="${ssPublicProfileEntryDefinitions}">
		      <c:if test="${!empty ssEntryDefinitionMap[item.key]}">
		        <option value="<c:out value="${item.value.id}"/>"><c:out 
		          value="${item.value.title}"/></option>
		      </c:if>
		    </c:forEach>
    	</select>
	  </c:if>
	  <c:if test="${ss_filterType == 'workflow'}">
	    [workflows and workflow states will be listed here]
	  </c:if>
	  <input type="hidden" name="filterType<c:out value="${ss_filterTermNumber}"/>"
	    value="<c:out value="${ss_filterType}"/>"/>
	</div></taconite-replace>

	<taconite-replace contextNodeID="elementList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="elementList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	 </div></taconite-replace>

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	 </div></taconite-replace>
<%
	}
%>	
</taconite-root>
