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
    	<select name="ss_entry_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	   id="ss_entry_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	  onChange="ss_getFilterSelectionBox(this, 'ss_entry_def_id', 'get_entry_elements')">
    	  <option value="" selected="selected"><ssf:nlt tag="filter.selectElement"/></option>
    	  <option value="_common"><ssf:nlt tag="filter.commonElements"/></option>
		    <c:forEach var="item" items="${ssEntryDefinitionMap}">
		        <option value="<c:out value="${item.value.id}"/>"><c:out 
		          value="${item.value.title}"/></option>
		    </c:forEach>
		    <c:forEach var="item" items="${ssPublicProfileEntryDefinitions}">
		      <c:if test="${!empty ssEntryDefinitionMap[item.key]}">
		        <option value="<c:out value="${item.value.id}"/>"><c:out 
		          value="${item.value.title}"/></option>
		      </c:if>
		    </c:forEach>
    	</select>
	  </c:if>


<c:if test="${ss_filterType != 'entry'}">
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

	<taconite-replace contextNodeID="entryList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div id="entryList<c:out value="${ss_filterTermNumber}"/>" 
	style="display:inline;">
	  <c:if test="${ss_filterType == 'text'}">
         <ssf:nlt tag="filter.searchText" text="Search text"/>: <input 
         type="text" class="ss_text" style="width:200px;" 
         name="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	  </c:if>
	  <c:if test="${ss_filterType == 'workflow'}">
    	<select name="ss_workflow_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	   id="ss_workflow_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	  onChange="ss_getFilterSelectionBox(this, 'ss_workflow_def_id', 'get_workflow_states')">
    	  <option value="" selected="selected"><ssf:nlt tag="filter.selectWorkflow"/></option>
		  <c:forEach var="item" items="${ssWorkflowDefinitionMap}">
		    <option value="<c:out value="${item.value.id}"/>"><c:out 
		      value="${item.value.title}"/></option>
		  </c:forEach>
    	</select>
	  </c:if>
	 <c:if test="${ss_filterType == 'folders'}">
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
</taconite-root>
</c:if>
</c:if>
