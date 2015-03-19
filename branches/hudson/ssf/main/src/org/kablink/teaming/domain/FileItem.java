/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.domain;

import org.kablink.util.Validator;

/**
 * This is used as a component class of FileAttachment
 * May also be used independently, but does not have
 * any persistent characteristics in that case.
 * 
 * @author Jong Kim
 */
public class FileItem  {
    private String name;
    private long length=0;
    private String md5;
    protected Description description;

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
    	if (Validator.isEmptyString(name)) throw new IllegalArgumentException("null name");
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

    /**
     * @hibernate.property column="fileMd5"
     * @return MD5 sum of the file
     */
    public String getMd5() {
        return this.md5;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * @hibernate.component prefix="description_"
     */
    public Description getDescription() {
        return this.description;
    }
    public void setDescription(Description description) {
    	this.description = description; 
    }
  
    public void setDescription(String descriptionText) {
		Description tmp = new Description(descriptionText);
    	if (description != null) {
    		if (description.equals(tmp)) return;
    	}
        this.description = tmp; 
    }

    /*
     * Convience routines
     * @return
     */
    public long getLengthKB() {
        return (this.length + 1023)/1024;
    }
    
    @Override
	public boolean equals(Object obj) {
   	 
    	if (obj == null) return false;
    	if (obj instanceof FileItem) {
    		FileItem o = (FileItem) obj;
    		if (name.equals(o.getName()))  return true;
    	}
    	return false;
    }
    
    @Override
	public int hashCode() {
       	return  name.hashCode();
    }
    
    @Override
	public String toString() {
    	return name;
    }
}
