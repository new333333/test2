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
package org.kablink.teaming.rest.v1.model;

import org.kablink.teaming.rest.v1.model.BaseRestObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Restrictions for sharing with external users.
 */
@XmlRootElement(name="external_sharing_restrictions")
public class ExternalSharingRestrictions extends BaseRestObject {
    public enum Mode {
        none,
        whitelist,
        blacklist
    }

    private String mode;
    private List<String> emailList;
    private List<String> domainList;

    public ExternalSharingRestrictions() {
    }

    /**
     * The sharing restriction mode.  Allowed values are "none", "whitelist" and "blacklist"
     * If "none", there are no domain or email restrictions for users with rights to share with external users
     * If "whitelist", users with external sharing rights can only share with listed email addresses and email domains
     * If "blacklist", users with external sharing rights can share with any email address except those listed.
     */
    @XmlElement(name="mode")
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * List of email address that are acceptable (whitelist mode) or unacceptable (blacklist mode)
     */
    @XmlElementWrapper(name="email_list")
    @XmlElement(name="email")
    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    /**
     * List of email domains that are acceptable (whitelist mode) or unacceptable (blacklist mode)
     */
    @XmlElementWrapper(name="domain_list")
    @XmlElement(name="domain")
    public List<String> getDomainList() {
        return domainList;
    }

    public void setDomainList(List<String> domainList) {
        this.domainList = domainList;
    }
}
