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
<c:set var="elementName" value="${property_name}"/>
<c:set var="caption" value="${property_caption}"/>

<c:set var="width" value=""/>
<c:if test='${! empty property_width}'>
<c:set var="width" value="size='${property_width}'"/>
</c:if>

<c:set var="required" value=""/>
<c:if test='${property_required}'>
<c:set var="required" value="<span class='required'>*</span>"/>
</c:if>

<c:set var="inline" value=""/>
<c:if test='${! empty property_inline and property_inline}'>
<c:set var="inline" value="style='display:inline;'"/>
</c:if>

<c:set var="count" value="1"/>
<c:if test='${! empty property_number}'>
<c:set var="count" value="${property_number}"/>
</c:if>

<c:set var="eName" value="${elementName}"/>

<c:if test='${! empty ssFolder}'>
 <c:set var="entryId" value=""/>
 <c:if test="${!empty ssEntry}">
  <c:set var="entryId" value="${ssEntry.id}"/>
 </c:if>
 <script type="text/javascript">
var ss_findEntryForFileUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="find_entry_for_file" />
	<ssf:param name="folderId" value="${ssFolder.id}" />
	<ssf:param name="entryId" value="${entryId}" />
	</ssf:url>";
  <c:forEach var="i" begin="1" end="${count}">
   <c:if test='${! empty property_number}'>
	<c:set var="eName" value="${elementName}${i}"/>
   </c:if>
ss_addValidator("ss_duplicateFileCheck_${eName}", ss_ajax_result_validator);
var ${eName}_ok = 1;
  </c:forEach>
 </script>
</c:if>

<div class="ss_entryContent" ${inline}>
<span class="ss_labelAbove" id="${elementName}_label">${caption}${required}</span>
<c:forEach var="i" begin="1" end="${count}">
 <c:if test='${! empty property_number}'>
	<c:set var="eName" value="${elementName}${i}"/>
 </c:if>
 <c:if test='${! empty ssFolder}'>
  <div class="needed-because-of-ie-bug"><div id="ss_duplicateFileCheck_${eName}" style="display:none; visibility:hidden;" ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
  <input type="file" class="ss_text" name="${eName}" id="${eName}" ${width} onchange="ss_ajaxValidate(ss_findEntryForFileUrl, this,'${elementName}_label', 'ss_duplicateFileCheck_${eName}');"/><br/>
 </c:if>
 <c:if test='${empty ssFolder}'>
  <input type="file" class="ss_text" name="${eName}" id="${eName}" ${width}/><br/>
 </c:if>
</c:forEach>

