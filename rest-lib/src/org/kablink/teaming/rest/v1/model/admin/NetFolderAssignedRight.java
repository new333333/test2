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

import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.Recipient;
import org.kablink.teaming.rest.v1.model.ShareRecipient;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A right that has been assigned to a recipient.
 *
 * <p>The recipient can be a user or a group.</p>
 */
@XmlRootElement(name="assigned_right")
public class NetFolderAssignedRight {
    private Recipient principal;
    private NetFolderAccess access;

    public NetFolderAssignedRight() {
    }

    public NetFolderAssignedRight(ShareRecipient principal, NetFolderAccess access) {
        this.principal = principal;
        this.access = access;
    }

    /**
     * The user or group.
     */
    public Recipient getPrincipal() {
        return principal;
    }

    public void setPrincipal(Recipient principal) {
        this.principal = principal;
    }

    /**
     * The access right granted to the recipient.
     */
    public NetFolderAccess getAccess() {
        return access;
    }

    public void setAccess(NetFolderAccess access) {
        this.access = access;
    }
}
