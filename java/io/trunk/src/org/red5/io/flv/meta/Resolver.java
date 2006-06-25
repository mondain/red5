/**
 * 
 */
package org.red5.io.flv.meta;

/**
 * @author The Red5 Project (red5@osflash.org)
 * @author daccattato(daccattato@gmail.com)
 * @version 0.3
 */
public class Resolver implements IResolver {

	/**
	 * 
	 */
	public Resolver() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.red5.io.flv.meta.IResolver#resolve(org.red5.io.flv.meta.IMeta, org.red5.io.flv.meta.IMeta)
	 */
	public IMeta resolve(IMeta m1, IMeta m2) {

		return m2;
	}


}
