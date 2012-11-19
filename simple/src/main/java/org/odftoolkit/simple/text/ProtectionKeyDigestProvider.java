package org.odftoolkit.simple.text;

public interface ProtectionKeyDigestProvider {

	/**
	 * Generate a digest value of the input key
	 * 
	 * @param key
	 *            -an key required to be digest
	 * @return the digest result
	 */
	public String generateHashKey(String key);

	public String getProtectionKeyDigestAlgorithm();

}
