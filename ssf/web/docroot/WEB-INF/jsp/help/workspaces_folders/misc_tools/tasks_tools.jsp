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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %> 
<div class="ss_style">
<div class="ss_help_style">

    <div class="ss_help_title"> <span class="ss_titlebold">View toolbar<ssf:nlt tag="helpSpot.tasksTools" /></span> 
    </div>

    <p>The View toolbar enables you to choose the tasks you want displayed in 
      the task list.<ssf:nlt tag="help.tasksTools.intro" /></p>

<p>You can sort tasks by the following categories:<ssf:nlt tag="help.tasksTools.tools.listIntro" /></p>

<ul>

      <li><b>Closed:</b> Displays tasks that have the status of Closed.<ssf:nlt tag="help.tasksTools.tools.listItem.closed"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></li>      
      <li><b>Today:</b> Displays tasks that have a due date of today.<ssf:nlt tag="help.tasksTools.tools.listItem.today" /></li>      
      <li><b>Week:</b> Displays tasks that have a due date sometime in the next 
        seven days.<ssf:nlt tag="help.tasksTools.tools.listItem.week" /></li>      
      <li><b>Month:</b> Displays tasks that have a due date sometime in the next 
        30 days.<ssf:nlt tag="help.tasksTools.tools.listItem.month" /></li>

      <li><b>All active:</b> Displays tasks that are currently active.<ssf:nlt tag="help.tasksTools.tools.listItem.allActive" /></li>

      <li><b>All entries:</b> Displays all tasks.<ssf:nlt tag="help.tasksTools.tools.listItem.allEntries" /></li>

</ul>

<p>For additional and customizable ways of sorting your tasks, use the Filter. Create new filters by clicking <i>New Filter</i>.</p>

</div>

</div>
