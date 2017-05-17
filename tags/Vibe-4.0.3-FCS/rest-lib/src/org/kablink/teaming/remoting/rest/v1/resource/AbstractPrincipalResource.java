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
package org.kablink.teaming.remoting.rest.v1.resource;

import org.kablink.teaming.domain.LimitedUserView;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.UserBrief;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Order;
import org.kablink.util.search.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: david
 * Date: 6/18/12
 * Time: 1:47 PM
 */
abstract public class AbstractPrincipalResource extends AbstractDefinableEntityResource {
    protected enum PrincipalOptions {
        local,
        ldap,
        all,
        none
    }

    protected Set<LimitedUserView> searchForPrincipalsLimited(Set<Long> ids) {
        if (ids==null || ids.size()==0) {
            return new HashSet<LimitedUserView>(0);
        }
        return getProfileModule().getLimitedUserViews(ids);
    }

    protected Map searchForPrincipals(Set<Long> ids, String keyword, PrincipalOptions userOption, PrincipalOptions groupOption, boolean includeAllUsers, String descriptionFormatStr, Integer offset, Integer maxCount, Map<String, Object> nextParams) {
        boolean allowExternal = false;
        Junction criterion = Restrictions.conjunction();
        if (ids!=null) {
            Junction or = Restrictions.disjunction();
            for (Long id : ids) {
                or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
                allowExternal = true;
            }
            criterion.add(or);
            nextParams.put("id", ids);
        }
        Junction orJunction = Restrictions.disjunction();
        orJunction.add(SearchUtils.getFalseCriterion());
        if (userOption!= PrincipalOptions.none) {
            Criterion userCrit = SearchUtils.buildUsersCriterion(allowExternal);
            // TODO: support local-only and ldap-only searches (not currently supported by the index)
            orJunction.add(userCrit);
        }
        if (groupOption!= PrincipalOptions.none) {
            Criterion groupCrit;
            if (groupOption== PrincipalOptions.local) {
                groupCrit = SearchUtils.buildGroupsCriterion(Boolean.FALSE, includeAllUsers);
            } else if (groupOption== PrincipalOptions.ldap) {
                groupCrit = SearchUtils.buildGroupsCriterion(Boolean.TRUE, includeAllUsers);
            } else {
                groupCrit = SearchUtils.buildGroupsCriterion(null, includeAllUsers);
            }
            orJunction.add(groupCrit);
        }
        criterion.add(orJunction);
        if (keyword!=null) {
            Junction or = Restrictions.disjunction();
            keyword = SearchUtils.modifyQuickFilter(keyword);
            or.add(Restrictions.like(Constants.TITLE_FIELD, keyword));
            or.add(Restrictions.like(Constants.EMAIL_FIELD, keyword));
            or.add(Restrictions.like(Constants.EMAIL_DOMAIN_FIELD, keyword));
            or.add(Restrictions.like(Constants.LOGINNAME_FIELD, keyword));
            criterion.add(or);
            nextParams.put("keyword", keyword);
        }
        nextParams.put("description_format", descriptionFormatStr);
        Criteria criteria = new Criteria();
        criteria.add(criterion);
        criteria.addOrder(new Order(Constants.SORT_TITLE_FIELD, true));

        return getBinderModule().executeSearchQuery(criteria, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
    }

    protected LimitedUserView getLimitedUser(Long id) {
        return getProfileModule().getLimitedUserView(id);
    }

    protected boolean canViewUsers() {
        try {
            getProfileModule().getProfileBinder();
            return true;
        } catch (AccessControlException e) {
            return false;
        }
    }

}