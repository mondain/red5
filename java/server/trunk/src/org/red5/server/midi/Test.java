package org.red5.server.midi;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Test {

	// Initialize Logging
	protected static Log log = LogFactory.getLog(Test.class.getName());

	public static void main(String[] args) throws Exception {
		Test t = new Test();
	}
	
	public static MidiDevice getMidiDevice(String name){
		
		MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
		
		for (int i = 0; i < info.length; i++) {
			if(info[i].getName().equals(name)) {
				try {
					return MidiSystem.getMidiDevice(info[i]);
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
		
	}

	public Test() throws Exception {

		String MIDI_NAME = "USB Uno MIDI  In";
		MidiDevice dev = getMidiDevice(MIDI_NAME);
		dev.open();
		MyReceiver rec = new MyReceiver();
		dev.getTransmitter().setReceiver(rec);
		Thread.sleep(30000);
		dev.close();

	}

	public class MyReceiver extends Object implements Receiver {

		public void send(MidiMessage midi, long time) {
			byte[] msg = midi.getMessage();
			int len = midi.getLength();
			String out = "Status: "+msg[0]+" Data: [";
			for(int i=1; i<len; i++){
				out += msg[i] + ((i==len-1) ? "" : ","); 
			}
			out += "]";
			log.debug(out);
		}

		public void close() {
			log.debug("Closing");
		}
	}

}