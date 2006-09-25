<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_loadCssStylesInline" value="true" scope="request"/>
<c:set var="ss_skipCssStyles" value="true" scope="request"/>
<jsp:include page="/WEB-INF/jsp/common/ssf_css.jsp" />

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="ss_tabbar" 
	parseInBrowser="true">
<%@ include file="/WEB-INF/jsp/definition_elements/tabbar.jsp" %>
	</taconite-replace>

<c:if test="${ss_tab_id > 0}">
	<taconite-replace contextNodeID="ss_tabDataDiv${ss_tab_id}" 
	  parseInBrowser="true"><div id="ss_tabDataDiv${ss_tab_id}" >
	  xxxxxxxxxxxxxxxxx
	  </div></taconite-replace>
</c:if>
</c:if>
</taconite-root>
