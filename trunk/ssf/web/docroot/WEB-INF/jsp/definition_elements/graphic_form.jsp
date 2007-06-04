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
<div class="ss_entryContent" ${inline}>
<%@ include file="/WEB-INF/jsp/definition_elements/file_browse.jsp" %>

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<c:set var="count" value="0"/>
<c:forEach var="selection" items="${selections}">
  <c:set var="count" value="${count + 1}"/>
</c:forEach>
<br/>
<span class="ss_bold"><ssf:nlt tag="form.graphic.currentGraphics" /></span>
<br/>
<table cellspacing="0" cellpadding="0" border="0"><tbody>
<c:forEach var="selection" items="${selections}">
<tr id="${selection.id}">
<c:if test="${count > 0}">
<td><a class="ss_inlineButton" 
onClick="ss_moveThisTableRow(this, '<portlet:namespace/>', 'down');ss_saveGraphicOrder(this);"
><img alt="<ssf:nlt tag="favorites.movedown"/>" title="<ssf:nlt tag="favorites.movedown"/>" 
src="<html:imagesPath/>icons/button_move_down.gif" 
/></a></td>

<td><a class="ss_inlineButton" 
onClick="ss_moveThisTableRow(this, '<portlet:namespace/>', 'up');ss_saveGraphicOrder(this);"
><img alt="<ssf:nlt tag="favorites.moveup"/>" title="<ssf:nlt tag="favorites.moveup"/>" 
src="<html:imagesPath/>icons/button_move_up.gif" 
/></a></td>
</c:if>

<td><input type="checkbox" name="_delete_${selection.id}"
>&nbsp;${selection.fileItem.name}</td>

</tr>
</c:forEach>
</tbody></table>	
<span class="ss_small">(<ssf:nlt tag="form.graphic.selectForDelete" text="Select the graphics to be deleted."/>)</span>
<br/>
<br/>
  <c:if test="${count > 0}">
    <input type="hidden" name="_graphic_id_order"/>
    <script type="text/javascript">
function ss_saveGraphicOrder(obj) {
	var formObj = ss_getContainingForm(obj);
	var hiddenObj = formObj['_graphic_id_order']
	hiddenObj.value = "";
	var tableNode = ss_findOwningElement(obj, 'tbody')
	for (var i = 0; i < tableNode.childNodes.length; i++) {
		var node = tableNode.childNodes[i]
		if (node.tagName && node.tagName.toLowerCase() == 'tr') {
			if (hiddenObj.value != '') hiddenObj.value += ' ';
			hiddenObj.value += node.id;
		}
	}
	//alert('hiddenObj.value = '+hiddenObj.value)
}
    </script>
  </c:if>
</c:if>

</div>
