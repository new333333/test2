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
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${ssFolder.entityType == 'folder'}">
	<div id="ss_div_folder_dropbox${ssFolder.id}${renderResponse.namespace}" 
	  class="ss_border_light" style="visibility:hidden;display:none;background-color:#fff;width:500px;">
		<div align="right">
			<a onClick="ss_hideFolderAddAttachmentDropbox('${renderResponse.namespace}','${ssFolder.id}'); return false;">
			   <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/> />
			</a>
		</div>
		<iframe frameborder="0" scrolling="auto" 
		    id="ss_iframe_folder_dropbox${ssFolder.id}${renderResponse.namespace}" 
		    name="ss_iframe_folder_dropbox${ssFolder.id}${renderResponse.namespace}"
		    title="<ssf:nlt tag="toolbar.menu.dropBox.title"/>" 
		    src="<html:rootPath/>js/forum/null.html" 
		    height="80%" width="96%" style="padding:6px;">Micro Focus Vibe</iframe>
	    <div align="right">
		  <span class="ss_fineprint"><ssf:nlt tag="file.upload.having_trouble"/></span>
		  <ssf:showHelp guideName="user" pageId="trouble" sectionId="trouble_fileupload" />
	    </div>
	</div>
</c:if>
