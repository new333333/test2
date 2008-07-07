<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% //Binder attributes form %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">
  <span class="ss_labelRight">${property_caption}</span>
  <table border="1">
    <tr>
    <td valign="top">
      <table>
      <c:forEach var="attributeSet" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}">
        <tr>
          <td valign="top">
        	<input type="checkbox" name="${property_name}__delete__${attributeSet}" /> 
        	xxx Attribute set name = ${attributeSet}<br/>
        	<span style="padding-left:20px;">xxx multiple allowed</span>
        	<input type="checkbox" name="${property_name}__setMultipleAllowed__${attributeSet}" 
        	  <c:set var="attributeSetMA" value="${property_name}__setMultipleAllowed__${attributeSet}"/>
        	  <c:if test="${ssDefinitionEntry.customAttributes[attributeSetMA].value}">checked</c:if> /> 
        	<input type="hidden" name="${property_name}" value="${attributeSet}"/>
        	<br/>
        	<c:set var="attributes" value="${property_name}__set__${attributeSet}"/>
        	<c:forEach var="attribute" items="${ssDefinitionEntry.customAttributes[attributes].valueSet}">
        	  <input type="checkbox" name="${property_name}__delete__${attributeSet}__${attribute}" /> 
        	  ${attribute}
        	  <input type="hidden" name="${property_name}__set__${attributeSet}" value="${attribute}"/><br/>
        	</c:forEach>
        	<input type="text" name="${property_name}__set__${attributeSet}" size="40"/>
        	<input class="ss_submit" type="submit" name="applyBtn" value="<ssf:nlt tag="button.add"/>" />
          </td>
        </tr>
      </c:forEach>
      </table>
    <td valign="top">
      <span class="ss_labelAbove">xxx Add a new category xxx</span>
      <input type="text" name="${property_name}" size="40"/>
      <input class="ss_submit" type="submit" name="applyBtn" value="<ssf:nlt tag="button.add"/>" />
    </td>
    </tr>
  </table>
</div>
