/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.rest.servlet;

import org.apache.commons.io.IOUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * User: david
 * Date: 3/16/11
 * Time: 3:02 PM
 */
public class PrioritizedRequestFilter implements Filter {
    private PrioritizedRequestManager prioritizedRequestManager;
    private List<PriorityEndpoints> priorityEndpoints;

    @Required
    public void setEndpointConfig(File endpointConfig) {
        priorityEndpoints = new ArrayList<PriorityEndpoints>();
        FileReader reader = null;
        try {
            reader = new FileReader(endpointConfig);
            String fileContents = IOUtils.toString(reader);
            JSONArray array = new JSONArray(fileContents);
            for (int i=0; i<array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String priority = obj.getString("priority");
                priorityEndpoints.add(new PriorityEndpoints(priority, obj.getJSONArray("patterns")));
            }
        } catch (Exception e) {
            throw new BeanInitializationException("Error reading or parsing file: " + endpointConfig.getAbsolutePath(), e);
        } finally {
            if (reader!=null) {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    @Required
    public void setPrioritizedRequestManager(PrioritizedRequestManager prioritizedRequestManager) {
        this.prioritizedRequestManager = prioritizedRequestManager;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String path = httpRequest.getPathInfo();
        String priority = null;
        for (PriorityEndpoints endpoints : priorityEndpoints) {
            if (endpoints.matches(path)) {
                priority = endpoints.priority;
                break;
            }
        }
        if (priority==null) {
            throw new ServletException("Failed to determine priority for path: " + path);
        }

        try {
            if (!prioritizedRequestManager.incrementInProgressOrFail(priority)) {
                prioritizedRequestManager.incrementRejected(priority);
                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } else {
                try {
                    chain.doFilter(request, response);
                    prioritizedRequestManager.incrementProcessed(priority);
                } catch (Exception e) {
                    prioritizedRequestManager.incrementFailed(priority);
                    throw new ServletException(e);
                } finally {
                    prioritizedRequestManager.decrementInProgress(priority);
                }
            }
        } catch (InvalidPriorityException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
    }

    private static class PriorityEndpoints {
        private String priority;
        private List<Pattern> patterns;

        private PriorityEndpoints(String priority, JSONArray patternArray) throws JSONException, PatternSyntaxException {
            this.priority = priority;
            patterns = new ArrayList<Pattern>();
            for (int i=0; i<patternArray.length(); i++) {
                patterns.add(Pattern.compile(patternArray.getString(i)));
            }
        }

        public boolean matches(String path) {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(path).matches()) {
                    return true;
                }
            }
            return false;
        }
    }
}
