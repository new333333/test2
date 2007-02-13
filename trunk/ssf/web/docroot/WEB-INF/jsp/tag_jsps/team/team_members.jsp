<% //Business mini card view %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="prefix" value="${form_name_all_team_members}_${form_name_team_member_ids}_${iCount}" />

<script type="text/javascript">

	function selectUnselectTeamMembers${prefix}() {
		var checkboxObj = $("${prefix}_${form_name_all_team_members}");
		var checked = checkboxObj && checkboxObj.checked;
		var teamMemberCheckboxes = document.getElementsByName("${form_name_team_member_ids}");
		for (var i = 0; i < teamMemberCheckboxes.length; i++) {
			teamMemberCheckboxes[i].checked = checked;
		}
	}
	
	function adjustTeamMembersCheck${prefix}() {
		var checkboxObj = $("${prefix}_${form_name_all_team_members}");
		var teamMemberCheckboxes = document.getElementsByName("${form_name_team_member_ids}");
		for (var i = 0; i < teamMemberCheckboxes.length; i++) {
			if (!teamMemberCheckboxes[i].checked)
				checkboxObj.checked = false;
		}
	}	
	
	var areTeamMembersLoaded${prefix} = false;
	var areTeamMembersShown${prefix} = false;	
		
	function toggleTeamMembersList${prefix} (divId) {
		ss_toggleDivWipe(divId);

		if (areTeamMembersShown${prefix}) {
			areTeamMembersShown${prefix} = false;
			ss_replaceImage('loadTeamMembersIcon${prefix}', '<html:imagesPath />pics/sym_s_collapse.gif');
		} else {
			areTeamMembersShown${prefix} = true;
			ss_replaceImage('loadTeamMembersIcon${prefix}', '<html:imagesPath />pics/sym_s_expand.gif');		
		}
			
		if (areTeamMembersLoaded${prefix})
			return;
			
		toggleAjaxLoadingIndicator("ajaxLoadingPane${prefix}");
		areTeamMembersLoaded${prefix} = true;
						 
		var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_team_members" /><ssf:param name="binderId" value="${ssBinder.id}" /></ssf:url>";
		url += "&ss_divId=" + divId;
		url += "&formElementName=${form_name_team_member_ids}";
		url += "&prefix=${prefix}";
		
		var ajaxRequest = new ss_AjaxRequest(url);
		ajaxRequest.setPostRequest(selectUnselectTeamMembers${prefix});		
		ajaxRequest.setUseGET();
		ajaxRequest.sendRequest();
	}
	
</script>

<c:choose>
	<c:when test="${no_team_members}">
		<a onclick="toggleTeamMembersList${prefix}('teamMembersListDiv${prefix}'); return false;" href="javascript: ;"><img id="loadTeamMembersIcon${prefix}" name="loadTeamMembersIcon${prefix}" border="0" name="" src="/ssf/images/pics/sym_s_expand.gif"/></a>
		<input type="checkbox" class="ss_style" name="${form_name_all_team_members}" id="${prefix}_${form_name_all_team_members}" value="true"  onChange="selectUnselectTeamMembers${prefix}(); ">&nbsp;<span class="ss_labelRight"><ssf:nlt tag="teamMembers.form.label"/></span>
		<div id="teamMembersListDiv${prefix}" style=""><div id="ajaxLoadingPane${prefix}"></div></div>
	</c:when>
	<c:otherwise>
		<span class="ss_light"><ssf:nlt tag="teamMembers.noMembers"/></span>
	</c:otherwise>
</c:choose>

