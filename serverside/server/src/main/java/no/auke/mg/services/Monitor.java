package no.auke.mg.services;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import no.auke.mg.channel.TimeFrame;
import no.auke.mg.channel.UserSession;

public abstract class Monitor {

	protected Queue<TimeFrame> send_frames = new ConcurrentLinkedQueue<TimeFrame>();
	public Queue<TimeFrame> getSend_frames() {return send_frames;}

	protected Queue<UserSession> send_users = new ConcurrentLinkedQueue<UserSession>();
	public Queue<UserSession> getSend_users() {return send_users;}

	public Monitor() {}

	public void sendUser(UserSession user) {
		send_users.add(user);
	}
	public void sendTimeFrame(TimeFrame frame) {
		send_frames.add(frame);
	}
	public abstract void init();

}