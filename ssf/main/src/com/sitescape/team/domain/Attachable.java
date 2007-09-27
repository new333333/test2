/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.domain;

/**
 * Attachability of a class is enabled by the class implementing the
 * <code>Attachable</code> interface. Classes that do not implement this
 * interface will not be allowed to be attached to another object. 
 * All subtypes of a attachable class are themselves attachable. 
 * 
 * @author Jong Kim
 *
 */
public interface Attachable {
    /**
     * The item can be attached to a single object only and the lifecycle
     * of the item is dependent upon the object to which it is attached. 
     */
    public static final int NON_SHARED = 1;// default
    /**
     * The item can be attached to multiple objects and the lifecycle of
     * attached item is dependent upon the objects to which it is attached.
     * Once the number of objects having the object as an attachment goes down 
     * to zero, the attached object is automatically removed from the system.
     */
    public static final int SHARABLE_DEPENDENT = 2; 
    /**
     * The item can be attached to multiple objects and the lifecycle of 
     * attached item is not dependent upon the objects to which it is attached. 
     * It is expected that the application explicitly manages the lifecycle of 
     * the object. 
     */
    public static final int SHARABLE_INDEPENDENT = 3;
    
    /**
     * Returns one of the following:
     * <p>
     * {@link #NON_SHARED}<br>
     * {@link #SHARABLE_DEPENDENT}<br>
     * {@link #SHARABLE_INDEPENDENT}
     * 
     * @return
     */
    public int getKind();
    public void setKind(int kind);
}
