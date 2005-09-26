package org.red5.server.io.test;

import java.util.Date;

public class TestJavaBean {

	private byte testByte = 65;
	private int testPrimitiveNumber = 3;
	private Integer testNumberObject = new Integer(33);
	private String testString = "red5 rocks!";
	private Date testDate = new Date();
	private Boolean testBooleanObject = Boolean.FALSE;
	private boolean testBoolean = true;
	
	public byte getTestByte() {
		return testByte;
	}
	public void setTestByte(byte testByte) {
		this.testByte = testByte;
	}
	public boolean isTestBoolean() {
		return testBoolean;
	}
	public void setTestBoolean(boolean testBoolean) {
		this.testBoolean = testBoolean;
	}
	public Boolean getTestBooleanObject() {
		return testBooleanObject;
	}
	public void setTestBooleanObject(Boolean testBooleanObject) {
		this.testBooleanObject = testBooleanObject;
	}
	public Date getTestDate() {
		return testDate;
	}
	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}
	public Integer getTestNumberObject() {
		return testNumberObject;
	}
	public void setTestNumberObject(Integer testNumberObject) {
		this.testNumberObject = testNumberObject;
	}
	public int getTestPrimitiveNumber() {
		return testPrimitiveNumber;
	}
	public void setTestPrimitiveNumber(int testPrimitiveNumber) {
		this.testPrimitiveNumber = testPrimitiveNumber;
	}
	public String getTestString() {
		return testString;
	}
	public void setTestString(String testString) {
		this.testString = testString;
	}
	
}
