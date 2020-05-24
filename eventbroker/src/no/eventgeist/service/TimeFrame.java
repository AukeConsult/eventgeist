package no.eventgeist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
	
	public ResultSlot readResultslot() {
    	try {
        	lock.lock();
        	return resultslot;
        } finally {
        	if(resultslot!=null) {
        		eventrunner.getCalculated_slots().add(resultslot);
        	}
        	resultslot=null;
        	lock.unlock();			
        }             	
	}
	
}
