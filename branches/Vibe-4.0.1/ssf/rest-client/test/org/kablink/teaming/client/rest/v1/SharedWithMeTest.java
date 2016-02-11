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
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.User;

import java.util.Date;

/**
 * User: David
 * Date: 10/28/14
 * Time: 2:06 PM
 */
public class SharedWithMeTest extends TestCase {
    private ApiTestBinding binding;

    @Override
    protected void setUp() throws Exception {
        binding = new ApiTestBinding();
    }

    public SharedWithMeTest() {
    }

    public void testWhen200SharesThenSharedWithMeCountIs200() throws InterruptedException {
        User user = binding.clientApiAsLdapUser.getSelf();
        binding.whenNoSharesWithUser(user);
        for (int i=0; i<200; i++) {
            binding.whenSharedWithUser(binding.whenFileExistsInAdminMyFiles(i), user);
        }
        binding.thenSharedWithMeCountIs(200);
    }

    public void testWhenNoChangesThenSharedWithMeLibraryChildrenNotChanged() throws InterruptedException {
        User user = binding.clientApiAsLdapUser.getSelf();
        binding.whenNoSharesWithUser(user);
        FileProperties props = binding.whenFileExistsInAdminMyFiles(1);
        binding.whenSharedWithUser(props, user);

        Date date = binding.givenSharedWithMeLibraryChildrenLastModifiedTime();
        Thread.sleep(2000);
        binding.thenSharedWithMeLibraryChildrenNotChanged(date);
    }

    public void testWhenDeleteSharedFileThenSharedWithMeLibraryChildrenChanged() throws InterruptedException {
        User user = binding.clientApiAsLdapUser.getSelf();
        binding.whenNoSharesWithUser(user);
        FileProperties props = binding.whenFileExistsInAdminMyFiles(1);
        binding.whenSharedWithUser(props, user);

        Date date = binding.givenSharedWithMeLibraryChildrenLastModifiedTime();
        Thread.sleep(2000);
        binding.whenFileDeleted(props);
        binding.thenSharedWithMeLibraryChildrenChanged(date);
    }

    public void testWhenRevokeSharedFileThenSharedWithMeLibraryChildrenChanged() throws InterruptedException {
        User user = binding.clientApiAsLdapUser.getSelf();
        binding.whenNoSharesWithUser(user);
        FileProperties props = binding.whenFileExistsInAdminMyFiles(1);
        binding.whenSharedWithUser(props, user);

        Date date = binding.givenSharedWithMeLibraryChildrenLastModifiedTime();
        Thread.sleep(2000);
        binding.whenNoSharesWithUser(user);
        binding.thenSharedWithMeLibraryChildrenChanged(date);
    }


}
