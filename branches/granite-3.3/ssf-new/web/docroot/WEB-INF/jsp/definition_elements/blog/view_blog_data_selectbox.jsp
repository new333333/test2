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
<% //Selectbox view %>
<%@ page import="org.kablink.teaming.web.util.DefinitionHelper" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.search.SearchFieldResult" %>
<%
	java.lang.Object thisEntry = (java.lang.Object) request.getAttribute("ssDefinitionEntry");
	if (thisEntry instanceof FolderEntry) {
%>
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<ul class="ss_nobullet">
<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}" >
<%
	String caption = DefinitionHelper.findCaptionForValue(ssConfigDefinition, item,
											(String) pageContext.getAttribute("selection"));
	caption = NLT.getDef(caption);
%>
<c:set var="caption" value="<%= caption %>"/>
<li><c:out value="${caption}" escapeXml="false"/></span></li>
</c:forEach>
</ul>
</div>
<%
	} else if (thisEntry instanceof Map) {
		java.lang.Object valueObj = ((Map)thisEntry).get(property_name);
		SearchFieldResult valueSet = null;
		if (valueObj instanceof SearchFieldResult) {
			valueSet = (SearchFieldResult) ((Map)thisEntry).get(property_name);
		} else if (valueObj instanceof java.lang.String) {
			valueSet = new SearchFieldResult();
			valueSet.addValue((java.lang.String)valueObj);
		}
%>
<c:set var="selections" value="<%= valueSet %>"/>
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<ul class="ss_nobullet">
<%  if (valueSet != null) {  %>
<c:forEach var="selection" items="<%= valueSet.getValueSet() %>" >
<%
	String caption = DefinitionHelper.findCaptionForValue(ssConfigDefinition, item,
											(String) pageContext.getAttribute("selection"));
	caption = NLT.getDef(caption);
%>
<c:set var="caption" value="<%= caption %>"/>
<li><c:out value="${caption}" escapeXml="false"/></span></li>
</c:forEach>
<%  }  %>
</ul>
</div>
<%
	}
%>
