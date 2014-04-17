/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.simplefile;

/**
 * @author jong
 *
 */
public class SimpleFileUtil {

	/**
	 * Produces 32-bit hash code suitable for persistent storage (in other word, the value of hash code
	 * should never change for the same string value).
	 * 
	 * IMPLEMENTATION NOTE:
	 * 
	 * This implementation is copied from String.hashCode() implementation from Sun JDK 1.7.0.
	 * We cannot just call String.hashCode() because the specification doesn't guarantee that
	 * the hashCode method will produce the same value across different JVM versions and vendors.
	 * 
	 * Although it is known that the JDK hashCode is subject to collision, the collision rate
	 * should remain acceptably low when hash value is obtained on random file path strings.
	 * For example, according to my experiment with the random file/folder path names on my
	 * laptop computer, the number of hash collisions were less than 200 out of a million. 
	 *
	 * @param str
	 * @return
	 */
	public static int persistentHashCode(String path) {
		if(path == null)
			throw new IllegalArgumentException("String must be specified");
			
		char[] value = path.toCharArray();		
        int h = 0;
        for (int i = 0; i < value.length; i++) {
            h = 31 * h + value[i];
        }
        return h;
	}

}
