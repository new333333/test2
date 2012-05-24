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

import org.dom4j.Element;
import org.kablink.teaming.rest.v1.model.DefinableEntityBrief;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.IdLinkPair;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import java.util.Date;
import java.util.Map;

/**
 * User: david
 * Date: 5/24/12
 * Time: 11:15 AM
 */
abstract public class DefinableEntityBriefBuilder {
    public void populateDefinableEntityBrief(DefinableEntityBrief model, Map entry, String parentBinderField) {
        String binderIdStr = (String) entry.get(Constants.DOCID_FIELD);
        Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;

        model.setId(binderId);
        model.setTitle((String) entry.get(Constants.TITLE_FIELD));
        model.setEntityType((String) entry.get(Constants.ENTITY_FIELD));
        model.setFamily((String) entry.get(Constants.FAMILY_FIELD));
        model.setDefinitionId((String) entry.get(Constants.COMMAND_DEFINITION_FIELD));
        model.setDefinitionType(Integer.valueOf((String) entry.get(Constants.DEFINITION_TYPE_FIELD)));

        Long parentBinderId = getLong(entry, parentBinderField);
        if (parentBinderId!=null) {
            model.setParentBinder(new IdLinkPair(parentBinderId, LinkUriUtil.getBinderLinkUri(parentBinderId)));
        }

        Long creator = Long.valueOf((String) entry.get(Constants.CREATORID_FIELD));
        model.setCreation(
                new HistoryStamp(new IdLinkPair(creator, LinkUriUtil.getUserLinkUri(creator)),
                        (Date) entry.get(Constants.CREATION_DATE_FIELD)));

        Long modifier = Long.valueOf((String) entry.get(Constants.MODIFICATIONID_FIELD));
        model.setModification(
                new HistoryStamp(new IdLinkPair(modifier, LinkUriUtil.getUserLinkUri(modifier)),
                        (Date) entry.get(Constants.MODIFICATION_DATE_FIELD)));
    }

    public void populateDefinableEntityBrief(DefinableEntityBrief model, Element elem, String parentBinderField) {
        String binderIdStr = (String) elem.attributeValue(Constants.DOCID_FIELD);
        Long binderId = (binderIdStr != null)? Long.valueOf(binderIdStr) : null;

        model.setId(binderId);
        model.setTitle((String) elem.attributeValue(Constants.TITLE_FIELD));
        model.setEntityType((String) elem.attributeValue(Constants.ENTITY_FIELD));
        model.setFamily((String) elem.attributeValue(Constants.FAMILY_FIELD));
        model.setDefinitionId((String) elem.attributeValue(Constants.COMMAND_DEFINITION_FIELD));
        model.setDefinitionType(Integer.valueOf((String) elem.attributeValue(Constants.DEFINITION_TYPE_FIELD)));

        Long parentBinderId = getLong(elem, parentBinderField);
        if (parentBinderId!=null) {
            model.setParentBinder(new IdLinkPair(parentBinderId, LinkUriUtil.getBinderLinkUri(parentBinderId)));
        }

//        Long creator = Long.valueOf((String) elem.attributeValue(Constants.CREATORID_FIELD));
//        model.setCreation(
//                new HistoryStamp(new IdLinkPair(creator, LinkUriUtil.getUserLinkUri(creator)),
//                        (Date) elem.attributeValue(Constants.CREATION_DATE_FIELD)));
//
//        Long modifier = Long.valueOf((String) elem.attributeValue(Constants.MODIFICATIONID_FIELD));
//        model.setModification(
//                new HistoryStamp(new IdLinkPair(modifier, LinkUriUtil.getUserLinkUri(modifier)),
//                        (Date) elem.attributeValue(Constants.MODIFICATION_DATE_FIELD)));
    }

    public static Boolean getBoolean(Map entry, String fieldName) {
        Boolean value = null;
        String libraryStr = (String) entry.get(fieldName);
        if(Constants.TRUE.equals(libraryStr))
            value = Boolean.TRUE;
        else if(Constants.FALSE.equals(libraryStr))
            value = Boolean.FALSE;
        return value;
    }

    public static Long getLong(Map entry, String fieldName) {
        Long parentBinderId = null;
        String parentBinderIdStr = (String) entry.get(fieldName);
        if(Validator.isNotNull(parentBinderIdStr))
            parentBinderId = Long.valueOf(parentBinderIdStr);
        return parentBinderId;
    }

    public static Long getLong(Element entry, String fieldName) {
        Long parentBinderId = null;
        String parentBinderIdStr = entry.attributeValue(fieldName);
        if(Validator.isNotNull(parentBinderIdStr))
            parentBinderId = Long.valueOf(parentBinderIdStr);
        return parentBinderId;
    }
}
