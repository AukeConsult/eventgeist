package no.auke.mg.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import no.auke.mg.event.feedbacks.FeedBack;

public class UserSession {

	private EventService event;

	private String sessionid;
	private String userid;
	private String team;
	private String position;

	private int delay;

	private AtomicBoolean open= new AtomicBoolean(true);
	private AtomicBoolean hasresult = new AtomicBoolean(false);

	public String getUserid() {return userid!=null?userid:"";}
	public String getTeam() {return team;}
	public String getPosition() {return position!=null?position:"";}


	public boolean isOpen() {
		return open.get();
	}

	private List<String> responses = Collections.synchronizedList(new ArrayList<String>());

	public int getNumResponses() {return responses.size();}

	public void addResponse(String response) {

		event.hit();
		// check commands
		if(response.startsWith("GI#")) {
			// set eventinfo

		} else if (response.startsWith("GH#")) {
			// set status

		} else if (response.startsWith("GS#")) {
			// set status

		} else {
			responses.add(response);
		}

	}

	public List<String> readResponses() {
		List<String> ret_rep = new ArrayList<String>(responses);
		responses.clear();
		return ret_rep;
	}

	public UserSession(String sessionid, EventService event, String userid, String team, String position, int delay) {
		this.sessionid=sessionid;
		this.event=event;
		this.userid=userid;
		this.team=team;
		this.position=position;
		this.delay=delay;
	}

	public boolean hasResult() {return hasresult.getAndSet(false);}
	public void setResponse(String response) {hasresult.set(true);}
	public EventService getEvent() {return event;}

	public int getDelay() {return delay;}
	public String getId() {return sessionid;}

	public void close() {open.set(false);}

	private ReentrantLock lock = new ReentrantLock();
	private List<FeedBack> results = new ArrayList<FeedBack>();

	public void setFeedback(FeedBack feedback) {
		try {
			lock.lock();
			this.results.add(feedback);
		} finally {
			lock.unlock();
		}
	}

	public List<FeedBack> readResults() {
		try {
			lock.lock();
			if(results.size()>0) {
				List<FeedBack> ret = new ArrayList<FeedBack>(results);
				return ret;
			} else {
				return null;
			}
		} finally {
			results.clear();
			lock.unlock();
		}
	}

}
