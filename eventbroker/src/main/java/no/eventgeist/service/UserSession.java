package no.eventgeist.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.Session;

import no.eventgeist.service.EventServer;

public class UserSession {
	
	private Session session;
	private EventServer event;
	
	private String userid;
	private String support;
	private String position;
	
	private int delay;
	
	public String getUserid() {return userid;}
	public String getSupport() {return support;}
    public String getPosition() {return position;}

	private List<String> responses = Collections.synchronizedList(new ArrayList<String>());

	private ReentrantLock lock = new ReentrantLock(); 	
	
	public void addResponse(String response) {
		//try {
		//	lock.lock();
			responses.add(response);
		//} finally {
		//	lock.unlock();
		//}
	}
	
    public List<String> readResponses() {

    	//try {
		//	lock.lock();
	    	List<String> ret_rep = new ArrayList<String>(responses);
	    	responses.clear();
			return ret_rep;
		//} finally {
		//	lock.unlock();
		//}

	}
	
	public UserSession(Session session, EventServer event, String userid, String support, String position, int delay) {
		this.session=session;
		this.event=event;
		this.userid=userid;
		this.support=support;
		this.position=position;
		this.delay=delay;
	}

	public Session getSession() {return session;}
	public EventServer getEvent() {return event;}
	
	public int getDelay() {return delay;}

	
	public String getId() {
		if(session!=null) {
			return session.getId();
		} else  {
			return userid;
		}
	}
	

}
