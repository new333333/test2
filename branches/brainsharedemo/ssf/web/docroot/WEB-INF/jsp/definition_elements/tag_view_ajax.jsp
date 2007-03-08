<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_tagViewNamespace" value="${ss_namespace}" scope="request"/>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
  <taconite-replace contextNodeID="<c:out value="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}"/>" 
    parseInBrowser="true">
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data_cloud.jsp" />
  </taconite-replace>
  <taconite-replace contextNodeID="<c:out value="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_data_pane_p"/>" 
    parseInBrowser="true">
	<c:set var="ssTags" value="${ssPersonalTags}" scope="request" />
	<c:set var="ssTagsType" value="p" scope="request" />
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
  </taconite-replace>
  <taconite-replace contextNodeID="<c:out value="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_data_pane_c"/>" 
    parseInBrowser="true">
	<c:set var="ssTags" value="${ssCommunityTags}" scope="request" />
	<c:set var="ssTagsType" value="c" scope="request" />
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
  </taconite-replace>
</c:if>
</taconite-root>
