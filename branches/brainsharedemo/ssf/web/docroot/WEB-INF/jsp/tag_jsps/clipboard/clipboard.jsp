<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
	String clipboardUsersCount = ((Integer) request.getAttribute("clipboard_user_count")).toString();
	String formElement = (String) request.getAttribute("formElement");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="clipboardUsersCount" value="<%= clipboardUsersCount %>"/>
<c:set var="formElement" value="<%= formElement %>"/>
<c:set var="prefix" value="${iCount}" />

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/clipboard/clipboard.js"></script>

<div class="ss_ClipboardUsersPane">
	<span onclick="if (window.ss_loadClipboardUsersList) ss_loadClipboardUsersList('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_clipboard_users" /></ssf:url>', ${prefix});"
		onmouseover="this.style.cursor='pointer'; " onmouseout="this.style.cursor='default'; ">
		<img id="ss_clipboardUsersIcon_${prefix}" src="<html:imagesPath/>pics/sym_s_expand.gif" />
		<span class="ss_bold"><ssf:nlt tag="clipboard.users.title" /></span>
	</span>

	<div id="ss_clipboardUsersList_${prefix}" class="ss_clipboardUsersList ss_style" style="display: block;"></div>

	<img src="<html:imagesPath/>pics/1pix.gif" onload="ss_setClipboardUsersVariables('${prefix}', '${formElement}');" />

</div>
