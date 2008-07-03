package com.sitescape.team.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.CollectionUtil.Func2;
import com.sitescape.team.util.CollectionUtil.Func1;
import com.sitescape.team.util.CollectionUtil.Predicate;

public class CollectionUtilTest extends AbstractTestBase {
	
	@Test
	public void map() throws Exception {
		List<Integer> xs = new ArrayList<Integer>();
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 100; ++i) {
			xs.add(r.nextInt());
		}
		List<Integer> ys = CollectionUtil.map(new Func1<Integer, Integer>() {
			public Integer apply(Integer x) {
				return Integer.parseInt(x.toString());
			}
		}, xs);
		assertArrayEquals(xs.toArray(), ys.toArray());
	}
	
	@Test
	public void foldl() throws Exception {
		List<Integer> xs = new ArrayList<Integer>();
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 100; ++i) {
			xs.add(r.nextInt());
		}
		int s0 = 0;
		for (int x : xs) {
			s0 += x;
		}
		int s = CollectionUtil.foldl(new Func2<Integer, Integer, Integer>() {
			public Integer apply(Integer a, Integer b) {
				return a + b;
			}
		}, 0, xs);
		assertEquals(s0, s);
	}
	
	@Test
	public void filter() throws Exception {
		List<Integer> xs = new ArrayList<Integer>();
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 100; ++i) {
			xs.add(r.nextInt());
		}
		List<Integer> xs0 = new ArrayList<Integer>();
		for (int x : xs) {
			if (x % 2 == 0) {
				xs0.add(x);
			}
		}
		List<Integer> xs1 = CollectionUtil.filter(new Predicate<Integer>() {
			public Boolean apply(Integer x) {
				return x % 2 == 0;
			}
		}, xs);
		assertArrayEquals(xs0.toArray(), xs1.toArray());
	}

}
