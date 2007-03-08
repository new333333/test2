<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // List team members %>
<% // Template used also on dashboard %>


<div class="ss_buddies">

	<div class="ss_buddiesListHeader">		
		<img border="0" src="<html:imagesPath/>icons/group.gif"/> <span class="ss_largerprint ss_bold"><ssf:nlt tag="teamMembersList.title"><ssf:param name="value" value="${ssBinder.title}"/></ssf:nlt></span> <span class="ss_fineprint ss_light"><ssf:nlt tag="teamMembersList.count"/></span> <span class="ss_fineprint ss_bold">${ssTeamMembersCount}</span>		
	</div>
	
	<table class="ss_buddiesList" cellpadding="0" cellspacing="0">
	
		<c:if test="${ssTeamMembersCount > 0}">					
			<c:forEach var="member" items="${ssTeamMembers}">
				<tr>
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
	
		
		<c:if test="${ssTeamMembersCount == 0}">
			<tr><td><ssf:nlt tag="teamMembersList.empty"/></td></tr>
		</c:if>
		
	</table>
	
</div>

