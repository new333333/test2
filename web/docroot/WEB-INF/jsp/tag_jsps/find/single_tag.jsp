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
	String accessibilityText = (String) request.getAttribute("accessibilityText");
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
	<ssf:ifaccessible>
 		<label for="ss_findTag_searchText_${prefix}">${label}</label>
 	</ssf:ifaccessible>
 	
	<textarea 
	    class="ss_text" style="height:14px; width:<%= findTagElementWidth %>; overflow:hidden;" 
	    name="<%= findTagElementName %>" 
	    id="ss_findTag_searchText_${prefix}"
	    onKeyUp="ss_findTagSearch('${prefix}', this.id, '<%= findTagElementName %>', '<%= findTagType %>');"
	    onBlur="ss_findTagBlurTextArea('${prefix}');"
    <c:if test="${!empty label}">
    	title="${label}"
    </c:if>
	></textarea>
	<img src="<html:imagesPath/>pics/1pix.gif" <ssf:alt/>
			onload="ss_confFindTagSearchVariables('${prefix}', '<%= clickRoutine %>', 
			<ssf:ifnotadapter>
			'<portlet:actionURL windowState="maximized" 
		portletMode="view"><portlet:param 
		name="action" value="advanced_search"/><portlet:param 
		name="searchTags" value="ss_tagPlaceHolder"/><portlet:param 
		name="operation" value="ss_searchResults"/><portlet:param 
		name="tabTitle" value="ss_tagPlaceHolder"/><portlet:param 
		name="newTab" value="1"/><portlet:param 
		name="searchItemType" value="workspace"/><portlet:param 
		name="searchItemType" value="folder"/><portlet:param 
		name="searchItemType" value="user"/><portlet:param 
		name="searchItemType" value="entry"/><portlet:param 
		name="searchItemType" value="reply"/></portlet:actionURL>'
		</ssf:ifnotadapter>
		<ssf:ifadapter>
		window.ss_tagSearchResultUrl?window.ss_tagSearchResultUrl:''
		</ssf:ifadapter>
	,'${leaveResultsVisible}', '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="false"><ssf:param name="operation" value="find_tag_search" /></ssf:url>'); ss_findTagInitializeForm('<%= findTagFormName %>', '${prefix}'); "
	/>    
</div>
<div id="ss_findTag_searchText_bottom_${prefix}" style="padding:0px; margin:0px;"></div>
<div id="ss_findTagNavBarDiv_${prefix}" 
    class="ss_typeToFindResults" style="visibility:hidden;"
    onmouseover="ss_findTagMouseOverList('${prefix}')"
    onmouseout="ss_findTagMouseOutList('${prefix}')">
    <div id="available_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
  
