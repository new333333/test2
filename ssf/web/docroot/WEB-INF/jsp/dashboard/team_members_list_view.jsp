<%
// The dashboard "workspace tree" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
<c:set var="ssNamespace" value="${ssNamespace}_${ssComponentId}"/>
</c:if>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>

<c:if test="${ssDashboard.scope == 'portlet'}">
<%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
<script type="text/javascript">    	
function ${ssNamespace}_user_url(binderId, entryId, type) {
	//Build a url to go to
	ss_dashboardPorletUrlSupport(binderId, entryId, type);
	return false;
}
</script>
</c:if>

<c:if test="${ssConfigJspStyle == 'template'}">
<script type="text/javascript">
function ${ssNamespace}_user_url(binderId, entryId, type) {
	return false;
}
</script>
</c:if>

<div class="ss_buddiesListHeader">
	<img border="0" src="<html:imagesPath/>icons/group.gif"/> <span class="ss_largerprint ss_bold"><ssf:nlt tag="teamMembersList.title"/></span> <span class="ss_fineprint ss_light"><ssf:nlt tag="teamMembersList.count"/></span> <span class="ss_fineprint ss_bold">${ssDashboard.beans[componentId].ssTeamMembersCount}</span>
</div>

<table class="ss_buddiesList" cellpadding="0" cellspacing="0">
	<c:if test="${ssDashboard.beans[componentId].ssTeamMembersCount > 0}">					
		<c:forEach var="member" items="${ssDashboard.beans[componentId].ssTeamMembers}">
			<tr>
				<td class="picture">
					<div class="ss_thumbnail_small_buddies_list"><div>
					  <c:set var="selections" value="${member.customAttributes['picture'].value}" />
					  <c:set var="pictureCount" value="0"/>
					  <c:forEach var="selection" items="${selections}">
					  	<c:if test="${pictureCount == 0}">
							<img border="0" src="<ssf:url 
							    webPath="viewFile"
							    folderId="${member.parentBinder.id}"
							    entryId="${member.id}" >
							    <ssf:param name="fileId" value="${selection.id}"/>
							    <ssf:param name="viewType" value="scaled"/>
							    </ssf:url>" />
						</c:if>
						<c:set var="pictureCount" value="${pictureCount + 1}"/>
					  </c:forEach>
					</div></div>
				 </td>
				<td>
					<a class="ss_bold" href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
						value="${member.parentBinder.id}"/><ssf:param name="entryId" 
						value="${member.id}"/></ssf:url>"
						onClick="if (document.${ssNamespace}_user_url) ${ssNamespace}_user_url('${member.id}','${member.id}', '${member.entityType}'); return true;">${member.title}</a>
				</td>
				<td>${member.organization}</td>
				<td>
					<div id="ss_presenceOptions_${ssNamespace}"></div>
					<ssf:presenceInfo user="${member}" 
					    showOptionsInline="false" 
					    optionsDivId="ss_presenceOptions_${ssNamespace}"/>
				</td>
				<td><a class="ss_bold" href="mailto:${member.emailAddress}">${member.emailAddress}</a></td>
			</tr>
		</c:forEach>
	</c:if>
	
	<c:if test="${ssDashboard.beans[componentId].ssTeamMembersCount == 0}">
		<tr><td><ssf:nlt tag="teamMembersList.empty"/></td></tr>
	</c:if>
</table>
