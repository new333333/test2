package com.sitescape.ef.domain;

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
