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

package org.kablink.teaming.rest.v1.model.admin;

import org.kablink.teaming.rest.v1.model.BaseRestObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

/**
 * An LDAP container to search for users or groups.
 */
@XmlRootElement(name="search_info")
public class LdapSearchInfo {
    private String baseDn;
    private String filter;
    private Boolean searchSubtree;
    private LdapHomeDirConfig homeDirConfig;

    /**
     * The base DN to search.
     */
    @XmlElement(name="base_dn")
    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    /**
     * The LDAP filter to use when searching.
     */
    @XmlElement(name="filter")
    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Defines whether to search the LDAP subtree or just a single container.
     */
    @XmlElement(name="search_subtree")
    public Boolean getSearchSubtree() {
        return searchSubtree!=null && searchSubtree;
    }

    public void setSearchSubtree(Boolean searchSubtree) {
        this.searchSubtree = searchSubtree;
    }

    /**
     * Defines how user directory net folders are configured.
     */
    @XmlElement(name="home_dir_config")
    public LdapHomeDirConfig getHomeDirConfig() {
        return homeDirConfig;
    }

    public void setHomeDirConfig(LdapHomeDirConfig homeDirConfig) {
        this.homeDirConfig = homeDirConfig;
    }
}
