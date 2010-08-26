package org.red5.xuggler;

import java.nio.ByteBuffer;

import org.red5.server.net.rtmp.event.AudioData;
import org.red5.server.net.rtmp.event.Notify;
import org.red5.server.net.rtmp.event.VideoData;

/**
 * Adapter class for "message" data, which are passed around via the handlers.
 * 
 * @author Paul Gregoire (paul@infrared5.com)
 */
public class Message {

	/**
	 * The types of messages that can be put into
	 */
	public enum Type {
		/**
		 * A configuration packet.
		 */
		CONFIG,
		/**
		 * A special configuration packet used in mpeg-ts
		 */
		CONFIG_PAT,
		/**
		 * A HEADER. The payload is usually null.
		 */
		HEADER,
		/**
		 * A Video B or P MPEG frame (interframe). The payload is usually a
		 * {@link VideoData} object.
		 */
		INTERFRAME,
		/**
		 * A Disposable Interframe. The payload is usually a {@link VideoData}
		 * object.
		 * 
		 * Note that most FFMPEG decoders seem to choke on these frames, so feel
		 * free to dispose them.
		 */
		DISPOSABLE_INTERFRAME,
		/**
		 * A Video I MPEG frame (key frame). The payload is usually a
		 * {@link VideoData} object.
		 */
		KEY_FRAME,
		/**
		 * An audio packet. The payload is usually a {@link AudioData} object.
		 */
		AUDIO,
		/**
		 * Simple media data, may be audio, video, or special transport data type.
		 */
		DATA,
		/**
		 * Some other type of data. For FLV METADATA can creep in here. The
		 * payload is usually a {@link Notify} object.
		 */
		OTHER,
		/** 
		 * A null packet, normally used in mpeg-ts
		 */
		NULL,
		/**
		 * A end of stream marker. The payload is usually null.
		 */
		END_STREAM;

		/**
		 * Is this a bit of configuration?
		 * 
		 * @return true if this contains configuration data
		 */
		public boolean isConfig() {
			return this == CONFIG;
		}
		
		/**
		 * Is this message audio data?
		 * 
		 * @return true if audio; false if not.
		 */
		public boolean isAudio() {
			return this == AUDIO;
		}

		/**
		 * Is this message video data?
		 * 
		 * @return true if any type of video; false if not.
		 */
		public boolean isVideo() {
			return (this == INTERFRAME || this == KEY_FRAME || this == DISPOSABLE_INTERFRAME);
		}

		/**
		 * Is this message other data?
		 * 
		 * @return true if other; false if not.
		 */
		public boolean isOther() {
			return this == OTHER;
		}

		/**
		 * Is this an end of stream marker?
		 * 
		 * @return true if the end; false if not.
		 */
		public boolean isEnd() {
			return this == END_STREAM;
		}

		/**
		 * Is this the beginning of a stream?
		 * 
		 * @return true if a header; false if not.
		 */
		public boolean isHeader() {
			return this == HEADER;
		}
	}

	protected Type type;

	protected ByteBuffer data;

	public Message() {
		
	}
	
	public Message(Type type, ByteBuffer data) {
		this.type = type;
		this.data = data;
	}
	
	public void setData(ByteBuffer data) {
		this.data = data;
	}

	/**
	 * Returns the data in this message.
	 * 
	 * @return The data in this message
	 */
	public ByteBuffer getData() {
		return data;
	}

	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * Returns the type of this red5 message
	 * 
	 * @return The type
	 */
	public Type getType() {
		return type;
	}

}
