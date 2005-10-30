package org.red5.server.rtmp.message;

public class Ping extends Packet {
	
	private static final int INITIAL_CAPACITY = 6;
	
	private short value1 = 0;
	private int value2 = 0;
	
	public Ping(){
		super(INITIAL_CAPACITY);
	}

	public short getValue1() {
		return value1;
	}

	public void setValue1(short value1) {
		this.value1 = value1;
	}

	public int getValue2() {
		return value2;
	}

	public void setValue2(int value2) {
		this.value2 = value2;
	}

	protected void doRelease() {
		value1 = 0;
		value2 = 0;
	}
	
	public String toString(){
		return "Ping: "+value1+", "+value2;
	}
	
}
