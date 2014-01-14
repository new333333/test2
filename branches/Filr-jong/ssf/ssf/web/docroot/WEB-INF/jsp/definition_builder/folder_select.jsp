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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div>
<span class="ss_bold"><ssf:nlt tag="definition.selectDestinationFolder"/></span>
<input type="hidden" id="propertyId_${propertyId}" 
  name="propertyId_${propertyId}"
  value="${propertyValue}" />
<table cellspacing="0" cellpadding="0" width="100%">
  <tbody>
	<tr>
		<td valign="top">
			<iframe frameborder="0" style="height:230px;" id="ss_folderSelectIframe" src="<ssf:url 
			    adapter="true" 
			    actionUrl="true"
			    portletName="ss_forum" 
			    action="__ajax_request">
				  <ssf:param name="operation" value="find_place_form" />
				  <ssf:param name="binderId" value="${ssBinderId}" />
				  <ssf:param name="propertyId" value="propertyId_${propertyId}" />
			    </ssf:url>"
			    title="<ssf:nlt tag="definition.selectDestinationFolder"/>">Novell Vibe</iframe> 
		</td>
	</tr>
  </tbody>
</table>
<br/>
<span class="ss_labelLeft"><ssf:nlt tag="definition.selected_folder"/></span>
<span id="propertyId_${propertyId}_title">${binderTitle}</span>
</div>
