package com.sitescape.ef.domain;

/**
 * <code>IdentifiableObject</code> is an object that can be identified by its
 * id. The API does not define the semantics or structure of the id.
 * 
 * @author Jong Kim
 *
 */
public interface Persistent {
    public long getLockVersion();
    public void setLockVersion(long lockVersion);
}
 