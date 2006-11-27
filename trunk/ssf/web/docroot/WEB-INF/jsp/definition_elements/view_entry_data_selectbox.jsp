<% //Selectbox view %>
<c:if test="${!empty ss_profile_element_display_style && 
    ss_profile_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_bold ss_profile_elements_spacer" valign="top" align="left">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
	<ul class="ss_nobullet">
	<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}" >
	<li><c:out value="${selection}" escapeXml="false"/></span></li>
	</c:forEach>
	</ul>
  </td>
</tr>
</c:if>

<c:if test="${empty ss_profile_element_display_style}">
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<ul class="ss_nobullet">
<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}" >
<li><c:out value="${selection}" escapeXml="false"/></span></li>
</c:forEach>
</ul>
</div>
</c:if>
