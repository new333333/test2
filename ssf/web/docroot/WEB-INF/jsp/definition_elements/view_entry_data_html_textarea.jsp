<% //Textarea view %>
<div class="ss_entryContent">
<c:if test="${!empty property_caption}">
 <span class="ss_bold"><c:out value="${property_caption}" /></span>
<br/>
</c:if>
 <span>
<c:out value="${ssDefinitionEntry.customAttributes[property_name].value.text}" escapeXml="false"/>
 </span>
</div>
