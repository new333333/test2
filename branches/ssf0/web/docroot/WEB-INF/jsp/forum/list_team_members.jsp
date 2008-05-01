<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // List team members %>
<% // Template used also on dashboard %>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
<c:set var="hitCount" value="0"/>

<ssf:ifnotadapter>
<% //setup for templates and dashboard %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
</ssf:ifnotadapter>



<div class="ss_buddies">

  <table cellpadding="0" cellspacing="0" width="100%">
	<tr>
	<td valign="top">
	<div class="ss_buddiesListHeader">		
		<img border="0" <ssf:alt/>
		  src="<html:brandedImagesPath/>icons/group.gif"/> 
		  <span>${ssBinder.title}</span> 
    </div>
    <div>
		  <span class="ss_fineprint ss_light"><ssf:nlt tag="teamMembersList.count"/>:</span> 
		  <span class="ss_fineprint ss_bold">${ssTeamMembersCount}</span>		
	</div>
	</td>
	<td>&nbsp;</td>
	<td valign="top" align="right">
	  <div align="left" style="float:right; padding-right: 2px; padding-bottom: 2px;">
	  <ul>
		<ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTeamMembers">
		    <li><a class="ss_linkButton" href="<ssf:url 
		    		adapter="true" 
		    		portletName="ss_forum" 
		    		action="add_team_member" 
		    		actionUrl="true"><ssf:param 
		    		name="binderId" value="${ssBinder.id}"/><ssf:param 
		    		name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
		    	  onClick="ss_openUrlInPortlet(this.href, true, 600, 600);return false"
		    	><ssf:nlt tag="toolbar.teams.addMember"/></a></li>
		</ssf:ifAccessAllowed>
		<c:if test="${!empty ssUser.emailAddress}">
	    <li><a class="ss_linkButton" href="<ssf:url 
	    		adapter="true" 
	    		portletName="ss_forum" 
	    		action="send_email" 
	    		actionUrl="true"><ssf:param 
	    		name="binderId" value="${ssBinder.id}"/><ssf:param 
	    		name="appendTeamMembers" value="true"/></ssf:url>"
	    	  onClick="ss_openUrlInPortlet(this.href, true, 600, 600);return false"
	    	><ssf:nlt tag="toolbar.teams.sendmail"/></a></li>
	    </c:if>
	    <li><a class="ss_linkButton" href="<ssf:url 
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
						  <a href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" 
									    binderId="${member.parentBinder.id}" ><ssf:param 
									    name="entityType" value="user"/><ssf:param name="entryId" 
										value="${member.id}"/><ssf:param 
										name="newTab" value="1"/></ssf:url>"
							onClick="return ss_gotoPermalink('${member.parentBinder.id}','${member.id}', 'user', '${ss_namespace}', 'yes');"
							>${member.title}</a>
						</td>
						<td><c:if test="${!empty member.organization}"><c:out value="${member.organization}" /></c:if></td>
						<td>
							<div id="ss_presenceOptions_${member.id}_${ss_namespace}_${componentId}"></div>
							<ssf:presenceInfo user="${member}" 
							    showOptionsInline="false" 
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

