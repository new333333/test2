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

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.util.Validator;

/**
 * This is used as a component class of FileAttachment
 * May also be used independently, but does not have
 * any persistent characteristics in that case.
 * @author Jong Kim
 *
 */
public class FileItem  {
    private String name;
    private long length=0;
   
    /**
     * This method name might be a bit misleading because it returns not just
     * name portion of the file but its pathname as well if it exists. 
     * (eg. /abc/xyz/foo.txt).  
     * 
     * Implementation Note: Given the fact that the path could be deep,
     * is 256 characters enough to store them?
     * 
     * @hibernate.property length="256" column="fileName"
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
    	if (Validator.isNull(name)) throw new IllegalArgumentException("null name");
       this.name = name;
    }
    /**
     * @hibernate.property column="fileLength"
     * @return size in bytes
     */
    public long getLength() {
        return this.length;
    }
    public void setLength(long length) {
        this.length = length;
    }
    
    /*
     * Convience routines
     * @return
     */
    public long getLengthKB() {
        return (this.length + 999)/1000;
    }
    public boolean equals(Object obj) {
   	 
    	if (obj == null) return false;
    	if (obj instanceof FileItem) {
    		FileItem o = (FileItem) obj;
    		if (name.equals(o.getName()))  return true;
    	}
    	return false;
    }
    public int hashCode() {
       	return  name.hashCode();
    }
    public String toString() {
    	return name;
    }
 
}
