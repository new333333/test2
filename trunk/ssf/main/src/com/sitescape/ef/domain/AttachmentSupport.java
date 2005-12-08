package com.sitescape.ef.domain;

import java.util.List;
import java.util.Collection;

/**
 * @author Jong Kim
 *
 */
public interface AttachmentSupport {
    /**
     * Returns a list of objects implementing {@link Attachable} interface.
     * 
     * @return
     */
    public List getAttachments();
    
    public void setAttachments(Collection attachments);
}
