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

@XmlRootElement(name="sharing_permission")
public class SharingPermission {
    private Boolean internal;
    private Boolean external;
    private Boolean public_;
    private Boolean grantReshare;

    @XmlElement(name = "external")
    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    @XmlElement(name = "internal")
    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    @XmlElement(name = "public")
    public Boolean getPublic() {
        return public_;
    }

    public void setPublic(Boolean public_) {
        this.public_ = public_;
    }

    @XmlElement(name = "grant_reshare")
    public Boolean getGrantReshare() {
        return grantReshare;
    }

    public void setGrantReshare(Boolean grantReshare) {
        this.grantReshare = grantReshare;
    }
}
