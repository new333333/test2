<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>
<c:if test="${empty property_maxWidth}">
  <c:set var="property_maxWidth" value="200" scope="request"/>
</c:if>
<c:if test="${empty property_maxHeight}">
  <c:set var="property_maxHeight" value="200" scope="request"/>
</c:if>
<table width="100%">
<tr>
<td valign="top" style="width:${property_maxWidth + 30}px; >
<div class="ss_smallRBoxTop2 ss_profileBox1"></div><div class="ss_smallRBoxTop1 ss_profileBox1"></div>
<div class="ss_profileBox1" style="padding: 10px;"><div style="height:${property_maxHeight + 25}px;">
 <div class="ss_profile_box_title">Photo</div>
 <div class="ss_profile_matte">
  <div class="ss_profile_picture_frame ss_profile_photo_box">
<c:if test="${!empty ssDefinitionEntry.customAttributes['picture']}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
<c:set var="pictureCount" value="0"/>
<c:forEach var="selection" items="${selections}">
  <c:if test="${pictureCount == 0}">
	<a href="#" onClick="ss_showThisImage(this);return false;"><img 
	  align="middle" id="ss_profilePicture<portlet:namespace/>"
	  border="0" 
	  src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="viewType" value="scaled"/>
	    </ssf:url>" alt="${property_caption}" /></a>
  </c:if>
  <c:set var="pictureCount" value="${pictureCount + 1}"/>
</c:forEach>
</c:if>
  </div>
 </div>
</div></div>
</div><div class="ss_smallRBoxBtm1 ss_profileBox1"></div><div class="ss_smallRBoxBtm2 ss_profileBox1"></div>
</td>

<td valign="top">
<div class="ss_smallRBoxTop2 ss_profileBox2"></div><div class="ss_smallRBoxTop1 ss_profileBox2"></div>
<div class="ss_profileBox2" style="padding: 10px;"><div class="ss_profileBox2" style="height:${property_maxHeight + 25}px;">
 <div class="ss_profile_box_title">Info</div>
 <div class="ss_profile_matte">
  <div class="ss_profile_info_frame  ss_profile_info_box">
<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>
  <table class="ss_transparent">

<c:forEach var="element" items="${propertyValues__elements}">

 <tr>
 <td align="right" class="ss_table_spacer_right">
  <span><ssf:nlt tag="profile.element.${element}"/></span>
 </td>
 <td>
  <c:if test="${!empty ssDefinitionEntry[element]}">
    <span class="ss_bold"><c:out value="${ssDefinitionEntry[element]}"/></span>
  </c:if>
  <c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
    <span class="ss_bold"><c:out value="${ssDefinitionEntry[element]}"/></span>
  </c:if>
 </td>
 </tr>
</c:forEach>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />

 </table>
<c:set var="ss_element_display_style" value="" scope="request"/>
  </div>
 </div>
</div></div>
<div class="ss_smallRBoxBtm1 ss_profileBox2"></div><div class="ss_smallRBoxBtm2 ss_profileBox2"></div>
</td>


<td valign="top">
<div class="ss_smallRBoxTop2 ss_profileBox2"></div><div class="ss_smallRBoxTop1 ss_profileBox2"></div>
<div class="ss_profileBox2" style="padding: 10px;"><div class="ss_profileBox2" style="height:${property_maxHeight + 25}px;">
 <div class="ss_profile_box_title">Contact</div>
 <div class="ss_profile_matte">
  <div class="ss_profile_info_frame ss_profile_contact_box">
    <div id="ss_presenceOptions_${renderResponse.namespace}"></div>
    <ssf:presenceInfo user="${ssDefinitionEntry}" 
     showOptionsInline="true" 
     optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
  </div>
  </div>
 </div>
</div></div>
</div><div class="ss_smallRBoxBtm1 ss_profileBox2"></div><div class="ss_smallRBoxBtm2 ss_profileBox2"></div>
</td>
</tr>
</table>

<c:if test="${pictureCount > 1}">
<table width="100%">
<tr>
<td align="left">
<div class="ss_smallRBoxTop2 ss_profileBox1"></div><div class="ss_smallRBoxTop1 ss_profileBox1"></div><div class="ss_profileBox1" style="height:50px; padding: 3px;">
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
</div><div class="ss_smallRBoxBtm1 ss_profileBox1"></div><div class="ss_smallRBoxBtm2 ss_profileBox1"></div>
</td>
</tr>
</table>
</c:if>
