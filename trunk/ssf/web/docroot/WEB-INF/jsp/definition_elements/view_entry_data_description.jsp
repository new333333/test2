<% //Description view %>
<c:if test="${!empty ssDefinitionEntry.description}">
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
 <span class="ss_text_field"><c:out value="${ssDefinitionEntry.description.text}" escapeXml="false"/></span>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
    <span class="ss_text_field"><c:out value="${ssDefinitionEntry.description.text}" escapeXml="false"/></span>
  </td>
</tr>
</c:if>
</c:if>
