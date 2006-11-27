<% //Business card view %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>
<div class="ss_entryContent">
<c:if test="${empty ssDefinitionEntry.title}">
<span class="ss_largestprint ss_bold"><c:out value="${ssDefinitionEntry.name}"/></span>
</c:if>
<c:if test="${!empty ssDefinitionEntry.title}">
<span class="ss_largestprint ss_bold"><c:out value="${ssDefinitionEntry.title}"/></span> 
<span class="ss_normalprint ss_light">(<c:out value="${ssDefinitionEntry.name}"/>)</span>
</c:if>
</div>

<table>
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
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" configJspStyle="${ssConfigJspStyle}" 
  processThisItem="false" />
</table>
<c:set var="ss_profile_element_display_style" value="" scope="request"/>

</td>
<td valign="bottom" valign="right" style="padding-left:40px;" nowrap>
<a href="<ssf:url 
    folderId="${ssDefinitionEntry.parentBinder.id}" 
    action="view_profile_entry"
    entryId="${ssDefinitionEntry.id}"/>">
<table cellspacing="0" cellpadding="0">
<tr>
<td>
  <span class="ss_light ss_smallprint"><ssf:nlt tag="profile.viewProfile"/></span>
</td>
<td> <img src="<html:imagesPath/>pics/sym_s_arrow_right.gif"></td>
</tr>
</table>
</a>
</td>
</tr>
</table>
