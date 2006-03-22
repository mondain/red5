package org.red5.io.flv.meta;

/**
 * ICuePoint defines contract methods for use with 
 * cuepoints
 * 
 * @author The Red5 Project (red5@osflash.org)
 * @author Dominick Accattato (daccattato@gmail.com)
 * @version 0.3
 */
public interface IMetaCue extends IMeta, Comparable {
	
	
	/**
	 * Sets the name
	 * @param String name
	 * @return void
	 * 
	 */
	public void setName(String name);
	
	/**
	 * Gets the name
	 * @return String name
	 * 
	 */
	public String getName();
	
	/**
	 * Sets the type
	 * type can be "event" or "navigation"
	 * @param String type
	 * @return void 
	 *
	 */
	public void setType(String type);
	
	/**
	 * Gets the type
	 * @return String type 
	 *
	 */
	public String getType();
	
	/**
	 * Sets the time
	 * @param double d
	 * @return void 
	 *
	 */
	public void setTime(double d);
	
	/**
	 * Gets the time
	 * @return double time 
	 *
	 */
	public double getTime();
}
