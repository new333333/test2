/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 *
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 *
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 *
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 *
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A reference to a user or group that is the recipient of a share or assigned permission.
 */
@XmlRootElement(name = "recipient")
public class Recipient extends LongIdLinkPair {
    public enum RecipientType {
        group,
        user
    }

    private String type;

    public Recipient() {
    }

    public Recipient(Long id, String type) {
        super(id, null);
        this.type = type;
    }

    /**
     * Type of the recipient.
     * <p>
     * For assigned permissions, allowed values are:
     * <ul>
     *     <li><code>user</code></li>
     *     <li><code>group</code></li>
     * </ul>
     * </p>
     * <p>
     * For share recipients, allowed values are:
     * <ul>
     *     <li><code>user</code></li>
     *     <li><code>group</code></li>
     *     <li><code>external_user</code></li>
     *     <li><code>public</code></li>
     *     <li><code>public_link</code></li>
     * </ul>
     * </p>
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipient recipient = (Recipient) o;

        return type.equals(recipient.type) && id.equals(recipient.id);
    }

}
