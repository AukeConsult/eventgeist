package no.eventgeist.service;


import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


//
// Calculating each event
// 

public abstract class EventServer implements Runnable {
	    
	private int max_hits=0;
	private String eventid;
	private long starttime;
	
	private AtomicInteger timepos= new AtomicInteger();
	private int timeslot_period=5000;

	protected Map<Integer, TimeSlot> timeslots = new ConcurrentHashMap<Integer, TimeSlot>();
	
	protected Map<Integer, EventTimeFrame> timeframes = new ConcurrentHashMap<Integer, EventTimeFrame>();		
	public List<EventTimeFrame> getTimeframes() {return new ArrayList<EventTimeFrame>(timeframes.values());}


	public int getCnt() {return max_hits;}	
	public String getEventid() {return eventid;}	
	
	public EventServer(String eventid) {
		this.eventid=eventid;
	}
	
	// init and read up even informations
	public void init() {
		starttime=System.currentTimeMillis();		
	}
	
	public void addUser(UserSession session) {
		if(!timeframes.containsKey(session.getDelay())) {
			timeframes.put(session.getDelay(), new EventTimeFrame(session.getDelay()));
		}
		EventTimeFrame t = timeframes.get(session.getDelay());		
		timeframes.get(session.getDelay()).addUser(session);
	}

	public List<UserSession> getUserSessions() {
		List<UserSession> ret = new ArrayList<UserSession>();
		for(EventTimeFrame timeframe:getTimeframes()) {
			ret.addAll(timeframe.getUsersessions());
		}
		return ret;
	}
	
	private AtomicBoolean stopthread = new AtomicBoolean(false);
	public void stopThreads() {
		stopthread.set(true);
	}

	public void calculate() {
		
        for(EventTimeFrame timeframe:timeframes.values()) {
        	
        	int slotpos = timepos.incrementAndGet() - timeframe.getDelay();
        	
        	TimeSlot slot;
        	if(!timeslots.containsKey(slotpos)) {
        		slot = new TimeSlot(slotpos);
        	} else {
        		slot = timeslots.get(slotpos);
        	}        	
        	
        	for(UserSession user:timeframe.getUsersessions()) {
        		executeResponse(user, slot);
        	}
        	
        	executeResult(slot);
        	
        	if(!slot.resultWork.equals("")) {
        		timeslots.put(slot.timepos, slot);
        		timeframe.setResultslot(slot);
        	}      
        	
        }

	}
		
	@Override
	public void run() {
				
		// calculating results
        while (!stopthread.get()) {
        	try {
        		Thread.sleep(timeslot_period);
        		calculate();
            } catch (Exception e) {
                e.printStackTrace();
            }        	
        }
	}
	protected abstract void executeResponse(UserSession usersession,TimeSlot slot);
	protected abstract void executeResult(TimeSlot slot);


}
