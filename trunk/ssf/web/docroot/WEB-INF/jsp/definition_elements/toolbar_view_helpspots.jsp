<%-- BEGIN Helpspots for folder menus --%><%--
        
        --%><c:choose><%--
        
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageFolderMenu'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_folder" offsetY="15" offsetX="0" <%--
                --%> title="<ssf:nlt tag="helpSpot.manageFolderMenu"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageWorkspaceMenu'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/manage_workspace" offsetY="15" offsetX="-15" <%--
                --%> title="<ssf:nlt tag="helpSpot.manageWorkspaceMenu"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.whatsNew'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/whats_new" offsetY="15" offsetX="0" <%--
                --%> title="<ssf:nlt tag="helpSpot.whatsNew"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.whatsUnread'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/menus_toolbars/whats_unread" offsetY="15" offsetX="0" <%--
                --%> title="<ssf:nlt tag="helpSpot.whatsUnread"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.modifyProfileButton'}"><%--
                --%><ssHelpSpot helpId="people/modify_profile" <%--
                --%> offsetY="10" offsetX="0" <%--
                --%> title="<ssf:nlt tag="helpSpot.modifyProfileButton"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--
            
            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageSubscriptionsMenu'}"><%--
                --%><ssHelpSpot helpId="/workspaces_folders/menus_toolbars/subscriptions" offsetY="15" offsetX="0" <%--
                --%> title="<ssf:nlt tag="helpSpot.subscriptionsMenu"/>"></ssHelpSpot><%--
            --%></c:when><%--

            --%><c:when test="${toolbarMenu.value.qualifiers.helpSpot == 'helpSpot.manageDashboard'}"><%--
                --%><ssHelpSpot helpId="workspaces_folders/misc_tools/manage_accessories"<%--
                --%> offsetX="0" <%--
                --%> offsetY="15" xAlignment="left" title="<ssf:nlt tag="helpSpot.manageDashboard"/>"><%--
                --%></ssHelpSpot><%--
            --%></c:when><%--

        --%></c:choose>
 