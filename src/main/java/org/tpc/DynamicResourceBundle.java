package org.tpc;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides an extensible resource bundle.
 * Additional resource can be added / removed with the <code>add</code>
 * and <code>remove</code> method.
 * 
 * @author Tobias Faller
 *
 */
public class DynamicResourceBundle extends ResourceBundle {

	private Map<String, String> values;

	public DynamicResourceBundle() {
		values = new ConcurrentHashMap<>();
	}

	/**
	 * Adds this key value pair to the bundle and overwrites the value if
	 * the key already exists.
	 * 
	 * @param key The key of the entry
	 * @param value The value to set for the entry
	 * @return This instance
	 */
	public ResourceBundle add(String key, String value) {
		values.put(key, value);
		return this;
	}

	/**
	 * Removes the key value pair from this bundle if the key exists.
	 * This instance stays unchanged if the key does not exist.
	 * 
	 * @param key The key to remove from this bundle
	 * @return This instance
	 */
	public ResourceBundle remove(String key) {
		values.remove(key);
		return this;
	}

	/**
	 * Returns if the key exists in this resource bundle.
	 * 
	 * @param key The key to check fo
	 * @return <code>true</code> if the key exists, false otherwise
	 */
	public boolean exists(String key) {
		return values.containsKey(key);
	}

	/**
	 * Returns the number of key value pairs stored in this resource bundle.
	 * 
	 * @return The number of entries
	 */
	public int getSize() {
		return values.size();
	}

	@Override
	public Enumeration<String> getKeys() {
		final Iterator<String> keys = values.keySet().iterator();
		return new Enumeration<String>() {
			@Override
			public String nextElement() {
				return keys.next();
			}
			
			@Override
			public boolean hasMoreElements() {
				return keys.hasNext();
			}
		};
	}

	@Override
	protected Object handleGetObject(String key) {
		return values.get(key);
	}

	@Override
	public String toString() {
		return values.toString();
	}

}
