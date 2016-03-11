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

import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.BinderChildren;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.ReleaseInfo;
import org.kablink.teaming.rest.v1.model.RootRestObject;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.ZoneConfig;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jong
 *
 */
public interface Api {
    public static final Long MY_FILES_ID = Long.valueOf(-100);
    // Reserved id used by the REST API for the "Shared With Me" virtual binder
    public static final Long SHARED_WITH_ME_ID = Long.valueOf(-101);
    // Reserved id used by the REST API for the "Shared By Me" virtual binder
    public static final Long SHARED_BY_ME_ID = Long.valueOf(-102);
    // Reserved id used by the REST API for the "Net Folders" virtual binder
    public static final Long NET_FOLDERS_ID = Long.valueOf(-103);
    // Reserved id used by the REST API for the "Public" virtual binder
    public static final Long PUBLIC_SHARES_ID = Long.valueOf(-104);

    Date getCurrentServerTime();
    Binder getMyFiles();
    Binder getNetFolders();
    ReleaseInfo getReleaseInfo();
    RootRestObject getRoot();
    User getSelf();
    Binder getSharedByMe();
    Binder getSharedWithMe();
    Binder getPublicShares();
    ZoneConfig getZoneConfig();
    SearchResultList<BinderBrief> getTopLevelFolders();
    SearchResultList<BinderBrief> getBinders(Long [] binderIds);
    List<BinderChildren> listBinderChildren(Long[] binderIds, Integer count);
    List<BinderChildren> listBinderChildren(Long[] binderIds, Long startingBinderId, Integer first, Integer count);
    SearchResultList<SearchableObject> listChildren(Binder binder);
    SearchResultList<SearchableObject> listChildren(Binder binder, Date ifModifiedSince);
    SearchResultList<SearchableObject> listChildren(Binder binder, Integer first, Integer count);
    SearchResultList<SearchableObject> listChildren(Binder binder, Integer first, Integer count, Date ifModifiedSince);
    BinderChanges listChanges(Binder binder, Date since, Integer count);
    Folder createLibraryFolder(Binder parentBinder, String name);
    Folder createLibraryFolderIfNecessary(Binder parentBinder, String name);
    FileProperties uploadFile(Binder parent, String fileName, boolean overwriteExisting, InputStream content);
    Share shareFolder(Folder folder, Share share);
    Share shareFile(FileProperties file, Share share);

    void delete(FileProperties file, boolean purge);
    void delete(Binder folder, boolean purge);
}
