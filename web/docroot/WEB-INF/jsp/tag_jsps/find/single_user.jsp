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
<% // Find a single user %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<%
	String findUserGroupType = (String) request.getAttribute("list_type");
	String findUserElementName = (String) request.getAttribute("form_element");
	String findUserElementWidth = (String) request.getAttribute("element_width");
	String instanceCount = (String) request.getAttribute("instanceCount");
	String instanceCode = (String) request.getAttribute("instanceCode");
	String leaveResultsVisible = ((Boolean) request.getAttribute("leaveResultsVisible")).toString();
	String label = ParamUtil.get(request, "label", "");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="iCode" value="<%= instanceCode %>"/>
<c:set var="leaveResultsVisible" value="<%= leaveResultsVisible %>"/>
<c:set var="prefix" value="${renderResponse.namespace}_${iCode}_${iCount}" />
<c:set var="label" value="<%= label %>" />

<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/single_user.js"></script>

<div class="ss_style_trans">
<div style="margin:0px; padding:0px;">
	<ssf:ifaccessible>
 		<label for="ss_findUser_searchText_${prefix}">${label}</label>
 	</ssf:ifaccessible>

	<textarea 
	    class="ss_text" style="height:14px; width:<%= findUserElementWidth %>; overflow:hidden;" 
	    name="<%= findUserElementName %>" 
	    id="ss_findUser_searchText_${prefix}"
	    onKeyUp="ss_findUserSearch('${prefix}', this.id, '<%= findUserElementName %><%= instanceCount %>', '<%= findUserGroupType %>');"
	    onBlur="ss_findUserBlurTextArea('${prefix}');"
	    <c:if test="${!empty label}">
	    	title="${label}"
	    </c:if>
    ></textarea>
    <img src="<html:imagesPath/>pics/1pix.gif" <ssf:alt/>
      onload="ss_findUserConfVariableForPrefix('${prefix}', '${clickRoutine}', '${clickRoutineArgs}', '<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
		value="${ssUser.parentBinder.id}"/><ssf:param name="entryId" 
		value="ss_entryIdPlaceholder"/><ssf:param name="newTab" value="1"/></ssf:url>', ${leaveResultsVisible}, '<ssf:url 
    	adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="false" >
		<ssf:param name="operation" value="find_user_search" />
    	</ssf:url>'); ss_findUserInitializeForm('${form_name}', '${prefix}')" />
    	</div>
<div id="ss_findUser_searchText_bottom_${prefix}" style="padding:0px; margin:0px;"></div>
<div id="ss_findUserNavBarDiv_${prefix}" 
    class="ss_typeToFindResults" style="display:none; visibility:hidden;"
    onmouseover="ss_findUserMouseOverList('${prefix}')"
    onmouseout="ss_findUserMouseOutList('${prefix}')">
    <div id="available_${prefix}">
      <ul>
      </ul>
    </div>
</div>
<input type="hidden" name="<%= findUserElementName %><%= instanceCount %>"/>
</div>

