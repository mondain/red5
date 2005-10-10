package org.red5.server.service.test;

import java.util.ArrayList;
import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.io.test.TestJavaBean;
import org.red5.server.service.ConversionUtils;

public class ConversionUtilsTest extends TestCase {

	private static final Log log = LogFactory.getLog(ConversionUtilsTest.class);
	
	public void testBasic(){
		Object result = ConversionUtils.convert(new Integer(42), String.class);
		if(!(result instanceof String)) Assert.fail("Should be a string");
		String str = (String) result;
		Assert.assertEquals("42",str);
	}
	
	public void testConvertListToStringArray(){
		ArrayList source = new ArrayList();
		
		source.add("Testing 1");
		source.add("Testing 2");
		source.add("Testing 3");
		
		Class target = (new String[0]).getClass();
		
		Object result = ConversionUtils.convert(source,target);
		if(!(result.getClass().isArray() 
				&& result.getClass().getComponentType().equals(String.class))){
				Assert.fail("Should be String[]");
		}
		String[] results = (String[]) result;
		
		Assert.assertEquals(results.length, source.size());
		Assert.assertEquals(results[2], source.get(2));		

	}
	
	public void testConvertObjectArrayToStringArray(){
		Object[] source = new Object[3];
		
		source[0]= new Integer(21);
		source[1] = Boolean.FALSE;
		source[2] = "Woot";
		
		Class target = (new String[0]).getClass();
		
		Object result = ConversionUtils.convert(source,target);
		if(!(result.getClass().isArray() 
				&& result.getClass().getComponentType().equals(String.class))){
				Assert.fail("Should be String[]");
		}
		String[] results = (String[]) result;
		
		Assert.assertEquals(results.length, source.length);
		Assert.assertEquals(results[2], source[2]);

	}
	
	public void testNoOppConvert(){
		TestJavaBean source = new TestJavaBean();
		Object result = ConversionUtils.convert(source, TestJavaBean.class);
		Assert.assertEquals(result, source);
	}
	
	public void testNullConvert(){
		Object result = ConversionUtils.convert(null, TestJavaBean.class);
		Assert.assertNull(result);
		result = ConversionUtils.convert(new TestJavaBean(), null);
		Assert.assertNull(result);
	}
	
	public void testConvertToSet(){
		Object[] source = new Object[3];
		source[0]= new Integer(21);
		source[1] = Boolean.FALSE;
		source[2] = "Woot";
		Object result = ConversionUtils.convert(source, Set.class);
		if(!(result instanceof Set)) Assert.fail("Should be a set");
		Set results = (Set) result;
		Assert.assertEquals(results.size(), source.length);
		
	}
	
}
