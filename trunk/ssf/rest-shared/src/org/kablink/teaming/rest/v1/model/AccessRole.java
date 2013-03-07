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
import java.util.Date;

/**
 * User: david
 * Date: 7/25/12
 * Time: 11:38 AM
 */
@XmlRootElement(name="access_role")
public class AccessRole extends BaseRestObject {
    private String role;
    private Boolean canShareInternal;
    private Boolean canShareExternal;
    private Boolean canSharePublic;
    private Boolean canShareForward;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @XmlElement(name = "can_share_external")
    public Boolean getCanShareExternal() {
        return canShareExternal;
    }

    public void setCanShareExternal(Boolean canShareExternal) {
        this.canShareExternal = canShareExternal;
    }

    @XmlElement(name = "can_share_forward")
    public Boolean getCanShareForward() {
        return canShareForward;
    }

    public void setCanShareForward(Boolean canShareForward) {
        this.canShareForward = canShareForward;
    }

    @XmlElement(name = "can_share_internal")
    public Boolean getCanShareInternal() {
        return canShareInternal;
    }

    public void setCanShareInternal(Boolean canShareInternal) {
        this.canShareInternal = canShareInternal;
    }

    @XmlElement(name = "can_share_public")
    public Boolean getCanSharePublic() {
        return canSharePublic;
    }

    public void setCanSharePublic(Boolean canSharePublic) {
        this.canSharePublic = canSharePublic;
    }
}
