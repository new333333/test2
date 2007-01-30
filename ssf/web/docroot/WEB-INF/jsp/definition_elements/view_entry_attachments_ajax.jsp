<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="ss_divAttachmentList${ssDefinitionEntry.id}${ss_namespace}" 
	parseInBrowser="true">
	
	<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${ss_namespace}" scope="request"/>
	
	<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_attachments_list.jsp" %>

	</taconite-replace>

</c:if>
</taconite-root>