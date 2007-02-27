<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
	String clipboardUsersCount = ((Integer) request.getAttribute("clipboard_user_count")).toString();
	String clickRoutine = (String) request.getAttribute("clickRoutine");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="clipboardUsersCount" value="<%= clipboardUsersCount %>"/>
<c:set var="clickRoutine" value="<%= clickRoutine %>"/>
<c:set var="prefix" value="${iCount}" />

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/clipboard/clipboard.js"></script>

<div class="ss_clipboardMenu">
	<img id="ss_clipboardIcon_${prefix}" src="<html:imagesPath/>icons/liveclipboard-icon-16x16.jpg" onmouseover="if (window.ss_displayClipboardMenu) ss_displayClipboardMenu('${prefix}');" />
	<div id="ss_clipboardOptions_${prefix}" class="ss_clipboardOptionsPane ss_style" style="visibility: hidden; display: none; position: absolute;">
		<ul id="ss_clipboardOptionsListUL_${prefix}">
			<li class="ss_pasteAllUsers" onmouseover="this.style.backgroundColor='#333'; this.style.color='#FFF'; this.style.cursor='pointer';" onmouseout="this.style.backgroundColor='#FFF'; this.style.color='#333'; this.style.cursor='default';" <c:if test="${clipboardUsersCount > 0}">onclick="ss_loadClipboardUsers('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_clipboard_users" /></ssf:url>', '${prefix}', '${clickRoutine}', ss_clickAllClipboardUsers);"</c:if>><c:if test="${clipboardUsersCount > 0}"><ssf:nlt tag="clipboard.addAll"/> (<strong>${clipboardUsersCount}</strong>)</c:if><c:if test="${clipboardUsersCount == 0}"><ssf:nlt tag="clipboard.noUsres"/></c:if></li>
			<c:if test="${clipboardUsersCount > 0}">
				<li class="ss_getAllUsers" id="ss_clipboardUsersList_${prefix}" onmouseover="this.style.backgroundColor='#333'; this.style.color='#FFF'; ss_loadClipboardUsers('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_clipboard_users" /></ssf:url>', '${prefix}', '${clickRoutine}');" onmouseout="this.style.backgroundColor='#FFF'; this.style.color='#333';"><img border="0" src="<html:imagesPath/>pics/sym_s_collapse.gif" /></li>
			</c:if>
		</ul>
	</div>
</div>

