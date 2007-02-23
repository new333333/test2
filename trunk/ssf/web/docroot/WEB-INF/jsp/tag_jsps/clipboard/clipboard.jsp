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

<div class="clipboardIcon">
	<img id="clipboardIcon_${prefix}" src="<html:imagesPath/>icons/liveclipboard-icon-16x16.jpg" onmouseover="displayClipboardMenu('${prefix}');" />
	<div id="clipboardMenu_${prefix}" class="clipboardIconMenuPane" style="visibility: hidden; display: none; position: absolute;">
		<ul id="clipboardUsersListUL_${prefix}" class="ss_finestprint">
			<li class="pasteAllUsers" onmouseover="this.style.backgroundColor='#333'; this.style.color='#FFF'; " onmouseout="this.style.backgroundColor='#FFF'; this.style.color='#333';" <c:if test="${clipboardUsersCount > 0}">onclick="loadClipboardUsers('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_clipboard_users" /></ssf:url>', '${prefix}', '${clickRoutine}', clickAllClipboardUsers);"</c:if>><c:if test="${clipboardUsersCount > 0}"><ssf:nlt tag="clipboard.addAll"/> (<strong>${clipboardUsersCount}</strong>)</c:if><c:if test="${clipboardUsersCount == 0}"><ssf:nlt tag="clipboard.noUsres"/></c:if></li>
			<c:if test="${clipboardUsersCount > 0}">
				<li class="getAllUsers" id="clipboardUsersList_${prefix}" onmouseover="this.style.backgroundColor='#333'; this.style.color='#FFF'; loadClipboardUsers('<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_clipboard_users" /></ssf:url>', '${prefix}', '${clickRoutine}');" onmouseout="this.style.backgroundColor='#FFF'; this.style.color='#333';"><img border="0" src="<html:imagesPath/>pics/sym_s_collapse.gif" /></li>
			</c:if>
		</ul>
	</div>
</div>

