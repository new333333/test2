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
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Configuration defining how home directory net folders are created for users that are imported from LDAP.
 */
@XmlRootElement(name="home_dir_config")
public class LdapHomeDirConfig {
    public static final String TYPE_HOME_DIR_ATTRIBUTE = "home_dir_attribute";
    public static final String TYPE_CUSTOM_NET_FOLDER = "custom_net_folder";
    public static final String TYPE_CUSTOM_ATTRIBUTE = "custom_attribute";
    public static final String TYPE_NONE = "none";

    private String option;
    // The following data members are relevant when the creation option is "formula"
    private LongIdLinkPair netFolderServer;
    private String path;
    // The following data members are relevant when the creation option is "custom attribute"
    private String ldapAttribute;

    /**
     * One of <code>home_dir_attribute</code>,<code>custom_attribute</code>,<code>custom_net_folder</code>,<code>none</code>
     * <p>
     *     <ul>
     *         <li><code>home_dir_attribute</code>: The LDAP home directory attribute contains the location of the user's home directory.  No other fields are necessary.</li>
     *         <li><code>custom_attribute</code>: A custom LDAP attribute contains the location of the user's home directory.  The attribute is defined in the <code>ldap_attribute</code> field.</li>
     *         <li><code>custom_net_folder</code>: User home directories can be found in a custom location defined by <code>net_folder_server</code> and <code>path</code>.</li>
     *         <li><code>none</code>: Home directory net folders are not created.</li>
     *     </ul>
     * </p>
     */
    @XmlElement(name="type")
    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    /**
     * When <code>type</code> is <code>custom_net_folder</code>, this defines the Net Folder Server that hosts the user home directory net folders.
     */
    @XmlElement(name="net_folder_server")
    public LongIdLinkPair getNetFolderServer() {
        return netFolderServer;
    }

    public void setNetFolderServer(LongIdLinkPair netFolderServer) {
        this.netFolderServer = netFolderServer;
    }

    /**
     * When <code>type</code> is <code>custom_net_folder</code>, this defines the path on the Net Folder Server where the user home directory net folders reside.
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * When <code>type</code> is <code>custom_attribute</code>, this defines the LDAP attribute that contains the location of the user home directory..
     */
    @XmlElement(name="ldap_attribute")
    public String getLdapAttribute() {
        return ldapAttribute;
    }

    public void setLdapAttribute(String ldapAttribute) {
        this.ldapAttribute = ldapAttribute;
    }
}
