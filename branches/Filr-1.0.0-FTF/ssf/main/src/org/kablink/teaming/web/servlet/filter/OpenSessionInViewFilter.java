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
package org.kablink.teaming.web.servlet.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SessionUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * User: david
 * Date: 6/14/12
 * Time: 8:30 AM
 */
public class OpenSessionInViewFilter implements Filter {
    private static Log logger = LogFactory.getLog(OpenSessionInViewFilter.class);

    public OpenSessionInViewFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        setupHibernateSession(servletRequest);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            teardownHibernateSession(servletRequest);
        }
    }

    public void destroy() {

    }

    private void setupHibernateSession(ServletRequest request) {
        // NOTE: This could be problematic if a single request from client ever results in a chained
        // invocation of more than one methods on resource(s). If such case is a possibility, we need
        // to create session conditionally (i.e., only when SessionUtil.sessionActive() returns false)
        // so that the thread of execution can share a single Hibernate session.
        if (SessionUtil.sessionActive())
            logger.warn("We've got an active Hibernate session for " + request.toString());
        else
            SessionUtil.sessionStartup();
    }

    private void teardownHibernateSession(ServletRequest request) {
        SessionUtil.sessionStop();
    }
}
