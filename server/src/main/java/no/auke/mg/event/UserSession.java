package no.auke.mg.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserSession {

	private EventService event;

	private String sessionid;
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
		return open.get();
	}

	private List<String> responses = Collections.synchronizedList(new ArrayList<String>());
	public int getNumResponses() {return responses.size();}
	public void addResponse(String response) {
		System.out.println("hit:" + response);
		event.hit();
		responses.add(response);
	}

	public List<String> readResponses() {
		List<String> ret_rep = new ArrayList<String>(responses);
		responses.clear();
		return ret_rep;
	}

	public UserSession(String sessionid, EventService event, String userid, String support, String position, int delay) {
		this.sessionid=sessionid;
		this.event=event;
		this.userid=userid;
		this.support=support;
		this.position=position;
		this.delay=delay;
	}

	public boolean hasResult() {
		return hasresult.getAndSet(false);
	}

	public void setResponse(String response) {
		hasresult.set(true);
	}

	public EventService getEvent() {return event;}

	public int getDelay() {return delay;}
	public String getId() {return sessionid;}

	public void close() {
		open.set(false);
	}

}
