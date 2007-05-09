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
<% // Find a single tag %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<%
	String findTagType = (String) request.getAttribute("list_type");
	String findTagFormName = (String) request.getAttribute("form_name");
	String findTagElementName = (String) request.getAttribute("form_element");
	String findTagElementWidth = (String) request.getAttribute("element_width");
	String clickRoutine = (String) request.getAttribute("clickRoutine");
	String instanceCount = (String) request.getAttribute("instanceCount");
	String instanceCode = (String) request.getAttribute("instanceCode");
	String leaveResultsVisible = ((Boolean) request.getAttribute("leaveResultsVisible")).toString();
	String label = ParamUtil.get(request, "label", "");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="iCode" value="<%= instanceCode %>"/>
<c:set var="prefix" value="${renderResponse.namespace}_${iCode}_${iCount}" />
<c:set var="tagType" value="<%= findTagType %>" />
<c:set var="leaveResultsVisible" value="<%= leaveResultsVisible %>"/>
<c:set var="label" value="<%= label %>" />

    <c:if test="${empty ss_single_tag_js_loaded}" >
      <script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/single_tag.js"></script>
      <c:set var="ss_single_tag_js_loaded" value="1" scope="request"/>
    </c:if>

<div style="margin:0px; padding:0px;display:inline;">

	<textarea 
	    class="ss_text" style="height:17px; width:<%= findTagElementWidth %>; overflow:hidden;" 
	    name="<%= findTagElementName %>" 
	    id="ss_findTag_searchText_${prefix}"
	    onKeyUp="ss_findTagSearch('${prefix}', this.id, '<%= findTagElementName %>', '<%= findTagType %>');"
	    onBlur="ss_findTagBlurTextArea('${prefix}');"
    <c:if test="${!empty label}">
    	title="${label}"
    </c:if>
	></textarea>
	<img src="<html:imagesPath/>pics/1pix.gif" <ssf:alt/>
			onload="ss_confFindTagSearchVariables('${prefix}', '<%= clickRoutine %>', window.ss_tagSearchResultUrl?window.ss_tagSearchResultUrl:'', '${leaveResultsVisible}', '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="false"><ssf:param name="operation" value="find_tag_search" /></ssf:url>'); ss_findTagInitializeForm('<%= findTagFormName %>', '${prefix}'); "
	/>    
</div>
<div id="ss_findTag_searchText_bottom_${prefix}" style="padding:0px; margin:0px;"></div>
<div id="ss_findTagNavBarDiv_${prefix}" 
    class="ss_findUserList" style="visibility:hidden;"
    onmouseover="ss_findTagMouseOverList('${prefix}')"
    onmouseout="ss_findTagMouseOutList('${prefix}')">
    <div id="available_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
  
