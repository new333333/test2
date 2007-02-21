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
<script type="text/javascript">
	
	function displayClipboardMenu_${prefix}() {
		var divObj = $("clipboardMenu_${prefix}");
		ss_moveDivToBody("clipboardMenu_${prefix}");
		ss_setObjectTop(divObj, parseInt(ss_getDivTop("clipboardIcon_${prefix}")) + + ss_getDivWidth("clipboardIcon_${prefix}"))
		ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("clipboardIcon_${prefix}")))
		ss_showDivActivate("clipboardMenu_${prefix}");
	}
	
	var clipboardUsersLoaded_${prefix} = false;
	
	function clickAllClipboardUsers_${prefix}() {
		var ulObj = $('clipboardUsersListUL_${prefix}');
		var lisObj = ulObj.getElementsByTagName("li");
		for (var i = 0; i < lisObj.length; i++) {
			if (lisObj[i].getElementsByTagName("a") && 
				lisObj[i].getElementsByTagName("a").length > 0 &&
				lisObj[i].getElementsByTagName("a").item(0).onclick) {
				lisObj[i].getElementsByTagName("a").item(0).onclick();
			}
		}
	}
    
    function loadClipboardUsers_${prefix}(afterPostRoutine) {
    	if (clipboardUsersLoaded_${prefix}) {
    		afterPostRoutine();
			return;
		}
		clipboardUsersLoaded_${prefix} = true;
		var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_clipboard_users" /></ssf:url>";
		url += "&ss_divId=clipboardUsersList_${prefix}";
		url += "&clickRoutine=${clickRoutine}";
		
		var ajaxRequest = new ss_AjaxRequest(url);	
		if (afterPostRoutine)
			ajaxRequest.setPostRequest(afterPostRoutine);
		ajaxRequest.setUseGET();
		ajaxRequest.sendRequest();
    }
	
</script>

<div class="clipboardIcon">
	<img id="clipboardIcon_${prefix}" src="<html:imagesPath/>icons/liveclipboard-icon-16x16.jpg" onmouseover="displayClipboardMenu_${prefix}();" />
	<div id="clipboardMenu_${prefix}" class="clipboardIconMenuPane" style="visibility: hidden; display: none; position: absolute;">
		<ul id="clipboardUsersListUL_${prefix}" class="ss_finestprint">
			<li class="pasteAllUsers" onmouseover="this.style.backgroundColor='#333'; this.style.color='#FFF'; " onmouseout="this.style.backgroundColor='#FFF'; this.style.color='#333';" <c:if test="${clipboardUsersCount > 0}">onclick="loadClipboardUsers_${prefix}(clickAllClipboardUsers_${prefix});"</c:if>><c:if test="${clipboardUsersCount > 0}">Add all (<strong>${clipboardUsersCount}</strong>)</c:if><c:if test="${clipboardUsersCount == 0}">There are no users on clipboard</c:if></li>
			<c:if test="${clipboardUsersCount > 0}">
				<li class="getAllUsers" id="clipboardUsersList_${prefix}" onmouseover="this.style.backgroundColor='#333'; this.style.color='#FFF'; loadClipboardUsers_${prefix}();" onmouseout="this.style.backgroundColor='#FFF'; this.style.color='#333';"><img border="0" src="<html:imagesPath/>pics/sym_s_collapse.gif" /></li>
			</c:if>
		</ul>
	</div>
</div>

