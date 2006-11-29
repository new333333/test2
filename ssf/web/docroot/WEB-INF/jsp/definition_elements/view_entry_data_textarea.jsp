<% //Textarea view %>
<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<span>
<c:out value="${ssDefinitionEntry.customAttributes[property_name].value}" escapeXml="false"/>
</span>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
    <div class="ss_text_field"><c:out 
      value="${ssDefinitionEntry.customAttributes[property_name].value}" 
      escapeXml="false"/></div>
  </td>
</tr>
</c:if>
</c:if>