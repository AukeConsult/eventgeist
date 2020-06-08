package no.auke.mg.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import no.auke.mg.event.models.Status;

// all user session within the same time frame
public class TimeFrame {

	private EventService eventservice;

	private int delay;
	public int getDelay() {return delay;}

	// running statuses and meta information
	private Map<String, Status> status = new ConcurrentHashMap<String, Status>();
	public Map<String, Status> getStatus() {return status;}
	public void setStatus(Map<String, Status> status) {this.status = status;}

	protected Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();
	public List<UserSession> getUserSessions() {return new ArrayList<UserSession>(usersessions.values());}


	public TimeFrame(EventService eventservice, int delay) {
		this.eventservice=eventservice;
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

	public ResultSlot readResults() {
		try {
			lock.lock();
			if(resultslot!=null) {
				return resultslot;
			} else {
				return null;
			}
		} finally {
			if(resultslot!=null) {
				eventservice.getStorage().saveResultSlot(resultslot);
			} else {

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
