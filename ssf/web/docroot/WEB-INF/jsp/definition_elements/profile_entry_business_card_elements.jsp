<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>
<c:if test="${ssUser.id == ssDefinitionEntry.id}">
    <c:set var="scopeBusinessCard" value="mine"/>
    <c:if test="${!empty ssUserProperties.businessCardShow_mine && !ssUserProperties.businessCardShow_mine}">
        <c:set var="showBusinessCard" value="no"/>
    </c:if>
    <c:if test="${empty ssUserProperties.businessCardShow_mine || ssUserProperties.businessCardShow_mine}">
        <c:set var="showBusinessCard" value="yes"/>
    </c:if>
</c:if>
<c:if test="${ssUser.id != ssDefinitionEntry.id}">
    <c:set var="scopeBusinessCard" value="other"/>
    <c:if test="${!empty ssUserProperties.businessCardShow_other && !ssUserProperties.businessCardShow_other}">
        <c:set var="showBusinessCard" value="no"/>
    </c:if>
    <c:if test="${empty ssUserProperties.businessCardShow_other || ssUserProperties.businessCardShow_other}">
        <c:set var="showBusinessCard" value="yes"/>
    </c:if>
</c:if>
<div class="ss_content_rule" style="margin-bottom: 10px; margin-top: 5px;" align="right">
<a href="javascript:;" onClick="ss_showHideBusinessCard('show','${scopeBusinessCard}');">[+]</a>
<a href="javascript:;" onClick="ss_showHideBusinessCard('hide','${scopeBusinessCard}');">[-]</a>
</div>
<c:if test="${showBusinessCard == 'no'}"><div id="ss_largeBusinessCard" style="display:none;"></c:if>
<c:if test="${showBusinessCard == 'yes'}"><div id="ss_largeBusinessCard" style="display:block;"></c:if>
<c:if test="${empty property_maxWidth}">
  <c:set var="property_maxWidth" value="200" scope="request"/>
</c:if>
<c:if test="${empty property_maxHeight}">
  <c:set var="property_maxHeight" value="200" scope="request"/>
</c:if>
<table style="width: 600px; padding-left: 20px;">
<tr>
<td valign="top" style="width:${property_maxWidth + 30}px;" >
<div class="ss_smallRBoxTop2 ss_profileBox1"></div><div class="ss_smallRBoxTop1 ss_profileBox1"></div>
<div class="ss_profileBox1" style="padding: 10px;"><div style="height:${property_maxHeight + 10}px;">
 <div class="ss_profile_matte">
<c:if test="${empty ssDefinitionEntry.customAttributes['picture']}">
  <div class="ss_profile_picture_frame ss_profile_photo_box_empty">
</c:if>
<c:if test="${!empty ssDefinitionEntry.customAttributes['picture']}">
  <div class="ss_profile_picture_frame">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
<c:set var="pictureCount" value="0"/>
<c:forEach var="selection" items="${selections}">
  <c:if test="${pictureCount == 0}">
	<a href="javascript:;" onClick="ss_showThisImage(this);return false;"><img 
	  align="middle" id="ss_profilePicture<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>"
	  border="0" 
	  src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}"
	    entityType="${ssDefinitionEntry.entityType}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="viewType" value="scaled"/>
    	<ssf:param name="fileTime" value="${selection.modification.date.time}"/>
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

<td valign="top" align="center">
<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>

<div class="ss_entryContent">
  <div id="ss_presenceOptions1_${renderResponse.namespace}"></div>
  <ssf:presenceInfo user="${ssDefinitionEntry}" 
      showOptionsInline="false" 
      optionsDivId="ss_presenceOptions1_${renderResponse.namespace}"/>
<c:if test="${empty ssDefinitionEntry.title}">
<span style="font-size: 18px;"><c:out value="${ssDefinitionEntry.name}"/></span>
</c:if>
<c:if test="${!empty ssDefinitionEntry.title}">
<span style="font-size: 18px;"><c:out value="${ssDefinitionEntry.title}"/></span> 
<span class="ss_normalprint ss_light">(<c:out value="${ssDefinitionEntry.name}"/>)</span>
</c:if>
</div>

 <table class="ss_transparent" style="border-spacing: 10px 2px;">

<c:forEach var="element" items="${propertyValues__elements}">
 <c:if test="${element != 'name' && element != 'title'}">
 <tr>
  <td valign="top" align="right">
   <span class="ss_light"><ssf:nlt tag="profile.element.${element}"/></span>
  </td>
  <td valign="top" align="left">
   <c:if test="${!empty ssDefinitionEntry[element]}">
    <span class="ss_bold">
    <c:if test="${element == 'emailAddress'}">
        <a href="mailto:${ssDefinitionEntry[element]}">
    </c:if>    
    <c:out value="${ssDefinitionEntry[element]}"/>
    <c:if test="${element == 'emailAddress'}"></a></c:if>
    </span>
   </c:if>
   <c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
    <span class="ss_bold"><c:out value="${ssDefinitionEntry[element]}"/></span>
   </c:if>
  </td>
 </tr>
 </c:if>
</c:forEach>

  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
       configElement="<%= item %>" 
       configJspStyle="${ssConfigJspStyle}" />

 
 </table>


<c:if test="${pictureCount > 1}">
<table width="99%">
<tr>
<td align="left">
  <div class="ss_thumbnail_gallery ss_thumbnail_small_no_text">
  <c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
  <c:forEach var="selection" items="${selections}">
	<div><a href="javascript:;" onClick="ss_showThisImage(this);return false;"
	  onMouseover="ss_showProfileImg(this, 'ss_profilePicture<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>'); return false;">
	<img <ssf:alt text="${selection.fileItem.name}"/> border="0" src="<ssf:url 
	    webPath="viewFile"
	    folderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}"
	    entityType="${ssDefinitionEntry.entityType}" >
	    <ssf:param name="fileId" value="${selection.id}"/>
	    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
	    <ssf:param name="viewType" value="thumbnail"/>
	    </ssf:url>" /></a></div>
  </c:forEach>
  </div>
</td>
</tr>
</table>
</c:if>

<c:set var="ss_element_display_style" value="" scope="request"/>
</td>
</tr>
</table>

</div>
<c:if test="${showBusinessCard == 'no'}"><div id="ss_smallBusinessCard" style="display:block;"></c:if>
<c:if test="${showBusinessCard == 'yes'}"><div id="ss_smallBusinessCard" style="display:none;"></c:if>
 <div class="ss_entryContent">
  <div id="ss_presenceOptions2_${renderResponse.namespace}"></div>
  <ssf:presenceInfo user="${ssDefinitionEntry}" 
      showOptionsInline="false" 
      optionsDivId="ss_presenceOptions2_${renderResponse.namespace}"/>

 <c:if test="${empty ssDefinitionEntry.title}">
 <span style="font-size: 18px;"><c:out value="${ssDefinitionEntry.name}"/></span>
 </c:if>
 <c:if test="${!empty ssDefinitionEntry.title}">
 <span style="font-size: 18px;"><c:out value="${ssDefinitionEntry.title}"/></span> 
 <span class="ss_normalprint ss_light">(<c:out value="${ssDefinitionEntry.name}"/>)</span>
 </c:if>
 </div>
</div>