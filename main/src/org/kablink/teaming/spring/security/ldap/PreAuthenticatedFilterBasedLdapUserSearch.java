/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.spring.security.ldap;

import org.springframework.security.ldap.search.LdapUserSearch;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import org.springframework.util.Assert;

import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;

import javax.naming.directory.SearchControls;

public class PreAuthenticatedFilterBasedLdapUserSearch implements LdapUserSearch {

    private static final Log logger = LogFactory.getLog(PreAuthenticatedFilterBasedLdapUserSearch.class);

    private ContextSource contextSource;

    private SearchControls searchControls = new SearchControls();

    private String searchBase = "";

    private String searchFilter;

    public PreAuthenticatedFilterBasedLdapUserSearch(String searchBase, String searchFilter, BaseLdapPathContextSource contextSource) {
        Assert.notNull(contextSource, "contextSource must not be null");
        Assert.notNull(searchFilter, "searchFilter must not be null.");
        Assert.notNull(searchBase, "searchBase must not be null (an empty string is acceptable).");

        this.searchFilter = searchFilter;
        this.contextSource = contextSource;
        this.searchBase = searchBase;

        setSearchSubtree(true);

        if (searchBase.length() == 0) {
            logger.info("SearchBase not set. Searches will be performed from the root: "
                + contextSource.getBaseLdapPath());
        }
    }

    public DirContextOperations searchForUser(String username) {
        if (logger.isDebugEnabled()) {
            logger.debug("Searching for user '" + username + "', with user search " + this);
        }

        PreAuthenticatedSpringSecurityLdapTemplate template = new PreAuthenticatedSpringSecurityLdapTemplate(contextSource);

        template.setSearchControls(searchControls);

        try {

            return template.searchForSingleEntry(searchBase, searchFilter, username);

        } catch (IncorrectResultSizeDataAccessException notFound) {
            if (notFound.getActualSize() == 0) {
                throw new UsernameNotFoundException("User " + username + " not found in directory.");
            }
            // Search should never return multiple results if properly configured, so just rethrow
            throw notFound;
        }
    }

    public void setDerefLinkFlag(boolean deref) {
        searchControls.setDerefLinkFlag(deref);
    }

    public void setSearchSubtree(boolean searchSubtree) {
        searchControls.setSearchScope(searchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
    }

    public void setSearchTimeLimit(int searchTimeLimit) {
        searchControls.setTimeLimit(searchTimeLimit);
    }
    
	public void setReturningAttributes(String[] attrs) {
	    searchControls.setReturningAttributes(attrs);
	}

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[ searchFilter: '").append(searchFilter).append("', ");
        sb.append("searchBase: '").append(searchBase).append("'");
        sb.append(", scope: ")
          .append(searchControls.getSearchScope() == SearchControls.SUBTREE_SCOPE ? "subtree" : "single-level, ");
        sb.append(", searchTimeLimit: ").append(searchControls.getTimeLimit());
        sb.append(", derefLinkFlag: ").append(searchControls.getDerefLinkFlag()).append(" ]");
        return sb.toString();
    }
}

