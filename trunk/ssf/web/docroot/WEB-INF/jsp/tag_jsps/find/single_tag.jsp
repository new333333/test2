<% // Find a single tag %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	String findTagType = (String) request.getAttribute("list_type");
	String findTagFormName = (String) request.getAttribute("form_name");
	String findTagElementName = (String) request.getAttribute("form_element");
	String findTagElementWidth = (String) request.getAttribute("element_width");
	String clickRoutine = (String) request.getAttribute("clickRoutine");
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
	Boolean leaveResultsVisible = (Boolean) request.getAttribute("leaveResultsVisible");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="prefix" value="${renderResponse.namespace}_${iCount}" />
<c:set var="tagType" value="<%= findTagType %>" />




<div style="margin:0px; padding:0px;display:inline;">

<img src="<html:imagesPath/>pics/1pix.gif" 
		onload="confVariableForPrefix('${prefix}', '<%= clickRoutine %>'); ss_createOnSubmitObj('${prefix}onSubmit', '<%= findTagFormName %>', ss_saveFindTagData('${prefix}', <%= leaveResultsVisible %>), '');"
/>

<textarea 
    class="ss_text" style="height:17px; width:<%= findTagElementWidth %>; overflow:hidden;" 
    name="<%= findTagElementName %>" 
    id="ss_findTags_searchText_${prefix}"
    
    onKeyUp="ss_findTagSearch('${prefix}', this.id, '<%= findTagElementName %>', '<%= findTagType %>', '<%= leaveResultsVisible %>', '<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="false"><ssf:param name="operation" value="find_tag_search" /></ssf:url>');"
    onBlur="setTimeout('ss_hideDiv(\'ss_findTagNavBarDiv_${prefix}\')', 200);"></textarea></div>
<div id="ss_findTag_searchText_bottom_${prefix}" style="padding:0px; margin:0px;"></div>
<div id="ss_findTagNavBarDiv_${prefix}" 
    class="ss_findUserList" style="visibility:hidden;">
    <div id="available_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
  
