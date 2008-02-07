package org.red5.stream;


public class Frame implements IFrame {

	protected Type type;

	protected ICodec codec;
	protected IData data;
	protected ISender sender;

	protected int time = 0;
	protected int size = 0;
	protected int sequence = 0;

	public ICodec getCodec() {
		// TODO Auto-generated method stub
		return null;
	}

	public IData getData() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISender getOrigin() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSequence() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Type getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public IFrame clone(){
		return null;
	}

	public Type getService() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
