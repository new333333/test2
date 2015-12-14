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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * User: david
 * Date: 7/25/12
 * Time: 11:38 AM
 */
@XmlRootElement(name="user_source")
public class LdapUserSource extends BaseRestObject {
    private String id;
    private String url;
    private String type = "ldap";
    private String usernameAttribute;
    private String guidAttribute = null;
    private List<KeyValuePair> mappings;
    private List<LdapSearchInfo> userSearches;
    private List<LdapSearchInfo> groupSearches;
    private String principal;
    private String credentials;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name="username_attribute")
    public String getUsernameAttribute() {
        return usernameAttribute;
    }

    public void setUsernameAttribute(String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    @XmlElement(name="guid_attribute")
    public String getGuidAttribute() {
        return guidAttribute;
    }

    public void setGuidAttribute(String guidAttribute) {
        this.guidAttribute = guidAttribute;
    }

    @XmlElementWrapper(name="attribute_map")
    @XmlElement(name="mapping")
    public List<KeyValuePair> getMappings() {
        return mappings;
    }

    public void setMappings(List<KeyValuePair> mappings) {
        this.mappings = mappings;
    }

    @XmlElementWrapper(name="user_contexts")
    @XmlElement(name="user_context")
    public List<LdapSearchInfo> getUserSearches() {
        return userSearches;
    }

    public void setUserSearches(List<LdapSearchInfo> userSearches) {
        this.userSearches = userSearches;
    }

    @XmlElementWrapper(name="group_contexts")
    @XmlElement(name="group_context")
    public List<LdapSearchInfo> getGroupSearches() {
        return groupSearches;
    }

    public void setGroupSearches(List<LdapSearchInfo> groupSearches) {
        this.groupSearches = groupSearches;
    }

    @XmlElement(name="username")
    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    @XmlElement(name="password")
    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
