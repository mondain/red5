package org.red5.stream;

public interface IFrame {

	enum Service { LIVE, ON_DEMAND };
	enum Type { AUDIO, VIDEO, DATA };

	public Type getService();
	public Type getType();

	public int getSequence();
	public int getTime();
	public int getSize();

	public ICodec getCodec();
	public IData getData();

	public ISender getOrigin();

	public IFrame clone();
	public void destroy();

}
