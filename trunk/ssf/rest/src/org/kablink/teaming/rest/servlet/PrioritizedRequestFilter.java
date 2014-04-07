/*
 * Copyright © 2009-2010 Novell, Inc.  All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.  IT MAY NOT BE USED, COPIED,
 * DISTRIBUTED, DISCLOSED, ADAPTED, PERFORMED, DISPLAYED, COLLECTED, COMPILED, OR LINKED WITHOUT NOVELL'S
 * PRIOR WRITTEN CONSENT.  USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE
 * PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 * NOVELL PROVIDES THE WORK "AS IS," WITHOUT ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING WITHOUT THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. NOVELL, THE
 * AUTHORS OF THE WORK, AND THE OWNERS OF COPYRIGHT IN THE WORK ARE NOT LIABLE FOR ANY CLAIM, DAMAGES,
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN
 * CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
 */
package com.novell.aca.api.util;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
