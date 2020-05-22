package no.eventgeist.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.websocket.Session;

import no.eventgeist.service.EventService;

public class UserSession {
	
	private Session session;
	private EventService event;
	private String userid;
	private String support;
	private String position;
	
    private List<String> responses = Collections.synchronizedList(new ArrayList<String>());
	public List<String> getResponses() {return responses;}
	
	public UserSession(Session session, EventService event, String userid, String support, String position) {
		this.session=session;
		this.event=event;
		this.userid=userid;
		this.support=support;
		this.position=position;
	}

	public Session getSession() {return session;}
	public EventService getEvent() {return event;}
	public void addResponse(String response) {responses.add(response);}

}
