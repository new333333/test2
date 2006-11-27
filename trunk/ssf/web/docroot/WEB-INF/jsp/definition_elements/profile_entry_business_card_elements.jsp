<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ssNamespace" value=""/>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>

<table width="100%">
<tr>
<td valign="top">
<div>
<c:if test="${!empty ssDefinitionEntry.customAttributes['picture']}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
<c:set var="count" value="0"/>
<c:forEach var="selection" items="${selections}">
  <c:if test="${count == 0}">
	<img border="0" src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="viewType" value="scaled"/>
	    </ssf:url>" alt="${property_caption}" />
  </c:if>
  <c:set var="count" value="${count + 1}"/>
</c:forEach>
</c:if>
</div>

</td>
<td valign="top" style="padding-left:30px;">

<c:set var="ss_profile_element_display_style" value="tableAlignLeft" scope="request"/>
<table>

<c:forEach var="element" items="${propertyValues__elements}">

<tr>
<td align="right" class="ss_profile_elements_spacer">
  <span class="ss_bold"><ssf:nlt tag="profile.element.${element}"/></span>
</td>
<td>
 <c:if test="${!empty ssDefinitionEntry[element]}">
<c:out value="${ssDefinitionEntry[element]}"/>
  </c:if>
  <c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
    <c:out value="${ssDefinitionEntry[element]}"/>
  </c:if>
</td>
</tr>
</c:forEach>

</table>
<c:set var="ss_profile_element_display_style" value="" scope="request"/>

</td>
<td valign="top" class="ss_profile_elements_spacer">
  <ssf:presenceInfo user="${ssDefinitionEntry}" showOptionsInline="true" optionsDivId="ss_presenceOptions"/>
  <div id="ss_presenceOptions"></div>
</td>
</tr>
</table>
