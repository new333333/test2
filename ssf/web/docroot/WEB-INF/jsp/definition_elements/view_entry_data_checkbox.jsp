<% //checkbox view %>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" checked DISABLED>
</c:if>
<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" DISABLED>
</c:if>
<span class="ss_labelRight"><c:out value="${property_caption}" /></span>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
	<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
	<input type="checkbox" checked DISABLED>
	</c:if>
	<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
	<input type="checkbox" DISABLED>
	</c:if>
  </td>
</tr>
</c:if>
