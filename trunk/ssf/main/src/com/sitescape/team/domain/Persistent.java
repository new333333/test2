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
 