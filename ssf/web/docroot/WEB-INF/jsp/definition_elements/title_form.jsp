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
<% //Title form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
 <script type="text/javascript">
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheck", ss_ajax_result_validator);
 </script>

<c:choose>
  <c:when test="${ss_formViewStyle == 'guestbook'}">
		<input type="hidden" name="title" value="<c:out value="${ssUser.firstName}"/> <c:out value="${ssUser.lastName}"/> wrote" />
  </c:when>

  <c:otherwise>
		<%
			String caption = (String) request.getAttribute("property_caption");
			if (caption == null) {caption = "";}
		
			String width = (String) request.getAttribute("property_width");
			if (width == null || width.equals("")) {
				width = "";
			} else {
				width = "size='"+width+"'";
			}
		%>
		<div class="ss_entryContent">
		<div class="ss_labelAbove" id='${elementName}_label'><c:out value="${property_caption}"/></div>
<c:if test='${ssBinderMarker}'>
  <div class="needed-because-of-ie-bug"><div id="ss_titleCheck" style="display:none; visibility:hidden;" ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
</c:if>
		<input type="text" class="ss_text" name="title" id="title" <%= width %>
<c:if test='${ssBinderMarker}'>
	onchange="ss_ajaxValidate(ss_checkTitleUrl, this,'${elementName}_label', 'ss_titleCheck');"
</c:if>
		 <c:if test="${empty ssDefinitionEntry.title}">
		   value="<c:out value="${ssEntryTitle}"/>" />
		 </c:if>
		 <c:if test="${!empty ssDefinitionEntry.title}">
		   value="<c:out value="${ssDefinitionEntry.title}"/>" />
		 </c:if>
		 <c:if test="${property_generated}">&nbsp;(<ssf:nlt tag="entry.titleGenerated"/>)</c:if>
		</div>
  </c:otherwise>
</c:choose>

