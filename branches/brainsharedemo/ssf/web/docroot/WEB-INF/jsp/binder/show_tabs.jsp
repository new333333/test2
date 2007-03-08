<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_loadCssStylesInline" value="true" scope="request"/>
<c:set var="ss_skipCssStyles" value="true" scope="request"/>
<jsp:include page="/WEB-INF/jsp/common/ssf_css.jsp" />

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="ss_tabs_variables" 
	parseInBrowser="true"><div id="ss_tabs_variables" style="display:none;">
<script type="text/javascript">
var ss_currentTab = "${ss_tabs.current_tab}";
var ss_nextTabNumber = "${ss_tabs.next_tab_id}";
</script>
</div>
	</taconite-replace>

</c:if>
</taconite-root>
