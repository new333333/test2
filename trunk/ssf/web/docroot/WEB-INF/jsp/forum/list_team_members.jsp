<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // List team members %>
<% // Template used also on dashboard %>

<c:if test="${ss_editTeamMembers}">
	<script type="text/javascript">
		dojo.require("dojo.io.*");
	  	dojo.require("dojo.event.*");
	  
		function removeTeamMembers(id) {
			/*
			toggleAjaxLoadingIndicator(id);
	
			var bindArgs = {
		    	url: "<ssf:url 
					    	adapter="true" 
					    	portletName="ss_forum" 
					    	action="__ajax_request" >
							<ssf:param name="binderId" value="${ssBinder.id}" />
							<ssf:param name="operation" value="remove_team_members" />
				    	</ssf:url>",
				error: function(type, data, evt) {
					toggleAjaxLoadingIndicator(id);
					alert("An error occurred.");
				},
				load: function(type, data, evt) {
					toggleAjaxLoadingIndicator(id);
					alert(data);
				},
				mimetype: "text/json"
			};
		   
			dojo.io.bind(bindArgs);
			
			*/
			alert('TODO: not implemented yet!');
		}
		
	</script>
</c:if>

<div class="ss_buddiesListHeader">
	<img border="0" src="<html:imagesPath/>icons/group.gif"/> <span class="ss_largerprint ss_bold"><ssf:nlt tag="teamMembersList.title"/></span> <span class="ss_fineprint ss_light"><ssf:nlt tag="teamMembersList.count"/></span> <span class="ss_fineprint ss_bold">${ssTeamMembersCount}</span>
</div>
	
	<table class="ss_buddiesList" cellpadding="0" cellspacing="0">
	
		<c:if test="${ssTeamMembersCount > 0}">					
			<c:forEach var="member" items="${ssTeamMembers}">
				<tr>
					<c:if test="${ss_editTeamMembers}">
						<td class="selectable"><input type="checkbox" name="team_member_ids_<portlet:namespace />" value="${member.id}" /></td>
					</c:if>
					<td class="picture">
						<ssf:buddyPhoto style="ss_thumbnail_small_buddies_list" photos="${member.customAttributes['picture'].value}" 
							folderId="${member.parentBinder.id}" entryId="${member.id}" />						
					 </td>
					<td>
						<a class="ss_bold" href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
							value="${member.parentBinder.id}"/><ssf:param name="entryId" 
							value="${member.id}"/></ssf:url>">${member.title}</a>
					</td>
					<td><c:out value="${member.organization}" /></td>
					<td>
						<div id="ss_presenceOptions_${renderResponse.namespace}"></div>
						<ssf:presenceInfo user="${member}" 
						    showOptionsInline="false" 
						    optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
					</td>
					<td><a class="ss_bold" href="mailto:<c:out value="${member.emailAddress}" />"><c:out value="${member.emailAddress}" /></a></td>
				</tr>
			</c:forEach>
		</c:if>
	
		<c:if test="${ssTeamMembersCount > 0 && ss_editTeamMembers}">	
			<tr class="options">
				<td class="ss_light ss_fineprint selectall"><input type="checkbox" id="team_member_ids_<portlet:namespace />" /><br/><label for="team_member_all_ids_<portlet:namespace />"><ssf:nlt tag="button.selectAll"/></label></td>
				<td colspan="5"></td>
			</tr>
		</c:if>
		
		<c:if test="${ssTeamMembersCount == 0}">
			<tr><td><ssf:nlt tag="teamMembersList.empty"/></td></tr>
		</c:if>
		
	</table>

	<c:if test="${ss_editTeamMembers}">
		<div class="ss_buddiesListFooter" id="<portlet:namespace />deleteTeamMembers">		
			<form class="ss_style ss_form" method="get"	onSubmit="return false;">
				<input type="submit" class="ss_submit" name="removeBtn"  			   
				   value="<ssf:nlt tag="button.removeSelected" text="Remove selected"/>" 
				   onclick="removeTeamMembers('<portlet:namespace />deleteTeamMembers');" />
			</form>
		</div>
	</c:if>


<c:if test="${ss_editTeamMembers}">
	<script type="text/javascript">
		ss_synchronizeCheckboxes("team_member_all_ids_<portlet:namespace />", "team_member_ids_<portlet:namespace />");
	</script>
</c:if>
		

