package org.tpc;

import static org.junit.Assert.*;

import java.util.MissingResourceException;

import org.junit.Before;
import org.junit.Test;

public class PrefixedResourceBundleTest {

	private PrefixedResourceBundle bundle;
	
	@Before
	public void setUp() throws Exception {
		bundle = new PrefixedResourceBundle();

		// Default values
		DynamicResourceBundle defBundle = new DynamicResourceBundle();
		defBundle.add("hello.world", "Hello World");
		defBundle.add("hello.again", "Hello Again");
		defBundle.add("this.is", "This is");
		defBundle.add("a.test", "a test!");
		bundle.addDefaultBundle(defBundle);

		DynamicResourceBundle defBundle2 = new DynamicResourceBundle();
		defBundle2.add("yet.another.test", "VALUE");
		defBundle2.add("value.test.another", "yet");
		bundle.addDefaultBundle(defBundle2);

		// a subgroup
		DynamicResourceBundle aBundle = new DynamicResourceBundle();
		aBundle.add("test", "A test!");
		aBundle.add("another", "Another test value ...");
		bundle.addBundle("a", aBundle);
		
		DynamicResourceBundle anotherBundle = new DynamicResourceBundle();
		anotherBundle.add("test.it", "This value gets tested!");
		anotherBundle.add("test.too", "This one too");
		bundle.addBundle("a", anotherBundle);

		// soup.other subgroup
		DynamicResourceBundle soupOtherBundle = new DynamicResourceBundle();
		soupOtherBundle.add("none", "Nothing to serve here");
		bundle.addBundle("soup.other", soupOtherBundle);

		// soup subgroup
		DynamicResourceBundle soupBundle = new DynamicResourceBundle();
		soupBundle.add("leaky house soup", "III Sickles");
		soupBundle.add("soup house leaky", "III Sickles");
		soupBundle.add("house soup leaky", "III Sickles");
		soupBundle.add("leaky soup house", "IV Sickles");
		soupBundle.add("soup leaky house", "IV Sickles");
		soupBundle.add("house leaky soup", "IV Sickles");
		soupBundle.add("leaky,leaky soup", "V Sickles");
		soupBundle.add("house,house soup", "V Sickles");
		soupBundle.add("soup,soup soup", "V Sickles");
		soupBundle.add("another.visible", "Is this value visible?");
		soupBundle.add("another.invisible", "This value is invisible");
		bundle.addBundle("soup", soupBundle);

		// soup.other subgroup
		DynamicResourceBundle soupAnotherBundle = new DynamicResourceBundle();
		soupAnotherBundle.add("yas", "Yet another soup");
		soupAnotherBundle.add("invisible", "This value is visible instead");
		bundle.addBundle("soup.another", soupAnotherBundle);
	}

	@Test
	public void testDefaultBundles() {
		assertEquals("Hello World", bundle.getString("hello.world"));
		assertEquals("Hello Again", bundle.getString("hello.again"));
		assertEquals("This is", bundle.getString("this.is"));
		assertEquals("VALUE", bundle.getString("yet.another.test"));
		assertEquals("yet", bundle.getString("value.test.another"));
	}

	@Test
	public void testPriorization() {
		// Check if default values get overwritten
		assertEquals("A test!", bundle.getString("a.test"));
		
		// Check if base keys get found
		assertEquals("Is this value visible?", bundle.getString("soup.another.visible"));
		
		// Check if base keys are overwritten
		assertEquals("This value is visible instead", bundle.getString("soup.another.invisible"));
	}
	
	@Test(expected = MissingResourceException.class)
	public void testNonExisting() {
		bundle.getString("non.existing.key");
	}
	
	@Test
	public void test() {
		// Check if multiple bundles work under the same prefix
		assertEquals("Another test value ...", bundle.getString("a.another"));
		assertEquals("This value gets tested!", bundle.getString("a.test.it"));
		assertEquals("This one too", bundle.getString("a.test.too"));
	}
	
	@Test
	public void testSoups() {
		// Test all soups
		assertEquals("III Sickles", bundle.getString("soup.leaky house soup"));
		assertEquals("III Sickles", bundle.getString("soup.soup house leaky"));
		assertEquals("III Sickles", bundle.getString("soup.house soup leaky"));
		assertEquals("IV Sickles", bundle.getString("soup.leaky soup house"));
		assertEquals("IV Sickles", bundle.getString("soup.soup leaky house"));
		assertEquals("IV Sickles", bundle.getString("soup.house leaky soup"));
		assertEquals("V Sickles", bundle.getString("soup.leaky,leaky soup"));
		assertEquals("V Sickles", bundle.getString("soup.house,house soup"));
		assertEquals("V Sickles", bundle.getString("soup.soup,soup soup"));

		// Test sub namespaces
		assertEquals("Nothing to serve here", bundle.getString("soup.other.none"));
		assertEquals("Yet another soup", bundle.getString("soup.another.yas"));
	}

}
