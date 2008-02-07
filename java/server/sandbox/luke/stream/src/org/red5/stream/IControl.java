package org.red5.stream;

public interface IControl {

	public static final double SPEED_STOP = 0.0;
	public static final double SPEED_NORMAL = 1.0;
	public static final double SPEED_FAST = 4.0;
	public static final double SPEED_SLOW = 0.1;
	public static final int POSITION_START = 0;
	public static final int POSITION_END = -1;

	enum State { STOPPED, PLAYING, PAUSED, SEEKING, FINISHED };

	public void play();
	public void pause();
	public void stop();
	public void seek(int position);
	public void rewind(double speed);
	public void forward(double speed);

	public void setSpeed(double speed);
	public double getSpeed();
	public State getState();

}
