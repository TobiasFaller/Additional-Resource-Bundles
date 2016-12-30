package org.tpc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DynamicResourceBundleTest {

	private DynamicResourceBundle bundle;

	@Before
	public void setUp() throws Exception {
		bundle = new DynamicResourceBundle();
	}

	@Test
	public void test() {
		assertEquals(0, bundle.getSize());
		
		bundle.add("hello.world", "value");
		assertEquals(1, bundle.getSize());
		assertEquals("value", bundle.getString("hello.world"));
		assertTrue(bundle.exists("hello.world"));
		
		bundle.add("another.key", "another value");
		assertEquals(2, bundle.getSize());
		assertEquals("value", bundle.getString("hello.world"));
		assertEquals("another value", bundle.getString("another.key"));
		assertTrue(bundle.exists("hello.world"));
		assertTrue(bundle.exists("another.key"));
		
		bundle.add("hello.world", "new value");
		assertEquals(2, bundle.getSize());
		assertEquals("new value", bundle.getString("hello.world"));
		assertEquals("another value", bundle.getString("another.key"));
		assertTrue(bundle.exists("hello.world"));
		assertTrue(bundle.exists("another.key"));
		
		bundle.remove("another.key");
		assertEquals(1, bundle.getSize());
		assertEquals("new value", bundle.getString("hello.world"));
		assertTrue(bundle.exists("hello.world"));
		assertFalse(bundle.exists("another.key"));
	}

}
