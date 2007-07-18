/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.domain;

import java.util.Set;
import java.util.Collection;

/**
 * @author Jong Kim
 *
 */
public interface AttachmentSupport {
    /**
     * Returns a set of objects implementing {@link Attachable} interface.
     * 
     * @return
     */
    public Set getAttachments();
    
    public void setAttachments(Collection attachments);
}
