package no.auke.mg.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Monitor {

	protected Queue<TimeFrame> send_frames = new ConcurrentLinkedQueue<TimeFrame>();
	protected Queue<UserSession> send_users = new ConcurrentLinkedQueue<UserSession>();

	public Monitor() {}

	public void sendUser(UserSession user) {
		send_users.add(user);
	}
	public void sendTimeFrame(TimeFrame frame) {
		send_frames.add(frame);
	}
	public abstract void init();

}