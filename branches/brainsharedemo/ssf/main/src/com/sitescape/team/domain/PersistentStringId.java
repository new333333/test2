package com.sitescape.team.domain;

/**
 * @author Jong Kim
 *
 */
public interface PersistentStringId  extends Persistent {
    public String getId();
    public void setId(String id);
}
