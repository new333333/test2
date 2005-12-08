<% // No more entries %>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<%
	String forum = ssFolder.getId().toString();

	String displayStyle = ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL;
	if (ssUserProperties.containsKey(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE)) {
		displayStyle = (String) ssUserProperties.get(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE);
	}
	int entryWindowWidth = (int)ParamUtil.get(request, "box_width", (double)RES_TOTAL);
	if (displayStyle.equals(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE_HORIZONTAL)) {
		entryWindowWidth = (entryWindowWidth / 3) * 2;
	}
	entryWindowWidth = entryWindowWidth - 4;
%>

<liferay:box top="/html/common/box_top.jsp" bottom="/html/common/box_bottom.jsp">
	<liferay:param name="box_width" value="<%= new Integer(entryWindowWidth).toString() %>" />

<div class="ss_portlet">
<span class="ss_content"><i>[There are no more entries.]</i></span>
<br/>
</div>

</liferay:box>

