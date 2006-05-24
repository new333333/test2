<% // The main forum view - for viewing folder listings and for viewing entries %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<c:if test="${empty ssReloadUrl}">
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<%@ include file="/WEB-INF/jsp/entry/view_listing_common.jsp" %>
<jsp:useBean id="ss_entryWindowWidth" type="java.lang.Integer" scope="request" />
<jsp:useBean id="ss_entryWindowHeight" type="java.lang.Integer" scope="request" />
<%
String op = (String) renderRequest.getAttribute(WebKeys.ACTION);

String ssReloadUrl = (String) renderRequest.getAttribute("ssReloadUrl");
if (ssReloadUrl == null) ssReloadUrl = "";
boolean reloadCaller = false;
if (!ssReloadUrl.equals("")) reloadCaller = true;

boolean isViewEntry = false;
if (!op.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING) && !op.equals(WebKeys.ACTION_VIEW_PROFILE_LISTING)) {
	isViewEntry = true;
}
	
%>
  <c:if test="<%= !reloadCaller %>">
    <c:if test="<%= !isViewEntry %>">

<div id="ss_showentryhighwatermark" style="position:absolute; visibility:visible;">
<img src="<html:imagesPath/>pics/1pix.gif">
</div>
<%@ include file="/WEB-INF/jsp/entry/view_popup.jsp" %>
     </c:if>
  </c:if>
  <c:if test="<%= reloadCaller %>">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<%= ssReloadUrl %>')
</script>
  </c:if>
</c:if>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

