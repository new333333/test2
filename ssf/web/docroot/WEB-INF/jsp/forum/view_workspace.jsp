<% // The main workspace view  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />

<%
String ssReloadUrl = (String) renderRequest.getAttribute("ssReloadUrl");
if (ssReloadUrl == null) ssReloadUrl = "";
boolean reloadCaller = false;
if (!ssReloadUrl.equals("")) reloadCaller = true;
%>

<c:if test="<%= reloadCaller %>">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<%= ssReloadUrl %>')
</script>
</c:if>

<c:if test="<%= !reloadCaller %>">
<script type="text/javascript">
var ss_reloadUrl = "${ss_reloadUrl}";
</script>

<div class="ss_style ss_portlet">
<% // Show the workspace according to its definition %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  processThisItem="true"
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  binder="${ssBinder}" />
</div>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

