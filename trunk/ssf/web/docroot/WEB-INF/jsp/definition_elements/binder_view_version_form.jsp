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
<% //Binder view version form element %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry }">
<jsp:useBean id="ssDefinitionEntry" type="org.kablink.teaming.domain.DefinableEntity" scope="request" />
<c:set var="binderViewVersionItem" 
  value='<%= (Element) ssDefinitionEntry.getEntryDefDoc().getRootElement().selectSingleNode("//item[@name=\'binderViewVersion\']/properties/property[@name=\'binderViewVersion\']") %>'/>
<c:set var="defaultView" value=""/>
<jsp:useBean id="binderViewVersionItem" type="org.dom4j.Element" />
<c:if test="${!empty binderViewVersionItem}">
  <c:set var="defaultView" value='<%= binderViewVersionItem.attributeValue("value") %>'/>
</c:if>
<c:set var="radioButtonValue" value="${defaultView }"/>
<c:if test="${!empty ssDefinitionEntry.customAttributes['binderViewVersion'].value}">
  <c:set var="radioButtonValue" value="${ssDefinitionEntry.customAttributes['binderViewVersion'].value }"/>
</c:if>

<div class="ss_entryContent ss_form_element">
<table>
<tr>
<td valign="top"><div class="ss_bold"><ssf:nlt tag="__binderViewVersion"/></div></td>
<td valign="top" style="padding-left:10px;" >
  	<input type="radio" class="ss_text" name="binderViewVersion" value="gwt" 
  	  <c:if test="${radioButtonValue == 'gwt'}"> checked="checked" </c:if>
  	/>
	<ssf:nlt tag="__binderViewVersion.gwt"/>
</td>
<td valign="top" style="padding-left:10px;" >
  	<input type="radio" class="ss_text" name="binderViewVersion" value="jsp" 
  	  <c:if test="${radioButtonValue == 'jsp'}"> checked="checked" </c:if>
  	/>
	<ssf:nlt tag="__binderViewVersion.jsp"/>
</td>
</tr>
</table>
</div>
</c:if>
