<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<c:if test="${empty property_maxWidth}">
  <c:set var="property_maxWidth" value="150" scope="request"/>
</c:if>
<c:if test="${empty property_maxHeight}">
  <c:set var="property_maxHeight" value="100" scope="request"/>
</c:if>
<table width="150px">
<tr>
<td valign="top" width="${property_maxWidth + 4}">
<div style="width:${property_maxWidth + 4}px; height:${property_maxHeight + 4}px;">
<c:if test="${!empty ssDefinitionEntry.customAttributes['picture']}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
<c:set var="pictureCount" value="0"/>
<c:forEach var="selection" items="${selections}">
  <c:if test="${pictureCount == 0}">
	<a href="#" onClick="ss_showThisImage(this);return false;"><img 
	  id="ss_profilePicture<portlet:namespace/>"
	  border="0" 
	  src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="viewType" value="thumbnail"/>
	    </ssf:url>" alt="${property_caption}" /></a>
  </c:if>
  <c:set var="pictureCount" value="${pictureCount + 1}"/>
</c:forEach>
</c:if>
</div>
</td>

<td valign="top" style="padding-left:10px;">

<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>
<table>

<c:forEach var="element" items="${propertyValues__elements}">

	<c:if test="${!empty ssDefinitionEntry[element]}">
		<tr>
			<td>
				<c:if test="${element == 'name'}">
					  <div id="ss_presenceOptions_${renderResponse.namespace}"></div>
						  <ssf:presenceInfo user="${ssDefinitionEntry}" 
						    showOptionsInline="false" 
						    optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
									
				</c:if>
			    <span class="ss_bold"><c:out value="${ssDefinitionEntry[element]}"/></span>
			</td>
		</tr>
	</c:if>
	
	<c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
		<tr>
			<td>
			    <span class="ss_bold"><c:out value="${ssDefinitionEntry[element]}"/></span>
			</td>
		</tr>
	</c:if>

</c:forEach>

</table>
<c:set var="ss_element_display_style" value="" scope="request"/>

</td>
</tr>
</table>

<c:if test="${pictureCount > 1}">
<table>
<tr>
<td align="left">
  <div class="ss_thumbnail_gallery ss_thumbnail_small_no_text">
  <c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
  <c:forEach var="selection" items="${selections}">
	<div><a href="#" onClick="ss_showThisImage(this);return false;"
	  onMouseover="ss_showProfileImg(this, 'ss_profilePicture<portlet:namespace/>'); return false;">
	<img border="0" src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="viewType" value="scaled"/>
	    </ssf:url>" /></a></div>
  </c:forEach>
  </div>
</td>
</tr>
</table>
</c:if>

<%
//Get the form item being displayed
	request.removeAttribute("propertyValues__elements");
%>

