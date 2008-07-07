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

<c:set var="prefix" value="${ssFindFormName}_${ssFindFormElement}_${ssFindInstanceCount}" />

<c:if test="${empty ss_find_js_loaded}" >
	<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/find.js"></script>
	<c:set var="ss_find_js_loaded" value="1" scope="request"/>
</c:if>

<c:choose>
	<c:when test="${ssFindListType == 'user'}">
		<c:set var="accessibilityText" value="navigation.findUser" />
	</c:when>
	<c:when test="${ssFindListType == 'group'}">
		<c:set var="accessibilityText" value="navigation.findGroup" />
	</c:when>
	<c:when test="${ssFindListType == 'team'}">
		<c:set var="accessibilityText" value="navigation.findTeam" />
	</c:when>	
	<c:when test="${ssFindListType == 'application'}">
		<c:set var="accessibilityText" value="navigation.findApplication" />
	</c:when>
	<c:when test="${ssFindListType == 'applicationGroup'}">
		<c:set var="accessibilityText" value="navigation.findGroup" />
	</c:when>
	<c:otherwise>
		<c:set var="accessibilityText" value="" />
	</c:otherwise>
</c:choose>

<input type="hidden" name="${ssFindFormElement}" id="${prefix}_ss_find_multiple_input"/>		
<table class="ss_style" cellspacing="0px" cellpadding="0px" style="padding-bottom:5px;">
<tbody>
<tr>
<td valign="top">
<img src="<html:imagesPath/>pics/1pix.gif" <ssf:alt/>
	onload="window['findMultiple${prefix}'] = ssFind.configMultiple({
						thisName: 'findMultiple${prefix}',
						prefix: '${prefix}', 
						clickRoutineObj: '${ssFindClickRoutineObj}', 
						clickRoutine: '${ssFindClickRoutine}',
						formName: '${ssFindFormName}',
						elementName: '${ssFindFormElement}'
			}); <c:forEach var="item" items="${ssFindUserList}" varStatus="status"> window['findMultiple${prefix}'].addValue('<c:out value="${item.id}"/>');</c:forEach>" />
  <ssf:find formName="" 
    formElement="searchText" 
    type="${ssFindListType}"
    userList="${ssFindUserList}"
    width="70px" 
    clickRoutine="addValueByElement"
    clickRoutineObj="findMultiple${prefix}"
    findMultipleObj="findMultiple${prefix}"
    leaveResultsVisible="${ssFindLeaveResultsVisible}"
    singleItem="true"
    accessibilityText="${accessibilityText}"
    addCurrentUser="${ssFindAddCurrentUser}"
    /> 
    <c:if test="${ssFindListType == 'user'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findUser"/></span></div>
    </c:if>
    <c:if test="${ssFindListType == 'group'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findGroup"/></span></div>
    </c:if>
    <c:if test="${ssFindListType == 'team'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findTeam"/></span></div>
    </c:if>    
    <c:if test="${ssFindListType == 'application'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findApplication"/></span></div>
    </c:if>
    <c:if test="${ssFindListType == 'applicationGroup'}">
      <div><span class="ss_fineprint"><ssf:nlt tag="navigation.findGroup"/></span></div>
    </c:if>
</td>
<td valign="top" style="padding-left:10px;">
<div style="float: left;">
  <div style="border:solid black 1px;">
    <ul id="added_${prefix}" class="ss_userlist">
      <c:forEach var="item" items="${ssFindUserList}">
        <li class="ss_nowrap" id="<c:out value="${item.id}"/>" ><c:out value="${item.title}"/>
          <a href="javascript: ;" 
			onclick="window['findMultiple${prefix}'].removeValueByElement(this, '<c:out value="${item.id}"/>', '<c:out value="${item.title}"/>'); return false;"
            ><img border="0" style="padding-left: 10px;" 
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

