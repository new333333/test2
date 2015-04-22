/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.client.rest.v1;

import junit.framework.TestCase;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderChildren;
import org.kablink.teaming.rest.v1.model.ReleaseInfo;
import org.kablink.teaming.rest.v1.model.RootRestObject;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.ZoneConfig;

import java.util.List;

/**
 * User: David
 * Date: 10/28/14
 * Time: 2:06 PM
 */
public class PublicSharesRootTest extends TestCase {
    private ApiTestBinding binder;

    @Override
    protected void setUp() throws Exception {
        binder = new ApiTestBinding();
    }

    public PublicSharesRootTest() {
    }

    public void testWhenGuestEnabledThenGuestEnabled() {
        binder.whenGuestEnabled();
        binder.thenGuestEnabled();
    }

    public void testWhenGuestDisabledThenGuestDisabled() {
        binder.whenGuestDisabled();
        binder.thenGuestDisabled();
    }

    public void testWhenNoPublicSharesThenNoPublicShares() {
        binder.whenNoPublicShares();
        binder.thenNoPublicShares();
    }

    public void testWhenPublicShareThenPublicShares() {
        binder.whenGuestEnabled();
        binder.whenPublicShare();
        binder.thenPublicShares();
    }

    public void testWhenGuestEnabledAndPublicShareThenGetPublicSharesRootSucceeds() {
        binder.whenGuestEnabledAndPublicShare();
        binder.thenGetPublicSharesRootSucceeds();
    }

    public void testWhenGuestEnabledAndNoPublicSharesThenGetPublicSharesRootFails() {
        binder.whenGuestEnabledAndNoPublicShares();
        binder.thenGetPublicSharesRootFails();
    }

    public void testWhenGuestDisabledAndPublicShareThenGetPublicSharesRootSucceeds() {
        binder.whenGuestDisabledAndPublicShare();
        binder.thenGetPublicSharesRootSucceeds();
    }

    public void testWhenGuestDisabledAndNoPublicSharesThenGetPublicSharesRootFails() {
        binder.whenGuestDisabledAndNoPublicShares();
        binder.thenGetPublicSharesRootFails();
    }

    public void testWhenGuestEnabledAndPublicShareThenGetSelfHasPublicSharesLink() {
        binder.whenGuestEnabledAndPublicShare();
        binder.thenGetSelfHasPublicSharesLink();
    }

    public void testWhenGuestEnabledAndNoPublicSharesThenGetSelfDoesNotHavePublicSharesLink() {
        binder.whenGuestEnabledAndNoPublicShares();
        binder.thenGetSelfDoesNotHavePublicSharesLink();
    }

    public void testWhenGuestDisabledAndPublicShareThenGetSelfHasPublicSharesLink() {
        binder.whenGuestDisabledAndPublicShare();
        binder.thenGetSelfHasPublicSharesLink();
    }

    public void testWhenGuestDisabledAndNoPublicSharesThenGetSelfDoesNotHavePublicSharesLink() {
        binder.whenGuestDisabledAndNoPublicShares();
        binder.thenGetSelfDoesNotHavePublicSharesLink();
    }

    public void testWhenGuestEnabledAndPublicShareThenGetPublicSharesByIdSucceeds() {
        binder.whenGuestEnabledAndPublicShare();
        binder.thenGetPublicSharesByIdSucceeds();
    }

    public void testWhenGuestEnabledAndNoPublicSharesThenGetPublicSharesByIdReturnsNothing() {
        binder.whenGuestEnabledAndNoPublicShares();
        binder.thenGetPublicSharesByIdReturnsNothing();
    }

    public void testWhenGuestDisabledAndPublicShareThenGetPublicSharesByIdSucceeds() {
        binder.whenGuestDisabledAndPublicShare();
        binder.thenGetPublicSharesByIdSucceeds();
    }

    public void testWhenGuestDisabledAndNoPublicSharesThenGetPublicSharesByIdReturnsNothing() {
        binder.whenGuestDisabledAndNoPublicShares();
        binder.thenGetPublicSharesByIdReturnsNothing();
    }

    public void testWhenGuestEnabledAndPublicShareThenGetRootsIncludesPublicShares() {
        binder.whenGuestEnabledAndPublicShare();
        binder.thenGetRootsIncludesPublicShares();
    }

    public void testWhenGuestEnabledAndNoPublicSharesThenGetRootsDoesNotIncludePublicShares() {
        binder.whenGuestEnabledAndNoPublicShares();
        binder.thenGetRootsDoesNotIncludePublicShares();
    }

    public void testWhenGuestDisabledAndPublicShareThenGetRootsIncludesPublicShares() {
        binder.whenGuestDisabledAndPublicShare();
        binder.thenGetRootsIncludesPublicShares();
    }

    public void testWhenGuestDisabledAndNoPublicSharesThenGetRootsDoesNotIncludePublicShares() {
        binder.whenGuestDisabledAndNoPublicShares();
        binder.thenGetRootsDoesNotIncludePublicShares();
    }

    public static void testListChildren(Api api, AdminApi adminApi) {
        RootRestObject root = api.getRoot();
        ReleaseInfo releaseInfo = api.getReleaseInfo();
        User self = api.getSelf();
        ZoneConfig zc = api.getZoneConfig();

        Binder myFiles = api.getMyFiles();
        Binder netFolder = api.getNetFolders();
        Binder sharedByMe = api.getSharedByMe();
        Binder sharedWithMe = api.getSharedWithMe();

        SearchResultList<SearchableObject> children11 = api.listChildren(myFiles, 0, 1);
        SearchResultList<SearchableObject> children12 = api.listChildren(myFiles, 1, 1);
        SearchResultList<SearchableObject> children2 = api.listChildren(netFolder);
        SearchResultList<SearchableObject> children3 = api.listChildren(sharedByMe);
        SearchResultList<SearchableObject> children4 = api.listChildren(sharedWithMe);

        List<BinderChildren> childrenList1 = api.listBinderChildren(new Long[]{
                Api.MY_FILES_ID, Api.NET_FOLDERS_ID, Api.SHARED_BY_ME_ID, Api.SHARED_WITH_ME_ID, -99L
        }, 100);

        List<BinderChildren> childrenList2 = api.listBinderChildren(new Long[]{
                Api.MY_FILES_ID, Api.NET_FOLDERS_ID, Api.SHARED_BY_ME_ID, Api.SHARED_WITH_ME_ID
        }, Api.MY_FILES_ID, 4, 10);

        System.out.println();
    }
}
