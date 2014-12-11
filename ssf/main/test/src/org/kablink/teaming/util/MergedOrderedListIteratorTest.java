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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.util;

import junit.framework.TestCase;
import org.kablink.teaming.util.MergedOrderedListIterator;

import java.lang.Object;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author david
 *
 */
public class MergedOrderedListIteratorTest extends TestCase {

	private static class IntComparator implements Comparator {
		@Override
		public int compare(Object o1, Object o2) {
			return ((Integer)o1) - ((Integer)o2);
		}
	}

	private List buildList(Integer... values) {
		return Arrays.asList(values);
	}

	public void testMergeThreeLists() {
		MergedOrderedListIterator iterator = new MergedOrderedListIterator(
				new IntComparator(),
				buildList(2, 10, 11, 12),
				buildList(3, 6, 8),
				buildList(1, 4, 5, 7, 9)
		);

		assertTrue(iterator.hasNext());
		assertEquals(1, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(2, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(3, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(4, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(5, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(6, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(7, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(8, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(9, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(10, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(11, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(12, iterator.next());
		assertFalse(iterator.hasNext());
		assertNull(iterator.next());
	}
	
}
