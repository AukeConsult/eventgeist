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

	private int delay;
	public int getDelay() {return delay;}
	
	protected Map<String, UserSession> usersessions = new ConcurrentHashMap<String, UserSession>();
	public List<UserSession> getUserSessions() {return new ArrayList<UserSession>(usersessions.values());}
	
	protected Queue<TimeSlot> calculated_slots = new ConcurrentLinkedQueue<TimeSlot>();
	
	public TimeFrame(int delay) {
		this.delay=delay;
	}
	
	public void addUser(UserSession session) {
		if(!usersessions.containsKey(session.getId())) {
			usersessions.put(session.getId(),session);
		}
	}
	
	private ReentrantLock lock = new ReentrantLock(); 	
	private TimeSlot resultslot;	
	public void setResultslot(TimeSlot resultslot) {
    	try {
        	lock.lock();
        	this.resultslot=resultslot;
        } finally {
        	lock.unlock();			
        }                	
	}
	public TimeSlot readResultslot() {
    	try {
        	lock.lock();
        	return resultslot;
        } finally {
        	if(resultslot!=null) {
        		calculated_slots.add(resultslot);
        	}
        	resultslot=null;
        	lock.unlock();			
        }                	
	}
	
}
