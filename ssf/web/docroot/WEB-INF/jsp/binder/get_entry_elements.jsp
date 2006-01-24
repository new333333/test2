<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<jsp:useBean id="ss_filter_entry_def_id" type="java.lang.String" scope="request" />
<jsp:useBean id="ssEntryDefinitionElementData" type="java.util.Map" scope="request" />

<taconite-root xml:space="preserve">
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

	<taconite-replace contextNodeID="elementList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	   id="elementList<c:out value="${ss_filterTermNumber}"/>" 
	   style="visibility:visible;"><select 
	   name="elementName<c:out value="${ss_filterTermNumber}"/>" onChange="ss_getElementValue(this)">
	     <c:forEach var="element" items="${ssEntryDefinitionElementData}">
	       <c:if test="${element.value.type == 'title' || element.value.type == 'event' || 
	                     element.value.type == 'text'  || element.value.type == 'selectbox' || 
	                     element.value.type == 'radio' || element.value.type == 'checkbox' || 
	       				 element.value.type == 'date'  || element.value.type == 'user_list'}">
	         <option value="<c:out value="${element.key}"/>"><c:out value="${element.value.caption}"/></option>
	       </c:if>
	     </c:forEach>
	   </select></div></taconite-replace>

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" style="visibility:visible;">
	 </div></taconite-replace>
<%
	}
%>	
</taconite-root>
