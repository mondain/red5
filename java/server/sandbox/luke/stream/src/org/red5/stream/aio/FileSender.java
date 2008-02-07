package org.red5.stream.aio;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import org.apache.aio.AioCallbackException;
import org.apache.aio.AioCompletionHandler;
import org.apache.aio.AioFuture;
import org.apache.aio.AsynchronousFileChannel;
import org.apache.aio.AsynchronousFileChannelFactory;
import org.apache.aio.Modes;
import org.apache.aio.AioFuture.ByteBufferFuture;
import org.red5.stream.IControl;
import org.red5.stream.IReceiver;
import org.red5.stream.ISender;
import org.red5.stream.ITicklish;

import sun.misc.HexDumpEncoder;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class FileSender implements ISender, ITicklish, IControl {

	// Read the meta data
	// Grab the data from the file
	// Push down the piple
	// Keep track of position, duration, etc.

	protected AsynchronousFileChannel channel;
	protected long position = 0;

	protected double speed = SPEED_STOP;
	protected State state = State.STOPPED;

	public FileSender(File file){
		AioFuture<AsynchronousFileChannel> future = AsynchronousFileChannelFactory.open(file, Modes.READ_ONLY);
		try {
			channel = future.get();
		} catch (AioCallbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setReceiver(IReceiver receiver) {
		// TODO Auto-generated method stub
	}

	public boolean tickle(long interval, long now) {
		System.err.println(now);
		ByteBuffer buffer = ByteBuffer.allocate(100);
		channel.read(buffer, position);
		position += 100;
		final HexDumpEncoder dumper = new HexDumpEncoder();
	       AioFuture<Integer> readFuture = channel.read(buffer, 0);
	        readFuture.addCompletionHandler(new AioCompletionHandler<Integer>() {
	            public void onCompletion(AioFuture<Integer> future) {
	                ByteBufferFuture byteBufferFuture = (ByteBufferFuture)future;
	                ByteBuffer buffer = byteBufferFuture.getByteBuffer();
	                buffer.flip();
	                System.err.println(dumper.encode(buffer));
	        }    }
	      );

		return true;
	}

	public void play() {
		state = State.PLAYING;
		setSpeed(SPEED_NORMAL);
	}

	public void pause() {
		state = State.PAUSED;
		setSpeed(SPEED_STOP);
	}

	public void forward(double speed) {
		state = State.SEEKING;
		setSpeed(speed);
	}

	public void rewind(double speed) {
		state = State.SEEKING;
		setSpeed(speed * -1);
	}

	public void stop() {
		state = State.STOPPED;
		setSpeed(SPEED_STOP);
		seek(0);
	}

	public void seek(int position) {
		double original = speed;
		setSpeed(SPEED_STOP);
		// do the seek here.
		setSpeed(original);
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getSpeed() {
		return speed;
	}

	public State getState() {
		return state;
	}

}
