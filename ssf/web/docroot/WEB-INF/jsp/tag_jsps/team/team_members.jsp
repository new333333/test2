<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
	String binderId = (String) request.getAttribute("binderId");
	String clickRoutine = (String) request.getAttribute("clickRoutine");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="binderId" value="<%= binderId %>"/>
<c:set var="clickRoutine" value="<%= clickRoutine %>"/>
<c:set var="prefix" value="${iCount}" />

<script type="text/javascript">
	
	var teamMembersCountLoaded_${prefix} = false;
	var teamMembersLoaded_${prefix} = false;
	
	function displayTeamMembersMenu_${prefix}() {
		if (teamMembersCountLoaded_${prefix}) {
			displayTeamMembersMenu_postRequest_${prefix}();
			return;
		}		
		var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_team_members_count" /><ssf:param name="binderId" value="${binderId}" /></ssf:url>";
		url += "&ss_divId=teamMembersAmount_${prefix}";
		
		var ajaxRequest = new ss_AjaxRequest(url);	
		ajaxRequest.setPostRequest(displayTeamMembersMenu_postRequest_${prefix});
		ajaxRequest.setUseGET();
		ajaxRequest.sendRequest();
	}
	
	function displayTeamMembersMenu_postRequest_${prefix}() {
		var divObj = $("teamMenu_${prefix}");
		ss_moveDivToBody("teamMenu_${prefix}");
		ss_setObjectTop(divObj, parseInt(ss_getDivTop("teamIcon_${prefix}")) + + ss_getDivWidth("teamIcon_${prefix}"))
		ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("teamIcon_${prefix}")))
		ss_showDivActivate("teamMenu_${prefix}");	
		
		if (teamMembersLoaded_${prefix} || teamMembersCountLoaded_${prefix}) 
			return;
		
		var ulObj = $("teamMembersListUL_${prefix}");
		if (ulObj) {
			var getAllLIObj = document.createElement("li");
			getAllLIObj.setAttribute("class", "getAllUsers");
			getAllLIObj.setAttribute("id", "teamMembersList_${prefix}");
			getAllLIObj.setAttribute("onmouseover", "this.style.backgroundColor='#333'; this.style.color='#FFF'; loadTeamMembers_${prefix}();");
			getAllLIObj.setAttribute("onmouseout", "this.style.backgroundColor='#FFF'; this.style.color='#333';");
			
			var getAllIMGObj = document.createElement("img");
			getAllIMGObj.setAttribute("class", "getAllUsers");
			getAllIMGObj.setAttribute("border", "0");
			getAllIMGObj.setAttribute("src", "<html:imagesPath/>pics/sym_s_collapse.gif");

			getAllLIObj.appendChild(getAllIMGObj);
			ulObj.appendChild(getAllLIObj);
		}
	}
	
	function addAllUsersFromTeam_${prefix}() {
		var ulObj = $('teamMembersListUL_${prefix}');
		var lisObj = ulObj.getElementsByTagName("li");
		for (var i = 0; i < lisObj.length; i++) {
			if (lisObj[i].getElementsByTagName("a") && 
				lisObj[i].getElementsByTagName("a").length > 0 &&
				lisObj[i].getElementsByTagName("a").item(0).onclick) {
				lisObj[i].getElementsByTagName("a").item(0).onclick();
			}
		}
	}
	
    function loadTeamMembers_${prefix}(afterPostRoutine) {
    	if (teamMembersLoaded_${prefix}) {
    		if (afterPostRoutine)
    			afterPostRoutine();
			return;
		}
		teamMembersLoaded_${prefix} = true;
		var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_team_members" /><ssf:param name="binderId" value="${binderId}" /></ssf:url>";
		url += "&ss_divId=teamMembersList_${prefix}";
		url += "&clickRoutine=${clickRoutine}";
		
		var ajaxRequest = new ss_AjaxRequest(url);	
		if (afterPostRoutine)
			ajaxRequest.setPostRequest(afterPostRoutine);
		ajaxRequest.setUseGET();
		ajaxRequest.sendRequest();
    }	
	
</script>

<div class="teamIcon">
	<img id="teamIcon_${prefix}" src="<html:imagesPath/>icons/group.gif" onmouseover="displayTeamMembersMenu_${prefix}();" />
	<div id="teamMenu_${prefix}" class="teamIconMenuPane" style="visibility: hidden; display: none; position: absolute;">
		<ul id="teamMembersListUL_${prefix}" class="ss_finestprint">
			<li class="pasteAllUsers" onmouseover="this.style.backgroundColor='#333'; this.style.color='#FFF'; " onmouseout="this.style.backgroundColor='#FFF'; this.style.color='#333';" onclick="loadTeamMembers_${prefix}(addAllUsersFromTeam_${prefix});">Add all (<strong id="teamMembersAmount_${prefix}">${teamMembersCount}</strong>)</li>	
		</ul>
	</div>	
</div>

