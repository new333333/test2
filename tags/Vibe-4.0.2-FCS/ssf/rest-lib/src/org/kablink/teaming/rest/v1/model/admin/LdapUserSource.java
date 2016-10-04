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

import com.webcohesion.enunciate.metadata.DocumentationExample;
import org.kablink.teaming.rest.v1.model.BaseRestObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * An LDAP server that servers as a source for users and groups.
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

    /**
     * A random ID assigned to the LDAP User Source.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The LDAP URL of the server.
     */
    @DocumentationExample("ldap://ldap.mycompany.com:389")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * The type of User Source.
     *
     * <p>The only type that is currently supported is "ldap".</p>
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * The LDAP attribute that defines the user's login name.
     *
     * <p>Typically cn for eDirectory and sAMAccountName for Active Directory</p>
     */
    @DocumentationExample("cn")
    @XmlElement(name="username_attribute")
    public String getUsernameAttribute() {
        return usernameAttribute;
    }

    public void setUsernameAttribute(String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    /**
     * The LDAP attribute that defines a unique ID for each user or group.
     *
     * <p>Typically GUID for eDirectory and objectGUID for Active Directory</p>
     */
    @DocumentationExample("GUID")
    @XmlElement(name="guid_attribute")
    public String getGuidAttribute() {
        return guidAttribute;
    }

    public void setGuidAttribute(String guidAttribute) {
        this.guidAttribute = guidAttribute;
    }

    /**
     * List of LDAP attribute mappings.
     *
     * <p>These attributes are read and used to populate fields in the imported User object.
     * The key of the mapping is the User field name (ex: firstName, lastName, emailAddress, phone).  The value is the
     * LDAP attribute name (ex: givenName, surname, mail, telephoneNumber).
     * </p>
     */
    @XmlElementWrapper(name="attribute_map")
    @XmlElement(name="mapping")
    public List<KeyValuePair> getMappings() {
        return mappings;
    }

    public void setMappings(List<KeyValuePair> mappings) {
        this.mappings = mappings;
    }

    /**
     * List of LDAP contexts to search for users.
     */
    @XmlElementWrapper(name="user_contexts")
    @XmlElement(name="user_context")
    public List<LdapSearchInfo> getUserSearches() {
        return userSearches;
    }

    public void setUserSearches(List<LdapSearchInfo> userSearches) {
        this.userSearches = userSearches;
    }

    /**
     * List of LDAP contexts to search for groups.
     */
    @XmlElementWrapper(name="group_contexts")
    @XmlElement(name="group_context")
    public List<LdapSearchInfo> getGroupSearches() {
        return groupSearches;
    }

    public void setGroupSearches(List<LdapSearchInfo> groupSearches) {
        this.groupSearches = groupSearches;
    }

    /**
     * The LDAP user that is used to bind to and search the LDAP directory for users and groups.
     */
    @XmlElement(name="username")
    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    /**
     * The password of the LDAP user.
     */
    @XmlElement(name="password")
    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
