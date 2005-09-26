package org.red5.server.io.test;

public class SimpleJavaBean {

	private String nameOfBean = "jeff";

	public String getNameOfBean() {
		return nameOfBean;
	}

	public void setNameOfBean(String nameOfBean) {
		this.nameOfBean = nameOfBean;
	}
	
	public boolean equals(Object obj){
		if(obj instanceof SimpleJavaBean){
			SimpleJavaBean sjb = (SimpleJavaBean) obj;
			return sjb.getNameOfBean() == sjb.getNameOfBean();
		}
		return false;
	}
	

}
