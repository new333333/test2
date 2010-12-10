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
package org.kablink.teaming.module.file;

import java.io.IOException;
import java.io.InputStream;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;

public interface ContentFilter {

	/**
	 * Applies filtering on the file content.
	 * 
	 * @param binder binder object closest from the file
	 * @param entity entity to which the file is being attached, may be an entry or a binder. 
	 * If the file is being attached to a binder, then this value is identical to the value
	 * of the <code>binder</code> parameter.
	 * @param fileName name of the input file
	 * @param content content of the input file
	 * @throws FilterException Thrown to indicate that the file failed to pass the filtering
	 * @throws IOException Thrown to indicate that there is an I/O error
	 */
	public void filter(Binder binder, DefinableEntity entity, String fileName, InputStream content) throws FilterException, IOException;
	
}
