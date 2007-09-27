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
<% // User/Group list widget %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	Set userList = (Set) request.getAttribute("user_list");
	String userGroupType = (String) request.getAttribute("list_type");
	String binderId = (String) request.getAttribute("binderId");
	String userListFormName = (String) request.getAttribute("form_name");
	String userListElementName = (String) request.getAttribute("form_element");
	String instanceCount = (String) request.getAttribute("instanceCount");
	String clickRoutine = (String) request.getAttribute("clickRoutine");
	String clickRoutineArgs = (String) request.getAttribute("clickRoutineArgs");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="userList" value="<%= userList %>"/>
<c:set var="binderId" value="<%= binderId %>"/>
<c:set var="prefix" value="${form_name}_${form_element}_${iCount}" />

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/user_list.js"></script>


<c:choose>
	<c:when test="${list_type == 'user'}">
		<c:set var="accessibilityText" value="navigation.findUser" />
	</c:when>
	<c:when test="${list_type == 'group'}">
		<c:set var="accessibilityText" value="navigation.findGroup" />
	</c:when>
	<c:otherwise>">
		<c:set var="accessibilityText" value="" />
	</c:otherwise>
</c:choose>


<input type="hidden" name="<%= userListElementName %>" id="ss_usersListInput${prefix}"/>		
<table class="ss_style" cellspacing="0px" cellpadding="0px" style="padding-bottom:5px;">
<tbody>
<tr>
<td valign="top">
<img src="<html:imagesPath/>pics/1pix.gif" <ssf:alt/>
  onload="ss_findUsersConfVariableForPrefix('${prefix}', '${clickRoutine}', '<%= userListFormName %>', '<%= userListElementName %>'); ss_findUserListInitializeForm('${prefix}', '<%= userListFormName %>', '<%= userListElementName %>');  <c:forEach var="item" items="${userList}" varStatus="status"> ss_addUserIdToFormElement('${prefix}', '<c:out value="${item.id}"/>');</c:forEach>" />
  <ssf:find formName="" 
    formElement="searchText" 
    type="${list_type}"
    width="70px" 
    clickRoutine="ss_userListSelectItem"
    clickRoutineArgs="${prefix}"
    leaveResultsVisible="${leaveResultsVisible}"
    singleItem="true"
    accessibilityText="${accessibilityText}"
    /> 
    <c:if test="${list_type == 'user'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findUser"/></span></div>
    </c:if>
    <c:if test="${list_type == 'group'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findGroup"/></span></div>
    </c:if>
</td>
<td valign="top" style="padding-left:10px;">
<div style="float: left;">
  <div style="border:solid black 1px;">
    <ul id="added_${prefix}" class="ss_userlist">
      <c:forEach var="item" items="${userList}">
        <li class="ss_nowrap" id="<c:out value="${item.id}"/>" ><c:out value="${item.title}"/>
          <a href="javascript: ;" 
            onClick="ss_userListRemove('${prefix}', this);return false;"><img border="0" style="padding-left: 10px;" 
            <ssf:alt tag="alt.delete"/> src="<html:imagesPath/>pics/sym_s_delete.gif"/></a>
        </li>
      </c:forEach>
    </ul>    
  </div>  
</div>
<div class="ss_clear"></div>
</td>
</tr>
</tbody>
</table>

