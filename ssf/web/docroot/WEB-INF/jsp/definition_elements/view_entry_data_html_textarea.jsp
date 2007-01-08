<% //Textarea view %>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<c:if test="${!empty property_caption}">
 <span class="ss_bold"><c:out value="${property_caption}" /></span>
<br/>
</c:if>
 <div class="ss_entryDescription">
<c:out value="${ssDefinitionEntry.customAttributes[property_name].value.text}" escapeXml="false"/>
 </div>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
    <div class="ss_entryContent ss_entryDescription"><c:out 
      value="${ssDefinitionEntry.customAttributes[property_name].value.text}" 
      escapeXml="false"/></div>
  </td>
</tr>
</c:if>
