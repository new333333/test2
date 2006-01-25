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

	<taconite-replace contextNodeID="typeList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div id="typeList<c:out value="${ss_filterTermNumber}"/>" 
      <a href="javascript: ;" 
        onClick="ss_getFilterSelectionBox(this.parentNode, 'typeList', 'get_filter_type', 'text');return false;">
          <ssf:nlt tag="filter.searchText" text="Search text"/>
      </a>
      <br/>
      <a href="javascript: ;" 
        onClick="ss_getFilterSelectionBox(this.parentNode, 'typeList', 'get_filter_type', 'entry');return false;">
          <ssf:nlt tag="filter.entryAttributes" text="Entry attributes"/>
      </a>
      <br/>
      <a href="javascript: ;" 
        onClick="ss_getFilterSelectionBox(this.parentNode, 'typeList', 'get_filter_type', 'workflow');return false;">
          <ssf:nlt tag="filter.workflowStates" text="Workflow states"/>
      </a>
      <br/>
	</div></taconite-replace>

	<taconite-replace contextNodeID="elementList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="elementList<c:out value="${ss_filterTermNumber}"/>" style="visibility:visible;">
	 </div></taconite-replace>

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" style="visibility:visible;">
	 </div></taconite-replace>
<%
	}
%>	
</taconite-root>
