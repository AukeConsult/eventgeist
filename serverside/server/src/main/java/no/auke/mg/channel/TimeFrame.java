package no.auke.mg.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import no.auke.mg.channel.feedbacks.FeedBack;

// all user session within the same time frame
public class TimeFrame {

	private ChannelService channelservice;

	private int delay;
	public int getDelay() {return delay;}

	// running statuses and meta information
	private Map<String, Status> status = new ConcurrentHashMap<String, Status>();
	public Map<String, Status> getStatus() {return status;}
	public void setStatus(Map<String, Status> status) {this.status = status;}

	protected Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();
	public List<UserSession> getUserSessions() {return new ArrayList<UserSession>(usersessions.values());}


	public TimeFrame(ChannelService channelservice, int delay) {
		this.channelservice=channelservice;
		this.delay=delay;
	}

	public void addUser(UserSession session) {
		if(!usersessions.containsKey(session.getId())) {
			usersessions.put(session.getId(),session);
		}
	}

	private ReentrantLock lock = new ReentrantLock();
	private ResultSlot resultslot;
	public void setResultslot(ResultSlot resultslot) {
		try {
			lock.lock();
			this.resultslot=resultslot;
		} finally {
			lock.unlock();
		}
	}

	public FeedBack readFeedBack() {
		try {
			lock.lock();
			if(resultslot!=null) {
				return resultslot.feedback;
			} else {
				return null;
			}
		} finally {
			if(resultslot!=null) {
				channelservice.getStorage().saveSlot(resultslot);
			}
			resultslot=null;
			lock.unlock();
		}
	}

	public void closeSession(UserSession session) {
		try {
			usersessions.remove(session.getId());
		} catch (Exception ex) {
			//TODO add logging
			System.out.println("session cant be removed " + session.getId());
		}
	}

}
