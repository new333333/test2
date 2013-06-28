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
<% //conditional elements %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<c:set var="licenseVibe" value="false"/>
<c:set var="licenseFilr" value="false"/>
<c:set var="licenseFilrAndVibe" value="false"/>
<c:set var="licenseGuestAccess" value="false"/>
<c:set var="licenseExternalUsers" value="false"/>
<c:set var="licenseKablink" value="false"/>
<c:forEach var="license" items="${propertyValues_license}">
  <c:if test="${ license == 'vibe' }"><c:set var="licenseVibe" value="true"/></c:if>
  <c:if test="${ license == 'filr' }"><c:set var="licenseFilr" value="true"/></c:if>
  <c:if test="${ license == 'filrAndVibe' }"><c:set var="licenseFilrAndVibe" value="true"/></c:if>
  <c:if test="${ license == 'guestAccess' }"><c:set var="licenseGuestAccess" value="true"/></c:if>
  <c:if test="${ license == 'externalUsers' }"><c:set var="licenseExternalUsers" value="true"/></c:if>
  <c:if test="${ license == 'kablink' }"><c:set var="licenseKablink" value="true"/></c:if>
</c:forEach>

<c:set var="showIt" value="false"/>
<c:if test="${ licenseVibe == 'true' }">
  <ssf:ifAuthorizedByLicense featureName="com.novell.teaming.Vibe">
  	<c:set var="showIt" value="true"/>
  </ssf:ifAuthorizedByLicense>
</c:if>
<c:if test="${ licenseFilr == 'true' }">
  <ssf:ifAuthorizedByLicense featureName="com.novell.teaming.Filr">
    <ssf:ifNotAuthorizedByLicense featureName="com.novell.teaming.Vibe">
  	  <c:set var="showIt" value="true"/>
  	</ssf:ifNotAuthorizedByLicense>
  </ssf:ifAuthorizedByLicense>
</c:if>
<c:if test="${ licenseFilrAndVibe == 'true' }">
  <ssf:ifAuthorizedByLicense featureName="com.novell.teaming.Filr">
    <ssf:ifAuthorizedByLicense featureName="com.novell.teaming.Vibe">
  	  <c:set var="showIt" value="true"/>
  	</ssf:ifAuthorizedByLicense>
  </ssf:ifAuthorizedByLicense>
</c:if>
<c:if test="${ licenseGuestAccess == 'true' }">
  <ssf:ifAuthorizedByLicense featureName="com.novell.teaming.GuestAccess">
  	<c:set var="showIt" value="true"/>
  </ssf:ifAuthorizedByLicense>
</c:if>
<c:if test="${ licenseExternalUsers == 'true' }">
  <ssf:ifAuthorizedByLicense featureName="com.novell.teaming.ExtUsers">
  	<c:set var="showIt" value="true"/>
  </ssf:ifAuthorizedByLicense>
</c:if>
<c:if test="${ licenseKablink == 'true' }">
  <ssf:ifNotAuthorizedByLicense featureName="com.novell.teaming.Vibe">
    <ssf:ifNotAuthorizedByLicense featureName="com.novell.teaming.Filr">
  	  <c:set var="showIt" value="true"/>
  	</ssf:ifNotAuthorizedByLicense>
  </ssf:ifNotAuthorizedByLicense>
</c:if>

<c:if test="${ showIt == 'true' }">
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="<%= item %>" 
	  configJspStyle="${ssConfigJspStyle}" 
	  entry="${ssDefinitionEntry}" 
	  />
</c:if>
