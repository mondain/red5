package org.red5.server.net.rtmp.codec;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.red5.io.object.Deserializer;
import org.red5.io.object.Serializer;

public class RTMPCodecFactory implements ProtocolCodecFactory {

	protected Deserializer deserializer = null;
	protected Serializer serializer = null;
	protected RTMPProtocolDecoder decoder;
	protected RTMPProtocolEncoder encoder;
	
	public void init(){
		decoder = new RTMPProtocolDecoder();
		decoder.setDeserializer(deserializer);
		encoder = new RTMPProtocolEncoder();
		encoder.setSerializer(serializer);
	}	
	
	public void setDeserializer(Deserializer deserializer) {
		this.deserializer = deserializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}



	public ProtocolDecoder getDecoder() {
		return decoder;
	}

	public ProtocolEncoder getEncoder() {
		return encoder;
	}

}
