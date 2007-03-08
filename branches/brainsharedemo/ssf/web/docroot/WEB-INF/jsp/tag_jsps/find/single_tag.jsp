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
	String leaveResultsVisible = ((Boolean) request.getAttribute("leaveResultsVisible")).toString();
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="prefix" value="${renderResponse.namespace}_${iCount}" />
<c:set var="tagType" value="<%= findTagType %>" />
<c:set var="leaveResultsVisible" value="<%= leaveResultsVisible %>"/>

<c:if test="${empty noInit || !noInit}">
    <c:if test="${empty ss_single_tag_js_loaded}" >
      <script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/single_tag.js"></script>
      <c:set var="ss_single_tag_js_loaded" value="1" scope="request"/>
    </c:if>
    <script type="text/javascript">
    function ss_tagVarOnload${prefix}() {
    	var url = "<ssf:url 
    				adapter="true" 
    				portletName="ss_forum" 
    				action="__ajax_request" 
    				actionUrl="false"><ssf:param 
    				name="operation" 
    				value="find_tag_search" /></ssf:url>";
    	ss_confFindTagSearchVariables("${prefix}", "<%= clickRoutine %>", tagSearchResultUrl, "${leaveResultsVisible}", url); 
    	ss_findTagInitializeForm("<%= findTagFormName %>", "${prefix}");
    }
    </script>
</c:if>

<c:if test="${empty initOnly || !initOnly}">
<div style="margin:0px; padding:0px; display:inline;">

<img src="<html:imagesPath/>pics/1pix.gif" 
  onload="ss_tagVarOnload${prefix}();"
/>

<textarea 
    class="ss_text" style="height:17px; width:<%= findTagElementWidth %>; overflow:hidden;" 
    name="<%= findTagElementName %>" 
    id="ss_findTag_searchText_${prefix}"
    
    onKeyUp="ss_findTagSearch('${prefix}', this.id, '<%= findTagElementName %>', '<%= findTagType %>');"
    onBlur="setTimeout('ss_hideDiv(\'ss_findTagNavBarDiv_${prefix}\')', 200);"></textarea></div>
<div id="ss_findTag_searchText_bottom_${prefix}" style="padding:0px; margin:0px;"></div>
<div id="ss_findTagNavBarDiv_${prefix}" 
    class="ss_findUserList" style="visibility:hidden;">
    <div id="available_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
</c:if>

