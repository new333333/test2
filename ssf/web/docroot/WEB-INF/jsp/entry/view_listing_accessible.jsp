<% // The main forum view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<c:if test="${!empty ssReloadUrl}">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>

</c:if>
<c:if test="${empty ssReloadUrl}">
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<%@ include file="/WEB-INF/jsp/entry/view_listing_common.jsp" %>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowHeight" type="java.lang.Integer" scope="request" />

  <c:if test="<%= !reloadCaller %>">
    <c:if test="<%= !isViewEntry %>">

<div id="ss_showentryhighwatermark" style="position:absolute; visibility:visible;">
<img src="<html:imagesPath/>pics/1pix.gif">
</div>
<%@ include file="/WEB-INF/jsp/entry/view_accessible.jsp" %>
     </c:if>
  </c:if>
  <c:if test="<%= reloadCaller %>">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>
  </c:if>
</c:if>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

