package no.eventgeist.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.websocket.Session;

import no.eventgeist.service.EventServer;

public class UserSession {
	
	private Session session;
	private EventServer event;
	private String userid;
	private String support;
	private String position;
	private int delay;

	public String getSupport() {return support;}
    public String getPosition() {return position;}

	private List<String> responses = Collections.synchronizedList(new ArrayList<String>());
	
    public List<String> readResponses() {
    	List<String> ret_rep = new ArrayList<String>(responses);
    	responses.clear();
		return ret_rep;
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
	public void addResponse(String response) {responses.add(response);}
	
	public String getId() {
		if(session!=null) {
			return session.getId();
		} else  {
			return userid;
		}
	}
	

}
