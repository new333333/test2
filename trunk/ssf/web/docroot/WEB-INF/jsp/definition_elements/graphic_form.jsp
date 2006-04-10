<% //Graphic form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = caption;
	}
	String width = (String) request.getAttribute("property_width");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size='"+width+"'";
	}
%>
<div class="ss_entryContent" >
<span class="ss_labelAbove"><%= caption %></span>
<input type="file" class="ss_text" name="<%= elementName %>" <%= width %> >

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<br>
<span class="ss_bold"><ssf:nlt tag="form.graphic.currentGraphics" text="Current graphics"/></span>
<br>
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
&nbsp;&nbsp;&nbsp;<input type="checkbox" name="_delete_${selection.id}"
>&nbsp;${selection.fileItem.name}<br>
</c:forEach>
<span class="ss_small">(<ssf:nlt tag="form.graphic.selectForDelete" text="Select the graphics to be deleted."/>)</span>
<br>
</c:if>

</div>
