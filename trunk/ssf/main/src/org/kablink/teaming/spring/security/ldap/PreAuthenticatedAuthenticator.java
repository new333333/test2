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

import org.kablink.util.StringUtil;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticator;
import org.springframework.security.web .authentication.preauth.PreAuthenticatedAuthenticationToken;

import org.springframework.util.Assert;

public class PreAuthenticatedAuthenticator extends AbstractLdapAuthenticator {

    public PreAuthenticatedAuthenticator(LdapContextSource contextSource) {
        super(contextSource);
    }

    public DirContextOperations authenticate(Authentication authentication) {
        DirContextOperations user = null;
        Assert.isInstanceOf(PreAuthenticatedAuthenticationToken.class, authentication,
                "Can only process PreAuthenticatedAuthenticationToken objects");

        String accountname = authentication.getName();

        // Otherwise use the configured locator to find the user
        // and authenticate with the returned DN.
        if (user == null && getUserSearch() != null) {
            DirContextOperations userFromSearch = getUserSearch().searchForUser(accountname);
            user = retrieveWithDn(userFromSearch.getDn().toString());
        }

        if (user == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        return user;
    }

    private DirContextOperations retrieveWithDn(String userDn) {
        PreAuthenticatedSpringSecurityLdapTemplate template = new PreAuthenticatedSpringSecurityLdapTemplate(getContextSource());

        return template.retrieveEntry(userDn, getUserAttributes());
    }

    public static void main(String[] args) {
    	String s1 = "jongteaming\\kim";
    	String s2 = "jongteaming\\";
    	String s3 = "\\kim";
    	String s4 = "kim";
    	String s5 = "";
    	
    	System.out.println("Regular Split for [" + s1 + "]");
    	write(s1.split("\\\\"));
    	System.out.println("StringUtil Split for [" + s1 + "]");
    	write(StringUtil.split(s1,"\\"));
    	
    	System.out.println("Regular Split for [" + s2 + "]");
    	write(s2.split("\\\\"));
    	System.out.println("StringUtil Split for [" + s2 + "]");
    	write(StringUtil.split(s2,"\\"));
    	
    	System.out.println("Regular Split for [" + s3 + "]");
    	write(s3.split("\\\\"));
    	System.out.println("StringUtil Split for [" + s3 + "]");
    	write(StringUtil.split(s3,"\\"));
    	
    	System.out.println("Regular Split for [" + s4 + "]");
    	write(s4.split("\\\\"));
    	System.out.println("StringUtil Split for [" + s4 + "]");
    	write(StringUtil.split(s4,"\\"));
    	
    	System.out.println("Regular Split for [" + s5 + "]");
    	write(s5.split("\\\\"));
    	System.out.println("StringUtil Split for [" + s5 + "]");
    	write(StringUtil.split(s5,"\\"));
    }
    private static void write(String[] strs) {
    	for(int i = 0; i < strs.length; i++) {
    		System.out.println(i + " [" + strs[i] + "]");
    	}
    	System.out.println();
    }
}

