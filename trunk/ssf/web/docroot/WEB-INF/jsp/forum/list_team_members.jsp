<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // List team members %>
<% // Template used also on dashboard %>


<div class="ss_buddies">

  <table cellpadding="0" cellspacing="0" width="100%">
	<tr>
	<td valign="top">
	<div class="ss_buddiesListHeader">		
		<img border="0" <ssf:alt/>
		  src="<html:imagesPath/>icons/group.gif"/> 
		  <ssf:nlt tag="teamMembersList.title">
		  <ssf:param name="value" value="${ssBinder.title}"/>
		  </ssf:nlt></span> 
    </div>
    <div>
		  <span class="ss_fineprint ss_light"><ssf:nlt tag="teamMembersList.count"/></span> 
		  <span class="ss_fineprint ss_bold">${ssTeamMembersCount}</span>		
	</div>
	</td>
	<td>&nbsp;</td>
	<td valign="top" align="right">
	  <div align="left" style="float:right;">
	  <ul>
	    <li><a href="<ssf:url 
	    		adapter="true" 
	    		portletName="ss_forum" 
	    		action="add_team_member" 
	    		actionUrl="true"><ssf:param 
	    		name="binderId" value="${ssBinder.id}"/><ssf:param 
	    		name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
	    	  onClick="ss_openUrlInPortlet(this.href, true, 600, 600);return false"
	    	><ssf:nlt tag="toolbar.teams.addMember"/></a></li>
	    <li><a href="<ssf:url 
	    		adapter="true" 
	    		portletName="ss_forum" 
	    		action="send_email" 
	    		actionUrl="true"><ssf:param 
	    		name="binderId" value="${ssBinder.id}"/><ssf:param 
	    		name="appendTeamMembers" value="true"/></ssf:url>"
	    	  onClick="ss_openUrlInPortlet(this.href, true, 600, 600);return false"
	    	><ssf:nlt tag="toolbar.teams.sendmail"/></a></li>
	    <li><a href="<ssf:url 
	    		adapter="true" 
	    		portletName="ss_forum" 
	    		action="add_meeting" 
	    		actionUrl="true"><ssf:param 
	    		name="binderId" value="${ssBinder.id}"/><ssf:param 
	    		name="appendTeamMembers" value="true"/></ssf:url>"
	    	  onClick="ss_openUrlInPortlet(this.href, true, 600, 600);return false"
	    	><ssf:nlt tag="toolbar.teams.meet"/></a></li>
	  </ul>
	  </div>
	</td>
	</tr>
  </table>
	
	<table class="ss_buddiesList" cellpadding="0" cellspacing="0">
	
		<c:choose>
			<c:when test="${ssTeamMembersCount > 0}">					
				<c:forEach var="member" items="${ssTeamMembers}">
					<tr>
						<td class="picture">
							<ssf:buddyPhoto style="ss_thumbnail_small_buddies_list" 
							photos="${member.customAttributes['picture'].value}" 
								folderId="${member.parentBinder.id}" entryId="${member.id}" />						
						 </td>
						<td>
							<a href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
								value="${member.parentBinder.id}"/><ssf:param name="entryId" 
								value="${member.id}"/></ssf:url>">${member.title}</a>
						</td>
						<td><c:if test="${!empty member.organization}"><c:out value="${member.organization}" /></c:if></td>
						<td>
							<div id="ss_presenceOptions_${renderResponse.namespace}"></div>
							<ssf:presenceInfo user="${member}" 
							    showOptionsInline="false" 
							    optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
						</td>
						<td><a href="mailto:<c:out value="${member.emailAddress}" 
						/>"><c:out value="${member.emailAddress}" /></a></td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr><td><ssf:nlt tag="teamMembersList.empty"/></td></tr>
			</c:otherwise>
		</c:choose>
		
	</table>
	
</div>

