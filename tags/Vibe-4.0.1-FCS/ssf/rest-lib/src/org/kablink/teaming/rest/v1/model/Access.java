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
 * The access a user or group has to a resource.
 */
@XmlRootElement(name="access")
public class Access extends BaseRestObject {
    public enum RoleType {
        NONE,
        VIEWER,
        EDITOR,
        CONTRIBUTOR,
        ACCESS
    }

    /**
     * One of NONE, VIEWER, EDITOR, CONTRIBUTOR and ACCESS
     */
    private String role;
    private SharingPermission sharing;

    /**
     * Allowed values are <code>NONE</code>, <code>VIEWER</code>, <code>EDITOR</code>, <code>CONTRIBUTOR</code> and <code>ACCESS</code>.
     * Not all values are allowed in every context where Access objects are used.
     */
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * The sharing permissions assigned to the user or group.
     */
    public SharingPermission getSharing() {
        return sharing;
    }

    public void setSharing(SharingPermission sharing) {
        this.sharing = sharing;
    }
}
