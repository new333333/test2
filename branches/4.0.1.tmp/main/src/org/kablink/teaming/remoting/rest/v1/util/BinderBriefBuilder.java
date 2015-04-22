/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.util.search.Constants;

import java.util.*;

/**
 * User: david
 * Date: 5/18/12
 * Time: 1:07 PM
 */
public class BinderBriefBuilder extends BaseBinderBriefBuilder implements ContainerSearchResultBuilder<BinderBrief> {
    public BinderBriefBuilder() {
    }

    public BinderBriefBuilder(int descriptionFormat) {
        super(descriptionFormat);
    }

    public BinderBrief[] factoryArray(int length) {
        return new BinderBrief[length];
    }

    public BinderBrief build(Map entry) {
        BinderBrief binder = new BinderBrief();
        populateBinderBrief(binder, entry);
        return binder;
    }

    public EntityIdentifier.EntityType getType(BinderBrief obj) {
        try {
            return EntityIdentifier.EntityType.valueOf(obj.getEntityType());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public BinderBrief lookup(AllModulesInjected ami, Object id) {
        BinderBrief bb = null;
        try {
            Binder binder = ami.getBinderModule().getBinder((Long) id, false, true);
            bb = ResourceUtil.buildBinderBrief(binder);
        } catch (NoBinderByTheIdException e) {
            // Ignore
        } catch (AccessControlException e) {
            // Ignore
        }
        return bb;
    }

    public void sort(List<BinderBrief> objs) {
        Collections.sort(objs, new Comparator<BinderBrief>() {
            public int compare(BinderBrief o1, BinderBrief o2) {
                return o1.getPath().compareTo(o2.getPath());
            }
        });
    }

    public Object getId(BinderBrief obj) {
        return obj.getId();
    }

    public Object getParentId(BinderBrief obj) {
        LongIdLinkPair parent = obj.getParentBinder();
        return parent==null ? null : parent.getId();
    }

    public SearchResultTreeNode<BinderBrief> factoryTreeNode(BinderBrief binder) {
        return new SearchResultTreeNode<BinderBrief>(binder);
    }

    public Date getLastModified(BinderBrief obj) {
        return obj.getModificationDate();
    }
}
