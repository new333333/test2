<% //Radio view %>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<span class="ss_labelRight"><c:out value="${property_caption}" /></span>
<c:out value="${ssDefinitionEntry.customAttributes[property_name].value}" escapeXml="false"/>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
	<span class="ss_bold">
	  <c:out value="${ssDefinitionEntry.customAttributes[property_name].value}" escapeXml="false"/>
	</span>
  </td>
</tr>
</c:if>
