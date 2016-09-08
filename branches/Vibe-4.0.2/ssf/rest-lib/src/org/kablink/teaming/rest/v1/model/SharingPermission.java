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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sharing permissions
 */
@XmlRootElement(name="sharing_permission")
public class SharingPermission {
    private String maxRole;
    private Boolean internal;
    private Boolean external;
    private Boolean public_;
    private Boolean publicLink;
    private Boolean grantReshare;

    /**
     * The maximum role that the user is allowed to grant where sharing an item with another user.
     * <p></p>One of <code>NONE</code>, <code>VIEWER</code>, <code>EDITOR</code>, and <code>CONTRIBUTOR</code>.</p>
     */
    @XmlElement(name = "max_role")
    public String getMaxRole() {
        return maxRole;
    }

    public void setMaxRole(String maxRole) {
        this.maxRole = maxRole;
    }

    /**
     * Whether or not sharing with external users is allowed.
     */
    @XmlElement(name = "external")
    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    /**
     * Whether or not sharing with internal users is allowed.
     */
    @XmlElement(name = "internal")
    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    /**
     * Whether or not making an item public is allowed..
     */
    @XmlElement(name = "public")
    public Boolean getPublic() {
        return public_;
    }

    public void setPublic(Boolean public_) {
        this.public_ = public_;
    }

    /**
     * Whether or not sharing via a public link is allowed.
     */
    @XmlElement(name = "public_link")
    public Boolean getPublicLink() {
        return publicLink;
    }

    public void setPublicLink(Boolean publicLink) {
        this.publicLink = publicLink;
    }

    /**
     * Whether or not the recipient can reshare an item.
     */
    @XmlElement(name = "grant_reshare")
    public Boolean getGrantReshare() {
        return grantReshare;
    }

    public void setGrantReshare(Boolean grantReshare) {
        this.grantReshare = grantReshare;
    }

    public void mergeIn(SharingPermission fileSharing) {
        if (fileSharing.internal!=null) {
            internal = fileSharing.internal;
        }
        if (fileSharing.external!=null) {
            external = fileSharing.external;
        }
        if (fileSharing.public_!=null) {
            public_ = fileSharing.public_;
        }
        if (fileSharing.publicLink!=null) {
            publicLink = fileSharing.publicLink;
        }
        if (fileSharing.grantReshare!=null) {
            grantReshare = fileSharing.grantReshare;
        }
    }
}
