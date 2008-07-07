package com.sitescape.team.util;

import static org.junit.Assert.*;
import static com.sitescape.team.util.Maybe.*;

import org.junit.Test;

import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.CollectionUtil.Func0;
import com.sitescape.team.util.CollectionUtil.Func1;

public class MaybeTest extends AbstractTestBase {
	
	@Test
	public void some() throws Exception {
		String some = "some";
		assertEquals(some, maybe(some).or("nothing"));
	}
	
	@Test
	public void nothing() throws Exception {
		assertEquals("nothing", maybe(null).or("nothing"));	
	}
	
	@Test
	public void lazyNothing() throws Exception {
		String nil = null;
		assertEquals("nothing", maybe(nil).orElse(new Func0<String>() {
			public String apply() {
				return "nothing";
			}}));
	}
	
	@Test
	public void lazySome() throws Exception {
		assertEquals("something", maybe("something").orElse(new Func0<String>() {
			public String apply() {
				String neverRun = null;
				return neverRun.toString();
			}}));
	}
	
	@Test
	public void andSome() throws Exception {
		assertEquals("another", maybe("something").and(new Func1<String, String>() {
			public String apply(String x) {
				return "another";
			}}, "nothing"));
	}
	
	@Test
	public void andNothing() throws Exception {
		String nil = null;
		assertEquals("nothing", maybe(nil).and(new Func1<String, String>() {
			public String apply(String x) {
				return "something";
			}}, "nothing"));
	}

}
