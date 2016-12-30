package org.tpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Provides an interface to include multiple resource bundles in one instance.
 * Multiple resource bundles can be assigned to the same prefix and share  
 * Each bundle can be prefixed with a chosen string value or added to the list of default bundles.
 * Each prefix is separated from the key by the set separator (default is the dot ".").
 * If multiple bundles exist with the same separator the order in which they are checked is unspecified.
 * 
 * @author Tobias Faller
 *
 */
public class PrefixedResourceBundle extends ResourceBundle {

	private static final String DEFAULT_SEPARATOR = ".";

	public static String getDefaultSeparator() {
		return DEFAULT_SEPARATOR;
	}

	protected Map<String, Collection<ResourceBundle>> bundles;
	protected Collection<ResourceBundle> defaultBundles;
	protected String separator;

	/**
	 * Creates an new instance with the default separator.
	 */
	public PrefixedResourceBundle() {
		this(DEFAULT_SEPARATOR);
	}

	/**
	 * Creates a new instance with a provided separator.
	 * The separator can not be empty or null.
	 * 
	 * @param separator The separator to use
	 */
	public PrefixedResourceBundle(String separator) {
		checkAndAssignSeparator(separator);
		
		bundles = new TreeMap<>(Comparator.reverseOrder());
		defaultBundles = new ArrayList<>();
	}

	/**
	 * Sets the separator to the provided value.
	 * The separator cannot be <code>null</code> or empty.
	 * 
	 * @param separator The separator to use
	 */
	public void setSeparator(String separator) {
		checkAndAssignSeparator(separator);
	}

	/**
	 * Adds a default <code>ResourceBundle</code> without a prefix.
	 * 
	 * @param defaultBundle The <code>ResourceBundle</code> to add
	 */
	public void addDefaultBundle(ResourceBundle defaultBundle) {
		defaultBundles.add(defaultBundle);
	}

	/**
	 * Returns a collection of the current default <code>ResourceBundle</code>s.
	 * 
	 * @return A collection with the current <code>ResourceBundle</code>s 
	 */
	public Collection<ResourceBundle> getDefaultBundles() {
		return Collections.unmodifiableCollection(defaultBundles);
	}

	/**
	 * Returns a collection of the current <code>ResourceBundle</code>s
	 * for the passed prefix.
	 * 
	 * @return A collection with the current <code>ResourceBundle</code>s 
	 */
	public Collection<ResourceBundle> getBundles(String prefix) {
		if(bundles.containsKey(prefix)) {
			return Collections.unmodifiableCollection(bundles.get(prefix));
		}

		return Collections.emptySet();
	}

	@Override
	protected Object handleGetObject(final String key) {
		String searchKey = key;
		int keyLength = key.length();

		// Search in prefixed bundles (tree map in reverse order)
		for(Map.Entry<String, Collection<ResourceBundle>> bundles : this.bundles.entrySet()){
			String prefix = bundles.getKey();
			int prefixLength = prefix.length();

			if((keyLength > prefixLength) && key.startsWith(prefix + separator)) {
				// Search key has at least one character (keyLength > prefixLength)
				searchKey = key.substring(prefixLength + separator.length());
				
				for(ResourceBundle bundle : bundles.getValue()) {
					if(bundle.containsKey(searchKey)) {
						return bundle.getObject(searchKey);
					}
				}
			}
		}

		// Value was not found in any of the prefixed bundles
		// -> Search in default ones
		for(ResourceBundle bundle : defaultBundles) {
			if(bundle.containsKey(key)) {
				return bundle.getObject(key);
			}
		}

		throw new MissingResourceException("The reource with key " + key
				+ " was not found!", "java.lang.Object", key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return new Enumeration<String>() {
			Iterator<Map.Entry<String, Collection<ResourceBundle>>>
					bundleSetIterator = bundles.entrySet().iterator();

			String currentPrefix = null;
			Collection<ResourceBundle> currentBundleSet = defaultBundles;
			Iterator<ResourceBundle> currentBundlesIterator = null;
			Enumeration<String> currentEnumeration = null;

			@Override
			public String nextElement() {
				if(!hasMoreElements()) {
					return null;
				}

				if(currentPrefix != null) {
					return currentPrefix + separator
							+ currentEnumeration.nextElement();
				}

				return currentEnumeration.nextElement();
			}

			@Override
			public boolean hasMoreElements() {
				if(currentBundleSet == null) { // No default bundle-set
					if(!getNextBundleSet()) {
						return false;
					}
				} else if(currentBundlesIterator == null){
					currentBundlesIterator = currentBundleSet.iterator();
				}

				for(;;) {
					if(currentEnumeration == null
							|| !currentEnumeration.hasMoreElements()) {
						while(!currentBundlesIterator.hasNext()) {
							if(!getNextBundleSet()) {
								return false;
							}
						}

						currentEnumeration
								= currentBundlesIterator.next().getKeys();
						continue;
					}

					return true; // CurrentEnumeration has more elements
				}
			}

			boolean getNextBundleSet() {
				if(!bundleSetIterator.hasNext()) {
					return false;
				}

				Map.Entry<String, Collection<ResourceBundle>> bundleSetEntry
						= bundleSetIterator.next();
				currentBundleSet = bundleSetEntry.getValue();
				currentPrefix = bundleSetEntry.getKey();
				currentBundlesIterator = currentBundleSet.iterator();
				currentEnumeration = null;

				return true;
			}
		};
	}

	/**
	 * Adds a <code>ResourceBundle</code> with a prefix to this instance.
	 * If the prefix is <code>null</code> or a whitespace-string,
	 * then the bundle will be added as default bundle.
	 * 
	 * @param prefix The prefix to use
	 * @param bundle The bundle to chain under the specified prefix
	 */
	public void addBundle(String prefix, ResourceBundle bundle) {
		String trimmedPrefix;

		if(prefix == null || (trimmedPrefix = prefix.trim()).isEmpty()) {
			addDefaultBundle(bundle);
			return;
		}

		Collection<ResourceBundle> bundleList = bundles.get(trimmedPrefix);

		if(bundleList == null) {
			bundleList = new ArrayList<>();
			bundles.put(trimmedPrefix, bundleList);
		}

		bundleList.add(bundle);
	}
	
	/**
	 * Checks and assigns the separator value.
	 * 
	 * @param separator The separator string to check
	 */
	private void checkAndAssignSeparator(String separator) {
		if(separator == null || (this.separator = separator.trim()).isEmpty())
			throw new IllegalArgumentException( "Separator cannot be null or empty!");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append('{');

		Enumeration<String> keys = getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();

			sb.append(key);
			sb.append('=');
			sb.append(getString(key));
			
			if (keys.hasMoreElements()) {
				sb.append(", ");
			}
		}

		sb.append('}');
		
		return sb.toString();
	}
}
