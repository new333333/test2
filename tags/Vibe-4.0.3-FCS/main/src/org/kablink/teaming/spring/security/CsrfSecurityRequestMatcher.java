/*
 * Copyright © 2009-2016 Novell, Inc.  All Rights Reserved.
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
package org.kablink.teaming.spring.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by david on 3/24/17.
 */
public class CsrfSecurityRequestMatcher implements RequestMatcher {

    protected Log logger = LogFactory.getLog(getClass());
    private RequestMatcher methodMatcher;
    private RequestMatcher pathMatcher;
    private boolean enabled = false;

    public CsrfSecurityRequestMatcher() {
        this(null, null);
    }

    public CsrfSecurityRequestMatcher(String [] ignoredPaths, String [] ignoredMethods) {
        setIgnoredPaths(ignoredPaths);
        setIgnoredMethods(ignoredMethods);
    }


    public void setEnabled(Boolean enabled) {
        if (enabled!=null) {
            this.enabled = enabled;
        }
    }

    public void setIgnoredMethods(String [] ignoredMethods) {
        if (ignoredMethods==null) {
            methodMatcher = new AllRequestMatcher();
        } else {
            methodMatcher = new NegatedRequestMatcher(new MethodRequestMatcher(ignoredMethods));
        }
    }

    public void setIgnoredPaths(String [] ignoredPaths) {
        if (ignoredPaths==null) {
            pathMatcher = new AllRequestMatcher();
        } else {
            List<RequestMatcher> pathMatchers = new ArrayList<>();
            for (String path : ignoredPaths) {
                pathMatchers.add(new RegexRequestMatcher(path, null));
            }
            pathMatcher = new NegatedRequestMatcher(new OrRequestMatcher(pathMatchers));
        }
    }

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        boolean matches = false;
        if (enabled) {
            matches = methodMatcher.matches(httpServletRequest) && pathMatcher.matches(httpServletRequest);
            if (matches) {
                logger.debug("CSRF token validation required for: " + httpServletRequest.getMethod() + " " +
                        httpServletRequest.getRequestURI());
            }
        }
        return matches;
    }

    private static class AllRequestMatcher implements RequestMatcher {
        @Override
        public boolean matches(HttpServletRequest httpServletRequest) {
            return true;
        }
    }

    private static class MethodRequestMatcher implements RequestMatcher {
        private Set<String> matchedMethods;

        public MethodRequestMatcher(String... matchedMethods) {
            this.matchedMethods = new HashSet<String>(Arrays.asList(matchedMethods));
        }

        @Override
        public boolean matches(HttpServletRequest httpServletRequest) {
            return matchedMethods.contains(httpServletRequest.getMethod());
        }
    }

}
