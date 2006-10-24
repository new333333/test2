<% //File entry title form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String width = (String) request.getAttribute("property_width");
	String fileEntryTitleFormName = (String) request.getAttribute("formName");
	String inline = (String) request.getAttribute("property_inline");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size='"+width+"'";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = caption;
	}
	if (inline == null) {inline = "block";}
	if (inline.equals("true")) {
		inline = "inline";
	} else {
		inline = "block";
	}
%>
<div class="ss_entryContent">
<span class="ss_labelAbove">${property_caption}</span>
<input onKeyPress="ss_fileEntryTitleKeyPress_<%= elementName %>(this)" type="file" 
  class="ss_text" name="<%= elementName %>" <%= width %> >
</div>
<script type="text/javascript">
function ss_fileEntryTitleKeyPress_<%= elementName %>(obj) {
	alert('<ssf:nlt tag="entry.fileFieldInput"/>')
}
</script>
