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
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Iterator" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<form method="post" action="/remoteapp/wstester/submit">
<input type="radio" name="operation" value="search_search" checked/> Search<br/>
<div style="padding-left:50px;">
  Query<br/>
  <textarea name="query" rows="8" cols="60"></textarea>
  <br/>
  <input type="text" size="4" name="startCount"/> Starting point<br/>
  <input type="text" size="4" name="maxCount"/> Max number to return<br/>
  <br/>
</div>
<br/>

<input type="radio" name="operation" value="folder_getFolderEntryAsXML"/> Get folder entry<br/>
<div style="padding-left:50px;">
  Binder Id <input type="text" size="20" name="binderId_getFolderEntry"/><br/>
  Entry Id <input type="text" size="20" name="entryId_getFolderEntry"/><br/>
  <input type="radio" name="includeAttachments" value="true"/>True&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input 
    type="radio" name="includeAttachments_getFolderEntry" value="false"/>False<br/>
  <br/>
</div>
<br/>

<input type="radio" name="operation" value="folder_addFolderEntry"/> Add folder entry<br/>
<div style="padding-left:50px;">
  Binder Id <input type="text" size="20" name="binderId_addFolderEntry"/><br/>
  Definition Id <input type="text" size="40" name="definitionId_addFolderEntry" value="402883b90cc53079010cc539bf260002"/><br/>
  Entry XML<br/>
  <textarea name="entryXml" rows="8" cols="60"></textarea>
  <br/>
</div>
<br/>


<br/>
<input type="submit" name="okBtn" value="OK"/>
<input type="hidden" name="ss_access_token" value="${ss_access_token}"/>

</form>
