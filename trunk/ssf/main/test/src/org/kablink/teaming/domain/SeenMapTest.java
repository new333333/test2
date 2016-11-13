/**
 * Copyright (c) 2008-2016 Novell, Inc. All Rights Reserved. THIS WORK IS AN
 * UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL PROPRIETARY AND TRADE SECRET
 * INFORMATION OF NOVELL, INC. ACCESS TO THIS WORK IS RESTRICTED TO NOVELL,INC.
 * EMPLOYEES WHO HAVE A NEED TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE
 * OF THEIR ASSIGNMENTS AND ENTITIES OTHER THAN NOVELL, INC. WHO HAVE
 * ENTERED INTO APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE
 * USED, PRACTICED, PERFORMED COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED,
 * RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF NOVELL,
 * INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 */
package org.kablink.teaming.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import gnu.trove.map.hash.TLongLongHashMap;

/**
 * @author Jong
 *
 */
public class SeenMapTest {

	@Test
	public void testCompareSerializedMapSizes() throws IOException, ClassNotFoundException {
		// MySQL, longblob - 4294967295 bytes (4 GB)
		// Oracle, blob - 2 GB
		// PostgreSQL, bytea - 1 GB
		// MSSQL, image - 2 GB
		
		long testSize = 20_000;
		System.out.println("Test size: " + testSize);
		
		long now = System.currentTimeMillis();
		Map<Long,Date> dateMap = new HashMap<Long,Date>();
		for(long i = 0; i < testSize; i++)
			dateMap.put(i, new Date(now+i));
		byte[] dateMapBytes = toByteArray(dateMap);
		System.out.println("Date map serialized length: " + dateMapBytes.length + " (" + (System.currentTimeMillis()-now) + ")");
		now = System.currentTimeMillis();
		Map<Long,Date> dateMap2 = (Map<Long,Date>) toObject(dateMapBytes);
		System.out.println("Date map deserialized size: " + dateMap2.size() + " (" + (System.currentTimeMillis()-now) + ")");
		Assert.assertEquals(dateMap.size(), dateMap2.size());
		
		now = System.currentTimeMillis();
		Map<Long,Long> timeMap = new HashMap<Long,Long>();
		for(long i = 0; i < testSize; i++)
			timeMap.put(i, now+i);
		byte[] timeMapBytes = toByteArray(timeMap);	
		System.out.println("Time map serialized length: " + timeMapBytes.length + " (" + (System.currentTimeMillis()-now) + ")");
		now = System.currentTimeMillis();
		Map<Long,Long> timeMap2 = (Map<Long,Long>) toObject(timeMapBytes);
		System.out.println("Time map deserialized size: " + timeMap2.size() + " (" + (System.currentTimeMillis()-now) + ")");
		Assert.assertEquals(timeMap.size(), timeMap2.size());
		
		now = System.currentTimeMillis();
		TLongLongHashMap troveMap = new TLongLongHashMap();
		for(long i = 0; i < testSize; i++)
			troveMap.put(i, now+i);
		//System.out.println("Trove map (1): " + troveMap);
		byte[] troveMapBytes = toByteArray(troveMap);
		System.out.println("Trove map serialized length: " + troveMapBytes.length + " (" + (System.currentTimeMillis()-now) + ")");
		now = System.currentTimeMillis();
		TLongLongHashMap troveMap2 = (TLongLongHashMap) toObject(troveMapBytes);
		System.out.println("Trove map deserialized size: " + troveMap2.size() + " (" + (System.currentTimeMillis()-now) + ")");
		Assert.assertEquals(troveMap.size(), troveMap2.size());
		//System.out.println("Trove map (2): " + troveMap2);
	}
	
	protected byte[] toByteArray(Object obj) throws IOException {
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
				ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(obj);
			oos.flush();
			return baos.toByteArray();
		}
	}

	protected Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
		try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais)) {
			return ois.readObject();
		}
	}
}