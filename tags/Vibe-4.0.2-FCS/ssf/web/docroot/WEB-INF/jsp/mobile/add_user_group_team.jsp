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
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<%
	String type = (String) request.getAttribute("ss_type");
	String elementName = (String) request.getAttribute("ssElementName");
	java.util.List userList = new java.util.ArrayList();
	java.util.Set userListSet = new java.util.HashSet();
%>

<script type="text/javascript">
function ss_submitSelectForm${ssElementName}(url) {
	var formObj = self.document.getElementById("selectIdForm${ssElementName}");
	formObj.action = url;
	formObj.submit();
}
</script>
<div class="ss_form_wrap">
    <div class="ss_form_header">
      <c:if test="${ss_type == 'user'}">
        <span><ssf:nlt tag="userlist.addUser" /></span>
      </c:if>
      <c:if test="${ss_type == 'group'}">
        <span><ssf:nlt tag="userlist.addGroup" /></span>
      </c:if>
      <c:if test="${ss_type == 'team'}">
        <span><ssf:nlt tag="userlist.addTeam" /></span>
      </c:if>
    </div>

	  <div class="ss_form_subhead">
		<div class="pad-top20 pad-left20 marginbottom2">
		  <form method="post" action="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="true" 
					operation="mobile_find_user_group_team"><ssf:param name="binderId"
				value="${ssBinder.id}"/><ssf:param name="entryId"
				value="${ssEntry.id}"/><ssf:param name="type"
				value="${ss_type}"/><ssf:param name="element"
				value="${ssElementName}"/><ssf:param name="_entryOperationType"
				value="${ssEntryOperationType}"/><ssf:param name="_entryDelayWorkflow"
				value="${ssEntryDelayWorkflow}"/></ssf:url>">
		  <label for="searchText">
		    <c:if test="${ss_type == 'user'}"><span class="label-gray"><ssf:nlt tag="navigation.findUser"/></span></c:if>
		    <c:if test="${ss_type == 'group'}"><span class="label-gray"><ssf:nlt tag="navigation.findGroup"/></span></c:if>
		    <c:if test="${ss_type == 'team'}"><span class="label-gray"><ssf:nlt tag="navigation.findTeam"/></span></c:if>
		  </label>
		  <input type="text" size="25" name="searchText" id="searchText" autocomplete="off"
		    value="<ssf:escapeQuotes>${ss_searchText}</ssf:escapeQuotes>"/>
		  <input 
		    type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"/>
		  <input 
		    type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"/>
		  </form>
		</div>

	  <div class="marginbottom2">
	    <form method="post" name="selectIdForm${ssElementName}" id="selectIdForm${ssElementName}">
		  <c:forEach var="user" items="${ssUsers}" >
		    <div class="folder-item">
			  <a href="<ssf:url adapter="true" portletName="ss_forum" 
						action="__ajax_mobile" actionUrl="true" 
						operation="mobile_add_user_group_team"><ssf:param name="binderId"
						value="${ssBinder.id}"/><ssf:param name="entryId"
						value="${ssEntry.id}"/><ssf:param name="operation2"
						value="add"/><ssf:param name="id"
						value="${user._docId}"/><ssf:param name="type"
						value="${ss_type}"/><ssf:param name="element"
						value="${ssElementName}"/><ssf:param name="addUserGroupTeamBtn"
						value="true"/><ssf:param name="_entryOperationType"
						value="${ssEntryOperationType}"/><ssf:param name="_entryDelayWorkflow"
						value="${ssEntryDelayWorkflow}"/></ssf:url>"
			  	onClick="ss_submitSelectForm${ssElementName}(this.href);return false;">
			  	<c:if test="${ss_type == 'user' || ss_type == 'group'}">
				    <span><ssf:userTitle user="${user._principal}"/></span>
				    <span>(<ssf:userName user="${user._principal}"/>)</span>
				</c:if>
			  	<c:if test="${ss_type == 'team'}">
				    <span>${user.title}</span>
				    <span>(${user._entityPath})</span>
				</c:if>
			  </a>
			</div>
		  </c:forEach>
	    </form>
	  </div>

  <c:if test="${!empty ss_prevPage || !empty ss_nextPage}">
    <br/>
	<div class="marginbottom2">
	  <c:if test="${!empty ss_prevPage}">
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile" 
			operation="mobile_find_user_group_team" 
			actionUrl="false" ><ssf:param name="binderId"
				value="${ssBinder.id}"/><ssf:param name="entryId"
				value="${ssEntry.id}"/><ssf:param name="type"
				value="${ss_type}"/><ssf:param name="element"
				value="${ssElementName}"/><ssf:param 
				name="searchText" useBody="true">${ss_searchText}</ssf:param><ssf:param 
				name="pageNumber" value="${ss_prevPage}"/><ssf:param name="_entryOperationType"
				value="${ssEntryOperationType}"/><ssf:param name="_entryDelayWorkflow"
				value="${ssEntryDelayWorkflow}"/></ssf:url>"
		><img class="actionbar-img" border="0" src="<html:rootPath/>images/mobile/left_50.png"/></a>
	  </c:if>
	  <c:if test="${empty ss_prevPage}">
	    <img class="actionbar-img" border="0" src="<html:rootPath/>images/mobile/left_dis_50.png"
	  	  <ssf:alt tag=""/> />
	  </c:if>

	  <c:if test="${!empty ss_nextPage}">
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="__ajax_mobile" 
			operation="mobile_find_user_group_team" 
			actionUrl="false" ><ssf:param name="binderId"
				value="${ssBinder.id}"/><ssf:param name="entryId"
				value="${ssEntry.id}"/><ssf:param name="type"
				value="${ss_type}"/><ssf:param name="element"
				value="${ssElementName}"/><ssf:param 
				name="searchText" useBody="true">${ss_searchText}</ssf:param><ssf:param 
				name="pageNumber" value="${ss_nextPage}"/><ssf:param name="_entryOperationType"
				value="${ssEntryOperationType}"/><ssf:param name="_entryDelayWorkflow"
				value="${ssEntryDelayWorkflow}"/></ssf:url>"
		><img class="actionbar-img" border="0" src="<html:rootPath/>images/mobile/right_50.png"/></a>
	  </c:if>
	  <c:if test="${empty ss_nextPage}">
	    <img class="actionbar-img" border="0" src="<html:rootPath/>images/mobile/right_dis_50.png"
	 	  <ssf:alt tag=""/> />
	  </c:if>
	</div>
  </c:if>
</div>

</body>
</html>
