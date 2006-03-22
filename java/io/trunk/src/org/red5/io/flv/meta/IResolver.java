/**
 * 
 */
package org.red5.io.flv.meta;

/**
 * @author The Red5 Project (red5@osflash.org)
 * @author daccattato(daccattato@gmail.com)
 * @version 0.3
 */
public interface IResolver {

	/**
	 * Merges the two Meta objects
	 * @param m1
	 * @param m2
	 * @return IMeta Meta 
	 */
	public IMeta resolve(IMeta m1, IMeta m2);
}
