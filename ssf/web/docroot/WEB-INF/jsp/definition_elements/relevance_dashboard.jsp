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
<% //Relevance dashboard %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div id="ss_profileTab" sytle="margin-top:10px;">
  <ul>
	<!-- CSS Tabs -->
	<li id="current"><a href="#"><span>My stuff</span></a></li>
	<li><a href="#"><span>My friends' stuff</span></a></li>
	<li><a href="#"><span>Everyone else's stuff</span></a></li>
	<li><a href="#"><span>Vistors</span></a></li>
  </ul>
</div>
<div class="ss_clear_float"></div>
<div style="margin:4px 10px 10px 10px;">
<table width="100%">
<tr>
<td width="50%" valign="top">
	<ssf:canvas id="relevanceDocuments" type="inline">
	<ssf:param name="title" value="<%= NLT.get("relevance.documents") %>"/>
	  Recent documents<br/>
	  Recent documents<br/>
	  Recent documents<br/>
	</ssf:canvas>
	<ssf:canvas id="relevanceTasks" type="inline">
	<ssf:param name="title" value="<%= NLT.get("relevance.tasks") %>"/>
	  Tasks<br/>
	  Tasks<br/>
	  Tasks<br/>
	  Tasks<br/>
	  Tasks<br/>
	</ssf:canvas>
</td>
<td width="50%" valign="top" style="padding-left:10px;">
	<ssf:canvas id="relevanceMail" type="inline">
	<ssf:param name="title" value="<%= NLT.get("relevance.email") %>"/>
		<iframe src="https://webacc.innerweb.novell.com/gw/webacc?merge=iwwebacc&gadget=Mail" 
		name="gwmail" title="gwmail" frameborder="0" scrolling="Auto" 
		width="100%" height="360">mail</iframe>	
	</ssf:canvas>
</td>
</tr>
</table>

</div>