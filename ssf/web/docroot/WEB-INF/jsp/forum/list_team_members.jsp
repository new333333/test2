<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% // List team members %>
<% // Template used also on dashboard %>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
<c:set var="hitCount" value="0"/>

<ssf:ifnotadapter>
<% //setup for templates and dashboard %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
</ssf:ifnotadapter>



<div class="ss_buddies">

	<div class="ss_buddiesListHeader">		
		<img border="0" <ssf:alt/>
		  src="<html:brandedImagesPath/>icons/group.gif"/> 
		  <span>${ssBinder.title}</span> 
    </div>
    <table>
    <tr>
    <td valign="top">
    <div>
		  <span class="ss_smallprint"><ssf:nlt tag="teamMembersList.count"/>:</span> 
		  <span class="ss_smallprint ss_bold">${ssTeamMembersCount}</span>		
	</div>
	</td>
	<td valign="top" style="padding-left:40px;">
	  <c:if test="${!empty ssTeamMemberGroups}">
	    <span class="ss_smallprint"><ssf:nlt tag="teamMembersList.groupsInTeam"/></span>
	    <div style="padding-left:10px;">
	    <c:forEach var="teamGroup" items="${ssTeamMemberGroups}">
	    	<a href="<ssf:url
					adapter="true" 
					crawlable="true"
					portletName="ss_forum" 
					action="__ajax_request"
					actionUrl="false"><ssf:param 
					name="operation" value="get_group_list"/><ssf:param 
					name="groupId" value="${teamGroup.value.id}"/></ssf:url>"
			    onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">
			<span class="ss_smallprint">${teamGroup.key}</span>
			<span class="ss_fineprint ss_light">(<ssf:nlt tag="teamMembersList.members">
			  <ssf:param name="value" value="${fn:length(teamGroup.value.members)}"/>
			</ssf:nlt>)</span>
			 </a><br/>
	    </c:forEach>
	    </div>
	  </c:if>
	</td>
	</tr>
	</table>
<c:if test="${ssConfigJspStyle != 'template'}">	
	<table class="ss_buddiesList" cellpadding="0" cellspacing="0">
	
		<c:choose>
			<c:when test="${ssTeamMembersCount > 0}">					
				<c:forEach var="member" items="${ssTeamMembers}">
				  <c:set var="hitCount" value="${hitCount + 1}"/>
					<tr>
						<td class="picture">
							<ssf:buddyPhoto style="ss_thumbnail_small_buddies_list" 
							photos="${member.customAttributes['picture'].value}" 
								folderId="${member.parentBinder.id}" entryId="${member.id}" />						
						 </td>
						<td>
						  <a href="<ssf:permalink entity="${member}"/>"
							onClick="return ss_gotoPermalink('${member.parentBinder.id}','${member.id}', 'user', '${ss_namespace}', 'yes');"
							>${member.title}</a>
						</td>
						<td><c:if test="${!empty member.organization}"><c:out value="${member.organization}" /></c:if></td>
						<td>
							<div id="ss_presenceOptions_${member.id}_${ss_namespace}_${componentId}"></div>
							<ssf:presenceInfo user="${member}" 
							    optionsDivId="ss_presenceOptions_${member.id}_${ss_namespace}_${componentId}"/>
						</td>
						<td><a href="mailto:<c:out value="${member.emailAddress}" 
						/>"><c:out value="${member.emailAddress}" />&nbsp</a></td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr><td><ssf:nlt tag="teamMembersList.empty"/></td></tr>
			</c:otherwise>
		</c:choose>
		
	</table>
	
<div>
  <table width="100%">
   <tr>
    <td>
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="folder.Results">
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
	    <ssf:param name="value" value="${ssTeamMembersCount}"/>
	    </ssf:nlt>]
	  </span>
</c:if>
	</td>
	<c:if test="${ssDashboard.scope != 'portlet'}">
		<c:set var="binderId" value="${ssBinder.id}"/>
	</c:if>
	<c:if test="${ssDashboard.scope == 'portlet'}">
		<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
	</c:if>
	
	<td align="right">
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onClick="ss_moreTeamMembers('${ssBinder.id}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}'); return false;"
	        href="javascript:;" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssTeamMembersCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreTeamMembers('${ssBinder.id}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}'); return false;"
	        href="javascript:;" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
</c:if>
</div>

