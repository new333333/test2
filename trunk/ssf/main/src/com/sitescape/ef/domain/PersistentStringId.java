package com.sitescape.ef.domain;

/**
 * @author Jong Kim
 *
 */
public interface PersistentStringId  extends Persistent {
    public String getId();
    public void setId(String id);
}
