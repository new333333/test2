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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
 <a href="javascript: ;" onClick="ss_toggleExpandableArea('<portlet:namespace/>customjsp', '<portlet:namespace/>customjspImg', 'wipe'); return false;">
 <img border="0" src="<html:imagesPath />pics/sym_s_expand.gif" id="<portlet:namespace/>customjspImg" name="<portlet:namespace/>customjspImg"/>
<ssf:nlt tag="__custom_jsp"/></a>

<div id="<portlet:namespace/>customjsp" style="visibility:hidden;display:none">
<table>
<c:if test="${empty jspElement}">

<c:if test="${jspFormOnly}">
<tr><td>	<ssf:nlt tag="__form_jsp_name"/><br/> <input type="text" size="40" name="jspName_form" value=""/></td></tr>
</c:if>
<c:if test="${!jspFormOnly}">
<tr><td><ssf:nlt tag="__view_jsp_name"/><br/> <input type="text" size="40" name="jspName_view" value=""/></td></tr>
<tr><td><ssf:nlt tag="__mail_jsp_name"/><br/> <input type="text" size="40" name="jspName_mail" value=""/></td></tr>
<tr><td><ssf:nlt tag="__mobile_jsp_name"/><br/> <input type="text" size="40" name="jspName_mobile" value=""/></td></tr>
</c:if>

</c:if>
<c:if test="${!empty jspElement}">
<jsp:useBean id="jspElement" type="org.dom4j.Element" scope="request" />
<% 
	org.dom4j.Element jsp =null;
%>

<c:if test="${jspFormOnly}">
<% 
		jsp = (org.dom4j.Element)jspElement.selectSingleNode("./jsp[@name='form']");
%>
<tr><td><ssf:nlt tag="__form_jsp_name"/><br/> <input type="text" size="40" name="jspName_form" value="<%= jsp==null?"":jsp.attributeValue("value") %>"/></td></tr>
</c:if>
<c:if test="${!jspFormOnly}">
<% 
		jsp = (org.dom4j.Element)jspElement.selectSingleNode("./jsp[@name='view']");
%>
<tr><td><ssf:nlt tag="__view_jsp_name"/><br/> <input type="text" size="40" name="jspName_view" value="<%= jsp==null?"":jsp.attributeValue("value") %>"/></td></tr>
<% 
		jsp = (org.dom4j.Element)jspElement.selectSingleNode("./jsp[@name='mail']");
%>
<tr><td><ssf:nlt tag="__mail_jsp_name"/><br/> <input type="text" size="40" name="jspName_mail" value="<%= jsp==null?"":jsp.attributeValue("value") %>"/></td></tr>
<% 
		jsp = (org.dom4j.Element)jspElement.selectSingleNode("./jsp[@name='mobile']");
%>
<tr><td><ssf:nlt tag="__mobile_jsp_name"/><br/> <input type="text" size="40" name="jspName_mobile" value="<%= jsp==null?"":jsp.attributeValue("value") %>"/></td></tr>
</c:if>

</c:if>
</table>
</div>

