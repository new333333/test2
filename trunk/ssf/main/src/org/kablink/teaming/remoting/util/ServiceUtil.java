/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.remoting.util;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.DatedMultipartFile;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jong
 *
 */
public class ServiceUtil {

	public static void modifyFolderEntryWithFile(FolderEntry entry, String dataName, String filename, InputStream is, Date modDate) 
			throws AccessControlException, ReservedByAnotherUserException, WriteFilesException, WriteEntryDataException 
			 {
		if (Validator.isNull(dataName) && entry.getParentFolder().isLibrary()) {
			// The file is being created within a library folder and the client hasn't specified a data item name explicitly.
			// This will attach the file to the most appropriate definition element (data item) of the entry type (which is by default "upload").
			FolderUtils.modifyLibraryEntry(entry, filename, is, modDate, true);
		}
		else {
			if (Validator.isNull(dataName)) 
				dataName="ss_attachFile1";
			Map options = null;
			MultipartFile mf;
			if(modDate != null) {
				options = new HashMap();
				options.put(ObjectKeys.INPUT_OPTION_NO_MODIFICATION_DATE, Boolean.TRUE);
				mf = new DatedMultipartFile(filename, is, modDate);
			}
			else {
				mf = new SimpleMultipartFile(filename, is); 					
			}
			Map fileItems = new HashMap(); // Map of names to file items	
			fileItems.put(dataName, mf); // single file item
			getFolderModule().modifyEntry(null, entry.getId(), 
					new EmptyInputData(), fileItems, null, null, options);
		}
	}

	public static void modifyUserWithFile(Principal user, String dataName,
			String filename, InputStream is, Date modDate)
			throws AccessControlException, ReservedByAnotherUserException,
			WriteFilesException, WriteEntryDataException {
		if (Validator.isNull(dataName))
			dataName = "ss_attachFile1";
		Map options = null;
		MultipartFile mf;
		if (modDate != null) {
			options = new HashMap();
			options.put(ObjectKeys.INPUT_OPTION_NO_MODIFICATION_DATE, Boolean.TRUE);
			mf = new DatedMultipartFile(filename, is, modDate);
		} else {
			mf = new SimpleMultipartFile(filename, is);
		}
		Map fileItems = new HashMap();
		fileItems.put(dataName, mf);
		getProfileModule().modifyEntry(user.getId(), new EmptyInputData(), fileItems, null, null, options);
	}

	public static void modifyBinderWithFile(Binder binder, String dataName,
			String filename, InputStream is)
			throws AccessControlException, ReservedByAnotherUserException,
			WriteFilesException, WriteEntryDataException {
		if (Validator.isNull(dataName))
			dataName = "ss_attachFile1";
		MultipartFile mf = new SimpleMultipartFile(filename, is);
		Map fileItems = new HashMap();
		fileItems.put(dataName, mf);
		getBinderModule().modifyBinder(binder.getId(), new EmptyInputData(), fileItems, null, null);
	}
	private static FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}

	private static ProfileModule getProfileModule() {
		return (ProfileModule) SpringContextUtil.getBean("profileModule");
	}

	private static BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("profileModule");
	}
}
