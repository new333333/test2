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
<%-- BEGIN Helpspots for folder menus --%><%--
        
        --%><c:choose><%--
        
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageFolderMenu'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_folder" offsetY="12" offsetX="5" <%--
                --%> title="<ssf:nlt tag="helpSpot.manageFolderMenu"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageWorkspaceMenu'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_workspace" offsetY="12" offsetX="5" <%--
                --%> title="<ssf:nlt tag="helpSpot.manageWorkspaceMenu"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.whatsNew'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/whats_new" offsetY="12" offsetX="5" <%--
                --%> title="<ssf:nlt tag="helpSpot.whatsNew"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.whatsUnread'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/whats_unread" offsetY="12" offsetX="5" <%--
                --%> title="<ssf:nlt tag="helpSpot.whatsUnread"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.modifyProfileButton'}"><%--
                --%><ssHelpSpot helpId="people/modify_profile" <%--
                --%> offsetY="12" offsetX="5" <%--
                --%> title="<ssf:nlt tag="helpSpot.modifyProfileButton"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageSubscriptionsMenu'}"><%--
                --%><ssHelpSpot helpId="/workspaces_folders/menus_toolbars/subscriptions" offsetY="15" offsetX="0" <%--
                --%> title="<ssf:nlt tag="helpSpot.subscriptionsMenu"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageDashboard'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/misc_tools/manage_accessories"<%--
                --%> offsetX="-20" <%--
                --%> offsetY="-5" xAlignment="left" title="<ssf:nlt tag="helpSpot.manageDashboard"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--

        --%></c:choose>
 