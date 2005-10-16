package org.red5.server.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * This service is to be used for John Grden's deomnstration of the Red5
 * prototype presented on Friday October 21st, 2005 at the OFLA Online: The
 * First Online Open Source Flash Conference.
 * 
 * @author Chris Allen mrchrisallen@gmail.com
 * 
 */

public class DemoService extends FileSystemXmlApplicationContext implements  ApplicationContextAware {
	protected static Log log = LogFactory.getLog(DemoService.class.getName());
	
	public DemoService(String context) throws BeansException {
		super(context);
	}

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.setParent(context);
	}
	
	public Map getListOfAvailableFLVs() {
		Map filesMap = new HashMap();
		Map fileInfo;
		try {
			log.debug("getting the FLV files");
			Resource[] flvs = getResources("flvs/*.flv");
			if(flvs!=null){
				for(int i=0; i<flvs.length; i++){
					Resource flv = flvs[i];
					File file = flv.getFile();
					Date lastModifiedDate = new Date(file.lastModified());
					String lastModified = formatDate(lastModifiedDate);
					String flvName = flv.getFile().getName();
					String flvBytes = new Long(file.length()).toString();
		
					log.debug("flvName: " + flvName);
					log.debug("lastModified date: " + lastModified);
					log.debug("flvBytes: " + flvBytes);
					log.debug("-------");
					
					fileInfo = new HashMap();
					fileInfo.put("name", flvName);
					fileInfo.put("lastModified", lastModified);
					fileInfo.put("size", flvBytes);
					filesMap.put(flvName, fileInfo);
					fileInfo = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filesMap;
	}	
	
	private String formatDate(Date date) {
		SimpleDateFormat formatter;
		String pattern = "dd/MM/yy H:mm:ss";
		Locale locale= new Locale("en","US");
		formatter = new SimpleDateFormat(pattern, locale);
		return formatter.format(date);
	}
	
}