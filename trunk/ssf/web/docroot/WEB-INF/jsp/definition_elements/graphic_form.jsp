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
<% //Graphic form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/file_browse.jsp" %>

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<br>
<span class="ss_bold"><ssf:nlt tag="form.graphic.currentGraphics" text="Current graphics"/></span>
<br>
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
&nbsp;&nbsp;&nbsp;<input type="checkbox" name="_delete_${selection.id}"
>&nbsp;${selection.fileItem.name}<br>
</c:forEach>
<span class="ss_small">(<ssf:nlt tag="form.graphic.selectForDelete" text="Select the graphics to be deleted."/>)</span>
<br>
</c:if>

</div>
