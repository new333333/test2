package com.sitescape.team.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import com.sitescape.team.support.AbstractTestBase;

public class SimpleEventSourceTest extends AbstractTestBase {

	private class TestEventSource extends
			SimpleEventSource<TestEventSource, String> { }

	private class TestEventListener implements
			EventListener<TestEventSource, String> {
		private TestEventSource source = null;
		private String event = null;

		public void onNotification(TestEventSource source, String event) {
			this.source = source;
			this.event = event;
		}
	}

	private TestEventSource source = new TestEventSource();
	private List<TestEventListener> notifieds;
	private List<TestEventListener> notNotifieds;

	@Before
	public void setup() throws Exception {
		notifieds = new ArrayList<TestEventListener>(10);
		notNotifieds = new ArrayList<TestEventListener>(10);
		for (int i = 0; i < 10; ++i) {
			TestEventListener n = new TestEventListener();
			notifieds.add(n);
			source.register(n);
			notNotifieds.add(new TestEventListener());
		}
	}

	@Test
	public void notifiesAll() throws Exception {
		String event = RandomStringUtils.random(20);
		source.propagate(event);
		for (TestEventListener l : notifieds) {
			assertEquals(source, l.source);
			assertEquals(event, l.event);
		}
	}
	
	@Test
	public void doNotNotifyUnregistered() throws Exception {
		String event = RandomStringUtils.random(20);
		source.propagate(event);
		for (TestEventListener l : notNotifieds) {
			assertNull(l.source);
			assertNull(l.event);
		}
	}
	
	@Test
	public void setListeners() throws Exception {
		List<TestEventListener> listeners = new ArrayList<TestEventListener>(
				10);
		List<EventListener<TestEventSource, String>> ls = new ArrayList<EventListener<TestEventSource,String>>(10);
		for (int i = 0; i < 10; ++i) {
			TestEventListener n = new TestEventListener();
			listeners.add(n);
			ls.add(n);
		}
		source.setListeners(ls);
		String event = RandomStringUtils.random(20);
		source.propagate(event);
		for (TestEventListener l : listeners) {
			assertEquals(source, l.source);
			assertEquals(event, l.event);
		}
	}

	@Test
	public void setListenersBeanSemantics() throws Exception {
		List<TestEventListener> listeners = new ArrayList<TestEventListener>(
				10);
		List<EventListener<TestEventSource, String>> ls = new ArrayList<EventListener<TestEventSource, String>>(
				10);
		for (int i = 0; i < 10; ++i) {
			TestEventListener n = new TestEventListener();
			listeners.add(n);
			ls.add(n);
		}
		source.setListeners(ls);
		source.propagate(RandomStringUtils.random(20));
		for (TestEventListener l : notifieds) {
			assertNull(l.source);
			assertNull(l.event);
		}
	}
}
