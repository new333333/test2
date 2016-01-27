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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.rest.v1.model.DefinableEntityBrief;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.util.search.Constants;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: david
 * Date: 6/4/12
 * Time: 2:41 PM
 */
public class UniversalBuilder implements SearchResultBuilder<SearchableObject> {

    private static class BuilderFactory {
        private Map<String, Class<? extends SearchResultBuilder>> buildersByDocType =
                new HashMap<String, Class<? extends SearchResultBuilder>>();
        private Map<String, Class<? extends SearchResultBuilder>> buildersByEntryType =
                new HashMap<String, Class<? extends SearchResultBuilder>>();

        private BuilderFactory() {
            buildersByDocType.put(Constants.DOC_TYPE_ATTACHMENT, FilePropertiesBuilder.class);
            buildersByDocType.put(Constants.DOC_TYPE_BINDER, BinderBriefBuilder.class);
            buildersByEntryType.put(Constants.ENTRY_TYPE_ENTRY, FolderEntryBriefBuilder.class);
            buildersByEntryType.put(Constants.ENTRY_TYPE_REPLY, ReplyBriefBuilder.class);
            buildersByEntryType.put(Constants.ENTRY_TYPE_USER, UserBriefBuilder.class);
            buildersByEntryType.put(Constants.ENTRY_TYPE_GROUP, GroupBriefBuilder.class);
            buildersByEntryType.put(Constants.ENTRY_TYPE_APPLICATION, ApplicationBriefBuilder.class);
            buildersByEntryType.put(Constants.ENTRY_TYPE_APPLICATION_GROUP, ApplicationGroupBriefBuilder.class);
        }

        public SearchResultBuilder<SearchableObject> factoryBuilder(AllModulesInjected ami, Map objectMap, boolean preferFileOverEntry) {
            Class<? extends SearchResultBuilder> clss = null;
            String docType = (String) objectMap.get(Constants.DOC_TYPE_FIELD);
            if(Constants.DOC_TYPE_ENTRY.equals(docType)) {
                clss = buildersByEntryType.get((String) objectMap.get(Constants.ENTRY_TYPE_FIELD));
            } else {
                clss = buildersByDocType.get(docType);
            }
            if (clss.equals(FolderEntryBriefBuilder.class) && preferFileOverEntry) {
                clss = FilePropertiesBuilder.class;
            }
            if (clss!=null) {
                try {
                    if (clss==FilePropertiesBuilder.class) {
                        Constructor<? extends SearchResultBuilder> constructor = clss.getConstructor(AllModulesInjected.class);
                        return constructor.newInstance(ami);
                    } else {
                        return clss.newInstance();
                    }
                } catch (Exception e) {
                }
            }
            return null;
        }
    }

    private static BuilderFactory builderFactory = new BuilderFactory();

    private AllModulesInjected ami;
    private int descriptionFormat;
    private boolean preferFileOverEntry;

    public UniversalBuilder(AllModulesInjected ami, int descriptionFormat, boolean preferFileOverEntry) {
        this.ami = ami;
        this.descriptionFormat = descriptionFormat;
        this.preferFileOverEntry = preferFileOverEntry;
    }

    public void setDescriptionFormat(int descriptionFormat) {
        this.descriptionFormat = descriptionFormat;
    }

    public SearchableObject build(Map objectMap) {
        SearchResultBuilder<SearchableObject> builder = builderFactory.factoryBuilder(ami, objectMap, preferFileOverEntry);
        if (builder!=null) {
            builder.setDescriptionFormat(descriptionFormat);
            return builder.build(objectMap);
        }
        return null;
    }

    public Object getId(SearchableObject obj) {
        return null;
    }

    public Object getParentId(SearchableObject obj) {
        return null;
    }

    public SearchResultTreeNode<SearchableObject> factoryTreeNode(SearchableObject obj) {
        return null;
    }

    public Date getLastModified(SearchableObject obj) {
        if (obj instanceof DefinableEntityBrief) {
            return ((DefinableEntityBrief)obj).getModificationDate();
        } else if (obj instanceof FileProperties) {
            return ((FileProperties)obj).getModificationDate();
        }
        return null;
    }
}
