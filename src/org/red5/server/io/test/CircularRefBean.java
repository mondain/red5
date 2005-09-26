package org.red5.server.io.test;

public class CircularRefBean extends SimpleJavaBean {

	private CircularRefBean refToSelf;
	
	public CircularRefBean(){
		super();
	}

	public CircularRefBean getRefToSelf() {
		return refToSelf;
	}

	public void setRefToSelf(CircularRefBean refToSelf) {
		this.refToSelf = refToSelf;
	}
	
	

}
