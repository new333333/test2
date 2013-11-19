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

import org.kablink.teaming.rest.v1.model.LongIdLinkPair;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: david
 * Date: 7/25/12
 * Time: 11:38 AM
 */
@XmlRootElement(name="home_dir_config")
public class LdapHomeDirConfig {
    public static String TYPE_CUSTOM_NET_FOLDER = "custom_net_folder";
    public static String TYPE_HOME_DIR_ATTRIBUTE = "home_dir_attribute";
    public static String TYPE_CUSTOM_ATTRIBUTE = "custom_attribute";
    public static String TYPE_NONE = "none";

    private String option;
    // The following data members are relevant when the creation option is "formula"
    private LongIdLinkPair netFolderServer;
    private String path;
    // The following data members are relevant when the creation option is "custom attribute"
    private String ldapAttribute;

    @XmlElement(name="type")
    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @XmlElement(name="net_folder_server")
    public LongIdLinkPair getNetFolderServer() {
        return netFolderServer;
    }

    public void setNetFolderServer(LongIdLinkPair netFolderServer) {
        this.netFolderServer = netFolderServer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlElement(name="ldap_attribute")
    public String getLdapAttribute() {
        return ldapAttribute;
    }

    public void setLdapAttribute(String ldapAttribute) {
        this.ldapAttribute = ldapAttribute;
    }
}
