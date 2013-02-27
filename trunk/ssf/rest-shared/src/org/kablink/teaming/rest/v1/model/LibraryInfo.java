/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * User: david
 * Date: 2/7/13
 * Time: 10:55 AM
 */
@XmlRootElement(name = "library_info")
public class LibraryInfo {
    private Date modifiedDate;
    private Integer fileCount;
    private Integer folderCount;
    private Long diskSpace;

    public LibraryInfo() {
    }

    public LibraryInfo(Long diskSpace, Integer fileCount, Integer folderCount, Date modifiedDate) {
        this.diskSpace = diskSpace;
        this.fileCount = fileCount;
        this.folderCount = folderCount;
        this.modifiedDate = modifiedDate;
    }

    @XmlElement(name = "mod_date")
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @XmlElement(name = "disk_space")
    public Long getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(Long diskSpace) {
        this.diskSpace = diskSpace;
    }

    @XmlElement(name = "file_count")
    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    @XmlElement(name = "folder_count")
    public Integer getFolderCount() {
        return folderCount;
    }

    public void setFolderCount(Integer folderCount) {
        this.folderCount = folderCount;
    }
}
