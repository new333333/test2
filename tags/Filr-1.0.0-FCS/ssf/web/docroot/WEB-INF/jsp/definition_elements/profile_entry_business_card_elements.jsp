<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% //Business card elements %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
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

<script type="text/javascript">
    function toggle_visibility(id) {
       <% if (BrowserSniffer.is_ie_6(request)) { %>
       		setTimeout("self.location.reload(true);", 200);
       <% } else { %>
			var one = document.getElementById('ss_profile_show');
			var two = document.getElementById('ss_profile_hide');
	       
			if (id == 'show') {
				ss_hideDivNone('ss_profile_hide')  
				ss_showDivObj(one)
			} else {
				ss_hideDivNone('ss_profile_show')
				ss_showDivObj(two)
			}
	   <% } %>
    }
</script>

<div class="ss_content_rule" style="margin-bottom: 10px; margin-top: 5px; padding-right: 5px;" align="right">

<div id="ss_profile_show"
  <c:choose>  
    <c:when test= "${showBusinessCard == 'yes'}"> style= "display: none;" </c:when>
    <c:otherwise> style="display: block;" </c:otherwise>
  </c:choose>
>
  <a href="javascript: ;" 
    onClick="ss_showHideBusinessCard('show','${scopeBusinessCard}');toggle_visibility('hide');return false;"
    title="<ssf:nlt tag="profile.page.toggleStyle"/>"
  ><img border="0" src="<html:imagesPath/>icons/profile_bizcard_full.gif"/></a>
</div>

<div id="ss_profile_hide"
  <c:choose>
    <c:when test= "${showBusinessCard == 'no'}"> style="display: none;" </c:when>
    <c:otherwise> style="display: block;" </c:otherwise>
  </c:choose>
>
  <a href="javascript: ;" 
    onClick="ss_showHideBusinessCard('hide','${scopeBusinessCard}');toggle_visibility('show');return false;"
    title="<ssf:nlt tag="profile.page.toggleStyle"/>"
  ><img border="0" src="<html:imagesPath/>icons/profile_bizcard_small.gif"/></a>
</div>

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
<div class="ss_profileBox1" style="padding: 1px;"><div style="height:${property_maxHeight + 10}px;">
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
	  align="middle" id="ss_profilePicture${renderResponse.namespace}"
	  border="0" 
	  src="<ssf:fileUrl webPath="readScaledFile" file="${selection}"/>"
		alt="${property_caption}" /></a>
  </c:if>
  <c:set var="pictureCount" value="${pictureCount + 1}"/>
</c:forEach>
</c:if>
  </div>
 </div>
</div>
</div><div class="ss_smallRBoxBtm1 ss_profileBox1"></div><div class="ss_smallRBoxBtm2 ss_profileBox1"></div>
</td>

<td valign="top" align="center">
<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>
<c:set var="ss_element_display_style_caption" value="ss_light" scope="request"/>
<c:set var="ss_element_display_style_item" value="ss_bold" scope="request"/>

 <table class="ss_transparent" style="border-spacing: 10px 2px;">
 <tr>
   <td valign="bottom" align="right">
	  <div class="ss_entryContent">
		  <div id="ss_presenceOptions1_${renderResponse.namespace}"></div>
		  <ssf:presenceInfo user="${ssDefinitionEntry}" 
		      optionsDivId="ss_presenceOptions1_${renderResponse.namespace}"/>
	  </div>
   </td>
   <td valign="bottom" align="left">
	  <div class="ss_entryContent">
		<c:if test="${empty ssDefinitionEntry.title}">
		  <span style="font-size: 18px;"><c:out value="${ssDefinitionEntry.name}" escapeXml="true"/></span>
		</c:if>
		<c:if test="${!empty ssDefinitionEntry.title}">
		  <span style="font-size: 18px;"><ssf:userTitle user="${ssDefinitionEntry}" /></span> 
		  <span class="ss_normalprint ss_light">(<c:out value="${ssDefinitionEntry.name}" escapeXml="true"/>)</span>
		</c:if>
	  </div>
   </td>
 </tr>

<c:forEach var="element" items="${propertyValues__elements}">
 <c:if test="${element != 'name' && element != 'title'}">
 <tr>
  <td valign="top" align="right">
   <span class="${ss_element_display_style_caption}"><ssf:nlt tag="profile.element.${element}"/></span>
  </td>
  <td valign="top" align="left">
   <c:if test="${!empty ssDefinitionEntry[element]}">
    <span class="${ss_element_display_style_item}">
	    <c:if test="${element == 'emailAddress' || element == 'mobileEmailAddress' || element == 'txtEmailAddress'}">
	        <ssf:mailto email="${ssDefinitionEntry[element]}"/>
	    </c:if>    
	    <c:if test="${element != 'emailAddress' && element != 'mobileEmailAddress' && element != 'txtEmailAddress'}">
	    	<c:out value="${ssDefinitionEntry[element]}" escapeXml="true"/>
		</c:if>
    </span>
   </c:if>
   <c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
    <span class="${ss_element_display_style_item}"><c:out value="${ssDefinitionEntry.customAttributes[element]}" escapeXml="true"/></span>
   </c:if>
  </td>
 </tr>
 </c:if>
</c:forEach>

  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
       configElement="<%= item %>" 
       configJspStyle="${ssConfigJspStyle}" 
       entry="${ssDefinitionEntry}" />

 
	<ssf:ifLoggedIn>
	<tr>
		<td align="right">
			<a href="javascript: ;" onClick="ss_viewMiniBlog('${ssDefinitionEntry.id}', '0', true);return false;"
			  style="text-decoration:underline;">
				<span class="ss_bold"><ssf:nlt tag="miniblog"/></span>
			</a>
		</td>
		<td align="left">
			<span id="ss_myStatusTitleDest" class="ss_smallprint">
			  <c:if test="${!empty ssDefinitionEntry.status}">
			    <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
			      value="${ssDefinitionEntry.statusDate}" type="both" 
			      timeStyle="short" dateStyle="short" />
			  </c:if>
			</span>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<span id="ss_myStatusTextDest" class="ss_normalprint ss_italic">
			  <c:if test="${!empty ssDefinitionEntry.status}">
			    <c:out value="${ssDefinitionEntry.status}" escapeXml="true"/>
			  </c:if>
			</span>
		</td>
	</tr>
	</ssf:ifLoggedIn>
</table>

<c:if test="${pictureCount > 1}">
<table width="99%">
<tr>
<td align="left">
  <div class="ss_thumbnail_gallery ss_thumbnail_small_no_text">
  <c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
  <c:forEach var="selection" items="${selections}">
	<div><a href="javascript:;" onClick="ss_showThisImage(this);return false;"
	  onMouseover="ss_showProfileImg(this, 'ss_profilePicture${renderResponse.namespace}'); return false;">
	<img <ssf:alt text="${selection.fileItem.name}"/> border="0" src="<ssf:fileUrl webPath="readThumbnail" file="${selection}"/>" />
	</a></div>
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
      optionsDivId="ss_presenceOptions2_${renderResponse.namespace}"/>

 <c:if test="${empty ssDefinitionEntry.title}">
 <span style="font-size: 18px;"><c:out value="${ssDefinitionEntry.name}" escapeXml="true"/></span>
 </c:if>
 <c:if test="${!empty ssDefinitionEntry.title}">
 <span style="font-size: 18px;"><ssf:userTitle user="${ssDefinitionEntry}" /></span> 
 <span class="ss_normalprint ss_light">(<c:out value="${ssDefinitionEntry.name}" escapeXml="true"/>)</span>
 </c:if>
<br/>
<span id="ss_myStatusTextDest2" class="ss_normalprint ss_italic">
  <c:if test="${!empty ssDefinitionEntry.status}">
    <c:out value="${ssDefinitionEntry.status}" escapeXml="true"/>
  </c:if>
</span>
 </div>
</div>
<script type="text/javascript">
ss_createOnLoadObj("ss_showEmailLinks", ss_showEmailLinks);
</script>
