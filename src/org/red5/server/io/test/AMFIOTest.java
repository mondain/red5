package org.red5.server.io.test;

import org.apache.mina.common.ByteBuffer;
import org.red5.server.io.amf.Input;
import org.red5.server.io.amf.Output;
import org.red5.server.utils.HexDump;

public class AMFIOTest extends AbstractIOTest {

	ByteBuffer buf;
	
	void setupIO() {
		buf = ByteBuffer.allocate(0); // 1kb
		buf.setAutoExpand(true);
		in = new Input(buf);
		out = new Output(buf);
	}

	void dumpOutput() {
		buf.flip();
		System.err.println(HexDump.formatHexDump(buf.getHexDump()));
	}

	void resetOutput() {
		 setupIO();
	}

}
