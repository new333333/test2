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
/*
 * Created on Oct 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import com.sitescape.team.NoObjectByTheIdException;


/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NoFolderEntryByTheIdException extends NoObjectByTheIdException {
    private static final String NoDocshareEntryByTheIdException_ErrorCode = "errorcode.no.folder.entry.by.the.id";
    
    public NoFolderEntryByTheIdException(Long entryId) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryId);
    }
    public NoFolderEntryByTheIdException(Long entryId, String message) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryId, message);
    }
    public NoFolderEntryByTheIdException(Long entryId, String message, Throwable cause) {
        super(NoDocshareEntryByTheIdException_ErrorCode,entryId, message, cause);
    }
    public NoFolderEntryByTheIdException(Long entryId, Throwable cause) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryId, cause);
    }
    public NoFolderEntryByTheIdException(String entryKey) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryKey);
    }
    public NoFolderEntryByTheIdException(String entryKey, String message) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryKey, message);
    }
    public NoFolderEntryByTheIdException(String entryKey, String message, Throwable cause) {
        super(NoDocshareEntryByTheIdException_ErrorCode,entryKey, message, cause);
    }
    public NoFolderEntryByTheIdException(String entryKey, Throwable cause) {
        super(NoDocshareEntryByTheIdException_ErrorCode, entryKey, cause);
    }
}
