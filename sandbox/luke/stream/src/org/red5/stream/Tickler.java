package org.red5.stream;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.red5.stream.aio.FileSender;

public class Tickler {

	// Tickle those components

	public static final long TICK = 100;

	public Timer timer = new Timer("Timer", true);

	public Set<ITicklish> ticklishs = new HashSet<ITicklish>();

	public void setup(){
		timer.scheduleAtFixedRate(new TicklerTask(), 0, TICK);
		File file = new File("test.flv");
		FileSender fileSender = new FileSender(file);
		ticklishs.add(fileSender);
	}

	protected class TicklerTask extends TimerTask {

		public void run(){
			final long now = System.currentTimeMillis();
			for(ITicklish ticklish : ticklishs)
				ticklish.tickle(TICK, now);
		}

	}

	public static final void main(String[] args) throws Exception {
		Tickler ticker = new Tickler();
		ticker.setup();
		Thread.sleep(100 * TICK);
	}

}
