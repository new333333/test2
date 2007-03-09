<% //Profile name view %>
<c:if test="${!empty ssDefinitionEntry.name}">

<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<h1 class="ss_entryTitle">
<c:out value="${ssDefinitionEntry.name}"/>
</h1>
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
	  <c:out value="${ssDefinitionEntry.name}"/>
	</span>
  </td>
</tr>
</c:if>
</c:if>
