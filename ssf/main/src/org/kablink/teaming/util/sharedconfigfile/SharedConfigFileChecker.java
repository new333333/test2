/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.util.sharedconfigfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

public class SharedConfigFileChecker implements InitializingBean {

	private static Log logger = LogFactory.getLog(SharedConfigFileChecker.class);
	
	private SharedConfigFile sharedConfigFile;
	private long lastKnownModifiedTime;
	private List<FileModificationListener> modificationListeners;
	
	public void setConfigFileName(String configFileName) throws FileNotFoundException {
		this.sharedConfigFile = new SharedConfigFile(configFileName);
	}

	public void setModificationListeners(List<FileModificationListener> modificationListeners) {
		this.modificationListeners = modificationListeners;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.lastKnownModifiedTime = sharedConfigFile.getFile().lastModified();
		logger.info("Last known modified time is initialized to " + lastKnownModifiedTime + " for file '" + sharedConfigFile.getFile().getAbsolutePath() + "'");
	}

	public void check() {
		File file = sharedConfigFile.getFile();
		long lastModified = file.lastModified();
		// Do not use for comparison the last local time at which this checking was performed
		// within the node executing this code. Instead, compare the current last-modified time
		// of the file with the last known modified time associated with the file. This way,
		// we only use the time attributes associated/stored with the file, and avoid problems
		// that can arise from the differences between the clocks on different nodes.
		if(lastModified > lastKnownModifiedTime) {
			// The file has changed since this object was instantiated.
			logger.info("Last known modified time has changed to " + lastKnownModifiedTime + " for file '" + sharedConfigFile.getFile().getAbsolutePath() + "' - Notifying listeners.");
			this.notifyModificationListeners(file);
			lastKnownModifiedTime = lastModified;
		}
	}
	
	protected void notifyModificationListeners(File file) {
		for(FileModificationListener listener:modificationListeners) {
			listener.fileModified(file);
		}
	}

}
