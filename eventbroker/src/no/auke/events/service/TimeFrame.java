package no.auke.events.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

// all user session within the same time frame
public class TimeFrame {

	private EventRunner eventrunner;
	
	private int delay;
	public int getDelay() {return delay;}
	
	protected Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();
	public List<UserSession> getUserSessions() {return new ArrayList<UserSession>(usersessions.values());}
	
	public TimeFrame(EventRunner eventrunner, int delay) {
		this.eventrunner=eventrunner;
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
	
	public String readResults() {
    	try {
        	lock.lock();
        	if(resultslot!=null) {
            	return resultslot.resultString;        		
        	} else {
        		return null;
        	}
        } finally {
        	if(resultslot!=null) {
        		resultslot.resultString=null;
        		eventrunner.getCalculated_slots().add(resultslot);
        	}
        	resultslot=null;
        	lock.unlock();			
        }             	
	}

	public void removeSession(UserSession session) {
		try {
			usersessions.remove(session.getId());		
		} catch (Exception ex) {
			//TODO add logging
			System.out.println("session cant be removed " + session.getId());
		}
	}
	
}
