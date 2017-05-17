/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * <p/>
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * <p/>
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * <p/>
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * <p/>
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * <p/>
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.rest.v1.provider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

import org.kablink.teaming.rest.v1.model.*;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import org.kablink.teaming.rest.v1.model.admin.*;

/**
 * @author jong
 *
 */
@Provider
public class DefaultJAXBContextResolver implements ContextResolver<JAXBContext> {

    private final JAXBContext context;

    private final Set<Class> types;

    public static final Class[] cTypes = {
            Access.class,
            ApplicationBrief.class,
            ApplicationGroupBrief.class,
            AverageRating.class,
            Binder.class,
            BinderBrief.class,
            BinderChange.class,
            BinderChanges.class,
            BinderChildren.class,
            BinderQuotasConfig.class,
            BinderTree.class,
            CustomField.class,
            DefinitionBrief.class,
            Description.class,
            DesktopAppConfig.class,
            DesktopAppProcessConfig.class,
            DiskQuotasConfig.class,
            EntityId.class,
            ErrorInfo.class,
            FileBrief.class,
            FileChange.class,
            FileProperties.class,
            FileVersionProperties.class,
            Folder.class,
            FolderEntry.class,
            FolderEntryBrief.class,
            FolderEntryChange.class,
            Group.class,
            GroupBrief.class,
            GroupMember.class,
            HistoryStamp.class,
            LegacyFileProperties.class,
            LegacyHistoryStamp.class,
            LibraryInfo.class,
            Link.class,
            Locale.class,
            LongIdLinkPair.class,
            MobileAppConfig.class,
            MobileDevice.class,
            NetFolderBrief.class,
            NotifyWarning.class,
            Operation.class,
            ParentBinder.class,
            Permission.class,
            PrincipalBrief.class,
            RecentActivityEntry.class,
            ReleaseInfo.class,
            Reply.class,
            ReplyBrief.class,
            RootRestObject.class,
            SearchResultList.class,
            SearchResultTree.class,
            SearchResultTreeNode.class,
            Share.class,
            SharedBinderBrief.class,
            SharedFileProperties.class,
            SharedFolderEntryBrief.class,
            ShareRecipient.class,
            StringIdLinkPair.class,
            Tag.class,
            TeamBrief.class,
            TeamMember.class,
            TemplateBrief.class,
            User.class,
            UserBrief.class,
            UserQuota.class,
            Workspace.class,
            ZoneConfig.class,

            // Admin model objects
            AssignedSharingPermission.class,
            DesktopAppAdminConfig.class,
            DesktopProcessConfig.class,
            ExternalSharingRestrictions.class,
            GroupSynchronization.class,
            KeyValuePair.class,
            LdapHomeDirConfig.class,
            LdapSearchInfo.class,
            LdapSyncResults.class,
            LdapUserSource.class,
            NetFolder.class,
            NetFolderAccess.class,
            NetFolderAssignedRight.class,
            NetFolderServer.class,
            NetFolderSyncStatus.class,
            PersonalStorage.class,
            ProcessInfo.class,
            RootAdminObject.class,
            Schedule.class,
            SelectedDays.class,
            ShareSettings.class,
            SystemSharingPermission.class,
            Time.class,
            UserSynchronization.class,
            UserSourceSynchronization.class,
            WebAppConfig.class,
    };

    public DefaultJAXBContextResolver() throws Exception {
        this.types = new HashSet<Class>();
        addModelClasses(types);
        this.context = new JSONJAXBContext(JSONConfiguration.natural().usePrefixesAtNaturalAttributes().build(), types.toArray(new Class[0]));
    }

    public JAXBContext getContext(Class<?> objectType) {
        return (types.contains(objectType)) ? context : null;
    }

    protected void addModelClasses(Set<Class> types) {
        types.addAll(Arrays.asList(cTypes));
    }

}
