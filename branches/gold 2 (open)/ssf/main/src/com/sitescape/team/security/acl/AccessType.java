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
package com.sitescape.team.security.acl;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>AccessType</code> represents a type of access to an object, and
 * is one of the following: read, write, delete, or change ACL. 
 * <p>
 * <code>AccessType</code> is an enumeration type and is not extensible.
 * 
 * @author Jong Kim
 */
public class AccessType {
    
    // It is critically important to have this map instantiated here
    // BEFORE pre-defined WorkAreOperation instances are created. 
    private static final Map Instances = new HashMap();

    public final static AccessType READ = new AccessType("read");
    public final static AccessType WRITE = new AccessType("write");
    public final static AccessType DELETE = new AccessType("delete");
    public final static AccessType CHANGE_ACL = new AccessType("change-acl");
    
    private String name;
    
    private AccessType(String name) {
        this.name = name;
        
        Instances.put(name, this);
    }
    
    public String getName() {
        return name;
    }
    
    public static AccessType getInstance(String name) {
        return (AccessType) Instances.get(name);
    }
    
    public String toString() {
        return getName();
    }
}
