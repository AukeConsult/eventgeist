package no.auke.events.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.Session;

public class UserSession {

	private Session session;
	private EventRunner event;

	private String userid;
	private String support;
	private String position;

	private int delay;

	private AtomicBoolean open= new AtomicBoolean(true);
	private AtomicBoolean hasresult = new AtomicBoolean(false);

	public String getUserid() {return userid;}
	public String getSupport() {return support;}
	public String getPosition() {return position;}

	public boolean isOpen() {
		return open.get() || (session!=null & session.isOpen());
	}

	private List<String> responses = Collections.synchronizedList(new ArrayList<String>());
	public int getNumResponses() {return responses.size();}
	public void addResponse(String response) {
		event.hit();
		responses.add(response);
	}

	public List<String> readResponses() {
		List<String> ret_rep = new ArrayList<String>(responses);
		responses.clear();
		return ret_rep;
	}

	public UserSession(Session session, EventRunner event, String userid, String support, String position, int delay) {
		this.session=session;
		this.event=event;
		this.userid=userid;
		this.support=support;
		this.position=position;
		this.delay=delay;
	}

	public boolean hasResult() {
		return hasresult.getAndSet(false);
	}
	private String result;
	private ReentrantLock lock = new ReentrantLock();
	public void setResult(String result) {
		try {
			lock.lock();
			this.result=result;
		} finally {
			lock.unlock();
			hasresult.set(true);
		}
	}

	public String readResults() {
		try {
			lock.lock();
			if(hasresult.getAndSet(false)) {
				return result;
			} else {
				return null;
			}
		} finally {
			lock.unlock();
		}
	}

	public void setResponse(String response) {
		hasresult.set(true);
	}

	public Session getSession() {return session;}
	public EventRunner getEvent() {return event;}

	public int getDelay() {return delay;}
	public String getId() {
		if(session!=null) {
			return session.getId();
		} else  {
			return userid;
		}
	}
	public void close() {
		open.set(false);
	}

}
