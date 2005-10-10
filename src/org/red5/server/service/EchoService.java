package org.red5.server.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.Standalone;
import org.w3c.dom.Document;

/**
 * The Echo service is used to test all of the different datatypes 
 * and to make sure that they are being returned properly.
 * 
 * @author Chris Allen mrchrisallen@gmail.com
 *
 */
public class EchoService implements IEchoService {

	private Log log = LogFactory.getLog(Standalone.class.getName());
	
	public void startUp() {
		log.info("The Echo Service has started...");
	}
	
	/** 
	 * @see org.red5.server.service.IEchoService#echoBoolean(boolean)
	 */
	public boolean echoBoolean(boolean bool) {
		return bool;
	}
	
	/**
	 * @see org.red5.server.service.IEchoService#echoNumber(float)
	 */
	public double echoNumber(double number) {
		return number;
	}
	
	/**
	 * @see org.red5.server.service.IEchoService#echoString(java.lang.String)
	 */
	public String echoString(String string) {
		return string;
	}
	
	/**
	 * @see org.red5.server.service.IEchoService#echoDate(java.util.Date)
	 */
	public Date echoDate(Date date) {
		return date;
	}
	
	/**
	 * @see org.red5.server.service.IEchoService#echoObject(java.util.Map)
	 */
	public Map echoObject(Map obj) {
		return obj;
	}
	
	/**
	 * @see org.red5.server.service.IEchoService#echoArray(java.lang.Object[])
	 */
	public Object[] echoArray(Object[] array) {
		return array;
	}
	
	/**
	 * @see org.red5.server.service.IEchoService#echoList(java.util.List)
	 */
	public List echoList(List list) {
		return list;
	}
	
	/**
	 * @see org.red5.server.service.IEchoService#echoXML(org.w3c.dom.Document)
	 */
	public Document echoXML(Document xml) {
		return xml;
	}

	public Object[] echoMultiParam(Map team, List words, String str){
		Object[] result = new Object[3];
		result[0] = team;
		result[1] = words;
		result[2] = str;
		return result;
	}
	
}
