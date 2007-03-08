<% //Description view %>
<c:if test="${!empty ssDefinitionEntry.description}">
<c:if test="${empty ss_element_display_style}">
<ssf:editable entity="${ssDefinitionEntry}" element="description" aclMap="${ss_accessControlMap}">
 <span><ssf:markup type="view" entity="${ssDefinitionEntry}"><c:out 
   value="${ssDefinitionEntry.description.text}" escapeXml="false"/></ssf:markup></span>
</ssf:editable>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
    <ssf:editable entity="${ssDefinitionEntry}" element="description" aclMap="${ss_accessControlMap}">
    <span><ssf:markup type="view" entity="${ssDefinitionEntry}"><c:out 
      value="${ssDefinitionEntry.description.text}" 
      escapeXml="false"/></ssf:markup></span>
    </ssf:editable>
  </td>
</tr>
</c:if>
</c:if>
