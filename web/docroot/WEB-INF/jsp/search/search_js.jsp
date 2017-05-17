<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>
<script type="text/javascript">
dojo.require("dijit.form.DateTextBox");
dojo.require("dijit.form.TimeTextBox");
</script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_search.js"></script>
<script type="text/javascript">

var ss_nlt_searchFormLabelWorkflow = "<ssf:nlt tag="searchForm.label.workflow"/>";
var ss_nlt_searchFormLabelWorkflowState = "<ssf:nlt tag="searchForm.label.workflowState"/>";
var ss_nlt_searchFormLabelEntry = "<ssf:nlt tag="searchForm.label.entry"/>";
var ss_nlt_searchFormLabelFieldName = "<ssf:nlt tag="searchForm.label.fieldName"/>";
var ss_nlt_searchFormLabelFieldValue = "<ssf:nlt tag="searchForm.label.fieldValue"/>";
var ss_nlt_tagsCommunityTags = "<ssf:nlt tag="tags.communityTags"/>";
var ss_nlt_tagsPersonalTags = "<ssf:nlt tag="tags.personalTags"/>";
var ss_searchFormLabelAuthor = "<ssf:nlt tag="searchForm.label.author"/>";
var ss_searchFormLabelDate = "<ssf:nlt tag="searchForm.label.date"/>";
var ss_searchFormLabelLastActivity = "<ssf:nlt tag="searchForm.label.lastActivity"/>";
var ss_AdvancedSearchURLNoOperation = "<ssf:url action="advanced_search" actionUrl="true"><ssf:param 
		name="tabId" value="${tabId}"/></ssf:url>";
var ss_AdvancedSearchURL = ss_AdvancedSearchURLNoOperation + "&operation=viewPage";
var ss_currentTabId = "${tabId}";

var ss_overwriteQuestion = "<ssf:nlt tag="search.save.overwriteQuestion"/>";
var ss_noNameMsg="<ssf:nlt tag="search.save.noName"/>";
var ss_invalidNameMsg="<ssf:nlt tag="search.save.invalidName"/>";
var ss_days_0 = "<ssf:nlt tag="searchForm.lastActivity.0"/>"
var ss_days_1 = "<ssf:nlt tag="searchForm.lastActivity.1"/>"
var ss_days_3 = "<ssf:nlt tag="searchForm.lastActivity.3"/>"
var ss_days_7 = "<ssf:nlt tag="searchForm.lastActivity.7"/>"
var ss_days_30 = "<ssf:nlt tag="searchForm.lastActivity.30"/>"
var ss_days_90 = "<ssf:nlt tag="searchForm.lastActivity.90"/>"
var ss_searchFormMoreOptionsShowLabel = "<ssf:nlt tag="searchForm.advanced.moreOptions"/>";
var ss_searchFormMoreOptionsHideLabel = "<ssf:nlt tag="searchForm.advanced.moreOptions.hide"/>";

var ss_searchResultSavedSearchInputLegend = "<ssf:nlt tag="searchResult.savedSearch.input.legend"/>";
var ss_weekStartsOn = "<%= CalendarHelper.getFirstDayOfWeek() - 1 %>"
</script>
