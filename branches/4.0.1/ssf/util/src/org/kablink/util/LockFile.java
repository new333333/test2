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
package org.kablink.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LockFile {
	protected Log logger = LogFactory.getLog(getClass());

	private Random random=null;

	private FileLock fileLock = null;

	private FileChannel fileChannel = null;

	private File lockFile=null;

	public LockFile(File lockFile) {
		this.lockFile = lockFile;
	}

	public boolean getLock() {
		int tryCount = 0;
		if (!lockFile.exists()) {
			try {
				lockFile.createNewFile();
			} catch (Exception e) {
				logger.info(e.toString());
			}
		}
		try {
			fileChannel = new RandomAccessFile(lockFile, "rw").getChannel();
			while (tryCount < 5) {
				fileLock = fileChannel.tryLock();
				if (fileLock != null) return true;
				if (random == null) random = new Random();
				Thread.sleep(random.nextInt(20));
				//Thread.sleep(500);
				tryCount++;
			}
			//delete the lockfile and try again
			if (lockFile.exists()) {
				lockFile.delete();
				return getLock();
			}
		} catch (Exception ignore) {}
		return false;
	}



	public boolean releaseLock() {
		try {
			fileLock.release();
			fileChannel.close();
			return true;
		} catch (Exception e) {
			logger.info("Couldn't release lock " + lockFile);
		} finally {
			try {
				fileChannel.close();
			} catch (Exception ignore) {}
		}
		return false;
	}
	
	public void releaseLockIfValid() {
		if (fileLock.isValid())
			releaseLock();
	}

}
