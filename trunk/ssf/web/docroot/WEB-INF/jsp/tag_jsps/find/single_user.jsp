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
<% // Find a single user %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<%
	String findUserGroupType = (String) request.getAttribute("list_type");
	String findUserElementName = (String) request.getAttribute("form_element");
	String findUserElementWidth = (String) request.getAttribute("element_width");
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
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
<div style="margin:0px; padding:0px;"><textarea 
    class="ss_text" style="height:17px; width:<%= findUserElementWidth %>; overflow:hidden;" 
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
    class="ss_findUserList" style="display:none; visibility:hidden;"
    onmouseover="ss_findUserMouseOverList('${prefix}')"
    onmouseout="ss_findUserMouseOutList('${prefix}')">
    <div id="available_${prefix}">
      <ul>
      </ul>
    </div>
</div>
<input type="hidden" name="<%= findUserElementName %><%= instanceCount %>"/>
</div>

