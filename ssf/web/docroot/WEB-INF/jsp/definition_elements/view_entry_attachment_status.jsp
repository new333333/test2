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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_divCounter" value="${ss_divCounter + 1}" scope="request" />
<jsp:useBean id="ss_attachedFile" type="org.kablink.teaming.domain.FileAttachment" scope="request"/>

 <c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
  <div>
    <a href="javascript: ;" onClick="ss_showHideMenuDiv('ss_fileStatusMenu${ss_divCounter}_${ss_attachedFile.id}');return false;"
    ><span id="fileStatus${ss_divCounter}_${ss_attachedFile.id}">
      <c:if test="${ss_attachedFile.fileStatus != 0}">${ss_attachedFile.fileStatusText}</c:if>
      <c:if test="${ss_attachedFile.fileStatus == 0}"><ssf:nlt tag="file.statusNoStatus"/></c:if>
      </span><img style="vertical-align: bottom;" src="<html:rootPath/>images/pics/menu_arrow.png"/></a>
  </div>
  <div id="ss_fileStatusMenu${ss_divCounter}_${ss_attachedFile.id}" 
    style="position:absolute; display:none; background:#fff; border:1px #ccc solid;">
    <div><span class="ss_bold"><ssf:nlt tag="file.setStatus"/></span></div>
    <ul style="margin:0px;padding:0px 10px 0px 10px;">
	  <li>
	    <a href="javascript: ;" 
	      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${ss_attachedFile.id}', '${ss_divCounter}', '0');return false;">
	      <ssf:nlt tag="file.statusNone"/>
	    </a>
	  </li>
	  <li>
	    <a href="javascript: ;" 
	      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${ss_attachedFile.id}', '${ss_divCounter}', '1');return false;">
	      <ssf:nlt tag="file.status1"/>
	    </a>
	  </li>
	  <li>
	    <a href="javascript: ;" 
	      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${ss_attachedFile.id}', '${ss_divCounter}', '2');return false;">
	      <ssf:nlt tag="file.status2"/>
	    </a>
	  </li>
	  <li>
	    <a href="javascript: ;" 
	      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${ss_attachedFile.id}', '${ss_divCounter}', '3');return false;">
	      <ssf:nlt tag="file.status3"/>
	    </a>
	  </li>
	</ul>
  </div>
 </c:if>
 <c:if test="${!ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
  <div>
    <span>${ss_attachedFile.fileStatusText}</span>
  </div>
 </c:if>
