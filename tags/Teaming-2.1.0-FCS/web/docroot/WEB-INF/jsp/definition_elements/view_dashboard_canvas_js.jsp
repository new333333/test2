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
<% //View dashboard canvas (javascript) %>
<script type="text/javascript">
//Initialize the variables only once
if (!ss_dbrn) {
	var ss_dbrn = Math.round(Math.random()*999999)
	var ss_componentTextHide = "<ssf:nlt tag="toolbar.hideDashboard"/>"
	var ss_componentTextShow = "<ssf:nlt tag="toolbar.showDashboard"/>"
	var ss_componentSrcHide = "<html:imagesPath/>icons/accessory_hide.gif"
	var ss_componentSrcShow = "<html:imagesPath/>icons/accessory_show.gif"
	var ss_componentAltHide = "<ssf:nlt tag="button.hide"/>"
	var ss_componentAltShow = "<ssf:nlt tag="button.show"/>"
	var ss_toolbarAddContent = "<ssf:nlt tag="dashboard.addContent"/>"
	var ss_toolbarHideContent = "<ssf:nlt tag="dashboard.addContentOff"/>"
	var ss_toolbarShowControls = "<ssf:nlt tag="dashboard.showHiddenControls"/>"
	var ss_toolbarHideControls = "<ssf:nlt tag="dashboard.showHiddenControlsOff"/>"
	var ss_dashboardConfirmDelete = "<ssf:nlt tag="dashboard.confirmDelete"/>";
	var ss_dashboardConfirmDeleteLocal = "<ssf:nlt tag="dashboard.confirmDeleteLocal"/>";
	var ss_dashboardConfirmDeleteGlobal = "<ssf:nlt tag="dashboard.confirmDeleteGlobal"/>";
	var ss_dashboardConfirmDeleteBinder = "<ssf:nlt tag="dashboard.confirmDeleteBinder"/>";
	var ss_dashboardConfirmDeleteUnknown = "<ssf:nlt tag="dashboard.confirmDeleteUnknown"/>";


}
var ${renderResponse.namespace}_dashboard_control_count = 0;
var ${renderResponse.namespace}_dashboard_border_count = 0;
var ${renderResponse.namespace}_dashboard_border_classNames = new Array();
var ${renderResponse.namespace}_toolbar_count = 0;

</script>
