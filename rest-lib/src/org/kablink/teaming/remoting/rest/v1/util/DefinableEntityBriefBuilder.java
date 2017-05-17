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

import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.search.Constants;

import java.util.Date;
import java.util.Map;

/**
 * User: david
 * Date: 5/24/12
 * Time: 11:15 AM
 */
abstract public class DefinableEntityBriefBuilder {
    private int descriptionFormat;

    protected DefinableEntityBriefBuilder() {
    }

    protected DefinableEntityBriefBuilder(int descriptionFormat) {
        this.descriptionFormat = descriptionFormat;
    }

    public void setDescriptionFormat(int descriptionFormat) {
        this.descriptionFormat = descriptionFormat;
    }

    public void populateDefinableEntityBrief(DefinableEntityBrief model, Map entry, String parentBinderField) {
        String binderIdStr = (String) entry.get(Constants.DOCID_FIELD);
        Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;

        model.setId(binderId);
        model.setTitle((String) entry.get(Constants.TITLE_FIELD));

        String descText = (String) entry.get(Constants.DESC_FIELD);
        String descFormat = (String) entry.get(Constants.DESC_FORMAT_FIELD);
        model.setDescription(ResourceUtil.buildDescription(descText, descFormat, descriptionFormat));

        model.setEntityType((String) entry.get(Constants.ENTITY_FIELD));
        model.setFamily((String) entry.get(Constants.FAMILY_FIELD));
        model.setIcon(LinkUriUtil.getIconLinkUri((String) entry.get(Constants.ICON_NAME_FIELD), model.getEntityType()));
        model.setPermaLink(PermaLinkUtil.getPermalink(entry));
        String defid = (String) entry.get(Constants.COMMAND_DEFINITION_FIELD);
        if (defid!=null) {
            model.setDefinition(new StringIdLinkPair(defid,
                    LinkUriUtil.getDefinitionLinkUri(defid)));
        }

        Long parentBinderId = SearchResultBuilderUtil.getLong(entry, parentBinderField);
        if (parentBinderId!=null) {
            model.setParentBinder(new ParentBinder(parentBinderId, LinkUriUtil.getBinderLinkUri(parentBinderId)));
        }

        Long creator = Long.valueOf((String) entry.get(Constants.CREATORID_FIELD));
        model.setCreation(
                new HistoryStamp(new LongIdLinkPair(creator, LinkUriUtil.getUserLinkUri(creator)),
                        (Date) entry.get(Constants.CREATION_DATE_FIELD)));

        Long modifier = Long.valueOf((String) entry.get(Constants.MODIFICATIONID_FIELD));
        model.setModification(
                new HistoryStamp(new LongIdLinkPair(modifier, LinkUriUtil.getUserLinkUri(modifier)),
                        (Date) entry.get(Constants.MODIFICATION_DATE_FIELD)));
    }
}
